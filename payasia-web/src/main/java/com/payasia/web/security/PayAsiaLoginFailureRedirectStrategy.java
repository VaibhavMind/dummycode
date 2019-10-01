package com.payasia.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.web.RedirectStrategy;

import com.payasia.common.bean.util.UserContext;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.bean.Company;
import com.payasia.web.util.URLUtils;

public class PayAsiaLoginFailureRedirectStrategy implements RedirectStrategy {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(PayAsiaLoginFailureRedirectStrategy.class);

	@Resource
	URLUtils urlUtils;

	@Resource
	CompanyDAO companyDAO;

	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {

		// For SSO - redirect employee to default company login
		if (!UserContext.getUserId().equalsIgnoreCase("0")) {
			Long employeeID = Long.valueOf(UserContext.getUserId());
			Company cmp = companyDAO.findCompanyByEmpId(employeeID);
			String empLoginURL = urlUtils.getEmployeeFailedLoginURL(request, cmp.getCompanyCode());
			response.sendRedirect(empLoginURL);
		} else {
			// For Portal login failure
			String loginURLPrefix = urlUtils.getURLPrefix(request);
			String loginURL = loginURLPrefix + urlUtils.getContextPath(request) + url;
			String urlWithCompanyCode = urlUtils.getURLWithCompanyCode(loginURL, request);
			LOGGER.debug("Authentication Failure URL : " + urlWithCompanyCode);
			if (urlWithCompanyCode != null) {
				response.sendRedirect(urlWithCompanyCode);
			} else {
				response.sendRedirect(request.getContextPath() + "/portal/login.html?login_error=1");
			}
		}

	}

}