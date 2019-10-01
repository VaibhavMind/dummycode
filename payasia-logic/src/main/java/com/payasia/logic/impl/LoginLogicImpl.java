package com.payasia.logic.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.ActivationDTO;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.Menu;
import com.payasia.common.dto.ModulePrivileges;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.ForgotPasswordForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.ImageUtils;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.AppConfigMasterDAO;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.CoherentOvertimeApplicationReviewerDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyGroupDAO;
import com.payasia.dao.CompanyLogoDAO;
import com.payasia.dao.CompanyModuleMappingDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeActivationCodeDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHRISReviewerDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.EmployeeLoginHistoryDAO;
import com.payasia.dao.EmployeePasswordChangeHistoryDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.ForgotPasswordTokenDAO;
import com.payasia.dao.HRISChangeRequestReviewerDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.SsoConfigurationDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.bean.AppConfigMaster;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyLogo;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeActivationCode;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeHRISReviewer;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLoginHistory;
import com.payasia.dao.bean.EmployeePasswordChangeHistory;
import com.payasia.dao.bean.EmployeeRoleMapping;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.ForgotPasswordToken;
import com.payasia.dao.bean.HRISChangeRequestReviewer;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.dao.bean.PrivilegeMaster;
import com.payasia.dao.bean.RoleMaster;
import com.payasia.dao.bean.SsoConfiguration;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.LoginLogic;
import com.payasia.logic.PasswordPolicyLogic;
import com.payasia.logic.SecurityLogic;
import com.payasia.logic.SwitchCompanyLogic;

