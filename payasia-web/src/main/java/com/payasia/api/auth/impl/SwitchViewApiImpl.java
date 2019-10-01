package com.payasia.api.auth.impl;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.auth.SwitchViewApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.util.CryptoUtil;

import io.swagger.annotations.Api;
import net.sf.json.JSONException;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH + "/switch", produces = "application/json")
@Api(tags = "COMMON")
public class SwitchViewApiImpl implements SwitchViewApi {

	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private DefaultTokenServices defaultTokenServices;

	@PostMapping(value = "view")
	@Override
	public ResponseEntity<?> doSwitch() throws JSONException {
		final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> additionalInfo = tokenStore.getAccessToken(auth).getAdditionalInformation();
		String employeeInfo = (String) additionalInfo.get("userData");
		String decryptData = CryptoUtil.decrypt(employeeInfo, CryptoUtil.SECRET_KEY);
		employeeInfo = new String(Base64.encodeBase64(decryptData.getBytes()));
		final String token = tokenStore.getAccessToken(auth).getValue();
		defaultTokenServices.revokeToken(token);
		return new ResponseEntity<>(employeeInfo, HttpStatus.OK);
	}

}
