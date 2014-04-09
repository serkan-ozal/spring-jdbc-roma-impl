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

import org.springframework.jdbc.roma.impl.integration.model.CreditCardInfo;

/**
 * @author Serkan ÖZAL
 */
public interface CreditCardInfoDAO {
	
	public CreditCardInfo get(Long id);
	public void add(CreditCardInfo creditCardInfo) throws Exception;
	public List<CreditCardInfo> list();
	public void addUserCreditCardInfo(Long userId, CreditCardInfo creditCardInfo) throws Exception;
	public CreditCardInfo getUserCreditCardInfo(Long userId);
	public CreditCardInfo getUserSecondaryCreditCardInfo(Long userId);

}
