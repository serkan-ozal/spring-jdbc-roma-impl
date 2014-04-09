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
public enum BloodType {

	TYPE_A_RH_POSITIVE(1),
	TYPE_A_RH_NEGATIVE(2),
	TYPE_B_RH_POSITIVE(3),
	TYPE_B_RH_NEGATIVE(4),
	TYPE_AB_RH_POSITIVE(5),
	TYPE_AB_RH_NEGATIVE(6),
	TYPE_0_RH_POSITIVE(7),
	TYPE_0_RH_NEGATIVE(8);
	
	int code;
	
	BloodType(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}
