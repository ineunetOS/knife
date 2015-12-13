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
 * @author Hilbert
 * @see {@link com.ineunet.knife.api.RecordStatus}
 * @since 1.2.0
 *
 * @param <T>
 */
public interface IJdbcStatusModel<T> extends IJdbcModel<T> {

	/**
	 *  0:normal.<br>
	 *  1:deleted.<br>
	 *  2:freeze.<br>
	 *  3:destroyed
	 * @return status code
	 */
	int getStatus();

	/**
	 *  0:normal.<br>
	 *  1:freeze.<br>
	 *  2:deleted.<br>
	 *  3:destroyed
	 * @param status -1==destroyed
	 */
	void setStatus(int status);
	
	/**
	 * @return deleteTime or destroyTime
	 */
	Date getDelTime();

	void setDelTime(Date delTime);
	
}
