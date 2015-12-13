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
package com.ineunet.knife.security.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.ineunet.knife.api.AbstractTenantEntity;
import com.ineunet.knife.api.INameEntity;

/**
 * 
 * @author Hilbert
 *
 */
@Entity
@Table(name = "knife_sys_param")
public class SysParam extends AbstractTenantEntity<String> implements INameEntity<String> {

	private static final long serialVersionUID = 1348487785025443978L;
	private String value;
	private String description;

	public SysParam() {
	}

	public SysParam(String id) {
		this.id = id;
	}

	@Id
	public String getId() {
		return this.id;
	}
	
	@Transient
	@JsonIgnore
	@Override
	public String getName() {
		return id;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String name) {
		this.value = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
