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

package org.springframework.jdbc.roma.impl.integration.model;

/**
 * @author Serkan ÖZAL
 */
public enum Occupation {

	OTHER(0),
	ARCHITECT(100),
	DOCTOR(200),
	ENGINEER(300),
	LAWYER(400),
	MUSICIAN(500),
	STUDENT(600),
	TEACHER(700);
	
	int code;
	
	Occupation(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}
