package com.payasia.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.payasia.web.util.URLUtils;

public class PayAsiaLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Resource
	URLUtils urlUtils;
	private static final Logger LOGGER = Logger
			.getLogger(PayAsiaLogoutSuccessHandler.class);
	public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String logoutURLPrefix = urlUtils.getLogoutURLPrefix(request);
		String contextPath = urlUtils.getContextPath(request);

		String logoutURL = logoutURLPrefix + contextPath + "/portal/login.html";

		String urlWithCompanyCode = urlUtils.getURLWithCompanyCode(logoutURL,
				request);

		LOGGER.debug("Logout URL : " + urlWithCompanyCode);

		if (authentication != null) {
			String username = (String) authentication.getName();
			request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME",
					username);
		}

		SecurityContextHolder.getContext().setAuthentication(null);

		if (StringUtils.isNotBlank(urlWithCompanyCode)) {
			response.sendRedirect(urlWithCompanyCode);
		} else {
			response.sendRedirect(request.getContextPath() + "/portal/"
					+ "login.html");
		}
	}
}
