package com.payasia.common.dto;

import java.io.Serializable;

public class OTItemMasterConditionDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1153737323957973126L;
	private String name;
	private String code;
	private String visibleOrHidden;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getVisibleOrHidden() {
		return visibleOrHidden;
	}
	public void setVisibleOrHidden(String visibleOrHidden) {
		this.visibleOrHidden = visibleOrHidden;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
