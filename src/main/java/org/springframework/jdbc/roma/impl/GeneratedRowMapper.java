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

package org.springframework.jdbc.roma.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperObjectCreater;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperObjectProcessor;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClassConfig;
import org.springframework.jdbc.roma.api.factory.RowMapperFieldGeneratorFactory;
import org.springframework.jdbc.roma.api.generator.RowMapperFieldGenerator;
import org.springframework.jdbc.roma.impl.factory.DefaultRowMapperGeneratorFactory;
import org.springframework.jdbc.roma.impl.generator.RowMapperAwareFieldGenerator;
import org.springframework.jdbc.roma.impl.proxy.ProxyHelper;
import org.springframework.jdbc.roma.impl.proxy.ProxyListLoader;
import org.springframework.jdbc.roma.impl.proxy.ProxyObjectLoader;
import org.springframework.jdbc.roma.impl.util.BeanUtil;
import org.springframework.jdbc.roma.impl.util.InstanceUtil;
import org.springframework.jdbc.roma.impl.util.JdbcUtil;
import org.springframework.jdbc.roma.impl.util.ReflectionUtil;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;
import org.springframework.jdbc.roma.impl.util.SpringUtil;

/**
 * @author Serkan ÖZAL
 */
public class GeneratedRowMapper<T> extends AbstractRowMapper<T> {
	
	protected static final Class<?>[] DEFAULT_CLASSES_TO_BE_ADDED = {
		RowMapper.class,
		ResultSet.class,
		Blob.class,
		Clob.class,
		GeneratedRowMapper.class,
		ProxyHelper.class,
		ProxyObjectLoader.class,
		ProxyListLoader.class,
		BeanUtil.class,
		InstanceUtil.class,
		ReflectionUtil.class,
		RowMapperUtil.class,
		JdbcUtil.class,
		SpringUtil.class,
	};
	
	protected static final Logger logger = Logger.getLogger(GeneratedRowMapper.class);
	
	protected static final Map<String, Class<? extends RowMapper<?>>> createdRowMappers = 
							new HashMap<String,  Class<? extends RowMapper<?>>>();
	
