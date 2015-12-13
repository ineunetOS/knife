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

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ineunet.knife.api.security.IRole;
import com.ineunet.knife.api.security.ITenant;
import com.ineunet.knife.config.Configs;
import com.ineunet.knife.core.security.AccountModel;
import com.ineunet.knife.mgt.log.MgtLogUtils;
import com.ineunet.knife.security.LoginType;
import com.ineunet.knife.security.Server;
import com.ineunet.knife.security.Server.SessionKeys;
import com.ineunet.knife.security.entity.Tenant;
import com.ineunet.knife.util.StringUtils;
import com.manispace.imam.xmpp.protocol.core.JabberId;

/**
 * 
 * Multi-tenant realm<br>
 * 
 * @author Hilbert
 * 
 */
@Transactional
@Component("defaultRealm")
public class DefaultRealm extends AuthorizingRealm {

	@Resource
	ISecurityService securityService;

	public DefaultRealm() {
		// This name must match the name in the User class's getPrincipals() method
		this.setName("defaultRealm"); 
		this.setCredentialsMatcher(new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME));
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String jidString = token.getUsername();
		JabberId jid = JabberId.parse(jidString);
		String domain = jid.getDomain();
		String node = jid.getNode();

		// allow tenant login
		if (Server.isMultiTenantMode()) {
			// must allowed tenant login, then find from tenant. else assume the
			// account is a user(tenant staff)
			if (StringUtils.isBlank(node) && Server.allowTenantLogin()) {
				// without domain with account string
				// use domain as node when node is blank
				node = domain;
				// tenant account login
				ITenant tenant = Server.getTenantByAccount(node);
				if (tenant != null) {
					try {
						Server.getSession().setAttribute(SessionKeys.LOGIN_TYPE, LoginType.tenant);
						Server.getSession().setAttribute(SessionKeys.CURRENT_ACCOUNT, tenant.getAccount());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return new SimpleAuthenticationInfo(tenant.getId(), tenant.getPassword(), this.getName());
				} else
					return null;
			} else {
				// tenant staff account login.
				AccountModel user = this.securityService.findUser(node, domain);
				if (user != null) {
					Server.getSession().setAttribute(SessionKeys.LOGIN_TYPE, LoginType.user);
					Server.getSession().setAttribute(SessionKeys.CURRENT_ACCOUNT, user.getAccount());
					return new SimpleAuthenticationInfo(user.getId(), user.getPasswd(), this.getName());
				} else {
					return null;
				}
			}
		} else {
			// user login. only for single tenant mode.
			// Note: There is no tenant-login when in single-tenant mode.
			AccountModel user;
			try {
				user = this.securityService.findUser(node);
			} catch (Exception e) {
				MgtLogUtils.error(e.getMessage(), e);
				if (Configs.isDevMode())
					e.printStackTrace();
				return null;
			}
			
			if (user != null) {
				Server.getSession().setAttribute(SessionKeys.LOGIN_TYPE, LoginType.user);
				Server.getSession().setAttribute(SessionKeys.CURRENT_ACCOUNT, user.getAccount());
				return new SimpleAuthenticationInfo(user.getId(), user.getPasswd(), this.getName());
			} else {
				return null;
			}
		}
	}

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Long accountId = (Long) principals.fromRealm(this.getName()).iterator().next();
		if (Server.isTenantLogin()) {
			ITenant tenant = this.securityService.getTenant(accountId, Tenant.class);
			if (tenant != null) {
				SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
				for (IRole role : tenant.getRoles()) {
					info.addRole(role.getName());
					info.addStringPermissions(role.getPermissions());
				}
				return info;
			} else {
				return null;
			}
		} else {
			// too many columns and records.
			// IUser user = this.securityService.getUser(accountId);
			List<Long> ids = this.securityService.getRoleIds(accountId);
			if (ids.isEmpty()) {
				return null;
			} else {
				SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
				for (Long roleId : ids) {
					String roleName = this.securityService.getRoleName(roleId);
					List<String> perms = this.securityService.getPermissions(accountId);
					info.addRole(roleName);
					info.addStringPermissions(perms);
				}
				return info;
			}
		}
	}

}
