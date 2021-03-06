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
package com.ineunet.knife.security.session;

import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.1
 *
 */
public class DefaultSessionValidationJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(DefaultSessionValidationJob.class);
	private static ValidatingSessionManager sessionManager;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(sessionManager != null) {
			sessionManager.validateSessions();
			log.info("Validated Sessions.");
		} else {
			log.info("sessionManager is null");
		}
	}
	
	static void setSessionManager(ValidatingSessionManager sessionManager) {
		DefaultSessionValidationJob.sessionManager = sessionManager;
	}

}
