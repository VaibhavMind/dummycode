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
import com.mind.payasia.xml.bean.workday.paydata.TimeTrackingCorrectionType;
import com.mind.payasia.xml.bean.workday.paydata.TimeTrackingType;
import com.payasia.common.dto.WorkDayReportDTO;
import com.payasia.common.dto.WorkdayTimeSheetDTO;
import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public class WorkDayExcelTemplateTimeSheetAU extends WorkDayExcelTemplate {

	@Override
	public XSSFWorkbook generateReport(List<WorkDayReportDTO> workDayReportDTOList) {
		XSSFWorkbook workbook = new XSSFWorkbook();

		XSSFSheet sheet = createSheetForTimeSheet(workbook, workDayReportDTOList);

		int rowValue = 1;
		for (WorkDayReportDTO workDayReportDTO : workDayReportDTOList) {
			XSSFRow row = sheet.createRow((short) rowValue);
			setDataForTimeSheet(workbook, sheet, row, (WorkdayTimeSheetDTO) workDayReportDTO);
			rowValue++;
		}
		return workbook;
	}

	@Override
	public List<WorkDayReportDTO> getReportDataMappedObject(JAXBElement employeeTypeElement,
			WorkdayPaygroupBatchData workdayPaygroupBatch) {
		try {
			Map<String, WorkdayTimeSheetDTO> workDayReportDTOMap = new HashMap<String, WorkdayTimeSheetDTO>();

			EmployeeType employeeType = (EmployeeType) employeeTypeElement.getValue();

			if (employeeType != null) {
				SummaryType summaryType = employeeType.getSummary();
				WorkdayTimeSheetDTO workdayTimeSheetDTO = null;
				String employeeID = "";
				String employeeName = "";
				if (summaryType != null) {
					employeeName = summaryType.getName() == null ? "" : summaryType.getName().getValue();
					employeeID = summaryType.getEmployeeID() == null ? "" : summaryType.getEmployeeID().getValue();
				}
				List<TimeTrackingType> timeTrackingList = employeeType.getTimeTracking();
				if (timeTrackingList != null && !timeTrackingList.isEmpty()) {
					for (TimeTrackingType timeTrackingType : timeTrackingList) {
						workdayTimeSheetDTO = new WorkdayTimeSheetDTO();
						workdayTimeSheetDTO.setEmployeeName(employeeName);
						//workdayTimeSheetDTO.setExternalID("");
						workdayTimeSheetDTO.setEmployeeID(employeeID);						
						workdayTimeSheetDTO.setLocationId("218633");
						String code = timeTrackingType.getCode() == null ? ""
								: timeTrackingType.getCode().getValue().getValue();
						workdayTimeSheetDTO.setPayCategoryId(code);
						workdayTimeSheetDTO.setUnits(timeTrackingType.getQuantity() == null ? ""
								: timeTrackingType.getQuantity().getValue().getValue());
						// workdayTimeSheetDTO.setRate("");
						workDayReportDTOMap.put("" + employeeID + code, workdayTimeSheetDTO);
					}
				}
				
				List<TimeTrackingCorrectionType> timeTrackingCorrectionList = employeeType.getTimeTrackingCorrection();
				if (timeTrackingCorrectionList != null && !timeTrackingCorrectionList.isEmpty()) {
					for (TimeTrackingCorrectionType timeTrackingCorrectionType : timeTrackingCorrectionList) {
						String code = timeTrackingCorrectionType.getCode() == null ? ""
								: timeTrackingCorrectionType.getCode().getValue().getValue();
						if (workDayReportDTOMap.containsKey(employeeID + code)) {
							workdayTimeSheetDTO= workDayReportDTOMap.get(employeeID + code);
							String oldUnits=workdayTimeSheetDTO.getUnits();
							String newUnits=timeTrackingCorrectionType.getQuantity() == null ? ""
									: timeTrackingCorrectionType.getQuantity().getValue().getValue();
							Double calValue = Double.valueOf(oldUnits)+ Double.valueOf(newUnits);
							workdayTimeSheetDTO.setUnits(""+calValue);								
						} else {
							workdayTimeSheetDTO = new WorkdayTimeSheetDTO();
							workdayTimeSheetDTO.setEmployeeName(employeeName);
							//workdayTimeSheetDTO.setExternalID("");
							workdayTimeSheetDTO.setEmployeeID(employeeID);							
							workdayTimeSheetDTO.setLocationId("218633");
							workdayTimeSheetDTO.setPayCategoryId(code);
							workdayTimeSheetDTO.setUnits(timeTrackingCorrectionType.getQuantity() == null ? ""
									: timeTrackingCorrectionType.getQuantity().getValue().getValue());
							// workdayTimeSheetDTO.setRate("");
							workDayReportDTOMap.put("" + employeeID + code, workdayTimeSheetDTO);
						}
					}
				}
			}
			List<WorkDayReportDTO> workDayReportDTOList = null;
			if(workDayReportDTOMap!=null && !workDayReportDTOMap.isEmpty()){
				workDayReportDTOList = new ArrayList<WorkDayReportDTO>();
				for (Map.Entry<String, WorkdayTimeSheetDTO> WorkdayTimeSheetDTO : workDayReportDTOMap.entrySet()) {
					workDayReportDTOList.add(WorkdayTimeSheetDTO.getValue());
				}
			}
			return workDayReportDTOList;
		} catch (Exception e) {

		}
		return null;
	}

	private XSSFSheet createSheetForTimeSheet(XSSFWorkbook workbook, List<WorkDayReportDTO> workDayReportDTOList) {

		XSSFSheet sheet = workbook.createSheet("Template for uploading");

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
		cell0A.setCellValue("Employee Name");
		cell0A.setCellStyle(style);

		sheet.setColumnWidth(0, 7000);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue("Employee Id");
		cell0B.setCellStyle(style);

		sheet.setColumnWidth(1, 7000);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue("Employee ID (HROnline)");
		cell0C.setCellStyle(style);

		sheet.setColumnWidth(2, 7000);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue("LocationId");
		cell0D.setCellStyle(style);

		sheet.setColumnWidth(3, 7000);

		XSSFCell cell0E = (XSSFCell) row0.createCell((short) 4);
		cell0E.setCellValue("PayCategoryId");
		cell0E.setCellStyle(style);

		sheet.setColumnWidth(4, 7000);

		XSSFCell cell0F = (XSSFCell) row0.createCell((short) 5);
		cell0F.setCellValue(" Units ");
		cell0F.setCellStyle(style);

		sheet.setColumnWidth(5, 7000);

		XSSFCell cell0G = (XSSFCell) row0.createCell((short) 6);
		cell0G.setCellValue("  Rate  ");
		cell0G.setCellStyle(style);

		sheet.setColumnWidth(6, 7000);

		return sheet;
	}

	private XSSFSheet setDataForTimeSheet(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row0,
			WorkdayTimeSheetDTO workdayTimeSheetDTO) {

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
		cell0A.setCellValue(workdayTimeSheetDTO.getEmployeeName());
		cell0A.setCellStyle(style);

		sheet.setColumnWidth(0, 7000);

		XSSFCell cell0B = (XSSFCell) row0.createCell((short) 1);
		cell0B.setCellValue(workdayTimeSheetDTO.getExternalID());
		cell0B.setCellStyle(style);

		sheet.setColumnWidth(1, 7000);

		XSSFCell cell0C = (XSSFCell) row0.createCell((short) 2);
		cell0C.setCellValue(workdayTimeSheetDTO.getEmployeeID());
		cell0C.setCellStyle(style);

		sheet.setColumnWidth(1, 7000);

		XSSFCell cell0D = (XSSFCell) row0.createCell((short) 3);
		cell0D.setCellValue(workdayTimeSheetDTO.getLocationId());
		cell0D.setCellStyle(style);

		sheet.setColumnWidth(2, 7000);

		XSSFCell cell0E = (XSSFCell) row0.createCell((short) 4);
		cell0E.setCellValue(workdayTimeSheetDTO.getPayCategoryId());
		cell0E.setCellStyle(style);

		sheet.setColumnWidth(3, 7000);

		XSSFCell cell0F = (XSSFCell) row0.createCell((short) 5);
		cell0F.setCellValue(workdayTimeSheetDTO.getUnits());
		cell0F.setCellStyle(style);

		sheet.setColumnWidth(4, 7000);

		XSSFCell cell0G = (XSSFCell) row0.createCell((short) 6);
		cell0G.setCellValue(workdayTimeSheetDTO.getRate());
		cell0G.setCellStyle(style);

		sheet.setColumnWidth(5, 7000);

		return sheet;
	}

}
