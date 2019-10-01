package com.payasia.api.auth.impl;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.CompanyForm;
import com.payasia.logic.SwitchCompanyLogic;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH, produces = "application/json")
@Api(tags="LOGIN", description="LOGIN related APIs")
public class LogoutApiImpl {

	@Autowired
	private DefaultTokenServices defaultTokenServices;

	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private SwitchCompanyLogic switchCompanyLogic;

	@DeleteMapping("/logout")
	public ResponseEntity<?> revokeToken() {
		final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		auth.setAuthenticated(false);
		auth.setDetails(null);
		final String token = tokenStore.getAccessToken(auth).getValue();
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		CompanyForm companyForm = switchCompanyLogic.getCompany(companyId);
		String companyCode = companyForm.getCompanyCode();
		defaultTokenServices.revokeToken(token);
		
		Map<String, Object> logMap = new HashMap<>();
		logMap.put("workingCompanyCode", companyCode);
		return new ResponseEntity<>(logMap, HttpStatus.OK);
	}
}