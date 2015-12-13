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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ineunet.knife.api.RecordStatus;
import com.ineunet.knife.api.security.IOrganization;
import com.ineunet.knife.api.security.ITenant;
import com.ineunet.knife.api.security.IUser;
import com.ineunet.knife.core.exception.KnifeDataException;
import com.ineunet.knife.core.security.AccountModel;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.security.Server;
import com.ineunet.knife.security.cache.ITenantCache;
import com.ineunet.knife.security.utils.EncryptUtils;
import com.ineunet.knife.util.StringUtils;

/**
 * Use with caution
 * 
 * @author Hilbert Wang
 * 
 */
@Service(ISecurityService.SERVICE_NAME)
@Transactional
public class SecurityServiceImpl implements ISecurityService {

	private static final String QUERY_ORG_IDS_BY_USER_ID = "select organizations_id from knife_users_orgs where knife_users_id=?";
	private static final String QUERY_ROLE_IDS_BY_USER_ID = "select roles_id from knife_users_roles where knife_users_id=?";
	
	private static final String QUERY_ROLE_NAME_BY_ID = "select name from knife_roles where id=?";
	
	// private ITenantCache tenantCache;
	
	@Override
	public Long getCurrentAccountId() {
		return (Long) SecurityUtils.getSubject().getPrincipal();
	}

	@Override
	public AccountModel getCurrentUser() {
		final Long currentUserId = getCurrentAccountId();
		if (currentUserId != null) {
			String sql = "select id,account,password,tenant_id from knife_users where id=? and record_status=?";
			AccountModel account = PersistUtils.getJdbc().queryForObject(sql, ROW_MAPPER_ACCOUNT_MODEL, currentUserId, RecordStatus.normal.getValue());
			return account;
		} else {
			return null;
		}
	}

	@Override
	public AccountModel findUser(String account, Long tenantId) {
		String sql = "select id,account,password,tenant_id from knife_users where account=? and tenant_id=? and record_status=?";
		List<AccountModel> user = PersistUtils.getJdbc().query(sql, ROW_MAPPER_ACCOUNT_MODEL, account, tenantId, RecordStatus.normal.getValue());
		if (user.isEmpty())
			return null;
		else
			return user.get(0);
	}

	@Override
	public AccountModel findUser(String node, String domain) {
		Long id = Server.getTenantCache().getIdByDomain(domain);
		if (id == null || id == 0) {
			throw new KnifeDataException("租户 " + domain + "不存在，或者该租户数据有错误。");
		}
		return findUser(node, id);
	}

	@Override
	public AccountModel findUser(String account) {
		String sql = "select id,account,password,tenant_id from knife_users where account=? and record_status=?";
		List<AccountModel> user = PersistUtils.getJdbc().query(sql, ROW_MAPPER_ACCOUNT_MODEL, account, RecordStatus.normal.getValue());
		if (user.isEmpty())
			return null;
		else
			return user.get(0);
	}
	
