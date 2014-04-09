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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.NumericEnumMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.StringEnumMapper;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperEnumFieldConfig;
import org.springframework.jdbc.roma.impl.util.ReflectionUtil;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;
import org.springframework.util.StringUtils;

/**
 * @author Serkan ÖZAL
 */
public class EnumFieldRowMapperGenerator<T> extends AbstractRowMapperFieldGenerator<T> {

	protected Object[] enumConstants;
	protected RowMapperEnumFieldConfig rmefc;
	protected EnumMapperGenerator enumMapperGenerator;
	
	protected Map<Integer, String> constantsAndMaps;
	protected boolean useStringValue;
	protected int enumStartValue;
	protected int defaultIndex;
	protected String defaultValue;
	protected Map<Integer, String> numericValueStringMappings;
	protected Map<String, String> stringValueStringMappings;
	protected Map<Integer, Integer> numericValueNumericMappings;
	protected Map<String, Integer> stringValueNumericMappings;
	@SuppressWarnings("rawtypes")
	protected Class<? extends NumericEnumMapper> numericMapperClass;
	@SuppressWarnings("rawtypes")
	protected Class<? extends StringEnumMapper> stringMapperClass;
	
	public EnumFieldRowMapperGenerator(Field field, ConfigManager configManager) {
		super(field, configManager);
		init();
	}
	
	protected void init() {
		enumConstants = fieldCls.getEnumConstants();
		rmefc = configManager.getRowMapperEnumFieldConfig(field);
		if (rmefc != null) {
			constantsAndMaps = rmefc.getConstantsAndMaps();
			useStringValue = rmefc.isUseStringValue();
			enumStartValue = rmefc.getEnumStartValue();
			defaultIndex = rmefc.getDefaultIndex();
			defaultValue = rmefc.getDefaultValue();
			numericValueStringMappings = rmefc.getNumericValueStringMappings();
			stringValueStringMappings = rmefc.getStringValueStringMappings();
			numericValueNumericMappings = rmefc.getNumericValueNumericMappings();
			stringValueNumericMappings = rmefc.getStringValueNumericMappings();
			numericMapperClass = rmefc.getNumericMapperClass();
			stringMapperClass = rmefc.getStringMapperClass();
			
			if (constantsAndMaps != null && constantsAndMaps.isEmpty() == false) {
				enumMapperGenerator = new ConstantsAndMapsGenerator(constantsAndMaps);
			}
			else if (enumStartValue > 0) {
				enumMapperGenerator = new SpecifiedEnumStartValueGenerator(enumStartValue);
			}
			else if (numericValueNumericMappings != null && numericValueNumericMappings.isEmpty() == false) {
				enumMapperGenerator = new NumericValueNumericMappingGenerator(numericValueNumericMappings);
			}
			else if (numericValueStringMappings != null && numericValueStringMappings.isEmpty() == false) {	
				enumMapperGenerator = new NumericValueStringMappingGenerator(numericValueStringMappings);
			}
			else if (stringValueNumericMappings != null && stringValueNumericMappings.isEmpty() == false) {
				enumMapperGenerator = new StringValueNumericMappingGenerator(stringValueNumericMappings);
			}
			else if (stringValueStringMappings != null && stringValueStringMappings.isEmpty() == false) {
				enumMapperGenerator = new StringValueStringMappingGenerator(stringValueStringMappings);
			}
			else if (numericMapperClass != null && numericMapperClass.equals(NumericEnumMapper.class) == false) {
				enumMapperGenerator = new NumericMapperGenerator(numericMapperClass);
			}
			else if (stringMapperClass != null && stringMapperClass.equals(NumericEnumMapper.class) == false) {
				enumMapperGenerator = new StringMapperGenerator(stringMapperClass);
			}
		}
		if (enumMapperGenerator == null) {
			enumMapperGenerator = new DefaultEnumMapperGenerator(useStringValue);
		}
	}
	
	@Override
	public String doFieldMapping(Field f) {
		String setterMethodName = getSetterMethodName(f);
		
		if (enumConstants == null) {
			return null;
		}
		
		return 
			wrapWithExceptionHandling(
				wrapWithNullCheckByConsideringDefaultValue(
					enumMapperGenerator.generate(f, columnName, setterMethodName), 
					setterMethodName));
	}
	
