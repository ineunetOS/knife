package com.ineunet.knife.api;

/**
 * 
 * @author Hilbert
 *
 * @param <T>
 * @since 2.2.5
 */
public interface INameEntity<T> extends IEntity<T> {
	
	/**
	 * 用来在系统日志中显示
	 * @return name of the business entity. e.g. admin
	 * @since 2.0.0
	 */
	String getName();

}
