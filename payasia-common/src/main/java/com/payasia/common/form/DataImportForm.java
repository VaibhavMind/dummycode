package com.payasia.common.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportHistoryDTO;
import com.payasia.common.dto.DataImportLogDTO;

public class DataImportForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 192530831110608035L;
	private long templateId;
	private long monthId;
	private long entityId;
	private String payslipFrequencyName;
	private long payslipFrequency;
	private int year;
	private int part;
	private String templateName;
	private CommonsMultipartFile fileName;
	private List<DataImportLogDTO> dataImportLogList;
	private List<DataImportHistoryDTO> dataImportHistoryList;
	private boolean templateValid;
	private boolean sameExcelField = false;
	private List<String> colName;
	private List<String> duplicateColNames;
	private List<HashMap<String, String>> importedData;
	private boolean releasePayslipWithImport;
	private boolean addCompanyLogoInPdf;
	private String payslipTextFormat;
	private String companyAddress;
	private String previewPdfEmpNumber;
	private String logoAlignment;
	private boolean fileNameContainsCompNameForPdf;
	private boolean isImportTypeTextPdfFile;
	private DataImportLogDTO dataImportLogSummaryDTO;
	private boolean companyAddressBelowLogo;
	private List<String> employeeNumberList;
	private boolean releasePayslipSchedule;
	private String scheduleDate;
	private String scheduleTime;

	public int getPart() {
		return part;
	}

	public void setPart(int part) {
		this.part = part;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public long getPayslipFrequency() {
		return payslipFrequency;
	}

	public void setPayslipFrequency(long payslipFrequency) {
		this.payslipFrequency = payslipFrequency;
	}

	public long getMonthId() {
		return monthId;
	}

	public void setMonthId(long monthId) {
		this.monthId = monthId;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public List<DataImportHistoryDTO> getDataImportHistoryList() {
		return dataImportHistoryList;
	}

	public void setDataImportHistoryList(
			List<DataImportHistoryDTO> dataImportHistoryList) {
		this.dataImportHistoryList = dataImportHistoryList;
	}

	public List<String> getColName() {
		return colName;
	}

	public void setColName(List<String> colName) {
		this.colName = colName;
	}

	public List<HashMap<String, String>> getImportedData() {
		return importedData;
	}

	public void setImportedData(List<HashMap<String, String>> importedData) {
		this.importedData = importedData;
	}

	public List<DataImportLogDTO> getDataImportLogList() {
		return dataImportLogList;
	}

	public void setDataImportLogList(List<DataImportLogDTO> dataImportLogList) {
		this.dataImportLogList = dataImportLogList;
	}

	public boolean isTemplateValid() {
		return templateValid;
	}

	public void setTemplateValid(boolean templateValid) {
		this.templateValid = templateValid;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public CommonsMultipartFile getFileName() {
		return fileName;
	}

	public void setFileName(CommonsMultipartFile fileName) {
		this.fileName = fileName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getPayslipFrequencyName() {
		return payslipFrequencyName;
	}

	public void setPayslipFrequencyName(String payslipFrequencyName) {
		this.payslipFrequencyName = payslipFrequencyName;
	}

	public boolean isSameExcelField() {
		return sameExcelField;
	}

	public void setSameExcelField(boolean sameExcelField) {
		this.sameExcelField = sameExcelField;
	}

	public List<String> getDuplicateColNames() {
		return duplicateColNames;
	}

	public void setDuplicateColNames(List<String> duplicateColNames) {
		this.duplicateColNames = duplicateColNames;
	}

	public boolean isReleasePayslipWithImport() {
		return releasePayslipWithImport;
	}

	public void setReleasePayslipWithImport(boolean releasePayslipWithImport) {
		this.releasePayslipWithImport = releasePayslipWithImport;
	}

	public String getPayslipTextFormat() {
		return payslipTextFormat;
	}

	public void setPayslipTextFormat(String payslipTextFormat) {
		this.payslipTextFormat = payslipTextFormat;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getPreviewPdfEmpNumber() {
		return previewPdfEmpNumber;
	}

	public void setPreviewPdfEmpNumber(String previewPdfEmpNumber) {
		this.previewPdfEmpNumber = previewPdfEmpNumber;
	}

	public boolean isAddCompanyLogoInPdf() {
		return addCompanyLogoInPdf;
	}

	public void setAddCompanyLogoInPdf(boolean addCompanyLogoInPdf) {
		this.addCompanyLogoInPdf = addCompanyLogoInPdf;
	}

	public String getLogoAlignment() {
		return logoAlignment;
	}

	public void setLogoAlignment(String logoAlignment) {
		this.logoAlignment = logoAlignment;
	}

	public boolean isFileNameContainsCompNameForPdf() {
		return fileNameContainsCompNameForPdf;
	}

	public void setFileNameContainsCompNameForPdf(
			boolean fileNameContainsCompNameForPdf) {
		this.fileNameContainsCompNameForPdf = fileNameContainsCompNameForPdf;
	}

	public boolean isImportTypeTextPdfFile() {
		return isImportTypeTextPdfFile;
	}

	public void setImportTypeTextPdfFile(boolean isImportTypeTextPdfFile) {
		this.isImportTypeTextPdfFile = isImportTypeTextPdfFile;
	}

	public DataImportLogDTO getDataImportLogSummaryDTO() {
		return dataImportLogSummaryDTO;
	}

	public void setDataImportLogSummaryDTO(DataImportLogDTO dataImportLogSummaryDTO) {
		this.dataImportLogSummaryDTO = dataImportLogSummaryDTO;
	}

	public boolean isCompanyAddressBelowLogo() {
		return companyAddressBelowLogo;
	}

	public void setCompanyAddressBelowLogo(boolean companyAddressBelowLogo) {
		this.companyAddressBelowLogo = companyAddressBelowLogo;
	}

	public List<String> getEmployeeNumberList() {
		return employeeNumberList;
	}

	public void setEmployeeNumberList(List<String> employeeNumberList) {
		this.employeeNumberList = employeeNumberList;
	}

	public boolean isReleasePayslipSchedule() {
		return releasePayslipSchedule;
	}

	public void setReleasePayslipSchedule(boolean releasePayslipSchedule) {
		this.releasePayslipSchedule = releasePayslipSchedule;
	}

	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}


}
