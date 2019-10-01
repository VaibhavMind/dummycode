package com.payasia.api.auth;

import org.springframework.http.ResponseEntity;

import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import net.sf.json.JSONException;

@Api(tags="COMMON", description="COMMON related APIs")
public interface SwitchViewApi extends SwaggerTags{

	ResponseEntity<?> doSwitch() throws JSONException;

}
