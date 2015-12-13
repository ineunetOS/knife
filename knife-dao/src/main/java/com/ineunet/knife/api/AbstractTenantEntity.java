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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.0.2
 *
 * @param <T>
 */
@MappedSuperclass
public abstract class AbstractTenantEntity<T> extends AbstractStatusEntity<T> implements IEntityTenant {

	private static final long serialVersionUID = 4038790702899603503L;
	protected Long tenantId;

	@Basic(optional = false)
	@Column(name = "tenant_id")
	public Long getTenantId() {
		return tenantId;
	}

	@Override
	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

}
