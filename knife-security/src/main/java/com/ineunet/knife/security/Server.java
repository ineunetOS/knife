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
package com.ineunet.knife.security;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;

import com.ineunet.knife.api.security.IOrganization;
import com.ineunet.knife.api.security.ITenant;
import com.ineunet.knife.api.security.ITenant.TenantRole;
import com.ineunet.knife.api.security.IUser;
import com.ineunet.knife.config.ConfigFactory;
import com.ineunet.knife.config.ConfigKeysKnife;
import com.ineunet.knife.core.Callable;
import com.ineunet.knife.core.security.AccountModel;
import com.ineunet.knife.mgt.BundleUtils;
import com.ineunet.knife.security.cache.ITenantCache;
import com.ineunet.knife.security.exception.NoPermissionException;
import com.ineunet.knife.security.exception.SecurityServerException;
import com.ineunet.knife.security.service.ISecurityService;
import com.ineunet.knife.util.ConcurrentCache;

/**
 * 
 * @author Hilbert
 * @since 1.0.2
 * 
 */
public final class Server {
	
	private static final ConcurrentCache<Long, Long> UID_TID = new ConcurrentCache<>();
	private static final Map<ServerCallable, Callable<?>> CALLABLES = new ConcurrentHashMap<>();
	
	private static ITenantCache tenantCache;
	private static ISecurityService securityService;
	
	/**
	 * @since 1.2.2
	 */
	public static final String KNIFE_ACCOUNT = "#*#*Knife*#*#";
	
	private Server() {
	}
	
	public static void addCallable(ServerCallable key, Callable<?> value) {
		CALLABLES.putIfAbsent(key, value);
	}

	/**
	 * @since 1.0.2
	 */
	public static Session getSession() {
		return SecurityUtils.getSubject().getSession();
	}

	/**
	 * @param session
	 * @since 1.2.1
	 */
	public static void logout(Session session) {
		session.removeAttribute(DefaultSubjectContext.AUTHENTICATED_SESSION_KEY);
		session.removeAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
	}

	/**
	 * Configured @root-context.xml
	 * @since 1.2.1
	 */
	public static SessionDAO getSessionDAO() {
		return BundleUtils.getService("sessionDAO", SessionDAO.class);
	}

	/**
	 * @param needPermission
	 * @param permission
	 * @return
	 * @since 1.2.1
	 */
	public static boolean isPermitted(boolean needPermission, String permission) {
		if (needPermission) {
			return SecurityUtils.getSubject().isPermitted(permission);
		}
		return true;
	}

	/**
	 * @param needPermission
	 * @param permission
	 * @throws NoPermissionException
	 * @since 1.2.1
	 */
	public static void validPermission(boolean needPermission, String permission) {
		if (!isPermitted(needPermission, permission))
			throw new NoPermissionException("hava no permission");
	}

	/**
	 * @param needPermission
	 * @param permission
	 * @throws NoPermissionException
	 * @since 1.2.1
	 */
	public static void validPermission(String permission) {
		if (!isPermitted(true, permission))
			throw new NoPermissionException("hava no permission");
	}
	
	/**
	 * @since 1.0.2
	 */
	public static boolean isMultiTenantMode() {
		return ConfigFactory.getKnifeConfig().get(ConfigKeysKnife.multi_tenant, false);
	}

	/**
	 * @since 1.0.2
	 */
	public static boolean allowTenantLogin() {
		return ConfigFactory.getKnifeConfig().get(ConfigKeysKnife.allow_tenant_login, false);
	}

	/**
	 * Cached, safe for use.
	 * 
	 * @return login type
	 * @since 1.0.2
	 */
	public static LoginType getLoginType() {
		return (LoginType) getSession().getAttribute(SessionKeys.LOGIN_TYPE);
	}
	
	/**
	 * @return has the user login
	 * @since 1.2.2
	 */
	public static boolean hasLogin() {
		if(getLoginType() == null || currentAccountId() == null)
			return false;
		return true;
	}

