package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.beanutils.BeanComparator;
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
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddLeaveConditionDTO;
import com.payasia.common.dto.ComboValueDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeDTO;
import com.payasia.common.dto.EmployeeLeaveApplicationReviewerDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationPdfDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveConditionDTO;
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.LeaveTypeForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeLeaveComparison;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeDefaultEmailCCDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeLeaveSchemeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.KeyPayIntLeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationAttachmentDAO;
import com.payasia.dao.LeaveApplicationCustomFieldDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationExtensionDetailsDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.LeaveApplicationWorkflowDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.LeaveSchemeTypeAvailingLeaveDAO;
import com.payasia.dao.LeaveSchemeTypeCustomFieldDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.LeaveSessionMasterDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeDefaultEmailCC;
import com.payasia.dao.bean.EmployeeLeaveReviewer;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory;
import com.payasia.dao.bean.IntegrationMaster;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveApplicationCustomField;
import com.payasia.dao.bean.LeaveApplicationExtensionDetails;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;
import com.payasia.dao.bean.LeaveSchemeTypeCustomField;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSessionMaster;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.NotificationAlert;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.LeaveApplicationPrintPDFLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class AddLeaveLogicImpl implements AddLeaveLogic {

	private static final Logger LOGGER = Logger.getLogger(AddLeaveLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	LeaveApplicationPrintPDFLogic leaveApplicationPrintPDFLogic;

	@Resource
	LeaveSchemeDAO leaveSchemeDAO;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@Resource
	LeaveStatusMasterDAO leaveStatusMasterDAO;

	@Resource
	LeaveApplicationDAO leaveApplicationDAO;

	@Resource
	LeaveApplicationWorkflowDAO leaveApplicationWorkflowDAO;

	@Resource
	LeaveApplicationAttachmentDAO leaveApplicationAttachmentDAO;

	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewerDAO;

	@Resource
	LeaveApplicationExtensionDetailsDAO leaveApplicationExtensionDetailsDAO;

	@Resource
	EmployeeLeaveSchemeDAO employeeLeaveSchemeDAO;

	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;

	@Resource
	LeaveSessionMasterDAO leaveSessionMasterDAO;

	@Resource
	LeaveSchemeTypeAvailingLeaveDAO leaveSchemeTypeAvailingLeaveDAO;

	@Resource
	LeaveSchemeTypeCustomFieldDAO leaveSchemeTypeCustomFieldDAO;

	@Resource
	LeaveApplicationCustomFieldDAO leaveApplicationCustomFieldDAO;

	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;

	@Resource
	GeneralMailLogic generalMailLogic;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	EmployeeLeaveSchemeTypeHistoryDAO employeeLeaveSchemeTypeHistoryDAO;

	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;

	@Resource
	ModuleMasterDAO moduleMasterDAO;

	@Resource
	NotificationAlertDAO notificationAlertDAO;
	@Resource
	EmployeeDefaultEmailCCDAO employeeDefaultEmailCCDAO;
	@Resource
	LeavePreferenceDAO leavePreferenceDAO;
	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	
	@Resource
	FileUtils fileUtils;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Resource
	AWSS3Logic awss3LogicImpl;
	
	@Resource
	KeyPayIntLeaveApplicationDAO keyPayIntLeaveApplicationDAO;


	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;
	
	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Override
	public LeaveSchemeForm getLeaveSchemes(Long companyId, Long employeeId) {

		LeaveSchemeForm leaveSchemeFormRes = new LeaveSchemeForm();
		Company companyVO = companyDAO.findById(companyId);
		EmployeeLeaveScheme empLeaveSchemeVO = employeeLeaveSchemeDAO.checkEmpLeaveSchemeByDate(null, employeeId,
				DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime()), companyVO.getDateFormat());
		List<ComboValueDTO> sessionList = leaveBalanceSummaryLogic.getLeaveSessionList();
		leaveSchemeFormRes.setSessionList(sessionList);
		if (empLeaveSchemeVO != null) {
			leaveSchemeFormRes.setEmployeeLeaveSchemeId(empLeaveSchemeVO.getEmployeeLeaveSchemeId());
			leaveSchemeFormRes.setLeaveSchemeId(empLeaveSchemeVO.getLeaveScheme().getLeaveSchemeId());
			leaveSchemeFormRes.setLeaveSchemeName(empLeaveSchemeVO.getLeaveScheme().getSchemeName());
			
		}
		return leaveSchemeFormRes;
	}

	@Override
	public AddLeaveForm getLeaveTypes(Long employeeLeaveSchemeId, Long companyId, Long employeeId) {

		AddLeaveForm addLeaveForm = new AddLeaveForm();
		EmployeeLeaveScheme employeeLeaveScheme = null;
		if (employeeLeaveSchemeId != null) {
			employeeLeaveScheme = employeeLeaveSchemeDAO.findById(employeeLeaveSchemeId);
		}

		LeavePreference leavePreference = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreference != null) {
			addLeaveForm.setPreApprovalReq(leavePreference.isPreApprovalRequired());
			addLeaveForm.setPreApprovalReqRemark(leavePreference.getPreApprovalReqRemark());
		}

		if (employeeLeaveScheme != null) {
			List<EmployeeLeaveReviewer> leaveReviewers = new ArrayList<EmployeeLeaveReviewer>(
					employeeLeaveScheme.getEmployeeLeaveReviewers());
			List<LeaveTypeForm> leaveTypeFormList = new ArrayList<LeaveTypeForm>();

			List<EmployeeLeaveSchemeType> employeeLeaveSchemeTypes = employeeLeaveSchemeTypeDAO
					.findByConditionEmpLeaveSchemeId(employeeLeaveSchemeId);

			for (EmployeeLeaveSchemeType employeeLeaveSchemeType : employeeLeaveSchemeTypes) {
				if (employeeLeaveSchemeType.getActive() == null) {
					continue;
				}
				if (employeeLeaveSchemeType.getActive() == false) {
					continue;
				}
				if (!employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().isFrontEndApplicationMode()) {
					continue;
				}
				if (!employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getVisibility()) {
					continue;
				}
				if (!employeeLeaveSchemeType.getLeaveSchemeType().getVisibility()) {
					continue;
				}
				LeaveTypeForm leaveTypeForm = new LeaveTypeForm();
				leaveTypeForm.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
				leaveTypeForm
						.setName(employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeName());
				leaveTypeForm
						.setTypeId(employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeId());

				Set<LeaveSchemeTypeAvailingLeave> leaveSchemeTypeAvailingLeaves = employeeLeaveSchemeType
						.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves();

				if (!leaveSchemeTypeAvailingLeaves.isEmpty()) {
					leaveTypeForm.setInstruction(leaveSchemeTypeAvailingLeaves.iterator().next().getRemarks());
				}

				leaveTypeFormList.add(leaveTypeForm); 
			}

			int totalNoOfReviewers = 0;
			for (EmployeeLeaveReviewer employeeLeaveReviewer : leaveReviewers) {
				if (employeeId == employeeLeaveReviewer.getEmployee1().getEmployeeId()) {
					totalNoOfReviewers++;
					if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
						addLeaveForm.setLeaveReviewer1(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
						addLeaveForm.setLeaveReviewer1Id(FormatPreserveCryptoUtil.encrypt(employeeLeaveReviewer.getEmployee2().getEmployeeId()));
						addLeaveForm.setApplyTo(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
						addLeaveForm.setApplyToEmail(employeeLeaveReviewer.getEmployee2().getEmail());
						addLeaveForm.setApplyToId(employeeLeaveReviewer.getEmployee2().getEmployeeId());

					} else if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

						addLeaveForm.setLeaveReviewer2(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
						addLeaveForm.setLeaveReviewer2Id(FormatPreserveCryptoUtil.encrypt(employeeLeaveReviewer.getEmployee2().getEmployeeId()));

					} else if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

						addLeaveForm.setLeaveReviewer3(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
						addLeaveForm.setLeaveReviewer3Id(FormatPreserveCryptoUtil.encrypt(employeeLeaveReviewer.getEmployee2().getEmployeeId()));
					}
				}

			}

			addLeaveForm.setTotalNoOfReviewers(totalNoOfReviewers);
			addLeaveForm.setLeaveTypeFormList(leaveTypeFormList);
		}
		return addLeaveForm;
	}

	@Override
	public AddLeaveForm addLeave(Long companyId, Long employeeId, AddLeaveForm addLeaveForm,
			LeaveSessionDTO sessionDTO) {
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		Employee reviewer = null;
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		leaveConditionDTO.setEmployeeId(employeeId);
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(addLeaveForm.getLeaveTypeId());
		leaveConditionDTO.setStartDate(addLeaveForm.getFromDate());
		leaveConditionDTO.setEndDate(addLeaveForm.getToDate());

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
		if (!isLeaveUnitDays) {
			addLeaveForm.setFromSessionId(1l);
			addLeaveForm.setToSessionId(2l);
			// Hours between Dates
			leaveConditionDTO.setTotalHoursBetweenDates(addLeaveForm.getNoOfDays().floatValue());
			leaveConditionDTO.setLeaveUnitHours(true);
		}
		leaveConditionDTO.setStartSession(addLeaveForm.getFromSessionId());
		leaveConditionDTO.setEndSession(addLeaveForm.getToSessionId());
		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setEmployeeLeaveApplicationId(null);
		int attachCount = 0;
		if (addLeaveForm.getAttachmentList() == null || addLeaveForm.getAttachmentList().size() < 0) {
			leaveConditionDTO.setAttachementStatus(false);

		} else {
			for (LeaveApplicationAttachmentDTO leaveApplicationAttachmentDTO : addLeaveForm.getAttachmentList()) {
				if (leaveApplicationAttachmentDTO.getAttachment() != null) {
					attachCount++;
				}
			}
			if (attachCount > 0) {
				leaveConditionDTO.setAttachementStatus(true);
			} else {
				leaveConditionDTO.setAttachementStatus(false);
			}
		}

		LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
		addLeaveFormRes.setLeaveDTO(leaveDTOValidate);
		if (leaveDTOValidate.getErrorCode() == 1) {
			return addLeaveFormRes;
		}

		Company company = companyDAO.findById(companyId);
		Employee employee = employeeDAO.findById(employeeId);

		EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO.checkEmpLeaveSchemeByDate(null, employeeId,
				DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime()), company.getDateFormat());
	
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
				.findById(addLeaveForm.getLeaveTypeId());

		leaveSchemeTypeDAO.findBySchemeType(employeeLeaveScheme.getLeaveScheme().getLeaveSchemeId(),
				employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeId());
		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setEmployee(employee);
		leaveApplication.setCompany(company);
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);

		leaveApplication.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
		leaveApplication.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));
		leaveApplication.setReason(addLeaveForm.getReason());
		boolean preApprovalRequest = false;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			preApprovalRequest = leavePreferenceVO.isPreApprovalRequired();
		}
		
		if (preApprovalRequest && addLeaveForm.getPreApprovalReq() != null) {
			leaveApplication.setPreApprovalRequest(addLeaveForm.getPreApprovalReq());
		} else {
			leaveApplication.setPreApprovalRequest(false);
		}

		if (getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId()) == null) {

			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.APPLYTO_REVIEWER_EMAIL_NOT_DEFINED);
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
			return addLeaveFormRes;

		}
		leaveApplication
				.setApplyTo(getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId()).getEmail());
		leaveApplication.setEmailCC(addLeaveForm.getEmailCC());

		if (addLeaveForm.getFromSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getFromSessionId());
			leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);

		}

		if (addLeaveForm.getToSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getToSessionId());
			leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
		}

		LeaveStatusMaster leaveStatusMaster = null;
		LeaveStatusMaster leaveApprovalNotReqStatus = null;

		if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_DRAFT);
		} else if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		}
		boolean approvalNotRequired = false;
		if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
				&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
					.getApprovalNotRequired() || (preApprovalRequest && addLeaveForm.getPreApprovalReq())) {
				approvalNotRequired = true;
				leaveApprovalNotReqStatus = leaveStatusMasterDAO
						.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
			}
		}

		if (approvalNotRequired) {
			leaveApplication.setLeaveStatusMaster(leaveApprovalNotReqStatus);
		} else {
			leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		}

		Date date = new Date();
		leaveApplication.setCreatedDate(new Timestamp(date.getTime()));
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = addLeaveForm.getNoOfDays();
		}

		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		leaveApplication.setTotalDays(Float.parseFloat(totalLeaveDays.toString()));
		Integer result = totalLeaveDays.compareTo(leaveBalance.getLeaveBalance());

		if (result == 1) {
			leaveApplication.setExcessDays(
					Float.parseFloat(totalLeaveDays.subtract(leaveBalance.getLeaveBalance()).toString()));

		}

		LeaveApplication persistLeaveApplication = leaveApplicationDAO.saveReturn(leaveApplication);

		List<LeaveCustomFieldDTO> customFieldDTOs = addLeaveForm.getCustomFieldDTOList();

		for (LeaveCustomFieldDTO leaveCustomFieldDTO : customFieldDTOs) {

			LeaveApplicationCustomField leaveApplicationCustomField = new LeaveApplicationCustomField();
			leaveApplicationCustomField.setLeaveApplication(persistLeaveApplication);
			leaveApplicationCustomField.setValue(leaveCustomFieldDTO.getValue());
			leaveApplicationCustomField.setLeaveSchemeTypeCustomField(
					leaveSchemeTypeCustomFieldDAO.findById(leaveCustomFieldDTO.getCustomFieldTypeId()));
			if (!leaveCustomFieldDTO.getValue().equals("") && leaveCustomFieldDTO.getValue() != null) {
				leaveApplicationCustomFieldDAO.save(leaveApplicationCustomField);
			}

		}

		if (addLeaveForm.getAttachmentList() != null) {
			FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
			for (LeaveApplicationAttachmentDTO applicationAttachmentDTO : addLeaveForm.getAttachmentList()) {
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() > 0) {
					String attachmentName = applicationAttachmentDTO.getAttachment().getOriginalFilename();
					LeaveApplicationAttachment leaveApplicationAttachment = new LeaveApplicationAttachment();
					leaveApplicationAttachment.setLeaveApplication(persistLeaveApplication);
					leaveApplicationAttachment.setFileName(attachmentName);
					leaveApplicationAttachment.setFileType(
							attachmentName.substring(attachmentName.lastIndexOf('.') + 1, attachmentName.length()));
					leaveApplicationAttachment.setUploadedDate(new Timestamp(date.getTime()));
					LeaveApplicationAttachment saveReturn = leaveApplicationAttachmentDAO
							.saveReturn(leaveApplicationAttachment);

					// save Leave attachment to file directory
					filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
							PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME, null, null, null, null,
							PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);

					String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
					/*
					 * String filePath = downloadPath + "/company/" + companyId
					 * + "/" + PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME
					 * + "/";
					 */

					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						String fileNameNew = applicationAttachmentDTO.getAttachment().getOriginalFilename();
						String ext = fileNameNew.substring(fileNameNew.lastIndexOf('.') + 1);

						if (!("").equals(fileNameNew)) {
							fileNameNew = saveReturn.getLeaveApplicationAttachmentId() + "." + ext;
						}
						awss3LogicImpl.uploadCommonMultipartFile(applicationAttachmentDTO.getAttachment(),
								filePath + fileNameNew);
					} else {
						FileUtils.uploadFile(applicationAttachmentDTO.getAttachment(), filePath, fileNameSeperator,
								saveReturn.getLeaveApplicationAttachmentId());
					}

				}
			}
		}
		String allowApprove = "";
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : employeeLeaveSchemeType.getLeaveSchemeType()
				.getLeaveSchemeTypeWorkflows()) {
			if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue();
			}
		}

		if (!addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {

			LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
			leaveApplicationWorkflow.setLeaveApplication(persistLeaveApplication);
			leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplicationWorkflow.setRemarks(addLeaveForm.getReason());
			leaveApplicationWorkflow.setForwardTo(addLeaveForm.getApplyTo());

			String emailCC = StringUtils.removeEnd(addLeaveForm.getEmailCC(), ";");
			if (approvalNotRequired) {
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
				EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(
						leaveApplication.getCompany().getCompanyId(), leaveApplication.getEmployee().getEmployeeId(),
						moduleMaster.getModuleId());
				if (employeeDefaultEmailCCVO != null) {
					if (StringUtils.isNotBlank(emailCC)) {
						emailCC += ";";
					}
					emailCC += employeeDefaultEmailCCVO.getEmailCC();
				}
			}
			leaveApplicationWorkflow.setEmailCC(emailCC);
			leaveApplicationWorkflow.setEmployee(employee);
			leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			leaveApplicationWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
			leaveApplicationWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

			leaveApplicationWorkflow.setTotalDays(persistLeaveApplication.getTotalDays());
			leaveApplicationWorkflow.setStartSessionMaster(persistLeaveApplication.getLeaveSessionMaster1());
			leaveApplicationWorkflow.setEndSessionMaster(persistLeaveApplication.getLeaveSessionMaster2());

			leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

			boolean leaveWorkflowNotRequired = false;
			if (leavePreferenceVO != null) {
				leaveWorkflowNotRequired = leavePreferenceVO.isLeaveWorkflowNotRequired();
			}

			if (approvalNotRequired) {
				LeaveApplicationWorkflow leaveApplWorkflow = new LeaveApplicationWorkflow();
				leaveApplWorkflow.setLeaveApplication(persistLeaveApplication);
				leaveApplWorkflow.setLeaveStatusMaster(leaveApprovalNotReqStatus);
				leaveApplWorkflow.setRemarks(addLeaveForm.getReason());
				leaveApplWorkflow.setForwardTo(addLeaveForm.getApplyTo());
				leaveApplWorkflow.setEmailCC(addLeaveForm.getEmailCC());
				leaveApplWorkflow.setEmployee(employee);
				leaveApplWorkflow.setCreatedDate(new Timestamp(date.getTime()));
				leaveApplWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
				leaveApplWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

				leaveApplWorkflow.setTotalDays(persistLeaveApplication.getTotalDays());
				leaveApplWorkflow.setStartSessionMaster(persistLeaveApplication.getLeaveSessionMaster1());
				leaveApplWorkflow.setEndSessionMaster(persistLeaveApplication.getLeaveSessionMaster2());

				leaveApplicationWorkflowDAO.save(leaveApplWorkflow);

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				reviewer = getDelegatedEmployeeForNotReqApproval(employeeId);
				leaveApplicationReviewer.setEmployee(reviewer);
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
				leaveApplicationReviewer.setPending(false);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);
			} else {
				if (addLeaveForm.getApplyToId() != null && addLeaveForm.getApplyToId() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
					reviewer = getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId());
					leaveApplicationReviewer.setEmployee(reviewer);
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
					leaveApplicationReviewer.setPending(true);
					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

				if (addLeaveForm.getLeaveReviewer2Id() != null && addLeaveForm.getLeaveReviewer2Id() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
					leaveApplicationReviewer.setEmployee(
							getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getLeaveReviewer2Id()));
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "2");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
					if (allowApprove.length() == 3 && allowApprove.substring(1, 2).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}

					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

				if (addLeaveForm.getLeaveReviewer3Id() != null && addLeaveForm.getLeaveReviewer3Id() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();

					leaveApplicationReviewer.setEmployee(
							getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getLeaveReviewer3Id()));
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "3");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
					if (allowApprove.length() == 3 && allowApprove.substring(2, 3).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}
					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

			}

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
					&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

				if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
						.getApprovalNotRequired() || (preApprovalRequest && addLeaveForm.getPreApprovalReq())) {
					leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

					saveEmployeeLeaveSchemeHistory(persistLeaveApplication);
				}

			}

			if (!approvalNotRequired) {
				generalMailLogic.sendEMailForLeave(companyId, persistLeaveApplication,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPLY, totalLeaveDays,
						leaveBalance.getLeaveBalance(), employee, reviewer, sessionDTO, isLeaveUnitDays);

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(reviewer);
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(persistLeaveApplication.getEmployee()) + " "
						+ PayAsiaConstants.LEAVEAPPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);

			} else if (approvalNotRequired && preApprovalRequest && addLeaveForm.getPreApprovalReq()) {

				List<EmployeeLeaveReviewer> employeeLeaveReviewerList = employeeLeaveReviewerDAO
						.findByLeaveApplicationId(persistLeaveApplication.getLeaveApplicationId());

				generalMailLogic.sendMailForPreApprovedLeave(
						leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_PRE_APPROVE_LEAVE_APPLY,
						totalLeaveDays, leaveBalance.getLeaveBalance(), employee, "", sessionDTO, isLeaveUnitDays,
						employee.getEmail());

				if (employeeLeaveReviewerList != null && !employeeLeaveReviewerList.isEmpty()) {
					for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewerList) {

						generalMailLogic.sendMailForPreApprovedLeave(
								leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany()
										.getCompanyId(),
								leaveApplicationWorkflow,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_PRE_APPROVE_LEAVE_APPLY_FOR_REVIEWER,
								totalLeaveDays, leaveBalance.getLeaveBalance(), employee, "", sessionDTO,
								isLeaveUnitDays, employeeLeaveReviewer.getEmployee2().getEmail());
					}
				}

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(reviewer);
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(persistLeaveApplication.getEmployee()) + " "
						+ PayAsiaConstants.PRE_APPROVAL_LEAVE_APPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);
				
				IntegrationMaster integrationMaster =	keyPayIntLeaveApplicationDAO.findByKeyPayDetailByCompanyId(companyId);
				if(integrationMaster!=null && !isLeaveUnitDays)
				{
					leaveBalanceSummaryLogic.addLeaveAppToKeyPayInt(persistLeaveApplication);
				}
			} else {
				generalMailLogic.sendAcceptRejectMailForLeave(
						leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPROVE, totalLeaveDays,
						leaveBalance.getLeaveBalance(), employee, "", sessionDTO, isLeaveUnitDays);
			}

		}

		return addLeaveFormRes;
	}

	private Employee getDelegatedEmployeeForNotReqApproval(Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterLeave = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_CATEGORY_LEAVE);

		WorkflowDelegate workflowDelegateLeave = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
				appCodeMasterLeave.getAppCodeID());

		if (workflowDelegateLeave != null) {
			workflowDelegate = workflowDelegateLeave;
		} else {

			AppCodeMaster appCodeMasterALL = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
					PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

			WorkflowDelegate workflowDelegateALL = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
					appCodeMasterALL.getAppCodeID());

			workflowDelegate = workflowDelegateALL;
		}

		if (workflowDelegate != null) {
			return workflowDelegate.getEmployee2();
		}
		return emp;
	}

	private Employee getDelegatedEmployee(Long leaveAppEmpId, Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterLeave = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_CATEGORY_LEAVE);

		WorkflowDelegate workflowDelegateLeave = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
				appCodeMasterLeave.getAppCodeID());

		if (workflowDelegateLeave != null) {
			workflowDelegate = workflowDelegateLeave;
		} else {

			AppCodeMaster appCodeMasterALL = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
					PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

			WorkflowDelegate workflowDelegateALL = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
					appCodeMasterALL.getAppCodeID());

			workflowDelegate = workflowDelegateALL;
		}

		if (workflowDelegate != null) {
			if (leaveAppEmpId.equals(workflowDelegate.getEmployee2().getEmployeeId())) {
				return emp;
			}
			return workflowDelegate.getEmployee2();
		}
		return emp;
	}

	private void saveEmployeeLeaveSchemeHistory(LeaveApplication leaveApplication) {

		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
		employeeLeaveSchemeTypeHistory.setEmployeeLeaveSchemeType(leaveApplication.getEmployeeLeaveSchemeType());
		AppCodeMaster appcodeTaken = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
				PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED);
		employeeLeaveSchemeTypeHistory.setAppCodeMaster(appcodeTaken);
		employeeLeaveSchemeTypeHistory.setLeaveApplication(leaveApplication);
		LeaveStatusMaster leaveStatusForHistory = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		if (leaveApplication.getLeaveCancelApplication() != null) {
			leaveStatusForHistory = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
		} else {
			leaveStatusForHistory = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}
		employeeLeaveSchemeTypeHistory.setLeaveStatusMaster(leaveStatusForHistory);
		employeeLeaveSchemeTypeHistory.setStartDate(leaveApplication.getStartDate());
		employeeLeaveSchemeTypeHistory.setEndDate(leaveApplication.getEndDate());
		employeeLeaveSchemeTypeHistory.setDays(BigDecimal.valueOf(leaveApplication.getTotalDays()));
		employeeLeaveSchemeTypeHistory.setReason(leaveApplication.getReason());
		employeeLeaveSchemeTypeHistory.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
		employeeLeaveSchemeTypeHistory.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());
		employeeLeaveSchemeTypeHistoryDAO.save(employeeLeaveSchemeTypeHistory);
	}

	@Override
	public AddLeaveFormResponse getPendingLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_DRAFT);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		//int recordSize = (leaveApplicationDAO.getCountForCondition(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByCondition(conditionDTO, null, null);
		
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		for (LeaveApplication leaveApplication : pendingLeaves) {
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			if (leaveApplication.getEmployeeLeaveSchemeType() == null) {
				continue;
			}
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			

			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
						addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
						addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
					} else {
						addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
					}
			addLeaveForm.setAction(true);
			addLeaveForm.setRequestType("draft");
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			addLeaveForm.setLeaveType(String.valueOf(leaveType));
			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getCreatedDate(), UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getStartDate(), UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setToDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate(),UserContext.getWorkingCompanyDateFormat()));
			//addLeaveForm.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());

			List<EmployeeLeaveReviewer> employeeLeaveReviewers = new ArrayList<>(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveReviewers());

			Collections.sort(employeeLeaveReviewers, new EmployeeReviewerComp());
			for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewers) {
				if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					addLeaveForm.setLeaveReviewer1(getEmployeeName(employeeLeaveReviewer.getEmployee2()));

				}
				if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					addLeaveForm.setLeaveReviewer2(getEmployeeName(employeeLeaveReviewer.getEmployee2()));

				}
				if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					addLeaveForm.setLeaveReviewer3(getEmployeeName(employeeLeaveReviewer.getEmployee2()));

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}
		
		//Collections.sort(addLeaveFormList, new LeaveCreationDateComp());

		if (sortDTO != null && !StringUtils.isEmpty(sortDTO.getColumnName()) && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList, new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}

	
	
	@Override
	public AddLeaveFormResponse getSubmittedLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3)) {
			if (StringUtils.isNotBlank(searchText)) {
				conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");
			}

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		conditionDTO.setEmployeeId(empId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		List<String> leaveStatusNames = new ArrayList<>();
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
		conditionDTO.setLeaveStatusNames(leaveStatusNames);

		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		//int recordSize = (leaveApplicationDAO.getCountForConditionSubmittedLeaveCancel(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionSubmittedLeaveCancel(conditionDTO,
				null, null);
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());
			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			//leaveType.append("<br>");
			/* ID ENCRYPT*/
			/*leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ");'>[View]</a></span>");*/
			

			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
						addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
						addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
					} else {
						addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
					}
			addLeaveForm.setAction(true);
			addLeaveForm.setRequestType("submitted");
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			
			addLeaveForm.setLeaveType(String.valueOf(leaveType));
			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getCreatedDate(), UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getStartDate(), UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setToDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate(), UserContext.getWorkingCompanyDateFormat()));
	
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			LeaveStatusMaster leaveStatusMasterApp = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO
							.findByConditionLeaveStatus(leaveApplication.getLeaveApplicationId(),
									leaveApplicationReviewer.getEmployee().getEmployeeId(),
									leaveStatusMasterApp.getLeaveStatusID());

					if (applicationWorkflow == null || leaveApplicationReviewer.getPending()) {

						addLeaveForm.setLeaveReviewer1(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
								pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

					}
				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO
							.findByConditionLeaveStatus(leaveApplication.getLeaveApplicationId(),
									leaveApplicationReviewer.getEmployee().getEmployeeId(),
									leaveStatusMasterApp.getLeaveStatusID());

					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {

						addLeaveForm.setLeaveReviewer2(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						if(applicationWorkflow != null){
							if(applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)){
								StringBuilder leaveReviewer2 = new StringBuilder(
										getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
												leaveApplicationReviewer.getEmployee()));

								leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+           DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

								addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

								reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;
								
							}
							
						}
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null || leaveApplicationReviewer.getPending()) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
							if(applicationWorkflow!=null)
							{
								if(applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
										.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)){
									StringBuilder leaveReviewer2 = new StringBuilder(
											getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
													leaveApplicationReviewer.getEmployee()));

									leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
											+           DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

									addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

									reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;
								}
							}
							
							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+           DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

							addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

						}

					}

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO
							.findByConditionLeaveStatus(leaveApplication.getLeaveApplicationId(),
									leaveApplicationReviewer.getEmployee().getEmployeeId(),
									leaveStatusMasterApp.getLeaveStatusID());

					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {

						addLeaveForm.setLeaveReviewer3(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null || leaveApplicationReviewer.getPending()) {
							addLeaveForm.setLeaveReviewer3(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						}

					}

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}

		//Collections.sort(addLeaveFormList, new LeaveCreationDateComp());
		
		if(sortDTO != null  && !StringUtils.isEmpty(sortDTO.getColumnName()) && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList, new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}

	@Override
	public AddLeaveFormResponse getCompletedLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		//int recordSize = (leaveApplicationDAO.getCountForConditionLeaveCancel(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionLeaveCancel(conditionDTO, null, null);
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			/*leaveType.append("<br>");
			 ID ENCRYPT
			leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ",\"approved\");'>[View]</a></span>");*/
			addLeaveForm.setAction(true);
			addLeaveForm.setRequestType("approved");
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
				addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
			} else {
				addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			}

			LeaveSchemeTypeAvailingLeave leaveSchemeTypeAvailingLeave = leaveSchemeTypeAvailingLeaveDAO
					.findByLeaveAppliationId(leaveApplication.getLeaveApplicationId());

			if (leaveSchemeTypeAvailingLeave != null && leaveSchemeTypeAvailingLeave.isLeaveExtension()) {
				/* ID ENCRYPT*/
				/*leaveType
						.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:leaveExtension("
								+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ","
								+ leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId()
								+ ");'>[Extend]</a></span>");*/
				//addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
				addLeaveForm.setIsExtend(true);
				addLeaveForm.setEmployeeLeaveSchemeId(FormatPreserveCryptoUtil
						.encrypt(leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId()));
			}
			addLeaveForm.setLeaveType(String.valueOf(leaveType));

			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getStartDate() ,UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setToDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate() ,UserContext.getWorkingCompanyDateFormat()));
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						StringBuilder leaveReviewer1 = new StringBuilder(
								getEmployeeName(leaveApplicationReviewer.getEmployee()));
						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));
					} else {
						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
								+ "</span>");
						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));
					}

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());

					if (applicationWorkflow == null) {
						StringBuilder leaveReviewer2 = new StringBuilder(
								getEmployeeName(leaveApplicationReviewer.getEmployee()));
						addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));
					} else {
						StringBuilder leaveReviewer2 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						if (applicationWorkflow.getCreatedDate() != null) {
							leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
									+ "</span>");
						}

						addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));
					}

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						StringBuilder leaveReviewer3 = new StringBuilder(
								getEmployeeName(leaveApplicationReviewer.getEmployee()));
						addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));
					} else {
						StringBuilder leaveReviewer3 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
								+ "</span>");

						addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));
					}

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}
        
		//Collections.sort(addLeaveFormList, new LeaveCreationDateComp());
		
		if (sortDTO != null  && !StringUtils.isEmpty(sortDTO.getColumnName()) && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList, new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}

	@Override
	public AddLeaveFormResponse getCompletedCancelLeaves(String fromDate, String toDate, Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath, String searchCondition,
			String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}
		conditionDTO.setLeaveType("cancel");

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		//int recordSize = (leaveApplicationDAO.getCountForConditionLeaveCancel(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionLeaveCancel(conditionDTO, null, null);
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			/*leaveType.append("<br>");
			 ID ENCRYPT
			leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ",\"approved\");'>[View]</a></span>");*/

			addLeaveForm.setAction(true);
			addLeaveForm.setRequestType("approvedCancel");
			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
				addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
			} else {
				addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			}
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			addLeaveForm.setLeaveType(String.valueOf(leaveType));

			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getStartDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setToDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate(),UserContext.getWorkingCompanyDateFormat()));
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					
					if(applicationWorkflow != null)
					{
						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));
						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));
				    }
					

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder leaveReviewer2 = new StringBuilder(
							getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
									leaveApplicationReviewer.getEmployee()));

					if (applicationWorkflow.getCreatedDate() != null) {
						leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
								+ "</span>");
					}

					addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (applicationWorkflow == null) {
						continue;
					}

					StringBuilder leaveReviewer3 = new StringBuilder(
							getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
									leaveApplicationReviewer.getEmployee()));

					leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
							+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

					addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}
		
