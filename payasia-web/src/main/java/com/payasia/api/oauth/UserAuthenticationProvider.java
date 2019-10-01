package com.payasia.api.oauth;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.payasia.api.exception.AppRuntimeException;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AuthenticationResultDTO;
import com.payasia.common.util.PayAsiaConstantEnum;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.logic.LoginLogic;
import com.payasia.logic.SecurityLogic;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider{
   
	
	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	/** The privilege master dao. */
	@Resource
	PrivilegeMasterDAO privilegeMasterDAO;

	/** The login logic. */
	@Resource
	LoginLogic loginLogic;

	/** The password policy config master dao. */
	@Resource
	PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;

	/** The security logic. */
	@Resource
	SecurityLogic securityLogic;

	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 * authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = UserContext.getLoginId();
		String password = UserContext.getPassword();
		String companyCode = UserContext.getCompanyCode();
		String userIPAddress = UserContext.getIpAddress();

		String companyUserArr[] = username.split(PayAsiaConstants.COMPANY_USERID_DELIMETER);

		if (companyUserArr.length > 1) {
			companyCode = companyUserArr[0];
			username = companyUserArr[1];
		}

		if (StringUtils.isBlank(companyCode)) {
			companyCode = loginLogic.getDefaultPayAsiaCompanyCode();
		}

		if (StringUtils.isEmpty(companyCode)) {
			throw new AppRuntimeException("Please enable your browser's cookies");
		}

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			throw new AppRuntimeException("Username or Password can not be empty");
		}
		Company company = companyDAO.findByCompanyCode(companyCode, null);
		if (!company.isActive()) {
			throw new AppRuntimeException(
					"The company portal you are accessing is inactive. Please contact your HR administrator");
		}

		Employee employeeVO = null;
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(company.getCompanyId());
		/*
		 * Authenticate Email as Login Name based on HrisPreference Configuration
		 */
		if (hrisPreferenceVO != null && hrisPreferenceVO.isUseEmailAndEmployeeNumberForLogin()) {
			List<Employee> employeeListVO = employeeDAO.getEmployeeByEmail(username, companyCode);
			if (employeeListVO.size() > 1) {
				throw new AppRuntimeException("Invalid login configuration, Please contact administrator");
			}
			if (employeeListVO.size() > 0) {
				employeeVO = employeeListVO.get(0);
			}
		}
		if (employeeVO == null) {
			employeeVO = employeeDAO.getEmployeeByLoginName(username, companyCode);
		}

		AuthenticationResultDTO authenticated = authenticate(employeeVO, password, companyCode);

		if (authenticated.getIsInvalidLoginAttemptsExceeded()) {
			throw new AppRuntimeException("Invalid attempt exceeded, Please reset");
		}

		if (!authenticated.getIsAuthenticated()) {
			throw new AppRuntimeException("Invalid username or password");
		}

		boolean userEnabled = isUserActive(employeeVO);
		
		if (!userEnabled) {
			throw new AppRuntimeException("Your account is disabled");
		}

		boolean status = loginLogic.isEmpLoginHistoryExist(employeeVO.getEmployeeId());

		if (status) {
		  loginLogic.saveLoginHistory(employeeVO, userIPAddress, PayAsiaConstantEnum.LoginMode.PORTAL.getMode());
		}

		UserContext.setUserId(String.valueOf(employeeVO.getEmployeeId()));
		
		List<GrantedAuthority> grantedAuthorities = getUserPrivilege(employeeVO, company);
		UserAuthenticationToken auth = new UserAuthenticationToken(authentication.getPrincipal(),authentication.getCredentials(), grantedAuthorities,employeeVO.getEmployeeId());
        return auth;
	}

	/**
	 * Authenticate Login user .
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param companyCode
	 *            the company code
	 * @return true, if successful
	 */
	private AuthenticationResultDTO authenticate(Employee employeeVO, String password, String companyCode)
			throws AuthenticationException {
		AuthenticationResultDTO authenticationResultDTO = new AuthenticationResultDTO();

		boolean isAuthenticated = false;
		boolean isInvalidLoginAttemptsExceeded = false;

		EmployeeLoginDetail employeeLoginDetailVO = null;
		if (employeeVO != null) {
			employeeLoginDetailVO = employeeLoginDetailDAO.findByEmployeeId(employeeVO.getEmployeeId());
			String salt = employeeLoginDetailVO.getSalt();

			isInvalidLoginAttemptsExceeded = isInvalidLoginAttemptsExceeded(employeeVO, employeeLoginDetailVO,
					companyCode);

			isAuthenticated = securityLogic.isPasswordValid(employeeLoginDetailVO.getPassword(), password, salt);

			if (isAuthenticated && !isInvalidLoginAttemptsExceeded) {

				EmployeeLoginDetail employeeLoginDetail = employeeLoginDetailVO;
				employeeLoginDetail.setInvalidLoginAttempt(0);
				employeeLoginDetailDAO.update(employeeLoginDetail);
			} else {
				updateInvalidLoginAttempts(employeeLoginDetailVO);

			}

		}
		authenticationResultDTO.setIsInvalidLoginAttemptsExceeded(isInvalidLoginAttemptsExceeded);
		authenticationResultDTO.setIsAuthenticated(isAuthenticated);

		return authenticationResultDTO;
	}

	/**
	 * Update invalid login attempts.
	 * 
	 * @param employeeVO
	 *            the employee vo
	 */
	private void updateInvalidLoginAttempts(EmployeeLoginDetail employeeLoginDetailVO) {
		Integer invalidLoginAttempts = employeeLoginDetailVO.getInvalidLoginAttempt();
		if (invalidLoginAttempts == null) {
			invalidLoginAttempts = 1;
		} else {
			invalidLoginAttempts++;
		}
		EmployeeLoginDetail employeeLoginDetail = employeeLoginDetailVO;
		employeeLoginDetail.setInvalidLoginAttempt(invalidLoginAttempts);
		employeeLoginDetailDAO.update(employeeLoginDetail);
	}

	/**
	 * Checks if is invalid login attempts exceeded.
	 * 
	 * @param employeeVO
	 *            the employee vo
	 * @return true, if is invalid login attempts exceeded
	 */
	private boolean isInvalidLoginAttemptsExceeded(Employee employeeVO, EmployeeLoginDetail employeeLoginDetailVO,
			String companyCode) {
		boolean status = false;
		PasswordPolicyConfigMaster passwordPolicyConfigMasterVO = passwordPolicyConfigMasterDAO
				.findByConditionCompanyCode(companyCode);
		if (passwordPolicyConfigMasterVO == null || !passwordPolicyConfigMasterVO.isEnablePwdPolicy()) {
			return false;
		}

		Integer invalidLoginAttempts = passwordPolicyConfigMasterVO.getAllowedInvalidAttempts();
		if (invalidLoginAttempts != 0) {
			if (employeeLoginDetailVO.getInvalidLoginAttempt() != null
					&& employeeLoginDetailVO.getInvalidLoginAttempt() != 0) {
				if (employeeLoginDetailVO.getInvalidLoginAttempt() >= invalidLoginAttempts) {
					status = true;
				}
			}

		}
		return status;
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
	 * Gets the user details.
	 * 
	 * @param username
	 *            the username
	 * @param companyCode
	 *            the company code
	 * @return the user details
	 */
	private Employee getUserDetails(String username, String companyCode) {
		Employee employeeVO = employeeDAO.getEmployeeByLoginName(username, companyCode);

		return employeeVO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.authentication.AuthenticationProvider#supports
	 * (java.lang.Class)
	 */
	/**
	 * Supports.
	 * 
	 * @param authentication
	 *            the authentication
	 * @return true, if successful
	 */
	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
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
