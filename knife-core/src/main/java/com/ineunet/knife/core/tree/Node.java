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

/**
 * 
 * @author Hilbert
 * 
 * @since 1.0.0
 *
 */
public class Node {
	private String id;
	private String parentId;
	private String text;
	private List<Node> children;
	private String url;
	private boolean checked = false;
	
	public Node(String id, String name) {
		this(id, name, null);
	}
	
	public Node(String id, String name, String url) {
		this.id = id;
		this.text = name;
		this.url = url;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Node> getChildren() {
		return this.children;
	}

	public Node addChild(Node child) {
		if (child == null)
			throw new IllegalArgumentException("Null node");
		if (children == null) {
			children = new ArrayList<Node>();
		}
		children.add(child);
		return this;
	}
	
	public boolean hasChild() {
		if(children == null) return false;
		if(children.isEmpty()) return false;
		return true;
	}
	
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getState() {
		return this.hasChild() ? "closed" : "opened";
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
}
