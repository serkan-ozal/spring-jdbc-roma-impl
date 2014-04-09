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

package org.springframework.jdbc.roma.impl.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperField;
import org.springframework.jdbc.roma.impl.ContextAwareRomaTest;
import org.springframework.jdbc.roma.impl.service.RowMapperService;

/**
 * @author Serkan ÖZAL
 */
public class RowMapperServiceTest extends ContextAwareRomaTest {

	@Autowired
	private RowMapperService rowMapperService;
	
	@Test
	public void getRowMapper() {
		Assert.assertNotNull(rowMapperService.getRowMapper(SampleClass.class));
	}
	
	public static class SampleClass {

		@RowMapperField(columnName="id")
		private Long id;
		@RowMapperField(columnName="name")
		private String name;
		
		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
	}	
	
}
