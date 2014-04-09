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

package org.springframework.jdbc.roma.impl.integration.dao.jdbc;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.roma.impl.integration.dao.RoleDAO;
import org.springframework.jdbc.roma.impl.integration.model.Role;
import org.springframework.stereotype.Repository;

/**
 * @author Serkan ÖZAL
 */
@Repository(value="roleDAO")
public class RoleJdbcDAO extends BaseJdbcDAO implements RoleDAO {

	private RowMapper<Role> roleRowMapper;
	
	@PostConstruct
	protected void init() {
		roleRowMapper = rowMapperService.getRowMapper(Role.class); 
	}
	
	@Override
	public Role get(Long id) {
		try {
			return jdbcTemplate.queryForObject("SELECT r.* FROM ROLE r WHERE r.id = ?", roleRowMapper, id);
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}	
	}

	@Override
	public void add(Role role) throws Exception {
		jdbcTemplate.update("INSERT INTO ROLE (name) VALUES (?)", role.getName());
	}

	@Override
	public List<Role> list() {
		return jdbcTemplate.query("SELECT r.* FROM ROLE r ORDER BY r.name", roleRowMapper);
	}

	@Override
	public List<Role> getUserRoleList(Long userId) {
		return 
			jdbcTemplate.query(
				"SELECT r.* FROM ROLE r WHERE r.id IN " +
				"(" +
					"SELECT ur.role_id FROM USER_ROLE ur WHERE ur.user_id = " + userId +
				") ORDER BY r.name", 
				roleRowMapper);
	}

	@Override
	public void addUserRole(Long userId, Role role) throws Exception {
		jdbcTemplate.update(
			"INSERT INTO USER_ROLE (user_id, role_id) VALUES (?, ?) ", 
			userId, role.getId());
	}

}
