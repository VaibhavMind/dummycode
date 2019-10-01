package com.payasia.api.dashboard;

import org.springframework.http.ResponseEntity;

import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.dto.HelpDeskDTO;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

public interface HelpDeskApi extends SwaggerTags{

	@ApiOperation(value = "Display guidelines", notes = "HelpDesk for providing guidelines.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> provideGuidelines(HelpDeskDTO helpDesk);

}
