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

package org.springframework.jdbc.roma.impl.config.manager;

import java.lang.reflect.Field;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.roma.api.config.DefaultConfigs;
import org.springframework.jdbc.roma.api.config.provider.ConfigProvider;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperBlobFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClassConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClobFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperEnumFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperObjectFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperTimeFieldConfig;
import org.springframework.jdbc.roma.impl.config.provider.annotation.AnnotationBasedConfigProvider;
import org.springframework.jdbc.roma.impl.config.provider.properties.PropertiesBasedConfigProvider;
import org.springframework.jdbc.roma.impl.config.provider.xml.XmlBasedConfigProvider;
import org.springframework.stereotype.Component;

/**
 * @author Serkan ÖZAL
 */
@Component
public class ConfigManagerImpl extends BaseConfigManager {

	@Autowired(required = false)
	private DefaultConfigs defaultConfigs;
	
	@Override
	protected void init() {
		addConfigProviderIfAvailable(new AnnotationBasedConfigProvider());
		addConfigProviderIfAvailable(new XmlBasedConfigProvider());
		addConfigProviderIfAvailable(new PropertiesBasedConfigProvider());
	}
	
	@PostConstruct
	protected void afterPropertiesSet() {
		if (defaultConfigs == null) {
			defaultConfigs = new DefaultConfigs();
		}
	}
	
	@Override
	public DefaultConfigs getDefaultConfigs() {
		return defaultConfigs;
	}

	@Override
	public RowMapperFieldConfig getRowMapperFieldConfig(Class<?> clazz, String fieldName) {
		for (ConfigProvider configProvider : configProviderList) {
			RowMapperFieldConfig rmfc = configProvider.getRowMapperFieldConfig(clazz, fieldName);
			if (rmfc != null) {
				return rmfc;
			}
		}
		return null;
	}

	@Override
	public RowMapperObjectFieldConfig getRowMapperObjectFieldConfig(Class<?> clazz, String fieldName) {
		for (ConfigProvider configProvider : configProviderList) {
			RowMapperObjectFieldConfig rmofc = configProvider.getRowMapperObjectFieldConfig(clazz, fieldName);
			if (rmofc != null) {
				return rmofc;
			}
		}
		return null;
	}

	@Override
	public RowMapperEnumFieldConfig getRowMapperEnumFieldConfig(Class<?> clazz, String fieldName) {
		for (ConfigProvider configProvider : configProviderList) {
			RowMapperEnumFieldConfig rmefc = configProvider.getRowMapperEnumFieldConfig(clazz, fieldName);
			if (rmefc != null) {
				return rmefc;
			}
		}
		return null;
	}

	@Override
	public RowMapperClobFieldConfig getRowMapperClobFieldConfig(Class<?> clazz, String fieldName) {
		for (ConfigProvider configProvider : configProviderList) {
			RowMapperClobFieldConfig rmcfc = configProvider.getRowMapperClobFieldConfig(clazz, fieldName);
			if (rmcfc != null) {
				return rmcfc;
			}
		}
		return null;
	}

	@Override
	public RowMapperBlobFieldConfig getRowMapperBlobFieldConfig(Class<?> clazz, String fieldName) {
		for (ConfigProvider configProvider : configProviderList) {
			RowMapperBlobFieldConfig rmbfc = configProvider.getRowMapperBlobFieldConfig(clazz, fieldName);
			if (rmbfc != null) {
				return rmbfc;
			}
		}
		return null;
	}

	@Override
	public RowMapperTimeFieldConfig getRowMapperTimeFieldConfig(Class<?> clazz, String fieldName) {
		for (ConfigProvider configProvider : configProviderList) {
			RowMapperTimeFieldConfig rmtfc = configProvider.getRowMapperTimeFieldConfig(clazz, fieldName);
			if (rmtfc != null) {
				return rmtfc;
			}
		}
		return null;
	}

	@Override
	public RowMapperClassConfig getRowMapperClassConfig(Class<?> clazz) {
		for (ConfigProvider configProvider : configProviderList) {
			RowMapperClassConfig rmcc = configProvider.getRowMapperClassConfig(clazz);
			if (rmcc != null) {
				return rmcc;
			}
		}
		return null;
	}

	@Override
	public RowMapperFieldConfig getRowMapperFieldConfig(Field field) {
		return getRowMapperFieldConfig(field.getDeclaringClass(), field.getName());
	}

	@Override
	public RowMapperObjectFieldConfig getRowMapperObjectFieldConfig(Field field) {
		return getRowMapperObjectFieldConfig(field.getDeclaringClass(), field.getName());
	}

	@Override
	public RowMapperEnumFieldConfig getRowMapperEnumFieldConfig(Field field) {
		return getRowMapperEnumFieldConfig(field.getDeclaringClass(), field.getName());
	}

	@Override
	public RowMapperClobFieldConfig getRowMapperClobFieldConfig(Field field) {
		return getRowMapperClobFieldConfig(field.getDeclaringClass(), field.getName());
	}

	@Override
	public RowMapperBlobFieldConfig getRowMapperBlobFieldConfig(Field field) {
		return getRowMapperBlobFieldConfig(field.getDeclaringClass(), field.getName());
	}

	@Override
	public RowMapperTimeFieldConfig getRowMapperTimeFieldConfig(Field field) {
		return getRowMapperTimeFieldConfig(field.getDeclaringClass(), field.getName());
	}
	
}
