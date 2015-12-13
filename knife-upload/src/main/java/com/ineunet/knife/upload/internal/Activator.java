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
package com.ineunet.knife.upload.internal;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

import com.ineunet.knife.mgt.AbstractActivator;
import com.ineunet.knife.mgt.IBundleContext;
import com.ineunet.knife.mgt.stereotype.BundleActivator;
import com.ineunet.knife.upload.UploadUtils;
import com.ineunet.knife.upload.WebPaths;

/**
 * 
 * @author Hilbert
 * 
 * @since 2.2.6
 * 
 */
@BundleActivator("uploadActivator")
class Activator extends AbstractActivator implements ServletContextAware {

	@Override
	protected void start(IBundleContext bc) {
		String rootPath = WebPaths.getRootPath();
		String cachePath = rootPath + UploadUtils.CACHE_PATH_PART;
		File cachePathFile = new File(cachePath);
		if (!cachePathFile.exists()) {
			cachePathFile.mkdirs();
		}
		
		String tempPath = WebPaths.getRootPath() + UploadUtils.TEMP_PATH_PART;
		File tempPathFile = new File(tempPath);
		if (!tempPathFile.exists()) {
			tempPathFile.mkdirs();
		}
	}

	@Override
	protected void stop(IBundleContext bc) {
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		WebPaths.init(servletContext);
	}

}
