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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.ValidatingSessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.session.HttpServletSession;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.ineunet.knife.security.Server;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.2.1
 * 
 */
public class DefaultWebSessionManager extends ServletContainerSessionManager implements ValidatingSessionManager,
		InitializingBean {
	static final Logger log = LoggerFactory.getLogger(DefaultWebSessionManager.class);
	SessionDAO sessionDAO;
	long globalSessionTimeout = 3600000;// MS
	boolean sessionValidationSchedulerEnabled;
	SessionValidationScheduler sessionValidationScheduler;

	private Cookie sessionIdCookie;
	private boolean sessionIdCookieEnabled;
	
	public DefaultWebSessionManager() {
		this.sessionIdCookieEnabled = true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (sessionValidationSchedulerEnabled) {
			sessionValidationScheduler.enableSessionValidation();
		}
	}

	protected Session createSession(HttpSession httpSession, String host) {
		Session session = new HttpServletSession(httpSession, host);
		this.sessionDAO.create(session);
		return session;
	}
	
	protected void onChange(Session session) {
        sessionDAO.update(session);
    }

	public SessionDAO getSessionDAO() {
		return sessionDAO;
	}

	public void setSessionDAO(SessionDAO sessionDAO) {
		this.sessionDAO = sessionDAO;
	}

	public SessionValidationScheduler getSessionValidationScheduler() {
		return sessionValidationScheduler;
	}

	public void setSessionValidationScheduler(SessionValidationScheduler sessionValidationScheduler) {
		this.sessionValidationScheduler = sessionValidationScheduler;
	}

	public Cookie getSessionIdCookie() {
		return sessionIdCookie;
	}

	public void setSessionIdCookie(Cookie sessionIdCookie) {
		this.sessionIdCookie = sessionIdCookie;
	}

	public boolean isSessionIdCookieEnabled() {
		return sessionIdCookieEnabled;
	}

	public void setSessionIdCookieEnabled(boolean sessionIdCookieEnabled) {
		this.sessionIdCookieEnabled = sessionIdCookieEnabled;
	}

	@Override
	public void validateSessions() {
		DefaultWebSessionManager.log.info("Validating all active sessions...");
		Collection<Session> activeSessions = getActiveSessions();
		if (activeSessions != null && !activeSessions.isEmpty()) {
			for (Session s : activeSessions) {
				try {
					this.validate(s);
				} catch (InvalidSessionException e) {
					if (DefaultWebSessionManager.log.isDebugEnabled()) {
						boolean expired = (e instanceof ExpiredSessionException);
						String msg = "Invalidated session with id [" + s.getId() + "]" + (expired ? " (expired)" : " (stopped)");
						DefaultWebSessionManager.log.debug(msg);
					}
				}
			}
		}
	}

	/**
	 * can be override by childClass
	 */
	protected void validate(Session session) {
		try {
			Date lastAccessTime = session.getLastAccessTime();
			if(System.currentTimeMillis() - lastAccessTime.getTime() > globalSessionTimeout) {
				Server.logout(session);
				sessionDAO.delete(session);
			}
		} catch (Exception e) {
			// program step here when session deprecated
			// log.error("validateSessions error.", e);
			sessionDAO.delete(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Collection<Session> getActiveSessions() {
		if (this.sessionDAO == null)
			return Collections.EMPTY_LIST;
		else
			return this.sessionDAO.getActiveSessions();
	}

	public boolean isSessionValidationSchedulerEnabled() {
		return sessionValidationSchedulerEnabled;
	}

	public void setSessionValidationSchedulerEnabled(boolean sessionValidationSchedulerEnabled) {
		this.sessionValidationSchedulerEnabled = sessionValidationSchedulerEnabled;
	}

	public long getGlobalSessionTimeout() {
		return globalSessionTimeout;
	}

	public void setGlobalSessionTimeout(long globalSessionTimeout) {
		this.globalSessionTimeout = globalSessionTimeout;
	}

}
