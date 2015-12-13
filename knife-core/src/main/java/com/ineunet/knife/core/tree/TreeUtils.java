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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ineunet.knife.util.StringUtils;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.0
 *
 */
public class TreeUtils {
	
	/**
	 * Add children into parents. Children must have parentId.
	 * @param parents
	 * @param children
	 * @return merged parents
	 */
	public static List<Node> merge(List<Node> parents, List<Node> children) {
		for(Node parent : parents) {
			for(Iterator<Node> iter = children.iterator(); iter.hasNext(); ) {
				Node child = iter.next();
				if(parent.getId().equals(child.getParentId())) {
					parent.addChild(child);
				}
			}
		}
		return parents;
	}
	
	/**
	 * @param nodes ITree
	 * @return tree
	 * @since 2.0.0
	 */
	public static<T extends ITree<X>, X> List<T> buildTree(List<T> nodes) {
		Map<X, T> treeTemp = new HashMap<X, T>();
		for (T node : nodes) {
			treeTemp.put(node.getId(), node);
		}

		// add children
		List<X> removedKeys = new ArrayList<X>();
		for (Entry<X, T> entry : treeTemp.entrySet()) {
			ITree<X> tree = entry.getValue();
			if (tree.getParentId() != null && StringUtils.isNotBlank(tree.getParentId().toString())) {
				ITree<X> parent = treeTemp.get(tree.getParentId());
				if (parent != null) {
					// case not load all records, loaded child but parent is not load.
					parent.addChild(tree);
				}
				removedKeys.add(entry.getKey());
			}
		}

		// remove children which added into its parent
		for (X remove : removedKeys) {
			treeTemp.remove(remove);
		}
		
		// before sort
		List<T> result = new LinkedList<T>(treeTemp.values());
		return result;
	}

}
