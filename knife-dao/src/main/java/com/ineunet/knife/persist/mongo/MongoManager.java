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
package com.ineunet.knife.persist.mongo;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 *
 */
public class MongoManager {

	private static MongoDBServer mongoServer;

	private MongoManager() {
	}

	public static synchronized void setMongoDBServer(MongoDBServer dataSource) {
		MongoManager.mongoServer = dataSource;
	}

	/**
	 * get data connection
	 * 
	 * @return
	 * @throws Exception
	 */
	public static MongoDBServer getMongoDBServer() throws Exception {
		if (null == mongoServer) {
			mongoServer = new MongoDBServerImpl();
		}
		return mongoServer;
	}

}
