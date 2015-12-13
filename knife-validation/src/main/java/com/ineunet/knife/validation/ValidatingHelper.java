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
package com.ineunet.knife.validation;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ineunet.knife.api.IEntity;
import com.ineunet.knife.config.Configs;
import com.ineunet.knife.core.validation.ValidatingType;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.util.ArrayUtils;
import com.ineunet.knife.util.ReflectionUtils;
import com.ineunet.knife.util.StringUtils;
import com.ineunet.knife.util.validation.IValidator;
import com.ineunet.knife.util.validation.WebValidator;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-19
 */
public class ValidatingHelper {
	private static final Logger log = LoggerFactory.getLogger(ValidatingHelper.class);
	private ValidatingHelper() {}

	/**
	 * @param validator
	 * @param command
	 * @param commandClass
	 * @since 2.0.1
	 */
	public static <X> IValidator validate(IValidator validator, X command, Class<X> commandClass, boolean isCreate) {
		// validate extension fields e.g. strExt7
		IValidatingConfig props = ValidatingConfigManager.get(commandClass);
		if (props != null) {
			validateByConfigs(props, validator, command, commandClass, isCreate);
		}
		return validator;
	}

	/**
	 * @param groupSize e.g. from strExt1 to strExt8, then groupSize is 8
	 * @param fieldGroup e.g. from strExt1 to strExt8, then groupField is strExt
	 * @param propConfigs
	 * @param validator
	 * @param command entity or model instance, e.g. new User()
	 * @since 2.0.1
	 */
	private static <X> void validateByConfigs(IValidatingConfig propConfigs, IValidator validator, X command, Class<X> commandClass, boolean isCreate) {
		List<String> props = propConfigs.getPropertyNames();
		for (String fieldName : props) {
			Object fieldValue = ReflectionUtils.invokeGetterMethod(command, fieldName);
			String propTitle = propConfigs.getTitle(fieldName);

			// 1. validate null
			boolean nullable = propConfigs.getNullable(fieldName);
			if (!nullable) {
				validator.notNull(propTitle, fieldValue);
			} else if (fieldValue == null) {
				// can be null. and not check null
				continue;
			}
			
			ValidatingType[] types = propConfigs.getType(fieldName);
			// 2. validate length
			int length = propConfigs.getLength(fieldName);
			if (ArrayUtils.contains(types, ValidatingType.STRING)) {
				try {
					if (!nullable) {
						validator.notBlank(propTitle, (String) fieldValue);
					}
					validator.notBlankSpace(propTitle, (String) fieldValue);
					validator.length(propTitle, (String) fieldValue, 0, length);
				} catch (Exception e) {
					log.info(propTitle + " Class=" + fieldValue.getClass() + " : " + Arrays.asList(types));
				}
			} else if (ArrayUtils.contains(types, ValidatingType.INTEGER)) {
				validator.length(propTitle, (Integer) fieldValue, 0, length);
			} else if (ArrayUtils.contains(types, ValidatingType.LONG)) {
				validator.length(propTitle, (Long) fieldValue, 0, length);
			} else if (ArrayUtils.contains(types, ValidatingType.BIG_DEMICAL)) {
				validator.length(propTitle, (BigDecimal) fieldValue, 0, length);
			}
			
			// 3. validate fmt
			if (ArrayUtils.contains(types, ValidatingType.MOBILE)) {
				validator.length(propTitle, (String) fieldValue, 0, length);
				validator.matchMobile(propTitle, (String) fieldValue);
			} else if (ArrayUtils.contains(types, ValidatingType.TELEPHONE)) {
				validator.length(propTitle, (String) fieldValue, 0, length);
				validator.matchTelephone(propTitle, (String) fieldValue);
			} else if (ArrayUtils.contains(types, ValidatingType.FAX)) {
				validator.length(propTitle, (String) fieldValue, 0, length);
				validator.matchFax(propTitle, (String) fieldValue);
			} else if (ArrayUtils.contains(types, ValidatingType.EMAIL)) {
				validator.length(propTitle, (String) fieldValue, 0, length);
				validator.matchEmail(propTitle, (String) fieldValue);
			}
			
			// 4. validate unique
			if (ArrayUtils.contains(types, ValidatingType.UNIQUE) && !ArrayUtils.contains(types, ValidatingType.ACCOUNT)) {
				validatingUnique(isCreate, command, validator, commandClass, fieldName, fieldValue, propTitle);
			}
			
			// 5. validate account
			if (ArrayUtils.contains(types, ValidatingType.ACCOUNT)) {
				if (length == 255) {
					validator.length(propTitle, (String) fieldValue, 0, 32);
				} else {
					validator.length(propTitle, (String) fieldValue, 0, length);
				}
				validator.matchAccount(propTitle, (String) fieldValue);
				validatingUnique(isCreate, command, validator, commandClass, fieldName, fieldValue, propTitle);
			}
			
			// 6. validate password
			if (ArrayUtils.contains(types, ValidatingType.PASSWORD)) {
				boolean isNotBlank = true;
				if (isCreate) {
					if (!nullable && ((String) fieldValue).length() == 0) {
						validator.notBlank(propTitle, (String) fieldValue);
						isNotBlank = false;
					}
				}
				if (isNotBlank && (isCreate || StringUtils.isNotBlank((String) fieldValue)) ) {
					if (length == 255) {
						validator.length(propTitle, (String) fieldValue, 0, 32);
					} else {
						validator.length(propTitle, (String) fieldValue, 0, length);
					}
					validator.matchPassword(propTitle, (String) fieldValue);
				}
			}
			
			// 7. validate user-defined type
			if (ArrayUtils.contains(types, ValidatingType.USER_TYPE)) {
				IValidatingConfig userDefinedConfig = ValidatingConfigManager.get(fieldValue.getClass());
				if (userDefinedConfig != null) {
					// Process Embedded by recursive.
					validateByConfigs(userDefinedConfig, validator, command, commandClass, isCreate);
				}
			}
		}
	}
	
	private static<X> void validatingUnique(boolean isCreate, X command, IValidator validator, Class<X> entityClass, String propName, Object propValue, String propTitle) {
		if (propValue == null) return;
		boolean unique = true;
		
		if (isCreate) {
			unique = !PersistUtils.getHibernateTemplate().existsByProperty(entityClass, propName, propValue);
		} else {
			StringBuilder sql = new StringBuilder("select ");
			sql.append(propName).append(" from ").append(PersistUtils.getHibernateTemplate().tableName(entityClass));
			sql.append(" where id=?");
			Object id = ((IEntity<?>) command).getId();
			List<Object> oldValues = PersistUtils.getJdbc().queryForList(sql.toString(), Object.class, id);
			if (oldValues.isEmpty()) {
				// no same value, is unique
				// unique = true;
			}
			
			Object oldValue = oldValues.get(0);
			if (oldValue.getClass() == propValue.getClass()) {
				unique = PersistUtils.getHibernateTemplate().isPropertyUnique(entityClass, propName, propValue, oldValue);
			} else {
				if (Configs.isOracle()) {
					if (oldValue instanceof BigDecimal && propValue instanceof Long) {
						unique = PersistUtils.getHibernateTemplate().isPropertyUnique(entityClass, propName, BigDecimal.valueOf((Long) propValue), oldValue);
					}
				}
			}
		}
		
		// do validation
		if (!unique) {
			validator.appendError(new WebValidator(false, propTitle + propValue + "已存在"));
		}
	}

}
