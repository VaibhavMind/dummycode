package com.payasia.web.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.exception.PayAsiaPageNotFoundException;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.LoginLogic;

/**
 * Utility class for URL/URI operations.
 */
@Component
public class URLUtils {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(URLUtils.class);

	private static final int COOKIE_EXPIRATION_TIME = 604800;

	private static final int SUBDOMAIN_ARRAY_SIZE = 3;

	/** The login logic. */
	@Resource
	LoginLogic loginLogic;

	/**
	 * Gets the URL with company code.
	 * 
	 * @param argURLString
	 *            the request URL
	 * @param defaultCompanyCode
	 *            the default company code
	 * @return the URL with company code
	 */
	public String getURLWithCompanyCode(String argURLString, HttpServletRequest httpRequest) {

		LOGGER.debug("Request URL String :: " + argURLString);
		if (argURLString.indexOf(PayAsiaConstants.BASE_URI_PATH) == -1) {
			return null;
		}

		try {
			URL requestURL = new URL(argURLString);

			String companyCode = getSubDomain(requestURL);
			String urlString = requestURL.toExternalForm();
			String urlPrefix;
			String uri;

			if (urlString.contains(PayAsiaConstants.BASE_URI_PATH) && companyCode != null) {
				if (urlString.contains(PayAsiaConstants.BASE_URI_PATH + companyCode + "/")) {
					return null;
				} else {
					urlPrefix = urlString.substring(0, urlString.indexOf(PayAsiaConstants.BASE_URI_PATH));
					uri = PayAsiaConstants.BASE_URI_PATH + companyCode + "/"
							+ urlString.substring(urlString.lastIndexOf("/") + 1);

					uri = uri.replaceAll("//", "/");

					urlString = urlPrefix + uri;

					LOGGER.debug("New URL :: " + urlString);

					return urlString;
				}
			}

		} catch (MalformedURLException mue) {
			LOGGER.error(mue);
		}

		return null;
	}

	/**
	 * Gets the sub domain from URL if available.
	 * 
	 * @param url
	 *            the url
	 * @return the sub domain
	 */
	public String getSubDomain(URL url) {
		String[] pathArray = getPathArray(url);
		String subdomain;
		if (pathArray.length > SUBDOMAIN_ARRAY_SIZE) {
			throw new PayAsiaPageNotFoundException(PayAsiaConstants.SUBDOMAIN_SIZE_MORE);
		}
		if (pathArray.length == SUBDOMAIN_ARRAY_SIZE) {
			subdomain = pathArray[0];
			LOGGER.debug("Subdomain :: " + subdomain);
			Boolean companyCode = loginLogic.checkCompanyCode(subdomain);
			if (companyCode.equals(true)) {
				return subdomain;

			} else {
				throw new PayAsiaPageNotFoundException(PayAsiaConstants.SUBDOMAIN_SIZE_MORE);

			}

		}
		return null;
	}

	private String[] getPathArray(URL url) {
		Pattern p = Pattern.compile("[.]");
		String pathArray[] = p.split(url.getAuthority());
		return pathArray;
	}

	/**
	 * Gets the domain name from URL.
	 * 
	 * @param url
	 *            the url
	 * @return the sub domain
	 */
	private String getDomain(URL url) {
		String[] pathArray = getPathArray(url);

		if (pathArray.length == SUBDOMAIN_ARRAY_SIZE) {
			String domain = pathArray[1];

			LOGGER.debug("Domain :: " + domain);

			return domain;
		}
		return null;
	}

	/**
	 * Gets the domain name extension from URL.
	 * 
	 * @param url
	 *            the url
	 * @return the sub domain
	 */
	private String getDomainExtension(URL url) {
		String[] pathArray = getPathArray(url);

		if (pathArray.length == SUBDOMAIN_ARRAY_SIZE) {
			String domainExtension = pathArray[2];

			LOGGER.debug("Domain Extention:: " + domainExtension);

			return domainExtension;
		}
		return null;
	}

