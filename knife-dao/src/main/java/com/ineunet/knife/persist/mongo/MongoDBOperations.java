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
package com.ineunet.knife.persist.mongo;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 * 
 */
public interface MongoDBOperations {

	<T extends Object> T findOne(String query, Class<T> clazz) throws Exception;

	<T extends Object> T findOne(Class<T> clazz, String query, Object... parameters) throws Exception;

	<T extends Object> Iterable<T> find(Class<T> clazz, String query, String sort) throws Exception;

	<T extends Object> Iterable<T> find(Class<T> clazz, String query, String sort, int skip, int max) throws Exception;

	<T extends Object> Iterable<T> find(Class<T> clazz, String query, String sort, Object... parameters) throws Exception;

	<T extends Object> Iterable<T> find(Class<T> clazz, String query, String sort, int skip, int max, Object... parameters)
			throws Exception;

	int save(Object object) throws Exception;

	int update(String query) throws Exception;

	int update(String query, Object[] queryParams, String setQuery, Object[] setParams) throws Exception;

	int update(String query, Object queryParam, String setQuery, Object setParam) throws Exception;

	int updateMulti(String query, Object[] queryParams, String setQuery, Object[] setParams) throws Exception;

	int delete(String query) throws Exception;

	int delete(ObjectId id) throws Exception;

	int delete(String query, Object... parameters) throws Exception;

	void drop() throws Exception;

	long count(String query, Object... parameters) throws Exception;

	MongoCollection getMongoCollection();

}
