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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * 
 * @author Hilbert
 * 
 * @since 1.0.5
 *
 */
public class BundleContext implements IBundleContext {

	private static final Map<String, Bundle> bundles = new HashMap<String, Bundle>();
	
	/**
	 * serviceName_serviceClassName : service
	 */
	private static final Map<String, Object> nameClassServices = new HashMap<String, Object>();
	
	/**
	 * serviceClass : service
	 */
	private static final Map<Class<?>, Object> classServices = new HashMap<Class<?>, Object>();

	private BundleContext() {}
	
	static BundleContext INSTANCE = new BundleContext();
	
	@Override
	public List<Bundle> getBundles() {
		List<Bundle> list = new ArrayList<Bundle>(bundles.values());
		sortBundles(list);
		return list;
	}

	@Override
	public Bundle getBundle(String bundleName) {
		return bundles.get(bundleName);
	}
	
	@Override
	public int getBundlesSize() {
		return bundles.size();
	}
	
	public void registerBundle(String bundleName, Bundle bundle) {
		bundles.put(bundleName, bundle);
	}
	
	@Override
	public void removeBundle(String bundleName) {
		bundles.remove(bundleName);
	}
	
	/**
	 * Sort bundles by index asc
	 * @param bundles
	 */
	protected static void sortBundles(List<Bundle> bundles)
	{
		Collections.sort(bundles, new Comparator<Bundle>() {
			@Override
			public int compare(Bundle b1, Bundle b2) {
				if(b1.getIndex() < b2.getIndex())
					return -1;
				else if(b1.getIndex() == b2.getIndex())
					return 0;
				else
					return 1;
			}
		});
	}
	
	@Override
	public Collection<Object> getServices() {
		Set<Object> all = new HashSet<Object>();
		all.addAll(classServices.values());
		all.addAll(nameClassServices.values());
		return all;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> serviceClass) {
		return (T) classServices.get(serviceClass);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getServices(Class<T> serviceClass) {
		List<T> list = new ArrayList<T>();
		T service = (T) classServices.get(serviceClass);
		if(service != null)
			list.add(service);
		else {
			String className = serviceClass.getName();
			for(String key : nameClassServices.keySet()) {
				if(key.endsWith(className))
					list.add((T) nameClassServices.get(key));
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(String serviceName, Class<T> serviceClass) {
		if(serviceName == null || serviceName.trim().length() == 0)
			return (T) classServices.get(serviceClass);
		else
			return (T) nameClassServices.get(serviceName + "_" + serviceClass.getName());
	}

	@Override
	public <T> void registerService(String serviceName, Class<T> serviceClass, Object service) {
		if(serviceName == null || serviceName.trim().length() == 0) {
			nameClassServices.put(serviceClass.getName(), service);
			classServices.put(serviceClass, service);
		} else
			nameClassServices.put(serviceName + "_" + serviceClass.getName(), service);
	}

	@Override
	public <T> void unregisterService(String serviceName, Class<T> serviceClass) {
		if(serviceName == null || serviceName.trim().length() == 0) {
			nameClassServices.remove(serviceClass);
			classServices.remove(serviceClass);
		} else
			nameClassServices.remove(serviceName + "_" + serviceClass.getName());
	}


}
