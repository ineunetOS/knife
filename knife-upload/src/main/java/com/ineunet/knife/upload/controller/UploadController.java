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
package com.ineunet.knife.upload.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ineunet.knife.api.IUploadEntity;
import com.ineunet.knife.config.Configs;
import com.ineunet.knife.mgt.log.MgtLogUtils;
import com.ineunet.knife.upload.UploadUtils;
import com.ineunet.knife.upload.WebPaths;
import com.ineunet.knife.upload.service.IFileViewService;
import com.ineunet.knife.upload.service.IUploadService;
import com.ineunet.knife.upload.service.impl.FileViewHibernateService;
import com.ineunet.knife.util.ClassStrUtils;
import com.ineunet.knife.util.StringUtils;

/**
 * For support file upload or image upload.
 * @author Hilbert Wang
 * @since 2.0.0
 */
public abstract class UploadController<T extends IUploadEntity> {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected abstract IUploadService<T> getService();
	@Resource(name = FileViewHibernateService.SERVICE_NAME)
	private IFileViewService fileViewService;
	protected static final String NAME_LIMIT_SIZE_KB = "limitSizeKB";
	
	protected void beforeDetail(HttpServletRequest request, T command, Model model) {
		WebPaths.init(request);
		// associatedId
		Long id = command.getId();
		Collection<String> fields = this.getService().getFields();
		for (String field : fields) {
			String column = this.getService().getColumnByField(field);
			String fileUrl = this.getFileURLInternal(id, column, false);
			if (StringUtils.isNotBlank(fileUrl)) {
				model.addAttribute(field + "URL", fileUrl);
			}
		}
	}
	
	/**
	 * @param id associatedId
	 * @param column column name
	 * @param isUpload true: modify/upload image, show from temp if create. false: show from cache if update.
	 * @return fileURL
	 */
	private String getFileURLInternal(Long id, String column, boolean isUpload) {
		String rootPath = WebPaths.getRootPath();
		if (StringUtils.isBlank(rootPath)) {
			// Never do upload. Must initialize after system lunched.
			return "";
		}
		
		/*
		 * 1. If is create,  return getFileUrlOnCreateAndUpload.
		 * 2. If is update and is upload, return getFileUrlOnCreateAndUpload.
		 * 3. If is update and is retrieve, load image from path 'cache' and return
		 */
		
		String fileUrl;
		if (id == null || id == 0) {
			// is create. load image from 'temp'
			id = this.getService().getAssociatedId(column);
			if (id == 0)
				return "";
			
			fileUrl = getFileUrlOnCreateAndUpload(rootPath, column, id);
		} else if (isUpload) {
			// is upload. show image from 'temp'
			fileUrl = getFileUrlOnCreateAndUpload(rootPath, column, id);
		} else {
			// is retrieve update view. load image from 'cache'
			String cachePath = rootPath + UploadUtils.CACHE_PATH_PART;
			String originalFileName = this.getService().findFileName(column, id);
			if (StringUtils.isBlank(originalFileName)) {
				// originalFileName cannot be null or blank. so, no originalFileName is no record.
				return "";
			}
			
			Long associatedId = fileViewService.findAssociatedId(column, id);
			/*
			 *  use cache. Load file into cache directory if there is no. Method of Build file name is 'buildTempFileName(...)'.
			 */
			String fileName = UploadUtils.buildTempFileName(this.getService().tableName(), column, associatedId, originalFileName);
			File file = new File(cachePath + fileName);
			if (!file.exists()) {
				// not cache. read from db or other place.
				byte[] content = this.getService().findFileContent(column, id);
				if (content == null || content.length == 0)
					return "";
				try {
					FileCopyUtils.copy(content, file);
				} catch (IOException e) {
					if (Configs.isDevMode())
						e.printStackTrace();
					throw MgtLogUtils.doThrow(new RuntimeException(e), logger);
				}
			}
			fileUrl = WebPaths.getRootURL() + UploadUtils.CACHE_PATH_PART + fileName;
		}
		return fileUrl;
	}
	
	private String getFileUrlOnCreateAndUpload(String rootPath, String column, Long associatedId) {
		String tempPath = rootPath + UploadUtils.TEMP_PATH_PART;
		String key = this.getService().getKey(column, associatedId);
		String fileName = this.getService().getTempFileName(key);
		File file = new File(tempPath + fileName);
		if (!file.exists())
			return "";
		return WebPaths.getRootURL() + UploadUtils.TEMP_PATH_PART + fileName;
	}

	@RequestMapping(value = "doUpload", method = RequestMethod.POST)
	public @ResponseBody
	String doUpload(HttpServletRequest request, @RequestParam(value = "field") String field) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		WebPaths.init(request);
		String rootPath = WebPaths.getRootPath();
		String column = ClassStrUtils.hump2Underline(field);
		
		// If associatedId is null or 0, generate one.
		Long associatedId = this.getService().validateId(column, null);
		String fileName = "";
		for (Map.Entry<String, MultipartFile> uf : fileMap.entrySet()) {
			MultipartFile mf = uf.getValue();
			String originalName = mf.getOriginalFilename();
			this.getService().setOriginalFileName(column, associatedId, originalName);
			if (!UploadUtils.checkFileType(originalName)) {
				throw new RuntimeException("unsupported file type");
			}
			
			fileName = UploadUtils.buildTempFileName(this.getService().tableName(), column, associatedId, originalName);
			String key = this.getService().getKey(column, associatedId);
			this.getService().setTempFileName(key, fileName);
			
			// copy file
			String tempPath = rootPath + UploadUtils.TEMP_PATH_PART;
			File file = new File(tempPath + fileName);
			FileCopyUtils.copy(mf.getBytes(), file);
			
			this.getService().setTempContent(mf.getBytes(), column, associatedId);
			break;
		}
		return fileName;
	}
	
}
