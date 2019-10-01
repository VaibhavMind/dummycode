package com.payasia.api.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.PrivilegeUtils;

/**
 * @author manojkumar2
 * @param : This class used to check method level Privilege
*/
public class PrivilegeInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private PrivilegeUtils privilegeUtils;
	
	@Override 
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestURL = request.getRequestURI().substring(request.getContextPath().length());
		int lastIndexOf = requestURL.lastIndexOf("/");

		String reqUrl = requestURL.substring(1, lastIndexOf);
		
		if(reqUrl.endsWith(ApiUtils.MY_CLAIM)) {
			isUserPrivilege("PRIV_MY_CLAIM");
		} else if (reqUrl.endsWith(ApiUtils.PENDING_CLAIM)) {
			isUserPrivilege("PRIV_PENDING_CLAIM");
		} else if (reqUrl.endsWith(ApiUtils.EMPLOYEE_CLAIM_SUMMARY)) {
			isUserPrivilege("PRIV_EMPLOYEE_CLAIM_SUMMARY");
		} else if (reqUrl.endsWith("/payslip")) {
			isUserPrivilege("PRIV_INFO_PAY_SLIP");
		}
		return true;
	}

	private void isUserPrivilege(String privilegeName) {
		List<String> privilegeList =  privilegeUtils.getPrivilege();
		if(privilegeList!=null && !privilegeList.isEmpty()){
		 boolean isContains = privilegeList.contains(privilegeName);
		 if(!isContains) {
		  throw new AccessDeniedException("403");
		}
	   }
	}
}