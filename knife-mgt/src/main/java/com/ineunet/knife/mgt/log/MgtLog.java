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
package com.ineunet.knife.mgt.log;

/**
 * 
 * @author Hilbert
 * 
 * @since 1.1.0
 * 
 */
public class MgtLog {

	private static final String separator = " ";
	private String level;
	private String operator;
	private String time;
	private String classname;
	private int line;
	private String message;

	/**
	 * level: info, debug, warning, error, fatal 
	 * @return
	 */
	public String getLevel() {
		return level;
	}

	public MgtLog setLevel(String level) {
		this.level = level;
		return this;
	}

	/**
	 * If someone login who he is the operator
	 * @return operator
	 */
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return log time
	 */
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return name of class which keep the error or info
	 */
	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	/**
	 * @return log message
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return line number of exception
	 */
	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
	
	public String[] toArray() {
		return new String[] {'[' + level + ']', time, operator, message, classname, String.valueOf(line)};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(level).append(']');
		sb.append(separator).append(time);
		sb.append(separator).append(operator);
		sb.append(separator).append(message);
		sb.append(separator).append('(').append(classname).append(".java:").append(line).append(')');
		sb.append("\n");
		return sb.toString();
	}

}
