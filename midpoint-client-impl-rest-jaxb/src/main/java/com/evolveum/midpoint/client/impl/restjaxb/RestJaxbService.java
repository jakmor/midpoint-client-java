/*
 * Copyright (c) 2017-2018 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.midpoint.client.impl.restjaxb;

import java.io.IOException;
import java.lang.reflect.Type;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.bind.Unmarshaller;

import com.evolveum.midpoint.xml.ns._public.common.common_3.*;
import com.evolveum.midpoint.client.api.scripting.ScriptingUtil;
import com.evolveum.midpoint.client.impl.restjaxb.scripting.ScriptingUtilImpl;

import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.WebClient;

import com.evolveum.midpoint.client.api.AuthenticationChallenge;
import com.evolveum.midpoint.client.api.AuthenticationManager;
import com.evolveum.midpoint.client.api.ObjectCollectionService;
import com.evolveum.midpoint.client.api.RpcService;
import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.api.ServiceUtil;
import com.evolveum.midpoint.client.api.exception.AuthenticationException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;

/**
 * @author semancik
 * @author katkav
 *
 */
public class RestJaxbService implements Service {

	private static final String IMPERSONATE_HEADER = "Switch-To-Principal";
	private final ServiceUtil util;
	private final ScriptingUtil scriptingUtil;

	// TODO: jaxb context
	
	private WebClient client;
	private DomSerializer domSerializer;
	private JAXBContext jaxbContext;
	private AuthenticationManager<?> authenticationManager;
	private List<AuthenticationType> supportedAuthenticationsByServer;
	
	public WebClient getClient() {
		return client;
	}
	
