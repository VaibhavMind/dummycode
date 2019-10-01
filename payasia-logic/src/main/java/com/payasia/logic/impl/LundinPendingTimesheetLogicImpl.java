package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.OTTimesheetWorkflowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimApplicationItemDAO;
import com.payasia.dao.ClaimApplicationItemWorkflowDAO;
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.LundinPendingTimesheetLogic;
import com.payasia.logic.LundinTimesheetMailLogic;
import com.payasia.logic.LundinTimesheetPrintPDFLogic;

@Component
public class LundinPendingTimesheetLogicImpl implements
		LundinPendingTimesheetLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinPendingTimesheetLogicImpl.class);

	@Resource
	EmployeeTimesheetApplicationDAO lundinTimesheetDAO;
	@Resource
	TimesheetApplicationReviewerDAO lundinTimesheetReviewerDAO;
	@Resource
	TimesheetWorkflowDAO lundinWorkflowDAO;
	@Resource
	TimesheetStatusMasterDAO lundinTimesheetStatusMasterDAO;
	@Resource
	TimesheetApplicationWorkflowDAO lundinTimesheetWorkflowDAO;

	@Resource
	ClaimApplicationItemDAO claimApplicationItemDAO;

	@Resource
	ClaimApplicationItemWorkflowDAO claimApplicationItemWorkflowDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	ClaimStatusMasterDAO claimStatusMasterDAO;

	@Resource
	ClaimApplicationWorkflowDAO claimApplicationWorkflowDAO;

	@Resource
	ClaimMailLogic claimMailLogic;

	@Resource
	ModuleMasterDAO moduleMasterDAO;

	@Resource
	NotificationAlertDAO notificationAlertDAO;

	@Resource
	LundinTimesheetMailLogic lundinTimesheetMailLogic;

	@Resource
	LundinTimesheetPrintPDFLogic lundinTimesheetPrintPDFLogic;
	
	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Override
	public PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText) {
		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = new PendingOTTimesheetResponseForm();

		PendingOTTimesheetConditionDTO otTimesheetConditionDTO = new PendingOTTimesheetConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setCreatedDate(searchText.trim());
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setEmployeeName("%" + searchText.trim()
						+ "%");
			}

		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {

				otTimesheetConditionDTO.setBatch("%" + searchText.trim() + "%");
			}

		}
		int recordSize = 0;

		recordSize = lundinTimesheetReviewerDAO.findByConditionCountRecords(
				empId, otTimesheetConditionDTO);
		List<TimesheetApplicationReviewer> otReviewers = lundinTimesheetReviewerDAO
				.findByCondition(empId, pageDTO, sortDTO,
						otTimesheetConditionDTO);

		otTimesheetConditionDTO
				.setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_PENDING);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();

		for (TimesheetApplicationReviewer otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			EmployeeTimesheetApplication lundinOTTimesheet = otReviewer
					.getEmployeeTimesheetApplication();
			TimesheetBatch lundinOtBatch = lundinOTTimesheet
					.getTimesheetBatch();

			String empName = getEmployeeName(lundinOTTimesheet.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils
					.timeStampToString(lundinOTTimesheet.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:viewPendingLundinTimesheetTemplateItems("
							+ FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getTimesheetId())
							+ ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc()
							+ "&apos;"
							+ ");'>Review" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(lundinOTTimesheet
					.getTimesheetId());
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch
					.getTimesheetBatchDesc());
			pendingOTTimesheetForm.setOtTimesheetReviewerId(otReviewer
					.getTimesheetReviewerId());
			pendingOTTimesheetForms.add(pendingOTTimesheetForm);
		}

		pendingOTTimesheetResponseForm
				.setPendingOTTimesheets(pendingOTTimesheetForms);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			pendingOTTimesheetResponseForm.setPage(pageDTO.getPageNumber());
			pendingOTTimesheetResponseForm.setTotal(totalPages);
			pendingOTTimesheetResponseForm.setRecords(recordSize);
		}
		return pendingOTTimesheetResponseForm;

	}

	@Override
	public LundinPendingItemsForm getPendingItemForReview(long timesheetId,
			Long employeeId, long companyId) {
		LundinPendingItemsForm toReturn = new LundinPendingItemsForm();
		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		
			
		EmployeeTimesheetApplication timesheetApplication = lundinTimesheetDAO
				.findTimesheetByCompanyId(timesheetId,companyId);

		toReturn.setEmployeeName(getEmployeeName(timesheetApplication
				.getEmployee()));

		List<TimesheetWorkflow> otWorkflows = lundinWorkflowDAO
				.findByCompanyId(timesheetApplication.getCompany()
						.getCompanyId());
		for (TimesheetWorkflow otWorkflow : otWorkflows) {
			WorkFlowRuleMaster ruleMaster = otWorkflow.getWorkFlowRuleMaster();
			if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
				allowOverride = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_REJECT)) {
				allowReject = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_FORWARD)) {
				allowForward = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(
					PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_APPROVE)) {
				allowApprove = ruleMaster.getRuleValue();
			}
		}
		for (TimesheetApplicationReviewer reviewer : timesheetApplication
				.getTimesheetApplicationReviewers()) {
			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

				if (employeeId.equals(reviewer.getEmployee().getEmployeeId())) {
					if (allowOverride.length() == 3
							&& allowOverride.substring(0, 1).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(0, 1).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(0, 1).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(0, 1).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}

			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

				if (employeeId.equals(reviewer.getEmployee().getEmployeeId())) {
					if (allowOverride.length() == 3
							&& allowOverride.substring(1, 2).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(1, 2).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(1, 2).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(1, 2).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}

			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

				if (employeeId.equals(reviewer.getEmployee().getEmployeeId())) {
					if (allowOverride.length() == 3
							&& allowOverride.substring(2, 3).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3
							&& allowReject.substring(2, 3).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3
							&& allowApprove.substring(2, 3).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3
							&& allowForward.substring(2, 3).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}
		}
		toReturn.setCompanyId(timesheetApplication.getCompany().getCompanyId());
		toReturn.setEmployeeId(employeeId);
		
		/* ID ENCRYPT*/
		toReturn.setTimesheetId(FormatPreserveCryptoUtil.encrypt(timesheetApplication.getTimesheetId()));
		
		toReturn.setRemarks("");
		return toReturn;
		
		
	}

	/**
	 * Comparator Class for Ordering ClaimApplicationReviewer List
	 */
	private class LundinTimesheetReviewerComp implements
			Comparator<TimesheetApplicationReviewer> {
		public int compare(TimesheetApplicationReviewer templateField,
				TimesheetApplicationReviewer compWithTemplateField) {
			if (templateField.getTimesheetReviewerId() > compWithTemplateField
					.getTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getTimesheetReviewerId() < compWithTemplateField
					.getTimesheetReviewerId()) {
				return -1;
			}
			return 0;

		}

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

	@Override
	public PendingOTTimesheetForm forwardTimesheet(
			PendingOTTimesheetForm pendingOtTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessfullyFor = false;
		EmployeeTimesheetApplication lundinOTTimesheetVO = null;
		TimesheetApplicationReviewer lundinOTTimesheetReviewer2 = null;
		TimesheetApplicationWorkflow lundinOTTimesheetWorkflow = null;
		Employee employee = employeeDAO.findById(employeeId);
		Date date = new Date();
		TimesheetApplicationReviewer lundinOTTimesheetReviewer = lundinTimesheetReviewerDAO
				.findByCondition(pendingOtTimesheetForm.getOtTimesheetId(),
						pendingOtTimesheetForm.getOtTimesheetReviewerId());
		String workflowLevel = String.valueOf(lundinTimesheetReviewerDAO
				.getOTTimesheetReviewerCount(lundinOTTimesheetReviewer
						.getEmployeeTimesheetApplication().getTimesheetId()));
		if (workflowLevel != null
				&& lundinOTTimesheetReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equalsIgnoreCase(workflowLevel)) {
			response = acceptTimesheet(pendingOtTimesheetForm, employeeId,
					companyId);
		} else {
			try {
				TimesheetApplicationWorkflow applicationWorkflow = new TimesheetApplicationWorkflow();

				for (TimesheetWorkflow otWorkflow : lundinWorkflowDAO
						.findByCompanyId(companyId)) {
					if (otWorkflow
							.getWorkFlowRuleMaster()
							.getRuleName()
							.equalsIgnoreCase(
									PayAsiaConstants.OT_DEF_WORKFLOW_LEVEL)) {
						workflowLevel = otWorkflow.getWorkFlowRuleMaster()
								.getRuleValue();

					}
				}
				TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
						.findByName(PayAsiaConstants.OT_STATUS_APPROVED);

				lundinOTTimesheetVO = lundinOTTimesheetReviewer
						.getEmployeeTimesheetApplication();

				// Validating claim application item.
				Boolean isAdmin = false;

				TimesheetStatusMaster otStatusCompleted = null;
				if (lundinOTTimesheetReviewer.getWorkFlowRuleMaster()
						.getRuleValue().equalsIgnoreCase(workflowLevel)) {
					otStatusCompleted = lundinTimesheetStatusMasterDAO
							.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
					lundinOTTimesheetVO
							.setTimesheetStatusMaster(otStatusCompleted);
				} else {

					lundinOTTimesheetReviewer2 = lundinTimesheetReviewerDAO
							.findById(lundinOTTimesheetReviewer
									.getTimesheetReviewerId() + 1);
					lundinOTTimesheetReviewer2.setPending(true);
					applicationWorkflow.setForwardTo(lundinOTTimesheetReviewer2
							.getEmployee().getEmail());

					lundinTimesheetReviewerDAO
							.update(lundinOTTimesheetReviewer2);
				}
				lundinOTTimesheetVO
						.setUpdatedDate(new Timestamp(date.getTime()));
				lundinTimesheetDAO.update(lundinOTTimesheetVO);

				lundinOTTimesheetReviewer.setPending(false);
				lundinOTTimesheetReviewer.setEmployee(employee);
				lundinTimesheetReviewerDAO.update(lundinOTTimesheetReviewer);

				applicationWorkflow.setCreatedBy(employee);
				applicationWorkflow
						.setEmployeeTimesheetApplication(lundinOTTimesheetReviewer
								.getEmployeeTimesheetApplication());
				applicationWorkflow.setEmailCC(pendingOtTimesheetForm
						.getEmailCC());
				applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
				applicationWorkflow.setRemarks(pendingOtTimesheetForm
						.getRemarks());
				applicationWorkflow
						.setCreatedDate(new Timestamp(date.getTime()));
				lundinOTTimesheetWorkflow = lundinTimesheetWorkflowDAO
						.saveReturn(applicationWorkflow);
				isSuccessfullyFor = true;
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
			}
			if (isSuccessfullyFor) {
				lundinTimesheetMailLogic
						.sendPendingEmailForTimesheet(
								companyId,
								lundinOTTimesheetWorkflow,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_FORWARD,
								employee, pendingOtTimesheetForm.getRemarks(),
								lundinOTTimesheetReviewer2.getEmployee());
			}
		}

		return response;

	}

	@Override
	public PendingOTTimesheetForm acceptTimesheet(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		Boolean isSuccessfullyAcc = false;
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		EmployeeTimesheetApplication lundinOTTimesheetVO = null;
		TimesheetApplicationWorkflow lundinOTTimesheetWorkflow = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			TimesheetApplicationReviewer otApplicationReviewer = lundinTimesheetReviewerDAO
					.findByCondition(pendingOTTimesheetForm.getOtTimesheetId(),
							pendingOTTimesheetForm.getOtTimesheetReviewerId());

			TimesheetApplicationWorkflow applicationWorkflow = new TimesheetApplicationWorkflow();

			lundinOTTimesheetVO = otApplicationReviewer
					.getEmployeeTimesheetApplication();

			List<String> otApprovedStatusList = new ArrayList<>();
			otApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			TimesheetStatusMaster otStatusCompleted = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
			lundinOTTimesheetVO.setTimesheetStatusMaster(otStatusCompleted);

			lundinOTTimesheetVO.setUpdatedDate(new Timestamp(date.getTime()));
			lundinTimesheetDAO.update(lundinOTTimesheetVO);

			otApplicationReviewer.setPending(false);
			otApplicationReviewer.setEmployee(employee);
			lundinTimesheetReviewerDAO.update(otApplicationReviewer);

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow
					.setEmployeeTimesheetApplication(otApplicationReviewer
							.getEmployeeTimesheetApplication());
			applicationWorkflow.setEmailCC(pendingOTTimesheetForm.getEmailCC());
			applicationWorkflow.setTimesheetStatusMaster(otStatusCompleted);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			lundinOTTimesheetWorkflow = lundinTimesheetWorkflowDAO
					.saveReturn(applicationWorkflow);
			isSuccessfullyAcc = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		if (isSuccessfullyAcc) {

			lundinTimesheetMailLogic
					.sendAcceptRejectMailForTimesheet(
							companyId,
							lundinOTTimesheetWorkflow,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPROVE,
							employee, pendingOTTimesheetForm.getRemarks());
		}

		return response;

	}

	@Override
	public PendingOTTimesheetForm rejectTimesheet(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessRejeted = false;
		EmployeeTimesheetApplication lundinOTTimesheetVO = null;
		TimesheetApplicationWorkflow workFlow = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			TimesheetApplicationReviewer otApplicationReviewer = lundinTimesheetReviewerDAO
					.findByCondition(pendingOTTimesheetForm.getOtTimesheetId(),
							pendingOTTimesheetForm.getOtTimesheetReviewerId());
			TimesheetApplicationWorkflow applicationWorkflow = new TimesheetApplicationWorkflow();

			TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_REJECTED);
			lundinOTTimesheetVO = otApplicationReviewer
					.getEmployeeTimesheetApplication();

			List<String> claimApprovedStatusList = new ArrayList<>();
			claimApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			lundinOTTimesheetVO.setTimesheetStatusMaster(otStatusMaster);
			lundinOTTimesheetVO.setUpdatedDate(new Timestamp(date.getTime()));
			lundinTimesheetDAO.update(lundinOTTimesheetVO);

			for (TimesheetApplicationReviewer applicationReviewer : lundinOTTimesheetVO
					.getTimesheetApplicationReviewers()) {
				applicationReviewer.setPending(false);
				lundinTimesheetReviewerDAO.update(applicationReviewer);
			}

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow
					.setEmployeeTimesheetApplication(otApplicationReviewer
							.getEmployeeTimesheetApplication());
			applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
			applicationWorkflow.setEmailCC(pendingOTTimesheetForm.getEmailCC());
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			workFlow = lundinTimesheetWorkflowDAO
					.saveReturn(applicationWorkflow);
			isSuccessRejeted = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		if (isSuccessRejeted) {

			lundinTimesheetMailLogic
					.sendAcceptRejectMailForTimesheet(
							companyId,
							workFlow,
							PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_REJECT,
							employee, pendingOTTimesheetForm.getRemarks());
		}
		return response;
	}

	@Override
	public OTPendingTimesheetForm getDataForPendingOtReviewWorkflow(
			Long otTimesheetId) {
		OTPendingTimesheetForm otPendingTimesheetForm = new OTPendingTimesheetForm();

		EmployeeTimesheetApplication lundinOTTimesheet = lundinTimesheetDAO
				.findById(otTimesheetId);

		otPendingTimesheetForm
				.setCreatedBy(getEmployeeNameWithNumber(lundinOTTimesheet
						.getEmployee()));

		otPendingTimesheetForm.setCreatedDate(DateUtils
				.timeStampToStringWithTime(lundinOTTimesheet.getCreatedDate()));
		List<TimesheetApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				lundinOTTimesheet.getTimesheetApplicationWorkflows());
		Collections.sort(applicationWorkflows, new OTTimesheetWorkFlowComp());
		Integer workFlowCount = 0;

		if (lundinOTTimesheet.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.HRIS_STATUS_WITHDRAWN)) {
			otPendingTimesheetForm
					.setUserStatus(PayAsiaConstants.HRIS_STATUS_WITHDRAWN);
		} else {
			otPendingTimesheetForm
					.setUserStatus(PayAsiaConstants.HRIS_STATUS_SUBMITTED);

		}

		for (TimesheetApplicationReviewer lundinOTTimesheetReviewer : lundinOTTimesheet
				.getTimesheetApplicationReviewers()) {

			if (lundinOTTimesheetReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("1")) {
				otPendingTimesheetForm
						.setOtTimesheetReviewer1(getEmployeeNameWithNumber(lundinOTTimesheetReviewer
								.getEmployee()));
				otPendingTimesheetForm
						.setOtTimesheetReviewer1Id(lundinOTTimesheetReviewer
								.getEmployee().getEmployeeId());
			}

			if (lundinOTTimesheetReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("2")) {
				otPendingTimesheetForm
						.setOtTimesheetReviewer2(getEmployeeNameWithNumber(lundinOTTimesheetReviewer
								.getEmployee()));
				otPendingTimesheetForm
						.setOtTimesheetReviewer2Id(lundinOTTimesheetReviewer
								.getEmployee().getEmployeeId());
			}
			if (lundinOTTimesheetReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("3")) {
				otPendingTimesheetForm
						.setOtTimesheetReviewer3(getEmployeeNameWithNumber(lundinOTTimesheetReviewer
								.getEmployee()));
				otPendingTimesheetForm
						.setOtTimesheetReviewer3Id(lundinOTTimesheetReviewer
								.getEmployee().getEmployeeId());
			}
		}

		otPendingTimesheetForm.setTotalNoOfReviewers(lundinOTTimesheet
				.getTimesheetApplicationReviewers().size());
		List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms = new ArrayList<>();
		for (TimesheetApplicationWorkflow lundinOTTimesheetWorkflow : applicationWorkflows) {

			OTTimesheetWorkflowForm otTimesheetWorkflowForm = new OTTimesheetWorkflowForm();
			otTimesheetWorkflowForm.setRemarks(lundinOTTimesheetWorkflow
					.getRemarks());
			otTimesheetWorkflowForm.setStatus(lundinOTTimesheetWorkflow
					.getTimesheetStatusMaster().getTimesheetStatusName());
			otTimesheetWorkflowForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(lundinOTTimesheetWorkflow
							.getCreatedDate()));
			otTimesheetWorkflowForm.setOrder(workFlowCount);

			otTimesheetWorkflowForms.add(otTimesheetWorkflowForm);
			workFlowCount++;
		}
		otPendingTimesheetForm
				.setOtTimesheetWorkflowForms(otTimesheetWorkflowForms);
		return otPendingTimesheetForm;
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

	private class OTTimesheetWorkFlowComp implements
			Comparator<TimesheetApplicationWorkflow> {

		@Override
		public int compare(TimesheetApplicationWorkflow templateField,
				TimesheetApplicationWorkflow compWithTemplateField) {
			if (templateField.getTimesheetWorkflowId() > compWithTemplateField
					.getTimesheetWorkflowId()) {
				return 1;
			} else if (templateField.getTimesheetWorkflowId() < compWithTemplateField
					.getTimesheetWorkflowId()) {
				return -1;
			}
			return 0;

		}

	}
