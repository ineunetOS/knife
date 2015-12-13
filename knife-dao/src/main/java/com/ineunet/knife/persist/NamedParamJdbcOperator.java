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
package com.ineunet.knife.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ineunet.knife.config.ConfigFactory;
import com.ineunet.knife.config.ConfigKeysLog;
import com.ineunet.knife.config.Configs;
import com.ineunet.knife.util.Asserts;
import com.ineunet.knife.util.DateUtils;
import com.ineunet.knife.util.ExpressionStrUtils;
import com.ineunet.knife.util.sql.SqlStrUtils;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.2
 * Created on 2015-4-9
 */
public class NamedParamJdbcOperator {
	
	private static final Logger log = LoggerFactory.getLogger(NamedParamJdbcOperator.class);
	private JdbcTemplate jdbcTemplate;
	
	public NamedParamJdbcOperator() {
	}
	
	public NamedParamJdbcOperator(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * @param sql sql paramName sql. e.g. update user set name=:name where id=:id
	 * @param args 
	 * @param values valid args of sql
	 * @return sql with '?'
	 * @since 2.0.2
	 */
	@SuppressWarnings("unchecked")
	static String processParameter(String sql, Map<String, Object> args, List<Object> values) {
		String sqlOther = sql;
		List<String> params = SqlStrUtils.getNamedParameters(sqlOther);
		for (String paramName : params) {
			String escaped = ExpressionStrUtils.escapeRegex$(paramName);
			Object value = args.get(paramName);
			Asserts.notNull(value, "Lack of parameter " + paramName);
			// transform list to array
			if (value instanceof List)
				value = ((List<Object>) value).toArray();
			if (value instanceof Object[]) {
				Object[] inValues = (Object[]) value;
				int valLength = inValues.length;
				// parameter is an array, check keyword 'in'
				if (sqlOther.matches(".+ +in *\\( *: *" + escaped + " *\\).*")) {
					if (valLength == 0) {
						// parameter is empty, remove 'in(:..)'
						sqlOther = sqlOther.replaceFirst("in *\\( *: *" + escaped + " *\\)", " is null and 1<1");
					} else {
						StringBuilder sb = new StringBuilder("?");
						values.add(inValues[0]);
						for (int i = 1; i < valLength; i++) {
							sb.append(",?");
							values.add(inValues[i]);
						}
						sqlOther = sqlOther.replaceFirst(": *" + escaped, sb.toString());
					}
				}
			} else {
				sqlOther = sqlOther.replaceFirst(": *" + escaped, "?");
				values.add(value);
			}
		}
		if (Configs.isDevMode() && ConfigFactory.getKnifeConfig().get("sqllog", false)) {
			log.info("#SQL#: " + sqlOther);
		}
		return sqlOther;
	}
	
	/**
	 * @param sql paramName sql. e.g. update user set name=:name where id=:id
	 * @return the number of rows affected
	 * @since 2.0.2
	 */
	public int update(String sql, Map<String, Object> args) {
		if (args == null) {
			return jdbcTemplate.update(sql);
		}
		List<Object> values = new ArrayList<Object>();
		String sqlOther = processParameter(sql, args, values);
		return jdbcTemplate.update(sqlOther, values.toArray());
	}
	
	/**
	 * @see JdbcTemplate#batchUpdate(String[])
	 * @param sql
	 * @param batchArgs
	 * @since 2.0.2
	 */
	public int[] batchUpdate(final String sql[], final Map<String, Object> batchArgs) {
		Asserts.notEmpty(sql, "SQL array must not be empty");
		String[] processedSql = new String[sql.length];
		for (int i = 0; i < sql.length; i++) {
			String sqlOther = sql[i].trim();
			List<String> params = SqlStrUtils.getNamedParameters(sqlOther);
			for (String paramName : params) {
				String escaped = ExpressionStrUtils.escapeRegex$(paramName);
				Object value = batchArgs.get(paramName);
				if (value == null) {
					if (sqlOther.startsWith("select "))
						sqlOther = sqlOther.replaceFirst("= *: *" + escaped, " is null");
					else {
						// when insert, update, delete
						sqlOther = sqlOther.replaceFirst(": *" + escaped, "null");
					}
						
				} else if (value instanceof String) {
					// 处理单引号
					String valueStr = (String) value;
					if (valueStr.startsWith("'")) {
						if (valueStr.endsWith("'"))
							sqlOther = sqlOther.replaceFirst(": *" + escaped, valueStr);
						else
							sqlOther = sqlOther.replaceFirst(": *" + escaped, "\\'" + valueStr);
					} else
						sqlOther = sqlOther.replaceFirst(": *" + escaped, "'" + valueStr + "'");
				} else if (value instanceof Number) {
					sqlOther = sqlOther.replaceFirst(": *" + escaped, String.valueOf((Number) value));
				} else if (value instanceof Boolean) {
					sqlOther = sqlOther.replaceFirst(": *" + escaped, String.valueOf(((Boolean) value) ? 1 : 0));
				} else if (value instanceof Date) {
					String date;
					try {
						date = DateUtils.toStrDateTime_((Date) value);
					} catch (Exception e) {
						throw new IllegalArgumentException("Wrong format of date: " + value);
					}
					sqlOther = sqlOther.replaceFirst(": *" + escaped, "'" + date + "'");
				} else {
					sqlOther = sqlOther.replaceFirst(": *" + escaped, String.valueOf(value));
				}
			}
			processedSql[i] = sqlOther;
			// print log info
			if (Configs.isDevMode() && ConfigFactory.getKnifeConfig().get(ConfigKeysLog.sqllog, false)) {
				log.info("#SQL#: " + sqlOther);
			}
		}
		return jdbcTemplate.batchUpdate(processedSql);
	}
	
	/**
	 * @param sql paramName sql. e.g. select name from user where id=:id
	 * @since 2.0.2
	 */
	public <X> X queryForObject(String sql, Class<X> requiredType, Map<String, Object> args) {
		if (args == null) {
			return jdbcTemplate.queryForObject(sql, requiredType);
		}
		List<Object> values = new ArrayList<Object>();
		String sqlOther = processParameter(sql, args, values);
		return jdbcTemplate.queryForObject(sqlOther, values.toArray(), requiredType);
	}
	
	public List<Map<String, Object>> queryForList(String sql, Map<String, Object> args) {
		List<Map<String, Object>> results;
		if (args == null) {
			results = jdbcTemplate.queryForList(sql);
		} else {
			List<Object> values = new ArrayList<Object>();
			String sqlOther = processParameter(sql, args, values);
			results = jdbcTemplate.queryForList(sqlOther, values.toArray());
		}
		
		if (Configs.isOracle()) {
			List<Map<String, Object>> resultsLower = new ArrayList<Map<String, Object>>(results.size());
			for (Map<String, Object> map : results) {
				Map<String, Object> resultLower = new HashMap<String, Object>();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					resultLower.put(entry.getKey().toLowerCase(), entry.getValue());
				}
				resultsLower.add(resultLower);
			}
			return resultsLower;
		}
		return results;
	}
	
