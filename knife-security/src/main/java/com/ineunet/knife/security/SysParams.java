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
package com.ineunet.knife.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.ineunet.knife.api.sys.ISysParamListener;
import com.ineunet.knife.config.ConfigFactory;
import com.ineunet.knife.config.ConfigType;
import com.ineunet.knife.mgt.BundleUtils;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.security.entity.SysParam;
import com.ineunet.knife.util.Asserts;
import com.ineunet.knife.util.StringUtils;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.1
 * 
 */
public class SysParams {
	
	private static final Map<String, SysParam> cache = new HashMap<String, SysParam>();

	public static void add(SysParam param) {
		cache.put(param.getId(), param);
		List<ISysParamListener> listeners = BundleUtils.getBC().getServices(ISysParamListener.class);
		for(ISysParamListener l : listeners)
			l.afterUpdate(param.getId(), param.getValue());
	}

	public static void update(SysParam param) {
		SysParam previous = cache.put(param.getId(), param);
		List<ISysParamListener> listeners = BundleUtils.getBC().getServices(ISysParamListener.class);
		for(ISysParamListener l : listeners)
			l.afterUpdate(param.getId(), previous == null? null : previous.getValue());
	}

	public static void remove(List<Object> keys) {
		for (Object key : keys) {
			SysParam previous = cache.remove(key);
			List<ISysParamListener> listeners = BundleUtils.getBC().getServices(ISysParamListener.class);
			for(ISysParamListener l : listeners)
				l.afterUpdate((String) key, previous == null? null : previous.getValue());
		}
	}

	public static String getValue(String key) {
		Asserts.notBlank(key);
		SysParam param = cache.get(key);
		if (param != null)
			return param.getValue();
		else {
			List<SysParam> params = PersistUtils.getJdbcTemplate().query("select id,value from knife_sys_param where id=?", new RowMapper<SysParam>() {
				@Override
				public SysParam mapRow(ResultSet rs, int rowNum) throws SQLException {
					SysParam sp = new SysParam();
					sp.setId(rs.getString("id"));
					sp.setValue(rs.getString("value"));
					return sp;
				}
			}, key);
			if(params.isEmpty()) return null;
			param = params.get(0);
			cache.put(key, param);
			return param.getValue();
		}
	}

	@SuppressWarnings("unchecked")
	public static <X> X getValue(String key, Class<X> objectClass) {
		String value = getValue(key);
		if (String.class.equals(objectClass))
			return (X) value;
		if (StringUtils.isBlank(value))
			return null;
		if (Long.class.equals(objectClass))
			return (X) Long.valueOf(value);
		if (Integer.class.equals(objectClass))
			return (X) Integer.valueOf(value);
		if (Boolean.class.equals(objectClass))
			return (X) Boolean.valueOf(value);
		throw new IllegalArgumentException("Unsupported object class: " + objectClass);
	}

	public static String getValue(String key, ConfigType type) {
		String value = getValue(key);
		if (value != null)
			return value;
		if (type != null)
			return ConfigFactory.getConfig(type).getString(key);
		return null;
	}

	public static String getValue(String key, ConfigType type, String defaultValue) {
		String value = getValue(key);
		if (value != null)
			return value;
		if (type != null)
			return ConfigFactory.getConfig(type).getString(key);
		return defaultValue;
	}

	public static String getValue(String key, String defaultValue) {
		String value = getValue(key);
		if (value != null)
			return value;
		return defaultValue;
	}

	// Integer
	public static Integer getValue(String key, ConfigType type, int defaultValue) {
		Integer value = getValue(key, Integer.class);
		if (value != null)
			return value;
		if (type != null)
			return ConfigFactory.getConfig(type).get(key, defaultValue);
		return defaultValue;
	}

	public static Integer getValue(String key, int defaultValue) {
		Integer value = getValue(key, Integer.class);
		if (value != null)
			return value;
		return defaultValue;
	}
	
	// Boolean
	public static Boolean getValue(String key, ConfigType type, boolean defaultValue) {
		Boolean value = getValue(key, Boolean.class);
		if (value != null)
			return value;
		if (type != null)
			return ConfigFactory.getConfig(type).get(key, defaultValue);
		return defaultValue;
	}

	public static Boolean getValue(String key, boolean defaultValue) {
		Boolean value = getValue(key, Boolean.class);
		if (value != null)
			return value;
		return defaultValue;
	}

}
