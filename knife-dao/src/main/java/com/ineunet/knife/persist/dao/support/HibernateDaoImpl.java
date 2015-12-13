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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ineunet.knife.persist.dao.IGenericDao;
import com.ineunet.knife.persist.exception.PersistException;
import com.ineunet.knife.util.Asserts;
import com.ineunet.knife.util.ClassStrUtils;
import com.ineunet.knife.util.ISortedMap;
import com.ineunet.knife.util.ReflectionUtils;
import com.ineunet.knife.util.StringUtils;

/**
 * 
 * @author Hilbert
 * 
 */
public class HibernateDaoImpl<T> implements IGenericDao<T> {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected SessionFactory sessionFactory;
	protected Class<T> entityClass = ReflectionUtils.getSuperClassGenricType(getClass());

	public HibernateDaoImpl() {}
	
	public HibernateDaoImpl(SessionFactory sessionFactory) {
		Asserts.notNull(sessionFactory);
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * 取得当前Session.
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void evict(Object object) {
		this.getSession().evict(object);
	}
	
	public <X> void evict(List<X> objects) {
		for (X x : objects) {
			this.evict(x);
		}
	}
	
	public void clear() {
		this.getSession().clear();
	}
	
	@Override
	public String tableName() {
		return tableName(this.entityClass);
	}
	
	@Override
	public <X> String tableName(Class<X> entityClass) {
		Table table = entityClass.getAnnotation(Table.class);
		if (table == null)
			throw new EntityExistsException("Lack of Annotation @Table");
		String name = table.name();
		if (StringUtils.isBlank(name))
			return ClassStrUtils.hump2Underline(name);
		return name;
	}
	
	@Override
	public String entityName() {
		return this.entityClass.getSimpleName();
	}

	@Override
	public <X> X create(X entity) {
		Asserts.notNull(entity, "entity cannot be null.");
		getSession().save(entity);
		getSession().flush();
		return entity;
	}

	@Override
	public <X> void create(Collection<X> entities) {
		Asserts.notNull(entities, "entities cannot be null.");
		for (X entity : entities)
			this.create(entity);
	}

	@Override
	public <X> X update(X entity) {
		Asserts.notNull(entity, "entity cannot be null.");
		getSession().update(entity);
		getSession().flush();
		return entity;
	}

	@Override
	public <X> void update(Collection<X> entities) {
		Asserts.notNull(entities, "entities cannot be null.");
		for (X entity : entities)
			this.update(entity);
	}

	@Override
	public <X> X merge(X entity) {
		Asserts.notNull(entity, "entity cannot be null.");
		getSession().merge(entity);
		getSession().flush();
		return entity;
	}

	@Override
	public <X> void saveOrUpdate(X entity) {
		Asserts.notNull(entity, "entity cannot be null.");
		getSession().saveOrUpdate(entity);
		getSession().flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(Object id) {
		Asserts.notNull(id, "id cannot be null.");
		return (T) getSession().get(entityClass, (Serializable) id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Object id, Class<X> entityClass) {
		Asserts.notNull(id, "id cannot be null.");
		return (X) getSession().get(entityClass, (Serializable) id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> getByIds(Collection<?> ids, Class<X> entityClass) {
		if (ids == null || ids.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		return getSession().createQuery("from " + entityClass.getSimpleName() + " where id in(:ids)")
				.setParameterList("ids", ids).list();
	}

	@Override
	public List<T> findByProperty(String propertyName, Object propertyValue) {
		Asserts.notBlank(propertyName, "propertyName cannot be null.");
		Criterion criterion = Restrictions.eq(propertyName, propertyValue);
		return find(criterion);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public<X> List<X> findByProperty(Class<X> entityClass, String propertyName, Object propertyValue) {
		StringBuilder sb = new StringBuilder();
		sb.append("select o from ").append(entityClass.getSimpleName()).append(" o where o.");
		sb.append(propertyName).append("=?");
		return this.createQuery(sb.toString(), propertyValue).list();
	}

	@Override
	public List<T> findByProperty(String propertyName, Object... propertyValues) {
		return (List<T>) this.findByProperty(propertyName, entityClass, propertyValues);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public<X> List<X> findByProperty(Class<X> entityClass, String propertyName, Object... propertyValues) {
		StringBuilder sb = new StringBuilder();
		sb.append("select o from ").append(entityClass.getSimpleName()).append(" o where ");
		for (int i = 0; i < propertyValues.length - 1; i++)
			sb.append("o.").append(propertyName).append("=?").append(" or ");
		sb.append("o.").append(propertyName).append("=?");
		return this.createQuery(sb.toString(), propertyValues).list();
	}
	
	@Override
	public <X> Long countByProperty(Class<X> entityClass, String propertyName, Object propertyValue) {
		StringBuilder jql = new StringBuilder("select count(*) from ");
		jql.append(entityClass.getSimpleName()).append(" where ");
		jql.append(propertyName).append("=?");
		return this.count(jql.toString(), propertyValue);
	}

	@Override
	public <X> boolean existsByProperty(Class<X> entityClass, String propertyName, Object propertyValue) {
		return 0 != countByProperty(entityClass, propertyName, propertyValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findOneByProp(String propertyName, Object propertyValue) {
		Asserts.notBlank(propertyName, "propertyName cannot be null.");
		Criterion criterion = Restrictions.eq(propertyName, propertyValue);
		return (T) createCriteria(criterion).uniqueResult();
	}
	
	@Override
	public <X> X findOne(String propertyName, Object propertyValue, Class<X> entityClass) {
		StringBuilder hql = new StringBuilder();
		hql.append("from ").append(entityClass.getSimpleName()).append(" o where o.").append(propertyName).append("=?");
		List<X> list = this.find(hql.toString(), propertyValue);
		if(list.isEmpty())
			return null;
		else if(list.size() == 1)
			return list.get(0);
		else
			throw new PersistException("too many rows, should be unique in business.");
	}
	
	@Override
	public <X> X findOne(String jql, Object... values) {
		List<X> list = this.find(jql, values);
		if(list.isEmpty())
			return null;
		else if(list.size() == 1)
			return list.get(0);
		else
			throw new PersistException("too many rows, should be unique in business.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> find(ISortedMap<String, Object> properties) {
		Asserts.notEmpty(properties.values(), "properties cannot be null.");
		StringBuilder sb = new StringBuilder();
		sb.append("from ").append(entityClass.getSimpleName()).append(" o where ");
		for (String key : properties.keyList()) {
			sb.append("o.").append(key).append("=:").append(key);
		}
		return this.createQuery(sb.toString(), properties.asMap()).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> find(String jql, List<Object> values) {
		return createQuery(jql, values).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> find(String jql, Object... values) {
		return createQuery(jql, values).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> find(String jql, Map<String, ?> values) {
		return createQuery(jql, values).list();
	}

	/**
	 * Not support collection parameter, 
	 * use <code>count(String jql, Map<String, ?> values)</code> if in need.
	 */
	@Override
	public long count(String jql, Object... values) {
		return (Long) this.createQuery(jql, values).list().get(0);
	}
	
	@Override
	public long count(String jql, Map<String, ?> values) {
		return (Long) this.createQuery(jql, values).list().get(0);
	}

	@Override
	public List<T> findAll() {
		return find("from " + entityClass.getSimpleName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> findAll(Class<X> entityClass) {
		return this.createQuery("from " + entityClass.getSimpleName()).list();
	}

	@Override
	public <X> void delete(X entity) {
		Asserts.notNull(entity, "entity cannot be null.");
		getSession().delete(entity);
	}

	@Override
	public void deleteById(Object id) {
		this.deleteById(id, entityClass);
	}
	
	@Override
	public void deleteById(Object id, Class<?> entityClass) {
		Asserts.notNull(id, "id cannot be null.");
		String hql = "delete from " + entityClass.getSimpleName() + " where id=?";
		this.batchExecute(hql, id);
	}

	@Override
	public void deleteByIds(List<Object> ids) {
		Asserts.notEmpty(ids, "id cannot be null.");
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(this.entityClass.getSimpleName());
		sb.append(" where ");
		for (int i = 0; i < ids.size() - 1; i++)
			sb.append("id=? or ");
		sb.append("id=?");
		this.batchExecute(sb.toString(), ids.toArray());
	}

	@Override
	public void deleteByProperties(ISortedMap<String, Object> properties) {
		Asserts.notEmpty(properties.values(), "properties cannot be null.");
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(entityClass.getSimpleName()).append(" o where ");
		for (String key : properties.keyList()) {
			sb.append("o.").append(key).append("=:").append(key);
		}
		this.batchExecute(sb.toString(), properties.asMap());
	}
	
	@Override
	public void deleteByProperty(String propertyName, Object propertyValue) {
		this.deleteByProperty(entityClass, propertyName, propertyValue);
	}
	
	@Override
	public void deleteByProperty(Class<?> entityClass, String propertyName, Object propertyValue) {
		String ql = "delete from " + entityClass.getSimpleName() + " o where o." + propertyName + "=?";
		this.batchExecute(ql, propertyValue);
	}

	@Override
	public void deleteByProperty(String propertyName, Object... propertyValues) {
		this.deleteByProperty(entityClass, propertyName, propertyValues);
	}
	
	@Override
	public void deleteByProperty(Class<?> entityClass, String propertyName, Object... propertyValues) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ").append(entityClass.getSimpleName()).append(" o where ");
		for (int i = 0; i < propertyValues.length - 1; i++)
			sb.append("o.").append(propertyName).append("=?").append(" or ");
		sb.append("o.").append(propertyName).append("=?");
		this.batchExecute(sb.toString(), propertyValues);
	}

	@Override
	public int batchExecute(String hql, Map<String, ?> values) {
		return createQuery(hql, values).executeUpdate();
	}

	@Override
	public int batchExecute(String hql, Object... values) {
		return createQuery(hql, values).executeUpdate();
	}

	public Class<T> getEntityClass() {
		return this.entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * 有多个SesionFactory的时候在子类重载本函数.
	 */
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/*
	 * =============================== for hibernate
	 * =============================
	 */

	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions)
			criteria.add(c);
		return criteria;
	}

	@SuppressWarnings("unchecked")
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	@SuppressWarnings("unchecked")
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}

	public Query createQuery(final String queryString, final Object... values) {
		Asserts.notBlank(queryString, "queryString cannot be null.");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象. 与find()函数可进行更加灵活的操作.
	 * 
	 * @param values
	 *            参数存放在动态数组,按顺序绑定.
	 */
	public Query createQuery(final String queryString, final List<Object> values) {
		Asserts.notBlank(queryString, "queryString cannot be null.");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			int j = 0;
			for (Iterator<Object> i = values.iterator(); i.hasNext();) {
				query.setParameter(j, i.next());
				j++;
			}
		}
		return query;
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象. 与find()函数可进行更加灵活的操作.
	 * 
	 * @param values
	 *            命名参数,按名称绑定.
	 */
	public Query createQuery(final String queryString, final Map<String, ?> values) {
		Asserts.notBlank(queryString, "queryString cannot be null.");
		Query query = getSession().createQuery(queryString);
		if (values != null)
			query.setProperties(values);
		return query;
	}

	/**
	 * 取得对象的主键名.
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}

	/**
	 * 初始化对象. 使用load()方法得到的仅是对象Proxy, 在传到View层前需要进行初始化. 如果传入entity,
	 * 则只初始化entity的直接属性,但不会初始化延迟加载的关联集合和属性. 如需初始化关联属性,需执行:
	 * Hibernate.initialize(user.getRoles())，初始化User的直接属性和关联集合.
	 * Hibernate.initialize
	 * (user.getDescription())，初始化User的直接属性和延迟加载的Description属性.
	 */
	public void initProxyObject(Object proxy) {
		Hibernate.initialize(proxy);
	}

	/**
	 * Flush当前Session.
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 */
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findOneByProp(propertyName, newValue);
		return (object == null);
	}
	
	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
	 * @since 2.0.1
	 */
	public boolean isPropertyUnique(Class<?> entityClass, final String propertyName, final Object newValue, final Object oldValue) {
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findOne(propertyName, newValue, entityClass);
		return (object == null);
	}

}
