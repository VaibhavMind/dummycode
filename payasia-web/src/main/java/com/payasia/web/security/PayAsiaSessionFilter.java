package com.payasia.web.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.payasia.web.util.URLUtils;

public class PayAsiaSessionFilter implements Filter, InitializingBean {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger(PayAsiaSessionFilter.class);

	private String expiredUrl;

	@Resource
	URLUtils urlUtils;

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest hReq = (HttpServletRequest) req;
			HttpServletResponse hRes = (HttpServletResponse) res;
			HttpSession session = hReq.getSession(false);

			if (session == null
					&& StringUtils.isNotEmpty(urlUtils.getRefererURL(hReq))
					&& hReq.getRequestedSessionId() != null
					&& !hReq.isRequestedSessionIdValid()
					&& !(hReq.getRequestURI().indexOf("login.html") > 0)
					&& !(hReq.getRequestURI().indexOf("logout") > 0)
					&& !(hReq.getRequestURI().indexOf("sessionExpired.html") > 0)) {

				String urlWithCompanyCode = urlUtils.getSessionExpiryURL(hReq,
						expiredUrl);

				LOGGER.info(">>>> Session Id: " + hReq.getRequestedSessionId());
				LOGGER.info(">>>> Valid Session: "
						+ hReq.isRequestedSessionIdValid());
				LOGGER.info(">>>>Session expired........... Redirecting to login page :: "
						+ urlWithCompanyCode);

				hRes.sendRedirect(hRes.encodeRedirectURL(urlWithCompanyCode));
				return;
			}
			chain.doFilter(req, res);
		}
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void afterPropertiesSet() throws Exception {
		LOGGER.info("Entry: afterPropertiesSet");
		Assert.hasText(expiredUrl, "ExpiredUrl Required");
		LOGGER.info("Exit: afterPropertiesSet");
	}

	public void setExpiredUrl(String expiredUrl) {
		this.expiredUrl = expiredUrl;
	}

}