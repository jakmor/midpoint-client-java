/**
 * Copyright (c) 2017 Evolveum
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

import java.util.Arrays;

import javax.jws.Oneway;
import javax.xml.namespace.QName;

import com.ctc.wstx.sw.EncodingXmlWriter;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ObjectListType;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ObjectModificationType;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.PolicyItemsDefinitionType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ValuePolicyType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_3.ExecuteScriptType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_3.ExpressionSequenceType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_3.SearchExpressionType;
import com.evolveum.prism.xml.ns._public.query_3.QueryType;

/**
 * 
 * @author katkav
 *
 */
public enum Types {

	USERS(UserType.class, new QName(SchemaConstants.NS_COMMON, "UserType"), new QName(SchemaConstants.NS_COMMON, "user"), "users"),
	QUERY(QueryType.class, new QName(SchemaConstants.NS_QUERY, "QueryType"), new QName(SchemaConstants.NS_QUERY, "query"), null),
	OBJECT_LIST_TYPE(ObjectListType.class, new QName(SchemaConstants.NS_API_TYPES, "ObjectListType"), new QName(SchemaConstants.NS_API_TYPES, "objectList"), ""),
	POLICY_ITEMS_DEFINITION(PolicyItemsDefinitionType.class, new QName(SchemaConstants.NS_API_TYPES, "PolicyItemsDefinitionType"), new QName(SchemaConstants.NS_API_TYPES, "policyItemsDefinition"), ""),
	OBJECT_MODIFICATION_TYPE(ObjectModificationType.class, new QName(SchemaConstants.NS_API_TYPES, "ObjectModificationType"), new QName(SchemaConstants.NS_API_TYPES, "objectModification"), ""),
	VALUE_POLICIES(ValuePolicyType.class, new QName(SchemaConstants.NS_COMMON, "ValuePolicyType"), new QName(SchemaConstants.NS_COMMON, "valuePolicy"), "valuePolicies"),
	EXECUTE_SCRIPT_TYPE(ExecuteScriptType.class, new QName(SchemaConstants.NS_EXTENSION, "ExecuteScriptType"), new QName(SchemaConstants.NS_EXTENSION, "executeScript"), "");

	private Class<?> clazz;
	/**
	 * element name - used for XML jaxb serialization
	 */
	private QName elementName;
	/**
	 * type name as QName, e.g. c:RoleType, c:ValuePolicyType.. e.g. we need it to the reference 
	 */
	private QName typeQName;
	private String restPath;
	
	private Types(Class<?> clazz, QName typeQName, QName elementName, String restPath) {
		this.clazz = clazz;
		this.typeQName = typeQName;
		this.elementName = elementName;
		this.restPath = restPath;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public QName getElementName() {
		return elementName;
	}
	
	public String getRestPath() {
		return restPath;
	}
	
	public QName getTypeQName() {
		return typeQName;
	}
	
	public static Types findType(Class<?> clazz) {
		return Arrays.asList(values()).stream().filter(type -> type.getClazz().equals(clazz)).findAny().orElse(null);
	}
}
