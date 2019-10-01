package com.payasia.api.hris.impl;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.LanguageApi;
import com.payasia.api.oauth.UserData;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.JSONConverterUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.util.CryptoUtil;
import com.payasia.logic.LanguageMasterLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Language management
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE)
public class LanguageApiImpl implements LanguageApi {

	@Resource
	private LanguageMasterLogic languageMasterLogic;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private MessageSource messageSource;

	@Override
	@PutMapping(value = "change-locale")
	public ResponseEntity<?> setLocaleData(@RequestBody String jsonStr) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		String languageCode = jsonObj.getString("languageCode");
		Long languageId = languageMasterLogic.getLanguageByCode(languageCode);
		if(languageId==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
		
		Locale locale =  LocaleUtils.toLocale(languageCode);
		final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
		Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
	
		String employeeInfo = (String) additionalInfo.get("userData");
		String decryptData = CryptoUtil.decrypt(employeeInfo, CryptoUtil.SECRET_KEY);
		UserData userData = (UserData) JSONConverterUtils.jsonStringToObject(decryptData, UserData.class);
		userData.setLocale(locale);
		userData.setLanguageId(languageId);
		String json = JSONConverterUtils.objectToJsonString(userData);
		String encryptData = CryptoUtil.encrypt(json, CryptoUtil.SECRET_KEY);
		additionalInfo.put("userData", encryptData);
		DefaultOAuth2AccessToken accessTokenWithUser = (DefaultOAuth2AccessToken) accessToken;
		accessTokenWithUser.setAdditionalInformation(additionalInfo);
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.success", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.OK);
	}

}
