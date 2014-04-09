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

import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;

/**
 * @author Serkan ÖZAL
 */
public class BooleanFieldRowMapperGenerator<T> extends AbstractRowMapperFieldGenerator<T> {

	public BooleanFieldRowMapperGenerator(Field field, ConfigManager configManager) {
		super(field, configManager);
	}

	@Override
	public String doFieldMapping(Field f) {
		String setterMethodName = getSetterMethodName(f);
		Class<?> paramType = RowMapperUtil.getFieldTypeFromSetterMethod(f, rowMapper.getClazz());
		if (paramType == null) {
			logger.warn("Expected setter method " + setterMethodName + " with exactly one parameter couldn't be found for field " + 
					field.getName() + " at class " + rowMapper.getClazz() + ". So this field will be ignored.");
			return "";
		}
		String setValueExpr = "false : true";
		if (paramType.equals(Boolean.class)) {
			setValueExpr = "Boolean.FALSE : Boolean.TRUE";
		}
		return 
			GENERATED_OBJECT_NAME + "." + 
				setterMethodName +
				"(" +
					RESULT_SET_ARGUMENT + ".getInt(\"" + columnName + "\") == 0 ? " + setValueExpr +
				");";
	}
}
