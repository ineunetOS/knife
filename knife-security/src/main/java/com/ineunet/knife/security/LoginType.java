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
package com.ineunet.knife.security;

/**
 * 
 * Define account kinds
 * 
 * @author Hilbert
 *
 */
public enum LoginType {

	unknown(0),
	tenant(1),
	user(2);

	private int value;

	LoginType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static LoginType valueOf(int value) {
		switch (value) {
		case 1:
			return tenant;
		case 2:
			return user;
		default:
			return unknown;
		}
	}
	
}
