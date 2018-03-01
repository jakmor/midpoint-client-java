package com.evolveum.midpoint.client.api;

import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

/**
 * @author jakmor
 */
public interface ScriptService
{
    ScriptFilterBuilder filter(Class<? extends ObjectType> object);
}
