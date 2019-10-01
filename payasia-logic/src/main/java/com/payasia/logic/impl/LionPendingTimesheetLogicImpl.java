package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DataExportForm;
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
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.LionEmployeeTimesheetDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.LionTimesheetPreferenceDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LionEmployeeTimesheet;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionTimesheetPreference;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetBatch_;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.LionPendingTimesheetLogic;
import com.payasia.logic.LionTimesheetPrintPDFLogic;
import com.payasia.logic.LionTimesheetReportsLogic;
import com.payasia.logic.LundinTimesheetMailLogic;

@Component
public class LionPendingTimesheetLogicImpl extends BaseLogic implements
		LionPendingTimesheetLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinPendingTimesheetLogicImpl.class);

	@Resource
	EmployeeTimesheetApplicationDAO employeeTimesheetApplication;
	@Resource
	TimesheetApplicationReviewerDAO lundinTimesheetReviewerDAO;
	@Resource
	TimesheetWorkflowDAO lundinWorkflowDAO;
	@Resource
	TimesheetStatusMasterDAO lundinTimesheetStatusMasterDAO;
	@Resource
	TimesheetApplicationWorkflowDAO lundinTimesheetWorkflowDAO;

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
	LionTimesheetPrintPDFLogic lionTimesheetPrintPDFLogic;

	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetReviewerDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeTimesheetApplicationDAO employeeTimesheetApplicationDAO;

	@Resource
	TimesheetBatchDAO timesheetBatchDAO;

	@Resource
	LionTimesheetPreferenceDAO lionTimesheetPreferenceDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;

	@Resource
	LionEmployeeTimesheetDAO lionEmployeeTimesheetDAO;

	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;

	@Resource
	LionTimesheetReportsLogic lionTimesheetReportsLogic;

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

		List<Tuple> otReviewers = lionTimesheetReviewerDAO
				.findReviewListByCondition(empId, pageDTO, sortDTO,
						otTimesheetConditionDTO);
		recordSize = lionTimesheetReviewerDAO.getCountByConditionReviewer(
				empId, pageDTO, sortDTO, otTimesheetConditionDTO).size();
		/*
		 * otTimesheetConditionDTO
		 * .setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_PENDING);
		 */

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
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionReviewrTimesheetTemplateItems("
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

				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionReviewrTimesheetTemplateItems("
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

		otTimesheetConditionDTO
				.setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);

		List<Tuple> otReviewers = lionTimesheetReviewerDAO
				.findReviewListByCondition(empId, pageDTO, sortDTO,
						otTimesheetConditionDTO);

		recordSize = lionTimesheetReviewerDAO.getCountByConditionReviewer(
				empId, pageDTO, sortDTO, otTimesheetConditionDTO).size();

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
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionReviewrTimesheetTemplateItems("
								+FormatPreserveCryptoUtil.encrypt(  (Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>View" + "</a>");

			} else {

				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionReviewrTimesheetTemplateItems("
								+ FormatPreserveCryptoUtil.encrypt( (Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>Review" + "</a>");

			}
			pendingOTTimesheetForm.setRemarks(employeeTimesheetApplication
					.getTimesheetStatusMaster().getTimesheetStatusName());
			pendingOTTimesheetForm.setOtTimesheetId(timesheetId);
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm
					.setBatchDesc((String) otReviewer.get(
							getAlias(TimesheetBatch_.timesheetBatchDesc),
							String.class));
			// pendingOTTimesheetForm
			// .setOtTimesheetReviewerId((Long) otReviewer
			// .get(getAlias(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId),
			// Long.class));
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

		otTimesheetConditionDTO
				.setOtTimesheetStatusName(PayAsiaConstants.CLAIM_STATUS_COMPLETED);

		List<Tuple> otReviewers = lionTimesheetReviewerDAO
				.findReviewListByCondition(empId, pageDTO, sortDTO,
						otTimesheetConditionDTO);

		recordSize = lionTimesheetReviewerDAO.getCountByConditionReviewer(
				empId, pageDTO, sortDTO, otTimesheetConditionDTO).size();

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
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionReviewrTimesheetTemplateItems("
								+  FormatPreserveCryptoUtil.encrypt((Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>View" + "</a>");

			} else {

				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewPendinglionReviewrTimesheetTemplateItems("
								+  FormatPreserveCryptoUtil.encrypt((Long) otReviewer
										.get(getAlias(EmployeeTimesheetApplication_.timesheetId),
												Long.class))
								+ ",&apos;"
								+ (String) otReviewer
										.get(getAlias(TimesheetBatch_.timesheetBatchDesc),
												String.class)
								+ "&apos;"
								+ ");'>Review" + "</a>");

			}
			pendingOTTimesheetForm.setRemarks(employeeTimesheetApplication
					.getTimesheetStatusMaster().getTimesheetStatusName());
			pendingOTTimesheetForm.setOtTimesheetId(timesheetId);
			pendingOTTimesheetForm.setTotalItems(String
					.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm
					.setBatchDesc((String) otReviewer.get(
							getAlias(TimesheetBatch_.timesheetBatchDesc),
							String.class));
			// pendingOTTimesheetForm
			// .setOtTimesheetReviewerId((Long) otReviewer
			// .get(getAlias(LionTimesheetApplicationReviewer_.employeeTimesheetReviewerId),
			// Long.class));
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
		EmployeeTimesheetApplication timesheetApplication = employeeTimesheetApplication
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
				employeeTimesheetApplication.update(lundinOTTimesheetVO);

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
			employeeTimesheetApplication.update(lundinOTTimesheetVO);

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
			employeeTimesheetApplication.update(lundinOTTimesheetVO);

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

		EmployeeTimesheetApplication lundinOTTimesheet = employeeTimesheetApplication
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
			Long employeeId, Long timesheetId) {
		TimesheetFormPdfDTO timesheetFormPdfDTO = new TimesheetFormPdfDTO();
		EmployeeTimesheetApplication lundinTimesheetVO = employeeTimesheetApplication
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
								companyId, employeeId, timesheetId, false));
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
								companyId, employeeId, timesheetId, false));
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

			PdfPTable lundinTimesheetPdfTable = lionTimesheetPrintPDFLogic
					.createTimesheetPdf(document, writer, 1, companyId,
							timesheetId, hasLundinTimesheetModule);

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
	public DataExportForm generateTimesheetExcel(Long timesheetId) {

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(timesheetId);
		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(employeeTimesheetApplication.getTimesheetBatch()
						.getTimesheetBatchId());
		LionTimesheetPreference lionTimesheetPreference = lionTimesheetPreferenceDAO
				.findByCompanyId(employeeTimesheetApplication.getCompany()
						.getCompanyId());

		DataExportForm exportForm = new DataExportForm();
		String fileName = employeeTimesheetApplication.getEmployee()
				.getEmployeeNumber()
				+ "_"
				+ timesheetBatch.getTimesheetBatchDesc();

		if (StringUtils.isBlank(fileName)) {
			fileName = PayAsiaConstants.EXPORT_FILE_DEFAULT_TEMPLATE_NAME;
		}
		exportForm.setFinalFileName(fileName);
		Workbook wb = new HSSFWorkbook();

		String companyFormat = employeeTimesheetApplication.getCompany()
				.getDateFormat();
		SimpleDateFormat dateFormat = new SimpleDateFormat(companyFormat);

		Font font = wb.createFont();// Create font
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		CellStyle bold = wb.createCellStyle();
		bold.setFont(font);

		CellStyle allBorder = wb.createCellStyle();
		allBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorder.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorder.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorder.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		CellStyle allBorderRight = wb.createCellStyle();
		allBorderRight.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		allBorderRight.setAlignment(CellStyle.ALIGN_RIGHT);

		CellStyle allBorderLeft = wb.createCellStyle();
		allBorderLeft.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		allBorderLeft.setAlignment(CellStyle.ALIGN_LEFT);

		HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
		palette.setColorAtIndex((short) 57, (byte) 83, (byte) 141, (byte) 213);

		CellStyle allBorderVoiletBackg = wb.createCellStyle();
		allBorderVoiletBackg.setFillForegroundColor(palette.getColor(57)
				.getIndex());
		allBorderVoiletBackg.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		allBorderVoiletBackg.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		allBorderVoiletBackg.setBorderTop(HSSFCellStyle.BORDER_THIN);
		allBorderVoiletBackg.setBorderRight(HSSFCellStyle.BORDER_THIN);
		allBorderVoiletBackg.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		Sheet sheet = wb.createSheet("Sheet1");

		int rowId = 0;

		Row row1 = sheet.createRow(rowId);
		Cell cell11 = row1.createCell(0);
		cell11.setCellStyle(bold);
		Cell cell12 = row1.createCell(1);
		Cell cell13 = row1.createCell(3);
		cell13.setCellStyle(bold);
		Cell cell14 = row1.createCell(4);
		Cell cell15 = row1.createCell(6);
		cell15.setCellStyle(bold);
		Cell cell16 = row1.createCell(7);

		cell11.setCellValue("Employee ID");
		cell12.setCellValue(employeeTimesheetApplication.getEmployee()
				.getEmployeeNumber());
		cell13.setCellValue("Start Date");

		cell14.setCellValue(dateFormat.format(timesheetBatch.getStartDate()));
		cell15.setCellValue("End Date");
		cell16.setCellValue(dateFormat.format(timesheetBatch.getEndDate()));

		rowId = rowId + 2;
		Row row2 = sheet.createRow(rowId);
		Cell cell21 = row2.createCell(0);
		cell21.setCellStyle(bold);
		Cell cell22 = row2.createCell(1);
		Cell cell23 = row2.createCell(3);
		cell23.setCellStyle(bold);
		Cell cell24 = row2.createCell(4);

		cell21.setCellValue("Employee Name");
		cell22.setCellValue(getEmployeeName(employeeTimesheetApplication
				.getEmployee()));
		cell23.setCellValue("Location");
		cell24.setCellValue(getLocation(employeeTimesheetApplication
				.getEmployee().getEmployeeId(), employeeTimesheetApplication
				.getCompany().getCompanyId(), employeeTimesheetApplication));

		rowId = rowId + 4;
		Row row3 = sheet.createRow(rowId);
		Cell cell31 = row3.createCell(0);
		cell31.setCellStyle(allBorderVoiletBackg);

		Cell cell32 = row3.createCell(1);
		cell32.setCellStyle(allBorderVoiletBackg);

		Cell cell33 = row3.createCell(2);
		cell33.setCellStyle(allBorderVoiletBackg);

		Cell cell34 = row3.createCell(3);
		cell34.setCellStyle(allBorderVoiletBackg);

		Cell cell35 = row3.createCell(4);
		cell35.setCellStyle(allBorderVoiletBackg);

		Cell cell36 = row3.createCell(5);
		cell36.setCellStyle(allBorderVoiletBackg);

		Cell cell37 = row3.createCell(6);
		cell37.setCellStyle(allBorderVoiletBackg);

		Cell cell38 = row3.createCell(7);
		cell38.setCellStyle(allBorderVoiletBackg);

		cell31.setCellValue("Date");
		cell32.setCellValue("Day");
		cell33.setCellValue("In Time");
		cell34.setCellValue("Out Time");
		cell35.setCellValue("Break/ Time Hours");
		cell36.setCellValue("Total Hours Worked");
		cell37.setCellValue("Remarks");
		cell38.setCellValue("Action");

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(timesheetId);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		rowId++;
		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {

			Row rowTable = sheet.createRow(rowId);
			Cell cellT1 = rowTable.createCell(0);
			cellT1.setCellStyle(allBorderRight);

			Cell cellT2 = rowTable.createCell(1);
			cellT2.setCellStyle(allBorderLeft);

			Cell cellT3 = rowTable.createCell(2);
			cellT3.setCellStyle(allBorderRight);

			Cell cellT4 = rowTable.createCell(3);
			cellT4.setCellStyle(allBorderRight);

			Cell cellT5 = rowTable.createCell(4);
			cellT5.setCellStyle(allBorderRight);

			Cell cellT6 = rowTable.createCell(5);
			cellT6.setCellStyle(allBorderRight);

			Cell cellT8 = rowTable.createCell(6);
			cellT8.setCellStyle(allBorderRight);

			Cell cellT7 = rowTable.createCell(7);
			cellT7.setCellStyle(allBorderRight);

			Timestamp timesheetDate = lionEmployeeTimesheetApplicationDetail
					.getTimesheetDate();

			cellT1.setCellValue(dateFormat.format(timesheetDate));

			java.util.GregorianCalendar cal = (GregorianCalendar) Calendar
					.getInstance();
			cal.setTime(timesheetDate);

			cellT2.setCellValue(getFullWeekDayNames(cal
					.get(java.util.Calendar.DAY_OF_WEEK) % 7));

			cellT3.setCellValue(timeFormat
					.format(lionEmployeeTimesheetApplicationDetail.getInTime()));

			cellT4.setCellValue(timeFormat
					.format(lionEmployeeTimesheetApplicationDetail.getOutTime()));

			cellT5.setCellValue(timeFormat
					.format(lionEmployeeTimesheetApplicationDetail
							.getBreakTimeHours()));

			cellT6.setCellValue(lionEmployeeTimesheetApplicationDetail
					.getTotalHoursWorked());

			cellT8.setCellValue(lionEmployeeTimesheetApplicationDetail
					.getRemarks());

			cellT7.setCellValue(lionEmployeeTimesheetApplicationDetail
					.getTimesheetStatusMaster().getTimesheetStatusName());

			rowId++;

		}

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(timesheetId).get(0);

		Row row2L = sheet.createRow(rowId);
		Cell cell2L1 = row2L.createCell(4);
		cell2L1.setCellStyle(allBorderRight);
		Cell cell2L2 = row2L.createCell(5);
		cell2L2.setCellStyle(allBorderRight);

		rowId++;

		Row rowL = sheet.createRow(rowId);
		Cell cellL1 = rowL.createCell(4);
		cellL1.setCellStyle(allBorderRight);
		Cell cellL2 = rowL.createCell(5);
		cellL2.setCellStyle(allBorderRight);

		cell2L1.setCellValue("TOTALS");
		cell2L2.setCellValue(lionEmployeeTimesheet.getTimesheetTotalHours());

		cellL1.setCellValue("EXCESS HOURS WORKED");
		cellL2.setCellValue(lionEmployeeTimesheet.getExcessHoursWorked());

		rowId = rowId + 4;

		Row row2R = sheet.createRow(rowId);
		Cell cell2R1 = row2R.createCell(4);
		cell2R1.setCellStyle(allBorderVoiletBackg);
		Cell cell2R2 = row2R.createCell(5);
		cell2R2.setCellStyle(allBorderVoiletBackg);

		rowId++;

		Row row2RL = sheet.createRow(rowId);
		Cell cellR1 = row2RL.createCell(4);
		cellR1.setCellStyle(allBorder);
		Cell cellR2 = row2RL.createCell(5);
		cellR2.setCellStyle(allBorder);

		cell2R1.setCellValue("Wokflow Role");
		cell2R2.setCellValue("Reviewer Name");

		cellR1.setCellValue("Reviewer 1");
		cellR2.setCellValue(getReviewerName(employeeTimesheetApplication));

		for (int columnIndex = 0; columnIndex < 10; columnIndex++) {
			sheet.autoSizeColumn(columnIndex);
		}

		exportForm.setFinalFileName(fileName);

		exportForm.setWorkbook(wb);

		return exportForm;
	}

	private String getLocation(long employeeId, long companyId,
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		String location = null;

		List<Object[]> locationEmployeeObjList = lionTimesheetReportsLogic
				.getTimesheetLocationEmpList(companyId,
						companyDAO.findById(companyId).getDateFormat(),
						employeeId);
		// Get Department Of Employees from Employee Information
		// Details
		for (Object[] deptObject : locationEmployeeObjList) {
			if (deptObject != null && deptObject[1] != null
					&& deptObject[0] != null) {
				if (StringUtils.isNotBlank(String.valueOf(deptObject[1]))
						&& employeeTimesheetApplication
								.getEmployee()
								.getEmployeeNumber()
								.equalsIgnoreCase(String.valueOf(deptObject[1]))) {
					location = String.valueOf(deptObject[0]);
				}

			}
		}
		return location;
	}

	private static String getFullWeekDayNames(int day) {
		String[] weekDayNames = { "Saturday", "Sunday", "Monday", "Tuesday",
				"Wednesday", "Thursday", "Friday" };
		return weekDayNames[day];
	}

	String getReviewerName(
			EmployeeTimesheetApplication employeeTimesheetApplication) {

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(employeeTimesheetApplication
						.getTimesheetId());

		Employee reviewer = null;

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {
			if ((lionEmployeeTimesheetApplicationDetail
					.getTimesheetStatusMaster().getTimesheetStatusName())
					.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)
					|| (lionEmployeeTimesheetApplicationDetail
							.getTimesheetStatusMaster()
							.getTimesheetStatusName())
							.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)
					|| (lionEmployeeTimesheetApplicationDetail
							.getTimesheetStatusMaster()
							.getTimesheetStatusName())
							.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)) {
				reviewer = lionTimesheetReviewerDAO
						.findByEmployeeTimesheetDetailId(
								lionEmployeeTimesheetApplicationDetail
										.getEmployeeTimesheetDetailID())
						.getEmployeeReviewer();
			}
		}

		if (reviewer == null) {
			EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO
					.findByEmployeeId(employeeTimesheetApplication
							.getEmployee().getEmployeeId());
			reviewer = employeeTimesheetReviewer.getEmployeeReviewer();
		}

		if (reviewer == null) {
			return "";
		} else {
			return getEmployeeNameWithNumber(reviewer);
		}

	}

}
