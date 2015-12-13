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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ineunet.knife.core.validation.ValidatingProperty;
import com.ineunet.knife.validation.IValidatingConfig;

/**
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-16
 */
public class ValidatingConfigManager {

	private static final Map<String, IValidatingConfig> map = new HashMap<String, IValidatingConfig>();
	
	public static void register(IValidatingConfig property) {
		String key = property.getOwnerClass().getName();
		List<ValidatingProperty> list = property.loadProperties();
		
		IValidatingConfig originalConfig = map.get(key);
		if (originalConfig == null) {
			// 1st time
			map.put(key, property);
		} else {
			// e.g. 2nd time. originalConfig addAll newConfig
			originalConfig.addProperties(list);
			map.put(key, originalConfig);
			property = null;
		}
	}
	
	public static void unregister(Class<?> ownerClass) {
		String key = ownerClass.getName();
		map.put(key, null);
		map.remove(key);
	}
	
	public static IValidatingConfig get(Class<?> ownerClass) {
		return map.get(ownerClass.getName());
	}
	
}
