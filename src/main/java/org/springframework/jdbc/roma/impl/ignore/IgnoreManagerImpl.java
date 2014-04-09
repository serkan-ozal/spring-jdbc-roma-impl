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

package org.springframework.jdbc.roma.impl.ignore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * @author Serkan ÖZAL
 */
@Component
public class IgnoreManagerImpl implements IgnoreManager {

	private Map<String, Boolean> ignoreConditionPropertyMap = new ConcurrentHashMap<String, Boolean>();
	
	@Override
	public synchronized void enableIgnoreConditionProperty(String propertyName) {
		ignoreConditionPropertyMap.put(propertyName, true);
	}

	@Override
	public synchronized void disableIgnoreConditionProperty(String propertyName) {
		ignoreConditionPropertyMap.put(propertyName, false);
	}
	
	@Override
	public synchronized void clearIgnoreConditionProperty(String propertyName) {
		ignoreConditionPropertyMap.remove(propertyName);
	}

	@Override
	public boolean getIgnoreConditionProperty(String propertyName) {
		Boolean propertyValue = ignoreConditionPropertyMap.get(propertyName);
		if (propertyValue == null) {
			return false;
		}
		else {
			return propertyValue;
		}	
	}
	
}
