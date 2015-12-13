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

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.Mongo;
import org.jongo.Find;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 *
 */
public class MongoDBTemplate implements MongoDBOperations {

    private MongoCollection mc;

    public MongoDBTemplate(String name) throws Exception {
        Mongo mongo = MongoManager.getMongoDBServer().getMongo();
        Jongo jongo = new Jongo(mongo.getDB(MongoManager.getMongoDBServer().getDefaultDbName()));
        this.mc = jongo.getCollection(name);
    }

    @Override
    public <T> T findOne(String query, Class<T> clazz) throws Exception {
        return this.mc.findOne(query).as(clazz);
    }

    @Override
    public <T> T findOne(Class<T> clazz, String query, Object... parameters) throws Exception {
        return this.mc.findOne(query, parameters).as(clazz);
    }

    @Override
    public <T> Iterable<T> find(Class<T> clazz, String query, String sort) throws Exception {
        Find find;
        if( null == query ){
            find = this.mc.find();
        }else{
            find = this.mc.find(query);
        }
        if (null != sort) {
            return find.sort(sort).as(clazz);
        } else {
            return find.as(clazz);
        }
    }

    @Override
    public <T> Iterable<T> find(Class<T> clazz, String query, String sort, int skip, int max) throws Exception {
        Find find;
        if( null == query ){
            find = this.mc.find();
        }else{
            find = this.mc.find(query);
        }        
        if (null != sort) {
            return find.sort(sort).skip(skip).limit(max).as(clazz);
        } else {
            return find.skip(skip).limit(max).as(clazz);
        }
    }

    @Override
    public <T> Iterable<T> find(Class<T> clazz, String query, String sort, Object... parameters) throws Exception {
        Find find;
        if( null == query ){
            find = this.mc.find();
        }else{
            find = this.mc.find(query,parameters);
        }        
        if (null != sort) {
            return find.sort(sort).as(clazz);
        } else {
            return find.as(clazz);
        }
    }

    @Override
    public <T> Iterable<T> find(Class<T> clazz, String query, String sort, int skip, int max, Object... parameters) throws Exception {
        Find find;
        if( null == query ){
            find = this.mc.find();
        }else{
            find = this.mc.find(query, parameters);
        }        
        if (null != sort) {
            return find.sort(sort).skip(skip).limit(max).as(clazz);
        } else {
            return find.skip(skip).limit(max).as(clazz);
        }
    }

    @Override
    public int save(Object object) throws Exception {
        return this.mc.save(object).getN();
    }

    @Override
    public int update(String query) throws Exception {
        return this.mc.save(query).getN();
    }

    @Override
    public int update(String query, Object[] queryParams, String setQuery, Object[] setParams) throws Exception {
        return this.mc.update(query, queryParams).upsert().with(setQuery, setParams).getN();
    }

    @Override
    public int update(String query, Object queryParam, String setQuery, Object setParam) throws Exception {
        return this.mc.update(query, queryParam).upsert().with(setQuery, setParam).getN();
    }

    @Override
    public int updateMulti(String query, Object[] queryParams, String setQuery, Object[] setParams) throws Exception {
        return this.mc.update(query, queryParams).upsert().multi().with(setQuery, setParams).getN();
    }

    @Override
    public int delete(String query) throws Exception {
        return this.mc.remove(query).getN();
    }

    @Override
    public int delete(String query, Object... parameters) throws Exception {
        return this.mc.remove(query, parameters).getN();
    }

    @Override
    public int delete(ObjectId id) throws Exception {
        return this.mc.remove(id).getN();
    }

    @Override
    public void drop() throws Exception {
        this.mc.drop();
    }

    @Override
    public MongoCollection getMongoCollection() {
        return this.mc;
    }

    @Override
    public long count(String query, Object... parameters) throws Exception {
        if( null == query ){
            return mc.count();
        }else{
            if(parameters == null ){
                return mc.count(query);
            }else{
                return mc.count(query, parameters);
            }
        }
    }
}