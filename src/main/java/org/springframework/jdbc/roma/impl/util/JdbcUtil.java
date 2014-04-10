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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider.SqlQueryInfo;
import org.springframework.util.StringUtils;

/**
 * @author Serkan ÖZAL
 */
public class JdbcUtil {

	private static final Logger logger = Logger.getLogger(JdbcUtil.class);
	
	private static final Map<String, JdbcTemplate> jdbcTemplates = new ConcurrentHashMap<String, JdbcTemplate>();
	
	private JdbcUtil() {
		
	}
	
	public static JdbcTemplate getJdbcTemplate(String dataSourceName) {
		JdbcTemplate jdbcTemplate = jdbcTemplates.get(dataSourceName);
		if (jdbcTemplate == null) {
			if (StringUtils.isEmpty(dataSourceName) || dataSourceName.equals("null")) {
				dataSourceName = 
						BeanUtil.getInstance().getConfigManager().
							getDefaultConfigs().getDefaultDataSourceName();
			}
			if (StringUtils.isEmpty(dataSourceName)) {
				logger.debug("Datasource name is empty");
				return null;
			}
			
			DataSource ds = SpringUtil.getBean(dataSourceName);
			if (ds == null) {
				logger.error("Unable to find datasource " + dataSourceName);
				return null;
			}
			
			jdbcTemplate = new JdbcTemplate(ds);
			jdbcTemplates.put(dataSourceName, jdbcTemplate);
		}
		return jdbcTemplate;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T> T runSql(Class<?> fieldType, JdbcTemplate jdbcTemplate, String sql, Object[] args, RowMapper<?> rowMapper) {
		if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
			return (T)(Integer)jdbcTemplate.queryForInt(sql, args);
		}
		else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
			return (T)(Long)jdbcTemplate.queryForLong(sql, args);
		}
		else if (List.class.isAssignableFrom(fieldType)) {
			return (T)jdbcTemplate.query(sql, args, rowMapper);
		}
		else {
			return (T)jdbcTemplate.queryForObject(sql, args, rowMapper);
		}
	}
	
	public static <T> T runSql(Class<?> fieldType, JdbcTemplate jdbcTemplate, SqlQueryInfo sqlQueryInfo, RowMapper<?> rowMapper) {
		return runSql(fieldType, jdbcTemplate, sqlQueryInfo.sqlQuery, sqlQueryInfo.args, rowMapper);
	}
	
}
