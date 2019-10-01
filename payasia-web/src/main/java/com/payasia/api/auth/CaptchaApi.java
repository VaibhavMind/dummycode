package com.payasia.api.auth;

import org.springframework.http.ResponseEntity;

import com.payasia.common.form.ForgotPasswordForm;

import io.swagger.annotations.Api;
import net.sf.json.JSONException;

@Api(tags="LOGIN", description="LOGIN related APIs")
public interface CaptchaApi {

	ResponseEntity<?> getCaptchaImg() throws JSONException;

	ResponseEntity<?> reloadCaptchaImage(String previousCaptchaId);

	ResponseEntity<?> challangeCaptcha(ForgotPasswordForm forgotPasswordForm);

}
