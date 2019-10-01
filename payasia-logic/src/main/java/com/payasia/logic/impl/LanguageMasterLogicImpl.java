package com.payasia.logic.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.payasia.common.dto.LanguageListDTO;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.bean.LanguageMaster;
import com.payasia.logic.LanguageMasterLogic;

/**
 * The Class LanguageMasterLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class LanguageMasterLogicImpl implements LanguageMasterLogic {

	/** The language master dao. */
	@Resource
	LanguageMasterDAO languageMasterDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LanguageMasterLogic#getLanguages()
	 */
	@Override
	public List<LanguageListDTO> getLanguages() {

		List<LanguageMaster> languageMasterList = languageMasterDAO.getLanguages();

		List<LanguageListDTO> languageList;

		NullAwareBeanUtilsBean nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();
		languageList = nullAwareBeanUtilsBean.copyList(LanguageListDTO.class, languageMasterList);

		return languageList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LanguageMasterLogic#getDefaultLanguageCode()
	 */
	@Override
	public String getDefaultLanguageCode() {

		LanguageMaster languageMasterVO = languageMasterDAO.getDefaultLanguage();
		if (languageMasterVO != null) {
			String languageCode = languageMasterVO.getLanguageCode();
			return languageCode;
		}
		return null;

	}

	/*
	 * ADDED FOR NEW API - TO GET LANGUAGE BY LANUAGE CODE
	 */
	@Override
	public Long getLanguageByCode(String languageCode) {

		LanguageMaster languageMasterVO = languageMasterDAO.findByLanguageCode(languageCode);
		if (languageMasterVO != null) {
			Long languageId = languageMasterVO.getLanguageId();
			return languageId;
		}
		return null;
	}

}