	protected List<RowMapperFieldGenerator<T>> rowMappers = new ArrayList<RowMapperFieldGenerator<T>>();
	protected RowMapperFieldGeneratorFactory<T> rowMapperFactory = new DefaultRowMapperGeneratorFactory<T>(configManager);
	protected Map<RowMapperFieldGenerator<T>, Field> rowMapperFieldMap = new HashMap<RowMapperFieldGenerator<T>, Field>();
	protected ClassPool cp = ClassPool.getDefault();
	protected RowMapper<T> generatedRowMapper;
	protected List<Class<?>> additionalClasses = new ArrayList<Class<?>>();
	protected RowMapperClassConfig classConfig;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GeneratedRowMapper(Class<T> cls) {
		super(cls);
		classConfig = configManager.getRowMapperClassConfig(cls);
		if (classConfig != null) {
			Class<? extends RowMapperFieldGeneratorFactory> fieldGeneratorFactoryClass = classConfig.getFieldGeneratorFactoryClass();
			if (fieldGeneratorFactoryClass != null && fieldGeneratorFactoryClass.equals(RowMapperFieldGeneratorFactory.class) == false) {
				try {
					rowMapperFactory = InstanceUtil.getSingleInstance(fieldGeneratorFactoryClass);
				} 
				catch (Exception e) {
					logger.error("Unable to create instance of " + fieldGeneratorFactoryClass.getName(), e);
				} 
			}
		}
		init();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GeneratedRowMapper(Class<T> cls, JdbcTemplate jdbcTemplate) {
		super(cls, jdbcTemplate);
		classConfig = configManager.getRowMapperClassConfig(cls);
		if (classConfig != null) {
			Class<? extends RowMapperFieldGeneratorFactory> fieldGeneratorFactoryClass = classConfig.getFieldGeneratorFactoryClass();
			if (fieldGeneratorFactoryClass != null && fieldGeneratorFactoryClass.equals(RowMapperFieldGeneratorFactory.class) == false) {
				try {
					rowMapperFactory = InstanceUtil.getSingleInstance(fieldGeneratorFactoryClass);
				} 
				catch (Exception e) {
					logger.error("Unable to create instance of " + fieldGeneratorFactoryClass.getName(), e);
				} 
			}
		}
		init();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GeneratedRowMapper(Class<T> cls, ConfigManager configManager) {
		super(cls, configManager);
		classConfig = configManager.getRowMapperClassConfig(cls);
		if (classConfig != null) {
			Class<? extends RowMapperFieldGeneratorFactory> fieldGeneratorFactoryClass = classConfig.getFieldGeneratorFactoryClass();
			if (fieldGeneratorFactoryClass != null && fieldGeneratorFactoryClass.equals(RowMapperFieldGeneratorFactory.class) == false) {
				try {
					rowMapperFactory = InstanceUtil.getSingleInstance(fieldGeneratorFactoryClass);
				} 
				catch (Exception e) {
					logger.error("Unable to create instance of " + fieldGeneratorFactoryClass.getName(), e);
				} 
			}
		}
		init();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public GeneratedRowMapper(Class<T> cls, JdbcTemplate jdbcTemplate, ConfigManager configManager) {
		super(cls, jdbcTemplate, configManager);
		classConfig = configManager.getRowMapperClassConfig(cls);
		if (classConfig != null) {
			Class<? extends RowMapperFieldGeneratorFactory> fieldGeneratorFactoryClass = classConfig.getFieldGeneratorFactoryClass();
			if (fieldGeneratorFactoryClass != null && fieldGeneratorFactoryClass.equals(RowMapperFieldGeneratorFactory.class) == false) {
				try {
					rowMapperFactory = InstanceUtil.getSingleInstance(fieldGeneratorFactoryClass);
				} 
				catch (Exception e) {
					logger.error("Unable to create instance of " + fieldGeneratorFactoryClass.getName(), e);
				} 
			}
		}
		init();
	}
	
	public GeneratedRowMapper(Class<T> cls, Class<? extends RowMapperFieldGeneratorFactory<T>> rowMapperFactoryCls) {
		super(cls);
		try {
			this.rowMapperFactory = InstanceUtil.getSingleInstance(rowMapperFactoryCls);
		} 
		catch (Exception e) {
			logger.error("Unable to create instance of " + rowMapperFactoryCls.getName(), e);
		} 
		init();
	}

	public GeneratedRowMapper(Class<T> cls, Class<? extends RowMapperFieldGeneratorFactory<T>> rowMapperFactoryCls,
			ConfigManager configManager) {
		super(cls, configManager);
		try {
			this.rowMapperFactory = InstanceUtil.getSingleInstance(rowMapperFactoryCls);
		} 
		catch (Exception e) {
			logger.error("Unable to create instance of " + rowMapperFactoryCls.getName(), e);
		} 
		init();
	}
	
	public GeneratedRowMapper(Class<T> cls, RowMapperFieldGeneratorFactory<T> rowMapperFactory) {
		super(cls);
		if (rowMapperFactory != null) {
			this.rowMapperFactory = rowMapperFactory;
		}	
		init();
	}
	
	public GeneratedRowMapper(Class<T> cls, RowMapperFieldGeneratorFactory<T> rowMapperFactory, 
			ConfigManager configManager) {
		super(cls, configManager);
		if (rowMapperFactory != null) {
			this.rowMapperFactory = rowMapperFactory;
		}	
		init();
	}
	
	protected void init() {
		for (Class<?> cls : DEFAULT_CLASSES_TO_BE_ADDED) {
			addAdditionalClass(cls);
		}
		reset();
		createRowMapperGenerators();
		generateRowMapper();
	}
	
	protected void createRowMapperGenerators() {
		List<Field> fields = RowMapperUtil.getAllRowMapperFields(cls);
		if (fields != null) {
			for (Field f : fields) {
				RowMapperFieldGenerator<T> rmfg = rowMapperFactory.createRowMapperFieldGenerator(f);
				if (rmfg != null) {
					rowMappers.add(rmfg);
					if (rmfg instanceof RowMapperAwareFieldGenerator) {
						((RowMapperAwareFieldGenerator<T>)rmfg).assignedToRowMapper(this);
					}
					rowMapperFieldMap.put(rmfg, f);
				}	
			}
		}
	}
	
	public void addAdditionalClass(Class<?> cls) {
		additionalClasses.add(cls);
		Package pck = cls.getPackage();
		if (pck != null) {
			cp.importPackage(pck.getName());
		}	
		cp.appendClassPath(new ClassClassPath(cls));
	}
	
	public List<Class<?>> getAdditionalClasses() {
		return additionalClasses;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void generateRowMapper() {
		try {
			final String generatedClassName = cls.getName() + "GeneratedRowMapper";
			
			Class<? extends RowMapper<?>> generatedClass = createdRowMappers.get(generatedClassName);
			
			if (generatedClass == null) {
				CtClass generatedRowMapperCls = null;
				try {
					generatedRowMapperCls = cp.get(cls.getName() + "GeneratedRowMapper");
				}
				catch (NotFoundException e) {
					
				}
				if (generatedRowMapperCls == null) {
					generatedRowMapperCls = cp.makeClass(generatedClassName);
					generatedRowMapperCls.defrost();
					generatedRowMapperCls.addInterface(cp.get(RowMapper.class.getName()));
					
					String fieldName = "jdbcTemplate";
					CtField ctf = 
								new CtField(cp.get(JdbcTemplate.class.getName()), fieldName, generatedRowMapperCls);
					generatedRowMapperCls.addField(ctf);
					String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
					CtMethod ctfSetter = 
								new CtMethod(
										CtClass.voidType, 
										setterName,
										new CtClass[] {cp.get(JdbcTemplate.class.getName())},
										generatedRowMapperCls);
					ctfSetter.setModifiers(Modifier.PUBLIC);
					String setterBody = 
						"{" + "\n" +
							"\t" + "this." + fieldName + " = " + "$1;" + "\n" +		
						"}";
					ctfSetter.setBody(setterBody);
					generatedRowMapperCls.addMethod(ctfSetter);
					
					CtMethod mapRowMethod = 
						new CtMethod(
								cp.get(Object.class.getName()), 
								"mapRow",
								new CtClass[] {cp.get(ResultSet.class.getName()), cp.get(int.class.getName())},
								generatedRowMapperCls);
					mapRowMethod.setModifiers(Modifier.PUBLIC);
					
					StringBuffer methodBody = new StringBuffer();
					String typeName = cls.getName();
					
					String objectCreationCode = null;
					if (classConfig != null) {
						Class<? extends RowMapperObjectCreater> objectCreaterClass = 
								classConfig.getObjectCreaterClass();
						if (objectCreaterClass != null && 
								objectCreaterClass.equals(RowMapperObjectCreater.class) == false) {
							objectCreationCode = 
								"(" + typeName + ")" +
								"(" +
									RowMapperUtil.generateGetSingleInstanceCode(objectCreaterClass) +
										".createObject" + "(" + typeName + ".class" + ")" + 
								");";
						}
					}

					if (objectCreationCode == null) {
						objectCreationCode = "new " + typeName + "();";
					}
					
					methodBody.
						append("\t").
							append(typeName).
							append(" ").
							append(RowMapperFieldGenerator.GENERATED_OBJECT_NAME).
							append(" = ").
							append(objectCreationCode).
							append("\n");
					
					for (RowMapperFieldGenerator<T> rmfg : rowMappers) {
						Field f = rowMapperFieldMap.get(rmfg);
						methodBody.append(RowMapperUtil.indent(rmfg.generateFieldMapping(f))).append("\n");	
					}
					
					if (classConfig != null) {
						Class<? extends RowMapperObjectProcessor> objectProcessorClass = 
								classConfig.getObjectProcessorClass();
						if (objectProcessorClass != null && 
								objectProcessorClass.equals(RowMapperObjectProcessor.class) == false) {
							methodBody.
								append("\t").
								append(
									RowMapperUtil.generateGetSingleInstanceCode(objectProcessorClass) +
										".processObject" + 
										"(" + 
											"(" + typeName + ")" + RowMapperFieldGenerator.GENERATED_OBJECT_NAME + "," + 
											RowMapperFieldGenerator.RESULT_SET_ARGUMENT + ", " + 	
											RowMapperFieldGenerator.ROW_NUM_ARGUMENT +	
										");");
						}
					}
					
					methodBody.
						append("\t").
						append("return" + " " + RowMapperFieldGenerator.GENERATED_OBJECT_NAME + ";").
						append("\n");
					
					String methodCode = 
						"try" + "\n" +
						"{" + "\n" +
							methodBody.toString() + 
						"}" + "\n" +
						"catch (Throwable t)" + "\n" +
						"{" + "\n" +
						"\t" + "t.printStackTrace();" + "\n" +
						"\t" + "return null;" + "\n" +
						"}";
					
					if (logger.isDebugEnabled()) {
						logger.debug("GeneratedRowMapper: " + generatedClassName + "\n" + methodCode);
					}
					
					mapRowMethod.setBody(methodCode);
					generatedRowMapperCls.addMethod(mapRowMethod);
				}	
				
				generatedClass = generatedRowMapperCls.toClass();
				createdRowMappers.put(generatedClassName, generatedClass);
				generatedRowMapperCls.detach();
			}	

			generatedRowMapper = (RowMapper<T>)generatedClass.newInstance();
			
			String fieldName = "jdbcTemplate";
			String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			Method setterMethod = 
					generatedRowMapper.getClass().getDeclaredMethod(setterName, JdbcTemplate.class);
			setterMethod.invoke(generatedRowMapper, jdbcTemplate);
		} 
		catch (Throwable e) {
			logger.error("Error occured while generating rowmapper", e);
		} 
	}
	
	protected void reset() {
		try {
			this.obj = cls.newInstance();
		} 
		catch (Exception e) {
			logger.error("Unable to create instance of " + cls.getName(), e);
		} 
	}
	
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		try {
			return generatedRowMapper.mapRow(rs, rowNum);
		}
		catch (Throwable e) {
			logger.error("Error occured while mapping row", e);
			return null;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static RowMapper provideRowMapper(String clsName) {
		try {
			return new GeneratedRowMapper(Class.forName(clsName));
		} 
		catch (ClassNotFoundException e) {
			logger.error("Unable to find class: " + clsName, e);
			return null;
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static RowMapper provideRowMapper(String clsName, JdbcTemplate jdbcTemplate) {
		try {
			return provideRowMapper(Class.forName(clsName), jdbcTemplate);
		} 
		catch (ClassNotFoundException e) {
			logger.error("Unable to find class: " + clsName, e);
			return null;
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static RowMapper provideRowMapper(Class<?> cls, JdbcTemplate jdbcTemplate) {
		return BeanUtil.getInstance().getRowMapperService().getRowMapper(cls, jdbcTemplate);
	}
	
}
