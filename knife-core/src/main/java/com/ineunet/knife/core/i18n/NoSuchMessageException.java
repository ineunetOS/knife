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
package com.ineunet.knife.core.i18n;

import java.util.Locale;

@SuppressWarnings("serial")
public class NoSuchMessageException extends RuntimeException {

	public NoSuchMessageException(String code, Locale locale) {
		super("No message found under code '" + code + "' for locale '" + locale + "'.");
	}

	public NoSuchMessageException(String code) {
		super("No message found under code '" + code + "' for locale '" + Locale.getDefault() + "'.");
	}

}