//		Collections.sort(addLeaveFormList, new LeaveCreationDateComp());
		
		if (sortDTO != null  && !StringUtils.isEmpty(sortDTO.getColumnName()) && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList, new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}

	@Override
	public AddLeaveFormResponse getRejectedLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_REJECTED);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		//int recordSize = (leaveApplicationDAO.getCountForConditionRejected(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionForRejected(conditionDTO, null, null);
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			/*leaveType.append("<br>");
			 ID ENCRYPT
			leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ",\"rejected\");'>[View]</a></span>");*/
			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
				addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
			} else {
				addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			}
			addLeaveForm.setAction(true);
			addLeaveForm.setRequestType("rejected");
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			addLeaveForm.setLeaveType(String.valueOf(leaveType));

			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getStartDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setToDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate(),UserContext.getWorkingCompanyDateFormat()));
			
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());

					if (applicationWorkflow == null) {

						addLeaveForm.setLeaveReviewer1(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
								pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToStringWOTimezone(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToStringWOTimezone(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;

					}
				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());

					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)
							|| reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						addLeaveForm.setLeaveReviewer2(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						if(applicationWorkflow != null){
							if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
									.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

								StringBuilder leaveReviewer2 = new StringBuilder(
										getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
												leaveApplicationReviewer.getEmployee()));

								leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

								addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

								reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

							}
							else if(applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
									.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)){
								addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED,
										pageContextPath, leaveApplicationReviewer.getEmployee()));

								reviewer2Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;
									}
							
						}
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

							addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED,
									pageContextPath, leaveApplicationReviewer.getEmployee()));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;

						}

					}

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)
							|| reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						addLeaveForm.setLeaveReviewer3(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));
						if(applicationWorkflow != null){
							if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
									.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

								StringBuilder leaveReviewer3 = new StringBuilder(
										getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
												leaveApplicationReviewer.getEmployee()));

								leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

								addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

							}
							else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
									.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

								StringBuilder leaveReviewer3 = new StringBuilder(
										getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED, pageContextPath,
												leaveApplicationReviewer.getEmployee()));

								leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

								addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

							}
						}

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addLeaveForm.setLeaveReviewer3(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						}

					}

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}

//		Collections.sort(addLeaveFormList, new LeaveCreationDateComp());

		if (sortDTO != null  && !StringUtils.isEmpty(sortDTO.getColumnName()) && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList, new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}

	@Override
	public AddLeaveFormResponse getWithDrawnLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		//int recordSize = (leaveApplicationDAO.getCountForConditionWithdrawnCancel(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionWithDrawnCancel(conditionDTO, null, null);
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {

			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());
			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
				addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
			} else {
				addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			}

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			/*leaveType.append("<br>");
			 ID ENCRYPT
			leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ",\"rejected\");'>[View]</a></span>");*/
			addLeaveForm.setAction(true);
			addLeaveForm.setRequestType("withdrawn");
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			addLeaveForm.setLeaveType(String.valueOf(leaveType));

			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getStartDate(),UserContext.getWorkingCompanyDateFormat()));
			addLeaveForm.setToDate(DateUtils.timeStampToStringWOTimezone(leaveApplication.getEndDate(),UserContext.getWorkingCompanyDateFormat()));

			
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {

				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					addLeaveForm.setLeaveReviewer2(getEmployeeName(leaveApplicationReviewer.getEmployee()));

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					addLeaveForm.setLeaveReviewer3(getEmployeeName(leaveApplicationReviewer.getEmployee()));

				}
			}

			addLeaveFormList.add(addLeaveForm);
		}

