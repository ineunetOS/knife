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
package com.ineunet.knife.security.cache;

import com.ineunet.knife.api.security.ITenant;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.1.1
 *
 */
public interface ITenantCache {
	
	static final String SERVICE_NAME = "tenantCache";

	ITenant getTenantById(Object id);
	
	/**
	 * @param key account
	 * @return tenant
	 */
	ITenant getTenantByAccount(String account);
	
	/**
	 * @param domain
	 * @return tenantId
	 * @since 1.2.2
	 */
	Long getIdByDomain(String domain);
	
	/**
	 * remove cache by tenantId
	 * @param tenantId
	 */
	void remove(Long tenantId);
	
}
