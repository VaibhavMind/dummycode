package com.payasia.logic.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.LionEmployeeTimesheetApplicationDetailDAO;
import com.payasia.dao.LionEmployeeTimesheetDAO;
import com.payasia.dao.LionTimesheetApplicationReviewerDAO;
import com.payasia.dao.LionTimesheetPreferenceDAO;
import com.payasia.dao.LundinTimesheetDetailDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.LionEmployeeTimesheet;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;
import com.payasia.dao.bean.LionTimesheetPreference;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.LionTimesheetPrintPDFLogic;
import com.payasia.logic.LionTimesheetReportsLogic;
import com.payasia.logic.LundinTimesheetRequestLogic;

@Component
public class LionTimesheetPrintPDFLogicImpl extends BaseLogic implements
		LionTimesheetPrintPDFLogic {

	@Resource
	LundinTimesheetDetailDAO lundinTimesheetDetailDAO;
	@Resource
	EmployeeTimesheetApplicationDAO employeeTimesheetApplicationDAO;
	@Resource
	LundinTimesheetRequestLogic lundinRequestLogic;

	@Resource
	TimesheetBatchDAO timesheetBatchDAO;

	@Resource
	LionTimesheetPreferenceDAO lionTimesheetPreferenceDAO;

	@Resource
	LionEmployeeTimesheetApplicationDetailDAO lionEmployeeTimesheetApplicationDetailDAO;

	@Resource
	LionEmployeeTimesheetDAO lionEmployeeTimesheetDAO;

	@Resource
	LionTimesheetApplicationReviewerDAO lionTimesheetApplicationReviewerDAO;

	@Resource
	EmployeeTimesheetReviewerDAO employeeTimesheetReviewerDAO;

	@Resource
	LionTimesheetReportsLogic lionTimesheetReportsLogic;

	@Resource
	CompanyDAO companyDAO;

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetPrintPDFLogicImpl.class);

	@Override
	public PdfPTable createTimesheetPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long timesheetId,
			boolean hasLundinTimesheetModule) {
		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		mainSectionTable.setWidthPercentage(95f);
		mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(5f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);
		EmployeeTimesheetApplication lundinTimesheetVO = employeeTimesheetApplicationDAO
				.findById(timesheetId);

		List<LocalDate> holidaysDateList = getHolidayDateList(lundinTimesheetVO);
		List<LocalDate> blockHolidaysList = getBlockHolidayDateList(lundinTimesheetVO);

		PdfPTable empInfoTable = createEmpInfoDetailTable(lundinTimesheetVO);
		empInfoTable.setWidthPercentage(110f);

		PdfPTable timesheetSectionTable = getTimesheetSectionTable(document,
				lundinTimesheetVO, companyId, holidaysDateList,
				blockHolidaysList);

		timesheetSectionTable.setWidthPercentage(110f);
		timesheetSectionTable.setSpacingBefore(10f);

		PdfPTable workflowSectionTable = createWorkflowHistoryTable(lundinTimesheetVO);

		mainCell.addElement(empInfoTable);
		mainCell.addElement(timesheetSectionTable);
		mainCell.addElement(workflowSectionTable);

		mainSectionTable.addCell(mainCell);
		return mainSectionTable;
	}

	private List<LocalDate> getBlockHolidayDateList(
			EmployeeTimesheetApplication lundinTimesheetVO) {
		List<LocalDate> blockHolidaysList = new ArrayList<LocalDate>();
		List<String> blockHolidays = lundinRequestLogic
				.getEmpResignedAndNewHiresDates(lundinTimesheetVO.getEmployee()
						.getEmployeeId(), lundinTimesheetVO.getTimesheetBatch()
						.getStartDate(), lundinTimesheetVO.getTimesheetBatch()
						.getEndDate());
		for (String dateStr : blockHolidays) {
			Date date = DateUtils.stringToDate(dateStr, "MM/dd/yyyy");
			DateTime dateTime = new DateTime(date);
			blockHolidaysList.add(dateTime.toLocalDate());
		}
		return blockHolidaysList;
	}

	private List<LocalDate> getHolidayDateList(
			EmployeeTimesheetApplication lundinTimesheetVO) {
		List<LocalDate> holidaysDateList = new ArrayList<LocalDate>();
		List<String> holidays = lundinRequestLogic.getHolidaysFor(
				lundinTimesheetVO.getEmployee().getEmployeeId(),
				lundinTimesheetVO.getTimesheetBatch().getStartDate(),
				lundinTimesheetVO.getTimesheetBatch().getEndDate());
		for (String dateStr : holidays) {
			Date date = DateUtils.stringToDate(dateStr, "MM/dd/yyyy");
			DateTime dateTime = new DateTime(date);
			holidaysDateList.add(dateTime.toLocalDate());
		}
		return holidaysDateList;
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

	private PdfPTable createEmpInfoDetailTable(
			EmployeeTimesheetApplication lionTimesheetVO) {

		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(lionTimesheetVO.getTimesheetBatch()
						.getTimesheetBatchId());
		LionTimesheetPreference lionTimesheetPreference = lionTimesheetPreferenceDAO
				.findByCompanyId(lionTimesheetVO.getCompany().getCompanyId());

		Timestamp timesheetBatchStartDate = timesheetBatch.getStartDate();
		Timestamp timesheetBatchEndDate = timesheetBatch.getEndDate();

		String companyDateFormat = companyDAO.findById(
				lionTimesheetVO.getCompany().getCompanyId()).getDateFormat();

		SimpleDateFormat dateFormat = new SimpleDateFormat(companyDateFormat);

		PdfPTable empInfoTable = new PdfPTable(new float[] { 1 });
		empInfoTable.setWidthPercentage(110f);

		PdfPCell revDetailCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		revDetailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		revDetailCell.setBorder(0);
		revDetailCell.setPadding(0);

		PdfPTable revDetailTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f,
				1f, 1f, 1f, 1f });
		revDetailTable.setWidthPercentage(110f);
		revDetailTable.setSpacingBefore(40f);

		PdfPCell empNumCell = new PdfPCell(new Phrase("Employee Id:",
				getBaseFont10Bold()));
		empNumCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNumCell.setBorder(0);
		empNumCell.setPadding(4);

		PdfPCell empNumValCell = new PdfPCell(new Phrase(lionTimesheetVO
				.getEmployee().getEmployeeNumber(), getBaseFontNormal()));
		empNumValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNumValCell.setBorder(0);
		empNumValCell.setPadding(4);

		PdfPCell startDateCell = new PdfPCell(new Phrase("Start Date:",
				getBaseFont10Bold()));
		startDateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		startDateCell.setBorder(0);
		startDateCell.setPadding(4);

		PdfPCell startDateValCell = new PdfPCell(
				new Phrase(dateFormat.format(timesheetBatchStartDate),
						getBaseFontNormal()));
		startDateValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		startDateValCell.setBorder(0);
		startDateValCell.setPadding(4);

		PdfPCell endDateCell = new PdfPCell(new Phrase("End Date:",
				getBaseFont10Bold()));
		endDateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		endDateCell.setBorder(0);
		endDateCell.setPadding(4);

		PdfPCell endDateValCell = new PdfPCell(new Phrase(
				dateFormat.format(timesheetBatchEndDate), getBaseFontNormal()));
		endDateValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		endDateValCell.setBorder(0);
		endDateValCell.setPadding(4);

		PdfPCell empNameCell = new PdfPCell(new Phrase("Name:",
				getBaseFont10Bold()));
		empNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNameCell.setBorder(0);
		empNameCell.setPadding(4);

		PdfPCell empNameValCell = new PdfPCell(new Phrase(
				getEmployeeName(lionTimesheetVO.getEmployee()),
				getBaseFontNormal()));
		empNameValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNameValCell.setBorder(0);
		empNameValCell.setPadding(4);

		PdfPCell locationCell = new PdfPCell(new Phrase("Location:",
				getBaseFont10Bold()));
		locationCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		locationCell.setBorder(0);
		locationCell.setPadding(4);

		PdfPCell locationValCell = new PdfPCell(new Phrase(getLocation(
				lionTimesheetVO.getEmployee().getEmployeeId(), lionTimesheetVO
						.getCompany().getCompanyId(), lionTimesheetVO),
				getBaseFontNormal()));
		locationValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		locationValCell.setBorder(0);
		locationValCell.setPadding(4);

		PdfPCell blankCell = new PdfPCell(new Phrase("", getBaseFontNormal()));
		blankCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankCell.setBorder(0);
		blankCell.setPadding(4);

		revDetailTable.addCell(empNumCell);
		revDetailTable.addCell(empNumValCell);
		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(startDateCell);
		revDetailTable.addCell(startDateValCell);
		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(endDateCell);
		revDetailTable.addCell(endDateValCell);
		revDetailTable.addCell(empNameCell);
		revDetailTable.addCell(empNameValCell);
		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(locationCell);
		revDetailTable.addCell(locationValCell);
		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(blankCell);

		revDetailCell.addElement(revDetailTable);
		empInfoTable.addCell(revDetailCell);

		return empInfoTable;
	}

	private PdfPTable getTimesheetSectionTable(Document document,
			EmployeeTimesheetApplication lundinTimesheetVO, Long companyId,
			List<LocalDate> holidaysList, List<LocalDate> blockHolidaysList) {

		PdfPTable timesheetSectionTable = new PdfPTable(new float[] { 1f });
		timesheetSectionTable.setWidthPercentage(110f);

		BaseColor myColor = WebColors.getRGBColor("#538DD5");
		BaseColor whiteColor = WebColors.getRGBColor("#ffffff");

		PdfPCell revDetailCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		revDetailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		revDetailCell.setBorder(Rectangle.NO_BORDER);
		revDetailCell.setPadding(0);

		PdfPTable revDetailTable = new PdfPTable(new float[] { 2f, 2f, 2f, 2f,
				2f, 2f, 3f, 2f });
		revDetailTable.setWidthPercentage(110f);
		revDetailTable.setSpacingBefore(10f);

		PdfPCell timesheetDateCell = new PdfPCell(new Phrase("Date",
				getBaseFont10WhiteBold()));
		timesheetDateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		timesheetDateCell.setBorderWidth(0.5f);
		timesheetDateCell.setPadding(2);
		timesheetDateCell.setBackgroundColor(myColor);

		PdfPCell timesheetDayCell = new PdfPCell(new Phrase("Day",
				getBaseFont10WhiteBold()));
		timesheetDayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		timesheetDayCell.setBorderWidth(0.5f);
		timesheetDayCell.setPadding(2);
		timesheetDayCell.setBackgroundColor(myColor);

		PdfPCell inTimeCell = new PdfPCell(new Phrase("In Time",
				getBaseFont10WhiteBold()));
		inTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		inTimeCell.setBorderWidth(0.5f);
		inTimeCell.setPadding(2);
		inTimeCell.setBackgroundColor(myColor);

		PdfPCell outTimeCell = new PdfPCell(new Phrase("Out Time",
				getBaseFont10WhiteBold()));
		outTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		outTimeCell.setBorderWidth(0.5f);
		outTimeCell.setPadding(2);
		outTimeCell.setBackgroundColor(myColor);

		PdfPCell breakTimeCell = new PdfPCell(new Phrase("Break/ Time Hours",
				getBaseFont10WhiteBold()));
		breakTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		breakTimeCell.setBorderWidth(0.5f);
		breakTimeCell.setPadding(2);
		breakTimeCell.setBackgroundColor(myColor);

		PdfPCell totalHoursCell = new PdfPCell(new Phrase("Total Hours Worked",
				getBaseFont10WhiteBold()));
		totalHoursCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		totalHoursCell.setBorderWidth(0.5f);
		totalHoursCell.setPadding(2);
		totalHoursCell.setBackgroundColor(myColor);

		PdfPCell remarkCell = new PdfPCell(new Phrase("Remarks",
				getBaseFont10WhiteBold()));
		remarkCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		remarkCell.setBorderWidth(0.5f);
		remarkCell.setPadding(2);
		remarkCell.setBackgroundColor(myColor);

		PdfPCell actionCell = new PdfPCell(new Phrase("Action",
				getBaseFont10WhiteBold()));
		actionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		actionCell.setBorderWidth(0.5f);
		actionCell.setPadding(2);
		actionCell.setBackgroundColor(myColor);

		revDetailTable.addCell(timesheetDateCell);
		revDetailTable.addCell(timesheetDayCell);
		revDetailTable.addCell(inTimeCell);
		revDetailTable.addCell(outTimeCell);
		revDetailTable.addCell(breakTimeCell);
		revDetailTable.addCell(totalHoursCell);
		revDetailTable.addCell(remarkCell);
		revDetailTable.addCell(actionCell);

		List<LionEmployeeTimesheetApplicationDetail> lionEmployeeTimesheetApplicationDetails = lionEmployeeTimesheetApplicationDetailDAO
				.getLionEmployeeTimesheetApplicationDetails(lundinTimesheetVO
						.getTimesheetId());

		String companyDateFormat = companyDAO.findById(companyId)
				.getDateFormat();

		SimpleDateFormat dateFormat = new SimpleDateFormat(companyDateFormat);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

		for (LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail : lionEmployeeTimesheetApplicationDetails) {

			Timestamp timesheetDate = lionEmployeeTimesheetApplicationDetail
					.getTimesheetDate();

			PdfPCell timesheetDateValCell = new PdfPCell(new Phrase(
					dateFormat.format(timesheetDate), getBaseFontNormal()));
			timesheetDateValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			timesheetDateValCell.setBorderWidth(0.5f);

			java.util.GregorianCalendar cal = (GregorianCalendar) Calendar
					.getInstance();
			cal.setTime(timesheetDate);

			PdfPCell timesheetDayValCell = new PdfPCell(
					new Phrase(getFullWeekDayNames(cal
							.get(java.util.Calendar.DAY_OF_WEEK) % 7),
							getBaseFontNormal()));
			timesheetDayValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			timesheetDayValCell.setBorderWidth(0.5f);

			PdfPCell inTimeValCell = new PdfPCell(new Phrase(
					timeFormat.format(lionEmployeeTimesheetApplicationDetail
							.getInTime()), getBaseFontNormal()));
			inTimeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			inTimeValCell.setBorderWidth(0.5f);

			PdfPCell outTimeValCell = new PdfPCell(new Phrase(
					timeFormat.format(lionEmployeeTimesheetApplicationDetail
							.getOutTime()), getBaseFontNormal()));
			outTimeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			outTimeValCell.setBorderWidth(0.5f);

			PdfPCell breakTimeValCell = new PdfPCell(new Phrase(
					timeFormat.format(lionEmployeeTimesheetApplicationDetail
							.getBreakTimeHours()), getBaseFontNormal()));
			breakTimeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			breakTimeValCell.setBorderWidth(0.5f);

			PdfPCell remarksCell = new PdfPCell(new Phrase(
					lionEmployeeTimesheetApplicationDetail.getRemarks(),
					getBaseFontNormal()));
			remarksCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			remarksCell.setBorderWidth(0.5f);

			PdfPCell totalHoursValCell = new PdfPCell(new Phrase(
					lionEmployeeTimesheetApplicationDetail
							.getTotalHoursWorked().toString(),
					getBaseFontNormal()));
			totalHoursValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			totalHoursValCell.setBorderWidth(0.5f);

			PdfPCell actionValCell = new PdfPCell(new Phrase(
					lionEmployeeTimesheetApplicationDetail
							.getTimesheetStatusMaster()
							.getTimesheetStatusName(), getBaseFontNormal()));
			actionValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			actionValCell.setBorderWidth(0.5f);

			revDetailTable.addCell(timesheetDateValCell);
			revDetailTable.addCell(timesheetDayValCell);
			revDetailTable.addCell(inTimeValCell);
			revDetailTable.addCell(outTimeValCell);
			revDetailTable.addCell(breakTimeValCell);
			revDetailTable.addCell(totalHoursValCell);
			revDetailTable.addCell(remarksCell);
			revDetailTable.addCell(actionValCell);

		}

		LionEmployeeTimesheet lionEmployeeTimesheet = lionEmployeeTimesheetDAO
				.findByEmployeeTimesheetApplication(
						lundinTimesheetVO.getTimesheetId()).get(0);

		PdfPCell blankBorderCell = new PdfPCell(new Phrase("",
				getBaseFontNormal()));
		blankBorderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankBorderCell.setBorder(Rectangle.NO_BORDER);

		PdfPCell borderLeftDownCell = new PdfPCell(new Phrase("",
				getBaseFontNormal()));
		borderLeftDownCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		borderLeftDownCell.setBorderWidth(0.5f);
		borderLeftDownCell.setBorder(6);

		PdfPCell borderDownCell = new PdfPCell(new Phrase("",
				getBaseFontNormal()));
		borderDownCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		borderDownCell.setBorder(Rectangle.BOTTOM);

		PdfPCell grandTotalHoursCell = new PdfPCell(new Phrase("TOTALS",
				getBaseFont7Bold()));
		grandTotalHoursCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		grandTotalHoursCell.setBorder(11);

		PdfPCell grandTotalHoursValCell = new PdfPCell(new Phrase(
				lionEmployeeTimesheet.getTimesheetTotalHours().toString(),
				getBaseFontNormal()));
		grandTotalHoursValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		grandTotalHoursValCell.setBorderWidth(0.5f);

		PdfPCell excessHoursWorkedCell = new PdfPCell(new Phrase(
				"EXCESS HOURS WORKED", getBaseFont7Bold()));
		excessHoursWorkedCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		excessHoursWorkedCell.setBorder(11);

		PdfPCell excessHoursWorkedValCell = new PdfPCell(new Phrase(
				lionEmployeeTimesheet.getExcessHoursWorked().toString(),
				getBaseFontNormal()));
		excessHoursWorkedValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		excessHoursWorkedValCell.setBorderWidth(0.5f);

		revDetailTable.addCell(borderLeftDownCell);
		revDetailTable.addCell(borderDownCell);
		revDetailTable.addCell(borderDownCell);
		revDetailTable.addCell(borderDownCell);
		revDetailTable.addCell(grandTotalHoursCell);
		revDetailTable.addCell(grandTotalHoursValCell);
		revDetailTable.addCell(borderDownCell);
		revDetailTable.addCell(borderDownCell);

		revDetailTable.addCell(borderLeftDownCell);
		revDetailTable.addCell(borderDownCell);
		revDetailTable.addCell(borderDownCell);
		revDetailTable.addCell(borderDownCell);
		revDetailTable.addCell(excessHoursWorkedCell);
		revDetailTable.addCell(excessHoursWorkedValCell);
		revDetailTable.addCell(blankBorderCell);

		revDetailCell.addElement(revDetailTable);
		timesheetSectionTable.addCell(revDetailCell);

		return timesheetSectionTable;
	}

	private PdfPTable createWorkflowHistoryTable(
			EmployeeTimesheetApplication lundinTimesheetVO) {
		PdfPTable workflowySectionTable = new PdfPTable(new float[] { 1f });
		workflowySectionTable.setWidthPercentage(110f);
		workflowySectionTable.getTotalWidth();

		BaseColor myColor = WebColors.getRGBColor("#6ba5d1");

		PdfPCell revDetailCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		revDetailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		revDetailCell.setBorder(0);
		revDetailCell.setPadding(0);

		PdfPTable revDetailTable = new PdfPTable(new float[] { 4f, 4f });
		revDetailTable.setWidthPercentage(80f);
		revDetailTable.setSpacingBefore(40f);

		PdfPCell workflowRoleCell = new PdfPCell(new Phrase("Workflow Role",
				getBaseFont12WhiteBold()));
		workflowRoleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		workflowRoleCell.setBorderWidth(0.5f);
		workflowRoleCell.setPadding(4);
		workflowRoleCell.setBackgroundColor(myColor);

		PdfPCell reviewerNameCell = new PdfPCell(new Phrase("Reviewer Name",
				getBaseFont12WhiteBold()));
		reviewerNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		reviewerNameCell.setBorderWidth(0.5f);
		reviewerNameCell.setPadding(4);
		reviewerNameCell.setBackgroundColor(myColor);

		PdfPCell workflowRoleValCell = new PdfPCell(new Phrase("Reviewer 1",
				getBaseFontNormal()));
		workflowRoleValCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		workflowRoleValCell.setBorderWidth(0.5f);
		workflowRoleValCell.setPadding(2);

		PdfPCell reviewerNameValCell = new PdfPCell(new Phrase(
				getReviewerName(lundinTimesheetVO), getBaseFontNormal()));
		reviewerNameValCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		reviewerNameValCell.setBorderWidth(0.5f);
		reviewerNameValCell.setPadding(2);

		revDetailTable.addCell(workflowRoleCell);
		revDetailTable.addCell(reviewerNameCell);
		revDetailTable.addCell(workflowRoleValCell);
		revDetailTable.addCell(reviewerNameValCell);

		revDetailCell.addElement(revDetailTable);
		workflowySectionTable.addCell(revDetailCell);

		return workflowySectionTable;
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
				reviewer = lionTimesheetApplicationReviewerDAO
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

	/**
	 * Comparator Class for Ordering timesheet Rev List
	 */
	private class timesheetRevComp implements
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

	private Font getBaseFontNormal() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 8, Font.NORMAL, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont7() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 7, Font.NORMAL, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont7Red() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 7, Font.NORMAL, BaseColor.RED);
		return unicodeFont;

	}

	private Font getBaseFont7Bold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 7, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont7BoldRed() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 7, Font.BOLD, BaseColor.RED);
		return unicodeFont;

	}

	private Font getBaseFont7WhiteBold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 7, Font.BOLD, BaseColor.WHITE);
		return unicodeFont;

	}

	private Font getBaseFont10WhiteBold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 10, Font.BOLD, BaseColor.WHITE);
		return unicodeFont;

	}

	private Font getBaseFont10Bold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 8, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont12Bold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 11, Font.BOLD, BaseColor.BLACK);
		return unicodeFont;

	}

	private Font getBaseFont12WhiteBold() {
		BaseFont bf = null;
		try {
			try {
				bf = BaseFont.createFont(
						PayAsiaPDFConstants.PDF_MULTILINGUAL_FONT_PATH,
						BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} catch (DocumentException e1) {
			LOGGER.error(e1.getMessage(), e1);
		}
		Font unicodeFont = new Font(bf, 10, Font.BOLD, BaseColor.WHITE);
		return unicodeFont;

	}

	private static String getFullWeekDayNames(int day) {
		String[] weekDayNames = { "Saturday", "Sunday", "Monday", "Tuesday",
				"Wednesday", "Thursday", "Friday" };
		return weekDayNames[day];
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

	public String getEmployeeNameWithNumber(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + "(" + employee.getEmployeeNumber() + ")";
		employeeName = employeeName.toUpperCase();
		return employeeName;
	}

	private PdfPCell tempCell() {
		PdfPCell tempCell = new PdfPCell(new Phrase("", getBaseFontNormal()));
		tempCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		tempCell.setBorder(0);
		return tempCell;
	}

}
