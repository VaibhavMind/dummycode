package com.payasia.api.oauth;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.payasia.api.exception.AppRuntimeException;
import com.payasia.api.utils.JSONConverterUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SsoConfigurationDTO;
import com.payasia.common.util.CryptoUtil;
import com.payasia.common.util.PropReader;
import com.payasia.dao.CompanyActiveBetaDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.SsoConfigurationDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyActiveBeta;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.SsoConfiguration;
import com.payasia.logic.SecurityLogic;
import com.payasia.logic.SsoConfigurationLogic;
import com.payasia.web.util.URLUtils;

/**
 * @author manojkumar2
 * @param : OAuth2CredentialsTokenEndpointFilter.java
 * @param : This class is used to 
*/

public class OAuth2CredentialsTokenEndpointFilter extends AbstractAuthenticationProcessingFilter{

	private static final Logger LOGGER = Logger.getLogger(OAuth2CredentialsTokenEndpointFilter.class);

	@Resource
	private URLUtils urlUtils;
	@Resource
	private CompanyDAO companyDAO;
	
	@Resource
	private SsoConfigurationDAO ssoConfigurationDAO;
	
	@Resource
	private CompanyActiveBetaDAO companyActiveBetaDAO;

    @Autowired
    private TokenStore tokenStore;
	
    @Autowired
    @Qualifier(value="payAsiaAuthenticationManager")
    AuthenticationManager authenticationManager;
	
    
	@Autowired
	@Qualifier(value="userAuthenticationManager")
	AuthenticationManager userAuthenticationManager;
	
	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private SsoConfigurationLogic ssoConfigurationLogic;
	
	@Autowired
	private SecurityLogic securityLogic;
    
	private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
	private boolean allowOnlyPost = false;

	private static final String DEFAULT_FILTER_PROCESSES_URL = "/oauth/token";
	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";
	public static final String SPRING_SECURITY_FORM_COMPANY_CODE_KEY = "companycode";
	public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";
	public static final String SPRING_SECURITY_SSO_USERNAME_KEY = "userId";
	public static final String SPRING_SECURITY_SSO_SESSIONID_KEY = "sessionId";
	public static final String SPRING_SECURITY_SSO_LANGUAGE_KEY = "language";
	public static final String SPRING_SECURITY_LOCALE_KEY = "en_US";
	public static final String REQUEST_METHOD = "POST";
	

	@Value("#{payasiaptProperties['payasia.app.encryption']}")
	private  String encryptionValue;
	
	@Value("#{payasiaptProperties['oauth2.access.token.expiration']}")
	private  String tokenExpiration;