	/**
	 * @return account of current account
	 * @since 1.0.2
	 */
	public static String currentAccount() {
		@SuppressWarnings("unchecked")
		Callable<String> call = (Callable<String>) CALLABLES.get(ServerCallable.currentAccount);
		if (call != null) {
			return call.call();
		}
		
		String account = (String) getSession().getAttribute(SessionKeys.CURRENT_ACCOUNT);
		if (account == null) {
			if (isTenantLogin()) {
				ITenant tenant = currentTenant();
				if (tenant == null)
					return "";
				account = tenant.getAccount();
			} else {
				AccountModel u = currentUser();
				if (u == null)
					return "";
				account = u.getAccount();
			}
			getSession().setAttribute(SessionKeys.CURRENT_ACCOUNT, account);
		}
		return account;
	}
	
	/**
	 * @return current accountId
	 * @since 1.0.2
	 */
	public static Long currentAccountId() {
		return (Long) SecurityUtils.getSubject().getPrincipal();
	}

	/**
	 * @return current user id
	 * @since 1.0.2
	 */
	public static Long currentUserId() {
		if (isTenantLogin())
			throw new SecurityServerException("No current user, current account is a tenant.");
		return currentAccountId();
	}

	/**
	 * Not distinguish tenant account or user account
	 * @return id of current tenant
	 * @since 1.0.2
	 */
	public static Long currentTenantId() {
		@SuppressWarnings("unchecked")
		Callable<Long> call = (Callable<Long>) CALLABLES.get(ServerCallable.currentTenantId);
		if (call != null) {
			return call.call();
		}
		
		if (isTenantLogin())
			return currentAccountId();

		Long currentUserId = currentAccountId();
		Long tenantId = UID_TID.get(currentUserId);
		if (tenantId == null) {
			AccountModel u = currentUser();
			if (u == null)
				return null;
			tenantId = u.getTenantId();
			UID_TID.put(currentUserId, tenantId);
		}
		return tenantId;
	}

	/**
	 * If current account is a tenant return <code>null</code>
	 * @return current organization id or <code>null</code>
	 * @since 1.0.2
	 */
	public static Long currentOrgId(Class<? extends IUser> userClass, Class<? extends IOrganization> orgClass) {
		Long cOrgId = (Long) getSession().getAttribute(SessionKeys.CURRENT_ORG_ID);
		if (cOrgId == null) {
			if (isTenantLogin())
				return null;
			cOrgId = getSecurityService().getCurrentOrgId(userClass, orgClass);
			getSession().setAttribute(SessionKeys.CURRENT_ORG_ID, cOrgId);
		}
		return cOrgId;
	}
	
	/**
	 * @since 2.0.0
	 * @see com.ineunet.knife.sys.controller.UserController#beforeSubmit
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Long> currPermedOrgIds() {
		List<Long> orgIds = (List<Long>) getSession().getAttribute(SessionKeys.PERM_ORG_IDS);
		if (orgIds == null) {
			if (isTenantLogin())
				return Collections.EMPTY_LIST;
			orgIds = getSecurityService().getCurrPermedOrgIds();
			getSession().setAttribute(SessionKeys.PERM_ORG_IDS, orgIds);
		}
		return orgIds;
	}

	/**
	 * Can lazy load anything from db
	 * @return current user
	 * @since 1.0.2
	 */
	public static AccountModel currentUser() {
		return getSecurityService().getCurrentUser();
	}

	/**
	 * @since 1.0.2
	 */
	public static ITenant currentTenant() {
		return (ITenant) getTenant(currentTenantId());
	}

	/**
	 * @return domain of current account
	 * @since 1.1.0
	 */
	public static String currentDomain() {
		ITenant tenant = currentTenant();
		if (tenant == null)
			return null;
		return tenant.getDomain();
	}

	/**
	 * @since 1.2.0
	 * 
	 * @return jid. e.g. hilbert@ineunet.com
	 */
	public static String currentJid() {
		return currentAccount() + "@" + currentDomain();
	}
	
