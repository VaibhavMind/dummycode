package com.payasia.logic;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.ActivationDTO;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.Menu;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.ForgotPasswordForm;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;

/**
 * The Interface LoginLogic.
 *
 * @author vivekjain
 */
/**
 * The Interface LoginLogic.
 */

@Transactional
public interface LoginLogic {

	/**
	 * purpose : get EmployeeId By LoginName.
	 * 
	 * @param loginName
	 *            the login name
	 * @param companyCode
	 *            the company code
	 * @return the employee id by login name
	 */
	Long getEmployeeIdByLoginName(String loginName, String companyCode);

	/**
	 * purpose : get Number Of Open Tabs Count.
	 * 
	 * @return the number of open tabs
	 */
	String getNumberOfOpenTabs();

	/**
	 * purpose : get Default PayAsia CompanyCode.
	 * 
	 * @return the default pay asia company code
	 */
	String getDefaultPayAsiaCompanyCode();

	/**
	 * purpose : get Company details.
	 * 
	 * @param loginName
	 *            the login name
	 * @param companyCode
	 *            the company code
	 * @return CompanyForm
	 */
	CompanyForm getCompany(String loginName, String companyCode);

	/**
	 * purpose : get Login Page CompanyLogo.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param defaultImagePath
	 *            the default image path
	 * @return Image Logo with byte Array
	 */
	byte[] getLoginPageCompanyLogo(String companyCode, String defaultImagePath);

	/**
	 * purpose : get Logo image Width and Height.
	 * 
	 * @param companyCode
	 *            the company code
	 * @param defaultImagePath
	 *            the default image path
	 * @return the logo width height
	 */
	String getLogoWidthHeight(String companyCode, String defaultImagePath);

	/**
	 * Gets the employee login history status.
	 * 
	 * @param String
	 *            the userName
	 * @param String
	 *            the companyCode
	 * @return the employee login history status
	 */
	boolean getEmployeeLoginHistoryStatus(String userName, String companyCode);

	/**
	 * Gets the email preference based on company code.
	 * 
	 * @param companyCode
	 *            the company code
	 * @return the email preference
	 */
	String getEmailPreference(String companyCode);

	/**
	 * Check company code based on subDomain.
	 * 
	 * @param subdomain
	 *            the subdomain
	 * @return the boolean
	 */
	Boolean checkCompanyCode(String subdomain);

	/**
	 * Check max pwd age exceeded.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @param companyId
	 *            the company id
	 * @return true, if successful
	 */
	boolean checkMaxPwdAgeExceeded(Long employeeId, Long companyId);

	/**
	 * Check is password reseted based on employeeId and companyId.
	 * 
	 * @param employeeId
	 *            the employee id
	 * @return true, if successful
	 */
	boolean checkIsPasswordReseted(Long employeeId);

	ActivationDTO getCompanyCode(String activationCode, String userName);

	/**
	 * 
	 * @param employeeId
	 * @return
	 */
	boolean isEmpLoginHistoryExist(long employeeId);


	/**
	 * @param companyCode
	 * @return
	 */
	Long getCompanyId(String companyCode);

	/**
	 * Get Short Company Code
	 * 
	 * @param companyCode
	 * @return ShortcompanyCode
	 */
	String getShortCompanyCode(String companyCode);

	String getEmployeeNumberById(Long employeeId);

	/**
	 * Purpose: Set User Privileges on switch role i.e Employee, Admin.
	 * 
	 * @param employeeId
	 * @param companyCode
	 * @param companyId
	 */
	void setUserPrivilegeOnInfoSwitchRole(Long employeeId, String companyCode, Long companyId);

	/**
	 * purpose : send ForgotPasswd Mail to employee.
	 * 
	 * @param ForgotPasswordForm
	 *            the forgotPasswordForm
	 * @return response
	 */
	String sendForgotPasswdMail(ForgotPasswordForm forgotPasswordForm);

	String sendForgotPasswdMail(String username, String companyCode);

	String getCompanyDefaultLanguage(Long companyCode);

	/**
	 * Save login history.
	 *
	 * @param employeeVO
	 *            the employee VO
	 * @param userIPAddress
	 *            the user IP address
	 * @param loginMode
	 *            the login mode
	 */
	void saveLoginHistory(Employee employeeVO, String userIPAddress, String loginMode);

	CompanyModuleDTO getCompanyModuleStatus(String companyCode);



	List<GrantedAuthority> getUserPrivilege(Employee employeeVO, Company company);

	Set<Menu> getUserPrivilege(Long empID, Long companyID, String role);

	byte[] getDefaultLogoImage(String companyCode);

	String getLoginUserEmail(long userID);

}
