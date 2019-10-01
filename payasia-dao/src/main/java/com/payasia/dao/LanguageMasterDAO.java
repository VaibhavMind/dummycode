package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LanguageMaster;

/**
 * The Interface LanguageMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface LanguageMasterDAO {

	/**
	 * Find LanguageMaster Object by language code.
	 * 
	 * @param lanCode
	 *            the lan code
	 * @return the language master
	 */
	LanguageMaster findByLanguageCode(String lanCode);

	/**
	 * Gets the LanguageMaster Objects List.
	 * 
	 * @return the languages
	 */
	List<LanguageMaster> getLanguages();

	/**
	 * Find LanguageMaster Object by languageId.
	 * 
	 * @param languageId
	 *            the language id
	 * @return the language master
	 */
	LanguageMaster findById(Long languageId);

	LanguageMaster getDefaultLanguage();

}
