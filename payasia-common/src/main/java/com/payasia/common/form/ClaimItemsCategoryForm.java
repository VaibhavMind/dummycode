package com.payasia.common.form;

import java.io.Serializable;

public class ClaimItemsCategoryForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4609160050583435938L;
	private long categoryId;
	private String name;
	private String code;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

}
