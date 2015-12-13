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

import com.ineunet.knife.upload.IFileView;

/**
 *
 * @author Hilbert Wang
 * @since 2.0.0
 */
public interface IFileViewService {

	IFileView create(IFileView view);
	
	IFileView update(IFileView view);
	
	void delete(Long id);
	
	void delete(String table, String column, Long categoryId);
	
	void delete(String table, Long categoryId);
	
	IFileView get(Long id);
	
	IFileView get(String table, String column, Long categoryId);
	
	/**
	 * @param column column name
	 * @param categoryId categoryId
	 * @return fileName of the FileView
	 */
	String findFileName(String column, Long categoryId);
	
	Long findAssociatedId(String column, Long categoryId);
	
}
