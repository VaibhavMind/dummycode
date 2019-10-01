package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHolidayCalendarDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.LionEmployeeTimesheetDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.LionTimesheetApplicationWorkflowDAO;
import com.payasia.dao.LionTimesheetPreferenceDAO;
import com.payasia.dao.TimesheetApplicationReviewerDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.LionEmployeeTimesheet;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.logic.LionTimesheetApplicationReviewerLogic;
import com.payasia.logic.LionTimesheetReportsLogic;

@Component
public class LionTimesheetApplicationReviewerLogicImpl implements
		LionTimesheetApplicationReviewerLogic {

	@Resource
	LionEmployeeTimesheetDAO lionEmployeeTimesheetDAO;

	@Resource
	EmployeeTimesheetApplicationDAO employeeTimesheetApplicationDAO;

	@Resource
	TimesheetStatusMasterDAO timesheetStatusMasterDAO;

	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	TimesheetApplicationReviewerDAO timesheetApplicationReviewerDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;

	@Resource
	TimesheetBatchDAO timesheetBatchDAO;

	@Resource
	LionTimesheetPreferenceDAO lionTimesheetPreferenceDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetApplicationReviewerDAO;

	@Resource
	LionTimesheetApplicationWorkflowDAO lionTimesheetApplicationWorkflowDAO;

	@Resource
	EmployeeHolidayCalendarDAO employeeHolidayCalendarDAO;

	@Resource
	LionTimesheetReportsLogic lionTimesheetReportsLogic;

	@Resource
	TimesheetWorkflowDAO timesheetWorkflowDAO;

	@Override
	public String getTimesheetApplications(long timesheetId, long employeeId,
			long companyId) {

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(timesheetId);

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(timesheetId);

		JSONObject jsonObject = new JSONObject();
		JSONArray lionEmployeeTimesheetApplicationDetailsJson = new JSONArray();

		long employeeTimesheetDetailIDForReviewr = 0l;

		SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

		String companyDateFormat = companyDAO.findById(companyId)
				.getDateFormat();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				companyDateFormat);

		Double grandTotalHours = 0.0;

		for (LionEmployeeTimesheetApplicationDetail employeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {
			try {
				JSONObject timesheetJsonObject = new JSONObject();
				/*ID ENCRYPT*/
				timesheetJsonObject.put("employeeTimesheetDetailID",
						 FormatPreserveCryptoUtil.encrypt(employeeTimesheetApplicationDetail
								.getEmployeeTimesheetDetailID()));
				timesheetJsonObject.put("timesheetDate", simpleDateFormat
						.format(employeeTimesheetApplicationDetail
								.getTimesheetDate()));

				timesheetJsonObject.put("timesheetDay", format2
						.format(employeeTimesheetApplicationDetail
								.getTimesheetDate()));

				Date date = DateUtils.stringToDate(simpleDateFormat
						.format(employeeTimesheetApplicationDetail
								.getTimesheetDate()), companyDateFormat);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				timesheetJsonObject.put("month", cal.get(Calendar.MONTH));
				timesheetJsonObject.put("day", cal.get(Calendar.DAY_OF_MONTH));
				timesheetJsonObject.put("year", cal.get(Calendar.YEAR));

				timesheetJsonObject.put("timesheetDay", format2
						.format(employeeTimesheetApplicationDetail
								.getTimesheetDate()));

				Date timesheetDate = new Date(
						employeeTimesheetApplicationDetail.getTimesheetDate()
								.getTime());

				List<String> holidayList = getHolidaysFor(
						employeeTimesheetApplication.getEmployee()
								.getEmployeeId(), timesheetDate, timesheetDate);

				if (holidayList.size() > 0) {
					timesheetJsonObject.put("isHoliday", "true");
				} else {
					timesheetJsonObject.put("isHoliday", "false");
				}

				timesheetJsonObject.put("inTime",
						employeeTimesheetApplicationDetail.getInTime());
				timesheetJsonObject.put("inTimeHoursChanged", String
						.valueOf(employeeTimesheetApplicationDetail
								.isInTimeChanged()));
				timesheetJsonObject.put("outTime",
						employeeTimesheetApplicationDetail.getOutTime());
				timesheetJsonObject.put("outTimeHoursChanged", String
						.valueOf(employeeTimesheetApplicationDetail
								.isOutTimeChanged()));
				timesheetJsonObject.put("breakTimeHours",
						employeeTimesheetApplicationDetail.getBreakTimeHours());
				timesheetJsonObject.put("breakTimeHoursChanged", String
						.valueOf(employeeTimesheetApplicationDetail
								.isBreakTimeHoursChanged()));
				timesheetJsonObject.put("totalHoursWorked",
						employeeTimesheetApplicationDetail
								.getTotalHoursWorked());
				if (employeeTimesheetApplicationDetail.getRemarks() != null) {
					timesheetJsonObject.put("remarks",
							employeeTimesheetApplicationDetail.getRemarks());
				} else {
					timesheetJsonObject.put("remarks", "");
				}
				timesheetJsonObject.put("timesheetStatus",
						employeeTimesheetApplicationDetail
								.getTimesheetStatusMaster()
								.getTimesheetStatusName());

				grandTotalHours += employeeTimesheetApplicationDetail
						.getTotalHoursWorked();

				lionEmployeeTimesheetApplicationDetailsJson
						.put(timesheetJsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

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

		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(employeeTimesheetApplication.getTimesheetBatch()
						.getTimesheetBatchId());

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(timesheetId) .get(0);

		Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
		Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
				.findByEmployeeTimesheetDetailId(employeeTimesheetDetailIDForReviewr);

		List<TimesheetWorkflow> timesheetWorkflowList = timesheetWorkflowDAO
				.findByCompanyId(companyId);

		boolean allowOverride = false;

		for (TimesheetWorkflow timesheetWorkflow : timesheetWorkflowList) {
			if (timesheetWorkflow.getWorkFlowRuleMaster().getWorkFlowRuleId() == 5) {
				allowOverride = true;
			}
		}

		try {

			jsonObject.put("startDate",
					simpleDateFormat.format(timesheetBatchStartDate));
			jsonObject.put("endDate",
					simpleDateFormat.format(timesheetBatchEndDate));
			jsonObject.put("timesheetBatchId",
					timesheetBatch.getTimesheetBatchId());
			jsonObject.put("lionEmployeeTimesheetApplicationDetails",
					lionEmployeeTimesheetApplicationDetailsJson);
			Employee employee = employeeTimesheetApplication.getEmployee();
			jsonObject.put("employeeId", employee.getEmployeeNumber());
			jsonObject.put("grandTotalHours", grandTotalHours.toString());
			jsonObject.put("excessHours", lionEmployeeTimesheet
					.getExcessHoursWorked().toString());

			jsonObject.put("employeeName", getEmployeeName(employee));

			String location = getLocation(employeeTimesheetApplication
					.getEmployee().getEmployeeId(),
					employeeTimesheetApplication.getCompany().getCompanyId(),
					employeeTimesheetApplication);

			jsonObject.put("location", location);

			jsonObject.put("reviewer",
					getEmployeeFullName(lionTimesheetApplicationReviewer
							.getEmployeeReviewer()));

			jsonObject.put("allowOverride", String.valueOf(allowOverride));

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject.toString();

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
	public String getMultipleTimesheetApplications(String timesheetIds,
			long employeeId, long companyId) {

		List<String> timesheetIdsList = new ArrayList<String>();
		if (timesheetIds.length() > 0) {
			timesheetIdsList = Arrays.asList(timesheetIds.split(","));
		}

		SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

		String companyDateFormat = companyDAO.findById(companyId)
				.getDateFormat();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				companyDateFormat);

		JSONObject lionTimesheetbatchJsonObject = new JSONObject();
		JSONArray lionTimesheetbatchJsonArray = new JSONArray();

		for (String timesheetIdS : timesheetIdsList) {
			long timesheetIdL = Long.parseLong(timesheetIdS);

			EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
					.findById(timesheetIdL);

			List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
					.getLionEmployeeTimesheetApplicationDetails(timesheetIdL);

			JSONObject jsonObject = new JSONObject();
			JSONArray lionEmployeeTimesheetApplicationDetailsJson = new JSONArray();

			long employeeTimesheetDetailIDForReviewr = 0l;

			for (LionEmployeeTimesheetApplicationDetail employeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {

				if (employeeTimesheetApplicationDetail
						.getTimesheetStatusMaster()
						.getTimesheetStatusName()
						.equals(PayAsiaConstants.LION_TIMESHEET_STATUS_SUBMITTED)) {

					try {
						JSONObject timesheetJsonObject = new JSONObject();
						/*ID ENCRYPT*/
						timesheetJsonObject.put("employeeTimesheetDetailID",
								 FormatPreserveCryptoUtil.encrypt(employeeTimesheetApplicationDetail
										.getEmployeeTimesheetDetailID()));
						timesheetJsonObject
								.put("timesheetDate",
										simpleDateFormat
												.format(employeeTimesheetApplicationDetail
														.getTimesheetDate()));

						Date date = DateUtils
								.stringToDate(
										simpleDateFormat
												.format(employeeTimesheetApplicationDetail
														.getTimesheetDate()),
										companyDateFormat);
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);

						timesheetJsonObject.put("month",
								cal.get(Calendar.MONTH));
						timesheetJsonObject.put("day",
								cal.get(Calendar.DAY_OF_MONTH));
						timesheetJsonObject.put("year", cal.get(Calendar.YEAR));

						timesheetJsonObject.put("timesheetDay", format2
								.format(employeeTimesheetApplicationDetail
										.getTimesheetDate()));

						Date timesheetDate = new Date(
								employeeTimesheetApplicationDetail
										.getTimesheetDate().getTime());

						List<String> holidayList = getHolidaysFor(employeeId,
								timesheetDate, timesheetDate);

						if (holidayList.size() > 0) {
							timesheetJsonObject.put("isHoliday", "true");
						} else {
							timesheetJsonObject.put("isHoliday", "false");
						}

						timesheetJsonObject.put("inTime",
								employeeTimesheetApplicationDetail.getInTime());
						timesheetJsonObject.put("inTimeHoursChanged", String
								.valueOf(employeeTimesheetApplicationDetail
										.isInTimeChanged()));
						timesheetJsonObject
								.put("outTime",
										employeeTimesheetApplicationDetail
												.getOutTime());
						timesheetJsonObject.put("outTimeHoursChanged", String
								.valueOf(employeeTimesheetApplicationDetail
										.isOutTimeChanged()));
						timesheetJsonObject.put("breakTimeHours",
								employeeTimesheetApplicationDetail
										.getBreakTimeHours());
						timesheetJsonObject.put("breakTimeHoursChanged", String
								.valueOf(employeeTimesheetApplicationDetail
										.isBreakTimeHoursChanged()));
						timesheetJsonObject.put("totalHoursWorked",
								employeeTimesheetApplicationDetail
										.getTotalHoursWorked());

						timesheetJsonObject.put("timesheetStatus",
								employeeTimesheetApplicationDetail
										.getTimesheetStatusMaster()
										.getTimesheetStatusName());

						lionEmployeeTimesheetApplicationDetailsJson
								.put(timesheetJsonObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				if (employeeTimesheetApplicationDetail
						.getTimesheetStatusMaster()
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

			TimesheetBatch timesheetBatch = timesheetBatchDAO
					.findById(employeeTimesheetApplication.getTimesheetBatch()
							.getTimesheetBatchId());

			LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
					.findByEmployeeTimesheetApplication(timesheetIdL).get(0);

			Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
			Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();

			LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
					.findByEmployeeTimesheetDetailId(employeeTimesheetDetailIDForReviewr);
			try {

				jsonObject.put("startDate",
						simpleDateFormat.format(timesheetBatchStartDate));
				jsonObject.put("endDate",
						simpleDateFormat.format(timesheetBatchEndDate));
				jsonObject.put("timesheetBatchId",
						timesheetBatch.getTimesheetBatchId());
				jsonObject.put("lionEmployeeTimesheetApplicationDetails",
						lionEmployeeTimesheetApplicationDetailsJson);
				Employee employee = employeeTimesheetApplication.getEmployee();
				jsonObject.put("employeeId", employee.getEmployeeNumber());
				jsonObject.put("grandTotalHours", lionEmployeeTimesheet
						.getTimesheetTotalHours().toString());
				jsonObject.put("excessHours", lionEmployeeTimesheet
						.getExcessHoursWorked().toString());

				jsonObject.put("employeeName", getEmployeeName(employee));

				String location = getLocation(employeeId, companyId,
						employeeTimesheetApplication);

				jsonObject.put("location", location);

				jsonObject.put("reviewer",
						getEmployeeFullName(lionTimesheetApplicationReviewer
								.getEmployeeReviewer()));

			} catch (JSONException e) {
				e.printStackTrace();
			}

			lionTimesheetbatchJsonArray.put(jsonObject);

		}
		try {
			lionTimesheetbatchJsonObject.put("lionTimesheetbatchJsonArray",
					lionTimesheetbatchJsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return lionTimesheetbatchJsonObject.toString();

	}

	public String getEmployeeFullName(Employee employee) {
		String empFullName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			empFullName = empFullName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			empFullName = empFullName + " " + employee.getLastName();
		}
		empFullName = empFullName + " (" + employee.getEmployeeNumber() + ")";
		empFullName = empFullName.toUpperCase();
		return empFullName;
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
}
