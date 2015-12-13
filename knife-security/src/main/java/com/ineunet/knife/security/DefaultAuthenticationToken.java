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
package com.ineunet.knife.security;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * ..%
 * @author Hilbert
 *
 */
public class DefaultAuthenticationToken implements HostAuthenticationToken, RememberMeAuthenticationToken {

	private static final long serialVersionUID = 689958372064805658L;

	/**
     * The account
     */
    private String jid;

    /**
     * The password, in char[] format
     */
    private char[] password;

    /**
     * Whether or not 'rememberMe' should be enabled for the corresponding login attempt;
     * default is <code>false</code>
     */
    private boolean rememberMe = false;

    /**
     * The location from where the login attempt occurs, or <code>null</code> if not known or explicitly
     * omitted.
     */
    private String host;
    
	@Override
	public Object getPrincipal() {
		return jid;
	}

	@Override
	public Object getCredentials() {
		return password;
	}

	@Override
	public boolean isRememberMe() {
		return rememberMe;
	}

	@Override
	public String getHost() {
		return host;
	}
	
	/**
     * Clears out (nulls) the jid, password, rememberMe, and inetAddress.  The password bytes are explicitly set to
     * <tt>0x00</tt> before nulling to eliminate the possibility of memory access at a later time.
     */
    public void clear() {
        this.jid = null;
        this.host = null;
        this.rememberMe = false;

        if (this.password != null) {
            for (int i = 0; i < password.length; i++) {
                this.password[i] = 0x00;
            }
            this.password = null;
        }

    }

}
