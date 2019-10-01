package com.payasia.common.form;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;

public class ImportEmployeeOvertimeShiftForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6889988409016304212L;
	
	private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;

	private CommonsMultipartFile fileUpload;

	private boolean sameExcelField = false;
	private List<String> colName;
	private List<String> duplicateColNames;
	private List<HashMap<String, String>> importedData;
	
	public boolean isDataValid() {
		return dataValid;
	}
	public void setDataValid(boolean dataValid) {
		this.dataValid = dataValid;
	}
	public List<DataImportLogDTO> getDataImportLogDTOs() {
		return dataImportLogDTOs;
	}
	public void setDataImportLogDTOs(List<DataImportLogDTO> dataImportLogDTOs) {
		this.dataImportLogDTOs = dataImportLogDTOs;
	}
	public CommonsMultipartFile getFileUpload() {
		return fileUpload;
	}
	public void setFileUpload(CommonsMultipartFile fileUpload) {
		this.fileUpload = fileUpload;
	}
	public boolean isSameExcelField() {
		return sameExcelField;
	}
	public void setSameExcelField(boolean sameExcelField) {
		this.sameExcelField = sameExcelField;
	}
	public List<String> getColName() {
		return colName;
	}
	public void setColName(List<String> colName) {
		this.colName = colName;
	}
	public List<String> getDuplicateColNames() {
		return duplicateColNames;
	}
	public void setDuplicateColNames(List<String> duplicateColNames) {
		this.duplicateColNames = duplicateColNames;
	}
	public List<HashMap<String, String>> getImportedData() {
		return importedData;
	}
	public void setImportedData(List<HashMap<String, String>> importedData) {
		this.importedData = importedData;
	}
	
	
	
	
	
	
	
	
	
	

}
