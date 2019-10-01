package com.payasia.api.hris.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.EmployeeActivationCodeApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ActivationDTO;
import com.payasia.common.form.EmployeeHomePageForm;
import com.payasia.logic.EmployeeHomePageLogic;
import com.payasia.logic.LoginLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
public class EmployeeActivationCodeApiImpl implements EmployeeActivationCodeApi {

	@Resource
	private EmployeeHomePageLogic employeeHomePageLogic;

	@Resource
	private LoginLogic loginLogic;
	
	@Autowired
	private MessageSource messageSource;

	@Override
	@PostMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE + "/generate-activation-code")
	public ResponseEntity<?> generateEmployeeActivationCode() {
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic.generateEmployeeActivationCode(employeeId);
		return new ResponseEntity<>(employeeHomePageForm, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "/employee/submit-activation-code")
	public ResponseEntity<?> submitEmployeeActivationCode(@RequestBody String jsonStr) {
		JsonConfig JsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, JsonConfig);
		String activationCode = jsonObj.getString("activationCode");
		String userName = jsonObj.getString("userName");
		ActivationDTO activationDTO = loginLogic.getCompanyCode(activationCode, userName);
		if (activationDTO == null) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("user.activationcode.required", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(activationDTO, HttpStatus.OK);
	}

}
