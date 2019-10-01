package com.payasia.logic.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmailDataDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CoherentTimesheetDTO;
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
import com.payasia.dao.CoherentOTEmployeeListDAO;
import com.payasia.dao.CoherentOvertimeApplicationDAO;
import com.payasia.dao.CoherentOvertimeApplicationDetailDAO;
import com.payasia.dao.CoherentOvertimeApplicationReviewerDAO;
import com.payasia.dao.CoherentOvertimeApplicationWorkflowDAO;
import com.payasia.dao.CoherentTimesheetPreferenceDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.ModuleMasterDAO;
import com.payasia.dao.NotificationAlertDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.bean.CoherentOTEmployeeList;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationDetail;
import com.payasia.dao.bean.CoherentOvertimeApplicationReviewer;
import com.payasia.dao.bean.CoherentOvertimeApplicationWorkflow;
import com.payasia.dao.bean.CoherentTimesheetPreference;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.CoherentEmployeeOvertimeLogic;
import com.payasia.logic.CoherentTimesheetMailLogic;
import com.payasia.logic.CoherentTimesheetPrintPDFLogic;
import com.payasia.logic.CoherentTimesheetReportsLogic;

@Component
public class CoherentEmployeeOvertimeLogicImpl implements CoherentEmployeeOvertimeLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(CoherentEmployeeOvertimeLogicImpl.class);

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
	CoherentTimesheetMailLogic coherentTimesheetMailLogic;

	@Resource
	CoherentTimesheetPrintPDFLogic coherentTimesheetPrintPDFLogic;
	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	CoherentOTEmployeeListDAO coherentOTEmployeeListDAO;
	@Resource
	CoherentOvertimeApplicationDAO coherentOvertimeApplicationDAO;
	@Resource
	CoherentTimesheetPreferenceDAO coherentTimesheetPreferenceDAO;
	@Resource
	CoherentOvertimeApplicationDetailDAO coherentOvertimeApplicationDetailDAO;
	@Resource
	CoherentOvertimeApplicationWorkflowDAO coherentOvertimeApplicationWorkflowDAO;
	@Resource
	CoherentOvertimeApplicationReviewerDAO coherentOvertimeApplicationReviewerDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;
	@Resource
	TimesheetBatchDAO timesheetBatchDAO;
	@Resource
	EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;
	@Resource
	CoherentTimesheetReportsLogic coherentTimesheetReportsLogic;

	@Override
	public PendingOTTimesheetResponseForm getPendingTimesheet(Long empId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText) {
		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = new PendingOTTimesheetResponseForm();

		PendingOTTimesheetConditionDTO otTimesheetConditionDTO = new PendingOTTimesheetConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setCreatedDate(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setEmployeeName("%" + searchText.trim() + "%");
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {

				otTimesheetConditionDTO.setBatch("%" + searchText.trim() + "%");
			}
		}

		List<String> claimStatus = new ArrayList<>();
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		claimStatus.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		otTimesheetConditionDTO.setStatusNameList(claimStatus);
		otTimesheetConditionDTO.setPendingStatus(true);
		int recordSize = 0;

		recordSize = coherentOvertimeApplicationReviewerDAO.findByConditionCountRecords(empId, otTimesheetConditionDTO,
				companyId);
		List<CoherentOvertimeApplicationReviewer> otReviewers = coherentOvertimeApplicationReviewerDAO
				.findByCondition(empId, pageDTO, sortDTO, otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentOvertimeApplicationReviewer otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			CoherentOvertimeApplication lundinOTTimesheet = otReviewer.getCoherentOvertimeApplication();
			TimesheetBatch lundinOtBatch = lundinOTTimesheet.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(lundinOTTimesheet.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils.timeStampToString(lundinOTTimesheet.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:editcoherentReviewerApplication("
							+ 	FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getOvertimeApplicationID()) + ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>Review" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(lundinOTTimesheet.getOvertimeApplicationID());
			pendingOTTimesheetForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch.getTimesheetBatchDesc());
			pendingOTTimesheetForm.setOtTimesheetReviewerId(otReviewer.getOvertimeApplciationReviewerID());
			pendingOTTimesheetForms.add(pendingOTTimesheetForm);
		}

		pendingOTTimesheetResponseForm.setPendingOTTimesheets(pendingOTTimesheetForms);
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
	public PendingOTTimesheetResponseForm getTimesheet(Long empId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText, List<String> StatusNameList) {
		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = new PendingOTTimesheetResponseForm();

		PendingOTTimesheetConditionDTO otTimesheetConditionDTO = new PendingOTTimesheetConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setCreatedDate(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setEmployeeName("%" + searchText.trim() + "%");
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {

				otTimesheetConditionDTO.setBatch("%" + searchText.trim() + "%");
			}
		}

		otTimesheetConditionDTO.setStatusNameList(StatusNameList);

		int recordSize = 0;

		recordSize = coherentOvertimeApplicationReviewerDAO.findByConditionCountRecords(empId, otTimesheetConditionDTO,
				companyId);
		List<CoherentOvertimeApplicationReviewer> otReviewers = coherentOvertimeApplicationReviewerDAO
				.findByCondition(empId, pageDTO, sortDTO, otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentOvertimeApplicationReviewer otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			CoherentOvertimeApplication lundinOTTimesheet = otReviewer.getCoherentOvertimeApplication();
			TimesheetBatch lundinOtBatch = lundinOTTimesheet.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(lundinOTTimesheet.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils.timeStampToString(lundinOTTimesheet.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:viewcoherentReviewerApplication("
							+ FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getOvertimeApplicationID()) + ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>View" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(lundinOTTimesheet.getOvertimeApplicationID());
			pendingOTTimesheetForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch.getTimesheetBatchDesc());
			pendingOTTimesheetForm.setOtTimesheetReviewerId(otReviewer.getOvertimeApplciationReviewerID());
			pendingOTTimesheetForms.add(pendingOTTimesheetForm);
		}

		pendingOTTimesheetResponseForm.setPendingOTTimesheets(pendingOTTimesheetForms);
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
	public PendingOTTimesheetResponseForm getAllTimesheet(Long empId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText, List<String> StatusNameList) {
		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = new PendingOTTimesheetResponseForm();

		PendingOTTimesheetConditionDTO otTimesheetConditionDTO = new PendingOTTimesheetConditionDTO();

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_CREATED_DATE)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setCreatedDate(searchText.trim());
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				otTimesheetConditionDTO.setEmployeeName("%" + searchText.trim() + "%");
			}
		}

		if (searchCondition.equals(PayAsiaConstants.MY_REQUEST_TIMESHEET_BATCH)) {
			if (StringUtils.isNotBlank(searchText)) {

				otTimesheetConditionDTO.setBatch("%" + searchText.trim() + "%");
			}
		}

		otTimesheetConditionDTO.setStatusNameList(StatusNameList);

		int recordSize = 0;

		recordSize = coherentOvertimeApplicationReviewerDAO.getCountForAllByCondition(empId, otTimesheetConditionDTO,
				companyId);
		List<CoherentOvertimeApplicationReviewer> otReviewers = coherentOvertimeApplicationReviewerDAO
				.findAllByCondition(empId, pageDTO, sortDTO, otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentOvertimeApplicationReviewer otReviewer : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			CoherentOvertimeApplication lundinOTTimesheet = otReviewer.getCoherentOvertimeApplication();
			TimesheetBatch lundinOtBatch = lundinOTTimesheet.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(lundinOTTimesheet.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm.setCreatedDate(DateUtils.timeStampToString(lundinOTTimesheet.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			if (lundinOTTimesheet.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)
					|| lundinOTTimesheet.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:editcoherentReviewerApplication("
								+ 	FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getOvertimeApplicationID()) + ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>Review" + "</a>");
			} else {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewcoherentReviewerApplication("
								+ 	FormatPreserveCryptoUtil.encrypt(lundinOTTimesheet.getOvertimeApplicationID()) + ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>View" + "</a>");
			}

			pendingOTTimesheetForm.setOtTimesheetId(lundinOTTimesheet.getOvertimeApplicationID());
			pendingOTTimesheetForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch.getTimesheetBatchDesc());
			pendingOTTimesheetForm.setOtTimesheetReviewerId(otReviewer.getOvertimeApplciationReviewerID());
			pendingOTTimesheetForms.add(pendingOTTimesheetForm);
		}

		pendingOTTimesheetResponseForm.setPendingOTTimesheets(pendingOTTimesheetForms);
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
	public LundinPendingItemsForm getPendingItemForReview(long timesheetId, Long employeeId, long companyId) {
		LundinPendingItemsForm toReturn = new LundinPendingItemsForm();
		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		CoherentOvertimeApplication timesheetApplication = coherentOvertimeApplicationDAO.findByCompanyId(timesheetId,companyId);

		toReturn.setEmployeeName(getEmployeeNameWithNumber(timesheetApplication.getEmployee()));

		List<TimesheetWorkflow> otWorkflows = lundinWorkflowDAO
				.findByCompanyId(timesheetApplication.getCompany().getCompanyId());
		for (TimesheetWorkflow otWorkflow : otWorkflows) {
			WorkFlowRuleMaster ruleMaster = otWorkflow.getWorkFlowRuleMaster();
			if (ruleMaster.getRuleName().equalsIgnoreCase(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
				allowOverride = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_REJECT)) {
				allowReject = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_FORWARD)) {
				allowForward = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_APPROVE)) {
				allowApprove = ruleMaster.getRuleValue();
			}
		}
		for (CoherentOvertimeApplicationReviewer reviewer : timesheetApplication
				.getCoherentOvertimeApplicationReviewers()) {
			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {

				if (employeeId.equals(reviewer.getEmployeeReviewer().getEmployeeId())) {
					if (allowOverride.length() == 3 && allowOverride.substring(0, 1).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3 && allowReject.substring(0, 1).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3 && allowApprove.substring(0, 1).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3 && allowForward.substring(0, 1).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}

			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {

				if (employeeId.equals(reviewer.getEmployeeReviewer().getEmployeeId())) {
					if (allowOverride.length() == 3 && allowOverride.substring(1, 2).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3 && allowReject.substring(1, 2).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3 && allowApprove.substring(1, 2).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3 && allowForward.substring(1, 2).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}

			if (reviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {

				if (employeeId.equals(reviewer.getEmployeeReviewer().getEmployeeId())) {
					if (allowOverride.length() == 3 && allowOverride.substring(2, 3).equals("1")) {
						toReturn.setCanOverride(true);
					} else {
						toReturn.setCanOverride(false);
					}
					if (allowReject.length() == 3 && allowReject.substring(2, 3).equals("1")) {
						toReturn.setCanReject(true);
					} else {
						toReturn.setCanReject(false);
					}
					if (allowApprove.length() == 3 && allowApprove.substring(2, 3).equals("1")) {
						toReturn.setCanApprove(true);
					} else {
						toReturn.setCanApprove(false);
					}
					if (allowForward.length() == 3 && allowForward.substring(2, 3).equals("1")) {
						toReturn.setCanForward(true);
					} else {
						toReturn.setCanForward(false);
					}
				}

			}
		}
		toReturn.setCompanyId(timesheetApplication.getCompany().getCompanyId());
		toReturn.setEmployeeId(employeeId);
		toReturn.setTimesheetId(timesheetApplication.getOvertimeApplicationID());
		toReturn.setRemarks("");
		return toReturn;
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

	private class EmployeeReviewerComp implements Comparator<EmployeeTimesheetReviewer> {
		public int compare(EmployeeTimesheetReviewer templateField, EmployeeTimesheetReviewer compWithTemplateField) {
			if (templateField.getEmployeeTimesheetReviewerId() > compWithTemplateField
					.getEmployeeTimesheetReviewerId()) {
				return 1;
			} else if (templateField.getEmployeeTimesheetReviewerId() < compWithTemplateField
					.getEmployeeTimesheetReviewerId()) {
				return -1;
			}
			return 0;
		}
	}

	private class ClaimReviewerComp implements Comparator<CoherentOvertimeApplicationReviewer> {
		public int compare(CoherentOvertimeApplicationReviewer templateField,
				CoherentOvertimeApplicationReviewer compWithTemplateField) {
			if (templateField.getOvertimeApplciationReviewerID() > compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return 1;
			} else if (templateField.getOvertimeApplciationReviewerID() < compWithTemplateField
					.getOvertimeApplciationReviewerID()) {
				return -1;
			}
			return 0;
		}
	}

	@Override
	public List<String> getHolidaysFor(Long employeeId, Date startDate, Date endDate) {
		EmployeeHolidayCalendar empHolidaycalendar = employeeHolidayCalendarDAO.getCalendarDetail(employeeId, startDate,
				endDate);
		List<String> toReturn = new ArrayList<String>();
		if (empHolidaycalendar != null) {
			CompanyHolidayCalendar compHolidayCalendar = empHolidaycalendar.getCompanyHolidayCalendar();
			Set<CompanyHolidayCalendarDetail> calDetails = compHolidayCalendar.getCompanyHolidayCalendarDetails();
			DateTime holidayDt = new DateTime();
			DateTime startdt = new DateTime(new Timestamp(startDate.getTime()));
			DateTime endDt = new DateTime(new Timestamp(endDate.getTime()));
			DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
			for (CompanyHolidayCalendarDetail compHolidayDetail : calDetails) {
				holidayDt = new DateTime(compHolidayDetail.getHolidayDate());
				if (startdt.compareTo(holidayDt) < 0 && endDt.compareTo(holidayDt) > 0) {
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

	private String convertTimestampToString(Timestamp timestamp, String dateFormat) {
		Date date = new Date(timestamp.getTime());
		dateFormat += " HH:mm";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	@Override
	public String getTimesheetApplications(long timesheetId, long companyId) {

		CoherentOvertimeApplication coherentTimesheetApplication = coherentOvertimeApplicationDAO.findByCompanyId(timesheetId,companyId);
         if(coherentTimesheetApplication!=null)
         {
		List<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetails = coherentOvertimeApplicationDetailDAO
				.getTimesheetDetailsByTimesheetId(timesheetId);

		JSONObject jsonObject = new JSONObject();
		JSONArray coherentEmployeeTimesheetApplicationDetailsJson = new JSONArray();

		SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

		String companyDateFormat = companyDAO.findById(companyId).getDateFormat();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(companyDateFormat);
		Double grantOTTotalHours = 0.0;
		Double grantOT15TotalHours = 0.0;
		Double grantOT10day = 0.0;
		Double grantOT20day = 0.0;
		for (CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail : coherentOvertimeApplicationDetails) {
			try {
				JSONObject timesheetJsonObject = new JSONObject();
				grantOTTotalHours += coherentOvertimeApplicationDetail.getOtHours();
				grantOT15TotalHours += coherentOvertimeApplicationDetail.getOt15Hours();
				grantOT10day += coherentOvertimeApplicationDetail.getOt10Day();
				grantOT20day += coherentOvertimeApplicationDetail.getOt20Day();

				timesheetJsonObject.put("employeeTimesheetDetailID",
						coherentOvertimeApplicationDetail.getOvertimeApplicationDetailID());
				timesheetJsonObject.put("timesheetDate",
						simpleDateFormat.format(coherentOvertimeApplicationDetail.getOvertimeDate()));

				Date date = DateUtils.stringToDate(
						simpleDateFormat.format(coherentOvertimeApplicationDetail.getOvertimeDate()),
						companyDateFormat);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				timesheetJsonObject.put("month", cal.get(Calendar.MONTH));
				timesheetJsonObject.put("day", cal.get(Calendar.DAY_OF_MONTH));
				timesheetJsonObject.put("year", cal.get(Calendar.YEAR));

				timesheetJsonObject.put("timesheetDay",
						format2.format(coherentOvertimeApplicationDetail.getOvertimeDate()));
				timesheetJsonObject.put("dayType", coherentOvertimeApplicationDetail.getDayType().getCodeDesc());

				Date timesheetDate = new Date(coherentOvertimeApplicationDetail.getOvertimeDate().getTime());

				List<String> holidayList = getHolidaysFor(coherentTimesheetApplication.getEmployee().getEmployeeId(),
						timesheetDate, timesheetDate);

				if (holidayList.size() > 0) {
					timesheetJsonObject.put("isHoliday", "true");
				} else {
					timesheetJsonObject.put("isHoliday", "false");
				}

				timesheetJsonObject.put("inTime",
						convertTimestampToString(coherentOvertimeApplicationDetail.getStartTime(), "yyyy-MM-dd"));
				timesheetJsonObject.put("inTimeHoursChanged",
						String.valueOf(coherentOvertimeApplicationDetail.isStartTimeChanged()));
				timesheetJsonObject.put("outTime",
						convertTimestampToString(coherentOvertimeApplicationDetail.getEndTime(), "yyyy-MM-dd"));
				timesheetJsonObject.put("outTimeHoursChanged",
						String.valueOf(coherentOvertimeApplicationDetail.isEndTimeChanged()));
				timesheetJsonObject.put("breakTimeHours", coherentOvertimeApplicationDetail.getMealDuration());
				timesheetJsonObject.put("breakTimeHoursChanged",
						String.valueOf(coherentOvertimeApplicationDetail.isMealDurationChanged()));

				timesheetJsonObject.put("totalHoursWorked", coherentOvertimeApplicationDetail.getOtHours());
				timesheetJsonObject.put("ot15hours", coherentOvertimeApplicationDetail.getOt15Hours());
				timesheetJsonObject.put("ot10day", coherentOvertimeApplicationDetail.getOt10Day());
				timesheetJsonObject.put("ot20day", coherentOvertimeApplicationDetail.getOt20Day());
				timesheetJsonObject.put("remarks", coherentOvertimeApplicationDetail.getRemarks());

				coherentEmployeeTimesheetApplicationDetailsJson.put(timesheetJsonObject);
			} catch (JSONException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}

		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(coherentTimesheetApplication.getTimesheetBatch().getTimesheetBatchId());

		Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
		Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();

		try {
			jsonObject.put("startDate", simpleDateFormat.format(timesheetBatchStartDate));
			jsonObject.put("endDate", simpleDateFormat.format(timesheetBatchEndDate));
			jsonObject.put("timesheetBatchId", timesheetBatch.getTimesheetBatchId());
			jsonObject.put("coherentEmployeeTimesheetApplicationDetails",
					coherentEmployeeTimesheetApplicationDetailsJson);
			Employee employee = employeeDAO.findById(coherentTimesheetApplication.getEmployee().getEmployeeId());
			CoherentOTEmployeeList coherentOTEmployeeList = coherentOTEmployeeListDAO
					.findById(employee.getEmployeeId());

			jsonObject.put("employeeId", employee.getEmployeeNumber());
			jsonObject.put("grandTotalHours", grantOTTotalHours.toString());
			jsonObject.put("grandot15hours", grantOT15TotalHours.toString());
			jsonObject.put("grandot20day", grantOT20day.toString());
			jsonObject.put("grandot10day", grantOT10day.toString());
			if (coherentOTEmployeeList != null) {
				jsonObject.put("iScoherentOTEmployee", true);
			} else {
				jsonObject.put("iScoherentOTEmployee", false);
			}
			jsonObject.put("employeeName", getEmployeeNameWithNumber(coherentTimesheetApplication.getEmployee()));
			jsonObject.put("timesheetStatus",
					coherentTimesheetApplication.getTimesheetStatusMaster().getTimesheetStatusName());
			jsonObject.put("remarks", coherentTimesheetApplication.getRemarks());
			jsonObject.put("batchName", coherentTimesheetApplication.getTimesheetBatch().getTimesheetBatchDesc());

			CoherentTimesheetPreference coherentTimesheetPreference = coherentTimesheetPreferenceDAO
					.findByCompanyId(companyId);
			if (coherentTimesheetPreference != null && coherentTimesheetPreference.getWorkingHoursInADay() > 0.0) {
				jsonObject.put("workingHoursInADay", coherentTimesheetPreference.getWorkingHoursInADay());
			} else {
				jsonObject.put("workingHoursInADay", 0.0);
			}

			Double totalOT15Hours = 0.0;
			Set<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetailVOList = coherentTimesheetApplication
					.getCoherentOvertimeApplicationDetails();
			for (CoherentOvertimeApplicationDetail applicationDetail : coherentOvertimeApplicationDetailVOList) {

				Date timesheetDate = new Date(applicationDetail.getOvertimeDate().getTime());
				List<String> holidayList = getHolidaysFor(coherentTimesheetApplication.getEmployee().getEmployeeId(),
						timesheetDate, timesheetDate);
				boolean isHoliday = false;

				if (holidayList.size() > 0) {
					isHoliday = true;
				}
				if (applicationDetail.getDayType().getCodeDesc()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL) && !isHoliday) {
					totalOT15Hours += applicationDetail.getOt15Hours();
				}
			}
			CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
					.findByCompanyId(companyId);
			if (totalOT15Hours >= PayAsiaConstants.COHERENT_TOTAL_NUM_OF_HOURS_LIMIT
					&& coherentTimesheetPreferenceVO.getIs_validation_72_Hours()) {
				jsonObject.put(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS,
						PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS);
			} else {
				jsonObject.put(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS, "");
			}

			String approvalSteps = "";
			if (!coherentTimesheetApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
				List<CoherentOvertimeApplicationReviewer> applicationReviewers = new ArrayList<>(
						coherentTimesheetApplication.getCoherentOvertimeApplicationReviewers());
				Collections.sort(applicationReviewers, new ClaimReviewerComp());

				for (CoherentOvertimeApplicationReviewer applicationReviewer : applicationReviewers) {
					approvalSteps += getEmployeeName(applicationReviewer.getEmployeeReviewer()) + " -> ";
				}
				if (approvalSteps.endsWith(" -> ")) {
					approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
				}
				jsonObject.put("approvalSteps", approvalSteps);
			}
			if (coherentTimesheetApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
				List<EmployeeTimesheetReviewer> applicationReviewers = new ArrayList<>(employeeTimesheetReviewerDAO
						.findByCondition(coherentTimesheetApplication.getEmployee().getEmployeeId(),
								coherentTimesheetApplication.getCompany().getCompanyId()));
				Collections.sort(applicationReviewers, new EmployeeReviewerComp());

				for (EmployeeTimesheetReviewer applicationReviewer : applicationReviewers) {
					approvalSteps += getEmployeeName(applicationReviewer.getEmployeeReviewer()) + " -> ";
				}
				if (approvalSteps.endsWith(" -> ")) {
					approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
				}
				jsonObject.put("approvalSteps", approvalSteps);
			}

			setDeptCostCentre(coherentTimesheetApplication.getEmployee().getEmployeeId(),
					coherentTimesheetApplication.getCompany().getCompanyId(),
					coherentTimesheetApplication.getEmployee().getEmployeeNumber(), jsonObject, companyDateFormat);

		} catch (JSONException exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		return jsonObject.toString();
         }
         return "";
	}

	private void setDeptCostCentre(long employeeId, long companyId, String employeeNumber, JSONObject jsonObject,
			String companyDateFormat) throws JSONException {
		List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic.getEmpDynFieldsValueList(companyId,
				companyDateFormat, employeeId);

		for (Object[] deptObject : deptCostCentreEmpObjectList) {
			if (deptObject != null && deptObject[3] != null && deptObject[3].equals(employeeNumber)) {
				if (StringUtils.isNotBlank(String.valueOf(deptObject[0]))
						&& !String.valueOf(deptObject[0]).equalsIgnoreCase("null")) {
					jsonObject.put("department", String.valueOf(deptObject[0]));
				} else {
					jsonObject.put("department", "");
				}
				if (StringUtils.isNotBlank(String.valueOf(deptObject[2]))
						&& !String.valueOf(deptObject[2]).equalsIgnoreCase("null")) {
					jsonObject.put("costCentre", String.valueOf(deptObject[2]));
				} else {
					jsonObject.put("costCentre", "");
				}
			}
		}
	}

	@Override
	public Map<String, String> updateCoherentOvertimeDetailByRev(CoherentTimesheetDTO coherentTimesheetDTO) {
		Map<String, String> statusMap = new HashMap<String, String>();
		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;

		CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail = coherentOvertimeApplicationDetailDAO
				.findById(coherentTimesheetDTO.getEmployeeTimesheetDetailId());

		CoherentOvertimeApplication coherentEmployeeTimesheet = coherentOvertimeApplicationDAO.findById(
				coherentOvertimeApplicationDetail.getCoherentOvertimeApplication().getOvertimeApplicationID());

		// Overtime Limit 72 Hours
		Double totalOT15Hours = 0.0;
		Set<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetailVOList = coherentEmployeeTimesheet
				.getCoherentOvertimeApplicationDetails();
		for (CoherentOvertimeApplicationDetail applicationDetail : coherentOvertimeApplicationDetailVOList) {
			if (coherentOvertimeApplicationDetail.getOvertimeApplicationDetailID() == applicationDetail
					.getOvertimeApplicationDetailID()) {
				continue;
			}
			Date timesheetDate = new Date(applicationDetail.getOvertimeDate().getTime());
			List<String> holidayList = getHolidaysFor(coherentEmployeeTimesheet.getEmployee().getEmployeeId(),
					timesheetDate, timesheetDate);
			boolean isHoliday = false;

			if (holidayList.size() > 0) {
				isHoliday = true;
			}
			if (applicationDetail.getDayType().getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL) && !isHoliday) {
				totalOT15Hours += applicationDetail.getOt15Hours();
			}
		}

		Date timesheetDt = new Date(coherentOvertimeApplicationDetail.getOvertimeDate().getTime());
		List<String> holidayListForDt = getHolidaysFor(coherentEmployeeTimesheet.getEmployee().getEmployeeId(),
				timesheetDt, timesheetDt);
		boolean isHolidayDt = false;

		if (holidayListForDt.size() > 0) {
			isHolidayDt = true;
		}
		if (coherentOvertimeApplicationDetail.getDayType().getCodeDesc()
				.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL) && !isHolidayDt) {
			totalOT15Hours += Double.parseDouble(coherentTimesheetDTO.getOt15hours());
		}
		CoherentTimesheetPreference coherentTimesheetPreferenceVO = coherentTimesheetPreferenceDAO
				.findByCompanyId(coherentOvertimeApplicationDetail.getCompanyId());
		if (totalOT15Hours > PayAsiaConstants.COHERENT_TOTAL_NUM_OF_HOURS_LIMIT
				&& coherentTimesheetPreferenceVO.getIs_validation_72_Hours()) {
			statusMap.put(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS,
					PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS);

			return statusMap;
		} else {
			statusMap.put(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS, "");
		}

		String[] oldInTime = coherentOvertimeApplicationDetail.getStartTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = coherentOvertimeApplicationDetail.getEndTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = coherentOvertimeApplicationDetail.getMealDuration().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!coherentTimesheetDTO.getInTime().equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + coherentTimesheetDTO.getInTime() + ":" + oldInTime2[2];
			coherentOvertimeApplicationDetail.setStartTime(Timestamp.valueOf(newInTime));
			coherentOvertimeApplicationDetail.setStartTimeChanged(true);
			inTimeStatus = 1;
		}
		if (!coherentTimesheetDTO.getOutTime().equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {
			String newOutTime = oldOutTime[0] + " " + coherentTimesheetDTO.getOutTime() + ":" + oldOutTime2[2];
			coherentOvertimeApplicationDetail.setEndTime(Timestamp.valueOf(newOutTime));
			coherentOvertimeApplicationDetail.setEndTimeChanged(true);
			outTimeStatus = 1;
		}
		if (!coherentTimesheetDTO.getBreakTime().equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + coherentTimesheetDTO.getBreakTime() + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			coherentOvertimeApplicationDetail.setMealDurationChanged(true);
			coherentOvertimeApplicationDetail.setMealDuration(newBreakTimeTimestamp);
			breakTimeStatus = 1;
		}

		coherentOvertimeApplicationDetail.setOtHours(Double.parseDouble(coherentTimesheetDTO.getTotalHours()));
		coherentOvertimeApplicationDetail.setOt10Day(Double.parseDouble(coherentTimesheetDTO.getOt10day()));
		coherentOvertimeApplicationDetail.setOt15Hours(Double.parseDouble(coherentTimesheetDTO.getOt15hours()));
		coherentOvertimeApplicationDetail.setOt20Day(Double.parseDouble(coherentTimesheetDTO.getOt20day()));

		coherentOvertimeApplicationDetailDAO.update(coherentOvertimeApplicationDetail);

		coherentEmployeeTimesheet.setTotalOT10Day(Double.parseDouble(coherentTimesheetDTO.getGrandot10day()));
		coherentEmployeeTimesheet.setTotalOT15Hours(Double.parseDouble(coherentTimesheetDTO.getGrandot15hours()));
		coherentEmployeeTimesheet.setTotalOT20Day(Double.parseDouble(coherentTimesheetDTO.getGrandot20day()));
		coherentEmployeeTimesheet.setTotalOTHours(Double.parseDouble(coherentTimesheetDTO.getGrandtotalhours()));
		coherentOvertimeApplicationDAO.update(coherentEmployeeTimesheet);

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));

		return statusMap;
	}

	@Override
	public PendingOTTimesheetForm forwardTimesheet(PendingOTTimesheetForm pendingOtTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessfullyFor = false;
		CoherentOvertimeApplication coherentOvertimeApplicationVO = null;
		CoherentOvertimeApplicationReviewer coherentOTTimesheetReviewer2 = null;

		Employee employee = employeeDAO.findById(employeeId);
		Date date = new Date();
		CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer = coherentOvertimeApplicationReviewerDAO
				.findByCondition(pendingOtTimesheetForm.getOtTimesheetId(),
						pendingOtTimesheetForm.getOtTimesheetReviewerId());
		String workflowLevel = String.valueOf(coherentOvertimeApplicationReviewerDAO.getOTTimesheetReviewerCount(
				coherentOvertimeApplicationReviewer.getCoherentOvertimeApplication().getOvertimeApplicationID()));
		if (workflowLevel != null && coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
				.equalsIgnoreCase(workflowLevel)) {
			response = acceptTimesheet(pendingOtTimesheetForm, employeeId, companyId);
		} else {
			try {
				CoherentOvertimeApplicationWorkflow applicationWorkflow = new CoherentOvertimeApplicationWorkflow();

				for (TimesheetWorkflow otWorkflow : lundinWorkflowDAO.findByCompanyId(companyId)) {
					if (otWorkflow.getWorkFlowRuleMaster().getRuleName()
							.equalsIgnoreCase(PayAsiaConstants.OT_DEF_WORKFLOW_LEVEL)) {
						workflowLevel = otWorkflow.getWorkFlowRuleMaster().getRuleValue();

					}
				}
				TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
						.findByName(PayAsiaConstants.OT_STATUS_APPROVED);

				coherentOvertimeApplicationVO = coherentOvertimeApplicationReviewer.getCoherentOvertimeApplication();

				TimesheetStatusMaster otStatusCompleted = null;
				if (coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()
						.equalsIgnoreCase(workflowLevel)) {
					otStatusCompleted = lundinTimesheetStatusMasterDAO.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
					coherentOvertimeApplicationVO.setTimesheetStatusMaster(otStatusCompleted);
				}
				coherentOvertimeApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
				coherentOvertimeApplicationDAO.update(coherentOvertimeApplicationVO);

				// Update Timesheet Approval Pending Status to next level
				// reviewer
				int nextWorkFlowRuleValueLevel = Integer
						.valueOf(coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster().getRuleValue()) + 1;
				for (CoherentOvertimeApplicationReviewer applicationReviewer : coherentOvertimeApplicationVO
						.getCoherentOvertimeApplicationReviewers()) {
					int workFlowRuleValueLevel = Integer
							.valueOf(applicationReviewer.getWorkFlowRuleMaster().getRuleValue());
					if (nextWorkFlowRuleValueLevel == workFlowRuleValueLevel) {
						coherentOTTimesheetReviewer2 = applicationReviewer;
						applicationReviewer.setPending(true);
						coherentOvertimeApplicationReviewerDAO.update(applicationReviewer);
					}
				}

				coherentOvertimeApplicationReviewer.setPending(false);
				coherentOvertimeApplicationReviewer.setEmployeeReviewer(employee);
				coherentOvertimeApplicationReviewerDAO.update(coherentOvertimeApplicationReviewer);

				applicationWorkflow.setCreatedBy(employee);
				applicationWorkflow.setCoherentOvertimeApplication(
						coherentOvertimeApplicationReviewer.getCoherentOvertimeApplication());
				applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
				applicationWorkflow.setRemarks(pendingOtTimesheetForm.getRemarks());
				applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
				coherentOvertimeApplicationWorkflowDAO.saveAndReturn(applicationWorkflow);
				isSuccessfullyFor = true;
			} catch (Exception exception) {
				LOGGER.error(exception.getMessage(), exception);
			}
			if (isSuccessfullyFor) {
				EmailDataDTO emailDataDTO = new EmailDataDTO();
				emailDataDTO.setTimesheetId(coherentOvertimeApplicationVO.getOvertimeApplicationID());
				emailDataDTO.setEmployeeName(getEmployeeName(coherentOvertimeApplicationVO.getEmployee()));
				emailDataDTO.setEmployeeNumber(coherentOvertimeApplicationVO.getEmployee().getEmployeeNumber());
				emailDataDTO.setEmpCompanyId(coherentOvertimeApplicationVO.getEmployee().getCompany().getCompanyId());
				emailDataDTO.setBatchDesc(coherentOvertimeApplicationVO.getTimesheetBatch().getTimesheetBatchDesc());
				emailDataDTO.setOvertimeShiftType(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
				emailDataDTO.setCurrentEmployeeName(getEmployeeName(employee));
				emailDataDTO.setEmailFrom(employee.getEmail());
				emailDataDTO.setEmailTo(coherentOTTimesheetReviewer2.getEmployeeReviewer().getEmail());
				emailDataDTO.setReviewerCompanyId(
						coherentOTTimesheetReviewer2.getEmployeeReviewer().getCompany().getCompanyId());
				emailDataDTO.setReviewerFirstName(coherentOTTimesheetReviewer2.getEmployeeReviewer().getFirstName());

				coherentTimesheetMailLogic.sendPendingEmailForTimesheet(companyId, emailDataDTO,
						PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_FORWARD,
						pendingOtTimesheetForm.getRemarks());
			}
		}

		return response;

	}

	@Override
	public PendingOTTimesheetForm acceptTimesheet(PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		Boolean isSuccessfullyAcc = false;
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		CoherentOvertimeApplication coherentOvertimeApplication = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			CoherentOvertimeApplicationReviewer otApplicationReviewer = coherentOvertimeApplicationReviewerDAO
					.findByCondition(pendingOTTimesheetForm.getOtTimesheetId(),
							pendingOTTimesheetForm.getOtTimesheetReviewerId());

			CoherentOvertimeApplicationWorkflow applicationWorkflow = new CoherentOvertimeApplicationWorkflow();

			coherentOvertimeApplication = otApplicationReviewer.getCoherentOvertimeApplication();

			List<String> otApprovedStatusList = new ArrayList<>();
			otApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			TimesheetStatusMaster otStatusCompleted = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
			coherentOvertimeApplication.setTimesheetStatusMaster(otStatusCompleted);

			coherentOvertimeApplication.setUpdatedDate(new Timestamp(date.getTime()));
			coherentOvertimeApplicationDAO.update(coherentOvertimeApplication);

			for (CoherentOvertimeApplicationReviewer appReviewer : coherentOvertimeApplication
					.getCoherentOvertimeApplicationReviewers()) {
				if (appReviewer.isPending()) {
					appReviewer.setPending(false);
					appReviewer.setEmployeeReviewer(employee);
					coherentOvertimeApplicationReviewerDAO.update(appReviewer);
				}
			}

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow.setCoherentOvertimeApplication(otApplicationReviewer.getCoherentOvertimeApplication());
			applicationWorkflow.setTimesheetStatusMaster(otStatusCompleted);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			coherentOvertimeApplicationWorkflowDAO.saveAndReturn(applicationWorkflow);
			isSuccessfullyAcc = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		if (isSuccessfullyAcc) {
			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentOvertimeApplication.getOvertimeApplicationID());
			emailDataDTO.setEmployeeName(getEmployeeName(coherentOvertimeApplication.getEmployee()));
			emailDataDTO.setEmpCompanyId(coherentOvertimeApplication.getEmployee().getCompany().getCompanyId());
			emailDataDTO.setEmployeeNumber(coherentOvertimeApplication.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentOvertimeApplication.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO.setOvertimeShiftType(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
			emailDataDTO.setEmailFrom(employee.getEmail());
			emailDataDTO.setEmailTo(coherentOvertimeApplication.getEmployee().getEmail());

			coherentTimesheetMailLogic.sendAcceptRejectMailForTimesheet(companyId, emailDataDTO,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPROVE,
					pendingOTTimesheetForm.getRemarks());
		}

		return response;

	}

	@Override
	public PendingOTTimesheetForm rejectTimesheet(PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessRejeted = false;
		CoherentOvertimeApplication coherentOvertimeApplication = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			CoherentOvertimeApplicationReviewer otApplicationReviewer = coherentOvertimeApplicationReviewerDAO
					.findByCondition(pendingOTTimesheetForm.getOtTimesheetId(),
							pendingOTTimesheetForm.getOtTimesheetReviewerId());
			CoherentOvertimeApplicationWorkflow applicationWorkflow = new CoherentOvertimeApplicationWorkflow();

			TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_REJECTED);
			coherentOvertimeApplication = otApplicationReviewer.getCoherentOvertimeApplication();

			List<String> claimApprovedStatusList = new ArrayList<>();
			claimApprovedStatusList.add(PayAsiaConstants.OT_STATUS_COMPLETED);

			coherentOvertimeApplication.setTimesheetStatusMaster(otStatusMaster);
			coherentOvertimeApplication.setUpdatedDate(new Timestamp(date.getTime()));
			coherentOvertimeApplicationDAO.update(coherentOvertimeApplication);

			for (CoherentOvertimeApplicationReviewer appReviewer : coherentOvertimeApplication
					.getCoherentOvertimeApplicationReviewers()) {
				if (appReviewer.isPending()) {
					appReviewer.setPending(false);
					appReviewer.setEmployeeReviewer(employee);
					coherentOvertimeApplicationReviewerDAO.update(appReviewer);
				}
			}

			applicationWorkflow.setCreatedBy(employee);
			applicationWorkflow.setCoherentOvertimeApplication(otApplicationReviewer.getCoherentOvertimeApplication());
			applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			coherentOvertimeApplicationWorkflowDAO.saveAndReturn(applicationWorkflow);
			isSuccessRejeted = true;
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		if (isSuccessRejeted) {
			EmailDataDTO emailDataDTO = new EmailDataDTO();
			emailDataDTO.setTimesheetId(coherentOvertimeApplication.getOvertimeApplicationID());
			emailDataDTO.setEmployeeName(getEmployeeName(coherentOvertimeApplication.getEmployee()));
			emailDataDTO.setEmpCompanyId(coherentOvertimeApplication.getEmployee().getCompany().getCompanyId());
			emailDataDTO.setEmployeeNumber(coherentOvertimeApplication.getEmployee().getEmployeeNumber());
			emailDataDTO.setBatchDesc(coherentOvertimeApplication.getTimesheetBatch().getTimesheetBatchDesc());
			emailDataDTO.setOvertimeShiftType(PayAsiaConstants.COHERENT_OVERTIME_TYPE);
			emailDataDTO.setEmailFrom(employee.getEmail());
			emailDataDTO.setEmailTo(coherentOvertimeApplication.getEmployee().getEmail());

			coherentTimesheetMailLogic.sendAcceptRejectMailForTimesheet(companyId, emailDataDTO,
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_REJECT, pendingOTTimesheetForm.getRemarks());
		}
		return response;
	}

	@Override
	public OTPendingTimesheetForm getTimesheetWorkflowHistory(Long otTimesheetId) {
		OTPendingTimesheetForm otMyRequestForm = new OTPendingTimesheetForm();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		CoherentOvertimeApplication coherentOvertimeApplication = coherentOvertimeApplicationDAO
				.findById(otTimesheetId,employeeId,companyId);
        if(coherentOvertimeApplication!=null){
		otMyRequestForm.setCreatedBy(getEmployeeNameWithNumber(coherentOvertimeApplication.getEmployee()));

		otMyRequestForm
				.setCreatedDate(DateUtils.timeStampToStringWithTime(new ArrayList<CoherentOvertimeApplicationReviewer>(
						coherentOvertimeApplication.getCoherentOvertimeApplicationReviewers()).get(0)
								.getCreatedDate()));
		List<CoherentOvertimeApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				coherentOvertimeApplication.getCoherentOvertimeApplicationWorkflows());
		Collections.sort(applicationWorkflows, new OTTimesheetWorkFlowComp());
		Integer workFlowCount = 0;

		if (coherentOvertimeApplication.getTimesheetStatusMaster().getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_WITHDRAWN)) {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_WITHDRAWN);
		} else {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_SUBMITTED);

		}

		for (CoherentOvertimeApplicationReviewer coherentApplicationReviewer : coherentOvertimeApplication
				.getCoherentOvertimeApplicationReviewers()) {

			if (coherentApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
				otMyRequestForm.setOtTimesheetReviewer1(
						getEmployeeNameWithNumber(coherentApplicationReviewer.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer1Id(coherentApplicationReviewer.getEmployeeReviewer().getEmployeeId());
			}

			if (coherentApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
				otMyRequestForm.setOtTimesheetReviewer2(
						getEmployeeNameWithNumber(coherentApplicationReviewer.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer2Id(coherentApplicationReviewer.getEmployeeReviewer().getEmployeeId());
			}
			if (coherentApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
				otMyRequestForm.setOtTimesheetReviewer3(
						getEmployeeNameWithNumber(coherentApplicationReviewer.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer3Id(coherentApplicationReviewer.getEmployeeReviewer().getEmployeeId());
			}
		}

		otMyRequestForm
				.setTotalNoOfReviewers(coherentOvertimeApplication.getCoherentOvertimeApplicationReviewers().size());
		List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms = new ArrayList<>();
		for (CoherentOvertimeApplicationWorkflow coherentApplicationWorkflow : applicationWorkflows) {

			OTTimesheetWorkflowForm otTimesheetWorkflowForm = new OTTimesheetWorkflowForm();
			otTimesheetWorkflowForm
					.setUserRemarks(coherentApplicationWorkflow.getCoherentOvertimeApplication().getRemarks());
			otTimesheetWorkflowForm.setRemarks(coherentApplicationWorkflow.getRemarks());
			otTimesheetWorkflowForm
					.setStatus(coherentApplicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName());
			otTimesheetWorkflowForm
					.setCreatedDate(DateUtils.timeStampToStringWithTime(coherentApplicationWorkflow.getCreatedDate()));
			otTimesheetWorkflowForm.setOrder(workFlowCount);

			otTimesheetWorkflowForms.add(otTimesheetWorkflowForm);
			workFlowCount++;
		}
		otMyRequestForm.setOtTimesheetWorkflowForms(otTimesheetWorkflowForms);
        }
		return otMyRequestForm;
	}

	private class OTTimesheetWorkFlowComp implements Comparator<CoherentOvertimeApplicationWorkflow> {

		@Override
		public int compare(CoherentOvertimeApplicationWorkflow templateField,
				CoherentOvertimeApplicationWorkflow compWithTemplateField) {
			if (templateField.getOvertimeApplicationWorkflowID() > compWithTemplateField
					.getOvertimeApplicationWorkflowID()) {
				return 1;
			} else if (templateField.getOvertimeApplicationWorkflowID() < compWithTemplateField
					.getOvertimeApplicationWorkflowID()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId, Long employeeId, Long timesheetId,
			boolean hasCoherentTimesheetModule) {
		TimesheetFormPdfDTO timesheetFormPdfDTO = new TimesheetFormPdfDTO();
		
		CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviwer = coherentOvertimeApplicationReviewerDAO.findByCondition(timesheetId,employeeId);
		if(coherentOvertimeApplicationReviwer!=null)
		{
		CoherentOvertimeApplication coherentOvertimeApplication = coherentOvertimeApplicationDAO.findById(timesheetId);
		timesheetFormPdfDTO
				.setTimesheetBatchDesc(coherentOvertimeApplication.getTimesheetBatch().getTimesheetBatchDesc());
		timesheetFormPdfDTO.setEmployeeNumber(coherentOvertimeApplication.getEmployee().getEmployeeNumber());
		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				timesheetFormPdfDTO.setTimesheetPdfByteFile(
						generateTimesheetFormPDF(companyId, employeeId, timesheetId, hasCoherentTimesheetModule));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		} catch (PDFMultiplePageException mpe) {

			LOGGER.error(mpe.getMessage(), mpe);
			PDFThreadLocal.pageNumbers.set(true);
			try {
				timesheetFormPdfDTO.setTimesheetPdfByteFile(
						generateTimesheetFormPDF(companyId, employeeId, timesheetId, hasCoherentTimesheetModule));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}
		}
		return timesheetFormPdfDTO;
	}

	private byte[] generateTimesheetFormPDF(Long companyId, Long employeeId, Long timesheetId,
			boolean hasCoherentTimesheetModule) throws DocumentException, IOException, JAXBException, SAXException {
		File tempFile = PDFUtils.getTemporaryFile(employeeId, PAYASIA_TEMP_PATH, "Timesheet");
		Document document = null;
		OutputStream pdfOut = null;
		InputStream pdfIn = null;
		try {
			document = new Document(PageSize.A4.rotate(), 88f, 88f, 10f, 10f);

			pdfOut = new FileOutputStream(tempFile);

			PdfWriter writer = PdfWriter.getInstance(document, pdfOut);
			writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

			document.open();

			PdfPTable coherentTimesheetPdfTable = coherentTimesheetPrintPDFLogic.createTimesheetPdf(document, writer, 1,
					companyId, timesheetId, hasCoherentTimesheetModule);

			document.add(coherentTimesheetPdfTable);

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
	public DataExportForm exportOvertimeDetail(Long timesheetId) {

		CoherentOvertimeApplication coherentOvertimeApplicationVO = coherentOvertimeApplicationDAO
				.findById(timesheetId);
		TimesheetBatch timesheetBatch = coherentOvertimeApplicationVO.getTimesheetBatch();

		String approvalSteps = "";
		if (!coherentOvertimeApplicationVO.getTimesheetStatusMaster().getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
			List<CoherentOvertimeApplicationReviewer> applicationReviewers = new ArrayList<>(
					coherentOvertimeApplicationVO.getCoherentOvertimeApplicationReviewers());
			Collections.sort(applicationReviewers, new ClaimReviewerComp());

			for (CoherentOvertimeApplicationReviewer applicationReviewer : applicationReviewers) {
				approvalSteps += getEmployeeName(applicationReviewer.getEmployeeReviewer()) + " -> ";
			}
			if (approvalSteps.endsWith(" -> ")) {
				approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
			}
		}

		if (coherentOvertimeApplicationVO.getTimesheetStatusMaster().getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
			List<EmployeeTimesheetReviewer> applicationReviewers = new ArrayList<>(employeeTimesheetReviewerDAO
					.findByCondition(coherentOvertimeApplicationVO.getEmployee().getEmployeeId(),
							coherentOvertimeApplicationVO.getCompany().getCompanyId()));
			Collections.sort(applicationReviewers, new EmployeeReviewerComp());

			for (EmployeeTimesheetReviewer applicationReviewer : applicationReviewers) {
				approvalSteps += getEmployeeName(applicationReviewer.getEmployeeReviewer()) + " -> ";
			}
			if (approvalSteps.endsWith(" -> ")) {
				approvalSteps = StringUtils.removeEnd(approvalSteps, " -> ");
			}
		}

		Company companyVO = companyDAO.findById(coherentOvertimeApplicationVO.getCompany().getCompanyId());

		List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic.getEmpDynFieldsValueList(
				coherentOvertimeApplicationVO.getCompany().getCompanyId(), companyVO.getDateFormat(),
				coherentOvertimeApplicationVO.getEmployee().getEmployeeId());
		String department = "";
		String costCentre = "";
		for (Object[] deptObject : deptCostCentreEmpObjectList) {

			if (deptObject != null && deptObject[3] != null
					&& deptObject[3].equals(coherentOvertimeApplicationVO.getEmployee().getEmployeeNumber())) {
				department = String.valueOf(deptObject[0]);

				if (deptObject[0] != null && deptObject[0] != "null") {
					department = String.valueOf(deptObject[0]);
				} else {
					department = "";
				}

				if (deptObject[2] != null && deptObject[2] != "null") {
					costCentre = String.valueOf(deptObject[2]);
				} else {
					costCentre = "";
				}
			}
		}

		String companyDateFormat = companyDAO.findById(coherentOvertimeApplicationVO.getCompany().getCompanyId())
				.getDateFormat();

		DataExportForm exportForm = new DataExportForm();
		String fileName = coherentOvertimeApplicationVO.getEmployee().getEmployeeNumber() + "_"
				+ timesheetBatch.getTimesheetBatchDesc();

		if (StringUtils.isBlank(fileName)) {
			fileName = PayAsiaConstants.EXPORT_FILE_DEFAULT_TEMPLATE_NAME;
		}
		exportForm.setFinalFileName(fileName);

		Workbook wb = new HSSFWorkbook();

		String companyFormat = coherentOvertimeApplicationVO.getCompany().getDateFormat();
		SimpleDateFormat dateFormat = new SimpleDateFormat(companyFormat);

		Font font = wb.createFont();// Create font
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);

		HSSFFont font1 = (HSSFFont) wb.createFont();
		// Font font1 = wb.createFont();
		font1.setColor(HSSFColor.RED.index);
		// style.setFont(font);

		CellStyle bold1 = wb.createCellStyle();
		bold1.setFont(font1);

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
		allBorderVoiletBackg.setFillForegroundColor(palette.getColor(57).getIndex());
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
		cell12.setCellValue(coherentOvertimeApplicationVO.getEmployee().getEmployeeNumber());
		cell13.setCellValue("Claim Month");

		cell14.setCellValue(coherentOvertimeApplicationVO.getTimesheetBatch().getTimesheetBatchDesc());

		cell15.setCellValue("Cost Centre");
		cell16.setCellValue(costCentre);

		rowId = rowId + 2;
		Row row2 = sheet.createRow(rowId);
		Cell cell21 = row2.createCell(0);
		cell21.setCellStyle(bold);
		Cell cell22 = row2.createCell(1);
		Cell cell23 = row2.createCell(3);
		cell23.setCellStyle(bold);
		Cell cell24 = row2.createCell(4);
		Cell cell25 = row2.createCell(6);
		cell25.setCellStyle(bold);
		Cell cell26 = row2.createCell(7);

		cell21.setCellValue("Name");
		cell22.setCellValue(getEmployeeName(coherentOvertimeApplicationVO.getEmployee()));
		cell23.setCellValue("Approval Steps");
		cell24.setCellValue(approvalSteps);
		cell25.setCellValue("Department");
		cell26.setCellValue(department);

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
		Cell cell39 = row3.createCell(8);
		cell39.setCellStyle(allBorderVoiletBackg);
		Cell cell310 = row3.createCell(9);
		cell310.setCellStyle(allBorderVoiletBackg);
		Cell cell311 = row3.createCell(10);
		cell311.setCellStyle(allBorderVoiletBackg);

		cell31.setCellValue("Date");
		cell32.setCellValue("Day");
		cell33.setCellValue("Start Time (A)");
		cell34.setCellValue("End Time (B)");
		cell35.setCellValue("Meal Duration (C)");
		cell36.setCellValue("Day Type");
		cell37.setCellValue("No. of OT Hours (B-A)-C");
		cell38.setCellValue("OT 1.5 Hours");
		cell39.setCellValue("OT 1.0 Day");
		cell310.setCellValue("OT 2.0 Day");
		cell311.setCellValue("Remarks");

		List<CoherentOvertimeApplicationDetail> coherentOvertimeApplicationDetailVOList = new ArrayList<CoherentOvertimeApplicationDetail>();
		coherentOvertimeApplicationDetailVOList = coherentOvertimeApplicationDetailDAO
				.getTimesheetDetailsByTimesheetId(timesheetId);

		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		DecimalFormat decimalFmt = new DecimalFormat("#,##0.00");
		rowId++;
		Double grantOTTotalHours = 0.0;
		Double grantOT15TotalHours = 0.0;
		Double grantOT10day = 0.0;
		Double grantOT20day = 0.0;
		for (CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetailVO : coherentOvertimeApplicationDetailVOList) {
			grantOTTotalHours += coherentOvertimeApplicationDetailVO.getOtHours();
			grantOT15TotalHours += coherentOvertimeApplicationDetailVO.getOt15Hours();
			grantOT10day += coherentOvertimeApplicationDetailVO.getOt10Day();
			grantOT20day += coherentOvertimeApplicationDetailVO.getOt20Day();
			Date timesheetDate1 = new Date(coherentOvertimeApplicationDetailVO.getOvertimeDate().getTime());
			List<String> holidayList = getHolidaysFor(coherentOvertimeApplicationVO.getEmployee().getEmployeeId(),
					timesheetDate1, timesheetDate1);

			Row rowTable = sheet.createRow(rowId);
			Cell cellT1 = rowTable.createCell(0);

			if (holidayList.size() > 0) {
				cellT1.setCellStyle(allBorderLeft);
				cellT1.setCellStyle(bold1);
			} else {
				cellT1.setCellStyle(allBorderLeft);
			}

			CellStyle alignR = wb.createCellStyle();
			alignR.setAlignment(CellStyle.ALIGN_RIGHT);
			alignR.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			alignR.setBorderTop(HSSFCellStyle.BORDER_THIN);
			alignR.setBorderRight(HSSFCellStyle.BORDER_THIN);
			alignR.setBorderLeft(HSSFCellStyle.BORDER_THIN);

			Cell cellT2 = rowTable.createCell(1);
			if (holidayList.size() > 0) {
				cellT2.setCellStyle(allBorderLeft);
				cellT2.setCellStyle(bold1);
			} else {
				cellT2.setCellStyle(allBorderLeft);
			}

			Cell cellT3 = rowTable.createCell(2);
			cellT3.setCellStyle(allBorderRight);

			Cell cellT4 = rowTable.createCell(3);
			cellT4.setCellStyle(allBorderLeft);
			Cell cellT5 = rowTable.createCell(4);
			cellT5.setCellStyle(allBorderLeft);
			Cell cellT6 = rowTable.createCell(5);
			cellT6.setCellStyle(allBorderLeft);

			Cell cellT7 = rowTable.createCell(6);
			cellT7.setCellType(Cell.CELL_TYPE_NUMERIC);
			cellT7.setCellStyle(alignR);

			Cell cellT8 = rowTable.createCell(7);
			cellT8.setCellType(Cell.CELL_TYPE_NUMERIC);
			cellT8.setCellStyle(alignR);

			Cell cellT9 = rowTable.createCell(8);
			cellT9.setCellType(Cell.CELL_TYPE_NUMERIC);
			cellT9.setCellStyle(alignR);

			Cell cellT10 = rowTable.createCell(9);
			cellT10.setCellStyle(alignR);
			cellT10.setCellType(Cell.CELL_TYPE_NUMERIC);

			Cell cellT11 = rowTable.createCell(10);
			cellT11.setCellStyle(allBorderLeft);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(companyDateFormat);

			Timestamp timesheetDate = coherentOvertimeApplicationDetailVO.getOvertimeDate();

			cellT1.setCellValue(simpleDateFormat.format(timesheetDate));

			java.util.GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
			cal.setTime(timesheetDate);

			SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

			cellT2.setCellValue(format2.format(coherentOvertimeApplicationDetailVO.getOvertimeDate()));
			if (coherentOvertimeApplicationDetailVO.isStartTimeChanged()) {
				cellT3.setCellValue(timeFormat.format(coherentOvertimeApplicationDetailVO.getStartTime()));
				cellT3.setCellStyle(allBorderLeft);
				cellT3.setCellStyle(bold1);
			} else {
				cellT3.setCellValue(timeFormat.format(coherentOvertimeApplicationDetailVO.getStartTime()));
				cellT3.setCellStyle(allBorderLeft);
			}

			if (coherentOvertimeApplicationDetailVO.isEndTimeChanged()) {
				cellT4.setCellValue(timeFormat.format(coherentOvertimeApplicationDetailVO.getEndTime()));
				cellT4.setCellStyle(allBorderLeft);
				cellT4.setCellStyle(bold1);
			} else {
				cellT4.setCellValue(timeFormat.format(coherentOvertimeApplicationDetailVO.getEndTime()));
				cellT4.setCellStyle(allBorderLeft);
			}

			if (coherentOvertimeApplicationDetailVO.isMealDurationChanged()) {
				cellT5.setCellValue(timeFormat.format(coherentOvertimeApplicationDetailVO.getMealDuration()));
				cellT5.setCellStyle(allBorderLeft);
				cellT5.setCellStyle(bold1);
			} else {
				cellT5.setCellValue(timeFormat.format(coherentOvertimeApplicationDetailVO.getMealDuration()));
				cellT5.setCellStyle(allBorderLeft);
			}

			cellT6.setCellValue(coherentOvertimeApplicationDetailVO.getDayType().getCodeDesc());
			cellT7.setCellValue(Double.valueOf(decimalFmt.format(coherentOvertimeApplicationDetailVO.getOtHours())));
			cellT8.setCellValue(Double.valueOf(decimalFmt.format(coherentOvertimeApplicationDetailVO.getOt15Hours())));
			cellT9.setCellValue(Double.valueOf(decimalFmt.format(coherentOvertimeApplicationDetailVO.getOt10Day())));
			cellT10.setCellValue(Double.valueOf(decimalFmt.format(coherentOvertimeApplicationDetailVO.getOt20Day())));
			cellT11.setCellValue(coherentOvertimeApplicationDetailVO.getRemarks());
			rowId++;

		}

		Row row2L = sheet.createRow(rowId);
		Cell cell2L1 = row2L.createCell(5);
		cell2L1.setCellStyle(allBorderRight);
		Cell cell2L2 = row2L.createCell(6);
		cell2L2.setCellStyle(allBorderRight);
		Cell cell2L3 = row2L.createCell(7);
		cell2L3.setCellStyle(allBorderRight);
		Cell cell2L4 = row2L.createCell(8);
		cell2L4.setCellStyle(allBorderRight);
		Cell cell2L5 = row2L.createCell(9);
		cell2L5.setCellStyle(allBorderRight);

		rowId++;

		cell2L1.setCellValue("TOTALS");
		cell2L2.setCellValue(grantOTTotalHours);
		cell2L3.setCellValue(grantOT15TotalHours);
		cell2L4.setCellValue(grantOT10day);
		cell2L5.setCellValue(grantOT20day);

		if (!coherentOvertimeApplicationVO.getTimesheetStatusMaster().getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
			rowId = rowId + 4;
			Row row2R = sheet.createRow(rowId);
			Cell cell2R1 = row2R.createCell(0);
			cell2R1.setCellStyle(allBorderVoiletBackg);
			Cell cell2R2 = row2R.createCell(1);
			cell2R2.setCellStyle(allBorderVoiletBackg);

			Cell cell2R3 = row2R.createCell(2);
			cell2R3.setCellStyle(allBorderVoiletBackg);

			Cell cell2R4 = row2R.createCell(3);
			cell2R4.setCellStyle(allBorderVoiletBackg);

			Cell cell2R5 = row2R.createCell(4);
			cell2R5.setCellStyle(allBorderVoiletBackg);

			rowId++;
			cell2R1.setCellValue("Wokflow Role");
			cell2R2.setCellValue("Name");
			cell2R3.setCellValue("Remarks");
			cell2R4.setCellValue("Status");
			cell2R5.setCellValue("Date");

			OTPendingTimesheetForm form = getTimesheetWorkflowHistory(timesheetId);

			List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms = form.getOtTimesheetWorkflowForms();

			// ////////////////
			boolean isCompleted = false;
			for (OTTimesheetWorkflowForm otTimesheetWorkflowForm : otTimesheetWorkflowForms) {
				if (otTimesheetWorkflowForm.getStatus().equalsIgnoreCase("Completed")) {
					isCompleted = true;
				}
			}

			for (int i = 0; i <= form.getTotalNoOfReviewers(); i++) {

				Row row2RL = sheet.createRow(rowId);
				Cell cellR1 = row2RL.createCell(0);
				cellR1.setCellStyle(allBorder);
				Cell cellR2 = row2RL.createCell(1);
				cellR2.setCellStyle(allBorder);

				Cell cellR3 = row2RL.createCell(2);
				cellR3.setCellStyle(allBorder);
				Cell cellR4 = row2RL.createCell(3);
				cellR4.setCellStyle(allBorder);
				Cell cellR5 = row2RL.createCell(4);
				cellR5.setCellStyle(allBorder);

				if (i == 0) {
					cellR1.setCellValue("User");
					cellR2.setCellValue(form.getCreatedBy());
				}

				if (i == 1) {
					cellR1.setCellValue("Reviewer 1");
					cellR2.setCellValue(form.getOtTimesheetReviewer1());
				}

				if (i == 2) {
					cellR1.setCellValue("Reviewer 2");
					cellR2.setCellValue(form.getOtTimesheetReviewer2());
				}

				if (i == 3) {
					cellR1.setCellValue("Reviewer 3");
					cellR2.setCellValue(form.getOtTimesheetReviewer3());
				}

				if (otTimesheetWorkflowForms.size() > i) {

					cellR3.setCellValue(otTimesheetWorkflowForms.get(i).getUserRemarks());
					cellR4.setCellValue(otTimesheetWorkflowForms.get(i).getStatus());
					cellR5.setCellValue(otTimesheetWorkflowForms.get(i).getCreatedDate());
				} else {

					if (!isCompleted) {
						cellR4.setCellValue("Pending");
					}
				}

				rowId++;
			}
		}
		// ////////////

		for (int columnIndex = 0; columnIndex < 10; columnIndex++) {
			sheet.autoSizeColumn(columnIndex);
		}

		exportForm.setFinalFileName(fileName);

		exportForm.setWorkbook(wb);

		return exportForm;
	}
}
