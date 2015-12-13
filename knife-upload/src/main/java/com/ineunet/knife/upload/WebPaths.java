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

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.WebUtils;

/**
 * 可在web.xml中配置listener来初始化，也可在第一次使用的时候初始化
 * @author hilbert.wang@hotmail.com
 * @since 2.2.6
 */
public class WebPaths implements ServletRequestListener {
	
	private static String rootUrl;
	private static String rootPath;
	private static boolean inited = false;
	
	/**
	 * @param request HttpServletRequest
	 */
	public static void init(HttpServletRequest request) {
		if (request == null) {
			return;
		}
		if (inited) return;
		// init rootUrl
		String reqUrl = request.getRequestURL().toString();
		String reqUri = request.getRequestURI();
		int index = reqUrl.length() - reqUri.length();
		rootUrl = reqUrl.substring(0, index) + request.getContextPath();
		
		// init rootPath
		rootPath = request.getSession().getServletContext().getRealPath("/");
		
		// set flag
		inited = true;
	}
	
	public static void init(ServletContext servletContext) {
		if (servletContext == null) {
			return;
		}
		if (inited) return;
		// init rootPath
		rootPath = servletContext.getRealPath("/");
	}
	
	/**
	 * @return e.g. /Workspace/iNeunet/ioo/src/main/webapp
	 */
	public static String getRootPath() {
		if (rootPath == null) {
			WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			if (webApplicationContext == null) {
				rootPath = System.getProperty(WebUtils.DEFAULT_WEB_APP_ROOT_KEY);
			} else {
				ServletContext servletContext = webApplicationContext.getServletContext();
				rootPath = servletContext.getRealPath("/");
			}
		}
		return rootPath;
	}
	
	/**
	 * @return e.g. http://localhost:8080/ioo
	 */
	public static String getRootURL() {
		return rootUrl;
	}
	
	@Override
	public void requestInitialized(ServletRequestEvent requestEvent) {
		if (!(requestEvent.getServletRequest() instanceof HttpServletRequest)) {
			throw new IllegalArgumentException(
					"Request is not an HttpServletRequest: " + requestEvent.getServletRequest());
		}
		HttpServletRequest request = (HttpServletRequest) requestEvent.getServletRequest();
		WebPaths.init(request);
	}
	
	@Override
	public void requestDestroyed(ServletRequestEvent requestEvent) {
	}

}