//remove employee check to print the Lundin TS data
	@Override
	public TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId,
			boolean hasLundinTimesheetModule,
			LundinTimesheetDTO lundinTimesheetDTO) {
		
	/*	TimesheetApplicationReviewer timesheetApplicationReviewer = lundinTimesheetReviewerDAO.findByCondition(timesheetId,employeeId);
		if(timesheetApplicationReviewer!=null) {*/
		TimesheetFormPdfDTO timesheetFormPdfDTO = new TimesheetFormPdfDTO();
		EmployeeTimesheetApplication lundinTimesheetVO = lundinTimesheetDAO
				.findById(timesheetId);
		timesheetFormPdfDTO.setTimesheetBatchDesc(lundinTimesheetVO
				.getTimesheetBatch().getTimesheetBatchDesc());
		timesheetFormPdfDTO.setEmployeeNumber(lundinTimesheetVO.getEmployee()
				.getEmployeeNumber());
		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				timesheetFormPdfDTO
						.setTimesheetPdfByteFile(generateTimesheetFormPDF(
								companyId, employeeId, timesheetId,
								hasLundinTimesheetModule, lundinTimesheetDTO));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				timesheetFormPdfDTO
						.setTimesheetPdfByteFile(generateTimesheetFormPDF(
								companyId, employeeId, timesheetId,
								hasLundinTimesheetModule, lundinTimesheetDTO));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}
	//	}
		//return null;
	}

	private byte[] generateTimesheetFormPDF(Long companyId, Long employeeId,
			Long timesheetId, boolean hasLundinTimesheetModule,
			LundinTimesheetDTO lundinTimesheetDTO) throws DocumentException,
			IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId,
				PAYASIA_TEMP_PATH, "Timesheet");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PageSize.A4.rotate(), 88f, 88f, 10f, 10f);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			PdfPTable lundinTimesheetPdfTable = lundinTimesheetPrintPDFLogic
					.createTimesheetPdf(document, writer, 1, companyId,
							timesheetId, hasLundinTimesheetModule,
							lundinTimesheetDTO);

			document.add(lundinTimesheetPdfTable);

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

}
