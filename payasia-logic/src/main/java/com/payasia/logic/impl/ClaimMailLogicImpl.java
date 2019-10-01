package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mind.payasia.xml.bean.Field;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.dto.ClaimMailDTO;
import com.payasia.common.dto.ColumnPropertyDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.EmailConfDTO;
import com.payasia.common.dto.EmailDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaEmailTO;
import com.payasia.common.util.PayAsiaMailUtils;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.common.util.PayAsiaStringUtils;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimApplicationDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimApplicationItemWorkflowDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.DynamicFormFieldRefValueDAO;
import com.payasia.dao.DynamicFormRecordDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.GeneralDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemAttachment;
import com.payasia.dao.bean.ClaimApplicationItemWorkflow;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyModuleMapping;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.ClaimFormPrintPDFLogic;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.DataExportUtils;

@Component
public class ClaimMailLogicImpl implements ClaimMailLogic {

	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String folderPath;
	@Value("#{payasiaptProperties['payasia.document.path.separator']}")
	private String docPathSeperator;
	@Value("#{payasiaptProperties['payasia.s3.bucket.name']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;
	@Resource
	CompanyDAO companyDAO;
	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;
	@Resource
	EmailTemplateDAO emailTemplateDAO;
	@Resource
	ClaimApplicationDAO claimApplicationDAO;
	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;
	@Resource
	ClaimFormPrintPDFLogic claimFormPrintPDFLogic;
	@Resource
	AWSS3Logic awss3LogicImpl;
	@Resource
	PayAsiaMailUtils payAsiaMailUtils;

	@Resource
	DataDictionaryDAO dataDictionaryDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralDAO generalDAO;
	@Resource
	ClaimApplicationItemWorkflowDAO claimApplicationItemWorkflowDAO;
	@Resource
	DynamicFormDAO dynamicFormDAO;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;
	@Resource
	DynamicFormRecordDAO dynamicFormRecordDAO;
	@Resource
	DynamicFormFieldRefValueDAO dynamicFormFieldRefValueDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	private DataExportUtils dataExportUtils;
	@Resource
	FileUtils fileUtils;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(ClaimMailLogicImpl.class);

	@Override
	public String sendClaimMail(ClaimMailDTO claimMailDTO) {

		EmailConfDTO emailConfDTO = checkEmailConfiguration(claimMailDTO.getSubcategoryName(),
				claimMailDTO.getClaimApplication().getCompany().getCompanyId());
		if (StringUtils.isNotBlank(emailConfDTO.getErrorKey())) {
			return emailConfDTO.getErrorKey();
		} else {

			EmailDTO email = setEmail(emailConfDTO, claimMailDTO);
			try {
				payAsiaMailUtils.sendClaimEmail(email);

			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
			} finally {
				email.getEmailBody().delete();
				email.getEmailSubject().delete();
				if (email.getAttachment() != null) {
					FileUtils.deleteFolder(email.getAttachment().getParentFile().getAbsolutePath());
				}
			}
			return "";
		}

	}

	private EmailDTO setEmail(EmailConfDTO emailConfDTO, ClaimMailDTO claimMailDTO) {

		Employee loggedInEmployee = employeeDAO.findById(claimMailDTO.getLoggedInEmpId());
		String fromAddress = "";
		String systemEmailFrom = isSystemMailReq(claimMailDTO.getClaimApplication().getCompany().getCompanyId());
		if (StringUtils.isNotBlank(systemEmailFrom)) {
			fromAddress = systemEmailFrom;
		} else {
			fromAddress = loggedInEmployee.getEmail();
		}

		String toAddress = getToAddress(emailConfDTO, claimMailDTO);

		File emailBody = null;
		File emailSubject = null;
		FileOutputStream emailBodyFos = null;
		FileOutputStream emailSubFos = null;
		PayAsiaEmailTO payAsiaEmailTO = new PayAsiaEmailTO();
		EmailDTO emailDTO = new EmailDTO();
		EmailTemplate emailTemplate = emailConfDTO.getEmailTemplate();

		String mailBody = emailTemplate.getBody();
		String mailSubject = emailTemplate.getSubject();
		Map<String, Object> emailParamMap = new HashMap<String, Object>();

		emailParamMap.put(PayAsiaConstants.Claim_EMPLOYEE_NUMBER,
				claimMailDTO.getClaimApplication().getEmployee().getEmployeeNumber());
		emailParamMap.put(PayAsiaConstants.Claim_EMPLOYEE_NAME,
				getEmployeeName(claimMailDTO.getClaimApplication().getEmployee()));
		emailParamMap.put(PayAsiaConstants.Claim_TOTAL_COUNT,
				claimMailDTO.getClaimApplication().getClaimApplicationItems().size());
		emailParamMap.put(PayAsiaConstants.Claim_TOTAL_AMOUNT, claimMailDTO.getClaimApplication().getTotalAmount());
		emailParamMap.put(PayAsiaConstants.CLAIM_NUMBER, claimMailDTO.getClaimApplication().getClaimNumber());

		String claimUserRemarks = "";
		if (StringUtils.isNotBlank(claimMailDTO.getClaimApplication().getRemarks())) {
			try {
				claimUserRemarks = URLDecoder.decode(claimMailDTO.getClaimApplication().getRemarks(), "UTF-8");
			} catch (IllegalArgumentException | UnsupportedEncodingException e) {
				claimUserRemarks = claimMailDTO.getClaimApplication().getRemarks();
				LOGGER.error(e.getMessage(), e);
			}
		}

		emailParamMap.put(PayAsiaConstants.CLAIM_REMARKS, claimUserRemarks);

		String claimRevRemarks = "";
		try {
			if (StringUtils.isNotBlank(claimMailDTO.getReviewerRemarks())) {
				claimRevRemarks = URLDecoder.decode(claimMailDTO.getReviewerRemarks(), "UTF-8");
			} else {
				claimRevRemarks = claimMailDTO.getReviewerRemarks();
			}
		} catch (IllegalArgumentException | UnsupportedEncodingException e) {
			claimRevRemarks = claimMailDTO.getReviewerRemarks();
			LOGGER.error(e.getMessage(), e);
		}
		emailParamMap.put(PayAsiaConstants.Claim_REVIEWER_REMARKS, claimRevRemarks);

		emailParamMap.put(PayAsiaConstants.Claim_TEMPLATE_DESCRIPTION,
				claimMailDTO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate() == null ? ""
						: claimMailDTO.getClaimApplication().getEmployeeClaimTemplate().getClaimTemplate()
								.getTemplateName());

		Set<ClaimApplicationItem> claimApplicationItems = claimMailDTO.getClaimApplication().getClaimApplicationItems();
		String modifiedClaimItems = "";
		AppCodeMaster appCodeMasterForOverrideAction = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION,
				PayAsiaConstants.APP_CODE_ClAIM_ITEM_WORKFLOW_ACTION_OVERRIDE);
		StringBuilder builder = new StringBuilder();
		for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {
			ClaimApplicationItemWorkflow claimApplicationItemWorkflow = claimApplicationItemWorkflowDAO.findByClaimItem(
					claimApplicationItem.getClaimApplicationItemId(), appCodeMasterForOverrideAction.getAppCodeID());
			if (claimApplicationItemWorkflow != null) {
				if (claimApplicationItemWorkflow.getOverriddenAmount() != null) {
					builder = builder.append(PayAsiaConstants.CLAIM_MODIFIED_CLAIMS_CLAIM_ITEM);
					builder = builder.append(": ");
					builder = builder.append(claimApplicationItem.getEmployeeClaimTemplateItem().getClaimTemplateItem()
							.getClaimItemMaster().getClaimItemName());
					builder = builder.append(", ");
					builder = builder.append(PayAsiaConstants.CLAIM_MODIFIED_CLAIMS_OLD_VALUE);
					builder = builder.append(":");
					builder = builder.append(claimApplicationItemWorkflow.getOverriddenAmount());
					builder = builder.append(", ");
					builder = builder.append(PayAsiaConstants.CLAIM_MODIFIED_CLAIMS_NEW_VALUE);
					builder = builder.append(":");
					builder = builder.append(claimApplicationItem.getClaimAmount());
					builder = builder.append("</br>");

				}
			}

		}
		modifiedClaimItems = builder.toString();
		emailParamMap.put(PayAsiaConstants.CLAIM_MODIFIED_CLAIMS, modifiedClaimItems);

		String url = getReviewerURL(emailConfDTO, claimMailDTO.getClaimApplication().getClaimApplicationReviewers());

		if (emailConfDTO.getEmailTemplate().getEmailTemplateSubCategoryMaster().getSubCategoryName()
				.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_FORWARD_EMPLOYEE)) {

			setReviewerDetails(emailParamMap, claimMailDTO.getClaimApplication().getClaimApplicationReviewers());

		}
		if (StringUtils.isBlank(url)) {
			url = "";
		}

