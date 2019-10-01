package com.payasia.api.auth.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.auth.AuthenticationApi;
import com.payasia.api.utils.ApiCustomeCode;
import com.payasia.api.utils.ApiErrorMsg;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.dto.AuthenticationResultDTO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.jwt.JwtAuthenticationRequest;
import com.payasia.jwt.JwtAuthenticationToken;
import com.payasia.jwt.JwtUtil;
import com.payasia.logic.LoginLogic;
import com.payasia.logic.SecurityLogic;
import com.payasia.web.security.PayAsiaUserDetails;
import com.payasia.web.util.URLUtils;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH +"/auth")
public class AuthenticationApiImpl implements AuthenticationApi {

       @Resource
       private URLUtils urlUtils;
       @Resource
       private EmployeeDAO employeeDAO;
       @Resource
       private EmployeeRoleMappingDAO employeeRoleMappingDAO;
       @Resource
       private PrivilegeMasterDAO privilegeMasterDAO;
       @Resource
       private LoginLogic loginLogic;
       @Resource
       private PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;
       @Resource
       private SecurityLogic securityLogic;
       @Resource
       private EmployeeLoginDetailDAO employeeLoginDetailDAO;
       @Resource
       private CompanyDAO companyDAO;

       @RequestMapping(value = "/token", method = RequestMethod.POST)
       public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                     HttpServletRequest request){


		final String companyCode = authenticationRequest.getCompanyCode();
		Employee employeeVO = employeeDAO.getEmployeeByLoginName(authenticationRequest.getUsername(), companyCode);
		AuthenticationResultDTO authenticationResultDTO = authenticate(employeeVO, authenticationRequest.getPassword(),companyCode);
	    ApiErrorMsg apiErrorMsg =  null;
		if (authenticationResultDTO.getIsInvalidLoginAttemptsExceeded()) {
			apiErrorMsg = new ApiErrorMsg(ApiCustomeCode.LOGIN_MODULE_VALIDATION_FAIL, "Invalid attempt exceeded, Please reset", true);
			return new ResponseEntity<>(apiErrorMsg, HttpStatus.OK);
		}else if(!authenticationResultDTO.getIsAuthenticated()) {
			apiErrorMsg = new ApiErrorMsg(ApiCustomeCode.LOGIN_MODULE_UNAUTHORIZED, "Invalid username or password", true);
			return new ResponseEntity<>(apiErrorMsg, HttpStatus.OK);
		}
		boolean userEnabled = isUserActive(employeeVO);
		boolean accountNotExpired = userEnabled;
		boolean credentialsNotExpired = userEnabled;
		boolean accountNotLocked = userEnabled;
		Company company = companyDAO.findByCompanyCode(companyCode, null);
		List<GrantedAuthority> grantedAuthorities = getUserPrivilege(employeeVO, company);
		final PayAsiaUserDetails userDetails = new PayAsiaUserDetails(employeeVO.getEmployeeNumber(),
				employeeVO.getEmployeeId(), authenticationRequest.getPassword(), userEnabled, accountNotExpired,
				credentialsNotExpired, accountNotLocked, grantedAuthorities);
		final String token = new JwtUtil().generateToken(userDetails);
		if(StringUtils.isEmpty(token)){
			apiErrorMsg = new ApiErrorMsg(ApiCustomeCode.LOGIN_MODULE_UNAUTHORIZED, "Problem occoured token generation.", true);
			return new ResponseEntity<>(apiErrorMsg, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(new JwtAuthenticationToken(token), HttpStatus.OK);
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
