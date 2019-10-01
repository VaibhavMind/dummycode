package com.payasia.api.hris.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.EmployeeContactUsApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.CaptchaUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.EmployeeContactUSForm;
import com.payasia.logic.EmployeeContactUSLogic;

/**
 * @author gauravkumar
 * @param :This class used for Contact-Us APIs
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class EmployeeContactUsApiImpl implements EmployeeContactUsApi {

	@Resource
	private EmployeeContactUSLogic employeeContactUSLogic;
	
	@Autowired
	private MessageSource messageSource;

	@Override
	@PostMapping(value = "send-mail")
	public ResponseEntity<?> sendMail(@RequestBody EmployeeContactUSForm employeeContactUSForm) {
		final Long employeeId = Long.valueOf(UserContext.getUserId());
	     
		boolean challange = new CaptchaUtils().validateCaptcha(employeeContactUSForm.getCaptchaId(),employeeContactUSForm.getTuring());
		if (!challange) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.invalid.captcha", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}

		String mailstatus = employeeContactUSLogic.sendMail(employeeContactUSForm, employeeId);
		    
		if("payasia.success".equalsIgnoreCase(mailstatus)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(mailstatus, new Object[] {}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(mailstatus, new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);

	}

}
