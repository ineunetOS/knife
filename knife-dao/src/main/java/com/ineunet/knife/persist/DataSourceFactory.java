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

import java.beans.PropertyVetoException;
import java.util.Map;

import com.ineunet.knife.persist.exception.PersistException;
import com.ineunet.knife.util.Asserts;
import com.ineunet.knife.util.StringUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.4
 * 
 */
public class DataSourceFactory {

	public static ComboPooledDataSource createDefaultMySQLC3p0(String jdbcUrl, String user, String password) {
		ComboPooledDataSource ds = new ComboPooledDataSource();
		try {
			ds.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			throw new PersistException(e);
		}
		ds.setJdbcUrl(jdbcUrl);
		ds.setUser(user);
		ds.setPassword(password);
		ds.setMinPoolSize(5);
		ds.setMaxPoolSize(20);
		ds.setMaxStatements(100);
		ds.setAcquireIncrement(2);
		ds.setIdleConnectionTestPeriod(120);
		return ds;
	}

	public static ComboPooledDataSource createC3p0(Map<String, String> params) {
		ComboPooledDataSource ds = new ComboPooledDataSource();
		String driverClass = params.get("driverClass");
		String jdbcUrl = params.get("jdbcUrl");
		Asserts.notBlank(driverClass);
		Asserts.notBlank(jdbcUrl);

		try {
			ds.setDriverClass(driverClass);
		} catch (PropertyVetoException e) {
			throw new PersistException(e);
		}
		ds.setJdbcUrl(jdbcUrl);

		String user = params.get("user");
		String password = params.get("password");
		String minPoolSize = params.get("minPoolSize");
		String maxPoolSize = params.get("maxPoolSize");
		String maxStatements = params.get("maxStatements");
		String acquireIncrement = params.get("acquireIncrement");
		String idleConnectionTestPeriod = params.get("idleConnectionTestPeriod");

		if (StringUtils.isNotBlank(user))
			ds.setUser(user);
		if (StringUtils.isNotBlank(password))
			ds.setPassword(password);
		if (StringUtils.isNotBlank(minPoolSize))
			ds.setMinPoolSize(Integer.parseInt(minPoolSize));
		if (StringUtils.isNotBlank(maxPoolSize))
			ds.setMaxPoolSize(Integer.parseInt(maxPoolSize));
		if (StringUtils.isNotBlank(maxStatements))
			ds.setMaxStatements(Integer.parseInt(maxStatements));
		if (StringUtils.isNotBlank(acquireIncrement))
			ds.setAcquireIncrement(Integer.parseInt(acquireIncrement));
		if (StringUtils.isNotBlank(idleConnectionTestPeriod))
			ds.setIdleConnectionTestPeriod(Integer.parseInt(idleConnectionTestPeriod));
		
		return ds;
	}

}
