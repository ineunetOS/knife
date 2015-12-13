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

import java.util.List;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 *
 */
public interface IEmailService {
	
	static final String SERVICE_NAME = "emailService";
	
	int send(String toEmail, String head, String content);
	
	int send(String fromName, String toEmail, String head, String content);
	
	int send(List<String> toEmails, String head, String content);
	
	int send(String fromName, List<String> toEmails, String head, String content);
	
	/**
	 * one-to-one
	 * @param mail
	 * @return
	 */
	int send(IMailInfo mail);
	
	/**
	 * for many-to-many
	 * @param mails
	 * @return
	 */
	int send(List<IMailInfo> mails);
	
	/**
	 * for one-to-many
	 * @param mails
	 * @param toEmail
	 * @return
	 */
	int send(List<IMailInfo> mails, String toEmail);

}
