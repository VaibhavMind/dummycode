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

import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.HRISChangeRequestWorkflowDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.dao.bean.HRISChangeRequestWorkflow;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.HRISMailLogic;

@Component
public class HRISMailLogicImpl implements HRISMailLogic {

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

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	HRISChangeRequestWorkflowDAO hrisChangeRequestWorkflowDAO;

	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HRISMailLogicImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendEMailForHRISDataChange(Long companyId,
			HRISChangeRequest hrisChangeRequest,
			HRISChangeRequestWorkflow requestWorkflow, String subCategoryName,
			Employee loggedInEmployee, Employee reviewer,
			EmployeeListFormPage employeeListFormPage) {

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
						hrisChangeRequest.getEmployee().getCompany()
								.getCompanyId());
		if (emailTemplateVO == null) {
			return "payasia.hris.data.change.template.is.not.defined";
		}
		EmailPreferenceMaster emailPreferenceURL = emailPreferenceMasterDAO
				.findByConditionCompany(reviewer.getCompany().getCompanyId());
		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (hrisChangeRequest != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();

			if (StringUtils.isNotBlank(requestWorkflow.getForwardTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME,
						getEmployeeName(hrisChangeRequest.getEmployee()));
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NUMBER,
						hrisChangeRequest.getEmployee().getEmployeeNumber());
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_REVIEWER_NAME,
						reviewer.getFirstName());

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_FIELD,
						employeeListFormPage.getFieldName());
				if (StringUtils.isNotBlank(employeeListFormPage.getOldValue())) {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
							employeeListFormPage.getOldValue());
				} else {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
							"");
				}
				if (StringUtils.isNotBlank(employeeListFormPage.getNewValue())) {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
							employeeListFormPage.getNewValue());
				} else {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
							"");
				}

				modelMap.put(
						PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_CREATED_DATE,
						DateUtils.timeStampToString(hrisChangeRequest
								.getCreatedDate()));

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_REMARKS,
						requestWorkflow.getRemarks());
				if (emailPreferenceURL != null) {
					if (StringUtils.isNotBlank(emailPreferenceURL
							.getCompanyUrl())) {
						String link = "<a href='"
								+ emailPreferenceURL.getCompanyUrl() + "'>"
								+ emailPreferenceURL.getCompanyUrl() + "</a>";
						modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL,
								link);
					} else {
						modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL,
								"");
					}
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendHRISDataChangeTemplate"
							+ hrisChangeRequest.getHrisChangeRequestId() + "_"
							+ uniqueId + ".vm");
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendHRISDataChangeSubject"
							+ hrisChangeRequest.getHrisChangeRequestId() + "_"
							+ uniqueId + ".vm");
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

					if (StringUtils.isNotBlank(requestWorkflow.getForwardTo())) {
						payAsiaEmailTO
								.addMailTo(requestWorkflow.getForwardTo());
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
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";

					if (StringUtils.isNotBlank(requestWorkflow.getEmailCC())) {
						String ccMailIds = requestWorkflow.getEmailCC();
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

					payAsiaMailUtils.sendEmailForHRISDataChange(
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendWithdrawEmailForHRISDataChange(Long companyId,
			HRISChangeRequest hrisChangeRequest, String subCategoryName,
			Employee loggedInEmployee, EmployeeListFormPage employeeListFormPage) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();
		HRISChangeRequestWorkflow requestWorkflow = hrisChangeRequestWorkflowDAO
				.findByCondition(hrisChangeRequest.getHrisChangeRequestId(),
						hrisChangeRequest.getEmployee().getEmployeeId());

		Long subCategoryId = getSubCategoryId(subCategoryName,
				emailTemplateSubCategoryMasterList);
		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(null, subCategoryId,
						hrisChangeRequest.getEmployee().getCompany()
								.getCompanyId());
		if (emailTemplateVO == null) {
			return "payasia.hris.data.change.template.is.not.defined";
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

		if (hrisChangeRequest != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils.isNotBlank(requestWorkflow.getForwardTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME,
						getEmployeeName(hrisChangeRequest.getEmployee()));
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NUMBER,
						hrisChangeRequest.getEmployee().getEmployeeNumber());

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_REMARKS,
						requestWorkflow.getRemarks());

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_FIELD,
						employeeListFormPage.getFieldName());
				if (StringUtils.isNotBlank(employeeListFormPage.getOldValue())) {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
							employeeListFormPage.getOldValue());
				} else {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
							"");
				}
				if (StringUtils.isNotBlank(employeeListFormPage.getNewValue())) {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
							employeeListFormPage.getNewValue());
				} else {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
							"");
				}

				modelMap.put(
						PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_CREATED_DATE,
						DateUtils.timeStampToString(hrisChangeRequest
								.getCreatedDate()));

				if (StringUtils.isNotBlank(emailPreferenceMasterVO
						.getCompanyUrl())) {
					String link = "<a href='"
							+ emailPreferenceMasterVO.getCompanyUrl() + "'>"
							+ emailPreferenceMasterVO.getCompanyUrl() + "</a>";
					modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL, link);
				} else {
					modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL, "");
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendHRISDataChangeTemplate"
							+ hrisChangeRequest.getHrisChangeRequestId() + "_"
							+ uniqueId + ".vm");
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendHRISDataChangeTemplateSubject"
							+ hrisChangeRequest.getHrisChangeRequestId() + "_"
							+ uniqueId + ".vm");
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

					if (StringUtils.isNotBlank(requestWorkflow.getForwardTo())) {
						payAsiaEmailTO
								.addMailTo(requestWorkflow.getForwardTo());
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
						payAsiaEmailTO.setMailFrom(loggedInEmployee.getEmail());
					}

					String defaultMailCCIds = "";

					if (StringUtils.isNotBlank(requestWorkflow.getEmailCC())) {
						String ccMailIds = requestWorkflow.getEmailCC();
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

					payAsiaMailUtils.sendEmailForHRISDataChange(
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.SendPasswordLogic#sendPwdEmail(java.lang.Long,
	 * java.lang.String[])
	 */
	@Override
	public String sendAcceptRejectMailForHRISDataChange(Long companyId,
			HRISChangeRequestWorkflow hrisChangeRequestWorkflow,
			String subCategoryName, Employee loggedInEmployee,
			String reviewRemarks, EmployeeListFormPage employeeListFormPage) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName,
				emailTemplateSubCategoryMasterList);
		HRISChangeRequest hrisChangeRequest = hrisChangeRequestWorkflow
				.getHrisChangeRequest();

		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(null, subCategoryId,
						hrisChangeRequest.getEmployee().getCompany()
								.getCompanyId());
		if (emailTemplateVO == null) {
			return "payasia.hris.data.change.template.is.not.defined";
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

		if (hrisChangeRequest != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			String mailBody = emailTemplateVO.getBody();
			String mailSubject = emailTemplateVO.getSubject();
			Map<String, Object> modelMap = new HashMap<String, Object>();

			modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME,
					getEmployeeName(hrisChangeRequest.getEmployee()));
			modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NUMBER,
					hrisChangeRequest.getEmployee().getEmployeeNumber());

			modelMap.put(PayAsiaConstants.HRIS_EMAIL_REMARKS,
					hrisChangeRequestWorkflow.getRemarks());
			modelMap.put(PayAsiaConstants.HRIS_EMAIL_REVIEWER_REMARKS,
					reviewRemarks);

			modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_FIELD,
					employeeListFormPage.getFieldName());

			if (StringUtils.isNotBlank(employeeListFormPage.getOldValue())) {
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
						employeeListFormPage.getOldValue());
			} else {
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
						"");
			}
			if (StringUtils.isNotBlank(employeeListFormPage.getNewValue())) {
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
						employeeListFormPage.getNewValue());
			} else {
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
						"");
			}

			modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_CREATED_DATE,
					DateUtils.timeStampToString(hrisChangeRequest
							.getCreatedDate()));

			if (StringUtils.isNotBlank(emailPreferenceMasterVO.getCompanyUrl())) {
				String link = "<a href='"
						+ emailPreferenceMasterVO.getCompanyUrl() + "'>"
						+ emailPreferenceMasterVO.getCompanyUrl() + "</a>";
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL, link);
			} else {
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL, "");
			}

			try {
				byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
				byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
				String uniqueId = RandomNumberGenerator
						.getNDigitRandomNumber(8);
				emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
						+ "sendHRISDataChangeTemplate"
						+ hrisChangeRequest.getHrisChangeRequestId() + "_"
						+ uniqueId + ".vm");
				emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
						+ "sendHRISDataChangeTemplateSubject"
						+ hrisChangeRequest.getHrisChangeRequestId() + "_"
						+ uniqueId + ".vm");
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

				if (hrisChangeRequest.getEmployee() != null) {
					payAsiaEmailTO.addMailTo(hrisChangeRequest.getEmployee()
							.getEmail());
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

				if (StringUtils.isNotBlank(hrisChangeRequestWorkflow
						.getEmailCC())) {
					String ccMailIds = hrisChangeRequestWorkflow.getEmailCC();
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

				payAsiaMailUtils.sendEmailForHRISDataChange(
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
	public String sendPendingEmailForHRISDataChange(Long companyId,
			HRISChangeRequestWorkflow hrisChangeRequestWorkflow,
			String subCategoryName, Employee loggenInEmployee,
			String revRemarks, Employee reviewer,
			EmployeeListFormPage employeeListFormPage) {
		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName,
				emailTemplateSubCategoryMasterList);

		HRISChangeRequest hrisChangeRequest = hrisChangeRequestWorkflow
				.getHrisChangeRequest();
		EmailTemplate emailTemplateVO = emailTemplateDAO
				.findByConditionSubCategoryAndCompId(null, subCategoryId,
						hrisChangeRequest.getEmployee().getCompany()
								.getCompanyId());
		if (emailTemplateVO == null) {
			return "payasia.hris.data.change.template.is.not.defined";
		}
		EmailPreferenceMaster emailPreferencURL = emailPreferenceMasterDAO
				.findByConditionCompany(reviewer.getCompany().getCompanyId());

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO
				.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			return "email.configuration.is.not.defined";
		}
		if (StringUtils.isBlank(emailPreferenceMasterVO.getContactEmail())) {
			return "email.configuration.contact.email.is.not.defined";
		}

		String returnEmpIds = "";

		if (hrisChangeRequest != null) {

			PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
			if (StringUtils
					.isNotBlank(hrisChangeRequestWorkflow.getForwardTo())) {
				String mailBody = emailTemplateVO.getBody();
				String mailSubject = emailTemplateVO.getSubject();
				Map<String, Object> modelMap = new HashMap<String, Object>();

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME,
						getEmployeeName(hrisChangeRequest.getEmployee()));
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NUMBER,
						hrisChangeRequest.getEmployee().getEmployeeNumber());
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_REVIEWER_NAME,
						reviewer.getFirstName());

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_FIELD,
						employeeListFormPage.getFieldName());

				if (StringUtils.isNotBlank(employeeListFormPage.getOldValue())) {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
							employeeListFormPage.getOldValue());
				} else {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_OLD_VALUE,
							"");
				}
				if (StringUtils.isNotBlank(employeeListFormPage.getNewValue())) {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
							employeeListFormPage.getNewValue());
				} else {
					modelMap.put(
							PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_NEW_VALUE,
							"");
				}

				modelMap.put(
						PayAsiaConstants.HRIS_EMAIL_DATA_CHANGE_CREATED_DATE,
						DateUtils.timeStampToString(hrisChangeRequest
								.getCreatedDate()));

				modelMap.put(PayAsiaConstants.HRIS_EMAIL_REMARKS,
						hrisChangeRequestWorkflow.getRemarks());
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_CURRENT_USER,
						getEmployeeName(loggenInEmployee));
				modelMap.put(PayAsiaConstants.HRIS_EMAIL_REVIEWER_REMARKS,
						revRemarks);
				if (emailPreferencURL != null) {
					if (StringUtils.isNotBlank(emailPreferencURL
							.getCompanyUrl())) {
						String link = "<a href='"
								+ emailPreferencURL.getCompanyUrl() + "'>"
								+ emailPreferencURL.getCompanyUrl() + "</a>";
						modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL,
								link);
					} else {
						modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_URL,
								"");
					}
				}

				try {
					byte[] mailBodyBytes = mailBody.getBytes("UTF-8");
					byte[] mailSubjectBytes = mailSubject.getBytes("UTF-8");
					String uniqueId = RandomNumberGenerator
							.getNDigitRandomNumber(8);
					emailTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendHRISDataChangeTemplate"
							+ hrisChangeRequest.getHrisChangeRequestId() + "_"
							+ uniqueId + ".vm");
					emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//"
							+ "sendHRISDataChangeTemplateSubject"
							+ hrisChangeRequest.getHrisChangeRequestId() + "_"
							+ uniqueId + ".vm");
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

					if (StringUtils.isNotBlank(hrisChangeRequestWorkflow
							.getForwardTo())) {
						payAsiaEmailTO.addMailTo(hrisChangeRequestWorkflow
								.getForwardTo());
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
						payAsiaEmailTO.setMailFrom(loggenInEmployee.getEmail());
					}

					String defaultMailCCIds = "";

					if (StringUtils.isNotBlank(hrisChangeRequestWorkflow
							.getEmailCC())) {
						String ccMailIds = hrisChangeRequestWorkflow
								.getEmailCC();
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

					payAsiaMailUtils.sendEmailForHRISDataChange(
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
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO
				.findByCompanyId(companyId);
		String fromAddress = "";
		if (hrisPreferenceVO.isUseSystemMailAsFromAddress()) {

			EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO
					.findByConditionCompany(companyId);
			fromAddress = emailPreferenceMaster
					.getSystemEmailForSendingEmails();
		}
		return fromAddress;
	}
}
