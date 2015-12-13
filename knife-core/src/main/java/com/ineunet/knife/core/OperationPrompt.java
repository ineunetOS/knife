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
package com.ineunet.knife.core;

/**
 * 
 * @author Hilbert Wang
 * 
 * @since 1.0.0
 *
 */
public class OperationPrompt {
	// default true
	private boolean success = true;
	private String msg;
	private Object id;
	private boolean create;
	
	/**
	 * @since 2.0.2
	 */
	private boolean submited = false;
	
	public OperationPrompt() {
	}
	
	public OperationPrompt(String msg) {
		this.msg = msg;
	}
	
	public OperationPrompt(boolean success) {
		this(null, success);
	}
	
	public OperationPrompt(String msg, boolean success) {
		this.success = success;
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	/**
	 * @return whether the submit is a create or a update operation.
	 * @since 1.1.1
	 */
	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
	}

	/**
	 * @return true continue to execute submit. false break;
	 * @since 2.0.2
	 */
	public boolean isSubmited() {
		return submited;
	}

	public void setSubmited(boolean submited) {
		this.submited = submited;
	}

}
