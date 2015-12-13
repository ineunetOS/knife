package com.ineunet.knife.core.dataflow;

/**
 * 
 * @author Hilbert Wang
 * @since 2.0.2
 * Created on 2015-3-21
 */
public class NodeStatus {

	private String value;
	private String name;
	
	public NodeStatus() {}
	
	/**
	 * @param status
	 * @param statusName
	 */
	public NodeStatus(String status, String statusName) {
		this.value = status;
		this.name = statusName;
	}
	
	public NodeStatus(int status, String statusName) {
		this.value = String.valueOf(status);
		this.name = statusName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String status) {
		this.value = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String statusName) {
		this.name = statusName;
	}

}
