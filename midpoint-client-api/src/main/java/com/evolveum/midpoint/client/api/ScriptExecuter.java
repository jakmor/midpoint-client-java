package com.evolveum.midpoint.client.api;

import com.evolveum.midpoint.client.api.verb.Post;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;

/**
 * @author jakmor
 */
public interface ScriptExecuter extends Post<OperationResultType>
{
    ScriptExecuter async();

}
