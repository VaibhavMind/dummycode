package com.payasia.common.dto;

import java.io.Serializable;

public class PasswordPolicyDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7117662035666548378L;
	
	private String message;
	private Integer msgSrNum;
	private Integer value;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getMsgSrNum() {
		return msgSrNum;
	}
	public void setMsgSrNum(Integer msgSrNum) {
		this.msgSrNum = msgSrNum;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	

}
