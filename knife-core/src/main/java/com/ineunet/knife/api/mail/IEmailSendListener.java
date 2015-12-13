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
package com.ineunet.knife.api.mail;


/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 *
 */
public interface IEmailSendListener {

	/**
	 * 
	 * @param operator system operator
	 * @param sender send from who's email address
	 * @param recipients
	 * @param success whether send succeed
	 */
	void handle(String operator, String sender, String[] recipients, boolean success);
	
}