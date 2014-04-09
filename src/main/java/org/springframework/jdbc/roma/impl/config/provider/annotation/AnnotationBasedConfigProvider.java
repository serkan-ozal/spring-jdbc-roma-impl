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

package org.springframework.jdbc.roma.impl.config.provider.annotation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.roma.api.config.provider.ConfigProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperBlobField;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClobField;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperCustomProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.NumericEnumMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.StringEnumMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumAutoMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumNumericMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumNumericValueNumericMapping;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumNumericValueStringMapping;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumStringMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumStringValueNumericMapping;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumStringValueStringMapping;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperExpressionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition.RowMapperCustomIgnoreConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition.RowMapperExpressionBasedIgnoreConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition.RowMapperIgnoreConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition.RowMapperPropertyBasedIgnoreConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperCustomLazyConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperExpressionBasedLazyConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperPropertyBasedLazyConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperLazyConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperField;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyLoadCondition;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyLoadCondition.RowMapperCustomLazyLoadConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyLoadCondition.RowMapperExpressionBasedLazyLoadConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyLoadCondition.RowMapperLazyLoadConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyLoadCondition.RowMapperPropertyBasedLazyLoadConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperObjectField;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperTimeField;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperBlobFieldConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperClassConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperClobFieldConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperCustomProviderConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperEnumFieldConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperExpressionProviderConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperFieldConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperIgnoreConditionConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperLazyConditionConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperLazyLoadConditionConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperObjectFieldConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperSqlProviderConfigBuilder;
import org.springframework.jdbc.roma.api.domain.builder.config.RowMapperTimeFieldConfigBuilder;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperBlobFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClassConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClobFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperEnumFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperIgnoreConditionConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperLazyConditionConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperLazyLoadConditionConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperObjectFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperTimeFieldConfig;
import org.springframework.jdbc.roma.impl.util.ReflectionUtil;
import org.springframework.util.StringUtils;

/**
 * @author Serkan ÖZAL
 */
public class AnnotationBasedConfigProvider implements ConfigProvider {

	@Override
	public boolean isAvailable() {
		return true;
	}
	
	@Override
	public RowMapperFieldConfig getRowMapperFieldConfig(Class<?> clazz, String fieldName) {
		Field field = ReflectionUtil.getField(clazz, fieldName);
		if (field.isAnnotationPresent(RowMapperField.class)) {
			RowMapperField rmf = field.getAnnotation(RowMapperField.class);
			return 
				new RowMapperFieldConfigBuilder().
						field(field).
						columnName(rmf.columnName()).	
						fieldGeneratorClass(rmf.fieldGenerator()).
						fieldMapperClass(rmf.fieldMapper()).
					build();	
		}
		else {
			return null;
		}	
	}

