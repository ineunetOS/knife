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
package com.ineunet.knife.security.session;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.CollectionUtils;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.1
 * 
 */
public class MemorySessionDAO extends AbstractSessionDAO {
	
	private ConcurrentMap<Serializable, Session> sessions;
	
	public MemorySessionDAO() {
		sessions = new ConcurrentHashMap<>();
	}

	public Serializable create(Session session) {
		Serializable sessionId = doCreate(session);
		return sessionId;
	}

	protected Serializable doCreate(Session session) {
		storeSession(session.getId(), session);
		return session.getId();
	}

	protected Session storeSession(Serializable id, Session session) {
		if (id == null) {
			throw new NullPointerException("id argument cannot be null.");
		}
		return sessions.putIfAbsent(id, session);
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		storeSession(session.getId(), session);
	}

	@Override
	public void delete(Session session) {
		if (session == null) {
            throw new NullPointerException("session argument cannot be null.");
        }
        Serializable id = session.getId();
        if (id != null) {
            sessions.remove(id);
        }
	}

	@Override
	public Collection<Session> getActiveSessions() {
		Collection<Session> values = sessions.values();
        if (CollectionUtils.isEmpty(values)) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableCollection(values);
        }
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		return sessions.get(sessionId);
	}

}
