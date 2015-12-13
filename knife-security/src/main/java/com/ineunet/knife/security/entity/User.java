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
package com.ineunet.knife.security.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Index;

import com.ineunet.knife.api.AbstractOrgEntity;
import com.ineunet.knife.api.IUploadEntity;
import com.ineunet.knife.api.security.IOrganization;
import com.ineunet.knife.api.security.IRole;
import com.ineunet.knife.api.security.IUser;
import com.ineunet.knife.core.fileupload.annotation.FileView;
import com.ineunet.knife.core.validation.ValidatingType;
import com.ineunet.knife.core.validation.annotation.VProp;

@Entity
@Table(name = "knife_users")
public class User extends AbstractOrgEntity<Long> implements IUser, IUploadEntity {

	private static final long serialVersionUID = 9210881090808897379L;
	private String account;// account
	private String password;
	private String name;// name of user. e.g. 毛泽东
	private Integer sex = 0;// default 0
	private String email;
	private String phone;
	private Set<Role> roles = new HashSet<Role>();
	private List<Long> roleIds = new ArrayList<Long>();
	private Set<Organization> organizations = new HashSet<Organization>();
	private List<Long> orgIds = new ArrayList<Long>();
	private AccountStatus accountStatus = AccountStatus.enabled;
	private String description;
	
	@FileView(value = true, field = "userPhoto", column = "user_photo", limitSizeKB = 50)
	private String userPhoto; // 用户照片
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	/**
	 * @return the account associated with this user account;
	 */
	@Basic(optional = false)
	@Column(name = "account", length = 64)
	@Index(name = "idx_users_account")
	@VProp(name = "account", length = 64, nullable = false, title = "【账号】", type = ValidatingType.ACCOUNT)
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Basic(optional = true)
	@Index(name = "idx_users_email")
	@Column(name = "email", length = 64)
	@VProp(name = "email", length = 64, title = "【邮箱】", type = ValidatingType.EMAIL)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Returns the password for this user.
	 * @return this user's password
	 */
	@Basic(optional = false)
	@Column(name = "password", length = 128)
	@VProp(name = "password", length = 128, nullable = false, title = "【密码】", type = ValidatingType.PASSWORD)
	public String getPassword() {
		return password;
	}

	public void setPassword(String passwd) {
		this.password = passwd;
	}

	@VProp(name = "name", length = 100, nullable = true, title = "【姓名】", type = ValidatingType.STRING)
	@Column(name = "name", length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL })
	@JoinTable(name = "knife_users_roles")
	public Set<Role> getRoles() {
		return roles;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setRoles(Set<? extends IRole> roles) {
		this.roles = (Set<Role>) roles;
	}

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.ALL })
	@JoinTable(name = "knife_users_orgs")
	public Set<Organization> getOrganizations() {
		return organizations;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setOrganizations(Set<? extends IOrganization> organizations) {
		this.organizations = (Set<Organization>) organizations;
	}

	@Transient
	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	@Transient
	public List<Long> getOrgIds() {
		return orgIds;
	}

	public void setOrgIds(List<Long> orgIds) {
		this.orgIds = orgIds;
	}

	@Basic(optional = false)
	@Enumerated(value = EnumType.ORDINAL)
	@Column(name = "account_status")
	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	@Override
	public void setAccountStatus(AccountStatus status) {
		this.accountStatus = status;
	}

	@Column(name = "description", length = 255)
	@VProp(name = "description", length = 255, title = "【描述】", type = ValidatingType.STRING)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@VProp(name = "phone", length = 16, title = "【手机号码】", type = ValidatingType.MOBILE)
	@Column(name = "phone", length = 16)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "User [id=" + this.getId() + ", account=" + account + ", email=" + email + ", password=" + password + ", roles=" + roles
				+ "]";
	}

}
