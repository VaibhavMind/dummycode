package com.payasia.common.form;

import java.io.Serializable;

public class ShiftForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1787569557627677575L;
	private long shiftId;
	private String desc;
	private String name;
	
	
	public long getShiftId() {
		return shiftId;
	}
	public void setShiftId(long shiftId) {
		this.shiftId = shiftId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
