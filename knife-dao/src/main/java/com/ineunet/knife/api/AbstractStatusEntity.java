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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.ineunet.knife.util.jackson.JsonDateTimeSerializer;

/**
 * 
 * @author Hilbert
 *
 * @param <T>
 * @since 2.0.0
 */
@MappedSuperclass
public abstract class AbstractStatusEntity<T> extends AbstractEntity<T> implements IStatusEntity<T> {

	private static final long serialVersionUID = -8381386325456243027L;

	private RecordStatus status = RecordStatus.normal;
	private Date delTime;
	private String delPerson;

	@Basic(optional = false)
	@Enumerated(value = EnumType.ORDINAL)
	@Column(name = "record_status")
	public RecordStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(RecordStatus status) {
		this.status = status;
	}

	@Column(name = "del_time")
	@JsonSerialize(using = JsonDateTimeSerializer.class)
	public Date getDelTime() {
		return delTime;
	}

	@Override
	public void setDelTime(Date delTime) {
		this.delTime = delTime;
	}

	@Column(name = "del_person", length = 32)
	public String getDelPerson() {
		return delPerson;
	}

	@Override
	public void setDelPerson(String delPerson) {
		this.delPerson = delPerson;
	}

}
