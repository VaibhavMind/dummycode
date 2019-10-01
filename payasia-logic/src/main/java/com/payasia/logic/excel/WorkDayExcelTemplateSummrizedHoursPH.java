package com.payasia.logic.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mind.payasia.xml.bean.workday.paydata.EmployeeType;
import com.mind.payasia.xml.bean.workday.paydata.SummaryType;
import com.mind.payasia.xml.bean.workday.paydata.TimeOffCorrectionType;
import com.mind.payasia.xml.bean.workday.paydata.TimeOffType;
import com.mind.payasia.xml.bean.workday.paydata.TimeTrackingCorrectionType;
import com.mind.payasia.xml.bean.workday.paydata.TimeTrackingType;
import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.common.dto.WorkDaySummrizedHoursDTO;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public class WorkDayExcelTemplateSummrizedHoursPH extends WorkDayExcelTemplate {

	@Override
	public XSSFWorkbook generateReport(List<WorkDayReportDTO> workDayReportDTOList) {
		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = createSheetForSummrizedHours(workbook, workDayReportDTOList);

		int rowValue = 1;
		for (WorkDayReportDTO workDayReportDTO : workDayReportDTOList) {
			XSSFRow row = sheet.createRow((short) rowValue);
			setDataForSummrizedHours(workbook, sheet, row, (WorkDaySummrizedHoursDTO) workDayReportDTO);
			rowValue++;
		}

		return workbook;
	}

	@Override
	public List<WorkDayReportDTO> getReportDataMappedObject(JAXBElement employeeTypeElement,
			WorkdayPaygroupBatchData workdayPaygroupBatch) {
		try {
			Map<String, WorkDaySummrizedHoursDTO> workDaySummrizedHoursDTOMap = new HashMap<String, WorkDaySummrizedHoursDTO>();

			EmployeeType employeeType = (EmployeeType) employeeTypeElement.getValue();

			if (employeeType != null) {
				SummaryType summaryType = employeeType.getSummary();
				WorkDaySummrizedHoursDTO workDaySummrizedHoursDTO = null;
				String employeeID = "";
				if (summaryType != null) {
					employeeID = summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue();
				}
								
				List<TimeOffType> timeOffTypeList = employeeType.getTimeOff();
				if (timeOffTypeList != null && !timeOffTypeList.isEmpty()) {
					for (TimeOffType timeOffType : timeOffTypeList) {
						workDaySummrizedHoursDTO = new WorkDaySummrizedHoursDTO();
						workDaySummrizedHoursDTO.setEmployeeID(employeeID);
						String code = timeOffType.getCode() == null ? ""
								: timeOffType.getCode().getValue().getValue();
						workDaySummrizedHoursDTO.setType(code);
						workDaySummrizedHoursDTO.setNoOfHours(timeOffType.getQuantity() == null ? ""
								: timeOffType.getQuantity().getValue().getValue());
						workDaySummrizedHoursDTO
								.setDate("" + workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate());
						workDaySummrizedHoursDTOMap.put("" + employeeID + code, workDaySummrizedHoursDTO);
					}
				}

				List<TimeOffCorrectionType> timeOffCorrectionTypeList = employeeType.getTimeOffCorrection();
				if (timeOffCorrectionTypeList != null && !timeOffCorrectionTypeList.isEmpty()) {
					for (TimeOffCorrectionType timeOffCorrectionType : timeOffCorrectionTypeList) {
						String code = timeOffCorrectionType.getCode() == null ? ""
								: timeOffCorrectionType.getCode().getValue().getValue();
						if (workDaySummrizedHoursDTOMap.containsKey(employeeID + code)) {
							workDaySummrizedHoursDTO = workDaySummrizedHoursDTOMap.get(employeeID + code);
							String oldUnits = workDaySummrizedHoursDTO.getNoOfHours();
							String newUnits = timeOffCorrectionType.getQuantity() == null ? ""
									: timeOffCorrectionType.getQuantity().getValue().getValue();
							Double calValue = Double.valueOf(oldUnits) + Double.valueOf(newUnits);
							workDaySummrizedHoursDTO.setNoOfHours("" + calValue);
						} else {
							workDaySummrizedHoursDTO = new WorkDaySummrizedHoursDTO();
							workDaySummrizedHoursDTO.setEmployeeID(employeeID);
							workDaySummrizedHoursDTO.setType(code);
							workDaySummrizedHoursDTO.setNoOfHours(timeOffCorrectionType.getQuantity() == null ? ""
									: timeOffCorrectionType.getQuantity().getValue().getValue());
							workDaySummrizedHoursDTO.setDate(
									"" + workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate());
							workDaySummrizedHoursDTOMap.put("" + employeeID + code, workDaySummrizedHoursDTO);
						}
					}
				}
				
				List<TimeTrackingType> timeTrackingList = employeeType.getTimeTracking();
				if (timeTrackingList != null && !timeTrackingList.isEmpty()) {
					for (TimeTrackingType timeTrackingType : timeTrackingList) {
						workDaySummrizedHoursDTO = new WorkDaySummrizedHoursDTO();
						workDaySummrizedHoursDTO.setEmployeeID(employeeID);
						String code = timeTrackingType.getCode() == null ? ""
								: timeTrackingType.getCode().getValue().getValue();
						workDaySummrizedHoursDTO.setType(code);
						workDaySummrizedHoursDTO.setNoOfHours(timeTrackingType.getQuantity() == null ? ""
								: timeTrackingType.getQuantity().getValue().getValue());
						workDaySummrizedHoursDTO
								.setDate("" + workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate());
						workDaySummrizedHoursDTOMap.put("" + employeeID + code, workDaySummrizedHoursDTO);
					}
				}

				List<TimeTrackingCorrectionType> timeTrackingCorrectionList = employeeType.getTimeTrackingCorrection();
				if (timeTrackingCorrectionList != null && !timeTrackingCorrectionList.isEmpty()) {
					for (TimeTrackingCorrectionType timeTrackingCorrectionType : timeTrackingCorrectionList) {
						String code = timeTrackingCorrectionType.getCode() == null ? ""
								: timeTrackingCorrectionType.getCode().getValue().getValue();
						if (workDaySummrizedHoursDTOMap.containsKey(employeeID + code)) {
							workDaySummrizedHoursDTO = workDaySummrizedHoursDTOMap.get(employeeID + code);
							String oldUnits = workDaySummrizedHoursDTO.getNoOfHours();
							String newUnits = timeTrackingCorrectionType.getQuantity() == null ? ""
									: timeTrackingCorrectionType.getQuantity().getValue().getValue();
							Double calValue = Double.valueOf(oldUnits) + Double.valueOf(newUnits);
							workDaySummrizedHoursDTO.setNoOfHours("" + calValue);
						} else {
							workDaySummrizedHoursDTO = new WorkDaySummrizedHoursDTO();
							workDaySummrizedHoursDTO.setEmployeeID(employeeID);
							workDaySummrizedHoursDTO.setType(code);
							workDaySummrizedHoursDTO.setNoOfHours(timeTrackingCorrectionType.getQuantity() == null ? ""
									: timeTrackingCorrectionType.getQuantity().getValue().getValue());
							workDaySummrizedHoursDTO.setDate(
									"" + workdayPaygroupBatch.getWorkdayPaygroupBatch().getPayPeriodStartDate());
							workDaySummrizedHoursDTOMap.put("" + employeeID + code, workDaySummrizedHoursDTO);
						}
					}
				}
			}
			List<WorkDayReportDTO> workDayReportDTOList = null;
			if(workDaySummrizedHoursDTOMap!=null && !workDaySummrizedHoursDTOMap.isEmpty()){
				workDayReportDTOList = new ArrayList<WorkDayReportDTO>();
				for (Map.Entry<String, WorkDaySummrizedHoursDTO> workDaySummrizedHoursDTO : workDaySummrizedHoursDTOMap.entrySet()) {
					workDayReportDTOList.add(workDaySummrizedHoursDTO.getValue());
				}
			}
			return workDayReportDTOList;
		}catch (Exception e) {
		}
		return null;
	}

	private XSSFSheet createSheetForSummrizedHours(XSSFWorkbook workbook, List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFSheet sheet = workbook.createSheet("Summrized Hours");

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(76, 183, 76)));
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);

		XSSFRow row0 = sheet.createRow((short) 0);
		row0.setHeightInPoints((short) 20);

		XSSFCell cell0A = (XSSFCell) row0.createCell((short) 0);
		cell0A.setCellValue("Employee ID");
		cell0A.setCellStyle(style);

		sheet.setColumnWidth(0, 7000);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue("Type");
		cell0B.setCellStyle(style);

		sheet.setColumnWidth(1, 15000);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue("No of Hours");
		cell0C.setCellStyle(style);

		sheet.setColumnWidth(2, 7000);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue("Date");
		cell0D.setCellStyle(style);

		sheet.setColumnWidth(3, 7000);

		return sheet;
	}

	private XSSFSheet setDataForSummrizedHours(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row0,
			WorkDaySummrizedHoursDTO workDaySummrizedHoursDTO) {

		XSSFFont font = workbook.createFont();
		font.setFontName("Calibri");
		font.setItalic(false);
		font.setColor(HSSFColor.BLACK.index);
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);

		XSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);

		row0.setHeightInPoints((short) 20);

		XSSFCell cell0A = (XSSFCell) row0.createCell((short) 0);
		cell0A.setCellValue(workDaySummrizedHoursDTO.getEmployeeID());
		cell0A.setCellStyle(style);

		sheet.setColumnWidth(0, 7000);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue(workDaySummrizedHoursDTO.getType());
		cell0B.setCellStyle(style);

		sheet.setColumnWidth(1, 15000);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue(workDaySummrizedHoursDTO.getNoOfHours());
		cell0C.setCellStyle(style);

		sheet.setColumnWidth(2, 7000);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue(workDaySummrizedHoursDTO.getDate());
		cell0D.setCellStyle(style);

		sheet.setColumnWidth(3, 7000);

		return sheet;
	}

}
