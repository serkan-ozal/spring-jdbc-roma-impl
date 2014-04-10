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

package org.springframework.jdbc.roma.impl.integration.custom;

import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider.RowMapperSqlQueryInfoProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider.SqlQueryInfo;
import org.springframework.jdbc.roma.impl.integration.model.User;

/**
 * @author Serkan ÖZAL
 */
public class UserAccountInfoSqlQueryInfoProvider implements RowMapperSqlQueryInfoProvider<User> {

	@Override
	public SqlQueryInfo provideSqlQueryInfo(User user, String fieldName) {
		return 
			new SqlQueryInfo(
				"SELECT a.* FROM ACCOUNT_INFO a WHERE a.ID IN " +
	            "(" +
	                "SELECT ua.account_info_id FROM USER_ACCOUNT_INFO ua WHERE ua.user_id = ?" +
	            ")",
	            new Object[] { user.getId() });
	}

}
