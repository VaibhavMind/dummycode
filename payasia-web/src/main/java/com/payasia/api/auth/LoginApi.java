package com.payasia.api.auth;

import org.springframework.http.ResponseEntity;

import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.ForgotPasswordForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="LOGIN", description="LOGIN related APIs")
public interface LoginApi extends SwaggerTags {

	ResponseEntity<?> getCompanyLogo(String companyCode);

	ResponseEntity<?> getLoginPageBackgroundImg(String companyCode);

	ResponseEntity<?> doForgetPassword(ForgotPasswordForm forgotPasswordForm);

	ResponseEntity<?> getloginproblem(String companyCode);

	@ApiOperation(value = "Display Customer Testimonial", notes = "Customer Testimonial data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> customertestimonial(String companyCode);

	@ApiOperation(value = "Change Password", notes = "Password can be reset using this API.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> resetPassword(String jsonStr);

	ResponseEntity<?> passwordPoliciesDetails(String companyCode);
	
	ResponseEntity<?> resetForgotPassword(String jsonData);

	ResponseEntity<?> showLanguageData();

}
