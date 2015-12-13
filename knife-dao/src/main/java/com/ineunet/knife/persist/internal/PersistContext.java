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
package com.ineunet.knife.persist.internal;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ineunet.knife.persist.Jdbc;
import com.ineunet.knife.persist.NamedParamJdbcOperator;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.persist.dao.IGenericDao;

/**
 * 替代持久层配置文件
 * @author Hilbert
 * @since 2.2.5
 */
@Configuration(value = "persistContext")
@EnableTransactionManagement(proxyTargetClass = true)
public class PersistContext {
	
	@Resource(name = "dataSource")
	private DataSource dataSource;
	
	@Resource(name = "sessionFactory")
	private SessionFactory sessionFactory;
	
	private TransactionAwareDataSourceProxy transactionAwareDataSourceProxy;
	private DataSourceTransactionManager dataSourceTransactionManager;
	private HibernateTransactionManager hibernateTransactionManager;
	private IGenericDao<?> hibernateTemplate;
	private JdbcTemplate jdbcTemplate;
	private Jdbc jdbc;
	private NamedParamJdbcOperator namedParamJdbcOperator;
	
	public DataSource dataSource() {
		return dataSource;
	}
	
	@Bean(name = "knifeTransactionAwareDataSourceProxy")
	public TransactionAwareDataSourceProxy transactionAwareDataSourceProxy() {
		if (transactionAwareDataSourceProxy ==null)
			transactionAwareDataSourceProxy = new TransactionAwareDataSourceProxy(dataSource);
		return transactionAwareDataSourceProxy;
	}
	
	@Bean(name = "knifeTransactionAwareDataSourceProxy")
	public DataSourceTransactionManager dataSourceTransactionManager() {
		if (dataSourceTransactionManager == null)
			dataSourceTransactionManager = new DataSourceTransactionManager(transactionAwareDataSourceProxy());
		return dataSourceTransactionManager;
	}
	
	@Bean(name = "knifeHibernate4TransactionManager")
	public PlatformTransactionManager hibernateTransactionManager() {
		if (hibernateTransactionManager == null)
			hibernateTransactionManager = new HibernateTransactionManager(sessionFactory);
		return hibernateTransactionManager;
	}
	
	@Bean(name = PersistUtils.NAME_HIBERNATE_BEAN)
	public IGenericDao<?> hibernateDaoImpl() {
		if (hibernateTemplate == null)
			hibernateTemplate = new com.ineunet.knife.persist.dao.support.HibernateDaoImpl<>(sessionFactory);
		return hibernateTemplate;
	}
	
	@Bean(name = "jdbcTemplate")
	public JdbcTemplate jdbcTemplate() {
		if (jdbcTemplate == null)
			jdbcTemplate = new JdbcTemplate(transactionAwareDataSourceProxy());
		return jdbcTemplate;
	}
	
	@Bean(name = PersistUtils.NAME_JDBC_BEAN)
	public Jdbc jdbc() {
		if (jdbc == null)
			jdbc = new Jdbc(jdbcTemplate());
		return jdbc;
	}
	
	@Bean(name = PersistUtils.NAME_NAMED_PARAM_JDBC_OPERATOR_BEAN)
	public NamedParamJdbcOperator namedParamJdbcOperator() {
		if (namedParamJdbcOperator == null)
			namedParamJdbcOperator = new NamedParamJdbcOperator(jdbcTemplate());
		return namedParamJdbcOperator;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
