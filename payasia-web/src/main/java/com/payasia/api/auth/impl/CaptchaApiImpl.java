package com.payasia.api.auth.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.auth.CaptchaApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.CaptchaUtils;
import com.payasia.common.form.ForgotPasswordForm;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * @author manojkumar2
 * @param : This class build captcha generation logic.   
*/

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH + "/captcha",produces="application/json")
public class CaptchaApiImpl implements CaptchaApi{
	
	@GetMapping(value = "captcha-img")
	@Override
	public ResponseEntity<?> getCaptchaImg() throws JSONException {
		String[] captchaData = new CaptchaUtils().generateCaptchaImage(null);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("captchaId", captchaData[1]);
		jsonObject.put("captchaImg", captchaData[0]);
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
	
	@GetMapping(value="reload-captcha-img")
	@Override
	public  ResponseEntity<?> reloadCaptchaImage(@RequestParam("previousCaptchaId") String previousCaptchaId){
		String[] captchaData= new CaptchaUtils().generateCaptchaImage(previousCaptchaId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("captchaId", captchaData[1]);
		jsonObject.put("captchaImg", captchaData[0]);
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
	
	@PostMapping(value="validate-captcha")
	@Override
	public ResponseEntity<?> challangeCaptcha(@RequestBody ForgotPasswordForm forgotPasswordForm){
		
		boolean challange = false;
		if((forgotPasswordForm!=null && forgotPasswordForm.getTuring().trim().length()!=0) && (forgotPasswordForm!=null && forgotPasswordForm.getCaptchaId().trim().length()!=0)){
			challange = new CaptchaUtils().validateCaptcha(forgotPasswordForm.getCaptchaId(), forgotPasswordForm.getTuring());
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", challange?"success":"fail");
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}
	
	
}
