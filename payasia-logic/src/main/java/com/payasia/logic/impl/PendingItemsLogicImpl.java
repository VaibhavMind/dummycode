package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeLeaveDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationPdfDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveConditionDTO;
import com.payasia.common.dto.LeaveCustomFieldDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveReviewFormDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.PendingLeaveDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.PendingItemsFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeLeaveTransactionComparison;
import com.payasia.common.util.EmployeePendingItemsComparison;
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
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.EmployeeLeaveSchemeTypeHistoryDAO;
import com.payasia.dao.KeyPayIntLeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationAttachmentDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.LeaveApplicationWorkflowDAO;
import com.payasia.dao.LeavePreferenceDAO;
import com.payasia.dao.LeaveSchemeTypeAvailingLeaveDAO;
import com.payasia.dao.LeaveSessionMasterDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeDefaultEmailCC;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory;
import com.payasia.dao.bean.IntegrationMaster;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveApplicationCustomField;
import com.payasia.dao.bean.LeaveApplicationReviewer;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeavePreference;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSchemeWorkflow;
import com.payasia.dao.bean.LeaveSessionMaster;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.ModuleMaster;
import com.payasia.dao.bean.NotificationAlert;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.KeyPayIntLogic;
import com.payasia.logic.LeaveApplicationPrintPDFLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.PendingItemsLogic;

@Component
public class PendingItemsLogicImpl implements PendingItemsLogic {
	private static final Logger LOGGER = Logger
			.getLogger(AddLeaveLogicImpl.class);

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	LeaveApplicationPrintPDFLogic leaveApplicationPrintPDFLogic;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewerDAO;

	@Resource
	LeaveStatusMasterDAO leaveStatusMasterDAO;

	@Resource
	LeaveApplicationWorkflowDAO applicationWorkflowDAO;

	@Resource
	LeaveApplicationDAO leaveApplicationDAO;

	@Resource
	LeaveSessionMasterDAO leaveSessionMasterDAO;

	@Resource
	AddLeaveLogic addLeaveLogic;

	@Resource
	GeneralMailLogic generalMailLogic;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;

	@Resource
	EmployeeLeaveSchemeTypeHistoryDAO employeeLeaveSchemeTypeHistoryDAO;

	@Resource
	LeavePreferenceDAO leavePreferenceDAO;
	@Resource
	CompanyDAO companyDAO;

	@Resource
	ModuleMasterDAO moduleMasterDAO;

	@Resource
	NotificationAlertDAO notificationAlertDAO;
	@Autowired
	private MessageSource messageSource;
	@Resource
	LeaveSchemeTypeAvailingLeaveDAO leaveSchemeTypeAvailingLeaveDAO;
	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	@Resource
	EmployeeDefaultEmailCCDAO employeeDefaultEmailCCDAO;
	@Resource
	KeyPayIntLogic keyPayIntLogic;
	
	@Resource
	LeaveApplicationAttachmentDAO leaveApplicationAttachmentDAO;

	@Resource
	FileUtils fileUtils;
	
	@Resource
	KeyPayIntLeaveApplicationDAO keyPayIntLeaveApplicationDAO;
	
	@Resource
	AWSS3Logic awss3LogicImpl;
	
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;
	
	@Resource
	EmployeeDetailLogic employeeDetailLogic;


	@Override
	public List<AppCodeDTO> getWorkflowTypeList() {
		List<AppCodeDTO> workflowTypes = new ArrayList<AppCodeDTO>();
		List<AppCodeMaster> appCodes = appCodeMasterDAO
				.findByConditionPendingItems(PayAsiaConstants.APP_CODE_WORKFLOW_TYPE);
		for (AppCodeMaster appCodeMaster : appCodes) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setCodeValue(appCodeMaster.getCodeValue());
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			workflowTypes.add(appCodeDTO);
		}

		return workflowTypes;
	}


	@Override
	public PendingItemsFormResponse getPendingLeaves(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			String workflowTypeValue, String searchCondition, String searchText) {

		PendingLeaveDTO pendingLeaveDTO = new PendingLeaveDTO();

		if (workflowTypeValue.equals(PayAsiaConstants.LEAVE_TYPE_SEARCH_ALL)) {
			pendingLeaveDTO
					.setLeaveType(PayAsiaConstants.LEAVE_TYPE_SEARCH_ALL);
		} else {
			AppCodeMaster appCodeMaster = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_WORKFLOW_TYPE, workflowTypeValue);
			pendingLeaveDTO.setLeaveType(appCodeMaster.getCodeDesc());
		}

		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_CREATED_BY)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setPendingEmployeeName("%" + searchText.trim()
						+ "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_STATUS)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setStatus("%" + searchText.trim() + "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_FROM_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setFromDate(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_TO_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setToDate(searchText.trim());
			}

		}

		List<LeaveApplicationReviewer> leaveReviewerList = null;
