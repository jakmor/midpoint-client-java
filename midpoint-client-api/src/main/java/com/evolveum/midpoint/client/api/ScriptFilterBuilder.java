package com.evolveum.midpoint.client.api;

import java.util.List;

/**
 * @author jakmor
 */
public interface ScriptFilterBuilder
{
    ScriptFilterBuilder inOid(List<String> oids);
    ScriptActionBuilder action();

}
