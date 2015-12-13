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

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 *
 */
public interface IMgtLogger {
	
	static final String SERVICE_NAME = "mgtLogger";
	
	public static enum LogLevel {
		info, debug, warn, error, fatal
	}

	/**
	 * Log an exception (throwable) at the WARN level with an
	 * accompanying message.
	 * 
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	void warn(String msg, Throwable t);

	/**
	 * Log an exception (throwable) at the ERROR level with an
	 * accompanying message.
	 * 
	 * @param msg
	 *            the message accompanying the exception
	 * @param t
	 *            the exception (throwable) to log
	 */
	void error(String msg, Throwable t);

}
