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

/**
 * @author Serkan ÖZAL
 */
public class BlobFieldRowMapperGenerator<T> extends AbstractRowMapperFieldGenerator<T> {

	public BlobFieldRowMapperGenerator(Field field, ConfigManager configManager) {
		super(field, configManager);
	}

	@Override
	public String doFieldMapping(Field f) {
		String setterMethodName = getSetterMethodName(f);
		Class<?> fieldCls = f.getType();
		Class<?> compType = fieldCls.getComponentType();
		if (fieldCls.isArray() && (compType.equals(byte.class) || compType.equals(Byte.class))) {
			String blobFieldName = "blob_" + f.getName();
			return		
				"Blob " + blobFieldName + " = " + 
					RESULT_SET_ARGUMENT + ".getBlob(\"" + columnName + "\");" + "\n" + 
					"\t" + "if (" + blobFieldName + " != null)" + "\n" +
					"\t" + "{" + "\n" +
					"\t" + "\t" + GENERATED_OBJECT_NAME + "." + 
								setterMethodName +
								"(" +
									blobFieldName + ".getBytes(1L, (int)" + blobFieldName + ".length())" +
								");" + "\n" +
					"\t" + "}";									
		}
		else {
			logger.error("Blob field types can only be \"byte[]\" or \"Byte[]\" !");
			return "";
		}	
	}

}
