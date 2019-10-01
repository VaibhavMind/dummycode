package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.payasia.dao.AppCodeMasterDAO;
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
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.CompanyHolidayCalendar;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHolidayCalendar;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.LionEmployeeTimesheet;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionTimesheetApplicationReviewer;
import com.payasia.dao.bean.LionTimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.LionEmployeeTimesheetApplicationDetailLogic;
import com.payasia.logic.LionTimesheetMailLogic;
import com.payasia.logic.LionTimesheetReportsLogic;

@Component
public class LionEmployeeTimesheetApplicationDetailLogicImpl implements LionEmployeeTimesheetApplicationDetailLogic {

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
	LionTimesheetApplicationReviewerDAO lionTimesheetReviewerDAO;

	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	LionTimesheetMailLogic lionTimesheetMailLogic;

	@Resource
	LionTimesheetReportsLogic lionTimesheetReportsLogic;

	private String convertTimestampToString(Timestamp timestamp, String dateFormat) {
		Date date = new Date(timestamp.getTime());
		dateFormat += " HH:mm";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	@Override
	public String getTimesheetApplications(long timesheetId, long employeeId, long companyId) {

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(timesheetId);

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(timesheetId);

		JSONObject jsonObject = new JSONObject();
		JSONArray lionEmployeeTimesheetApplicationDetailsJson = new JSONArray();

		SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

		String companyDateFormat = companyDAO.findById(companyId).getDateFormat();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(companyDateFormat);

		Double grandTotalHours = 0.0;

		for (LionEmployeeTimesheetApplicationDetail employeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {
			try {
				JSONObject timesheetJsonObject = new JSONObject();
				/* ID ENCRYPT */
				timesheetJsonObject.put("employeeTimesheetDetailID", FormatPreserveCryptoUtil
						.encrypt(employeeTimesheetApplicationDetail.getEmployeeTimesheetDetailID()));
				timesheetJsonObject.put("timesheetDate",
						simpleDateFormat.format(employeeTimesheetApplicationDetail.getTimesheetDate()));

				Date date = DateUtils.stringToDate(
						simpleDateFormat.format(employeeTimesheetApplicationDetail.getTimesheetDate()),
						companyDateFormat);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				timesheetJsonObject.put("month", cal.get(Calendar.MONTH));
				timesheetJsonObject.put("day", cal.get(Calendar.DAY_OF_MONTH));
				timesheetJsonObject.put("year", cal.get(Calendar.YEAR));

				timesheetJsonObject.put("timesheetDay",
						format2.format(employeeTimesheetApplicationDetail.getTimesheetDate()));

				Date timesheetDate = new Date(employeeTimesheetApplicationDetail.getTimesheetDate().getTime());

				List<String> holidayList = getHolidaysFor(employeeId, timesheetDate, timesheetDate);

				if (holidayList.size() > 0) {
					timesheetJsonObject.put("isHoliday", "true");
				} else {
					timesheetJsonObject.put("isHoliday", "false");
				}

				timesheetJsonObject.put("inTime", employeeTimesheetApplicationDetail.getInTime());
				if (employeeTimesheetApplicationDetail.getRemarks() != null) {
					timesheetJsonObject.put("remarks", employeeTimesheetApplicationDetail.getRemarks());
				} else {
					timesheetJsonObject.put("remarks", "");
				}
				timesheetJsonObject.put("inTimeHoursChanged",
						String.valueOf(employeeTimesheetApplicationDetail.isInTimeChanged()));
				timesheetJsonObject.put("outTime", employeeTimesheetApplicationDetail.getOutTime());
				timesheetJsonObject.put("outTimeHoursChanged",
						String.valueOf(employeeTimesheetApplicationDetail.isOutTimeChanged()));
				timesheetJsonObject.put("breakTimeHours", employeeTimesheetApplicationDetail.getBreakTimeHours());
				timesheetJsonObject.put("breakTimeHoursChanged",
						String.valueOf(employeeTimesheetApplicationDetail.isBreakTimeHoursChanged()));

				timesheetJsonObject.put("totalHoursWorked", employeeTimesheetApplicationDetail.getTotalHoursWorked());

				timesheetJsonObject.put("timesheetStatus",
						employeeTimesheetApplicationDetail.getTimesheetStatusMaster().getTimesheetStatusName());
				grandTotalHours += employeeTimesheetApplicationDetail.getTotalHoursWorked();

				lionEmployeeTimesheetApplicationDetailsJson.put(timesheetJsonObject);
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}

		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(employeeTimesheetApplication.getTimesheetBatch().getTimesheetBatchId());

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(timesheetId).get(0);

		Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
		Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();

		try {

			jsonObject.put("startDate", simpleDateFormat.format(timesheetBatchStartDate));
			jsonObject.put("endDate", simpleDateFormat.format(timesheetBatchEndDate));
			jsonObject.put("timesheetBatchId", timesheetBatch.getTimesheetBatchId());
			jsonObject.put("lionEmployeeTimesheetApplicationDetails", lionEmployeeTimesheetApplicationDetailsJson);
			Employee employee = employeeDAO.findById(employeeId);
			jsonObject.put("employeeId", employee.getEmployeeNumber());
			jsonObject.put("grandTotalHours", grandTotalHours.toString());
			jsonObject.put("excessHours", lionEmployeeTimesheet.getExcessHoursWorked().toString());

			jsonObject.put("employeeName", getEmployeeName(employeeTimesheetApplication.getEmployee()));
			jsonObject.put("reviewerName", getReviewerName(employeeTimesheetApplication));

			String location = getLocation(employeeTimesheetApplication.getEmployee().getEmployeeId(),
					employeeTimesheetApplication.getCompany().getCompanyId(), employeeTimesheetApplication);

			jsonObject.put("location", location);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return jsonObject.toString();

	}

	private String getLocation(long employeeId, long companyId,
			EmployeeTimesheetApplication employeeTimesheetApplication) {
		String location = null;

		List<Object[]> locationEmployeeObjList = lionTimesheetReportsLogic.getTimesheetLocationEmpList(companyId,
				companyDAO.findById(companyId).getDateFormat(), employeeId);
		// Get Department Of Employees from Employee Information
		// Details
		for (Object[] deptObject : locationEmployeeObjList) {
			if (deptObject != null && deptObject[1] != null && deptObject[0] != null) {
				if (StringUtils.isNotBlank(String.valueOf(deptObject[1])) && employeeTimesheetApplication.getEmployee()
						.getEmployeeNumber().equalsIgnoreCase(String.valueOf(deptObject[1]))) {
					location = String.valueOf(deptObject[0]);
				}

			}
		}
		return location;
	}

	@Override
	public String getLionTimesheetJSON(long batchId, long companyId, long employeeId) {

		EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO.findByEmployeeId(employeeId);

		JSONObject jsonObject = new JSONObject();

		if (employeeTimesheetReviewer.getEmployeeReviewer() == null) {
			try {
				jsonObject.put("reviewrStatus", "false");
			} catch (JSONException e) {

				e.printStackTrace();
			}
		} else {

			TimesheetBatch timesheetBatch = timesheetBatchDAO.findById(batchId);

			List<EmployeeTimesheetApplication> employeeTimesheetApplicationOld = employeeTimesheetApplicationDAO
					.findByTimesheetBatchId(batchId, companyId, employeeId);

			List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = new ArrayList<LionEmployeeTimesheetApplicationDetail>();

			String companyDateFormat = companyDAO.findById(companyId).getDateFormat();

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(companyDateFormat);

			Long timesheetId = 0l;
			if (employeeTimesheetApplicationOld.size() == 0) {
				timesheetId = saveEmployeeTimesheetApplication(batchId, companyId, employeeId);
				EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
						.findById(timesheetId);

				lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
						.getLionEmployeeTimesheetApplicationDetails(timesheetId);

				Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
				Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();
				timesheetBatchStartDate.setHours(0);
				timesheetBatchStartDate.setMinutes(0);
				Timestamp newTimestamp = timesheetBatchStartDate;
				LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail;

				int dayNum = 1;
				long oneDay = 1 * 24 * 60 * 60 * 1000;
				if (lionEmployeeTimesheetApplicationDetails.size() == 0) {
					do {
						newTimestamp.setHours(0);
						newTimestamp.setMinutes(0);

						lionEmployeeTimesheetApplicationDetail = new LionEmployeeTimesheetApplicationDetail();

						lionEmployeeTimesheetApplicationDetail
								.setEmployeeTimesheetApplication(employeeTimesheetApplication);
						lionEmployeeTimesheetApplicationDetail.setTimesheetDate(newTimestamp);
						lionEmployeeTimesheetApplicationDetail.setInTime(newTimestamp);
						lionEmployeeTimesheetApplicationDetail.setOutTime(newTimestamp);
						lionEmployeeTimesheetApplicationDetail.setBreakTimeHours(newTimestamp);
						lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(0.0);
						lionEmployeeTimesheetApplicationDetail.setInTimeChanged(false);
						lionEmployeeTimesheetApplicationDetail.setOutTimeChanged(false);
						lionEmployeeTimesheetApplicationDetail.setBreakTimeHoursChanged(false);
						lionEmployeeTimesheetApplicationDetail.setRemarks("");
						lionEmployeeTimesheetApplicationDetail
								.setTimesheetStatusMaster(timesheetStatusMasterDAO.findByName("Draft"));
						lionEmployeeTimesheetApplicationDetailDAO.save(lionEmployeeTimesheetApplicationDetail);

						newTimestamp = new Timestamp(timesheetBatchStartDate.getTime() + oneDay * dayNum);
						dayNum++;

					} while (newTimestamp.compareTo(timesheetBatchEndDate) <= 0);

					LionEmployeeTimesheet lionEmployeeTimesheet = new LionEmployeeTimesheet();
					lionEmployeeTimesheet.setEmployeeTimesheetApplication(employeeTimesheetApplication);
					lionEmployeeTimesheet.setTimesheetTotalHours(0.0);
					lionEmployeeTimesheet.setExcessHoursWorked(0.0);
					lionEmployeeTimesheetDAO.save(lionEmployeeTimesheet);
				}

			} else {
				timesheetId = employeeTimesheetApplicationOld.get(0).getTimesheetId();
			}

			lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
					.getLionEmployeeTimesheetApplicationDetails(timesheetId);

			JSONArray lionEmployeeTimesheetApplicationDetailsJson = new JSONArray();

			SimpleDateFormat format2 = new SimpleDateFormat("EEEE");

			for (LionEmployeeTimesheetApplicationDetail employeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {
				try {
					/* ID ENCRYPT */
					JSONObject timesheetJsonObject = new JSONObject();
					timesheetJsonObject.put("employeeTimesheetDetailID", FormatPreserveCryptoUtil
							.encrypt(employeeTimesheetApplicationDetail.getEmployeeTimesheetDetailID()));
					timesheetJsonObject.put("timesheetDate",
							simpleDateFormat.format(employeeTimesheetApplicationDetail.getTimesheetDate()));

					timesheetJsonObject.put("timesheetDay",
							format2.format(employeeTimesheetApplicationDetail.getTimesheetDate()));

					Date date = DateUtils.stringToDate(
							simpleDateFormat.format(employeeTimesheetApplicationDetail.getTimesheetDate()),
							companyDateFormat);
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);

					timesheetJsonObject.put("month", cal.get(Calendar.MONTH));
					timesheetJsonObject.put("day", cal.get(Calendar.DAY_OF_MONTH));
					timesheetJsonObject.put("year", cal.get(Calendar.YEAR));

					Date timesheetDate = new Date(employeeTimesheetApplicationDetail.getTimesheetDate().getTime());

					List<String> holidayList = getHolidaysFor(employeeId, timesheetDate, timesheetDate);

					if (holidayList.size() > 0) {
						timesheetJsonObject.put("isHoliday", "true");
					} else {
						timesheetJsonObject.put("isHoliday", "false");
					}

					timesheetJsonObject.put("inTime",
							convertTimestampToString(employeeTimesheetApplicationDetail.getInTime(), "yyyy-MM-dd"));
					timesheetJsonObject.put("outTime",
							convertTimestampToString(employeeTimesheetApplicationDetail.getOutTime(), "yyyy-MM-dd"));

					timesheetJsonObject.put("breakTimeHours", employeeTimesheetApplicationDetail.getBreakTimeHours());

					timesheetJsonObject.put("totalHoursWorked",
							employeeTimesheetApplicationDetail.getTotalHoursWorked());

					lionEmployeeTimesheetApplicationDetailsJson.put(timesheetJsonObject);
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}

			try {
				jsonObject.put("startDate", simpleDateFormat.format(timesheetBatch.getStartDate()));
				jsonObject.put("endDate", simpleDateFormat.format(timesheetBatch.getEndDate()));
				jsonObject.put("timesheetBatchId", timesheetBatch.getTimesheetBatchId());
				jsonObject.put("lionEmployeeTimesheetApplicationDetails", lionEmployeeTimesheetApplicationDetailsJson);
				Employee employee = employeeDAO.findById(employeeId);
				jsonObject.put("employeeId", employee.getEmployeeNumber());

				jsonObject.put("employeeName", getEmployeeName(employee));

				EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
						.findById(timesheetId);

				String location = getLocation(employeeTimesheetApplication.getEmployee().getEmployeeId(),
						employeeTimesheetApplication.getCompany().getCompanyId(), employeeTimesheetApplication);
				jsonObject.put("timesheetId", employeeTimesheetApplication.getTimesheetId());
				jsonObject.put("location", location);
				jsonObject.put("reviewrStatus", "true");
			} catch (JSONException e) {

				e.printStackTrace();
			}

		}
		return jsonObject.toString();

	}

	@Override
	public long saveEmployeeTimesheetApplication(long batchId, long companyId, long employeeId) {

		EmployeeTimesheetApplication employeeTimesheetApplication = new EmployeeTimesheetApplication();
		employeeTimesheetApplication.setCompany(companyDAO.findById(companyId));
		employeeTimesheetApplication.setEmployee(employeeDAO.findById(employeeId));
		TimesheetBatch timesheetBatch = timesheetBatchDAO.findById(batchId);
		employeeTimesheetApplication.setTimesheetBatch(timesheetBatch);
		employeeTimesheetApplication.setRemarks("Draft");
		employeeTimesheetApplication.setTimesheetStatusMaster(timesheetStatusMasterDAO.findByName("Draft"));

		return employeeTimesheetApplicationDAO.saveAndReturn(employeeTimesheetApplication);

	}

	@Override
	public Map<String, String> updateLionEmployeeTimesheetApplicationDetailEmployee(long employeeTimesheetDetailId,
			String inTime, String outTime, String breakTime, String totalHours, String grandTotalHours,
			String excessHours, String remarks) {

		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;

		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);
		lionEmployeeTimesheetApplicationDetail.setRemarks(remarks);
		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":" + oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail.setInTime(newInTimeTimestamp);
			inTimeStatus = 1;
		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":" + oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail.setOutTime(newOutTimeTimestamp);
			outTimeStatus = 1;
		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHours(newBreakTimeTimestamp);
			breakTimeStatus = 1;
		}

		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(Double.parseDouble(totalHours));

		lionEmployeeTimesheetApplicationDetailDAO.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId())
				.get(0);

		lionEmployeeTimesheet.setTimesheetTotalHours(Double.parseDouble(grandTotalHours));
		lionEmployeeTimesheet.setExcessHoursWorked(Double.parseDouble(excessHours));
		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));

		return statusMap;

	}