	/**
	 * @param elementType e.g. Integer.class, Long.class
	 */
	public <X> List<X> queryForList(String sql, Map<String, Object> args, Class<X> elementType) {
		if (args == null) {
			return jdbcTemplate.queryForList(sql, elementType);
		}
		List<Object> values = new ArrayList<Object>();
		String sqlOther = processParameter(sql, args, values);
		return jdbcTemplate.queryForList(sqlOther, elementType, values.toArray());
	}
	
	/**
	 * @see #queryForList(String, Map, Class)
	 */
	public <X> List<X> queryForList(String sql, Map<String, Object> args, RowMapper<X> rowMapper) {
		if (args == null) {
			return jdbcTemplate.query(sql, rowMapper);
		}
		List<Object> values = new ArrayList<Object>();
		String sqlOther = processParameter(sql, args, values);
		return jdbcTemplate.query(sqlOther, values.toArray(), rowMapper);
	}
	
	public Map<String, Object> queryForMap(String sql, Map<String, Object> args) {
		Map<String, Object> result;
		if (args == null) {
			result = jdbcTemplate.queryForMap(sql);
		} else {
			List<Object> values = new ArrayList<Object>();
			String sqlOther = processParameter(sql, args, values);
			int size = values.size();
			
			if (size == 1) {
				result = jdbcTemplate.queryForMap(sqlOther, values.get(0));
			} else if (size == 0) {
				result = jdbcTemplate.queryForMap(sqlOther);
			} else {
				result = jdbcTemplate.queryForMap(sqlOther, values.toArray());
			}
		}
		
		if (Configs.isOracle()) {
			// if oracle, transfer column to lower case
			Map<String, Object> resultLower = new HashMap<String, Object>();
			for (Map.Entry<String, Object> entry : result.entrySet()) {
				resultLower.put(entry.getKey().toLowerCase(), entry.getValue());
			}
			return resultLower;
		}
		return result;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
}
