package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.util.ImportEmployeeClaimDTO;

public class ImportEmployeeClaimForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6889988409016304212L;
	
	
	private List<DataImportLogDTO> dataImportLogDTOs;
	private CommonsMultipartFile fileUpload;
	private List<ImportEmployeeClaimDTO> importEmployeeClaimsDTO;
	List<ImportEmployeeClaimDTO> employeeClaims;
	private Boolean dataValid;
	
	
	
	public Boolean getDataValid() {
		return dataValid;
	}
	public void setDataValid(Boolean dataValid) {
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
	public List<ImportEmployeeClaimDTO> getImportEmployeeClaimsDTO() {
		return importEmployeeClaimsDTO;
	}
	public void setImportEmployeeClaimsDTO(
			List<ImportEmployeeClaimDTO> importEmployeeClaimsDTO) {
		this.importEmployeeClaimsDTO = importEmployeeClaimsDTO;
	}
	public List<ImportEmployeeClaimDTO> getEmployeeClaims() {
		return employeeClaims;
	}
	public void setEmployeeClaims(List<ImportEmployeeClaimDTO> employeeClaims) {
		this.employeeClaims = employeeClaims;
	}
	
	
	
	
	
	
	
	

}
