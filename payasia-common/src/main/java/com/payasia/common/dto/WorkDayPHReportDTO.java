package com.payasia.common.dto;

public class WorkDayPHReportDTO extends WorkDayReportDTO{
	
	private NewHiresPHReportDTO newHiresPHReport;
	private EmeeDataChangesPHReportDTO emeeDataChangesPHReport;
	private ResignationsPHReportDTO resignationsPHReport;
	private CareerProgressionPHReportDTO careerProgressionPHReport;
	private BasicRateProgressionPHReportDTO basicRateProgressionPHReport;
	private PayItemsRecurringPHReportDTO payItemsRecurringPHReportDTO;
	
	public PayItemsRecurringPHReportDTO getPayItemsRecurringPHReportDTO() {
		return payItemsRecurringPHReportDTO;
	}

	public void setPayItemsRecurringPHReportDTO(PayItemsRecurringPHReportDTO payItemsRecurringPHReportDTO) {
		this.payItemsRecurringPHReportDTO = payItemsRecurringPHReportDTO;
	}

	public BasicRateProgressionPHReportDTO getBasicRateProgressionPHReport() {
		return basicRateProgressionPHReport;
	}

	public void setBasicRateProgressionPHReport(BasicRateProgressionPHReportDTO basicRateProgressionPHReport) {
		this.basicRateProgressionPHReport = basicRateProgressionPHReport;
	}

	public CareerProgressionPHReportDTO getCareerProgressionPHReport() {
		return careerProgressionPHReport;
	}

	public void setCareerProgressionPHReport(CareerProgressionPHReportDTO careerProgressionPHReport) {
		this.careerProgressionPHReport = careerProgressionPHReport;
	}

	public NewHiresPHReportDTO getNewHiresPHReport() {
		return newHiresPHReport;
	}

	public void setNewHiresPHReport(NewHiresPHReportDTO newHiresPHReport) {
		this.newHiresPHReport = newHiresPHReport;
	}

	public EmeeDataChangesPHReportDTO getEmeeDataChangesPHReport() {
		return emeeDataChangesPHReport;
	}

	public void setEmeeDataChangesPHReport(EmeeDataChangesPHReportDTO emeeDataChangesPHReport) {
		this.emeeDataChangesPHReport = emeeDataChangesPHReport;
	}

	public ResignationsPHReportDTO getResignationsPHReport() {
		return resignationsPHReport;
	}

	public void setResignationsPHReport(ResignationsPHReportDTO resignationsPHReport) {
		this.resignationsPHReport = resignationsPHReport;
	}
}
