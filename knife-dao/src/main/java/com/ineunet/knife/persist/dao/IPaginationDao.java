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

import com.ineunet.knife.core.datatables.DataTableResult;
import com.ineunet.knife.core.query.QueryParameters;
import com.ineunet.knife.core.query.QueryResult;

/**
 * 
 * @author hilbert.wang@hotmail.com
 * @param <T>
 * @since 2.2.7
 */
public interface IPaginationDao<T> extends IGenericDao<T> {

	/**
	 * 用于分页查询
	 * @param queryParameters 查询参数
	 * @return 表总记录数
	 */
	long getTotal(QueryParameters queryParameters);

	List<T> list(QueryParameters queryParameters, int start, int rows, String orderBy);
	
	/**
	 * 用于分页查询
	 * @param queryParameters 查询参数
	 * @return 表总记录数
	 */
	List<T> list(QueryParameters queryParameters);
	
	QueryResult<T> getResult(QueryParameters queryParameters);
	
	DataTableResult<T> getDataTableResult(QueryParameters queryParameters);
	
}
