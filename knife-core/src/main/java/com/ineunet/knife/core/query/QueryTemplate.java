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
package com.ineunet.knife.core.query;

import com.ineunet.knife.core.datatables.DataTableParams;
import com.ineunet.knife.core.datatables.DataTableResult;

/**
 * 
 * @author hilbert.wang@hotmail.com
 * @since 1.0.0
 */
public class QueryTemplate {
	
	/**
	 * 最初主要是支持easyui和ligerui使用
	 */
	public static <T> QueryResult<T> query(QueryParameters queryParameters, QueryExecutor<T> executor) {
		QueryParamParser args = new QueryParamParser(queryParameters);
		return new QueryResult<T>(executor.getTotal(), executor.getResult(args.getStart(), args.getRows(), args.getOrderBy()));
	}
	
	/**
	 * For JQuery DataTables
	 * @since 2.2.8
	 */
	public static <T> DataTableResult<T> query(DataTableParams queryParameters, QueryExecutor<T> executor) {
		QueryParamParser args = new QueryParamParser(queryParameters);
		return new DataTableResult<T>(executor.getTotal(), executor.getResult(args.getStart(), args.getRows(), args.getOrderBy()));
	}
	
}
