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

package org.springframework.jdbc.roma.impl.integration.dao;

import java.util.List;

import org.springframework.jdbc.roma.impl.integration.model.Permission;

/**
 * @author Serkan ÖZAL
 */
public interface PermissionDAO {
	
	public Permission get(Long id);
	public void add(Permission perm) throws Exception;
	public List<Permission> list();
	public List<Permission> getRolePermissionList(Long roleId);
	public void addRolePermission(Long roleId, Permission perm) throws Exception;
	public List<Permission> getUserPermissionList(Long userId);

}
