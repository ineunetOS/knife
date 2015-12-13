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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.ineunet.knife.api.dataflow.IDataFlowDefinition;
import com.ineunet.knife.util.StringUtils;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.2.0
 * 
 */
public abstract class AbstractActivator implements InitializingBean, DisposableBean {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	static final BundleContext bc = ActivatorHelper.bc;
	private static final int bundleSize = ActivatorHelper.getBundleSize();
	@Resource
	private ApplicationContext applicationContext;

	protected abstract void start(IBundleContext bc);
	protected abstract void stop(IBundleContext bc);

	@Override
	public void afterPropertiesSet() throws Exception {
		Bundle bundle = new Bundle(getBundleName(), bc.getBundlesSize());
		bc.registerBundle(bundle.getBundleName(), bundle);
		try {
			this.start(bc);
			if (bundleSize != 0) {
				// bundle_control=size
				if (bundleSize == bc.getBundlesSize()) {
					new ActivatorHelper(applicationContext, logger).initAfterBundlesStarted();
				} else if (bundleSize < bc.getBundlesSize()) {
					System.err.println("Number of Bundles in knife.properties is " + bundleSize + ". Now started "
							+ bc.getBundlesSize() + " bundles. Please modify the configrature.");
					System.err.println("Activator must named 'Activator' in package '*.internal' in Customer source code @2.0.x.");
					System.exit(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("activator start error: ", e);
			System.err.println(e.getMessage());
			System.exit(1);
		}
		// knife.mgt as a management bundle of all bundle.
		// every bundle which started must register to BundlePool
	}

	@Override
	public void destroy() throws Exception {
		try {
			this.stop(bc);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("activator stop error: ", e);
		}
	}

	protected String getBundleName() {
		// subclasses of this class are all in the package 'internal'
		String className = this.getClass().getName();
		int i = className.indexOf(".internal.");
		className = className.substring(0, i);
		return className;
	}
	
	/**
	 * @param serviceName
	 * @param serviceClass
	 * @since 2.0.0
	 * Created on 2015-3-11
	 */
	protected <T> void registerSpringBean(String serviceName, Class<T> serviceClass) {
		T service = applicationContext.getBean(serviceName, serviceClass);
		
		// set flowId if it is null
		if (service instanceof IDataFlowDefinition) {
			String id = ((IDataFlowDefinition) service).getId();
			if (StringUtils.isBlank(id)) {
				((IDataFlowDefinition) service).setId(serviceName);
			}
		}
		bc.registerService(serviceName, serviceClass, service);
	}
	
	/**
	 * @param serviceName
	 * @param serviceClass
	 * @since 2.0.0
	 * Created on 2015-3-11
	 */
	protected <T> void registerSpringBeanIfExists(String serviceName, Class<T> serviceClass) {
		T service;
		
		try {
			service = applicationContext.getBean(serviceName, serviceClass);
		} catch (Exception e) {
			if (e instanceof NoSuchBeanDefinitionException)
				service = null;
			else {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		
		// set flowId if it is null
		if (service instanceof IDataFlowDefinition) {
			String id = ((IDataFlowDefinition) service).getId();
			if (StringUtils.isBlank(id)) {
				((IDataFlowDefinition) service).setId(serviceName);
			}
		}
		if (service != null)
			bc.registerService(serviceName, serviceClass, service);
	}

}
