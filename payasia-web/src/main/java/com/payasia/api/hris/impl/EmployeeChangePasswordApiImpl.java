package com.payasia.api.hris.impl;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.EmployeeChangePasswordApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.MessageDTO;
import com.payasia.logic.EmployeeChangePasswordLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Change Password APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class EmployeeChangePasswordApiImpl implements EmployeeChangePasswordApi {

	@Resource
	private EmployeeChangePasswordLogic employeeChangePasswordLogic;

	@Autowired
	private MessageSource messageSource;

	@Override
	@PostMapping(value = "change-password")
	public ResponseEntity<?> changePassword(@RequestBody String jsonStr) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());

		JsonConfig JsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, JsonConfig);
		
		String oldPassword = jsonObj.getString("oldPassword");
		oldPassword = new String(Base64.decodeBase64(oldPassword.getBytes()));
		
		String newPassword = jsonObj.getString("newPassword");
		newPassword = new String(Base64.decodeBase64(newPassword.getBytes()));
		
		MessageDTO msgDto = employeeChangePasswordLogic.changePwdForFirstTimeLogin(employeeId, companyId, oldPassword, newPassword,UserContext.getIpAddress());
		if (msgDto.getKey().equalsIgnoreCase("new.passWord.has.been.changed")) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(msgDto.getKey(), new Object[] {msgDto.getArgs()}, UserContext.getLocale())), HttpStatus.OK);
		} else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(msgDto.getKey(), new Object[] {msgDto.getArgs()}, UserContext.getLocale())),HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@PostMapping(value = "change-password-after-login")
	public ResponseEntity<?> changePasswordAfterLogin(@RequestBody String jsonStr) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final String ipAddress = UserContext.getIpAddress();

		JsonConfig JsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, JsonConfig);
		String oldPassword = jsonObj.getString("oldPassword");
		String newPassword = jsonObj.getString("newPassword");
		MessageDTO msgDto = employeeChangePasswordLogic.changePwdForFirstTimeLogin(employeeId, companyId, oldPassword,newPassword, ipAddress);

		if (msgDto.getKey().equalsIgnoreCase("new.passWord.has.been.changed")) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(msgDto.getKey(), new Object[] {msgDto.getArgs()}, UserContext.getLocale())), HttpStatus.OK);
		} else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(msgDto.getKey(), new Object[] {msgDto.getArgs()}, UserContext.getLocale())),HttpStatus.NOT_FOUND);
		}
	}

}
