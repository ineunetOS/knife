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
package com.ineunet.knife.persist.dao.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.beans.factory.InitializingBean;

import com.ineunet.knife.core.datatables.DataTableParams;
import com.ineunet.knife.core.datatables.DataTableResult;
import com.ineunet.knife.core.query.QueryParamParser;
import com.ineunet.knife.core.query.QueryParameters;
import com.ineunet.knife.core.query.QueryResult;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.persist.dao.IPaginationDao;
import com.ineunet.knife.qlmap.criteria.Criteria;
import com.ineunet.knife.qlmap.criteria.ICriteria;

/**
 * 分页基类
 * @author hilbert.wang@hotmail.com
 * @param <T>
 */
public abstract class PaginationDaoSupport<T> extends HibernateDaoImpl<T> implements IPaginationDao<T>, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.getSessionFactory() == null) {
			this.setSessionFactory(PersistUtils.getSessionFactory());
		}
	}
	
	protected abstract void prepareQuery(ICriteria c, QueryParameters queryParameters);
	
	/**
	 * 当 {@link #prepareQuery(ICriteria, QueryParameters)} 不能满足需求时，可以由子类覆盖此方法
	 * @param queryParameters
	 * @param paramValues
	 * @return jql or named parameters hql
	 */
	protected String prepareQuery(QueryParameters queryParameters, Map<String, Object> paramValues) {
		ICriteria c = new Criteria(entityClass, "o", true);
		this.prepareQuery(c, queryParameters);
		paramValues.putAll(c.getNamedValues());
		return c.getQueryString();
	}
	
	@Override
	public long getTotal(QueryParameters queryParameters) {
		Map<String, Object> paramValues = new HashMap<String, Object>();
		String jql = this.prepareQuery(queryParameters, paramValues);
		jql = "select count(*) " + jql;
		return this.count(jql, paramValues);
	}
	
	@Override
	public List<T> list(QueryParameters queryParameters, int start, int rows, String orderBy) {
		Map<String, Object> paramValues = new HashMap<String, Object>();
		String jql = this.prepareQuery(queryParameters, paramValues);
		if (orderBy != null && orderBy.trim().length() != 0)
			jql += " order by o." + orderBy;
		Query query = this.createQuery(jql, paramValues);
		query.setFirstResult(start);
		query.setMaxResults(rows);
		@SuppressWarnings("unchecked")
		List<T> list = query.list();
		return list;
	}

	@Override
	public List<T> list(QueryParameters queryParameters) {
		QueryParamParser param = new QueryParamParser(queryParameters);
		int start = param.getStart();
		String orderBy = param.getOrderBy();
		int rows = param.getRows();

		Map<String, Object> paramValues = new HashMap<String, Object>();
		String jql = this.prepareQuery(queryParameters, paramValues);
		if (orderBy != null && orderBy.trim().length() != 0)
			jql += " order by o." + orderBy;
		Query query = this.createQuery(jql, paramValues);
		query.setFirstResult(start);
		query.setMaxResults(rows);
		@SuppressWarnings("unchecked")
		List<T> list = query.list();
		return list;
	}
	
	@Override
	public QueryResult<T> getResult(QueryParameters queryParameters) {
		QueryResult<T> result = new QueryResult<>(this.getTotal(queryParameters), this.list(queryParameters));
		return result;
	}
	
	@Override
	public DataTableResult<T> getDataTableResult(QueryParameters queryParameters) {
		DataTableResult<T> result = new DataTableResult<>(this.getTotal(queryParameters), this.list(queryParameters));
		result.setDraw( ((DataTableParams) queryParameters).getDraw() );
		return result;
	}

}