	/**
	 * Gets the switch company url prefix in case URL is of subdomain syntax.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @param companyCode
	 *            the company code
	 * @return the switch company url prefix
	 */
	private String getSwitchCompanyURLPrefix(HttpServletRequest httpRequest, String companyCode) {

		String urlPrefix = getURLPrefix(httpRequest);

		try {
			String subdomain = getSubDomain(new URL(httpRequest.getRequestURL().toString()));

			if (subdomain != null) {
				urlPrefix = urlPrefix.replaceFirst(subdomain, companyCode);
			}
		} catch (MalformedURLException mue) {
			LOGGER.error(mue);
		}

		return urlPrefix;
	}

	/**
	 * Gets the non sub domain company code, i.e. company code from session.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @param defaultCompanyCode
	 *            the default company code
	 * @return the non sub domain company code
	 */
	public static String getNonSubDomainCompanyCode(HttpServletRequest httpRequest, String defaultCompanyCode) {
		HttpSession session = httpRequest.getSession(false);

		String companyCode = null;

		if (session != null) {
			companyCode = (String) session.getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_CODE);

		}

		if (companyCode == null) {
			companyCode = defaultCompanyCode;
		}

		return companyCode;
	}

	/**
	 * Gets the switch company admin url.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @param companyCode
	 *            the company code
	 * @return the switch company admin url
	 */
	public String getSwitchCompanyAdminURL(HttpServletRequest httpRequest, String companyCode) {
		String switchCompanyAdminURI = httpRequest.getContextPath() + PayAsiaConstants.BASE_URI_PATH + companyCode
				+ PayAsiaConstants.ADMIN_HOME_URI;

		String switchCompanyAdminURL = getSwitchCompanyURLPrefix(httpRequest, companyCode) + switchCompanyAdminURI;

		return switchCompanyAdminURL;
	}

	/**
	 * Gets the switch Role url.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @param companyCode
	 *            the company code
	 * @return the switch Role url
	 */
	public String getSwitchRoleEmployeeURL(HttpServletRequest httpRequest, String companyCode) {
		String switchRoleURI = httpRequest.getContextPath() + PayAsiaConstants.BASE_URI_PATH + companyCode
				+ PayAsiaConstants.EMPLOYEE_HOME_URI;

		String sessionLocale = (String) httpRequest.getSession()
				.getAttribute(PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL);

		String switchRoleURL = null;

		if (StringUtils.isNotBlank(sessionLocale)) {
			switchRoleURL = getSwitchCompanyURLPrefix(httpRequest, companyCode) + switchRoleURI + "?locale="
					+ sessionLocale;
		} else {
			switchRoleURL = getSwitchCompanyURLPrefix(httpRequest, companyCode) + switchRoleURI;
		}

		return switchRoleURL;
	}

	/**
	 * Gets employee failed company login url.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @param companyCode
	 *            the company code
	 * @return the failed login url
	 */
	public String getEmployeeFailedLoginURL(HttpServletRequest httpRequest, String companyCode) {
		String switchRoleURI = httpRequest.getContextPath() + PayAsiaConstants.BASE_URI_PATH + companyCode
				+ PayAsiaConstants.EMPLOYEE_FAILED_LOGIN_URI;

		String failedLoginURL = getSwitchCompanyURLPrefix(httpRequest, companyCode) + switchRoleURI;
		return failedLoginURL;
	}

	/**
	 * Gets the URL prefix.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @return the uRL prefix
	 */
	public String getURLPrefix(HttpServletRequest httpRequest) {
		return httpRequest.getRequestURL().substring(0,
				httpRequest.getRequestURL().indexOf(httpRequest.getRequestURI()));
	}

	/**
	 * Gets the logout URL prefix.
	 * 
	 * @param httpRequest
	 *            the http request
	 * @return the uRL prefix
	 */
	public String getLogoutURLPrefix(HttpServletRequest httpRequest) {
		String urlPrefix = httpRequest.getRequestURL().substring(0,
				httpRequest.getRequestURL().indexOf(httpRequest.getRequestURI()));

		String subdomain = "";
		try {
			subdomain = getSubDomain(new URL(httpRequest.getRequestURL().toString()));
		} catch (MalformedURLException mue) {
			LOGGER.error(mue);
		}

		String employeeCompanyCode = getCookieValue(PayAsiaConstants.EMPLOYEE_COMPANY_COOKIE_NAME, httpRequest);

		if (!StringUtils.isEmpty(employeeCompanyCode) && !StringUtils.isEmpty(subdomain)
				&& !employeeCompanyCode.equals(subdomain)) {
			urlPrefix = urlPrefix.replaceFirst(subdomain, employeeCompanyCode);
		}

		return urlPrefix;
	}

	public void addCookie(String cookieName, String cookieValue, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		Cookie cookie = new Cookie(cookieName, cookieValue);
		// cookie.setMaxAge(COOKIE_EXPIRATION_TIME);
		// cookie.setPath("/");
		try {
			URL url = new URL(httpRequest.getRequestURL().toString());
			String domain = getDomain(url);
			String domainExt = getDomainExtension(url);
			if (domainExt.indexOf(':') >= 0) {
				domainExt = domainExt.substring(0, domainExt.indexOf(':'));
			}
			String cookieDomainPath = "." + domain + "." + domainExt;

			cookie.setDomain(cookieDomainPath);

			LOGGER.debug("Added cookie : " + cookieName + " :: " + cookieValue);
		} catch (MalformedURLException mue) {
			LOGGER.error(mue);
		}

		httpResponse.addCookie(cookie);
	}

	public static String getCookieValue(String cookieName, HttpServletRequest httpRequest) {
		Cookie[] cookies = httpRequest.getCookies();
		String cookieValue = null;

		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			if (cookieName.equals(name)) {
				cookieValue = cookie.getValue();
				break;
			}
		}

		LOGGER.debug("Getting cookie : " + cookieName + " :: " + cookieValue);
		return cookieValue;
	}

	public String getDefaultCompanyCode(HttpServletRequest httpRequest) {
		String defaultCompanyCode = loginLogic.getDefaultPayAsiaCompanyCode();
		defaultCompanyCode = URLUtils.getNonSubDomainCompanyCode(httpRequest, defaultCompanyCode);

		return defaultCompanyCode;
	}

	public String getEmployeeCompanyCode(HttpServletRequest httpRequest) {
		String employeeCompanyCode = getCookieValue(PayAsiaConstants.EMPLOYEE_COMPANY_COOKIE_NAME, httpRequest);

		if (StringUtils.isEmpty(employeeCompanyCode)) {
			String defaultCompanyCode = loginLogic.getDefaultPayAsiaCompanyCode();
			defaultCompanyCode = URLUtils.getNonSubDomainCompanyCode(httpRequest, defaultCompanyCode);

			employeeCompanyCode = defaultCompanyCode;

		}
		return employeeCompanyCode;
	}

	public String getContextPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();

		if ("/".equals(contextPath)) {
			contextPath = "";
		}
		return contextPath;
	}

	public String getRefererURL(HttpServletRequest request) {
		return request.getHeader("referer");
	}

	public String getSessionExpiryURL(HttpServletRequest hReq, String expiredUrl) {
		String targetURL = getLogoutURLPrefix(hReq) + getContextPath(hReq) + expiredUrl;

		String urlWithCompanyCode = getURLWithCompanyCode(targetURL, hReq);
		return urlWithCompanyCode;
	}

	/**
	 * Purpose : Get New Locale with specified Script(As short company Code).
	 * 
	 * @param localeParam
	 *            the locale Parameter
	 * @param shortcompanyCode
	 *            the short company Code
	 * @return the New Locale
	 */
	public Locale getNewLocale(String localeParam, String shortCompanyCode) {
		Builder localeBuilder = new Locale.Builder();
		localeBuilder.setLocale(org.springframework.util.StringUtils.parseLocaleString(localeParam));

		if (StringUtils.isNotBlank(shortCompanyCode)) {
			localeBuilder.setScript(shortCompanyCode);
		}
		Locale newLocale = localeBuilder.build();

		return newLocale;
	}

}
