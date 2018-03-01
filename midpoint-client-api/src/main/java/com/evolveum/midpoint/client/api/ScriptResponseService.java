package com.evolveum.midpoint.client.api;

import com.evolveum.midpoint.client.api.verb.Get;
import com.evolveum.midpoint.xml.ns._public.common.api_types_3.ExecuteScriptResponseType;

/**
 * @author jakmor
 */
public interface ScriptResponseService extends Get<ExecuteScriptResponseType>
{
    ScriptResponseService response(String oid);
}
