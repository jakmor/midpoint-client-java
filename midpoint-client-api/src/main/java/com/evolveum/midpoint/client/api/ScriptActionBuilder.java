package com.evolveum.midpoint.client.api;

import com.evolveum.midpoint.client.api.verb.Post;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;
import com.evolveum.prism.xml.ns._public.types_3.ModificationTypeType;

import java.util.Map;

/**
 * @author jakmor
 */
public interface ScriptActionBuilder
{
    ScriptExecuter modify(Map<String, Object> pathValueMap, ModificationTypeType modificationType);
    ScriptExecuter modify(String path, Object value, ModificationTypeType modificationType);
}
