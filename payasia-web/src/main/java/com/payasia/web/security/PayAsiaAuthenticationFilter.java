/*
 * <h4>Description</h4>
 * Custom PayAsiaAuthenticationFilter service.
 * This class is not called but is required at runtime.
 *
 * @author Yogendra Pratap Singh
 * @date Friday July 04, 2011
 */
package com.payasia.web.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.SsoConfigurationDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.SsoConfiguration;
import com.payasia.web.util.PayAsiaSessionAttributes;
import com.payasia.web.util.URLUtils;

public class PayAsiaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	private static final Logger LOGGER = Logger.getLogger(PayAsiaAuthenticationFilter.class);
	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
	public static final String SPRING_SECURITY_FORM_COMPANY_CODE_KEY = "companycode";
	public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";
	public static final String SPRING_SECURITY_SSO_USERNAME_KEY = "userId";
	public static final String SPRING_SECURITY_SSO_SESSIONID_KEY = "sessionId";
	public static final String SPRING_SECURITY_SSO_LANGUAGE_KEY = "language";
	public static final String SPRING_SECURITY_LOCALE_KEY = "en";
	public static final String REQUEST_METHOD = "POST";

	public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";

	/** The authentication failure url. */
	private String authenticationFailureUrl;

	@Resource
	URLUtils urlUtils;
	@Resource
	CompanyDAO companyDAO;
	
	@Resource
	SsoConfigurationDAO ssoConfigurationDAO;

	@Value("#{payasiaptProperties['payasia.app.encryption']}")
	private  String encryptionValue;
	
	/**
	 * Instantiates a new PayAsia authentication filter.
	 */
	/*protected PayAsiaAuthenticationFilter() {
		super(DEFAULT_FILTER_PROCESSES_URL);
	}*/
	
	 public PayAsiaAuthenticationFilter() {
	        super("/**");
	    }

	    @Override
	    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
	        return true;
	    }
	
	

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;

		String languageId = (String) request.getSession().getAttribute(PayAsiaSessionAttributes.LANGUAGE_ID);
		if (StringUtils.isNotBlank(languageId)) {
			UserContext.setLanguageId(Long.valueOf(languageId));
		}

		UserContext.setUserId(getEmployeeID());
		UserContext.setWorkingCompanyId(
				String.valueOf(request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID)));
		UserContext.setWorkingCompanyTimeZoneGMTOffset(
				String.valueOf(request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_TIMEZONE_GMT_OFFSET)));
		UserContext.setWorkingCompanyDateFormat(
				String.valueOf(request.getSession().getAttribute(PayAsiaSessionAttributes.COMPANY_DATE_FORMAT)));
		String reqURL = request.getRequestURL().toString();
		
		//Set key for encryption and decryption 			
		String privateKeyID = (String) request.getSession().getAttribute("privateKey");
		if(StringUtils.isNotBlank(privateKeyID) && encryptionValue!=null && encryptionValue.equalsIgnoreCase("YES")){
		    UserContext.setKey(privateKeyID);
		 }else{
			 UserContext.setKey(null);
		 }
		//check if root request is from mobile and sso is enabled
		DeviceResolver deviceResolver = new LiteDeviceResolver();
		Device device = deviceResolver.resolveDevice(request);
		if (request.getRequestURI().equals("/") && device != null && (device.isMobile() || device.isTablet())) {
				LOGGER.info("Redirecting user to mobile url schema");
				String companyCode = urlUtils.getSubDomain(new URL(request.getRequestURL().toString()));
//				SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyCode(companyCode);
				SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyCodeWithGroup(companyCode);
				HttpServletResponse response = (HttpServletResponse) res;
				if (ssoConfiguration != null && ssoConfiguration.getIsEnableSso()) {
					response.sendRedirect("/saml/login?idp=" + URLEncoder.encode(ssoConfiguration.getIdpIssuer(), "UTF-8"));
				}
		}
		
		if (!reqURL.endsWith("/portal/login.html") && reqURL.indexOf(PayAsiaConstants.BASE_URI_PATH) >= 0) {

			String urlWithCompanyCode = urlUtils.getURLWithCompanyCode(request.getRequestURL().toString(), request);

			HttpServletResponse response = (HttpServletResponse) res;

			if (urlWithCompanyCode != null) {
				response.sendRedirect(urlWithCompanyCode);
			} else {
				super.doFilter(req, res, chain);
			}
		} else {
			super.doFilter(req, res, chain);
		}

	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
		if (request.getMethod().equals(REQUEST_METHOD)) {
			return attemptAuthenticationPOST(request, response);
		} else {
			throw new AuthenticationServiceException("Invalid username or password");
		}
	}

	
	@Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        // As this authentication is in HTTP header, after success we need to continue the request normally
        // and return the response as if the resource was not secured at all
        chain.doFilter(request, response);
    }
	
	
	/**
	 * Attempt authentication using POST request (i.e. when user directly logs on to
	 * PayAsia Application).
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the authentication
	 * @throws AuthenticationException
	 *             the authentication exception
	 * @throws IOException
	 *             the IO exception
	 * @throws ServletException
	 *             the servlet exception
	 */
	private Authentication attemptAuthenticationPOST(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		HttpSession session = request.getSession(false);
		String username = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String lastUsername = username;
		String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
		String locale = SPRING_SECURITY_LOCALE_KEY;

		String companyCode = request.getParameter(SPRING_SECURITY_FORM_COMPANY_CODE_KEY);

		if (username == null) {
			username = "";
		}

		if (lastUsername == null) {
			lastUsername = "";
		}

		if (password == null) {
			password = "";
		}
		if (companyCode == null) {
			companyCode = "";
		} else {
			try {
				if (StringUtils.isNotBlank(username)) {
					username = new String(Base64.decodeBase64(username.getBytes()));
					username = URLDecoder.decode(username, "UTF-8");
					lastUsername = username;

					password = new String(Base64.decodeBase64(password.getBytes()));
					password = URLDecoder.decode(password, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			username = companyCode + PayAsiaConstants.COMPANY_USERID_DELIMETER + username;
		}

		username = username.trim();

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

		if (session != null || getAllowSessionCreation()) {
			request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, lastUsername);
			if (session != null) {
				session.setAttribute(PayAsiaSessionAttributes.EMPLOYEE_COMPANY_CODE, companyCode);
				session.setAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_CODE, companyCode);
			}

		}

		setDetails(request, authRequest);

		setLocale(request, response, locale, companyCode);

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	/**
	 * Sets the details.
	 * 
	 * @param request
	 *            the request
	 * @param authRequest
	 *            the auth request
	 */
	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	/**
	 * Sets locale set during login.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @throws ServletException
	 *             the servlet exception
	 */
	private void setLocale(HttpServletRequest request, HttpServletResponse response, String locale, String companyCode)
			throws ServletException {

		String shortCompanyCode = "";
		if (StringUtils.isNotBlank(companyCode)) {
			Company companyVO = companyDAO.findByCompanyCode(companyCode, null);
			if (companyVO != null && StringUtils.isNotBlank(companyVO.getShortCompanyCode())) {
				shortCompanyCode = companyVO.getShortCompanyCode();
			}

		}
		String localeParam = locale + "_US";
		Locale newLocale = urlUtils.getNewLocale(localeParam, shortCompanyCode);

		WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);
	}

	/**
	 * Sets the authentication failure url.
	 * 
	 * @param authenticationFailureUrl
	 *            the new authentication failure url
	 */
	public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
		this.authenticationFailureUrl = authenticationFailureUrl;
	}

	/**
	 * Gets the authentication failure url.
	 * 
	 * @return the authentication failure url
	 */
	public String getAuthenticationFailureUrl() {
		return authenticationFailureUrl;
	}

	static public String getURLWithCompanyCode(URL url) {
		String companyCode = getSubDomain(url);

		String urlString = url.toExternalForm();

		if (urlString.contains("/portal")) {
			if (urlString.contains(companyCode)) {
				return null;
			} else {
				urlString = urlString.substring(0, urlString.indexOf("/portal")) + "/portal/" + companyCode
						+ urlString.substring(urlString.indexOf("/portal") + "/portal".length());
				return urlString;
			}
		}

		return null;
	}

	private static String getSubDomain(URL url) {
		String host = url.getHost().toString();
		Pattern p = Pattern.compile("[.]");
		String u[] = p.split(url.getAuthority());
		if (u.length > 2) {
			int lastIndex = host.lastIndexOf(".");
			int index = host.lastIndexOf(".", lastIndex - 1);

			return host.substring(0, index);
		}
		return null;
	}

	private String getEmployeeID() {

		String empId = "NA";

		try {
			if (SecurityContextHolder.getContext() != null
					&& SecurityContextHolder.getContext().getAuthentication() != null) {

				Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

				if (userDetails != null) {
					if (userDetails instanceof PayAsiaUserDetails) {
						empId = String.valueOf(((PayAsiaUserDetails) userDetails).getUserId());
					} else {
						empId = (String) userDetails;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Not able to locate user from security context. Reason: " + e.getMessage(), e);
		}

		return empId;
	}
	
	
}
