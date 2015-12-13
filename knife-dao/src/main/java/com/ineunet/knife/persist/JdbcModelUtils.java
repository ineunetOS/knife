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
package com.ineunet.knife.persist;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.0
 *
 */
public abstract class JdbcModelUtils {
	
	private static final String NORMAL = "正常";
	private static final String FREEZE = "已冻结";
	private static final String DELETED = "已删除";
	private static final String DESTROYED = "已销毁";
	
	/**
	 *  0:normal.<br>
	 * -1:deleted.<br>
	 * -2:freeze.<br>
	 * -3:destroyed
	 * @return status Name
	 */
	public static String getDeleteStatus(int status) {
		switch (status) {
		case 0:
			return NORMAL;
		case -1:
			return FREEZE;
		case -2:
			return DELETED;
		case -3:
			return DESTROYED;
		}
		throw new IllegalArgumentException("Unknown status: " + status);
	}

}
