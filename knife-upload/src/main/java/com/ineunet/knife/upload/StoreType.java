package com.ineunet.knife.upload;

/**
 * 
 * @author hilbert.wang@hotmail.com
 * Created on 2015年3月8日
 */
public enum StoreType {

	/**
	 * 分散保存在各自业务表中
	 */
	db_selfTable, 
	
	/**
	 * 统一保存在knife_image_view表中
	 */
	db_DBImageView,
	
	/**
	 * 不存数据库，存webapp目录下
	 */
	dir_webapp
	
}
