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
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClobFieldConfig;

/**
 * @author Serkan ÖZAL
 */
public class StringFieldRowMapperGenerator<T> extends AbstractRowMapperFieldGenerator<T> {

	public StringFieldRowMapperGenerator(Field field, ConfigManager configManager) {
		super(field, configManager);
	}

	@Override
	public String doFieldMapping(Field f) {
		String setterMethodName = getSetterMethodName(f);
		RowMapperClobFieldConfig clobFieldConfig = configManager.getRowMapperClobFieldConfig(f);
		if (clobFieldConfig != null) {
			String clobFieldName = "clob_" + f.getName();
			return		
				"Clob " + clobFieldName + " = " + 
					RESULT_SET_ARGUMENT + ".getClob(\"" + columnName + "\");" + "\n" + 
					"\t" + "if (" + clobFieldName + " != null)" + "\n" +
					"\t" + "{" + "\n" +
					"\t" + "\t" + GENERATED_OBJECT_NAME + "." + 
								setterMethodName +
								"(" +
									clobFieldName + ".getSubString(1L, (int)" + clobFieldName + ".length())" +
								");"+ "\n" +
					"\t" + "}";						
		}
		else {
			return 
				GENERATED_OBJECT_NAME + "." + 
					setterMethodName +
					"(" +
						RESULT_SET_ARGUMENT + ".getString(\"" + columnName + "\")" +
					");";
		}	
	}
	
}
