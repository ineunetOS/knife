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
package com.ineunet.knife.upload.service;

import java.util.Collection;

import com.ineunet.knife.persist.dao.IGenericDao;

/**
 *
 * @author Hilbert Wang
 * @since 2.0.0
 */
public interface IUploadService<T> extends IGenericDao<T> {
	
	public static final String SERVICE_NAME = "uploadService";

	// **** associatedId, key, temp, originalName **** //
	
	/**
	 * If id is not null, return id. <br>
	 * If id is null or 0, generate one.
	 * @param id id of <tt>T</tt>
	 * @return id or associatedId
	 */
	Long validateId(String column, Long id);
	
	Long getAssociatedId(String column);
	
	/**
	 * Get key. If no key build one.
	 * @param column
	 * @param id record id when update, null or 0 when create or upload.
	 * @return key.
	 */
	String getKey(String column, Long id);
	
	byte[] getTempContent(String column, Long associatedId);
	
	void setTempContent(byte[] content, String column, Long associatedId);
	
	/**
	 * @param fileNameKey getFileNameKey
	 * @return fileName in dir 'temp'
	 */
	String getTempFileName(String fileNameKey);
	
	void setTempFileName(String key, String fileName);
	
	/**
	 * @param column
	 * @param associatedId
	 * @param fileName generated file name.
	 * @return key
	 */
	String setOriginalFileName(String column, Long associatedId, String fileName);
	
	// **** column -- fields **** //
	
	Collection<String> getFields();
	
	Collection<String> getColumns();
	
	String getColumnByField(String field);
	
	/**
	 * @param field name of field. e.g. userPhoto
	 * @return limitSize, unit is KB
	 * Created on 2015-3-8
	 */
	long getLimitSizeKB(String field);
	
	// **** from cache or db **** //
	
	/**
	 * @see IFileViewService#findFileName(String, Long)
	 * @param column
	 * @param id id of the record. If id is null, id = new associatedId.
	 * @return fileName
	 */
	String findFileName(String column, Long id);
	
	// e.g. from db
	byte[] findFileContent(String column, Long id);
	
}
