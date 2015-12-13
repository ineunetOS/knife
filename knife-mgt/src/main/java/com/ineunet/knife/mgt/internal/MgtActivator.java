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
package com.ineunet.knife.mgt.internal;

import com.ineunet.knife.mgt.AbstractActivator;
import com.ineunet.knife.mgt.IBundleContext;
import com.ineunet.knife.mgt.stereotype.BundleActivator;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.0.5
 *
 */
@BundleActivator("mgtActivator")
class MgtActivator extends AbstractActivator {

	@Override
	protected void start(IBundleContext bundle) {
		logger.info("MgtActivator started.");
	}

	@Override
	protected void stop(IBundleContext bundle) {
		logger.info("MgtActivator stopped.");
	}

}