//		int recordSize = (leaveApplicationReviewerDAO.getCountForCondition(empId)).intValue();
		leaveReviewerList = leaveApplicationReviewerDAO.findByConditionLeaveType(empId, null, null, pendingLeaveDTO);

		List<PendingItemsForm> pendingItemsForms = new ArrayList<PendingItemsForm>();

		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveReviewerList) {
			PendingItemsForm pendingItemsForm = new PendingItemsForm();
			/* ID ENCRYPT*/
		
			pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(leaveApplicationReviewer
							.getLeaveApplicationReviewerId()));
			getActionAndBuutonConfiguraton(pendingItemsForm,empId);
			pendingItemsForm
					.setCreatedBy(getEmployeeNameWithNumber(leaveApplicationReviewer
							.getLeaveApplication().getEmployee()));
			pendingItemsForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(leaveApplicationReviewer
							.getLeaveApplication().getCreatedDate()));
			pendingItemsForm.setFromDate(DateUtils
					.timeStampToString(leaveApplicationReviewer
							.getLeaveApplication().getStartDate()));
			pendingItemsForm.setToDate(DateUtils
					.timeStampToString(leaveApplicationReviewer
							.getLeaveApplication().getEndDate()));
			pendingItemsForm.setDays(BigDecimal.valueOf(leaveApplicationReviewer.getLeaveApplication().getTotalDays()));

			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
				pendingItemsForm.setStatus("payasia.approved");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				pendingItemsForm.setStatus("payasia.approved.cancel");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_BALANCE)) {
				pendingItemsForm.setStatus("payasia.balance");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_ENCHASED)) {
				pendingItemsForm.setStatus("payasia.encashed");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CARRIED_FORWARD)) {
				pendingItemsForm.setStatus("payasia.carried.forward");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED)) {
				pendingItemsForm.setStatus("payasia.submitted");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_WITHDRAWN)) {
				pendingItemsForm.setStatus("payasia.withdrawn");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_REJECTED)) {
				pendingItemsForm.setStatus("payasia.rejected");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED_CANCEL)) {
				pendingItemsForm.setStatus("payasia.submitted.cancel");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				pendingItemsForm.setStatus("payasia.approved.cancel");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CREDITED)) {
				pendingItemsForm.setStatus("payasia.credited");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED)) {
				pendingItemsForm.setStatus("payasia.forfeited");
			}
			pendingItemsForm.setStatus("payasia.pending");
			pendingItemsForm.setAction(true);
			pendingItemsForm.setMultiAction(true);
			pendingItemsForms.add(pendingItemsForm);
		}
		

		List<PendingItemsForm> finalPendingItemsForms = new ArrayList<PendingItemsForm>();
		PendingItemsFormResponse response = new PendingItemsFormResponse();
		
		if (sortDTO != null && !pendingItemsForms.isEmpty()) {
			Collections.sort(pendingItemsForms, new EmployeePendingItemsComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		if (pageDTO != null && !pendingItemsForms.isEmpty()) {
			int recordSizeFinal = pendingItemsForms.size();
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

			finalPendingItemsForms = pendingItemsForms.subList(startPos, endPos);
			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSizeFinal);
		}
		response.setRows(finalPendingItemsForms);
		return response;
	}
	
	private void getActionAndBuutonConfiguraton(PendingItemsForm pendingItemsForm,Long employeeId){
		int reviewOrder = 0;
		
		Long leaveApplicationReviewerId  = FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId());
		LeaveApplicationReviewer applicationReviewer = leaveApplicationReviewerDAO
				.getLeaveApplicationReviewerDetail(leaveApplicationReviewerId,employeeId);
				AddLeaveForm addLeaveForm = new AddLeaveForm();
				String allowOverride = "";
				String allowReject = "";
				String allowApprove = "";
				String allowForward = "";
				for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : applicationReviewer
						.getLeaveApplication().getEmployeeLeaveSchemeType()
						.getLeaveSchemeType().getLeaveSchemeTypeWorkflows()) {
					if (leaveSchemeTypeWorkflow
							.getWorkFlowRuleMaster()
							.getRuleName()
							.equalsIgnoreCase(
									PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
						allowOverride = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
								.getRuleValue();

					} else if (leaveSchemeTypeWorkflow
							.getWorkFlowRuleMaster()
							.getRuleName()
							.equalsIgnoreCase(
									PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
						allowReject = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
								.getRuleValue();
					} else if (leaveSchemeTypeWorkflow
							.getWorkFlowRuleMaster()
							.getRuleName()
							.equalsIgnoreCase(
									PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
						allowForward = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
								.getRuleValue();
					} else if (leaveSchemeTypeWorkflow
							.getWorkFlowRuleMaster()
							.getRuleName()
							.equalsIgnoreCase(
									PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
						allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
								.getRuleValue();
					}

				}
		for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewer
				.getLeaveApplication().getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee()
						.getEmail());
				addLeaveForm
						.setLeaveReviewer1(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setApplyToId(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 1;
					if (allowOverride.length() == 3
							&& allowOverride.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}
			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				addLeaveForm
						.setLeaveReviewer2(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());

				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				addLeaveForm
						.setLeaveReviewer3(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}
			}
		}
		
		
	}
	
	@Override
	public PendingItemsFormResponse getPendingLeavesForAdmin(Long sessionEmpId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String workflowTypeValue, String searchCondition, String searchText) {

		PendingLeaveDTO pendingLeaveDTO = new PendingLeaveDTO();

		if (workflowTypeValue.equals(PayAsiaConstants.LEAVE_TYPE_SEARCH_ALL)) {
			pendingLeaveDTO
					.setLeaveType(PayAsiaConstants.LEAVE_TYPE_SEARCH_ALL);
		} else {
			AppCodeMaster appCodeMaster = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_WORKFLOW_TYPE, workflowTypeValue);
			pendingLeaveDTO.setLeaveType(appCodeMaster.getCodeDesc());
		}

		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_CREATED_BY)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setPendingEmployeeName("%" + searchText.trim()
						+ "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setCreatedDate(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_FROM_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setFromDate(searchText.trim());
			}

		}
		if (searchCondition.equals(PayAsiaConstants.PENDING_ITEMS_TO_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				pendingLeaveDTO.setToDate(searchText.trim());
			}

		}
		List<String> leaveStatusNames = new ArrayList<>();
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
		leaveStatusNames.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

		/* Check employee shortlist */
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(sessionEmpId, companyId);
		List<BigInteger> companyShortListEmployeeIds = employeeShortListDTO
				.getShortListEmployeeIds();

		List<LeaveApplicationReviewer> leaveReviewerList = new ArrayList<>();
		int recordSize = 0;
		recordSize = (leaveApplicationReviewerDAO.getCountForConditionForAdmin(
				null, companyId, pendingLeaveDTO)).intValue();
		leaveReviewerList = leaveApplicationReviewerDAO
				.findByConditionLeaveTypeForAdmin(null, companyId, null,
						sortDTO, leaveStatusNames, pendingLeaveDTO);

		List<PendingItemsForm> pendingItemsForms = new ArrayList<PendingItemsForm>();
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveReviewerList) {
			if (employeeShortListDTO.getEmployeeShortList()
					&& !companyShortListEmployeeIds.contains(BigInteger
							.valueOf(leaveApplicationReviewer
									.getLeaveApplication().getEmployee()
									.getEmployeeId()))) {
				continue;
			}
			PendingItemsForm pendingItemsForm = new PendingItemsForm();
			/* ID ENCRYPT*/
			pendingItemsForm
					.setLeaveApplicationReviewerId(
							FormatPreserveCryptoUtil.encrypt(leaveApplicationReviewer
							.getLeaveApplicationReviewerId()));
			pendingItemsForm
					.setCreatedBy(getEmployeeNameWithNumber(leaveApplicationReviewer
							.getLeaveApplication().getEmployee()));
			pendingItemsForm.setCreatedById(leaveApplicationReviewer.getLeaveApplication().getEmployee().getEmployeeId());
			pendingItemsForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(leaveApplicationReviewer
							.getLeaveApplication().getCreatedDate()));
			pendingItemsForm.setStatus(leaveApplicationReviewer
					.getLeaveApplication().getLeaveStatusMaster()
					.getLeaveStatusName());
			pendingItemsForm.setFromDate(DateUtils
					.timeStampToString(leaveApplicationReviewer
							.getLeaveApplication().getStartDate()));
			pendingItemsForm.setToDate(DateUtils
					.timeStampToString(leaveApplicationReviewer
							.getLeaveApplication().getEndDate()));
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)) {
				pendingItemsForm.setStatus("payasia.approved");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				pendingItemsForm.setStatus("payasia.approved.cancel");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_BALANCE)) {
				pendingItemsForm.setStatus("payasia.balance");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_ENCHASED)) {
				pendingItemsForm.setStatus("payasia.encashed");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CARRIED_FORWARD)) {
				pendingItemsForm.setStatus("payasia.carried.forward");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED)) {
				pendingItemsForm.setStatus("payasia.submitted");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_WITHDRAWN)) {
				pendingItemsForm.setStatus("payasia.withdrawn");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_REJECTED)) {
				pendingItemsForm.setStatus("payasia.rejected");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_SUBMITTED_CANCEL)) {
				pendingItemsForm.setStatus("payasia.submitted.cancel");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
				pendingItemsForm.setStatus("payasia.approved.cancel");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_CREDITED)) {
				pendingItemsForm.setStatus("payasia.credited");
			}
			if (leaveApplicationReviewer
					.getLeaveApplication()
					.getLeaveStatusMaster()
					.getLeaveStatusName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_FORFEITED)) {
				pendingItemsForm.setStatus("payasia.forfeited");
			}
			if (leaveApplicationReviewer.getLeaveApplication()
					.getLeaveStatusMaster().getLeaveStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
				pendingItemsForm.setStatus("payasia.completed");
			}
			pendingItemsForm.setStatus("payasia.pending");
			pendingItemsForms.add(pendingItemsForm);
		}

		PendingItemsFormResponse response = new PendingItemsFormResponse();
		response.setRows(pendingItemsForms);
		// if (pageDTO != null) {
		//
		// int pageSize = pageDTO.getPageSize();
		// int totalPages = recordSize / pageSize;
		//
		// if (recordSize % pageSize != 0) {
		// totalPages = totalPages + 1;
		// }
		// if (recordSize == 0) {
		// pageDTO.setPageNumber(0);
		// }
		//
		// response.setPage(pageDTO.getPageNumber());
		// response.setTotal(totalPages);
		// response.setRecords(recordSize);
		// }
		return response;
	}

	@Override
	public PendingItemsForm getDataForLeaveReview(Long reviewId) {

		PendingItemsForm pendingItemsForm = new PendingItemsForm();
		LeaveApplicationReviewer applicationReviewer = leaveApplicationReviewerDAO
				.findById(reviewId);

		AddLeaveForm addLeaveForm = new AddLeaveForm();

		List<LeaveApplicationCustomField> leaveApplicationCustomFields = new ArrayList<>(
				applicationReviewer.getLeaveApplication()
						.getLeaveApplicationCustomFields());
		Collections.sort(leaveApplicationCustomFields,
				new LeaveSchemeTypeCusFieldComp());
		List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = new ArrayList<>();
		for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationCustomFields) {

			LeaveCustomFieldDTO customField = new LeaveCustomFieldDTO();
			customField.setValue(leaveApplicationCustomField.getValue());
			customField.setCustomFieldId(leaveApplicationCustomField
					.getLeaveApplicationCustomFieldId());
			customField.setCustomFieldTypeId(leaveApplicationCustomField
					.getLeaveSchemeTypeCustomField().getCustomFieldId());
			customField.setCustomFieldName(leaveApplicationCustomField
					.getLeaveSchemeTypeCustomField().getFieldName());
			leaveCustomFieldDTOs.add(customField);
		}
		addLeaveForm.setCustomFieldDTOList(leaveCustomFieldDTOs);

		// Set Leave unit
		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(applicationReviewer.getLeaveApplication()
						.getCompany().getCompanyId());
		if (leavePreferenceVO != null
				&& leavePreferenceVO.getLeaveUnit() != null) {
			addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit()
					.getCodeDesc());
		} else {
			addLeaveForm
					.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
		}

		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				applicationReviewer.getLeaveApplication()
						.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());

		addLeaveForm.setLeaveBalance(leaveBalance.getLeaveBalance());
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(applicationReviewer.getLeaveApplication()
						.getCompany().getCompanyId());

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils
					.timeStampToString(applicationReviewer
							.getLeaveApplication().getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(applicationReviewer
					.getLeaveApplication().getEndDate()));
			leaveDTO.setSession1(applicationReviewer.getLeaveApplication()
					.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(applicationReviewer.getLeaveApplication()
					.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(applicationReviewer
					.getLeaveApplication().getEmployeeLeaveSchemeType()
					.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, null,
					leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(applicationReviewer
					.getLeaveApplication().getTotalDays()).setScale(2,
					BigDecimal.ROUND_HALF_UP);
		}
		addLeaveForm.setNoOfDays(totalLeaveDays);
		addLeaveForm.setLeaveScheme(applicationReviewer.getLeaveApplication()
				.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveScheme().getSchemeName());
		addLeaveForm.setLeaveType(applicationReviewer.getLeaveApplication()
				.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveTypeMaster().getLeaveTypeName());
		addLeaveForm.setFromDate(DateUtils
				.timeStampToString(applicationReviewer.getLeaveApplication()
						.getStartDate()));
		addLeaveForm.setToDate(DateUtils.timeStampToString(applicationReviewer
				.getLeaveApplication().getEndDate()));
        /* ID ENCRYPT*/
		addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(applicationReviewer
				.getLeaveApplication().getLeaveApplicationId()));
		addLeaveForm.setReason(applicationReviewer.getLeaveApplication()
				.getReason());
		addLeaveForm.setStatus(setStatusMultilingualKey(applicationReviewer
				.getLeaveApplication().getLeaveStatusMaster()
				.getLeaveStatusName()));
		addLeaveForm.setStatusId(applicationReviewer.getLeaveApplication()
				.getLeaveStatusMaster().getLeaveStatusID());

		if (applicationReviewer.getLeaveApplication().getLeaveSessionMaster1() != null) {
			addLeaveForm.setFromSession(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1()
					.getSession());
			addLeaveForm.setFromSessionLabelKey(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1()
					.getSessionLabelKey());
			addLeaveForm.setFromSessionId(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1()
					.getLeaveSessionId());
		}

		if (applicationReviewer.getLeaveApplication().getLeaveSessionMaster2() != null) {
			addLeaveForm.setToSession(applicationReviewer.getLeaveApplication()
					.getLeaveSessionMaster2().getSession());
			addLeaveForm.setToSessionLabelKey(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2()
					.getSessionLabelKey());
			addLeaveForm.setToSessionId(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2()
					.getLeaveSessionId());
		}

		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : applicationReviewer
				.getLeaveApplication().getEmployeeLeaveSchemeType()
				.getLeaveSchemeType().getLeaveSchemeTypeWorkflows()) {
			if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
				allowOverride = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();

			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
				allowReject = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
				allowForward = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			}

		}
		int reviewOrder = 0;
		for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewer
				.getLeaveApplication().getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee()
						.getEmail());
				addLeaveForm
						.setLeaveReviewer1(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setApplyToId(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 1;
					if (allowOverride.length() == 3
							&& allowOverride.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}
			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				addLeaveForm
						.setLeaveReviewer2(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());

				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				addLeaveForm
						.setLeaveReviewer3(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}
			}
		}

		List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

		for (LeaveApplicationAttachment leaveApplicationAttachment : applicationReviewer
				.getLeaveApplication().getLeaveApplicationAttachments()) {
			LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			applicationAttachmentDTO.setFileName(leaveApplicationAttachment
					.getFileName());
			/* ID ENCRYPT*/
			applicationAttachmentDTO
					.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplicationAttachment
							.getLeaveApplicationAttachmentId()));
			leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
		}

		addLeaveForm.setAttachmentList(leaveApplicationAttachmentDTOs);
		addLeaveForm.setTotalNoOfReviewers(applicationReviewer
				.getLeaveApplication().getLeaveApplicationReviewers().size());

		addLeaveForm.setLeaveAppEmp(getEmployeeName(applicationReviewer
				.getLeaveApplication().getEmployee()));
		addLeaveForm.setLeaveAppCreated(DateUtils
				.timeStampToStringWithTime(applicationReviewer
						.getLeaveApplication().getCreatedDate()));
		addLeaveForm.setLeaveAppRemarks(applicationReviewer
				.getLeaveApplication().getReason());
		addLeaveForm.setLeaveAppStatus(setStatusMultilingualKey("Submitted"));

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<LeaveApplicationWorkflowDTO>();
		Integer workFlowCount = 0;
		List<LeaveApplicationWorkflow> leaveApplicationWorkflows = new ArrayList<>(
				applicationReviewer.getLeaveApplication()
						.getLeaveApplicationWorkflows());
		Collections.sort(leaveApplicationWorkflows,
				new LeaveApplicationWorkflowComp());
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplicationWorkflows) {

			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();

			workFlowCount++;

			if (workFlowCount != 1) {
				applicationWorkflowDTO.setCreatedDate(DateUtils
						.timeStampToStringWithTime(leaveApplicationWorkflow
								.getCreatedDate()));
				applicationWorkflowDTO
						.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationWorkflow
								.getEmployee()));
				applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow
						.getRemarks());

				applicationWorkflowDTO
						.setStatus(setStatusMultilingualKey(leaveApplicationWorkflow
								.getLeaveStatusMaster().getLeaveStatusName()));
				applicationWorkflowDTO.setCreatedDate(DateUtils
						.timeStampToStringWithTime(leaveApplicationWorkflow
								.getCreatedDate()));
			}

			if (leaveApplicationWorkflow.getEmployee().getEmployeeId() == applicationReviewer
					.getLeaveApplication().getEmployee().getEmployeeId()) {

				applicationWorkflowDTO.setOrder(0);

			}

			if (addLeaveForm.getApplyToId() != null
					&& addLeaveForm.getApplyToId() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null
					&& addLeaveForm.getLeaveReviewer2Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null
					&& addLeaveForm.getLeaveReviewer3Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		pendingItemsForm
				.setCreatedBy(getEmployeeNameWithNumber(applicationReviewer
						.getLeaveApplication().getEmployee()));
		pendingItemsForm.setCreatedById(applicationReviewer
				.getLeaveApplication().getEmployee().getEmployeeId());
		if (reviewOrder == 1 && addLeaveForm.getLeaveReviewer2Id() != null) {
			Employee empReviewer2 = employeeDAO.findById(addLeaveForm
					.getLeaveReviewer2Id());
			pendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer2));
			pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.encrypt(empReviewer2.getEmployeeId()));
		} else if (reviewOrder == 2
				&& addLeaveForm.getLeaveReviewer3Id() != null) {
			Employee empReviewer3 = employeeDAO.findById(addLeaveForm
					.getLeaveReviewer3Id());
			pendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer3));
			pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.encrypt(empReviewer3.getEmployeeId()));
		} else {
			pendingItemsForm.setForwardTo("");
		}

		pendingItemsForm
				.setTypeOfApplication(generalLogic
						.getTypeOfApplication(applicationReviewer
								.getLeaveApplication()));

		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		pendingItemsForm.setAddLeaveForm(addLeaveForm);
		return pendingItemsForm;
	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class LeaveSchemeTypeCusFieldComp implements
			Comparator<LeaveApplicationCustomField> {
		public int compare(LeaveApplicationCustomField templateField,
				LeaveApplicationCustomField compWithTemplateField) {
			if (templateField.getLeaveSchemeTypeCustomField()
					.getCustomFieldId() > compWithTemplateField
					.getLeaveSchemeTypeCustomField().getCustomFieldId()) {
				return 1;
			} else if (templateField.getLeaveSchemeTypeCustomField()
					.getCustomFieldId() < compWithTemplateField
					.getLeaveSchemeTypeCustomField().getCustomFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	public String setStatusMultilingualKey(String leaveStatusName) {
		if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)) {
			return "payasia.approved";
		} else if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
			return "payasia.submitted";
		} else if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_WITHDRAWN)) {
			return "payasia.withdrawn";
		} else if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_REJECTED)) {
			return "payasia.rejected";
		} else if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_CANCELLED)) {
			return "payasia.cancelled";
		} else if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_COMPLETED)) {
			return "payasia.completed";
		} else if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_DRAFT)) {
			return "payasia.draft";
		} else if (leaveStatusName
				.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_PENDING)) {
			return "payasia.pending";
		}
		return leaveStatusName;

	}

	/**
	 * Comparator Class for Ordering LeaveSchemeType List
	 */
	private class LeaveApplicationWorkflowComp implements
			Comparator<LeaveApplicationWorkflow> {
		public int compare(LeaveApplicationWorkflow templateField,
				LeaveApplicationWorkflow compWithTemplateField) {
			if (templateField.getLeaveApplicationWorkflowID() > compWithTemplateField
					.getLeaveApplicationWorkflowID()) {
				return -1;
			} else if (templateField.getLeaveApplicationWorkflowID() < compWithTemplateField
					.getLeaveApplicationWorkflowID()) {
				return 1;
			}
			return 0;

		}

	}

	@Override
	public AddLeaveForm acceptLeave(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();

		Date date = new Date();
		LeaveApplicationReviewer leaveApplicationReviewer = leaveApplicationReviewerDAO
				.findById(pendingItemsForm.getLeaveApplicationReviewerId());
		LeaveApplicationWorkflow applicationWorkflow = new LeaveApplicationWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		String workflowLevel = "";

		LeaveApplication leaveApplication = new LeaveApplication();
		try {
			BeanUtils.copyProperties(leaveApplication,
					leaveApplicationReviewer.getLeaveApplication());
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if(!isSameCompanyGroupExist(leaveApplication.getLeaveApplicationId(),leaveApplicationReviewer.getCompanyId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		if(pendingItemsForm.getLeaveApplicationId() != null   && !pendingItemsForm.getLeaveApplicationId().equals(leaveApplication.getLeaveApplicationId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplication.getCompany().getCompanyId());

		// For KeyPay
		// Check status of leave application for cancellation
		if (!isLeaveUnitDays
				&& leaveApplication.getLeaveCancelApplication() != null) {
			boolean leaveCancelStatus = keyPayIntLogic
					.checkLeaveStatusForCancellation(leaveApplication
							.getLeaveCancelApplication()
							.getLeaveApplicationId(), leaveApplication
							.getCompany().getCompanyId());
			if (!leaveCancelStatus) {
				LeaveDTO leaveDTO = new LeaveDTO();
				leaveDTO.setErrorCode(1);
				leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_CANCEL_VALIDATION_KEY);
				leaveDTO.setErrorValue(" ;");
				addLeaveForm.setLeaveDTO(leaveDTO);
				return addLeaveForm;
			}
		}

		List<String> leaveApprovedStatusList = new ArrayList<>();
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		if (leaveApprovedStatusList.contains(leaveApplication
				.getLeaveStatusMaster().getLeaveStatusName())) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.LEAVE_APPLICATION_ALREADY_APPROVED);
			leaveDTO.setErrorValue(" ;");
			addLeaveForm.setLeaveDTO(leaveDTO);
			return addLeaveForm;
		}

		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		if (!isLeaveUnitDays) {
			pendingItemsForm.setFromSessionId(1l);
			pendingItemsForm.setToSessionId(2l);
			// Hours between Dates
			leaveConditionDTO.setTotalHoursBetweenDates(leaveApplication
					.getTotalDays());
			leaveConditionDTO.setLeaveUnitHours(true);
		}

		leaveConditionDTO.setEmployeeId(leaveApplication.getEmployee()
				.getEmployeeId());
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(leaveApplication
				.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		leaveConditionDTO.setEmployeeLeaveApplicationId(leaveApplication
				.getLeaveApplicationId());
		leaveConditionDTO.setStartDate(DateUtils
				.timeStampToString(leaveApplication.getStartDate()));
		leaveConditionDTO.setEndDate(DateUtils
				.timeStampToString(leaveApplication.getEndDate()));
		leaveConditionDTO.setStartSession(leaveApplication
				.getLeaveSessionMaster1().getLeaveSessionId());
		leaveConditionDTO.setEndSession(leaveApplication
				.getLeaveSessionMaster2().getLeaveSessionId());
		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setReviewer(true);
		leaveConditionDTO.setAttachementStatus(true);

		if (leaveApplication.getLeaveCancelApplication() == null) {
			LeaveDTO leaveDTOValidate = leaveApplicationDAO
					.validateLeaveApplication(leaveConditionDTO);
			addLeaveForm.setLeaveDTO(leaveDTOValidate);
			if (leaveDTOValidate.getErrorCode() == 1) {
				return addLeaveForm;
			}
			if (leaveDTOValidate.getErrorCode() == 0) {
				
				
				addLeaveForm.setLeaveDTO(null);
				
			}
		}

		for (LeaveSchemeWorkflow leaveSchemeWorkflow : leaveApplicationReviewer
				.getLeaveApplication().getEmployeeLeaveSchemeType()
				.getLeaveSchemeType().getLeaveScheme()
				.getLeaveSchemeWorkflows()) {
			if (leaveSchemeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
				workflowLevel = leaveSchemeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();

			}
		}
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		if (pendingItemsForm.isCanOverride()) {
			leaveApplication.setStartDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getFromDate()));
			leaveApplication.setEndDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getToDate()));

			if (pendingItemsForm.getFromSessionId() != null) {
				LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
						.findById(pendingItemsForm.getFromSessionId());
				leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);
			}
			if (pendingItemsForm.getToSessionId() != null) {
				LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
						.findById(pendingItemsForm.getToSessionId());
				leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
			}

		}
		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplicationDAO.update(leaveApplication);

		leaveApplicationReviewer.setPending(false);
		leaveApplicationReviewer.setEmployee(employee);
		leaveApplicationReviewerDAO.update(leaveApplicationReviewer);

		for (LeaveApplicationReviewer leaveAppReviewer : leaveApplication
				.getLeaveApplicationReviewers()) {
			leaveAppReviewer.setPending(false);
			leaveApplicationReviewerDAO.update(leaveAppReviewer);
		}
		applicationWorkflow.setEmployee(employee);

		String emailCC = StringUtils.removeEnd(pendingItemsForm.getEmailCC(),
				";");
		ModuleMaster moduleMaster = moduleMasterDAO
				.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO
				.findByCondition(leaveApplication.getCompany().getCompanyId(),
						leaveApplication.getEmployee().getEmployeeId(),
						moduleMaster.getModuleId());
		if (employeeDefaultEmailCCVO != null) {
			if (StringUtils.isNotBlank(emailCC)) {
				emailCC += ";";
			}
			emailCC += employeeDefaultEmailCCVO.getEmailCC();
		}
		applicationWorkflow.setEmailCC(emailCC);
		applicationWorkflow.setLeaveApplication(leaveApplicationReviewer
				.getLeaveApplication());
		applicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
		applicationWorkflow.setRemarks(pendingItemsForm.getRemarks());

		if (pendingItemsForm.isCanOverride()) {
			applicationWorkflow.setStartDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getFromDate()));
			applicationWorkflow.setEndDate(DateUtils
					.stringToTimestamp(pendingItemsForm.getToDate()));
		} else {
			applicationWorkflow.setStartDate(leaveApplicationReviewer
					.getLeaveApplication().getStartDate());
			applicationWorkflow.setEndDate(leaveApplicationReviewer
					.getLeaveApplication().getEndDate());
		}

		applicationWorkflow.setStartSessionMaster(leaveApplicationReviewer
				.getLeaveApplication().getLeaveSessionMaster1());
		applicationWorkflow.setEndSessionMaster(leaveApplicationReviewer
				.getLeaveApplication().getLeaveSessionMaster2());
		applicationWorkflow.setTotalDays(leaveApplicationReviewer
				.getLeaveApplication().getTotalDays());

		applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		LeaveApplicationWorkflow leaveApplicationWorkflow = applicationWorkflowDAO
				.saveReturn(applicationWorkflow);

		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication
				.getEmployeeLeaveSchemeType();
		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication
					.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication
					.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
					.getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
					.getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType
					.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, employeeId,
					leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays())
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		// Insert Forfeit leave entry in EmployeeLeaveSchemeTypeHistory
		// table, only if current leave taken by Consider Leave Balance from
		// Other leave
		if (leaveApplication.getLeaveCancelApplication() == null) {
			leaveBalanceSummaryLogic.forfeitFromOtherLeaveType(
					leaveApplication, employeeLeaveSchemeType
							.getLeaveSchemeType().getLeaveSchemeTypeId());
		} else {
			leaveBalanceSummaryLogic.deleteForfeitFromOtherLeaveType(
					leaveApplication, employeeLeaveSchemeType
							.getLeaveSchemeType().getLeaveSchemeTypeId());
		}

		EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
		employeeLeaveSchemeTypeHistory
				.setEmployeeLeaveSchemeType(leaveApplication
						.getEmployeeLeaveSchemeType());
		AppCodeMaster appcodeTaken = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
				PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED);
		employeeLeaveSchemeTypeHistory.setAppCodeMaster(appcodeTaken);
		employeeLeaveSchemeTypeHistory.setLeaveApplication(leaveApplication);
		LeaveStatusMaster leaveStatusForHistory = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		if (leaveApplication.getLeaveCancelApplication() != null) {
			leaveStatusForHistory = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
		} else {
			leaveStatusForHistory = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}
		employeeLeaveSchemeTypeHistory
				.setLeaveStatusMaster(leaveStatusForHistory);
		employeeLeaveSchemeTypeHistory.setStartDate(leaveApplication
				.getStartDate());
		employeeLeaveSchemeTypeHistory
				.setEndDate(leaveApplication.getEndDate());
		employeeLeaveSchemeTypeHistory.setDays(BigDecimal
				.valueOf(leaveApplication.getTotalDays()));
		employeeLeaveSchemeTypeHistory.setReason(leaveApplication.getReason());
		employeeLeaveSchemeTypeHistory.setStartSessionMaster(leaveApplication
				.getLeaveSessionMaster1());
		employeeLeaveSchemeTypeHistory.setEndSessionMaster(leaveApplication
				.getLeaveSessionMaster2());
		employeeLeaveSchemeTypeHistoryDAO.save(employeeLeaveSchemeTypeHistory);

		// Add Leave Application to keyPay Sync table name
		// 'KeyPay_Int_Leave_Application'
		IntegrationMaster integrationMaster =	keyPayIntLeaveApplicationDAO.findByKeyPayDetailByCompanyId(leaveApplication.getCompany().getCompanyId());
		if (integrationMaster!=null && !isLeaveUnitDays) {
				leaveBalanceSummaryLogic.addLeaveAppToKeyPayInt(leaveApplication);
		}

		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());

		if (leaveApplication.getLeaveCancelApplication() != null) {

			LeaveApplication leaveApplicationToBeCancelled = leaveApplication
					.getLeaveCancelApplication();
			LeaveStatusMaster cancelLeaveStatusMaster = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
			leaveApplicationToBeCancelled
					.setLeaveStatusMaster(cancelLeaveStatusMaster);
			leaveApplicationDAO.update(leaveApplicationToBeCancelled);

			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_APPROVE,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);
			

		} else {
			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPROVE,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);
		}

		return addLeaveForm;

	}

	@Override
	public String rejectLeave(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO) {

		Date date = new Date();
		LeaveApplicationReviewer leaveApplicationReviewer = leaveApplicationReviewerDAO
				.findById(pendingItemsForm.getLeaveApplicationReviewerId());
		LeaveApplicationWorkflow applicationWorkflow = new LeaveApplicationWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_REJECTED);
		LeaveApplication leaveApplication = new LeaveApplication();
		try {
			BeanUtils.copyProperties(leaveApplication,
					leaveApplicationReviewer.getLeaveApplication());
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if(!leaveApplicationReviewerDAO.checkLeaveReviewers(leaveApplication,leaveApplicationReviewer.getEmployee().getEmployeeId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		if(!isSameCompanyGroupExist(leaveApplication.getLeaveApplicationId(),leaveApplicationReviewer.getCompanyId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		if(!isSameCompanyGroupExist(leaveApplication.getLeaveApplicationId(),leaveApplicationReviewer.getCompanyId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		if(pendingItemsForm.getLeaveApplicationId() != null   && !pendingItemsForm.getLeaveApplicationId().equals(leaveApplication.getLeaveApplicationId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplication.getCompany().getCompanyId());

		List<String> leaveApprovedStatusList = new ArrayList<>();
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		if (leaveApprovedStatusList.contains(leaveApplication
				.getLeaveStatusMaster().getLeaveStatusName())) {
			return PayAsiaConstants.LEAVE_STATUS_COMPLETED;
		}

		leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
		leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
		leaveApplicationDAO.update(leaveApplication);

		for (LeaveApplicationReviewer applicationReviewer : leaveApplicationReviewer
				.getLeaveApplication().getLeaveApplicationReviewers()) {
			applicationReviewer.setPending(false);
			leaveApplicationReviewerDAO.update(applicationReviewer);
		}

		applicationWorkflow.setEmployee(employee);

		String emailCC = StringUtils.removeEnd(pendingItemsForm.getEmailCC(),
				";");
		ModuleMaster moduleMaster = moduleMasterDAO
				.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
		EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO
				.findByCondition(leaveApplication.getCompany().getCompanyId(),
						leaveApplication.getEmployee().getEmployeeId(),
						moduleMaster.getModuleId());
		if (employeeDefaultEmailCCVO != null) {
			if (StringUtils.isNotBlank(emailCC)) {
				emailCC += ";";
			}
			emailCC += employeeDefaultEmailCCVO.getEmailCC();
		}
		applicationWorkflow.setEmailCC(emailCC);
		applicationWorkflow.setLeaveApplication(leaveApplicationReviewer
				.getLeaveApplication());
		applicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
		applicationWorkflow.setRemarks(pendingItemsForm.getRemarks());
		if (pendingItemsForm.getForwardTo() != null) {
			applicationWorkflow.setForwardTo(pendingItemsForm.getForwardTo());
		}
		applicationWorkflow.setStartDate(leaveApplicationReviewer
				.getLeaveApplication().getStartDate());
		applicationWorkflow.setEndDate(leaveApplicationReviewer
				.getLeaveApplication().getEndDate());
		applicationWorkflow.setStartSessionMaster(leaveApplicationReviewer
				.getLeaveApplication().getLeaveSessionMaster1());
		applicationWorkflow.setEndSessionMaster(leaveApplicationReviewer
				.getLeaveApplication().getLeaveSessionMaster2());

		applicationWorkflow.setTotalDays(leaveApplicationReviewer
				.getLeaveApplication().getTotalDays());
		applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
		LeaveApplicationWorkflow leaveApplicationWorkflow = applicationWorkflowDAO
				.saveReturn(applicationWorkflow);

		EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication
				.getEmployeeLeaveSchemeType();

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils.timeStampToString(leaveApplication
					.getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication
					.getEndDate()));
			leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
					.getLeaveSessionId());
			leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
					.getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType
					.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, employeeId,
					leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays())
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		}

		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				leaveApplication.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());

		if (leaveApplication.getLeaveCancelApplication() != null) {
			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_REJECT,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);
		} else {
			generalMailLogic.sendAcceptRejectMailForLeave(
					leaveApplicationWorkflow.getLeaveApplication()
							.getEmployee().getCompany().getCompanyId(),
					leaveApplicationWorkflow,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_REJECT,
					totalLeaveDays, leaveBalance.getLeaveBalance(), employee,
					pendingItemsForm.getRemarks(), sessionDTO, isLeaveUnitDays);
		}
		return PayAsiaConstants.TRUE;
	}

	@Override
	public AddLeaveForm rejectLeaveForAdmin(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();

		List<String> statusList = new ArrayList<>();
		statusList.add(PayAsiaConstants.LEAVE_STATUS_REJECTED);
		statusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		Date date = new Date();
		LeaveApplicationReviewer leaveApplicationReviewer = leaveApplicationReviewerDAO
				.findById(pendingItemsForm.getLeaveApplicationReviewerId());
		LeaveApplicationWorkflow applicationWorkflow = new LeaveApplicationWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_REJECTED);
		LeaveApplication leaveApplication = new LeaveApplication();
		try {
			BeanUtils.copyProperties(leaveApplication,
					leaveApplicationReviewer.getLeaveApplication());
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if(pendingItemsForm.getLeaveApplicationId() != null   && !pendingItemsForm.getLeaveApplicationId().equals(leaveApplication.getLeaveApplicationId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplication.getCompany().getCompanyId());

		List<String> leaveApprovedStatusList = new ArrayList<>();
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		if (leaveApprovedStatusList.contains(leaveApplication
				.getLeaveStatusMaster().getLeaveStatusName())) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.LEAVE_APPLICATION_ALREADY_APPROVED);
			leaveDTO.setErrorValue(" ;");
			addLeaveForm.setLeaveDTO(leaveDTO);
			return addLeaveForm;
		}

		if (!statusList.contains(leaveApplication.getLeaveStatusMaster()
				.getLeaveStatusName())) {
			leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
			leaveApplicationDAO.update(leaveApplication);

			for (LeaveApplicationReviewer applicationReviewer : leaveApplicationReviewer
					.getLeaveApplication().getLeaveApplicationReviewers()) {

				if (applicationReviewer.getPending()) {
					applicationReviewer.setPending(false);
					applicationReviewer.setEmployee(employee);
					leaveApplicationReviewerDAO.update(applicationReviewer);
				}

			}

			applicationWorkflow.setEmployee(employee);
			applicationWorkflow.setEmailCC(pendingItemsForm.getEmailCC());
			applicationWorkflow.setLeaveApplication(leaveApplicationReviewer
					.getLeaveApplication());
			applicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			applicationWorkflow.setRemarks(pendingItemsForm.getRemarks());
			if (pendingItemsForm.getForwardTo() != null) {
				applicationWorkflow.setForwardTo(pendingItemsForm
						.getForwardTo());
			}
			applicationWorkflow.setStartDate(leaveApplicationReviewer
					.getLeaveApplication().getStartDate());
			applicationWorkflow.setEndDate(leaveApplicationReviewer
					.getLeaveApplication().getEndDate());
			applicationWorkflow.setStartSessionMaster(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1());
			applicationWorkflow.setEndSessionMaster(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2());

			applicationWorkflow.setTotalDays(leaveApplicationReviewer
					.getLeaveApplication().getTotalDays());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			LeaveApplicationWorkflow leaveApplicationWorkflow = applicationWorkflowDAO
					.saveReturn(applicationWorkflow);

			EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication
					.getEmployeeLeaveSchemeType();

			BigDecimal totalLeaveDays = null;
			if (isLeaveUnitDays) {
				LeaveDTO leaveDTO = new LeaveDTO();
				leaveDTO.setFromDate(DateUtils
						.timeStampToString(leaveApplication.getStartDate()));
				leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication
						.getEndDate()));
				leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
						.getLeaveSessionId());
				leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
						.getLeaveSessionId());
				leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType
						.getEmployeeLeaveSchemeTypeId());
				AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null,
						employeeId, leaveDTO);
				totalLeaveDays = noOfDays.getNoOfDays();
			} else {
				// Hours between dates
				totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays())
						.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
			AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null,
					null, leaveApplication.getEmployeeLeaveSchemeType()
							.getEmployeeLeaveSchemeTypeId());

			if (leaveApplication.getLeaveCancelApplication() != null) {
				generalMailLogic
						.sendAcceptRejectMailForLeave(
								leaveApplicationWorkflow.getLeaveApplication()
										.getEmployee().getCompany()
										.getCompanyId(),
								leaveApplicationWorkflow,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_REJECT,
								totalLeaveDays, leaveBalance.getLeaveBalance(),
								employee, pendingItemsForm.getRemarks(),
								sessionDTO, isLeaveUnitDays);
			} else {
				generalMailLogic.sendAcceptRejectMailForLeave(
						leaveApplicationWorkflow.getLeaveApplication()
								.getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_REJECT,
						totalLeaveDays, leaveBalance.getLeaveBalance(),
						employee, pendingItemsForm.getRemarks(), sessionDTO,
						isLeaveUnitDays);
			}
		}
		return addLeaveForm;

	}

	private String getEmployeeNameWithNumber(Employee employee) {
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

	private String getEmployeeName(Employee employee) {
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
	public AddLeaveForm forwardLeave(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		Date date = new Date();
		LeaveApplicationReviewer leaveApplicationReviewer = leaveApplicationReviewerDAO
				.findById(pendingItemsForm.getLeaveApplicationReviewerId());
		LeaveApplicationWorkflow applicationWorkflow = new LeaveApplicationWorkflow();
		Employee forwardToEmployee = null;
		Employee employee = employeeDAO.findById(employeeId);
		String workflowLevel = String.valueOf(leaveApplicationReviewerDAO
				.getLeaveReviewerCount(leaveApplicationReviewer
						.getLeaveApplication().getLeaveApplicationId()));
		if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
				.equalsIgnoreCase(workflowLevel)) {

			addLeaveForm = acceptLeave(pendingItemsForm, employeeId, sessionDTO);

		} else {

			LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			LeaveApplication leaveApplication = new LeaveApplication();
			try {
				BeanUtils.copyProperties(leaveApplication,
						leaveApplicationReviewer.getLeaveApplication());
			} catch (IllegalAccessException | InvocationTargetException e) {
				LOGGER.error(e.getMessage(), e);
			}
			
			if(!leaveApplicationReviewerDAO.checkLeaveReviewers(leaveApplication,leaveApplicationReviewer.getEmployee().getEmployeeId()))
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
			
			if(!isSameCompanyGroupExist(leaveApplication.getLeaveApplicationId(),leaveApplicationReviewer.getCompanyId()))
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
			
			if(pendingItemsForm.getLeaveApplicationId() != null   && !pendingItemsForm.getLeaveApplicationId().equals(leaveApplication.getLeaveApplicationId()))
			{
				throw new PayAsiaSystemException("Authentication Exception");
			}
			
			if (pendingItemsForm.isCanOverride()) {
				leaveApplication.setStartDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getFromDate()));
				leaveApplication.setEndDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getToDate()));

				if (pendingItemsForm.getFromSessionId() != null) {
					LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
							.findById(pendingItemsForm.getFromSessionId());
					leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);
				}
				if (pendingItemsForm.getToSessionId() != null) {
					LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
							.findById(pendingItemsForm.getToSessionId());
					leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
				}

			}
			LeaveStatusMaster leaveStatusCompleted = null;

			leaveStatusCompleted = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_APPROVED);
			leaveApplication.setLeaveStatusMaster(leaveStatusCompleted);

			forwardToEmployee = employeeDAO.findById(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
			applicationWorkflow.setForwardTo(forwardToEmployee.getEmail());

			// Update Leave Approval Pending Status to next level reviewer
			int nextWorkFlowRuleValueLevel = Integer
					.valueOf(leaveApplicationReviewer.getWorkFlowRuleMaster()
							.getRuleValue()) + 1;
			for (LeaveApplicationReviewer leaveAppReviewer : leaveApplication
					.getLeaveApplicationReviewers()) {
				int workFlowRuleValueLevel = Integer.valueOf(leaveAppReviewer
						.getWorkFlowRuleMaster().getRuleValue());
				if (nextWorkFlowRuleValueLevel == workFlowRuleValueLevel) {
					leaveAppReviewer.setPending(true);
					leaveApplicationReviewerDAO.update(leaveAppReviewer);
				}
			}

			leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
			leaveApplicationDAO.update(leaveApplication);

			leaveApplicationReviewer.setPending(false);
			leaveApplicationReviewer.setEmployee(employee);
			leaveApplicationReviewerDAO.update(leaveApplicationReviewer);

			applicationWorkflow.setEmployee(employee);

			String emailCC = StringUtils.removeEnd(
					pendingItemsForm.getEmailCC(), ";");
			ModuleMaster moduleMaster = moduleMasterDAO
					.findByName(PayAsiaConstants.PAYASIA_LEAVE_MODULE_NAME);
			EmployeeDefaultEmailCC employeeDefaultEmailCCVO = employeeDefaultEmailCCDAO
					.findByCondition(leaveApplication.getCompany()
							.getCompanyId(), leaveApplication.getEmployee()
							.getEmployeeId(), moduleMaster.getModuleId());
			if (employeeDefaultEmailCCVO != null) {
				if (StringUtils.isNotBlank(emailCC)) {
					emailCC += ";";
				}
				emailCC += employeeDefaultEmailCCVO.getEmailCC();
			}
			applicationWorkflow.setEmailCC(emailCC);
			applicationWorkflow.setLeaveApplication(leaveApplicationReviewer
					.getLeaveApplication());
			applicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			applicationWorkflow.setRemarks(pendingItemsForm.getRemarks());

			if (pendingItemsForm.isCanOverride()) {
				applicationWorkflow.setStartDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getFromDate()));
				applicationWorkflow.setEndDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getToDate()));
			} else {
				applicationWorkflow.setStartDate(leaveApplicationReviewer
						.getLeaveApplication().getStartDate());
				applicationWorkflow.setEndDate(leaveApplicationReviewer
						.getLeaveApplication().getEndDate());
			}
			applicationWorkflow.setStartSessionMaster(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1());
			applicationWorkflow.setEndSessionMaster(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2());
			applicationWorkflow.setTotalDays(leaveApplicationReviewer
					.getLeaveApplication().getTotalDays());

			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			LeaveApplicationWorkflow leaveApplicationWorkflow = applicationWorkflowDAO
					.saveReturn(applicationWorkflow);

			boolean isLeaveUnitDays = leaveBalanceSummaryLogic
					.isLeaveUnitDays(leaveApplication.getCompany()
							.getCompanyId());

			BigDecimal totalLeaveDays = null;
			if (isLeaveUnitDays) {
				LeaveDTO leaveDTO = new LeaveDTO();
				leaveDTO.setFromDate(DateUtils
						.timeStampToString(leaveApplication.getStartDate()));
				leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication
						.getEndDate()));
				leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
						.getLeaveSessionId());
				leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
						.getLeaveSessionId());
				leaveDTO.setEmployeeLeaveSchemeTypeId(leaveApplication
						.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());
				AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, null,
						leaveDTO);
				totalLeaveDays = noOfDays.getNoOfDays();
			} else {
				// Hours between dates
				totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays())
						.setScale(2, BigDecimal.ROUND_HALF_UP);
			}

			AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null,
					null, leaveApplication.getEmployeeLeaveSchemeType()
							.getEmployeeLeaveSchemeTypeId());

			if (leaveApplication.getLeaveCancelApplication() != null) {

				generalMailLogic
						.sendPendingEmailForLeave(
								leaveApplicationWorkflow.getLeaveApplication()
										.getEmployee().getCompany()
										.getCompanyId(),
								leaveApplicationWorkflow,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_FORWARD,
								totalLeaveDays, leaveBalance.getLeaveBalance(),
								employee, pendingItemsForm.getRemarks(),
								forwardToEmployee, sessionDTO, isLeaveUnitDays);

			} else {

				generalMailLogic.sendPendingEmailForLeave(
						leaveApplicationWorkflow.getLeaveApplication()
								.getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_FORWARD,
						totalLeaveDays, leaveBalance.getLeaveBalance(),
						employee, pendingItemsForm.getRemarks(),
						forwardToEmployee, sessionDTO, isLeaveUnitDays);

			}

			NotificationAlert notificationAlert = new NotificationAlert();
			notificationAlert.setEmployee(forwardToEmployee);
			notificationAlert
					.setMessage(getEmployeeName(leaveApplicationWorkflow
							.getLeaveApplication().getEmployee())
							+ " "
							+ PayAsiaConstants.LEAVEAPPLICATION_NOTIFICATION_ALERT);
			notificationAlert.setModuleMaster(moduleMaster);
			notificationAlert.setShownStatus(false);
			notificationAlertDAO.save(notificationAlert);

		}
		return addLeaveForm;

	}

	@Override
	public PendingItemsFormResponse getEmployeesOnLeave(String fromDate,
			String toDate, Long leaveApplicationId, Long empId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		if(!StringUtils.isEmpty(fromDate)){
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(fromDate));
		}
		if(!StringUtils.isEmpty(toDate)){
			conditionDTO.setToDate(DateUtils.stringToTimestamp(toDate));
		}
		conditionDTO.setEmployeeId(empId);

		Company company = companyDAO.findById(companyId);

		List<String> leaveStatusList = new ArrayList<>();

		LeavePreference leavePreference = leavePreferenceDAO
				.findByCompanyId(companyId);

		if (leavePreference != null) {

			if (leavePreference
					.getLeaveTransactionToShow()
					.getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_BOTH)) {

				leaveStatusList
						.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference
					.getLeaveTransactionToShow()
					.getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_APPROVED)) {
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference
					.getLeaveTransactionToShow()
					.getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_PENDING)) {
				leaveStatusList
						.add(PayAsiaConstants.CLAIM_APPLICATION_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

			}

		} else {
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}
		conditionDTO.setLeaveApplicationId(leaveApplicationId);
		conditionDTO.setLeaveStatusNames(leaveStatusList);
		conditionDTO.setCompanyDateFormat(company.getDateFormat());
		List<LeaveApplication> leaveApplications = leaveApplicationDAO
				.findEmployeesOnLeave(conditionDTO);

		List<EmployeeLeaveDTO> employeeLeaveDTOs = new ArrayList<>();
		if (leaveApplications != null) {
			for (LeaveApplication leaveApplication : leaveApplications) {
				EmployeeLeaveDTO employeeLeaveDTO = new EmployeeLeaveDTO();
				employeeLeaveDTO
						.setEmployeeName(getEmployeeName(leaveApplication
								.getEmployee()));
				employeeLeaveDTO.setEmployeeNumber(leaveApplication
						.getEmployee().getEmployeeNumber());
				employeeLeaveDTO.setFromDate(DateUtils
						.timeStampToStringWOTimezone(leaveApplication.getStartDate(), UserContext.getWorkingCompanyDateFormat()));
				employeeLeaveDTO.setToDate(DateUtils
						.timeStampToStringWOTimezone(leaveApplication.getEndDate(), UserContext.getWorkingCompanyDateFormat()));
				employeeLeaveDTO.setLeaveTypeName(leaveApplication
						.getEmployeeLeaveSchemeType().getLeaveSchemeType()
						.getLeaveTypeMaster().getLeaveTypeName());
				employeeLeaveDTO.setLeaveStatus(leaveApplication.getLeaveStatusMaster().getLeaveStatusName());
				employeeLeaveDTOs.add(employeeLeaveDTO);
			}
		}
		
		if (sortDTO != null && !employeeLeaveDTOs.isEmpty()) {
			Collections.sort(employeeLeaveDTOs, new EmployeeLeaveTransactionComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}
		
		List<EmployeeLeaveDTO> finalLeave = new ArrayList<>();
		PendingItemsFormResponse response = new PendingItemsFormResponse();
		
		/*	PAGINATION	*/
		if (pageDTO != null && !employeeLeaveDTOs.isEmpty()) {
			int recordSizeFinal = employeeLeaveDTOs.size();
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

			finalLeave = employeeLeaveDTOs.subList(startPos, endPos);
			response.setTotal(totalPages);
		}
		response.setPage(pageDTO.getPageNumber());
		response.setRecordSize(employeeLeaveDTOs.size());
		response.setEmployeeLeaveDTOs(finalLeave);

		return response;
	}

	@Override
	public PendingItemsFormResponse getLeaveTransactions(Long createdById,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long leaveApplicationId, String startDate,
			String endDate) {

		AddLeaveConditionDTO conditionDTO = new AddLeaveConditionDTO();
		conditionDTO.setEmployeeId(createdById);

		Company company = companyDAO.findById(companyId);
		if (!StringUtils.isEmpty(startDate)) {
			conditionDTO.setFromDate(DateUtils.stringToTimestamp(startDate,
					company.getDateFormat()));
		}
		if (!StringUtils.isEmpty(endDate)) {
			conditionDTO.setToDate(DateUtils.stringToTimestamp(endDate,
					company.getDateFormat()));
		}

		List<String> leaveStatusList = new ArrayList<>();

		LeavePreference leavePreference = leavePreferenceDAO
				.findByCompanyId(companyId);

		if (leavePreference != null) {

			if (leavePreference
					.getLeaveTransactionToShow()
					.getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_BOTH)) {

				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference
					.getLeaveTransactionToShow()
					.getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_APPROVED)) {
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			} else if (leavePreference
					.getLeaveTransactionToShow()
					.getCodeDesc()
					.equals(PayAsiaConstants.LEAVE_PREFERENCE_SHOW_LEAVE_TRANASACTION_PENDING)) {
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);
				leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_APPROVED);

			}

		} else {
			leaveStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		}

		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		conditionDTO.setLeaveStatusId(leaveStatusMaster.getLeaveStatusID());
		conditionDTO.setLeaveStatusNames(leaveStatusList);
		List<LeaveApplication> leaveApplications = leaveApplicationDAO
				.findEmployeeLeaveTransactions(conditionDTO, null, null,
						leaveApplicationId);
	/*int recordSize = leaveApplicationDAO.getCountEmployeeLeaveTransactions(
				conditionDTO, leaveApplicationId);*/
		List<EmployeeLeaveDTO> employeeLeaveDTOs = new ArrayList<>();
		for (LeaveApplication leaveApplication : leaveApplications) {

			if (leaveApplication.getLeaveApplicationId() == leaveApplicationId) {
				continue;
			}
			EmployeeLeaveDTO employeeLeaveDTO = new EmployeeLeaveDTO();
			employeeLeaveDTO.setEmployeeName(getEmployeeName(leaveApplication
					.getEmployee()));

			employeeLeaveDTO.setEmployeeNumber(leaveApplication.getEmployee()
					.getEmployeeNumber());
			employeeLeaveDTO.setFromDate(DateUtils
					.timeStampToString(leaveApplication.getStartDate()));
			employeeLeaveDTO.setToDate(DateUtils
					.timeStampToString(leaveApplication.getEndDate()));
			employeeLeaveDTO.setLeaveTypeName(leaveApplication
					.getEmployeeLeaveSchemeType().getLeaveSchemeType()
					.getLeaveTypeMaster().getLeaveTypeName());
			if (leaveApplication.getLeaveStatusMaster().getLeaveStatusDesc()
					.equalsIgnoreCase(PayAsiaConstants.LEAVE_STATUS_APPROVED)
					|| leaveApplication
							.getLeaveStatusMaster()
							.getLeaveStatusDesc()
							.equalsIgnoreCase(
									PayAsiaConstants.LEAVE_STATUS_SUBMITTED)) {
				employeeLeaveDTO
						.setLeaveStatus(PayAsiaConstants.LEAVE_STATUS_SUBMITTED);

			} else {
				employeeLeaveDTO.setLeaveStatus(leaveApplication
						.getLeaveStatusMaster().getLeaveStatusName());
			}

			employeeLeaveDTO.setNoOfDays(leaveApplication.getTotalDays());
			employeeLeaveDTOs.add(employeeLeaveDTO);
		}

		int recordSize = employeeLeaveDTOs.size();
		PendingItemsFormResponse response = new PendingItemsFormResponse();
		
		if (sortDTO != null && !employeeLeaveDTOs.isEmpty()) {
			Collections.sort(employeeLeaveDTOs, new EmployeeLeaveTransactionComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		List<EmployeeLeaveDTO> finalEmployeeLeaveDTOs = new ArrayList<>();
		
		if (pageDTO != null && !employeeLeaveDTOs.isEmpty()) {
			int recordSizeFinal = employeeLeaveDTOs.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;
			
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}
			
			if(startPos < recordSizeFinal && ((startPos+pageSize)<=recordSizeFinal)){
				endPos = startPos+pageSize;
			}
			else if(startPos < recordSizeFinal){
				endPos = recordSizeFinal;
			}
		
			finalEmployeeLeaveDTOs = employeeLeaveDTOs.subList(startPos, endPos);
			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);
			response.setRecords(recordSize);
		}
		response.setEmployeeLeaveDTOs(finalEmployeeLeaveDTOs);
		return response;
	}

	@Override
	public AddLeaveForm acceptLeaveforAdmin(PendingItemsForm pendingItemsForm,
			Long employeeId, LeaveSessionDTO sessionDTO) {
		AddLeaveForm addLeaveForm = new AddLeaveForm();

		List<String> statusList = new ArrayList<>();
		statusList.add(PayAsiaConstants.LEAVE_STATUS_REJECTED);
		statusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

		Date date = new Date();
		LeaveApplicationReviewer leaveApplicationReviewer = leaveApplicationReviewerDAO
				.findById(pendingItemsForm.getLeaveApplicationReviewerId());
		LeaveApplicationWorkflow applicationWorkflow = new LeaveApplicationWorkflow();
		Employee employee = employeeDAO.findById(employeeId);
		for (LeaveSchemeWorkflow leaveSchemeWorkflow : leaveApplicationReviewer
				.getLeaveApplication().getEmployeeLeaveSchemeType()
				.getLeaveSchemeType().getLeaveScheme()
				.getLeaveSchemeWorkflows()) {
			if (leaveSchemeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {

			}
		}
		LeaveStatusMaster leaveStatusMaster = leaveStatusMasterDAO
				.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		LeaveApplication leaveApplication = new LeaveApplication();
		try {
			BeanUtils.copyProperties(leaveApplication,
					leaveApplicationReviewer.getLeaveApplication());
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error(e.getMessage(), e);
		}
		if(!leaveApplicationReviewerDAO.checkLeaveReviewers(leaveApplication,leaveApplicationReviewer.getEmployee().getEmployeeId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		if(!isSameCompanyGroupExist(leaveApplication.getLeaveApplicationId(),leaveApplicationReviewer.getCompanyId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}

		if(pendingItemsForm.getLeaveApplicationId() != null   && !pendingItemsForm.getLeaveApplicationId().equals(leaveApplication.getLeaveApplicationId()))
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
			
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(leaveApplication.getCompany().getCompanyId());

		// Validate Leave Application
		LeaveConditionDTO leaveConditionDTO = new LeaveConditionDTO();
		if (!isLeaveUnitDays) {
			pendingItemsForm.setFromSessionId(1l);
			pendingItemsForm.setToSessionId(2l);
			// Hours between Dates
			leaveConditionDTO.setTotalHoursBetweenDates(leaveApplication
					.getTotalDays());
			leaveConditionDTO.setLeaveUnitHours(true);
		}

		leaveConditionDTO.setEmployeeId(leaveApplication.getEmployee()
				.getEmployeeId());
		leaveConditionDTO.setEmployeeLeaveSchemeTypeId(leaveApplication
				.getEmployeeLeaveSchemeType().getEmployeeLeaveSchemeTypeId());
		leaveConditionDTO.setEmployeeLeaveApplicationId(leaveApplication
				.getLeaveApplicationId());
		leaveConditionDTO.setStartDate(DateUtils
				.timeStampToString(leaveApplication.getStartDate()));
		leaveConditionDTO.setEndDate(DateUtils
				.timeStampToString(leaveApplication.getEndDate()));
		leaveConditionDTO.setStartSession(leaveApplication
				.getLeaveSessionMaster1().getLeaveSessionId());
		leaveConditionDTO.setEndSession(leaveApplication
				.getLeaveSessionMaster2().getLeaveSessionId());
		leaveConditionDTO.setPost(false);
		leaveConditionDTO.setReviewer(true);
		leaveConditionDTO.setAttachementStatus(true);

		if (leaveApplication.getLeaveCancelApplication() == null) {
			LeaveDTO leaveDTOValidate = leaveApplicationDAO
					.validateLeaveApplication(leaveConditionDTO);
			addLeaveForm.setLeaveDTO(leaveDTOValidate);
			if (leaveDTOValidate.getErrorCode() == 1) {
				return addLeaveForm;
			}
			if (leaveDTOValidate.getErrorCode() == 0) {
				addLeaveForm.setLeaveDTO(null);
			}
		}

		// For KeyPay
		// Check status of leave application for cancellation
		if (!isLeaveUnitDays
				&& leaveApplication.getLeaveCancelApplication() != null) {
			boolean leaveCancelStatus = keyPayIntLogic
					.checkLeaveStatusForCancellation(leaveApplication
							.getLeaveCancelApplication()
							.getLeaveApplicationId(), leaveApplication
							.getCompany().getCompanyId());
			if (!leaveCancelStatus) {
				LeaveDTO leaveDTO = new LeaveDTO();
				leaveDTO.setErrorCode(1);
				leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_KEYPAY_INT_LEAVE_CANCEL_VALIDATION_KEY);
				leaveDTO.setErrorValue(" ;");
				addLeaveForm.setLeaveDTO(leaveDTO);
				return addLeaveForm;
			}
		}

		List<String> leaveApprovedStatusList = new ArrayList<>();
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
		leaveApprovedStatusList.add(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
		if (leaveApprovedStatusList.contains(leaveApplication
				.getLeaveStatusMaster().getLeaveStatusName())) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.LEAVE_APPLICATION_ALREADY_APPROVED);
			leaveDTO.setErrorValue(" ;");
			addLeaveForm.setLeaveDTO(leaveDTO);
			return addLeaveForm;
		}

		if (!isLeaveUnitDays) {
			pendingItemsForm.setFromSessionId(1l);
			pendingItemsForm.setToSessionId(2l);
		}

		if (!statusList.contains(leaveApplication.getLeaveStatusMaster()
				.getLeaveStatusName())) {
			if (pendingItemsForm.isCanOverride()) {
				leaveApplication.setStartDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getFromDate()));
				leaveApplication.setEndDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getToDate()));

				if (pendingItemsForm.getFromSessionId() != null) {
					LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
							.findById(pendingItemsForm.getFromSessionId());
					leaveApplication.setLeaveSessionMaster1(leaveSessionMaster);
				}
				if (pendingItemsForm.getToSessionId() != null) {
					LeaveSessionMaster leaveSessionMaster = leaveSessionMasterDAO
							.findById(pendingItemsForm.getToSessionId());
					leaveApplication.setLeaveSessionMaster2(leaveSessionMaster);
				}

			}
			leaveApplication.setLeaveStatusMaster(leaveStatusMaster);
			leaveApplication.setUpdatedDate(new Timestamp(date.getTime()));
			leaveApplicationDAO.update(leaveApplication);

			leaveApplicationReviewer.setPending(false);
			leaveApplicationReviewer.setEmployee(employee);
			leaveApplicationReviewerDAO.update(leaveApplicationReviewer);

			for (LeaveApplicationReviewer leaveAppReviewer : leaveApplication
					.getLeaveApplicationReviewers()) {
				leaveAppReviewer.setPending(false);
				leaveApplicationReviewerDAO.update(leaveAppReviewer);
			}

			applicationWorkflow.setEmployee(employee);
			applicationWorkflow.setEmailCC(pendingItemsForm.getEmailCC());
			applicationWorkflow.setLeaveApplication(leaveApplicationReviewer
					.getLeaveApplication());
			applicationWorkflow.setLeaveStatusMaster(leaveStatusMaster);
			applicationWorkflow.setRemarks(pendingItemsForm.getRemarks());

			if (pendingItemsForm.isCanOverride()) {
				applicationWorkflow.setStartDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getFromDate()));
				applicationWorkflow.setEndDate(DateUtils
						.stringToTimestamp(pendingItemsForm.getToDate()));
			} else {
				applicationWorkflow.setStartDate(leaveApplicationReviewer
						.getLeaveApplication().getStartDate());
				applicationWorkflow.setEndDate(leaveApplicationReviewer
						.getLeaveApplication().getEndDate());
			}

			applicationWorkflow.setStartSessionMaster(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1());
			applicationWorkflow.setEndSessionMaster(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2());
			applicationWorkflow.setTotalDays(leaveApplicationReviewer
					.getLeaveApplication().getTotalDays());

			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			LeaveApplicationWorkflow leaveApplicationWorkflow = applicationWorkflowDAO
					.saveReturn(applicationWorkflow);

			EmployeeLeaveSchemeType employeeLeaveSchemeType = leaveApplication
					.getEmployeeLeaveSchemeType();

			BigDecimal totalLeaveDays = null;
			if (isLeaveUnitDays) {
				LeaveDTO leaveDTO = new LeaveDTO();
				leaveDTO.setFromDate(DateUtils
						.timeStampToString(leaveApplication.getStartDate()));
				leaveDTO.setToDate(DateUtils.timeStampToString(leaveApplication
						.getEndDate()));
				leaveDTO.setSession1(leaveApplication.getLeaveSessionMaster1()
						.getLeaveSessionId());
				leaveDTO.setSession2(leaveApplication.getLeaveSessionMaster2()
						.getLeaveSessionId());
				leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType
						.getEmployeeLeaveSchemeTypeId());
				AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null,
						employeeId, leaveDTO);
				totalLeaveDays = noOfDays.getNoOfDays();
			} else {
				// Hours between dates
				totalLeaveDays = new BigDecimal(leaveApplication.getTotalDays())
						.setScale(2, BigDecimal.ROUND_HALF_UP);
			}

			// Insert Forfeit leave entry in EmployeeLeaveSchemeTypeHistory
			// table, only if current leave taken by Consider Leave Balance from
			// Other leave
			if (leaveApplication.getLeaveCancelApplication() == null) {
				leaveBalanceSummaryLogic.forfeitFromOtherLeaveType(
						leaveApplication, employeeLeaveSchemeType
								.getLeaveSchemeType().getLeaveSchemeTypeId());
			}

			EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory = new EmployeeLeaveSchemeTypeHistory();
			employeeLeaveSchemeTypeHistory
					.setEmployeeLeaveSchemeType(leaveApplication
							.getEmployeeLeaveSchemeType());
			AppCodeMaster appcodeTaken = appCodeMasterDAO
					.findByCategoryAndDesc(
							PayAsiaConstants.APP_CODE_LEAVE_TRANSACTION_TYPE,
							PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED);
			employeeLeaveSchemeTypeHistory.setAppCodeMaster(appcodeTaken);
			employeeLeaveSchemeTypeHistory
					.setLeaveApplication(leaveApplication);
			LeaveStatusMaster leaveStatusForHistory = leaveStatusMasterDAO
					.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);
			if (leaveApplication.getLeaveCancelApplication() != null) {
				leaveStatusForHistory = leaveStatusMasterDAO
						.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
			} else {
				leaveStatusForHistory = leaveStatusMasterDAO
						.findByCondition(PayAsiaConstants.LEAVE_STATUS_COMPLETED);

			}
			employeeLeaveSchemeTypeHistory
					.setLeaveStatusMaster(leaveStatusForHistory);
			employeeLeaveSchemeTypeHistory.setStartDate(leaveApplication
					.getStartDate());
			employeeLeaveSchemeTypeHistory.setEndDate(leaveApplication
					.getEndDate());
			employeeLeaveSchemeTypeHistory.setDays(BigDecimal
					.valueOf(leaveApplication.getTotalDays()));
			employeeLeaveSchemeTypeHistory.setReason(leaveApplication
					.getReason());
			employeeLeaveSchemeTypeHistory
					.setStartSessionMaster(leaveApplication
							.getLeaveSessionMaster1());
			employeeLeaveSchemeTypeHistory.setEndSessionMaster(leaveApplication
					.getLeaveSessionMaster2());
			employeeLeaveSchemeTypeHistoryDAO
					.save(employeeLeaveSchemeTypeHistory);

			// Add Leave Application to keyPay Sync table name
			// 'KeyPay_Int_Leave_Application'

			IntegrationMaster integrationMaster =	keyPayIntLeaveApplicationDAO.findByKeyPayDetailByCompanyId(leaveApplication.getCompany().getCompanyId());
			if (integrationMaster!=null && !isLeaveUnitDays) {
					leaveBalanceSummaryLogic
						.addLeaveAppToKeyPayInt(leaveApplication);
			}

			AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null,
					null, leaveApplication.getEmployeeLeaveSchemeType()
							.getEmployeeLeaveSchemeTypeId());

			if (leaveApplication.getLeaveCancelApplication() != null) {

				LeaveApplication leaveApplicationToBeCancelled = leaveApplication
						.getLeaveCancelApplication();
				LeaveStatusMaster cancelLeaveStatusMaster = leaveStatusMasterDAO
						.findByCondition(PayAsiaConstants.LEAVE_STATUS_CANCELLED);
				leaveApplicationToBeCancelled
						.setLeaveStatusMaster(cancelLeaveStatusMaster);
				leaveApplicationDAO.update(leaveApplicationToBeCancelled);

				generalMailLogic
						.sendAcceptRejectMailForLeave(
								leaveApplicationWorkflow.getLeaveApplication()
										.getEmployee().getCompany()
										.getCompanyId(),
								leaveApplicationWorkflow,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_CANCEL_APPROVE,
								totalLeaveDays, leaveBalance.getLeaveBalance(),
								employee, pendingItemsForm.getRemarks(),
								sessionDTO, isLeaveUnitDays,
								pendingItemsForm.getSystemEmail());

			} else {
				generalMailLogic.sendAcceptRejectMailForLeave(
						leaveApplicationWorkflow.getLeaveApplication()
								.getEmployee().getCompany().getCompanyId(),
						leaveApplicationWorkflow,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LEAVE_APPROVE,
						totalLeaveDays, leaveBalance.getLeaveBalance(),
						employee, pendingItemsForm.getRemarks(), sessionDTO,
						isLeaveUnitDays, pendingItemsForm.getSystemEmail());
			}

		}
		return addLeaveForm;

	}

	@Override
	public LeaveApplicationPdfDTO generateLeaveApplicationPrintPDF(
			Long companyId, Long employeeId, Long leaveApplicationReviewerId) {

		LeaveApplicationPdfDTO leaveApplicationPdfDTO = new LeaveApplicationPdfDTO();
		LeaveApplicationReviewer leaveApplicationReviewerVO = leaveApplicationReviewerDAO
				.findById(leaveApplicationReviewerId);
		
		leaveApplicationPdfDTO.setLeaveSchemeName(leaveApplicationReviewerVO
				.getLeaveApplication().getEmployeeLeaveSchemeType()
				.getLeaveSchemeType().getLeaveScheme().getSchemeName());
		leaveApplicationPdfDTO.setEmployeeNumber(leaveApplicationReviewerVO
				.getLeaveApplication().getEmployee().getEmployeeNumber());
	

		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				leaveApplicationPdfDTO
						.setLeaveAppPdfByteFile(generateLeaveApplicationPDF(
								companyId, employeeId,
								leaveApplicationReviewerVO
										.getLeaveApplication()
										.getLeaveApplicationId()));
				return leaveApplicationPdfDTO;
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				leaveApplicationPdfDTO
						.setLeaveAppPdfByteFile(generateLeaveApplicationPDF(
								companyId, employeeId,
								leaveApplicationReviewerVO
										.getLeaveApplication()
										.getLeaveApplicationId()));
				return leaveApplicationPdfDTO;
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

	}

	private byte[] generateLeaveApplicationPDF(Long companyId, Long employeeId,
			Long leaveApplicationId) throws DocumentException, IOException,
			JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId,
				PAYASIA_TEMP_PATH, "Leave");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PayAsiaPDFConstants.PAGE_SIZE,
					PayAsiaPDFConstants.PAGE_LEFT_MARGIN,
					PayAsiaPDFConstants.PAGE_TOP_MARGIN,
					PayAsiaPDFConstants.PAGE_RIGHT_MARGIN,
					PayAsiaPDFConstants.PAGE_BOTTOM_MARGIN);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			PdfPTable leaveApplicationPrintPDFTable = leaveApplicationPrintPDFLogic
					.createLeaveReportPdf(document, writer, 1, companyId,
							leaveApplicationId);

			document.add(leaveApplicationPrintPDFTable);

			PdfAction action = PdfAction.gotoLocalPage(1, new PdfDestination(
					PdfDestination.FIT, 0, 10000, 1), writer);
			writer.setOpenAction(action);
			writer.setViewerPreferences(PdfWriter.FitWindow
					| PdfWriter.CenterWindow);

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
	public PendingItemsFormResponse viewMultipleLeaveApplications(
			Long companyId, Long loggedInEmpId, String[] leaveApplicationRevIds) {
		Locale locale = UserContext.getLocale();
		List<Long> leaveApplicationRevIdsList = new ArrayList<Long>();
		for (int count = 0; count < leaveApplicationRevIds.length; count++) {
			if (StringUtils.isNotBlank(leaveApplicationRevIds[count])) {
				leaveApplicationRevIdsList.add(Long
						.parseLong(leaveApplicationRevIds[count]));
			}
		}
		List<LeaveApplicationReviewer> leaveApplicationReviewerList = leaveApplicationReviewerDAO
				.getPendingLeaveApplicationByIds(null,
						leaveApplicationRevIdsList);
		PendingItemsFormResponse response = new PendingItemsFormResponse();

		// Set Leave unit
		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(companyId);
		if (leavePreferenceVO != null
				&& leavePreferenceVO.getLeaveUnit() != null) {
			response.setLeaveUnit(leavePreferenceVO.getLeaveUnit()
					.getCodeDesc());
		} else {
			response.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
		}

		List<PendingItemsForm> pendingItemsFormList = new ArrayList<PendingItemsForm>();
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationReviewerList) {
			PendingItemsForm pendingItemsForm = new PendingItemsForm();
			AddLeaveForm addLeaveForm = new AddLeaveForm();

			String allowReject = "";
			String allowApprove = "";
			String allowForward = "";
			for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : leaveApplicationReviewer
					.getLeaveApplication().getEmployeeLeaveSchemeType()
					.getLeaveSchemeType().getLeaveSchemeTypeWorkflows()) {
				if (leaveSchemeTypeWorkflow
						.getWorkFlowRuleMaster()
						.getRuleName()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
					allowReject = leaveSchemeTypeWorkflow
							.getWorkFlowRuleMaster().getRuleValue();
				} else if (leaveSchemeTypeWorkflow
						.getWorkFlowRuleMaster()
						.getRuleName()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
					allowForward = leaveSchemeTypeWorkflow
							.getWorkFlowRuleMaster().getRuleValue();
				} else if (leaveSchemeTypeWorkflow
						.getWorkFlowRuleMaster()
						.getRuleName()
						.equalsIgnoreCase(
								PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
					allowApprove = leaveSchemeTypeWorkflow
							.getWorkFlowRuleMaster().getRuleValue();
				}

			}
			int reviewOrder = 0;
			for (LeaveApplicationReviewer applicationReviewer : leaveApplicationReviewer
					.getLeaveApplication().getLeaveApplicationReviewers()) {
			
				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("1")) {
					addLeaveForm.setApplyTo(applicationReviewer.getEmployee()
							.getEmail());
					addLeaveForm
							.setLeaveReviewer1(getEmployeeNameWithNumber(applicationReviewer
									.getEmployee()));
					addLeaveForm.setApplyToId(applicationReviewer.getEmployee()
							.getEmployeeId());
					if (leaveApplicationReviewer.getEmployee().getEmployeeId() == applicationReviewer
							.getEmployee().getEmployeeId()) {
						reviewOrder = 1;
						if (allowReject.length() == 3
								&& allowReject.substring(0, 1).equals("1")) {
							pendingItemsForm.setCanReject(true);
						} else {
							pendingItemsForm.setCanReject(false);
						}
						if (allowApprove.length() == 3
								&& allowApprove.substring(0, 1).equals("1")) {
							pendingItemsForm.setCanApprove(true);
						} else {
							pendingItemsForm.setCanApprove(false);
						}
						if (allowForward.length() == 3
								&& allowForward.substring(0, 1).equals("1")) {
							pendingItemsForm.setCanForward(true);
						} else {
							pendingItemsForm.setCanForward(false);
						}
					}
				}

				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("2")) {
					addLeaveForm
							.setLeaveReviewer2(getEmployeeNameWithNumber(applicationReviewer
									.getEmployee()));
					addLeaveForm.setLeaveReviewer2Id(applicationReviewer
							.getEmployee().getEmployeeId());
					if (leaveApplicationReviewer.getEmployee().getEmployeeId() == applicationReviewer
							.getEmployee().getEmployeeId()) {
						reviewOrder = 2;
						if (allowReject.length() == 3
								&& allowReject.substring(1, 2).equals("1")) {
							pendingItemsForm.setCanReject(true);
						} else {
							pendingItemsForm.setCanReject(false);
						}
						if (allowApprove.length() == 3
								&& allowApprove.substring(1, 2).equals("1")) {
							pendingItemsForm.setCanApprove(true);
						} else {
							pendingItemsForm.setCanApprove(false);
						}
						if (allowForward.length() == 3
								&& allowForward.substring(1, 2).equals("1")) {
							pendingItemsForm.setCanForward(true);
						} else {
							pendingItemsForm.setCanForward(false);
						}
					}

				}

				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equals("3")) {
					addLeaveForm
							.setLeaveReviewer3(getEmployeeNameWithNumber(applicationReviewer
									.getEmployee()));
					addLeaveForm.setLeaveReviewer3Id(applicationReviewer
							.getEmployee().getEmployeeId());
					if (leaveApplicationReviewer.getEmployee().getEmployeeId() == applicationReviewer
							.getEmployee().getEmployeeId()) {
						reviewOrder = 2;
						if (allowReject.length() == 3
								&& allowReject.substring(2, 3).equals("1")) {
							pendingItemsForm.setCanReject(true);
						} else {
							pendingItemsForm.setCanReject(false);
						}
						if (allowApprove.length() == 3
								&& allowApprove.substring(2, 3).equals("1")) {
							pendingItemsForm.setCanApprove(true);
						} else {
							pendingItemsForm.setCanApprove(false);
						}
						if (allowForward.length() == 3
								&& allowForward.substring(2, 3).equals("1")) {
							pendingItemsForm.setCanForward(true);
						} else {
							pendingItemsForm.setCanForward(false);
						}
					}
				}
			}

			if (reviewOrder == 1 && addLeaveForm.getLeaveReviewer2Id() != null) {
				Employee empReviewer2 = employeeDAO.findById(addLeaveForm
						.getLeaveReviewer2Id());
				pendingItemsForm
						.setForwardTo(getEmployeeNameWithNumber(empReviewer2));
				pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.encrypt(empReviewer2.getEmployeeId()));
			} else if (reviewOrder == 2
					&& addLeaveForm.getLeaveReviewer3Id() != null) {
				Employee empReviewer3 = employeeDAO.findById(addLeaveForm
						.getLeaveReviewer3Id());
				pendingItemsForm
						.setForwardTo(getEmployeeNameWithNumber(empReviewer3));
				pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.encrypt(empReviewer3.getEmployeeId()));
			} else {
				pendingItemsForm.setForwardTo("");
			}

			pendingItemsForm
					.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.encrypt(leaveApplicationReviewer
							.getLeaveApplicationReviewerId()));
			pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplicationReviewer
					.getLeaveApplication().getLeaveApplicationId()));
			pendingItemsForm.setFromDate(DateUtils
					.timeStampToString(leaveApplicationReviewer
							.getLeaveApplication().getStartDate()));
			pendingItemsForm.setToDate(DateUtils
					.timeStampToString(leaveApplicationReviewer
							.getLeaveApplication().getEndDate()));

		/*	if (StringUtils.isNotBlank(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1()
					.getSessionLabelKey())) {
				pendingItemsForm.setFromSession(messageSource.getMessage(
						leaveApplicationReviewer.getLeaveApplication()
								.getLeaveSessionMaster1().getSessionLabelKey(),
						new Object[] {}, locale));
			}
			if (StringUtils.isNotBlank(leaveApplicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2()
					.getSessionLabelKey())) {
				pendingItemsForm.setToSession(messageSource.getMessage(
						leaveApplicationReviewer.getLeaveApplication()
								.getLeaveSessionMaster2().getSessionLabelKey(),
						new Object[] {}, locale));
			}*/
			pendingItemsForm.setReason(leaveApplicationReviewer
					.getLeaveApplication().getReason());
			pendingItemsForm.setLeaveTypeName(leaveApplicationReviewer
					.getLeaveApplication().getEmployeeLeaveSchemeType()
					.getLeaveSchemeType().getLeaveTypeMaster()
					.getLeaveTypeName());
			pendingItemsForm
					.setEmployee(getEmployeeNameWithNumber(leaveApplicationReviewer
							.getLeaveApplication().getEmployee()));
			pendingItemsForm.setDays(new BigDecimal(leaveApplicationReviewer
					.getLeaveApplication().getTotalDays()).setScale(2,
					BigDecimal.ROUND_HALF_UP));
			pendingItemsFormList.add(pendingItemsForm);
		}
		response.setRows(pendingItemsFormList);
		return response;
	}

	@Override
	public PendingItemsForm showEmpLeaveWorkflowStatus(Long companyId,Long employeeId,
			Long leaveApplicationId) {
		PendingItemsForm pendingItemsForm = new PendingItemsForm();

		LeaveApplication leaveApplicationVO = leaveApplicationDAO
				.findById(leaveApplicationId);
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationVO
				.getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee()
						.getEmail());
				addLeaveForm
						.setLeaveReviewer1(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setApplyToId(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
			}
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				addLeaveForm
						.setLeaveReviewer2(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
			}
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				addLeaveForm
						.setLeaveReviewer3(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
			}
		}

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<>();
		Integer workFlowCount = 0;
		List<LeaveApplicationWorkflow> leaveApplicationWorkflows = new ArrayList<>(
				leaveApplicationVO.getLeaveApplicationWorkflows());
		Collections.sort(leaveApplicationWorkflows,
				new LeaveApplicationWorkflowComp());
		
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplicationWorkflows) {
			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();

			workFlowCount++;

			if (workFlowCount != 1) {
				applicationWorkflowDTO.setCreatedDate(DateUtils
						.timeStampToStringWithTime(leaveApplicationWorkflow
								.getCreatedDate()));
				applicationWorkflowDTO
						.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationWorkflow
								.getEmployee()));
				applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow
						.getRemarks());

				applicationWorkflowDTO
						.setStatus(setStatusMultilingualKey(leaveApplicationWorkflow
								.getLeaveStatusMaster().getLeaveStatusName()));
				applicationWorkflowDTO.setCreatedDate(DateUtils
						.timeStampToStringWithTime(leaveApplicationWorkflow
								.getCreatedDate()));
			}

			if (leaveApplicationWorkflow.getEmployee().getEmployeeId() == leaveApplicationVO
					.getEmployee().getEmployeeId()) {

				applicationWorkflowDTO.setOrder(0);

			}

			if (addLeaveForm.getApplyToId() != null
					&& addLeaveForm.getApplyToId() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null
					&& addLeaveForm.getLeaveReviewer2Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null
					&& addLeaveForm.getLeaveReviewer3Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		addLeaveForm.setTotalNoOfReviewers(leaveApplicationVO
				.getLeaveApplicationReviewers().size());
		addLeaveForm
				.setLeaveAppEmp(getEmployeeNameWithNumber(leaveApplicationVO
						.getEmployee()));
		addLeaveForm
				.setLeaveAppCreated(DateUtils
						.timeStampToStringWithTime(leaveApplicationVO
								.getCreatedDate()));
		addLeaveForm.setLeaveAppRemarks(leaveApplicationVO.getReason());
		addLeaveForm.setLeaveAppStatus(setStatusMultilingualKey("Submitted"));
		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		pendingItemsForm.setAddLeaveForm(addLeaveForm);
		return pendingItemsForm;
	}
	@Override
	public PendingItemsForm showEmpLeaveWorkflowStatusForMobile(Long companyId,Long employeeId,
			Long leaveApplicationId) {
		PendingItemsForm pendingItemsForm = new PendingItemsForm();
		byte[] byteImage = null;
		LeaveApplication leaveApplicationVO = leaveApplicationDAO.findById(leaveApplicationId);
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		
		List<LeaveApplicationWorkflow> leaveApplicationWorkflows = new ArrayList<>(
				leaveApplicationVO.getLeaveApplicationWorkflows());
		Collections.sort(leaveApplicationWorkflows, new LeaveApplicationWorkflowComp());
		LinkedList<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new LinkedList<>();
		
		String name = "payasia."+leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName().toLowerCase(UserContext.getLocale());
		Integer value = leaveApplicationWorkflows.size()-1;
		
		for (LeaveApplicationReviewer leaveApplicationReviewer : leaveApplicationVO.getLeaveApplicationReviewers()) {
			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();
			if(leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")){
				applicationWorkflowDTO.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationReviewer.getEmployee()));
				applicationWorkflowDTO.setLeaveReviewerType("Reviewer 1");
				applicationWorkflowDTO.setRemarks("");
				applicationWorkflowDTO.setStatus(setStatusMultilingualKey("Pending"));
				try {
					byteImage = employeeDetailLogic.getEmployeeImage(leaveApplicationReviewer.getEmployee().getEmployeeId(),null, employeeImageWidth, employeeImageHeight);
				} catch (IOException e) {
				}
				applicationWorkflowDTO.setLeaveApplicationReviewerImage(byteImage!=null ? byteImage :"".getBytes());
				applicationWorkflowDTO.setCreatedDate("");
			}
			if(leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")){
						applicationWorkflowDTO.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationReviewer.getEmployee()));
						applicationWorkflowDTO.setLeaveReviewerType("Reviewer 2");
						applicationWorkflowDTO.setRemarks("");
						applicationWorkflowDTO.setStatus(setStatusMultilingualKey("Pending"));
						applicationWorkflowDTO.setCreatedDate("");
						try {
							byteImage = employeeDetailLogic.getEmployeeImage(leaveApplicationReviewer.getEmployee().getEmployeeId(),null, employeeImageWidth, employeeImageHeight);
						} catch (IOException e) {
						}
						applicationWorkflowDTO.setLeaveApplicationReviewerImage(byteImage!=null ? byteImage :"".getBytes());
					}
			if(leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")){
						applicationWorkflowDTO.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationReviewer.getEmployee()));
						applicationWorkflowDTO.setLeaveReviewerType("Reviewer 3");
						applicationWorkflowDTO.setRemarks("");
						applicationWorkflowDTO.setStatus(setStatusMultilingualKey("Pending"));
						applicationWorkflowDTO.setCreatedDate("");
						try {
							byteImage = employeeDetailLogic.getEmployeeImage(leaveApplicationReviewer.getEmployee().getEmployeeId(),null, employeeImageWidth, employeeImageHeight);
						} catch (IOException e) {
						}
						applicationWorkflowDTO.setLeaveApplicationReviewerImage(byteImage!=null ? byteImage :"".getBytes());
					}
			
			for(LeaveApplicationWorkflow leaveApplicationWorkflow:leaveApplicationWorkflows){
				
			if(leaveApplicationReviewer.getEmployee().getEmployeeId()==leaveApplicationWorkflow.getEmployee().getEmployeeId()){
				applicationWorkflowDTO.setEmoployeeInfoId(leaveApplicationWorkflow.getLeaveApplicationWorkflowID());
				applicationWorkflowDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationWorkflow.getCreatedDate()));
				applicationWorkflowDTO.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationWorkflow.getEmployee()));
				applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow.getRemarks());
				
				applicationWorkflowDTO.setStatus(setStatusMultilingualKey(leaveApplicationWorkflow.getLeaveStatusMaster().getLeaveStatusName()));
				if(StringUtils.equals(leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue(), value.toString())){
					applicationWorkflowDTO.setStatus(setStatusMultilingualKey(name));
				}
				
				leaveApplicationWorkflow.getLeaveApplicationWorkflowID();
				
				applicationWorkflowDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationWorkflow.getCreatedDate()));
				}
			}
			
			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}
		Collections.sort(applicationWorkflowDTOs, new LeaveApplicationWorkflowList());
		
		LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();
		
		applicationWorkflowDTO.setStatus(setStatusMultilingualKey(StringUtils.equalsIgnoreCase(leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName(), "Withdrawn")?"Withdrawn":"Submitted"));
		applicationWorkflowDTO.setRemarks(leaveApplicationVO.getReason());
		applicationWorkflowDTO.setCreatedDate(DateUtils.timeStampToStringWithTime(leaveApplicationVO.getCreatedDate()));
		applicationWorkflowDTO.setLeaveReviewerType("User");
		applicationWorkflowDTO.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationVO.getEmployee()));
		try {
			byteImage = employeeDetailLogic.getEmployeeImage(leaveApplicationVO.getEmployee().getEmployeeId(),null, employeeImageWidth, employeeImageHeight);
		} catch (IOException e) {
		}
		applicationWorkflowDTO.setLeaveApplicationReviewerImage(byteImage!=null ? byteImage :"".getBytes());
		applicationWorkflowDTOs.addFirst(applicationWorkflowDTO);
		
		List<LeaveApplicationWorkflowDTO> list = new ArrayList<>();
		List<LeaveApplicationWorkflowDTO> finalList = new ArrayList<>();
		list.addAll(applicationWorkflowDTOs);
		String prevStatusName = "anuj";
		
		for(LeaveApplicationWorkflowDTO leaveAppWFD : list){
			if (StringUtils.equalsIgnoreCase(leaveAppWFD.getLeaveReviewerType(), "Reviewer 1")) {
				finalList.add(1, leaveAppWFD);
			}
			else if(StringUtils.equalsIgnoreCase(leaveAppWFD.getLeaveReviewerType(), "Reviewer 2")){
				
				finalList.add(2, leaveAppWFD);
				
			}
			else if(StringUtils.equalsIgnoreCase(leaveAppWFD.getLeaveReviewerType(), "Reviewer 3")){
				finalList.add(3, leaveAppWFD);
			}
			else {
				finalList.add(0, leaveAppWFD);
			}
		}
		
		for(LeaveApplicationWorkflowDTO leaveAppWFD : finalList){
			if (StringUtils.equalsIgnoreCase(leaveAppWFD.getLeaveReviewerType(), "Reviewer 1")) {
				
				if (StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "") || StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "payasia.submitted")) {
					leaveAppWFD.setStatus("payasia.pending");
					prevStatusName = PayAsiaConstants.LEAVE_STATUS_PENDING;
				}
