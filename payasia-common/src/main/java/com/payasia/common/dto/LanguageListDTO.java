package com.payasia.common.dto;

import java.io.Serializable;

public class LanguageListDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String language;
	private String languageCode;
	private Boolean defaultLang;
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public Boolean getDefaultLang() {
		return defaultLang;
	}
	public void setDefaultLang(Boolean defaultLang) {
		this.defaultLang = defaultLang;
	}

}
