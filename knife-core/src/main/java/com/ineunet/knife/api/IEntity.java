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

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.0.0
 *
 */
public interface IEntity<T> extends Serializable {

	T getId();
	void setId(T id);
	
	Class<T> getIdClass();
	
	/**
	 * @since 2.0.0
	 */
	String getCreatePerson();
	
	/**
	 * @since 2.0.0
	 */
	void setCreatePerson(String createPerson);
	
	/**
	 * @since 2.0.0
	 */
	Date getCreateTime();
	
	/**
	 * @since 2.0.0
	 */
	void setCreateTime(Date createTime);
	
	/**
	 * @since 2.0.0
	 */
	String getUpdatePerson();
	
	/**
	 * @since 2.0.0
	 */
	void setUpdatePerson(String updatePerson);
	
	/**
	 * @since 2.0.0
	 */
	Date getUpdateTime();
	
	/**
	 * @since 2.0.0
	 */
	void setUpdateTime(Date updateTime);
	
}
