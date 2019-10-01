package com.payasia.api.auth.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.auth.LoginApi;
import com.payasia.api.exception.AppRuntimeException;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.CaptchaUtils;
import com.payasia.api.utils.CompanyLogo;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LanguageListDTO;
import com.payasia.common.dto.MessageDTO;
import com.payasia.common.dto.PasswordPolicyDTO;
import com.payasia.common.form.ChangePasswordForm;
import com.payasia.common.form.ForgotPasswordForm;
import com.payasia.logic.EmployeeChangePasswordLogic;
import com.payasia.logic.LanguageMasterLogic;
import com.payasia.logic.LoginLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;


/**
 * @author manojkumar2
 * @param : This class used to build login page related API.
*/
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH+"/login",produces="application/json")
public class LoginApiImpl implements LoginApi {

	/** The login logic. */
	@Resource
	private LoginLogic loginLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private LanguageMasterLogic languageMasterLogic;
	
	@Resource
	private EmployeeChangePasswordLogic employeeChangePasswordLogic;
	
	@GetMapping(value = "company-logo")
	@Override
	public ResponseEntity<?> getCompanyLogo(@RequestParam(value = "companyCode", required = true) String companyCode) {
		String defaultCompLogoPath = "company/defaultLogo/default-company.png";
		byte[] byteFile = loginLogic.getLoginPageCompanyLogo(companyCode, defaultCompLogoPath);
		
		if(byteFile==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("login.company.logo", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}else{
			CompanyLogo jsonObject = new CompanyLogo();
			jsonObject.setImage(byteFile);
			return new ResponseEntity<>(jsonObject, HttpStatus.OK);
		}
	}

	// TODO : Change the logic of (Login page) background image company-wise
	@GetMapping(value = "login-background-image")
	@Override
	public ResponseEntity<?> getLoginPageBackgroundImg(@RequestParam(value = "companyCode", required = true) String companyCode) {
		
		byte[] byteFile = loginLogic.getDefaultLogoImage(companyCode);
		CompanyLogo jsonObject = new CompanyLogo();
		jsonObject.setImage(byteFile);
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@PostMapping(value = "recover-username-and-password")
	@Override
	public ResponseEntity<?> doForgetPassword(@RequestBody ForgotPasswordForm forgotPasswordForm) {

		boolean challange = false;
	
		if(forgotPasswordForm != null && StringUtils.isNotEmpty(forgotPasswordForm.getCaptchaId())
				&& StringUtils.isNotEmpty(forgotPasswordForm.getTuring())
				&& forgotPasswordForm.isDontKnowMyUsername()) {
			challange = new CaptchaUtils().validateCaptcha(forgotPasswordForm.getCaptchaId(),
					forgotPasswordForm.getTuring());
			if (!challange) {
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.invalid.captcha", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
			}
		}
		
		String statusMsg = loginLogic.sendForgotPasswdMail(forgotPasswordForm);
		if(statusMsg.equalsIgnoreCase("password.reset.link.sent") || statusMsg.equalsIgnoreCase("username.forgot.link.sent")){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(statusMsg, new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(statusMsg, new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	// TODO : send to mail id Payasia admin.
	@GetMapping(value = "login-problem")
	@Override
	public ResponseEntity<?> getloginproblem(@RequestParam(value = "companyCode", required = true) String companyCode) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("mailId", loginLogic.getEmailPreference(companyCode));
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	// TODO : remove dummy data logic.
	@GetMapping(value = "customer-testimonial")
	@Override
	public ResponseEntity<?> customertestimonial(@RequestParam(value = "companyCode", required = false) String companyCode) {

		JSONArray employees = new JSONArray();
		
		byte[] byteFile = loginLogic.getLoginPageCompanyLogo(companyCode, null);
		
//		employees.put(getTestimonialData("David Mendez", "HR Manager","Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",byteFile));
//		employees.put(getTestimonialData("Anil Kumar", "Manager","Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",byteFile));
		employees.put(getTestimonialData("Dr Tej Deol", "MD","When we started our multinational life sciences consulting firm, we wanted to partner with a cost effective and efficient firm which could rollout our payroll infrastructure across our subsidiaries in South East Asia. PayAsia has proven itself to be a very effective, innovative and responsive supplier of these services and we are grateful for the support they have provided our firm.",byteFile));
		return new ResponseEntity<Object>(employees.toString(), HttpStatus.OK);
	}
	
	// FOR MOBILE ONLY
	@PostMapping(value="reset-password")
	@Override
	public ResponseEntity<?> resetPassword(@RequestBody String jsonStr) {

		JsonConfig JsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, JsonConfig);
		String companyCode = jsonObj.getString("companyCode");
		String employeeNumber = jsonObj.getString("employeeNumber");
		
		String response = loginLogic.sendForgotPasswdMail(employeeNumber, companyCode);
			
		if("password.reset.link.sent".equalsIgnoreCase(response)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(response, new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		else{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(response, new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
	}

	// TODO : Testimonial Dummy Data
	private Map<String, String> getTestimonialData(String name, String designation, String description,
			byte[] byteFileImg) {
		Map<String, String> map = new HashMap<>();
		map.put("name", name);
		map.put("designation", designation);
		map.put("description", description);
		map.put("displayImg", byteFileImg.toString());
		return map;
	}
	
	@Override
	@PostMapping(value = "password-policies")
	public ResponseEntity<?> passwordPoliciesDetails(
			@RequestParam(value = "companyCode", required = false) String companyCode) {

		final Long companyId;

		if (!StringUtils.isEmpty(companyCode)){
			companyId = loginLogic.getCompanyId(companyCode);
		} else {
			companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		}
		
		ChangePasswordForm changePasswordForm = employeeChangePasswordLogic.getPassWordPolicyDetails(companyId);
		List<PasswordPolicyDTO> passwordPolicyDTOs = changePasswordForm.getPasswordPolicyDTO();
		List<PasswordPolicyDTO> passWordPolicyDetails = new ArrayList<>();
		Integer msgNumber = 1;
		for (PasswordPolicyDTO passwordPolicy : passwordPolicyDTOs) {
			PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
			if (passwordPolicy.getValue() != null) {
				passwordPolicyDTO.setMessage(messageSource.getMessage(passwordPolicy.getMessage(),new Object[] {passwordPolicy.getValue()}, UserContext.getLocale()));
			} else {
				passwordPolicyDTO.setMessage(messageSource.getMessage(passwordPolicy.getMessage(), new Object[]{},UserContext.getLocale()));
			}
			passwordPolicyDTO.setMsgSrNum(msgNumber);
			msgNumber++;
			passWordPolicyDetails.add(passwordPolicyDTO);

		}
		changePasswordForm.getPasswordPolicyDTO().clear();
		changePasswordForm.setPasswordPolicyDTO(passWordPolicyDetails);
		return new ResponseEntity<>(changePasswordForm, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "reset-forgot-password")
	@ResponseBody
	public ResponseEntity<?> resetForgotPassword(@RequestBody String jsonData) {
	
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(jsonData, jsonConfig);
		String username = jsonObject.getString("username");
		String newPassword = jsonObject.getString("newPassword");
		String token = jsonObject.getString("token");
		String companyCode = jsonObject.getString("companyCode");
		if(StringUtils.isEmpty(username)){
			new AppRuntimeException("User Name is required");
		}if(StringUtils.isEmpty(newPassword)){
			new AppRuntimeException("Password is required");
		}if(StringUtils.isEmpty(token)){
			new AppRuntimeException("Token is required");
		}if(StringUtils.isEmpty(companyCode)){
			new AppRuntimeException("Company Code is required");
		}
		Long companyId = loginLogic.getCompanyId(companyCode);
		newPassword = new String(Base64.decodeBase64(newPassword.getBytes()));
		MessageDTO msgDto = employeeChangePasswordLogic.resetForgotPassword(username, newPassword, token,companyId, null);
		if(msgDto.getKey().equalsIgnoreCase("new.passWord.has.been.changed")){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(msgDto.getKey(), new Object[] {}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(msgDto.getKey(), new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "language-list")
	public ResponseEntity<?> showLanguageData() {
		
		List<LanguageListDTO> languageList = languageMasterLogic.getLanguages();
		Map<String, Object> languageMap = new HashMap<>();
		languageMap.put("languageList", languageList);
		return new ResponseEntity<>(languageMap, HttpStatus.OK);
	}
	
}

