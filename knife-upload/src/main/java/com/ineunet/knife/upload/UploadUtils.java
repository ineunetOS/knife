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
package com.ineunet.knife.upload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.ineunet.knife.security.Server;
import com.ineunet.knife.util.Asserts;

/**
 * 
 * @author Hilbert
 * @since 2.0.0
 */
public final class UploadUtils {
	
	public static final String TEMP_PATH_PART = "/resources/data/upload/temp/";
	public static final String CACHE_PATH_PART = "/resources/data/upload/cache/";

	private static String[] allowFiles = { ".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".wmv", ".gif", ".png", ".jpg", ".jpeg", ".bmp" };
	
	private static final Random RAND = new Random();
	
	/** Attribute name of associatedId */
	public static final String NAME_ATTRIBUTE_ASSOCIATEDID = "associatedID_";

	private UploadUtils() {}
	
	/**
	 * 文件类型判断
	 * @param fileName
	 * @return
	 */
	public static boolean checkFileType(String fileName) {
		for (Iterator<String> type = Arrays.asList(allowFiles).iterator(); type.hasNext();) {
			String ext = type.next();
			if (fileName.toLowerCase().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取文件扩展名
	 * @return string
	 */
	public static String getFileSuffix(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 依据原始文件名生成新文件名
	 * 
	 * @return
	 */
	public static String getName(String fileName, boolean containsTime) {
		Random random = new Random();
		String name = fileName.substring(0, fileName.lastIndexOf(".")) + random.nextInt(10000);
		if(containsTime)
			return name + System.currentTimeMillis() + getFileSuffix(fileName);
		else
			return name + getFileSuffix(fileName);
	}

	/**
	 * 根据字符串创建本地目录 并按照日期建立子目录返回
	 * 
	 * @param path
	 * @return
	 */
	public static String getFolder(HttpServletRequest request, String path) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
		path += "/" + formater.format(new Date());
		File dir = new File(getPhysicalPath(request, path));
		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return path;
	}

	/**
	 * 根据传入的虚拟路径获取物理路径
	 * @param path
	 * @return
	 */
	public static String getPhysicalPath(HttpServletRequest request, String path) {
		String servletPath = request.getServletPath();
		String realPath = request.getSession().getServletContext().getRealPath(servletPath);
		String ueditor = new File(realPath).getParentFile().getParent();
		return ueditor + "/" + path;
	}

	/**
	 * key of image column.
	 * @param imgViewId id of the image in <tt>DBImageView</tt>
	 * @param tableName table name mapped with the entity
	 * @param column definition column of the table <tt>tableName</tt>
	 * @param associatedId id value of the row of the <tt>tableName</tt>
	 * @return context path of the image stored in the table <tt>tableName</tt>
	 * @since 2.0.0
	 */
	public static String buildPathOfDBImgView(Long imgViewId, String tableName, String column, Long associatedId) {
		if (imgViewId == null || imgViewId == 0) {
			return buildPathOfDBImgView(tableName, column, associatedId);
		}
		return imgViewId + "," + buildPathOfDBImgView(tableName, column, associatedId);
	}
	
	public static String buildPathOfDBImgView(String tableName, String column, Long associatedId) {
		Asserts.notBlank(tableName);
		Asserts.notBlank(column);
		return tableName + "/" + column + "/" + validAssociatedId(tableName, column, associatedId);
	}
	
	/**
	 * @param tableName
	 * @param column
	 * @param associatedId
	 * @param originalName
	 * @return e.g. user_photo_0.png
	 */
	public static String buildTempFileName(String tableName, String column, Long associatedId, String originalName) {
		Asserts.notBlank(originalName);
		return buildTempFileKeyOfDbImgView(tableName, column, associatedId) + originalName;
	}
	
	public static String buildTempFileKeyOfDbImgView(String tableName, String column, Long associatedId) {
		Asserts.notBlank(tableName);
		Asserts.notBlank(column);
		return tableName + "_" + column + "_" + validAssociatedId(tableName, column, associatedId);
	}
	
	/**
	 * @param associatedId id of the row of the definition table.
	 * @return <code>System.currentTimeMillis() + RAND.nextInt(1000)</code> if <tt>associatedId</tt> is null or 0.
	 */
	public static final Long validAssociatedId(String tableName, String column, Long associatedId) {
		if (associatedId == null || associatedId == 0) {
			associatedId = System.currentTimeMillis() + RAND.nextInt(1000);
			addAttribute(NAME_ATTRIBUTE_ASSOCIATEDID + tableName + "_" + column, associatedId);
		}
		return associatedId;
	}
	
	static final void addAttribute(String key, Object value) {
		Server.getSession().setAttribute(key, value);
	}
	
	/**
	 * @return associatedId which get from session. If <code>associatedId==null</code> return 0L.
	 */
	public static Long getAssociatedId(String tableName, String column) {
		Long associatedId = (Long) Server.getSession().getAttribute(NAME_ATTRIBUTE_ASSOCIATEDID + tableName + "_" + column);
		if (associatedId == null)
			return 0L;
		return associatedId;
	}

}
