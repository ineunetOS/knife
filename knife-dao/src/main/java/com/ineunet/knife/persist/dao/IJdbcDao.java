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
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.ineunet.knife.api.IJdbcModel;
import com.ineunet.knife.qlmap.criteria.ICriteria;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.1
 *
 * @param <T>
 */
public interface IJdbcDao<T extends IJdbcModel<?>> {
	
	String getTableName();
	
	RowMapper<T> getRowMapper();
	
	void save(T model);
	
	/**
	 * For increment id save.
	 * @param model
	 * @return generated id
	 */
	Long saveIncre(final T model);
	
	/**
	 * 
	 * @param id table id.
	 * @param params table columns.
	 */
	void update(Object id, Map<String, Object> params);
	
	void update(Object id, String property, Object value);
	
	T findOne(Object id);
	
	/**
	 * If there are many records, return the first one.<br>
	 * If there is no record, return <code>null</code><br>
	 * @param propertyName
	 * @param propertyValue
	 * @return one record query by property
	 */
	T findOne(String propertyName, Object propertyValue);
	
	T findOne(ICriteria criteria);
	
	Long count(ICriteria criteria);
	
	Long count();
	
	List<T> findAll();
	
	List<T> find(ICriteria criteria);
	
	List<T> find(String propertyName, Object propertyValue);
	
	/**
	 * 
	 * @param propertyName
	 * @param propertyValue
	 * @param symbol for example =, &lt;&gt;, &lt;=, &gt;=
	 * @return
	 * 
	 * @since 1.2.0
	 */
	List<T> findBySymbol(String propertyName, Object propertyValue, String symbol);
	
	/**
	 * Fuzzy query
	 * @param propertyName property name
	 * @param propertyValue property value
	 * @return
	 */
	List<T> findFuzzy(String propertyName, String propertyValue);
	
	/**
	 * @param sql
	 * @return
	 * @since 1.2.0
	 */
	List<T> query(String sql);
	
	/**
	 * @param sql
	 * @param args
	 * @return
	 * @since 1.2.0
	 */
	List<T> query(String sql, Object...args);
	
	<X> List<X> query(String sql, RowMapper<X> rowMapper);
	
	<X> List<X> query(String sql, Object[] args, RowMapper<X> rowMapper);
	
	<X> List<X> query(String sql, RowMapper<X> rowMapper, Object...args);
	
	Integer queryForInt(String sql, Object...args);
	
	Long queryForLong(String sql, Object...args);
	
	/**
	 * Find Long list by select column.
	 */
	List<Long> queryForLongs(String sql, Object...args);
	
	String queryForString(String sql, Object...args);
	
	List<String> queryForStrings(String sql, Object...args);
	
	/**
	 * @since 1.2.0
	 */
	<X> X queryForObject(String sql, RowMapper<X> rowMapper, Object... args);
	
	/**
	 * Query unique row from table. If there are many rows, throw <code>UnUniqueRowsException</code><br>
	 * @param sql
	 * @param cols how many columns be selected
	 * @param args 
	 * @return unique row
	 * @exception If there are many rows, throw <code>UnUniqueRowsException</code>
	 */
	Object[] queryForArray(String sql, int cols, Object...args);
	
	void deleteById(Object id);
	
	void deleteByIds(List<Object> ids);
	
	void delete(String propertyName, Object propertyValue);
	
	/**
	 * Fuzzy deletion
	 * @param propertyName
	 * @param propertyValue
	 */
	void deleteFuzzy(String propertyName, String propertyValue);

}
