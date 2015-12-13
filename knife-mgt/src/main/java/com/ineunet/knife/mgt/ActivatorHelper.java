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
package com.ineunet.knife.mgt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

import com.ineunet.knife.api.dataflow.IDataFlowDefinition;
import com.ineunet.knife.api.dataflow.IDataFlowNode;
import com.ineunet.knife.api.expression.IExpression;
import com.ineunet.knife.core.IScheduler;
import com.ineunet.knife.core.Initializer;
import com.ineunet.knife.core.exception.DataFlowException;
import com.ineunet.knife.mgt.exception.RegisterServiceException;
import com.ineunet.knife.mgt.stereotype.BundleActivator;
import com.ineunet.knife.util.Asserts;
import com.ineunet.knife.util.ClassLocator;
import com.ineunet.knife.util.ReflectionUtils;
import com.ineunet.knife.util.StringUtils;

/**
 * @author Hilbert Wang
 * @since 1.0.0
 */
class ActivatorHelper {
	
	private ApplicationContext applicationContext;
	protected Logger log;
	static final BundleContext bc = BundleContext.INSTANCE;
	static boolean hasDataFlowEngineProxy = false;
	Class<?> dataFlowEngineProxy;
	
	ActivatorHelper(ApplicationContext applicationContext, Logger log) {
		this.applicationContext = applicationContext;
		this.log = log;
		try {
			dataFlowEngineProxy = ClassLocator.loadClass("com.ineunet.knifeflow.service.service.impl.DataFlowEngineProxy");
			hasDataFlowEngineProxy = true;
		} catch (ClassNotFoundException e) {
			// do nothing
		}
	}
	
	static int getBundleSize() {
		int size = 0;
		Package[] pkgs = Package.getPackages();
		for(Package pkg : pkgs) {
			String name = pkg.getName();
			if (name.endsWith(".internal")) {
				if (name.contains(".knife.")) {
					size ++;
				} else if ("com.sun.naming.internal".equals(name)){
					continue;
				} else {
					try {
						Class<?> cls = ClassLocator.loadClass(name + ".Activator");
						BundleActivator c = cls.getAnnotation(BundleActivator.class);
						if (c != null) {
							size ++;
						}
					} catch (ClassNotFoundException e) {
						// other package has the same name '*.internal', ignore
						continue;
					}
				}
			}
		}
		return size;
	}

	/**
	 * 1. Execute initial by <code>Initializer</code>. 2. Execute scheduler by
	 * <code>IScheduler</code>.
	 * @throws ClassNotFoundException if DataFlowService not found
	 * @since 1.2.0
	 */
	void initAfterBundlesStarted() {
		Collection<Object> services = bc.getServices();
		List<String> registeredDataFlowId = new ArrayList<String>();
		for (Object service : services) {
			if (service instanceof Initializer) {
				((Initializer) service).init();
				// if (service.getClass().getName().contains("com.ineunet.knife.security.internal")) continue;
				// log.info("Executing init() method of " + service.toString());
			} else if (service instanceof IScheduler) {
				((IScheduler) service).start();
				// if (service.getClass().getName().contains("com.ineunet.knife.security.internal")) continue;
				// log.info("Executing start() method of " + service.toString());
			} else if (service instanceof IDataFlowDefinition) {
				String flowId = ((IDataFlowDefinition) service).getId();
				// validate functionCode of flow definition
				Asserts.notBlank(flowId);
				registeredDataFlowId.add(flowId);
				
				// process dataFlowDefinition & dataFlowNode that defined in spring-xml
				initDataFlow(flowId, (IDataFlowDefinition) service);
			}
		}
		
		// initialize dataFlow and dataFlowEngine
		Map<String, IDataFlowDefinition> flowDefinitions = applicationContext.getBeansOfType(IDataFlowDefinition.class);
		for (Map.Entry<String, IDataFlowDefinition> entry : flowDefinitions.entrySet()) {
			if (!registeredDataFlowId.contains(entry.getKey())) {
				// has not initialized
				initDataFlow(entry.getKey(), entry.getValue());
				// register to bundleContext
				IDataFlowDefinition flow = entry.getValue();
				bc.registerService(flow.getId(), IDataFlowDefinition.class, flow);
			}
			
			try {
				if (hasDataFlowEngineProxy)
				ReflectionUtils.invokeStaticMethod(dataFlowEngineProxy, "registerEngine", new Class[] {IDataFlowDefinition.class}, new Object[] {entry.getValue()});
			} catch (Exception e) {
				throw new RegisterServiceException("Call registerEngine exception.", e);
			}
		}
	}
	
	/**
	 * process dataFlowDefinition & dataFlowNode that defined in spring-xml
	 */
	private void initDataFlow(String flowId, IDataFlowDefinition dataFlowBean) {
		if (StringUtils.isBlank(dataFlowBean.getId())) {
			dataFlowBean.setId(flowId);
		}
		Map<String, IDataFlowNode> nodes = applicationContext.getBeansOfType(IDataFlowNode.class);
		for (Map.Entry<String, IDataFlowNode> entry : nodes.entrySet()) {
			IDataFlowNode node = entry.getValue();
			node.setId(entry.getKey());
			IDataFlowDefinition flow = node.getFlow();
			if (flow == null) {
				throw new DataFlowException("[" + entry.getKey() + "] 未指定流程");
			} else if (!flowId.equals(flow.getId())) {
				// node of other flow
				continue;
			} else if (flowId.equals(flow.getId())) {
				if (!flow.equals(dataFlowBean)) {
					throw new IllegalStateException("Many instances of DataFlowDefinition have the same flowId: " + flowId);
				}
				IExpression expression = node.getDataExpression();
				Asserts.notNull(expression);
				expression.setFlowId(flowId);
				dataFlowBean.addNode(node);
			}
		}
	}

}
