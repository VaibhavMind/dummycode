package com.payasia.common.dto;

import java.io.Serializable;

public class LanguageMasterDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7415529270620714740L;

	/** The language id. */
	private long languageId;

	/** The language Name. */
	private String language;

	/** The language Description. */
	private String languageDesc;

	public long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(long languageId) {
		this.languageId = languageId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguageDesc() {
		return languageDesc;
	}

	public void setLanguageDesc(String languageDesc) {
		this.languageDesc = languageDesc;
	}
}
