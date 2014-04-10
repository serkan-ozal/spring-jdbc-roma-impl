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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;

import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperCustomProvider.RowMapperFieldProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition.RowMapperIgnoreConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperLazyConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyLoadCondition.RowMapperLazyLoadConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider.RowMapperSqlQueryInfoProvider;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperCustomProviderConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperExpressionProviderConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperIgnoreConditionConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperLazyConditionConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperLazyLoadConditionConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperObjectFieldConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperSqlProviderConfig;
import org.springframework.jdbc.roma.api.el.RowMapperExpressionLanguage;
import org.springframework.jdbc.roma.api.generator.RowMapperFieldGenerator;
import org.springframework.jdbc.roma.impl.GeneratedRowMapper;
import org.springframework.jdbc.roma.impl.proxy.ProxyHelper;
import org.springframework.jdbc.roma.impl.proxy.ProxyListLoader;
import org.springframework.jdbc.roma.impl.proxy.ProxyMapLoader;
import org.springframework.jdbc.roma.impl.proxy.ProxyObjectLoader;
import org.springframework.jdbc.roma.impl.proxy.ProxySetLoader;
import org.springframework.jdbc.roma.impl.util.ReflectionUtil;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;
import org.springframework.jdbc.roma.impl.util.SpringUtil;
import org.springframework.util.StringUtils;

/**
 * @author Serkan ÖZAL
 */
public class ObjectFieldRowMapperGenerator<T> extends AbstractRowMapperFieldGenerator<T> implements RowMapperExpressionLanguage {

