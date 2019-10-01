package com.payasia.saml.security;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.payasia.common.dto.EmployeeDTO;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaMobileConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.bean.Company;
import com.payasia.web.security.PayAsiaUserDetails;
import com.payasia.web.util.URLUtils;

/**
 * Automatically registers a user successfully authenticated with SAML SSO.
 */
public class PayAsiaSAMLAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private static Logger LOGGER = LoggerFactory.getLogger(PayAsiaSAMLAuthenticationSuccessHandler.class);

	@Resource
	URLUtils urlUtils;

	@Resource
	CompanyDAO companyDAO;
	
	@Autowired
	private ServletContext servletContext;
	
	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws ServletException, IOException {

		// Find Company_ID from userId
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
		PayAsiaUserDetails userDetails = (PayAsiaUserDetails) token.getPrincipal();
		long userId = userDetails.getUserId();
		Company company = companyDAO.findCompanyByEmpId(userId);
		String employeeCompanyCode = company.getCompanyCode();

		DeviceResolver deviceResolver = new LiteDeviceResolver();
		Device device = deviceResolver.resolveDevice(request);
		if(device != null) {
			LOGGER.info("User device desktop: " + device.isNormal());
			LOGGER.info("User device mobile: " + device.isMobile());
			LOGGER.info("User device tablet: " + device.isTablet());
		}else {
			LOGGER.info("Not able to identify user device");
		}
		
		if((device == null || device.isNormal()) &&  StringUtils.isNotBlank(employeeCompanyCode)) {
			// Add cookie only for portal
			urlUtils.addCookie(PayAsiaConstants.EMPLOYEE_COMPANY_COOKIE_NAME, employeeCompanyCode, request, response);
			LOGGER.info(">>> Added cookie : " + PayAsiaConstants.EMPLOYEE_COMPANY_COOKIE_NAME);
			
			String empHomeURL = urlUtils.getContextPath(request) + "/portal/" + employeeCompanyCode + "/employeeHome.html";
			response.sendRedirect(empHomeURL);
		}else if (device != null && (device.isMobile() || device.isTablet())) {
			LOGGER.info("Redirecting user to mobile url schema");
			
			String userLoginToken = userId	+ PasswordUtils.getRandomPassword(10);
			
			HashMap<String, EmployeeDTO> employeeInfo = (HashMap<String, EmployeeDTO>) servletContext.getAttribute(
							PayAsiaMobileConstants.MOBILE_TOKEN_KEY);
			if (employeeInfo == null) {
				employeeInfo = new HashMap<>();

			}
			EmployeeDTO employeeDTO = new EmployeeDTO();
			
			employeeInfo.put(userLoginToken, employeeDTO);

			servletContext.setAttribute(
					PayAsiaMobileConstants.MOBILE_TOKEN_KEY, employeeInfo);

			response.sendRedirect("payasia://?token="+userLoginToken);
		}
		
	}
}
