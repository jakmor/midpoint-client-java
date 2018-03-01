package com.evolveum.midpoint.client.impl.restjaxb;

import com.evolveum.midpoint.client.api.*;
import com.evolveum.midpoint.client.api.exception.AuthenticationException;
import com.evolveum.midpoint.client.api.exception.AuthorizationException;
import com.evolveum.midpoint.client.api.exception.CommonException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteScriptResponseType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;
import com.evolveum.midpoint.xml.ns._public.model.scripting_3.*;
import com.evolveum.prism.xml.ns._public.query_3.SearchFilterType;
import com.evolveum.prism.xml.ns._public.types_3.ModificationTypeType;
import com.evolveum.prism.xml.ns._public.types_3.ObjectDeltaType;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

/**
 * @author jakmor
 */
public class RestJaxbScriptService extends AbstractWebResource implements ScriptService, ScriptFilterBuilder, ScriptActionBuilder, ScriptExecuter, ScriptResponseService
{
    private static final String EXECUTE_URL_PREFIX =  "/executeScript";
    private static final String RESULTS_URL_PREFIX =  "/executeScriptResults/";
    private static final String ASYNC_OPTION = "?asynchronous=true";
    private static final String ACTION_EXPRESSION_TYPE_MODIFY = "modify";
    private static final String ACTION_PARAMETER_TYPE_DELTA = "delta";

    private ExpressionSequenceType expression;
    private SearchExpressionType searchExpression;
    private ActionExpressionType actionExpressionType;
    private String scriptTaskOid;
    private boolean async = false;

    RestJaxbScriptService(final RestJaxbService service){
        super(service);
        expression = new ExpressionSequenceType();
    }

    @Override
    public ScriptFilterBuilder filter(Class<? extends ObjectType> object){

        searchExpression = new SearchExpressionType();
        searchExpression.setType(new QName(object.getSimpleName()));
        return this;
    }

    @Override
    public ScriptFilterBuilder inOid(List<String> oids)
    {

        SearchFilterType searchFilter = new SearchFilterType();
        searchFilter.setFilterClause(getService().getDomSerializer().createInOidFilter(oids));
        searchExpression.setSearchFilter(searchFilter);
        return this;
    }

    @Override
    public ScriptActionBuilder action(){
        actionExpressionType = new ActionExpressionType();
        return this;
    }

    @Override
    public ScriptExecuter modify(Map <String, Object> pathValueMap, ModificationTypeType modificationType)
    {
        //TODO: Action expression builder?
        actionExpressionType.setType(ACTION_EXPRESSION_TYPE_MODIFY);
        ActionParameterValueType actionParameterValueType = new ActionParameterValueType();

        actionParameterValueType.setName(ACTION_PARAMETER_TYPE_DELTA);
        ObjectDeltaType deltaType = new ObjectDeltaType();

        for(Map.Entry<String, Object> entry : pathValueMap.entrySet())
        {
            deltaType.getItemDelta().add(RestUtil.buildItemDelta(modificationType, entry.getKey(), entry.getValue()));
        }

        actionParameterValueType.setValue(deltaType);
        actionExpressionType.getParameter().add(actionParameterValueType);
        return this;
    }

    @Override
    public ScriptResponseService response(String oid){
        scriptTaskOid = oid;
        return this;
    }

    @Override
    public ScriptExecuter async(){
        async = true;
        return this;
    }

    @Override
    public ExecuteScriptResponseType get() throws CommonException{
        String urlPrefix = "/executeScriptResults/" + scriptTaskOid;

        Response response = getService().getClient().replacePath(urlPrefix).get();
        switch (response.getStatus()) {
            case 200:
                return response.readEntity(ExecuteScriptResponseType.class);
            case 400:
                throw new BadRequestException(response.getStatusInfo().getReasonPhrase());
            case 401:
                throw new AuthenticationException(response.getStatusInfo().getReasonPhrase());
            case 403:
                throw new AuthorizationException(response.getStatusInfo().getReasonPhrase());
            case 404:
                throw new ObjectNotFoundException(response.getStatusInfo().getReasonPhrase());
            default:
                throw new UnsupportedOperationException("Implement other status codes, unsupported return status: " + response.getStatus());
        }
    }

    @Override
    public TaskFuture<OperationResultType> apost() throws CommonException
    {
        String restPath;

        if(async)
        {
            restPath = EXECUTE_URL_PREFIX + ASYNC_OPTION;
        }else{
            restPath = EXECUTE_URL_PREFIX;
        }

        searchExpression.setScriptingExpression(new JAXBElement<ScriptingExpressionType>(new QName("action"), ScriptingExpressionType.class,actionExpressionType));
        expression.getScriptingExpression().add(new JAXBElement<SearchExpressionType>(new QName("search"), SearchExpressionType.class,searchExpression));

        Response response = getService().getClient().replacePath(restPath).post(expression);

        switch (response.getStatus()) {
            case 200:
                return new RestJaxbCompletedFuture<>(new OperationResultType());
            case 400:
                throw new BadRequestException(response.getStatusInfo().getReasonPhrase());
            case 401:
                throw new AuthenticationException(response.getStatusInfo().getReasonPhrase());
            case 403:
                throw new AuthorizationException(response.getStatusInfo().getReasonPhrase());
                //TODO: Do we want to return a reference? Might be useful.
            case 404:
                throw new ObjectNotFoundException(response.getStatusInfo().getReasonPhrase());
            default:
                throw new UnsupportedOperationException("Implement other status codes, unsupported return status: " + response.getStatus());
        }
    }
}
