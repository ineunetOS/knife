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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.ineunet.knife.api.IJdbcModel;
import com.ineunet.knife.persist.dao.IJdbcDao;
import com.ineunet.knife.persist.exception.PersistException;
import com.ineunet.knife.qlmap.criteria.ICriteria;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.2.0
 * 
 */
public abstract class JdbcDaoSupport<T extends IJdbcModel<?>> implements IJdbcDao<T> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private final String findById = "select * from " + this.getTableName() + " where id=?";
	private final String deleteById = "delete from " + this.getTableName() + " where id=?";

	protected abstract JdbcTemplate getJdbcTemplate();

	@Override
	public void save(T model) {
		log.info(model.getInsertSQL());
		getJdbcTemplate().update(model.getInsertSQL(), model.getInsertParams());
	}

	@Override
	public Long saveIncre(final T model) {
		log.info(model.getInsertSQL());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(model.getInsertSQL(), new String[] { "id" });
				for (int i = 0; i < model.getInsertParams().length; i++)
					ps.setObject(i + 1, model.getInsertParams()[i]);
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	@Override
	public void update(Object id, Map<String, Object> params) {
		if (params == null || params.isEmpty())
			throw new PersistException("params can not be null");

		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(getTableName()).append(" set ");
		Set<String> keys = params.keySet();
		List<Object> values = new ArrayList<Object>();
		Iterator<String> keysIter = keys.iterator();

		// first
		String key1 = keysIter.next();
		sql.append(key1).append("=?");
		values.add(params.get(key1));

		// others
		while (keysIter.hasNext()) {
			sql.append(", ");
			String key = keysIter.next();
			sql.append(key).append("=?");
			values.add(params.get(key));
		}

		// where
		sql.append(" where id=?");
		values.add(id);
		log.info(sql.toString());
		getJdbcTemplate().update(sql.toString(), values.toArray());
	}

	@Override
	public void update(Object id, String property, Object value) {
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(getTableName());
		sql.append(" set ").append(property).append("=?");
		sql.append(" where id=?");
		log.info(sql.toString());
		getJdbcTemplate().update(sql.toString(), new Object[] { value, id });
	}

	@Override
	public T findOne(Object id) {
		log.info(findById);
		List<T> list = getJdbcTemplate().query(findById, this.getRowMapper(), id);
		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	@Override
	public T findOne(String propertyName, Object propertyValue) {
		List<T> list = this.find(propertyName, propertyValue);
		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	@Override
	public T findOne(ICriteria criteria) {
		List<T> list = this.find(criteria);
		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}
	
	@Override
	public Long count() {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from ");
		sql.append(this.getTableName());
		log.info(sql.toString());
		return getJdbcTemplate().queryForLong(sql.toString());
	}

	@Override
	public Long count(ICriteria criteria) {
		String sql = criteria.getCountString();
		log.info(sql);
		return getJdbcTemplate().queryForLong(sql, criteria.getValues());
	}

	@Override
	public List<T> findAll() {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(this.getTableName());
		log.info(sql.toString());
		return getJdbcTemplate().query(sql.toString(), this.getRowMapper());
	}

	@Override
	public List<T> find(ICriteria criteria) {
		String sql = criteria.getQueryString();
		log.info(sql);
		return getJdbcTemplate().query(sql.toString(), criteria.getValues(), this.getRowMapper());
	}

	@Override
	public List<T> find(String propertyName, Object propertyValue) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(this.getTableName());
		sql.append(" where ").append(propertyName).append("=?");
		log.info(sql.toString());
		return getJdbcTemplate().query(sql.toString(), this.getRowMapper(), propertyValue);
	}

	@Override
	public List<T> findBySymbol(String propertyName, Object propertyValue, String symbol) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(this.getTableName());
		sql.append(" where ").append(propertyName).append(symbol).append("?");
		log.info(sql.toString());
		return getJdbcTemplate().query(sql.toString(), this.getRowMapper(), propertyValue);
	}

	@Override
	public List<T> findFuzzy(String propertyName, String propertyValue) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ");
		sql.append(this.getTableName());
		sql.append(" where ").append(propertyName).append(" like ");
		sql.append("'%").append(propertyValue).append("%'");
		log.info(sql.toString());
		return getJdbcTemplate().query(sql.toString(), this.getRowMapper());
	}
	
	@Override
	public List<T> query(String sql) {
		return getJdbcTemplate().query(sql, this.getRowMapper());
	}

	@Override
	public List<T> query(String sql, Object... args) {
		return getJdbcTemplate().query(sql, this.getRowMapper(), args);
	}
	
	public <X> List<X> query(String sql, RowMapper<X> rowMapper) {
		return getJdbcTemplate().query(sql, rowMapper);
	}
	
	public <X> List<X> query(String sql, Object[] args, RowMapper<X> rowMapper) {
		return getJdbcTemplate().query(sql, args, rowMapper);
	}
	
	public <X> List<X> query(String sql, RowMapper<X> rowMapper, Object...args) {
		return getJdbcTemplate().query(sql, rowMapper, args);
	}
	
	@Override
	public Integer queryForInt(String sql, Object... args) {
		return getJdbcTemplate().queryForInt(sql, args);
	}
	
	@Override
	public Long queryForLong(String sql, Object...args) {
		return getJdbcTemplate().queryForLong(sql, args);
	}

	@Override
	public List<Long> queryForLongs(String sql, Object...args) {
		return getJdbcTemplate().queryForList(sql, args, Long.class);
	}
	
	@Override
	public String queryForString(String sql, Object...args) {
		return getJdbcTemplate().queryForObject(sql, args, String.class);
	}

	@Override
	public List<String> queryForStrings(String sql, Object...args) {
		return getJdbcTemplate().queryForList(sql, args, String.class);
	}
	
	public <X> X queryForObject(String sql, RowMapper<X> rowMapper, Object... args) {
		return getJdbcTemplate().queryForObject(sql, rowMapper, args);
	}
	
	@Override
	public Object[] queryForArray(String sql, int cols, Object...args) {
		SqlRowSet sqlSet = getJdbcTemplate().queryForRowSet(sql, args);
		Object[] array = null;
		//sqlSet.getRow(): 0
		//sqlSet.isBeforeFirst(): true
		//sqlSet.isFirst(): false
		if(sqlSet.next()) {
			array = new Object[cols];
			for(int i = 0; i < cols; i++ ) {
				array[i] = sqlSet.getObject(i+1);
			}
		}
		if(sqlSet.next()) {
			throw new IncorrectResultSizeDataAccessException(1);
		}
		//sqlSet.isFirst(): true
		//sqlSet.isLast(): true
		//sqlSet.isAfterLast(): false
		return array;
	}

	@Override
	public void deleteById(Object id) {
		getJdbcTemplate().update(deleteById, id);
		log.info(deleteById);
	}

	@Override
	public void deleteByIds(List<Object> ids) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(this.getTableName());
		sql.append(" where id=?");
		for (int i = 1; i < ids.size(); i++)
			sql.append(" or id=?");
		log.info(sql.toString());
		getJdbcTemplate().update(sql.toString(), ids.toArray());
	}

	@Override
	public void delete(String propertyName, Object propertyValue) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(this.getTableName());
		sql.append(" where ").append(propertyName).append("=?");
		log.info(sql.toString());
		getJdbcTemplate().update(sql.toString(), propertyValue);
	}

	@Override
	public void deleteFuzzy(String propertyName, String propertyValue) {
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ");
		sql.append(this.getTableName());
		sql.append(" where ").append(propertyName).append(" like ");
		sql.append("'%?%'");
		log.info(sql.toString());
		getJdbcTemplate().update(sql.toString(), propertyValue);
	}

}
