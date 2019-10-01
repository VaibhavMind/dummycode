package com.payasia.logic.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.form.CompanyExchangeRateDTO;
import com.payasia.common.form.CurrencyDefinitionForm;
import com.payasia.common.form.CurrencyDefinitionResponse;
import com.payasia.common.form.HRISReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CompanyExchangeRateDAO;
import com.payasia.dao.CurrencyMasterDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyExchangeRate;
import com.payasia.dao.bean.CurrencyMaster;
import com.payasia.logic.CurrencyDefinitionLogic;

/**
 * The Class CurrencyDefinitionLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class CurrencyDefinitionLogicImpl implements CurrencyDefinitionLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CurrencyDefinitionLogicImpl.class);

	/** The currency master dao. */
	@Resource
	CurrencyMasterDAO currencyMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The month master dao. */
	@Resource
	MonthMasterDAO monthMasterDAO;

	/** The company exchange rate dao. */
	@Resource
	CompanyExchangeRateDAO companyExchangeRateDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CurrencyDefinitionLogic#viewCurrencyDefinition(java
	 * .lang.Long, java.lang.Integer, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public CurrencyDefinitionResponse viewCurrencyDefinition(Long companyId,
			Long currencyId, String currencyDate, PageRequest pageDTO,
			SortCondition sortDTO) {
		int recordSize = 0;
		List<CurrencyDefinitionForm> currencyDefinitionFormList = new ArrayList<CurrencyDefinitionForm>();

		Company companyVO = companyDAO.findById(companyId);
		Timestamp currencyDatetmsp = null;
		if (StringUtils.isNotBlank(currencyDate)) {
			currencyDatetmsp = DateUtils.stringToTimestamp(currencyDate,
					companyVO.getDateFormat());
		}

		List<CompanyExchangeRate> companyExchangeRateList = companyExchangeRateDAO
				.findByCondition(companyId, currencyId, currencyDatetmsp,
						pageDTO, sortDTO);

		for (CompanyExchangeRate companyExchangeRate : companyExchangeRateList) {
			CurrencyDefinitionForm currencyDefinitionForm = new CurrencyDefinitionForm();
			/*ID ENCRYPT*/
			currencyDefinitionForm.setCompanyExchangeRateId(FormatPreserveCryptoUtil.encrypt(companyExchangeRate
					.getCompExchangeRateId()));
			currencyDefinitionForm.setCurrency(companyExchangeRate
					.getCurrencyMaster().getCurrencyName());
			currencyDefinitionForm.setStartDate(DateUtils
					.timeStampToString(companyExchangeRate.getStartDate()));
			currencyDefinitionForm.setEndDate(DateUtils
					.timeStampToString(companyExchangeRate.getEndDate()));
			currencyDefinitionForm.setExchangeRate(companyExchangeRate
					.getExchangeRate());
			currencyDefinitionFormList.add(currencyDefinitionForm);
			currencyDefinitionForm.setCurrencyId(companyExchangeRate.getCurrencyMaster().getCurrencyId());
		}

		recordSize = companyExchangeRateDAO.getCountForCondition(companyId,
				currencyId, currencyDatetmsp);

		CurrencyDefinitionResponse response = new CurrencyDefinitionResponse();
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

		}
		response.setRecords(recordSize);
		response.setRows(currencyDefinitionFormList);
		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CurrencyDefinitionLogic#addCurrencyDefinition(com.payasia
	 * .common.form.CurrencyDefinitionForm, java.lang.Long)
	 */
	@Override
	public String addCurrencyDefinition(
			CurrencyDefinitionForm currencyDefinitionForm, Long companyId) {
		boolean status = true;

		CompanyExchangeRate companyExchangeRate = new CompanyExchangeRate();

		Company company = companyDAO.findById(companyId);

		CurrencyMaster currencyMaster = currencyMasterDAO
				.findById(currencyDefinitionForm.getCurrencyId());

		CompanyExchangeRate checkDuplicateByStartDate = companyExchangeRateDAO
				.CheckExchangeRate(
						DateUtils.stringToTimestamp(
								currencyDefinitionForm.getStartDate(),
								company.getDateFormat()),
						currencyDefinitionForm.getCurrencyId(), companyId, null);
		if (checkDuplicateByStartDate != null) {
			status = false;
		}

		CompanyExchangeRate checkDuplicateByEndDate = companyExchangeRateDAO
				.CheckExchangeRate(
						DateUtils.stringToTimestamp(
								currencyDefinitionForm.getEndDate(),
								company.getDateFormat()),
						currencyDefinitionForm.getCurrencyId(), companyId, null);
		if (checkDuplicateByEndDate != null) {
			status = false;
		}
		if (status) {
			companyExchangeRate.setCurrencyMaster(currencyMaster);
			companyExchangeRate.setCompany(company);
			companyExchangeRate.setStartDate(DateUtils.stringToTimestamp(
					currencyDefinitionForm.getStartDate(),
					company.getDateFormat()));
			companyExchangeRate.setEndDate(DateUtils.stringToTimestamp(
					currencyDefinitionForm.getEndDate(),
					company.getDateFormat()));
			companyExchangeRate.setExchangeRate(currencyDefinitionForm
					.getExchangeRate());
			companyExchangeRateDAO.save(companyExchangeRate);
			return "success";
		} else {
			return "overlap";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CurrencyDefinitionLogic#updateCurrencyDefinition(com
	 * .payasia.common.form.CurrencyDefinitionForm, java.lang.Long,
	 * java.lang.Long, java.lang.Integer)
	 */
	@Override
	public String updateCurrencyDefinition(
			CurrencyDefinitionForm currencyDefinitionForm, Long companyId) {
		boolean status = true;
		/*CompanyExchangeRate companyExchangeRate = companyExchangeRateDAO
				.findById(currencyDefinitionForm.getCompanyExchangeRateId());*/
		
		CompanyExchangeRate companyExchangeRate = companyExchangeRateDAO
				.findByCompanyExchangeRateId(currencyDefinitionForm.getCompanyExchangeRateId(),companyId); 

		Company company = companyDAO.findById(companyId);

		CompanyExchangeRate checkDuplicateByStartDate = companyExchangeRateDAO
				.CheckExchangeRate(
						DateUtils.stringToTimestamp(
								currencyDefinitionForm.getStartDate(),
								company.getDateFormat()),
						currencyDefinitionForm.getCurrencyId(), companyId,
						currencyDefinitionForm.getCompanyExchangeRateId());
		if (checkDuplicateByStartDate != null) {
			status = false;
		}

		CompanyExchangeRate checkDuplicateByEndDate = companyExchangeRateDAO
				.CheckExchangeRate(
						DateUtils.stringToTimestamp(
								currencyDefinitionForm.getEndDate(),
								company.getDateFormat()),
						currencyDefinitionForm.getCurrencyId(), companyId,
						currencyDefinitionForm.getCompanyExchangeRateId());
		if (checkDuplicateByEndDate != null) {
			status = false;
		}

		if (status) {
			companyExchangeRate.setCompany(company);

			CurrencyMaster currencyMaster = currencyMasterDAO
					.findById(currencyDefinitionForm.getCurrencyId());
			companyExchangeRate.setCurrencyMaster(currencyMaster);
			companyExchangeRate.setStartDate(DateUtils.stringToTimestamp(
					currencyDefinitionForm.getStartDate(),
					company.getDateFormat()));
			companyExchangeRate.setEndDate(DateUtils.stringToTimestamp(
					currencyDefinitionForm.getEndDate(),
					company.getDateFormat()));
			companyExchangeRate.setExchangeRate(currencyDefinitionForm
					.getExchangeRate());
			companyExchangeRateDAO.update(companyExchangeRate);
			return "success";
		} else {
			return "overlap";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CurrencyDefinitionLogic#deleteCurrencyDefinition(java
	 * .lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public String deleteCurrencyDefinition(Long companyId,
			Long companyExchangeRateId) {
		CompanyExchangeRate companyExchangeRate = companyExchangeRateDAO
				.findByCompanyExchangeRateId(companyExchangeRateId,companyId);
		if(companyExchangeRate!=null)
		{
		companyExchangeRateDAO.delete(companyExchangeRate);
		return "Success";
		}
		return "UnSuccess";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.CurrencyDefinitionLogic#getCurrencyName()
	 */
	@Override
	public List<CurrencyDefinitionForm> getCurrencyName() {
		List<CurrencyDefinitionForm> currencyDefinitionList = new ArrayList<CurrencyDefinitionForm>();
		List<CurrencyMaster> currencyMasterList = currencyMasterDAO.findAll();
		for (CurrencyMaster currencyMaster : currencyMasterList) {
			CurrencyDefinitionForm currencyDefinitionForm = new CurrencyDefinitionForm();
			currencyDefinitionForm
					.setCurrency(currencyMaster.getCurrencyName());
			currencyDefinitionForm
					.setCurrencyId(currencyMaster.getCurrencyId());
			currencyDefinitionList.add(currencyDefinitionForm);
		}

		return currencyDefinitionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.CurrencyDefinitionLogic#getDataForCurrencyDefinition
	 * (java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public CurrencyDefinitionForm getDataForCurrencyDefinition(Long companyId,
			Long companyExchangeRateId) {
		CompanyExchangeRate companyExchangeRate = companyExchangeRateDAO
				.findByCompanyExchangeRateId(companyExchangeRateId,companyId);
		CurrencyDefinitionForm currencyDefinitionForm = new CurrencyDefinitionForm();
		if (companyExchangeRate != null) {
			currencyDefinitionForm.setCompanyExchangeRateId(companyExchangeRate
					.getCompExchangeRateId());
			currencyDefinitionForm.setBaseCurrency(companyExchangeRate
					.getCompany().getCurrencyMaster().getCurrencyName());
			currencyDefinitionForm.setStartDate(DateUtils
					.timeStampToString(companyExchangeRate.getStartDate()));
			currencyDefinitionForm.setEndDate(DateUtils
					.timeStampToString(companyExchangeRate.getEndDate()));
			currencyDefinitionForm.setExchangeRate(companyExchangeRate
					.getExchangeRate());
			currencyDefinitionForm.setCurrency(companyExchangeRate
					.getCurrencyMaster().getCurrencyName());
			currencyDefinitionForm.setCurrencyId(companyExchangeRate
					.getCurrencyMaster().getCurrencyId());
		}

		return currencyDefinitionForm;
	}

	@Override
	public String getBaseCurrency(Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		String baseCurrency = "";
		if (companyVO != null) {
			baseCurrency = companyVO.getCurrencyMaster().getCurrencyName();
			
		}

		return baseCurrency;
	}

	@Override
	public CurrencyDefinitionForm importCompanyExchangeRate(
			CurrencyDefinitionForm currencyDefinitionForm, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		String fileName = currencyDefinitionForm.getFileUpload()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		HRISReviewerForm exchangeRateImportDataList = new HRISReviewerForm();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			exchangeRateImportDataList = ExcelUtils
					.getHRISReviewersFromXLS(currencyDefinitionForm
							.getFileUpload());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			exchangeRateImportDataList = ExcelUtils
					.getHRISReviewersFromXLSX(currencyDefinitionForm
							.getFileUpload());
		}
		CurrencyDefinitionForm response = new CurrencyDefinitionForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();

		HashMap<String, CompanyExchangeRateDTO> companyExchangeRateDTOMap = new HashMap<>();
		populateCompanyExchangeRateImportDataDTO(dataImportLogDTOs,
				exchangeRateImportDataList, companyExchangeRateDTOMap,
				companyId);

		// Currency List
		HashMap<String, Long> currencyCodeIdMap = new HashMap<String, Long>();
		HashMap<String, CurrencyMaster> currencyMasterMap = new HashMap<String, CurrencyMaster>();
		List<CurrencyMaster> currencyMasterList = currencyMasterDAO.findAll();
		for (CurrencyMaster currencyMaster : currencyMasterList) {
			if (StringUtils.isNotBlank(currencyMaster.getCurrencyCode())) {
				currencyCodeIdMap.put(currencyMaster.getCurrencyCode(),
						currencyMaster.getCurrencyId());
				currencyMasterMap.put(currencyMaster.getCurrencyCode(),
						currencyMaster);
			}
		}

		validateImportedData(companyExchangeRateDTOMap, dataImportLogDTOs,
				currencyCodeIdMap, companyId);

		if (!dataImportLogDTOs.isEmpty()) {
			response.setDataValid(false);
			response.setDataImportLogDTOs(dataImportLogDTOs);
			return response;
		}

		Set<String> keySet = companyExchangeRateDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			CompanyExchangeRateDTO compExchangeRateDTO = companyExchangeRateDTOMap
					.get(key);

			boolean status = true;
			CurrencyMaster currencyMaster = currencyMasterMap
					.get(compExchangeRateDTO.getForeignCurrencyCode());

			CompanyExchangeRate checkDuplicateByStartDate = companyExchangeRateDAO
					.CheckExchangeRate(DateUtils.stringToTimestamp(
							compExchangeRateDTO.getStartDate(),
							PayAsiaConstants.SERVER_SIDE_DATE_FORMAT),
							currencyMaster.getCurrencyId(), companyId, null);
			if (checkDuplicateByStartDate != null) {
				status = false;
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO
						.setColName(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE);
				dataImportLogDTO
						.setRemarks("payasia.currency.definition.exchange.rate.overlap.error");
				dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
				dataImportLogDTOs.add(dataImportLogDTO);

				response.setDataValid(false);
				response.setDataImportLogDTOs(dataImportLogDTOs);
				return response;
			}

			CompanyExchangeRate checkDuplicateByEndDate = companyExchangeRateDAO
					.CheckExchangeRate(DateUtils.stringToTimestamp(
							compExchangeRateDTO.getEndDate(),
							PayAsiaConstants.SERVER_SIDE_DATE_FORMAT),
							compExchangeRateDTO.getCurrencyId(), companyId,
							null);
			if (checkDuplicateByEndDate != null) {
				status = false;
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO
						.setColName(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE);
				dataImportLogDTO
						.setRemarks("payasia.currency.definition.exchange.rate.overlap.error");
				dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
				dataImportLogDTOs.add(dataImportLogDTO);
				response.setDataValid(false);
				response.setDataImportLogDTOs(dataImportLogDTOs);
				return response;
			}
			if (status) {
				CompanyExchangeRate companyExchangeRate = new CompanyExchangeRate();
				companyExchangeRate.setCurrencyMaster(currencyMaster);
				companyExchangeRate.setCompany(companyVO);
				companyExchangeRate.setStartDate(DateUtils.stringToTimestamp(
						compExchangeRateDTO.getStartDate(),
						PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
				companyExchangeRate.setEndDate(DateUtils.stringToTimestamp(
						compExchangeRateDTO.getEndDate(),
						PayAsiaConstants.SERVER_SIDE_DATE_FORMAT));
				companyExchangeRate.setExchangeRate(new BigDecimal(
						compExchangeRateDTO.getExchangeRate()));
				companyExchangeRateDAO.save(companyExchangeRate);
			}
		}
		response.setDataValid(true);
		return response;
	}

	public void populateCompanyExchangeRateImportDataDTO(
			List<DataImportLogDTO> dataImportLogDTOs,
			HRISReviewerForm compExchangeRateImportDataForm,
			HashMap<String, CompanyExchangeRateDTO> companyExchangeRateDTOMap,
			Long companyId) {
		for (HashMap<String, String> map : compExchangeRateImportDataForm
				.getImportedData()) {
			CompanyExchangeRateDTO companyExchangeRateDTO = new CompanyExchangeRateDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get(PayAsiaConstants.HASH_KEY_ROW_NUMBER);
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				if (key != PayAsiaConstants.HASH_KEY_ROW_NUMBER) {
					if (key.equals(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE)) {
						companyExchangeRateDTO.setExchangeRate(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_COMPANY_FOREIGN_CURRENCY_CODE)) {
						companyExchangeRateDTO.setForeignCurrencyCode(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE_FROM_DATE)) {
						companyExchangeRateDTO.setStartDate(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE_TO_DATE)) {
						companyExchangeRateDTO.setEndDate(value);
					}

					companyExchangeRateDTOMap.put(rowNumber,
							companyExchangeRateDTO);
				}
			}
		}
	}

	/**
	 * Purpose: To Validate imported Company Exchange Rate.
	 * 
	 * @param companyExchangeRateDTOMap
	 *            the companyExchangeRateDTOMap
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImportedData(
			HashMap<String, CompanyExchangeRateDTO> companyExchangeRateDTOMap,
			List<DataImportLogDTO> dataImportLogDTOs,
			HashMap<String, Long> currencyCodeIdMap, Long companyId) {
		Set<String> keySet = companyExchangeRateDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			CompanyExchangeRateDTO companyExchangeRateDTO = companyExchangeRateDTOMap
					.get(key);

			if (StringUtils.isNotBlank(companyExchangeRateDTO
					.getForeignCurrencyCode())) {
				if (currencyCodeIdMap.get(companyExchangeRateDTO
						.getForeignCurrencyCode()) == null) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setColName(PayAsiaConstants.PAYASIA_COMPANY_FOREIGN_CURRENCY_CODE);
					dataImportLogDTO
							.setRemarks("payasia.currency.definition.wrong.currency.code");
					dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
					dataImportLogDTOs.add(dataImportLogDTO);
				}
			} else {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO
						.setColName(PayAsiaConstants.PAYASIA_COMPANY_FOREIGN_CURRENCY_CODE);
				dataImportLogDTO
						.setRemarks("payasia.currency.definition.wrong.currency.code");
				dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
				dataImportLogDTOs.add(dataImportLogDTO);
			}

			if (StringUtils.isBlank(companyExchangeRateDTO.getStartDate())) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO
						.setColName(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE_FROM_DATE);
				dataImportLogDTO
						.setRemarks("payasia.currency.definition.from.date.empty");
				dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
				dataImportLogDTOs.add(dataImportLogDTO);
			}
			if (StringUtils.isBlank(companyExchangeRateDTO.getEndDate())) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO
						.setColName(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE_TO_DATE);
				dataImportLogDTO
						.setRemarks("payasia.currency.definition.to.date.empty");
				dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
				dataImportLogDTOs.add(dataImportLogDTO);
			}
			if (StringUtils.isBlank(companyExchangeRateDTO.getExchangeRate())) {
				DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
				dataImportLogDTO
						.setColName(PayAsiaConstants.PAYASIA_COMPANY_EXCHANGE_RATE);
				dataImportLogDTO
						.setRemarks("payasia.currency.definition.exchange.rate.empty");
				dataImportLogDTO.setRowNumber(Long.parseLong(key) + 1);
				dataImportLogDTOs.add(dataImportLogDTO);
			}

			companyExchangeRateDTOMap.put(key, companyExchangeRateDTO);
		}
	}
}