	protected String wrapWithNullCheckByConsideringDefaultValue(String generated, String setterMethodName) {
		if (ReflectionUtil.isPrimitiveType(field.getType())) {
			return generated;
		}
		else {
			String defaultExpr = null;
			if (defaultIndex >= 0) {
				defaultExpr = field.getType().getName() + ".values()" + "[" + defaultIndex + "]";
			}
			else if (StringUtils.isEmpty(defaultValue) == false) {
				defaultExpr = field.getType().getName() + ".valueOf" + "(" + "\"" + defaultValue + "\"" + ")";
			}
			if (defaultExpr == null) {
				return super.wrapWithNullCheck(generated, setterMethodName);
			}
			else {
				return super.wrapWithNullCheck(generated, setterMethodName, defaultExpr);
			}	
		}	
	}
	
	protected interface EnumMapperGenerator {
		
		String generate(Field field, String columnName, String setterMethodName);
		
	}
	
	protected class DefaultEnumMapperGenerator implements EnumMapperGenerator {
		
		protected boolean useStringValue;
		
		protected DefaultEnumMapperGenerator(boolean useStringValue) {
			this.useStringValue = useStringValue;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			if (useStringValue) {
				String stringValueExpr = RESULT_SET_ARGUMENT + ".getString(\"" + columnName + "\")";
				stringValueExpr = field.getType().getName() + ".valueOf" + "(" + stringValueExpr + ")";
				return GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + stringValueExpr + ");";
			}
			else {
				String numericValueExpr = RESULT_SET_ARGUMENT + ".getInt(\"" + columnName + "\")";
				numericValueExpr = field.getType().getName() + ".values()" + "[" + numericValueExpr + "]";
				return GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + numericValueExpr + ");";
			}	
		}
		
	}
	
	protected class SpecifiedEnumStartValueGenerator implements EnumMapperGenerator {
		
		protected int startValue;
		
		protected SpecifiedEnumStartValueGenerator(int startValue) {
			this.startValue = startValue;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			String indexVariableName = field.getName() + "IndexVal";
			String indexVariableDefinition = 
						"int " + indexVariableName + " = " +  
								RESULT_SET_ARGUMENT + ".getInt(\"" + columnName + "\")" + " - " + startValue + ";";
			String numericValueExpr = field.getType().getName() + ".values()" + "[" + indexVariableName + "]";
			return 
					indexVariableDefinition + "\n" + 
					GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + numericValueExpr + ");";
		}
		
	}	
	
	protected class ConstantsAndMapsGenerator implements EnumMapperGenerator {
		
		protected Map<Integer, String> constantsAndMaps;
		
		protected ConstantsAndMapsGenerator(Map<Integer, String> constantsAndMaps) {
			this.constantsAndMaps = constantsAndMaps;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			StringBuilder sb = new StringBuilder();
			
			String numericValueProvideExpr = "Integer.valueOf" + "(" + RESULT_SET_ARGUMENT + ".getInt(\"" + columnName + "\")" + ")";
			String valueFieldName = field.getName() + "IntVal";
			
			sb.
				append("Integer " + valueFieldName).
				append(" = ").
				append(numericValueProvideExpr).
				append(";").append("\n");
			
			Set<Integer> constantSet = new TreeSet<Integer>(constantsAndMaps.keySet());
			boolean added = false;
			
			for (Integer constant : constantSet) {
				String map = constantsAndMaps.get(constant);
				
				if (added) {
					sb.append("else ");
				}
				
				sb.
					append("if ").
					append("(").
						append(valueFieldName + ".equals" + "(" + "Integer.valueOf" + "(" + constant + ")" + ")").
					append(")").append("\n").
					append("{").append("\n").
					append("\t").append(GENERATED_OBJECT_NAME + "." + setterMethodName).
						append("(").
							append(fieldCls.getName() + ".valueOf" + "(" + "\"" + map + "\"" + ")").
						append(")").append(";").append("\n").
					append("}").append("\n");
				
				added = true;
			}
			
			injectDefaultExpressionAsElseStatementIfNeeded(sb, setterMethodName);
			
			return sb.toString();
		}
		
	}
	
