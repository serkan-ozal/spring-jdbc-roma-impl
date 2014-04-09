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

package org.springframework.jdbc.roma.impl.util;

import org.springframework.aop.framework.Advised;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Serkan ÖZAL
 */
@Component
public class SpringUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		if (applicationContext == null) {
			return null;
		}
		else {
			return (T)applicationContext.getBean(beanName);
		}	
	}
	
	public static <T> T getBean(Class<T> clazz) {
		if (applicationContext == null) {
			return null;
		}
		else {
			return (T)applicationContext.getBean(clazz);
		}	
	}
	
	public static Class<?> getType(String beanName) {
		if (applicationContext == null) {
			return null;
		}
		else {
			Object bean = applicationContext.getBean(beanName);
			if (bean == null) {
				return null;
			}
			if (bean instanceof Advised) {
				Advised advised = (Advised) bean;
				Class<?>[] proxiedInterfaces = advised.getProxiedInterfaces();
				if (proxiedInterfaces == null || proxiedInterfaces.length == 0) {
					return advised.getTargetSource().getTargetClass();
				}
				else {
					return proxiedInterfaces[0];
				}
			}	
			else {
				return bean.getClass();
			}
		}	
	}
	
}
