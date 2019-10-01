package com.payasia.common.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.DataImportLogDTO;


/**
 * The Class CurrencyDefinitionForm.
 */
public class CurrencyDefinitionForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2313733155428768805L;

	private String currency;
	
	private Long currencyId;
	
	private String currencyDesc;
	private String startDate;
	private String endDate;
	private BigDecimal exchangeRate;
	private Long companyExchangeRateId;
	private String baseCurrency;
	
	private String status;
	
	private List<Integer> yearList;
    
    private Integer year;
	
    private boolean dataValid;
	private List<DataImportLogDTO> dataImportLogDTOs;

	private CommonsMultipartFile fileUpload;

	private boolean sameExcelField = false;
	private List<String> colName;
	private List<String> duplicateColNames;
	private List<HashMap<String, String>> importedData;

	public String getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency.
	 *
	 * @param currency the new currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Integer> getYearList() {
		return yearList;
	}

	public void setYearList(List<Integer> yearList) {
		this.yearList = yearList;
	}

	

	

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * Gets the currency id.
	 *
	 * @return the currency id
	 */
	public Long getCurrencyId() {
		return currencyId;
	}

	/**
	 * Sets the currency id.
	 *
	 * @param currencyId the new currency id
	 */
	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	/**
	 * Gets the currency desc.
	 *
	 * @return the currency desc
	 */
	public String getCurrencyDesc() {
		return currencyDesc;
	}

	/**
	 * Sets the currency desc.
	 *
	 * @param currencyDesc the new currency desc
	 */
	public void setCurrencyDesc(String currencyDesc) {
		this.currencyDesc = currencyDesc;
	}

	/**
	 * Gets the year.
	 *
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * Sets the year.
	 *
	 * @param year the new year
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	public Long getCompanyExchangeRateId() {
		return companyExchangeRateId;
	}

	public void setCompanyExchangeRateId(Long companyExchangeRateId) {
		this.companyExchangeRateId = companyExchangeRateId;
	}

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
