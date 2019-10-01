package com.payasia.web.controller.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.LanguageListDTO;
import com.payasia.logic.LanguageMasterLogic;
import com.payasia.web.controller.LanguageMasterController;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * The Class LanguageMasterControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = { "/admin/Language", "/employee/Language" })
public class LanguageMasterControllerImpl implements LanguageMasterController {

	/** The language master logic. */
	@Resource
	LanguageMasterLogic languageMasterLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LanguageMasterController#getLanguage()
	 */
	@Override
	@RequestMapping(value = "/getLanguage.html", method = RequestMethod.POST)
	public @ResponseBody
	String getLanguage() {
		List<LanguageListDTO> languageList = languageMasterLogic.getLanguages();
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(languageList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LanguageMasterController#getDefaultLanguageCode
	 * ()
	 */
	@Override
	@RequestMapping(value = "/getDefaultLangCode.html", method = RequestMethod.POST)
	public @ResponseBody
	String getDefaultLanguageCode() {
		String defaultLanguagecode = languageMasterLogic
				.getDefaultLanguageCode();
		return defaultLanguagecode;

	}

}
