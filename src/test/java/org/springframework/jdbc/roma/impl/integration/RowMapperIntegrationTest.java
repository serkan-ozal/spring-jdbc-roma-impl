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

package org.springframework.jdbc.roma.impl.integration;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.roma.impl.integration.dao.UserDAO;
import org.springframework.jdbc.roma.impl.integration.model.BloodType;
import org.springframework.jdbc.roma.impl.integration.model.Education;
import org.springframework.jdbc.roma.impl.integration.model.Gender;
import org.springframework.jdbc.roma.impl.integration.model.Language;
import org.springframework.jdbc.roma.impl.integration.model.MaritalStatus;
import org.springframework.jdbc.roma.impl.integration.model.Occupation;
import org.springframework.jdbc.roma.impl.integration.model.Permission;
import org.springframework.jdbc.roma.impl.integration.model.Religion;
import org.springframework.jdbc.roma.impl.integration.model.Role;
import org.springframework.jdbc.roma.impl.integration.model.User;

/**
 * @author Serkan ÖZAL
 */
public class RowMapperIntegrationTest extends BaseRomaIntegrationTest {
	
	
	@Autowired
	private UserDAO userDAO;

	@SuppressWarnings("deprecation")
	@Test
	public void usersAndTheirRolesAndTheirPermissionsRetrievedSuccessfully() {
		rowMapperService.enableLazyConditionProperty("creditCardInfoLazyCondition");
		
		rowMapperService.enableIgnoreConditionProperty("creditCardInfoIgnoreCondition");
		
		List<User> userList = userDAO.list();
		
		Assert.assertNotNull(userList);
		Assert.assertEquals(1, userList.size());
		
		User user = userList.get(0);

		Assert.assertEquals("user", user.getUsername());
		Assert.assertEquals("password", user.getPassword());
		Assert.assertEquals("Serkan", user.getFirstname());
		Assert.assertEquals("OZAL", user.getLastname());
		Assert.assertEquals("Ankara", user.getAddress().getCity());
		Assert.assertEquals("Turkey", user.getAddress().getCountry());
		Assert.assertEquals(true, user.isEnabled());
		Assert.assertEquals(Gender.MALE, user.getGender());
		Assert.assertEquals(Language.TURKISH, user.getLanguage());
		Assert.assertEquals(Occupation.ENGINEER, user.getOccupation());
		Assert.assertEquals(Education.MASTER, user.getEducation());
		Assert.assertEquals(BloodType.TYPE_A_RH_POSITIVE, user.getBloodType());
		Assert.assertEquals(MaritalStatus.SINGLE, user.getMaritalStatus());
		Assert.assertEquals(Religion.MUSLIM, user.getReligion());
		Assert.assertEquals(new Date(1986 - 1900, 9 - 1, 15), user.getBirthDate());
		Assert.assertEquals("+901234567890", user.getPhoneNumber());
		
		List<Role> roleList = user.getRoles();
		
		Assert.assertNotNull(roleList);
		Assert.assertEquals(2, roleList.size());
		
		Role roleAdmin = roleList.get(0);
		Role roleMember = roleList.get(1);
		
		Assert.assertEquals("Admin", roleAdmin.getName());
		Assert.assertEquals("Member", roleMember.getName());
		
		List<Permission> adminPermissionList = roleAdmin.getPermissions();
		List<Permission> memberPermissionList = roleMember.getPermissions();
		
		Collections.sort(adminPermissionList);
		Collections.sort(memberPermissionList);
		
		Assert.assertNotNull(adminPermissionList);
		Assert.assertNotNull(memberPermissionList);
		
		Assert.assertEquals(4, adminPermissionList.size());
		Assert.assertEquals(4, memberPermissionList.size());
		
		Assert.assertEquals("ADMIN_DELETE_PERM", adminPermissionList.get(0).getName());
		Assert.assertEquals("ADMIN_GET_PERM", adminPermissionList.get(1).getName());
		Assert.assertEquals("ADMIN_LIST_PERM", adminPermissionList.get(2).getName());
		Assert.assertEquals("ADMIN_UPDATE_PERM", adminPermissionList.get(3).getName());
		
		Assert.assertEquals("MEMBER_DELETE_PERM", memberPermissionList.get(0).getName());
		Assert.assertEquals("MEMBER_GET_PERM", memberPermissionList.get(1).getName());
		Assert.assertEquals("MEMBER_LIST_PERM", memberPermissionList.get(2).getName());
		Assert.assertEquals("MEMBER_UPDATE_PERM", memberPermissionList.get(3).getName());
		
		rowMapperService.disableIgnoreConditionProperty("creditCardInfoIgnoreCondition");
		
		rowMapperService.disableLazyConditionProperty("creditCardInfoLazyCondition");
	}
	
}
