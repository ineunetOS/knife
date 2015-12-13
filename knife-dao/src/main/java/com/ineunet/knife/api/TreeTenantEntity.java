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
package com.ineunet.knife.api;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.ineunet.knife.core.tree.ITree;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 2.0.0
 *
 */
@MappedSuperclass
public abstract class TreeTenantEntity<T> extends AbstractTenantEntity<T> implements ITree<T> {

	private static final long serialVersionUID = 1879776951601897122L;

	private T parentId;
	private String name;
	private String code;
	private boolean folder = false;
	private List<? extends ITree<T>> children;
	private boolean checked = false;

	@Override
	@Column(name = "parent_id")
	public T getParentId() {
		return parentId;
	}
	
	public void setParentId(T parentId) {
		this.parentId = parentId;
	}
	
	@Override
	@Column(name = "name", length = 32)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	@Column(name = "code", length = 32)
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name = "is_folder")
	public boolean isFolder() {
		return folder;
	}

	public void setFolder(boolean folder) {
		this.folder = folder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X extends ITree<T>> void addChild(X child) {
		if (children == null)
			children = new ArrayList<X>();
		((ArrayList<X>) this.children).add(child);
	}

	@Transient
	@Override
	public List<? extends ITree<T>> getChildren() {
		if (children == null)
			children = new ArrayList<ITree<T>>();
		return children;
	}

	@Override
	public boolean hasChild() {
		if (children == null)
			return false;
		if (children.isEmpty())
			return false;
		return true;
	}

	@Transient
	public boolean getChecked() {
		return checked;
	}

	@Override
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
