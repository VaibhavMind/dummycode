package com.payasia.logic.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetStatusReportDTO;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PDFUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaPDFConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetApplicationDAO;
import com.payasia.dao.LundinDepartmentDAO;
import com.payasia.dao.LundinTimesheetDetailDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.LundinDepartment;
import com.payasia.dao.bean.LundinTimesheetDetail;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.LundinTimesheetPrintPDFLogic;
import com.payasia.logic.LundinTimesheetReportsLogic;
import com.payasia.logic.LundinTimesheetRequestLogic;

@Component
public class LundinTimesheetPrintPDFLogicImpl extends BaseLogic implements
		LundinTimesheetPrintPDFLogic {

	@Resource
	LundinTimesheetDetailDAO lundinTimesheetDetailDAO;
	@Resource
	EmployeeTimesheetApplicationDAO lundinTimesheetDAO;
	@Resource
	LundinTimesheetRequestLogic lundinRequestLogic;
	@Resource
	LundinDepartmentDAO lundinDepartmentDAO;
	@Resource
	TimesheetBatchDAO lundinTimesheetBatchDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	LundinTimesheetReportsLogic lundinTimesheetReportsLogic;

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetPrintPDFLogicImpl.class);

	@Override
	public PdfPTable createTimesheetPdf(Document document, PdfWriter writer,
			int currentPageNumber, Long companyId, Long timesheetId,
			boolean hasLundinTimesheetModule,
			LundinTimesheetDTO lundinTimesheetDTO) {
		PdfPTable mainSectionTable = new PdfPTable(new float[] { 1 });
		mainSectionTable.setWidthPercentage(95f);
		mainSectionTable.setHorizontalAlignment(Element.ALIGN_CENTER);

		PdfPCell mainCell = new PdfPCell();
		mainCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		mainCell.setPadding(0);
		mainCell.setPaddingTop(5f);
		mainCell.setBorder(0);
		PDFUtils.setDefaultCellLayout(mainCell);
		EmployeeTimesheetApplication lundinTimesheetVO = lundinTimesheetDAO
				.findById(timesheetId);

		List<LocalDate> holidaysDateList = getHolidayDateList(lundinTimesheetVO);
		List<LocalDate> blockHolidaysList = getBlockHolidayDateList(lundinTimesheetVO);

		PdfPTable empInfoTable = createEmpInfoDetailTable(lundinTimesheetVO,
				lundinTimesheetDTO);
		empInfoTable.setWidthPercentage(110f);

		PdfPTable timesheetSectionTable = getTimesheetSectionTable(document,
				lundinTimesheetVO, companyId, holidaysDateList,
				blockHolidaysList);
		timesheetSectionTable.setWidthPercentage(110f);
		timesheetSectionTable.setSpacingBefore(10f);

		PdfPTable workflowSectionTable = null;
		if (!lundinTimesheetVO.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_DRAFT)) {
			PdfPTable pdfTableWorkflowHeaderTable = getPdfTableHeader(document,
					"Workflow Status");
			pdfTableWorkflowHeaderTable.setWidthPercentage(110f);
			workflowSectionTable = createWorkflowHistoryTable(lundinTimesheetVO);
			workflowSectionTable.setWidthPercentage(121f);
			workflowSectionTable.setSpacingBefore(10f);
		}

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

	private PdfPTable getPdfTableHeader(Document document,
			String tableHeaderName) {
		PdfPTable pdfTableHeaderTable = new PdfPTable(new float[] { 1f });
		pdfTableHeaderTable.setWidthPercentage(110f);
		pdfTableHeaderTable.getTotalWidth();
		pdfTableHeaderTable.setSpacingBefore(5f);

		PdfPCell headerCell = new PdfPCell(new Phrase(tableHeaderName,
				getBaseFont10Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerCell.setPadding(PayAsiaPDFConstants.CELL_PADDING);
		headerCell.setPadding(2);
		headerCell
				.setBackgroundColor(PayAsiaPDFConstants.TABLE_STANDARD_HEADER_GRAY_COLOR);

		pdfTableHeaderTable.addCell(headerCell);
		return pdfTableHeaderTable;
	}

	private PdfPTable createEmpInfoDetailTable(
			EmployeeTimesheetApplication lundinTimesheetVO,
			LundinTimesheetDTO lundinTimesheetDTO) {
		PdfPTable empInfoTable = new PdfPTable(new float[] { 1 });
		empInfoTable.setWidthPercentage(110f);

		PdfPCell revDetailCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		revDetailCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		revDetailCell.setBorder(0);
		revDetailCell.setPadding(0);

		PdfPTable revDetailTable = new PdfPTable(new float[] { 1f, 1f, 3f });
		revDetailTable.setWidthPercentage(110f);
		revDetailTable.setSpacingBefore(40f);

		PdfPCell deptCell = new PdfPCell(new Phrase("Department:",
				getBaseFontNormal()));
		deptCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		deptCell.setBorder(0);
		deptCell.setPadding(4);

		PdfPCell deptValCell = null;
		if (lundinTimesheetDTO != null
				&& StringUtils.isNotBlank(lundinTimesheetDTO
						.getDepartmentCode())
				&& !lundinTimesheetDTO.getDepartmentCode().equalsIgnoreCase(
						"No Department Assigned")) {
			deptValCell = new PdfPCell(
					new Phrase(lundinTimesheetDTO.getDepartmentCode(),
							getBaseFontNormal()));
			deptValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			deptValCell.setBorder(0);
			deptValCell.setPadding(4);
		}

		PdfPCell empNumCell = new PdfPCell(new Phrase("Employee ID:",
				getBaseFontNormal()));
		empNumCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNumCell.setBorder(0);
		empNumCell.setPadding(4);

		PdfPCell empNumValCell = new PdfPCell(new Phrase(lundinTimesheetVO
				.getEmployee().getEmployeeNumber(), getBaseFontNormal()));
		empNumValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNumValCell.setBorder(0);
		empNumValCell.setPadding(4);

		PdfPCell empNameCell = new PdfPCell(new Phrase("Employee Name:",
				getBaseFontNormal()));
		empNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNameCell.setBorder(0);
		empNameCell.setPadding(4);

		PdfPCell empNameValCell = new PdfPCell(new Phrase(
				getEmployeeName(lundinTimesheetVO.getEmployee()),
				getBaseFontNormal()));
		empNameValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		empNameValCell.setBorder(0);
		empNameValCell.setPadding(4);

		PdfPCell batchCell = new PdfPCell(new Phrase("Timesheet Batch:",
				getBaseFontNormal()));
		batchCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		batchCell.setBorder(0);
		batchCell.setPadding(4);

		PdfPCell batchValCell = new PdfPCell(new Phrase(lundinTimesheetVO
				.getTimesheetBatch().getTimesheetBatchDesc(),
				getBaseFontNormal()));
		batchValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		batchValCell.setBorder(0);
		batchValCell.setPadding(4);

		PdfPCell statusTimesheetCell = new PdfPCell(new Phrase(
				"Timesheet Status:", getBaseFontNormal()));
		statusTimesheetCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		statusTimesheetCell.setBorder(0);
		statusTimesheetCell.setPadding(4);

		PdfPCell statusTimesheetValCell = new PdfPCell(new Phrase(
				lundinTimesheetVO.getTimesheetStatusMaster()
						.getTimesheetStatusName(), getBaseFontNormal()));
		statusTimesheetValCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		statusTimesheetValCell.setBorder(0);
		statusTimesheetValCell.setPadding(4);

		if (lundinTimesheetDTO != null
				&& StringUtils.isNotBlank(lundinTimesheetDTO
						.getDepartmentCode())
				&& !lundinTimesheetDTO.getDepartmentCode().equalsIgnoreCase(
						"No Department Assigned")) {
			revDetailTable.addCell(deptCell);
			revDetailTable.addCell(deptValCell);
			revDetailTable.addCell(tempCell());
		}
		revDetailTable.addCell(empNumCell);
		revDetailTable.addCell(empNumValCell);
		revDetailTable.addCell(tempCell());
		revDetailTable.addCell(empNameCell);
		revDetailTable.addCell(empNameValCell);
		revDetailTable.addCell(tempCell());
		revDetailTable.addCell(batchCell);
		revDetailTable.addCell(batchValCell);
		revDetailTable.addCell(tempCell());
		revDetailTable.addCell(statusTimesheetCell);
		revDetailTable.addCell(statusTimesheetValCell);
		revDetailTable.addCell(tempCell());
		revDetailCell.addElement(revDetailTable);
		empInfoTable.addCell(revDetailCell);

		return empInfoTable;
	}

	private PdfPTable getTimesheetSectionTable(Document document,
			EmployeeTimesheetApplication lundinTimesheetVO, Long companyId,
			List<LocalDate> holidaysList, List<LocalDate> blockHolidaysList) {
		PdfPTable timesheetSectionTable = new PdfPTable(new float[] { 1f });
		timesheetSectionTable.setWidthPercentage(110f);

		PdfPCell headerCell = createTimesheetHeader();

		// Month Name Table
		PdfPCell monthNameCell = createMonthHeader(lundinTimesheetVO);

		// Date
		PdfPCell DateCell = createDateHeader(lundinTimesheetVO, holidaysList,
				blockHolidaysList);

		// Day
		PdfPCell dayCell = new PdfPCell(new Phrase("", getBaseFont7()));
		dayCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		dayCell.setBorder(0);
		dayCell.setPadding(0);

		PdfPTable dayTable = getTimesheetDayHeaderColumns(lundinTimesheetVO,
				holidaysList, blockHolidaysList);
		dayTable.setWidthPercentage(110f);
		dayCell.addElement(dayTable);

		// Block Afe Caption
		PdfPCell blockAfeCapCell = createBlockAfeHeader();

		// Block Afe Data Caption
		// Day
		PdfPCell blockAfeDataCell = setTimesheetDetail(lundinTimesheetVO,
				holidaysList, blockHolidaysList);
		blockAfeDataCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blockAfeDataCell.setBorder(0);
		blockAfeDataCell.setPadding(0);

		timesheetSectionTable.addCell(headerCell);
		timesheetSectionTable.addCell(monthNameCell);
		timesheetSectionTable.addCell(DateCell);
		timesheetSectionTable.addCell(dayCell);
		timesheetSectionTable.addCell(blockAfeCapCell);
		timesheetSectionTable.addCell(blockAfeDataCell);

		return timesheetSectionTable;
	}

	private PdfPCell createTimesheetHeader() {
		PdfPCell headerCell = new PdfPCell(new Phrase("", getBaseFont12Bold()));
		headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerCell.setBorder(0);
		headerCell.setPadding(0);

		PdfPTable headerNameTable = new PdfPTable(new float[] { 5f });
		headerNameTable.setWidthPercentage(110f);

		PdfPCell headerNameCell = new PdfPCell(new Phrase("Timesheet",
				getBaseFont12WhiteBold()));
		headerNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerNameCell.setBorder(0);
		headerNameCell.setBackgroundColor(new BaseColor(113, 169, 210));
		headerNameCell.setPadding(4);
		enableCellBorder(headerNameCell);

		headerNameTable.addCell(headerNameCell);
		headerCell.addElement(headerNameTable);
		return headerCell;
	}

	private PdfPCell createBlockAfeHeader() {
		PdfPCell blockAfeCapCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		blockAfeCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blockAfeCapCell.setBorder(0);
		blockAfeCapCell.setPadding(0);

		PdfPTable blockAfeCapTable = new PdfPTable(
				new float[] { 0.5f, 0.5f, 4f });
		blockAfeCapTable.setWidthPercentage(110f);

		PdfPCell blockCapCell = new PdfPCell(
				new Phrase("Block", getBaseFont7()));
		blockCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blockCapCell.setBorder(0);
		blockCapCell.setBackgroundColor(new BaseColor(191, 191, 191));
		blockCapCell.setPadding(4);
		blockCapCell.setPaddingLeft(2);
		enableCellBorder(blockCapCell);

		PdfPCell afeCapCell = new PdfPCell(new Phrase("AFE", getBaseFont7()));
		afeCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		afeCapCell.setBorder(0);
		afeCapCell.setBackgroundColor(new BaseColor(191, 191, 191));
		afeCapCell.setPadding(4);
		afeCapCell.setPaddingLeft(2);
		enableCellBorder(afeCapCell);

		PdfPCell blockAfeBlankCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		blockAfeBlankCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blockAfeBlankCell.setBorder(0);
		blockAfeBlankCell.setBackgroundColor(new BaseColor(191, 191, 191));
		blockAfeBlankCell.setPadding(4);
		blockAfeBlankCell.setPaddingLeft(2);
		enableCellBorder(blockAfeBlankCell);

		blockAfeCapTable.addCell(blockCapCell);
		blockAfeCapTable.addCell(afeCapCell);
		blockAfeCapTable.addCell(blockAfeBlankCell);
		blockAfeCapCell.addElement(blockAfeCapTable);
		return blockAfeCapCell;
	}

	private PdfPCell createDateHeader(
			EmployeeTimesheetApplication lundinTimesheetVO,
			List<LocalDate> holidaysList, List<LocalDate> blockHolidaysList) {
		PdfPCell DateOuterCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		DateOuterCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		DateOuterCell.setBorder(0);
		DateOuterCell.setPadding(0);

		PdfPTable DateHeaderTable = new PdfPTable(new float[] { 1f, 3.75f,
				0.25f });
		DateHeaderTable.setWidthPercentage(110f);

		PdfPCell dateCapCell = new PdfPCell(new Phrase("Date",
				getBaseFont7WhiteBold()));
		dateCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		dateCapCell.setBorder(0);
		dateCapCell.setPaddingTop(4);
		dateCapCell.setPaddingBottom(4);
		dateCapCell.setPaddingLeft(2);
		dateCapCell.setPaddingRight(2);
		dateCapCell.setBackgroundColor(new BaseColor(0, 112, 192));
		enableCellBorder(dateCapCell);

		PdfPCell dateHeaderCell = getTimesheetDateHeaderColumns(
				lundinTimesheetVO, holidaysList, blockHolidaysList);
		dateHeaderCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		dateHeaderCell.setBorder(0);
		enableCellBorder(dateHeaderCell);
		dateHeaderCell.setPadding(0);

		PdfPCell totalCapCell = new PdfPCell(new Phrase("Total",
				getBaseFont7Bold()));
		totalCapCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		totalCapCell.setBorder(0);
		totalCapCell.setPaddingTop(4);
		totalCapCell.setPaddingBottom(4);
		totalCapCell.setPaddingLeft(2);
		totalCapCell.setPaddingRight(2);
		totalCapCell.setBackgroundColor(new BaseColor(141, 180, 226));
		enableCellBorder(totalCapCell);

		DateHeaderTable.addCell(dateCapCell);
		DateHeaderTable.addCell(dateHeaderCell);
		DateHeaderTable.addCell(totalCapCell);
		DateOuterCell.addElement(DateHeaderTable);
		return DateOuterCell;
	}

	private PdfPCell createMonthHeader(
			EmployeeTimesheetApplication lundinTimesheetVO) {
		PdfPCell monthHeaderOuterCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		monthHeaderOuterCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		monthHeaderOuterCell.setBorder(0);
		monthHeaderOuterCell.setPadding(0);

		PdfPTable monthHeaderTable = new PdfPTable(
				new float[] { 1f, 2.3f, 1.7f });
		monthHeaderTable.setWidthPercentage(110f);

		PdfPCell monthBlankCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		monthBlankCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		monthBlankCell.setBorder(0);
		enableCellBorder(monthBlankCell);

		DateTime startdt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getStartDate());
		String firstMonthBatchValue = getMonthName(startdt.getMonthOfYear() - 1)
				+ " " + startdt.getYear();
		DateTime enddt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getEndDate());
		String secondMonthBatchValue = getMonthName(enddt.getMonthOfYear() - 1)
				+ " " + enddt.getYear();

		PdfPCell firstMonthBatchCell = new PdfPCell(new Phrase(
				firstMonthBatchValue, getBaseFont12WhiteBold()));
		firstMonthBatchCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		firstMonthBatchCell.setBorder(0);
		firstMonthBatchCell.setBackgroundColor(new BaseColor(0, 112, 192));
		firstMonthBatchCell.setPadding(4);

		PdfPCell secondMonthBatchCell = new PdfPCell(new Phrase(
				secondMonthBatchValue, getBaseFont12WhiteBold()));
		secondMonthBatchCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		secondMonthBatchCell.setBorder(0);
		secondMonthBatchCell.enableBorderSide(Rectangle.RIGHT);
		secondMonthBatchCell.setBackgroundColor(new BaseColor(0, 112, 192));
		secondMonthBatchCell.setPadding(4);

		monthHeaderTable.addCell(monthBlankCell);
		monthHeaderTable.addCell(firstMonthBatchCell);
		monthHeaderTable.addCell(secondMonthBatchCell);
		monthHeaderOuterCell.addElement(monthHeaderTable);
		return monthHeaderOuterCell;
	}

	private PdfPCell setTimesheetDetail(
			EmployeeTimesheetApplication lundinTimesheetVO,
			List<LocalDate> holidaysList, List<LocalDate> blockHolidaysList) {
		List<LundinTimesheetDetail> lundinTsheetDetails = lundinTimesheetDetailDAO
				.findByTimesheetId(lundinTimesheetVO.getTimesheetId());
		Map<String, String> dayMonthValueMap = new HashMap<String, String>();
		Map<Integer, String> totalByColumnMap = new HashMap<Integer, String>();
		getEmployeeTimesheetDetails(lundinTimesheetVO, lundinTsheetDetails,
				dayMonthValueMap);

		// Block Afe Data Caption
		// Day
		List<String> alreadyAttached = new ArrayList<String>();

		PdfPCell blockAfeDataOuterCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		blockAfeDataOuterCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		blockAfeDataOuterCell.setBorder(0);
		blockAfeDataOuterCell.setPadding(0);

		PdfPTable blockAfeDataTable = new PdfPTable(new float[] { 0.5f, 0.5f,
				3.75f, 0.25f });
		blockAfeDataTable.setWidthPercentage(110f);

		for (int i = 0; i < lundinTsheetDetails.size(); i++) {
			LundinTimesheetDetail lundinTimesheetDetail = lundinTsheetDetails
					.get(i);
			if (alreadyAttached.contains(lundinTimesheetDetail.getLundinBlock()
					.getBlockName()
					+ lundinTimesheetDetail.getLundinAFE().getAfeName())) {
				continue;
			}

			String blockName = "";
			if (StringUtils.isNotBlank(lundinTimesheetDetail.getBlockName())) {
				blockName = lundinTimesheetDetail.getBlockName();
			} else {
				blockName = lundinTimesheetDetail.getLundinBlock()
						.getBlockName();
			}
			PdfPCell blockNameCell = new PdfPCell(new Phrase(blockName,
					getBaseFont7()));
			blockNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			blockNameCell.setBorder(0);
			blockNameCell.setPaddingTop(4);
			blockNameCell.setPaddingBottom(4);
			blockNameCell.setPaddingLeft(2);
			blockNameCell.setPaddingRight(2);
			blockNameCell.setBackgroundColor(new BaseColor(175, 206, 243));
			enableCellBorder(blockNameCell);

			String afeName = "";
			if (StringUtils.isNotBlank(lundinTimesheetDetail.getAfeName())) {
				afeName = lundinTimesheetDetail.getAfeName();
			} else {
				afeName = lundinTimesheetDetail.getLundinAFE().getAfeName();
			}
			PdfPCell afeCapCell = new PdfPCell(new Phrase(afeName,
					getBaseFont7()));
			afeCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			afeCapCell.setBorder(0);
			afeCapCell.setPaddingTop(4);
			afeCapCell.setPaddingBottom(4);
			afeCapCell.setPaddingLeft(2);
			afeCapCell.setPaddingRight(2);
			afeCapCell.setBackgroundColor(new BaseColor(175, 206, 243));
			enableCellBorder(afeCapCell);

			PdfPCell timesheetValOuterCell = new PdfPCell(new Phrase("",
					getBaseFont12Bold()));
			timesheetValOuterCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			timesheetValOuterCell.setBorder(0);
			timesheetValOuterCell.setPadding(0);
			enableCellBorder(timesheetValOuterCell);

			DateTime startdt = new DateTime(lundinTimesheetVO
					.getTimesheetBatch().getStartDate());
			DateTime endDt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
					.getEndDate());

			PdfPTable timesheetValTable = createBatchDaysTable(startdt, endDt);
			timesheetValTable.setWidthPercentage(100f);

			float totalByRows = (float) 0.0;
			while (startdt.compareTo(endDt) < 0 || startdt.equals(endDt)) {
				int dayOfMonth = startdt.getDayOfMonth();
				String Key = lundinTimesheetDetail.getLundinBlock()
						.getBlockName()
						+ lundinTimesheetDetail.getLundinAFE().getAfeName()
						+ dayOfMonth;
				String value = dayMonthValueMap.get(Key);
				if (StringUtils.isBlank(value)) {
					value = "";
				} else {
					if (NumberUtils.isNumber(value)) {
						totalByRows += Float.parseFloat(value);
					}

					if (totalByColumnMap.containsKey(dayOfMonth)) {
						String totalByColumn = totalByColumnMap.get(dayOfMonth);
						if (NumberUtils.isNumber(value)) {
							totalByColumn = String.valueOf(Float
									.parseFloat(totalByColumn)
									+ Float.parseFloat(value));
						}
						totalByColumnMap.put(dayOfMonth, totalByColumn);
					} else {
						if (NumberUtils.isNumber(value)) {
							totalByColumnMap.put(dayOfMonth, value);
						}
					}
				}
				PdfPCell timesheetValCell = new PdfPCell(new Phrase(value,
						getBaseFont7()));
				if (holidaysList.contains(startdt.toLocalDate())) {
					timesheetValCell = new PdfPCell(new Phrase(value,
							getBaseFont7Red()));
				}
				timesheetValCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				timesheetValCell.setBorder(0);
				enableCellBorder(timesheetValCell);
				timesheetValCell.setPaddingTop(4);
				timesheetValCell.setPaddingBottom(4);
				timesheetValCell.setPaddingLeft(2);
				timesheetValCell.setPaddingRight(2);
				if (startdt.getDayOfWeek() == 6 || startdt.getDayOfWeek() == 7) {
					timesheetValCell.setBackgroundColor(new BaseColor(191, 191,
							191));
				}
				if (holidaysList.contains(startdt.toLocalDate())) {
					timesheetValCell.setBackgroundColor(new BaseColor(254, 251,
							201));
				}
				if (blockHolidaysList.contains(startdt.toLocalDate())) {
					timesheetValCell.setBackgroundColor(new BaseColor(85, 85,
							85));
				}
				timesheetValTable.addCell(timesheetValCell);
				startdt = startdt.plusDays(1);
			}
			timesheetValOuterCell.addElement(timesheetValTable);

			// total by rows
			PdfPCell totalByRowsCell = new PdfPCell(new Phrase(
					String.valueOf(totalByRows), getBaseFont7()));
			totalByRowsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			totalByRowsCell.setPaddingTop(4);
			totalByRowsCell.setPaddingBottom(4);
			totalByRowsCell.setPaddingLeft(2);
			totalByRowsCell.setPaddingRight(2);
			totalByRowsCell.setBackgroundColor(new BaseColor(228, 246, 230));
			enableCellBorder(totalByRowsCell);

			blockAfeDataTable.addCell(blockNameCell);
			blockAfeDataTable.addCell(afeCapCell);
			blockAfeDataTable.addCell(timesheetValOuterCell);
			blockAfeDataTable.addCell(totalByRowsCell);

			alreadyAttached.add(lundinTimesheetDetail.getLundinBlock()
					.getBlockName()
					+ lundinTimesheetDetail.getLundinAFE().getAfeName());
		}
		blockAfeDataOuterCell.addElement(blockAfeDataTable);

		// Totals By Column
		PdfPTable totalByColTable = new PdfPTable(new float[] { 1f, 3.75f,
				0.25f });
		totalByColTable.setWidthPercentage(110f);

		PdfPCell totalPerDayCapCell = new PdfPCell(new Phrase("Totals per day",
				getBaseFont7WhiteBold()));
		totalPerDayCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		totalPerDayCapCell.setBorder(0);
		totalPerDayCapCell.setPaddingTop(4);
		totalPerDayCapCell.setPaddingBottom(4);
		totalPerDayCapCell.setPaddingLeft(2);
		totalPerDayCapCell.setPaddingRight(2);
		totalPerDayCapCell.setBackgroundColor(new BaseColor(0, 112, 192));
		enableCellBorder(totalPerDayCapCell);

		PdfPCell totalByColOuterCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		totalByColOuterCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		totalByColOuterCell.setBorder(0);
		totalByColOuterCell.setPadding(0);
		enableCellBorder(totalByColOuterCell);

		DateTime startdt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getStartDate());
		DateTime endDt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getEndDate());

		PdfPTable totalByColInnerTable = createBatchDaysTable(startdt, endDt);
		totalByColInnerTable.setWidthPercentage(100f);

		float totalByColumns = (float) 0.0;
		while (startdt.compareTo(endDt) < 0 || startdt.equals(endDt)) {
			int dayOfMonth = startdt.getDayOfMonth();
			String floatValue = totalByColumnMap.get(dayOfMonth);
			String value = "";
			if (floatValue == null) {
				floatValue = "0";
				value = "0";
			} else {
				if (NumberUtils.isNumber(floatValue)) {
					totalByColumns += Float.parseFloat(floatValue);
				}
				value = String.valueOf(floatValue);
			}

			PdfPCell totalByColValCell = new PdfPCell(new Phrase(value,
					getBaseFont7()));
			totalByColValCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			totalByColValCell.setBorder(0);
			totalByColValCell.setFixedHeight(totalByColInnerTable
					.calculateHeights());
			enableCellBorder(totalByColValCell);
			totalByColValCell.setPaddingTop(4);
			totalByColValCell.setPaddingBottom(4);
			totalByColValCell.setPaddingLeft(2);
			totalByColValCell.setPaddingRight(2);
			totalByColValCell.setBackgroundColor(new BaseColor(240, 240, 180));
			totalByColInnerTable.addCell(totalByColValCell);
			startdt = startdt.plusDays(1);
		}
		totalByColOuterCell.addElement(totalByColInnerTable);

		PdfPCell finalTotalByColCell = new PdfPCell(new Phrase(
				String.valueOf(totalByColumns), getBaseFont7()));
		finalTotalByColCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		finalTotalByColCell.setPaddingTop(4);
		finalTotalByColCell.setPaddingBottom(4);
		finalTotalByColCell.setPaddingLeft(2);
		finalTotalByColCell.setPaddingRight(2);
		finalTotalByColCell.setBackgroundColor(new BaseColor(240, 240, 180));
		enableCellBorder(finalTotalByColCell);

		totalByColTable.addCell(totalPerDayCapCell);
		totalByColTable.addCell(totalByColOuterCell);
		totalByColTable.addCell(finalTotalByColCell);

		blockAfeDataOuterCell.addElement(totalByColTable);

		// remarks
		PdfPTable remarksTable = getRemarksTable(lundinTimesheetVO);
		blockAfeDataOuterCell.addElement(remarksTable);

		return blockAfeDataOuterCell;
	}

	private PdfPTable getRemarksTable(
			EmployeeTimesheetApplication lundinTimesheetVO) {
		PdfPTable remarksTable = new PdfPTable(new float[] { 1f, 3.75f, 0.25f });
		remarksTable.setWidthPercentage(110f);

		PdfPCell remarksCapCell = new PdfPCell(new Phrase("Remarks",
				getBaseFont7WhiteBold()));
		remarksCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		remarksCapCell.setVerticalAlignment(Element.ALIGN_CENTER);
		remarksCapCell.setBorder(0);
		remarksCapCell.setFixedHeight(35f);
		remarksCapCell.setBackgroundColor(new BaseColor(0, 112, 192));
		enableCellBorder(remarksCapCell);

		PdfPCell remarksColCell = new PdfPCell(new Phrase(
				lundinTimesheetVO.getRemarks(), getBaseFontNormal()));
		remarksColCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		remarksColCell.setBorder(0);
		remarksCapCell.setFixedHeight(35f);
		enableCellBorder(remarksColCell);

		PdfPCell tempColCell = new PdfPCell(new Phrase("", getBaseFont12Bold()));
		tempColCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		tempColCell.setBorder(0);
		remarksCapCell.setFixedHeight(35f);
		enableCellBorder(tempColCell);

		remarksTable.addCell(remarksCapCell);
		remarksTable.addCell(remarksColCell);
		remarksTable.addCell(tempColCell);
		return remarksTable;
	}

	private void getEmployeeTimesheetDetails(
			EmployeeTimesheetApplication lundinTimesheetVO,
			List<LundinTimesheetDetail> lundinTsheetDetails,
			Map<String, String> dayMonthValueMap) {
		List<Long> alreadyAttached = new ArrayList<Long>();
		for (int i = 0; i < lundinTsheetDetails.size(); i++) {
			LundinTimesheetDetail lundinTimesheetDetail = lundinTsheetDetails
					.get(i);
			Long afeId = lundinTimesheetDetail.getLundinAFE().getAfeId();
			Long blockId = lundinTimesheetDetail.getLundinBlock().getBlockId();
			if (alreadyAttached.contains(lundinTimesheetDetail
					.getTimesheetDetailID())) {
				continue;
			}
			List<LundinTimesheetDetail> internalAttachments = lundinTimesheetDetailDAO
					.findByBlockAndAfe(blockId, afeId,
							lundinTimesheetVO.getTimesheetId());
			for (LundinTimesheetDetail details : internalAttachments) {
				DateTime timesheetDate = new DateTime(
						details.getTimesheetDate());
				String Key = lundinTimesheetDetail.getLundinBlock()
						.getBlockName()
						+ lundinTimesheetDetail.getLundinAFE().getAfeName()
						+ timesheetDate.getDayOfMonth();
				dayMonthValueMap.put(Key, details.getValue().getCodeValue());
				alreadyAttached.add(details.getTimesheetDetailID());
			}
		}
	}

	private PdfPCell getTimesheetDateHeaderColumns(
			EmployeeTimesheetApplication lundinTimesheetVO,
			List<LocalDate> holidaysList, List<LocalDate> blockHolidaysList) {
		PdfPCell dateOuterCell = new PdfPCell(new Phrase("",
				getBaseFont12Bold()));
		dateOuterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		dateOuterCell.setBorder(0);
		dateOuterCell.setPadding(0);

		DateTime startdt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getStartDate());
		DateTime endDt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getEndDate());

		PdfPTable dateTable = createBatchDaysTable(startdt, endDt);
		dateTable.setWidthPercentage(100f);

		while (startdt.compareTo(endDt) < 0 || startdt.equals(endDt)) {
			PdfPCell dateCapCell = new PdfPCell(
					new Phrase(String.valueOf(startdt.getDayOfMonth()),
							getBaseFont7Bold()));
			if (holidaysList.contains(startdt.toLocalDate())) {
				dateCapCell = new PdfPCell(new Phrase(String.valueOf(startdt
						.getDayOfMonth()), getBaseFont7BoldRed()));
			}

			dateCapCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dateCapCell.setBorder(0);
			enableCellBorder(dateCapCell);
			dateCapCell.setPaddingTop(4);
			dateCapCell.setPaddingBottom(4);
			dateCapCell.setPaddingLeft(0);
			dateCapCell.setPaddingRight(0);
			if (startdt.getDayOfWeek() == 6 || startdt.getDayOfWeek() == 7) {
				dateCapCell.setBackgroundColor(new BaseColor(191, 191, 191));
			} else {
				dateCapCell.setBackgroundColor(new BaseColor(141, 180, 226));
			}

			if (holidaysList.contains(startdt.toLocalDate())) {
				dateCapCell.setBackgroundColor(new BaseColor(254, 251, 201));
			}
			if (blockHolidaysList.contains(startdt.toLocalDate())) {
				dateCapCell.setBackgroundColor(new BaseColor(85, 85, 85));
			}
			dateTable.addCell(dateCapCell);
			startdt = startdt.plusDays(1);
		}
		dateOuterCell.addElement(dateTable);
		return dateOuterCell;
	}

	private PdfPTable getTimesheetDayHeaderColumns(
			EmployeeTimesheetApplication lundinTimesheetVO,
			List<LocalDate> holidaysList, List<LocalDate> blockHolidaysList) {
		PdfPTable dayTable = new PdfPTable(new float[] { 1f, 3.75f, 0.25f });
		dayTable.setWidthPercentage(110f);

		PdfPCell dayCapCell = new PdfPCell(new Phrase("Day",
				getBaseFont7WhiteBold()));
		dayCapCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		dayCapCell.setBorder(0);
		dayCapCell.setPaddingTop(4);
		dayCapCell.setPaddingBottom(4);
		dayCapCell.setPaddingLeft(2);
		dayCapCell.setPaddingRight(2);
		dayCapCell.setBackgroundColor(new BaseColor(0, 112, 192));
		enableCellBorder(dayCapCell);

		PdfPCell dayOuterCell = new PdfPCell(new Phrase("", getBaseFont7()));
		dayOuterCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		dayOuterCell.setBorder(0);
		dayOuterCell.setPadding(0);

		DateTime startdt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getStartDate());
		DateTime endDt = new DateTime(lundinTimesheetVO.getTimesheetBatch()
				.getEndDate());

		PdfPTable dayInnerTable = createBatchDaysTable(startdt, endDt);
		dayInnerTable.setWidthPercentage(100f);

		int totalWorkingDays = 0;
		while (startdt.compareTo(endDt) < 0 || startdt.equals(endDt)) {
			PdfPCell dayValCell = new PdfPCell(
					new Phrase(getWeekDayNames(startdt.getDayOfWeek() - 1),
							getBaseFont7()));
			if (holidaysList.contains(startdt.toLocalDate())) {
				dayValCell = new PdfPCell(new Phrase(
						getWeekDayNames(startdt.getDayOfWeek() - 1),
						getBaseFont7Red()));
			}
			dayValCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dayValCell.setBorder(0);
			enableCellBorder(dayValCell);
			dayValCell.setPaddingTop(4);
			dayValCell.setPaddingBottom(4);
			dayValCell.setPaddingLeft(2);
			if (startdt.getDayOfWeek() == 6 || startdt.getDayOfWeek() == 7) {
				dayValCell.setBackgroundColor(new BaseColor(191, 191, 191));
			}
			if (holidaysList.contains(startdt.toLocalDate())) {
				dayValCell.setBackgroundColor(new BaseColor(254, 251, 201));
			}
			if (blockHolidaysList.contains(startdt.toLocalDate())) {
				dayValCell.setBackgroundColor(new BaseColor(85, 85, 85));
			}
			dayValCell.setPaddingRight(2);
			dayInnerTable.addCell(dayValCell);

			if (startdt.getDayOfWeek() != 6 && startdt.getDayOfWeek() != 7
					&& !holidaysList.contains(startdt.toLocalDate())) {
				totalWorkingDays = totalWorkingDays + 1;
			}

			startdt = startdt.plusDays(1);
		}
		dayOuterCell.addElement(dayInnerTable);

		PdfPCell totalWorkingDaysCell = new PdfPCell(new Phrase(
				String.valueOf(totalWorkingDays), getBaseFont7()));
		totalWorkingDaysCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		totalWorkingDaysCell.setBorder(0);
		totalWorkingDaysCell.setPaddingTop(4);
		totalWorkingDaysCell.setPaddingBottom(4);
		totalWorkingDaysCell.setPaddingLeft(2);
		totalWorkingDaysCell.setPaddingRight(2);
		enableCellBorder(totalWorkingDaysCell);

		dayTable.addCell(dayCapCell);
		dayTable.addCell(dayOuterCell);
		dayTable.addCell(totalWorkingDaysCell);
		return dayTable;
	}

	private int getCountBatchDays(DateTime startdt, DateTime endDt) {
		int totalDays = 0;
		while (startdt.compareTo(endDt) < 0 || startdt.equals(endDt)) {
			totalDays += 1;
			startdt = startdt.plusDays(1);
		}
		return totalDays;
	}

	private PdfPTable createBatchDaysTable(DateTime startdt, DateTime endDt) {
		int totalDays = getCountBatchDays(startdt, endDt);
		PdfPTable dateTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f, 1f,
				1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
				1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f });
		if (totalDays == 28) {
			dateTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f });
		} else if (totalDays == 29) {
			dateTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f, 1f });
		} else if (totalDays == 30) {
			dateTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f });
		} else if (totalDays == 31) {
			dateTable = new PdfPTable(new float[] { 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,
					1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f });
		}
		return dateTable;
	}

	private PdfPTable createWorkflowHistoryTable(
			EmployeeTimesheetApplication lundinTimesheetVO) {
		PdfPTable workflowySectionTable = new PdfPTable(new float[] { 1f });
		workflowySectionTable.setWidthPercentage(110f);
		workflowySectionTable.getTotalWidth();

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
		if (lundinTimesheetVO.getTimesheetStatusMaster()
				.getTimesheetStatusName()
				.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_WITHDRAWN)) {
			userStatus = lundinTimesheetVO.getTimesheetStatusMaster()
					.getTimesheetStatusName();
		} else {
			userStatus = "Submitted";
		}

		HashMap<Long, TimesheetApplicationWorkflow> workFlow = new HashMap<>();
		for (TimesheetApplicationWorkflow claimApplicationWorkflow : lundinTimesheetVO
				.getTimesheetApplicationWorkflows()) {

			workFlow.put(claimApplicationWorkflow.getCreatedBy()
					.getEmployeeId(), claimApplicationWorkflow);

		}

		TimesheetApplicationWorkflow userWorkflow = workFlow
				.get(lundinTimesheetVO.getEmployee().getEmployeeId());
		PdfPCell userCell = createWorkFlowSectionTableData("1", "User",
				getEmployeeNameWithEmpNumber(lundinTimesheetVO.getEmployee()),
				lundinTimesheetVO.getRemarks(), userStatus,
				DateUtils.timeStampToStringWithTime(userWorkflow
						.getCreatedDate()), getBaseFont7());
		PDFUtils.setDefaultCellLayout(userCell);
		userCell.enableBorderSide(Rectangle.LEFT);
		userCell.enableBorderSide(Rectangle.RIGHT);

		workflowySectionTable.addCell(workflowyTableHeaderCell);
		workflowySectionTable.addCell(pdfCell1);

		List<TimesheetApplicationReviewer> claimApplicationReviewers = new ArrayList<>(
				lundinTimesheetVO.getTimesheetApplicationReviewers());
		Collections.sort(claimApplicationReviewers, new timesheetRevComp());

		if (claimApplicationReviewers.size() == 0) {
			userCell.enableBorderSide(Rectangle.BOTTOM);
		}
		workflowySectionTable.addCell(userCell);

		Integer snoCount = 2;
		Integer revCount = 1;

		String claimRev1Status = "";
		String claimRev2Status = "";
		String claimRev3Status = "";
		int tempRevCount = 1;

		for (TimesheetApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			TimesheetApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(claimApplicationReviewer.getEmployee().getEmployeeId());
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

		int claimAppRevListSize = claimApplicationReviewers.size();
		for (TimesheetApplicationReviewer claimApplicationReviewer : claimApplicationReviewers) {
			TimesheetApplicationWorkflow claimApplicationWorkflow = workFlow
					.get(claimApplicationReviewer.getEmployee().getEmployeeId());
			String remarks = "";
			String status = "";
			String claimDate = "";
			if (claimApplicationWorkflow != null) {
				remarks = claimApplicationWorkflow.getRemarks();
				status = claimApplicationWorkflow.getTimesheetStatusMaster()
						.getTimesheetStatusName();
				claimDate = DateUtils
						.timeStampToStringWithTime(claimApplicationWorkflow
								.getCreatedDate());
			}
			if (!lundinTimesheetVO.getTimesheetStatusMaster()
					.getTimesheetStatusName()
					.equalsIgnoreCase(PayAsiaConstants.LUNDIN_STATUS_WITHDRAWN)) {
				if (revCount == 1) {
					if (StringUtils.isBlank(claimRev1Status)) {
						status = "Pending";
					}
				}
				if (revCount == 2) {
					if (StringUtils.isBlank(claimRev2Status)) {
						if (StringUtils.isBlank(claimRev1Status)) {
							status = "Pending";
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
							status = "Pending";
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
					snoCount.toString(), "Reviewer" + revCount,
					getEmployeeNameWithEmpNumber(claimApplicationReviewer
							.getEmployee()), remarks, status, claimDate,
					getBaseFont7());
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

	private void enableCellBorder(PdfPCell pdfPCell) {
		pdfPCell.enableBorderSide(Rectangle.LEFT);
		pdfPCell.enableBorderSide(Rectangle.RIGHT);
		pdfPCell.enableBorderSide(Rectangle.TOP);
		pdfPCell.enableBorderSide(Rectangle.BOTTOM);
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
		Font unicodeFont = new Font(bf, 7, Font.NORMAL, BaseColor.WHITE);
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
		Font unicodeFont = new Font(bf, 10, Font.NORMAL, BaseColor.WHITE);
		return unicodeFont;

	}

	private static String getWeekDayNames(int day) {
		String[] weekDayNames = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
				"Sun" };
		return weekDayNames[day];
	}

	private static String getMonthName(int month) {
		String[] monthNames = { "January", "February", "March", "April", "May",
				"June", "July", "August", "September", "October", "November",
				"December" };
		return monthNames[month];
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

	public String getEmployeeNameWithEmpNumber(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getMiddleName())) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		employeeName = employeeName + " (" + employee.getEmployeeNumber() + ")";
		return employeeName;
	}

	private PdfPCell tempCell() {
		PdfPCell tempCell = new PdfPCell(new Phrase("", getBaseFontNormal()));
		tempCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		tempCell.setBorder(0);
		return tempCell;
	}

	@Override
	public List<LundinTimesheetDTO> generateTimesheets(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			Long employeeId, Long companyId) {
		Timestamp fromBatchdate = null;
		Timestamp toBatchDate = null;

		// Get Employee Department
		Map<Long, String> empIdDeptMap = getEmployeeDepartmentList(
				lundinTimesheetReportsForm, companyId, employeeId);
		List<Long> employeeIDs = new ArrayList<>();
		List<LundinTimesheetDTO> lundinTimesheetDTOs = new ArrayList<>();

		TimesheetBatch lundinTimesheetBatch = lundinTimesheetBatchDAO
				.findById(lundinTimesheetReportsForm.getFromBatchId());
		if (lundinTimesheetBatch != null) {
			fromBatchdate = lundinTimesheetBatch.getStartDate();

		}
		TimesheetBatch lundinTimesheetB = lundinTimesheetBatchDAO
				.findById(lundinTimesheetReportsForm.getToBatchId());
		if (lundinTimesheetB != null) {
			toBatchDate = lundinTimesheetB.getEndDate();

		}

		List<String> employeeIdList = Arrays.asList(lundinTimesheetReportsForm
				.getEmployeeIds().split("\\s*,\\s*"));
		for (String employeeID : employeeIdList) {
			if (StringUtils.isNotBlank(employeeID)) {
				employeeIDs.add(Long.valueOf(employeeID));
			}
		}
		List<String> timesheetStatus = new ArrayList<>();
		timesheetStatus.add(PayAsiaConstants.LUNDIN_STATUS_COMPLETED);
		List<EmployeeTimesheetApplication> lundinTimesheets = lundinTimesheetDAO
				.findTimesheetDetailsByDate(fromBatchdate, toBatchDate,
						employeeIDs, companyId, timesheetStatus);
		for (EmployeeTimesheetApplication lundinTimesheet : lundinTimesheets) {
			LundinTimesheetDTO lundinTimesheetDTO = new LundinTimesheetDTO();
			lundinTimesheetDTO.setEmployeeId(lundinTimesheet.getEmployee()
					.getEmployeeId());
			lundinTimesheetDTO.setEmployeeNumber(lundinTimesheet.getEmployee()
					.getEmployeeNumber());
			lundinTimesheetDTO.setEmployeeName(getEmployeeName(lundinTimesheet
					.getEmployee()));
			if (!empIdDeptMap.isEmpty() && empIdDeptMap != null) {
				if (empIdDeptMap.get(lundinTimesheet.getEmployee()
						.getEmployeeId()) != null) {
					lundinTimesheetDTO
							.setDepartmentCode(empIdDeptMap.get(lundinTimesheet
									.getEmployee().getEmployeeId()));
				} else {
					lundinTimesheetDTO
							.setDepartmentCode("No Department Assigned");
				}
			} else {
				lundinTimesheetDTO.setDepartmentCode("No Department Assigned");
			}
			lundinTimesheetDTO.setTimesheetId(lundinTimesheet.getTimesheetId());
			lundinTimesheetDTOs.add(lundinTimesheetDTO);
		}

		if (StringUtils.isNotBlank(lundinTimesheetReportsForm.getSortBy())
				&& lundinTimesheetReportsForm.getSortBy().equalsIgnoreCase(
						"empNumber")) {

			Collections.sort(lundinTimesheetDTOs,
					new Comparator<LundinTimesheetDTO>() {

						@Override
						public int compare(LundinTimesheetDTO o1,
								LundinTimesheetDTO o2) {
							return o1.getEmployeeNumber().compareTo(
									o2.getEmployeeNumber());
						}
					});
		} else if (StringUtils.isNotBlank(lundinTimesheetReportsForm
				.getSortBy())
				&& lundinTimesheetReportsForm.getSortBy().equalsIgnoreCase(
						"empName")) {
			Collections.sort(lundinTimesheetDTOs,
					new Comparator<LundinTimesheetDTO>() {

						@Override
						public int compare(LundinTimesheetDTO o1,
								LundinTimesheetDTO o2) {
							return o1.getEmployeeName().compareTo(
									o2.getEmployeeName());
						}
					});
		} else if (StringUtils.isNotBlank(lundinTimesheetReportsForm
				.getSortBy())
				&& lundinTimesheetReportsForm.getSortBy().equalsIgnoreCase(
						"empDept")) {
			// //sort by department////
			Collections.sort(lundinTimesheetDTOs,
					new LundinDeptAndEmpNameComp());
		}

		return lundinTimesheetDTOs;
	}

	private class LundinDeptAndEmpNameComp implements
			Comparator<LundinTimesheetDTO> {
		public int compare(LundinTimesheetDTO templateField,
				LundinTimesheetDTO compWithTemplateField) {
			String x1 = ((LundinTimesheetDTO) templateField)
					.getDepartmentCode();
			String x2 = ((LundinTimesheetDTO) compWithTemplateField)
					.getDepartmentCode();
			int sComp = x1.compareTo(x2);

			if (sComp != 0) {
				return sComp;
			} else {
				String x3 = ((LundinTimesheetDTO) templateField)
						.getEmployeeName();
				String x4 = ((LundinTimesheetDTO) compWithTemplateField)
						.getEmployeeName();
				return x3.compareTo(x4);
			}
		}
	}

	private Map<Long, String> getEmployeeDepartmentList(
			LundinTimesheetReportsForm lundinTimesheetReportsForm,
			Long companyId, Long employeeId) {
		Map<Long, String> empIdDeptMap = new HashMap<Long, String>();
		Company companyVO = companyDAO.findById(companyId);

		List<LundinDepartment> lundinDepartmentVOList = new ArrayList<LundinDepartment>();
		if (lundinTimesheetReportsForm.getDepartmentId() != 0) {
			LundinDepartment lundinDepartment = lundinDepartmentDAO
					.findById(lundinTimesheetReportsForm.getDepartmentId());
			if (lundinDepartment != null) {
				lundinDepartmentVOList.add(lundinDepartment);
			}
		} else {
			lundinDepartmentVOList = lundinDepartmentDAO
					.findByCondition(companyId);
		}

		if (!lundinDepartmentVOList.isEmpty() && lundinDepartmentVOList != null) {
			// Get All Employees By company
			Map<String, Long> empNumEmpIdMap = new HashMap<String, Long>();
			List<Employee> employeeIdVOList = employeeDAO
					.findByCompany(companyId);
			for (Employee employee : employeeIdVOList) {
				empNumEmpIdMap.put(employee.getEmployeeNumber(),
						employee.getEmployeeId());
			}

			for (LundinDepartment lundinDepartment : lundinDepartmentVOList) {
				String deptCode = lundinDepartment
						.getDynamicFormFieldRefValue().getCode();
				List<Object[]> deptEmployeeObjList = lundinTimesheetReportsLogic
						.getTimesheetStatusDepartmentEmpList(companyId,
								companyVO.getDateFormat(), employeeId,
								lundinDepartment.getDynamicFormFieldRefValue()
										.getDataDictionary());
				// Get Department Of Employees from Employee Information
				// Details
				for (Object[] deptObject : deptEmployeeObjList) {
					if (deptObject != null
							&& deptObject[2] != null
							&& deptObject[0] != null
							&& String.valueOf(deptObject[0]).equalsIgnoreCase(
									deptCode)) {
						LundinTimesheetStatusReportDTO lundinTimesheetStatusReportDTO = new LundinTimesheetStatusReportDTO();
						lundinTimesheetStatusReportDTO.setEmployeeNumber(String
								.valueOf(deptObject[2]));
						lundinTimesheetStatusReportDTO
								.setEmployeeId(empNumEmpIdMap.get(String
										.valueOf(deptObject[2])));
						lundinTimesheetStatusReportDTO.setDepartmentCode(String
								.valueOf(deptObject[0]));
						lundinTimesheetStatusReportDTO.setDepartmentDesc(String
								.valueOf(deptObject[1]));
						empIdDeptMap.put(empNumEmpIdMap.get(String
								.valueOf(deptObject[2])), String
								.valueOf(deptObject[0]));
					}
				}
			}
		}
		return empIdDeptMap;
	}
}
