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

import java.util.Date;
import java.util.Set;

/**
 * 
 * @author Hilbert
 * 
 */
public interface ITenant {

	Long getId();

	void setId(Long id);

	String getAccount();

	void setAccount(String account);

	String getPassword();

	void setPassword(String passwd);

	String getName();

	void setName(String name);

	String getDomain();

	void setDomain(String domain);

	String getEmail();

	void setEmail(String email);

	String getPhone();

	void setPhone(String phone);

	TenantRole getRole();

	void setRole(TenantRole role);

	String getDescription();

	void setDescription(String description);

	Set<? extends IRole> getRoles();

	void setRoles(Set<? extends IRole> roles);
	
	String getIndustryNumber();
	
	void setIndustryNumber(String industryNumber);
	
	String getCityNumber();
	
	void setCityNumber(String cityNumber);
	
	String getAddress();
	
	void setAddress(String address);
	
	Date getLimitBegin();
	
	void setLimitBegin(Date limitBegin);
	
	Date getLimitEnd();
	
	void setLimitEnd(Date limitEnd);
	
	public static enum TenantRole {

		none(0),
		visitor(1),
		member(2),
		admin(3),
		owner(4);

		private int value;

		TenantRole(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static TenantRole valueOf(int value) {
			switch (value) {
			case 0:
				return none;
			case 1:
				return visitor;
			case 2:
				return member;
			case 3:
				return admin;
			case 4:
				return owner;
			default:
				return none;
			}
		}
		
	}

}
