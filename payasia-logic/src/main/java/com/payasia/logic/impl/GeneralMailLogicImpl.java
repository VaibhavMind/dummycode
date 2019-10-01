package com.payasia.logic.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.PreviousWorkflowDTO;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.GeneralMailLogic;

@Component
public class GeneralMailLogicImpl implements GeneralMailLogic {

	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;

	@Resource
	EmailTemplateDAO emailTemplateDAO;

	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	@Resource
	PayAsiaMailUtils payAsiaMailUtils;

	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;

	@Resource
	LeavePreferenceDAO leavePreferenceDAO;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(GeneralMailLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendEMailForLeave(Long companyId, LeaveApplication leaveApplication, String subCategoryName, BigDecimal days, BigDecimal balance,
			Employee loggedInEmployee, Employee reviewer, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays) {

		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);
		EmailTemplate emailTemplateVO = null;
		if (leaveApplication != null) {
			emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId, leaveApplication.getCompany().getCompanyId());
		}
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}
		EmailPreferenceMaster emailPreferenceURL = emailPreferenceMasterDAO.findByConditionCompany(reviewer.getCompany().getCompanyId());
		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (leaveApplication != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE, DateUtils.timeStampToString(leaveApplication.getStartDate()));
				modelMap.put(PayAsiaConstants.LEAVE_TO_DATE, DateUtils.timeStampToString(leaveApplication.getEndDate()));
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC,
						leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME, getEmployeeName(leaveApplication.getEmployee()));
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication.getEmployee().getEmployeeNumber());
				modelMap.put(PayAsiaConstants.LEAVE_REVIEWER_NAME, reviewer.getFirstName());

				if (!isLeaveUnitDays) {
					modelMap.put(PayAsiaConstants.PAYASIA_LEAVE_HOURS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				} else {
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1().getSession())) {
						if (leaveApplication.getLeaveSessionMaster1().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getToSessionName());
						}
					}
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2().getSession())) {
						if (leaveApplication.getLeaveSessionMaster2().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getToSessionName());
						}
					}
					modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				}

				modelMap.put(PayAsiaConstants.LEAVE_REASON, leaveApplication.getReason());
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt.format(Double.parseDouble(String.valueOf(balance))));

				if (emailPreferenceURL != null) {
					if (StringUtils.isNotBlank(emailPreferenceURL.getCompanyUrl())) {
						String link = "<a href='" + emailPreferenceURL.getCompanyUrl() + "'>" + emailPreferenceURL.getCompanyUrl() + "</a>";
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);
					} else {
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
					}
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
					emailTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplate" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplateSubject" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

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

					if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
						payAsiaEmailTO.addMailTo(leaveApplication.getApplyTo());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}
					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else {
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";
					if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0) {
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
								.getLeaveSchemeTypeAvailingLeaves().iterator().next();
						if (leaveSchemeTypeAvailingLeave.getSendMailToDefaultCC()
								&& StringUtils.isNotBlank(leaveSchemeTypeAvailingLeave.getDefaultCCEmail())) {

							defaultMailCCIds = leaveSchemeTypeAvailingLeave.getDefaultCCEmail() + ";";

						}

					}

					if (StringUtils.isNotBlank(leaveApplication.getEmailCC())) {
						String ccMailIds = leaveApplication.getEmailCC();
						if (StringUtils.isNotBlank(defaultMailCCIds)) {
							defaultMailCCIds += ccMailIds;
						} else {
							defaultMailCCIds = ccMailIds;
						}

						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}

					} else if (StringUtils.isNotBlank(defaultMailCCIds)) {
						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}
					}

					payAsiaMailUtils.sendEmailForLeave(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true, payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();
						emailSubjectTemplate.delete();
					}
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/" + returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	@Override
	public String sendEMailLeaveByAdminForEmployee(Long companyId, LeaveApplication leaveApplication, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggedInEmployee, Employee reviewer, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays, String toEmailId) {

		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);
		EmailTemplate emailTemplateVO = null;
		if (leaveApplication != null) {
			emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId, leaveApplication.getCompany().getCompanyId());
		}
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}
		EmailPreferenceMaster emailPreferenceURL = emailPreferenceMasterDAO.findByConditionCompany(reviewer.getCompany().getCompanyId());
		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (leaveApplication != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE, DateUtils.timeStampToString(leaveApplication.getStartDate()));
				modelMap.put(PayAsiaConstants.LEAVE_TO_DATE, DateUtils.timeStampToString(leaveApplication.getEndDate()));
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC,
						leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME, getEmployeeName(leaveApplication.getEmployee()));
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication.getEmployee().getEmployeeNumber());

				modelMap.put(PayAsiaConstants.LEAVE_ADMIN_EMPLOYEE_NAME, getEmployeeName(loggedInEmployee));
				modelMap.put(PayAsiaConstants.LEAVE_ADMIN_EMPLOYEE_NUMBER, loggedInEmployee.getEmployeeNumber());

				modelMap.put(PayAsiaConstants.LEAVE_REVIEWER_NAME, reviewer.getFirstName());

				if (!isLeaveUnitDays) {
					modelMap.put(PayAsiaConstants.PAYASIA_LEAVE_HOURS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				} else {
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1().getSession())) {
						if (leaveApplication.getLeaveSessionMaster1().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getToSessionName());
						}
					}
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2().getSession())) {
						if (leaveApplication.getLeaveSessionMaster2().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getToSessionName());
						}
					}
					modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				}

				modelMap.put(PayAsiaConstants.LEAVE_REASON, leaveApplication.getReason());
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt.format(Double.parseDouble(String.valueOf(balance))));
				if (emailPreferenceURL != null) {
					if (StringUtils.isNotBlank(emailPreferenceURL.getCompanyUrl())) {
						String link = "<a href='" + emailPreferenceURL.getCompanyUrl() + "'>" + emailPreferenceURL.getCompanyUrl() + "</a>";
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);
					} else {
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
					}
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
					emailTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplate" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplateSubject" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

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

					if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
						payAsiaEmailTO.addMailTo(toEmailId);
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}
					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else {
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";
					if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0) {
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
								.getLeaveSchemeTypeAvailingLeaves().iterator().next();
						if (leaveSchemeTypeAvailingLeave.getSendMailToDefaultCC()
								&& StringUtils.isNotBlank(leaveSchemeTypeAvailingLeave.getDefaultCCEmail())) {

							defaultMailCCIds = leaveSchemeTypeAvailingLeave.getDefaultCCEmail() + ";";

						}

					}

					if (StringUtils.isNotBlank(leaveApplication.getEmailCC())) {
						String ccMailIds = leaveApplication.getEmailCC();
						if (StringUtils.isNotBlank(defaultMailCCIds)) {
							defaultMailCCIds += ccMailIds;
						} else {
							defaultMailCCIds = ccMailIds;
						}

						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}

					} else if (StringUtils.isNotBlank(defaultMailCCIds)) {
						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}
					}

					payAsiaMailUtils.sendEmailForLeave(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true, payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();
						emailSubjectTemplate.delete();
					}
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/" + returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	private String isSystemMailReq(Long companyId) {
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		String fromAddress = "";
		if (leavePreferenceVO.isUseSystemMailAsFromAddress()) {

			EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO.findByConditionCompany(companyId);
			fromAddress = emailPreferenceMaster.getSystemEmailForSendingEmails();
		}
		return fromAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendWithdrawEmailForLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggedInEmployee, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);
		LeaveApplication leaveApplication = leaveApplicationWorkflow.getLeaveApplication();
		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId,
				leaveApplication.getCompany().getCompanyId());
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (leaveApplication != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE, DateUtils.timeStampToString(leaveApplication.getStartDate()));
				modelMap.put(PayAsiaConstants.LEAVE_TO_DATE, DateUtils.timeStampToString(leaveApplication.getEndDate()));
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC,
						leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME, getEmployeeName(leaveApplication.getEmployee()));
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication.getEmployee().getEmployeeNumber());
				DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
				if (!isLeaveUnitDays) {
					modelMap.put(PayAsiaConstants.PAYASIA_LEAVE_HOURS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				} else {
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1().getSession())) {
						if (leaveApplication.getLeaveSessionMaster1().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getToSessionName());
						}
					}
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2().getSession())) {
						if (leaveApplication.getLeaveSessionMaster2().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getToSessionName());
						}
					}
					modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				}

				modelMap.put(PayAsiaConstants.LEAVE_REASON, leaveApplication.getReason());
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt.format(Double.parseDouble(String.valueOf(balance))));

				if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
					String link = "<a href='" + emailPreferenceMasterVO.getCompanyUrl() + "'>" + emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);
				} else {
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
					emailTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplate" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplateSubject" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

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

					if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
						payAsiaEmailTO.addMailTo(leaveApplicationWorkflow.getForwardTo());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}

					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else {
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";
					if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0) {
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
								.getLeaveSchemeTypeAvailingLeaves().iterator().next();
						if (leaveSchemeTypeAvailingLeave.getSendMailToDefaultCC()
								&& StringUtils.isNotBlank(leaveSchemeTypeAvailingLeave.getDefaultCCEmail())) {

							defaultMailCCIds = leaveSchemeTypeAvailingLeave.getDefaultCCEmail() + ";";

						}

					}

					if (StringUtils.isNotBlank(leaveApplicationWorkflow.getEmailCC())) {
						String ccMailIds = leaveApplicationWorkflow.getEmailCC();
						if (StringUtils.isNotBlank(defaultMailCCIds)) {
							defaultMailCCIds += ccMailIds;
						} else {
							defaultMailCCIds = ccMailIds;
						}

						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}

					} else if (StringUtils.isNotBlank(defaultMailCCIds)) {
						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}
					}

					payAsiaMailUtils.sendEmailForLeave(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true, payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();
						emailSubjectTemplate.delete();
					}
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/" + returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendPendingEmailForLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggenInEmployee, String revRemarks, Employee reviewer, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);

		LeaveApplication leaveApplication = leaveApplicationWorkflow.getLeaveApplication();
		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId,
				leaveApplication.getCompany().getCompanyId());
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}
		EmailPreferenceMaster emailPreferencURL = emailPreferenceMasterDAO.findByConditionCompany(reviewer.getCompany().getCompanyId());

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (leaveApplication != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE, DateUtils.timeStampToString(leaveApplication.getStartDate()));
				modelMap.put(PayAsiaConstants.LEAVE_TO_DATE, DateUtils.timeStampToString(leaveApplication.getEndDate()));
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC,
						leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME, getEmployeeName(leaveApplication.getEmployee()));
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication.getEmployee().getEmployeeNumber());
				modelMap.put(PayAsiaConstants.LEAVE_REVIEWER_NAME, reviewer.getFirstName());
				DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
				if (!isLeaveUnitDays) {
					modelMap.put(PayAsiaConstants.PAYASIA_LEAVE_HOURS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				} else {
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1().getSession())) {
						if (leaveApplication.getLeaveSessionMaster1().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getToSessionName());
						}
					}
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2().getSession())) {
						if (leaveApplication.getLeaveSessionMaster2().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getToSessionName());
						}
					}
					modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				}

				modelMap.put(PayAsiaConstants.LEAVE_REASON, leaveApplication.getReason());

				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt.format(Double.parseDouble(String.valueOf(balance))));
				modelMap.put(PayAsiaConstants.LEAVE_CURRENT_USER, getEmployeeName(loggenInEmployee));
				modelMap.put(PayAsiaConstants.LEAVE_REVIEWER_REMARKS, revRemarks);
				if (emailPreferencURL != null) {
					if (StringUtils.isNotBlank(emailPreferencURL.getCompanyUrl())) {
						String link = "<a href='" + emailPreferencURL.getCompanyUrl() + "'>" + emailPreferencURL.getCompanyUrl() + "</a>";
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);
					} else {
						modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
					}
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
					emailTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplate" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplateSubject" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

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

					if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
						payAsiaEmailTO.addMailTo(leaveApplicationWorkflow.getForwardTo());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}

					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else {
						payAsiaEmailTO.setMailFrom(loggenInEmployee.getEmail());
					}

					String defaultMailCCIds = "";
					if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0) {
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
								.getLeaveSchemeTypeAvailingLeaves().iterator().next();
						if (leaveSchemeTypeAvailingLeave.getSendMailToDefaultCC()
								&& StringUtils.isNotBlank(leaveSchemeTypeAvailingLeave.getDefaultCCEmail())) {

							defaultMailCCIds = leaveSchemeTypeAvailingLeave.getDefaultCCEmail() + ";";

						}

					}

					if (StringUtils.isNotBlank(leaveApplicationWorkflow.getEmailCC())) {
						String ccMailIds = leaveApplicationWorkflow.getEmailCC();
						if (StringUtils.isNotBlank(defaultMailCCIds)) {
							defaultMailCCIds += ccMailIds;
						} else {
							defaultMailCCIds = ccMailIds;
						}

						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}

					} else if (StringUtils.isNotBlank(defaultMailCCIds)) {
						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}
					}

					payAsiaMailUtils.sendEmailForLeave(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true, payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();
						emailSubjectTemplate.delete();
					}
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/" + returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendAcceptRejectMailForLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggedInEmployee, String reviewRemarks, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays,
			String... fromEmail) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);
		LeaveApplication leaveApplication = leaveApplicationWorkflow.getLeaveApplication();
		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId,
				leaveApplication.getCompany().getCompanyId());
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (leaveApplication != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE, DateUtils.timeStampToString(leaveApplication.getStartDate()));
				modelMap.put(PayAsiaConstants.LEAVE_TO_DATE, DateUtils.timeStampToString(leaveApplication.getEndDate()));
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC,
						leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME, getEmployeeName(leaveApplication.getEmployee()));
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication.getEmployee().getEmployeeNumber());
				DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
				if (!isLeaveUnitDays) {
					modelMap.put(PayAsiaConstants.PAYASIA_LEAVE_HOURS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				} else {
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1().getSession())) {
						if (leaveApplication.getLeaveSessionMaster1().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getToSessionName());
						}
					}
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2().getSession())) {
						if (leaveApplication.getLeaveSessionMaster2().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getToSessionName());
						}
					}
					modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));

				}
				modelMap.put(PayAsiaConstants.LEAVE_REASON, leaveApplication.getReason());

				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt.format(Double.parseDouble(String.valueOf(balance))));
				modelMap.put(PayAsiaConstants.LEAVE_REVIEWER_REMARKS, reviewRemarks);
				if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
					String link = "<a href='" + emailPreferenceMasterVO.getCompanyUrl() + "'>" + emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);
				} else {
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
					emailTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplate" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplateSubject" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

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

					if (leaveApplication.getEmployee() != null) {
						payAsiaEmailTO.addMailTo(leaveApplication.getEmployee().getEmail());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}

					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else if (fromEmail.length > 0 && StringUtils.isNotBlank(fromEmail[0])) {
						payAsiaEmailTO.setMailFrom(fromEmail[0]);
					} else {
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";
					if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0) {
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
								.getLeaveSchemeTypeAvailingLeaves().iterator().next();
						if (leaveSchemeTypeAvailingLeave.getSendMailToDefaultCC()
								&& StringUtils.isNotBlank(leaveSchemeTypeAvailingLeave.getDefaultCCEmail())) {

							defaultMailCCIds = leaveSchemeTypeAvailingLeave.getDefaultCCEmail() + ";";

						}

					}

					if (StringUtils.isNotBlank(leaveApplicationWorkflow.getEmailCC())) {
						String ccMailIds = leaveApplicationWorkflow.getEmailCC();
						if (StringUtils.isNotBlank(defaultMailCCIds)) {
							defaultMailCCIds += ccMailIds;
						} else {
							defaultMailCCIds = ccMailIds;
						}

						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}

					} else if (StringUtils.isNotBlank(defaultMailCCIds)) {
						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}
					}

					payAsiaMailUtils.sendEmailForLeave(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true, payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();
						emailSubjectTemplate.delete();
					}
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/" + returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
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
	@Override
	public Long getSubCategoryId(String subCategoryName, List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList) {
		for (EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster : EmailTemplateSubCategoryMasterList) {
			if ((subCategoryName.toUpperCase()).equals(emailTemplateSubCategoryMaster.getSubCategoryName().toUpperCase())) {
				return emailTemplateSubCategoryMaster.getEmailTemplateSubCategoryId();
			}
		}
		return null;
	}

	@Override
	public AddLeaveForm getLeaveBalance(Long companyId, Long employeeId, Long employeeLeaveSchemeTypeId) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();

		LeaveDTO leaveDTO = employeeLeaveSchemeTypeDAO.getLeaveBalance(employeeLeaveSchemeTypeId);
		addLeaveForm.setLeaveBalance(leaveDTO.getLeaveBalance());

		return addLeaveForm;
	}

	@Override
	public AddLeaveForm getNoOfDays(Long companyId, Long employeeId, LeaveDTO leaveDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		LeaveDTO leaveDTORes = employeeLeaveSchemeTypeDAO.getNoOfDays(leaveDTO);
		addLeaveForm.setNoOfDays(leaveDTORes.getDays());
		return addLeaveForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendMailToDelgate(Long companyId, WorkflowDelegate workflowDelegatem, PreviousWorkflowDTO previousWorkflowDTO, String subcategoryName,
			boolean isLeaveUnitDays) {

		File emailTemplate = null;
		FileOutputStream fos = null;
		File emailSubjectTemplate = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subcategoryName, emailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId, companyId);
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}
		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

		String mailBody = emailTemplateVO.getBody();
		String mailSubject = emailTemplateVO.getSubject();
		Map<String, Object> modelMap = new HashMap<String, Object>();

		String applyTo = workflowDelegatem.getEmployee2().getEmail();

		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_WORKFLOW, workflowDelegatem.getWorkflowType().getCodeDesc());
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_FROM_DATE, DateUtils.timeStampToString(workflowDelegatem.getStartDate()));
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE__TO_DATE, DateUtils.timeStampToString(workflowDelegatem.getEndDate()));
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE__FROM_USER, getEmployeeName(workflowDelegatem.getEmployee1()));
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE__DELEGATE, getEmployeeName(workflowDelegatem.getEmployee2()));
		if (previousWorkflowDTO != null) {
			modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_PRE_WORKFLOW, previousWorkflowDTO.getPreWorkflow());
			modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_PRE_FROM_DATE, previousWorkflowDTO.getPreFromDate());
			modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_PRE_TO_DATE, previousWorkflowDTO.getPreToDate());
		}

		try {
			byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
			byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "sendDelegateTemp" + workflowDelegatem.getWorkflowDelegateId() + "_" + uniqueId + ".vm");

			emailTemplate.deleteOnExit();
			emailSubjectTemplate = new File(
					PAYASIA_TEMP_PATH + "//" + "sendDelegateSubjectTemp" + workflowDelegatem.getWorkflowDelegateId() + "_" + uniqueId + ".vm");
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

			if (StringUtils.isNotBlank(applyTo)) {
				payAsiaEmailTO.addMailTo(applyTo);
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
				payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
				payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
			}
			if (StringUtils.isNotBlank(emailPreferenceMasterVO.getContactEmail())) {
				payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO.getContactEmail());
			}

			payAsiaMailUtils.sendEmail(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
					emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), false, payAsiaEmailTO);

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
			}
		}
		return "";

	}

	@Override
	public String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		return employeeName;
	}

	@Override
	public String sendMailForPreApprovedLeave(Long companyId, LeaveApplicationWorkflow leaveApplicationWorkflow, String subCategoryName, BigDecimal days,
			BigDecimal balance, Employee loggedInEmployee, String reviewRemarks, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays, String toEmail,
			String... fromEmail) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);
		LeaveApplication leaveApplication = leaveApplicationWorkflow.getLeaveApplication();
		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId,
				leaveApplication.getCompany().getCompanyId());
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (leaveApplication != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE, DateUtils.timeStampToString(leaveApplication.getStartDate()));
				modelMap.put(PayAsiaConstants.LEAVE_TO_DATE, DateUtils.timeStampToString(leaveApplication.getEndDate()));
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC,
						leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME, getEmployeeName(leaveApplication.getEmployee()));
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication.getEmployee().getEmployeeNumber());

				DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
				if (!isLeaveUnitDays) {
					modelMap.put(PayAsiaConstants.PAYASIA_LEAVE_HOURS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				} else {
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1().getSession())) {
						if (leaveApplication.getLeaveSessionMaster1().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getToSessionName());
						}
					}
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2().getSession())) {
						if (leaveApplication.getLeaveSessionMaster2().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getToSessionName());
						}
					}
					modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				}

				modelMap.put(PayAsiaConstants.LEAVE_REASON, leaveApplication.getReason());

				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt.format(Double.parseDouble(String.valueOf(balance))));

				modelMap.put(PayAsiaConstants.LEAVE_REVIEWER_REMARKS, reviewRemarks);
				if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
					String link = "<a href='" + emailPreferenceMasterVO.getCompanyUrl() + "'>" + emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);
				} else {
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
					emailTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplate" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplateSubject" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

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

					payAsiaEmailTO.addMailTo(toEmail);

					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}

					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else if (fromEmail.length > 0 && StringUtils.isNotBlank(fromEmail[0])) {
						payAsiaEmailTO.setMailFrom(fromEmail[0]);
					} else {
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";
					if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0) {
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
								.getLeaveSchemeTypeAvailingLeaves().iterator().next();
						if (leaveSchemeTypeAvailingLeave.getSendMailToDefaultCC()
								&& StringUtils.isNotBlank(leaveSchemeTypeAvailingLeave.getDefaultCCEmail())) {

							defaultMailCCIds = leaveSchemeTypeAvailingLeave.getDefaultCCEmail() + ";";

						}

					}

					if (StringUtils.isNotBlank(leaveApplicationWorkflow.getEmailCC())) {
						String ccMailIds = leaveApplicationWorkflow.getEmailCC();
						if (StringUtils.isNotBlank(defaultMailCCIds)) {
							defaultMailCCIds += ccMailIds;
						} else {
							defaultMailCCIds = ccMailIds;
						}

						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}

					} else if (StringUtils.isNotBlank(defaultMailCCIds)) {
						String[] ccEmailIds = defaultMailCCIds.split(";");
						for (String ccEmailId : ccEmailIds) {
							payAsiaEmailTO.addMainCc(ccEmailId);
						}
					}

					payAsiaMailUtils.sendEmailForLeave(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true, payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();
						emailSubjectTemplate.delete();
					}
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/" + returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}
	@Override
	public String sendMailForLeaveExtension(Long companyId, LeaveApplication leaveApplication, String subCategoryName, BigDecimal days, BigDecimal balance,
			Employee loggedInEmployee, String reviewRemarks, LeaveSessionDTO sessionDTO, boolean isLeaveUnitDays, String toEmail, String... fromEmail) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId,
				leaveApplication.getCompany().getCompanyId());
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (leaveApplication != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(leaveApplication.getApplyTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.LEAVE_FROM_DATE, DateUtils.timeStampToString(leaveApplication.getStartDate()));
				modelMap.put(PayAsiaConstants.LEAVE_TO_DATE, DateUtils.timeStampToString(leaveApplication.getEndDate()));
				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_TYPE_DESC,
						leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NAME, getEmployeeName(leaveApplication.getEmployee()));
				modelMap.put(PayAsiaConstants.LEAVE_EMPLOYEE_NUMBER, leaveApplication.getEmployee().getEmployeeNumber());

				DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
				if (!isLeaveUnitDays) {
					modelMap.put(PayAsiaConstants.PAYASIA_LEAVE_HOURS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				} else {
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster1().getSession())) {
						if (leaveApplication.getLeaveSessionMaster1().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_FROM_SESSION, sessionDTO.getToSessionName());
						}
					}
					if (StringUtils.isNotBlank(leaveApplication.getLeaveSessionMaster2().getSession())) {
						if (leaveApplication.getLeaveSessionMaster2().getSession().equalsIgnoreCase(PayAsiaConstants.LEAVE_SESSION_1)) {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getFromSessionName());
						} else {
							modelMap.put(PayAsiaConstants.LEAVE_TO_SESSION, sessionDTO.getToSessionName());
						}
					}
					modelMap.put(PayAsiaConstants.LEAVE_DAYS, decimalFmt.format(Double.parseDouble(String.valueOf(days))));
				}

				modelMap.put(PayAsiaConstants.LEAVE_REASON, leaveApplication.getReason());

				modelMap.put(PayAsiaConstants.LEAVE_LEAVE_BALANCE, decimalFmt.format(Double.parseDouble(String.valueOf(balance))));

				modelMap.put(PayAsiaConstants.LEAVE_REVIEWER_REMARKS, reviewRemarks);
				if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
					String link = "<a href='" + emailPreferenceMasterVO.getCompanyUrl() + "'>" + emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, link);
				} else {
					modelMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, "");
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
					emailTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplate" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate = new File(
							PAYASIA_TEMP_PATH + "//" + "sendLeaveTemplateSubject" + leaveApplication.getLeaveApplicationId() + "_" + uniqueId + ".vm");
					emailSubjectTemplate.deleteOnExit();
					emailTemplate.deleteOnExit();

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

					payAsiaEmailTO.addMailTo(toEmail);

					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}

					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else if (fromEmail.length > 0 && StringUtils.isNotBlank(fromEmail[0])) {
						payAsiaEmailTO.setMailFrom(fromEmail[0]);
					} else {
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";
					if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0) {
						LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
								.getLeaveSchemeTypeAvailingLeaves().iterator().next();
						if (leaveSchemeTypeAvailingLeave.getSendMailToDefaultCC()
								&& StringUtils.isNotBlank(leaveSchemeTypeAvailingLeave.getDefaultCCEmail())) {

							defaultMailCCIds = leaveSchemeTypeAvailingLeave.getDefaultCCEmail() + ";";

						}

					}

					payAsiaMailUtils.sendEmailForLeave(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), true, payAsiaEmailTO);

				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				} finally {
					if (emailTemplate != null) {
						emailTemplate.delete();
						emailSubjectTemplate.delete();
					}
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/" + returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}
	
	/*
	 * FOR USER SELF-MAIL CHECK 
	 */
	@Override
	public String sendMailToDelgate(Long companyId, WorkflowDelegate workflowDelegatem,
			PreviousWorkflowDTO previousWorkflowDTO, String subcategoryName, boolean isLeaveUnitDays,
			boolean userCCmailCheck) {

		File emailTemplate = null;
		FileOutputStream fos = null;
		File emailSubjectTemplate = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subcategoryName, emailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId,
				companyId);
		if (emailTemplateVO == null) {
			return "send.password.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}
		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

		String mailBody = emailTemplateVO.getBody();
		String mailSubject = emailTemplateVO.getSubject();
		Map<String, Object> modelMap = new HashMap<String, Object>();

		String applyTo = workflowDelegatem.getEmployee2().getEmail();

		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_WORKFLOW, workflowDelegatem.getWorkflowType().getCodeDesc());
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_FROM_DATE,
				DateUtils.timeStampToString(workflowDelegatem.getStartDate()));
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE__TO_DATE,
				DateUtils.timeStampToString(workflowDelegatem.getEndDate()));
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE__FROM_USER, getEmployeeName(workflowDelegatem.getEmployee1()));
		modelMap.put(PayAsiaConstants.LEAVE_DELEGATE__DELEGATE, getEmployeeName(workflowDelegatem.getEmployee2()));
		if (previousWorkflowDTO != null) {
			modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_PRE_WORKFLOW, previousWorkflowDTO.getPreWorkflow());
			modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_PRE_FROM_DATE, previousWorkflowDTO.getPreFromDate());
			modelMap.put(PayAsiaConstants.LEAVE_DELEGATE_PRE_TO_DATE, previousWorkflowDTO.getPreToDate());
		}

		try {
			byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
			byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "sendDelegateTemp"
					+ workflowDelegatem.getWorkflowDelegateId() + "_" + uniqueId + ".vm");

			emailTemplate.deleteOnExit();
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//" + "sendDelegateSubjectTemp"
					+ workflowDelegatem.getWorkflowDelegateId() + "_" + uniqueId + ".vm");
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

			if (StringUtils.isNotBlank(applyTo)) {
				payAsiaEmailTO.addMailTo(applyTo);
			}

			if (userCCmailCheck) {
				String emailCC = workflowDelegatem.getEmployee1().getEmail();

				if (StringUtils.isNotBlank(emailCC)) {
					payAsiaEmailTO.addMainCc(emailCC);
					
				}

			}

			if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
				payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
				payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
			}
			if (StringUtils.isNotBlank(emailPreferenceMasterVO.getContactEmail())) {
				payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO.getContactEmail());
			}

			payAsiaMailUtils.sendEmail(modelMap,
					emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
					emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), false,
					payAsiaEmailTO);

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
			}
		}
		return "";

	}
	
	
	@Override
	public String sendMailToEmployee(Long companyId, String subcategoryName,String email,String otp,String expire) {

		File emailTemplate = null;
		FileOutputStream fos = null;
		File emailSubjectTemplate = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO.findAll();

		Long subCategoryId = getSubCategoryId(subcategoryName, emailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId, companyId);
		if (emailTemplateVO == null) {
			return "send.otp.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}
		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

		String mailBody = emailTemplateVO.getBody();
		String mailSubject = emailTemplateVO.getSubject();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		long expireMin = Long.parseLong(expire)/60;
		modelMap.put("OTP", otp);
		modelMap.put("Expire", expireMin);
		
		try {
			byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
			byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "twoWayAuthTemp" + UserContext.getUserId() + "_" + uniqueId + ".vm");

			emailTemplate.deleteOnExit();
			emailSubjectTemplate = new File(
					PAYASIA_TEMP_PATH + "//" + "twoWayAuthSubjectTemp" + UserContext.getUserId() + "_" + uniqueId + ".vm");
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

			if (StringUtils.isNotBlank(email)) {
				payAsiaEmailTO.addMailTo(email);
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
				payAsiaEmailTO.setMailSubject(emailTemplateVO.getSubject());
			}
			if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
				payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
			}
			if (StringUtils.isNotBlank(emailPreferenceMasterVO.getContactEmail())) {
				payAsiaEmailTO.setMailFrom(emailPreferenceMasterVO.getContactEmail());
			}

			payAsiaMailUtils.sendEmail(modelMap, emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1),
					emailSubjectTemplate.getPath().substring(emailSubjectTemplate.getParent().length() + 1), false, payAsiaEmailTO);

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
			}
		}
		return "";

	}
	
}
