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
package com.ineunet.knife.core.validation;

import com.ineunet.knife.util.ArrayUtils;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-16
 */
public class ValidatingProperty {

	private String name;
	private String title;
	private int length = 255;
	private boolean nullable = true;
	private ValidatingType[] validatingType = {ValidatingType.STRING};
	
	private Class<?> ownerClass;
	
	public ValidatingProperty(String name, String title) {
		this.name = name;
		this.title = title;
	}
	
	public ValidatingProperty(String name, String title, int length, boolean nullable) {
		this(name, title);
		this.length = length;
		this.nullable = nullable;
	}
	
	public ValidatingProperty(String name, String title, int length, boolean nullable, ValidatingType... validatingType) {
		this(name, title, length, nullable);
		this.validatingType = validatingType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLength() {
		if (length == 255) {
			if (ArrayUtils.contains(this.validatingType, ValidatingType.INTEGER)) {
				return 11;
			} else if (ArrayUtils.contains(this.validatingType, ValidatingType.LONG)) {
				return 19;
			}
		}
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean getNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public Class<?> getOwnerClass() {
		return ownerClass;
	}

	public void setOwnerClass(Class<?> ownerClass) {
		this.ownerClass = ownerClass;
	}

	public ValidatingType[] getValidatingType() {
		return validatingType;
	}

	public void setValidatingType(ValidatingType[] validatingType) {
		this.validatingType = validatingType;
	}

}