	public ObjectFieldRowMapperGenerator(Field field, ConfigManager configManager) {
		super(field, configManager);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String doFieldMapping(Field f) {
		RowMapperObjectFieldConfig rmofc = configManager.getRowMapperObjectFieldConfig(f);
		if (rmofc == null) {
			return "";
		}
		
		String setterMethodName = getSetterMethodName(f);
		
		RowMapperCustomProviderConfig rmcpc = rmofc.getRowMapperCustomProviderConfig();
		Class<? extends RowMapperFieldProvider> rmfpCls = rmcpc != null ? rmcpc.getFieldProviderClass() : null;
		if (rmfpCls != null && rmfpCls.equals(RowMapperFieldProvider.class) == false) {
			return 
				wrapWithExceptionHandling(
					wrapWithIgnoreConditionIfNeeded(f, rmofc, 
							getValueFromCustomProvider(f, rmofc, rmfpCls, setterMethodName)));
		}
		else {
			RowMapperExpressionProviderConfig rmExpressionProviderConfig = 
					rmofc.getRowMapperExpressionProviderConfig();
			String expression = rmExpressionProviderConfig != null ? 
									rmExpressionProviderConfig.getExpression() : null;
			if (StringUtils.isEmpty(expression) == false) {
				return 
					wrapWithExceptionHandling(
						wrapWithIgnoreConditionIfNeeded(f, rmofc, 	
								getValueFromExpression(f, rmofc, expression, setterMethodName)));
			}
			else {
				RowMapperSqlProviderConfig rmSqlProviderConfig = rmofc.getRowMapperSqlProviderConfig();
				String sqlCode = rmSqlProviderConfig != null ? rmSqlProviderConfig.getProvideSql() : null;
				Class<? extends RowMapperSqlQueryInfoProvider> sqlQueryInfoProviderClass = 
						rmSqlProviderConfig.getSqlQueryInfoProviderClass();
				if (StringUtils.isEmpty(sqlCode) == false ||
						(sqlQueryInfoProviderClass != null && 
							sqlQueryInfoProviderClass.equals(RowMapperSqlQueryInfoProvider.class) == false)) {
					return 
						wrapWithExceptionHandling(
							wrapWithIgnoreConditionIfNeeded(f, rmofc, 	
									getValueFromSqlCode(f, rmofc, 
											rmSqlProviderConfig, setterMethodName)));
				}
				else {
					return "";
				}	
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected String getValueFromCustomProvider(Field f, RowMapperObjectFieldConfig rmofc, 
			Class<? extends RowMapperFieldProvider> rmfpCls, String setterMethodName) {
		rowMapper.addAdditionalClass(f.getType());
		
		String
			customProviderCreationCode = 
				"(" +
					"(" + rmfpCls.getName() + ")" +
					"(" +
						RowMapperUtil.generateGetSingleInstanceCode(rmfpCls) +
					")" + 
				")";

		return 
				wrapWithLazyLoadingIfNeeded(f, setterMethodName, rmofc.isLazy(), rmofc, 
					GENERATED_OBJECT_NAME + "." + 
					setterMethodName + 
					"(" + 
						"(" + f.getType().getSimpleName() + ")" +
						customProviderCreationCode + ".provideField" + 
						"(" + 
							"(" + rowMapper.getClazz().getName() + ")" + RowMapperFieldGenerator.GENERATED_OBJECT_NAME + "," + 
							"\"" + f.getName() + "\"" + "," + 
							RowMapperFieldGenerator.RESULT_SET_ARGUMENT + ", " + 	
							RowMapperFieldGenerator.ROW_NUM_ARGUMENT +	
						")" +
					");", null, null);		
	}
	
	protected String getValueFromExpression(Field f, RowMapperObjectFieldConfig rmofc, 
			String expression, String setterMethodName) {
		StringBuilder variables = new StringBuilder();
		List<String> variableNames = new ArrayList<String>();
		expression = processExpression(expression, variables, variableNames);
		RowMapperExpressionProviderConfig rmepc = rmofc.getRowMapperExpressionProviderConfig();
		Class<?>[] usedClasses = rmepc.getUsedClasses();
		if (usedClasses != null) {
			for (Class<?> cls : usedClasses) {
				rowMapper.addAdditionalClass(cls);
			}	
		}
		
		return 
				wrapWithLazyLoadingIfNeeded(f, setterMethodName, rmofc.isLazy(),  rmofc, 
					GENERATED_OBJECT_NAME + "." + 
						setterMethodName + 
						"(" + 
							"(" + f.getType().getName() + ")" + expression +
						");", variables, variableNames);
	}
	
	@SuppressWarnings("rawtypes")
	protected String getValueFromSqlCode(Field f, RowMapperObjectFieldConfig rmofc, 
			RowMapperSqlProviderConfig rmSqlProviderConfig, String setterMethodName) {
		String sqlCode = rmSqlProviderConfig != null ? rmSqlProviderConfig.getProvideSql() : null;
		Class<? extends RowMapperSqlQueryInfoProvider> sqlQueryInfoProviderClass = 
				rmSqlProviderConfig.getSqlQueryInfoProviderClass();
		
		Class<?> fieldCls = f.getType();
		rowMapper.addAdditionalClass(f.getType());

		if (Collection.class.isAssignableFrom(fieldCls) && List.class.isAssignableFrom(fieldCls) == false) {
			logger.error("Only List type is supported for Collection typed fields " + "(field " + f.getName() + ")");
			return "";
		}
		
		Class<?> entityCls = rmofc.getRowMapperSqlProviderConfig().getEntityType();
		if (entityCls == null || entityCls.equals(Object.class)) {
			if (List.class.isAssignableFrom(fieldCls)) {
				logger.error("Entity type of List typed field named " + f.getName() + 
							 " must be declared for SQL provider");
				return "";
			}
			else {
				entityCls = fieldCls;
			}
		}
		
		String jdbcTemplateCode = "JdbcUtil.getJdbcTemplate" + "(" + "\"" + rmSqlProviderConfig.getDataSourceName() + "\"" + ")";
		
		String rowMapperCreationCode = 
				GeneratedRowMapper.class.getName() + ".provideRowMapper" + 
						"(" + 
							entityCls.getName() + ".class" + ", " + 
							jdbcTemplateCode + 
						")";
		
		if (StringUtils.isEmpty(sqlCode) == false) {
			Matcher m = PROPERTY_OR_RESULT_SET_PATTERN.matcher(sqlCode);
			StringBuilder parametersCode = new StringBuilder();
			parametersCode.append("new Object[] {");
			boolean parameterAdded = false;
			while (m.find()) {
				String param = m.group(0);
				if (param.startsWith(PROPERTY_SIGN)) {
					String paramName = m.group(1);
					String formattedParam = ":" + paramName;
					String getterMethod = "get" + 
												Character.toUpperCase(paramName.charAt(0)) + paramName.substring(1) + 
													"()";
					sqlCode = sqlCode.replace(param, formattedParam);
					if (parameterAdded) {
						parametersCode.append(", ");
					}
					parametersCode.append(GENERATED_OBJECT_NAME + "." + getterMethod);
				}
				else if (param.startsWith(RESULT_SET_SIGN)) {
					String paramType = m.group(2);
					String paramName = m.group(3);
					String formattedParam = ":" + paramName;
					String getterMethod = "get" + 
												Character.toUpperCase(paramType.charAt(0)) + paramType.substring(1) + 
													"(" + "\"" + paramName + "\"" + ")"; 
					sqlCode = sqlCode.replace(param, formattedParam);
					if (parameterAdded) {
						parametersCode.append(", ");
					}
					parametersCode.append(
							wrapWithNonPrimitiveTypeIfNeeded(paramType, RESULT_SET_ARGUMENT + "." + getterMethod));
				}
				else {
					logger.debug("Unknown expression for SQL provider: " + param);
				}
			}
			parametersCode.append("}");
	
			return 
					wrapWithLazyLoadingIfNeeded(f, setterMethodName, rmofc.isLazy(), rmofc, 
						GENERATED_OBJECT_NAME + "." + 
						setterMethodName + 
						"(" + 
							"(" + f.getType().getSimpleName() + ")" +
							"JdbcUtil.runSql" + 
							"(" +
								fieldCls.getName() + ".class" + ", " +
								jdbcTemplateCode + ", " +
								"\"" + sqlCode + "\"" + ", " +
								parametersCode.toString() + ", " + 
								rowMapperCreationCode +
							")" +
						");", null, null);
		}	
		else if (sqlQueryInfoProviderClass != null && 
					sqlQueryInfoProviderClass.equals(RowMapperSqlQueryInfoProvider.class) == false) {
			String sqlQueryInfoProviderCreationCode = 
				"(" +
					"(" + sqlQueryInfoProviderClass.getName() + ")" +
					"(" +
						RowMapperUtil.generateGetSingleInstanceCode(sqlQueryInfoProviderClass) +
					")" + 
				")";
			
			return 
					wrapWithLazyLoadingIfNeeded(f, setterMethodName, rmofc.isLazy(), rmofc, 
						GENERATED_OBJECT_NAME + "." + 
						setterMethodName + 
						"(" + 
							"(" + f.getType().getSimpleName() + ")" +
							"JdbcUtil.runSql" + 
							"(" +
								fieldCls.getName() + ".class" + ", " +
								jdbcTemplateCode + ", " +
								sqlQueryInfoProviderCreationCode + ".provideSqlQueryInfo" + 
								"(" + 
									"(" + rowMapper.getClazz().getName() + ")" + RowMapperFieldGenerator.GENERATED_OBJECT_NAME + "," + 
									"\"" + f.getName() + "\"" +
								")" + "," +
								rowMapperCreationCode +
							")" +
						");", null, null);
		}
		else {
			return "";
		}
	}
	
	protected String wrapWithLazyLoadingIfNeeded(Field f, String setterMethodName, boolean isLazy, 
			RowMapperObjectFieldConfig rmofc, String generatedCode, StringBuilder variables, List<String> variableNames) {
		if (variables == null) {
			variables = new StringBuilder();
		}
		if (variableNames == null) {
			variableNames = new ArrayList<String>();
		}
		if (isLazy) {
			int firstIndex = generatedCode.indexOf("(");
			int lastIndex = generatedCode.lastIndexOf(")");
			String valueProvideCode = generatedCode.substring(firstIndex + 1, lastIndex);
			
			Class<?> fieldCls = f.getType();
			String fieldClsName = fieldCls.getSimpleName();
			
			StringBuffer additionalClasses = new StringBuffer();
			for (Class<?> cls : rowMapper.getAdditionalClasses()) {
				additionalClasses.append(",").append(cls.getName() + ".class");
			}
			
			valueProvideCode = valueProvideCode.replace("\"", "\\\"");
			
			StringBuilder objectParamsBuilder = new StringBuilder();
			objectParamsBuilder.append("new Object[] {");
			objectParamsBuilder.append("\"mappedObject\", mappedObject");
			if (variables != null) {
				for (String variableName : variableNames) {
					objectParamsBuilder.append(", \"" + variableName + "\", " + variableName);
				}
			}	
			objectParamsBuilder.append("}");
			String variableDefinitions = "";
			if (variables != null) {
				variableDefinitions = variables.toString();
			}
			
			RowMapperLazyLoadConditionConfig rmllcc = rmofc.getRowMapperLazyLoadConditionConfig();
			
			valueProvideCode = "return " + valueProvideCode;
			
			if (rmllcc != null) {
				String propertyName = rmllcc.getPropertyName();
				String expression = rmllcc.getExpression();
				@SuppressWarnings("rawtypes")
				Class<? extends RowMapperLazyLoadConditionProvider> lazyLoadConditionProviderClass = 
						rmllcc.getLazyLoadConditionProviderClass();
				if (StringUtils.isEmpty(propertyName) == false) {
					valueProvideCode = 
						"if " + "(" + "BeanUtil.getInstance().getLazyManager()" + 
										".getLazyLoadConditionProperty" + "(" + "\\\"" + propertyName + "\\\"" + ")" + ")" +  
						"{" + 
							valueProvideCode + ";" +
						"}" + 
						"else" + 
						"{" + 
							"return null;" + 
						"}";
				}
				else if (StringUtils.isEmpty(expression) == false) {
					expression = processExpression(expression, variables, variableNames);
					valueProvideCode = 
						"if " + "(" + expression + ")" + 
						"{" + 
							valueProvideCode + ";" +
						"}" + 
						"else" + 
						"{" + 
							"return null;" + 
						"}";
				}
				else if (lazyLoadConditionProviderClass != null && 
							lazyLoadConditionProviderClass.equals(RowMapperLazyLoadConditionProvider.class) == false) {
					valueProvideCode = 	
						"if " + 
							"(" + 
								RowMapperUtil.generateGetSingleInstanceCode(lazyLoadConditionProviderClass) + 
								".evaluateCondition" + 
								"(" + 
									"(" + rowMapper.getClazz().getName() + ")" + 
										RowMapperFieldGenerator.GENERATED_OBJECT_NAME + "," + 
									"\\\"" + f.getName() + "\\\"" + 
								")" +
							")" + 
						"{" + 
							valueProvideCode + ";" +
						"}" + 
						"else" + 
						"{" + 
							"return null;" + 
						"}";
				}
			}

			String code = null;
			
			if (ReflectionUtil.canCreateInstance(fieldCls)) {
				rowMapper.addAdditionalClass(fieldCls);
				code = 
					variableDefinitions + "\n" +
					GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + 
						"(" + f.getType().getName() + ")" + ProxyHelper.class.getName() + ".proxyObject(" + fieldClsName + ".class, " + 
							ProxyObjectLoader.class.getName() + ".createProxyObjectLoader(" +
								"\"" + rowMapper.getClazz().getName() + "$" + f.getName() + "\"" + ", " + 
								"\"" + valueProvideCode + "\"" + ", " + 
								"\"" + additionalClasses.toString() + "\"" + ", " + 
								objectParamsBuilder.toString() +
							")" +
						")" + 
					");";
			}
			else if (List.class.isAssignableFrom(fieldCls)) {
				code = 
					variableDefinitions + "\n" +
					GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + 
						"(" + f.getType().getName() + ")" + ProxyHelper.class.getName() + ".proxyList(" + 
							ProxyListLoader.class.getName() + ".createProxyListLoader(" + 
								"\"" + rowMapper.getClazz().getName()  + "$" + f.getName() + "\"" + ", " + 
								"\"" + valueProvideCode + "\"" + ", " + 
								"\"" + additionalClasses.toString() + "\"" + "," +
								objectParamsBuilder.toString() +
							")" +
						")" + 
					");";
			}
			else if (Set.class.isAssignableFrom(fieldCls)) {
				code = 
					variableDefinitions + "\n" +
					GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + 
						"(" + f.getType().getName() + ")" + ProxyHelper.class.getName() + ".proxySet(" + 
							ProxySetLoader.class.getName() + ".createProxySetLoader(" + 
								"\"" + rowMapper.getClazz().getName()  + "$" + f.getName() + "\"" + ", " + 
								"\"" + valueProvideCode + "\"" + ", " + 
								"\"" + additionalClasses.toString() + "\"" + "," +
								objectParamsBuilder.toString() +
							")" +
						")" + 
					");";
			}
			else if (Map.class.isAssignableFrom(fieldCls)) {
				code = 
					variableDefinitions + "\n" +
					GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + 
						"(" + f.getType().getName() + ")" + ProxyHelper.class.getName() + ".proxyMap(" + 
							ProxyMapLoader.class.getName() + ".createProxyMapLoader(" + 
								"\"" + rowMapper.getClazz().getName()  + "$" + f.getName() + "\"" + ", " + 
								"\"" + valueProvideCode + "\"" + ", " + 
								"\"" + additionalClasses.toString() + "\"" + "," +
								objectParamsBuilder.toString() +
							")" +
						")" + 
					");";
			}
			else {
				rowMapper.addAdditionalClass(fieldCls);
				code = 
					variableDefinitions + "\n" +
					GENERATED_OBJECT_NAME + "." + setterMethodName + "(" + 
						"(" + f.getType().getName() + ")" + ProxyHelper.class.getName() + ".proxyObject(" + 
							fieldClsName + ".class, " + 
							ProxyObjectLoader.class.getName() + ".createProxyObjectLoader(" +
								"\"" + rowMapper.getClazz().getName() + "$" + f.getName() + "\"" + ", " + 
								"\"" + valueProvideCode + "\"" + ", " + 
								"\"" + additionalClasses.toString() + "\"" + ", " + 
								objectParamsBuilder.toString() +
							")" +
						")" + 
					");";
			}
			
			RowMapperLazyConditionConfig rmlcc = rmofc.getRowMapperLazyConditionConfig();
			if (rmlcc != null) {
				String propertyName = rmlcc.getPropertyName();
				String expression = rmlcc.getExpression();
				@SuppressWarnings("rawtypes")
				Class<? extends RowMapperLazyConditionProvider> lazyConditionProviderClass = 
						rmlcc.getLazyConditionProviderClass();
				if (StringUtils.isEmpty(propertyName) == false) {
					code = 
						variables.toString() + "\n" +	
						"if " + "(" + "BeanUtil.getInstance().getLazyManager()" + ".getLazyConditionProperty" + "(" + "\"" + propertyName + "\"" + ")" + ")" + "\n" + 
						"{" + "\n" +
							RowMapperUtil.indent(code) + "\n" +
						"}" + "\n" + 
						"else" + "\n" + 
						"{" + "\n" +
							RowMapperUtil.indent(generatedCode) + "\n" +
						"}";
				}
				else if (StringUtils.isEmpty(expression) == false) {
					expression = processExpression(expression, variables, variableNames);
					code = 
						variables.toString() + "\n" +
						"if " + "(" + expression + ")" + "\n" + 
						"{" + "\n" +
							RowMapperUtil.indent(code) + "\n" +
						"}" + "\n" + 
						"else" + "\n" + 
						"{" + "\n" +
							RowMapperUtil.indent(generatedCode) + "\n" +
						"}";
				}
				else if (lazyConditionProviderClass != null && 
							lazyConditionProviderClass.equals(RowMapperLazyConditionProvider.class) == false) {
					code = 
						variables.toString() + "\n" +		
						"if " + 
							"(" + 
								RowMapperUtil.generateGetSingleInstanceCode(lazyConditionProviderClass) + 
								".evaluateCondition" + 
								"(" + 
									"(" + rowMapper.getClazz().getName() + ")" + 
										RowMapperFieldGenerator.GENERATED_OBJECT_NAME + "," + 
									"\"" + f.getName() + "\"" + "," + 
									RowMapperFieldGenerator.RESULT_SET_ARGUMENT + ", " + 	
									RowMapperFieldGenerator.ROW_NUM_ARGUMENT +	
								")" +
							")" + "\n" + 
						"{" + "\n" +
							RowMapperUtil.indent(code) + "\n" +
						"}" + "\n" + 
						"else" + "\n" + 
						"{" + "\n" +
							RowMapperUtil.indent(generatedCode) + "\n" +
						"}";
				}
			}
			
			return code;
		}
		else {
			return (variables != null ? (variables.toString() + "\n") : "") + generatedCode;
		}
	}
	
	protected String wrapWithIgnoreConditionIfNeeded(Field f, RowMapperObjectFieldConfig rmofc, String generatedCode) {
		RowMapperIgnoreConditionConfig rmicc = rmofc.getRowMapperIgnoreConditionConfig();
		if (rmicc != null) {
			StringBuilder variables = new StringBuilder();
			List<String> variableNames = new ArrayList<String>();
			String propertyName = rmicc.getPropertyName();
			String expression = rmicc.getExpression();
			@SuppressWarnings("rawtypes")
			Class<? extends RowMapperIgnoreConditionProvider> ignoreConditionProviderClass = 
					rmicc.getIgnoreConditionProviderClass();
			if (StringUtils.isEmpty(propertyName) == false) {
				generatedCode = 
					variables.toString() + "\n" +	
					"if " + "(" + "BeanUtil.getInstance().getIgnoreManager()" + 
									".getIgnoreConditionProperty" + "(" + "\"" + propertyName + "\"" + ")" + " == false" + ")" + "\n" + 
					"{" + "\n" +
						RowMapperUtil.indent(generatedCode) + "\n" +
					"}";
			}
			else if (StringUtils.isEmpty(expression) == false) {
				expression = processExpression(expression, variables, variableNames);
				generatedCode = 
					variables.toString() + "\n" +
					"if " + "(" + expression + " == false" + ")" + "\n" + 
					"{" + "\n" +
						RowMapperUtil.indent(generatedCode) + "\n" +
					"}";
			}
			else if (ignoreConditionProviderClass != null && 
						ignoreConditionProviderClass.equals(RowMapperIgnoreConditionProvider.class) == false) {
				generatedCode = 
					variables.toString() + "\n" +		
					"if " + 
						"(" + 
							RowMapperUtil.generateGetSingleInstanceCode(ignoreConditionProviderClass) + 
							".evaluateCondition" + 
							"(" + 
								"(" + rowMapper.getClazz().getName() + ")" + 
									RowMapperFieldGenerator.GENERATED_OBJECT_NAME + "," + 
								"\"" + f.getName() + "\"" + "," + 
								RowMapperFieldGenerator.RESULT_SET_ARGUMENT + ", " + 	
								RowMapperFieldGenerator.ROW_NUM_ARGUMENT +	
							")" + " == false" +
						")" + "\n" + 
					"{" + "\n" +
						RowMapperUtil.indent(generatedCode) + "\n" +
					"}";
			}
		}	
		return generatedCode;
	}
	protected String processBeanNames(String code, StringBuilder variables, List<String> variableNames) {
		Matcher m = BEAN_PATTERN.matcher(code);
		while (m.find()) {
			String group = m.group(0);
			String springBeanName = m.group(1);
			Class<?> beanType= SpringUtil.getType(springBeanName);
			if (beanType == null) {
				continue;
			}
			rowMapper.addAdditionalClass(beanType);
			code = 
				code.replace(group, 
						"(" +
							"(" + beanType.getName() + ")" + 
							SpringUtil.class.getName() + ".getBean(\"" + springBeanName + "\")" + 
						")");
		}
		return code;
	}
	
	protected String processProperties(String code, StringBuilder variables, List<String> variableNames) {
		Matcher m = PROPERTY_PATTERN.matcher(code);
		while (m.find()) {
			String param = m.group(0);
			String paramName = m.group(1);
			String randomId = UUID.randomUUID().toString().replace("-", "_");
			String variableName = paramName + "$" + randomId;
			
			Field paramField = ReflectionUtil.getField(rowMapper.getClazz(), paramName);
			if (paramField == null) {
				logger.error("Unable to find field " + paramName + " in class " + rowMapper.getClazz().getName());
				return "";
			}
			
			Class<?> actualParamType = paramField.getType();
			Class<?> paramType = ReflectionUtil.getNonPrimitiveType(actualParamType);
			String getterMethod = "get" + 
					Character.toUpperCase(paramName.charAt(0)) + paramName.substring(1) + "()";
			String providerForParamValue = GENERATED_OBJECT_NAME + "." + getterMethod;
			if (ReflectionUtil.isPrimitiveType(actualParamType)) {
				providerForParamValue = wrapWithNonPrimitiveTypeIfNeeded(actualParamType.getName(), providerForParamValue);
			}
			if (paramType != null) {
				variables.
					append(paramType.getName()).append(" ").append(variableName).
					append(" = ").
					append(providerForParamValue).
					append(";");
				variableNames.add(variableName);
				code = code.replace(param, variableName);
			}
			else {
				code = code.replace(param, providerForParamValue);
			}
		}
		return code;
	}
	
	protected String processResultSetColumnNames(String code, StringBuilder variables, List<String> variableNames) {
		Matcher m = RESULT_SET_PATTERN.matcher(code);
		while (m.find()) {
			String param = m.group(0);
			String paramType = m.group(1);
			String paramName = m.group(2);
			String randomId = UUID.randomUUID().toString().replace("-", "_");
			String variableName = paramName + "$" + randomId;
			String getterMethod = "get" + Character.toUpperCase(paramType.charAt(0)) + paramType.substring(1) + 
								  "(" + "\"" + paramName + "\"" + ")"; 
			String providerForParamValue = wrapWithNonPrimitiveTypeIfNeeded(paramType, RESULT_SET_ARGUMENT + "." + getterMethod);

			Class<?> paramTypeClass = ReflectionUtil.getNonPrimitiveType(paramType);

			if (paramTypeClass != null) {
				paramType = paramTypeClass.getName();
			}
			variables.
				append(paramType).append(" ").append(variableName).
				append(" = ").
				append(providerForParamValue).
				append(";");
			variableNames.add(variableName);
			code = code.replace(param, variableName);
		}
		return code;
	}
	
	protected String processAttributes(String code, StringBuilder variables) {
		Matcher m = ATTRIBUTE_PATTERN.matcher(code);
		while (m.find()) {
			String param = m.group(0);
			String paramName = m.group(1);
			String getterMethod = "get" + 
					Character.toUpperCase(paramName.charAt(0)) + paramName.substring(1) + "()";
			String formattedParam = getterMethod;
			code = code.replace(param, formattedParam);
		}
		return code;
	}
	
	protected String processExpression(String expression, StringBuilder variables, List<String> variableNames) {
		expression = processBeanNames(expression, variables, variableNames);
		expression = processProperties(expression, variables, variableNames);
		expression = processResultSetColumnNames(expression, variables, variableNames);
		expression = processAttributes(expression, variables);
		return expression;
	}
	
	protected String wrapWithNonPrimitiveTypeIfNeeded(String paramType, String expression) {
		if (ReflectionUtil.isNonPrimitiveType(paramType)) {
			return expression;
		}
		else {
			Class<?> nonPrimitiveType = ReflectionUtil.getNonPrimitiveType(paramType);
			return nonPrimitiveType.getName() + "." + "valueOf" + "(" + expression + ")";
		}
	}

}
