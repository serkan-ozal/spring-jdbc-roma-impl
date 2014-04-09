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

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.roma.impl.ContextAwareRomaTest;
import org.springframework.test.jdbc.JdbcTestUtils;

/**
 * @author Serkan ÖZAL
 */
public abstract class BaseRomaIntegrationTest extends ContextAwareRomaTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void before() {
		deleteDB();
		createDB();
		insertDB();
	}
	
	@After
	public void after() {
		deleteDB();
	}
	
	private void createDB() {
		JdbcTestUtils.executeSqlScript(jdbcTemplate, new ClassPathResource("/db/db-creation-scripts.sql"), true);
	}
	
	private void insertDB() {
		JdbcTestUtils.executeSqlScript(jdbcTemplate, new ClassPathResource("/db/db-insertion-scripts.sql"), true);
	}
	
	private void deleteDB() {
		JdbcTestUtils.executeSqlScript(jdbcTemplate, new ClassPathResource("/db/db-deletion-scripts.sql"), true);
	}
	
}
