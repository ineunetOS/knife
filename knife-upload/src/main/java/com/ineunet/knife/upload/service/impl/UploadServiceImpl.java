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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;

import com.ineunet.knife.api.IUploadEntity;
import com.ineunet.knife.api.RecordStatus;
import com.ineunet.knife.config.KnifeConfig;
import com.ineunet.knife.mgt.log.MgtLogUtils;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.persist.dao.support.HibernateDaoImpl;
import com.ineunet.knife.security.Server;
import com.ineunet.knife.upload.IFileView;
import com.ineunet.knife.upload.StoreType;
import com.ineunet.knife.upload.UploadConfigKeys;
import com.ineunet.knife.upload.UploadUtils;
import com.ineunet.knife.upload.WebPaths;
import com.ineunet.knife.upload.entity.ImageView;
import com.ineunet.knife.upload.service.IFileViewService;
import com.ineunet.knife.upload.service.IUploadService;
import com.ineunet.knife.util.Asserts;
import com.ineunet.knife.util.ConcurrentCache;
import com.ineunet.knife.util.StringUtils;

/**
 *
 * @author Hilbert Wang
 * @since 2.0.0
 */
public class UploadServiceImpl<T extends IUploadEntity> extends HibernateDaoImpl<T> implements IUploadService<T>, InitializingBean {

	@Resource(name = FileViewHibernateService.SERVICE_NAME)
	private IFileViewService fileViewService;
	
