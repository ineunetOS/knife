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

import java.util.List;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 2.0.0
 *
 */
public interface ITree<IDType> {
	IDType getId();
	void setId(IDType id);
	IDType getParentId();
	void setParentId(IDType parentId);
	String getName();
	void setName(String name);
	String getCode();
	void setCode(String code);
	boolean isFolder();
	void setFolder(boolean folder);
	<T extends ITree<IDType>> void addChild(T child);
	List<? extends ITree<IDType>> getChildren();
	boolean hasChild();
	boolean getChecked();
	void setChecked(boolean checked);
}
