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

package org.springframework.jdbc.roma.impl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreField;

/**
 * @author Serkan ÖZAL
 */
public class RowMapperUtil {

	private static final Logger logger = Logger.getLogger(RowMapperUtil.class);
	
	private RowMapperUtil() {
		
	}
	
	public static String getRowMapperArgumentExpression(int argNo) {
		return "$" + argNo;	
	}
	
	public static String generateRandomClassPostFix() {
		return UUID.randomUUID().toString().replace("-", "-");
	}
	
	public static String getColumnClassName(ResultSet rs, String columnName) {
		try {
			ResultSetMetaData metadata = rs.getMetaData();
			int columnIndex = rs.findColumn(columnName);
			return metadata.getColumnClassName(columnIndex);
		}
		catch (Throwable t) {
			logger.error("Unable to get column class name for column " + columnName, t);
			return null;
		}
	}
	
	public static String generateGetterMethodName(Field field) {
		String fieldName = field.getName();
		return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}
	
	public static String generateSetterMethodName(Field field) {
		String fieldName = field.getName();
		return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
	}
	
	public static Class<?> getFieldTypeFromSetterMethod(Field field, Class<?> clazz) {
		String setterMethodName = generateSetterMethodName(field);
		Method setterMethod = ReflectionUtil.getMethod(clazz, setterMethodName);
		if (setterMethod == null) {
			return null;
		}
		Class<?>[] parameterTypes = setterMethod.getParameterTypes();
		if (parameterTypes.length != 1) {
			return null;
		}
		return parameterTypes[0];
	}
	
	public static List<Field> getAllRowMapperFields(Class<?> cls) {
		List<Field> fields = ReflectionUtil.getAllFields(cls, false, false, false, null);
		if (fields != null) {
			List<Field> processedFields = new ArrayList<Field>();
			for (Field field : fields) {
				if (field.isAnnotationPresent(RowMapperIgnoreField.class) == false) {
					processedFields.add(field);
				}
			}
			return processedFields;	
		}
		else {
			return null;
		}
	}
	
	public static List<String> generatePossibleColumnNames(Field f) {
		return generatePossibleNames(f.getName());
	}
	
	public static List<String> generatePossibleTableNames(Class<?> clazz) {
		return generatePossibleNames(clazz.getSimpleName());
	}
	
	public static List<String> generatePossibleNames(String name) {
		StringBuilder name1 = new StringBuilder();
		StringBuilder name2 = new StringBuilder();
		StringBuilder name3 = new StringBuilder();
		StringBuilder name4 = new StringBuilder();
		StringBuilder name5 = new StringBuilder();
		StringBuilder name6 = new StringBuilder();
		
		boolean previousIsLowerCase = false;
		boolean previousIsAlphabetic = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			
			boolean currentIsLowerCase = Character.isLowerCase(c);
			boolean currentIsAlphabetic = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
			
			name1.append(c);
			name2.append(Character.toLowerCase(c));
			name3.append(Character.toUpperCase(c));
			
			if (	(previousIsAlphabetic && currentIsAlphabetic) &&
					previousIsLowerCase  == true && 
					currentIsLowerCase == false) {
				name4.append("_");
				name5.append("_");
				name6.append("_");
			}
			
			name4.append(c);
			name5.append(Character.toLowerCase(c));
			name6.append(Character.toUpperCase(c));
			
			previousIsLowerCase = currentIsLowerCase;
			previousIsAlphabetic = currentIsAlphabetic;
		}

		return 
			Arrays.asList(
					name1.toString(), 
					name2.toString(), 
					name3.toString(),
					name4.toString(),
					name5.toString(),
					name6.toString());
	}
	
	public static String generateGetInstanceCode(Class<?> clazz, boolean single) {
		return
			"(" +
				"(" + clazz.getName() + ")" +
				"InstanceUtil.getInstance" + "(" + clazz.getName() + ".class" + ", " + single + ")" + 
			")";
	}
	
	public static String generateGetPrototypeInstanceCode(Class<?> clazz) {
		return
			"(" +
				"(" + clazz.getName() + ")" +
				"InstanceUtil.getSingleInstance" + "(" + clazz.getName() + ".class" + ")" + 
			")";
	}
	
	public static String generateGetSingleInstanceCode(Class<?> clazz) {
		return
			"(" +
				"(" + clazz.getName() + ")" +
				"InstanceUtil.getSingleInstance" + "(" + clazz.getName() + ".class" + ")" + 
			")";
	}
	
	public static String indent(String code) {
		return "\t" + code.trim().replaceAll("\n", "\n\t");
	}

}
