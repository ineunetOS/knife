package com.ineunet.knife.security;

/**
 * 通过回调函数的方式修改已有方法实现自定义函数。如：修改Server.currentTenantId()的返回值。可通过注册Callable达到。<br>
 * 
 * @author hilbert.wang@hotmail.com
 * Created on 2015年12月6日
 */
public enum ServerCallable {

	currentTenantId, 
	currentAccount, currentFullAccount
	
}