		emailParamMap.put(PayAsiaConstants.LEAVE_COMPANY_URL, url);

		try {

			byte[] mailBodyBytes = mailBody.getBytes();
			byte[] mailSubjectBytes = mailSubject.getBytes();
			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);
			emailBody = new File(PAYASIA_TEMP_PATH + "//" + "sendClaimTemplate"
					+ claimMailDTO.getClaimApplication().getClaimApplicationId() + "_" + uniqueId + ".vm");
			emailSubject = new File(PAYASIA_TEMP_PATH + "//" + "sendClaimTemplateSubject"
					+ claimMailDTO.getClaimApplication().getClaimApplicationId() + "_" + uniqueId + ".vm");

			emailBody.deleteOnExit();
			emailSubject.deleteOnExit();

			emailBodyFos = new FileOutputStream(emailBody);
			emailBodyFos.write(mailBodyBytes);
			emailBodyFos.flush();
			emailBodyFos.close();
			emailSubFos = new FileOutputStream(emailSubject);
			emailSubFos.write(mailSubjectBytes);
			emailSubFos.flush();
			emailSubFos.close();

			payAsiaEmailTO.addMailTo(toAddress);
			payAsiaEmailTO.setMailSubject(emailTemplate.getSubject());
			payAsiaEmailTO.setMailText(emailTemplate.getBody());
			payAsiaEmailTO.setMailFrom(fromAddress);

