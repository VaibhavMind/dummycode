package com.payasia.logic.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import com.payasia.common.form.CoherentShiftDetailForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.OTTimesheetWorkflowForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.CoherentShiftApplicationDAO;
import com.payasia.dao.CoherentShiftApplicationDetailDAO;
import com.payasia.dao.CoherentShiftApplicationReviewerDAO;
import com.payasia.dao.CoherentShiftApplicationWorkflowDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationDetail;
import com.payasia.dao.bean.CoherentShiftApplicationReviewer;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.CoherentShiftPrintPDFLogic;
import com.payasia.logic.CoherentTimesheetReportsLogic;
import com.payasia.logic.LundinTimesheetRequestLogic;

@Component
public class CoherentShiftPrintPDFLogicImpl extends BaseLogic implements
		CoherentShiftPrintPDFLogic {

	@Resource
	LundinTimesheetRequestLogic lundinRequestLogic;

	@Resource
	CoherentShiftApplicationDAO coherentShiftApplicationDAO;
	@Resource
	CoherentShiftApplicationDetailDAO coherentShiftApplicationDetailDAO;

	@Resource
	CoherentShiftApplicationReviewerDAO coherentShiftApplicationReviewerDAO;

	@Resource
	CoherentShiftApplicationWorkflowDAO coherentShiftApplicationWorkflowDAO;
	@Resource
	TimesheetBatchDAO timesheetBatchDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	CoherentTimesheetReportsLogic coherentTimesheetReportsLogic;

	private static final Logger LOGGER = Logger
			.getLogger(CoherentShiftPrintPDFLogicImpl.class);

	@Override
	public PdfPTable createShiftPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long timesheetId,
			boolean hasCoherentTimesheetModule) {
		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		mainSectionTable.setWidthPercentage(95f);
		mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(5f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);

		CoherentShiftApplication coherentOvertimeApplication = coherentShiftApplicationDAO
				.findById(timesheetId);

		List<LocalDate> holidaysDateList = getHolidayDateList(coherentOvertimeApplication);
		List<LocalDate> blockHolidaysList = getBlockHolidayDateList(coherentOvertimeApplication);

		PdfPTable empInfoTable = createEmpInfoDetailTable(coherentOvertimeApplication);
		empInfoTable.setWidthPercentage(110f);

		PdfPTable timesheetSectionTable = getTimesheetSectionTable(document,
				coherentOvertimeApplication, companyId, holidaysDateList,
				blockHolidaysList);

		timesheetSectionTable.setWidthPercentage(110f);
		timesheetSectionTable.setSpacingBefore(10f);

		mainCell.addElement(empInfoTable);
		mainCell.addElement(timesheetSectionTable);
		if (!coherentOvertimeApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_DRAFT)) {
			PdfPTable workflowSectionTable = createWorkflowHistoryTable(coherentOvertimeApplication);
			mainCell.addElement(workflowSectionTable);
		}
		mainSectionTable.addCell(mainCell);
		return mainSectionTable;
	}

	public OTPendingTimesheetForm getTimesheetWorkflowHistory(Long otTimesheetId) {
		OTPendingTimesheetForm otMyRequestForm = new OTPendingTimesheetForm();

		CoherentShiftApplication coherentOvertimeApplication = coherentShiftApplicationDAO
				.findById(otTimesheetId);

		otMyRequestForm
				.setCreatedBy(getEmployeeNameWithNumber(coherentOvertimeApplication
						.getEmployee()));

		otMyRequestForm
				.setCreatedDate(DateUtils
						.timeStampToStringWithTime(new ArrayList<CoherentShiftApplicationReviewer>(
								coherentOvertimeApplication
										.getCoherentShiftApplicationReviewer())
								.get(0).getCreatedDate()));
		List<CoherentShiftApplicationWorkflow> applicationWorkflows = new ArrayList<>(
				coherentOvertimeApplication
						.getCoherentShiftApplicationWorkflows());
		Collections.sort(applicationWorkflows, new OTTimesheetWorkFlowComp());
		Integer workFlowCount = 0;

		if (coherentOvertimeApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_WITHDRAWN)) {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_WITHDRAWN);
		} else {
			otMyRequestForm.setUserStatus(PayAsiaConstants.OT_STATUS_SUBMITTED);

		}

		for (CoherentShiftApplicationReviewer coherentApplicationReviewer : coherentOvertimeApplication
				.getCoherentShiftApplicationReviewer()) {

			if (coherentApplicationReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("1")) {
				otMyRequestForm
						.setOtTimesheetReviewer1(getEmployeeNameWithNumber(coherentApplicationReviewer
								.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer1Id(coherentApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
			}

			if (coherentApplicationReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("2")) {
				otMyRequestForm
						.setOtTimesheetReviewer2(getEmployeeNameWithNumber(coherentApplicationReviewer
								.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer2Id(coherentApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
			}
			if (coherentApplicationReviewer.getWorkFlowRuleMaster()
					.getRuleValue().equals("3")) {
				otMyRequestForm
						.setOtTimesheetReviewer3(getEmployeeNameWithNumber(coherentApplicationReviewer
								.getEmployeeReviewer()));
				otMyRequestForm
						.setOtTimesheetReviewer3Id(coherentApplicationReviewer
								.getEmployeeReviewer().getEmployeeId());
			}
		}

		otMyRequestForm.setTotalNoOfReviewers(coherentOvertimeApplication
				.getCoherentShiftApplicationReviewer().size());
		List<OTTimesheetWorkflowForm> otTimesheetWorkflowForms = new ArrayList<>();
		for (CoherentShiftApplicationWorkflow coherentApplicationWorkflow : applicationWorkflows) {

			OTTimesheetWorkflowForm otTimesheetWorkflowForm = new OTTimesheetWorkflowForm();
			otTimesheetWorkflowForm.setRemarks(coherentApplicationWorkflow
					.getRemarks());
			otTimesheetWorkflowForm.setStatus(coherentApplicationWorkflow
					.getTimesheetStatusMaster().getTimesheetStatusName());
			otTimesheetWorkflowForm.setCreatedDate(DateUtils
					.timeStampToStringWithTime(coherentApplicationWorkflow
							.getCreatedDate()));
			otTimesheetWorkflowForm.setOrder(workFlowCount);

			otTimesheetWorkflowForms.add(otTimesheetWorkflowForm);
			workFlowCount++;
		}
		otMyRequestForm.setOtTimesheetWorkflowForms(otTimesheetWorkflowForms);
		return otMyRequestForm;
	}

	private class OTTimesheetWorkFlowComp implements
			Comparator<CoherentShiftApplicationWorkflow> {

		@Override
		public int compare(CoherentShiftApplicationWorkflow templateField,
				CoherentShiftApplicationWorkflow compWithTemplateField) {
			if (templateField.getShiftApplicationWorkflowID() > compWithTemplateField
					.getShiftApplicationWorkflowID()) {
				return 1;
			} else if (templateField.getShiftApplicationWorkflowID() < compWithTemplateField
					.getShiftApplicationWorkflowID()) {
				return -1;
			}
			return 0;

		}

	}

	private class timesheetRevComp implements
			Comparator<CoherentShiftApplicationReviewer> {
		public int compare(CoherentShiftApplicationReviewer templateField,
				CoherentShiftApplicationReviewer compWithTemplateField) {
			if (templateField.getShiftApplicationReviewerID() > compWithTemplateField
					.getShiftApplicationReviewerID()) {
				return 1;
			} else if (templateField.getShiftApplicationReviewerID() < compWithTemplateField
					.getShiftApplicationReviewerID()) {
				return -1;
			}
			return 0;

		}

	}

	private PdfPCell createWorkFlowSectionTableData(String leftColKey,
			String leftColVal, String middleColKey, String middleColVal,
			String rightColKey, String rightColVal, Font font) {
		PdfPCell outerCell = new PdfPCell();
		outerCell.setBorder(0);

		PdfPTable outerTable = new PdfPTable(new float[] { 0.3f, 0.8f, 1.5f,
				2f, 0.7f, 0.7f });
		outerTable.setWidthPercentage(100f);

		PdfPCell nestedLeftKeyCell = new PdfPCell(new Phrase(leftColKey, font));
		nestedLeftKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftKeyCell.setBorder(0);

		PdfPCell nestedLeftValueCell = new PdfPCell(
				new Phrase(leftColVal, font));
		nestedLeftValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedLeftValueCell.setBorder(0);
		PdfPCell nestedMiddleKeyCell = new PdfPCell(new Phrase(middleColKey,
				font));
		nestedMiddleKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleKeyCell.setBorder(0);
		PdfPCell nestedMiddleValueCell = new PdfPCell(new Phrase(middleColVal,
				font));
		nestedMiddleValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedMiddleValueCell.setBorder(0);
		PdfPCell nestedRightKeyCell = new PdfPCell(
				new Phrase(rightColKey, font));
		nestedRightKeyCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightKeyCell.setBorder(0);

		PdfPCell nestedRightValueCell = new PdfPCell(new Phrase(rightColVal,
				font));
		nestedRightValueCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		nestedRightValueCell.setBorder(0);

		outerTable.addCell(nestedLeftKeyCell);
		outerTable.addCell(nestedLeftValueCell);
		outerTable.addCell(nestedMiddleKeyCell);
		outerTable.addCell(nestedMiddleValueCell);
		outerTable.addCell(nestedRightKeyCell);
		outerTable.addCell(nestedRightValueCell);

		outerCell.addElement(outerTable);
		return outerCell;
	}

	private PdfPTable getTimesheetSectionTable(Document document,
			CoherentShiftApplication coherentTimesheetVO, Long companyId,
			List<LocalDate> holidaysList, List<LocalDate> blockHolidaysList) {

		PdfPTable timesheetSectionTable = new PdfPTable(new float[] { 1f });
		timesheetSectionTable.setWidthPercentage(110f);

		BaseColor myColor = WebColors.getRGBColor("#538DD5");
		PdfPCell revDetailCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		revDetailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		revDetailCell.setBorder(Rectangle.NO_BORDER);
		revDetailCell.setPadding(0);

		PdfPTable revDetailTable = new PdfPTable(new float[] { 2f, 2f, 2f, 2f,
				4f });
		revDetailTable.setWidthPercentage(110f);
		revDetailTable.setSpacingBefore(10f);

		PdfPCell timesheetDateCell = new PdfPCell(new Phrase("Date",
				getBaseFont10White()));
		timesheetDateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		timesheetDateCell.setBorderWidth(0.5f);
		timesheetDateCell.setPadding(2);
		timesheetDateCell.setBackgroundColor(myColor);

		PdfPCell timesheetDayCell = new PdfPCell(new Phrase("Day",
				getBaseFont10White()));
		timesheetDayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		timesheetDayCell.setBorderWidth(0.5f);
		timesheetDayCell.setPadding(2);
		timesheetDayCell.setBackgroundColor(myColor);

		/*
		 * PdfPCell inTimeCell = new PdfPCell(new Phrase("Start Time(A)",
		 * getBaseFont10White()));
		 * inTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * inTimeCell.setBorderWidth(0.5f); inTimeCell.setPadding(2);
		 * inTimeCell.setBackgroundColor(myColor);
		 */

		/*
		 * PdfPCell outTimeCell = new PdfPCell(new Phrase("End Time(B)",
		 * getBaseFont10White()));
		 * outTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * outTimeCell.setBorderWidth(0.5f); outTimeCell.setPadding(2);
		 * outTimeCell.setBackgroundColor(myColor);
		 */

		/*
		 * PdfPCell breakTimeCell = new PdfPCell(new Phrase("Meal Duration C",
		 * getBaseFont10White()));
		 * breakTimeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * breakTimeCell.setBorderWidth(0.5f); breakTimeCell.setPadding(2);
		 * breakTimeCell.setBackgroundColor(myColor);
		 */

		PdfPCell shiftTypeCell = new PdfPCell(new Phrase("Shift Type",
				getBaseFont10White()));
		shiftTypeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		shiftTypeCell.setBorderWidth(0.5f);
		shiftTypeCell.setPadding(2);
		shiftTypeCell.setBackgroundColor(myColor);

		PdfPCell dayTypeCell = new PdfPCell(new Phrase("Shifts",
				getBaseFont10White()));
		dayTypeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		dayTypeCell.setBorderWidth(0.5f);
		dayTypeCell.setPadding(2);
		dayTypeCell.setBackgroundColor(myColor);

		/*
		 * PdfPCell OTHoursCell = new PdfPCell(new Phrase(
		 * "No. of OT Hours (B-A)-C", getBaseFont10White()));
		 * OTHoursCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * OTHoursCell.setBorderWidth(0.5f); OTHoursCell.setPadding(2);
		 * OTHoursCell.setBackgroundColor(myColor);
		 */

		/*
		 * PdfPCell OT_1_5_HoursCell = new PdfPCell(new Phrase("OT 1.5 Hours",
		 * getBaseFont10White()));
		 * OT_1_5_HoursCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * OT_1_5_HoursCell.setBorderWidth(0.5f);
		 * OT_1_5_HoursCell.setPadding(2);
		 * OT_1_5_HoursCell.setBackgroundColor(myColor);
		 */

		/*
		 * PdfPCell OT_1_0_DayCell = new PdfPCell(new Phrase("OT 1.0 Day",
		 * getBaseFont10White()));
		 * OT_1_0_DayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * OT_1_0_DayCell.setBorderWidth(0.5f); OT_1_0_DayCell.setPadding(2);
		 * OT_1_0_DayCell.setBackgroundColor(myColor);
		 */

		/*
		 * PdfPCell OT_2_0_DayCell = new PdfPCell(new Phrase("OT 2.0 Day",
		 * getBaseFont10White()));
		 * OT_2_0_DayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 * OT_2_0_DayCell.setBorderWidth(0.5f); OT_2_0_DayCell.setPadding(2);
		 * OT_2_0_DayCell.setBackgroundColor(myColor);
		 */

		PdfPCell remarksCell = new PdfPCell(new Phrase("Remarks",
				getBaseFont10White()));
		remarksCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		remarksCell.setBorderWidth(0.5f);
		remarksCell.setPadding(2);
		remarksCell.setBackgroundColor(myColor);

		revDetailTable.addCell(timesheetDateCell);
		revDetailTable.addCell(timesheetDayCell);
		/*
		 * revDetailTable.addCell(inTimeCell);
		 * revDetailTable.addCell(outTimeCell);
		 * revDetailTable.addCell(breakTimeCell);
		 */

		revDetailTable.addCell(shiftTypeCell);
		revDetailTable.addCell(dayTypeCell);
		/*
		 * revDetailTable.addCell(OTHoursCell);
		 * revDetailTable.addCell(OT_1_5_HoursCell);
		 * revDetailTable.addCell(OT_1_0_DayCell);
		 * revDetailTable.addCell(OT_2_0_DayCell);
		 */
		revDetailTable.addCell(remarksCell);

		List<CoherentShiftApplicationDetail> coherentOvertimeApplicationDetails = coherentShiftApplicationDetailDAO
				.getTimesheetDetailsByTimesheetId(coherentTimesheetVO
						.getShiftApplicationID());

		String companyDateFormat = companyDAO.findById(companyId)
				.getDateFormat();

		SimpleDateFormat dateFormat = new SimpleDateFormat(companyDateFormat);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

		for (CoherentShiftApplicationDetail coherentOvertimeApplicationDetail : coherentOvertimeApplicationDetails) {

			Timestamp timesheetDate = coherentOvertimeApplicationDetail
					.getShiftDate();

			DateTime timesheetDatedt = new DateTime(timesheetDate);

			Font holidayFont = getBaseFontNormal();

			if (holidaysList.contains(timesheetDatedt.toLocalDate())) {
				holidayFont = getBaseFont7Red();
			}

			PdfPCell timesheetDateValCell = new PdfPCell(new Phrase(
					dateFormat.format(timesheetDate), holidayFont));
			timesheetDateValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			timesheetDateValCell.setBorderWidth(0.5f);

			java.util.GregorianCalendar cal = (GregorianCalendar) Calendar
					.getInstance();
			cal.setTime(timesheetDate);

			PdfPCell timesheetDayValCell = new PdfPCell(
					new Phrase(getFullWeekDayNames(cal
							.get(java.util.Calendar.DAY_OF_WEEK) % 7),
							holidayFont));
			timesheetDayValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			timesheetDayValCell.setBorderWidth(0.5f);

			Font startDateFont = getBaseFontNormal();

			if (coherentOvertimeApplicationDetail.isShiftChanged()) {
				startDateFont = getBaseFont7Red();
			}

			/*
			 * PdfPCell startDateValCell = new PdfPCell(new Phrase(
			 * timeFormat.format(coherentOvertimeApplicationDetail
			 * .getStartTime()), startDateFont));
			 * startDateValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * startDateValCell.setBorderWidth(0.5f);
			 * 
			 * Font endDateFont = getBaseFontNormal();
			 * 
			 * if (coherentOvertimeApplicationDetail.isEndTimeChanged()) {
			 * endDateFont = getBaseFont7Red(); }
			 */

			/*
			 * PdfPCell endDateValCell = new PdfPCell(new Phrase(
			 * timeFormat.format(coherentOvertimeApplicationDetail
			 * .getEndTime()), endDateFont));
			 * endDateValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * endDateValCell.setBorderWidth(0.5f);
			 * 
			 * Font breakTimeFont = getBaseFontNormal();
			 * 
			 * if (coherentOvertimeApplicationDetail.isMealDurationChanged()) {
			 * breakTimeFont = getBaseFont7Red(); }
			 */

			/*
			 * PdfPCell breakTimeValCell = new PdfPCell(new Phrase(
			 * timeFormat.format(coherentOvertimeApplicationDetail
			 * .getMealDuration()), breakTimeFont));
			 * breakTimeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * breakTimeValCell.setBorderWidth(0.5f);
			 */

			PdfPCell shiftTypeValCell = new PdfPCell();
			if (coherentOvertimeApplicationDetail.isShiftTypeChanged()) {
				if (coherentOvertimeApplicationDetail.getShiftType() != null) {
					shiftTypeValCell = new PdfPCell(new Phrase(
							coherentOvertimeApplicationDetail.getShiftType()
									.getCodeDesc(), getBaseFont7Red()));
					shiftTypeValCell
							.setHorizontalAlignment(Element.ALIGN_RIGHT);
					shiftTypeValCell.setBorderWidth(0.5f);
				} else {
					shiftTypeValCell = new PdfPCell(new Phrase("NULL",
							getBaseFont7Red()));
					shiftTypeValCell
							.setHorizontalAlignment(Element.ALIGN_RIGHT);
					shiftTypeValCell.setBorderWidth(0.5f);
				}

			} else {
				if (coherentOvertimeApplicationDetail.getShiftType() != null) {
					shiftTypeValCell = new PdfPCell(new Phrase(
							coherentOvertimeApplicationDetail.getShiftType()
									.getCodeDesc(), getBaseFontNormal()));
					shiftTypeValCell
							.setHorizontalAlignment(Element.ALIGN_RIGHT);
					shiftTypeValCell.setBorderWidth(0.5f);
				} else {
					shiftTypeValCell = new PdfPCell(new Phrase("NULL",
							getBaseFontNormal()));
					shiftTypeValCell
							.setHorizontalAlignment(Element.ALIGN_RIGHT);
					shiftTypeValCell.setBorderWidth(0.5f);
				}
			}

			PdfPCell dayTypeValCell = new PdfPCell();

			if (coherentOvertimeApplicationDetail.isShiftChanged()) {
				if (coherentOvertimeApplicationDetail.isShift()) {
					dayTypeValCell = new PdfPCell(new Phrase("Yes",
							getBaseFont7Red()));
					dayTypeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					dayTypeValCell.setBorderWidth(0.5f);
				} else {
					dayTypeValCell = new PdfPCell(new Phrase("No",
							getBaseFont7Red()));
					dayTypeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					dayTypeValCell.setBorderWidth(0.5f);
				}

			} else {
				if (coherentOvertimeApplicationDetail.isShift()) {
					dayTypeValCell = new PdfPCell(new Phrase("Yes",
							getBaseFontNormal()));
					dayTypeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					dayTypeValCell.setBorderWidth(0.5f);
				} else {
					dayTypeValCell = new PdfPCell(new Phrase("No",
							getBaseFontNormal()));
					dayTypeValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					dayTypeValCell.setBorderWidth(0.5f);
				}
			}

			/*
			 * PdfPCell OTHoursValCell = new PdfPCell(new Phrase(
			 * coherentOvertimeApplicationDetail.getOtHours().toString(),
			 * getBaseFontNormal()));
			 * OTHoursValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * OTHoursValCell.setBorderWidth(0.5f);
			 * 
			 * PdfPCell OT_1_5_HoursValCell = new PdfPCell( new
			 * Phrase(coherentOvertimeApplicationDetail.getOt15Hours()
			 * .toString(), getBaseFontNormal()));
			 * OT_1_5_HoursValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * OT_1_5_HoursValCell.setBorderWidth(0.5f);
			 * 
			 * PdfPCell OT_1_0_DayValCell = new PdfPCell(new Phrase(
			 * coherentOvertimeApplicationDetail.getOt10Day().toString(),
			 * getBaseFontNormal()));
			 * OT_1_0_DayValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * OT_1_0_DayValCell.setBorderWidth(0.5f);
			 * 
			 * PdfPCell OT_2_0_DayValCell = new PdfPCell(new Phrase(
			 * coherentOvertimeApplicationDetail.getOt20Day().toString(),
			 * getBaseFontNormal()));
			 * OT_2_0_DayValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			 * OT_2_0_DayValCell.setBorderWidth(0.5f);
			 */

			PdfPCell remarksValCell = new PdfPCell(new Phrase(
					coherentOvertimeApplicationDetail.getRemarks(),
					getBaseFontNormal()));
			remarksValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			remarksValCell.setBorderWidth(0.5f);

			revDetailTable.addCell(timesheetDateValCell);
			revDetailTable.addCell(timesheetDayValCell);
			/*
			 * revDetailTable.addCell(startDateValCell);
			 * revDetailTable.addCell(endDateValCell);
			 * revDetailTable.addCell(breakTimeValCell);
			 */
			revDetailTable.addCell(shiftTypeValCell);
			revDetailTable.addCell(dayTypeValCell);
			/*
			 * revDetailTable.addCell(OTHoursValCell);
			 * revDetailTable.addCell(OT_1_5_HoursValCell);
			 * revDetailTable.addCell(OT_1_0_DayValCell);
			 * revDetailTable.addCell(OT_2_0_DayValCell);
			 */
			revDetailTable.addCell(remarksValCell);

		}

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
		grandTotalHoursCell.setBorderWidth(0.5f);
		grandTotalHoursCell.setHorizontalAlignment(Element.ALIGN_LEFT);

		PdfPCell totalOTHoursValCell = new PdfPCell(new Phrase(
				coherentTimesheetVO.getTotalShifts().toString(),
				getBaseFontNormal()));
		totalOTHoursValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		totalOTHoursValCell.setBorderWidth(0.5f);
		totalOTHoursValCell.setBorder(11);

		PdfPCell tempCell = new PdfPCell(new Phrase("", getBaseFontNormal()));
		tempCell.setBorder(Rectangle.NO_BORDER);
		// tempCell.setBorderWidth(0.5f);
		// tempCell.setBorder(11);

		/*
		 * PdfPCell totalOT_1_5_HoursValCell = new PdfPCell(new Phrase(
		 * coherentTimesheetVO.getTotalOT15Hours().toString(),
		 * getBaseFontNormal()));
		 * totalOT_1_5_HoursValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		 * totalOT_1_5_HoursValCell.setBorderWidth(0.5f);
		 * 
		 * PdfPCell totalOT_1_0_DayValCell = new PdfPCell(new Phrase(
		 * coherentTimesheetVO.getTotalOT10Day().toString(),
		 * getBaseFontNormal()));
		 * totalOT_1_0_DayValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		 * totalOT_1_0_DayValCell.setBorder(11);
		 * 
		 * PdfPCell totalOT_2_0_DayValCell = new PdfPCell(new Phrase(
		 * coherentTimesheetVO.getTotalOT20Day().toString(),
		 * getBaseFontNormal()));
		 * totalOT_2_0_DayValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		 * totalOT_2_0_DayValCell.setBorderWidth(0.5f);
		 */

		// revDetailTable.addCell(borderLeftDownCell);
		// revDetailTable.addCell(grandTotalHoursCell);
		// revDetailTable.addCell(tempCell);
		revDetailTable.addCell(tempCell);
		revDetailTable.addCell(tempCell);
		revDetailTable.addCell(grandTotalHoursCell);
		revDetailTable.addCell(totalOTHoursValCell);
		/*
		 * revDetailTable.addCell(totalOT_1_5_HoursValCell);
		 * revDetailTable.addCell(totalOT_1_0_DayValCell);
		 * revDetailTable.addCell(totalOT_2_0_DayValCell);
		 */
		revDetailTable.addCell(tempCell);

		revDetailCell.addElement(revDetailTable);
		timesheetSectionTable.addCell(revDetailCell);

		return timesheetSectionTable;
	}

	private List<LocalDate> getBlockHolidayDateList(
			CoherentShiftApplication coherentTimesheetVO) {
		List<LocalDate> blockHolidaysList = new ArrayList<LocalDate>();
		List<String> blockHolidays = lundinRequestLogic
				.getEmpResignedAndNewHiresDates(coherentTimesheetVO
						.getEmployee().getEmployeeId(), coherentTimesheetVO
						.getTimesheetBatch().getStartDate(),
						coherentTimesheetVO.getTimesheetBatch().getEndDate());
		for (String dateStr : blockHolidays) {
			Date date = DateUtils.stringToDate(dateStr, "MM/dd/yyyy");
			DateTime dateTime = new DateTime(date);
			blockHolidaysList.add(dateTime.toLocalDate());
		}
		return blockHolidaysList;
	}

	private List<LocalDate> getHolidayDateList(
			CoherentShiftApplication coherentOvertimeApplication) {
		List<LocalDate> holidaysDateList = new ArrayList<LocalDate>();
		List<String> holidays = lundinRequestLogic.getHolidaysFor(
				coherentOvertimeApplication.getEmployee().getEmployeeId(),
				coherentOvertimeApplication.getTimesheetBatch().getStartDate(),
				coherentOvertimeApplication.getTimesheetBatch().getEndDate());
		for (String dateStr : holidays) {
			Date date = DateUtils.stringToDate(dateStr, "MM/dd/yyyy");
			DateTime dateTime = new DateTime(date);
			holidaysDateList.add(dateTime.toLocalDate());
		}
		return holidaysDateList;
	}

	private PdfPTable createEmpInfoDetailTable(
			CoherentShiftApplication coherentTimesheetVO) {
		CoherentShiftDetailForm coherentOvertimeDetailForm = new CoherentShiftDetailForm();
		CoherentShiftDetailForm response = setDeptCostCentre(
				coherentTimesheetVO.getEmployee().getEmployeeId(),
				coherentTimesheetVO.getEmployee().getCompany().getCompanyId(),
				coherentTimesheetVO.getEmployee().getEmployeeNumber(),
				coherentOvertimeDetailForm, coherentTimesheetVO.getEmployee()
						.getCompany().getDateFormat());

		if (StringUtils.isEmpty(response.getCostCentre())
				|| response.getCostCentre().equalsIgnoreCase("null")) {
			response.setCostCentre("");
		}

		if (StringUtils.isEmpty(response.getDepartment())
				|| response.getDepartment().equalsIgnoreCase("null")) {
			response.setCostCentre("");
		}

		TimesheetBatch timesheetBatch = timesheetBatchDAO
				.findById(coherentTimesheetVO.getTimesheetBatch()
						.getTimesheetBatchId());
		List<CoherentShiftApplicationReviewer> coherentOvertimeApplicationReviewers = coherentShiftApplicationReviewerDAO
				.findByCoherentShiftApplication(coherentTimesheetVO
						.getShiftApplicationID());

		StringBuilder approvalSteps = new StringBuilder();

		for (int i = 0; i < coherentOvertimeApplicationReviewers.size(); i++) {
			if (i != 0) {
				approvalSteps.append(" -> ");
			}
			approvalSteps
					.append(getEmployeeName(coherentOvertimeApplicationReviewers
							.get(i).getEmployeeReviewer()));
		}

		PdfPTable empInfoTable = new PdfPTable(new float[] { 1 });
		empInfoTable.setWidthPercentage(110f);

		PdfPCell revDetailCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		revDetailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		revDetailCell.setBorder(0);
		revDetailCell.setPadding(0);

		PdfPTable revDetailTable = new PdfPTable(new float[] { 1f, 1.5f, 0.1f,
				1f, 3f, 0.1f, 1f, 2f });
		revDetailTable.setWidthPercentage(110f);
		revDetailTable.setSpacingBefore(40f);

		PdfPCell empNumCell = new PdfPCell(new Phrase("Employee Id:",
				getBaseFontNormal()));
		empNumCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNumCell.setBorder(0);
		empNumCell.setPadding(4);

		PdfPCell empNumValCell = new PdfPCell(new Phrase(coherentTimesheetVO
				.getEmployee().getEmployeeNumber(), getBaseFontNormal()));
		empNumValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNumValCell.setBorder(0);
		empNumValCell.setPadding(4);

		PdfPCell claimMonthCell = new PdfPCell(new Phrase("Claim Month:",
				getBaseFontNormal()));
		claimMonthCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		claimMonthCell.setBorder(0);
		claimMonthCell.setPadding(4);

		PdfPCell claimMonthValCell = new PdfPCell(new Phrase(
				timesheetBatch.getTimesheetBatchDesc(), getBaseFontNormal()));
		claimMonthValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		claimMonthValCell.setBorder(0);
		claimMonthValCell.setPadding(4);

		PdfPCell shiftTypeCell = new PdfPCell(new Phrase("",
				getBaseFontNormal()));
		shiftTypeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		shiftTypeCell.setBorder(0);
		shiftTypeCell.setPadding(4);

		PdfPCell shiftTypeValCell = new PdfPCell(new Phrase("",
				getBaseFontNormal()));
		shiftTypeValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		shiftTypeValCell.setBorder(0);
		shiftTypeValCell.setPadding(4);

		PdfPCell costCentreCell = new PdfPCell(new Phrase("Cost Centre:",
				getBaseFontNormal()));
		costCentreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		costCentreCell.setBorder(0);
		costCentreCell.setPadding(4);

		PdfPCell costCentreValCell = new PdfPCell(new Phrase(
				response.getCostCentre(), getBaseFontNormal()));
		costCentreValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		costCentreValCell.setBorder(0);
		costCentreValCell.setPadding(4);

		PdfPCell empNameCell = new PdfPCell(new Phrase("Name:",
				getBaseFontNormal()));
		empNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNameCell.setBorder(0);
		empNameCell.setPadding(4);

		PdfPCell empNameValCell = new PdfPCell(new Phrase(
				getEmployeeName(coherentTimesheetVO.getEmployee()),
				getBaseFontNormal()));
		empNameValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNameValCell.setBorder(0);
		empNameValCell.setPadding(4);

		PdfPCell approvalStepsCell = new PdfPCell(new Phrase("Approval Steps:",
				getBaseFontNormal()));
		approvalStepsCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		approvalStepsCell.setBorder(0);
		approvalStepsCell.setPadding(4);

		PdfPCell approvalStepsValCell = new PdfPCell(new Phrase(
				approvalSteps.toString(), getBaseFontNormal()));
		approvalStepsValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		approvalStepsValCell.setBorder(0);
		approvalStepsValCell.setPadding(4);

		PdfPCell departmentCell = new PdfPCell(new Phrase("Department:",
				getBaseFontNormal()));
		departmentCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		departmentCell.setBorder(0);
		departmentCell.setPadding(4);

		PdfPCell departmentValCell = new PdfPCell(new Phrase(
				response.getDepartment(), getBaseFontNormal()));
		departmentValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		departmentValCell.setBorder(0);
		departmentValCell.setPadding(4);

		PdfPCell blankCell = new PdfPCell(new Phrase("", getBaseFontNormal()));
		blankCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blankCell.setBorder(0);
		blankCell.setPadding(0);

		revDetailTable.addCell(empNumCell);
		revDetailTable.addCell(empNumValCell);
		revDetailTable.addCell(blankCell);

		revDetailTable.addCell(claimMonthCell);
		revDetailTable.addCell(claimMonthValCell);
		revDetailTable.addCell(blankCell);

		revDetailTable.addCell(shiftTypeCell);
		revDetailTable.addCell(shiftTypeValCell);
		// revDetailTable.addCell(blankCell);

		revDetailTable.addCell(empNameCell);
		revDetailTable.addCell(empNameValCell);
		revDetailTable.addCell(blankCell);

		revDetailTable.addCell(approvalStepsCell);
		revDetailTable.addCell(approvalStepsValCell);
		revDetailTable.addCell(blankCell);

		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(blankCell);

		revDetailTable.addCell(costCentreCell);
		revDetailTable.addCell(costCentreValCell);
		revDetailTable.addCell(blankCell);

		revDetailTable.addCell(departmentCell);
		revDetailTable.addCell(departmentValCell);
		revDetailTable.addCell(blankCell);

		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(blankCell);
		revDetailTable.addCell(blankCell);

		revDetailCell.addElement(revDetailTable);
		empInfoTable.addCell(revDetailCell);

		return empInfoTable;
	}

	private CoherentShiftDetailForm setDeptCostCentre(long employeeId,
			long companyId, String employeeNumber,
			CoherentShiftDetailForm coherentOvertimeDetailForm,
			String companyDateFormat) {
		List<Object[]> deptCostCentreEmpObjectList = coherentTimesheetReportsLogic
				.getEmpDynFieldsValueList(companyId, companyDateFormat,
						employeeId);

		for (Object[] deptObject : deptCostCentreEmpObjectList) {
			if (deptObject != null && deptObject[3] != null
					&& deptObject[3].equals(employeeNumber)) {
				coherentOvertimeDetailForm.setDepartment(String
						.valueOf(deptObject[0]));
				coherentOvertimeDetailForm.setCostCentre(String
						.valueOf(deptObject[2]));
			}
		}
		return coherentOvertimeDetailForm;
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

		return employeeName;
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

	private Font getBaseFont10() {
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

	private Font getBaseFont10White() {
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
		Font unicodeFont = new Font(bf, 10, Font.NORMAL, BaseColor.WHITE);
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

	private PdfPTable createWorkflowHistoryTable(
			CoherentShiftApplication coherentOvertimeApplication) {
		PdfPTable workflowySectionTable = new PdfPTable(new float[] { 1f });
		workflowySectionTable.setWidthPercentage(110f);
		workflowySectionTable.getTotalWidth();
		workflowySectionTable.setSpacingBefore(70f);

		PdfPCell workflowyTableHeaderCell = new PdfPCell(new Phrase(
				"WorkFlow Status", getBaseFont12WhiteBold()));
		workflowyTableHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		workflowyTableHeaderCell
				.setBackgroundColor(new BaseColor(113, 169, 210));
		workflowyTableHeaderCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);

		PdfPCell pdfCell1 = createWorkFlowSectionTableData("S.No",
				"WorkFlow Role", "Name", "Remarks", "Status", "Date",
				getBaseFont7());
		PDFUtils.setDefaultCellLayout(pdfCell1);
		pdfCell1.enableBorderSide(Rectangle.LEFT);
		pdfCell1.enableBorderSide(Rectangle.RIGHT);
		pdfCell1.setBackgroundColor(new BaseColor(107, 165, 209));

		String userStatus = "";

		if (coherentOvertimeApplication.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.OT_STATUS_WITHDRAWN)) {
			userStatus = PayAsiaConstants.OT_STATUS_WITHDRAWN;
		} else {
			userStatus = PayAsiaConstants.OT_STATUS_SUBMITTED;

		}

		String date = DateUtils
				.timeStampToStringWithTime(new ArrayList<CoherentShiftApplicationReviewer>(
						coherentOvertimeApplication
								.getCoherentShiftApplicationReviewer()).get(0)
						.getCreatedDate());

		PdfPCell userCell = createWorkFlowSectionTableData("1", "User",
				getEmployeeName(coherentOvertimeApplication.getEmployee()), "",
				userStatus, date, getBaseFont7());
		PDFUtils.setDefaultCellLayout(userCell);
		userCell.enableBorderSide(Rectangle.LEFT);
		userCell.enableBorderSide(Rectangle.RIGHT);

		workflowySectionTable.addCell(workflowyTableHeaderCell);
		workflowySectionTable.addCell(pdfCell1);

		HashMap<Long, CoherentShiftApplicationWorkflow> workFlow = new HashMap<>();
		for (CoherentShiftApplicationWorkflow claimApplicationWorkflow : coherentOvertimeApplication
				.getCoherentShiftApplicationWorkflows()) {

			workFlow.put(claimApplicationWorkflow.getCreatedBy()
					.getEmployeeId(), claimApplicationWorkflow);

		}

		List<CoherentShiftApplicationReviewer> coherentOvertimeApplicationReviewers = new ArrayList<>(
				coherentOvertimeApplication
						.getCoherentShiftApplicationReviewer());

		Collections.sort(coherentOvertimeApplicationReviewers,
				new timesheetRevComp());

		if (coherentOvertimeApplicationReviewers.size() == 0) {
			userCell.enableBorderSide(Rectangle.BOTTOM);
		}
		workflowySectionTable.addCell(userCell);

		Integer snoCount = 2;
		Integer revCount = 1;

		String claimRev1Status = "";
		String claimRev2Status = "";
		String claimRev3Status = "";
		int tempRevCount = 1;

		for (CoherentShiftApplicationReviewer coherentOvertimeApplicationReviewer : coherentOvertimeApplicationReviewers) {
			CoherentShiftApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(coherentOvertimeApplication.getEmployee()
							.getEmployeeId());
			String status = "";
			if (claimApplicationWorkflow != null) {
				status = claimApplicationWorkflow.getTimesheetStatusMaster()
						.getTimesheetStatusName();
			}
			if (tempRevCount == 1) {
				claimRev1Status = status;
			}
			if (tempRevCount == 2) {
				claimRev2Status = status;
			}
			if (tempRevCount == 3) {
				claimRev3Status = status;
			}

			tempRevCount++;
		}
		boolean isCompleted = false;
		int claimAppRevListSize = coherentOvertimeApplicationReviewers.size();
		for (CoherentShiftApplicationReviewer coherentOvertimeApplicationReviewer : coherentOvertimeApplicationReviewers) {

			CoherentShiftApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(coherentOvertimeApplicationReviewer
							.getEmployeeReviewer().getEmployeeId());
			String remarks = "";
			String status = "";
			String claimDate = "";
			if (claimApplicationWorkflow != null) {
				remarks = claimApplicationWorkflow.getRemarks();
				status = claimApplicationWorkflow.getTimesheetStatusMaster()
						.getTimesheetStatusName();
				if (status.equalsIgnoreCase("Completed")) {
					isCompleted = true;
				}
				claimDate = DateUtils
						.timeStampToStringWithTime(claimApplicationWorkflow
								.getCreatedDate());
			} else {
				if (!isCompleted) {
					status = "Pending";
				}
			}
			if (!coherentOvertimeApplication.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_WITHDRAWN)) {
				if (revCount == 1) {
					if (StringUtils.isBlank(claimRev1Status)) {
						if (!isCompleted) {
							status = "Pending";
						}
					}
				}
				if (revCount == 2) {
					if (StringUtils.isBlank(claimRev2Status)) {
						if (StringUtils.isBlank(claimRev1Status)) {
							if (!isCompleted) {
								status = "Pending";
							}
						} else {
							if (claimRev1Status
									.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_COMPLETED)) {
								status = "";
							}
						}

					}
				}

				if (revCount == 3) {
					if (StringUtils.isBlank(claimRev3Status)) {
						if (StringUtils.isBlank(claimRev1Status)
								&& StringUtils.isBlank(claimRev2Status)) {
							if (!isCompleted) {
								status = "Pending";
							}
						} else {
							if (claimRev2Status
									.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_COMPLETED)) {
								status = "";
							}
						}

					}
				}

			}

			PdfPCell claimRevCell = createWorkFlowSectionTableData(
					snoCount.toString(),
					"Reviewer" + revCount,
					getEmployeeNameWithNumber(coherentOvertimeApplicationReviewer
							.getEmployeeReviewer()), remarks, status,
					claimDate, getBaseFont7());
			PDFUtils.setDefaultCellLayout(claimRevCell);
			claimRevCell.enableBorderSide(Rectangle.LEFT);
			claimRevCell.enableBorderSide(Rectangle.RIGHT);
			if (claimAppRevListSize == revCount) {
				claimRevCell.enableBorderSide(Rectangle.BOTTOM);
			}

			workflowySectionTable.addCell(claimRevCell);

			snoCount++;
			revCount++;
		}

		return workflowySectionTable;
	}

}
