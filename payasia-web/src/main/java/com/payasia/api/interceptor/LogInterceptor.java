package com.payasia.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.payasia.common.bean.util.UserContext;

/**
 * @author manojkumar2
 * @param : This class used to capture login information 
*/
public class LogInterceptor extends HandlerInterceptorAdapter {
	
	static final Log log = LogFactory.getLog("API_ACCESS_LOG");
	
	@Override 
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String requestURL = request.getRequestURI().substring(request.getContextPath().length());
		String queryString = request.getQueryString();
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		String loginID = UserContext.getLoginId()!=null?UserContext.getLoginId():"DEFAUL-USER";
		String companyCode = UserContext.getCompanyCode()!=null? UserContext.getCompanyCode():"DEFAULT-COMPANY-CODE";
		String requestHandler = null;
		String deviceName = UserContext.getDevice();
		
		if(ipAddress == null) {
			ipAddress = request.getRemoteAddr();			
		}
		
		if(queryString != null && !queryString.isEmpty()){
			requestHandler = requestURL + "?" + queryString;
		}else{
			requestHandler = requestURL;
		}
		
		StringBuilder loggerMessage = new StringBuilder();
		loggerMessage.append(ipAddress);
		loggerMessage.append("|");
		loggerMessage.append(deviceName);
		loggerMessage.append("|");
		loggerMessage.append(companyCode);
		loggerMessage.append("|");
		loggerMessage.append(loginID);
		loggerMessage.append("|");
		loggerMessage.append(requestHandler);
		loggerMessage.append("|");
		loggerMessage.append("PRE-HANDLER");
		log.info(loggerMessage.toString());
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		String requestURL = request.getRequestURI().substring(request.getContextPath().length());
		String queryString = request.getQueryString();
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		String loginID = UserContext.getLoginId()!=null?UserContext.getLoginId():"DEFAUL-USER";
		String companyCode = UserContext.getCompanyCode()!=null? UserContext.getCompanyCode():"DEFAULT-COMPANY-CODE";
		String requestHandler = null;
		String deviceName = UserContext.getDevice();
		
		if(ipAddress == null) {
			ipAddress = request.getRemoteAddr();			
		}
		if(queryString != null && !queryString.isEmpty()){
			requestHandler = requestURL + "?" + queryString;
		}else{
			requestHandler = requestURL;
		}
		StringBuilder loggerMessage = new StringBuilder();
		loggerMessage.append(ipAddress);
		loggerMessage.append("|");
		loggerMessage.append(deviceName);
		loggerMessage.append("|");
		loggerMessage.append(companyCode);
		loggerMessage.append("|");
		loggerMessage.append(loginID);
		loggerMessage.append("|");
		loggerMessage.append(requestHandler);
		loggerMessage.append("|");
		loggerMessage.append("POST-HANDLER");
		log.info(loggerMessage.toString());
		super.afterCompletion(request, response, handler, ex);
	}
}