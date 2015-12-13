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
package com.ineunet.knife.mgt.log;

import org.slf4j.Logger;

import com.ineunet.knife.config.Configs;
import com.ineunet.knife.mgt.BundleUtils;
import com.ineunet.knife.util.Asserts;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 *
 */
public abstract class MgtLogUtils {

	private static IMgtLogger log;
	
	private static final IMgtLogger getLog() {
		if(log == null)
			log = BundleUtils.getService(IMgtLogger.SERVICE_NAME, IMgtLogger.class);
		return log;
	}
	
	/**
	 * @since 1.2.0
	 */
	public static void error(Throwable t) {
		error(t.getMessage(), t);
	}
	
	public static void error(String msg, Throwable t) {
		if(Configs.mgtlog()) {
			IMgtLogger log = getLog();
			if (log != null)
				log.error(msg, t);
		}
		if (Configs.isDevMode())
			t.printStackTrace();
	}
	
	/**
	 * @since 2.0.2
	 */
	public static void error(Throwable t, Logger log) {
		error(t);
		log.error(t.getMessage(), t);
	}
	
	/**
	 * @since 1.2.0
	 */
	public static void warn(Throwable t) {
		warn(t.getMessage(), t);
	}
	
	public static void warn(String msg, Throwable t) {
		if(Configs.mgtlog()) {
			IMgtLogger log = getLog();
			if (log != null)
				log.warn(msg, t);
		}
	}
	
	/**
	 * For example:<br>
	 * <code>
	 * 		IllegalArgumentException e = new IllegalArgumentException()<br>
	 * 		throw MgtLogUtils.doThrow(e, Logger)
	 * </code>
	 * @return nothing
	 * @since 2.0.0
	 */
	public static RuntimeException doThrow(Throwable e, Logger log) {
		Asserts.notNull(e);
		MgtLogUtils.error(e.getMessage(), e);
		if (log != null)
			log.error(e.getMessage(), e);
		if (e instanceof RuntimeException)
			throw (RuntimeException) e;
		throw new RuntimeException(e);
	}
	
}