	@Value("#{payasiaptProperties['company.code.allowed.list']}")
	private  String allowedCompanyCode;
	
	
	public OAuth2CredentialsTokenEndpointFilter() {
		this(DEFAULT_FILTER_PROCESSES_URL);
	}

	
	public OAuth2CredentialsTokenEndpointFilter(String path) {
		super(path);
		setRequiresAuthenticationRequestMatcher(new ClientCredentialsRequestMatcher(path));
		// If authentication fails the type is "Form"
		((OAuth2AuthenticationEntryPoint) authenticationEntryPoint).setTypeName("Form");
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		LOGGER.info("Inside doFilter");
		
	    final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
	    HttpServletRequest request = (HttpServletRequest) req;
	    String reqURL = request.getRequestURL().toString();
	    
	    final String authorizationHeader = request.getHeader("Authorization");
	    
	    LOGGER.info("reqURL" + reqURL);
	    
	    if((reqURL.endsWith("/oauth/token") || reqURL.endsWith("/captcha/captcha-img") || reqURL.endsWith("/captcha/reload-captcha-img") || reqURL.endsWith("/login/company-logo")
	    	|| reqURL.endsWith("/login/login-background-image") || reqURL.endsWith("/login/recover-username-and-password") || reqURL.endsWith("/login/login-problem")
	    	|| reqURL.endsWith("/login/customer-testimonial") || reqURL.endsWith("/login/password-policies") || reqURL.endsWith("/login/reset-forgot-password") || reqURL.endsWith("/login/reset-password")
	    	|| reqURL.endsWith("/login/language-list") || reqURL.endsWith("/common/sso-status")) &&  StringUtils.isEmpty(authorizationHeader)){
	  
	    	UserContext.setDevice(getDeviceDetails(request));
	    	
	    	super.doFilter(req, res, chain);
	    	
	    }else{
	    	
	    	if(auth!=null && auth.isAuthenticated() && !StringUtils.isEmpty(authorizationHeader) && authorizationHeader.startsWith("Bearer")){
	    	
	    		String tabID = request.getHeader("X-tabId");
	    		
	    		//set data in user context
				setUserData(auth,tabID,reqURL);
				
				setAccessTokenTime(auth);

				//check if root request is from mobile and sso is enabled
				DeviceResolver deviceResolver = new LiteDeviceResolver();
				Device device = deviceResolver.resolveDevice(request);
				if (request.getRequestURI().equals("/") && device != null && (device.isMobile() || device.isTablet())) {
						LOGGER.info("Redirecting user to mobile url schema");
						String companyCode = urlUtils.getSubDomain(new URL(request.getRequestURL().toString()));
						SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyCodeWithGroup(companyCode);
						HttpServletResponse response = (HttpServletResponse) res;
						if (ssoConfiguration != null && ssoConfiguration.getIsEnableSso()) {
							response.sendRedirect("/saml/login?idp=" + URLEncoder.encode(ssoConfiguration.getIdpIssuer(), "UTF-8"));
						}
				   }
				
				 super.doFilter(req, res, chain);
	    		
	    	}else{
	    		 LOGGER.error("Authorization header is missing.");
	    		throw new AppRuntimeException(PropReader.getMessage("user.authorization.header"));
	    	}
	    	
	    }

	}
   
	private void setUserData(OAuth2Authentication auth ,String tabID,String reqURL) {

		Map<String, Object> additionalInfo = tokenStore.getAccessToken(auth).getAdditionalInformation();
		String employeeInfo =  (String) additionalInfo.get("userData");
		boolean isTwoFactorAuth =  (boolean) additionalInfo.get("isTwoWayAuth");
		String decryptData = CryptoUtil.decrypt(employeeInfo,CryptoUtil.SECRET_KEY);
		LOGGER.info("User Data " + decryptData);
		UserData userData = (UserData) JSONConverterUtils.jsonStringToObject(decryptData, UserData.class);
		
		if(!reqURL.endsWith("/common/token-valid")  && userData.getTabID()!=null && tabID!=null && !userData.getTabID().equals(tabID)){
			throw new AppRuntimeException(PropReader.getMessage("browser.tab.error"));
			
		}
		
		if(isTwoFactorAuth){
			boolean isValidTwoFactorAuth = (boolean) additionalInfo.get("twoWayAuthCheck");
			if(!isValidTwoFactorAuth && !reqURL.endsWith("/common/send-otp") && !reqURL.endsWith("/common/validate-otp") && !reqURL.endsWith("/common/two-factor-status") && !reqURL.endsWith("/login/password-policies") && !reqURL.endsWith("/hris/change-password") && !reqURL.endsWith("/hris/profile-image") && !reqURL.endsWith("login/language-list") && !reqURL.endsWith("login/company-logo")){
				throw new AccessDeniedException("403");
			}
		}
		
		UserContext.setUserId(userData.getUserId());
		UserContext.setWorkingCompanyId(userData.getWorkingCompanyId());
		UserContext.setWorkingCompanyTimeZoneGMTOffset(userData.getWorkingCompanyTimeZone());
		UserContext.setWorkingCompanyDateFormat(userData.getWorkingCompanyDateFormat());
		UserContext.setLanguageId(userData.getLanguageId());
	    UserContext.setKey(userData.getPrivateKey());
	    UserContext.setTab(userData.getTabID());
	    UserContext.setLoginId(userData.getEmployeeNumber());
	    UserContext.setCompanyCode(userData.getCompanyCode());
	    UserContext.setHrisModule(userData.isHasHrisModule());
        UserContext.setClaimModule(userData.isHasClaimModule());
        UserContext.setLeaveModule(userData.isHasLeaveModule());
        UserContext.setMobileModule(userData.isHasMobileModule());
        UserContext.setCoherentTimesheetModule(userData.isHasCoherentTimesheetModule());
        UserContext.setLionTimesheetModule(userData.isHasLionTimesheetModule());
        UserContext.setLundinTimesheetModule(userData.isHasLundinTimesheetModule());
        UserContext.setLocale(userData.getLocale());
        UserContext.setDevice(userData.getDeviceName());
        UserContext.setIpAddress(userData.getIpAddress());
        UserContext.setClientAdminId(userData.getClientAdminId());
	    

	}

