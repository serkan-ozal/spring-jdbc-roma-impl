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

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperClass;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperCustomProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperSqlProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumAutoMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumNumericMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumNumericValueNumericMapping;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumStringMapper;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperEnumField.RowMapperEnumStringValueNumericMapping;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreField;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperExpressionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperIgnoreCondition.RowMapperPropertyBasedIgnoreConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperCustomLazyConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperLazyCondition.RowMapperPropertyBasedLazyConditionProvider;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperObjectField;
import org.springframework.jdbc.roma.impl.integration.custom.BloodTypeEnumMapper;
import org.springframework.jdbc.roma.impl.integration.custom.MaritalStatusEnumMapper;
import org.springframework.jdbc.roma.impl.integration.custom.UserAccountInfoSqlQueryInfoProvider;
import org.springframework.jdbc.roma.impl.integration.custom.UserObjectProcessor;
import org.springframework.jdbc.roma.impl.integration.custom.UserPhoneNumberFieldProvider;
import org.springframework.jdbc.roma.impl.integration.custom.UserRolesLazyConditionProvider;

/**
 * @author Serkan ÖZAL
 */
@RowMapperClass(objectProcessor = UserObjectProcessor.class)
public class User {

	private Long id;
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	@RowMapperObjectField(
		provideViaCustomProvider = 
			@RowMapperCustomProvider(
					fieldProvider = UserPhoneNumberFieldProvider.class))
	private String phoneNumber;
	@RowMapperObjectField(
		provideViaExpressionProvider = 
			@RowMapperExpressionProvider(
					expression = "new Address(&{[string]city}, &{[string]country})",
					usedClasses = {Address.class}))
	private Address address;
	private boolean enabled = true;
	private Gender gender;
	private Date birthDate;
	@RowMapperEnumField(enumStartValue = 1)
	private Language language;
	@RowMapperEnumField(
			mapViaAutoMapper = 
				@RowMapperEnumAutoMapper(
						mapViaNumericValueNumericMappings = {
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 0 , value = 0),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 1 , value = 100),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 2 , value = 200),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 3 , value = 300),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 4 , value = 400),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 5 , value = 500),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 6 , value = 600),
								@RowMapperEnumNumericValueNumericMapping(mappingIndex = 7 , value = 700)
						}))
	private Occupation occupation;
	@RowMapperEnumField(
			mapViaAutoMapper = 
				@RowMapperEnumAutoMapper(
						mapViaStringValueNumericMappings = {
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 0, value = "PRIMARY_SCHOOL"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 1, value = "SECONDARY_SCHOOL"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 2, value = "HIGH_SCHOOL"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 3, value = "BACHELOR"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 4, value = "MASTER"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 5, value = "PHD" ),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 6, value = "ASSOCIATE_PROFESSOR"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 7, value = "PROFESSOR"),
								@RowMapperEnumStringValueNumericMapping(mappingIndex = 8, value = "OTHER")
						}))
	private Education education;
	@RowMapperEnumField(
			mapViaNumericMapper = 
				@RowMapperEnumNumericMapper(
							mapper = BloodTypeEnumMapper.class))
	private BloodType bloodType;
	@RowMapperEnumField(
			mapViaStringMapper = 
				@RowMapperEnumStringMapper(
							mapper = MaritalStatusEnumMapper.class))
	private MaritalStatus maritalStatus;
	@RowMapperEnumField(useStringValue = true)
	private Religion religion;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
					expression = "@{roleDAO}.getUserRoleList(${id})"),		
			lazy = true,
			lazyCondition = 
			@RowMapperLazyCondition(
					provideViaCustomProvider = 
						@RowMapperCustomLazyConditionProvider(
								lazyConditionProvider = UserRolesLazyConditionProvider.class)))
	private List<Role> roles;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
					expression = "@{creditCardInfoDAO}.getUserCreditCardInfo(${id})"),		
			lazy = true,
			lazyCondition = 
			@RowMapperLazyCondition(
					provideViaPropertyBasedProvider = 
						@RowMapperPropertyBasedLazyConditionProvider(
								propertyName = "creditCardInfoLazyCondition")))
	private CreditCardInfo creditCardInfo;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
					expression = "@{creditCardInfoDAO}.getUserSecondaryCreditCardInfo(${id})"),		
			lazy = true,
			lazyCondition = 
			@RowMapperLazyCondition(
					provideViaPropertyBasedProvider = 
						@RowMapperPropertyBasedLazyConditionProvider(
								propertyName = "creditCardInfoLazyCondition")))
	private CreditCardInfo secondaryCreditCardInfo;
	@RowMapperObjectField(
			provideViaExpressionProvider = 
				@RowMapperExpressionProvider(
					expression = "@{creditCardInfoDAO}.getUserCreditCardInfo(${id})"),		
			ignoreCondition = 
				@RowMapperIgnoreCondition(
					provideViaPropertyBasedProvider = 
						@RowMapperPropertyBasedIgnoreConditionProvider(
								propertyName = "creditCardInfoIgnoreCondition")))
	private CreditCardInfo previousCreditCardInfo;
	@RowMapperObjectField(
			provideViaSqlProvider = 
				@RowMapperSqlProvider(
					sqlQueryInfoProvider = UserAccountInfoSqlQueryInfoProvider.class))
	private AccountInfo accountInfo;
	
	@RowMapperIgnoreField // Or define field as transient
	private byte age;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstname() {
		return firstname;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Gender getGender() {
		return gender;
	}
	
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	public Date getBirthDate() {
		return birthDate;
	}
	
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public void setLanguage(Language language) {
		this.language = language;
	}
	
	public Occupation getOccupation() {
		return occupation;
	}
	
	public void setOccupation(Occupation occupation) {
		this.occupation = occupation;
	}
	
	public Education getEducation() {
		return education;
	}
	
	public void setEducation(Education education) {
		this.education = education;
	}
	
	public BloodType getBloodType() {
		return bloodType;
	}
	
	public void setBloodType(BloodType bloodType) {
		this.bloodType = bloodType;
	}
	
	public MaritalStatus getMaritalStatus() {
		return maritalStatus;
	}
	
	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	
	public Religion getReligion() {
		return religion;
	}
	
	public void setReligion(Religion religion) {
		this.religion = religion;
	}
	
	public List<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		roles.add(role);
	}
	
	public void removeRole(Role role) {
		roles.remove(role);
	}
	
	public CreditCardInfo getCreditCardInfo() {
		return creditCardInfo;
	}
	
	public void setCreditCardInfo(CreditCardInfo creditCardInfo) {
		this.creditCardInfo = creditCardInfo;
	}
	
	public CreditCardInfo getSecondaryCreditCardInfo() {
		return secondaryCreditCardInfo;
	}
	
	public void setSecondaryCreditCardInfo(CreditCardInfo secondaryCreditCardInfo) {
		this.secondaryCreditCardInfo = secondaryCreditCardInfo;
	}
	
	public CreditCardInfo getPreviousCreditCardInfo() {
		return previousCreditCardInfo;
	}
	
	public void setPreviousCreditCardInfo(CreditCardInfo previousCreditCardInfo) {
		this.previousCreditCardInfo = previousCreditCardInfo;
	}
	
	public AccountInfo getAccountInfo() {
		return accountInfo;
	}
	
	public void setAccountInfo(AccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}
	
	public byte getAge() {
		return age;
	}
	
	public void setAge(byte age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		return 
				"Username                   : " + username 					+ "\n" +
				"Password                   : " + password 					+ "\n" +
				"First Name                 : " + firstname 				+ "\n" +
				"Last Name                  : " + lastname 					+ "\n" +
				"Phone Number               : " + phoneNumber 				+ "\n" +
				"Address                    : " + address 					+ "\n" +
				"Enabled                    : " + enabled 					+ "\n" +
				"Gender                     : " + gender 					+ "\n" +
				"Birth Date                 : " + birthDate 				+ "\n" +
				"Language                   : " + language 					+ "\n" +
				"Occupation                 : " + occupation 				+ "\n" +
				"Education                  : " + education 				+ "\n" +
				"Blood Type                 : " + bloodType					+ "\n" +
				"Marital Status             : " + maritalStatus				+ "\n" +
				"Religion                   : " + religion					+ "\n" +
				"Credit Card Info           : " + creditCardInfo			+ "\n" +
				"Secondary Credit Card Info : " + secondaryCreditCardInfo	+ "\n" +
				"Previous Credit Card Info  : " + previousCreditCardInfo	+ "\n" +
				"Account Info               : " + accountInfo				+ "\n" +
				"Age                        : " + age;
	}
	
}
