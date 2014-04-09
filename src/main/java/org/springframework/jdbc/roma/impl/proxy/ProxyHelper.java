/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.jdbc.roma.impl.proxy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author Serkan ÖZAL
 */
public class ProxyHelper {

	@SuppressWarnings("unchecked")
	public static <T> T proxyObject(Class<T> clazz, ProxyObjectLoader<T> loader) {
		return (T)Enhancer.create(clazz, loader);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> proxyList(ProxyListLoader<T> loader) {
		return (List<T>)Enhancer.create(List.class, loader);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<T> proxySet(ProxySetLoader<T> loader) {
		return (Set<T>)Enhancer.create(Set.class, loader);
	}
	
	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> proxyMap(ProxyMapLoader<K, V> loader) {
		return (Map<K, V>)Enhancer.create(Map.class, loader);
	}
	
}
