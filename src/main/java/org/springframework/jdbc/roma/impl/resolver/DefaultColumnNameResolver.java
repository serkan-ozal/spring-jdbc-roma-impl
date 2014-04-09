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

package org.springframework.jdbc.roma.impl.resolver;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperColumnNameResolver;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperTableNameResolver;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClassConfig;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperFieldConfig;
import org.springframework.jdbc.roma.impl.util.BeanUtil;
import org.springframework.jdbc.roma.impl.util.InstanceUtil;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;
import org.springframework.jdbc.roma.impl.util.SpringUtil;
import org.springframework.util.StringUtils;

/**
 * @author Serkan ÖZAL
 */
public class DefaultColumnNameResolver implements RowMapperColumnNameResolver {

	protected static final Logger logger = Logger.getLogger(DefaultColumnNameResolver.class);
	
	protected ConfigManager configManager = BeanUtil.getInstance().getConfigManager();
	protected RowMapperTableNameResolver tableNameResolver = new DefaultTableNameResolver();
	
	@Override
	public String resolveColumnName(Field f) {
		Connection connection = null;
		
		try {
			f.setAccessible(true);
			RowMapperFieldConfig rmfc = configManager.getRowMapperFieldConfig(f);
			if (rmfc != null && StringUtils.isEmpty(rmfc.getColumnName()) == false) {
				logger.debug("Column name " + rmfc.getColumnName() + 
							 "found from field config for field "+ f.getName() + 
							 " in class " + f.getDeclaringClass().getName());
				return rmfc.getColumnName();
			}
			else if (Modifier.isTransient(f.getModifiers())) {
				logger.debug("Field " + f.getName() + " in class " + f.getDeclaringClass().getName() + 
							 " is transient and it is being ignored");
				return null;
			}
			else {
				RowMapperClassConfig rmcc = configManager.getRowMapperClassConfig(f.getDeclaringClass());
				if (rmcc == null) {
					logger.debug("Unable to find class config for class " + f.getDeclaringClass().getName());
				}
				
				Class<? extends RowMapperTableNameResolver> tableNameResolverClass = 
						rmcc != null ? rmcc.getTableNameResolverClass() : null;
				if (tableNameResolverClass != null) {
					RowMapperTableNameResolver tnr = InstanceUtil.getSingleInstance(tableNameResolverClass);
					if (tnr != null) {
						tableNameResolver = tnr;
					}
				}
				String dataSourceName = rmcc != null ? rmcc.getDataSourceName() : null;
				if (StringUtils.isEmpty(dataSourceName)) {
					dataSourceName = 
							BeanUtil.getInstance().getConfigManager().
								getDefaultConfigs().getDefaultDataSourceName();
				}
				if (StringUtils.isEmpty(dataSourceName)) {
					logger.debug("Datasource name is empty");
					return null;
				}
					
				String schemaName = rmcc != null ? rmcc.getSchemaName() : null;
				if (StringUtils.isEmpty(schemaName)) {
					schemaName = 
							BeanUtil.getInstance().getConfigManager().
								getDefaultConfigs().getDefaultSchemaName();
				}
					
				String tableName = tableNameResolver.resolveTableName(f.getDeclaringClass());
				if (StringUtils.isEmpty(tableName)) {
					logger.debug("Table name is empty");
					return null;
				}
					
				DataSource ds = SpringUtil.getBean(dataSourceName);
				if (ds == null) {
					logger.error("Unable to find datasource " + dataSourceName);
					return null;
				}
						
				try {
					connection = ds.getConnection();
				}
				catch (Throwable t) {
					logger.error("Unable to get connection from datasource " + dataSourceName, t);
					return null;
				}
						
				DatabaseMetaData dsMetadata = null;
				try {
					dsMetadata = connection.getMetaData();
				}
				catch (Throwable t) {
					logger.error("Unable to get metadata from datasource " + dataSourceName, t);
					return null;
				}
	
				ResultSet resultSet = null;
				try {
					if (StringUtils.isEmpty(schemaName)) {
						resultSet = dsMetadata.getColumns(null, null, tableName.toUpperCase(Locale.ENGLISH), null);
					}
					else {
						resultSet = dsMetadata.getColumns(null, schemaName, tableName.toUpperCase(Locale.ENGLISH), null);
					}
				}
				catch (Throwable t) {
					logger.error("Unable to get columns of table " + tableName, t);
					return null;
				}
						
				List<String> columnNames = new ArrayList<String>();
				try {
					while (resultSet.next()) {
						String columnName = resultSet.getString("COLUMN_NAME");
						columnNames.add(columnName);
					}
				}
				catch (Throwable t) {
					logger.error("Unable to get columns of table " + tableName, t);
					return null;
				}
						
				List<String> possibleColumnNames = RowMapperUtil.generatePossibleColumnNames(f);
				if (possibleColumnNames != null) {
					for (String possibleColumnName : possibleColumnNames) {
						if (columnNames.contains(possibleColumnName)) {
							return possibleColumnName;
						}
					}
				}
						
				return null;
			}
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				}
				catch (Throwable t) {
					logger.error("Error occured while closing connection", t);
				}
			}
		}
	}
	
}
