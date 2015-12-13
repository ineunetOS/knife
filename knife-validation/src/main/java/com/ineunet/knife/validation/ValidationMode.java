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
package com.ineunet.knife.validation;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.1
 * Created on 2015-3-19
 */
public enum ValidationMode {

	AUTO,
	
	MANUAL,
	
	/**
	 * For extensions. <br>
	 * e.g. Almost all props validating automatic, but one prop need some special validating.<br>
	 * e.g. Some properties of User use AUTO validation. but 'strExt1' to 'strExt9' use unknown validating restricts.
	 */
	ALL,
	
	NONE
	
}
