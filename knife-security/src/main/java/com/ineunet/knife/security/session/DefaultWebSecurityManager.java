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

import javax.servlet.ServletRequest;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.WebSubject;

/**
 * To resolve NullPointerException at org.apache.catalina.connector.Request.notifyAttributeAssigned(Request.java:1563)
 * @author Hilbert Wang
 * @since 2.0.2
 * Created on 2015-6-11
 */
public class DefaultWebSecurityManager extends org.apache.shiro.web.mgt.DefaultWebSecurityManager {
	
	protected void removeRequestIdentity(Subject subject) {
        if (subject instanceof WebSubject) {
            WebSubject webSubject = (WebSubject) subject;
            ServletRequest request = webSubject.getServletRequest();
            if (request != null) {
            	// TODO
//                try {
//                	request.setAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY, Boolean.TRUE);
//                } catch (Exception e) {
//                	e.printStackTrace();
//                }
            }
        }
    }

}