	private void setAccessTokenTime(OAuth2Authentication auth) {
		OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
		DefaultOAuth2AccessToken accessToken2 = (DefaultOAuth2AccessToken) accessToken;
		if((accessToken2.getExpiresIn()) < 120) {
			LOGGER.info("Token Time extends");
			long tokenExpirationTime = Long.valueOf(tokenExpiration);
			long milliseconds = TimeUnit.SECONDS.toMillis(tokenExpirationTime);
			accessToken2.getExpiration().setTime(accessToken2.getExpiration().getTime() + milliseconds);
			tokenStore.storeAccessToken(accessToken2, auth);
		}
	}
	
	/**
	 * @param authenticationEntryPoint the authentication entry point to set
	 */
	public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	
	public void setAllowOnlyPost(boolean allowOnlyPost) {
		this.allowOnlyPost = allowOnlyPost;
	}
	
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				if (exception instanceof BadCredentialsException) {
					exception = new BadCredentialsException(exception.getMessage(), new BadClientCredentialsException());
				}
				authenticationEntryPoint.commence(request, response, exception);
			}
		});
		setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// no-op - just allow filter chain to continue to token endpoint
			}
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (allowOnlyPost && !REQUEST_METHOD.equalsIgnoreCase(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { REQUEST_METHOD });
		}

		//SSO Check
		String companyCode = request.getParameter("companyCode").trim();
		String email = request.getParameter("email");
		String loginId = null;
		String password = null;
		boolean isSsoEnable=false;
		String clientId = request.getParameter("client_id").trim();
		String clientSecret = request.getParameter("client_secret").trim();
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		String tabID = request.getHeader("X-tabId");
		String locale = request.getParameter("applocale");
		
		if(!StringUtils.isEmpty(email) && !StringUtils.isEmpty(companyCode)){
			SsoConfigurationDTO ssoConfigurationDTO = ssoConfigurationLogic.getSsoConfigByCompCodeWithGroup(companyCode);
			if(ssoConfigurationDTO!=null && ssoConfigurationDTO.getIsEnableSso()){
				isSsoEnable=true;
				List<Employee> empList = ssoConfigurationLogic.getUserByEmail(email, companyCode);
				if(empList!=null && empList.size()==1){
					EmployeeLoginDetail loginDetail = ssoConfigurationLogic.getUserLoginByEmployeeID(empList.get(0).getEmployeeId());
					if(loginDetail!=null){
						loginId = loginDetail.getLoginName();
						password = securityLogic.decrypt(loginDetail.getPassword(), loginDetail.getSalt());
					}else {
						throw new AppRuntimeException(messageSource.getMessage("payasia.username.password.not.empty", new Object[]{}, UserContext.getLocale()));
					}
				}
				else{
					throw new AppRuntimeException(messageSource.getMessage("payasia.user.more.email", new Object[]{}, UserContext.getLocale()));
				}
			}
		 }

	    if(!isSsoEnable) {
			loginId = request.getParameter("username").trim();
			password = request.getParameter("password").trim();
			if(!StringUtils.isEmpty(password)){
			   password = new String(Base64.decodeBase64(password.getBytes()));
			}
		}
		
		if(locale==null){
			setLocale(SPRING_SECURITY_LOCALE_KEY,locale);
		}else{
			setLocale(locale,companyCode);
		}
		
		if(ipAddress == null) {
			ipAddress = request.getRemoteAddr();			
		}
		
		
		if (StringUtils.isEmpty(loginId) || StringUtils.isEmpty(password)) {
			throw new AppRuntimeException(messageSource.getMessage("payasia.username.password.not.empty", new Object[]{}, UserContext.getLocale()));
		}
		else if (StringUtils.isEmpty(clientId) || StringUtils.isEmpty(clientSecret)) {
			throw new AppRuntimeException(messageSource.getMessage("payasia.clientid.clientsecret.not.empty", new Object[]{}, UserContext.getLocale()));
		}
		
		else if (StringUtils.isEmpty(companyCode)) {
			throw new AppRuntimeException("Company code can not be empty");
		}
		
		
		if(!isAllowedCompany(companyCode)){
			throw new AppRuntimeException(messageSource.getMessage("payasia.company.not.allowed", new Object[]{}, UserContext.getLocale()));
		}
		
		// If the request is already authenticated we can assume that this
		// filter is not needed
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return authentication;
		}

		if (clientId == null) {
			throw new AppRuntimeException(messageSource.getMessage("payasia.client.credentials.disable", new Object[]{}, UserContext.getLocale()));
		}

		if (clientSecret == null) {
			clientSecret = "";
		}

		clientId = clientId.trim();
	
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId, clientSecret);
		
		Authentication clientAuthentication = authenticationManager.authenticate(authRequest);
	
		if(clientAuthentication!=null && clientAuthentication.isAuthenticated()){
			UserContext.setCompanyCode(companyCode);
			UserContext.setIpAddress(ipAddress);
			UserContext.setLoginId(loginId);
			UserContext.setPassword(password);
			UserContext.setTab(tabID);	
			return userAuthenticationManager.authenticate(authRequest);
		}else{
			throw new AppRuntimeException(messageSource.getMessage("payasia.client.credentials.disable", new Object[]{}, UserContext.getLocale()));
		}
	
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}

	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}
	
	protected static class ClientCredentialsRequestMatcher implements RequestMatcher {

		private String path;

		public ClientCredentialsRequestMatcher(String path) {
			this.path = path;

		}

		@Override
		public boolean matches(HttpServletRequest request) {
			String uri = request.getRequestURI();
			int pathParamIndex = uri.indexOf(';');

			if (pathParamIndex > 0) {
				// strip everything after the first semi-colon
				uri = uri.substring(0, pathParamIndex);
			}

			String clientId = request.getParameter("client_id");

			if (clientId == null) {
				// Give basic auth a chance to work instead (it's preferred anyway)
				return false;
			}

			if ("".equals(request.getContextPath())) {
				return uri.endsWith(path);
			}

			return uri.endsWith(request.getContextPath() + path);
		}

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
	 */
	private void setLocale(String locale, String companyCode) {

		String shortCompanyCode = "";
		if (StringUtils.isNotBlank(companyCode)) {
			Company companyVO = companyDAO.findByCompanyCode(companyCode, null);
			if (companyVO != null && StringUtils.isNotBlank(companyVO.getShortCompanyCode())) {
				shortCompanyCode = companyVO.getShortCompanyCode();
			}
		}
		Locale newLocale = urlUtils.getNewLocale(locale, shortCompanyCode);
		UserContext.setLocale(newLocale);

	}

	/**
	 * @param: Capture device details 
	*/
	private String getDeviceDetails(HttpServletRequest request){
		DeviceResolver deviceResolver = new LiteDeviceResolver();
		Device device = deviceResolver.resolveDevice(request);
		String deviceName = null;
		if(device.isMobile() && device.getDevicePlatform().equals(DevicePlatform.ANDROID)){			
			deviceName= "ANDROID DEVICE";
		}else if(device.isMobile() && device.getDevicePlatform().equals(DevicePlatform.IOS)){			
			deviceName= "IOS DEVICE";
		}else if(device.isTablet()){
			deviceName = "TABLET DEVICE";
		}else{
			deviceName = "WEB BROWSER";
		}
		return deviceName;
	}
	
	private boolean isAllowedCompany(String companyCode) {
	    
		List<CompanyActiveBeta> activeCompanies = companyActiveBetaDAO.findAllActive();
		if(activeCompanies.isEmpty())
			return true;
		Company company = companyDAO.findByCompanyCode(companyCode, null);
		if(company == null)
			return false;
		CompanyActiveBeta companyStatus = companyActiveBetaDAO.findByCompanyId(company.getCompanyId());
		return companyStatus != null && companyStatus.isActive();
	}
}