	@Override
	public RowMapperObjectFieldConfig getRowMapperObjectFieldConfig(Class<?> clazz, String fieldName) {
		Field field = ReflectionUtil.getField(clazz, fieldName);
		if (field.isAnnotationPresent(RowMapperObjectField.class)) {
			RowMapperObjectField rmof = field.getAnnotation(RowMapperObjectField.class);
			
			RowMapperExpressionProvider rmExpressionProvider = rmof.provideViaExpressionProvider();
			RowMapperSqlProvider rmSqlProvider = rmof.provideViaSqlProvider();
			RowMapperCustomProvider rmCustomProvider = rmof.provideViaCustomProvider();
			RowMapperLazyCondition rmLazyCondition = rmof.lazyCondition();
			RowMapperLazyLoadCondition rmLazyLoadCondition = rmof.lazyLoadCondition();
			RowMapperIgnoreCondition rmIgnoreCondition = rmof.ignoreCondition();
			RowMapperLazyConditionConfig rmLazyConditionConfig = null;
			boolean rmLazyConditionConfigExist = false;
			RowMapperLazyLoadConditionConfig rmLazyLoadConditionConfig = null;
			boolean rmLazyLoadConditionConfigExist = false;
			RowMapperIgnoreConditionConfig rmIgnoreConditionConfig = null;
			boolean rmIgnoreConditionConfigExist = false;
			if (rmLazyCondition != null) {
				RowMapperPropertyBasedLazyConditionProvider propertyBasedLazyConditionProvider = 
						rmLazyCondition.provideViaPropertyBasedProvider();
				RowMapperExpressionBasedLazyConditionProvider expressionBasedLazyConditionProvider = 
						rmLazyCondition.provideViaExpressionBasedProvider();
				RowMapperCustomLazyConditionProvider customLazyConditionProvider =
						rmLazyCondition.provideViaCustomProvider();
				String propertyName = null;
				String expression = null;
				@SuppressWarnings("rawtypes")
				Class<? extends RowMapperLazyConditionProvider> lazyConditionProviderClass = null;
				if (propertyBasedLazyConditionProvider != null) {
					propertyName = propertyBasedLazyConditionProvider.propertyName();
					if (StringUtils.isEmpty(propertyName) == false) {
						rmLazyConditionConfigExist = true;
					}	
				}
				if (expressionBasedLazyConditionProvider != null) {
					expression = expressionBasedLazyConditionProvider.expression();
					if (StringUtils.isEmpty(expression) == false) {
						rmLazyConditionConfigExist = true;
					}	
				}
				if (customLazyConditionProvider != null) {
					lazyConditionProviderClass = customLazyConditionProvider.lazyConditionProvider();
					if (RowMapperLazyConditionProvider.class.equals(lazyConditionProviderClass) == false) {
						rmLazyConditionConfigExist = true;
					}
				}
				if (rmLazyConditionConfigExist) {
					rmLazyConditionConfig = 
							new RowMapperLazyConditionConfigBuilder().
									field(field).
									propertyName(propertyName).
									expression(expression).
									lazyConditionProviderClass(lazyConditionProviderClass).
								build();
				}
			}
			if (rmLazyLoadCondition != null) {
				RowMapperPropertyBasedLazyLoadConditionProvider propertyBasedLazyLoadConditionProvider = 
						rmLazyLoadCondition.provideViaPropertyBasedProvider();
				RowMapperExpressionBasedLazyLoadConditionProvider expressionBasedLazyLoadConditionProvider = 
						rmLazyLoadCondition.provideViaExpressionBasedProvider();
				RowMapperCustomLazyLoadConditionProvider customLazyLoadConditionProvider =
						rmLazyLoadCondition.provideViaCustomProvider();
				String propertyName = null;
				String expression = null;
				@SuppressWarnings("rawtypes")
				Class<? extends RowMapperLazyLoadConditionProvider> lazyLoadConditionProviderClass = null;
				if (propertyBasedLazyLoadConditionProvider != null) {
					propertyName = propertyBasedLazyLoadConditionProvider.propertyName();
					if (StringUtils.isEmpty(propertyName) == false) {
						rmLazyLoadConditionConfigExist = true;
					}	
				}
				if (expressionBasedLazyLoadConditionProvider != null) {
					expression = expressionBasedLazyLoadConditionProvider.expression();
					if (StringUtils.isEmpty(expression) == false) {
						rmLazyLoadConditionConfigExist = true;
					}	
				}
				if (customLazyLoadConditionProvider != null) {
					lazyLoadConditionProviderClass = customLazyLoadConditionProvider.lazyLoadConditionProvider();
					if (RowMapperLazyLoadConditionProvider.class.equals(lazyLoadConditionProviderClass) == false) {
						rmLazyLoadConditionConfigExist = true;
					}
				}
				if (rmLazyLoadConditionConfigExist) {
					rmLazyLoadConditionConfig = 
							new RowMapperLazyLoadConditionConfigBuilder().
									field(field).
									propertyName(propertyName).
									expression(expression).
									lazyLoadConditionProviderClass(lazyLoadConditionProviderClass).
								build();
				}
			}
			if (rmIgnoreCondition != null) {
				RowMapperPropertyBasedIgnoreConditionProvider propertyBasedIgnoreConditionProvider = 
						rmIgnoreCondition.provideViaPropertyBasedProvider();
				RowMapperExpressionBasedIgnoreConditionProvider expressionBasedIgnoreConditionProvider = 
						rmIgnoreCondition.provideViaExpressionBasedProvider();
				RowMapperCustomIgnoreConditionProvider customIgnoreConditionProvider =
						rmIgnoreCondition.provideViaCustomProvider();
				String propertyName = null;
				String expression = null;
				@SuppressWarnings("rawtypes")
				Class<? extends RowMapperIgnoreConditionProvider> ignoreConditionProviderClass = null;
				if (propertyBasedIgnoreConditionProvider != null) {
					propertyName = propertyBasedIgnoreConditionProvider.propertyName();
					if (StringUtils.isEmpty(propertyName) == false) {
						rmIgnoreConditionConfigExist = true;
					}	
				}
				if (expressionBasedIgnoreConditionProvider != null) {
					expression = expressionBasedIgnoreConditionProvider.expression();
					if (StringUtils.isEmpty(expression) == false) {
						rmIgnoreConditionConfigExist = true;
					}	
				}
				if (customIgnoreConditionProvider != null) {
					ignoreConditionProviderClass = customIgnoreConditionProvider.ignoreConditionProvider();
					if (RowMapperIgnoreConditionProvider.class.equals(ignoreConditionProviderClass) == false) {
						rmIgnoreConditionConfigExist = true;
					}
				}
				if (rmIgnoreConditionConfigExist) {
					rmIgnoreConditionConfig = 
							new RowMapperIgnoreConditionConfigBuilder().
									field(field).
									propertyName(propertyName).
									expression(expression).
									ignoreConditionProviderClass(ignoreConditionProviderClass).
								build();
				}
			}
			
			return 
				new RowMapperObjectFieldConfigBuilder().
						field(field).
						rowMapperExpressionProviderConfig(
								rmExpressionProvider != null ? 
										new RowMapperExpressionProviderConfigBuilder().
												expression(rmExpressionProvider.expression()).
												usedClasses(rmExpressionProvider.usedClasses()).
											build() :	
										null).
						rowMapperSqlProviderConfig(
								rmSqlProvider != null ? 
										new RowMapperSqlProviderConfigBuilder().
												field(field).
												provideSql(rmSqlProvider.provideSql()).
												dataSourceName(rmSqlProvider.dataSourceName()).
												entityType(rmSqlProvider.entityType()).
												sqlQueryInfoProviderClass(rmSqlProvider.sqlQueryInfoProvider()).
											build() : 
										null).				
						
						rowMapperCustomProviderConfig(
								rmCustomProvider != null ? 
										new RowMapperCustomProviderConfigBuilder().
												field(field).
												fieldProviderClass(rmCustomProvider.fieldProvider()).
											build() :	
										null).
						fieldType(rmof.fieldType()).
						lazy(rmof.lazy() || rmLazyConditionConfigExist || rmLazyLoadConditionConfigExist).
						rowMapperLazyConditionConfig(rmLazyConditionConfig).
						rowMapperLazyLoadConditionConfig(rmLazyLoadConditionConfig).
						rowMapperIgnoreConditionConfig(rmIgnoreConditionConfig).
					build();	
		}
		else {
			return null;
		}	
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Override
	public RowMapperEnumFieldConfig getRowMapperEnumFieldConfig(Class<?> clazz, String fieldName) {
		Field field = ReflectionUtil.getField(clazz, fieldName);
		if (field.isAnnotationPresent(RowMapperEnumField.class)) {
			RowMapperEnumField rmef = field.getAnnotation(RowMapperEnumField.class);
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			String[] constantsAndMapsArray = rmef.constantsAndMaps();
			Map<Integer, String> constantsAndMaps = new HashMap<Integer, String>();
			
			if (constantsAndMapsArray != null && constantsAndMapsArray.length > 0) {
				for (String constantAndMap : constantsAndMapsArray) {
					String[] constantAndMapParts = constantAndMap.split(":");
					String constant = constantAndMapParts[0];
					String map = constantAndMapParts[1];
					constantsAndMaps.put(Integer.parseInt(constant), map);
				}
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			boolean useStringValue = rmef.useStringValue();
			int enumStartValue = rmef.enumStartValue();
			int defaultIndex = rmef.defaultIndex();
			String defaultValue = rmef.defaultValue();
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			RowMapperEnumAutoMapper autoMapper = rmef.mapViaAutoMapper();
			
			Map<Integer, Integer> numericValueNumericMappings = null;
			RowMapperEnumNumericValueNumericMapping[] numericValueNumericMappingsArray = 
					autoMapper.mapViaNumericValueNumericMappings();
			if (numericValueNumericMappingsArray != null && numericValueNumericMappingsArray.length > 0) {
				numericValueNumericMappings = new HashMap<Integer, Integer>();
				for (RowMapperEnumNumericValueNumericMapping mapping : numericValueNumericMappingsArray) {
					numericValueNumericMappings.put(mapping.value(), mapping.mappingIndex());
				}
			}
			
			Map<Integer, String> numericValueStringMappings = null;
			RowMapperEnumNumericValueStringMapping[] numericValueStringMappingsArray = 
					autoMapper.mapViaNumericValueStringMappings();
			if (numericValueStringMappingsArray != null && numericValueStringMappingsArray.length > 0) {
				numericValueStringMappings = new HashMap<Integer, String>();
				for (RowMapperEnumNumericValueStringMapping mapping : numericValueStringMappingsArray) {
					numericValueStringMappings.put(mapping.value(), mapping.mappingValue());
				}
			}
			
			Map<String, Integer> stringValueNumericMappings = null;
			RowMapperEnumStringValueNumericMapping[] stringValueNumericMappingsArray = 
					autoMapper.mapViaStringValueNumericMappings();
			if (stringValueNumericMappingsArray != null && stringValueNumericMappingsArray.length > 0) {
				stringValueNumericMappings = new HashMap<String, Integer>();
				for (RowMapperEnumStringValueNumericMapping mapping : stringValueNumericMappingsArray) {
					stringValueNumericMappings.put(mapping.value(), mapping.mappingIndex());
				}
			}
			
			Map<String, String> stringValueStringMappings = null;
			RowMapperEnumStringValueStringMapping[] stringValueStringMappingsArray = 
					autoMapper.mapViaStringValueStringMappings();
			if (stringValueStringMappingsArray != null && stringValueStringMappingsArray.length > 0) {
				stringValueStringMappings = new HashMap<String, String>();
				for (RowMapperEnumStringValueStringMapping mapping : stringValueStringMappingsArray) {
					stringValueStringMappings.put(mapping.value(), mapping.mappingValue());
				}
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			RowMapperEnumNumericMapper numericMapper = rmef.mapViaNumericMapper();
			Class<? extends NumericEnumMapper> numericMapperClass = numericMapper.mapper();
			if (numericMapperClass.equals(NumericEnumMapper.class)) {
				numericMapperClass = null;
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			RowMapperEnumStringMapper stringMapper = rmef.mapViaStringMapper();
			Class<? extends StringEnumMapper> stringMapperClass = stringMapper.mapper();
			if (stringMapperClass.equals(StringEnumMapper.class)) {
				stringMapperClass = null;
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			
			return 
				new RowMapperEnumFieldConfigBuilder().
						field(field).
						constantsAndMaps(constantsAndMaps).
						useStringValue(useStringValue).
						enumStartValue(enumStartValue).
						defaultIndex(defaultIndex).
						defaultValue(defaultValue).
						numericValueNumericMappings(numericValueNumericMappings).
						numericValueStringMappings(numericValueStringMappings).
						stringValueNumericMappings(stringValueNumericMappings).
						stringValueStringMappings(stringValueStringMappings).
						numericMapperClass(numericMapperClass).
						stringMapperClass(stringMapperClass).
					build();	
		}
		else {
			return null;
		}	
	}

	@Override
	public RowMapperClobFieldConfig getRowMapperClobFieldConfig(Class<?> clazz, String fieldName) {
		Field field = ReflectionUtil.getField(clazz, fieldName);
		if (field.isAnnotationPresent(RowMapperClobField.class)) {
			return 
				new RowMapperClobFieldConfigBuilder().
						field(field).
					build();	
		}
		else {
			return null;
		}	
	}

	@Override
	public RowMapperBlobFieldConfig getRowMapperBlobFieldConfig(Class<?> clazz, String fieldName) {
		Field field = ReflectionUtil.getField(clazz, fieldName);
		if (field.isAnnotationPresent(RowMapperBlobField.class)) {
			return 
				new RowMapperBlobFieldConfigBuilder().
						field(field).
					build();	
		}
		else {
			return null;
		}	
	}

	@Override
	public RowMapperTimeFieldConfig getRowMapperTimeFieldConfig(Class<?> clazz, String fieldName) {
		Field field = ReflectionUtil.getField(clazz, fieldName);
		if (field.isAnnotationPresent(RowMapperTimeField.class)) {
			RowMapperTimeField rmtf = clazz.getAnnotation(RowMapperTimeField.class);
			return 
				new RowMapperTimeFieldConfigBuilder().
						field(field).
						asTimestamp(rmtf.asTimestamp()).
					build();	
		}
		else {
			return null;
		}	
	}

	@Override
	public RowMapperClassConfig getRowMapperClassConfig(Class<?> clazz) {
		if (clazz.isAnnotationPresent(RowMapperClass.class)) {
			RowMapperClass rmc = clazz.getAnnotation(RowMapperClass.class);
			return 
				new RowMapperClassConfigBuilder().
						clazz(clazz).
						fieldGeneratorFactoryClass(rmc.fieldGeneratorFactory()).
						objectCreaterClass(rmc.objectCreater()).
						objectProcessorClass(rmc.objectProcessor()).
						columnNameResolverClass(rmc.columnNameResolver()).
						tableNameResolverClass(rmc.tableNameResolver()).
						dataSourceName(rmc.dataSourceName()).
						schemaName(rmc.schemaName()).
						tableName(rmc.tableName()).
					build();	
		}
		else {
			return null;
		}	
	}

}
