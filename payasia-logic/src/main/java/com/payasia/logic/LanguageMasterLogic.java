package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.LanguageListDTO;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface LanguageMasterLogic.
 */
public interface LanguageMasterLogic {
	/**
	 * purpose : get Language list.
	 * 
	 * @return LanguageListDTO List
	 */
	List<LanguageListDTO> getLanguages();

	/**
	 * purpose : get Default LanguageCode.
	 * 
	 */
	String getDefaultLanguageCode();

	Long getLanguageByCode(String languageCode);

}
