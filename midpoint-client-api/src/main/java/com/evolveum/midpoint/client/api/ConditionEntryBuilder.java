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
package com.evolveum.midpoint.client.api;

import java.util.Collection;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

public interface ConditionEntryBuilder<O extends ObjectType> {

	
	MatchingRuleEntryBuilder<O> eq(Object... values);
	public MatchingRuleEntryBuilder<O> eq();
	public MatchingRuleEntryBuilder<O> eqPoly(String orig, String norm);
	public MatchingRuleEntryBuilder<O> eqPoly(String orig);
	public MatchingRuleEntryBuilder<O> gt(Object value);
	public AtomicFilterExit<O> gt();
	public MatchingRuleEntryBuilder<O> ge(Object value);
	public AtomicFilterExit<O> ge();
	public MatchingRuleEntryBuilder<O> lt(Object value);
	public AtomicFilterExit<O> lt();
	public MatchingRuleEntryBuilder<O> le(Object value);
	public AtomicFilterExit<O> le();
	public MatchingRuleEntryBuilder<O> startsWith(Object value);
	public MatchingRuleEntryBuilder<O>  startsWithPoly(String orig, String norm);
	public MatchingRuleEntryBuilder<O>  startsWithPoly(String orig);
	public MatchingRuleEntryBuilder<O>  endsWith(Object value);
	public MatchingRuleEntryBuilder<O>  endsWithPoly(String orig, String norm);
	public MatchingRuleEntryBuilder<O>  endsWithPoly(String orig);
	public MatchingRuleEntryBuilder<O>  contains(Object value);
	public MatchingRuleEntryBuilder<O>  containsPoly(String orig, String norm);
	public MatchingRuleEntryBuilder<O>  containsPoly(String orig);
	public AtomicFilterExit<O>  ref(QName relation);
	public AtomicFilterExit<O>  ref(ObjectReferenceType... value);
	public AtomicFilterExit<O>  ref(Collection<ObjectReferenceType> values);
	public AtomicFilterExit<O>  ref(String... oid);
	public AtomicFilterExit<O>  ref(String oid, QName targetTypeName);
	public AtomicFilterExit<O>  isNull();
}
