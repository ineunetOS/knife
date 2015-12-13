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

import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;

import com.ineunet.knife.core.datatables.DataTableParams;
import com.ineunet.knife.util.ClassStrUtils;
import com.ineunet.knife.util.StringUtils;

/**
 * 
 * @author hilbert.wang@hotmail.com
 * @since 1.2.0
 */
public class QueryParamParser {

	public static final String DESC = "DESC";
	public static final String ASC = "ASC";
	
	private int start;
	private int rows;
	private String orderBy;
	
	private String orderByField;
	private String orderByColumn;
	private String sort;
	private boolean parsed;

	public QueryParamParser(QueryParameters queryParameters) {
		int start;
		int rows;
		if (queryParameters instanceof DataTableParams) {
			DataTableParams dataTableParam = (DataTableParams) queryParameters;
			start = dataTableParam.getStart();
			rows = dataTableParam.getLength();
		} else {
			int page = queryParameters.getPage();
			rows = queryParameters.getRows();
			
			if (page == Pageable.NON_PAGEABLE) {
				page = 1;
				rows = Integer.MAX_VALUE;
			}
			start = (page - 1) * rows;
		}
		
		String orderBy = null;
		if (queryParameters.getSort() != null) {
			orderBy = queryParameters.getSort();
			
			if (queryParameters.getOrder() != null && "desc".equals(queryParameters.getOrder().toLowerCase())) {
				orderBy = String.format("%s %s", orderBy, DESC);
			} else {
				orderBy = String.format("%s %s", orderBy, ASC);
			}
		}
		
		this.start = start;
		this.rows = rows;
		this.orderBy = orderBy;
	}
	
	QueryParamParser(int start, int end, String orderBy) {
		this.start = start;
		this.orderBy = orderBy;
	}
	
	/**
	 * @param orderBy e.g. name ASC
	 * @return string0=orderby, string1=sort(ASC or DESC)
	 */
	public static String[] parseOrderBy(String orderBy) {
		if(StringUtils.isBlank(orderBy))
			return new String[2];
		return orderBy.trim().split(" +");
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getRows() {
		return rows;
	}

	/**
	 * @return e.g. commodityName ASC
	 */
	public String getOrderBy() {
		return orderBy;
	}
	
	/**
	 * @return e.g. commodity_name ASC or empty String <code>""</code>
	 */
	public String getSqlOrderBy() {
		String column = this.getOrderByColumn();
		if(StringUtils.isBlank(column))
			return "";
		return column + " " + sort;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public String getOrderByColumn() {
		if(StringUtils.isNotBlank(orderByColumn))
			return orderByColumn;
		String field = this.getOrderByField();
		if(StringUtils.isBlank(field))
			return null;
		return ClassStrUtils.hump2Underline(field);
	}
	
	public String getOrderByField() {
		parse();
		return orderByField;
	}
	
	private void parse() {
		if(!parsed) {
			String[] arr = parseOrderBy(orderBy);
			this.orderByField = arr[0];
			if(arr.length == 2) {
				this.sort = arr[1];
			} else {
				this.sort = ASC;
			}
			parsed = true;
		}
	}

	public String getSort() {
		parse();
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	/**
	 * Create a Comparator by <code>orderBy</code> and <code>sort</code>.
	 */
	@SuppressWarnings("unchecked")
	public <X> Comparator<X> getComparator() {
		parse();
		if(StringUtils.isBlank(orderByField))
			return ComparatorUtils.NATURAL_COMPARATOR;
		Comparator<X> mycmp = ComparableComparator.getInstance();
		if("DESC".equalsIgnoreCase(sort)) {
			mycmp = ComparatorUtils.reversedComparator(mycmp);
		}
		return new BeanComparator(orderByField, mycmp);
	}

}
