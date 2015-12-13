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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ineunet.knife.util.ISortedMap;

/**
 * 
 * Generic service of the service layer
 * 
 * @author Hilbert Wang
 * @since 2010-12-1
 *
 */
public interface IGenericDao<T> {
	
	/**
	 * Remove this instance from the session cache. 
	 * Changes to the instance will not be synchronized with the database. 
	 * @param object
	 * @since 2.0.0
	 */
	void evict(Object object);
	
	/**
	 * @see evict(Object object)
	 * @since 2.0.0
	 */
	<X> void evict(List<X> objects);
	
	/**
	 * Completely clear the session. 
	 * Evict all loaded instances and cancel all pending saves, updates and deletions. 
	 * Do not close open iterators or instances of ScrollableResults.
	 * @since 2.0.0
	 */
	void clear();
	
	/**
	 * @return table name of current entity
	 * @since 2.0.0
	 */
	String tableName();
	
	/**
	 * Get table name by <tt>entityClass</tt>
	 * @param entityClass
	 * @return table name of entity <tt>entityClass</tt>
	 * @since 2.0.0
	 */
	<X> String tableName(Class<X> entityClass);
	
	/**
	 * @return entity name of current entity
	 * @since 2.0.0
	 */
	String entityName();
	
	/*==================================== create methods ========================================*/
	
	<X> X create(X entity);
	
	<X> void create(Collection<X> entities);
	
	/*==================================== update methods ========================================*/
	
	<X> X update(X entity);
	
	<X> void update(Collection<X> entities);
	
	<X> X merge(X entity);
	
	<X> void saveOrUpdate(X entity);
	
	/*==================================== retrieve methods ========================================*/

	/**
	 * 根据id获取对象，如果不存在返回null
	 */
	T get(Object id);
	
	/**
	 * 根据id获取对象，如果不存在返回null
	 */
	<X> X get(Object id, Class<X> entityClass);
	
	<X> List<X> getByIds(Collection<?> ids, Class<X> entityClass);
	
	List<T> findByProperty(String propertyName, Object propertyValue);
	
	List<T> findByProperty(String propertyName, Object...propertyValues);
	
	<X> List<X> findByProperty(Class<X> entityClass, String propertyName, Object propertyValue);
	
	<X> List<X> findByProperty(Class<X> entityClass, String propertyName, Object...propertyValues);
	
	/**
	 * Count size of <tt>entityClass</tt> by <tt>propertyName</tt>
	 * @param entityClass class of entity
	 * @param propertyName name of property to query
	 * @param propertyValue value of property to query
	 * @return size of <code>findByProperty</code>
	 * @since 2.0.0
	 */
	<X> Long countByProperty(Class<X> entityClass, String propertyName, Object propertyValue);
	
	/**
	 * @see #countByProperty(Class, String, Object)
	 * @return true if exists record where <tt>propertyName</tt> equals <tt>propertyValue</tt>
	 * @since 2.0.0
	 */
	<X> boolean existsByProperty(Class<X> entityClass, String propertyName, Object propertyValue);
	
	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 * @since 2.0.0
	 */
	boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue);
	
	/**  @since 2.0.1 */
	boolean isPropertyUnique(Class<?> entityClass, final String propertyName, final Object newValue, final Object oldValue);
	
	T findOneByProp(String propertyName, Object propertyValue);
	
	<X> X findOne(String propertyName, Object propertyValue, Class<X> entityClass);
	
	/**  @since 2.2.5 */
	<X> X findOne(String jql, Object... values);
	
	List<T> find(ISortedMap<String, Object> properties);
	
	<X> List<X> find(String jql, List<Object> values);
	
	<X> List<X> find(String jql, Object... values);
	
	<X> List<X> find(String jql, Map<String, ?> values);
	
	List<T> findAll();
	
	<X> List<X> findAll(Class<X> entityClass);
	
	long count(String jql, Object... values);
	long count(String jql, Map<String, ?> values);
	
	/*==================================== delete methods ========================================*/
	
	<X> void delete(X entity);
	
	void deleteById(Object id);
	
	void deleteById(Object id, Class<?> entityClass);
	
	void deleteByIds(List<Object> ids);
	
	void deleteByProperties(ISortedMap<String, Object> properties);
	
	void deleteByProperty(String propertyName, Object propertyValue);
	
	void deleteByProperty(String propertyName, Object...propertyValues);
	
	void deleteByProperty(Class<?> entityClass, String propertyName, Object propertyValue);
	
	void deleteByProperty(Class<?> entityClass, String propertyName, Object...propertyValues);
	
	/*==================================== batch methods ========================================*/
	int batchExecute(final String hql, final Map<String, ?> values);
	int batchExecute(final String hql, final Object... values);
	
}
