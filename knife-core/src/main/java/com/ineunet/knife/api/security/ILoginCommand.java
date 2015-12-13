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
package com.ineunet.knife.api.security;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.1.0
 *
 */
public interface ILoginCommand {

	String getAccount();

	void setAccount(String account);

	String getPassword();

	void setPassword(String password);

	boolean isRememberMe();

	void setRememberMe(boolean rememberMe);
	
	/**
	 * return account type if in need.<br>
	 * Also can as a redirect view type.
	 * @return account type
	 * 
	 * @since 1.2.0
	 */
	int getType();

	void setType(int type);

}
