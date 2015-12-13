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
package com.ineunet.knife.api.dataflow;

import java.util.List;

import com.ineunet.knife.core.dataflow.DataFlowMode;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.2
 * Created on 2015-3-21
 */
public interface IDataFlowDefinition {

	/**
	 * @return id e.g. as a function code
	 */
	String getId();
	void setId(String id);
	
	/**
	 * @return flow name. e.g. as a function name
	 */
	String getName();
	void setName(String name);
	
	List<IDataFlowNode> getNodes();
	void setNodes(List<IDataFlowNode> flowNodes);
	
	/**
	 * Add the node if this flow not contains it.
	 */
	void addNode(IDataFlowNode node);
	IDataFlowNode getNode(String id);
	boolean containsNode(IDataFlowNode node);
	
	DataFlowMode getMode();
	void setMode(DataFlowMode mode);
	
	String getNextRule();
	void setNextRule(String nextRule);

	String getBackRule();
	void setBackRule(String backRule);
	
}
