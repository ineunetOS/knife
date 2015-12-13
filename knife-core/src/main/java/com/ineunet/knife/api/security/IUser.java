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

import java.util.Set;

import com.ineunet.knife.api.IEntityOrg;
import com.ineunet.knife.api.IEntityTenant;
import com.ineunet.knife.api.INameEntity;

/**
 * 
 * @author Hilbert
 * @since 2013-8-21
 *
 */
public interface IUser extends IEntityOrg, IEntityTenant, INameEntity<Long> {
	Long getId();
	void setId(Long id);
	String getAccount();
	void setAccount(String account);
	String getPassword();
	void setPassword(String passwd);
	String getName();
	void setName(String name);
	
	/**
	 * @return 0 female, 1 male
	 * @since 2.0.0
	 */
	Integer getSex();

	/**
	 * @param sex 0 female, 1 male
	 * @since 2.0.0
	 */
	void setSex(Integer sex);
	
	String getPhone();
	void setPhone(String phone);
	
	/**
	 * @return email of user
	 * 
	 * @since 1.2.0
	 */
	String getEmail();
	
	/**
	 * @param email email of user
	 * @since 1.2.0
	 */
    void setEmail(String email);
	Set<? extends IRole> getRoles();
    void setRoles(Set<? extends IRole> roles);
    
    Set<? extends IOrganization> getOrganizations();
    void setOrganizations(Set<? extends IOrganization> organizations);
    
    AccountStatus getAccountStatus();
    void setAccountStatus(AccountStatus status);
	String getDescription();
	void setDescription(String description);
    
    public static enum AccountStatus {
    	enabled(0),
    	disabled(1);

    	private int value;

    	AccountStatus(int value) {
    		this.value = value;
    	}

    	public int getValue() {
    		return value;
    	}

    	public static AccountStatus valueOf(int value) {
    		switch (value) {
    		case 0:
    			return enabled;
    		case 1:
    			return disabled;
    		default:
    			return enabled;
    		}
    	}
    }
    
}