//				else if (StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "payasia.submitted")){
//					leaveAppWFD.setStatus("payasia.pending");
//					prevStatusName = PayAsiaConstants.LEAVE_STATUS_PENDING;
//				}
				else if (StringUtils.equalsIgnoreCase(leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName(), "withdrawn")) {
					leaveAppWFD.setStatus("");
				}
				else{
					prevStatusName = leaveAppWFD.getStatus();
				}
			}
			
			else if(StringUtils.equalsIgnoreCase(leaveAppWFD.getLeaveReviewerType(), "Reviewer 2")){
				
				if (StringUtils.equalsIgnoreCase(prevStatusName, "payasia.completed") || StringUtils.equalsIgnoreCase(prevStatusName, "payasia.rejected")) {
					leaveAppWFD.setStatus("");
					prevStatusName = "";
				}
				else if (StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "") || StringUtils.equalsIgnoreCase(prevStatusName, PayAsiaConstants.LEAVE_STATUS_PENDING) || StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "payasia.submitted")) {
					leaveAppWFD.setStatus("payasia.pending");
					prevStatusName = PayAsiaConstants.LEAVE_STATUS_PENDING;
				}
//				else if (StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "payasia.submitted")){
//					leaveAppWFD.setStatus("payasia.pending");
//					prevStatusName = PayAsiaConstants.LEAVE_STATUS_PENDING;
//				}
				else if (StringUtils.equalsIgnoreCase(leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName(), "withdrawn")) {
					leaveAppWFD.setStatus("");
				}
				else{
					prevStatusName = leaveAppWFD.getStatus();
				}
				
			}
			else if(StringUtils.equalsIgnoreCase(leaveAppWFD.getLeaveReviewerType(), "Reviewer 3")){
				if (StringUtils.equalsIgnoreCase(prevStatusName, "payasia.completed") || StringUtils.equalsIgnoreCase(prevStatusName, "") || StringUtils.equalsIgnoreCase(prevStatusName, "payasia.rejected")) {
					leaveAppWFD.setStatus("");
					prevStatusName = "";
				}
				else if (StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "") || StringUtils.equalsIgnoreCase(prevStatusName, PayAsiaConstants.LEAVE_STATUS_PENDING) || StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "payasia.submitted")) {
					leaveAppWFD.setStatus("payasia.pending");
					prevStatusName = PayAsiaConstants.LEAVE_STATUS_PENDING;
				}
