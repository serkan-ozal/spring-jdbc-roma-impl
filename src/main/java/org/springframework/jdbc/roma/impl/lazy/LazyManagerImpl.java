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

package org.springframework.jdbc.roma.impl.lazy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * @author Serkan ÖZAL
 */
@Component
public class LazyManagerImpl implements LazyManager {

	private Map<String, Boolean> lazyConditionPropertyMap = new ConcurrentHashMap<String, Boolean>();
	private Map<String, Boolean> lazyLoadConditionPropertyMap = new ConcurrentHashMap<String, Boolean>();
	
	@Override
	public synchronized void enableLazyConditionProperty(String propertyName) {
		lazyConditionPropertyMap.put(propertyName, true);
	}

	@Override
	public synchronized void disableLazyConditionProperty(String propertyName) {
		lazyConditionPropertyMap.put(propertyName, false);
	}
	
	@Override
	public synchronized void clearLazyConditionProperty(String propertyName) {
		lazyConditionPropertyMap.remove(propertyName);
	}

	@Override
	public boolean getLazyConditionProperty(String propertyName) {
		Boolean propertyValue = lazyConditionPropertyMap.get(propertyName);
		if (propertyValue == null) {
			return false;
		}
		else {
			return propertyValue;
		}	
	}
	
	@Override
	public synchronized void enableLazyLoadConditionProperty(String propertyName) {
		lazyLoadConditionPropertyMap.put(propertyName, true);
	}

	@Override
	public synchronized void disableLazyLoadConditionProperty(String propertyName) {
		lazyLoadConditionPropertyMap.put(propertyName, false);
	}
	
	@Override
	public synchronized void clearLazyLoadConditionProperty(String propertyName) {
		lazyLoadConditionPropertyMap.remove(propertyName);
	}

	@Override
	public boolean getLazyLoadConditionProperty(String propertyName) {
		Boolean propertyValue = lazyLoadConditionPropertyMap.get(propertyName);
		if (propertyValue == null) {
			return false;
		}
		else {
			return propertyValue;
		}	
	}

}
