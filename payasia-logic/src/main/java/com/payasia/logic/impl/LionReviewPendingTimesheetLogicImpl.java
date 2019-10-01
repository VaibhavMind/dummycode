package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.OTTimesheetWorkflowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PDFThreadLocal;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.LionEmployeeTimesheetDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.LionTimesheetApplicationWorkflowDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LionEmployeeTimesheet;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.LionTimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LionReviewPendingTimesheetLogic;
import com.payasia.logic.LionTimesheetMailLogic;
import com.payasia.logic.LundinTimesheetMailLogic;
import com.payasia.logic.LundinTimesheetPrintPDFLogic;

@Component
public class LionReviewPendingTimesheetLogicImpl extends BaseLogic implements
		LionReviewPendingTimesheetLogic {

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
	WorkflowDelegateDAO workflowDelegateDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	ClaimStatusMasterDAO claimStatusMasterDAO;

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

	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetReviewerDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;

	@Resource
	LionEmployeeTimesheetDAO lionEmployeeTimesheetDAO;

	@Resource
	TimesheetStatusMasterDAO timesheetStatusMasterDAO;

	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;

	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetApplicationReviewerDAO;

	@Resource
	LionTimesheetApplicationWorkflowDAO lionTimesheetApplicationWorkflowDAO;

	@Resource
	EmployeeTimesheetApplicationDAO employeeTimesheetApplicationDAO;

	@Resource
	EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;

	@Resource
	LionTimesheetMailLogic lionTimesheetMailLogic;

	@Resource
	GeneralLogic generalLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Override
	public PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, String pageContextPath, long companyId) {
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

		recordSize = lionTimesheetReviewerDAO.getCountByCondition(companyId,
				pageDTO, sortDTO, otTimesheetConditionDTO).size();

		List<Tuple> otReviewers = lionTimesheetReviewerDAO
				.findAllReviewListByCondition(companyId, pageDTO, sortDTO,
						otTimesheetConditionDTO);

		otTimesheetConditionDTO
				.setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_PENDING);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();

		for (Tuple otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			String firstName = (String) otReviewer.get(
					getAlias(Employee_.firstName), String.class);
			String lastName = (String) otReviewer.get(
					getAlias(Employee_.lastName), String.class);
			String empNum = (String) otReviewer.get(
					getAlias(Employee_.employeeNumber), String.class);

			pendingOTTimesheetForm.setCreatedBy(getEmployeeNameFromInputs(
					firstName, lastName, empNum));

			pendingOTTimesheetForm
					.setCreatedDate(DateUtils.timeStampToString((Timestamp) otReviewer
							.get(getAlias(EmployeeTimesheetApplication_.createdDate),
									Timestamp.class)));
			StringBuilder claimTemplateItemCount = new StringBuilder();

			long timesheetId = (Long) otReviewer.get(
					getAlias(EmployeeTimesheetApplication_.timesheetId),
					Long.class);

			EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
					.findById(timesheetId);

			if ((employeeTimesheetApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName())
					.equals(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED)) {
				/*ID ENCRYPT */
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionAdminTimesheetTemplateItems("
								+ FormatPreserveCryptoUtil.encrypt((Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>View" + "</a>");

			} else {
				/*ID ENCRYPT */
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionAdminTimesheetTemplateItems("
								+ FormatPreserveCryptoUtil.encrypt((Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>Review" + "</a>");

			}
			Employee reviewer = getReviewer(employeeTimesheetApplication);

			String reviewerImage = getStatusImage(employeeTimesheetApplication,
					pageContextPath, reviewer, companyId);
			pendingOTTimesheetForm.setEmailCC(reviewerImage);
			pendingOTTimesheetForm.setRemarks(employeeTimesheetApplication
					.getTimesheetStatusMaster().getTimesheetStatusName());
			pendingOTTimesheetForm.setOtTimesheetId(timesheetId);
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm
					.setBatchDesc((String) otReviewer.get(
							getAlias(TimesheetBatch_.timesheetBatchDesc),
							String.class));

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
	public PendingOTTimesheetResponseForm getSubmittedTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, String pageContextPath, long companyId) {
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

		otTimesheetConditionDTO
				.setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		List<Tuple> otReviewers = lionTimesheetReviewerDAO
				.findAllReviewListByCondition(companyId, pageDTO, sortDTO,
						otTimesheetConditionDTO);

		recordSize = lionTimesheetReviewerDAO.getCountByCondition(companyId,
				pageDTO, sortDTO, otTimesheetConditionDTO).size();

		otTimesheetConditionDTO
				.setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_PENDING);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();

		for (Tuple otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			String firstName = (String) otReviewer.get(
					getAlias(Employee_.firstName), String.class);
			String lastName = (String) otReviewer.get(
					getAlias(Employee_.lastName), String.class);
			String empNum = (String) otReviewer.get(
					getAlias(Employee_.employeeNumber), String.class);

			pendingOTTimesheetForm.setCreatedBy(getEmployeeNameFromInputs(
					firstName, lastName, empNum));

			pendingOTTimesheetForm
					.setCreatedDate(DateUtils.timeStampToString((Timestamp) otReviewer
							.get(getAlias(EmployeeTimesheetApplication_.createdDate),
									Timestamp.class)));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			long timesheetId = (Long) otReviewer.get(
					getAlias(EmployeeTimesheetApplication_.timesheetId),
					Long.class);

			EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
					.findById(timesheetId);

			if ((employeeTimesheetApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName())
					.equals(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED)) {
				/*ID ENCRYPT */
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionAdminTimesheetTemplateItems("
								+FormatPreserveCryptoUtil.encrypt((Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>View" + "</a>");

			} else {
				/*ID ENCRYPT */
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionAdminTimesheetTemplateItems("
								+FormatPreserveCryptoUtil.encrypt ((Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>Review" + "</a>");

			}

			Employee reviewer = getReviewer(employeeTimesheetApplication);

			String reviewerImage = getStatusImage(employeeTimesheetApplication,
					pageContextPath, reviewer, companyId);
			pendingOTTimesheetForm.setEmailCC(reviewerImage);
			pendingOTTimesheetForm.setRemarks(employeeTimesheetApplication
					.getTimesheetStatusMaster().getTimesheetStatusName());
			
			pendingOTTimesheetForm.setOtTimesheetId(timesheetId);
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm
					.setBatchDesc((String) otReviewer.get(
							getAlias(TimesheetBatch_.timesheetBatchDesc),
							String.class));

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
	public PendingOTTimesheetResponseForm getApprovedTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, String pageContextPath, long companyId) {
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

		otTimesheetConditionDTO
				.setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_COMPLETED);

		List<Tuple> otReviewers = lionTimesheetReviewerDAO
				.findAllReviewListByCondition(companyId, pageDTO, sortDTO,
						otTimesheetConditionDTO);

		recordSize = lionTimesheetReviewerDAO.getCountByCondition(companyId,
				pageDTO, sortDTO, otTimesheetConditionDTO).size();

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();

		for (Tuple otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			String firstName = (String) otReviewer.get(
					getAlias(Employee_.firstName), String.class);
			String lastName = (String) otReviewer.get(
					getAlias(Employee_.lastName), String.class);
			String empNum = (String) otReviewer.get(
					getAlias(Employee_.employeeNumber), String.class);

			pendingOTTimesheetForm.setCreatedBy(getEmployeeNameFromInputs(
					firstName, lastName, empNum));

			pendingOTTimesheetForm
					.setCreatedDate(DateUtils.timeStampToString((Timestamp) otReviewer
							.get(getAlias(EmployeeTimesheetApplication_.createdDate),
									Timestamp.class)));
			StringBuilder claimTemplateItemCount = new StringBuilder();

			long timesheetId = (Long) otReviewer.get(
					getAlias(EmployeeTimesheetApplication_.timesheetId),
					Long.class);

			EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
					.findById(timesheetId);

			if ((employeeTimesheetApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName())
					.equals(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED)) {
				/*ID ENCRYPT */
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionAdminTimesheetTemplateItems("
								+FormatPreserveCryptoUtil.encrypt( (Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>View" + "</a>");

			} else {
				/*ID ENCRYPT */
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionAdminTimesheetTemplateItems("
								+FormatPreserveCryptoUtil.encrypt( (Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>Review" + "</a>");

			}
			Employee reviewer = getReviewer(employeeTimesheetApplication);

			String reviewerImage = getStatusImage(employeeTimesheetApplication,
					pageContextPath, reviewer, companyId);
			pendingOTTimesheetForm.setEmailCC(reviewerImage);
			pendingOTTimesheetForm.setRemarks(employeeTimesheetApplication
					.getTimesheetStatusMaster().getTimesheetStatusName());
			pendingOTTimesheetForm.setOtTimesheetId((Long) otReviewer.get(
					getAlias(EmployeeTimesheetApplication_.timesheetId),
					Long.class));
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm
					.setBatchDesc((String) otReviewer.get(
							getAlias(TimesheetBatch_.timesheetBatchDesc),
							String.class));

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
				.findById(timesheetId);

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
		toReturn.setTimesheetId(timesheetApplication.getTimesheetId());
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

	private String getEmployeeNameFromInputs(String firstName, String lastName,
			String empNum) {

		String employeeName = firstName;

		if (lastName != null) {
			employeeName = employeeName + " " + lastName;
		}
		employeeName = employeeName + " (" + empNum + ")";
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

	@Override
	public TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId, boolean hasLundinTimesheetModule) {
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
								hasLundinTimesheetModule));
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
								hasLundinTimesheetModule));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException
					| SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}
	}

	private byte[] generateTimesheetFormPDF(Long companyId, Long employeeId,
			Long timesheetId, boolean hasLundinTimesheetModule)
			throws DocumentException, IOException, JAXBException, SAXException {
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
			LundinTimesheetDTO lundinTimesheetDTO = new LundinTimesheetDTO();
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

	@Override
	public Map<String, String> updateLionEmployeeTimesheetApplicationDetail(
			long employeeTimesheetDetailId, String inTime, String outTime,
			String breakTime, String totalHours, String remarks) {

		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;

		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);
		lionEmployeeTimesheetApplicationDetail.setRemarks(remarks);
		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime()
				.toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail
				.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail
				.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":"
					+ oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail
					.setInTime(newInTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setInTimeChanged(true);
			inTimeStatus = 1;
		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":"
					+ oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail
					.setOutTime(newOutTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setOutTimeChanged(true);
			outTimeStatus = 1;
		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":"
					+ oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail
					.setBreakTimeHours(newBreakTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail
					.setBreakTimeHoursChanged(true);
			breakTimeStatus = 1;
		}

		Double totalHoursOld = lionEmployeeTimesheetApplicationDetail
				.getTotalHoursWorked();
		Double totalHoursD = Double.parseDouble(totalHours);
		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(totalHoursD);
		TimesheetStatusMaster timesheetStatusMaster = timesheetStatusMasterDAO
				.findByName("Submitted");

		lionEmployeeTimesheetApplicationDetail
				.setTimesheetStatusMaster(timesheetStatusMaster);
		lionEmployeeTimesheetApplicationDetailDAO
				.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail
								.getEmployeeTimesheetApplication()
								.getTimesheetId()).get(0);

		Double grandTotalHoursOld = lionEmployeeTimesheet
				.getTimesheetTotalHours();
		Double grandTotalHoursNew = grandTotalHoursOld - totalHoursOld
				+ totalHoursD;

		Double excessHoursWorked = grandTotalHoursNew - 104.3;

		if (excessHoursWorked < 0) {
			excessHoursWorked = 0.0;
		}

		lionEmployeeTimesheet.setTimesheetTotalHours(grandTotalHoursNew);

		lionEmployeeTimesheet.setExcessHoursWorked(excessHoursWorked);

		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
				.findByEmployeeTimesheetDetailId(employeeTimesheetDetailId);

		if (lionTimesheetApplicationReviewer == null) {
			EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO
					.findByEmployeeId(lionEmployeeTimesheetApplicationDetail
							.getEmployeeTimesheetApplication().getEmployee()
							.getEmployeeId());

			Employee reviewer = getDelegatedEmployee(employeeTimesheetReviewer
					.getEmployeeReviewer().getEmployeeId());

			LionTimesheetApplicationReviewer newLionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
			newLionTimesheetApplicationReviewer
					.setLionEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
			newLionTimesheetApplicationReviewer
					.setWorkFlowRuleMaster(employeeTimesheetReviewer
							.getWorkFlowRuleMaster());
			newLionTimesheetApplicationReviewer.setEmployeeReviewer(reviewer);
			newLionTimesheetApplicationReviewer
					.setReviewerEmail(employeeTimesheetReviewer
							.getReviewerEmail());
			newLionTimesheetApplicationReviewer.setPending(true);

			lionTimesheetApplicationReviewerDAO
					.save(newLionTimesheetApplicationReviewer);

		}

		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));
		statusMap.put("grandTotalHours", grandTotalHoursNew.toString());
		statusMap.put("excessHoursWorked", excessHoursWorked.toString());

		return statusMap;

	}

	@Override
	public Map<String, String> approveLionEmployeeTimesheetApplicationDetail(
			long employeeId, long employeeTimesheetDetailId, String inTime,
			String outTime, String breakTime, String totalHours, String remarks) {

		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;

		boolean isSuccessfullyFor = false;

		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);
		lionEmployeeTimesheetApplicationDetail.setRemarks(remarks);
		long timesheetId = lionEmployeeTimesheetApplicationDetail
				.getEmployeeTimesheetApplication().getTimesheetId();

		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime()
				.toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail
				.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail
				.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		String oldTotalHours = lionEmployeeTimesheetApplicationDetail
				.getTotalHoursWorked().toString();

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":"
					+ oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail
					.setInTime(newInTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setInTimeChanged(true);
			inTimeStatus = 1;
		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":"
					+ oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail
					.setOutTime(newOutTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setOutTimeChanged(true);
			outTimeStatus = 1;
		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":"
					+ oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail
					.setBreakTimeHours(newBreakTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail
					.setBreakTimeHoursChanged(true);
			breakTimeStatus = 1;
		}

		TimesheetStatusMaster timesheetStatusMaster = timesheetStatusMasterDAO
				.findByName("Approved");

		lionEmployeeTimesheetApplicationDetail
				.setTimesheetStatusMaster(timesheetStatusMaster);

		Double totalHoursOld = lionEmployeeTimesheetApplicationDetail
				.getTotalHoursWorked();
		Double totalHoursD = Double.parseDouble(totalHours);
		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(totalHoursD);

		lionEmployeeTimesheetApplicationDetailDAO
				.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail
								.getEmployeeTimesheetApplication()
								.getTimesheetId()).get(0);

		Double grandTotalHoursOld = lionEmployeeTimesheet
				.getTimesheetTotalHours();
		Double grandTotalHoursNew = grandTotalHoursOld - totalHoursOld
				+ totalHoursD;

		Double excessHoursWorked = grandTotalHoursNew - 104.3;

		if (excessHoursWorked < 0) {
			excessHoursWorked = 0.0;
		}

		lionEmployeeTimesheet.setTimesheetTotalHours(grandTotalHoursNew);

		lionEmployeeTimesheet.setExcessHoursWorked(excessHoursWorked);
		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
				.findByEmployeeTimesheetDetailId(lionEmployeeTimesheetApplicationDetail
						.getEmployeeTimesheetDetailID());

		if (lionTimesheetApplicationReviewer == null) {
			EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO
					.findByEmployeeId(lionEmployeeTimesheetApplicationDetail
							.getEmployeeTimesheetApplication().getEmployee()
							.getEmployeeId());

			Employee reviewer = getDelegatedEmployee(employeeTimesheetReviewer
					.getEmployeeReviewer().getEmployeeId());

			LionTimesheetApplicationReviewer newLionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
			newLionTimesheetApplicationReviewer
					.setLionEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
			newLionTimesheetApplicationReviewer
					.setWorkFlowRuleMaster(employeeTimesheetReviewer
							.getWorkFlowRuleMaster());
			newLionTimesheetApplicationReviewer.setEmployeeReviewer(reviewer);
			newLionTimesheetApplicationReviewer
					.setReviewerEmail(employeeTimesheetReviewer
							.getReviewerEmail());
			newLionTimesheetApplicationReviewer.setPending(false);

			lionTimesheetApplicationReviewerDAO
					.save(newLionTimesheetApplicationReviewer);

		} else {
			lionTimesheetApplicationReviewer.setPending(false);

			lionTimesheetApplicationReviewerDAO
					.update(lionTimesheetApplicationReviewer);
		}

		LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow = new LionTimesheetApplicationWorkflow();
		lionTimesheetApplicationWorkflow
				.setEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
		lionTimesheetApplicationWorkflow
				.setTimesheetStatusMaster(timesheetStatusMaster);
		lionTimesheetApplicationWorkflow.setCreatedDate(DateUtils
				.getCurrentTimestampWithTime());
		lionTimesheetApplicationWorkflow.setCreatedBy(employeeDAO
				.findById(employeeId));

		lionTimesheetApplicationWorkflowDAO
				.save(lionTimesheetApplicationWorkflow);

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(lionEmployeeTimesheetApplicationDetail
						.getEmployeeTimesheetApplication().getTimesheetId());

		int timesheetStatusCheck = 0;

		List<LionEmployeeTimesheetApplicationDetail> unApprovedTimesheet = new ArrayList<LionEmployeeTimesheetApplicationDetail>();

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail2 : lionEmployeeTimesheetApplicationDetails) {
			if (!lionEmployeeTimesheetApplicationDetail2
					.getTimesheetStatusMaster().getTimesheetStatusName()
					.equals("Approved")) {
				timesheetStatusCheck = 1;
				unApprovedTimesheet
						.add(lionEmployeeTimesheetApplicationDetail2);
			}
		}

		int timesheetStatusHolidayCheck = 0;

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(lionEmployeeTimesheetApplicationDetail
						.getEmployeeTimesheetApplication().getTimesheetId());

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail2 : unApprovedTimesheet) {

			Date timesheetDate = new Date(
					lionEmployeeTimesheetApplicationDetail2.getTimesheetDate()
							.getTime());

			List<String> holidayList = getHolidaysFor(
					employeeTimesheetApplication.getEmployee().getEmployeeId(),
					timesheetDate, timesheetDate);
			if (holidayList.size() == 0) {
				timesheetStatusHolidayCheck = 1;
			}
		}

		if (timesheetStatusCheck == 0 || timesheetStatusHolidayCheck == 0) {

			TimesheetStatusMaster timesheetStatusMaster2 = timesheetStatusMasterDAO
					.findByName("Completed");
			employeeTimesheetApplication
					.setTimesheetStatusMaster(timesheetStatusMaster2);
			employeeTimesheetApplicationDAO
					.update(employeeTimesheetApplication);
		}

		isSuccessfullyFor = true;

		String reviewRemarks = "";

		if (isSuccessfullyFor) {
			try {
				lionTimesheetMailLogic
						.sendAcceptRejectMailForTimesheet(
								employeeTimesheetApplication.getCompany()
										.getCompanyId(),
								employeeTimesheetApplication,
								lionTimesheetApplicationReviewer,
								PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPROVE,
								employeeDAO.findById(employeeId), reviewRemarks);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));
		statusMap.put("grandTotalHours", grandTotalHoursNew.toString());
		statusMap.put("excessHoursWorked", excessHoursWorked.toString());

		return statusMap;

	}

	private String getStatusImage(
			EmployeeTimesheetApplication employeeTimesheetApplication,
			String contextPath, Employee employee, long companyId) {
		String status = employeeTimesheetApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName();
		Timestamp lastSubmittedDate = employeeTimesheetApplication
				.getUpdatedDate();

		String dateFormat = companyDAO.findById(companyId).getDateFormat();

		String formattedDate = DateUtils.timeStampToStringWithTime(
				lastSubmittedDate, dateFormat);

		String imagePath = contextPath + "/resources/images/icon/16/";
		if (status.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)) {
			imagePath = imagePath + "pending.png";
		} else if (status
				.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)
				|| status
						.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_COMPLETED)) {
			imagePath = imagePath + "approved.png";
		}
		String employeeName = getEmployeeName(employee);

		return "<img src='" + imagePath + "'  />  " + employeeName + "<br>"
				+ formattedDate;

	}

	private Employee getReviewer(
			EmployeeTimesheetApplication employeeTimesheetApplication) {

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(employeeTimesheetApplication
						.getTimesheetId());

		long employeeTimesheetDetailIDForReviewr = 0l;

		for (LionEmployeeTimesheetApplicationDetail employeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {

			if (employeeTimesheetApplicationDetail.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equals(PayAsiaConstants.LION_TIMESHEET_STATUS_SUBMITTED)
					|| employeeTimesheetApplicationDetail
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equals(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED)
					|| employeeTimesheetApplicationDetail
							.getTimesheetStatusMaster()
							.getTimesheetStatusName()
							.equals(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)) {
				employeeTimesheetDetailIDForReviewr = employeeTimesheetApplicationDetail
						.getEmployeeTimesheetDetailID();

			}

		}

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
				.findByEmployeeTimesheetDetailId(employeeTimesheetDetailIDForReviewr);

		return employeeDAO.findById(lionTimesheetApplicationReviewer
				.getEmployeeReviewer().getEmployeeId());
	}

	public List<String> getHolidaysFor(Long employeeId, Date startDate,
			Date endDate) {
		EmployeeHolidayCalendar empHolidaycalendar = employeeHolidayCalendarDAO
				.getCalendarDetail(employeeId, startDate, endDate);
		List<String> toReturn = new ArrayList<String>();
		if (empHolidaycalendar != null) {
			CompanyHolidayCalendar compHolidayCalendar = empHolidaycalendar
					.getCompanyHolidayCalendar();
			Set<CompanyHolidayCalendarDetail> calDetails = compHolidayCalendar
					.getCompanyHolidayCalendarDetails();
			DateTime holidayDt = new DateTime();
			DateTime startdt = new DateTime(new Timestamp(startDate.getTime()));
			DateTime endDt = new DateTime(new Timestamp(endDate.getTime()));
			DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
			for (CompanyHolidayCalendarDetail compHolidayDetail : calDetails) {
				holidayDt = new DateTime(compHolidayDetail.getHolidayDate());
				if (startdt.compareTo(holidayDt) < 0
						&& endDt.compareTo(holidayDt) > 0) {
					toReturn.add(dtfOut.print(holidayDt));
				}
				if (startdt.toLocalDate().equals(holidayDt.toLocalDate())
						|| endDt.toLocalDate().equals(holidayDt.toLocalDate())) {
					toReturn.add(dtfOut.print(holidayDt));
				}

			}
		}
		return toReturn;
	}

	private Employee getDelegatedEmployee(Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterOT = appCodeMasterDAO.findByCategoryAndDesc(
				PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_TYPE_LION_TIMESHEET_DESC);

		WorkflowDelegate workflowDelegateLeave = workflowDelegateDAO
				.findEmployeeByCurrentDate(employeeId,
						appCodeMasterOT.getAppCodeID());

		if (workflowDelegateLeave != null) {
			workflowDelegate = workflowDelegateLeave;
		} else {

			AppCodeMaster appCodeMasterALL = appCodeMasterDAO
					.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
							PayAsiaConstants.WORKFLOW_CATEGORY_ALL);

			WorkflowDelegate workflowDelegateALL = workflowDelegateDAO
					.findEmployeeByCurrentDate(employeeId,
							appCodeMasterALL.getAppCodeID());

			workflowDelegate = workflowDelegateALL;
		}

		if (workflowDelegate != null) {
			return workflowDelegate.getEmployee2();
		}
		return emp;
	}

	@Override
	public WorkFlowDelegateResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			try {
				conditionDTO.setEmployeeName("%"
						+ URLDecoder.decode(empName.trim(), "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}

		if (StringUtils.isNotBlank(empNumber)) {
			try {
				conditionDTO.setEmployeeNumber("%"
						+ URLDecoder.decode(empNumber.trim(), "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		// For All Subordinate Employee
		List<Tuple> employeeVOList = new ArrayList<Tuple>();
		employeeVOList = employeeTimesheetReviewerDAO
				.getEmployeeIdsTupleForTimesheetReviewer(conditionDTO,
						companyId, employeeId, employeeShortListDTO);
		int recordSize = 0;
		recordSize = employeeVOList.size();
		for (Tuple employeeTuple : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employeeTuple.get(getAlias(Employee_.firstName),
					String.class) + " ";
			if (StringUtils.isNotBlank(employeeTuple.get(
					getAlias(Employee_.lastName), String.class))) {
				employeeName += employeeTuple.get(getAlias(Employee_.lastName),
						String.class);
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employeeTuple.get(
					getAlias(Employee_.employeeNumber), String.class));

			employeeForm.setEmployeeID(employeeTuple.get(
					getAlias(Employee_.employeeId), Long.class));
			employeeListFormList.add(employeeForm);
		}

		WorkFlowDelegateResponse response = new WorkFlowDelegateResponse();
		if (pageDTO != null) {

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

		}

		response.setSearchEmployeeList(employeeListFormList);
		return response;

	}

}
