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
package com.ineunet.knife.persist.internal;

import javax.annotation.Resource;

import com.ineunet.knife.config.ConfigFactory;
import com.ineunet.knife.config.ConfigKeysDB;
import com.ineunet.knife.mgt.AbstractActivator;
import com.ineunet.knife.mgt.IBundleContext;
import com.ineunet.knife.mgt.stereotype.BundleActivator;
import com.ineunet.knife.persist.PersistUtils;
import com.ineunet.knife.persist.mongo.MongoDBServer;
import com.ineunet.knife.persist.mongo.MongoDBServerImpl;
import com.ineunet.knife.persist.mongo.MongoManager;
import com.ineunet.knife.util.ReflectionUtils;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.0.5
 * 
 */
@BundleActivator("persistActivator")
class PersistActivator extends AbstractActivator {

	@Resource
	private PersistContext persistContext;
	
	@Override
	protected void start(IBundleContext bc) {
		
		ReflectionUtils.invokeStaticMethod(PersistUtils.class, "setPersistContext",
				new Class[] { PersistContext.class }, new PersistContext[] { persistContext });
		
		// mongodb
		if (ConfigFactory.getKnifeConfig().get(ConfigKeysDB.mongo_db_use, false)) {
			MongoDBServer mdb;
			try {
				mdb = new MongoDBServerImpl();
				MongoManager.setMongoDBServer(mdb);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected void stop(IBundleContext bc) {
	}

}
