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
package com.ineunet.knife.security.service;

import java.util.List;

import com.ineunet.knife.api.security.IOrganization;
import com.ineunet.knife.api.security.ITenant;
import com.ineunet.knife.api.security.IUser;
import com.ineunet.knife.core.security.AccountModel;

/**
 * For extension by someone who implemented new subclass of IUser etc.
 * @author Hilbert Wang
 * @since 2013-8-21
 */
public interface ISecurityService {
	
	static final String SERVICE_NAME = "securityService";

	Long getCurrentAccountId();
	
	AccountModel getCurrentUser();
	
	/**
	 * @param account
	 * @param tenantId
	 * @return
	 */
	AccountModel findUser(String account, Long tenantId);
	
	/**
	 * for realm
	 * @param account
	 * @return user if staus==normal, else retun null.
	 */
	AccountModel findUser(String account, String domain);
	
	/**
	 * for realm
	 * @param account
	 * @return user if staus==normal, else retun null.
	 */
	AccountModel findUser(String account);
	
	IUser getUser(Long id, Class<? extends IUser> entityClass);
	
	/**
	 * 
	 * @param user
	 * @param encrypt whether encrypt
	 * @return id of new user
	 * @since 1.2.0
	 */
	Long createUser(IUser user, boolean encrypt);
	
	/**
	 * bind clear cache
	 * @param user
	 * @param encrypt whether encrypt
	 * @since 1.2.0
	 */
	void updateUser(IUser user, boolean encrypt);
	
	/**
	 * bind clear cache
	 * @param userId
	 * @since 1.0.5
	 */
	void deleteUserById(Long userId, Class<? extends IUser> entityClass);
	
	/**
	 * @param account account of user
	 * @param tenantId tenantId of account
	 * @since 1.2.0
	 */
	void deleteUserByAccount(String account, Object tenantId, Class<? extends IUser> entityClass);
	
	ITenant getTenant(Long id, Class<? extends ITenant> tenantClass);
	
	/**
	 * bind clear cache
	 * @param tenant
	 * 
	 * @since 1.0.5
	 */
	void updateTenant(ITenant tenant);
	
	/**
	 * bind clear cache
	 * @param tenantId
	 * 
	 * @since 1.0.5
	 */
	void deleteTenantById(Object tenantId, Class<? extends ITenant> tenantClass);
	
	/**
	 * @param tenantAccount account of tenant
	 * @return
	 */
	ITenant getTenantByAccount(String tenantAccount, Class<? extends ITenant> tenantClass);
	
	IOrganization getCurrentOrg(Class<? extends IUser> userClass, Class<? extends IOrganization> orgClass);
	
	Long getCurrentOrgId(Class<? extends IUser> userClass, Class<? extends IOrganization> orgClass);
	
	/**
	 * @since 2.0.0
	 * @return
	 */
	List<Long> getCurrPermedOrgIds();
	
	/**
	 * @param accountId
	 * @return id of Roles
	 * @since 2.0.0
	 */
	List<Long> getRoleIds(Long accountId);
	
	/**
	 * @param roleId
	 * @return name of role
	 */
	String getRoleName(Long roleId);
	
	/**
	 * @param accountId
	 * @return permissions of the user <tt>accountId</tt>
	 */
	List<String> getPermissions(Long accountId);
	
	//********************* passwd BEGIN ***************************//
	String getUserPasswd(Long id);
	
	String getTenantPasswd(Long id);
	
	/**
	 * @param old old password
	 * @param _new new password
	 * 
	 * @since 1.0.5
	 */
	boolean modifyPasswd(String old, String _new, Class<? extends IUser> userClass, Class<? extends ITenant> tenantClass);
	//********************* passwd END ***************************//
	
}
