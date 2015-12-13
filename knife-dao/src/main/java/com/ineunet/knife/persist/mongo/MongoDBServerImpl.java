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

import java.util.ArrayList;
import java.util.List;

import com.ineunet.knife.config.ConfigFactory;
import com.ineunet.knife.config.ConfigKeysDB;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 * 
 */
public class MongoDBServerImpl implements MongoDBServer {

	private final int defaultPort = 27017;
	private Mongo mongo;
	private String dbName;

	public MongoDBServerImpl() throws Exception {
		String url = ConfigFactory.getKnifeConfig().get(ConfigKeysDB.mongo_db_addr, "localhost");
		this.dbName = ConfigFactory.getKnifeConfig().get(ConfigKeysDB.mongo_db_defaultdb, "knife");
		String[] addrs = url.split(",");
		List<ServerAddress> sa = new ArrayList<ServerAddress>();
		for (String ad : addrs) {
			String[] tmp = ad.split(":");
			if (tmp.length == 1) {
				sa.add(new ServerAddress(tmp[0], defaultPort));
			} else {
				sa.add(new ServerAddress(tmp[0], Integer.parseInt(tmp[1])));
			}
		}

		this.mongo = new Mongo(sa);
		mongo.setWriteConcern(WriteConcern.SAFE);
	}

	@Override
	public Mongo getMongo() {
		return this.mongo;
	}

	@Override
	public String getDefaultDbName() {
		return this.dbName;
	}
}
