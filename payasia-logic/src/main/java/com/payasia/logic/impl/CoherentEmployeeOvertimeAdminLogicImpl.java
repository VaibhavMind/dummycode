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
import java.util.HashMap;
import java.util.Iterator;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
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
import com.payasia.common.dto.CoherentOvertimeDetailReportDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmailDataDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveReviewFormDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.PendingOTTimesheetConditionDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PDFMultiplePageException;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.CoherentOvertimeDetailForm;
import com.payasia.common.form.CoherentOvertimeDetailFormResponse;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.OTTimesheetWorkflowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
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
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
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
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.ClaimMailLogic;
import com.payasia.logic.CoherentEmployeeOvertimeAdminLogic;
import com.payasia.logic.CoherentTimesheetMailLogic;
import com.payasia.logic.CoherentTimesheetReportsLogic;

@Component
public class CoherentEmployeeOvertimeAdminLogicImpl extends BaseLogic implements CoherentEmployeeOvertimeAdminLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(CoherentEmployeeOvertimeAdminLogicImpl.class);

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
	CoherentOTEmployeeListDAO coherentOTEmployeeListDAO;
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

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

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
	@Resource
	TimesheetStatusMasterDAO timesheetStatusMasterDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

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

		recordSize = coherentOvertimeApplicationDAO.getCountForFindByCondition(otTimesheetConditionDTO, companyId);
		List<CoherentOvertimeApplication> otReviewers = coherentOvertimeApplicationDAO.findByCondition(pageDTO, sortDTO,
				otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentOvertimeApplication coherentOvertimeApplication : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			TimesheetBatch lundinOtBatch = coherentOvertimeApplication.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(coherentOvertimeApplication.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm
					.setCreatedDate(DateUtils.timeStampToString(coherentOvertimeApplication.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:editcoherentReviewerApplication("
							+ FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication.getOvertimeApplicationID()) + ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>Review" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(coherentOvertimeApplication.getOvertimeApplicationID());
			pendingOTTimesheetForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch.getTimesheetBatchDesc());
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

		recordSize = coherentOvertimeApplicationDAO.getCountForFindByCondition(otTimesheetConditionDTO, companyId);
		List<CoherentOvertimeApplication> otReviewers = coherentOvertimeApplicationDAO.findByCondition(pageDTO, sortDTO,
				otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentOvertimeApplication coherentOvertimeApplication : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			TimesheetBatch lundinOtBatch = coherentOvertimeApplication.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(coherentOvertimeApplication.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm
					.setCreatedDate(DateUtils.timeStampToString(coherentOvertimeApplication.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			claimTemplateItemCount
					.append("<a class='alink' href='#' onClick='javascipt:viewcoherentReviewerApplication("
							+ 	FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication.getOvertimeApplicationID()) + ",&apos;"
							+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>View" + "</a>");

			pendingOTTimesheetForm.setOtTimesheetId(coherentOvertimeApplication.getOvertimeApplicationID());
			pendingOTTimesheetForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch.getTimesheetBatchDesc());
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

		recordSize = coherentOvertimeApplicationDAO.getCountForFindAllByCondition(otTimesheetConditionDTO, companyId);
		List<CoherentOvertimeApplication> otReviewers = coherentOvertimeApplicationDAO.findAllByCondition(pageDTO,
				sortDTO, otTimesheetConditionDTO, companyId);

		List<PendingOTTimesheetForm> pendingOTTimesheetForms = new ArrayList<PendingOTTimesheetForm>();
		for (CoherentOvertimeApplication coherentOvertimeApplication : otReviewers) {
			PendingOTTimesheetForm pendingOTTimesheetForm = new PendingOTTimesheetForm();

			TimesheetBatch lundinOtBatch = coherentOvertimeApplication.getTimesheetBatch();

			String empName = getEmployeeNameWithNumber(coherentOvertimeApplication.getEmployee());
			pendingOTTimesheetForm.setCreatedBy(empName);

			pendingOTTimesheetForm
					.setCreatedDate(DateUtils.timeStampToString(coherentOvertimeApplication.getCreatedDate()));
			StringBuilder claimTemplateItemCount = new StringBuilder();
			/*ID ENCRYPT*/
			if (coherentOvertimeApplication.getTimesheetStatusMaster().getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_SUBMITTED)
					|| coherentOvertimeApplication.getTimesheetStatusMaster().getTimesheetStatusName()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_STATUS_APPROVED)) {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:editcoherentReviewerApplication("
								+ FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication.getOvertimeApplicationID()) + ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>Review" + "</a>");
			} else {
				claimTemplateItemCount
						.append("<a class='alink' href='#' onClick='javascipt:viewcoherentReviewerApplication("
								+ FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication.getOvertimeApplicationID()) + ",&apos;"
								+ lundinOtBatch.getTimesheetBatchDesc() + "&apos;" + ");'>View" + "</a>");
			}

			pendingOTTimesheetForm.setOtTimesheetId(FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplication.getOvertimeApplicationID()));
			pendingOTTimesheetForm.setTotalItems(String.valueOf(claimTemplateItemCount));
			pendingOTTimesheetForm.setBatchDesc(lundinOtBatch.getTimesheetBatchDesc());
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

		CoherentOTEmployeeList coherentOTEmployeeList = coherentOTEmployeeListDAO
				.findById(coherentTimesheetApplication.getEmployee().getEmployeeId());

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
						FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplicationDetail.getOvertimeApplicationDetailID()));
				timesheetJsonObject.put("timesheetDate",
						simpleDateFormat.format(coherentOvertimeApplicationDetail.getOvertimeDate()));
				Date date = DateUtils.stringToDate(
						simpleDateFormat.format(coherentOvertimeApplicationDetail.getOvertimeDate()),
						companyDateFormat);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				if (coherentOTEmployeeList != null) {
					jsonObject.put("iScoherentOTEmployee", true);
				} else {
					jsonObject.put("iScoherentOTEmployee", false);
				}

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

						coherentOvertimeApplicationDetail.getStartTime());
				timesheetJsonObject.put("inTimeHoursChanged",
						String.valueOf(coherentOvertimeApplicationDetail.isStartTimeChanged()));
				timesheetJsonObject.put("outTime",

						coherentOvertimeApplicationDetail.getEndTime());
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
			jsonObject.put("employeeId", employee.getEmployeeNumber());
			jsonObject.put("grandTotalHours", grantOTTotalHours.toString());
			jsonObject.put("grandot15hours", grantOT15TotalHours.toString());
			jsonObject.put("grandot20day", grantOT20day.toString());
			jsonObject.put("grandot10day", grantOT10day.toString());

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
	public PendingOTTimesheetForm forwardTimesheet(CoherentOvertimeDetailForm pendingOtTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessfullyFor = false;
		CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewerVO = null;
		CoherentOvertimeApplicationReviewer coherentOTTimesheetReviewer2 = null;
		Employee employee = employeeDAO.findById(employeeId);
		Date date = new Date();
		CoherentOvertimeApplication coherentOvertimeApplicationVO = coherentOvertimeApplicationDAO
				.findById(pendingOtTimesheetForm.getOtTimesheetId());

		for (CoherentOvertimeApplicationReviewer appReviewer : coherentOvertimeApplicationVO
				.getCoherentOvertimeApplicationReviewers()) {
			if (appReviewer.isPending()) {
				coherentOvertimeApplicationReviewerVO = appReviewer;
			}
		}

		String workflowLevel = String.valueOf(coherentOvertimeApplicationReviewerDAO
				.getOTTimesheetReviewerCount(coherentOvertimeApplicationVO.getOvertimeApplicationID()));
		if (workflowLevel != null && coherentOvertimeApplicationReviewerVO.getWorkFlowRuleMaster().getRuleValue()
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

				TimesheetStatusMaster otStatusCompleted = null;
				if (coherentOvertimeApplicationReviewerVO.getWorkFlowRuleMaster().getRuleValue()
						.equalsIgnoreCase(workflowLevel)) {
					otStatusCompleted = lundinTimesheetStatusMasterDAO.findByName(PayAsiaConstants.OT_STATUS_COMPLETED);
					coherentOvertimeApplicationVO.setTimesheetStatusMaster(otStatusCompleted);
				}
				coherentOvertimeApplicationVO.setUpdatedDate(new Timestamp(date.getTime()));
				coherentOvertimeApplicationDAO.update(coherentOvertimeApplicationVO);

				// Update Timesheet Approval Pending Status to next level
				// reviewer
				int nextWorkFlowRuleValueLevel = Integer
						.valueOf(coherentOvertimeApplicationReviewerVO.getWorkFlowRuleMaster().getRuleValue()) + 1;
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

				coherentOvertimeApplicationReviewerVO.setPending(false);
				coherentOvertimeApplicationReviewerVO.setEmployeeReviewer(employee);
				coherentOvertimeApplicationReviewerDAO.update(coherentOvertimeApplicationReviewerVO);

				applicationWorkflow.setCreatedBy(employee);
				applicationWorkflow.setCoherentOvertimeApplication(coherentOvertimeApplicationVO);
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
	public PendingOTTimesheetForm acceptTimesheet(CoherentOvertimeDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		Boolean isSuccessfullyAcc = false;
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		CoherentOvertimeApplication coherentOvertimeApplication = null;
		Employee employee = employeeDAO.findById(employeeId);
	
		pendingOTTimesheetForm.setOtTimesheetId(pendingOTTimesheetForm.getOtTimesheetId());
		try {
			Date date = new Date();
			CoherentOvertimeApplicationWorkflow applicationWorkflow = new CoherentOvertimeApplicationWorkflow();
            coherentOvertimeApplication = coherentOvertimeApplicationDAO.
            		findByCompanyId(pendingOTTimesheetForm.getOtTimesheetId(),companyId);
             if(coherentOvertimeApplication!=null)
             { 
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
			applicationWorkflow.setCoherentOvertimeApplication(coherentOvertimeApplication);
			applicationWorkflow.setTimesheetStatusMaster(otStatusCompleted);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			coherentOvertimeApplicationWorkflowDAO.saveAndReturn(applicationWorkflow);
			isSuccessfullyAcc = true;
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
		}catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}

		return response;

	}

	@Override
	public PendingOTTimesheetForm rejectTimesheet(CoherentOvertimeDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId) {
		PendingOTTimesheetForm response = new PendingOTTimesheetForm();
		Boolean isSuccessRejeted = false;
		CoherentOvertimeApplication coherentOvertimeApplication = null;
		Employee employee = employeeDAO.findById(employeeId);
		try {
			Date date = new Date();
			CoherentOvertimeApplicationWorkflow applicationWorkflow = new CoherentOvertimeApplicationWorkflow();

			TimesheetStatusMaster otStatusMaster = lundinTimesheetStatusMasterDAO
					.findByName(PayAsiaConstants.OT_STATUS_REJECTED);
			coherentOvertimeApplication = coherentOvertimeApplicationDAO
					.findByCompanyId(pendingOTTimesheetForm.getOtTimesheetId(),companyId);
        if(coherentOvertimeApplication!=null){
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
			applicationWorkflow.setCoherentOvertimeApplication(coherentOvertimeApplication);
			applicationWorkflow.setTimesheetStatusMaster(otStatusMaster);
			applicationWorkflow.setRemarks(pendingOTTimesheetForm.getRemarks());
			applicationWorkflow.setCreatedDate(new Timestamp(date.getTime()));
			coherentOvertimeApplicationWorkflowDAO.saveAndReturn(applicationWorkflow);
			isSuccessRejeted = true;
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
		}
		catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		return response;
	}

	@Override
	public OTPendingTimesheetForm getTimesheetWorkflowHistory(Long otTimesheetId) {
		OTPendingTimesheetForm otMyRequestForm = new OTPendingTimesheetForm();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		CoherentOvertimeApplication coherentOvertimeApplication = coherentOvertimeApplicationDAO
				.findByCompanyId(otTimesheetId,companyId);
        if(coherentOvertimeApplication!=null)
        {
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
	public CoherentOvertimeDetailFormResponse viewMultipleTimesheetApps(Long companyId, Long loggedInEmpId,
			String[] timesheetIds) {
		List<Long> timesheetIdsList = new ArrayList<Long>();
		for (int count = 0; count < timesheetIds.length; count++) {
		   if (StringUtils.isNotBlank(timesheetIds[count])) {
				/*ID DECRYPT*/
				timesheetIdsList.add(FormatPreserveCryptoUtil.decrypt(Long.parseLong(timesheetIds[count])));
			}
		}
		List<CoherentOvertimeApplicationReviewer> coherentOvertimeApplicationList = coherentOvertimeApplicationReviewerDAO
				.getPendingTimesheetByIds(null, timesheetIdsList, companyId);
		CoherentOvertimeDetailFormResponse response = new CoherentOvertimeDetailFormResponse();

		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		List<TimesheetWorkflow> otWorkflows = lundinWorkflowDAO.findByCompanyId(companyId);
		for (TimesheetWorkflow otWorkflow : otWorkflows) {
			WorkFlowRuleMaster ruleMaster = otWorkflow.getWorkFlowRuleMaster();
			if (ruleMaster.getRuleName().equalsIgnoreCase(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_REJECT)) {
				allowReject = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_FORWARD)) {
				allowForward = ruleMaster.getRuleValue();
			} else if (ruleMaster.getRuleName().equalsIgnoreCase(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_APPROVE)) {
				allowApprove = ruleMaster.getRuleValue();
			}
		}

		List<CoherentOvertimeDetailForm> coherentOvertimeDetailFormList = new ArrayList<CoherentOvertimeDetailForm>();
		for (CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewerVO : coherentOvertimeApplicationList) {
			CoherentOvertimeDetailForm coherentOvertimeDetailForm = new CoherentOvertimeDetailForm();
			AddLeaveForm addLeaveForm = new AddLeaveForm();

			int reviewOrder = 0;
			for (CoherentOvertimeApplicationReviewer applicationReviewer : coherentOvertimeApplicationReviewerVO
					.getCoherentOvertimeApplication().getCoherentOvertimeApplicationReviewers()) {
				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
					addLeaveForm.setApplyTo(applicationReviewer.getEmployeeReviewer().getEmail());
					addLeaveForm
							.setLeaveReviewer1(getEmployeeNameWithNumber(applicationReviewer.getEmployeeReviewer()));
					addLeaveForm.setApplyToId(applicationReviewer.getEmployeeReviewer().getEmployeeId());
					if (coherentOvertimeApplicationReviewerVO.getEmployeeReviewer()
							.getEmployeeId() == applicationReviewer.getEmployeeReviewer().getEmployeeId()) {
						reviewOrder = 1;
						if (allowReject.length() == 3 && allowReject.substring(0, 1).equals("1")) {
							coherentOvertimeDetailForm.setCanReject(true);
						} else {
							coherentOvertimeDetailForm.setCanReject(false);
						}
						if (allowApprove.length() == 3 && allowApprove.substring(0, 1).equals("1")) {
							coherentOvertimeDetailForm.setCanApprove(true);
						} else {
							coherentOvertimeDetailForm.setCanApprove(false);
						}
						if (allowForward.length() == 3 && allowForward.substring(0, 1).equals("1")) {
							coherentOvertimeDetailForm.setCanForward(true);
						} else {
							coherentOvertimeDetailForm.setCanForward(false);
						}
					}
				}

				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
					addLeaveForm
							.setLeaveReviewer2(getEmployeeNameWithNumber(applicationReviewer.getEmployeeReviewer()));
					addLeaveForm.setLeaveReviewer2Id(applicationReviewer.getEmployeeReviewer().getEmployeeId());
					if (coherentOvertimeApplicationReviewerVO.getEmployeeReviewer()
							.getEmployeeId() == applicationReviewer.getEmployeeReviewer().getEmployeeId()) {
						reviewOrder = 2;
						if (allowReject.length() == 3 && allowReject.substring(1, 2).equals("1")) {
							coherentOvertimeDetailForm.setCanReject(true);
						} else {
							coherentOvertimeDetailForm.setCanReject(false);
						}
						if (allowApprove.length() == 3 && allowApprove.substring(1, 2).equals("1")) {
							coherentOvertimeDetailForm.setCanApprove(true);
						} else {
							coherentOvertimeDetailForm.setCanApprove(false);
						}
						if (allowForward.length() == 3 && allowForward.substring(1, 2).equals("1")) {
							coherentOvertimeDetailForm.setCanForward(true);
						} else {
							coherentOvertimeDetailForm.setCanForward(false);
						}
					}

				}

				if (applicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
					addLeaveForm
							.setLeaveReviewer3(getEmployeeNameWithNumber(applicationReviewer.getEmployeeReviewer()));
					addLeaveForm.setLeaveReviewer3Id(applicationReviewer.getEmployeeReviewer().getEmployeeId());
					if (coherentOvertimeApplicationReviewerVO.getEmployeeReviewer()
							.getEmployeeId() == applicationReviewer.getEmployeeReviewer().getEmployeeId()) {
						reviewOrder = 2;
						if (allowReject.length() == 3 && allowReject.substring(2, 3).equals("1")) {
							coherentOvertimeDetailForm.setCanReject(true);
						} else {
							coherentOvertimeDetailForm.setCanReject(false);
						}
						if (allowApprove.length() == 3 && allowApprove.substring(2, 3).equals("1")) {
							coherentOvertimeDetailForm.setCanApprove(true);
						} else {
							coherentOvertimeDetailForm.setCanApprove(false);
						}
						if (allowForward.length() == 3 && allowForward.substring(2, 3).equals("1")) {
							coherentOvertimeDetailForm.setCanForward(true);
						} else {
							coherentOvertimeDetailForm.setCanForward(false);
						}
					}
				}
			}

			if (reviewOrder == 1 && addLeaveForm.getLeaveReviewer2Id() != null) {
				Employee empReviewer2 = employeeDAO.findById(addLeaveForm.getLeaveReviewer2Id());
				coherentOvertimeDetailForm.setForwardTo(getEmployeeNameWithNumber(empReviewer2));
				coherentOvertimeDetailForm.setForwardToId(empReviewer2.getEmployeeId());
			} else if (reviewOrder == 2 && addLeaveForm.getLeaveReviewer3Id() != null) {
				Employee empReviewer3 = employeeDAO.findById(addLeaveForm.getLeaveReviewer3Id());
				coherentOvertimeDetailForm.setForwardTo(getEmployeeNameWithNumber(empReviewer3));
				coherentOvertimeDetailForm.setForwardToId(empReviewer3.getEmployeeId());
			} else {
				coherentOvertimeDetailForm.setForwardTo("");
			}

			coherentOvertimeDetailForm.setCoherentOvertimeApplicationReviewerId(
					coherentOvertimeApplicationReviewerVO.getOvertimeApplciationReviewerID());
			/*ID ENCRYPT*/
			coherentOvertimeDetailForm.setCoherentOvertimeApplicationId(
					FormatPreserveCryptoUtil.encrypt(coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getOvertimeApplicationID()));
			coherentOvertimeDetailForm.setClaimMonth(coherentOvertimeApplicationReviewerVO
					.getCoherentOvertimeApplication().getTimesheetBatch().getTimesheetBatchDesc());

			setDeptCostCentre(
					coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getEmployee()
							.getEmployeeId(),
					coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getCompany().getCompanyId(),
					coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getEmployee()
							.getEmployeeNumber(),
					coherentOvertimeDetailForm, coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication()
							.getCompany().getDateFormat());

			coherentOvertimeDetailForm.setTotalOTHours(String
					.valueOf(coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getTotalOTHours()));
			coherentOvertimeDetailForm.setTotalOT15Hours(String.valueOf(
					coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getTotalOT15Hours()));
			coherentOvertimeDetailForm.setTotalOT10Day(String
					.valueOf(coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getTotalOT10Day()));
			coherentOvertimeDetailForm.setTotalOT20Day(String
					.valueOf(coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getTotalOT20Day()));
			coherentOvertimeDetailForm.setEmployee(getEmployeeNameWithNumber(
					coherentOvertimeApplicationReviewerVO.getCoherentOvertimeApplication().getEmployee()));
			coherentOvertimeDetailFormList.add(coherentOvertimeDetailForm);
		}
		response.setPendingItemsList(coherentOvertimeDetailFormList);
		return response;
	}

	private void setDeptCostCentre(long employeeId, long companyId, String employeeNumber,
			CoherentOvertimeDetailForm coherentOvertimeDetailForm, String companyDateFormat) {
		List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic.getEmpDynFieldsValueList(companyId,
				companyDateFormat, employeeId);

		for (Object[] deptObject : deptCostCentreEmpObjectList) {
			if (deptObject != null && deptObject[3] != null && deptObject[3].equals(employeeNumber)) {
				if (StringUtils.isNotBlank(String.valueOf(deptObject[2]))
						&& !String.valueOf(deptObject[2]).equalsIgnoreCase("null")) {
					coherentOvertimeDetailForm.setCostCentre(String.valueOf(deptObject[2]));
				} else {
					coherentOvertimeDetailForm.setCostCentre("");
				}
				if (StringUtils.isNotBlank(String.valueOf(deptObject[0]))
						&& !String.valueOf(deptObject[0]).equalsIgnoreCase("null")) {
					coherentOvertimeDetailForm.setDepartment(String.valueOf(deptObject[0]));
				} else {
					coherentOvertimeDetailForm.setDepartment("");
				}
			}
		}
	}

	@Override
	public CoherentOvertimeDetailForm showEmpWorkflowHistory(Long companyId, Long timesheetId) {
		CoherentOvertimeDetailForm pendingItemsForm = new CoherentOvertimeDetailForm();

		CoherentOvertimeApplication coherentOvertimeApplicationVO = coherentOvertimeApplicationDAO
				.findById(timesheetId);
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		for (CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer : coherentOvertimeApplicationVO
				.getCoherentOvertimeApplicationReviewers()) {
			if (coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("1")) {
				addLeaveForm.setApplyTo(coherentOvertimeApplicationReviewer.getEmployeeReviewer().getEmail());
				addLeaveForm.setLeaveReviewer1(
						getEmployeeNameWithNumber(coherentOvertimeApplicationReviewer.getEmployeeReviewer()));
				addLeaveForm.setApplyToId(coherentOvertimeApplicationReviewer.getEmployeeReviewer().getEmployeeId());
			}
			if (coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("2")) {
				addLeaveForm.setLeaveReviewer2(
						getEmployeeNameWithNumber(coherentOvertimeApplicationReviewer.getEmployeeReviewer()));
				addLeaveForm
						.setLeaveReviewer2Id(coherentOvertimeApplicationReviewer.getEmployeeReviewer().getEmployeeId());
			}
			if (coherentOvertimeApplicationReviewer.getWorkFlowRuleMaster().getRuleValue().equals("3")) {
				addLeaveForm.setLeaveReviewer3(
						getEmployeeNameWithNumber(coherentOvertimeApplicationReviewer.getEmployeeReviewer()));
				addLeaveForm
						.setLeaveReviewer3Id(coherentOvertimeApplicationReviewer.getEmployeeReviewer().getEmployeeId());
			}
		}

		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = new ArrayList<>();
		Integer workFlowCount = 0;
		List<CoherentOvertimeApplicationWorkflow> coherentOvertimeApplicationWorkflows = new ArrayList<>(
				coherentOvertimeApplicationVO.getCoherentOvertimeApplicationWorkflows());
		Collections.sort(coherentOvertimeApplicationWorkflows, new OTTimesheetWorkFlowComp());
		for (CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow : coherentOvertimeApplicationWorkflows) {
			LeaveApplicationWorkflowDTO applicationWorkflowDTO = new LeaveApplicationWorkflowDTO();

			workFlowCount++;

			if (workFlowCount != 1) {
				applicationWorkflowDTO.setCreatedDate(
						DateUtils.timeStampToStringWithTime(coherentOvertimeApplicationWorkflow.getCreatedDate()));
				applicationWorkflowDTO
						.setEmployeeInfo(getEmployeeNameWithNumber(coherentOvertimeApplicationWorkflow.getCreatedBy()));
				applicationWorkflowDTO.setRemarks(coherentOvertimeApplicationWorkflow.getRemarks());

				applicationWorkflowDTO.setStatus(
						coherentOvertimeApplicationWorkflow.getTimesheetStatusMaster().getTimesheetStatusName());
				applicationWorkflowDTO.setCreatedDate(
						DateUtils.timeStampToStringWithTime(coherentOvertimeApplicationWorkflow.getCreatedDate()));
			}

			if (coherentOvertimeApplicationWorkflow.getCreatedBy().getEmployeeId() == coherentOvertimeApplicationVO
					.getEmployee().getEmployeeId()) {

				applicationWorkflowDTO.setOrder(0);

			}

			if (addLeaveForm.getApplyToId() != null && addLeaveForm
					.getApplyToId() == coherentOvertimeApplicationWorkflow.getCreatedBy().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(1);
			}

			if (addLeaveForm.getLeaveReviewer2Id() != null && addLeaveForm
					.getLeaveReviewer2Id() == coherentOvertimeApplicationWorkflow.getCreatedBy().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(2);
			}

			if (addLeaveForm.getLeaveReviewer3Id() != null && addLeaveForm
					.getLeaveReviewer3Id() == coherentOvertimeApplicationWorkflow.getCreatedBy().getEmployeeId()) {
				applicationWorkflowDTO.setOrder(3);
			}
			applicationWorkflowDTOs.add(applicationWorkflowDTO);
		}

		addLeaveForm
				.setTotalNoOfReviewers(coherentOvertimeApplicationVO.getCoherentOvertimeApplicationReviewers().size());
		addLeaveForm.setLeaveAppEmp(getEmployeeNameWithNumber(coherentOvertimeApplicationVO.getEmployee()));
		addLeaveForm.setLeaveAppCreated(
				DateUtils.timeStampToStringWithTime(coherentOvertimeApplicationVO.getCreatedDate()));
		addLeaveForm.setLeaveAppRemarks(coherentOvertimeApplicationVO.getRemarks());
		addLeaveForm.setLeaveAppStatus("Submitted");
		addLeaveForm.setWorkflowList(applicationWorkflowDTOs);
		pendingItemsForm.setAddLeaveForm(addLeaveForm);
		return pendingItemsForm;
	}

	@Override
	public List<PendingOTTimesheetForm> reviewMultipleAppByAdmin(CoherentOvertimeDetailForm pendingItemsForm,
			Long employeeId, Long companyId, LeaveSessionDTO sessionDTO) {
		List<PendingOTTimesheetForm> addLeaveFormList = new ArrayList<PendingOTTimesheetForm>();
		List<LeaveReviewFormDTO> leaveReviewFormDTOList = pendingItemsForm.getCoherentReviewFormDTOList();
		for (LeaveReviewFormDTO leaveReviewFormDTO : leaveReviewFormDTOList) {
			pendingItemsForm.setOtTimesheetId(null);
			pendingItemsForm.setRemarks("");

			if (StringUtils.isBlank(leaveReviewFormDTO.getLeaveReviewAction())) {
				continue;
			}

			if (leaveReviewFormDTO.getLeaveApplicationId() != null) {
				pendingItemsForm.setOtTimesheetId(leaveReviewFormDTO.getLeaveApplicationId());
				pendingItemsForm.setRemarks(leaveReviewFormDTO.getRemarks());
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase("Approve")) {
					PendingOTTimesheetForm addLeaveForm = acceptTimesheet(pendingItemsForm, employeeId, companyId);
					addLeaveForm.setOtTimesheetId(leaveReviewFormDTO.getLeaveApplicationId());
					addLeaveForm.setRemarks("Approved.");
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase("Reject")) {
					PendingOTTimesheetForm addLeaveForm = rejectTimesheet(pendingItemsForm, employeeId, companyId);
					addLeaveForm.setOtTimesheetId(leaveReviewFormDTO.getLeaveApplicationId());
					addLeaveForm.setRemarks("Rejected.");
					addLeaveFormList.add(addLeaveForm);
				}
				if (leaveReviewFormDTO.getLeaveReviewAction().equalsIgnoreCase("Forward")) {
					PendingOTTimesheetForm addLeaveForm = forwardTimesheet(pendingItemsForm, employeeId, companyId);
					addLeaveForm.setOtTimesheetId(leaveReviewFormDTO.getLeaveApplicationId());
					addLeaveForm.setRemarks("Forward to next reviewer.");
					addLeaveFormList.add(addLeaveForm);
				}

			}
		}
		return addLeaveFormList;
	}

	@Override
	public LundinPendingItemsForm getPendingItemForReview(long timesheetId, Long employeeId, long companyId) {
		LundinPendingItemsForm toReturn = new LundinPendingItemsForm();
		String allowOverride = "";
		String allowReject = "";
		String allowApprove = "";
		String allowForward = "";
		CoherentOvertimeApplication timesheetApplication = coherentOvertimeApplicationDAO.findByCompanyId(timesheetId,companyId);
         if (timesheetApplication!=null)
         {
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

				if (reviewer.isPending()) {
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

				if (reviewer.isPending()) {
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

				if (reviewer.isPending()) {
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
		toReturn.setTimesheetId(timesheetApplication.getOvertimeApplicationID());
		toReturn.setRemarks("");
         }
		return toReturn;
	}

	@Override
	public TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId, Long employeeId, Long timesheetId,
			boolean hasLundinTimesheetModule) {
		TimesheetFormPdfDTO timesheetFormPdfDTO = new TimesheetFormPdfDTO();
		EmployeeTimesheetApplication lundinTimesheetVO = lundinTimesheetDAO.findById(timesheetId);
		timesheetFormPdfDTO.setTimesheetBatchDesc(lundinTimesheetVO.getTimesheetBatch().getTimesheetBatchDesc());
		timesheetFormPdfDTO.setEmployeeNumber(lundinTimesheetVO.getEmployee().getEmployeeNumber());
		try {

			PDFThreadLocal.pageNumbers.set(true);
			try {
				timesheetFormPdfDTO.setTimesheetPdfByteFile(
						generateTimesheetFormPDF(companyId, employeeId, timesheetId, hasLundinTimesheetModule));
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
						generateTimesheetFormPDF(companyId, employeeId, timesheetId, hasLundinTimesheetModule));
				return timesheetFormPdfDTO;
			} catch (DocumentException | IOException | JAXBException | SAXException e) {
				LOGGER.error(e.getMessage(), e);
				throw new PayAsiaSystemException(e.getMessage(), e);
			}
		}
	}

	private byte[] generateTimesheetFormPDF(Long companyId, Long employeeId, Long timesheetId,
			boolean hasLundinTimesheetModule) throws DocumentException, IOException, JAXBException, SAXException {
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

			PdfPTable lundinTimesheetPdfTable = null;
			// lundinTimesheetPdfTable = lundinTimesheetPrintPDFLogic
			// .createTimesheetPdf(document, writer, 1, companyId,
			// timesheetId, hasLundinTimesheetModule);

			document.add(lundinTimesheetPdfTable);

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
	public ImportEmployeeOvertimeShiftForm importEmployeeOvertime(
			ImportEmployeeOvertimeShiftForm importEmployeeClaimForm, Long companyId, Long employeeId) {
		ImportEmployeeOvertimeShiftForm response = new ImportEmployeeOvertimeShiftForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		try {

			importEmployeeClaimForm = readEmployeeOvertimeImportData(importEmployeeClaimForm.getFileUpload(), companyId,
					dataImportLogDTOs);

			validateAndInsertEmployeeOvertime(importEmployeeClaimForm, companyId, dataImportLogDTOs, employeeId);

		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaRollBackDataException(dataImportLogDTOs);

		}
		response.setDataImportLogDTOs(dataImportLogDTOs);
		return response;

	}

	private ImportEmployeeOvertimeShiftForm readEmployeeOvertimeImportData(CommonsMultipartFile fileUpload,
			Long companyId, List<DataImportLogDTO> dataImportLogDTOs) {
		ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift = new ImportEmployeeOvertimeShiftForm();
		String fileName = fileUpload.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());

		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			importEmployeeOvertimeShift = ExcelUtils.getCoherentOvertimeShiftFromXLS(fileUpload, dataImportLogDTOs);
		} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			importEmployeeOvertimeShift = ExcelUtils.getCoherentOvertimeShiftFromXLSX(fileUpload, dataImportLogDTOs);
		}
		return importEmployeeOvertimeShift;
	}

	private ImportEmployeeOvertimeShiftForm validateAndInsertEmployeeOvertime(
			ImportEmployeeOvertimeShiftForm importEmployeeClaimForm, Long companyId,
			List<DataImportLogDTO> dataImportLogDTOs, Long loggedInEmployeeId) {
		ImportEmployeeOvertimeShiftForm importEmployeeClaimFrm = new ImportEmployeeOvertimeShiftForm();
		HashMap<String, CoherentOvertimeDetailReportDTO> overtimeDetailDTOMap = new HashMap<>();

		setOvertimeDetailImportDataDTO(dataImportLogDTOs, importEmployeeClaimForm, overtimeDetailDTOMap, companyId);
		validateImpotedData(dataImportLogDTOs, overtimeDetailDTOMap, companyId);

		if (!dataImportLogDTOs.isEmpty()) {
			importEmployeeClaimFrm.setDataValid(false);
			importEmployeeClaimFrm.setDataImportLogDTOs(dataImportLogDTOs);
			return importEmployeeClaimFrm;
		}

		Company companyVO = companyDAO.findById(companyId);
		Employee loggedInEmployeeVO = employeeDAO.findById(loggedInEmployeeId);

		TimesheetStatusMaster timesheetStatusMasterVO = timesheetStatusMasterDAO
				.findByName(PayAsiaConstants.LION_TIMESHEET_STATUS_COMPLETED);

		HashMap<String, AppCodeMaster> appCodeMasterMap = new HashMap<>();
		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE);
		for (AppCodeMaster appCodeMaster : appCodeMasterList) {
			appCodeMasterMap.put(appCodeMaster.getCodeDesc(), appCodeMaster);
		}

		Set<String> keySet = overtimeDetailDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			CoherentOvertimeDetailReportDTO overtimeDetailDTO = overtimeDetailDTOMap.get(key);

			if (overtimeDetailDTO.getEmployeeId() == null) {
				continue;
			}

			Employee employeeVO = employeeDAO.findById(overtimeDetailDTO.getEmployeeId());
			if (employeeVO == null) {
				continue;
			}
			TimesheetBatch timesheetBatch = timesheetBatchDAO.getBatchByCurrentDate(companyId, DateUtils
					.stringToTimestamp(overtimeDetailDTO.getMonthDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
			if (timesheetBatch == null) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO.setRowNumber(Long.parseLong(key));
				dataImportLogDTO.setErrorKey("payasia.coherent.batch.not.found");
				dataImportLogDTO.setColName("Month");
				dataImportLogDTO.setErrorValue("");
				dataImportLogDTOs.add(dataImportLogDTO);
				throw new PayAsiaRollBackDataException(dataImportLogDTOs);
			}
			Timestamp newTimestamp = timesheetBatch.getStartDate();

			CoherentOvertimeApplication coherentOvertimeApplication = null;
			CoherentOvertimeApplication coherentOvertimeApplicationReturn = null;

			CoherentOvertimeApplication coherentOvertimeApplicationExisting = coherentOvertimeApplicationDAO
					.findByCondition(companyId, employeeVO.getEmployeeId(), timesheetBatch.getTimesheetBatchId(), null);
			CoherentOvertimeApplication coherentOvertimeApplicationWithImportRemarks = coherentOvertimeApplicationDAO
					.findByCondition(companyId, employeeVO.getEmployeeId(), timesheetBatch.getTimesheetBatchId(),
							"Import By");
			boolean isExist = false;
			if (coherentOvertimeApplicationExisting != null && coherentOvertimeApplicationWithImportRemarks == null) {
				continue;
			}

			if (coherentOvertimeApplicationWithImportRemarks == null) {
				coherentOvertimeApplication = new CoherentOvertimeApplication();
				coherentOvertimeApplication.setCompany(companyVO);

				coherentOvertimeApplication.setEmployee(employeeVO);
				coherentOvertimeApplication.setRemarks("Import By " + getEmployeeNameWithNumber(loggedInEmployeeVO));
				coherentOvertimeApplication.setTimesheetStatusMaster(timesheetStatusMasterVO);

				coherentOvertimeApplication.setTimesheetBatch(timesheetBatch);

				if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
					coherentOvertimeApplication.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
					coherentOvertimeApplication.setUpdatedDate(DateUtils.getCurrentTimestampWithTime());
				} else {
					coherentOvertimeApplication.setCreatedDate(DateUtils.stringToTimestamp(
							overtimeDetailDTO.getUpdatedDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
					coherentOvertimeApplication.setUpdatedDate(DateUtils.stringToTimestamp(
							overtimeDetailDTO.getUpdatedDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
				}
				if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOTHoursStr())) {
					String otHours = overtimeDetailDTO.getTotalOTHoursStr().substring(0, 5);
					otHours = otHours.replace(":", ".");
					coherentOvertimeApplication.setTotalOTHours(Double.valueOf(otHours));
				}
				if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOT15HoursStr())) {
					String otHours = overtimeDetailDTO.getTotalOT15HoursStr().substring(0, 5);
					otHours = otHours.replace(":", ".");
					coherentOvertimeApplication.setTotalOT15Hours(Double.valueOf(otHours));
				}
				if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOT10DayStr())) {
					coherentOvertimeApplication.setTotalOT10Day(Double.valueOf(overtimeDetailDTO.getTotalOT10DayStr()));
				} else {
					coherentOvertimeApplication.setTotalOT10Day(0.0);
				}
				if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOT20DayStr())) {
					coherentOvertimeApplication.setTotalOT20Day(Double.valueOf(overtimeDetailDTO.getTotalOT20DayStr()));
				} else {
					coherentOvertimeApplication.setTotalOT20Day(0.0);
				}
				coherentOvertimeApplicationReturn = coherentOvertimeApplicationDAO
						.saveAndReturn(coherentOvertimeApplication);
			}
			if (coherentOvertimeApplicationWithImportRemarks != null) {
				isExist = true;
				coherentOvertimeApplicationReturn = coherentOvertimeApplicationWithImportRemarks;
			}

			// Insert Into CoherentOvertimeApplicationDetail
			CoherentOvertimeApplicationDetail coherentOvertimeApplicationDetail = new CoherentOvertimeApplicationDetail();
			coherentOvertimeApplicationDetail.setCoherentOvertimeApplication(coherentOvertimeApplicationReturn);

			if (StringUtils.isNotBlank(overtimeDetailDTO.getDayType())) {
				if (overtimeDetailDTO.getDayType().trim().equalsIgnoreCase("rest") || overtimeDetailDTO.getDayType()
						.trim().equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_REST_DAY)) {
					coherentOvertimeApplicationDetail
							.setDayType(appCodeMasterMap.get(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_REST_DAY));
				} else {
					coherentOvertimeApplicationDetail
							.setDayType(appCodeMasterMap.get(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL));
				}
			} else {
				coherentOvertimeApplicationDetail
						.setDayType(appCodeMasterMap.get(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE_NORMAL));
			}

			coherentOvertimeApplicationDetail.setStartTime(newTimestamp);
			coherentOvertimeApplicationDetail.setEndTime(newTimestamp);
			coherentOvertimeApplicationDetail.setMealDuration(newTimestamp);
			coherentOvertimeApplicationDetail.setOvertimeDate(newTimestamp);

			Double otHoursDetail = 0.0;
			Double otHours15Detail = 0.0;
			Double otHours10Detail = 0.0;
			Double otHours20Detail = 0.0;

			if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOTHoursStr())) {
				String otHours = overtimeDetailDTO.getTotalOTHoursStr().substring(0, 5);
				otHours = otHours.replace(":", ".");
				otHoursDetail = Double.valueOf(otHours);
				coherentOvertimeApplicationDetail.setOtHours(otHoursDetail);
			}
			if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOT15HoursStr())) {
				String otHours = overtimeDetailDTO.getTotalOT15HoursStr().substring(0, 5);
				otHours = otHours.replace(":", ".");
				otHours15Detail = Double.valueOf(otHours);
				coherentOvertimeApplicationDetail.setOt15Hours(otHours15Detail);
			}
			if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOT10DayStr())) {
				otHours10Detail = Double.valueOf(overtimeDetailDTO.getTotalOT10DayStr());
				coherentOvertimeApplicationDetail.setOt10Day(otHours10Detail);
			} else {
				coherentOvertimeApplicationDetail.setOt10Day(0.0);
			}
			if (StringUtils.isNotBlank(overtimeDetailDTO.getTotalOT20DayStr())) {
				otHours20Detail = Double.valueOf(overtimeDetailDTO.getTotalOT20DayStr());
				coherentOvertimeApplicationDetail.setOt20Day(otHours20Detail);
			} else {
				coherentOvertimeApplicationDetail.setOt20Day(0.0);
			}
			coherentOvertimeApplicationDetail.setRemarks("");
			coherentOvertimeApplicationDetailDAO.save(coherentOvertimeApplicationDetail);

			if (isExist) {
				coherentOvertimeApplicationReturn
						.setTotalOTHours(coherentOvertimeApplicationReturn.getTotalOTHours() + otHoursDetail);
				coherentOvertimeApplicationReturn
						.setTotalOT15Hours(coherentOvertimeApplicationReturn.getTotalOT15Hours() + otHours15Detail);
				coherentOvertimeApplicationReturn
						.setTotalOT10Day(coherentOvertimeApplicationReturn.getTotalOT10Day() + otHours10Detail);
				coherentOvertimeApplicationReturn
						.setTotalOT20Day(coherentOvertimeApplicationReturn.getTotalOT20Day() + otHours20Detail);
				coherentOvertimeApplicationDAO.update(coherentOvertimeApplicationReturn);
			}

			// Set Reviewers
			if (!isExist) {
				CoherentOvertimeApplicationReviewer coherentOvertimeApplicationReviewer = new CoherentOvertimeApplicationReviewer();
				WorkFlowRuleMaster workflowRuleMaster = workFlowRuleMasterDAO
						.findByRuleNameValue(PayAsiaConstants.WORK_FLOW_RULE_NAME_OT_REVIEWER, "1");
				coherentOvertimeApplicationReviewer.setWorkFlowRuleMaster(workflowRuleMaster);
				coherentOvertimeApplicationReviewer.setCoherentOvertimeApplication(coherentOvertimeApplicationReturn);
				coherentOvertimeApplicationReviewer.setPending(false);
				coherentOvertimeApplicationReviewer.setEmployeeReviewer(loggedInEmployeeVO);
				if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
					coherentOvertimeApplicationReviewer.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
				} else {
					coherentOvertimeApplicationReviewer.setCreatedDate(DateUtils.stringToTimestamp(
							overtimeDetailDTO.getUpdatedDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
				}
				coherentOvertimeApplicationReviewerDAO.save(coherentOvertimeApplicationReviewer);

				// Set Workflow
				CoherentOvertimeApplicationWorkflow overtimeApplicationSubmittedWorkflow = new CoherentOvertimeApplicationWorkflow();
				overtimeApplicationSubmittedWorkflow.setCoherentOvertimeApplication(coherentOvertimeApplicationReturn);
				overtimeApplicationSubmittedWorkflow.setCreatedBy(loggedInEmployeeVO);
				overtimeApplicationSubmittedWorkflow.setRemarks("");
				overtimeApplicationSubmittedWorkflow.setTimesheetStatusMaster(
						timesheetStatusMasterDAO.findByName(PayAsiaConstants.LION_TIMESHEET_STATUS_SUBMITTED));
				if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
					overtimeApplicationSubmittedWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
				} else {
					overtimeApplicationSubmittedWorkflow.setCreatedDate(DateUtils.stringToTimestamp(
							overtimeDetailDTO.getUpdatedDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
				}
				coherentOvertimeApplicationWorkflowDAO.save(overtimeApplicationSubmittedWorkflow);

				CoherentOvertimeApplicationWorkflow overtimeApplicationApprovedWorkflow = new CoherentOvertimeApplicationWorkflow();
				overtimeApplicationApprovedWorkflow.setCoherentOvertimeApplication(coherentOvertimeApplicationReturn);
				overtimeApplicationApprovedWorkflow.setCreatedBy(loggedInEmployeeVO);
				overtimeApplicationApprovedWorkflow
						.setRemarks("Import By " + getEmployeeNameWithNumber(loggedInEmployeeVO));
				overtimeApplicationApprovedWorkflow.setTimesheetStatusMaster(timesheetStatusMasterVO);
				if (StringUtils.isBlank(overtimeDetailDTO.getUpdatedDate())) {
					overtimeApplicationApprovedWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
				} else {
					overtimeApplicationApprovedWorkflow.setCreatedDate(DateUtils.stringToTimestamp(
							overtimeDetailDTO.getUpdatedDate(), PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
				}
				coherentOvertimeApplicationWorkflowDAO.save(overtimeApplicationApprovedWorkflow);
			}

		}

		return importEmployeeClaimFrm;

	}

	public void setOvertimeDetailImportDataDTO(List<DataImportLogDTO> dataImportLogDTOs,
			ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShiftForm,
			HashMap<String, CoherentOvertimeDetailReportDTO> overtimeDetailDTOMap, Long companyId) {
		for (HashMap<String, String> map : importEmployeeOvertimeShiftForm.getImportedData()) {
			CoherentOvertimeDetailReportDTO overtimeDetailDTO = new CoherentOvertimeDetailReportDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get(PayAsiaConstants.HASH_KEY_ROW_NUMBER);
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				key = key.trim();

				if (key != PayAsiaConstants.HASH_KEY_ROW_NUMBER) {
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_EMPLOYEENO)) {
						overtimeDetailDTO.setEmployeeNumber(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CATEGORY_DAY_TYPE)) {
						overtimeDetailDTO.setDayType(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_TOTAL_OT_HOURS)
							&& StringUtils.isNotBlank(value)) {
						overtimeDetailDTO.setTotalOTHoursStr(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_TOTAL_OT_15HOURS)) {
						overtimeDetailDTO.setTotalOT15HoursStr(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_TOTAL_OT_10DAY)) {
						overtimeDetailDTO.setTotalOT10DayStr(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_TOTAL_OT_20DAY)) {
						overtimeDetailDTO.setTotalOT20DayStr(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_APPROVED_DATE)) {
						overtimeDetailDTO.setUpdatedDate(value);
					}
					if (key.equalsIgnoreCase(PayAsiaConstants.COHERENT_REPORT_MONTH) && StringUtils.isNotBlank(value)) {
						overtimeDetailDTO.setMonthDate(value);
						overtimeDetailDTOMap.put(rowNumber, overtimeDetailDTO);
					}
				}
			}
		}
	}

	/**
	 * Purpose: To Validate imported Overtime Data.
	 * 
	 * @param overtimeDetailDTOMap
	 *            the overtimeDetailDTOMap
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(List<DataImportLogDTO> dataImportLogDTOs,
			HashMap<String, CoherentOvertimeDetailReportDTO> overtimeDetailDTOMap, Long companyId) {
		HashMap<String, Long> employeeNumberMap = new HashMap<String, Long>();
		List<Tuple> employeeNameListVO = employeeDAO.getEmployeeNameTupleList(companyId);
		for (Tuple empTuple : employeeNameListVO) {
			String employeeName = (String) empTuple.get(getAlias(Employee_.employeeNumber), String.class);
			Long employeeId = (Long) empTuple.get(getAlias(Employee_.employeeId), Long.class);
			employeeNumberMap.put(employeeName.toUpperCase(), employeeId);
		}

		Set<String> keySet = overtimeDetailDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			CoherentOvertimeDetailReportDTO overtimeDetailDTO = overtimeDetailDTOMap.get(key);

			String rowNumber = key;
			if (key.equalsIgnoreCase(PayAsiaConstants.HASH_KEY_ROW_NUMBER)) {
				continue;
			}
			if (StringUtils.isNotBlank(overtimeDetailDTO.getEmployeeNumber())) {
				if (!employeeNumberMap.containsKey(overtimeDetailDTO.getEmployeeNumber().toUpperCase())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.COHERENT_REPORT_EMPLOYEENO,
							"payasia.invalid.employee.number", Long.parseLong(rowNumber));
					continue;
				}
				overtimeDetailDTO
						.setEmployeeId(employeeNumberMap.get(overtimeDetailDTO.getEmployeeNumber().toUpperCase()));
				// Month
				if (StringUtils.isBlank(overtimeDetailDTO.getMonthDate())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.COHERENT_REPORT_MONTH,
							"payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
			}
			overtimeDetailDTOMap.put(key, overtimeDetailDTO);
		}
	}

	public void setClaimReviewersDataImportLogs(List<DataImportLogDTO> dataImportLogDTOs, String key, String remarks,
			Long rowNumber) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setColName(key);
		dataImportLogDTO.setRemarks(remarks);
		dataImportLogDTO.setRowNumber(rowNumber + 1);
		dataImportLogDTOs.add(dataImportLogDTO);
	}

}
