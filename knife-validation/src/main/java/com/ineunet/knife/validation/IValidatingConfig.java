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

import java.util.List;

import com.ineunet.knife.core.validation.ValidatingProperty;
import com.ineunet.knife.core.validation.ValidatingType;

/**
 * To configure property name and property title or validation length, etc. for extension fields.
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-16
 */
public interface IValidatingConfig {

	Class<?> getOwnerClass();
	
	String getTitle(String propertyName);
	
	/**
	 * Default {string=255, long=19, int=11}
	 * @param propertyName
	 * @return validating length
	 */
	int getLength(String propertyName);
	
	boolean getNullable(String propertyName);
	
	boolean existsProperty(String propertyName);
	
	ValidatingType[] getType(String propertyName);
	
	List<ValidatingProperty> loadProperties();
	
	// like init
	void addProperties(List<ValidatingProperty> props);
	
	/**
	 * @return names of get method. e.g. [name,code]
	 */
	List<String> getPropertyNames();
	
}
