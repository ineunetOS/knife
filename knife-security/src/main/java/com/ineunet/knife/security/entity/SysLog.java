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
package com.ineunet.knife.security.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ineunet.knife.api.AbstractOrgEntity;
import com.ineunet.knife.api.sys.ISysLog;
import com.ineunet.knife.util.DateUtils;


/**
 * 
 * @author Hilbert
 * 
 * @since 2.0.0
 * 
 */
@Entity
@Table(name="knife_sys_log")
public class SysLog extends AbstractOrgEntity<Long> implements ISysLog {

	private static final long serialVersionUID = 6905686061402610980L;
	private String classify;// e.g. User Mgt
	private int level; // 1 generic(info), 2 warn, 3 illegal, 4 serious
	private String action;
	
	public SysLog() {
	}
	
	public SysLog(String classify, String action) {
		this();
		this.classify = classify;
		this.action = action;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	@Column(name = "log_action", length = 128)
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DateUtils.toStrDateTime_(this.getCreateTime()));
		sb.append(",").append(this.getCreatePerson());
		sb.append(",").append(action);
		return sb.toString();
	}

	@Column(name = "`classify`", length = 32)
	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	@Column(name = "log_level")
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