// TODO: Auto-generated Javadoc
/**
 * The Class LoginLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class LoginLogicImpl implements LoginLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(LoginLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The company logo max width. */
	@Value("#{payasiaptProperties['payasia.login.company.logo.max.width']}")
	private String COMPANY_LOGO_MAX_WIDTH;

	/** The company logo max height. */
	@Value("#{payasiaptProperties['payasia.login.company.logo.max.height']}")
	private String COMPANY_LOGO_MAX_HEIGHT;

	@Resource
	ModuleMasterDAO moduleMasterDAO;
	@Resource
	SsoConfigurationDAO ssoConfigurationDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The company logo dao. */
	@Resource
	CompanyLogoDAO companyLogoDAO;

	/** The email preference master dao. */
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	/** The email template sub category master dao. */
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;

	/** The email template dao. */
	@Resource
	EmailTemplateDAO emailTemplateDAO;

	/** The pay asia mail utils. */
	@Resource
	PayAsiaMailUtils payAsiaMailUtils;

	/** The app config master dao. */
	@Resource
	AppConfigMasterDAO appConfigMasterDAO;

	/** The employee login history dao. */
	@Resource
	EmployeeLoginHistoryDAO employeeLoginHistoryDAO;

	/** The security logic. */
	@Resource
	SecurityLogic securityLogic;

	/** The password policy config master dao. */
	@Resource
	PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;

	/** The employee password change history dao. */
	@Resource
	EmployeePasswordChangeHistoryDAO employeePasswordChangeHistoryDAO;

	/** The password policy logic. */
	@Resource
	PasswordPolicyLogic passwordPolicyLogic;

	/** The employee login detail DAO. */
	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;

	/** The employee activation code DAO. */
	@Resource
	EmployeeActivationCodeDAO employeeActivationCodeDAO;

	/** The employee role mapping DAO. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	/** The privilege master DAO. */
	@Resource
	PrivilegeMasterDAO privilegeMasterDAO;

	/** The employee leave reviewer DAO. */
	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;

	/** The leave application reviewer DAO. */
	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewerDAO;

	/** The employee claim reviewer DAO. */
	@Resource
	EmployeeClaimReviewerDAO employeeClaimReviewerDAO;

	/** The claim application reviewer DAO. */
	@Resource
	ClaimApplicationReviewerDAO claimApplicationReviewerDAO;

	/** The employee HRIS reviewer DAO. */
	@Resource
	EmployeeHRISReviewerDAO employeeHRISReviewerDAO;

	/** The hris change request reviewer DAO. */
	@Resource
	HRISChangeRequestReviewerDAO hrisChangeRequestReviewerDAO;

	/** The forgot password token DAO. */
	@Resource
	ForgotPasswordTokenDAO forgotPasswordTokenDAO;

	/** The company group DAO. */
	@Resource
	CompanyGroupDAO companyGroupDAO;

	/** The hris preference DAO. */
	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;

	/** The lundin employee reviewer DAO. */
	@Resource
	EmployeeTimesheetReviewerDAO lundinEmployeeReviewerDAO;

	@Resource
	TimesheetApplicationReviewerDAO lundinTimesheetReviewerDAO;

	@Resource
	GeneralMailLogic generalMailLogic;

	/** The Switch Company logic. */
	@Resource
	SwitchCompanyLogic switchCompanyLogic;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	/** The lion timesheet application reviewer DAO. */
	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetApplicationReviewerDAO;

	/** The coherent overtime application reviewer DAO. */
	@Resource
	CoherentOvertimeApplicationReviewerDAO coherentOvertimeApplicationReviewerDAO;

	/** The file utils. */
	@Resource
	FileUtils fileUtils;

	/** The awss 3 logic impl. */
	@Resource
	AWSS3Logic awss3LogicImpl;

	/** The app deploy location. */
	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;
	
	@Resource
	private CompanyModuleMappingDAO companyModuleMappingDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getCompany(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CompanyForm getCompany(String loginName, String companyCode) {
		Employee employeeVO = employeeDAO.getEmployeeByLoginName(loginName, companyCode);
		if (employeeVO != null) {
			CompanyForm companyForm = new CompanyForm();
			companyForm.setCompanyId(employeeVO.getCompany().getCompanyId());
			companyForm.setCompanyName(employeeVO.getCompany().getCompanyName());
			companyForm.setCompanyCode(employeeVO.getCompany().getCompanyCode());
			companyForm.setDateFormat(employeeVO.getCompany().getDateFormat());
			companyForm.setGmtOffset(employeeVO.getCompany().getTimeZoneMaster().getGmtOffset());
			companyForm.setIsTwoFactorAuth(employeeVO.getCompany().getIsTwoFactorAuth()!=null?employeeVO.getCompany().getIsTwoFactorAuth():false);
			Set<CompanyModuleMapping> companyModuleList = employeeVO.getCompany().getCompanyModuleMappings();
			for (CompanyModuleMapping companyModuleMapping : companyModuleList) {

				if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
					companyForm.setHasClaimModule(true);
				} else if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
					companyForm.setHasLeaveModule(true);
				} else if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_HRIS)) {
					companyForm.setHasHrisModule(true);
				} else if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_MOBILE)) {
					companyForm.setHasMobileModule(true);
				} else if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET)) {
					companyForm.setHasLundinTimesheetModule(true);
				} else if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET)) {
					companyForm.setHasLionTimesheetModule(true);
				} else if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET)) {
					companyForm.setHasCoherentTimesheetModule(true);
				}

			}

			return companyForm;
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.LoginLogic#getEmployeeIdByLoginName(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Long getEmployeeIdByLoginName(String loginName, String companyCode) {

		Employee employeeVO = employeeDAO.getEmployeeByLoginName(loginName, companyCode);
		if (employeeVO != null) {
			Long employeeId = employeeVO.getEmployeeId();
			return employeeId;
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getEmployeeNumberById(java.lang.Long)
	 */
	@Override
	public String getEmployeeNumberById(Long employeeId) {
		String employeeNumber;
		Employee employeeVO = employeeDAO.findById(employeeId);
		if (employeeVO != null) {
			employeeNumber = employeeVO.getEmployeeNumber();
			return employeeNumber;
		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#sendForgotPasswdMail(java.lang.String)
	 */
	@Override
	public String sendForgotPasswdMail(ForgotPasswordForm forgotPasswordForm) {
		Employee employeeVO = null;
		Company company = companyDAO.findByCompanyCode(forgotPasswordForm.getCompanycode(), null);
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(company.getCompanyId());

		String returnMessage = "";
		if (forgotPasswordForm.isDontKnowMyPassword()) {
			returnMessage = "password.reset.link.sent";
		} else {
			returnMessage = "username.forgot.link.sent";
		}

		if (forgotPasswordForm.isDontKnowMyPassword()) {
			List<Employee> employeeListVO = employeeDAO.getEmpByUsernameOrEmailOrFullName(
					forgotPasswordForm.getEmailOrUsernameOrFullName(), company.getCompanyCode());
			if (employeeListVO.size() > 1) {
				return "payasia.invalid.login.credential";
			}
			if (employeeListVO.size() > 0) {
				employeeVO = employeeListVO.get(0);
			}
		}
		if (forgotPasswordForm.isDontKnowMyUsername()) {
			List<Employee> employeeListVO = employeeDAO.getEmpByEmailOrFullName(forgotPasswordForm.getEmailOrFullName(),
					company.getCompanyCode());
			if (employeeListVO.size() > 1) {
				return "payasia.invalid.login.credential";
			}
			if (employeeListVO.size() > 0) {
				employeeVO = employeeListVO.get(0);
			}
		}

		if (employeeVO == null) {
			return "forgot.user.not.found";
		} else {
			if (!StringUtils.isNotBlank(employeeVO.getEmail())) {
				return "forgot.user.not.found";
			}
		}

		if (!employeeVO.isStatus()) {
			return "your.account.is.disabled";
		}

		File emailTemplate = null;
		FileOutputStream fos = null;
		File emailSubjectTemplate = null;
		FileOutputStream fosSubject = null;

		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

		List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(PayAsiaConstants.PAYASIA_SUB_CATEGORY_PASSWORD,
				EmailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = null;
		String returnMailTemplateMessage = "";
		if (forgotPasswordForm.isDontKnowMyPassword()) {
			emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(
					PayAsiaConstants.PAYASIA_FORGOT_PASSWORD_NAME, subCategoryId,
					employeeVO.getCompany().getCompanyId());
			if (emailTemplateVO == null) {
				returnMailTemplateMessage = "forgot.password.template.is.not.defined";
			}
		}

		if (forgotPasswordForm.isDontKnowMyUsername()) {
			emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(
					PayAsiaConstants.PAYASIA_FORGOT_USERNAME, subCategoryId, employeeVO.getCompany().getCompanyId());
			if (emailTemplateVO == null) {
				returnMailTemplateMessage = "forgot.username.template.is.not.defined";
			}
		}
		if (emailTemplateVO == null) {
			return returnMailTemplateMessage;
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(employeeVO.getCompany().getCompanyId());
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String token = PasswordUtils.getRandomPassword(PayAsiaConstants.PAYASIA_FORGOT_PASSWORD_TOKEN_SIZE);

		String mailBody = emailTemplateVO.getBody();
		String mailSubject = emailTemplateVO.getSubject();
		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put("firstName", employeeVO.getFirstName());
		modelMap.put("First_Name", employeeVO.getFirstName());
		modelMap.put("employeeId", employeeVO.getEmployeeNumber());
		modelMap.put("Employee_Number", employeeVO.getEmployeeNumber());
		modelMap.put("userName", employeeVO.getEmployeeLoginDetail().getLoginName());

		if (forgotPasswordForm.isDontKnowMyUsername()) {
			if (hrisPreferenceVO != null && hrisPreferenceVO.isUseEmailAndEmployeeNumberForLogin()) {
				modelMap.put("Username", employeeVO.getEmail());
			} else {
				modelMap.put("Username", employeeVO.getEmployeeNumber());
			}
		} else {
			modelMap.put("Username", employeeVO.getEmployeeLoginDetail().getLoginName());
		}
		modelMap.put("companyName", employeeVO.getCompany().getCompanyName());
		modelMap.put("Company_Name", employeeVO.getCompany().getCompanyName());
		if (StringUtils.isNotBlank(employeeVO.getEmployeeLoginDetail().getPassword())) {

			modelMap.put("password", "");
			modelMap.put("Password", "");
		}
		if (!StringUtils.isNotBlank(employeeVO.getEmployeeLoginDetail().getPassword())) {
			modelMap.put("password", "");
			modelMap.put("Password", "");
		}
		if (StringUtils.isNotBlank(employeeVO.getMiddleName())) {
			modelMap.put("middleName", employeeVO.getMiddleName());
			modelMap.put("Middle_Name", employeeVO.getMiddleName());
		}
		if (!StringUtils.isNotBlank(employeeVO.getMiddleName())) {
			modelMap.put("middleName", "");
			modelMap.put("Middle_Name", "");
		}
		if (StringUtils.isNotBlank(employeeVO.getLastName())) {
			modelMap.put("lastName", employeeVO.getLastName());
			modelMap.put("Last_Name", employeeVO.getLastName());
		}
		if (!StringUtils.isNotBlank(employeeVO.getLastName())) {
			modelMap.put("lastName", "");
			modelMap.put("Last_Name", "");
		}
		if (StringUtils.isNotBlank(employeeVO.getEmail())) {
			modelMap.put("email", employeeVO.getEmail());
			modelMap.put("Email", employeeVO.getEmail());
		}
		if (!StringUtils.isNotBlank(employeeVO.getEmail())) {
			modelMap.put("email", "");
			modelMap.put("Email", "");
		}
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME, generalMailLogic.getEmployeeName(employeeVO));

		if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())
				&& forgotPasswordForm.isDontKnowMyPassword()) {
			String resetPasswordURL = "";
			if (emailPreferenceMasterVO.getCompanyUrl().endsWith("/")) {
				resetPasswordURL += emailPreferenceMasterVO.getCompanyUrl() + PayAsiaConstants.PAYASIA_URL_CONSTANT+ "/login/resetPassword?reqParam=" + token;
			} else {
				resetPasswordURL += emailPreferenceMasterVO.getCompanyUrl() + "/"+PayAsiaConstants.PAYASIA_URL_CONSTANT + "/login/resetPassword?reqParam=" + token;
			}

			String link = "<a href='" + resetPasswordURL + "'>" + resetPasswordURL + "</a>";
			modelMap.put(PayAsiaConstants.URL, link);
		} else {
			modelMap.put(PayAsiaConstants.URL, "");
		}

		try {
			byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
			byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "forgotPasswordTemplate" + uniqueId + ".vm");
			emailTemplate.deleteOnExit();
			emailSubjectTemplate = new File(
					PAYASIA_TEMP_PATH + "//" + "forgotPasswordSubjectTemplate" + "_" + uniqueId + ".vm");
			emailSubjectTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();
			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			if (StringUtils.isNotBlank(employeeVO.getEmail())) {
				payAsiaEmailTO.addMailTo(employeeVO.getEmail());
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
				payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
				payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
			}

			if (StringUtils.isNotBlank(employeeVO.getEmail())) {
				payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO.getContactEmail());
			}

			if (forgotPasswordForm.isDontKnowMyPassword()) {
				ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
				forgotPasswordToken.setEmployee(employeeVO);
				forgotPasswordToken.setToken(token);
				forgotPasswordToken.setActive(true);
				forgotPasswordTokenDAO.save(forgotPasswordToken);
			}

			payAsiaMailUtils.sendEmail(modelMap,
					emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
					emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true,
					payAsiaEmailTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();

			}

		}
		return returnMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#sendForgotPasswdMail(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String sendForgotPasswdMail(String username, String companyCode) {
		Employee employeeVO = null;
		Company company = companyDAO.findByCompanyCode(companyCode, null);
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(company.getCompanyId());

		/*
		 * Authenticate Email as Login Name based on HrisPreference
		 * Configuration
		 */
		if (hrisPreferenceVO != null && hrisPreferenceVO.isUseEmailAndEmployeeNumberForLogin()) {
			List<Employee> employeeListVO = employeeDAO.getEmployeeByEmail(username, companyCode);
			if (employeeListVO.size() > 1) {
				return "payasia.invalid.login.credential";
			}
			if (employeeListVO.size() > 0) {
				employeeVO = employeeListVO.get(0);
			}
		}
		if (employeeVO == null) {
			employeeVO = employeeDAO.getEmployeeByLoginName(username, companyCode);
		}

		// Employee employeeVO = employeeDAO.getEmployeeByEmpNumAndCompCode(
		// employeeNum, companyCode);

		if (employeeVO == null) {
			return "password.reset.link.sent";
		} else {
			if (!StringUtils.isNotBlank(employeeVO.getEmail())) {
				return "password.reset.link.sent";
			}
		}

		if (!employeeVO.isStatus()) {
			return "password.reset.link.sent";
		}

		File emailTemplate = null;
		FileOutputStream fos = null;
		File emailSubjectTemplate = null;
		FileOutputStream fosSubject = null;

		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

		List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(PayAsiaConstants.PAYASIA_SUB_CATEGORY_PASSWORD,
				EmailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(
				PayAsiaConstants.PAYASIA_FORGOT_PASSWORD_NAME, subCategoryId, employeeVO.getCompany().getCompanyId());
		if (emailTemplateVO == null) {
			return "password.reset.link.sent";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(employeeVO.getCompany().getCompanyId());
		if (emailPreferenceMasterVO == null) {
			return "password.reset.link.sent";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "password.reset.link.sent";
		}

		String token = PasswordUtils.getRandomPassword(PayAsiaConstants.PAYASIA_FORGOT_PASSWORD_TOKEN_SIZE);

		String mailBody = emailTemplateVO.getBody();
		String mailSubject = emailTemplateVO.getSubject();
		Map<String, Object> modelMap = new HashMap<String, Object>();

		modelMap.put("firstName", employeeVO.getFirstName());
		modelMap.put("First_Name", employeeVO.getFirstName());
		modelMap.put("employeeId", employeeVO.getEmployeeNumber());
		modelMap.put("Employee_Number", employeeVO.getEmployeeNumber());
		modelMap.put("userName", employeeVO.getEmployeeLoginDetail().getLoginName());
		modelMap.put("Username", employeeVO.getEmployeeLoginDetail().getLoginName());
		modelMap.put("companyName", employeeVO.getCompany().getCompanyName());
		modelMap.put("Company_Name", employeeVO.getCompany().getCompanyName());
		if (StringUtils.isNotBlank(employeeVO.getMiddleName())) {
			modelMap.put("middleName", employeeVO.getMiddleName());
			modelMap.put("Middle_Name", employeeVO.getMiddleName());
		}
		if (!StringUtils.isNotBlank(employeeVO.getMiddleName())) {
			modelMap.put("middleName", "");
			modelMap.put("Middle_Name", "");
		}
		if (StringUtils.isNotBlank(employeeVO.getLastName())) {
			modelMap.put("lastName", employeeVO.getLastName());
			modelMap.put("Last_Name", employeeVO.getLastName());
		}
		if (!StringUtils.isNotBlank(employeeVO.getLastName())) {
			modelMap.put("lastName", "");
			modelMap.put("Last_Name", "");
		}
		if (StringUtils.isNotBlank(employeeVO.getEmail())) {
			modelMap.put("email", employeeVO.getEmail());
			modelMap.put("Email", employeeVO.getEmail());
		}
		if (!StringUtils.isNotBlank(employeeVO.getEmail())) {
			modelMap.put("email", "");
			modelMap.put("Email", "");
		}
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME, generalMailLogic.getEmployeeName(employeeVO));

		if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
			String resetPasswordURL = "";
			if (emailPreferenceMasterVO.getCompanyUrl().endsWith("/")) {
				resetPasswordURL += emailPreferenceMasterVO.getCompanyUrl() +  PayAsiaConstants.PAYASIA_URL_CONSTANT +  "/login/resetPassword?reqParam=" + token;
			} else {
				resetPasswordURL += emailPreferenceMasterVO.getCompanyUrl() + "/" + PayAsiaConstants.PAYASIA_URL_CONSTANT + "/login/resetPassword?reqParam=" + token;
			}

			String link = "<a href='" + resetPasswordURL + "'>" + resetPasswordURL + "</a>";
			modelMap.put(PayAsiaConstants.URL, link);
		} else {
			modelMap.put(PayAsiaConstants.URL, "");
		}

		try {
			byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
			byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "forgotPasswordTemplate" + uniqueId + ".vm");
			emailTemplate.deleteOnExit();
			emailSubjectTemplate = new File(
					PAYASIA_TEMP_PATH + "//" + "forgotPasswordSubjectTemplate" + "_" + uniqueId + ".vm");
			emailSubjectTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();
			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			if (StringUtils.isNotBlank(employeeVO.getEmail())) {
				payAsiaEmailTO.addMailTo(employeeVO.getEmail());
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
				payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
				payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
			}

			if (StringUtils.isNotBlank(employeeVO.getEmail())) {
				payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO.getContactEmail());
			}

			ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
			forgotPasswordToken.setEmployee(employeeVO);
			forgotPasswordToken.setToken(token);
			forgotPasswordToken.setActive(true);
			forgotPasswordTokenDAO.save(forgotPasswordToken);
			payAsiaMailUtils.sendEmail(modelMap,
					emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
					emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true,
					payAsiaEmailTO);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();

			}

		}
		return "password.reset.link.sent";

	}

	/**
	 * Gets the sub category id.
	 * 
	 * @param subCategoryName
	 *            the sub category name
	 * @param EmailTemplateSubCategoryMasterList
	 *            the email template sub category master list
	 * @return the sub category id
	 */
	public Long getSubCategoryId(String subCategoryName,
			List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList) {
		for (EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster : EmailTemplateSubCategoryMasterList) {
			if ((subCategoryName.toUpperCase())
					.equals(emailTemplateSubCategoryMaster.getSubCategoryName().toUpperCase())) {
				return emailTemplateSubCategoryMaster.getEmailTemplateSubCategoryId();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getNumberOfOpenTabs()
	 */
	@Override
	public String getNumberOfOpenTabs() {
		AppConfigMaster appConfigMaster = appConfigMasterDAO.findByName(PayAsiaConstants.NUMBER_OF_OPEN_TABS);
		if (appConfigMaster != null) {
			String NoOfOpenTabs = appConfigMaster.getParamValue();
			return NoOfOpenTabs;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getDefaultPayAsiaCompanyCode()
	 */
	@Override
	public String getDefaultPayAsiaCompanyCode() {
		String companyCode = null;
		Company companyVO = companyDAO.findByCompanyName(PayAsiaConstants.PAYASIA_COMPANY_NAME.toUpperCase());
		if (companyVO != null) {
			companyCode = companyVO.getCompanyCode();
		}
		return companyCode;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.LoginLogic#getLoginPageCompanyLogo(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public byte[] getLoginPageCompanyLogo(String companyCode, String defaultImagePath) {
		CompanyLogo companyLogoData = companyLogoDAO.findByConditionCompanyCode(companyCode);
		byte[] originalByteFile = null;
		byte[] resizedImageInByte = null;
		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyLogoData.getCompany().getCompanyId(), PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
					String.valueOf(companyLogoData.getCompanyLogoId()), null, null, null,
					PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);

			try {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					originalByteFile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
				} else {
					originalByteFile = Files.readAllBytes(file.toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}

			int imageNameIndex = companyLogoData.getImageName().lastIndexOf(".");
			String imageType = companyLogoData.getImageName().substring(imageNameIndex + 1);
			if (originalByteFile == null)
				originalByteFile = new byte[0]; // Added temprory by Mayur
			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);
				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(originalBufferedImage, maxWidth, maxHeight,
						true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, imageType, baos);
				baos.flush();
				resizedImageInByte = baos.toByteArray();
				baos.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		} else {
			String defaultImgPath = defaultImagePath;

			//File defaultImageFile = new File(defaultImgPath);
			try {
				//originalByteFile = PasswordUtils.getBytesFromFile(defaultImageFile);
				originalByteFile = org.apache.commons.io.IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(defaultImgPath));
				
				InputStream inputStream = new ByteArrayInputStream(originalByteFile);

				BufferedImage originalBufferedImage = ImageIO.read(inputStream);

				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(originalBufferedImage, maxWidth, maxHeight,
						true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, "png", baos);
				baos.flush();
				resizedImageInByte = baos.toByteArray();
				baos.close();

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return resizedImageInByte;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#saveLoginHistory(com.payasia.dao.bean.
	 * Employee, java.lang.String, java.lang.String)
	 */
	@Override
	public void saveLoginHistory(Employee employeeVO, String userIPAddress, String loginMode) {
		EmployeeLoginHistory employeeLoginHistory = new EmployeeLoginHistory();
		if (employeeVO != null) {
			employeeLoginHistory.setEmployee(employeeVO);
			employeeLoginHistory.setLoginMode(loginMode);
			employeeLoginHistory.setLoggedInDate(DateUtils.getCurrentTimestampWithTime());

			if (userIPAddress != null) {
				employeeLoginHistory.setIpAddress(userIPAddress);
			}

			employeeLoginHistoryDAO.save(employeeLoginHistory);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.LoginLogic#getEmployeeLoginHistoryStatus(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public boolean getEmployeeLoginHistoryStatus(String userName, String companyCode) {
		Employee employeeVO = employeeDAO.getEmployeeByLoginName(userName, companyCode);
		if (employeeVO != null) {
			List<EmployeeLoginHistory> employeeLoginHistoryList = employeeLoginHistoryDAO
					.findByEmployeeId(employeeVO.getEmployeeId());
			if ((!employeeLoginHistoryList.isEmpty())) {
				return true;
			}
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#isEmpLoginHistoryExist(long)
	 */
	@Override
	public boolean isEmpLoginHistoryExist(long employeeId) {
		return employeeLoginHistoryDAO.isEmployeeLoginHistoryExist(employeeId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getLogoWidthHeight(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getLogoWidthHeight(String companyCode, String defaultImagePath) {
		CompanyLogo companyLogoData = companyLogoDAO.findByConditionCompanyCode(companyCode);
		byte[] originalByteFile = null;

		String logoHeight = null;
		String logoWidth = null;
		if (companyLogoData != null) {
			/*
			 * String filePath = "company/" +
			 * companyLogoData.getCompany().getCompanyId() + "/" +
			 * PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME + "/" +
			 * companyLogoData.getCompanyLogoId();
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyLogoData.getCompany().getCompanyId(), PayAsiaConstants.COMPANY_LOGO_DIRECTORY_NAME,
					String.valueOf(companyLogoData.getCompanyLogoId()), null, null, null,
					PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			File file = new File(filePath);

			try {
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					originalByteFile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
				} else {
					originalByteFile = Files.readAllBytes(file.toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
			int imageNameIndex = companyLogoData.getImageName().lastIndexOf(".");
			String imageType = companyLogoData.getImageName().substring(imageNameIndex + 1);

			InputStream inputStream = new ByteArrayInputStream(originalByteFile);
			try {
				BufferedImage originalBufferedImage = ImageIO.read(inputStream);
				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(originalBufferedImage, maxWidth, maxHeight,
						true);

				logoHeight = String.valueOf(convertedBufferdImage.getHeight());
				logoWidth = String.valueOf(convertedBufferdImage.getWidth());

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, imageType, baos);
				baos.flush();
				baos.close();
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		} else {
			String defaultImgPath = defaultImagePath;

			File defaultImageFile = new File(defaultImgPath);
			try {
				originalByteFile = PasswordUtils.getBytesFromFile(defaultImageFile);

				InputStream inputStream = new ByteArrayInputStream(originalByteFile);

				BufferedImage originalBufferedImage = ImageIO.read(inputStream);

				int maxWidth = Integer.parseInt(COMPANY_LOGO_MAX_WIDTH);
				int maxHeight = Integer.parseInt(COMPANY_LOGO_MAX_HEIGHT);

				BufferedImage convertedBufferdImage = ImageUtils.resize(originalBufferedImage, maxWidth, maxHeight,
						true);

				logoHeight = String.valueOf(convertedBufferdImage.getHeight());
				logoWidth = String.valueOf(convertedBufferdImage.getWidth());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				ImageIO.write(convertedBufferdImage, "png", baos);
				baos.flush();

				baos.close();

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return logoHeight + "/" + logoWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getEmailPreference(java.lang.String)
	 */
	@Override
	public String getEmailPreference(String companyCode) {
		Company company = companyDAO.findByCompanyCode(companyCode, null);
		String contactEmail = "";
		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(company.getCompanyId());
		if (emailPreferenceMasterVO == null) {
			return "";
		} else {
			contactEmail = emailPreferenceMasterVO.getContactEmail();
		}
		return contactEmail;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#checkCompanyCode(java.lang.String)
	 */
	@Override
	public Boolean checkCompanyCode(String subdomain) {
		Company company = companyDAO.findByCompanyCode(subdomain, null);
		if (company != null) {
			return true;
		} else {
			return false;

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#checkMaxPwdAgeExceeded(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public boolean checkMaxPwdAgeExceeded(Long employeeId, Long companyId) {
		EmployeePasswordChangeHistory empPasswordChangeHistory = employeePasswordChangeHistoryDAO
				.getPreviousPasswords(employeeId);

		PasswordPolicyConfigMaster passwordPolicyConfigMasterVO = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);

		Boolean isPasswordChangeRequired = passwordPolicyLogic.isPasswordChangeRequired(empPasswordChangeHistory,
				passwordPolicyConfigMasterVO);
			return isPasswordChangeRequired;		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#checkIsPasswordReseted(java.lang.Long)
	 */
	@Override
	public boolean checkIsPasswordReseted(Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		return emp.getEmployeeLoginDetail().getPasswordReset();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getCompanyCode(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ActivationDTO getCompanyCode(String activationCode, String userName) {
		ActivationDTO activationDTO = new ActivationDTO();
		EmployeeActivationCode employeeActivationCode = employeeActivationCodeDAO.findByActivationCode(activationCode,
				userName);
		if (employeeActivationCode == null) {
			return null;		//activationDTO;
		}
		Long companyId = employeeActivationCode.getEmployee().getCompany().getCompanyId();

		activationDTO.setCompanyCode(employeeActivationCode.getEmployee().getCompany().getCompanyCode());
		activationDTO.setEmployeeActivationCodeId(employeeActivationCode.getEmployeeActivationCodeId());
		activationDTO.setCompanyId(companyId);
		SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyId(companyId);
		activationDTO.setSsoEnable(ssoConfiguration == null ? false : ssoConfiguration.getIsEnableSso());
		return activationDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getUserPrivilege(com.payasia.dao.bean.
	 * Employee, com.payasia.dao.bean.Company)
	 */
	@Override
	public List<GrantedAuthority> getUserPrivilege(Employee employeeVO, Company company) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

		List<EmployeeRoleMapping> employeeRoleMappingVOList = employeeRoleMappingDAO
				.findByConditionEmpIdAndCompanyId(company.getCompanyId(), employeeVO.getEmployeeId());
		Set<PrivilegeMaster> empPrivilegeMasterSet = new HashSet<PrivilegeMaster>();
		Set<RoleMaster> roleMasterSet = new HashSet<>();
		if (employeeRoleMappingVOList != null && !employeeRoleMappingVOList.isEmpty()) {
			for (EmployeeRoleMapping employeeRoleMappingVO : employeeRoleMappingVOList) {
				roleMasterSet.add(employeeRoleMappingVO.getRoleMaster());
				empPrivilegeMasterSet.addAll(
						privilegeMasterDAO.getPrivilegesByRole(employeeRoleMappingVO.getRoleMaster().getRoleId()));
			}
		}

		for (RoleMaster roleMaster : roleMasterSet) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleMaster.getRoleName().toUpperCase()));
		}
		for (PrivilegeMaster privilegeMasterVO : empPrivilegeMasterSet) {
			grantedAuthorities.add(new SimpleGrantedAuthority("PRIV_" + privilegeMasterVO.getPrivilegeName()));
		}

		List<EmployeeLeaveReviewer> leaveReviewersList = employeeLeaveReviewerDAO
				.checkEmployeeReviewer(employeeVO.getEmployeeId());
		if (leaveReviewersList != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_LEAVE_MANAGER"));
		} else {
			List<String> leaveStatusList = new ArrayList<>();
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			List<LeaveApplicationReviewer> leaveAppRevsList = leaveApplicationReviewerDAO
					.checkEmployeeReviewer(employeeVO.getEmployeeId(), leaveStatusList);
			if (leaveAppRevsList != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_LEAVE_MANAGER"));
			}
		}

		List<EmployeeClaimReviewer> claimReviewersList = employeeClaimReviewerDAO
				.checkEmployeeClaimReviewer(employeeVO.getEmployeeId());
		if (claimReviewersList != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CLAIM_MANAGER"));
		} else {
			List<String> claimStatusList = new ArrayList<>();
			claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
			claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
			List<ClaimApplicationReviewer> claimAppRevsList = claimApplicationReviewerDAO
					.checkClaimEmployeeReviewer(employeeVO.getEmployeeId(), claimStatusList);
			if (claimAppRevsList != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CLAIM_MANAGER"));
			}
		}

		List<EmployeeHRISReviewer> hrisReviewersList = employeeHRISReviewerDAO
				.checkEmployeeHRISReviewer(employeeVO.getEmployeeId());
		if (hrisReviewersList != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_HRIS_MANAGER"));
		} else {
			List<String> hrisStatusList = new ArrayList<>();
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_SUBMITTED);
			hrisStatusList.add(PayAsiaConstants.HRIS_STATUS_APPROVED);
			List<HRISChangeRequestReviewer> hrisRevsList = hrisChangeRequestReviewerDAO
					.checkHRISEmployeeReviewer(employeeVO.getEmployeeId(), hrisStatusList);
			if (hrisRevsList != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_HRIS_MANAGER"));
			}
		}
		List<EmployeeTimesheetReviewer> lundinReviewersList = lundinEmployeeReviewerDAO
				.checkEmployeeOTReviewer(employeeVO.getEmployeeId());
		if (lundinReviewersList != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_LUNDIN_MANAGER"));
		} else {
			List<String> otStatusList = new ArrayList<>();
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
			List<TimesheetApplicationReviewer> otRevsList = lundinTimesheetReviewerDAO
					.checkOTEmployeeReviewer(employeeVO.getEmployeeId(), otStatusList);
			if (otRevsList != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_LUNDIN_MANAGER"));
			}
		}

		// Lion Manager
		List<EmployeeTimesheetReviewer> lionReviewersList = lundinEmployeeReviewerDAO
				.checkEmployeeOTReviewer(employeeVO.getEmployeeId());
		if (lionReviewersList != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_LION_MANAGER"));
		} else {
			List<String> otStatusList = new ArrayList<>();
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
			List<LionTimesheetApplicationReviewer> otRevsList = lionTimesheetApplicationReviewerDAO
					.checkOTEmployeeReviewer(employeeVO.getEmployeeId(), otStatusList);
			if (otRevsList != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_LION_MANAGER"));
			}
		}

		// Coherent Manager
		if (lundinReviewersList != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_COHERENT_MANAGER"));
		} else {
			List<String> otStatusList = new ArrayList<>();
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
			List<CoherentOvertimeApplicationReviewer> coherentRevsList = coherentOvertimeApplicationReviewerDAO
					.checkOTEmployeeReviewer(employeeVO.getEmployeeId(), otStatusList);
			if (coherentRevsList != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_COHERENT_MANAGER"));
			}
		}

		// PayAsia Manager
		if (lundinReviewersList != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_PAYASIA_MANAGER"));
		} else {
			List<String> otStatusList = new ArrayList<>();
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			otStatusList.add(PayAsiaConstants.LUNDIN_STATUS_APPROVED);
			List<CoherentOvertimeApplicationReviewer> coherentRevsList = coherentOvertimeApplicationReviewerDAO
					.checkOTEmployeeReviewer(employeeVO.getEmployeeId(), otStatusList);
			if (coherentRevsList != null) {
				grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_PAYASIA_MANAGER"));
			}
		}

		// Check modules for the company
		CompanyForm companyInfo = switchCompanyLogic.getCompany(company.getCompanyId());
		if (companyInfo.isHasClaimModule()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("MODULE_CLAIM"));
		}
		if (companyInfo.isHasLeaveModule()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("MODULE_LEAVE"));
		}
		if (companyInfo.isHasHrisModule()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("MODULE_HRIS"));
		}
		if (companyInfo.isHasCoherentTimesheetModule()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("MODULE_COHERENT_TIMESHEET"));
		}
		if (companyInfo.isHasLionTimesheetModule()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("MODULE_LION_TIMESHEET"));
		}
		if (companyInfo.isHasLundinTimesheetModule()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("MODULE_LUNDIN_TIMESHEET"));
		}

		if (companyInfo.isHasMobileModule()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("MODULE_MOBILE"));
		}
		
		// Check isEmployeeChangeWorkflow enabled
		HRISPreference hrisPreference = hrisPreferenceDAO.findByCompanyId(company.getCompanyId());
		if (hrisPreference != null && hrisPreference.isEnableEmployeeChangeWorkflow())
			grantedAuthorities.add(new SimpleGrantedAuthority("PRIV_EMPLOYEE_CHANGE_WORKFLOW"));

		return grantedAuthorities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getCompanyId(java.lang.String)
	 */
	@Override
	public Long getCompanyId(String companyCode) {
		Company company = companyDAO.findByCompanyCode(companyCode, null);
		if (company != null) {
			return company.getCompanyId();
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.LoginLogic#getShortCompanyCode(java.lang.String)
	 */
	@Override
	public String getShortCompanyCode(String companyCode) {
		String shortCompanyCode = "";
		if (StringUtils.isNotBlank(companyCode)) {
			Company companyVO = companyDAO.findByCompanyCode(companyCode, null);
			if (companyVO != null && StringUtils.isNotBlank(companyVO.getShortCompanyCode())) {
				shortCompanyCode = companyVO.getShortCompanyCode();
			}

		}
		return shortCompanyCode;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.LoginLogic#setUserPrivilegeOnInfoSwitchRole(java.lang.
	 * Long, java.lang.String, java.lang.Long)
	 */
	@Override
	public void setUserPrivilegeOnInfoSwitchRole(Long employeeId, String companyCode, Long companyId) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();

		Employee employee = employeeDAO.findById(employeeId);
		Company company = null;
		if (companyId == null) {
			company = companyDAO.findByCompanyCode(companyCode, companyId);
		} else {
			company = companyDAO.findById(companyId);
		}
		List<GrantedAuthority> grantedAuthorities = getUserPrivilege(employee, company);
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
				authentication.getCredentials(), grantedAuthorities));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.LoginLogic#getCompanyDefaultLanguage(java.lang.Long)
	 */
	@Override
	public String getCompanyDefaultLanguage(Long companyCode) {
		String companyDefaultLanguage = companyDAO.getCompanyDefaultLanguage(companyCode);
		return companyDefaultLanguage;
	}

	@Override
	public CompanyModuleDTO getCompanyModuleStatus(String companyCode) {
		Company cmp = companyDAO.findByCompanyCode(companyCode, null);
		CompanyForm companyInfo = switchCompanyLogic.getCompany(cmp.getCompanyId());
		CompanyModuleDTO companyModuleDTO = new CompanyModuleDTO();
		companyModuleDTO.setHasMobile(companyInfo.isHasMobileModule());
		companyModuleDTO.setHasHrisModule(companyInfo.isHasHrisModule());
		companyModuleDTO.setHasLeaveModule(companyInfo.isHasLeaveModule());
		companyModuleDTO.setHasClaimModule(companyInfo.isHasClaimModule());
		companyModuleDTO.setHasCoherentTimesheetModule(companyInfo.isHasCoherentTimesheetModule());
		companyModuleDTO.setHasLionTimesheetModule(companyInfo.isHasLionTimesheetModule());
		companyModuleDTO.setHasLundinTimesheetModule(companyInfo.isHasLundinTimesheetModule());

		// Check if SSO if enabled
		SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyId(cmp.getCompanyId());
		if (ssoConfiguration.getIsEnableSso() != null && ssoConfiguration.getIsEnableSso()) {
			companyModuleDTO.setSsoEnabled(true);
		}
		return companyModuleDTO;
	}

	/**
	 * ADDED BY MANOJ
	 */

	@Override
	public Set<Menu> getUserPrivilege(Long empID, Long companyID, String role) {

		Set<Menu> userPrivilegeSet = new HashSet<>();
		Set<ModulePrivileges> privilegeDTOs = new HashSet<ModulePrivileges>();
		Menu menu = null;
		Set<PrivilegeMaster> allPrivlist = new HashSet<>();
		List<EmployeeRoleMapping> listOfEmployeeRoleMapping = employeeRoleMappingDAO.findByConditionEmpIdAndCompanyId(companyID, empID);
		
		for(EmployeeRoleMapping obj : listOfEmployeeRoleMapping) {
    			List<PrivilegeMaster> privilegeMasterList = privilegeMasterDAO.getPrivilegesByRole(obj.getRoleMaster().getRoleId());
    			for(PrivilegeMaster master : privilegeMasterList){
    				allPrivlist.add(master);
    			}
		 }
 
	  for(CompanyModuleMapping moduleMaster : companyModuleMappingDAO.fetchModuleByCompanyId(companyID)){
 		
		for(PrivilegeMaster privilegeMaster : allPrivlist){
			if(moduleMaster.getModuleMaster().getModuleId()==privilegeMaster.getModuleMaster().getModuleId() && role.equalsIgnoreCase(privilegeMaster.getPrivilegeRole()) ){
				privilegeDTOs.add(new ModulePrivileges(privilegeMaster.getPrivilegeName(), privilegeMaster.getPrivilegeDesc()));
			}
		 }
		 if(privilegeDTOs != null && !privilegeDTOs.isEmpty() && !privilegeDTOs.contains(menu)) {
				userPrivilegeSet.add(new Menu(moduleMaster.getModuleMaster().getModuleName(), privilegeDTOs));
			}
			privilegeDTOs = new HashSet<>();
		}
       return userPrivilegeSet;
	}

	@Override
	public byte[] getDefaultLogoImage(String companyCode) {

		byte[] originalByteFile = null;
		String filePath = "company/defaultLogo/defaultLogo.png";
		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				originalByteFile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
			} else {
				return null;
}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return originalByteFile;
	}

	@Override
	public String getLoginUserEmail(long userID) {

		Employee employeeVO = employeeDAO.findByID(userID);
		if (employeeVO != null) {
			return employeeVO.getEmail();
		} else {
			return null;
		}

	}

}