	public static final RowMapper<AccountModel> ROW_MAPPER_ACCOUNT_MODEL = new RowMapper<AccountModel>() {
		@Override
		public AccountModel mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new AccountModel(rs.getLong("id"), rs.getString("account"), rs.getString("password"), rs.getLong("tenant_id"));
		}
	};

	@Override
	public IUser getUser(Long id, Class<? extends IUser> entityClass) {
		return PersistUtils.getHibernateTemplate().get(id, entityClass);
	}

	@Override
	public String getUserPasswd(Long id) {
		String sql = "select password from knife_users where id=" + id;
		return (String) PersistUtils.getJdbcTemplate().queryForMap(sql).get("password");
	}

	@Override
	public ITenant getTenant(Long id, Class<? extends ITenant> tenantClass) {
		// TODO what is TenantCacheImpl? why not use it?
		return PersistUtils.getHibernateTemplate().get(id, tenantClass);
	}

	@Override
	public String getTenantPasswd(Long id) {
		String sql = "select password from knife_tenant where id=" + id;
		return (String) PersistUtils.getJdbcTemplate().queryForMap(sql).get("password");
	}

	@Override
	public ITenant getTenantByAccount(String tenantAccount, Class<? extends ITenant> tenantClass) {
		return PersistUtils.getHibernateTemplate().findOne("account", tenantAccount, tenantClass);
	}

	@Override
	public IOrganization getCurrentOrg(Class<? extends IUser> entityClass, Class<? extends IOrganization> orgClass) {
		IUser cu = PersistUtils.getHibernateTemplate().get(Server.currentUserId(), entityClass);
		Long orgId = cu.getOrganizationId();
		if (orgId == null)
			return null;
		IOrganization corg = PersistUtils.getHibernateTemplate().get(orgId, orgClass);
		return corg;
	}

	@Override
	public Long getCurrentOrgId(Class<? extends IUser> userClass, Class<? extends IOrganization> orgClass) {
		IOrganization org = getCurrentOrg(userClass, orgClass);
		if (org == null)
			return null;
		return org.getId();
	}

	@Override
	public List<Long> getCurrPermedOrgIds() {
		// assume that current accountId is a userId
		Long userId = Server.currentAccountId();
		List<Long> orgIds = PersistUtils.getJdbc().queryForList(QUERY_ORG_IDS_BY_USER_ID, Long.class, userId);
		return orgIds;
	}

	/**
	 * Cached <tt>accountId</tt> mapped roleIds.<br>
	 * Remove cache when update the user of <tt>accountId</tt>
	 * @return roleIds
	 * Created on 2015-3-5
	 */
	@Override
	public List<Long> getRoleIds(Long accountId) {
		List<Long> roleIds = null; // TODO UserRoleCache.getRoleIds(accountId);
		if (roleIds == null) {
			roleIds = PersistUtils.getJdbc().queryForList(QUERY_ROLE_IDS_BY_USER_ID, Long.class, accountId);
			// UserRoleCache.putRoleIds(accountId, roleIds);
		}
		return roleIds;
	}

	@Override
	public String getRoleName(Long roleId) {
		String roleName = null; // TODO UserRoleCache.getRoleName(roleId);
		if (StringUtils.isBlank(roleName)) {
			roleName = PersistUtils.getJdbc().queryString(QUERY_ROLE_NAME_BY_ID, roleId);
			// UserRoleCache.putRoleName(roleId, roleName);
		}
		return roleName;
	}

	@Override
	public List<String> getPermissions(Long accountId) {
		List<Long> roleIds = this.getRoleIds(accountId);
		StringBuilder sql = new StringBuilder("select permissions from knife_roles_permissions where Role_id=");
		Iterator<Long> iter = roleIds.iterator();
		sql.append(iter.next());
		while (iter.hasNext()) {
			Long roleId = iter.next();
			sql.append(" or Role_id=").append(roleId);
		}
		List<String> perms = PersistUtils.getJdbc().queryForList(sql.toString(), String.class);
		return perms;
	}
	
	@Override
	public boolean modifyPasswd(String old, String _new, Class<? extends IUser> userClass, Class<? extends ITenant> tenantClass) {
		old = EncryptUtils.encryptPasswd(old);
		String passwd = "";
		Long currAccountId = Server.currentAccountId();
		if (Server.isTenantLogin()) {
			passwd = Server.getSecurityService().getTenantPasswd(currAccountId);
			if (passwd.equals(old)) {
				ITenant tenant = PersistUtils.getHibernateTemplate().get(Server.currentAccountId(), tenantClass);
				_new = EncryptUtils.encryptPasswd(_new);
				tenant.setPassword(_new);
				// tenantCache.remove(tenant.getId());
				PersistUtils.getHibernateTemplate().update(tenant);
				return true;
			} else {
				return false;
			}
		} else if (Server.isUserLogin()) {
			passwd = Server.getSecurityService().getUserPasswd(currAccountId);
			if (passwd.equals(old)) {
				IUser user = PersistUtils.getHibernateTemplate().get(Server.currentAccountId(), userClass);
				_new = EncryptUtils.encryptPasswd(_new);
				user.setPassword(_new);
				PersistUtils.getHibernateTemplate().update(user);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public Long createUser(IUser user, boolean encrypt) {
		if (encrypt)
			user.setPassword(EncryptUtils.encryptPasswd(user.getPassword()));
		String account = Server.currentFullAccount();
		Date date = new Date();
		user.setCreatePerson(account);
		user.setCreateTime(date);
		PersistUtils.getHibernateTemplate().create(user);
		return user.getId();
	}

	@Override
	public void updateUser(IUser user, boolean encrypt) {
		// Long accountId = user.getId();
		// TODO UserRoleCache.removeRoleIds(accountId);
		// clear menu cache of user when roles of the user changed
		// TODO MenuCache.clearUserCache(accountId);
		if (encrypt)
			user.setPassword(EncryptUtils.encryptPasswd(user.getPassword()));
		String account = Server.currentFullAccount();
		user.setUpdatePerson(account);
		user.setUpdateTime(new Date());
		PersistUtils.getHibernateTemplate().merge(user);
	}

	@Override
	public void deleteUserById(Long userId, Class<? extends IUser> entityClass) {
		// TODO UserRoleCache.removeRoleIds(userId);
		// TODO MenuCache.clearUserCache(userId);
		PersistUtils.getJdbcTemplate().update("delete from knife_users_roles where knife_users_id=?", userId);
		PersistUtils.getHibernateTemplate().deleteById(userId, entityClass);
	}

	@Override
	public void deleteUserByAccount(String account, Object tenantId, Class<? extends IUser> entityClass) {
		String jql = "from User where tenantId=? and account=?";
		List<IUser> users = PersistUtils.getHibernateTemplate().find(jql, tenantId, account);
		if (users.isEmpty())
			return;
		IUser user = users.get(0);
		Long userId = user.getId();
		deleteUserById(userId, entityClass);
	}

	@Override
	public void updateTenant(ITenant tenant) {
		// clear menu cache of tenant when roles changed
		// TODO MenuCache.clearTenantCache(tenant.getId());
		// tenantCache.remove(tenant.getId());
		PersistUtils.getHibernateTemplate().merge(tenant);
	}

	@Override
	public void deleteTenantById(Object tenantId, Class<? extends ITenant> tenantClass) {
		// TODO MenuCache.clearTenantCache((Long) tenantId);
		// tenantCache.remove((Long) tenantId);
		PersistUtils.getHibernateTemplate().deleteById(tenantId, tenantClass);
	}

}
