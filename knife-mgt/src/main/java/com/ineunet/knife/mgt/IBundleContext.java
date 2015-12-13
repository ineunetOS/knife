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

import java.util.Collection;
import java.util.List;


/**
 * 
 * @author Hilbert
 * 
 * @since 1.0.5
 *
 */
public interface IBundleContext {

	/**
	 * Get unique service by serviceClass, if none or many return null;
	 * @param serviceClass
	 * @return unique service or null
	 */
	<T> T getService(Class<T> serviceClass);
	
	/**
	 * @return all services that registered
	 * @since 1.2.0
	 */
	Collection<Object> getServices();
	
	/**
	 * Get services by serviceClass, include its subclass services.<br>
	 * If none return empty List.
	 * @param serviceClass service Class
	 * @return list of services
	 */
	<T> List<T> getServices(Class<T> serviceClass);
	
	<T> T getService(String serviceName, Class<T> serviceClass);
	
	<T> void registerService(String serviceName, Class<T> serviceClass, Object service);
	
	<T> void unregisterService(String serviceName, Class<T> serviceClass);
	
	/**
	 * @return all bundles has internal activator
	 */
	List<Bundle> getBundles();
	
	int getBundlesSize();
	
	Bundle getBundle(String bundleName);
	
	void registerBundle(String bundleName, Bundle bundle);
	
	void removeBundle(String bundleName);
	
}
