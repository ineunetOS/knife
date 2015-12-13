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

import java.util.Map;

import com.ineunet.knife.api.expression.IExpression;
import com.ineunet.knife.core.dataflow.NodeStatus;

/**
 * Must override equals() & hashCode() of default
 * 
 * @author Hilbert Wang
 * @since 2.0.2
 * Created on 2015-3-21
 */
public interface IDataFlowNode {

	String getId();
	void setId(String id);
	IDataFlowDefinition getFlow();
	
	NodeStatus getNodeStatus();
	void setNodeStatus(NodeStatus status);
	
	IExpression getDataExpression();
	void setDataExpression(IExpression expression);
	Object execute(Map<String, Object> args);
	
	String getExpression();
	void setExpression(String expression);
	
	boolean isReadOnly();
	void setReadOnly(boolean readOnly);
	
}
