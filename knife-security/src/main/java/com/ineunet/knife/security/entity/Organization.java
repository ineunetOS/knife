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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Index;

import com.ineunet.knife.api.AbstractTenantEntity;
import com.ineunet.knife.api.security.IOrganization;
import com.ineunet.knife.core.validation.ValidatingType;
import com.ineunet.knife.core.validation.annotation.VProp;

/**
 * 
 * @author Hilbert
 * @since 1.0.3
 *
 */
@Entity
@Table(name = "knife_organization")
public class Organization extends AbstractTenantEntity<Long> implements IOrganization {
	private static final long serialVersionUID = 3849077972096312772L;
	private Organization parent;
	private String name;
	private String code;
	private Long associatedId;
	private Integer type = 0;// org type
	private String description;
	private boolean selected;
	
	public Organization() {}

	public Organization(String name, String code, Long associatedId, String description) {
		this.name = name;
		this.code = code;
		this.associatedId = associatedId;
		this.description = description;
	}
	
	public Organization(String name, String code, Integer type, Long associatedId, String description) {
		this(name, code, associatedId, description);
		this.type = type;
	}
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	@JsonIgnore
	@Basic(fetch = FetchType.LAZY)
	@ManyToOne(cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "parent_id")
	public Organization getParent() {
		return parent;
	}

	public void setParent(Organization parent) {
		this.parent = parent;
	}

	@Transient
	@Override
	public Long getParentId() {
		if (parent == null)
			return null;
		return parent.id;
	}

	@Basic(optional = false)
	@Column(name = "name", length = 64)
	@Index(name = "idx_knife_org_name")
	@VProp(name = "name", length = 64, title = "【部门名称】", nullable = false, type = ValidatingType.STRING)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "code", length = 32)
	@VProp(name = "code", length = 32, title = "【部门编号】", nullable = false, type = ValidatingType.STRING)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name = "associated_id")
	public Long getAssociatedId() {
		return associatedId;
	}

	@Override
	public void setAssociatedId(Long associatedId) {
		this.associatedId = associatedId;
	}

	@Basic(optional = false)
	@VProp(name = "type", length = 5, title = "【部门类型】", type = ValidatingType.INTEGER)
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Basic(optional = true)
	@Column(name = "description", length = 255)
	@VProp(name = "description", length = 255, title = "【描述描述】", type = ValidatingType.STRING)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associatedId == null) ? 0 : associatedId.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Organization other = (Organization) obj;
		if (associatedId == null) {
			if (other.associatedId != null)
				return false;
		} else if (!associatedId.equals(other.associatedId))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Organization [name=" + name + ", code=" + code + ", associatedId=" + associatedId + ", type=" + type
				+ ", description=" + description + "]";
	}

}
