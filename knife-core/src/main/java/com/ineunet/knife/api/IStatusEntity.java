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

/**
 * 
 * @author Hilbert Wang
 *
 * @param <T>
 * @since 2.0.0
 */
public interface IStatusEntity<T> extends IEntity<T> {

	RecordStatus getStatus();

	void setStatus(RecordStatus status);
	
	/**
	 * @return deleteTime or destroyTime
	 */
	Date getDelTime();

	void setDelTime(Date delTime);
	
	/**
	 * @return account of the person who do 'delete'
	 */
	String getDelPerson();
	
	void setDelPerson(String delPerson);
	
}
