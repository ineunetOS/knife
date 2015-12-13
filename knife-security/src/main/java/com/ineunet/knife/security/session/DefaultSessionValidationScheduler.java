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

import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import com.ineunet.knife.mgt.log.MgtLogUtils;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.1
 *
 */
public class DefaultSessionValidationScheduler implements SessionValidationScheduler {
	
	public static final String NAME = "defaultSessionValidationScheduler";
	static final SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
	Scheduler scheduler;
	
	/**
	 * "0 0 0/1 * * ?"
     * The default interval at which sessions will be validated (1 hour);
     * This can be overridden by calling {@link #setSessionValidationInterval(long)}
     */
    public static final String DEFAULT_SESSION_VALIDATION_INTERVAL = "0 0 0/1 * * ?";
    String interval = DEFAULT_SESSION_VALIDATION_INTERVAL;
	boolean enabled;
	ValidatingSessionManager sessionManager;
	
	public DefaultSessionValidationScheduler() {
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void enableSessionValidation() {
		try {
			this.scheduler = DefaultSessionValidationScheduler.schedFact.getScheduler();
			DefaultSessionValidationJob.setSessionManager(this.sessionManager);
			JobDetail jobDetail = new JobDetail("defaultSessionValidationJob", Scheduler.DEFAULT_GROUP, DefaultSessionValidationJob.class);
			CronTrigger trigger = new CronTrigger("defaultSessionValidationJobTrigger", "sessionValidation", this.interval);
			this.scheduler.scheduleJob(jobDetail, trigger);
			this.scheduler.start();
		} catch (Exception e) {
			MgtLogUtils.error(e);
			throw new RuntimeException(e);
		}
		this.enabled = true;
	}

	@Override
	public void disableSessionValidation() {
		try {
			if(this.scheduler != null)
				this.scheduler.shutdown();
		} catch (SchedulerException e) {
			MgtLogUtils.error("disable defaultSessionValidationScheduler error", e);
			throw new RuntimeException("disable defaultSessionValidationScheduler error", e);
		}
	}

	public String getInterval() {
		return interval;
	}

	public void setInterval(String interval) {
		this.interval = interval;
	}

	public ValidatingSessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(ValidatingSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
}
