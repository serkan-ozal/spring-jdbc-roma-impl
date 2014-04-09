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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.impl.factory.RowMapperFactoryProvider;
import org.springframework.jdbc.roma.impl.ignore.IgnoreManager;
import org.springframework.jdbc.roma.impl.lazy.LazyManager;
import org.springframework.jdbc.roma.impl.util.BeanUtil;
import org.springframework.stereotype.Service;

/**
 * @author Serkan ÖZAL
 */
@Service("rowMapperService")
@DependsOn({"springUtil", "beanUtil"})
public class RowMapperServiceImpl implements RowMapperService {

	@Autowired
	private LazyManager lazyManager;
	@Autowired
	private IgnoreManager ignoreManager;
	
	private Map<Class<?>, RowMapper<?>> cachedRowMappers = new ConcurrentHashMap<Class<?>, RowMapper<?>>();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> RowMapper<T> getRowMapper(Class<T> clazz) {
		RowMapper<T> rowMapper = (RowMapper<T>) cachedRowMappers.get(clazz);
		if (rowMapper == null) {
			rowMapper = 
					RowMapperFactoryProvider.getRowMapperFactory().
						getRowMapper(clazz, BeanUtil.getInstance().getConfigManager());
			cachedRowMappers.put(clazz, rowMapper);
		}
		return rowMapper;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> RowMapper<T> getRowMapper(Class<T> clazz, JdbcTemplate jdbcTemplate) {
		RowMapper<T> rowMapper = (RowMapper<T>) cachedRowMappers.get(clazz);
		if (rowMapper == null) {
			rowMapper = 
					RowMapperFactoryProvider.getRowMapperFactory().getRowMapper(clazz, jdbcTemplate);
			cachedRowMappers.put(clazz, rowMapper);
		}
		return rowMapper;
	}

	@Override
	public void enableLazyConditionProperty(String propertyName) {
		lazyManager.enableLazyConditionProperty(propertyName);
	}

	@Override
	public void disableLazyConditionProperty(String propertyName) {
		lazyManager.disableLazyConditionProperty(propertyName);
	}
	
	@Override
	public void enableLazyLoadConditionProperty(String propertyName) {
		lazyManager.enableLazyLoadConditionProperty(propertyName);
	}
	
	@Override
	public void disableLazyLoadConditionProperty(String propertyName) {
		lazyManager.disableLazyLoadConditionProperty(propertyName);
	}
	
	@Override
	public void enableIgnoreConditionProperty(String propertyName) {
		ignoreManager.enableIgnoreConditionProperty(propertyName);
	}
	
	@Override
	public void disableIgnoreConditionProperty(String propertyName) {
		ignoreManager.disableIgnoreConditionProperty(propertyName);
	}
	
}
