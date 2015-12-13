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
package com.ineunet.knife.persist.dao;

import java.util.List;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.1.1
 *
 */
public interface ISimpleDao<T> {
	
	<X> X create(X entity);
	
	<X> X update(X entity);
	
	<X> X get(Object id, Class<X> entityClass);
	
	T findUnique(String propertyName, Object propertyValue);
	
	List<T> findByProperty(String propertyName, Object...propertyValues);
	
	List<T> findAll();
	
	void deleteById(Object id);
	
	void deleteById(Object id, Class<?> entityClass);
	
	void deleteByIds(List<Object> ids);
	
	void deleteByProperty(String propertyName, Object...propertyValues);

}
