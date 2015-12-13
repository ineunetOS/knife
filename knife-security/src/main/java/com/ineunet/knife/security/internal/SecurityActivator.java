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
package com.ineunet.knife.security.internal;

import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.ineunet.knife.mgt.AbstractActivator;
import com.ineunet.knife.mgt.IBundleContext;
import com.ineunet.knife.mgt.log.IMgtLogger;
import com.ineunet.knife.mgt.stereotype.BundleActivator;
import com.ineunet.knife.security.Server;
import com.ineunet.knife.security.cache.ITenantCache;
import com.ineunet.knife.security.service.ISecurityService;

/**
 * 
 * @author Hilbert
 *
 */
@BundleActivator("securityActivator")
class SecurityActivator extends AbstractActivator {
	
	@Override
	protected void start(IBundleContext bc) {
		// initialize static configuration before first use
		Server.isMultiTenantMode();
		
		//securityService
		this.registerSpringBean(ISecurityService.SERVICE_NAME, ISecurityService.class);
		this.registerSpringBeanIfExists(ITenantCache.SERVICE_NAME, ITenantCache.class);
		
		// sessionDao
		this.registerSpringBeanIfExists("sessionDAO", SessionDAO.class);
		
		// MenuCache.jobClearData();
	}

	@Override
	protected void stop(IBundleContext bc) {
		bc.unregisterService(IMgtLogger.SERVICE_NAME, IMgtLogger.class);
		bc.unregisterService(ITenantCache.SERVICE_NAME, ITenantCache.class);
		bc.unregisterService(ISecurityService.SERVICE_NAME, ISecurityService.class);
		bc.unregisterService("sessionDAO", SessionDAO.class);
	}
	
}
