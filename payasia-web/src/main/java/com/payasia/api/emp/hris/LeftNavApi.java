package com.payasia.api.emp.hris;

import org.springframework.http.ResponseEntity;

public interface LeftNavApi {

	ResponseEntity<?> getUserMenus(String params);

	ResponseEntity<?> getUserPrivilege();

	ResponseEntity<?> tokenValid();

}
