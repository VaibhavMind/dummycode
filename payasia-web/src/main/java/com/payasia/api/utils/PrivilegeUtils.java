package com.payasia.api.utils;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeUtils {
	
	@Autowired
	private TokenStore tokenStore;
	
	@SuppressWarnings("unchecked")
	public List<String> getModule(){
		 List<String> moduleList =  (List<String>) getAdditionalInformation().get("module");
		 return moduleList;
	}

	@SuppressWarnings("unchecked")
	public List<String> getRole(){
		 List<String> roleList =  (List<String>) getAdditionalInformation().get("role");
		 return roleList;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getPrivilege(){
		 List<String> privilegeList =  (List<String>) getAdditionalInformation().get("privilege");
		 return privilegeList;
	}
	
	private Map<String, Object> getAdditionalInformation(){
		final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
		return accessToken.getAdditionalInformation();
	}
	
}
