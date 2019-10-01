package com.payasia.api.auth.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.auth.SsoConfigStatusApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.dto.SsoConfigurationDTO;
import com.payasia.logic.SsoConfigurationLogic;

/**
 * @author gauravkumar
 * @param : This class used to check SSO Enable status
*/
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_COMMON)
public class SsoConfigStatusApiImpl implements SsoConfigStatusApi{

	@Autowired
	private SsoConfigurationLogic ssoConfigurationLogic;
	
	@Override
	@GetMapping(value = "sso-status")
	public ResponseEntity<?> getSsoStatus(@RequestParam (required = true) String companyCode){
		SsoConfigurationDTO ssoConfigurationDTO = ssoConfigurationLogic.getSsoConfigByCompCodeWithGroup(companyCode);
		return new ResponseEntity<>(ssoConfigurationDTO, HttpStatus.OK);
	}
}
