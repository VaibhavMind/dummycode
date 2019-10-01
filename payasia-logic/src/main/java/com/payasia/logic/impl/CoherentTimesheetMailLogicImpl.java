package com.payasia.logic.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.EmailDataDTO;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.CoherentTimesheetPreferenceDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.bean.CoherentTimesheetPreference;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.CoherentTimesheetMailLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class CoherentTimesheetMailLogicImpl implements
		CoherentTimesheetMailLogic {

	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;

	@Resource
	EmailTemplateDAO emailTemplateDAO;

	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	@Resource
	PayAsiaMailUtils payAsiaMailUtils;
	@Resource
	GeneralLogic generalLogic;

	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	TimesheetApplicationWorkflowDAO lundinTimesheetWorkflowDAO;

	@Resource
	CoherentTimesheetPreferenceDAO coherentTimesheetPreferenceDAO;

	private static final Logger LOGGER = Logger
			.getLogger(CoherentTimesheetMailLogicImpl.class);

	@Override
	public String sendEMailForTimesheet(Long companyId,
			EmailDataDTO emailDataDTO, String subCategoryName) {

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName,
				emailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(null, subCategoryId,
						companyId);
		if (emailTemplateVO == null) {
			return "payasia.lundin.timesheet.template.is.not.defined";
		}
		EmailPreferenceMaster emailPreferenceURL = emailPreferenceMasterDAO
				.findByConditionCompany(emailDataDTO.getReviewerCompanyId());
		if (emailPreferenceURL == null) {
			return "email.configuration.is.not.defined";
		}

		String returnEmpIds = "";

		if (emailDataDTO.getTimesheetId() != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

			if (StringUtils.isNotBlank(emailDataDTO.getEmailTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
						emailDataDTO.getEmployeeName());
				modelMap.put(
						PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
						emailDataDTO.getEmployeeNumber());
				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_REVIEWER_NAME,
						emailDataDTO.getReviewerFirstName());
				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH,
						emailDataDTO.getBatchDesc());
				modelMap.put(
						PayAsiaConstants.COHERENT_OVERTIME_SHIFT_TYPE_PLACEHOLDER,
						emailDataDTO.getOvertimeShiftType());

				if (emailPreferenceURL != null) {
					if (StringUtils.isNotBlank(emailPreferenceURL
							.getCompanyUrl())) {
						String link = "<a href='"
								+ emailPreferenceURL.getCompanyUrl() + "'>"
								+ emailPreferenceURL.getCompanyUrl() + "</a>";
						modelMap.put(
								PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL,
								link);
					} else {
						modelMap.put(
								PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL,
								"");
					}
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendOTTimesheetTemplate"
							+ emailDataDTO.getTimesheetId() + "_" + uniqueId
							+ ".vm");
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendOTTimesheetSubject"
							+ emailDataDTO.getTimesheetId() + "_" + uniqueId
							+ ".vm");
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

					if (StringUtils.isNotBlank(emailDataDTO.getEmailTo())) {
						payAsiaEmailTO.addMailTo(emailDataDTO.getEmailTo());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO
								.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}

					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else {
						payAsiaEmailTO.setMailFrom(emailDataDTO.getEmailFrom());
					}

					payAsiaMailUtils.sendEmailForOTTimesheet(
							modelMap,
							emailTemplate.getPath().substring(
									emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath()
									.substring(
											emailSubjectTemplate.getParent()
													.length() + 1), true,
							payAsiaEmailTO);

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
			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are"
					+ returnEmpIds;
		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	@Override
	public String sendWithdrawEmailForTimesheet(Long companyId,
			EmailDataDTO emailDataDTO, String subCategoryName) {

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName,
				emailTemplateSubCategoryMasterList);
		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(null, subCategoryId,
						companyId);
		if (emailTemplateVO == null) {
			return "payasia.lundin.timesheet.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (emailDataDTO.getTimesheetId() != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(emailDataDTO.getEmailTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
						emailDataDTO.getEmployeeName());
				modelMap.put(
						PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
						emailDataDTO.getEmployeeNumber());
				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH,
						emailDataDTO.getBatchDesc());
				modelMap.put(
						PayAsiaConstants.COHERENT_OVERTIME_SHIFT_TYPE_PLACEHOLDER,
						emailDataDTO.getOvertimeShiftType());

				if (StringUtils.isNotBlank(emailPreferenceMasterVO
						.getCompanyUrl())) {
					String link = "<a href='"
							+ emailPreferenceMasterVO.getCompanyUrl() + "'>"
							+ emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(
							PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL,
							link);
				} else {
					modelMap.put(
							PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL, "");
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendOTTimesheetTemplate"
							+ emailDataDTO.getTimesheetId() + "_" + uniqueId
							+ ".vm");
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendOTTimesheetTemplateSubject"
							+ emailDataDTO.getTimesheetId() + "_" + uniqueId
							+ ".vm");
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

					if (StringUtils.isNotBlank(emailDataDTO.getEmailTo())) {
						payAsiaEmailTO.addMailTo(emailDataDTO.getEmailTo());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO
								.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}

					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else {
						payAsiaEmailTO.setMailFrom(emailDataDTO.getEmailFrom());
					}

					payAsiaMailUtils.sendEmailForOTTimesheet(
							modelMap,
							emailTemplate.getPath().substring(
									emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath()
									.substring(
											emailSubjectTemplate.getParent()
													.length() + 1), true,
							payAsiaEmailTO);

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

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/"
					+ returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	@Override
	public String sendAcceptRejectMailForTimesheet(Long companyId,
			EmailDataDTO emailDataDTO, String subCategoryName,
			String reviewRemarks) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName,
				emailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(null, subCategoryId,
						emailDataDTO.getEmpCompanyId());
		if (emailTemplateVO == null) {
			return "payasia.lundin.timesheet.template.is.not.defined";
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(emailDataDTO.getEmpCompanyId());
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}

		String returnEmpIds = "";

		if (emailDataDTO.getTimesheetId() != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			String mailBody = emailTemplateVO.getBody();
			String mailSubject = emailTemplateVO.getSubject();
			Map<String, Object> modelMap = new HashMap<String, Object>();

			modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
					emailDataDTO.getEmployeeName());
			modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
					emailDataDTO.getEmployeeNumber());
			modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH,
					emailDataDTO.getBatchDesc());
			modelMap.put(
					PayAsiaConstants.COHERENT_OVERTIME_SHIFT_TYPE_PLACEHOLDER,
					emailDataDTO.getOvertimeShiftType());

			if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
				String link = "<a href='"
						+ emailPreferenceMasterVO.getCompanyUrl() + "'>"
						+ emailPreferenceMasterVO.getCompanyUrl() + "</a>";
				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL,
						link);
			} else {
				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL,
						"");
			}

			try {
				byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
				byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
				String uniqueId = RandomNumberGenerator
						.getNDigitRandomNumber(8);
				emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
						+ "sendOTTimesheetTemplate"
						+ emailDataDTO.getTimesheetId() + "_" + uniqueId
						+ ".vm");
				emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
						+ "sendOTTimesheetTemplateSubject"
						+ emailDataDTO.getTimesheetId() + "_" + uniqueId
						+ ".vm");
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

				if (emailDataDTO.getEmailTo() != null) {
					payAsiaEmailTO.addMailTo(emailDataDTO.getEmailTo());
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
					payAsiaEmailTO.setMailFrom(emailDataDTO.getEmailFrom());
				}

				payAsiaMailUtils.sendEmailForOTTimesheet(
						modelMap,
						emailTemplate.getPath().substring(
								emailTemplate.getParent().length() + 1),
						emailSubjectTemplate.getPath().substring(
								emailSubjectTemplate.getParent().length() + 1),
						true, payAsiaEmailTO);

			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (emailTemplate != null) {
					emailTemplate.delete();
					emailSubjectTemplate.delete();
				}
			}

		}

		if (StringUtils.isNotBlank(returnEmpIds)) {

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/"
					+ returnEmpIds;

		} else {
			return "password.is.sent.to.selected.employees";
		}
	}

	@Override
	public String sendPendingEmailForTimesheet(Long companyId,
			EmailDataDTO emailDataDTO, String subCategoryName, String revRemarks) {

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName,
				emailTemplateSubCategoryMasterList);

		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(null, subCategoryId,
						emailDataDTO.getEmpCompanyId());
		if (emailTemplateVO == null) {
			return "payasia.lundin.timesheet.template.is.not.defined";
		}
		EmailPreferenceMaster emailPreferencURL = emailPreferenceMasterDAO
				.findByConditionCompany(emailDataDTO.getEmpCompanyId());

		if (emailPreferencURL == null) {
			return "email.configuration.is.not.defined";
		}

		String returnEmpIds = "";

		if (emailDataDTO.getTimesheetId() != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(emailDataDTO.getEmailTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NAME,
						emailDataDTO.getEmployeeName());
				modelMap.put(
						PayAsiaConstants.OT_TIMESHEET_EMAIL_EMPLOYEE_NUMBER,
						emailDataDTO.getEmployeeNumber());
				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_REVIEWER_NAME,
						emailDataDTO.getReviewerFirstName());

				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_CURRENT_USER,
						emailDataDTO.getCurrentEmployeeName());
				modelMap.put(PayAsiaConstants.OT_TIMESHEET_EMAIL_BATCH,
						emailDataDTO.getBatchDesc());
				modelMap.put(
						PayAsiaConstants.COHERENT_OVERTIME_SHIFT_TYPE_PLACEHOLDER,
						emailDataDTO.getOvertimeShiftType());

				if (emailPreferencURL != null) {
					if (StringUtils.isNotBlank(emailPreferencURL
							.getCompanyUrl())) {
						String link = "<a href='"
								+ emailPreferencURL.getCompanyUrl() + "'>"
								+ emailPreferencURL.getCompanyUrl() + "</a>";
						modelMap.put(
								PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL,
								link);
					} else {
						modelMap.put(
								PayAsiaConstants.OT_TIMESHEET_EMAIL_COMPANY_URL,
								"");
					}
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendOTTimesheetTemplate"
							+ emailDataDTO.getTimesheetId() + "_" + uniqueId
							+ ".vm");
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendOTTimesheetTemplateSubject"
							+ emailDataDTO.getTimesheetId() + "_" + uniqueId
							+ ".vm");
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

					if (StringUtils.isNotBlank(emailDataDTO.getEmailTo())) {
						payAsiaEmailTO.addMailTo(emailDataDTO.getEmailTo());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getSubject())) {
						payAsiaEmailTO.setMailSubject(emailTemplateVO
								.getSubject());
					}
					if (StringUtils.isNotBlank(emailTemplateVO.getBody())) {
						payAsiaEmailTO.setMailText(emailTemplateVO.getBody());
					}
					String systemEmailFrom = isSystemMailReq(companyId);
					if (StringUtils.isNotBlank(systemEmailFrom)) {
						payAsiaEmailTO.setMailFrom(systemEmailFrom);
					} else {
						payAsiaEmailTO.setMailFrom(emailDataDTO.getEmailFrom());
					}

					payAsiaMailUtils.sendEmailForOTTimesheet(
							modelMap,
							emailTemplate.getPath().substring(
									emailTemplate.getParent().length() + 1),
							emailSubjectTemplate.getPath()
									.substring(
											emailSubjectTemplate.getParent()
													.length() + 1), true,
							payAsiaEmailTO);

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

			return "password.are.not.sent.to.these.employees.excluding.employe.numbers.are/"
					+ returnEmpIds;

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
	public Long getSubCategoryId(
			String subCategoryName,
			List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList) {
		for (EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster : EmailTemplateSubCategoryMasterList) {
			if ((subCategoryName.toUpperCase())
					.equals(emailTemplateSubCategoryMaster.getSubCategoryName()
							.toUpperCase())) {
				return emailTemplateSubCategoryMaster
						.getEmailTemplateSubCategoryId();
			}
		}
		return null;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		return employeeName;
	}

	private String isSystemMailReq(Long companyId) {
		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		String fromAddress = "";
		if (coherentTimesheetPreferenceVO.isUseSystemMailAsFromAddress()) {

			EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO
					.findByConditionCompany(companyId);
			fromAddress = emailPreferenceMaster
					.getSystemEmailForSendingEmails();
		}
		return fromAddress;
	}

}
