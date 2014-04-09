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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.roma.api.config.manager.ConfigManager;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass.RowMapperTableNameResolver;
import org.springframework.jdbc.roma.api.domain.model.config.RowMapperClassConfig;
import org.springframework.jdbc.roma.impl.util.BeanUtil;
import org.springframework.jdbc.roma.impl.util.RowMapperUtil;
import org.springframework.jdbc.roma.impl.util.SpringUtil;
import org.springframework.util.StringUtils;

/**
 * @author Serkan ÖZAL
 */
public class DefaultTableNameResolver implements RowMapperTableNameResolver {

	protected static final Logger logger = Logger.getLogger(DefaultTableNameResolver.class);
	
	protected ConfigManager configManager = BeanUtil.getInstance().getConfigManager();
	
	@Override
	public String resolveTableName(Class<?> clazz) {
		Connection connection = null;
		try {
			RowMapperClassConfig rmcc = configManager.getRowMapperClassConfig(clazz);
			if (rmcc == null) {
				logger.debug("Unable to find class config for class " + clazz.getName());
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
				
			String tableName = rmcc != null ? rmcc.getTableName() : null;
			if (StringUtils.isEmpty(tableName) == false) {
				return tableName;
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
					resultSet = dsMetadata.getTables(null, null, "%", new String[] {"TABLE"});
				}
				else {
					resultSet = dsMetadata.getTables(null, schemaName, "%", new String[] {"TABLE"});
				}
			}
			catch (Throwable t) {
				logger.error("Unable to get tables of datasource" + dataSourceName, t);
				return null;
			}
						
			List<String> tableNames = new ArrayList<String>();
			try {
				while (resultSet.next()) {
					String tblName = resultSet.getString("TABLE_NAME");
					tableNames.add(tblName);
				}
			}
			catch (Throwable t) {
				logger.error("Unable to get tables of datasource" + dataSourceName, t);
				return null;
			}
						
			List<String> possibleTableNames = RowMapperUtil.generatePossibleTableNames(clazz);
			if (possibleTableNames != null) {
				for (String possibleTableName : possibleTableNames) {
					if (tableNames.contains(possibleTableName)) {
						return possibleTableName;
					}
				}
			}
						
			return null;
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
