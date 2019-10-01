package com.payasia.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.MessageDTO;
import com.payasia.common.dto.PasswordPolicyDTO;
import com.payasia.common.exception.PayAsiaPasswordPolicyException;
import com.payasia.common.form.ChangePasswordForm;
import com.payasia.common.util.DateUtils;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLoginDetailDAO;
import com.payasia.dao.EmployeeLoginHistoryDAO;
import com.payasia.dao.EmployeePasswordChangeHistoryDAO;
import com.payasia.dao.ForgotPasswordTokenDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeLoginDetail;
import com.payasia.dao.bean.EmployeeLoginHistory;
import com.payasia.dao.bean.EmployeePasswordChangeHistory;
import com.payasia.dao.bean.ForgotPasswordToken;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.logic.EmployeeChangePasswordLogic;
import com.payasia.logic.PasswordPolicyLogic;
import com.payasia.logic.SecurityLogic;

/**
 * The Class EmployeeChangePasswordLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class EmployeeChangePasswordLogicImpl implements
		EmployeeChangePasswordLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeChangePasswordLogicImpl.class);

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The employee password change history dao. */
	@Resource
	EmployeePasswordChangeHistoryDAO employeePasswordChangeHistoryDAO;

	/** The password policy config master dao. */
	@Resource
	PasswordPolicyConfigMasterDAO passwordPolicyConfigMasterDAO;

	/** The employee login history dao. */
	@Resource
	EmployeeLoginHistoryDAO employeeLoginHistoryDAO;
	
	@Resource
	PasswordPolicyLogic passwordPolicyLogic;

	@Resource
	SecurityLogic securityLogic;

	@Resource
	EmployeeLoginDetailDAO employeeLoginDetailDAO;
	@Resource
	ForgotPasswordTokenDAO forgotPasswordTokenDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeChangePasswordLogic#changePassword(java.lang
	 * .Long, java.lang.String, java.lang.String)
	 */
	@Override
	public MessageDTO changePassword(Long employeeId, Long companyId,
			String oldPassword, String newPassword) {
		MessageDTO messageDto = getPassWordChangeStatus(employeeId, companyId,
				oldPassword, newPassword, null);
		return messageDto;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeChangePasswordLogic#changePwdForFirstTimeLogin
	 * (java.lang.Long, java.lang.Long, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public MessageDTO changePwdForFirstTimeLogin(Long employeeId,
			Long companyId, String oldPassword, String newPassword,
			String ipAddrs) {

		MessageDTO messageDto = getPassWordChangeStatus(employeeId, companyId,
				oldPassword, newPassword, ipAddrs);
		return messageDto;

	}

	private MessageDTO getPassWordChangeStatus(Long employeeId, Long companyId,
			String oldPassword, String newPassword, String ipAddrs) {

		MessageDTO messageDTO = new MessageDTO();
		Employee employee = employeeDAO.findById(employeeId);
		EmployeePasswordChangeHistory empPasswordChangeHistory = new EmployeePasswordChangeHistory();
		if (employee					
				.getEmployeeLoginDetail()
				.getPassword()
				.equals(securityLogic.encrypt(oldPassword, employee
						.getEmployeeLoginDetail().getSalt()))) {
			if (StringUtils.isNotBlank(newPassword)) {
				try {
					passwordPolicyLogic.isPasswordAllowed(employeeId,
							companyId, newPassword);
				} catch (PayAsiaPasswordPolicyException policyException) {
					LOGGER.error(policyException.getMessage(), policyException);
					messageDTO.setKey(policyException.getKey());
					if (policyException.getErrorArgs() != null) {
						messageDTO.setArgs(policyException.getErrorArgs()[0]);
					}
					return messageDTO;
				}

				if (ipAddrs != null) {
					EmployeeLoginHistory employeeLoginHistory = new EmployeeLoginHistory();
					employeeLoginHistory.setEmployee(employee);
					employeeLoginHistory.setLoggedInDate(DateUtils
							.getCurrentTimestampWithTime());
					employeeLoginHistory.setIpAddress(ipAddrs);

					employeeLoginHistoryDAO.save(employeeLoginHistory);
				}

				String salt = securityLogic.generateSalt();
				newPassword = securityLogic.encrypt(newPassword, salt);

				EmployeeLoginDetail employeeLoginDetail = employee
						.getEmployeeLoginDetail();
				employeeLoginDetail.setPassword(newPassword);
				employeeLoginDetail.setSalt(salt);
				employeeLoginDetail.setPasswordReset(false);
				employeeLoginDetailDAO.update(employeeLoginDetail);

				empPasswordChangeHistory.setEmployee(employee);
				empPasswordChangeHistory.setChangedPassword(newPassword);
				empPasswordChangeHistory.setChangeDate(DateUtils
						.getCurrentTimestampWithTime());
				empPasswordChangeHistory.setSalt(salt);
				employeePasswordChangeHistoryDAO.save(empPasswordChangeHistory);
				messageDTO.setKey("new.passWord.has.been.changed");
				return messageDTO;
			}
		} else {
			messageDTO.setKey("old.passWord.does.not.match.please.try.again");
			return messageDTO;
		}
		messageDTO.setKey("new.passWord.does.not.change");
		return messageDTO;
	}

	@Override
	public ChangePasswordForm getPassWordPolicyDetails(Long companyId) {
		ChangePasswordForm changePasswordForm = new ChangePasswordForm();
		List<PasswordPolicyDTO> passwordPolicyDTOs = new ArrayList<>();
		PasswordPolicyConfigMaster passwordPolicyConfigMaster = passwordPolicyConfigMasterDAO
				.findByConditionCompany(companyId);
		if (passwordPolicyConfigMaster == null
				|| !passwordPolicyConfigMaster.isEnablePwdPolicy()) {
			changePasswordForm.setIsPasswordPolicyEnabled(false);
		} else {
			changePasswordForm.setIsPasswordPolicyEnabled(true);

			PasswordPolicyDTO passwordPolicyAlphaNumeric = new PasswordPolicyDTO();
			passwordPolicyAlphaNumeric
					.setMessage("payasia.password.policy.should.contain.atleast.one.alphanumeric");
			passwordPolicyDTOs.add(passwordPolicyAlphaNumeric);
			PasswordPolicyDTO passwordPolicyFNLNEC = new PasswordPolicyDTO();
			passwordPolicyFNLNEC
					.setMessage("payasia.password.should.not.contain.first.name.last.name");
			passwordPolicyDTOs.add(passwordPolicyFNLNEC);

			if (passwordPolicyConfigMaster.getMaxPwdLength() > 0
					&& passwordPolicyConfigMaster.getMaxPwdLength() != null) {
				PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
				passwordPolicyDTO.setMessage("password.should.contain.maximum");
				passwordPolicyDTO.setValue(passwordPolicyConfigMaster
						.getMaxPwdLength());
				passwordPolicyDTOs.add(passwordPolicyDTO);
			}
			if (passwordPolicyConfigMaster.getMinPwdLength() > 0) {
				PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
				passwordPolicyDTO.setMessage("password.should.contain.minimum");
				passwordPolicyDTO.setValue(passwordPolicyConfigMaster
						.getMinPwdLength());
				passwordPolicyDTOs.add(passwordPolicyDTO);
			}
			if (passwordPolicyConfigMaster.getMemoryListSize() > 0) {
				PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
				passwordPolicyDTO
						.setMessage("payasia.password.should.not.same.as.last.previous.passwords");
				passwordPolicyDTO.setValue(passwordPolicyConfigMaster
						.getMemoryListSize());
				passwordPolicyDTOs.add(passwordPolicyDTO);
			}
			if (passwordPolicyConfigMaster.isIncludeSpecialCharacter()) {
				PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
				passwordPolicyDTO
						.setMessage("payasia.password.should.contain.at.least.one.of.special.characters");
				passwordPolicyDTOs.add(passwordPolicyDTO);

			}
			if (passwordPolicyConfigMaster.getIncludeNumericCharacter()
					&& passwordPolicyConfigMaster.getIncludeNumericCharacter() != null) {
				PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
				passwordPolicyDTO
						.setMessage("payasia.password.should.contain.atleast.one.numeric");
				passwordPolicyDTOs.add(passwordPolicyDTO);

			}
			if (passwordPolicyConfigMaster.getIncludeUpperLowerCase()
					&& passwordPolicyConfigMaster.getIncludeUpperLowerCase() != null) {
				PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
				passwordPolicyDTO
						.setMessage("payasia.password.should.contain.combination.of.upper.and.lower.case");
				passwordPolicyDTOs.add(passwordPolicyDTO);

			}
			if (passwordPolicyConfigMaster.getMaxExpiryDaysLimit() > 0) {
				PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
				passwordPolicyDTO
						.setMessage("payasia.password.will.expire.after.days");
				passwordPolicyDTO.setValue(passwordPolicyConfigMaster
						.getMaxExpiryDaysLimit());
				passwordPolicyDTOs.add(passwordPolicyDTO);

			}

		}
		changePasswordForm.setPasswordPolicyDTO(passwordPolicyDTOs);
		return changePasswordForm;
	}

	@Override
	public MessageDTO resetForgotPassword(String username, String newPassword,
			String employeeToken, Long companyId, String remoteAddr) {
		MessageDTO messageDto = new MessageDTO();

		Employee employeeVO = null;
		Company company = companyDAO.findById(companyId);
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(company.getCompanyId());

		/*
		 * Authenticate Email as Login Name based on HrisPreference
		 * Configuration
		 */
		if (hrisPreferenceVO != null
				&& hrisPreferenceVO.isUseEmailAndEmployeeNumberForLogin()) {
			List<Employee> employeeListVO = employeeDAO.getEmployeeByEmail(
					username, company.getCompanyCode());
			if (employeeListVO.size() > 1) {
				messageDto.setKey("payasia.invalid.login.credential");
			}
			if (employeeListVO.size() > 0) {
				employeeVO = employeeListVO.get(0);
			}
		}
		if (employeeVO == null) {
			employeeVO = employeeDAO.getEmployeeByLoginName(username,
					company.getCompanyCode());
		}

		if (employeeVO != null) {
			ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenDAO
					.findByCondition(employeeToken, employeeVO.getEmployeeId(),
							companyId, true);
			if (forgotPasswordToken != null) {
				messageDto = resetPassword(username, companyId, newPassword);
				// Expire active token after Reset password When Forgot password
				if (messageDto.getKey().equalsIgnoreCase(
						"new.passWord.has.been.changed")) {
					forgotPasswordToken.setActive(false);
					forgotPasswordTokenDAO.update(forgotPasswordToken);
				}
			} else {
				messageDto.setKey("forgot.password.token.has.expired");
			}
		} else {
			messageDto.setKey("payasia.invalid.username");
		}

		return messageDto;
	}

	private MessageDTO resetPassword(String username, Long companyId,
			String newPassword) {

		MessageDTO messageDTO = new MessageDTO();
		Employee employeeVO = null;
		Company company = companyDAO.findById(companyId);
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(company.getCompanyId());

		/*
		 * Authenticate Email as Login Name based on HrisPreference
		 * Configuration
		 */
		if (hrisPreferenceVO != null
				&& hrisPreferenceVO.isUseEmailAndEmployeeNumberForLogin()) {
			List<Employee> employeeListVO = employeeDAO.getEmployeeByEmail(
					username, company.getCompanyCode());
			if (employeeListVO.size() > 1) {
				messageDTO.setKey("payasia.invalid.login.credential");
			}
			if (employeeListVO.size() > 0) {
				employeeVO = employeeListVO.get(0);
			}
		}
		if (employeeVO == null) {
			employeeVO = employeeDAO.getEmployeeByLoginName(username,
					company.getCompanyCode());
		}

		EmployeePasswordChangeHistory empPasswordChangeHistory = new EmployeePasswordChangeHistory();
		if (employeeVO != null) {
			if (StringUtils.isNotBlank(newPassword)) {
				try {
					passwordPolicyLogic.isPasswordAllowed(
							employeeVO.getEmployeeId(), companyId, newPassword);
				} catch (PayAsiaPasswordPolicyException policyException) {
					messageDTO.setKey(policyException.getKey());
					LOGGER.error(policyException.getMessage(), policyException);
					if (policyException.getErrorArgs() != null) {
						messageDTO.setArgs(policyException.getErrorArgs()[0]);
					}
					return messageDTO;
				}

				String salt = securityLogic.generateSalt();
				newPassword = securityLogic.encrypt(newPassword, salt);

				EmployeeLoginDetail employeeLoginDetail = employeeVO
						.getEmployeeLoginDetail();
				employeeLoginDetail.setPassword(newPassword);
				employeeLoginDetail.setSalt(salt);
				employeeLoginDetail.setPasswordReset(false);
				employeeLoginDetail.setInvalidLoginAttempt(0);
				employeeLoginDetailDAO.update(employeeLoginDetail);

				empPasswordChangeHistory.setEmployee(employeeVO);
				empPasswordChangeHistory.setChangedPassword(newPassword);
				empPasswordChangeHistory.setChangeDate(DateUtils
						.getCurrentTimestampWithTime());
				empPasswordChangeHistory.setSalt(salt);
				employeePasswordChangeHistoryDAO.save(empPasswordChangeHistory);
				messageDTO.setKey("new.passWord.has.been.changed");
				return messageDTO;
			}
		}

		messageDTO.setKey("new.passWord.does.not.change");
		return messageDTO;
	}
}