	/**
	 * @return e.g. tom@ineunet.com case <code>isMultiTenantMode()</code> or 
	 * tom case <code>!isMultiTenantMode()</code>
	 */
	public static String currentFullAccount() {
		@SuppressWarnings("unchecked")
		Callable<String> call = (Callable<String>) CALLABLES.get(ServerCallable.currentFullAccount);
		if (call != null) {
			return call.call();
		}
		
		if (isMultiTenantMode() && !isTenantLogin())
			return currentJid();
		else
			return currentAccount();
	}
	
	/**
	 * @return roleIds of current login user
	 * @since 2.0.0
	 * Created on 2015-3-7
	 */
	public static List<Long> currentRoleIds() {
		return getSecurityService().getRoleIds(currentAccountId());
	}
	
	/**
	 * @return e.g. [1,2]
	 * @since 2.0.0
	 */
	public static String currentRoleKey() {
		List<Long> currentRoleIds = currentRoleIds();
		if (currentRoleIds.isEmpty())
			return "";
		return currentRoleIds.toString();
	}
	
	/**
	 * @param accountId
	 * @return roleKey. e.g. [1,2]
	 * @since 2.0.0
	 */
	public static String getRoleKey(Long accountId) {
		List<Long> roleIds = getSecurityService().getRoleIds(accountId);
		if (roleIds.isEmpty())
			return "";
		return roleIds.toString();
	}

	/**
	 * @param id id of tenant
	 * @since 1.0.2
	 */
	public static ITenant getTenant(Long id) {
		return getTenantCache().getTenantById(id);
	}

	/**
	 * @param id tenantId
	 * @return domain of the tenant
	 */
	public static String getDomain(Long id) {
		return getTenantCache().getTenantById(id).getDomain();
	}

	/**
	 * @param account account of tenant
	 * @since 1.0.2
	 */
	public static ITenant getTenantByAccount(String account) {
		return getTenantCache().getTenantByAccount(account);
	}

	/**
	 * @param account account of tenant
	 * @since 1.2.0
	 */
	public static Long getTenantIdByAccount(String account) {
		return getTenantByAccount(account).getId();
	}

	/**
	 * @return whether current login account is a tenant account while not a user account
	 * @since 1.0.2
	 */
	public static boolean isTenantLogin() {
		if (getLoginType() == LoginType.tenant)
			return true;
		return false;
	}

	/**
	 * @since 1.0.2
	 */
	public static boolean isUserLogin() {
		if (getLoginType() == LoginType.user)
			return true;
		return false;
	}

	/**
	 * @return whether current user is belong to owner
	 * @since 1.0.2
	 */
	public static boolean isOwner() {
		if (isTenantLogin())
			return currentTenant().getRole() == TenantRole.owner;
		else
			return getTenant(currentUser().getTenantId()).getRole() == TenantRole.owner;
	}

	/**
	 * @since 1.0.2
	 */
	public static ISecurityService getSecurityService() {
		if (securityService == null)
			securityService = BundleUtils.getService(ISecurityService.SERVICE_NAME, ISecurityService.class);
		return securityService;
	}

	/**
	 * @since 1.1.1
	 */
	public static ITenantCache getTenantCache() {
		if (tenantCache == null)
			tenantCache = BundleUtils.getService(ITenantCache.SERVICE_NAME, ITenantCache.class);
		return tenantCache;
	}

	/**
	 * Keys for use
	 * 
	 * @since 1.0.2
	 */
	public static class SessionKeys {
		public static final String LOGIN_TYPE = "lgType";
		/**
		 * 0=generic. e.g. login by admin@ineunet.com<br>
		 * 1=customized. e.g. url=/login?t=1<br>
		 * Default is generic.
		 */
		public static final String LOGIN_VIEW_TYPE = "lvType";
		public static final String CURRENT_ACCOUNT = "curAcc";
		public static final String CURRENT_ORG_ID = "curOrgId";
		
		/**
		 * @since 2.0.0
		 */
		public static final String PERM_ORG_IDS = "permOrgIds";
	}

}
