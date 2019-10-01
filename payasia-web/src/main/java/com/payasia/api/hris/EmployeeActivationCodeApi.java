package com.payasia.api.hris;

import org.springframework.http.ResponseEntity;

import io.swagger.annotations.Api;

@Api(tags="COMMON", description="COMMON related APIs")
public interface EmployeeActivationCodeApi {

	ResponseEntity<?> generateEmployeeActivationCode();

	ResponseEntity<?> submitEmployeeActivationCode(String jsonStr);

}