//		Collections.sort(addLeaveFormList, new LeaveCreationDateComp());

		if (sortDTO != null  && !StringUtils.isEmpty(sortDTO.getColumnName()) && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList, new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}

	private String getStatusImage(String status, String contextPath, Employee employee) {
		/*String imagePath = contextPath + "/resources/images/icon/16/";
		if (status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {
			imagePath = imagePath + "pending.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
			imagePath = imagePath + "approved.png";
		} else if ("NA".equalsIgnoreCase(status)) {
			imagePath = imagePath + "pending-next-level.png";
		} else if (status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {
			imagePath = imagePath + "rejected.png";
		}
		String employeeName = getEmployeeName(employee);

		return "<img src='" + imagePath + "'  />  " + employeeName;*/
		String employeeName = getEmployeeName(employee);
		return  status+"##"+employeeName;

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	@SuppressWarnings("unused")
	private class LeaveTypeComp implements Comparator<EmployeeLeaveSchemeType> {
		public int compare(EmployeeLeaveSchemeType templateField, EmployeeLeaveSchemeType compWithTemplateField) {
			if (templateField.getEmployeeLeaveSchemeTypeId() > compWithTemplateField.getEmployeeLeaveSchemeTypeId()) {
				return 1;
			} else if (templateField.getEmployeeLeaveSchemeTypeId() < compWithTemplateField
					.getEmployeeLeaveSchemeTypeId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering LeaveApplicationWorkflow List
	 */
	private class LeaveReviewerComp implements Comparator<LeaveApplicationReviewer> {
		public int compare(LeaveApplicationReviewer templateField, LeaveApplicationReviewer compWithTemplateField) {
			if (templateField.getLeaveApplicationReviewerId() > compWithTemplateField.getLeaveApplicationReviewerId()) {
				return 1;
			} else if (templateField.getLeaveApplicationReviewerId() < compWithTemplateField
					.getLeaveApplicationReviewerId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator Class for Ordering LeaveApplicationWorkflow List
	 */
	private class EmployeeReviewerComp implements Comparator<EmployeeLeaveReviewer> {
		public int compare(EmployeeLeaveReviewer templateField, EmployeeLeaveReviewer compWithTemplateField) {
			if (templateField.getEmployeeLeaveReviewerID() > compWithTemplateField.getEmployeeLeaveReviewerID()) {
				return 1;
			} else if (templateField.getEmployeeLeaveReviewerID() < compWithTemplateField
					.getEmployeeLeaveReviewerID()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public AddLeaveForm getDataForPendingLeave(Long leaveApplicationId, Long employeeId, Long companyId) {

		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId,
				employeeId, companyId);
		AddLeaveForm addLeaveForm = null;
		if (leaveApplication == null) {
			addLeaveForm = new AddLeaveForm();
		} else {
			addLeaveForm = new AddLeaveForm();
			if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves()
					.size() > 0) {
				String remarks = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
						.getLeaveSchemeTypeAvailingLeaves().iterator().next().getRemarks();
				addLeaveForm.setRemarks(remarks);

			}

			LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
				addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
				addLeaveForm.setPreApprovalReqRemark(leavePreferenceVO.getPreApprovalReqRemark());
			} else {
				addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			}
			addLeaveForm.setPreApprovalReq(leaveApplication.getPreApprovalRequest());

			addLeaveForm
					.setNoOfDays(new BigDecimal(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			List<LeaveApplicationCustomField> leaveApplicationCustomFields = new ArrayList<>(
					leaveApplication.getLeaveApplicationCustomFields());
			Collections.sort(leaveApplicationCustomFields, new LeaveSchemeTypeCusFieldComp());
			List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = new ArrayList<>();
			for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationCustomFields) {

				LeaveCustomFieldDTO customField = new LeaveCustomFieldDTO();
				customField.setValue(leaveApplicationCustomField.getValue());
				customField.setCustomFieldId(leaveApplicationCustomField.getLeaveApplicationCustomFieldId());
				customField.setCustomFieldTypeId(
						leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getCustomFieldId());
				customField.setCustomFieldName(leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getFieldName());
				customField.setCustomFieldMandatory(leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getMandatory());
				leaveCustomFieldDTOs.add(customField);
			}
			addLeaveForm.setCustomFieldDTOList(leaveCustomFieldDTOs);

			addLeaveForm.setLeaveSchemeId(leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveScheme()
					.getLeaveScheme().getLeaveSchemeId()

			);
			addLeaveForm.setEmployeeLeaveSchemeId(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveScheme().getEmployeeLeaveSchemeId());
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveScheme()
					.getLeaveScheme().getSchemeName());
			addLeaveForm.setLeaveTypeId(leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
			addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));

			if (leaveApplication.getLeaveSessionMaster1() != null) {
				addLeaveForm.setFromSession(leaveApplication.getLeaveSessionMaster1().getSession());
				addLeaveForm.setFromSessionId(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			}

			if (leaveApplication.getLeaveSessionMaster2() != null) {
				addLeaveForm.setToSession(leaveApplication.getLeaveSessionMaster2().getSession());
				addLeaveForm.setToSessionId(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			}

			addLeaveForm.setReason(leaveApplication.getReason());
			addLeaveForm.setEmailCC(leaveApplication.getEmailCC());
			addLeaveForm.setTotalNoOfReviewers(leaveApplication.getLeaveApplicationReviewers().size());
			for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication.getLeaveApplicationReviewers()) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee().getEmail());
					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));
					addLeaveForm.setApplyToId(leaveApplicationReviewer.getEmployee().getEmployeeId());
					addLeaveForm.setApplyToEmail(leaveApplicationReviewer.getEmployee().getEmail());
				}

				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
					addLeaveForm.setLeaveReviewer2(getEmployeeName(leaveApplicationReviewer.getEmployee()));
					addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
				}

				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					addLeaveForm.setLeaveReviewer3(getEmployeeName(leaveApplicationReviewer.getEmployee()));
					addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
				}
			}

			List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

			for (LeaveApplicationAttachment leaveApplicationAttachment : leaveApplication
					.getLeaveApplicationAttachments()) {
				LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
				applicationAttachmentDTO.setFileName(leaveApplicationAttachment.getFileName());
				applicationAttachmentDTO
						.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplicationAttachment.getLeaveApplicationAttachmentId()));
				leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
			}

			addLeaveForm.setAttachmentList(leaveApplicationAttachmentDTOs);
		}
		return addLeaveForm;
	}

	@Override
	public AddLeaveForm viewLeave(Long leaveApplicationId) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(leaveApplicationId);

		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(leaveApplication.getCompany().getCompanyId());
		if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
			addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
			addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
		} else {
			addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
		}
		addLeaveForm.setPreApprovalReq(leaveApplication.getPreApprovalRequest());

		addLeaveForm.setNoOfDays(new BigDecimal(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
		Boolean isAdminPostedLeave = false;
		List<LeaveApplicationWorkflow> leaveApplicationWorkflows = new ArrayList<>(
				leaveApplication.getLeaveApplicationWorkflows());
		Collections.sort(leaveApplicationWorkflows, new LeaveApplicationWorkflowComp());
		if (leaveApplication.getEmployee().getEmployeeId() != leaveApplicationWorkflows.get(0).getEmployee()
				.getEmployeeId()) {
			isAdminPostedLeave = true;
		}

		Boolean isWithdrawn = false;
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplication.getLeaveApplicationWorkflows()) {
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
				isWithdrawn = true;
			}
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
				isWithdrawn = true;
			}
		}

		addLeaveForm.setIsWithdrawn(isWithdrawn);

		if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves()
				.size() > 0) {
			String remarks = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveSchemeTypeAvailingLeaves().iterator().next().getRemarks();
			addLeaveForm.setRemarks(remarks);
		}

		List<LeaveApplicationCustomField> leaveApplicationCustomFields = new ArrayList<>(
				leaveApplication.getLeaveApplicationCustomFields());
		Collections.sort(leaveApplicationCustomFields, new LeaveSchemeTypeCusFieldComp());
		List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = new ArrayList<>();
		for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationCustomFields) {

			LeaveCustomFieldDTO customField = new LeaveCustomFieldDTO();
			customField.setValue(leaveApplicationCustomField.getValue());
			customField.setCustomFieldId(leaveApplicationCustomField.getLeaveApplicationCustomFieldId());
			customField.setCustomFieldTypeId(
					leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getCustomFieldId());
			customField.setCustomFieldName(leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getFieldName());
			leaveCustomFieldDTOs.add(customField);
		}

		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication.getEmployeeLeaveSchemeType();
		addLeaveForm.setCustomFieldDTOList(leaveCustomFieldDTOs);
		addLeaveForm
				.setEmployeeLeaveSchemeId(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId());
		addLeaveForm.setLeaveSchemeId(
				leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getLeaveSchemeId());
		addLeaveForm.setLeaveScheme(
				leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getSchemeName());
		addLeaveForm.setLeaveTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
		addLeaveForm.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveTypeMaster().getLeaveTypeName());
		addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
		addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
		addLeaveForm.setReason(leaveApplication.getReason());
		addLeaveForm.setEmailCC(leaveApplication.getEmailCC());

		if (leaveApplication.getLeaveSessionMaster1() != null) {
			addLeaveForm.setFromSession(leaveApplication.getLeaveSessionMaster1().getSession());
			addLeaveForm.setFromSessionId(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
		}

		if (leaveApplication.getLeaveSessionMaster2() != null) {
			addLeaveForm.setToSession(leaveApplication.getLeaveSessionMaster2().getSession());
			addLeaveForm.setToSessionId(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
		}

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication.getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee().getEmail());
				if (isAdminPostedLeave) {
					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationWorkflows.get(0).getEmployee()));
				} else {
					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				}

				addLeaveForm.setApplyTo(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setApplyToEmail(leaveApplicationReviewer.getEmployee().getEmail());
				addLeaveForm.setApplyToId(leaveApplicationReviewer.getEmployee().getEmployeeId());

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
				addLeaveForm.setLeaveReviewer2(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
				addLeaveForm.setLeaveReviewer3(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
			}
		}

		addLeaveForm.setTotalNoOfReviewers(leaveApplication.getLeaveApplicationReviewers().size());
		if (isAdminPostedLeave) {
			addLeaveForm.setLeaveAppEmp(getEmployeeName(leaveApplicationWorkflows.get(0).getEmployee()));
		} else {
			addLeaveForm.setLeaveAppEmp(getEmployeeName(leaveApplication.getEmployee()));
		}
		addLeaveForm.setLeaveAppByEmp(getEmployeeName(leaveApplication.getEmployee()));
		addLeaveForm.setLeaveAppCreated(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
		addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
		addLeaveForm.setLeaveAppRemarks(leaveApplication.getReason());
		if (leaveApplication.getLeaveStatusMaster().getLeaveStatusName()
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
			addLeaveForm.setLeaveAppStatus("payasia.withdrawn");
		} else {
			addLeaveForm.setLeaveAppStatus("payasia.submitted");
		}

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<LeaveApplicationWorkflowDTO>();
		Integer workFlowCount = 0;

		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplicationWorkflows) {
			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();
			if (addLeaveForm.getApplyToId() != null
					&& addLeaveForm.getApplyToId() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null
					&& addLeaveForm.getLeaveReviewer2Id() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null
					&& addLeaveForm.getLeaveReviewer3Id() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			workFlowCount++;

			if (workFlowCount != 1 || isAdminPostedLeave) {
				applicationWorkflowDTO
						.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationWorkflow.getCreatedDate()));
				applicationWorkflowDTO.setCreatedDateM(leaveApplicationWorkflow.getCreatedDate());
				applicationWorkflowDTO.setEmployeeInfo(getEmployeeName(leaveApplicationWorkflow.getEmployee()));
				applicationWorkflowDTO.setUserRemarks(leaveApplicationWorkflow.getLeaveApplication().getReason());
				applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow.getRemarks());
				if (leaveApplication.getLeaveStatusMaster().getLeaveStatusName()
						.equals(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN));
				} else if (isAdminPostedLeave) {
					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(PayAsiaConstants.LEAVE_STATUS_COMPLETED));
				} else {

					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(
							leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()));
				}
			}

			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

		for (LeaveApplicationAttachment leaveApplicationAttachment : leaveApplication
				.getLeaveApplicationAttachments()) {
			LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			applicationAttachmentDTO.setFileName(leaveApplicationAttachment.getFileName());
			applicationAttachmentDTO
					.setLeaveApplicationId(leaveApplicationAttachment.getLeaveApplicationAttachmentId());
			leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
		}

		addLeaveForm.setAttachmentList(leaveApplicationAttachmentDTOs);
		return addLeaveForm;
	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class LeaveSchemeTypeCusFieldComp implements Comparator<LeaveApplicationCustomField> {
		public int compare(LeaveApplicationCustomField templateField,
				LeaveApplicationCustomField compWithTemplateField) {
			if (templateField.getLeaveSchemeTypeCustomField().getCustomFieldId() > compWithTemplateField
					.getLeaveSchemeTypeCustomField().getCustomFieldId()) {
				return 1;
			} else if (templateField.getLeaveSchemeTypeCustomField().getCustomFieldId() < compWithTemplateField
					.getLeaveSchemeTypeCustomField().getCustomFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	public String setStatusMultilingualKey(String leaveStatusName) {
		if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
			return "payasia.approved";
		} else if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			return "payasia.submitted";
		} else if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
			return "payasia.withdrawn";
		} else if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {
			return "payasia.rejected";
		} else if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_CANCELLED)) {
			return "payasia.cancelled";
		} else if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
			return "payasia.completed";
		} else if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			return "payasia.draft";
		} else if (leaveStatusName.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {
			return "payasia.pending";
		}
		return leaveStatusName;

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class LeaveApplicationWorkflowComp implements Comparator<LeaveApplicationWorkflow> {
		public int compare(LeaveApplicationWorkflow templateField, LeaveApplicationWorkflow compWithTemplateField) {
			if (templateField.getLeaveApplicationWorkflowID() > compWithTemplateField.getLeaveApplicationWorkflowID()) {
				return 1;
			} else if (templateField.getLeaveApplicationWorkflowID() < compWithTemplateField
					.getLeaveApplicationWorkflowID()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public LeaveApplicationAttachmentDTO viewAttachment(long attachmentId) {

		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		LeaveApplicationAttachment attachment = leaveApplicationAttachmentDAO.findById(attachmentId);
		LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
		// applicationAttachmentDTO.setAttachmentBytes(attachment.getAttachment());
		applicationAttachmentDTO.setFileName(attachment.getFileName());
		applicationAttachmentDTO.setFileType(attachment.getFileType());

		String fileExt = attachment.getFileType();

		filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				attachment.getLeaveApplication().getCompany().getCompanyId(),
				PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
				String.valueOf(attachment.getLeaveApplicationAttachmentId()), null, null, fileExt,
				PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		/*
		 * String filePath = "/company/" +
		 * attachment.getLeaveApplication().getCompany().getCompanyId() + "/" +
		 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
		 * attachment.getLeaveApplicationAttachmentId() + "." + fileExt;
		 */
		applicationAttachmentDTO.setAttachmentPath(filePath);

		File file = new File(filePath);
		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				byte[] byteFile = org.apache.commons.io.IOUtils
						.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
				applicationAttachmentDTO.setAttachmentBytes(byteFile);
			} else {
				applicationAttachmentDTO.setAttachmentBytes(Files.readAllBytes(file.toPath()));
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return applicationAttachmentDTO;
	}

	@Override
	public void deleteAttachment(long attachmentId) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		LeaveApplicationAttachment applicationAttachment = leaveApplicationAttachmentDAO.findById(attachmentId);
		boolean success = true;
		try {
			String fileExt = applicationAttachment.getFileType();

			filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					applicationAttachment.getLeaveApplication().getCompany().getCompanyId(),
					PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
					String.valueOf(applicationAttachment.getLeaveApplicationAttachmentId()), null, null, fileExt,
					PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);

			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * String filePath = "/company/" +
			 * applicationAttachment.getLeaveApplication().getCompany()
			 * .getCompanyId() + "/" +
			 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
			 * applicationAttachment.getLeaveApplicationAttachmentId() + "." +
			 * fileExt;
			 */

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
			} else {
				FileUtils.deletefile(filePath);
			}

		} catch (Exception exception) {
			success = false;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		if (success == true) {
			// delete attachment
			leaveApplicationAttachmentDAO.delete(applicationAttachment);
		}
	}

	@Override
	public AddLeaveForm editLeave(Long companyId, Long employeeId, AddLeaveForm addLeaveForm,
			LeaveSessionDTO sessionDTO) {

		Employee loggenInEmp = employeeDAO.findById(employeeId);
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		/* ID DECRYPT */
		addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(addLeaveForm.getLeaveApplicationId()));
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(addLeaveForm.getLeaveApplicationId());
		Employee reviewer = null;
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		leaveConditionDTO.setEmployeeId(employeeId);
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(addLeaveForm.getLeaveTypeId());
		leaveConditionDTO.setStartDate(addLeaveForm.getFromDate());
		leaveConditionDTO.setEndDate(addLeaveForm.getToDate());

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
		if (!isLeaveUnitDays) {
			addLeaveForm.setFromSessionId(1l);
			addLeaveForm.setToSessionId(2l);
			// Hours between Dates
			leaveConditionDTO.setTotalHoursBetweenDates(addLeaveForm.getNoOfDays().floatValue());
			leaveConditionDTO.setLeaveUnitHours(true);
		}
		leaveConditionDTO.setStartSession(addLeaveForm.getFromSessionId());
		leaveConditionDTO.setEndSession(addLeaveForm.getToSessionId());
		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setEmployeeLeaveApplicationId(leaveApplication.getLeaveApplicationId());
		int attachCount = 0;

		if (leaveApplication.getLeaveApplicationAttachments().size() > 0) {
			leaveConditionDTO.setAttachementStatus(true);
		} else {

			if (addLeaveForm.getAttachmentList() == null || addLeaveForm.getAttachmentList().size() < 0) {
				leaveConditionDTO.setAttachementStatus(false);

			} else {
				for (LeaveApplicationAttachmentDTO leaveApplicationAttachmentDTO : addLeaveForm.getAttachmentList()) {
					if (leaveApplicationAttachmentDTO.getAttachment() != null) {
						attachCount++;
					}
				}
				if (attachCount > 0) {
					leaveConditionDTO.setAttachementStatus(true);
				} else {
					leaveConditionDTO.setAttachementStatus(false);
				}
			}

		}

		LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
		addLeaveFormRes.setLeaveDTO(leaveDTOValidate);
		if (leaveDTOValidate.getErrorCode() == 1) {
			return addLeaveFormRes;
		}
		Company company = companyDAO.findById(companyId);
		Employee employee = employeeDAO.findById(employeeId);

		leaveApplication.setEmailCC(addLeaveForm.getEmailCC());
		leaveApplication.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));
		boolean preApprovalRequest = false;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			preApprovalRequest = leavePreferenceVO.isPreApprovalRequired();
		}
		if (preApprovalRequest && addLeaveForm.getPreApprovalReq() != null) {
			leaveApplication.setPreApprovalRequest(addLeaveForm.getPreApprovalReq());
		} else {
			leaveApplication.setPreApprovalRequest(false);
		}

		List<LeaveCustomFieldDTO> customFieldDTOs = addLeaveForm.getCustomFieldDTOList();

		for (LeaveCustomFieldDTO leaveCustomFieldDTO : customFieldDTOs) {

			LeaveApplicationCustomField leaveApplicationCustomField = new LeaveApplicationCustomField();
			if (leaveCustomFieldDTO.getCustomFieldId() == null || leaveCustomFieldDTO.getCustomFieldId() == 0) {
				leaveApplicationCustomField.setLeaveApplication(leaveApplication);
				leaveApplicationCustomField.setValue(leaveCustomFieldDTO.getValue());
				leaveApplicationCustomField.setLeaveSchemeTypeCustomField(
						leaveSchemeTypeCustomFieldDAO.findById(leaveCustomFieldDTO.getCustomFieldTypeId()));
				if (!leaveCustomFieldDTO.getValue().equals("") && leaveCustomFieldDTO.getValue() != null) {
					leaveApplicationCustomFieldDAO.save(leaveApplicationCustomField);
				}

			} else {

				LeaveApplicationCustomField leaveApplicationCustomFieldVO = leaveApplicationCustomFieldDAO
						.findById(leaveCustomFieldDTO.getCustomFieldId());
				leaveApplicationCustomFieldVO.setValue(leaveCustomFieldDTO.getValue());
				leaveApplicationCustomFieldDAO.update(leaveApplicationCustomFieldVO);
			}

		}

		EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO.checkEmpLeaveSchemeByDate(null, employeeId,
				DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime()), company.getDateFormat());

		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
				.findById(addLeaveForm.getLeaveTypeId());

		leaveSchemeTypeDAO.findBySchemeType(employeeLeaveScheme.getLeaveScheme().getLeaveSchemeId(),
				employeeLeaveSchemeType.getLeaveSchemeType().getLeaveTypeMaster().getLeaveTypeId());

		leaveApplication.setReason(addLeaveForm.getReason());
		leaveApplication.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));

		if (addLeaveForm.getFromSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getFromSessionId());
			leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);

		}

		if (addLeaveForm.getToSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getToSessionId());
			leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
		}

		Date date = new Date();
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);

		LeaveStatusMaster leaveStatusMaster = null;
		LeaveStatusMaster leaveApprovalNotReqStatus = null;
		if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_DRAFT);
		} else if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		}

		boolean approvalNotRequired = false;
		if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
				&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
					.getApprovalNotRequired() || (preApprovalRequest && addLeaveForm.getPreApprovalReq())) {
				approvalNotRequired = true;
				leaveApprovalNotReqStatus = leaveStatusMasterDAO
						.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
			}
		}

		if (approvalNotRequired) {
			leaveApplication.setLeaveStatusMaster(leaveApprovalNotReqStatus);
		} else {
			leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		}

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between Dates
			totalLeaveDays = addLeaveForm.getNoOfDays();
		}
		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		Integer result = totalLeaveDays.compareTo(leaveBalance.getLeaveBalance());

		if (result == 1) {
			leaveApplication.setExcessDays(
					Float.parseFloat(totalLeaveDays.subtract(leaveBalance.getLeaveBalance()).toString()));

		}
		leaveApplication.setTotalDays(Float.parseFloat(totalLeaveDays.toString()));

		leaveApplicationDAO.update(leaveApplication);
		if (addLeaveForm.getAttachmentList() != null) {
			FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
			for (LeaveApplicationAttachmentDTO applicationAttachmentDTO : addLeaveForm.getAttachmentList()) {
				if (applicationAttachmentDTO.getAttachment() != null) {
					String attachmentName = applicationAttachmentDTO.getAttachment().getOriginalFilename();
					LeaveApplicationAttachment leaveApplicationAttachment = new LeaveApplicationAttachment();
					leaveApplicationAttachment.setLeaveApplication(leaveApplication);
					leaveApplicationAttachment.setFileName(attachmentName);
					leaveApplicationAttachment.setFileType(
							attachmentName.substring(attachmentName.lastIndexOf('.') + 1, attachmentName.length()));
					leaveApplicationAttachment.setUploadedDate(new Timestamp(date.getTime()));
					LeaveApplicationAttachment saveReturn = leaveApplicationAttachmentDAO
							.saveReturn(leaveApplicationAttachment);

					// save Leave attachment to file directory
					filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
							PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME, null, null, null, null,
							PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);

					String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
					/*
					 * String filePath = downloadPath + "/company/" + companyId
					 * + "/" + PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME
					 * + "/";
					 */
					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						String fileNameNew = applicationAttachmentDTO.getAttachment().getOriginalFilename();
						String ext = fileNameNew.substring(fileNameNew.lastIndexOf('.') + 1);

						if (!("").equals(fileNameNew)) {
							fileNameNew = saveReturn.getLeaveApplicationAttachmentId() + "." + ext;
						}
						awss3LogicImpl.uploadCommonMultipartFile(applicationAttachmentDTO.getAttachment(),
								filePath + fileNameNew);
					} else {
						FileUtils.uploadFile(applicationAttachmentDTO.getAttachment(), filePath, fileNameSeperator,
								saveReturn.getLeaveApplicationAttachmentId());
					}

				}
			}
		}
		String allowApprove = "";
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : employeeLeaveSchemeType.getLeaveSchemeType()
				.getLeaveSchemeTypeWorkflows()) {
			if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue();
			}
		}
		if (!addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
			leaveApplicationWorkflow.setLeaveApplication(leaveApplication);
			leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplicationWorkflow.setRemarks(addLeaveForm.getReason());
			leaveApplicationWorkflow.setForwardTo(addLeaveForm.getApplyTo());
			leaveApplicationWorkflow.setEmailCC(addLeaveForm.getEmailCC());
			leaveApplicationWorkflow.setEmployee(leaveApplication.getEmployee());
			leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			leaveApplicationWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
			leaveApplicationWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

			leaveApplicationWorkflow.setTotalDays(leaveApplication.getTotalDays());

			leaveApplicationWorkflow.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
			leaveApplicationWorkflow.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());

			leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

			boolean leaveWorkflowNotRequired = false;
			if (leavePreferenceVO != null) {
				leaveWorkflowNotRequired = leavePreferenceVO.isLeaveWorkflowNotRequired();
			}

			if (approvalNotRequired) {
				LeaveApplicationWorkflow leaveApplWorkflow = new LeaveApplicationWorkflow();
				leaveApplWorkflow.setLeaveApplication(leaveApplication);
				leaveApplWorkflow.setLeaveStatusMaster(leaveApprovalNotReqStatus);
				leaveApplWorkflow.setRemarks(addLeaveForm.getReason());
				leaveApplWorkflow.setForwardTo(addLeaveForm.getApplyTo());
				leaveApplWorkflow.setEmailCC(addLeaveForm.getEmailCC());
				leaveApplWorkflow.setEmployee(employee);
				leaveApplWorkflow.setCreatedDate(new Timestamp(date.getTime()));
				leaveApplWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
				leaveApplWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

				leaveApplWorkflow.setTotalDays(leaveApplication.getTotalDays());
				leaveApplWorkflow.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
				leaveApplWorkflow.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());

				leaveApplicationWorkflowDAO.save(leaveApplWorkflow);

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				reviewer = getDelegatedEmployeeForNotReqApproval(employeeId);
				leaveApplicationReviewer.setEmployee(reviewer);
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(leaveApplication);
				leaveApplicationReviewer.setPending(false);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);
			} else {
				if (addLeaveForm.getApplyToId() != null && addLeaveForm.getApplyToId() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();

					reviewer = getDelegatedEmployee(leaveApplication.getEmployee().getEmployeeId(),
							addLeaveForm.getApplyToId());
					leaveApplicationReviewer.setEmployee(reviewer);
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(leaveApplication);
					leaveApplicationReviewer.setPending(true);
					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

				if (addLeaveForm.getLeaveReviewer2Id() != null && addLeaveForm.getLeaveReviewer2Id() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
					leaveApplicationReviewer.setEmployee(getDelegatedEmployee(
							leaveApplication.getEmployee().getEmployeeId(), addLeaveForm.getLeaveReviewer2Id()));
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "2");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(leaveApplication);
					if (allowApprove.length() == 3 && allowApprove.substring(1, 2).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}

					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

				if (addLeaveForm.getLeaveReviewer3Id() != null && addLeaveForm.getLeaveReviewer3Id() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
					leaveApplicationReviewer.setEmployee(getDelegatedEmployee(
							leaveApplication.getEmployee().getEmployeeId(), addLeaveForm.getLeaveReviewer3Id()));
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "3");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(leaveApplication);
					if (allowApprove.length() == 3 && allowApprove.substring(2, 3).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}
					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

			}

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
					&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

				if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
						.getApprovalNotRequired() || (preApprovalRequest && addLeaveForm.getPreApprovalReq())) {
					leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

					saveEmployeeLeaveSchemeHistory(leaveApplication);
				}

			}

			if (!approvalNotRequired) {
				generalMailLogic.sendEMailForLeave(companyId, leaveApplication,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPLY, totalLeaveDays,
						leaveBalance.getLeaveBalance(), loggenInEmp, reviewer, sessionDTO, isLeaveUnitDays);

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(reviewer);
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(leaveApplication.getEmployee()) + " "
						+ PayAsiaConstants.LEAVEAPPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);

			} else if (approvalNotRequired && preApprovalRequest && addLeaveForm.getPreApprovalReq()) {

				List<EmployeeLeaveReviewer> employeeLeaveReviewerList = employeeLeaveReviewerDAO
						.findByLeaveApplicationId(leaveApplication.getLeaveApplicationId());

				generalMailLogic.sendMailForPreApprovedLeave(
						leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_PRE_APPROVE_LEAVE_APPLY,
						totalLeaveDays, leaveBalance.getLeaveBalance(), employee, "", sessionDTO, isLeaveUnitDays,
						employee.getEmail());

				if (employeeLeaveReviewerList != null && !employeeLeaveReviewerList.isEmpty()) {
					for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewerList) {

						generalMailLogic.sendMailForPreApprovedLeave(
								leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany()
										.getCompanyId(),
								leaveApplicationWorkflow,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_PRE_APPROVE_LEAVE_APPLY_FOR_REVIEWER,
								totalLeaveDays, leaveBalance.getLeaveBalance(), employee, "", sessionDTO,
								isLeaveUnitDays, employeeLeaveReviewer.getEmployee2().getEmail());
					}
				}

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(reviewer);
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(leaveApplication.getEmployee()) + " "
						+ PayAsiaConstants.PRE_APPROVAL_LEAVE_APPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);

			} else {
				generalMailLogic.sendAcceptRejectMailForLeave(
						leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPROVE, totalLeaveDays,
						leaveBalance.getLeaveBalance(), employee, "", sessionDTO, isLeaveUnitDays);
			}
		}

		return addLeaveFormRes;
	}

	@Override
	public AddLeaveForm extensionLeave(Long companyId, Long employeeId, AddLeaveForm addLeaveForm,
			LeaveSessionDTO sessionDTO) {

		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(addLeaveForm.getLeaveApplicationId());
		if(leaveApplication == null){
			return null;
		}
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		leaveConditionDTO.setAttachementStatus(true);
		leaveConditionDTO.setEmployeeId(leaveApplication.getEmployee().getEmployeeId());
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(addLeaveForm.getLeaveTypeId());

		Date newStartDate = null;
		if (leaveApplication.getLeaveSessionMaster2().getLeaveSessionId() == 1) {
			leaveConditionDTO.setStartDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveConditionDTO.setStartSession(2L);

		} else if (leaveApplication.getLeaveSessionMaster2().getLeaveSessionId() == 2) {
			newStartDate = DateUtils.addDays(leaveApplication.getEndDate(), 1);
			String newStartDateStr = DateUtils.timeStampToString(DateUtils.convertDateToTimeStamp(newStartDate));
			leaveConditionDTO.setStartDate(newStartDateStr);
			leaveConditionDTO.setStartSession(1L);
		}

		leaveConditionDTO.setEndDate(addLeaveForm.getToDate());
		leaveConditionDTO.setEndSession(addLeaveForm.getToSessionId());

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);

		if (!isLeaveUnitDays) {
			addLeaveForm.setFromSessionId(1l);
			addLeaveForm.setToSessionId(2l);
			// Hours between Dates
			leaveConditionDTO.setTotalHoursBetweenDates(addLeaveForm.getNoOfDays().floatValue());
			leaveConditionDTO.setLeaveUnitHours(true);
		}

		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setEmployeeLeaveApplicationId(leaveApplication.getLeaveApplicationId());

		LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
		addLeaveFormRes.setLeaveDTO(leaveDTOValidate);
		if (leaveDTOValidate.getErrorCode() == 1) {
			return addLeaveFormRes;
		}
		Employee employee = employeeDAO.findById(leaveApplication.getEmployee().getEmployeeId());

		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
				.findById(addLeaveForm.getLeaveTypeId());

		Set<LeaveApplicationExtensionDetails> extensionDetailsList = new HashSet<LeaveApplicationExtensionDetails>();
		if (!leaveApplication.getLeaveExtension()) {

			LeaveApplicationExtensionDetails extensionDetails = new LeaveApplicationExtensionDetails();
			extensionDetails.setFromDate(leaveApplication.getStartDate());
			extensionDetails.setToDate(leaveApplication.getEndDate());
			extensionDetails.setLeaveSessionMaster1(leaveApplication.getLeaveSessionMaster1());
			extensionDetails.setLeaveSessionMaster2(leaveApplication.getLeaveSessionMaster2());
			extensionDetails.setRemarks(leaveApplication.getReason());
			extensionDetails.setLeaveApplication(leaveApplication);
			leaveApplicationExtensionDetailsDAO.save(extensionDetails);
			extensionDetailsList.add(extensionDetails);
		}

		LeaveApplicationExtensionDetails extensionDetails = new LeaveApplicationExtensionDetails();

		extensionDetails.setFromDate(DateUtils.stringToTimestamp(leaveConditionDTO.getStartDate()));
		extensionDetails.setLeaveSessionMaster1(leaveSessionMasterDAO.findById(leaveConditionDTO.getStartSession()));

		extensionDetails.setToDate(DateUtils.stringToTimestamp(leaveConditionDTO.getEndDate()));
		extensionDetails.setLeaveSessionMaster2(leaveSessionMasterDAO.findById(leaveConditionDTO.getEndSession()));

		extensionDetails.setRemarks(addLeaveForm.getReason());
		extensionDetails.setLeaveApplication(leaveApplication);
		leaveApplicationExtensionDetailsDAO.save(extensionDetails);
		extensionDetailsList.add(extensionDetails);

		BigDecimal totalDaysAfterAdd = BigDecimal.valueOf(leaveApplication.getTotalDays())
				.add(addLeaveForm.getNoOfDays());

		leaveApplication.setTotalDays(totalDaysAfterAdd.floatValue());
		leaveApplication.setLeaveApplicationExtensionDetails(extensionDetailsList);

		leaveApplication.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

		if (addLeaveForm.getToSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getToSessionId());
			leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
		}

		Date date = new Date();
		leaveApplication.setLeaveExtension(true);
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between Dates
			totalLeaveDays = addLeaveForm.getNoOfDays();
		}
		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		Integer result = totalLeaveDays.compareTo(leaveBalance.getLeaveBalance());

		if (result == 1) {
			leaveApplication.setExcessDays(
					Float.parseFloat(totalLeaveDays.subtract(leaveBalance.getLeaveBalance()).toString()));

		}
		leaveApplication.setTotalDays(Float.parseFloat(totalLeaveDays.toString()));

		leaveApplicationDAO.update(leaveApplication);

		Set<EmployeeLeaveSchemeTypeHistory> employeeLeaveSchemeTypeHistory = leaveApplication
				.getEmployeeLeaveSchemeTypeHistories();
		for (EmployeeLeaveSchemeTypeHistory empleaveSchemeTypeHistory : employeeLeaveSchemeTypeHistory) {

			empleaveSchemeTypeHistory.setDays(totalDaysAfterAdd);
			empleaveSchemeTypeHistory.setEndDate(leaveApplication.getEndDate());
			empleaveSchemeTypeHistory.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());
			employeeLeaveSchemeTypeHistoryDAO.update(empleaveSchemeTypeHistory);
		}

		List<EmployeeLeaveReviewer> employeeLeaveReviewerList = employeeLeaveReviewerDAO
				.findByLeaveApplicationId(leaveApplication.getLeaveApplicationId());

		if (employeeLeaveReviewerList != null && !employeeLeaveReviewerList.isEmpty()) {
			for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewerList) {

				generalMailLogic.sendMailForLeaveExtension(leaveApplication.getCompany().getCompanyId(),
						leaveApplication, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_EXTENSION, totalLeaveDays,
						leaveBalance.getLeaveBalance(), employee, "", sessionDTO, isLeaveUnitDays,
						employeeLeaveReviewer.getEmployee2().getEmail());
			}
		}

		return addLeaveFormRes;
	}

	@Override
	public void deleteLeave(long leaveApplicationId) {
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(leaveApplicationId);
		Set<LeaveApplicationAttachment> applicationAttachmentSet = leaveApplication.getLeaveApplicationAttachments();
		for (LeaveApplicationAttachment applicationAttachment : applicationAttachmentSet) {
			deleteAttachment(applicationAttachment.getLeaveApplicationAttachmentId());
		}

		leaveApplicationDAO.delete(leaveApplication);

	}

	@Override
	public String withdrawLeave(Long leaveApplicationId, Long empId, LeaveSessionDTO sessionDTO) {
		Employee loggedInEmployee = employeeDAO.findById(empId);
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(leaveApplicationId);
		List<LeaveApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				leaveApplication.getLeaveApplicationWorkflows());

		for (LeaveApplicationWorkflow leaveApplicationWorkflow : applicationWorkflows) {
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
				return "ERROR";
			}
		}

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplication.getCompany().getCompanyId());
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN);
		Date date = new Date();
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplicationDAO.update(leaveApplication);

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication.getLeaveApplicationReviewers()) {
			leaveApplicationReviewer.setPending(false);
			leaveApplicationReviewerDAO.update(leaveApplicationReviewer);
		}

		LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
		leaveApplicationWorkflow.setLeaveApplication(leaveApplication);
		leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplicationWorkflow.setRemarks(leaveApplication.getReason());
		leaveApplicationWorkflow.setForwardTo(leaveApplication.getApplyTo());
		leaveApplicationWorkflow.setEmailCC(leaveApplication.getEmailCC());
		leaveApplicationWorkflow.setEmployee(leaveApplication.getEmployee());
		leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		leaveApplicationWorkflow.setStartDate(leaveApplication.getStartDate());
		leaveApplicationWorkflow.setEndDate(leaveApplication.getEndDate());
		leaveApplicationWorkflow.setTotalDays(leaveApplication.getTotalDays());
		leaveApplicationWorkflow.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
		leaveApplicationWorkflow.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());

		leaveApplicationWorkflow = leaveApplicationWorkflowDAO.saveReturn(leaveApplicationWorkflow);

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		generalMailLogic.sendWithdrawEmailForLeave(
				leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
				leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_WITHDRAW, totalLeaveDays,
				leaveBalance.getLeaveBalance(), loggedInEmployee, sessionDTO, isLeaveUnitDays);

		return "SUCCESS";
	}

	@Override
	public String extensionLeaveView(Long leaveApplicationId, Long empId, Long companyId) {

		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId,
				empId, companyId);
		JSONObject jsonObject = new JSONObject();
		if (leaveApplication == null) {
			return jsonObject.toString();
		}
		String startDate = DateUtils.timeStampToString(leaveApplication.getStartDate());
		String endDate = DateUtils.timeStampToString(leaveApplication.getEndDate());

		Date newStartDate = null;
		if (leaveApplication.getLeaveSessionMaster2().getLeaveSessionId() == 1) {
			jsonObject.put("currentStartDate", endDate);
			jsonObject.put("currentStartSession", 2);
		} else if (leaveApplication.getLeaveSessionMaster2().getLeaveSessionId() == 2) {
			newStartDate = DateUtils.addDays(leaveApplication.getEndDate(), 1);
			String newStartDateStr = DateUtils.timeStampToString(DateUtils.convertDateToTimeStamp(newStartDate));
			jsonObject.put("currentStartDate", newStartDateStr);
			jsonObject.put("currentStartSession", 1);
		}

		List<LeaveApplicationExtensionDetails> applicationExtensionDetails = new ArrayList<>(
				leaveApplication.getLeaveApplicationExtensionDetails());
		BeanComparator beanComparator = new BeanComparator("leaveApplicationExtensionDetailsId");
		Collections.sort(applicationExtensionDetails, beanComparator);

		if (applicationExtensionDetails != null && !applicationExtensionDetails.isEmpty()) {

			JSONArray jsonArray = new JSONArray();
			int count = 0;
			for (LeaveApplicationExtensionDetails extensionDetails : applicationExtensionDetails) {

				JSONObject json = new JSONObject();
				json.put("sno", ++count);
				json.put("startDate", DateUtils.timeStampToString(extensionDetails.getFromDate()));
				json.put("endDate", DateUtils.timeStampToString(extensionDetails.getToDate()));
				json.put("startSession", "Session " + extensionDetails.getLeaveSessionMaster1().getLeaveSessionId());
				json.put("endSession", "Session " + extensionDetails.getLeaveSessionMaster2().getLeaveSessionId());
				json.put("reason", extensionDetails.getRemarks());
				jsonArray.add(json);
			}
			jsonObject.put("extensionDetails", jsonArray);

		} else {
			JSONArray jsonArray = new JSONArray();

			JSONObject json = new JSONObject();
			json.put("sno", 1);
			json.put("startDate", startDate);
			json.put("endDate", endDate);
			json.put("startSession", "Session " + leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			json.put("endSession", "Session " + leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			json.put("reason", leaveApplication.getReason());
			jsonArray.add(json);

			jsonObject.put("extensionDetails", jsonArray);
		}

		return jsonObject.toString();
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}
	private String getReviewName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		return employeeName;
	}

	@Override
	public List<ComboValueDTO> getLeaveSessionList() {
		List<LeaveSessionMaster> leaveSessionMasters = leaveSessionMasterDAO.findAll();
		List<ComboValueDTO> sessionList = new ArrayList<>();
		for (LeaveSessionMaster leaveSessionMaster : leaveSessionMasters) {
			ComboValueDTO comboValueDTO = new ComboValueDTO();
			comboValueDTO.setLabel(leaveSessionMaster.getSession());
			comboValueDTO.setValue(leaveSessionMaster.getLeaveSessionId());
			comboValueDTO.setLabelKey(leaveSessionMaster.getSessionLabelKey());
			sessionList.add(comboValueDTO);
		}
		return sessionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#searchEmployee(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public LeaveReviewerResponseForm searchEmployee(PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String empName, String empNumber, Long companyId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		Company companyVO = companyDAO.findById(companyId);
		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		conditionDTO.setGroupId(companyVO.getCompanyGroup().getGroupId());
		int recordSize = employeeDAO.findEmployeesOfGroupCompaniesCount(conditionDTO, pageDTO, sortDTO, companyId);

		List<Employee> finalList = employeeDAO.findEmployeesOfGroupCompanies(conditionDTO, pageDTO, sortDTO, companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			employeeForm.setEmail(employee.getEmail());
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);

		}

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public AddLeaveForm getLeaveCustomFields(Long employeeLeaveSchemeId, Long leaveTypeId, Long companyId,
			Long employeeId, Long employeeLeaveSchemeTypeId) {
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		try {
			List<LeaveCustomFieldDTO> customFieldDTOValues = new ArrayList<>();
			int workFlowRuleValue = 0;

			EmployeeLeaveSchemeType employeeLeaveSchemeTypeVO = employeeLeaveSchemeTypeDAO
					.findByleaveSchemeTypeIdAndCompanyIdAndEmpId(employeeLeaveSchemeTypeId, companyId, employeeId);
			if(employeeLeaveSchemeTypeVO == null)
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
			
			/*EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO.findByEmpIdAndLeaveSchemeId(employeeId, employeeLeaveSchemeId);
			if(employeeLeaveScheme == null)
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}*/
			
			LeaveSchemeType leaveSchemeTypeVO = employeeLeaveSchemeTypeVO.getLeaveSchemeType();
			addLeaveFormRes.setCustomFieldDTOList(customFieldDTOValues);
			Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows = leaveSchemeTypeVO.getLeaveSchemeTypeWorkflows();
			for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : leaveSchemeTypeWorkflows) {
				if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
					workFlowRuleValue = Integer
							.parseInt(leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue());
				}

			}

			String leaveTypeRemarks = "";

			List<EmployeeLeaveReviewer> employeeLeaveReviewers = employeeLeaveReviewerDAO
					.findByEmployeeLeaveSchemeID(employeeLeaveSchemeTypeId);
            List<EmployeeLeaveApplicationReviewerDTO> leaveApplicationReviewerDToList= new ArrayList<EmployeeLeaveApplicationReviewerDTO>();
			List<LeaveTypeForm> leaveTypeFormList = new ArrayList<LeaveTypeForm>();
          
			int totalNoOfReviewers = 0;
			
			for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewers) {
				EmployeeLeaveApplicationReviewerDTO leaveApplicationReviewerDTO= new EmployeeLeaveApplicationReviewerDTO();
				if (totalNoOfReviewers < workFlowRuleValue) {
					if (employeeId == employeeLeaveReviewer.getEmployee1().getEmployeeId()) {
						totalNoOfReviewers++;
						if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
							addLeaveFormRes.setLeaveReviewer1(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
							/*addLeaveFormRes.setApplyTo(
									URLEncoder.encode(getEmployeeName(employeeLeaveReviewer.getEmployee2()), "UTF-8"));*/
							addLeaveFormRes.setApplyTo(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
							addLeaveFormRes.setApplyToEmail(employeeLeaveReviewer.getEmployee2().getEmail());
							addLeaveFormRes.setApplyToId(employeeLeaveReviewer.getEmployee2().getEmployeeId());
							leaveApplicationReviewerDTO.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(employeeLeaveReviewer.getEmployee2().getEmployeeId()));
							leaveApplicationReviewerDTO.setLeaveApplicationReviewer(getReviewName(employeeLeaveReviewer.getEmployee2()).trim());
							byte [] byteImage=employeeDetailLogic.getEmployeeImage(employeeLeaveReviewer.getEmployee2().getEmployeeId(),null, employeeImageWidth, employeeImageHeight);
							leaveApplicationReviewerDTO.setLeaveApplicationReviewerImage(byteImage);
							leaveApplicationReviewerDTO.setReviewerType("Reviewer1");
						} else if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

							addLeaveFormRes.setLeaveReviewer2(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
							//addLeaveFormRes.setLeaveReviewer2Id(employeeLeaveReviewer.getEmployee2().getEmployeeId());
							leaveApplicationReviewerDTO.setLeaveApplicationReviewer(getReviewName(employeeLeaveReviewer.getEmployee2()).trim());
							leaveApplicationReviewerDTO.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(employeeLeaveReviewer.getEmployee2().getEmployeeId()));
							byte [] byteImage=employeeDetailLogic.getEmployeeImage(employeeLeaveReviewer.getEmployee2().getEmployeeId(),null, employeeImageWidth, employeeImageHeight);
							leaveApplicationReviewerDTO.setLeaveApplicationReviewerImage(byteImage);
							leaveApplicationReviewerDTO.setReviewerType("Reviewer2");
						} else if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

							addLeaveFormRes.setLeaveReviewer3(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
							leaveApplicationReviewerDTO.setLeaveApplicationReviewer(getReviewName(employeeLeaveReviewer.getEmployee2()).trim());
							leaveApplicationReviewerDTO.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(employeeLeaveReviewer.getEmployee2().getEmployeeId()));
							byte [] byteImage=employeeDetailLogic.getEmployeeImage(employeeLeaveReviewer.getEmployee2().getEmployeeId(),null, employeeImageWidth, employeeImageHeight);
							leaveApplicationReviewerDTO.setLeaveApplicationReviewerImage(byteImage);
							leaveApplicationReviewerDTO.setReviewerType("Reviewer3");
							//addLeaveFormRes.setLeaveReviewer3Id(employeeLeaveReviewer.getEmployee2().getEmployeeId());
						}
					
						leaveApplicationReviewerDToList.add(leaveApplicationReviewerDTO);
					}
				}
				
				
			}

			addLeaveFormRes.setTotalNoOfReviewers(employeeLeaveReviewers.size());
			addLeaveFormRes.setLeaveTypeFormList(leaveTypeFormList);
			addLeaveFormRes.setEmployeeLeaveApplicationReviewerDToList(leaveApplicationReviewerDToList);
			if (leaveSchemeTypeVO != null) {

				if (leaveSchemeTypeVO.getLeaveSchemeTypeAvailingLeaves().size() > 0) {
					leaveTypeRemarks = leaveSchemeTypeVO.getLeaveSchemeTypeAvailingLeaves().iterator().next()
							.getRemarks();
					addLeaveFormRes.setRemarks(leaveTypeRemarks);
				}

				Set<LeaveSchemeTypeAvailingLeave> leSchemeTypeAvailingLeaves = leaveSchemeTypeVO
						.getLeaveSchemeTypeAvailingLeaves();
				if (leSchemeTypeAvailingLeaves != null && !leSchemeTypeAvailingLeaves.isEmpty()) {
					Set<LeaveSchemeTypeCustomField> leaveSchemeCustomFields = leSchemeTypeAvailingLeaves.iterator()
							.next().getLeaveSchemeTypeCustomFields();

					List<LeaveCustomFieldDTO> customFieldDTOs = new ArrayList<>();
					for (LeaveSchemeTypeCustomField leaveSchemeTypeCustomField : leaveSchemeCustomFields) {

						LeaveCustomFieldDTO leaveCustomFieldDTO = new LeaveCustomFieldDTO();
						leaveCustomFieldDTO.setCustomFieldTypeId(leaveSchemeTypeCustomField.getCustomFieldId());
						leaveCustomFieldDTO.setCustomFieldName(leaveSchemeTypeCustomField.getFieldName());
						leaveCustomFieldDTO.setCustomFieldMandatory(leaveSchemeTypeCustomField.getMandatory());
						customFieldDTOs.add(leaveCustomFieldDTO);

					}

					Collections.sort(customFieldDTOs, new LeaveSchemeCustomFieldComparator());

					addLeaveFormRes.setLeaveCustomFieldDTO(customFieldDTOs);
				}

			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return addLeaveFormRes;
	}

	@Override
	public AddLeaveForm getLeaveBalance(Long companyId, Long employeeId, Long employeeLeaveSchemeTypeId) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		LeaveDTO leaveDTO = employeeLeaveSchemeTypeDAO.getLeaveBalance(employeeLeaveSchemeTypeId);
		if(leaveDTO!=null)
		{
		addLeaveForm.setLeaveBalance(leaveDTO.getLeaveBalance());
		}
		return addLeaveForm;
	}

	@Override
	public AddLeaveForm getNoOfDays(Long companyId, Long employeeId, LeaveDTO leaveDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		LeaveDTO leaveDTORes = employeeLeaveSchemeTypeDAO.getNoOfDays(leaveDTO);
		if(leaveDTORes!=null)
		{
		addLeaveForm.setNoOfDays(leaveDTORes.getDays());
		addLeaveForm.setLeaveDTO(leaveDTORes);
		}
		return addLeaveForm;
	}

	@Override
	public AddLeaveFormResponse getSubmittedCancelledLeaves(String fromDate, String toDate, Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath, String searchCondition,
			String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		conditionDTO.setLeaveType("cancel");
		List<String> leaveStatusNames = new ArrayList<>();
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
		conditionDTO.setLeaveStatusNames(leaveStatusNames);
		//int recordSize = (leaveApplicationDAO.getCountForConditionSubmittedLeaveCancel(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionSubmittedLeaveCancel(conditionDTO, null, null);
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			/*leaveType.append("<br>");
			 ID ENCRYPT
			leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ");'>[View]</a></span>");*/
			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
				addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
			} else {
				addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			}
			addLeaveForm.setLeaveType(String.valueOf(leaveType));
			addLeaveForm.setAction(true);
			addLeaveForm.setRequestType("submittedCancel");
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()));
			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
		
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());

			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			LeaveStatusMaster leaveStatusMasterApp = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO
							.findByConditionLeaveStatus(leaveApplication.getLeaveApplicationId(),
									leaveApplicationReviewer.getEmployee().getEmployeeId(),
									leaveStatusMasterApp.getLeaveStatusID());

					if (applicationWorkflow == null || leaveApplicationReviewer.getPending()) {

						addLeaveForm.setLeaveReviewer1(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
								pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

					}
				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO
							.findByConditionLeaveStatus(leaveApplication.getLeaveApplicationId(),
									leaveApplicationReviewer.getEmployee().getEmployeeId(),
									leaveStatusMasterApp.getLeaveStatusID());

					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {

						addLeaveForm.setLeaveReviewer2(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						if(applicationWorkflow !=null){
							if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
									.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

								StringBuilder leaveReviewer2 = new StringBuilder(
										getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
												leaveApplicationReviewer.getEmployee()));

								leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

								addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

								reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

							}
						}
						
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

							addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

						}

					}

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO
							.findByConditionLeaveStatus(leaveApplication.getLeaveApplicationId(),
									leaveApplicationReviewer.getEmployee().getEmployeeId(),
									leaveStatusMasterApp.getLeaveStatusID());

					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {

						addLeaveForm.setLeaveReviewer3(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));
						if(applicationWorkflow != null){
							if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
									.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

								StringBuilder leaveReviewer3 = new StringBuilder(
										getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
												leaveApplicationReviewer.getEmployee()));

								leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
										+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

								addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

							}
						}

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addLeaveForm.setLeaveReviewer3(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ DateUtils.timeStampToString(applicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()) + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						}

					}

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}
		//Collections.sort(addLeaveFormList, new LeaveCreationDateComp());
		
		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}

	@Override
	public AddLeaveFormResponse getRejectedCancelLeaves(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText) {
		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		conditionDTO.setLeaveType("cancel");
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_REJECTED);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		int recordSize = (leaveApplicationDAO.getCountForConditionRejected(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionForRejected(conditionDTO, pageDTO,
				sortDTO);

		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			leaveType.append("<br>");
			/* ID ENCRYPT*/
			leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ",\"rejected\");'>[View]</a></span>");

			addLeaveForm.setLeaveType(String.valueOf(leaveType));

			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));

			addLeaveForm.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());

					if (applicationWorkflow == null) {

						addLeaveForm.setLeaveReviewer1(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
								pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToStringWithTime(applicationWorkflow.getCreatedDate())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToStringWithTime(applicationWorkflow.getCreatedDate())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;

					}
				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());

					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)
							|| reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						addLeaveForm.setLeaveReviewer2(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED,
									pageContextPath, leaveApplicationReviewer.getEmployee()));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;

						}

					}

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)
							|| reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						addLeaveForm.setLeaveReviewer3(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addLeaveForm.setLeaveReviewer3(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						}

					}

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}

		AddLeaveFormResponse response = new AddLeaveFormResponse();
		response.setRows(addLeaveFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public AddLeaveFormResponse getWithDrawnCancelLeaves(String fromDate, String toDate, Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath, String searchCondition,
			String searchText) {
		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		conditionDTO.setLeaveType("cancel");
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		int recordSize = (leaveApplicationDAO.getCountForConditionWithdrawnCancel(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionWithDrawnCancel(conditionDTO, pageDTO,
				sortDTO);

		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {

			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			leaveType.append("<br>");
			/* ID ENCRYPT*/
			leaveType
					.append("<span class='Textsmall1' style='padding-bottom: 3px;'><a class='alink' href='#' onClick='javascipt:addLeavesSubmittedLeavesView("
							+ FormatPreserveCryptoUtil.encrypt(leaveApplication.getLeaveApplicationId()) + ",\"rejected\");'>[View]</a></span>");

			addLeaveForm.setLeaveType(String.valueOf(leaveType));

			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));

			addLeaveForm.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {

				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					addLeaveForm.setLeaveReviewer2(getEmployeeName(leaveApplicationReviewer.getEmployee()));

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

					addLeaveForm.setLeaveReviewer3(getEmployeeName(leaveApplicationReviewer.getEmployee()));

				}
			}

			addLeaveFormList.add(addLeaveForm);
		}

		AddLeaveFormResponse response = new AddLeaveFormResponse();
		response.setRows(addLeaveFormList);

		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		return response;
	}

	@Override
	public LeaveApplicationPdfDTO generateLeaveApplicationPrintPDF(Long companyId, Long employeeId,
			Long leaveApplicationId) {

		LeaveApplicationPdfDTO leaveApplicationPdfDTO = new LeaveApplicationPdfDTO();

		LeaveApplication leaveApplicationVO = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId, employeeId, companyId);
		if(leaveApplicationVO == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		leaveApplicationPdfDTO.setLeaveSchemeName(
				leaveApplicationVO.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getSchemeName());
		leaveApplicationPdfDTO.setEmployeeNumber(leaveApplicationVO.getEmployee().getEmployeeNumber());

		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				leaveApplicationPdfDTO
						.setLeaveAppPdfByteFile(generateLeaveApplicationPDF(companyId, employeeId, leaveApplicationId));
				return leaveApplicationPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				leaveApplicationPdfDTO
						.setLeaveAppPdfByteFile(generateLeaveApplicationPDF(companyId, employeeId, leaveApplicationId));
				return leaveApplicationPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

	}

	private byte[] generateLeaveApplicationPDF(Long companyId, Long employeeId, Long leaveApplicationId)
			throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId, PAYASIA_TEMP_PATH, "Leave");
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

			PdfPTable claimReportPdfTable = leaveApplicationPrintPDFLogic.createLeaveReportPdf(document, writer, 1,
					companyId, leaveApplicationId);

			document.add(claimReportPdfTable);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow | PdfWriter.CenterWindow);

			document.close();

			pdfIn = new FileInputStream(tempFile);

			return IOUtils.toByteArray(pdfIn);
		} catch (DocumentException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
			throw new PayAsiaSystemException(ex.getMessage(), ex);
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
					throw new PayAsiaSystemException(ex.getMessage(), ex);
				}
			}

			IOUtils.closeQuietly(pdfOut);
			IOUtils.closeQuietly(pdfIn);

			try {
				if (tempFile.exists()) {
					tempFile.delete();
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new PayAsiaSystemException(ex.getMessage(), ex);
			}

		}
	}

	@Override
	public AddLeaveForm addLeaveMobile(Long companyId, Long employeeId, AddLeaveForm addLeaveForm,
			Integer noOfAttachements, LeaveSessionDTO leaveSessionDTO) {
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		Employee reviewer = null;
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		leaveConditionDTO.setEmployeeId(employeeId);
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(addLeaveForm.getLeaveTypeId());
		leaveConditionDTO.setStartDate(addLeaveForm.getFromDate());
		leaveConditionDTO.setEndDate(addLeaveForm.getToDate());

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
		if (!isLeaveUnitDays) {
			addLeaveForm.setFromSessionId(1l);
			addLeaveForm.setToSessionId(2l);
			// Hours between Dates
			if (addLeaveForm.getNoOfDays() != null) {
				leaveConditionDTO.setTotalHoursBetweenDates(addLeaveForm.getNoOfDays().floatValue());
			}
			leaveConditionDTO.setLeaveUnitHours(true);
		}
		leaveConditionDTO.setStartSession(addLeaveForm.getFromSessionId());
		leaveConditionDTO.setEndSession(addLeaveForm.getToSessionId());
		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setEmployeeLeaveApplicationId(null);

		if (noOfAttachements > 0) {
			leaveConditionDTO.setAttachementStatus(true);
		} else {
			leaveConditionDTO.setAttachementStatus(false);
		}

		LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
		addLeaveFormRes.setLeaveDTO(leaveDTOValidate);
		if (leaveDTOValidate.getErrorCode() == 1) {
			return addLeaveFormRes;
		}

		Company company = companyDAO.findById(companyId);
		Employee employee = employeeDAO.findById(employeeId);

		employeeLeaveSchemeDAO.checkEmpLeaveSchemeByDate(null, employeeId,
				DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime()), company.getDateFormat());

		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
				.findById(addLeaveForm.getLeaveTypeId());

		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setEmployee(employee);
		leaveApplication.setCompany(company);
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);

		leaveApplication.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
		leaveApplication.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));
		leaveApplication.setReason(addLeaveForm.getReason());
		leaveApplication
				.setApplyTo(getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId()).getEmail());
		leaveApplication.setEmailCC(addLeaveForm.getEmailCC());

		if (addLeaveForm.getFromSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getFromSessionId());
			leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);

		}

		if (addLeaveForm.getToSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getToSessionId());
			leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
		}

		LeaveStatusMaster leaveStatusMaster = null;

		if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_DRAFT);
		} else if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		}

		if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
				&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
					.getApprovalNotRequired()) {

				leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			}

		}

		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		Date date = new Date();
		leaveApplication.setCreatedDate(new Timestamp(date.getTime()));
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = addLeaveForm.getNoOfDays();
		}

		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		leaveApplication.setTotalDays(Float.parseFloat(totalLeaveDays.toString()));
		Integer result = totalLeaveDays.compareTo(leaveBalance.getLeaveBalance());

		if (result == 1) {
			leaveApplication.setExcessDays(
					Float.parseFloat(totalLeaveDays.subtract(leaveBalance.getLeaveBalance()).toString()));

		}

		LeaveApplication persistLeaveApplication = leaveApplicationDAO.saveReturn(leaveApplication);

		List<LeaveCustomFieldDTO> customFieldDTOs = addLeaveForm.getCustomFieldDTOList();

		for (LeaveCustomFieldDTO leaveCustomFieldDTO : customFieldDTOs) {

			LeaveApplicationCustomField leaveApplicationCustomField = new LeaveApplicationCustomField();
			leaveApplicationCustomField.setLeaveApplication(persistLeaveApplication);
			leaveApplicationCustomField.setValue(leaveCustomFieldDTO.getValue());
			leaveApplicationCustomField.setLeaveSchemeTypeCustomField(
					leaveSchemeTypeCustomFieldDAO.findById(leaveCustomFieldDTO.getCustomFieldTypeId()));
			if (!leaveCustomFieldDTO.getValue().equals("") && leaveCustomFieldDTO.getValue() != null) {
				leaveApplicationCustomFieldDAO.save(leaveApplicationCustomField);
			}

		}

		if (!addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
			leaveApplicationWorkflow.setLeaveApplication(persistLeaveApplication);
			leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplicationWorkflow.setRemarks(addLeaveForm.getReason());
			leaveApplicationWorkflow.setForwardTo(addLeaveForm.getApplyTo());
			leaveApplicationWorkflow.setEmailCC(addLeaveForm.getEmailCC());
			leaveApplicationWorkflow.setEmployee(employee);
			leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			leaveApplicationWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
			leaveApplicationWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

			leaveApplicationWorkflow.setTotalDays(persistLeaveApplication.getTotalDays());
			leaveApplicationWorkflow.setStartSessionMaster(persistLeaveApplication.getLeaveSessionMaster1());
			leaveApplicationWorkflow.setEndSessionMaster(persistLeaveApplication.getLeaveSessionMaster2());

			leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

			if (addLeaveForm.getApplyToId() != null && addLeaveForm.getApplyToId() != 0) {

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				reviewer = getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId());
				leaveApplicationReviewer.setEmployee(reviewer);
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
				leaveApplicationReviewer.setPending(true);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

			}

			if (addLeaveForm.getLeaveReviewer2Id() != null && addLeaveForm.getLeaveReviewer2Id() != 0) {

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				leaveApplicationReviewer.setEmployee(
						getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getLeaveReviewer2Id()));
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "2");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
				leaveApplicationReviewer.setPending(false);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

			}

			if (addLeaveForm.getLeaveReviewer3Id() != null && addLeaveForm.getLeaveReviewer3Id() != 0) {

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();

				leaveApplicationReviewer.setEmployee(
						getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getLeaveReviewer3Id()));
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "3");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
				leaveApplicationReviewer.setPending(false);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

			}

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
					&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

				if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
						.getApprovalNotRequired()) {

					leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

					saveEmployeeLeaveSchemeHistory(persistLeaveApplication);

				}

			}

			generalMailLogic.sendEMailForLeave(companyId, persistLeaveApplication,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPLY, totalLeaveDays, leaveBalance.getLeaveBalance(),
					employee, reviewer, leaveSessionDTO, true);

			NotificationAlert notificationAlert = new NotificationAlert();
			notificationAlert.setEmployee(reviewer);
			ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

			notificationAlert.setModuleMaster(moduleMaster);
			notificationAlert.setMessage(getEmployeeName(persistLeaveApplication.getEmployee()) + " "
					+ PayAsiaConstants.LEAVEAPPLICATION_NOTIFICATION_ALERT);
			notificationAlert.setShownStatus(false);
			notificationAlertDAO.save(notificationAlert);

		}
		addLeaveFormRes.setLeaveApplicationId(persistLeaveApplication.getLeaveApplicationId());

		return addLeaveFormRes;

	}

	@Override
	public void uploadLeaveApllication(String fileName, byte[] imgBytes, Long leaveApplicationId) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		Date date = new Date();
		LeaveApplicationAttachment leaveApplicationAttachment = new LeaveApplicationAttachment();
		leaveApplicationAttachment.setFileName(fileName);
		leaveApplicationAttachment.setFileType(fileName);
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(leaveApplicationId);
		leaveApplicationAttachment.setLeaveApplication(leaveApplication);
		leaveApplicationAttachment.setFileType(fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length()));
		leaveApplicationAttachment.setUploadedDate(new Timestamp(date.getTime()));
		LeaveApplicationAttachment saveReturn = leaveApplicationAttachmentDAO.saveReturn(leaveApplicationAttachment);

		// save Leave attachment to file directory
		filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				leaveApplication.getCompany().getCompanyId(), PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME, null,
				null, null, null, PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);

		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		/*
		 * String filePath = downloadPath + "/company/" +
		 * leaveApplication.getCompany().getCompanyId() + "/" +
		 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/";
		 */

		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
			String fileNameNew = String.valueOf(saveReturn.getLeaveApplicationAttachmentId());
			if (!("").equals(fileNameNew)) {
				fileNameNew = saveReturn.getLeaveApplicationAttachmentId() + "." + ext;
			}
			awss3LogicImpl.uploadByteArrayFile(imgBytes, filePath + fileNameNew);

		} else {
			FileUtils.uploadFile(imgBytes, fileName, filePath, fileNameSeperator,
					saveReturn.getLeaveApplicationAttachmentId());
		}

	}

	@Override
	public AddLeaveForm updateLeaveMobile(Long companyId, Long employeeId, AddLeaveForm addLeaveForm,
			Integer noOfAttachments, LeaveSessionDTO leaveSessionDTO) {

		Employee loggenInEmp = employeeDAO.findById(employeeId);
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
//		LeaveApplication leaveApplication = leaveApplicationDAO.findById(addLeaveForm.getLeaveApplicationId());
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(addLeaveForm.getLeaveApplicationId(), employeeId, companyId);
		Employee reviewer = null;
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		leaveConditionDTO.setEmployeeId(employeeId);
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(addLeaveForm.getLeaveTypeId());
		leaveConditionDTO.setStartDate(addLeaveForm.getFromDate());
		leaveConditionDTO.setEndDate(addLeaveForm.getToDate());

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
		if (!isLeaveUnitDays) {
			addLeaveForm.setFromSessionId(1l);
			addLeaveForm.setToSessionId(2l);
			// Hours between Dates
			if (addLeaveForm.getNoOfDays() != null) {
				leaveConditionDTO.setTotalHoursBetweenDates(addLeaveForm.getNoOfDays().floatValue());
			}
			leaveConditionDTO.setLeaveUnitHours(true);
		}
		leaveConditionDTO.setStartSession(addLeaveForm.getFromSessionId());
		leaveConditionDTO.setEndSession(addLeaveForm.getToSessionId());
		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setEmployeeLeaveApplicationId(leaveApplication.getLeaveApplicationId());

		if (noOfAttachments > 0 || leaveApplication.getLeaveApplicationAttachments().size() > 0) {
			leaveConditionDTO.setAttachementStatus(true);
		} else {
			leaveConditionDTO.setAttachementStatus(false);

		}

		LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
		addLeaveFormRes.setLeaveDTO(leaveDTOValidate);
		if (leaveDTOValidate.getErrorCode() == 1) {
			return addLeaveFormRes;
		}

		leaveApplication.setEmailCC(addLeaveForm.getEmailCC());
		leaveApplication.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

		List<LeaveCustomFieldDTO> customFieldDTOs = addLeaveForm.getCustomFieldDTOList();

		for (LeaveCustomFieldDTO leaveCustomFieldDTO : customFieldDTOs) {

			LeaveApplicationCustomField leaveApplicationCustomField = new LeaveApplicationCustomField();
			LeaveApplicationCustomField leaveApplicationCustomFieldVO = leaveApplicationCustomFieldDAO
					.findById(leaveCustomFieldDTO.getCustomFieldId());
			if (leaveApplicationCustomFieldVO == null) {
				leaveApplicationCustomFieldVO = new LeaveApplicationCustomField();
				leaveApplicationCustomField.setLeaveApplication(leaveApplication);
				leaveApplicationCustomField.setValue(leaveCustomFieldDTO.getValue());
				leaveApplicationCustomField.setLeaveSchemeTypeCustomField(
						leaveSchemeTypeCustomFieldDAO.findById(leaveCustomFieldDTO.getCustomFieldTypeId()));
				if (!leaveCustomFieldDTO.getValue().equals("") && leaveCustomFieldDTO.getValue() != null) {
					leaveApplicationCustomFieldDAO.save(leaveApplicationCustomField);
				}
			} else {
				leaveApplicationCustomFieldVO = leaveApplicationCustomFieldDAO
						.findById(leaveCustomFieldDTO.getCustomFieldId());

				leaveApplicationCustomFieldVO.setValue(leaveCustomFieldDTO.getValue());

				leaveApplicationCustomFieldDAO.update(leaveApplicationCustomFieldVO);
			}

		}

		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
				.findById(addLeaveForm.getLeaveTypeId());

		leaveApplication.setReason(addLeaveForm.getReason());
		leaveApplication.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));

		if (addLeaveForm.getFromSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getFromSessionId());
			leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);

		}

		if (addLeaveForm.getToSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getToSessionId());
			leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
		}

		Date date = new Date();
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);

		LeaveStatusMaster leaveStatusMaster = null;
		if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_DRAFT);
		} else if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		}

		if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
				&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)
				&& employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
						.getApprovalNotRequired()) {

			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}

		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = addLeaveForm.getNoOfDays();
		}

		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		Integer result = totalLeaveDays.compareTo(leaveBalance.getLeaveBalance());

		if (result == 1) {
			leaveApplication.setExcessDays(
					Float.parseFloat(totalLeaveDays.subtract(leaveBalance.getLeaveBalance()).toString()));

		}
		leaveApplication.setTotalDays(Float.parseFloat(totalLeaveDays.toString()));

		leaveApplicationDAO.update(leaveApplication);

		if (!addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
			leaveApplicationWorkflow.setLeaveApplication(leaveApplication);
			leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplicationWorkflow.setRemarks(addLeaveForm.getReason());
			leaveApplicationWorkflow.setForwardTo(addLeaveForm.getApplyTo());
			leaveApplicationWorkflow.setEmailCC(addLeaveForm.getEmailCC());
			leaveApplicationWorkflow.setEmployee(leaveApplication.getEmployee());
			leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			leaveApplicationWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
			leaveApplicationWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

			leaveApplicationWorkflow.setTotalDays(leaveApplication.getTotalDays());

			leaveApplicationWorkflow.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
			leaveApplicationWorkflow.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());

			leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

			if (addLeaveForm.getApplyToId() != null && addLeaveForm.getApplyToId() != 0) {

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();

				reviewer = getDelegatedEmployee(leaveApplication.getEmployee().getEmployeeId(),
						addLeaveForm.getApplyToId());
				leaveApplicationReviewer.setEmployee(reviewer);
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(leaveApplication);
				leaveApplicationReviewer.setPending(true);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

			}

			if (addLeaveForm.getLeaveReviewer2Id() != null && addLeaveForm.getLeaveReviewer2Id() != 0) {

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				leaveApplicationReviewer.setEmployee(getDelegatedEmployee(
						leaveApplication.getEmployee().getEmployeeId(), addLeaveForm.getLeaveReviewer2Id()));
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "2");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(leaveApplication);
				leaveApplicationReviewer.setPending(false);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

			}
			if (addLeaveForm.getLeaveReviewer3Id() != null && addLeaveForm.getLeaveReviewer3Id() != 0) {

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				leaveApplicationReviewer.setEmployee(getDelegatedEmployee(
						leaveApplication.getEmployee().getEmployeeId(), addLeaveForm.getLeaveReviewer3Id()));
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "3");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(leaveApplication);
				leaveApplicationReviewer.setPending(false);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

			}

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
					&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)
					&& employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
							.getApprovalNotRequired()) {

				leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

				saveEmployeeLeaveSchemeHistory(leaveApplication);

			}

			NotificationAlert notificationAlert = new NotificationAlert();
			notificationAlert.setEmployee(reviewer);
			ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

			notificationAlert.setModuleMaster(moduleMaster);
			notificationAlert.setMessage(getEmployeeName(leaveApplication.getEmployee()) + " "
					+ PayAsiaConstants.LEAVEAPPLICATION_NOTIFICATION_ALERT);
			notificationAlert.setShownStatus(false);
			notificationAlertDAO.save(notificationAlert);

			generalMailLogic.sendEMailForLeave(companyId, leaveApplication,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPLY, totalLeaveDays, leaveBalance.getLeaveBalance(),
					loggenInEmp, reviewer, leaveSessionDTO, true);
		}

		return addLeaveFormRes;
	}

	@Override
	public AddLeaveForm addLeaveByAdmin(Long companyId, Long adminEmployeeId, AddLeaveForm addLeaveForm,
			LeaveSessionDTO sessionDTO) {
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();

		Long employeeId = null;
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
				.findById(addLeaveForm.getLeaveTypeId());
		if (employeeLeaveSchemeType != null) {
			employeeId = employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployee().getEmployeeId();
		}

		Employee reviewer = null;
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		leaveConditionDTO.setEmployeeId(employeeId);
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(addLeaveForm.getLeaveTypeId());
		leaveConditionDTO.setStartDate(addLeaveForm.getFromDate());
		leaveConditionDTO.setEndDate(addLeaveForm.getToDate());

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
		if (!isLeaveUnitDays) {
			addLeaveForm.setFromSessionId(1l);
			addLeaveForm.setToSessionId(2l);
			// Hours between Dates
			leaveConditionDTO.setTotalHoursBetweenDates(addLeaveForm.getNoOfDays().floatValue());
			leaveConditionDTO.setLeaveUnitHours(true);
		}
		leaveConditionDTO.setStartSession(addLeaveForm.getFromSessionId());
		leaveConditionDTO.setEndSession(addLeaveForm.getToSessionId());
		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setEmployeeLeaveApplicationId(null);
		int attachCount = 0;
		if (addLeaveForm.getAttachmentList() == null || addLeaveForm.getAttachmentList().size() < 0) {
			leaveConditionDTO.setAttachementStatus(false);

		} else {
			for (LeaveApplicationAttachmentDTO leaveApplicationAttachmentDTO : addLeaveForm.getAttachmentList()) {
				if (leaveApplicationAttachmentDTO.getAttachment() != null) {
					attachCount++;
				}
			}
			if (attachCount > 0) {
				leaveConditionDTO.setAttachementStatus(true);
			} else {
				leaveConditionDTO.setAttachementStatus(false);
			}
		}

		LeaveDTO leaveDTOValidate = leaveApplicationDAO.validateLeaveApplication(leaveConditionDTO);
		addLeaveFormRes.setLeaveDTO(leaveDTOValidate);
		if (leaveDTOValidate.getErrorCode() == 1) {
			return addLeaveFormRes;
		}

		Company company = companyDAO.findById(companyId);
		Employee employee = employeeDAO.findById(employeeId);

		employeeLeaveSchemeDAO.checkEmpLeaveSchemeByDate(null, employeeId,
				DateUtils.timeStampToString(DateUtils.getCurrentTimestampWithTime()), company.getDateFormat());

		LeaveApplication leaveApplication = new LeaveApplication();
		leaveApplication.setEmployee(employee);
		leaveApplication.setCompany(company);
		leaveApplication.setEmployeeLeaveSchemeType(employeeLeaveSchemeType);

		leaveApplication.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
		leaveApplication.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));
		leaveApplication.setReason(addLeaveForm.getReason());
		boolean preApprovalRequest = false;
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		if (leavePreferenceVO != null) {
			preApprovalRequest = leavePreferenceVO.isPreApprovalRequired();
		}
		if (preApprovalRequest && addLeaveForm.getPreApprovalReq() != null) {
			leaveApplication.setPreApprovalRequest(addLeaveForm.getPreApprovalReq());
		} else {
			leaveApplication.setPreApprovalRequest(false);
		}

		if (getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId()) != null && StringUtils
				.isBlank(getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId()).getEmail())) {

			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.APPLYTO_REVIEWER_EMAIL_NOT_DEFINED);
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
			return addLeaveFormRes;

		}
		leaveApplication
				.setApplyTo(getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId()).getEmail());
		leaveApplication.setEmailCC(addLeaveForm.getEmailCC());

		if (addLeaveForm.getFromSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getFromSessionId());
			leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);

		}

		if (addLeaveForm.getToSessionId() != null) {
			LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO.findById(addLeaveForm.getToSessionId());
			leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
		}

		LeaveStatusMaster leaveStatusMaster = null;
		LeaveStatusMaster leaveApprovalNotReqStatus = null;

		if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_DRAFT);
		} else if (addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		}
		boolean approvalNotRequired = false;
		if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
				&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
					.getApprovalNotRequired() || (preApprovalRequest && addLeaveForm.getPreApprovalReq())) {
				approvalNotRequired = true;
				leaveApprovalNotReqStatus = leaveStatusMasterDAO
						.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
			}
		}

		if (approvalNotRequired) {
			leaveApplication.setLeaveStatusMaster(leaveApprovalNotReqStatus);
		} else {
			leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		}

		Date date = new Date();
		leaveApplication.setCreatedDate(new Timestamp(date.getTime()));
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = addLeaveForm.getNoOfDays();
		}

		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		leaveApplication.setTotalDays(Float.parseFloat(totalLeaveDays.toString()));
		Integer result = totalLeaveDays.compareTo(leaveBalance.getLeaveBalance());

		if (result == 1) {
			leaveApplication.setExcessDays(
					Float.parseFloat(totalLeaveDays.subtract(leaveBalance.getLeaveBalance()).toString()));

		}

		LeaveApplication persistLeaveApplication = leaveApplicationDAO.saveReturn(leaveApplication);

		List<LeaveCustomFieldDTO> customFieldDTOs = addLeaveForm.getCustomFieldDTOList();

		for (LeaveCustomFieldDTO leaveCustomFieldDTO : customFieldDTOs) {

			LeaveApplicationCustomField leaveApplicationCustomField = new LeaveApplicationCustomField();
			leaveApplicationCustomField.setLeaveApplication(persistLeaveApplication);
			leaveApplicationCustomField.setValue(leaveCustomFieldDTO.getValue());
			leaveApplicationCustomField.setLeaveSchemeTypeCustomField(
					leaveSchemeTypeCustomFieldDAO.findById(leaveCustomFieldDTO.getCustomFieldTypeId()));
			if (!leaveCustomFieldDTO.getValue().equals("") && leaveCustomFieldDTO.getValue() != null) {
				leaveApplicationCustomFieldDAO.save(leaveApplicationCustomField);
			}

		}

		if (addLeaveForm.getAttachmentList() != null) {
			FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
			for (LeaveApplicationAttachmentDTO applicationAttachmentDTO : addLeaveForm.getAttachmentList()) {
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() > 0) {
					String attachmentName = applicationAttachmentDTO.getAttachment().getOriginalFilename();
					LeaveApplicationAttachment leaveApplicationAttachment = new LeaveApplicationAttachment();
					leaveApplicationAttachment.setLeaveApplication(persistLeaveApplication);
					leaveApplicationAttachment.setFileName(attachmentName);
					leaveApplicationAttachment.setFileType(
							attachmentName.substring(attachmentName.lastIndexOf('.') + 1, attachmentName.length()));
					leaveApplicationAttachment.setUploadedDate(new Timestamp(date.getTime()));
					LeaveApplicationAttachment saveReturn = leaveApplicationAttachmentDAO
							.saveReturn(leaveApplicationAttachment);

					// save Leave attachment to file directory

					filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
							PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME, null, null, null, null,
							PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);

					String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
					/*
					 * String filePath = downloadPath + "/company/" + companyId
					 * + "/" + PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME
					 * + "/";
					 */

					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						String fileNameNew = applicationAttachmentDTO.getAttachment().getOriginalFilename();
						String ext = fileNameNew.substring(fileNameNew.lastIndexOf('.') + 1);

						if (!("").equals(fileNameNew)) {
							fileNameNew = saveReturn.getLeaveApplicationAttachmentId() + "." + ext;
						}
						awss3LogicImpl.uploadCommonMultipartFile(applicationAttachmentDTO.getAttachment(),
								filePath + fileNameNew);
					} else {
						FileUtils.uploadFile(applicationAttachmentDTO.getAttachment(), filePath, fileNameSeperator,
								saveReturn.getLeaveApplicationAttachmentId());
					}

				}
			}
		}
		String allowApprove = "";
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : employeeLeaveSchemeType.getLeaveSchemeType()
				.getLeaveSchemeTypeWorkflows()) {
			if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue();
			}
		}

		if (!addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {

			LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
			leaveApplicationWorkflow.setLeaveApplication(persistLeaveApplication);
			leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplicationWorkflow.setRemarks(addLeaveForm.getReason());
			leaveApplicationWorkflow.setForwardTo(addLeaveForm.getApplyTo());

			String emailCC = StringUtils.removeEnd(addLeaveForm.getEmailCC(), ";");
			if (approvalNotRequired) {
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
				EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO.findByCondition(
						leaveApplication.getCompany().getCompanyId(), leaveApplication.getEmployee().getEmployeeId(),
						moduleMaster.getModuleId());
				if (employeeDefaultEmailCCVO != null) {
					if (StringUtils.isNotBlank(emailCC)) {
						emailCC += ";";
					}
					emailCC += employeeDefaultEmailCCVO.getEmailCC();
				}
			}
			leaveApplicationWorkflow.setEmailCC(emailCC);
			leaveApplicationWorkflow.setEmployee(employee);
			leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			leaveApplicationWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
			leaveApplicationWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

			leaveApplicationWorkflow.setTotalDays(persistLeaveApplication.getTotalDays());
			leaveApplicationWorkflow.setStartSessionMaster(persistLeaveApplication.getLeaveSessionMaster1());
			leaveApplicationWorkflow.setEndSessionMaster(persistLeaveApplication.getLeaveSessionMaster2());

			leaveApplicationWorkflowDAO.save(leaveApplicationWorkflow);

			boolean leaveWorkflowNotRequired = false;
			if (leavePreferenceVO != null) {
				leaveWorkflowNotRequired = leavePreferenceVO.isLeaveWorkflowNotRequired();
			}

			if (approvalNotRequired) {
				LeaveApplicationWorkflow leaveApplWorkflow = new LeaveApplicationWorkflow();
				leaveApplWorkflow.setLeaveApplication(persistLeaveApplication);
				leaveApplWorkflow.setLeaveStatusMaster(leaveApprovalNotReqStatus);
				leaveApplWorkflow.setRemarks(addLeaveForm.getReason());
				leaveApplWorkflow.setForwardTo(addLeaveForm.getApplyTo());
				leaveApplWorkflow.setEmailCC(addLeaveForm.getEmailCC());
				leaveApplWorkflow.setEmployee(employee);
				leaveApplWorkflow.setCreatedDate(new Timestamp(date.getTime()));
				leaveApplWorkflow.setStartDate(DateUtils.stringToTimestamp(addLeaveForm.getFromDate()));
				leaveApplWorkflow.setEndDate(DateUtils.stringToTimestamp(addLeaveForm.getToDate()));

				leaveApplWorkflow.setTotalDays(persistLeaveApplication.getTotalDays());
				leaveApplWorkflow.setStartSessionMaster(persistLeaveApplication.getLeaveSessionMaster1());
				leaveApplWorkflow.setEndSessionMaster(persistLeaveApplication.getLeaveSessionMaster2());

				leaveApplicationWorkflowDAO.save(leaveApplWorkflow);

				LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
				reviewer = getDelegatedEmployeeForNotReqApproval(employeeId);
				leaveApplicationReviewer.setEmployee(reviewer);
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
				leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
				leaveApplicationReviewer.setPending(false);
				leaveApplicationReviewerDAO.save(leaveApplicationReviewer);
			} else {
				if (addLeaveForm.getApplyToId() != null && addLeaveForm.getApplyToId() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
					reviewer = getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getApplyToId());
					leaveApplicationReviewer.setEmployee(reviewer);
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "1");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
					leaveApplicationReviewer.setPending(true);
					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

				if (addLeaveForm.getLeaveReviewer2Id() != null && addLeaveForm.getLeaveReviewer2Id() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();
					leaveApplicationReviewer.setEmployee(
							getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getLeaveReviewer2Id()));
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "2");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
					if (allowApprove.length() == 3 && allowApprove.substring(1, 2).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}

					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

				if (addLeaveForm.getLeaveReviewer3Id() != null && addLeaveForm.getLeaveReviewer3Id() != 0) {

					LeaveApplicationReviewer leaveApplicationReviewer = new LeaveApplicationReviewer();

					leaveApplicationReviewer.setEmployee(
							getDelegatedEmployee(employee.getEmployeeId(), addLeaveForm.getLeaveReviewer3Id()));
					WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
							.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_LEAVE_REVIEWER, "3");
					leaveApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
					leaveApplicationReviewer.setLeaveApplication(persistLeaveApplication);
					if (allowApprove.length() == 3 && allowApprove.substring(2, 3).equals("1")
							&& leaveWorkflowNotRequired) {
						leaveApplicationReviewer.setPending(true);
					} else {
						leaveApplicationReviewer.setPending(false);
					}
					leaveApplicationReviewerDAO.save(leaveApplicationReviewer);

				}

			}

			if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().size() > 0
					&& addLeaveForm.getStatus().equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {

				if (employeeLeaveSchemeType.getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves().iterator().next()
						.getApprovalNotRequired() || (preApprovalRequest && addLeaveForm.getPreApprovalReq())) {
					leaveStatusMaster = leaveStatusMasterDAO.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

					saveEmployeeLeaveSchemeHistory(persistLeaveApplication);
				}

			}

			if (!approvalNotRequired) {
				Employee adminEmp = employeeDAO.findById(adminEmployeeId);
				generalMailLogic.sendEMailLeaveByAdminForEmployee(companyId, persistLeaveApplication,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPLY_BY_ADMIN_TO_MANAGER, totalLeaveDays,
						leaveBalance.getLeaveBalance(), adminEmp, reviewer, sessionDTO, isLeaveUnitDays,
						persistLeaveApplication.getApplyTo());

				generalMailLogic.sendEMailLeaveByAdminForEmployee(companyId, persistLeaveApplication,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPLY_BY_ADMIN_TO_EMP, totalLeaveDays,
						leaveBalance.getLeaveBalance(), adminEmp, reviewer, sessionDTO, isLeaveUnitDays,
						persistLeaveApplication.getEmployee().getEmail());

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(reviewer);
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(persistLeaveApplication.getEmployee()) + " "
						+ PayAsiaConstants.LEAVEAPPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);

			} else if (approvalNotRequired && preApprovalRequest && addLeaveForm.getPreApprovalReq()) {

				List<EmployeeLeaveReviewer> employeeLeaveReviewerList = employeeLeaveReviewerDAO
						.findByLeaveApplicationId(persistLeaveApplication.getLeaveApplicationId());

				generalMailLogic.sendMailForPreApprovedLeave(
						leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_PRE_APPROVE_LEAVE_APPLY,
						totalLeaveDays, leaveBalance.getLeaveBalance(), employee, "", sessionDTO, isLeaveUnitDays,
						employee.getEmail());

				if (employeeLeaveReviewerList != null && !employeeLeaveReviewerList.isEmpty()) {
					for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewerList) {

						generalMailLogic.sendMailForPreApprovedLeave(
								leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany()
										.getCompanyId(),
								leaveApplicationWorkflow,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_PRE_APPROVE_LEAVE_APPLY_FOR_REVIEWER,
								totalLeaveDays, leaveBalance.getLeaveBalance(), employee, "", sessionDTO,
								isLeaveUnitDays, employeeLeaveReviewer.getEmployee2().getEmail());
					}
				}

				NotificationAlert notificationAlert = new NotificationAlert();
				notificationAlert.setEmployee(reviewer);
				ModuleMaster moduleMaster = moduleMasterDAO.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);

				notificationAlert.setModuleMaster(moduleMaster);
				notificationAlert.setMessage(getEmployeeName(persistLeaveApplication.getEmployee()) + " "
						+ PayAsiaConstants.PRE_APPROVAL_LEAVE_APPLICATION_NOTIFICATION_ALERT);
				notificationAlert.setShownStatus(false);
				notificationAlertDAO.save(notificationAlert);
				
				IntegrationMaster integrationMaster =	keyPayIntLeaveApplicationDAO.findByKeyPayDetailByCompanyId(companyId);
				if(integrationMaster!=null && !isLeaveUnitDays)
				{
					leaveBalanceSummaryLogic.addLeaveAppToKeyPayInt(persistLeaveApplication);
				}
			} else {
				generalMailLogic.sendAcceptRejectMailForLeave(
						leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPROVE, totalLeaveDays,
						leaveBalance.getLeaveBalance(), employee, "", sessionDTO, isLeaveUnitDays);
			}

		}

		return addLeaveFormRes;

	}

	@Override
	public AddLeaveForm getLeaveCustomFieldAndReviewerForAdmin(Long employeeLeaveSchemeId, Long leaveTypeId,
			Long companyId, Long employeeId, Long employeeLeaveSchemeTypeId) {
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		try {
			List<LeaveCustomFieldDTO> customFieldDTOValues = new ArrayList<>();
			int workFlowRuleValue = 0;

			EmployeeLeaveSchemeType employeeLeaveSchemeTypeVO = employeeLeaveSchemeTypeDAO
					.findSchemeTypeByCompanyId(employeeLeaveSchemeTypeId, companyId);
			if(employeeLeaveSchemeTypeVO == null)
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
				
			EmployeeLeaveScheme employeeLeaveScheme = employeeLeaveSchemeDAO.findSchemeByCompanyId(employeeLeaveSchemeId, companyId);
			if(employeeLeaveScheme == null)
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
			LeaveSchemeType leaveSchemeTypeVO = employeeLeaveSchemeTypeVO.getLeaveSchemeType();
			addLeaveFormRes.setCustomFieldDTOList(customFieldDTOValues);
			Set<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflows = leaveSchemeTypeVO.getLeaveSchemeTypeWorkflows();
			for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : leaveSchemeTypeWorkflows) {
				if (leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleName().toUpperCase()
						.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
					workFlowRuleValue = Integer
							.parseInt(leaveSchemeTypeWorkflow.getWorkFlowRuleMaster().getRuleValue());
				}

			}

			String leaveTypeRemarks = "";

			List<EmployeeLeaveReviewer> employeeLeaveReviewers = employeeLeaveReviewerDAO
					.findByEmployeeLeaveSchemeID(employeeLeaveSchemeTypeId);

			List<LeaveTypeForm> leaveTypeFormList = new ArrayList<LeaveTypeForm>();

			int totalNoOfReviewers = 0;
			for (EmployeeLeaveReviewer employeeLeaveReviewer : employeeLeaveReviewers) {
				if (totalNoOfReviewers < workFlowRuleValue) {
					if (employeeLeaveSchemeTypeVO.getEmployeeLeaveScheme().getEmployee()
							.getEmployeeId() == employeeLeaveReviewer.getEmployee1().getEmployeeId()) {
						totalNoOfReviewers++;
						if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
							addLeaveFormRes.setLeaveReviewer1(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
							addLeaveFormRes.setApplyTo(
									URLEncoder.encode(getEmployeeName(employeeLeaveReviewer.getEmployee2()), "UTF-8"));
							addLeaveFormRes.setApplyToEmail(employeeLeaveReviewer.getEmployee2().getEmail());
							addLeaveFormRes.setApplyToId(employeeLeaveReviewer.getEmployee2().getEmployeeId());

						} else if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

							addLeaveFormRes.setLeaveReviewer2(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
							addLeaveFormRes.setLeaveReviewer2Id(employeeLeaveReviewer.getEmployee2().getEmployeeId());

						} else if (employeeLeaveReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

							addLeaveFormRes.setLeaveReviewer3(getEmployeeName(employeeLeaveReviewer.getEmployee2()));
							addLeaveFormRes.setLeaveReviewer3Id(employeeLeaveReviewer.getEmployee2().getEmployeeId());
						}
					}
				}

			}

			addLeaveFormRes.setTotalNoOfReviewers(totalNoOfReviewers);
			addLeaveFormRes.setLeaveTypeFormList(leaveTypeFormList);

			if (leaveSchemeTypeVO != null) {

				if (leaveSchemeTypeVO.getLeaveSchemeTypeAvailingLeaves().size() > 0) {
					leaveTypeRemarks = leaveSchemeTypeVO.getLeaveSchemeTypeAvailingLeaves().iterator().next()
							.getRemarks();
					addLeaveFormRes.setRemarks(leaveTypeRemarks);
				}

				Set<LeaveSchemeTypeAvailingLeave> leSchemeTypeAvailingLeaves = leaveSchemeTypeVO
						.getLeaveSchemeTypeAvailingLeaves();
				if (leSchemeTypeAvailingLeaves != null && !leSchemeTypeAvailingLeaves.isEmpty()) {
					Set<LeaveSchemeTypeCustomField> leaveSchemeCustomFields = leSchemeTypeAvailingLeaves.iterator()
							.next().getLeaveSchemeTypeCustomFields();

					List<LeaveCustomFieldDTO> customFieldDTOs = new ArrayList<>();
					for (LeaveSchemeTypeCustomField leaveSchemeTypeCustomField : leaveSchemeCustomFields) {

						LeaveCustomFieldDTO leaveCustomFieldDTO = new LeaveCustomFieldDTO();
						leaveCustomFieldDTO.setCustomFieldTypeId(leaveSchemeTypeCustomField.getCustomFieldId());
						leaveCustomFieldDTO.setCustomFieldName(leaveSchemeTypeCustomField.getFieldName());
						leaveCustomFieldDTO.setCustomFieldMandatory(leaveSchemeTypeCustomField.getMandatory());
						customFieldDTOs.add(leaveCustomFieldDTO);

					}

					Collections.sort(customFieldDTOs, new LeaveSchemeCustomFieldComparator());

					addLeaveFormRes.setLeaveCustomFieldDTO(customFieldDTOs);
				}

			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return addLeaveFormRes;
	}
	
	@Override
	public AddLeaveForm viewLeave(Long leaveApplicationId, Long employeeId, Long companyId) {

		AddLeaveForm addLeaveForm = new AddLeaveForm();
		LeavePreference leavePreferenceVO = null;
		
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId,
				employeeId, companyId);
		if(leaveApplication == null)
		{
			addLeaveForm=null;
			throw new PayAsiaSystemException("Authentication Exception");
		}
		else
		{
			leavePreferenceVO = leavePreferenceDAO.findByCompanyId(leaveApplication.getCompany().getCompanyId());
		}
		if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
			addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
			addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
		} else {
			addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
		}
		addLeaveForm.setPreApprovalReq(leaveApplication.getPreApprovalRequest());

		addLeaveForm.setNoOfDays(new BigDecimal(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
		Boolean isAdminPostedLeave = false;
		List<LeaveApplicationWorkflow> leaveApplicationWorkflows = new ArrayList<>(
				leaveApplication.getLeaveApplicationWorkflows());
		Collections.sort(leaveApplicationWorkflows, new LeaveApplicationWorkflowComp());
		if (leaveApplication.getEmployee().getEmployeeId() != leaveApplicationWorkflows.get(0).getEmployee()
				.getEmployeeId()) {
			isAdminPostedLeave = true;
		}

		Boolean isWithdrawn = false;
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplication.getLeaveApplicationWorkflows()) {
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
				isWithdrawn = true;
			}
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
				isWithdrawn = true;
			}
		}

		addLeaveForm.setIsWithdrawn(isWithdrawn);

		if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves()
				.size() > 0) {
			String remarks = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveSchemeTypeAvailingLeaves().iterator().next().getRemarks();
			addLeaveForm.setRemarks(remarks);
		}

		List<LeaveApplicationCustomField> leaveApplicationCustomFields = new ArrayList<>(
				leaveApplication.getLeaveApplicationCustomFields());
		Collections.sort(leaveApplicationCustomFields, new LeaveSchemeTypeCusFieldComp());
		List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = new ArrayList<>();
		for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationCustomFields) {

			LeaveCustomFieldDTO customField = new LeaveCustomFieldDTO();
			customField.setValue(leaveApplicationCustomField.getValue());
			customField.setCustomFieldId(leaveApplicationCustomField.getLeaveApplicationCustomFieldId());
			customField.setCustomFieldTypeId(
					leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getCustomFieldId());
			customField.setCustomFieldName(leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getFieldName());
			leaveCustomFieldDTOs.add(customField);
		}

		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication.getEmployeeLeaveSchemeType();
		addLeaveForm.setCustomFieldDTOList(leaveCustomFieldDTOs);
		addLeaveForm
				.setEmployeeLeaveSchemeId(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId());
		
		addLeaveForm.setLeaveSchemeId(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getLeaveSchemeId());
		addLeaveForm.setLeaveScheme(
				leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getSchemeName());
		addLeaveForm.setLeaveTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
		addLeaveForm.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveTypeMaster().getLeaveTypeName());
		addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
		addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
		addLeaveForm.setReason(leaveApplication.getReason());
		addLeaveForm.setEmailCC(leaveApplication.getEmailCC());

		if (leaveApplication.getLeaveSessionMaster1() != null) {
			addLeaveForm.setFromSession(leaveApplication.getLeaveSessionMaster1().getSession());
			addLeaveForm.setFromSessionId(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
		}

		if (leaveApplication.getLeaveSessionMaster2() != null) {
			addLeaveForm.setToSession(leaveApplication.getLeaveSessionMaster2().getSession());
			addLeaveForm.setToSessionId(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
		}

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication.getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee().getEmail());
				if (isAdminPostedLeave) {
					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationWorkflows.get(0).getEmployee()));
					
				} else {
					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				}

				addLeaveForm.setApplyTo(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setApplyToEmail(leaveApplicationReviewer.getEmployee().getEmail());
				addLeaveForm.setApplyToId(leaveApplicationReviewer.getEmployee().getEmployeeId());

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
				addLeaveForm.setLeaveReviewer2(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
				addLeaveForm.setLeaveReviewer3(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
			}
		}

		addLeaveForm.setTotalNoOfReviewers(leaveApplication.getLeaveApplicationReviewers().size());
		if (isAdminPostedLeave) {
			addLeaveForm.setLeaveAppEmp(getEmployeeName(leaveApplicationWorkflows.get(0).getEmployee()));
		} else {
			addLeaveForm.setLeaveAppEmp(getEmployeeName(leaveApplication.getEmployee()));
		}
		addLeaveForm.setLeaveAppByEmp(getEmployeeName(leaveApplication.getEmployee()));
		
		addLeaveForm.setLeaveAppCreated(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
		addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
		addLeaveForm.setLeaveAppRemarks(leaveApplication.getReason());
		if (leaveApplication.getLeaveStatusMaster().getLeaveStatusName()
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
			addLeaveForm.setLeaveAppStatus("payasia.withdrawn");
		} else {
			addLeaveForm.setLeaveAppStatus("payasia.submitted");
		}

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<LeaveApplicationWorkflowDTO>();
		Integer workFlowCount = 0;

		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplicationWorkflows) {
			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();
			if (addLeaveForm.getApplyToId() != null
					&& addLeaveForm.getApplyToId() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null
					&& addLeaveForm.getLeaveReviewer2Id() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null
					&& addLeaveForm.getLeaveReviewer3Id() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			workFlowCount++;

			if (workFlowCount != 1 || isAdminPostedLeave) {
				applicationWorkflowDTO
						.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationWorkflow.getCreatedDate()));
			applicationWorkflowDTO.setCreatedDateM(leaveApplicationWorkflow.getCreatedDate());
			applicationWorkflowDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationWorkflow.getCreatedDate()));
			//applicationWorkflowDTO.setCreatedDate(DateUtils.timeStampToStringForCalendar(leaveApplicationWorkflow.getCreatedDate(),UserContext.getWorkingCompanyDateFormat()));
				applicationWorkflowDTO.setEmployeeInfo(getEmployeeName(leaveApplicationWorkflow.getEmployee()));
				applicationWorkflowDTO.setEmoployeeInfoId(leaveApplicationWorkflow.getEmployee().getEmployeeId());
				applicationWorkflowDTO.setUserRemarks(leaveApplicationWorkflow.getLeaveApplication().getReason());
				applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow.getRemarks());
				if (leaveApplication.getLeaveStatusMaster().getLeaveStatusName()
						.equals(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN));
				} else if (isAdminPostedLeave) {
					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(PayAsiaConstants.LEAVE_STATUS_COMPLETED));
				} else {

					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(
							leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()));
				}
			}

			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

		for (LeaveApplicationAttachment leaveApplicationAttachment : leaveApplication
				.getLeaveApplicationAttachments()) {
			LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			applicationAttachmentDTO.setFileName(leaveApplicationAttachment.getFileName());

			/* ID ENCRYPT*/
			applicationAttachmentDTO
					.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplicationAttachment.getLeaveApplicationAttachmentId()));
			leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
		}

		addLeaveForm.setAttachmentList(leaveApplicationAttachmentDTOs);
		addLeaveForm.setLeaveReviewer2Id(null);
		addLeaveForm.setLeaveReviewer3Id(null);
		return addLeaveForm;
	}
	
	@Override
	public void deleteAttachment(Long attachmentId,Long empId,Long companyId) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		LeaveApplicationAttachment applicationAttachment = leaveApplicationAttachmentDAO.findAttachmentByEmployeeCompanyId(attachmentId, empId, companyId);
		
		if(applicationAttachment!=null){
			if(!empId.equals(applicationAttachment.getLeaveApplication().getEmployee().getEmployeeId()) && !companyId.equals(applicationAttachment.getLeaveApplication().getCompany().getCompanyId())){
				throw new PayAsiaSystemException("Authentication Exception");
			}
		}
		boolean success = true;
		try {
			String fileExt = applicationAttachment.getFileType();

			filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					applicationAttachment.getLeaveApplication().getCompany().getCompanyId(),
					PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
					String.valueOf(applicationAttachment.getLeaveApplicationAttachmentId()), null, null, fileExt,
					PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, 0);

			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * String filePath = "/company/" +
			 * applicationAttachment.getLeaveApplication().getCompany()
			 * .getCompanyId() + "/" +
			 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
			 * applicationAttachment.getLeaveApplicationAttachmentId() + "." +
			 * fileExt;
			 */

			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
			} else {
				FileUtils.deletefile(filePath);
			}

		} catch (Exception exception) {
			success = false;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		if (success == true) {
			// delete attachment
			leaveApplicationAttachmentDAO.delete(applicationAttachment);
		}
	}
	
	@Override
	public String withdrawLeave(Long leaveApplicationId, Long empId, LeaveSessionDTO sessionDTO, Long companyId) {

		LeaveApplication leaveApplication =leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId,empId, companyId);
		if (leaveApplication == null) {
			return "ERROR";
		}
		Employee loggedInEmployee = employeeDAO.findById(empId);

		List<LeaveApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				leaveApplication.getLeaveApplicationWorkflows());

		for (LeaveApplicationWorkflow leaveApplicationWorkflow : applicationWorkflows) {
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
				return "ERROR";
			}
		}

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplication.getCompany().getCompanyId());
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN);
		Date date = new Date();
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplicationDAO.update(leaveApplication);

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication.getLeaveApplicationReviewers()) {
			leaveApplicationReviewer.setPending(false);
			leaveApplicationReviewerDAO.update(leaveApplicationReviewer);
		}

		LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
		leaveApplicationWorkflow.setLeaveApplication(leaveApplication);
		leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplicationWorkflow.setRemarks(leaveApplication.getReason());
		leaveApplicationWorkflow.setForwardTo(leaveApplication.getApplyTo());
		leaveApplicationWorkflow.setEmailCC(leaveApplication.getEmailCC());
		leaveApplicationWorkflow.setEmployee(leaveApplication.getEmployee());
		leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		leaveApplicationWorkflow.setStartDate(leaveApplication.getStartDate());
		leaveApplicationWorkflow.setEndDate(leaveApplication.getEndDate());
		leaveApplicationWorkflow.setTotalDays(leaveApplication.getTotalDays());
		leaveApplicationWorkflow.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
		leaveApplicationWorkflow.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());

		leaveApplicationWorkflow = leaveApplicationWorkflowDAO.saveReturn(leaveApplicationWorkflow);

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(companyId, empId, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		generalMailLogic.sendWithdrawEmailForLeave(
				leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
				leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_WITHDRAW, totalLeaveDays,
				leaveBalance.getLeaveBalance(), loggedInEmployee, sessionDTO, isLeaveUnitDays);

		return "SUCCESS";
	}
	
	@Override
	public void deleteLeave(long leaveApplicationId, long empId, long companyId) {
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId,
				empId, companyId);
		if (leaveApplication == null) {
			throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_UNAUTHORIZED_ACCESS, PayAsiaConstants.PAYASIA_UNAUTHORIZED_ACCESS);
		}
		Set<LeaveApplicationAttachment> applicationAttachmentSet = leaveApplication.getLeaveApplicationAttachments();
		for (LeaveApplicationAttachment applicationAttachment : applicationAttachmentSet) {
			deleteAttachment(applicationAttachment.getLeaveApplicationAttachmentId());
		}

		leaveApplicationDAO.delete(leaveApplication);

	}

	@Override
	public LeaveApplicationAttachmentDTO viewAttachment(Long attachmentId,Long empId,Long companyId) {
		LeaveApplicationAttachment attachment = leaveApplicationAttachmentDAO.findAttachmentByEmployeeCompanyId(attachmentId, companyId);
		if(attachment == null)
		{
			return null;
		}
		
		LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
		
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		
		// applicationAttachmentDTO.setAttachmentBytes(attachment.getAttachment());
		applicationAttachmentDTO.setFileName(attachment.getFileName());
		applicationAttachmentDTO.setFileType(attachment.getFileType());

		String fileExt = attachment.getFileType();

		filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				attachment.getLeaveApplication().getCompany().getCompanyId(),
				PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
				String.valueOf(attachment.getLeaveApplicationAttachmentId()), null, null, fileExt,
				PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		/*
		 * String filePath = "/company/" +
		 * attachment.getLeaveApplication().getCompany().getCompanyId() + "/" +
		 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
		 * attachment.getLeaveApplicationAttachmentId() + "." + fileExt;
		 */
		applicationAttachmentDTO.setAttachmentPath(filePath);

		File file = new File(filePath);
		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				byte[] byteFile = org.apache.commons.io.IOUtils
						.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
				applicationAttachmentDTO.setAttachmentBytes(byteFile);
			} else {
				applicationAttachmentDTO.setAttachmentBytes(Files.readAllBytes(file.toPath()));
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return applicationAttachmentDTO;
	}
	
	@Override
	public String extensionLeaveViewAdmin(Long leaveApplicationId, Long empId, Long companyId) {

		LeaveApplication leaveApplication =null;// leaveApplicationDAO.findLeaveApplicationByCompanyId(leaveApplicationId, companyId);
		JSONObject jsonObject = new JSONObject();
		if (leaveApplication == null) {
			return jsonObject.toString();
		}
		String startDate = DateUtils.timeStampToString(leaveApplication.getStartDate());
		String endDate = DateUtils.timeStampToString(leaveApplication.getEndDate());

		Date newStartDate = null;
		if (leaveApplication.getLeaveSessionMaster2().getLeaveSessionId() == 1) {
			jsonObject.put("currentStartDate", endDate);
			jsonObject.put("currentStartSession", 2);
		} else if (leaveApplication.getLeaveSessionMaster2().getLeaveSessionId() == 2) {
			newStartDate = DateUtils.addDays(leaveApplication.getEndDate(), 1);
			String newStartDateStr = DateUtils.timeStampToString(DateUtils.convertDateToTimeStamp(newStartDate));
			jsonObject.put("currentStartDate", newStartDateStr);
			jsonObject.put("currentStartSession", 1);
		}

		List<LeaveApplicationExtensionDetails> applicationExtensionDetails = new ArrayList<>(
				leaveApplication.getLeaveApplicationExtensionDetails());
		BeanComparator beanComparator = new BeanComparator("leaveApplicationExtensionDetailsId");
		Collections.sort(applicationExtensionDetails, beanComparator);

		if (applicationExtensionDetails != null && !applicationExtensionDetails.isEmpty()) {

			JSONArray jsonArray = new JSONArray();
			int count = 0;
			for (LeaveApplicationExtensionDetails extensionDetails : applicationExtensionDetails) {

				JSONObject json = new JSONObject();
				json.put("sno", ++count);
				json.put("startDate", DateUtils.timeStampToString(extensionDetails.getFromDate()));
				json.put("endDate", DateUtils.timeStampToString(extensionDetails.getToDate()));
				json.put("startSession", "Session " + extensionDetails.getLeaveSessionMaster1().getLeaveSessionId());
				json.put("endSession", "Session " + extensionDetails.getLeaveSessionMaster2().getLeaveSessionId());
				json.put("reason", extensionDetails.getRemarks());
				jsonArray.add(json);
			}
			jsonObject.put("extensionDetails", jsonArray);

		} else {
			JSONArray jsonArray = new JSONArray();

			JSONObject json = new JSONObject();
			json.put("sno", 1);
			json.put("startDate", startDate);
			json.put("endDate", endDate);
			json.put("startSession", "Session " + leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			json.put("endSession", "Session " + leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			json.put("reason", leaveApplication.getReason());
			jsonArray.add(json);

			jsonObject.put("extensionDetails", jsonArray);
		}

		return jsonObject.toString();
	}

	@Override
	public void deleteLeave(Long leaveApplicationId, EmployeeDTO employeeDTO) {
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId, employeeDTO.getEmployeeId(), employeeDTO.getCompanyId());
		Set<LeaveApplicationAttachment> applicationAttachmentSet = leaveApplication.getLeaveApplicationAttachments();
		for (LeaveApplicationAttachment applicationAttachment : applicationAttachmentSet) {
			deleteAttachment(applicationAttachment.getLeaveApplicationAttachmentId());
		}

		leaveApplicationDAO.delete(leaveApplication);
		
	}

	@Override
	public LeaveApplicationAttachmentDTO viewAttachmentMob(Long attachmentId, Long companyId, Long employeeId) {
		FilePathGeneratorDTO filePathGenerator = new FilePathGeneratorDTO();
		LeaveApplicationAttachment attachment = leaveApplicationAttachmentDAO.findAttachmentByEmployeeCompanyId(attachmentId, employeeId, companyId);
		LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
		// applicationAttachmentDTO.setAttachmentBytes(attachment.getAttachment());
		applicationAttachmentDTO.setFileName(attachment.getFileName());
		applicationAttachmentDTO.setFileType(attachment.getFileType());

		String fileExt = attachment.getFileType();

		filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
				attachment.getLeaveApplication().getCompany().getCompanyId(),
				PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME,
				String.valueOf(attachment.getLeaveApplicationAttachmentId()), null, null, fileExt,
				PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT, 0);

		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		/*
		 * String filePath = "/company/" +
		 * attachment.getLeaveApplication().getCompany().getCompanyId() + "/" +
		 * PayAsiaConstants.LEAVE_ATTACHMENT_DIRECTORY_NAME + "/" +
		 * attachment.getLeaveApplicationAttachmentId() + "." + fileExt;
		 */
		applicationAttachmentDTO.setAttachmentPath(filePath);

		File file = new File(filePath);
		try {
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				byte[] byteFile = org.apache.commons.io.IOUtils
						.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
				applicationAttachmentDTO.setAttachmentBytes(byteFile);
			} else {
				applicationAttachmentDTO.setAttachmentBytes(Files.readAllBytes(file.toPath()));
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return applicationAttachmentDTO;
	}

	@Override
	public String withdrawLeaveMobile(Long leaveApplicationId, Long employeeId, LeaveSessionDTO sessionDTO,
			Long companyId) {
		Employee loggedInEmployee = employeeDAO.findById(employeeId);
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId, employeeId, companyId);
		List<LeaveApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				leaveApplication.getLeaveApplicationWorkflows());

		for (LeaveApplicationWorkflow leaveApplicationWorkflow : applicationWorkflows) {
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
				return "ERROR";
			}
		}

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplication.getCompany().getCompanyId());
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN);
		Date date = new Date();
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplicationDAO.update(leaveApplication);

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication.getLeaveApplicationReviewers()) {
			leaveApplicationReviewer.setPending(false);
			leaveApplicationReviewerDAO.update(leaveApplicationReviewer);
		}

		LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
		leaveApplicationWorkflow.setLeaveApplication(leaveApplication);
		leaveApplicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplicationWorkflow.setRemarks(leaveApplication.getReason());
		leaveApplicationWorkflow.setForwardTo(leaveApplication.getApplyTo());
		leaveApplicationWorkflow.setEmailCC(leaveApplication.getEmailCC());
		leaveApplicationWorkflow.setEmployee(leaveApplication.getEmployee());
		leaveApplicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		leaveApplicationWorkflow.setStartDate(leaveApplication.getStartDate());
		leaveApplicationWorkflow.setEndDate(leaveApplication.getEndDate());
		leaveApplicationWorkflow.setTotalDays(leaveApplication.getTotalDays());
		leaveApplicationWorkflow.setStartSessionMaster(leaveApplication.getLeaveSessionMaster1());
		leaveApplicationWorkflow.setEndSessionMaster(leaveApplication.getLeaveSessionMaster2());

		leaveApplicationWorkflow = leaveApplicationWorkflowDAO.saveReturn(leaveApplicationWorkflow);

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(
					leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = getNoOfDays(null, null, leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		AddLeaveForm leaveBalance = getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());

		generalMailLogic.sendWithdrawEmailForLeave(
				leaveApplicationWorkflow.getLeaveApplication().getEmployee().getCompany().getCompanyId(),
				leaveApplicationWorkflow, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_WITHDRAW, totalLeaveDays,
				leaveBalance.getLeaveBalance(), loggedInEmployee, sessionDTO, isLeaveUnitDays);

		return "SUCCESS";
		
	}

	@Override
	public AddLeaveForm viewLeave(Long leaveApplicationId, EmployeeDTO employeeDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId, employeeDTO.getEmployeeId(), employeeDTO.getCompanyId());

		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(leaveApplication.getCompany().getCompanyId());
		if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
			addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
			addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
		} else {
			addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
		}
		addLeaveForm.setPreApprovalReq(leaveApplication.getPreApprovalRequest());

		addLeaveForm.setNoOfDays(new BigDecimal(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
		Boolean isAdminPostedLeave = false;
		List<LeaveApplicationWorkflow> leaveApplicationWorkflows = new ArrayList<>(
				leaveApplication.getLeaveApplicationWorkflows());
		Collections.sort(leaveApplicationWorkflows, new LeaveApplicationWorkflowComp());
		if (leaveApplication.getEmployee().getEmployeeId() != leaveApplicationWorkflows.get(0).getEmployee()
				.getEmployeeId()) {
			isAdminPostedLeave = true;
		}

		Boolean isWithdrawn = false;
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplication.getLeaveApplicationWorkflows()) {
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
				isWithdrawn = true;
			}
			if (leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
				isWithdrawn = true;
			}
		}

		addLeaveForm.setIsWithdrawn(isWithdrawn);

		if (leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveSchemeTypeAvailingLeaves()
				.size() > 0) {
			String remarks = leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveSchemeTypeAvailingLeaves().iterator().next().getRemarks();
			addLeaveForm.setRemarks(remarks);
		}

		List<LeaveApplicationCustomField> leaveApplicationCustomFields = new ArrayList<>(
				leaveApplication.getLeaveApplicationCustomFields());
		Collections.sort(leaveApplicationCustomFields, new LeaveSchemeTypeCusFieldComp());
		List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = new ArrayList<>();
		for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationCustomFields) {

			LeaveCustomFieldDTO customField = new LeaveCustomFieldDTO();
			customField.setValue(leaveApplicationCustomField.getValue());
			customField.setCustomFieldId(leaveApplicationCustomField.getLeaveApplicationCustomFieldId());
			customField.setCustomFieldTypeId(
					leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getCustomFieldId());
			customField.setCustomFieldName(leaveApplicationCustomField.getLeaveSchemeTypeCustomField().getFieldName());
			leaveCustomFieldDTOs.add(customField);
		}

		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication.getEmployeeLeaveSchemeType();
		addLeaveForm.setCustomFieldDTOList(leaveCustomFieldDTOs);
		addLeaveForm
				.setEmployeeLeaveSchemeId(employeeLeaveSchemeType.getEmployeeLeaveScheme().getEmployeeLeaveSchemeId());
		addLeaveForm.setLeaveSchemeId(
				leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getLeaveSchemeId());
		addLeaveForm.setLeaveScheme(
				leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveScheme().getSchemeName());
		addLeaveForm.setLeaveTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
		addLeaveForm.setLeaveType(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveTypeMaster().getLeaveTypeName());
		addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
		addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
		addLeaveForm.setReason(leaveApplication.getReason());
		addLeaveForm.setEmailCC(leaveApplication.getEmailCC());

		if (leaveApplication.getLeaveSessionMaster1() != null) {
			addLeaveForm.setFromSession(leaveApplication.getLeaveSessionMaster1().getSession());
			addLeaveForm.setFromSessionId(leaveApplication.getLeaveSessionMaster1().getLeaveSessionId());
		}

		if (leaveApplication.getLeaveSessionMaster2() != null) {
			addLeaveForm.setToSession(leaveApplication.getLeaveSessionMaster2().getSession());
			addLeaveForm.setToSessionId(leaveApplication.getLeaveSessionMaster2().getLeaveSessionId());
		}

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplication.getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee().getEmail());
				if (isAdminPostedLeave) {
					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationWorkflows.get(0).getEmployee()));
				} else {
					addLeaveForm.setLeaveReviewer1(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				}

				addLeaveForm.setApplyTo(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setApplyToEmail(leaveApplicationReviewer.getEmployee().getEmail());
				addLeaveForm.setApplyToId(leaveApplicationReviewer.getEmployee().getEmployeeId());

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
				addLeaveForm.setLeaveReviewer2(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
				addLeaveForm.setLeaveReviewer3(getEmployeeName(leaveApplicationReviewer.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer.getEmployee().getEmployeeId());
			}
		}

		addLeaveForm.setTotalNoOfReviewers(leaveApplication.getLeaveApplicationReviewers().size());
		if (isAdminPostedLeave) {
			addLeaveForm.setLeaveAppEmp(getEmployeeName(leaveApplicationWorkflows.get(0).getEmployee()));
		} else {
			addLeaveForm.setLeaveAppEmp(getEmployeeName(leaveApplication.getEmployee()));
		}
		addLeaveForm.setLeaveAppByEmp(getEmployeeName(leaveApplication.getEmployee()));
		addLeaveForm.setLeaveAppCreated(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
		addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
		addLeaveForm.setLeaveAppRemarks(leaveApplication.getReason());
		if (leaveApplication.getLeaveStatusMaster().getLeaveStatusName()
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
			addLeaveForm.setLeaveAppStatus("payasia.withdrawn");
		} else {
			addLeaveForm.setLeaveAppStatus("payasia.submitted");
		}

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<LeaveApplicationWorkflowDTO>();
		Integer workFlowCount = 0;

		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplicationWorkflows) {
			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();
			if (addLeaveForm.getApplyToId() != null
					&& addLeaveForm.getApplyToId() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null
					&& addLeaveForm.getLeaveReviewer2Id() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null
					&& addLeaveForm.getLeaveReviewer3Id() == leaveApplicationWorkflow.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			workFlowCount++;

			if (workFlowCount != 1 || isAdminPostedLeave) {
				applicationWorkflowDTO
						.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationWorkflow.getCreatedDate()));
				applicationWorkflowDTO.setCreatedDateM(leaveApplicationWorkflow.getCreatedDate());
				applicationWorkflowDTO.setEmployeeInfo(getEmployeeName(leaveApplicationWorkflow.getEmployee()));
				applicationWorkflowDTO.setUserRemarks(leaveApplicationWorkflow.getLeaveApplication().getReason());
				applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow.getRemarks());
				if (leaveApplication.getLeaveStatusMaster().getLeaveStatusName()
						.equals(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN));
				} else if (isAdminPostedLeave) {
					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(PayAsiaConstants.LEAVE_STATUS_COMPLETED));
				} else {

					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(
							leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()));
				}
			}

			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

		for (LeaveApplicationAttachment leaveApplicationAttachment : leaveApplication
				.getLeaveApplicationAttachments()) {
			LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			applicationAttachmentDTO.setFileName(leaveApplicationAttachment.getFileName());
			applicationAttachmentDTO
					.setLeaveApplicationId(leaveApplicationAttachment.getLeaveApplicationAttachmentId());
			leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
		}

		addLeaveForm.setAttachmentList(leaveApplicationAttachmentDTOs);
		return addLeaveForm;
	}

	@Override
	public AddLeaveFormResponse getAllLeaveRequestData(String fromDate, String toDate, Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String pageContextPath, String searchCondition, String searchText,Long companyId) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(empId);
		if (StringUtils.isNotBlank(fromDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}

		if (StringUtils.isNotBlank(toDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_LEAVE_TYPE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveTypeName("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_1) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer1("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_2) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_REVIEWER_3) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveReviewer3("%" + searchText.trim() + "%");

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setCreatedDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_FROM_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveAppfromDate(searchText.trim());

		}
		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TO_DATE) && StringUtils.isNotBlank(searchText)) {

			conditionDTO.setLeaveApptoDate(searchText.trim());

		}

		//int recordSize = (leaveApplicationDAO.getCountForConditionAllLeave(conditionDTO)).intValue();

		List<LeaveApplication> pendingLeaves = leaveApplicationDAO.findByConditionForAllLeaveRequest(conditionDTO, null, null);
		LeavePreference leavePreferenceVO = leavePreferenceDAO.findByCompanyId(companyId);
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		for (LeaveApplication leaveApplication : pendingLeaves) {
			String reviewer1Status = "";
			String reviewer2Status = "";
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm.setLeaveScheme(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveScheme().getSchemeName());

			StringBuilder leaveType = new StringBuilder();
			leaveType.append(leaveApplication.getEmployeeLeaveSchemeType().getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			if (leavePreferenceVO != null && leavePreferenceVO.getLeaveUnit() != null) {
				addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit().getCodeDesc());
				addLeaveForm.setLeavePreferencePreApproval(leavePreferenceVO.isPreApprovalRequired());
			} else {
				addLeaveForm.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
			}
			addLeaveForm.setLeaveType(String.valueOf(leaveType));
			addLeaveForm.setNoOfDays(BigDecimal.valueOf(leaveApplication.getTotalDays()).setScale(2, BigDecimal.ROUND_HALF_UP));
			addLeaveForm.setCreateDate(DateUtils.timeStampToStringWithTime(leaveApplication.getCreatedDate()));
			addLeaveForm.setCreateDateM(leaveApplication.getCreatedDate());
			addLeaveForm.setFromDate(DateUtils.timeStampToString(leaveApplication.getStartDate()));
			addLeaveForm.setToDate(DateUtils.timeStampToString(leaveApplication.getEndDate()));
			addLeaveForm.setRequestType(leaveApplication.getLeaveStatusMaster().getLeaveStatusName());
			addLeaveForm.setLeaveApplicationId(leaveApplication.getLeaveApplicationId());
			List<LeaveApplicationReviewer> applicationReviewers = new ArrayList<>(
					leaveApplication.getLeaveApplicationReviewers());
			Collections.sort(applicationReviewers, new LeaveReviewerComp());
			for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewers) {
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());

					if (applicationWorkflow == null) {

						addLeaveForm.setLeaveReviewer1(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
								pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToStringWithTime(applicationWorkflow.getCreatedDate())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

					} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
							.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						StringBuilder leaveReviewer1 = new StringBuilder(
								getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED, pageContextPath,
										leaveApplicationReviewer.getEmployee()));

						leaveReviewer1.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
								+ DateUtils.timeStampToStringWithTime(applicationWorkflow.getCreatedDate())
								+ "</span>");

						addLeaveForm.setLeaveReviewer1(String.valueOf(leaveReviewer1));

						reviewer1Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;

					}
				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());

					if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)
							|| reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						addLeaveForm.setLeaveReviewer2(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

						reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
					} else if (reviewer1Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_PENDING;
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer2 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer2.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addLeaveForm.setLeaveReviewer2(String.valueOf(leaveReviewer2));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_APPROVED;

						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

							addLeaveForm.setLeaveReviewer2(getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED,
									pageContextPath, leaveApplicationReviewer.getEmployee()));

							reviewer2Status = PayAsiaConstants.LEAVE_STATUS_REJECTED;

						}

					}

				}
				if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					LeaveApplicationWorkflow applicationWorkflow = leaveApplicationWorkflowDAO.findByCondition(
							leaveApplication.getLeaveApplicationId(),
							leaveApplicationReviewer.getEmployee().getEmployeeId());
					if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)
							|| reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

						addLeaveForm.setLeaveReviewer3(
								getStatusImage("NA", pageContextPath, leaveApplicationReviewer.getEmployee()));

					} else if (reviewer2Status.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

						if (applicationWorkflow == null) {
							addLeaveForm.setLeaveReviewer3(getStatusImage(PayAsiaConstants.LEAVE_STATUS_PENDING,
									pageContextPath, leaveApplicationReviewer.getEmployee()));
						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_APPROVED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						} else if (applicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()
								.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {

							StringBuilder leaveReviewer3 = new StringBuilder(
									getStatusImage(PayAsiaConstants.LEAVE_STATUS_REJECTED, pageContextPath,
											leaveApplicationReviewer.getEmployee()));

							leaveReviewer3.append("<br><span class='TextsmallLeave' style='padding-top: 5px;'>"
									+ applicationWorkflow.getCreatedDate() + "</span>");

							addLeaveForm.setLeaveReviewer3(String.valueOf(leaveReviewer3));

						}

					}

				}

			}

			addLeaveFormList.add(addLeaveForm);
		}

		//Collections.sort(addLeaveFormList, new LeaveCreationDateComp());
		//Collections.sort(addLeaveFormList, new AddLeaveFormRequestTypeSort());
		
		if (sortDTO != null  && !StringUtils.isEmpty(sortDTO.getColumnName()) && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList, new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		AddLeaveFormResponse response = new AddLeaveFormResponse();
		sortLeaveRequestData(response,sortDTO ,pageDTO, addLeaveFormList);
		return response;
	}
	
	private void sortLeaveRequestData(AddLeaveFormResponse response, SortCondition sortDTO, PageRequest pageDTO,
			List<AddLeaveForm> addLeaveFormList) {
		if (sortDTO != null && !addLeaveFormList.isEmpty()) {
			Collections.sort(addLeaveFormList,
					new EmployeeLeaveComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		List<AddLeaveForm> finalAddLeaveFormList = new ArrayList<AddLeaveForm>();

		if (pageDTO != null && !addLeaveFormList.isEmpty()) {
			int recordSizeFinal = addLeaveFormList.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;

			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				endPos = startPos + pageSize;
			} else if (startPos <= recordSizeFinal) {
				endPos = recordSizeFinal;
			}

			finalAddLeaveFormList = addLeaveFormList.subList(startPos, endPos);
			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSizeFinal);
		}
		response.setRows(finalAddLeaveFormList);

	}
	

}
	


class LeaveSchemeCustomFieldComparator implements Comparator<LeaveCustomFieldDTO> {

	@Override
	public int compare(LeaveCustomFieldDTO leaveCustomFieldDTO1, LeaveCustomFieldDTO leaveCustomFieldDTO2) {
		return leaveCustomFieldDTO1.getCustomFieldTypeId().compareTo(leaveCustomFieldDTO2.getCustomFieldTypeId());
	}
}
	class AddLeaveFormRequestTypeSort implements Comparator<AddLeaveForm> {

		@Override
		public int compare(AddLeaveForm addLeaveForm1, AddLeaveForm addLeaveForm2) {
		
			return addLeaveForm1.getRequestType().compareTo(addLeaveForm2.getRequestType());
		}

		
}
