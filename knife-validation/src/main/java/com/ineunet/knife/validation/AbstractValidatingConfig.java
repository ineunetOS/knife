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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ineunet.knife.core.validation.ValidatingProperty;
import com.ineunet.knife.core.validation.ValidatingType;
import com.ineunet.knife.util.Asserts;

/**
 * e.g. extension of User<p>
 * <code>
 * 		public class UserProp extends AbstractPropertyConfig {<br>
			public void addProperties() {<br>
				this.addProperty(new Property(name, title, ownerClass));<br>
			}<br>
 *      	<br>
 *      	@Override public Class<?> getOwnerClass() {<br> 
 *      		return User.class; <br>
 *      	}<br>
 * 		} 
 * </code>
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-16
 */
public abstract class AbstractValidatingConfig implements IValidatingConfig {

	private final List<String> propNames = new ArrayList<String>();
	private final Map<String, ValidatingProperty> props = new HashMap<String, ValidatingProperty>();
	private boolean first = true;
	
	protected abstract void addValidatingProperties(List<ValidatingProperty> list);

	@Override
	public List<ValidatingProperty> loadProperties() {
		List<ValidatingProperty> list = new ArrayList<ValidatingProperty>();
		addValidatingProperties(list);
		if (first) {
			for(ValidatingProperty prop : list)
				this.addProperty(prop);
			first = false;
		}
		return list;
	}
	
	@Override
	public void addProperties(List<ValidatingProperty> props) {
		if (props == null) return;
		for (ValidatingProperty prop : props) {
			this.addProperty(prop);
		}
	}
	
	private void addProperty(ValidatingProperty prop) {
		String key = prop.getName();
		Asserts.notBlank(key);
		propNames.add(key);
		props.put(key, prop);
	}

	@Override
	public String getTitle(String propertyName) {
		ValidatingProperty vp = props.get(propertyName);
		if (vp == null)
			throw new IllegalArgumentException(propertyName + " not exsists.");
		return vp.getTitle();
	}

	@Override
	public int getLength(String propertyName) {
		ValidatingProperty vp = props.get(propertyName);
		if (vp == null)
			throw new IllegalArgumentException(propertyName + " not exsists.");
		return vp.getLength();
	}

	@Override
	public boolean getNullable(String propertyName) {
		ValidatingProperty vp = props.get(propertyName);
		if (vp == null)
			throw new IllegalArgumentException(propertyName + " not exsists.");
		return vp.getNullable();
	}
	
	@Override
	public ValidatingType[] getType(String propertyName) {
		ValidatingProperty vp = props.get(propertyName);
		if (vp == null)
			throw new IllegalArgumentException(propertyName + " not exsists.");
		return vp.getValidatingType();
	}
	
	@Override
	public boolean existsProperty(String propertyName) {
		return propNames.contains(propertyName);
	}
	
	@Override
	public List<String> getPropertyNames() {
		return propNames;
	}

}