			String defaultMailCCIds = getDefaultCC(claimMailDTO.getClaimApplication());

			if (StringUtils.isNotBlank(claimMailDTO.getClaimApplication().getEmailCC())
					&& claimMailDTO.getSubcategoryName().equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_APPLY)) {
				String ccMailIds = claimMailDTO.getClaimApplication().getEmailCC();
				if (StringUtils.isNotBlank(defaultMailCCIds)) {
					defaultMailCCIds += ccMailIds;
				} else {
					defaultMailCCIds = ccMailIds;
				}

				String[] ccEmailIds = defaultMailCCIds.split(";");
				for (String ccEmailId : ccEmailIds) {
					payAsiaEmailTO.addMainCc(ccEmailId);
				}

			}

			if (claimMailDTO.getClaimApplicationWorkflow() != null
					&& StringUtils.isNotBlank(claimMailDTO.getClaimApplicationWorkflow().getEmailCC())) {
				String ccMailIds = claimMailDTO.getClaimApplicationWorkflow().getEmailCC();
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

			if (claimMailDTO.isAttachmentRequired()) {
				ClaimPreference claimPreference = claimPreferenceDAO
						.findByCompanyId(claimMailDTO.getClaimApplication().getCompany().getCompanyId());
				if (claimPreference != null && claimPreference.getAttachmentForMail() != null) {
					ClaimApplication claimApplication = claimMailDTO.getClaimApplication();

					FileUtils.createFolder(folderPath);
					UUID uuid = UUID.randomUUID();
					String dynamicFolderName = claimApplication.getEmployee().getEmployeeNumber() + "_"
							+ claimApplication.getClaimNumber();

					String directoryPath = folderPath + docPathSeperator + dynamicFolderName;
					boolean isSubFolderCreated = FileUtils.createSubFolder(directoryPath);
					if (isSubFolderCreated) {

						List<ClaimApplicationItemAttach> attachements = getAllAttachmentRelatedClaim(
								claimApplication.getClaimApplicationId());
						if (attachements.size() > 0) {
							for (ClaimApplicationItemAttach attachment : attachements) {
								String fileExt = attachment.getFileType();
								downloadAttachmentFiles(downloadPath, rootDirectoryName,
										claimPreference.getCompany().getCompanyId(),
										PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME, directoryPath,
										attachment.getFileName(),

										String.valueOf(attachment.getClaimApplicationItemAttachementId()), fileExt,
										PayAsiaConstants.PAYASIA_DOC_READALL_EVENT, 0);

							}
						}
						String reportName = claimApplication.getEmployee().getEmployeeNumber() + "_"
								+ claimApplication.getClaimNumber() + uuid + ".pdf";
						Company companyVO = claimPreference.getCompany();
						boolean isDownloadedClaimReport = createClaimReport(directoryPath, reportName, claimApplication,
								claimApplication.getEmployee().getEmployeeId(),
								claimMailDTO.getClaimApplication().getCompany().getCompanyId(),
								hasLundinTimesheetModule(companyVO.getCompanyId()));
						if (isDownloadedClaimReport) {

							DataDictionary dataDictionary = claimPreference.getPwdForAttachmentMail();

							String originalValue = null;
							if (dataDictionary.getFieldType().equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {
								Object value = employeeDAO.getEmpByValueCustomField(companyVO.getCompanyId(),
										claimApplication.getEmployee().getEmployeeId(), dataDictionary.getColumnName());

								if (value != null) {

									if (PayAsiaConstants.PAYASIA_EMPLOYEE_Confirmation_Date
											.equalsIgnoreCase(dataDictionary.getColumnName())
											|| PayAsiaConstants.PAYASIA_EMPLOYEE_HIREDATE
													.equalsIgnoreCase(dataDictionary.getColumnName())
											|| PayAsiaConstants.PAYASIA_EMPLOYEE_Original_Hire_Date
													.equalsIgnoreCase(dataDictionary.getColumnName())
											|| PayAsiaConstants.PAYASIA_EMPLOYEE_Resignation_Date
													.equalsIgnoreCase(dataDictionary.getColumnName())) {
										Timestamp date = (Timestamp) value;
										originalValue = DateUtils.timeStampToString(date, companyVO.getDateFormat());
									}

									else {
										originalValue = String.valueOf(value);
									}
								}
							} else {
								originalValue = getEmployeeDynamicFieldValue(dataDictionary, companyVO.getCompanyId(),
										companyVO.getDateFormat(), claimApplication.getEmployee().getEmployeeId(),
										claimApplication.getEmployee().getEmployeeNumber());

							}

							if (StringUtils.isBlank(originalValue)) {
								originalValue = claimApplication.getEmployee().getEmployeeNumber();
							}

							List<File> files = FileUtils.getFiles(directoryPath);
							String zipFolderName = "ClaimAttachments" + "_" + claimApplication.getClaimNumber();
							String zipFilePath = directoryPath + docPathSeperator + zipFolderName + ".zip";

							boolean isZipPwdCreated = FileUtils.createPasswordProtectedZip(zipFilePath, files,
									originalValue);
							if (isZipPwdCreated) {
								File destDir = new File(zipFilePath);
								if (destDir.exists()) {
									emailDTO.setAttachment(destDir);
								}

							}
						}

					}

				}
			}

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		} finally {
			if (emailBodyFos != null) {
				try {
					emailBodyFos.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		emailDTO.setEmailBody(emailBody);
		emailDTO.setEmailSubject(emailSubject);
		emailDTO.setPayAsiaEmailTO(payAsiaEmailTO);
		emailDTO.setEmailParamMap(emailParamMap);
		return emailDTO;
	}

	private String isSystemMailReq(Long companyId) {
		ClaimPreference claimPreferenceVO = claimPreferenceDAO.findByCompanyId(companyId);
		String fromAddress = "";
		if (claimPreferenceVO.isUseSystemMailAsFromAddress()) {

			EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO.findByConditionCompany(companyId);
			fromAddress = emailPreferenceMaster.getSystemEmailForSendingEmails();
		}
		return fromAddress;
	}

	private String getDefaultCC(ClaimApplication claimApplication) {
		String defaultEmailCC = "";
		ClaimTemplate claimTemplate = claimApplication.getEmployeeClaimTemplate().getClaimTemplate();
		if (claimTemplate.getSendMailToDefaultCC()) {
			if (StringUtils.isNotBlank(claimTemplate.getDefaultCC())) {
				defaultEmailCC = claimTemplate.getDefaultCC();
				return defaultEmailCC + ";";
			}
		}
		return "";
	}

	private void setReviewerDetails(Map<String, Object> emailParamMap,
			Set<ClaimApplicationReviewer> claimApplicationReviewers) {
		Employee revEmployee = null;

		for (ClaimApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			if (claimApplicationReviewer.getPending()) {
				revEmployee = claimApplicationReviewer.getEmployee();
			}

		}
		emailParamMap.put(PayAsiaConstants.Claim_REVIEWER_USER_NAME, getEmployeeName(revEmployee));

		emailParamMap.put(PayAsiaConstants.Claim_REVIEWER_EMPLOYEE_NUMBER, revEmployee.getEmployeeNumber());

	}

	private String getToAddress(EmailConfDTO emailConfDTO, ClaimMailDTO claimMailDTO) {
		String toAddress = "";
		Employee revEmployee = null;

		for (ClaimApplicationReviewer claimApplicationReviewer : claimMailDTO.getClaimApplication()
				.getClaimApplicationReviewers()) {
			if (claimApplicationReviewer.getPending()) {
				revEmployee = claimApplicationReviewer.getEmployee();
			}

		}
		String templateName = emailConfDTO.getEmailTemplate().getEmailTemplateSubCategoryMaster().getSubCategoryName();

		if (templateName.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_APPLY)
				|| templateName.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_FORWARD_REVIEWER)
				|| templateName.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_WITHDRAWN_REVIEWER)) {
			toAddress = revEmployee.getEmail();

		} else if (templateName.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_ACCEPTED)
				|| templateName.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_REJECTED)
				|| templateName.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_FORWARD_EMPLOYEE)) {
			toAddress = claimMailDTO.getClaimApplication().getEmployee().getEmail();

		}

		return toAddress;
	}

	private String getReviewerURL(EmailConfDTO emailConfDTO, Set<ClaimApplicationReviewer> claimAppReviewers) {
		String url = "";

		if (emailConfDTO.getEmailTemplate().getName().equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_APPLY)
				|| emailConfDTO.getEmailTemplate().getName()
						.equals(PayAsiaConstants.PAYASIA_SUB_CATEGORY_ClAIM_FORWARD_REVIEWER)) {

			Employee reviewer = null;

			for (ClaimApplicationReviewer claimApplicationReviewer : claimAppReviewers) {
				if (claimApplicationReviewer.getPending()) {
					reviewer = claimApplicationReviewer.getEmployee();
				}

			}
			EmailPreferenceMaster emailPreferenceURL = emailPreferenceMasterDAO
					.findByConditionCompany(reviewer.getCompany().getCompanyId());
			if (emailPreferenceURL != null) {

				String companyURL = "";
				if (StringUtils.isNotBlank(emailPreferenceURL.getCompanyUrl())) {
					companyURL = emailPreferenceURL.getCompanyUrl();
				}

				url = "<a href='" + companyURL + "'>" + companyURL + "</a>";
			}

		}
		return url;
	}

	private EmailConfDTO checkEmailConfiguration(String subCategoryName, Long companyId) {
		EmailConfDTO emailConfDTO = new EmailConfDTO();

		List<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();

		Long subCategoryId = getSubCategoryId(subCategoryName, emailTemplateSubCategoryMasterList);

		if (subCategoryId == null) {
			emailConfDTO.setErrorKey("send.claim.sub.category.not.available");
			return emailConfDTO;

		}

		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategoryAndCompId(null, subCategoryId,
				companyId);
		emailConfDTO.setEmailTemplate(emailTemplateVO);
		if (emailTemplateVO == null) {
			emailConfDTO.setErrorKey("send.claim.template.is.not.defined");
		}

		EmailPreferenceMaster emailPreferenceMasterVO = emailPreferenceMasterDAO.findByConditionCompany(companyId);
		if (emailPreferenceMasterVO == null) {
			emailConfDTO.setErrorKey("email.configuration.is.not.defined");
		}
		return emailConfDTO;

	}

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

	/**
	 * @author anuragsrivastav
	 * @return List of all attachment related to particular claim
	 */

	private List<ClaimApplicationItemAttach> getAllAttachmentRelatedClaim(final long claimApplicationId) {
		final List<ClaimApplicationItemAttach> attachements = new ArrayList<>();
		final List<ClaimApplicationItem> claimApplicationItems = claimApplicationItemDAO
				.findByCondition(claimApplicationId, PayAsiaConstants.DESC);
		if (!(claimApplicationItems.isEmpty()) || claimApplicationItems.size() > 0 || claimApplicationItems != null) {
			for (ClaimApplicationItem claimApplicationItem : claimApplicationItems) {

				for (ClaimApplicationItemAttachment claimApplicationItemAttachment : claimApplicationItem
						.getClaimApplicationItemAttachments()) {
					ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
					claimApplicationItemAttach.setClaimApplicationItemAttachementId(
							claimApplicationItemAttachment.getClaimApplicationItemAttachmentId());
					claimApplicationItemAttach.setFileName(claimApplicationItemAttachment.getFileName());

					claimApplicationItemAttach.setFileType(claimApplicationItemAttachment.getFileType());
					attachements.add(claimApplicationItemAttach);
				}

			}
		}
		return attachements;
	}

	/**
	 * @author anuragsrivastav
	 * @param downloadPath
	 * @param rootDirectoryName
	 * @param companyId
	 * @param docDirectoryName
	 * @param eventName
	 * @param topicId
	 * @return
	 */

	private boolean downloadAttachmentFiles(String downloadPath, String rootDirectoryName, long companyId,
			String docDirectoryName, String originalFilePath, String originalFileName, String fileName, String fileExt,
			String eventName, long topicId) {
		boolean downloadStatus = true;
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.CLAIM_ATTACHMENT_DIRECTORY_NAME, fileName, null, null, fileExt,
				PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
		String filePath = fileUtils.getAttachmentMailFilePath(filePathGenerator);
		byte[] byteFile = null;
		File file = new File(filePath);

		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				byteFile = org.apache.commons.io.IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
			} else {
				byteFile = Files.readAllBytes(file.toPath());
			}

			FileUtils.createAttachmentFile(byteFile, originalFileName, originalFilePath, fileExt);
		} catch (IOException e) {
			downloadStatus = false;
			LOGGER.error(e.getMessage(), e);
		}
		return downloadStatus;
	}

	private boolean createClaimReport(String downloadPath, String reportName, ClaimApplication claimApplicationVO,
			Long employeeId, Long companyId, boolean hasLundinTimesheetModule) {
		boolean isDownloaded = false;
		ClaimFormPdfDTO claimFormPdfDTO = new ClaimFormPdfDTO();
		claimFormPdfDTO.setClaimTemplateName(
				claimApplicationVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName());
		claimFormPdfDTO.setEmployeeNumber(claimApplicationVO.getEmployee().getEmployeeNumber());

		PDFThreadLocal.pageNumbers.set(true);
		try {
			claimFormPdfDTO.setClaimFormPdfByteFile(generateClaimFormPDF(companyId, employeeId,
					claimApplicationVO.getClaimApplicationId(), hasLundinTimesheetModule));

			FileUtils.createAttachmentFile(claimFormPdfDTO.getClaimFormPdfByteFile(), reportName, downloadPath, "pdf");
			isDownloaded = true;
		} catch (DocumentException | IOException | JAXBException | SAXException e) {
			LOGGER.error("Error while genereating/downloading Claim Report for mail" + e.getMessage());
			isDownloaded = false;
		}

		return isDownloaded;
	}

	private byte[] generateClaimFormPDF(Long companyId, Long employeeId, Long claimApplicationId,
			boolean hasLundinTimesheetModule) throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId, PAYASIA_TEMP_PATH, "ClaimForm");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;

		try {
			document = new Document(PayAsiaPDFConstants.PAGE_SIZE, PayAsiaPDFConstants.PAGE_LEFT_MARGIN,
					PayAsiaPDFConstants.PAGE_TOP_MARGIN, PayAsiaPDFConstants.PAGE_RIGHT_MARGIN,
					PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			PdfPTable claimReportPdfTable = claimFormPrintPDFLogic.createClaimReportPdf(document, writer, 1, companyId,
					claimApplicationId, hasLundinTimesheetModule);

			document.add(claimReportPdfTable);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow | PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException ex) {
			LOGGER.error("Error while Generated Pdf" + ex.getMessage());
			return null;
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			return null;
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception ex) {
					LOGGER.error("Error while Generated Pdf" + ex.getMessage());
					return null;
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception ex) {
				LOGGER.error("Error while Generated Pdf" + ex.getMessage());
			}

		}
	}

	private boolean hasLundinTimesheetModule(long companyId) {
		boolean hasLundinTimesheetModule = false;
		Company companyVO = companyDAO.findById(companyId);
		if (companyVO != null) {
			Set<CompanyModuleMapping> companyModuleList = companyVO.getCompanyModuleMappings();
			for (CompanyModuleMapping companyModuleMapping : companyModuleList) {

				if (companyModuleMapping.getModuleMaster().getModuleName()
						.equals(PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET)) {
					hasLundinTimesheetModule = true;
				}

			}

		}
		return hasLundinTimesheetModule;
	}

	private void getColMap(DataDictionary dataDictionary, Map<String, DataImportKeyValueDTO> colMap) {
		Long formId = dataDictionary.getFormID();
		Tab tab = null;
		int maxVersion = dynamicFormDAO.getMaxVersionByFormId(dataDictionary.getCompany().getCompanyId(),
				dataDictionary.getEntityMaster().getEntityId(), dataDictionary.getFormID());

		DynamicForm dynamicForm = dynamicFormDAO.findByMaxVersionByFormId(dataDictionary.getCompany().getCompanyId(),
				dataDictionary.getEntityMaster().getEntityId(), maxVersion, dataDictionary.getFormID());

		tab = dataExportUtils.getTabObject(dynamicForm);
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		List<Field> listOfFields = tab.getField();
		for (Field field : listOfFields) {
			if (!StringUtils.equalsIgnoreCase(field.getType(), PayAsiaConstants.TABLE_FIELD_TYPE)) {
				if (field.getDictionaryId().equals(dataDictionary.getDataDictionaryId())) {
					dataImportKeyValueDTO.setStatic(false);
					dataImportKeyValueDTO.setChild(false);
					dataImportKeyValueDTO.setFieldType(field.getType());
					String colNumber = PayAsiaStringUtils.getColNumber(field.getName());
					dataImportKeyValueDTO.setMethodName(colNumber);
					dataImportKeyValueDTO.setFormId(formId);
					dataImportKeyValueDTO
							.setActualColName(new String(Base64.decodeBase64(field.getLabel().getBytes())));
					String tablePosition = PayAsiaStringUtils.getColNumber(field.getName());
					dataImportKeyValueDTO.setTablePosition(tablePosition);
					if (dataDictionary.getEntityMaster().getEntityName()
							.equalsIgnoreCase(PayAsiaConstants.EMPLOYEE_ENTITY_NAME)) {
						colMap.put(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId(),
								dataImportKeyValueDTO);
					}
				}
			}
		}
	}

	private void setStaticDictionary(Map<String, DataImportKeyValueDTO> colMap, String colKey,
			DataDictionary dataDictionary) {
		DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();
		try {
			ColumnPropertyDTO colProp = generalDAO.getColumnProperties(dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			dataImportKeyValueDTO.setMethodName(PayAsiaStringUtils.getCamelCase(dataDictionary.getColumnName()));
			dataImportKeyValueDTO.setStatic(true);
			dataImportKeyValueDTO.setActualColName(dataDictionary.getColumnName());
			colMap.put(colKey, dataImportKeyValueDTO);
		} catch (Exception exception) {
			LOGGER.error("Eror in setStaticDictonary method for mail" + exception.getMessage());

		}
	}

	private String getEmployeeDynamicFieldValue(DataDictionary dataDictionary, Long companyId, String dateFormat,
			Long employeeId, String employeeNumber) {
		List<Long> formIds = new ArrayList<Long>();
		List<Object[]> objectList = null;
		String originalValue = null;
		try {
			formIds.add(dataDictionary.getFormID());
			Map<String, DataImportKeyValueDTO> tableRecordInfoFrom = new HashMap<String, DataImportKeyValueDTO>();
			Map<String, DataImportKeyValueDTO> colMap = new LinkedHashMap<String, DataImportKeyValueDTO>();
			getColMap(dataDictionary, colMap);
			List<DataImportKeyValueDTO> tableElements = new ArrayList<DataImportKeyValueDTO>();
			List<ExcelExportFiltersForm> finalFilterList = new ArrayList<ExcelExportFiltersForm>();
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			DataDictionary empNumberDataDictionary = dataDictionaryDAO.findByCondition(null,
					PayAsiaConstants.EMPLOYEE_ENTITY_ID, null, PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
					PayAsiaConstants.PAYASIA_DATA_DICTIONARY_DATA_TYPE_FIELD);
			ColumnPropertyDTO colProp = generalDAO.getColumnProperties(dataDictionary.getTableName(),
					dataDictionary.getColumnName());
			dataImportKeyValueDTO.setActualColName(empNumberDataDictionary.getColumnName());
			dataImportKeyValueDTO.setFieldType(colProp.getColumnType());
			dataImportKeyValueDTO.setFieldValid(false);
			dataImportKeyValueDTO.setFormId(0);
			dataImportKeyValueDTO.setChild(false);
			dataImportKeyValueDTO.setCodeDescField(false);
			dataImportKeyValueDTO.setEmployeeEntity(false);
			dataImportKeyValueDTO.setFormula(false);
			dataImportKeyValueDTO.setStatic(true);
			dataImportKeyValueDTO
					.setMethodName(PayAsiaStringUtils.getCamelCase(empNumberDataDictionary.getColumnName()));
			dataImportKeyValueDTO.setNonTemplateVal(false);

			ExcelExportFiltersForm excelExportFiltersForm = new ExcelExportFiltersForm();
			excelExportFiltersForm.setCloseBracket(")");
			excelExportFiltersForm.setDataDictionaryName(empNumberDataDictionary.getDataDictName());
			excelExportFiltersForm.setEqualityOperator("=");
			excelExportFiltersForm.setValue(employeeNumber);
			excelExportFiltersForm.setOpenBracket("(");
			excelExportFiltersForm.setDictionaryId(empNumberDataDictionary.getDataDictionaryId());
			excelExportFiltersForm.setLogicalOperator("AND");
			excelExportFiltersForm.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			finalFilterList.add(excelExportFiltersForm);

			if (colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId())
					.getTablePosition() != null) {
				String tableKey;
				tableKey = colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId()).getFormId()
						+ colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId())
								.getTablePosition();
				tableRecordInfoFrom.put(tableKey,
						colMap.get(dataDictionary.getLabel() + dataDictionary.getDataDictionaryId()));
			}

			EmployeeShortListDTO employeeShortListDTO = new EmployeeShortListDTO();
			List<BigInteger> list = new ArrayList<BigInteger>();
			employeeShortListDTO.setEmployeeShortList(false);
			employeeShortListDTO.setShortListEmployeeIds(list);

			// Get Static Dictionary8
			DataDictionary dataDictionaryEmp = dataDictionaryDAO.findByDictionaryNameGroup(null, 1l, "Employee Number",
					null, null);
			setStaticDictionary(colMap, dataDictionaryEmp.getLabel() + dataDictionaryEmp.getDataDictionaryId(),
					dataDictionaryEmp);

			boolean showByEffectiveDateTableData = true;

			objectList = employeeDAO.findByCondition(colMap, formIds, companyId, finalFilterList, dateFormat,
					tableRecordInfoFrom, tableElements, employeeShortListDTO, showByEffectiveDateTableData);
			if (objectList != null) {
				for (Object[] extIdObj : objectList) {
					if (extIdObj != null) {
						originalValue = String.valueOf(extIdObj[0]);
					}
				}

				Iterator<Map.Entry<String, DataImportKeyValueDTO>> entries = colMap.entrySet().iterator();
				while (entries.hasNext()) {
					Entry<String, DataImportKeyValueDTO> thisEntry = entries.next();

					String key = thisEntry.getKey();
					DataImportKeyValueDTO valueDTO = colMap.get(key);
					if (valueDTO.getFieldType().equalsIgnoreCase(PayAsiaConstants.FIELD_TYPE_DATE)) {
						originalValue = DateUtils.convertDateToSpecificFormat(originalValue, dateFormat);
						break;
					}

				}
			}
		} catch (Exception exception) {
			LOGGER.error("Dynamic Field is null for mail" + exception.getMessage());
		}
		return originalValue;
	}

}