	@Override
	public Map<String, String> updateLionEmployeeTimesheetApplicationDetailReviewer(long employeeTimesheetDetailId,
			String inTime, String outTime, String breakTime, String totalHours, String grandTotalHours,
			String excessHours, String remark) {

		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;

		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);
		lionEmployeeTimesheetApplicationDetail.setRemarks(remark);
		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":" + oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail.setInTime(newInTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setInTimeChanged(true);
			inTimeStatus = 1;
		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":" + oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail.setOutTime(newOutTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setOutTimeChanged(true);
			outTimeStatus = 1;
		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHours(newBreakTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHoursChanged(true);
			breakTimeStatus = 1;
		}

		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(Double.parseDouble(totalHours));

		TimesheetStatusMaster timesheetStatusMaster = timesheetStatusMasterDAO.findByName("Submitted");

		lionEmployeeTimesheetApplicationDetail.setTimesheetStatusMaster(timesheetStatusMaster);
		lionEmployeeTimesheetApplicationDetailDAO.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId())
				.get(0);

		lionEmployeeTimesheet.setTimesheetTotalHours(Double.parseDouble(grandTotalHours));
		lionEmployeeTimesheet.setExcessHoursWorked(Double.parseDouble(excessHours));
		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
				.findByEmployeeTimesheetDetailId(employeeTimesheetDetailId);

		if (lionTimesheetApplicationReviewer == null) {
			EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO
					.findByEmployeeId(lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication()
							.getEmployee().getEmployeeId());

			Employee reviewer = getDelegatedEmployee(employeeTimesheetReviewer.getEmployeeReviewer().getEmployeeId());

			LionTimesheetApplicationReviewer newLionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
			newLionTimesheetApplicationReviewer
					.setLionEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
			newLionTimesheetApplicationReviewer
					.setWorkFlowRuleMaster(employeeTimesheetReviewer.getWorkFlowRuleMaster());
			newLionTimesheetApplicationReviewer.setEmployeeReviewer(reviewer);
			newLionTimesheetApplicationReviewer.setReviewerEmail(employeeTimesheetReviewer.getReviewerEmail());
			newLionTimesheetApplicationReviewer.setPending(true);

			lionTimesheetApplicationReviewerDAO.save(newLionTimesheetApplicationReviewer);

		}

		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));

		return statusMap;

	}

	@Override
	public void submitLionEmployeeTimesheetApplicationDetail(long employeeId, long employeeTimesheetDetailId,
			String inTime, String outTime, String breakTime, String totalHours, String grandTotalHours,
			String excessHours, String remarks) {

		boolean isSuccessfullyFor = false;

		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);
		lionEmployeeTimesheetApplicationDetail.setRemarks(remarks);
		long timesheetId = lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId();

		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":" + oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail.setInTime(newInTimeTimestamp);

		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":" + oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail.setOutTime(newOutTimeTimestamp);

		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHours(newBreakTimeTimestamp);

		}

		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(Double.parseDouble(totalHours));

		TimesheetStatusMaster timesheetStatusMaster = timesheetStatusMasterDAO.findByName("Submitted");

		lionEmployeeTimesheetApplicationDetail.setTimesheetStatusMaster(timesheetStatusMaster);

		lionEmployeeTimesheetApplicationDetailDAO.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId())
				.get(0);

		lionEmployeeTimesheet.setTimesheetTotalHours(Double.parseDouble(grandTotalHours));
		lionEmployeeTimesheet.setExcessHoursWorked(Double.parseDouble(excessHours));
		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO.findByEmployeeId(employeeId);

		Employee reviewer = getDelegatedEmployee(employeeTimesheetReviewer.getEmployeeReviewer().getEmployeeId());

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
		lionTimesheetApplicationReviewer
				.setLionEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
		lionTimesheetApplicationReviewer.setWorkFlowRuleMaster(employeeTimesheetReviewer.getWorkFlowRuleMaster());
		lionTimesheetApplicationReviewer.setEmployeeReviewer(reviewer);
		lionTimesheetApplicationReviewer.setReviewerEmail(employeeTimesheetReviewer.getReviewerEmail());
		lionTimesheetApplicationReviewer.setPending(true);

		long lionTimesheetReviewerId = lionTimesheetApplicationReviewerDAO
				.saveReviewerAndReturnId(lionTimesheetApplicationReviewer);

		LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow = new LionTimesheetApplicationWorkflow();
		lionTimesheetApplicationWorkflow.setEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
		lionTimesheetApplicationWorkflow.setTimesheetStatusMaster(timesheetStatusMaster);
		lionTimesheetApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		lionTimesheetApplicationWorkflow.setCreatedBy(employeeDAO.findById(employeeId));

		lionTimesheetApplicationWorkflowDAO.save(lionTimesheetApplicationWorkflow);

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(timesheetId);
		employeeTimesheetApplication.setTimesheetStatusMaster(timesheetStatusMaster);
		employeeTimesheetApplicationDAO.update(employeeTimesheetApplication);

		isSuccessfullyFor = true;

		if (isSuccessfullyFor) {
			lionTimesheetMailLogic.sendEMailForTimesheet(employeeTimesheetApplication.getCompany().getCompanyId(),
					employeeTimesheetApplication, lionTimesheetApplicationReviewerDAO.findById(lionTimesheetReviewerId),
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPLY, employeeDAO.findById(employeeId),
					reviewer);

		}

	}

	@Override
	public void submitEmpTimesheetRowByRev(long employeeId, long employeeTimesheetDetailId, String inTime,
			String outTime, String breakTime, String totalHours, String grandTotalHours, String excessHours,
			String remarks) {
		boolean isSuccessfullyFor = false;

		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);

		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;
		long timesheetId = lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId();
		lionEmployeeTimesheetApplicationDetail.setRemarks(remarks);
		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":" + oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail.setInTime(newInTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setInTimeChanged(true);
			inTimeStatus = 1;
		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":" + oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail.setOutTime(newOutTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setOutTimeChanged(true);
			outTimeStatus = 1;
		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHours(newBreakTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHoursChanged(true);
			breakTimeStatus = 1;
		}

		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(Double.parseDouble(totalHours));

		TimesheetStatusMaster timesheetStatusMaster = timesheetStatusMasterDAO.findByName("Submitted");

		lionEmployeeTimesheetApplicationDetail.setTimesheetStatusMaster(timesheetStatusMaster);

		lionEmployeeTimesheetApplicationDetailDAO.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId())
				.get(0);

		lionEmployeeTimesheet.setTimesheetTotalHours(Double.parseDouble(grandTotalHours));
		lionEmployeeTimesheet.setExcessHoursWorked(Double.parseDouble(excessHours));
		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO.findByEmployeeId(
				lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getEmployee().getEmployeeId());

		Employee reviewer = getDelegatedEmployee(employeeTimesheetReviewer.getEmployeeReviewer().getEmployeeId());

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
		lionTimesheetApplicationReviewer
				.setLionEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
		lionTimesheetApplicationReviewer.setWorkFlowRuleMaster(employeeTimesheetReviewer.getWorkFlowRuleMaster());
		lionTimesheetApplicationReviewer.setEmployeeReviewer(reviewer);
		lionTimesheetApplicationReviewer.setReviewerEmail(employeeTimesheetReviewer.getReviewerEmail());
		lionTimesheetApplicationReviewer.setPending(true);

		long lionTimesheetReviewerId = lionTimesheetApplicationReviewerDAO
				.saveReviewerAndReturnId(lionTimesheetApplicationReviewer);

		LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow = new LionTimesheetApplicationWorkflow();
		lionTimesheetApplicationWorkflow.setEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
		lionTimesheetApplicationWorkflow.setTimesheetStatusMaster(timesheetStatusMaster);
		lionTimesheetApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		lionTimesheetApplicationWorkflow.setCreatedBy(employeeDAO.findById(employeeId));

		lionTimesheetApplicationWorkflowDAO.save(lionTimesheetApplicationWorkflow);

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(timesheetId);
		employeeTimesheetApplication.setTimesheetStatusMaster(timesheetStatusMaster);
		employeeTimesheetApplicationDAO.update(employeeTimesheetApplication);

		isSuccessfullyFor = true;

		if (isSuccessfullyFor) {
			lionTimesheetMailLogic.submitTimesheetEmailByRevForEmp(
					employeeTimesheetApplication.getCompany().getCompanyId(), employeeTimesheetApplication,
					lionTimesheetApplicationReviewerDAO.findById(lionTimesheetReviewerId),
					PayAsiaConstants.PAYASIA_SUB_CATEGORY_TIMESHEET_SUBMIT_BY_MANAGER_FOR_EMP,
					employeeDAO.findById(employeeId), reviewer);

		}

		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));

		// return statusMap;

	}

	@Override
	public Map<String, String> approveLionEmployeeTimesheetApplicationDetail(long employeeId,
			long employeeTimesheetDetailId, String inTime, String outTime, String breakTime, String totalHours,
			String grandTotalHours, String excessHours, String remark) {

		boolean isSuccessfullyFor = false;

		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;

		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);
		lionEmployeeTimesheetApplicationDetail.setRemarks(remark);

		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":" + oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail.setInTime(newInTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setInTimeChanged(true);
			inTimeStatus = 1;
		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":" + oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail.setOutTime(newOutTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setOutTimeChanged(true);
			outTimeStatus = 1;
		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHours(newBreakTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHoursChanged(true);
			breakTimeStatus = 1;
		}

		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(Double.parseDouble(totalHours));

		TimesheetStatusMaster timesheetStatusMaster = timesheetStatusMasterDAO.findByName("Approved");

		lionEmployeeTimesheetApplicationDetail.setTimesheetStatusMaster(timesheetStatusMaster);

		lionEmployeeTimesheetApplicationDetailDAO.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId())
				.get(0);

		lionEmployeeTimesheet.setTimesheetTotalHours(Double.parseDouble(grandTotalHours));
		lionEmployeeTimesheet.setExcessHoursWorked(Double.parseDouble(excessHours));
		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO.findByReviewerId(employeeId);

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
				.findByEmployeeTimesheetDetailId(lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetDetailID());

		if (lionTimesheetApplicationReviewer == null) {

			Employee reviewer = getDelegatedEmployee(employeeTimesheetReviewer.getEmployeeReviewer().getEmployeeId());

			LionTimesheetApplicationReviewer newLionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
			newLionTimesheetApplicationReviewer
					.setLionEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
			newLionTimesheetApplicationReviewer
					.setWorkFlowRuleMaster(employeeTimesheetReviewer.getWorkFlowRuleMaster());
			newLionTimesheetApplicationReviewer.setEmployeeReviewer(reviewer);
			newLionTimesheetApplicationReviewer.setReviewerEmail(employeeTimesheetReviewer.getReviewerEmail());
			newLionTimesheetApplicationReviewer.setPending(false);

			lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
					.saveAndReturn(newLionTimesheetApplicationReviewer);

		} else {
			lionTimesheetApplicationReviewer.setPending(false);

			lionTimesheetApplicationReviewerDAO.update(lionTimesheetApplicationReviewer);
		}

		LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow = new LionTimesheetApplicationWorkflow();
		lionTimesheetApplicationWorkflow.setEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
		lionTimesheetApplicationWorkflow.setTimesheetStatusMaster(timesheetStatusMaster);
		lionTimesheetApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		lionTimesheetApplicationWorkflow.setCreatedBy(employeeDAO.findById(employeeId));

		lionTimesheetApplicationWorkflowDAO.save(lionTimesheetApplicationWorkflow);

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = new ArrayList<LionEmployeeTimesheetApplicationDetail>(
				lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication()
						.getLionEmployeeTimesheetApplicationDetails());

		int timesheetStatusCheck = 0;

		List<LionEmployeeTimesheetApplicationDetail> unApprovedTimesheet = new ArrayList<LionEmployeeTimesheetApplicationDetail>();

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail2 : lionEmployeeTimesheetApplicationDetails) {
			if (!lionEmployeeTimesheetApplicationDetail2.getTimesheetStatusMaster().getTimesheetStatusName()
					.equals("Approved")) {
				timesheetStatusCheck = 1;
				unApprovedTimesheet.add(lionEmployeeTimesheetApplicationDetail2);
			}
		}

		int timesheetStatusHolidayCheck = 0;

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId());

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail2 : unApprovedTimesheet) {

			Date timesheetDate = new Date(lionEmployeeTimesheetApplicationDetail2.getTimesheetDate().getTime());

			List<String> holidayList = getHolidaysFor(employeeTimesheetApplication.getEmployee().getEmployeeId(),
					timesheetDate, timesheetDate);
			if (holidayList.size() == 0) {
				timesheetStatusHolidayCheck = 1;
			}
		}

		if (timesheetStatusCheck == 0 || timesheetStatusHolidayCheck == 0) {

			TimesheetStatusMaster timesheetStatusMaster2 = timesheetStatusMasterDAO.findByName("Completed");
			employeeTimesheetApplication.setTimesheetStatusMaster(timesheetStatusMaster2);
			employeeTimesheetApplicationDAO.update(employeeTimesheetApplication);
		}

		isSuccessfullyFor = true;

		String reviewRemarks = "";

		if (isSuccessfullyFor) {
			lionTimesheetMailLogic.sendAcceptRejectMailForTimesheet(
					employeeTimesheetApplication.getCompany().getCompanyId(), employeeTimesheetApplication,
					lionTimesheetApplicationReviewer, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPROVE,
					employeeDAO.findById(employeeId), reviewRemarks);

		}

		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));

		return statusMap;
	}

	@Override
	public Map<String, String> submitAndApproveEmpTimesheetAppDetailByRev(long employeeId,
			long employeeTimesheetDetailId, String inTime, String outTime, String breakTime, String totalHours,
			String grandTotalHours, String excessHours, String remarks) {
		boolean isSuccessfullyFor = false;

		int inTimeStatus = 0;
		int outTimeStatus = 0;
		int breakTimeStatus = 0;
		LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail = lionEmployeeTimesheetApplicationDetailDAO
				.findById(employeeTimesheetDetailId);
		lionEmployeeTimesheetApplicationDetail.setRemarks(remarks);
		String timesheetStatusBeforeApprove = lionEmployeeTimesheetApplicationDetail.getTimesheetStatusMaster()
				.getTimesheetStatusName();

		String[] oldInTime = lionEmployeeTimesheetApplicationDetail.getInTime().toString().split(" ");
		String[] oldInTime2 = oldInTime[1].split(":");

		String[] oldOutTime = lionEmployeeTimesheetApplicationDetail.getOutTime().toString().split(" ");
		String[] oldOutTime2 = oldOutTime[1].split(":");

		String[] oldBreakTime = lionEmployeeTimesheetApplicationDetail.getBreakTimeHours().toString().split(" ");
		String[] oldBreakTime2 = oldBreakTime[1].split(":");

		if (!inTime.equals(oldInTime2[0] + ":" + oldInTime2[1])) {
			String newInTime = oldInTime[0] + " " + inTime + ":" + oldInTime2[2];
			Timestamp newInTimeTimestamp = Timestamp.valueOf(newInTime);
			lionEmployeeTimesheetApplicationDetail.setInTime(newInTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setInTimeChanged(true);
			inTimeStatus = 1;
		}
		if (!outTime.equals(oldOutTime2[0] + ":" + oldOutTime2[1])) {

			String newOutTime = oldOutTime[0] + " " + outTime + ":" + oldOutTime2[2];
			Timestamp newOutTimeTimestamp = Timestamp.valueOf(newOutTime);
			lionEmployeeTimesheetApplicationDetail.setOutTime(newOutTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setOutTimeChanged(true);
			outTimeStatus = 1;
		}
		if (!breakTime.equals(oldBreakTime2[0] + ":" + oldBreakTime2[1])) {

			String newBreakTime = oldBreakTime[0] + " " + breakTime + ":" + oldBreakTime2[2];
			Timestamp newBreakTimeTimestamp = Timestamp.valueOf(newBreakTime);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHours(newBreakTimeTimestamp);
			lionEmployeeTimesheetApplicationDetail.setBreakTimeHoursChanged(true);
			breakTimeStatus = 1;
		}

		lionEmployeeTimesheetApplicationDetail.setTotalHoursWorked(Double.parseDouble(totalHours));

		TimesheetStatusMaster timesheetStatusMaster = timesheetStatusMasterDAO.findByName("Approved");

		lionEmployeeTimesheetApplicationDetail.setTimesheetStatusMaster(timesheetStatusMaster);

		lionEmployeeTimesheetApplicationDetailDAO.update(lionEmployeeTimesheetApplicationDetail);

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId())
				.get(0);

		lionEmployeeTimesheet.setTimesheetTotalHours(Double.parseDouble(grandTotalHours));
		lionEmployeeTimesheet.setExcessHoursWorked(Double.parseDouble(excessHours));
		lionEmployeeTimesheetDAO.update(lionEmployeeTimesheet);

		EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO.findByReviewerId(employeeId);

		LionTimesheetApplicationReviewer lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
				.findByEmployeeTimesheetDetailId(lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetDetailID());

		if (lionTimesheetApplicationReviewer == null) {

			Employee reviewer = getDelegatedEmployee(employeeTimesheetReviewer.getEmployeeReviewer().getEmployeeId());

			LionTimesheetApplicationReviewer newLionTimesheetApplicationReviewer = new LionTimesheetApplicationReviewer();
			newLionTimesheetApplicationReviewer
					.setLionEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
			newLionTimesheetApplicationReviewer
					.setWorkFlowRuleMaster(employeeTimesheetReviewer.getWorkFlowRuleMaster());
			newLionTimesheetApplicationReviewer.setEmployeeReviewer(reviewer);
			newLionTimesheetApplicationReviewer.setReviewerEmail(employeeTimesheetReviewer.getReviewerEmail());
			newLionTimesheetApplicationReviewer.setPending(false);

			lionTimesheetApplicationReviewer = lionTimesheetApplicationReviewerDAO
					.saveAndReturn(newLionTimesheetApplicationReviewer);

		} else {
			lionTimesheetApplicationReviewer.setPending(false);

			lionTimesheetApplicationReviewerDAO.update(lionTimesheetApplicationReviewer);
		}

		if (timesheetStatusBeforeApprove.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_DRAFT)) {
			LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow = new LionTimesheetApplicationWorkflow();
			lionTimesheetApplicationWorkflow
					.setEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
			TimesheetStatusMaster timesheetStatusSubmitted = timesheetStatusMasterDAO
					.findByName(PayAsiaConstants.LUNDIN_STATUS_SUBMITTED);
			lionTimesheetApplicationWorkflow.setTimesheetStatusMaster(timesheetStatusSubmitted);
			lionTimesheetApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			lionTimesheetApplicationWorkflow.setCreatedBy(employeeDAO.findById(employeeId));
			lionTimesheetApplicationWorkflowDAO.save(lionTimesheetApplicationWorkflow);
		}

		LionTimesheetApplicationWorkflow lionTimesheetApplicationWorkflow = new LionTimesheetApplicationWorkflow();
		lionTimesheetApplicationWorkflow.setEmployeeTimesheetApplicationDetail(lionEmployeeTimesheetApplicationDetail);
		lionTimesheetApplicationWorkflow.setTimesheetStatusMaster(timesheetStatusMaster);
		lionTimesheetApplicationWorkflow.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		lionTimesheetApplicationWorkflow.setCreatedBy(employeeDAO.findById(employeeId));

		lionTimesheetApplicationWorkflowDAO.save(lionTimesheetApplicationWorkflow);

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = new ArrayList<LionEmployeeTimesheetApplicationDetail>(
				lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication()
						.getLionEmployeeTimesheetApplicationDetails());

		int timesheetStatusCheck = 0;

		List<LionEmployeeTimesheetApplicationDetail> unApprovedTimesheet = new ArrayList<LionEmployeeTimesheetApplicationDetail>();

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail2 : lionEmployeeTimesheetApplicationDetails) {
			if (!lionEmployeeTimesheetApplicationDetail2.getTimesheetStatusMaster().getTimesheetStatusName()
					.equals("Approved")) {
				timesheetStatusCheck = 1;
				unApprovedTimesheet.add(lionEmployeeTimesheetApplicationDetail2);
			}
		}

		int timesheetStatusHolidayCheck = 0;

		EmployeeTimesheetApplication employeeTimesheetApplication = employeeTimesheetApplicationDAO
				.findById(lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetApplication().getTimesheetId());

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail2 : unApprovedTimesheet) {

			Date timesheetDate = new Date(lionEmployeeTimesheetApplicationDetail2.getTimesheetDate().getTime());

			List<String> holidayList = getHolidaysFor(employeeTimesheetApplication.getEmployee().getEmployeeId(),
					timesheetDate, timesheetDate);
			if (holidayList.size() == 0) {
				timesheetStatusHolidayCheck = 1;
			}
		}

		if (timesheetStatusCheck == 0 || timesheetStatusHolidayCheck == 0) {

			TimesheetStatusMaster timesheetStatusMaster2 = timesheetStatusMasterDAO.findByName("Completed");
			employeeTimesheetApplication.setTimesheetStatusMaster(timesheetStatusMaster2);
			employeeTimesheetApplicationDAO.update(employeeTimesheetApplication);
		}

		isSuccessfullyFor = true;

		if (isSuccessfullyFor) {

			lionTimesheetMailLogic.submitTimesheetEmailByRevForEmp(
					employeeTimesheetApplication.getCompany().getCompanyId(), employeeTimesheetApplication,
					lionTimesheetApplicationReviewer, PayAsiaConstants.PAYASIA_SUB_CATEGORY_LUNDIN_TIMESHEET_APPROVE,
					employeeTimesheetApplication.getEmployee(), null);

		}

		Map<String, String> statusMap = new HashMap<String, String>();

		statusMap.put("inTimeStatus", String.valueOf(inTimeStatus));
		statusMap.put("outTimeStatus", String.valueOf(outTimeStatus));
		statusMap.put("breakTimeStatus", String.valueOf(breakTimeStatus));

		return statusMap;
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

	String getReviewerName(EmployeeTimesheetApplication employeeTimesheetApplication) {

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(employeeTimesheetApplication.getTimesheetId());

		Employee reviewer = null;

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {
			if ((lionEmployeeTimesheetApplicationDetail.getTimesheetStatusMaster().getTimesheetStatusName())
					.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)
					|| (lionEmployeeTimesheetApplicationDetail.getTimesheetStatusMaster().getTimesheetStatusName())
							.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)
					|| (lionEmployeeTimesheetApplicationDetail.getTimesheetStatusMaster().getTimesheetStatusName())
							.equalsIgnoreCase(PayAsiaConstants.LION_TIMESHEET_STATUS_APPROVED)) {
				reviewer = lionTimesheetReviewerDAO
						.findByEmployeeTimesheetDetailId(
								lionEmployeeTimesheetApplicationDetail.getEmployeeTimesheetDetailID())
						.getEmployeeReviewer();
			}
		}

		if (reviewer == null) {
			EmployeeTimesheetReviewer employeeTimesheetReviewer = employeeTimesheetReviewerDAO
					.findByEmployeeId(employeeTimesheetApplication.getEmployee().getEmployeeId());
			reviewer = employeeTimesheetReviewer.getEmployeeReviewer();
		}

		if (reviewer == null) {
			return "";
		} else {
			return getEmployeeNameWithNumber(reviewer);
		}

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

	private Employee getDelegatedEmployee(Long employeeId) {
		Employee emp = employeeDAO.findById(employeeId);
		WorkflowDelegate workflowDelegate = null;

		AppCodeMaster appCodeMasterOT = appCodeMasterDAO.findByCategoryAndDesc(PayAsiaConstants.WORKFLOW_CATEGORY,
				PayAsiaConstants.WORKFLOW_TYPE_LION_TIMESHEET_DESC);

		WorkflowDelegate workflowDelegateLeave = workflowDelegateDAO.findEmployeeByCurrentDate(employeeId,
				appCodeMasterOT.getAppCodeID());

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

}