	public DomSerializer getDomSerializer() {
		return domSerializer;
	}
	
	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}
	
	public List<AuthenticationType> getSupportedAuthenticationsByServer() {
		if (supportedAuthenticationsByServer == null) {
			supportedAuthenticationsByServer = new ArrayList<>();
		}
		return supportedAuthenticationsByServer;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends AuthenticationChallenge> AuthenticationManager<T> getAuthenticationManager() {
		return (AuthenticationManager<T>) authenticationManager;
	}
	
	public RestJaxbService() {
		super();
		client = WebClient.create("");
		util = new RestJaxbServiceUtil(null);
		scriptingUtil = new ScriptingUtilImpl(util);
	}
	
	RestJaxbService(String url, String username, String password, AuthenticationType authentication, List<SecurityQuestionAnswer> secQ) throws IOException {
		super();
		try {
			jaxbContext = createJaxbContext();
		} catch (JAXBException e) {
			throw new IOException(e);
		}
		
		if (AuthenticationType.SECQ == authentication) {
			authenticationManager = new SecurityQuestionAuthenticationManager(username, secQ);
		} else if (authentication != null ){
			authenticationManager = new BasicAuthenticationManager(username, password);
		}
		
		CustomAuthNProvider<?> authNProvider = new CustomAuthNProvider<>(authenticationManager, this);
		client = WebClient.create(url, Arrays.asList(new JaxbXmlProvider<>(jaxbContext)));
		ClientConfiguration config = WebClient.getConfig(client);
		config.getInInterceptors().add(authNProvider);
		config.getInFaultInterceptors().add(authNProvider);
		client.accept(MediaType.APPLICATION_XML);
		client.type(MediaType.APPLICATION_XML);
		
		if (authenticationManager != null) {
			client.header("Authorization", authenticationManager.createAuthorizationHeader());
		}
				
		util = new RestJaxbServiceUtil(jaxbContext);
		scriptingUtil = new ScriptingUtilImpl(util);
		domSerializer = new DomSerializer(jaxbContext);
	}

	@Override
	public Service impersonate(String oid){
		client.header(IMPERSONATE_HEADER, oid);
		return this;
	}

	@Override
	public Service addHeader(String header, String value){
		client.header(header, value);
		return this;
	}
	

	@Override
	public ObjectCollectionService<UserType> users() {
		return new RestJaxbObjectCollectionService<>(this, Types.USERS.getRestPath(), UserType.class);
	}

	@Override
	public ObjectCollectionService<ValuePolicyType> valuePolicies() {
		return new RestJaxbObjectCollectionService<>(this, Types.VALUE_POLICIES.getRestPath(), ValuePolicyType.class);
	}

	@Override
	public ObjectCollectionService<SecurityPolicyType> securityPolicies() {
		return new RestJaxbObjectCollectionService<>(this, Types.SECURITY_POLICIES.getRestPath(), SecurityPolicyType.class);
	}

	@Override
	public ObjectCollectionService<ConnectorType> connectors() {
		return new RestJaxbObjectCollectionService<>(this, Types.CONNECTORS.getRestPath(), ConnectorType.class);
	}

	@Override
	public ObjectCollectionService<ConnectorHostType> connectorHosts() {
		return new RestJaxbObjectCollectionService<>(this, Types.CONNECTOR_HOSTS.getRestPath(), ConnectorHostType.class);
	}

	@Override
	public ObjectCollectionService<GenericObjectType> genericObjects() {
		return new RestJaxbObjectCollectionService<>(this, Types.GENERIC_OBJECTS.getRestPath(), GenericObjectType.class);
	}

	@Override
	public ObjectCollectionService<ResourceType> resources() {
		return new RestJaxbObjectCollectionService<>(this, Types.RESOURCES.getRestPath(), ResourceType.class);
	}

	@Override
	public ObjectCollectionService<ObjectTemplateType> objectTemplates() {
		return new RestJaxbObjectCollectionService<>(this, Types.OBJECT_TEMPLATES.getRestPath(), ObjectTemplateType.class);
	}

	@Override
	public ObjectCollectionService<SystemConfigurationType> systemConfigurations() {
		return new RestJaxbObjectCollectionService<>(this, Types.SYSTEM_CONFIGURATIONS.getRestPath(), SystemConfigurationType.class);
	}

	@Override
	public ObjectCollectionService<TaskType> tasks() {
		return new RestJaxbObjectCollectionService<>(this, Types.TASKS.getRestPath(), TaskType.class);
	}

	@Override
	public ObjectCollectionService<ShadowType> shadows() {
		return new RestJaxbObjectCollectionService<>(this, Types.SHADOWS.getRestPath(), ShadowType.class);
	}

	@Override
	public ObjectCollectionService<RoleType> roles() {
		return new RestJaxbObjectCollectionService<>(this, Types.ROLES.getRestPath(), RoleType.class);
	}

	@Override
	public ObjectCollectionService<OrgType> orgs() {
		return new RestJaxbObjectCollectionService<>(this, Types.ORGS.getRestPath(), OrgType.class);
	}

	@Override
	public <T> RpcService<T> rpc() {
		return new RestJaxbRpcService<>(this);
	}

	@Override
	public ServiceUtil util() {
		return util;
	}

	@Override
	public ScriptingUtil scriptingUtil() {
		return scriptingUtil;
	}
	
		/**
	 * Used frequently at several places. Therefore unified here.
	 * @throws ObjectNotFoundException 
	 */
	<O extends ObjectType> O getObject(final Class<O> type, final String oid) throws ObjectNotFoundException, AuthenticationException {
		// TODO
		String urlPrefix = RestUtil.subUrl(Types.findType(type).getRestPath(), oid);
		Response response = client.replacePath(urlPrefix).get();
		
		if (Status.OK.getStatusCode() == response.getStatus() ) {
			return response.readEntity(type);
		}
		
		if (Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
			throw new ObjectNotFoundException("Cannot get object with oid" + oid + ". Object doesn't exist");
		}
		
		if (Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
			throw new AuthenticationException(response.getStatusInfo().getReasonPhrase());
		}
		
		return null;
	}

	<O extends ObjectType> void deleteObject(final Class<O> type, final String oid) throws ObjectNotFoundException, AuthenticationException {
		String urlPrefix = RestUtil.subUrl(Types.findType(type).getRestPath(), oid);
		Response response = client.replacePath(urlPrefix).delete();

		//TODO: Looks like midPoint returns a 204 and not a 200 on success
		if (Status.OK.getStatusCode() == response.getStatus() ) {
			//TODO: Do we want to return anything on successful delete or just remove this if block?
		}

		if (Status.NO_CONTENT.getStatusCode() == response.getStatus() ) {
			//TODO: Do we want to return anything on successful delete or just remove this if block?
		}


		if (Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			throw new BadRequestException("Bad request");
		}

		if (Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
			throw new ObjectNotFoundException("Cannot delete object with oid" + oid + ". Object doesn't exist");
		}

		if (Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
			throw new AuthenticationException("Cannot authentication user");
		}
	}

	@Override
	public UserType self() throws AuthenticationException{
		String urlPrefix = "/self";
		Response response = client.replacePath(urlPrefix).get();


		if (Status.OK.getStatusCode() == response.getStatus() ) {
			return response.readEntity(UserType.class);
		}

		if (Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			throw new BadRequestException("Bad request");
		}

		if (Status.UNAUTHORIZED.getStatusCode() == response.getStatus()) {
			throw new AuthenticationException("Cannot authentication user");
		}
		return null;
	}

	private JAXBContext createJaxbContext() throws JAXBException {
		return JAXBContext.newInstance("com.evolveum.midpoint.xml.ns._public.common.api_types_3:"
				+ "com.evolveum.midpoint.xml.ns._public.common.audit_3:"
				+ "com.evolveum.midpoint.xml.ns._public.common.common_3:"
				+ "com.evolveum.midpoint.xml.ns._public.connector.icf_1.connector_extension_3:"
				+ "com.evolveum.midpoint.xml.ns._public.connector.icf_1.connector_schema_3:"
				+ "com.evolveum.midpoint.xml.ns._public.connector.icf_1.resource_schema_3:"
				+ "com.evolveum.midpoint.xml.ns._public.gui.admin_1:"
				+ "com.evolveum.midpoint.xml.ns._public.model.extension_3:"
				+ "com.evolveum.midpoint.xml.ns._public.model.scripting_3:"
				+ "com.evolveum.midpoint.xml.ns._public.model.scripting.extension_3:"
				+ "com.evolveum.midpoint.xml.ns._public.report.extension_3:"
				+ "com.evolveum.midpoint.xml.ns._public.resource.capabilities_3:"
				+ "com.evolveum.midpoint.xml.ns._public.task.extension_3:"
				+ "com.evolveum.midpoint.xml.ns._public.task.jdbc_ping.handler_3:"
				+ "com.evolveum.midpoint.xml.ns._public.task.noop.handler_3:"
				+ "com.evolveum.prism.xml.ns._public.annotation_3:"
				+ "com.evolveum.prism.xml.ns._public.query_3:"
				+ "com.evolveum.prism.xml.ns._public.types_3");
	}
}
