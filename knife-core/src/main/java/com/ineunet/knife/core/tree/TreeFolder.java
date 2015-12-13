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
package com.ineunet.knife.core.tree;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.ineunet.knife.util.StringUtils;

/**
 * 
 * @author Hilbert Wang
 *
 * @param <String> id type
 * 
 * @since 2.0.0
 */
public class TreeFolder implements ITree<String> {

	private String id;
	private String parentId;
	private String name;
	private List<? extends ITree<String>> children;
	private String state;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getParentId() {
		return parentId;
	}

	@Override
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X extends ITree<String>> void addChild(X child) {
		if (children == null)
			children = new ArrayList<X>();
		((ArrayList<X>) this.children).add(child);
	}

	@Override
	public List<? extends ITree<String>> getChildren() {
		if (children == null)
			children = new ArrayList<ITree<String>>();
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
	
	@Override
	public boolean isFolder() {
		return true;
	}
	
	public String getText() {
		return name;
	}

	public void setText(String text) {
		this.name = text;
	}

	public String getState() {
		if (StringUtils.isNotBlank(state))
			return state;
		if(!this.hasChild()) 
			return "open";
		return "closed";
	}

	public void open() {
		this.state = "open";
	}
	
	public void close() {
		this.state = "closed";
	}

	@Override
	public void setFolder(boolean folder) {
		throw new UnsupportedOperationException();
	}
	
	@JsonIgnore
	@Override
	public String getCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCode(String code) {
		throw new UnsupportedOperationException();
	}

	@JsonIgnore
	@Override
	public boolean getChecked() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setChecked(boolean checked) {
		throw new UnsupportedOperationException();
	}

}