	protected void injectDefaultExpressionAsElseStatementIfNeeded(StringBuilder sb, String setterMethodName) {
		if (defaultIndex >= 0) {
			sb.
				append("else").
				append("{").append("\n").
				append("\t").append(GENERATED_OBJECT_NAME + "." + setterMethodName).
					append("(").
						append(fieldCls.getName() + ".values()" + "[" + defaultIndex + "]").
					append(")").append(";").append("\n").
				append("}").append("\n");
		}
		else if (StringUtils.isEmpty(defaultValue) == false) {
			sb.
				append("else").
				append("{").append("\n").
				append("\t").append(GENERATED_OBJECT_NAME + "." + setterMethodName).
					append("(").
						append(fieldCls.getName() + ".valueOf" + "(" + "\"" + defaultIndex + "\"" + ")").
					append(")").append(";").append("\n").
				append("}").append("\n");
		}
	}
	
	protected class NumericValueNumericMappingGenerator implements EnumMapperGenerator {
		
		protected Map<Integer, Integer> numericValueNumericMappings;
		
		protected NumericValueNumericMappingGenerator(Map<Integer, Integer> numericValueNumericMappings) {
			this.numericValueNumericMappings = numericValueNumericMappings;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			StringBuilder sb = new StringBuilder();
			
			String numericValueProvideExpr = "Integer.valueOf" + "(" + RESULT_SET_ARGUMENT + ".getInt(\"" + columnName + "\")" + ")";
			String valueFieldName = field.getName() + "IntVal";
			
			sb.
				append("Integer " + valueFieldName).
				append(" = ").
				append(numericValueProvideExpr).
				append(";").append("\n");
			
			Set<Integer> constantSet = new TreeSet<Integer>(numericValueNumericMappings.keySet());
			boolean added = false;
			
			for (Integer constant : constantSet) {
				Integer map = numericValueNumericMappings.get(constant);
				
				if (added) {
					sb.append("else ");
				}
				
				sb.
					append("if ").
					append("(").
						append(valueFieldName + ".equals" + "(" + "Integer.valueOf" + "(" + constant + ")" + ")").
					append(")").append("\n").
					append("{").append("\n").
					append("\t").append(GENERATED_OBJECT_NAME + "." + setterMethodName).
						append("(").
							append(fieldCls.getName() + ".values()" + "[" + map + "]").
						append(")").append(";").append("\n").
					append("}").append("\n");
				
				added = true;
			}
			
			injectDefaultExpressionAsElseStatementIfNeeded(sb, setterMethodName);
			
			return sb.toString();
		}
		
	}
	
	protected class NumericValueStringMappingGenerator implements EnumMapperGenerator {
		
		protected Map<Integer, String> numericValueStringMappings;
		
		protected NumericValueStringMappingGenerator(Map<Integer, String> numericValueStringMappings) {
			this.numericValueStringMappings = numericValueStringMappings;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			StringBuilder sb = new StringBuilder();
			
			String numericValueProvideExpr = "Integer.valueOf" + "(" + RESULT_SET_ARGUMENT + ".getInt(\"" + columnName + "\")" + ")";
			String valueFieldName = field.getName() + "IntVal";
			
			sb.
				append("Integer " + valueFieldName).
				append(" = ").
				append(numericValueProvideExpr).
				append(";").append("\n");
			
			Set<Integer> constantSet = new TreeSet<Integer>(numericValueStringMappings.keySet());
			boolean added = false;
			
			for (Integer constant : constantSet) {
				String map = numericValueStringMappings.get(constant);
				
				if (added) {
					sb.append("else ");
				}
				
				sb.
					append("if ").
					append("(").
						append(valueFieldName + ".equals" + "(" + "Integer.valueOf" + "(" + constant + ")" + ")").
					append(")").append("\n").
					append("{").append("\n").
					append("\t").append(GENERATED_OBJECT_NAME + "." + setterMethodName).
						append("(").
							append(fieldCls.getName() + ".valueOf" + "(" + "\"" + map + "\"" + ")").
						append(")").append(";").append("\n").
					append("}").append("\n");
				
				added = true;
			}
			
			injectDefaultExpressionAsElseStatementIfNeeded(sb, setterMethodName);
			
			return sb.toString();
		}
		
	}
	
	protected class StringValueNumericMappingGenerator implements EnumMapperGenerator {
		
		protected Map<String, Integer> stringValueNumericMappings;
		
