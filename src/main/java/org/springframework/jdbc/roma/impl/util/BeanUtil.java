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

package org.springframework.jdbc.roma.impl.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.impl.ignore.IgnoreManager;
import org.springframework.jdbc.roma.impl.lazy.LazyManager;
import org.springframework.jdbc.roma.impl.service.RowMapperService;
import org.springframework.stereotype.Component;

/**
 * @author Serkan ÖZAL
 */
@Component
public class BeanUtil {

	private static BeanUtil instance;
	
	@Autowired
	private RowMapperService rowMapperService;
	@Autowired
	private ConfigManager configManager;
	@Autowired
	private LazyManager lazyManager;
	@Autowired
	private IgnoreManager ignoreManager;
	
	@PostConstruct
	protected void afterPropertiesSet() {
		instance = this;
	}

	public static BeanUtil getInstance() {
		return instance;
	}
	
	public RowMapperService getRowMapperService() {
		return rowMapperService;
	}
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public LazyManager getLazyManager() {
		return lazyManager;
	}
	
	public IgnoreManager getIgnoreManager() {
		return ignoreManager;
	}
	
}
