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

package org.springframework.jdbc.roma.impl.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Serkan ÖZAL
 */
public interface RowMapperService {

	public <T> RowMapper<T> getRowMapper(Class<T> clazz);
	public <T> RowMapper<T> getRowMapper(Class<T> clazz, JdbcTemplate jdbcTemplate);
	
	public void enableLazyConditionProperty(String propertyName);
	public void disableLazyConditionProperty(String propertyName);
	
	public void enableLazyLoadConditionProperty(String propertyName);
	public void disableLazyLoadConditionProperty(String propertyName);
	
	public void enableIgnoreConditionProperty(String propertyName);
	public void disableIgnoreConditionProperty(String propertyName);
	
}
