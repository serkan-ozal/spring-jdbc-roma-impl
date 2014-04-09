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

package org.springframework.jdbc.roma.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.impl.util.BeanUtil;

/**
 * @author Serkan ÖZAL
 */
public abstract class AbstractRowMapper<T> implements RowMapper<T> {

	protected ConfigManager configManager = BeanUtil.getInstance().getConfigManager();
	protected Class<T> cls;
	protected JdbcTemplate jdbcTemplate;
	protected T obj;
	
	public AbstractRowMapper(Class<T> cls) {
		this.cls = cls;
	}
	
	public AbstractRowMapper(Class<T> cls, JdbcTemplate jdbcTemplate) {
		this.cls = cls;
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public AbstractRowMapper(Class<T> cls, ConfigManager configManager) {
		this.cls = cls;
		this.configManager = configManager;
	}
	
	public AbstractRowMapper(Class<T> cls, JdbcTemplate jdbcTemplate, ConfigManager configManager) {
		this.cls = cls;
		this.jdbcTemplate = jdbcTemplate;
		this.configManager = configManager;
	}
	
	public Class<T> getClazz() {
		return cls;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public T getObject() {
		return obj;
	}
	
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

}
