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
import org.springframework.jdbc.roma.impl.integration.dao.PermissionDAO;
import org.springframework.jdbc.roma.impl.integration.model.Permission;
import org.springframework.stereotype.Repository;

/**
 * @author Serkan ÖZAL
 */
@Repository(value="permissionDAO")
public class PermissionJdbcDAO extends BaseJdbcDAO implements PermissionDAO {

	private RowMapper<Permission> permRowMapper;

	@PostConstruct
	protected void init() {
		permRowMapper = rowMapperService.getRowMapper(Permission.class);
	}
	
	@Override
	public Permission get(Long id) {
		try {
			return 
				jdbcTemplate.queryForObject(
					"SELECT p.* FROM PERMISSION p WHERE p.id = ?", permRowMapper, id);
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}	
	}

	@Override
	public void add(Permission perm) throws Exception {
		jdbcTemplate.update(
			"INSERT INTO PERMISSION (name) " + 
			"VALUES (?) ", 
			perm.getId(), perm.getName());
	}

	@Override
	public List<Permission> list() {
		return jdbcTemplate.query("SELECT p.* FROM PERMISSION p ORDER BY p.name", permRowMapper);
	}

	@Override
	public List<Permission> getRolePermissionList(Long roleId) {
		return 
			jdbcTemplate.query(
				"SELECT p.* FROM PERMISSION p WHERE p.id IN " +
				"(" +
					"SELECT rp.permission_id FROM ROLE_PERMISSION rp WHERE rp.role_id = " + roleId +
				") ORDER BY p.name", 
				permRowMapper);
	}

	@Override
	public void addRolePermission(Long roleId, Permission perm) throws Exception {
		jdbcTemplate.update(
			"INSERT INTO ROLE_PERMISSION (role_id, permission_id) " + 
			"VALUES (?, ?) ", roleId, perm.getId());
	}

	@Override
	public List<Permission> getUserPermissionList(Long userId) {
		return 
			jdbcTemplate.query(
					"SELECT p.* FROM PERMISSION p WHERE p.id IN " +
					"(" +
						"SELECT rp.permission_id FROM ROLE_PERMISSION rp WHERE rp.role_id IN " +
						"(" +
							"SELECT ur.ROLE_ID FROM USER_ROLE ur WHERE ur.user_id = " + userId +
						")" +
					") ORDER BY p.name", 
					permRowMapper);
	}

}
