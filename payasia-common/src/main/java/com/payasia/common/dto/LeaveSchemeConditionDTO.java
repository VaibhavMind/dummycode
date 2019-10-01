/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;

public class LeaveSchemeConditionDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3228415372839473181L;
	private String schemeName;
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSchemeName() {
		return schemeName;
	}
	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
}
