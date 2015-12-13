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
package com.ineunet.knife.core.dataflow;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.2
 * Created on 2015-3-22
 */
public enum DataFlowMode {
	
	/**
	 * Keep history data and history status.<br>
	 * Recovery data by history status when execute <tt>back()</tt>.
	 */
	KEEP_STATUS,
	
	/**
	 * Compute by rule expressions. <br>e.g. 
	 * update?set[status=2]&set[updateTime=${datetime}]&set[updatePerson=${currentAccount}]& param[status=1]
	 */
	COMPUTE

}
