package com.payasia.web.controller;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface LanguageMasterController.
 */
public interface LanguageMasterController {

	/**
	 * purpose : get Default LanguageCode.
	 * 
	 */
	String getDefaultLanguageCode();

	/**
	 * purpose : get Language list.
	 * 
	 * @return LanguageListDTO List
	 */
	String getLanguage();

}
