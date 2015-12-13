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
import java.util.Date;
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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Index;
import org.springframework.format.annotation.DateTimeFormat;

import com.ineunet.knife.api.AbstractEntity;
import com.ineunet.knife.api.security.IRole;
import com.ineunet.knife.api.security.ITenant;
import com.ineunet.knife.core.validation.ValidatingType;
import com.ineunet.knife.core.validation.annotation.VProp;

/**
 * 
 * @author Hilbert
 * 
 */
@Entity
@Table(name = "knife_tenant")
public class Tenant extends AbstractEntity<Long> implements ITenant {
	private static final long serialVersionUID = -9071597028490769748L;
	private String account;
	private String password;
	private String name;
	private String domain;
	private String email;
	private String phone;
	private TenantRole role = TenantRole.none;
	private String description;
	private Set<Role> roles = new HashSet<Role>();
	private List<Long> roleIds = new ArrayList<Long>();
	
	private String industryNumber;
	private String cityNumber;
	private String address;
	private Date limitBegin;
	private Date limitEnd;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	@Basic(optional = false)
	@Column(name = "account", length = 100)
	@Index(name="idx_tenant_account")
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	@Basic(optional = false)
	@Column(name = "password", length = 128)
	@VProp(name = "password", length = 100, nullable = false, title = "【密码】", type = ValidatingType.PASSWORD)
	public String getPassword() {
		return password;
	}

	public void setPassword(String passwd) {
		this.password = passwd;
	}

	@Basic(optional = false)
	@Column(name = "name", length = 100)
	@VProp(name = "name", length = 100, nullable = true, title = "【名称】", type = ValidatingType.STRING)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Basic(optional = false)
	@Column(name = "domain", length = 100)
	@Index(name="idx_tenant_domain")
	@VProp(name = "domain", length = 100, nullable = false, title = "【域名】", type = ValidatingType.STRING)
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Basic(optional = false)
	@Column(name = "email", length = 100)
	@VProp(name = "email", length = 100, nullable = false, title = "【邮箱】", type = ValidatingType.EMAIL)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Basic(optional = false)
	@Column(name = "phone", length = 32)
	@VProp(name = "phone", length = 100, nullable = false, title = "【电话】", type = ValidatingType.STRING)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Basic(optional = false)
	@Enumerated(value = EnumType.ORDINAL)
	public TenantRole getRole() {
		return role;
	}

	public void setRole(TenantRole role) {
		this.role = role;
	}

	@Column(name = "description", length = 255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="knife_tenants_roles")
    @Cascade(value = CascadeType.ALL)
    public Set<Role> getRoles() {
        return roles;
    }

    @SuppressWarnings("unchecked")
	@Override
	public void setRoles(Set<? extends IRole> roles) {
    	this.roles = (Set<Role>) roles;
	}
    
    @Transient
	public List<Long> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Long> roleIds) {
		this.roleIds = roleIds;
	}

	@Basic(optional = true)
	@Column(name = "industry_number", length = 100)
	public String getIndustryNumber() {
		return industryNumber;
	}

	public void setIndustryNumber(String industryNumber) {
		this.industryNumber = industryNumber;
	}

	@Basic(optional = true)
	@Column(name = "city_number", length = 100)
	public String getCityNumber() {
		return cityNumber;
	}

	public void setCityNumber(String cityNumber) {
		this.cityNumber = cityNumber;
	}

	@Basic(optional = true)
	@Column(length = 255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "limit_begin")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public Date getLimitBegin() {
		return limitBegin;
	}

	public void setLimitBegin(Date limitBegin) {
		this.limitBegin = limitBegin;
	}

	@Column(name = "limit_end")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public Date getLimitEnd() {
		return limitEnd;
	}

	public void setLimitEnd(Date limitEnd) {
		this.limitEnd = limitEnd;
	}

}
