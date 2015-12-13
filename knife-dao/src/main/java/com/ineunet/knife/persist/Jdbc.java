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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ineunet.knife.api.IJdbcModel;
import com.ineunet.knife.config.Configs;
import com.ineunet.knife.core.param.IdCode;
import com.ineunet.knife.core.param.IdCodeName;
import com.ineunet.knife.core.param.IdName;
import com.ineunet.knife.core.param.SingleParam;
import com.ineunet.knife.core.tree.Node;
import com.ineunet.knife.core.tree.TreeFolder;
import com.ineunet.knife.persist.exception.PersistException;
import com.ineunet.knife.util.Asserts;

/**
 * Replace of <code>JdbcOperator</code> and <code>JdbcQuery</code>. Because they
 * can not change data source.<br>
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.3
 *
 */
public class Jdbc {

	private static final Logger log = LoggerFactory.getLogger(Jdbc.class);
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private String schema;
	
	public Jdbc() {
		this.schema = PersistUtils.getSchema();
	}
	
	public Jdbc(DataSource dataSource) {
		Asserts.notNull(dataSource);
		this.setDataSource(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.schema = PersistUtils.getSchema();
	}
	
	public Jdbc(JdbcTemplate jdbcTemplate) {
		Asserts.notNull(jdbcTemplate);
		this.jdbcTemplate = jdbcTemplate;
		this.schema = PersistUtils.getSchema();
	}

	/**
	 * @param jdbcTemplate
	 * @param schema schema name or database name
	 */
	public Jdbc(JdbcTemplate jdbcTemplate, String schema) {
		Asserts.notNull(jdbcTemplate);
		Asserts.notBlank(schema);
		this.jdbcTemplate = jdbcTemplate;
		this.schema = schema;
	}

	public String getSchema() {
		return schema;
	}

	// ************************ @see JdbcOperator ********************* //

	/**
	 * Copy from <tt>com.ineunet.knife.persist.dao.support.JdbcDaoSupport</tt>
	 * 
	 * @param model
	 */
	public <X extends IJdbcModel<?>> void create(X model) {
		jdbcTemplate.update(model.getInsertSQL(), model.getInsertParams());
	}

	public void create(String sql, Object... values) {
		jdbcTemplate.update(sql, values);
	}

	public void update(String sql, Object... values) {
		jdbcTemplate.update(sql, values);
	}
	
	/**
	 * @param sql
	 * @param params
	 *            e.g. batch size is 10. params.size=10. params.Object[].length
	 *            = types.length.
	 * @param types
	 *            sql types. Must match params.Object[]. e.g. [1, "q"] match
	 *            with [int, String]
	 * @return an array of the number of rows affected by each statement
	 */
	public int[] batchUpdate(String sql, final List<Object[]> params, final int[] types) {
		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				for (int j = 0; j < types.length; j++) {
					ps.setObject(j + 1, params.get(i)[j], types[j]);
				}
			}

			@Override
			public int getBatchSize() {
				return params.size();
			}
		});
	}
	
	/**
	 * Copy from <tt>com.ineunet.knife.persist.dao.support.JdbcDaoSupport</tt>
	 * 
	 * @param model
	 */
	public <X extends IJdbcModel<?>> Long createIncrement(final X model) {
		return createIncrement(model.getInsertSQL(), model.getInsertParams());
	}

	/**
	 * Column Name of Primary key must be 'id'.
	 * 
	 * @param sql
	 * @param values
	 * @return id
	 */
	public <X> Long createIncrement(final String sql, final Object... values) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql, new String[] { "id" });
				for (int i = 0; i < values.length; i++)
					ps.setObject(i + 1, values[i]);
				return ps;
			}
		}, keyHolder);
		return keyHolder.getKey().longValue();
	}

	public Long createIncrement(String tableName, Map<String, Object> params) {
		if (params == null || params.isEmpty())
			throw new PersistException("params can not be null");

		StringBuilder sql = new StringBuilder();
		List<Object> values = new ArrayList<Object>();
		StringBuilder sqlValues = new StringBuilder(" values(");
		if (PersistUtils.NAME_MYSQL.equals(Configs.getSysDBType())) {
			sql.append("insert into `").append(tableName).append("`(");
			Set<String> keys = params.keySet();
			Iterator<String> keysIter = keys.iterator();

			// first
			String key1 = keysIter.next();
			sql.append("`").append(key1).append("`");
			sqlValues.append("?");
			values.add(params.get(key1));

			// others
			while (keysIter.hasNext()) {
				sql.append(", ");
				String key = keysIter.next();
				sql.append("`").append(key).append("`");
				sqlValues.append(",?");
				values.add(params.get(key));
			}
		} else {
			// oralce ?
			sql.append("insert into ").append(tableName).append("(");
			Set<String> keys = params.keySet();
			Iterator<String> keysIter = keys.iterator();

			// first
			String key1 = keysIter.next();
			sql.append(key1);
			sqlValues.append("?");
			values.add(params.get(key1));

			// others
			while (keysIter.hasNext()) {
				sql.append(", ");
				String key = keysIter.next();
				sql.append(key);
				sqlValues.append(",?");
				values.add(params.get(key));
			}
		}
		sql.append(")");
		sqlValues.append(")");
		String sqlString = sql.append(sqlValues).toString();
		log.info(sqlString);
		return this.createIncrement(sqlString, values.toArray());
	}

	/**
	 * Copy from <tt>com.ineunet.knife.persist.dao.support.JdbcDaoSupport</tt>
	 */
	public void update(String tableName, Object id, Map<String, Object> params) {
		if (params == null || params.isEmpty())
			throw new PersistException("params can not be null");

		List<Object> values = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		if (PersistUtils.NAME_MYSQL.equals(Configs.getSysDBType())) {
			sql.append("update `").append(tableName).append("` set ");
			Set<String> keys = params.keySet();
			Iterator<String> keysIter = keys.iterator();

			// first
			String key1 = keysIter.next();
			sql.append("`").append(key1).append("`=?");
			values.add(params.get(key1));

			// others
			while (keysIter.hasNext()) {
				sql.append(", ");
				String key = keysIter.next();
				sql.append("`").append(key).append("`=?");
				values.add(params.get(key));
			}
		} else {
			// oralce ?
			sql.append("update ").append(tableName).append(" set ");
			Set<String> keys = params.keySet();
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
		}

		// where
		sql.append(" where id=?");
		values.add(id);
		log.info(sql.toString());
		jdbcTemplate.update(sql.toString(), values.toArray());
	}

	/**
	 * Copy from <tt>com.ineunet.knife.persist.dao.support.JdbcDaoSupport</tt>
	 */
	public void update(String tableName, Object id, String property, Object value) {
		StringBuilder sql = new StringBuilder();
		sql.append("update `").append(tableName);
		sql.append("` set `").append(property).append("`=?");
		sql.append(" where id=?");
		log.info(sql.toString());
		jdbcTemplate.update(sql.toString(), new Object[] { value, id });
	}

	/**
	 * @param tableName
	 * @param ids
	 */
	public <X> void deleteByIds(String tableName, X[] ids) {
		Asserts.notNull(ids, "ids cannot be null.");
		if (ids.length == 0)
			return;

		StringBuilder sql = new StringBuilder("delete from ").append(tableName).append(" where id in(?");
		for (int i = 1; i < ids.length; i++) {
			sql.append(",").append("?");
		}
		sql.append(")");
		jdbcTemplate.update(sql.toString(), ids);
	}

	/**
	 * @param tableName
	 * @param ids
	 */
	public <X> void deleteById(String tableName, X id) {
		Asserts.notNull(id);
		StringBuilder sql = new StringBuilder("delete from ").append(tableName).append(" where id=?");
		jdbcTemplate.update(sql.toString(), id);
	}

	public void delete(String tableName, String property, Object value) {
		StringBuilder sql = new StringBuilder("delete from ").append(tableName).append(" where ");
		sql.append(property).append("=?");
		jdbcTemplate.update(sql.toString(), value);
	}

	// ************************ @see JdbcQuery ********************* //
	private static final RowMapper<IdCode> ID_CODE_MAPPER = new RowMapper<IdCode>() {
		@Override
		public IdCode mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new IdCode(checkId(checkId(rs.getObject("id"))), rs.getString("code"));
		}
	};
	
	private static final RowMapper<IdName> ID_NAME_MAPPER = new RowMapper<IdName>() {
		@Override
		public IdName mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new IdName(checkId(rs.getObject("id")), rs.getString("name"));
		}
	};

	private static final RowMapper<IdCodeName> ID_CODE_NAME_MAPPER = new RowMapper<IdCodeName>() {
		@Override
		public IdCodeName mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new IdCodeName(checkId(rs.getObject("id")), rs.getString("code"), rs.getString("name"));
		}
	};

	private static final RowMapper<Node> NODE_MAPPER = new RowMapper<Node>() {
		@Override
		public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
			Node node = new Node(String.valueOf(checkId(rs.getObject("id"))), rs.getString("name"));
			node.setParentId(rs.getString("parent_id"));
			return node;
		}
	};
	
	private static final RowMapper<SingleParam> singleValueParam_MAPPER = new RowMapper<SingleParam>() {
		@Override
		public SingleParam mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new SingleParam(rs.getObject("param"));
		}
		
	};

	/**
	 * @since 2.0.0
	 */
	private static final RowMapper<TreeFolder> treeFolder_MAPPER = new RowMapper<TreeFolder>() {
		@Override
		public TreeFolder mapRow(ResultSet rs, int rowNum) throws SQLException {
			Object parentId = rs.getObject("parent_id");
			if (parentId != null)
				parentId = String.valueOf(parentId);
			TreeFolder node = new TreeFolder();
			node.setId(String.valueOf(checkId(rs.getObject("id"))));
			node.setParentId((String) parentId);
			node.setName(rs.getString("name"));
			return node;
		}
	};
	
	/**
	 * @param id id of some table
	 * @return resolved id
	 * @since 2.0.0
	 */
	public static final Object checkId(Object id) {
		if (Configs.isOracle()) {
			if (id instanceof BigDecimal) {
				// id from oracle db by sql is default trans to BigDecimal
				id = ((BigDecimal) id).longValue();
			}
		}
		return id;
	}

	/**
	 * table design must match id, name,*.
	 * 
	 * @return RowMapper of <tt>IdName</tt>
	 */
	public static RowMapper<IdCode> getRowMapperIdCode() {
		return ID_CODE_MAPPER;
	}
	
	/**
	 * table design must match id, name,*.
	 * 
	 * @return RowMapper of <tt>IdName</tt>
	 */
	public static RowMapper<IdName> getRowMapperIdName() {
		return ID_NAME_MAPPER;
	}

	/**
	 * table design must match id, code, name,*.
	 * 
	 * @return RowMapper of <tt>IdCodeName</tt>
	 */
	public static RowMapper<IdCodeName> getRowMapperIdCodeName() {
		return ID_CODE_NAME_MAPPER;
	}

	/**
	 * @return RowMapper of <tt>Node</tt>
	 */
	public static RowMapper<Node> getRowMapperNode() {
		return NODE_MAPPER;
	}
	
	/**
	 * @since 2.0.0
	 */
	public static<X> RowMapper<TreeFolder> getRowMapperTreeFolder() {
		return treeFolder_MAPPER;
	}
	
	/**
	 * Alias of column is 'single_value'
	 * @since 2.0.0
	 */
	public static RowMapper<SingleParam> getRowMapSingleValueParam() {
		return singleValueParam_MAPPER;
	}

	public List<IdName> getIdNames(String table) {
		return jdbcTemplate.query("select id,name from ".concat(table), getRowMapperIdName());
	}

	public List<IdCodeName> getIdCodeNames(String table) {
		return jdbcTemplate.query("select id,code,name from ".concat(table), getRowMapperIdCodeName());
	}

	/**
	 * @param table
	 *            table name
	 * @param property
	 *            property name
	 * @param value
	 *            property value
	 */
	public List<IdCodeName> getIdCodeNames(String table, String property, Object value) {
		StringBuilder sql = new StringBuilder("select id,code,name from ");
		sql.append(table).append(" where ").append(property).append("=?");
		return jdbcTemplate.query(sql.toString(), getRowMapperIdCodeName(), value);
	}
	
	/**
	 * Fuzzy query by <tt>value</tt> of <tt>property</tt>
	 * @since 2.0.0
	 */
	public List<IdCodeName> getIdCodeNamesFussy(String table, String property, String value) {
		StringBuilder sql = new StringBuilder("select id,code,name from ");
		sql.append(table).append(" where ").append(property).append(" like ?");
		return jdbcTemplate.query(sql.toString(), getRowMapperIdCodeName(), value);
	}
	
	/**
	 * @param table table name
	 * @return code value
	 * @since 2.0.0
	 */
	public String getCodeById(String tableName, Object id) {
		String sql = "select code from " + tableName + " where id=?";
		return queryForString(sql, id);
	}
	
	/**
	 * @param tableName
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 * @since 2.0.0
	 */
	public String queryStringByProperty(String tableName, String destProp, String propertyName, Object propertyValue) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ").append(destProp).append(" from ");
		sql.append(tableName).append(" where ").append(propertyName).append("=?");
		return queryForString(sql.toString(), propertyValue);
	}

	public <X> List<X> query(String sql, RowMapper<X> rowMapper, Object... args) {
		return jdbcTemplate.query(sql, rowMapper, args);
	}

	public <X> List<X> query(String sql, Object[] args, RowMapper<X> rowMapper) {
		return jdbcTemplate.query(sql, args, rowMapper);
	}

	public String queryForString(String sql, Object... args) {
		return jdbcTemplate.queryForObject(sql, String.class, args);
	}

	/**
	 * @param sql
	 * @param args
	 * @return string or <code>null</code>
	 * @throws PersistException
	 *             if result size > 1
	 */
	public String queryString(String sql, Object... args) {
		return jdbcTemplate.query(sql, args, new ResultSetExtractor<String>() {
			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					String s = rs.getString(1);
					if (rs.next())
						throw new PersistException("Expected 1, actual many.");
					return s;
				}
				return null;
			}
		});
	}

	public Long queryForLong(String sql, Object... args) {
		return jdbcTemplate.queryForObject(sql, Long.class, args);
	}

	public <X> X queryForObject(String sql, RowMapper<X> rowMapper, Object... args) {
		return jdbcTemplate.queryForObject(sql, rowMapper, args);
	}

	public <X> X queryForObject(String sql, Class<X> requiredType, Object... args) {
		return jdbcTemplate.queryForObject(sql, requiredType, args);
	}
	
	/**
	 * Query given SQL to create a prepared statement from SQL and a list of
	 * arguments to bind to the query, expecting a result list.
	 * @param sql
	 * @param elementType the required type of element in the result list (for example, Integer.class)
	 * @param args
	 */
	public <X> List<X> queryForList(String sql, Class<X> elementType, Object... args) {
		return jdbcTemplate.queryForList(sql, elementType, args);
	}

	public List<Map<String, Object>> queryForList(String sql, Object... args) {
		return jdbcTemplate.queryForList(sql, args);
	}
	
	public Map<String, Object> queryForMap(String sql, Object... args) {
		return jdbcTemplate.queryForMap(sql, args);
	}
	
	public <X> X findById(String tableName, RowMapper<X> rowMapper, Object id) {
		String sql = "select * from " + tableName + " where id=?";
		return this.queryForObject(sql, rowMapper, id);
	}

	public <X> List<X> findByIds(String tableName, Object[] ids, RowMapper<X> rowMapper) {
		Asserts.notBlank(tableName, "tableName cannot be null.");
		Asserts.notEmpty(ids, "ids cannot be null");
		StringBuilder sql = new StringBuilder("select * from ").append(tableName);
		sql.append(" where id in(?");
		for (int i = 1; i < ids.length; i++)
			sql.append(",?");
		sql.append(")");
		return query(sql.toString(), ids, rowMapper);
	}

	public boolean existsTable(final String tableName) {
		return existsTable(tableName, jdbcTemplate);
	}

	public boolean existsTable(final String tableName, JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.query("show tables", new ResultSetExtractor<Boolean>() {
			@Override
			public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
				while (rs.next()) {
					String tableNameOther = rs.getString(1);
					if (tableName.equalsIgnoreCase(tableNameOther))
						return true;
				}
				return false;
			}
		});
	}

	public List<String> showTables(String dbName) {
		return this.queryForList("show tables", String.class);
	}

	public void execute(String sql) {
		jdbcTemplate.execute(sql);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		Asserts.notNull(dataSource, "dataSource cannot be null.");
		if (dataSource instanceof TransactionAwareDataSourceProxy) {
			this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
		}
		else {
			this.dataSource = dataSource;
		}
	}

}
