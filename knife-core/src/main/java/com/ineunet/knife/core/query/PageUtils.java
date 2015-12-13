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

import java.util.List;

/**
 * 
 * @author Hilbert
 * @since 2.0.2-final
 *
 */
public abstract class PageUtils {
	
	/**
	 * Do pagination to <tt>list</tt>.
	 * @param pagination
	 * @param list
	 * @since 2.0.2
	 */
	public static<X> QueryResult<X> paging(final IPagination pagination, List<X> list) {
		int totalSize = list.size();
		int page = pagination.getPage();
		int rows = pagination.getRows();
		
		if (page == Pageable.NON_PAGEABLE) {
			page = 1;
			rows = Integer.MAX_VALUE;
		}
		
		int start = (page - 1) * rows;
		int end = page * rows;
		List<X> subList; 
		if (totalSize <= end)
			subList = list;
		else
			subList = list.subList(start, end);
		QueryResult<X> result = new QueryResult<X>(totalSize, subList);
		return result;
	}
	
}