		protected StringValueNumericMappingGenerator(Map<String, Integer> stringValueNumericMappings) {
			this.stringValueNumericMappings = stringValueNumericMappings;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			StringBuilder sb = new StringBuilder();
			
			String stringValueProvideExpr = RESULT_SET_ARGUMENT + ".getString(\"" + columnName + "\")";
			String valueFieldName = field.getName() + "StrVal";
			
			sb.
				append("String " + valueFieldName).
				append(" = ").
				append(stringValueProvideExpr).
				append(";").append("\n");
			

			boolean added = false;
			
			for (String constant : stringValueNumericMappings.keySet()) {
				Integer map = stringValueNumericMappings.get(constant);
				
				if (added) {
					sb.append("else ");
				}
				
				sb.
					append("if ").
					append("(").
						append(valueFieldName + ".equals" + "(" + "\"" + constant + "\"" + ")").
					append(")").append("\n").
					append("{").append("\n").
					append("\t").append(GENERATED_OBJECT_NAME + "." + setterMethodName).
						append("(").
							append(fieldCls.getName() + ".values()" + "[" + map + "]").
						append(")").append(";").append("\n").
					append("}").append("\n");
				
				added = true;
			}
			
			injectDefaultExpressionAsElseStatementIfNeeded(sb, setterMethodName);
			
			return sb.toString();
		}
		
	}
	
	protected class StringValueStringMappingGenerator implements EnumMapperGenerator {
		
		protected Map<String, String> stringValueStringMappings;
		
		protected StringValueStringMappingGenerator(Map<String, String> stringValueStringMappings) {
			this.stringValueStringMappings = stringValueStringMappings;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			StringBuilder sb = new StringBuilder();
			
			String stringValueProvideExpr = RESULT_SET_ARGUMENT + ".getString(\"" + columnName + "\")";
			String valueFieldName = field.getName() + "StrVal";
			
			sb.
				append("String " + valueFieldName).
				append(" = ").
				append(stringValueProvideExpr).
				append(";").append("\n");
			
			boolean added = false;
			
			for (String constant : stringValueStringMappings.keySet()) {
				String map = stringValueStringMappings.get(constant);
				
				if (added) {
					sb.append("else ");
				}
				
				sb.
					append("if ").
					append("(").
						append(valueFieldName + ".equals" + "(" + "\"" + constant + "\"" + ")").
					append(")").append("\n").
					append("{").append("\n").
					append("\t").append(GENERATED_OBJECT_NAME + "." + setterMethodName).
						append("(").
							append(fieldCls.getName() + ".valueOf" + "(" + "\"" + map + "\"" + ")").
						append(")").append(";").append("\n").
					append("}").append("\n");
				
				added = true;
			}
			
			injectDefaultExpressionAsElseStatementIfNeeded(sb, setterMethodName);
			
			return sb.toString();
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	protected class NumericMapperGenerator implements EnumMapperGenerator {

		protected Class<? extends NumericEnumMapper> numericMapperClass;
		
		protected NumericMapperGenerator(Class<? extends NumericEnumMapper> numericMapperClass) {
			this.numericMapperClass = numericMapperClass;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			String numericValueExpr = 
					"Integer.valueOf" + "(" + RESULT_SET_ARGUMENT + ".getInt(\"" + columnName + "\")" + ")";
			return 
				GENERATED_OBJECT_NAME + "." + setterMethodName + 
				"(" +
					"(" + field.getType().getName() + ")" + 
					"(" +
						RowMapperUtil.generateGetSingleInstanceCode(numericMapperClass) +
						".map" + "(" + numericValueExpr + ")" +
					")" + 
				");";
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	protected class StringMapperGenerator implements EnumMapperGenerator {
		
		protected Class<? extends StringEnumMapper> stringMapperClass;
		
		protected StringMapperGenerator(Class<? extends StringEnumMapper> stringMapperClass) {
			this.stringMapperClass = stringMapperClass;
		}
		
		@Override
		public String generate(Field field, String columnName, String setterMethodName) {
			String stringValueExpr = RESULT_SET_ARGUMENT + ".getString(\"" + columnName + "\")";
			return 
				GENERATED_OBJECT_NAME + "." + setterMethodName + 
				"(" +
					"(" + field.getType().getName() + ")" + 
					"(" +
						RowMapperUtil.generateGetSingleInstanceCode(stringMapperClass) +
						".map" + "(" + stringValueExpr + ")" +
					")" +
				");";
		}
		
	}

}
