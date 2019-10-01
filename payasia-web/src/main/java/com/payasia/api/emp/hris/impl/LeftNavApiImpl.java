package com.payasia.api.emp.hris.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.emp.hris.LeftNavApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.PrivilegeUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.Menu;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.LoginLogic;

import io.swagger.annotations.Api;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_COMMON)
@Api(tags = "COMMON")
public class LeftNavApiImpl implements LeftNavApi {

	@Resource
	private LoginLogic loginLogic;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private PrivilegeUtils privilegeUtils;

	@SuppressWarnings("unchecked")
	@PostMapping(value = "/user-menus")
	@Override
	public ResponseEntity<?> getUserMenus(@RequestBody String params) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject obj = JSONObject.fromObject(params, jsonConfig);
		String role = obj.getString("role");
		Map<String, Object> map = new HashedMap();

		Employee employee = new Employee();
		employee.setEmployeeId(employeeId);
		Company company = new Company();
		company.setCompanyId(companyId);
	
		Set<Menu> menus = null;
		Long workingCompanyID = Long.valueOf(UserContext.getWorkingCompanyId());
		Long clientAdminID = UserContext.getClientAdminId();
	
		if(workingCompanyID.equals(clientAdminID)){
			menus = loginLogic.getUserPrivilege(employeeId, companyId, role);
		}else {
			menus = new HashSet<>();
		}
		map.put("menus", menus);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/user-privilege")
	@Override
	public ResponseEntity<?> getUserPrivilege() {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());

		Map<String, Object> map = new HashedMap();

		Employee employee = new Employee();
		employee.setEmployeeId(employeeId);
		Company company = new Company();
		company.setCompanyId(companyId);
		
		List<GrantedAuthority> list = loginLogic.getUserPrivilege(employee, company);
		List<String> roleList = new ArrayList<>();
		List<String> moduleList = new ArrayList<>();
		List<String> privList = new ArrayList<>();
		
		for(GrantedAuthority authority : list) {

			if (authority.getAuthority().startsWith("ROLE_")) {
				roleList.add(authority.getAuthority());

			}
			if (authority.getAuthority().startsWith("MODULE_")) {
				moduleList.add(authority.getAuthority());

			}
			if (authority.getAuthority().startsWith("PRIV_")) {
				privList.add(authority.getAuthority());
			}

		}
		map.put("ROLE", roleList);
		map.put("MODULE", moduleList);
		map.put("PRIV", privList);
		map.put("dateFormat",UserContext.getWorkingCompanyDateFormat());
		
		if(privilegeUtils.getModule()==null || privilegeUtils.getRole()==null || privilegeUtils.getPrivilege()==null){
			setUserPrivilege(moduleList,roleList,privList);
		}
		
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	
	@PostMapping(value = "/token-valid")
	@Override
	public ResponseEntity<?> tokenValid() {
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	private void setUserPrivilege(List<String> moduleList,List<String> roleList,List<String> privList){
			final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
			OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
			Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
			additionalInfo.put("module", moduleList);
			additionalInfo.put("role", roleList);
			additionalInfo.put("privilege", privList);
			DefaultOAuth2AccessToken accessTokenWithUser = (DefaultOAuth2AccessToken) accessToken;
			accessTokenWithUser.setAdditionalInformation(additionalInfo);
		
	}
}
