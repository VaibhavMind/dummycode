package com.payasia.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.payasia.common.util.PayAsiaConstants;
import com.payasia.web.util.URLUtils;

public class PayAsiaAuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {

	@Resource
	URLUtils urlUtils;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse, Authentication authentication)
			throws IOException {
		String employeeCompanyCode = urlUtils.getDefaultCompanyCode(httpRequest);

		urlUtils.addCookie(PayAsiaConstants.EMPLOYEE_COMPANY_COOKIE_NAME,
				employeeCompanyCode, httpRequest, httpResponse);

		logger.info(">>> Added cookie : "
				+ PayAsiaConstants.EMPLOYEE_COMPANY_COOKIE_NAME);

		if (StringUtils.isNotBlank(employeeCompanyCode)) {
			httpResponse.sendRedirect(urlUtils.getContextPath(httpRequest)
					+ "/portal/" + employeeCompanyCode + "/employeeHome.html");
		}
	}

}
