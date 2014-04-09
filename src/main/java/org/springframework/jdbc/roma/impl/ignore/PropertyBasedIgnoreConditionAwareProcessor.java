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

package org.springframework.jdbc.roma.impl.ignore;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperPropertyBasedIgnoreConditionAware;
import org.springframework.stereotype.Component;

/**
 * @author Serkan ÖZAL
 */
@Component
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PropertyBasedIgnoreConditionAwareProcessor {

	private static final Logger logger = Logger.getLogger(PropertyBasedIgnoreConditionAwareProcessor.class);
	
	@Autowired
	private IgnoreManager ignoreManager;
	
	@Around("@annotation(org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperPropertyBasedIgnoreConditionAware)" + 
			" and args(propertyBasedIgnoreConditionAware)")
	public Object propertyBasedIgnoreConditionAwareInterceptor(ProceedingJoinPoint joinPoint, 
			RowMapperPropertyBasedIgnoreConditionAware propertyBasedIgnoreConditionAware) throws Throwable {
		final String propertyName = propertyBasedIgnoreConditionAware.propertyName();
		try {
			if ((propertyBasedIgnoreConditionAware.options() & 
					RowMapperPropertyBasedIgnoreConditionAware.ENABLE_ON_START) > 0) {
				ignoreManager.enableIgnoreConditionProperty(propertyName);
				if (logger.isDebugEnabled()) {
					logger.debug("Enabled property " + propertyName + " on start of " + joinPoint.toLongString());
				}	
			}
			else if ((propertyBasedIgnoreConditionAware.options() & 
					RowMapperPropertyBasedIgnoreConditionAware.DISABLE_ON_START) > 0) {
				ignoreManager.disableIgnoreConditionProperty(propertyName);
				if (logger.isDebugEnabled()) {
					logger.debug("Disabled property " + propertyName + " on start of " + joinPoint.toLongString());
				}	
			}
			
		    return joinPoint.proceed();
		}
		finally {
			if ((propertyBasedIgnoreConditionAware.options() & 
					RowMapperPropertyBasedIgnoreConditionAware.ENABLE_ON_FINISH) > 0) {
				ignoreManager.enableIgnoreConditionProperty(propertyName);
				if (logger.isDebugEnabled()) {
					logger.debug("Enabled property " + propertyName + " on finish of " + joinPoint.toLongString());
				}
			}
			else if ((propertyBasedIgnoreConditionAware.options() & 
					RowMapperPropertyBasedIgnoreConditionAware.DISABLE_ON_FINISH) > 0) {
				ignoreManager.disableIgnoreConditionProperty(propertyName);
				if (logger.isDebugEnabled()) {
					logger.debug("Disabled property " + propertyName + " on finish of " + joinPoint.toLongString());
				}	
			}
		}
	}
	
}
