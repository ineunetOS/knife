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
package com.ineunet.knife.api;

/**
 * 
 * Status of db records.
 * @author Hilbert Wang
 * @since 1.0.5
 * 
 */
public enum RecordStatus {

	normal(0),
	freezed(1),
	deleted(2), // logical deleted
	destroyed(3); 
	private int value;

	RecordStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public static RecordStatus nameOf(String name) {
		if (normal.name().equals(name))
			return normal;
		if (deleted.name().equals(name))
			return deleted;
		if (freezed.name().equals(name))
			return freezed;
		if (destroyed.name().equals(name))
			return destroyed;
		throw new IllegalArgumentException("Illegal argument name: " + name);
	}

	public static RecordStatus valueOf(int value) {
		switch (value) {
		case 0:
			return normal;
		case -1:
			return deleted;
		case -2:
			return freezed;
		case -3:
			return destroyed;
		}
		throw new IllegalArgumentException("Illegal argument value: " + value);
	}
}
