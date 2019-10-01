package com.payasia.logic.excel;

public abstract class WorkDayExcelTemplateFactory {

	public static WorkDayExcelTemplate instantiate(String name) {

		WorkDayExcelTemplate excelLogic = null;

		switch (name) {
		case "WorkDayExcelTemplateNewUploadAU":
			excelLogic = new WorkDayExcelTemplateNewUploadAU();
			break;
		case "WorkDayExcelTemplateTimeSheetAU":
			excelLogic = new WorkDayExcelTemplateTimeSheetAU();
			break;
		case "WorkDayExcelTemplatePayrollPreparationAU":
			excelLogic = new WorkDayExcelTemplatePayrollPreparationAU();
			break;
		case "WorkDayExcelTemplateMovementPH":
			excelLogic = new WorkDayExcelTemplateMovementPH();
			break;
		case "WorkDayExcelTemplateSummrizedHoursPH":
			excelLogic = new WorkDayExcelTemplateSummrizedHoursPH();
			break;
		case "WorkDayExcelTemplateNewStarterUK":
			excelLogic = new WorkDayExcelTemplateNewStarterUK();
			break;
			
		}
		return excelLogic;
	}

}
