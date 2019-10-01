package com.payasia.saml.security;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLRuntimeException;
import org.opensaml.ws.transport.http.HTTPInTransport;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.util.CollectionUtils;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaDuplicateSessionException;
import com.payasia.common.util.PayAsiaConstantEnum;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.SsoConfigurationDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.LoginLogic;
import com.payasia.web.security.PayAsiaUserDetails;

public class PayAsiaSAMLAuthenticationProvider extends SAMLAuthenticationProvider {

	private final static Logger log = LoggerFactory.getLogger(SAMLAuthenticationProvider.class);

	@Resource
	SsoConfigurationDAO ssoConfigurationDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	LoginLogic loginLogic;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		if (!supports(authentication.getClass())) {
			throw new IllegalArgumentException(
					"Only SAMLAuthenticationToken is supported, " + authentication.getClass() + " was attempted");
		}

		SAMLAuthenticationToken token = (SAMLAuthenticationToken) authentication;
		SAMLMessageContext context = token.getCredentials();
		SAMLCredential credential;

		try {
			if (SAMLConstants.SAML2_WEBSSO_PROFILE_URI.equals(context.getCommunicationProfileId())) {
				credential = consumer.processAuthenticationResponse(context);
			} else if (SAMLConstants.SAML2_HOK_WEBSSO_PROFILE_URI.equals(context.getCommunicationProfileId())) {
				credential = hokConsumer.processAuthenticationResponse(context);
			} else {
				throw new SAMLException(
						"Unsupported profile encountered in the context " + context.getCommunicationProfileId());
			}
		} catch (SAMLRuntimeException e) {
			log.debug("Error validating SAML message", e);
			samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
			throw new AuthenticationServiceException("Error validating SAML message", e);
		} catch (SAMLException e) {
			log.debug("Error validating SAML message", e);
			samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
			throw new AuthenticationServiceException("Error validating SAML message", e);
		} catch (ValidationException e) {
			log.debug("Error validating signature", e);
			samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
			throw new AuthenticationServiceException("Error validating SAML message signature", e);
		} catch (org.opensaml.xml.security.SecurityException e) {
			log.debug("Error validating signature", e);
			samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
			throw new AuthenticationServiceException("Error validating SAML message signature", e);
		} catch (DecryptionException e) {
			log.debug("Error decrypting SAML message", e);
			samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
			throw new AuthenticationServiceException("Error decrypting SAML message", e);
		}

		Object principal = getPrincipal(credential, userDetails);

		SAMLCredential authenticationCredential = credential;
		SecurityContext scontext = SecurityContextHolder.getContext();

		if (scontext != null && scontext.getAuthentication() != null) {
			if (principal != null && principal instanceof User) {
				throw new PayAsiaDuplicateSessionException("duplicate.session");
			}
		}

		final String samlPrincipal = (String) principal.toString();
		String idpIssuer = authenticationCredential.getRemoteEntityID();

		// Find Company_ID from IDP Issuer
		Company company = ssoConfigurationDAO.findCompanyByIDPIssuer(idpIssuer);
		if (company == null) {
			throw new BadCredentialsException("The company portal you are accessing is inactive or SSO not enabled");
		}

		List<Employee> empList = employeeDAO.getEmpByUsernameOrEmailOrFullName(samlPrincipal, company.getCompanyCode());
		if (CollectionUtils.isEmpty(empList)) {
			throw new BadCredentialsException("User not found");
		}

		Employee employeeVO = empList.get(0);

		boolean userEnabled = isUserActive(employeeVO);
		boolean accountNotExpired = userEnabled;
		boolean credentialsNotExpired = userEnabled;
		boolean accountNotLocked = userEnabled;

		// Saving info in threadlocal
		UserContext.setUserId(String.valueOf(employeeVO.getEmployeeId()));

		if (!userEnabled) {
			throw new BadCredentialsException("Your account is disabled");
		}

		// Saving employee login history
		String userIPAddress = StringUtils.EMPTY;
		if (context.getInboundMessageTransport() != null) {
			HTTPInTransport transport = (HTTPInTransport) context.getInboundMessageTransport();
			userIPAddress = transport.getPeerAddress();
		}
		loginLogic.saveLoginHistory(employeeVO, userIPAddress, PayAsiaConstantEnum.LoginMode.SSO.getMode());

		List<GrantedAuthority> grantedAuthorities = getUserPrivilege(employeeVO, company);

		User userDetails = new PayAsiaUserDetails(employeeVO.getEmployeeNumber(), employeeVO.getEmployeeId(), "",
				userEnabled, accountNotExpired, credentialsNotExpired, accountNotLocked, grantedAuthorities);

		return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(),
				grantedAuthorities);

	}

	/**
	 * Checks if is user active.
	 * 
	 * @param user
	 *            the user
	 * @return true, if is user active
	 */
	private boolean isUserActive(Employee user) {
		boolean userStatus = false;

		if (user.isStatus() == true) {
			userStatus = true;
		}

		return userStatus;
	}

	/**
	 * Gets the user roles.
	 * 
	 * @param userName
	 *            the user name
	 * @param companyCode
	 *            the company code
	 * @return the user roles
	 */
	private List<GrantedAuthority> getUserPrivilege(Employee employeeVO, Company company) {
		return loginLogic.getUserPrivilege(employeeVO, company);
	}
}
