package com.evolveum.midpoint.client.api;

import com.evolveum.midpoint.client.api.verb.Post;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteScriptResponseType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;

/**
 * @author jakmor
 */
public interface ScriptExecuter extends Post<ExecuteScriptResponseType>
{
    ScriptExecuter async();

}
