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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.jongo.Jongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.ineunet.knife.config.ConfigFactory;
import com.ineunet.knife.persist.dao.IGenericDao;
import com.ineunet.knife.persist.exception.PersistException;
import com.ineunet.knife.persist.internal.PersistContext;
import com.ineunet.knife.persist.mongo.MongoManager;
import com.mongodb.Mongo;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.0.3
 * 
 */
public abstract class PersistUtils {

	private static final Logger log = LoggerFactory.getLogger(PersistUtils.class);
	public static final String NAME_ORACLE = "oracle";
	public static final String NAME_MYSQL = "mysql";
	
	public static final String NAME_JDBC_BEAN = "jdbc";
	public static final String NAME_JDBC_TEMPLATE_BEAN = "jdbcTemplate";
	public static final String NAME_HIBERNATE_BEAN = "hibernate";
	public static final String NAME_NAMED_PARAM_JDBC_OPERATOR_BEAN = "namedParamJdbcOperator";
	
	private static final String DATA_SOURCE_ID = ConfigFactory.getKnifeConfig().get("data_source_id", "dataSource");
	private static final String SCHEMA = ConfigFactory.getKnifeConfig().get("schema", "knife");
	
	private static Jongo jongo;
	private static PersistContext persistContext;
	
	public static String getDataSourceId() {
		return DATA_SOURCE_ID;
	}
	
	public static String getSchema() {
		return SCHEMA;
	}

	public static JdbcTemplate getJdbcTemplate() {
		return persistContext.jdbcTemplate();
	}
	
	public static Jdbc getJdbc() {
		return persistContext.jdbc();
	}
	
//	public static NamedParamJdbcOperator getNamedParamJdbc() {
//		return namedParamJdbcOperator;
//	}
	
	public static IGenericDao<?> getHibernateTemplate() {
		return persistContext.hibernateDaoImpl();
	}

	public static DataSourceTransactionManager getDataSourceTransactionManager() {
		return persistContext.dataSourceTransactionManager();
	}
	
	public static PlatformTransactionManager getHibernateTransactionManager() {
		return persistContext.hibernateTransactionManager();
	}
	
	public static DataSource getDataSource() {
		return persistContext.dataSource();
	}
	
	public static SessionFactory getSessionFactory() {
		return persistContext.getSessionFactory();
	}

	/**
	 * With no cache because call it when initializing
	 * 
	 * @param tableName
	 * @return
	 * 
	 * @since 1.0.5
	 */
	public static boolean existsTable(final String tableName) {
		return existsTable(tableName, persistContext.jdbcTemplate());
	}
	
	public static boolean existsTable(final String tableName, JdbcTemplate jdbcTemplate) {
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

	/**
	 * @since 1.2.3
	 */
	public static boolean existsTable(String dbName, String tableName, JdbcTemplate jdbcTemplate) {
		String sql = "select `TABLE_NAME` from `INFORMATION_SCHEMA`.`TABLES` where `TABLE_SCHEMA`=? and `TABLE_NAME`=?";
		List<String> table = jdbcTemplate.queryForList(sql, String.class, dbName, tableName);
		if (table.isEmpty())
			return false;
		return true;
	}

	/**
	 * 
	 * @return jongo
	 * 
	 * @since 1.1.0
	 */
	public static Jongo getJongo() {
		if (jongo == null) {
			try {
				Mongo mongo = MongoManager.getMongoDBServer().getMongo();
				jongo = new Jongo(mongo.getDB(MongoManager.getMongoDBServer().getDefaultDbName()));
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
				throw new PersistException("cannot get jongo", e);
			}
		}
		return jongo;
	}

	static void setPersistContext(PersistContext persistContext) {
		PersistUtils.persistContext = persistContext;
	}

}
