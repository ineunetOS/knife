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

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Index;

import com.ineunet.knife.api.AbstractTenantEntity;
import com.ineunet.knife.api.security.IRole;
import com.ineunet.knife.core.validation.ValidatingType;
import com.ineunet.knife.core.validation.annotation.VProp;

/**
 * Model object that represents a security role.
 */
@Entity
@Table(name="knife_roles")
public class Role extends AbstractTenantEntity<Long> implements IRole {
	private static final long serialVersionUID = -2811440017222106871L;
    private String name;
    private String expression;
    private String description;
    private Set<String> permissions;
    private String temp;
    private boolean selected;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
    
    @Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

    @Basic(optional=false)
    @Column(length=100)
    @Index(name="idx_roles_name")
    @VProp(name = "name", length = 100, title = "【角色名】", nullable = false, type = { ValidatingType.STRING, ValidatingType.UNIQUE })
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Transient
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

    @Basic(optional=true)
    @Column(length=255)
    @VProp(name = "description", length = 255, title = "【描述】", type = ValidatingType.STRING)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @JoinTable(name="knife_roles_permissions")
    @Cascade(value = CascadeType.ALL)
    @JsonIgnore
    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    @Transient
	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
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
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Role other = (Role) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		return true;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

}


