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
package com.ineunet.knife.upload.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ineunet.knife.mgt.log.MgtLogUtils;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.persist.dao.IGenericDao;
import com.ineunet.knife.upload.IFileView;
import com.ineunet.knife.upload.entity.ImageView;
import com.ineunet.knife.upload.service.IFileViewService;
import com.ineunet.knife.util.Asserts;

/**
 *
 * @author Hilbert Wang
 * @since 2.0.0
 */
@Service(FileViewHibernateService.SERVICE_NAME)
@Transactional
public class FileViewHibernateService implements IFileViewService, InitializingBean {
	private static final Logger log = LoggerFactory.getLogger(FileViewHibernateService.class);
	public static final String SERVICE_NAME = "fileViewHibernateService";
	private static final String TABLE_NAME = "knife_image_view";
	private static final String ENTITY_NAME = ImageView.class.getSimpleName();
	
	private IGenericDao<?> hibernate;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.hibernate = PersistUtils.getHibernateTemplate();
	}
	
	protected String tableName() {
		return TABLE_NAME;
	}
	
	@Override
	public IFileView create(IFileView view) {
		this.beforeSaveOrUpdate(view);
		ImageView dbView = (ImageView) view;
		String table = dbView.getCategoryTable();
		String column = dbView.getCategoryColumn();
		Long idValue = dbView.getCategoryId();
		IFileView otherView = this.get(table, column, idValue);
		if (otherView == null)
			return this.hibernate.create(view);
		IllegalArgumentException e = new IllegalArgumentException("Duplicated view.");
		throw MgtLogUtils.doThrow(e, log);
	}

	@Override
	public IFileView update(IFileView view) {
		this.beforeSaveOrUpdate(view);
		ImageView dbView = (ImageView) view;
		String table = dbView.getCategoryTable();
		String column = dbView.getCategoryColumn();
		Long idValue = dbView.getCategoryId();
		List<IFileView> views = this.find(table, column, idValue);
		int size = views.size();
		if (size == 0) {
			IllegalStateException e = new IllegalStateException("Wrong operation of update. view not exist: " + view);
			throw MgtLogUtils.doThrow(e, log);
		} else if (size != 1) {
			IllegalStateException e = new IllegalStateException("Wrong data in database. Too many views of: " + view);
			throw MgtLogUtils.doThrow(e, log);
		} else {
			ImageView po = (ImageView) views.get(0);
			if (po.getId() != dbView.getId()) {
				IllegalArgumentException e = new IllegalArgumentException("View path exists: " + po);
				throw MgtLogUtils.doThrow(e, log);
			}
		}
		return this.hibernate.update(view);
	}
	
	private void beforeSaveOrUpdate(IFileView view) {
		if (! (view instanceof ImageView) ) {
			IllegalArgumentException e = new IllegalArgumentException("Wrong type: not DBImageView.");
			MgtLogUtils.doThrow(e, log);
		}
	}

	@Override
	public void delete(Long id) {
		this.hibernate.deleteById(id);
	}

	@Override
	public void delete(String table, String column, Long categoryId) {
		Asserts.notBlank(table);
		Asserts.notBlank(column);
		Asserts.notBlank(categoryId);
		String jql = "delete from " + ENTITY_NAME + " where categoryId=? and categoryColumn=? and categoryTable=?";
		this.hibernate.batchExecute(jql, categoryId, column, table);
	}

	@Override
	public void delete(String table, Long categoryId) {
		Asserts.notBlank(table);
		Asserts.notBlank(categoryId);
		String jql = "delete from " + ENTITY_NAME + " where categoryId=? and categoryTable=?";
		this.hibernate.batchExecute(jql, categoryId, table);
	}
	
	@Override
	public IFileView get(Long id) {
		return this.hibernate.get(id, ImageView.class);
	}

	@Override
	public IFileView get(String table, String column, Long categoryId) {
		List<IFileView> views = this.find(table, column, categoryId);
		if (views.isEmpty())
			return null;
		if (views.size() == 1)
			return views.get(0);
		else {
			throw new IncorrectResultSizeDataAccessException(1, views.size());
		}
	}

	private List<IFileView> find(String table, String column, Long categoryId) {
		Asserts.notBlank(table);
		Asserts.notBlank(column);
		Asserts.notBlank(categoryId);
		String hql = "from " + ImageView.class.getSimpleName() + " where categoryTable=? and categoryColumn=? and categoryId=?";
		List<IFileView> views = this.hibernate.find(hql, table, column, categoryId);
		return views;
	}

	@Override
	public String findFileName(String column, Long categoryId) {
		Asserts.notBlank(column);
		Asserts.notBlank(categoryId);
		String sql = "select file_name from " + TABLE_NAME + " where category_id=? and category_column=?";
		return PersistUtils.getJdbc().queryString(sql, categoryId, column);
	}

	@Override
	public Long findAssociatedId(String column, Long categoryId) {
		Asserts.notBlank(column);
		Asserts.notBlank(categoryId);
		String sql = "select associated_id from " + TABLE_NAME + " where category_id=? and category_column=?";
		try {
			return PersistUtils.getJdbc().queryForLong(sql, categoryId, column);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
