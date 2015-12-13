/*
 * Copyright 2013-2016 iNeunet OpenSource and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ineunet.knife.validation;

import java.lang.reflect.Method;
import java.util.List;

import com.ineunet.knife.core.validation.ValidatingProperty;
import com.ineunet.knife.core.validation.annotation.VProp;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-19
 */
public class AutoValidatingConfig extends AbstractValidatingConfig {

	private Class<?> entityClass;
	
	public AutoValidatingConfig(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
	@Override
	public Class<?> getOwnerClass() {
		return entityClass;
	}

	@Override
	protected void addValidatingProperties(List<ValidatingProperty> list) {
		Method[] methods = this.entityClass.getDeclaredMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			// match 'public * get*()'
			if (methodName.startsWith("get")) {
				VProp annot = method.getAnnotation(VProp.class);
				if (annot != null) {
					ValidatingProperty prop = new ValidatingProperty(annot.name(), annot.title(), annot.length(), annot.nullable(), annot.type());
					list.add(prop);
				}
			}
		}
	}

}
