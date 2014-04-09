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
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperField.RowMapperFieldMapper;
import org.springframework.jdbc.roma.api.generator.RowMapperFieldGenerator;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;

/**
 * @author Serkan ÖZAL
 */
public class FieldMapperFieldRowMapperGenerator<T> extends AbstractRowMapperFieldGenerator<T> {

	@SuppressWarnings("rawtypes")
	private Class<? extends RowMapperFieldMapper> fieldMapperCls;
	
	@SuppressWarnings("rawtypes")
	public FieldMapperFieldRowMapperGenerator(Field field, ConfigManager configManager, 
			Class<? extends RowMapperFieldMapper> fieldMapperCls) {
		super(field, configManager);
		this.fieldMapperCls = fieldMapperCls;
	}

	@Override
	public String doFieldMapping(Field f) {
		return 
			RowMapperUtil.generateGetSingleInstanceCode(fieldMapperCls) + 
			".mapField" + 
			"(" +
				"(" + rowMapper.getClazz().getName() + ")" + RowMapperFieldGenerator.GENERATED_OBJECT_NAME + "," + 
				"\"" + f.getName() + "\"" + "," + 
				RowMapperFieldGenerator.RESULT_SET_ARGUMENT + ", " + 	
				RowMapperFieldGenerator.ROW_NUM_ARGUMENT +	
			");";
	}
	
}
