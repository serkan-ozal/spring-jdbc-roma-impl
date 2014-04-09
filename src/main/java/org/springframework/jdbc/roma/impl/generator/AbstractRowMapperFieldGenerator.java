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

package org.springframework.jdbc.roma.impl.generator;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperColumnNameResolver;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClassConfig;
import org.springframework.jdbc.roma.impl.GeneratedRowMapper;
import org.springframework.jdbc.roma.impl.resolver.DefaultColumnNameResolver;
import org.springframework.jdbc.roma.impl.util.InstanceUtil;
import org.springframework.jdbc.roma.impl.util.ReflectionUtil;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;
import org.springframework.util.StringUtils;

/**
 * @author Serkan ÖZAL
 */
public abstract class AbstractRowMapperFieldGenerator<T> implements RowMapperAwareFieldGenerator<T> {

	protected final Logger logger = Logger.getLogger(getClass());
	
	protected ConfigManager configManager;
	protected Field field;
	protected Class<?> fieldCls;
	protected String columnName;
	protected GeneratedRowMapper<T> rowMapper;
	protected RowMapperColumnNameResolver columnNameResolver = new DefaultColumnNameResolver();
	
	public AbstractRowMapperFieldGenerator(Field field, ConfigManager configManager) {
		field.setAccessible(true);
		this.field = field;
		this.configManager = configManager;
		this.fieldCls = field.getType();
		RowMapperClassConfig rmcc = configManager.getRowMapperClassConfig(field.getDeclaringClass());
		if (rmcc != null) {
			Class<? extends RowMapperColumnNameResolver> columnNameResolverClass = rmcc.getColumnNameResolverClass();
			if (columnNameResolverClass != null) {
				RowMapperColumnNameResolver cnr = InstanceUtil.getSingleInstance(columnNameResolverClass);
				if (cnr != null) {
					columnNameResolver = cnr;
				}
			}
		}
		this.columnName = columnNameResolver.resolveColumnName(field);
	}
	
	@Override
	public void assignedToRowMapper(GeneratedRowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	protected String getSetterMethodName(Field f) {
		return RowMapperUtil.generateSetterMethodName(f);
	}
	
	abstract protected String doFieldMapping(Field f);
	
	@Override
	public final String generateFieldMapping(Field f) {
		return doFieldMapping(f);
	}

	protected String wrapWithNullCheck(String generated, String setterMethodName) {
		if (ReflectionUtil.isPrimitiveType(field.getType())) {
			return generated;
		}
		else {
			return 
				"if " + "(" + RESULT_SET_ARGUMENT + ".wasNull()" + ")" + "\n" +
				"{" + "\n" +
				"\t" + GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + "null" + ");" + "\n" +
				"}" + "\n" +
				"else" + "\n" +
				"{" + "\n" +
					RowMapperUtil.indent(generated) + "\n" +
				"}" + "\n";
		}	
	}
	
	protected String wrapWithNullCheck(String generated, String setterMethodName, String defaultValueExpr) {
		if (StringUtils.isEmpty(defaultValueExpr)) {
			return wrapWithNullCheck(generated, setterMethodName);
		}
		else {
			return 
				"if " + "(" + RESULT_SET_ARGUMENT + ".wasNull()" + ")" + "\n" +
				"{" + "\n" +
				"\t" + GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + defaultValueExpr + ");" + "\n" +
				"}" + "\n" +
				"else" + "\n" +
				"{" + "\n" +
					RowMapperUtil.indent(generated) + "\n" +
				"}" + "\n";
		}	
	}
	
	protected String wrapWithExceptionHandling(String generated) {
		return 
			"try" + "\n" +
			"{" + "\n" +
			RowMapperUtil.indent(generated) + "\n" +
			"}" + "\n" +
			"catch (Throwable t)" + "\n" + 
			"{" + "\n" +
			"\t" + "t.printStackTrace();\n" +
			"}";
	}
	
}