//				else if (StringUtils.equalsIgnoreCase(leaveAppWFD.getStatus(), "payasia.submitted")){
//					leaveAppWFD.setStatus("payasia.pending");
//					prevStatusName = PayAsiaConstants.LEAVE_STATUS_PENDING;
//				}
				else if (StringUtils.equalsIgnoreCase(leaveApplicationVO.getLeaveStatusMaster().getLeaveStatusName(), "withdrawn")) {
					leaveAppWFD.setStatus("");
				}
				else{
					prevStatusName = leaveAppWFD.getStatus();
				}
			}
		}
		
		
		addLeaveForm.setWorkflowList(finalList);
//		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		addLeaveForm.setTotalNoOfReviewers(leaveApplicationVO.getLeaveApplicationReviewers().size());
//		addLeaveForm.setTotalNoOfReviewers(finalList.size()-1);
		pendingItemsForm.setAddLeaveForm(addLeaveForm);
				return pendingItemsForm;
	}
	

	@Override
	public List<AddLeaveForm> reviewMultipleLeaveAppByAdmin(
			PendingItemsForm pendingItemsForm, Long employeeId, Long companyId,
			LeaveSessionDTO sessionDTO) {
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		List<LeaveReviewFormDTO> leaveReviewFormDTOList = pendingItemsForm
				.getLeaveReviewFormDTOList();
		for (LeaveReviewFormDTO leaveReviewFormDTO : leaveReviewFormDTOList) {
			pendingItemsForm.setLeaveApplicationReviewerId(null);
			pendingItemsForm.setRemarks("");

			if (StringUtils.isBlank(leaveReviewFormDTO.getLeaveReviewAction())) {
				continue;
			}

			if (leaveReviewFormDTO.getLeaveApplicationRevId() != null) {
				pendingItemsForm
						.setLeaveApplicationReviewerId(leaveReviewFormDTO
								.getLeaveApplicationRevId());
				pendingItemsForm.setRemarks(leaveReviewFormDTO.getRemarks());
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Approve")) {
					AddLeaveForm addLeaveForm = acceptLeaveforAdmin(
							pendingItemsForm, employeeId, sessionDTO);
					addLeaveForm
							.setLeaveApplicationReviewerId(leaveReviewFormDTO
									.getLeaveApplicationRevId());
					if (addLeaveForm.getLeaveDTO() == null) {
						LeaveDTO leaveDTO = new LeaveDTO();
						leaveDTO.setErrorCode(1);
						leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_APPROVED);
						leaveDTO.setErrorValue(" ;");
						addLeaveForm.setLeaveDTO(leaveDTO);
					}
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Reject")) {
					AddLeaveForm addLeaveForm = rejectLeaveForAdmin(
							pendingItemsForm, employeeId, sessionDTO);
					addLeaveForm
							.setLeaveApplicationReviewerId(leaveReviewFormDTO
									.getLeaveApplicationRevId());
					if (addLeaveForm.getLeaveDTO() == null) {
						LeaveDTO leaveDTO = new LeaveDTO();
						leaveDTO.setErrorCode(1);
						leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_REJECTED);
						leaveDTO.setErrorValue(" ;");
						addLeaveForm.setLeaveDTO(leaveDTO);
					}
					addLeaveFormList.add(addLeaveForm);
				}

			}
		}
		return addLeaveFormList;
	}

	@Override
	public List<AddLeaveForm> reviewMultipleLeaveApp(
			PendingItemsForm pendingItemsForm, Long employeeId, Long companyId,
			LeaveSessionDTO sessionDTO) {
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		List<LeaveReviewFormDTO> leaveReviewFormDTOList = pendingItemsForm
				.getLeaveReviewFormDTOList();
		for (LeaveReviewFormDTO leaveReviewFormDTO : leaveReviewFormDTOList) {
			
		
			pendingItemsForm.setLeaveApplicationReviewerId(null);
			pendingItemsForm.setRemarks("");

			if (StringUtils.isBlank(leaveReviewFormDTO.getLeaveReviewAction())) {
				continue;
			}
			if (leaveReviewFormDTO.getLeaveApplicationRevId() != null) {
				pendingItemsForm
						.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO.getLeaveApplicationRevId()));
								
				pendingItemsForm.setRemarks(leaveReviewFormDTO.getRemarks());
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Approve")) {
					pendingItemsForm.getEmailCC();
					
					AddLeaveForm addLeaveForm = acceptLeave(pendingItemsForm,
							employeeId, sessionDTO);
					addLeaveForm
							.setLeaveApplicationReviewerId(leaveReviewFormDTO
									.getLeaveApplicationRevId());
					if (addLeaveForm.getLeaveDTO() == null) {
						LeaveDTO leaveDTO = new LeaveDTO();
						leaveDTO.setErrorCode(1);
						leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_APPROVED);
						leaveDTO.setErrorValue(" ;");
						addLeaveForm.setLeaveDTO(leaveDTO);
					}
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Reject")) {
					rejectLeave(pendingItemsForm, employeeId, sessionDTO);
					AddLeaveForm addLeaveForm = new AddLeaveForm();
					addLeaveForm
							.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO.getLeaveApplicationRevId()));
					if (addLeaveForm.getLeaveDTO() == null) {
						LeaveDTO leaveDTO = new LeaveDTO();
						leaveDTO.setErrorCode(1);
						leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_REJECTED);
						leaveDTO.setErrorValue(" ;");
						addLeaveForm.setLeaveDTO(leaveDTO);
					}
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Forward")) {
					pendingItemsForm.setForwardToId(leaveReviewFormDTO
							.getForwardToId());
					pendingItemsForm.getEmailCC();
					AddLeaveForm addLeaveForm = forwardLeave(pendingItemsForm,
							employeeId, sessionDTO);
					addLeaveForm
							.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO
									.getLeaveApplicationRevId()));
					if (addLeaveForm.getLeaveDTO() == null) {
						LeaveDTO leaveDTO = new LeaveDTO();
						leaveDTO.setErrorCode(1);
						leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_FORWARDED);
						leaveDTO.setErrorValue(" ;");
						addLeaveForm.setLeaveDTO(leaveDTO);
					}
					addLeaveFormList.add(addLeaveForm);
				}

			}
		}
		return addLeaveFormList;
	}
	
	@Override
	public List<AddLeaveForm> reviewMultipleLeaveApproveandForward(
			PendingItemsForm pendingItemsForm, Long employeeId, Long companyId,
			LeaveSessionDTO sessionDTO) {
		List<AddLeaveForm> addLeaveFormList = new ArrayList<AddLeaveForm>();
		List<LeaveReviewFormDTO> leaveReviewFormDTOList = pendingItemsForm
				.getLeaveReviewFormDTOList();
		for (LeaveReviewFormDTO leaveReviewFormDTO : leaveReviewFormDTOList) {
			
		if(leaveReviewFormDTO.isCanApprove() && leaveReviewFormDTO.isCanForward()){
			AddLeaveForm addLeaveForm = new AddLeaveForm();
			addLeaveForm
			.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO
					.getLeaveApplicationRevId()));
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_MULTIPAL_APPROVE_FORWORD_ERROR);
			leaveDTO.setErrorValue(" ;");
			addLeaveForm.setLeaveDTO(leaveDTO);
			addLeaveFormList.add(addLeaveForm);
			
		}
		else{
			pendingItemsForm.setLeaveApplicationReviewerId(null);
			pendingItemsForm.setRemarks("");

			if (StringUtils.isBlank(leaveReviewFormDTO.getLeaveReviewAction())) {
				continue;
			}
			if (leaveReviewFormDTO.getLeaveApplicationRevId() != null) {
				pendingItemsForm
						.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO.getLeaveApplicationRevId()));
								
				pendingItemsForm.setRemarks(leaveReviewFormDTO.getRemarks());
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Approve") && leaveReviewFormDTO.isCanApprove()) {
					pendingItemsForm.getEmailCC();
					
					AddLeaveForm addLeaveForm = acceptLeave(pendingItemsForm,
							employeeId, sessionDTO);
					addLeaveForm
					.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO
							.getLeaveApplicationRevId()));
					if (addLeaveForm.getLeaveDTO() == null) {
						LeaveDTO leaveDTO = new LeaveDTO();
						leaveDTO.setErrorCode(1);
						leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_APPROVED);
						leaveDTO.setErrorValue(" ;");
						addLeaveForm.setLeaveDTO(leaveDTO);
					}
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase(
						"Forward") && leaveReviewFormDTO.isCanForward()) {
					pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO
							.getForwardToId()));
					pendingItemsForm.getEmailCC();
					AddLeaveForm addLeaveForm = forwardLeave(pendingItemsForm,
							employeeId, sessionDTO);
					addLeaveForm
							.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(leaveReviewFormDTO
									.getLeaveApplicationRevId()));
					if (addLeaveForm.getLeaveDTO() == null) {
						LeaveDTO leaveDTO = new LeaveDTO();
						leaveDTO.setErrorCode(1);
						leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_FORWARDED);
						leaveDTO.setErrorValue(" ;");
						addLeaveForm.setLeaveDTO(leaveDTO);
					}
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.isCanApprove()==false && leaveReviewFormDTO.isCanForward()==false ) {
					
					AddLeaveForm addLeaveForm = new AddLeaveForm();
					addLeaveForm
					.setLeaveApplicationReviewerId(leaveReviewFormDTO
							.getLeaveApplicationRevId());
					LeaveDTO leaveDTO = new LeaveDTO();
					leaveDTO.setErrorCode(1);
					leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_MULTIPAL_APPROVE_CONFIG_ERROR);
					leaveDTO.setErrorValue(" ;");
					addLeaveForm.setLeaveDTO(leaveDTO);
					addLeaveFormList.add(addLeaveForm);
				}
			}

			}
		}
		return addLeaveFormList;
	}
	
	@Override
	public PendingItemsForm getDataForLeaveReviewEmp(Long reviewId,Long employeeId) {

		PendingItemsForm pendingItemsForm = new PendingItemsForm();
		LeaveApplicationReviewer applicationReviewer = leaveApplicationReviewerDAO
				.getLeaveApplicationReviewerDetail(reviewId,employeeId);

		if(applicationReviewer==null){
			
			return null;
		}
		
		AddLeaveForm addLeaveForm = new AddLeaveForm();

		List<LeaveApplicationCustomField> leaveApplicationCustomFields = new ArrayList<>(
				applicationReviewer.getLeaveApplication()
						.getLeaveApplicationCustomFields());
		Collections.sort(leaveApplicationCustomFields,
				new LeaveSchemeTypeCusFieldComp());
		List<LeaveCustomFieldDTO> leaveCustomFieldDTOs = new ArrayList<>();
		for (LeaveApplicationCustomField leaveApplicationCustomField : leaveApplicationCustomFields) {

			LeaveCustomFieldDTO customField = new LeaveCustomFieldDTO();
			customField.setValue(leaveApplicationCustomField.getValue());
			customField.setCustomFieldId(leaveApplicationCustomField
					.getLeaveApplicationCustomFieldId());
			customField.setCustomFieldTypeId(leaveApplicationCustomField
					.getLeaveSchemeTypeCustomField().getCustomFieldId());
			customField.setCustomFieldName(leaveApplicationCustomField
					.getLeaveSchemeTypeCustomField().getFieldName());
			leaveCustomFieldDTOs.add(customField);
		}
		addLeaveForm.setCustomFieldDTOList(leaveCustomFieldDTOs);

		// Set Leave unit
		LeavePreference leavePreferenceVO = leavePreferenceDAO
				.findByCompanyId(applicationReviewer.getLeaveApplication()
						.getCompany().getCompanyId());
		if (leavePreferenceVO != null
				&& leavePreferenceVO.getLeaveUnit() != null) {
			addLeaveForm.setLeaveUnit(leavePreferenceVO.getLeaveUnit()
					.getCodeDesc());
		} else {
			addLeaveForm
					.setLeaveUnit(PayAsiaConstants.PAYASIA_LEAVE_UNIT_IN_DAYS);
		}

		AddLeaveForm leaveBalance = addLeaveLogic.getLeaveBalance(null, null,
				applicationReviewer.getLeaveApplication()
						.getEmployeeLeaveSchemeType()
						.getEmployeeLeaveSchemeTypeId());

		addLeaveForm.setLeaveBalance(leaveBalance.getLeaveBalance());
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(applicationReviewer.getLeaveApplication()
						.getCompany().getCompanyId());

		BigDecimal totalLeaveDays = null;
		if (isLeaveUnitDays) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setFromDate(DateUtils
					.timeStampToString(applicationReviewer
							.getLeaveApplication().getStartDate()));
			leaveDTO.setToDate(DateUtils.timeStampToString(applicationReviewer
					.getLeaveApplication().getEndDate()));
			leaveDTO.setSession1(applicationReviewer.getLeaveApplication()
					.getLeaveSessionMaster1().getLeaveSessionId());
			leaveDTO.setSession2(applicationReviewer.getLeaveApplication()
					.getLeaveSessionMaster2().getLeaveSessionId());
			leaveDTO.setEmployeeLeaveSchemeTypeId(applicationReviewer
					.getLeaveApplication().getEmployeeLeaveSchemeType()
					.getEmployeeLeaveSchemeTypeId());
			AddLeaveForm noOfDays = addLeaveLogic.getNoOfDays(null, null,
					leaveDTO);
			totalLeaveDays = noOfDays.getNoOfDays();
		} else {
			// Hours between dates
			totalLeaveDays = new BigDecimal(applicationReviewer
					.getLeaveApplication().getTotalDays()).setScale(2,
					BigDecimal.ROUND_HALF_UP);
		}
		addLeaveForm.setNoOfDays(totalLeaveDays);
		addLeaveForm.setLeaveScheme(applicationReviewer.getLeaveApplication()
				.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveScheme().getSchemeName());
		addLeaveForm.setLeaveType(applicationReviewer.getLeaveApplication()
				.getEmployeeLeaveSchemeType().getLeaveSchemeType()
				.getLeaveTypeMaster().getLeaveTypeName());
		addLeaveForm.setFromDate(DateUtils
				.timeStampToString(applicationReviewer.getLeaveApplication()
						.getStartDate()));
		addLeaveForm.setToDate(DateUtils.timeStampToString(applicationReviewer
				.getLeaveApplication().getEndDate()));
         /* ID ENCRYPT*/
		addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(applicationReviewer
				.getLeaveApplication().getLeaveApplicationId()));
		addLeaveForm.setReason(applicationReviewer.getLeaveApplication()
				.getReason());
		addLeaveForm.setStatus(setStatusMultilingualKey(applicationReviewer
				.getLeaveApplication().getLeaveStatusMaster()
				.getLeaveStatusName()));
		addLeaveForm.setStatusId(applicationReviewer.getLeaveApplication()
				.getLeaveStatusMaster().getLeaveStatusID());

		if (applicationReviewer.getLeaveApplication().getLeaveSessionMaster1() != null) {
			addLeaveForm.setFromSession(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1()
					.getSession());
			addLeaveForm.setFromSessionLabelKey(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1()
					.getSessionLabelKey());
			addLeaveForm.setFromSessionId(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster1()
					.getLeaveSessionId());
		}

		if (applicationReviewer.getLeaveApplication().getLeaveSessionMaster2() != null) {
			addLeaveForm.setToSession(applicationReviewer.getLeaveApplication()
					.getLeaveSessionMaster2().getSession());
			addLeaveForm.setToSessionLabelKey(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2()
					.getSessionLabelKey());
			addLeaveForm.setToSessionId(applicationReviewer
					.getLeaveApplication().getLeaveSessionMaster2()
					.getLeaveSessionId());
		}

		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		for (LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow : applicationReviewer
				.getLeaveApplication().getEmployeeLeaveSchemeType()
				.getLeaveSchemeType().getLeaveSchemeTypeWorkflows()) {
			if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
				allowOverride = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();

			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
				allowReject = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_FORWARD)) {
				allowForward = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			} else if (leaveSchemeTypeWorkflow
					.getWorkFlowRuleMaster()
					.getRuleName()
					.equalsIgnoreCase(
							PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_APPROVE)) {
				allowApprove = leaveSchemeTypeWorkflow.getWorkFlowRuleMaster()
						.getRuleValue();
			}

		}
		int reviewOrder = 0;
		for (LeaveApplicationReviewer leaveApplicationReviewer : applicationReviewer
				.getLeaveApplication().getLeaveApplicationReviewers()) {
			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				addLeaveForm.setApplyTo(leaveApplicationReviewer.getEmployee()
						.getEmail());
				addLeaveForm
						.setLeaveReviewer1(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setApplyToId(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 1;
					if (allowOverride.length() == 3
							&& allowOverride.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(0, 1).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}
			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {
				addLeaveForm
						.setLeaveReviewer2(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer2Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());

				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(1, 2).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}

			}

			if (leaveApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				addLeaveForm
						.setLeaveReviewer3(getEmployeeNameWithNumber(leaveApplicationReviewer
								.getEmployee()));
				addLeaveForm.setLeaveReviewer3Id(leaveApplicationReviewer
						.getEmployee().getEmployeeId());
				if (applicationReviewer.getEmployee().getEmployeeId() == leaveApplicationReviewer
						.getEmployee().getEmployeeId()) {
					reviewOrder = 2;
					if (allowOverride.length() == 3
							&& allowOverride.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanOverride(true);
					} else {
						pendingItemsForm.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanReject(true);
					} else {
						pendingItemsForm.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanApprove(true);
					} else {
						pendingItemsForm.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(2, 3).equals("1")) {
						pendingItemsForm.setCanForward(true);
					} else {
						pendingItemsForm.setCanForward(false);
					}
				}
			}
		}

		List<LeaveApplicationAttachmentDTO> leaveApplicationAttachmentDTOs = new ArrayList<LeaveApplicationAttachmentDTO>();

		for (LeaveApplicationAttachment leaveApplicationAttachment : applicationReviewer
				.getLeaveApplication().getLeaveApplicationAttachments()) {
			LeaveApplicationAttachmentDTO applicationAttachmentDTO = new LeaveApplicationAttachmentDTO();
			applicationAttachmentDTO.setFileName(leaveApplicationAttachment
					.getFileName());
			/* ID ENCRYPT*/
			applicationAttachmentDTO
					.setLeaveApplicationId(FormatPreserveCryptoUtil.encrypt(leaveApplicationAttachment
							.getLeaveApplicationAttachmentId()));
			leaveApplicationAttachmentDTOs.add(applicationAttachmentDTO);
		}

		addLeaveForm.setAttachmentList(leaveApplicationAttachmentDTOs);
		addLeaveForm.setTotalNoOfReviewers(applicationReviewer
				.getLeaveApplication().getLeaveApplicationReviewers().size());

		addLeaveForm.setLeaveAppEmp(getEmployeeName(applicationReviewer
				.getLeaveApplication().getEmployee()));
		addLeaveForm.setLeaveAppCreated(DateUtils
				.timeStampToStringWithTime(applicationReviewer
						.getLeaveApplication().getCreatedDate()));
		addLeaveForm.setLeaveAppRemarks(applicationReviewer
				.getLeaveApplication().getReason());
		addLeaveForm.setLeaveAppStatus(setStatusMultilingualKey("Submitted"));

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<LeaveApplicationWorkflowDTO>();
		Integer workFlowCount = 0;
		List<LeaveApplicationWorkflow> leaveApplicationWorkflows = new ArrayList<>(
				applicationReviewer.getLeaveApplication()
						.getLeaveApplicationWorkflows());
		Collections.sort(leaveApplicationWorkflows,
				new LeaveApplicationWorkflowComp());
		for (LeaveApplicationWorkflow leaveApplicationWorkflow : leaveApplicationWorkflows) {

			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();

			workFlowCount++;

			if (workFlowCount != 1) {
				applicationWorkflowDTO.setCreatedDate(DateUtils
						.timeStampToStringWithTime(leaveApplicationWorkflow
								.getCreatedDate()));
				applicationWorkflowDTO
						.setEmployeeInfo(getEmployeeNameWithNumber(leaveApplicationWorkflow
								.getEmployee()));
				applicationWorkflowDTO.setEmoployeeInfoId(leaveApplicationWorkflow.getEmployee().getEmployeeId());
				applicationWorkflowDTO.setRemarks(leaveApplicationWorkflow
						.getRemarks());

				applicationWorkflowDTO
						.setStatus(setStatusMultilingualKey(leaveApplicationWorkflow
								.getLeaveStatusMaster().getLeaveStatusName()));
				applicationWorkflowDTO.setCreatedDate(DateUtils
						.timeStampToStringWithTime(leaveApplicationWorkflow
								.getCreatedDate()));
			}

			if (leaveApplicationWorkflow.getEmployee().getEmployeeId() == applicationReviewer
					.getLeaveApplication().getEmployee().getEmployeeId()) {

				applicationWorkflowDTO.setOrder(0);

			}

			if (addLeaveForm.getApplyToId() != null
					&& addLeaveForm.getApplyToId() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null
					&& addLeaveForm.getLeaveReviewer2Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null
					&& addLeaveForm.getLeaveReviewer3Id() == leaveApplicationWorkflow
							.getEmployee().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		pendingItemsForm
				.setCreatedBy(getEmployeeNameWithNumber(applicationReviewer
						.getLeaveApplication().getEmployee()));
		pendingItemsForm.setCreatedById(FormatPreserveCryptoUtil.encrypt(applicationReviewer
				.getLeaveApplication().getEmployee().getEmployeeId()));
		if (reviewOrder == 1 && addLeaveForm.getLeaveReviewer2Id() != null) {
			Employee empReviewer2 = employeeDAO.findById(addLeaveForm
					.getLeaveReviewer2Id());
			pendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer2));
			pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.encrypt(empReviewer2.getEmployeeId()));
		} else if (reviewOrder == 2
				&& addLeaveForm.getLeaveReviewer3Id() != null) {
			Employee empReviewer3 = employeeDAO.findById(addLeaveForm
					.getLeaveReviewer3Id());
			pendingItemsForm
					.setForwardTo(getEmployeeNameWithNumber(empReviewer3));
			pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.encrypt(empReviewer3.getEmployeeId()));
		} else {
			pendingItemsForm.setForwardTo("");
		}

		pendingItemsForm
				.setTypeOfApplication(generalLogic
						.getTypeOfApplication(applicationReviewer
								.getLeaveApplication()));

		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		pendingItemsForm.setAddLeaveForm(addLeaveForm);
		return pendingItemsForm;
	}
	
	@Override
	public LeaveApplicationAttachmentDTO viewAttachmentByReviewer(Long attachmentId, Long empReviewerId, Long companyId) {

		LeaveApplicationAttachment attachment = leaveApplicationAttachmentDAO.viewAttachmentByReviewer(attachmentId, empReviewerId, companyId);
		if(attachment == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
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
	public boolean isSameCompanyGroupExist(Long leaveApplicationId,Long revCompanyId)
	{
		LeaveApplication leaveApplication = leaveApplicationDAO.findById(leaveApplicationId);
		
		Company revCompany = companyDAO.findById(revCompanyId);
		
		if(leaveApplication.getCompany().getCompanyGroup().getGroupId() == revCompany.getCompanyGroup().getGroupId() )
		{
			return true;
		}
		return false;
	}
	
}
class LeaveApplicationWorkflowList implements Comparator<LeaveApplicationWorkflowDTO>{

	@Override
	public int compare(LeaveApplicationWorkflowDTO o1, LeaveApplicationWorkflowDTO o2) {
		// TODO Auto-generated method stub
		return o1.getLeaveReviewerType().compareTo(o2.getLeaveReviewerType());
	}
	
}