	private ConcurrentCache<String, byte[]> fileContentCache = new ConcurrentCache<>(100);
	private ConcurrentCache<String, String> tempFileNameCache = new ConcurrentCache<>(500);
	private ConcurrentCache<String, String> originalfileNameCache = new ConcurrentCache<>(1000);
	/**
	 * e.g. { userPhoto : user_photo }
	 */
	private final Map<String, String> field_column = new HashMap<String, String>();
	/**
	 * e.g. { userPhoto : 50 }, unit of limitSize is KB
	 */
	private final Map<String, Long> field_limitSizeKB = new HashMap<String, Long>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// init columns
		Field[] fields = this.entityClass.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			com.ineunet.knife.core.fileupload.annotation.FileView annot = field.getAnnotation(com.ineunet.knife.core.fileupload.annotation.FileView.class);
			if (annot != null && annot.value()) {
				// @FileView(true)
				String column = annot.column();
				String fieldName = annot.field();
				if (StringUtils.isBlank(fieldName)) {
					fieldName = field.getName();
				}
				if (StringUtils.isBlank(column)) {
					// default column is name of field
					column = fieldName;
				}
				field_column.put(fieldName, column);
				field_limitSizeKB.put(fieldName, annot.limitSizeKB());
			}
		}
		
		if (this.getSessionFactory() == null) {
			this.setSessionFactory(PersistUtils.getSessionFactory());
		}
	}
	
	/**
	 * @return columns
	 */
	@Override
	public Collection<String> getColumns() {
		return field_column.values();
	}
	
	@Override
	public Collection<String> getFields() {
		return field_column.keySet();
	}

	@Override
	public String getColumnByField(String field) {
		return field_column.get(field);
	}
	
	/**
	 * @param field name of field. e.g. userPhoto
	 * @return limitSize, unit is KB
	 * Created on 2015-3-8
	 */
	@Override
	public long getLimitSizeKB(String field) {
		return field_limitSizeKB.get(field);
	}
	
	/**
	 * @return associatedId from cache.
	 */
	@Override
	public Long getAssociatedId(String column) {
		return UploadUtils.getAssociatedId(tableName(), column);
	}
	
	@Override
	public Long validateId(String column, Long id) {
		return UploadUtils.validAssociatedId(tableName(), column, id);
	}
	
	@Override
	public String getKey(String column, Long associatedId) {
		if (associatedId == null || associatedId == 0)
			associatedId = UploadUtils.getAssociatedId(tableName(), column);
		String key = UploadUtils.buildPathOfDBImgView(tableName(), column, associatedId);
		return key;
	}

	@Override
	public byte[] getTempContent(String column, Long id) {
		return this.fileContentCache.get(this.getKey(column, id));
	}
	
	@Override
	public void setTempContent(byte[] content, String column, Long id) {
		this.fileContentCache.put(this.getKey(column, id), content);
	}
	
	@Override
	public String getTempFileName(String key) {
		return this.tempFileNameCache.get(key);
	}
	
	@Override
	public void setTempFileName(String key, String fileName) {
		this.tempFileNameCache.put(key, fileName);
	}
	
	public String setOriginalFileName(String column, Long associatedId, String fileName) {
		Asserts.notBlank(associatedId);
		String key = this.getKey(column, associatedId);
		originalfileNameCache.put(key, fileName);
		return key;
	}
	
	public String getOriginalFileName(String column, Long associatedId) {
		String key = this.getKey(column, associatedId);
		String fileName = originalfileNameCache.get(key);
		return fileName;
	}
	
	public <X> X create(X entity) {
		if (entity instanceof IUploadEntity) {
			entity = super.create(entity);
			Long id = ((IUploadEntity) entity).getId();
			this.createFileView(id);
			return entity;
		} else {
			return super.create(entity);
		}
	}
	
	public void createFileView(Long id) {
		String tableName = tableName();
		Date createTime = new Date();
		String createPerson = Server.currentFullAccount();
		
		for (String column : getColumns()) {
			Long cachedAssociatId = this.getAssociatedId(column);
			String key = this.getKey(column, cachedAssociatId);
			byte[] content = fileContentCache.get(key);
			
			ImageView view = new ImageView("", tableName, column, id);
			String fileName = getOriginalFileName(column, cachedAssociatId);
			if (!checkFile(fileName, content)) {
				continue;
			}
			
			String selfTable = StoreType.db_selfTable.toString();
			StoreType storeType = StoreType.valueOf(KnifeConfig.get(UploadConfigKeys.STORE_TYPE, selfTable));
			if (storeType == StoreType.dir_webapp) {
				// copy to cache directory
				this.copyToCache(tableName, column, cachedAssociatId, fileName, content);
			} else if (storeType == StoreType.db_selfTable) {
				// TODO add column automatic by configure or not on initializing
				try {
					// Packet for query is too large (17996232 > 1048576).
					// The last packet successfully received from the server was 1,037 milliseconds ago.
					PersistUtils.getJdbc().update(tableName, id, column, content);
				} catch (Exception e) {
					logger.error("Image may be too large. You could modify database configure.", e);
					this.copyToCache(tableName, column, cachedAssociatId, fileName, content);
				}
			} else if (storeType == StoreType.db_DBImageView) {
				view.setContent(content);
			}
			view.setFileName(fileName);
			view.setTenantId(Server.currentTenantId());
			view.setCreatePerson(createPerson);
			view.setCreateTime(createTime);
			view.setStatus(RecordStatus.normal);
			view.setAssociatedId(cachedAssociatId);
			fileViewService.create(view);
		}
	}
	
	public <X> X update(X entity) {
		if (entity instanceof IUploadEntity) {
			Long categoryId = ((IUploadEntity) entity).getId();
			this.updateFileView(categoryId);
			entity = super.update(entity);
			return entity;
		} else {
			return super.update(entity);
		}
	}
	
	public void updateFileView(Long id) {
		String tableName = tableName();
		for (String column : getColumns()) {
			Long cachedAssociatId = this.getAssociatedId(column);
			String key = this.getKey(column, cachedAssociatId);
			byte[] content = fileContentCache.get(key);
			String fileName = getOriginalFileName(column, cachedAssociatId);
			if (!checkFile(fileName, content)) {
				continue;
			}
			
			ImageView view = (ImageView) fileViewService.get(tableName, column, id);
			String selfTable = StoreType.db_selfTable.toString();
			StoreType storeType = StoreType.valueOf(KnifeConfig.get(UploadConfigKeys.STORE_TYPE, selfTable));
			if (storeType == StoreType.dir_webapp) {
				// copy to cache directory
				this.copyToCache(tableName, column, cachedAssociatId, fileName, content);
			} else if (storeType == StoreType.db_selfTable) {
				// TODO add column automatic by configure or not on initializing
				try {
					// Packet for query is too large (17996232 > 1048576).
					// The last packet successfully received from the server was 1,037 milliseconds ago.
					PersistUtils.getJdbc().update(tableName, id, column, content);
				} catch (Exception e) {
					logger.error("Image may be too large. You could modify database configure.", e);
					this.copyToCache(tableName, column, cachedAssociatId, fileName, content);
				}
			} else if (storeType == StoreType.db_DBImageView) {
				view.setContent(content);
			} else {
				throw new UnsupportedOperationException("Unknown StoreType");
			}
			
			if (view == null) {
				// create new view
				Long tenantId = Server.currentTenantId();
				Date createTime = new Date();
				String createPerson = Server.currentFullAccount();
				
				view = new ImageView("", tableName, column, id);
				view.setFileName(fileName);
				view.setTenantId(tenantId);
				view.setCreatePerson(createPerson);
				view.setCreateTime(createTime);
				view.setStatus(RecordStatus.normal);
				view.setAssociatedId(cachedAssociatId);
				fileViewService.create(view);
			} else {
				Date updateTime = new Date();
				String updatePerson = Server.currentFullAccount();
				
				view.setFileName(fileName);
				view.setUpdatePerson(updatePerson);
				view.setUpdateTime(updateTime);
				view.setAssociatedId(cachedAssociatId);
				fileViewService.update(view);
			}
			
		}
	}
	
	// copy to cache directory
	private void copyToCache(String tableName, String column, Long cachedAssociatId, String fileName, byte[] content) {
		try {
			String cachePath = WebPaths.getRootPath() + UploadUtils.CACHE_PATH_PART;
			String accessFileName = UploadUtils.buildTempFileName(tableName, column, cachedAssociatId, fileName);
			File file = new File(cachePath + accessFileName);
			FileCopyUtils.copy(content, file);
		} catch (IOException e) {
			throw MgtLogUtils.doThrow(new RuntimeException(e), logger);
		}
	}
	
	/**
	 * 1. Check whether the file is new uploaded. Means whether need update the fileView.
	 * 2. Check whether has the logical exception. e.g. no fileName but has file content.
	 * @param fileName
	 * @param content
	 * @return is valid
	 */
	private boolean checkFile(String fileName, byte[] content) {
		if (StringUtils.isBlank(fileName)) {
			// no fileName is assume no content.
			// check whether no content
			if (content == null || content.length == 0)
				return false;
			else {
				IllegalArgumentException e = new IllegalArgumentException("fileName is null but content is not null.");
				throw MgtLogUtils.doThrow(e, logger);
			}
		}
		return true;
	}
	
	@Override
	public void deleteByIds(List<Object> ids) {
		super.deleteByIds(ids);
		for (Object id : ids) {
			Long associatedId = (Long) id;
			fileViewService.delete(tableName(), associatedId);
		}
	}
	
	/**
	 * @see FileViewHibernateService#findFileName(String, Long)
	 */
	@Override
	public String findFileName(String column, Long id) {
		return fileViewService.findFileName(column, id);
	}

	@Override
	public byte[] findFileContent(String column, Long id) {
		String selfTable = StoreType.db_selfTable.toString();
		StoreType storeType = StoreType.valueOf(KnifeConfig.get(UploadConfigKeys.STORE_TYPE, selfTable));
		if (storeType == StoreType.dir_webapp) {
			return null;
		} else if (storeType == StoreType.db_selfTable) {
			return PersistUtils.getJdbc().queryForObject("select " + column + " from " + tableName() + " where id=?", byte[].class, id);
		} else if (storeType == StoreType.db_DBImageView) {
			IFileView view = fileViewService.get(tableName(), column, id);
			if (view == null)
				return null;
			return view.getContent();
		} else {
			throw new UnsupportedOperationException("Unknown StoreType");
		}
	}

	public IFileViewService getFileViewService() {
		return fileViewService;
	}

	public void setFileViewService(IFileViewService fileViewService) {
		this.fileViewService = fileViewService;
	}

}
