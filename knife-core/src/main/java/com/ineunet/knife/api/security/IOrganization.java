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
package com.ineunet.knife.api.security;


/**
 * 
 * @author Hilbert Wang
 * @since 1.0.3
 *
 */
public interface IOrganization {
	Long getId();
	void setId(Long id);
	Long getParentId();
	String getName();
	void setName(String name);
	String getCode();
	void setCode(String code);
	/**
	 * default:0; <br>
	 * 0: public or default department of certain tenant; 1~100 : specified organization or department;
	 * @return organization type
	 */
	Integer getType();
	void setType(Integer type);
	String getDescription();
	void setDescription(String description);
	
	/**
	 * @return id of associated entity
	 * @since 2.0.0
	 */
	Long getAssociatedId();
	
	/**
	 * @param associated id of associated entity
	 * @since 2.0.0
	 */
	void setAssociatedId(Long associatedId);
}
