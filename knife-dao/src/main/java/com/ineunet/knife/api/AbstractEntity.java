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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.ineunet.knife.util.ReflectionUtils;
import com.ineunet.knife.util.jackson.JsonDateTimeSerializer;

/**
 * 
 * @author Hilbert
 *
 * @param <T>
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class AbstractEntity<T> implements IEntity<T> {

	private static final long serialVersionUID = 4038790702899603503L;
	protected T id;
	protected Class<T> idClass = ReflectionUtils.getSuperClassGenricType(getClass());
	private String createPerson;
	private Date createTime;
	private String updatePerson;
	private Date updateTime;
	
	public void setId(T id) {
		this.id = id;
	}
	
	@Transient
	@JsonIgnore
	public Class<T> getIdClass() {
		return idClass;
	}

	@Column(name = "create_person", length = 32)
	public String getCreatePerson() {
		return createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	@Column(name = "create_time")
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_person", length = 32)
	public String getUpdatePerson() {
		return updatePerson;
	}

	public void setUpdatePerson(String updatePerson) {
		this.updatePerson = updatePerson;
	}

	@Column(name = "update_time")
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
