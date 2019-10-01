package com.payasia.logic.impl;

/**
 * @author vivekjain
 *
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.HolidayListMasterDTO;
import com.payasia.common.form.HolidayListMasterForm;
import com.payasia.common.form.HolidayListMasterResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CountryMasterDAO;
import com.payasia.dao.HolidayConfigMasterDAO;
import com.payasia.dao.StateMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.HolidayConfigMaster;
import com.payasia.dao.bean.StateMaster;
import com.payasia.logic.HolidayListMasterLogic;

/**
 * The Class HolidayListMasterLogicImpl.
 */

@Component
public class HolidayListMasterLogicImpl implements HolidayListMasterLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HolidayListMasterLogicImpl.class);

	/** The holiday config master dao. */
	@Resource
	HolidayConfigMasterDAO holidayConfigMasterDAO;

	/** The country master dao. */
	@Resource
	CountryMasterDAO countryMasterDAO;

	/** The state master dao. */
	@Resource
	StateMasterDAO stateMasterDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.HolidayListMasterLogic#getYearList()
	 */
	@Override
	public List<Integer> getYearList() {
		List<Integer> yearList = holidayConfigMasterDAO.getYearList();
		return yearList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#getHolidayList(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition, java.lang.Long,
	 * java.lang.Long, int, java.lang.Long)
	 */
	@Override
	public HolidayListMasterResponse getHolidayList(PageRequest pageDTO,
			SortCondition sortDTO, String countryId, String stateId,
			String year, Long companyId) {
		Company company = companyDAO.findById(companyId);
		int recordSize = holidayConfigMasterDAO.getHolidayListCount(countryId,
				stateId, year);

		List<HolidayConfigMaster> holidayMasterList = holidayConfigMasterDAO
				.findByCondition(pageDTO, sortDTO, countryId, stateId, year);

		List<HolidayListMasterForm> holListMasterForms = new ArrayList<HolidayListMasterForm>();

		for (HolidayConfigMaster holidayConfigMaster : holidayMasterList) {
			HolidayListMasterForm holidayListMasterForm = new HolidayListMasterForm();
			holidayListMasterForm.setOccasion(holidayConfigMaster
					.getHolidayDesc());
			// ID ENCRYPT
			holidayListMasterForm.setHolidayId(FormatPreserveCryptoUtil.encrypt(holidayConfigMaster
					.getHolidayConfigMasterId()));
			holidayListMasterForm.setDated(DateUtils.timeStampToString(
					holidayConfigMaster.getHolidayDate(),
					company.getDateFormat()));
			holidayListMasterForm.setCountryId(holidayConfigMaster
					.getCountryMaster().getCountryId());
			holidayListMasterForm.setCountryName(holidayConfigMaster
					.getCountryMaster().getCountryName());
			holidayListMasterForm.setStateId(holidayConfigMaster
					.getStateMaster().getStateId());
			holidayListMasterForm.setStateName(holidayConfigMaster
					.getStateMaster().getStateName());
			Date date = new Date(holidayConfigMaster.getHolidayDate().getTime());
			SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
			holidayListMasterForm.setDay(dayFormat.format(date));

			holListMasterForms.add(holidayListMasterForm);
		}

		HolidayListMasterResponse response = new HolidayListMasterResponse();

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
			response.setRecords(recordSize);
		}
		response.setHolidayListMasterForms(holListMasterForms);

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#getHolidayData(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public HolidayListMasterForm getHolidayData(Long holidayId, Long companyId) {
		Company company = companyDAO.findById(companyId);

		HolidayListMasterForm masterForm = new HolidayListMasterForm();
		HolidayConfigMaster holidayConfigMaster = holidayConfigMasterDAO
				.findByID(holidayId,companyId);
		/*ID ENCRYPT*/
		masterForm.setHolidayId(FormatPreserveCryptoUtil.encrypt(holidayConfigMaster.getHolidayConfigMasterId()));
		masterForm.setCountryId(holidayConfigMaster.getCountryMaster()
				.getCountryId());
		masterForm.setCountryName(holidayConfigMaster.getCountryMaster()
				.getCountryName());
		masterForm
				.setStateId(holidayConfigMaster.getStateMaster().getStateId());
		masterForm.setStateName(holidayConfigMaster.getStateMaster()
				.getStateName());
		masterForm.setDated(DateUtils.timeStampToString(
				holidayConfigMaster.getHolidayDate(), company.getDateFormat()));
		masterForm.setOccasion(holidayConfigMaster.getHolidayDesc());
		return masterForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.HolidayListMasterLogic#getCountryList()
	 */
	@Override
	public List<HolidayListMasterForm> getCountryList() {
		List<HolidayListMasterForm> holidayListMasterFormList = new ArrayList<HolidayListMasterForm>();
		List<CountryMaster> countryList = countryMasterDAO.findAll();
		for (CountryMaster countryMaster : countryList) {
			HolidayListMasterForm holidayListMasterForm = new HolidayListMasterForm();
			holidayListMasterForm.setCountryId(countryMaster.getCountryId());
			try {
				holidayListMasterForm.setCountryName(URLEncoder.encode(
						countryMaster.getCountryName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			holidayListMasterFormList.add(holidayListMasterForm);
		}
		return holidayListMasterFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#getStateList(java.lang.Long)
	 */
	@Override
	public List<HolidayListMasterForm> getStateList(Long countryId) {
		List<HolidayListMasterForm> holidayListMasterFormList = new ArrayList<HolidayListMasterForm>();
		List<StateMaster> stateList = stateMasterDAO.findByCountry(countryId);
		for (StateMaster stateMaster : stateList) {
			HolidayListMasterForm holidayListMasterForm = new HolidayListMasterForm();
			holidayListMasterForm.setStateId(stateMaster.getStateId());
			try {
				holidayListMasterForm.setStateName(URLEncoder.encode(
						stateMaster.getStateName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			holidayListMasterFormList.add(holidayListMasterForm);
		}
		return holidayListMasterFormList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#addHolidayMaster(com.payasia
	 * .common.form.HolidayListMasterForm, java.lang.Long)
	 */
	@Override
	public HolidayListMasterForm addHolidayMaster(
			HolidayListMasterForm holidayListMasterForm, Long companyId) {
		boolean status = true;
		Company company = companyDAO.findById(companyId);

		HolidayConfigMaster holidayConfigMaster = new HolidayConfigMaster();

		CountryMaster countryMaster = countryMasterDAO
				.findById(holidayListMasterForm.getCountryId());
		holidayConfigMaster.setCountryMaster(countryMaster);

		StateMaster stateMaster = stateMasterDAO.findById(holidayListMasterForm
				.getStateId());
		holidayConfigMaster.setStateMaster(stateMaster);

		holidayConfigMaster.setHolidayDate(DateUtils.stringToTimestamp(
				holidayListMasterForm.getDated(), company.getDateFormat()));
		holidayConfigMaster.setHolidayDesc(holidayListMasterForm.getOccasion());

		status = checkHolidayDate(null, holidayListMasterForm.getCountryId(),
				holidayListMasterForm.getStateId(),
				holidayListMasterForm.getOccasion(),
				holidayListMasterForm.getDated(), company.getDateFormat());

		HolidayListMasterForm listMasterForm = new HolidayListMasterForm();

		if (status) {
			holidayConfigMasterDAO.save(holidayConfigMaster);
			List<Integer> yearList = holidayConfigMasterDAO.getYearList();
			listMasterForm.setYearList(yearList);
			listMasterForm.setStatus(PayAsiaConstants.TRUE);
		} else {
			List<Integer> yearList = holidayConfigMasterDAO.getYearList();
			listMasterForm.setYearList(yearList);
			listMasterForm.setStatus(PayAsiaConstants.FALSE);
		}

		return listMasterForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#editHolidayMaster(com.payasia
	 * .common.form.HolidayListMasterForm, java.lang.Long, java.lang.Long)
	 */
	@Override
	public HolidayListMasterForm editHolidayMaster(
			HolidayListMasterForm holidayListMasterForm, Long holidayId,
			Long companyId) {
		boolean status = true;
		Company company = companyDAO.findById(companyId);
		HolidayConfigMaster holidayConfigMaster = new HolidayConfigMaster();
		
		/**
	     * This code & check comment as per discussion with Mr.Surya  
	     * */
		/*HolidayConfigMaster holidayConfigMaster = holidayConfigMasterDAO.findByID(holidayId,companyId);*/
		/*if(holidayConfigMaster != null){
			holidayConfigMaster.setHolidayConfigMasterId(holidayId);
		}*/
		holidayConfigMaster.setHolidayConfigMasterId(holidayId);
		CountryMaster countryMaster = countryMasterDAO
				.findById(holidayListMasterForm.getCountryId());
		holidayConfigMaster.setCountryMaster(countryMaster);

		StateMaster stateMaster = stateMasterDAO.findById(holidayListMasterForm
				.getStateId());
		holidayConfigMaster.setStateMaster(stateMaster);

		holidayConfigMaster.setHolidayDate(DateUtils.stringToTimestamp(
				holidayListMasterForm.getDated(), company.getDateFormat()));
		holidayConfigMaster.setHolidayDesc(holidayListMasterForm.getOccasion());

		status = checkHolidayDate(null, holidayListMasterForm.getCountryId(),
				holidayListMasterForm.getStateId(),
				holidayListMasterForm.getOccasion(),
				holidayListMasterForm.getDated(), company.getDateFormat());

		HolidayListMasterForm listMasterForm = new HolidayListMasterForm();

		if (status) {
			holidayConfigMasterDAO.update(holidayConfigMaster);
			List<Integer> yearList = holidayConfigMasterDAO.getYearList();
			listMasterForm.setYearList(yearList);
			listMasterForm.setStatus(PayAsiaConstants.TRUE);
		} else {
			List<Integer> yearList = holidayConfigMasterDAO.getYearList();
			listMasterForm.setYearList(yearList);
			listMasterForm.setStatus(PayAsiaConstants.FALSE);
		}

		return listMasterForm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#deleteHolidayMaster(java.lang
	 * .Long)
	 */
	@Override
	public List<Integer> deleteHolidayMaster(Long holidayId,Long companyId) {
		HolidayConfigMaster holidayConfigMaster = holidayConfigMasterDAO
				.findByID(holidayId,companyId);
		holidayConfigMasterDAO.delete(holidayConfigMaster);
		List<Integer> yearList = holidayConfigMasterDAO.getYearList();
		return yearList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.HolidayListMasterLogic#getHolidayListFromXL(com.payasia
	 * .common.form.HolidayListMasterForm, java.lang.Long)
	 */
	@Override
	public HolidayListMasterForm getHolidayListFromXL(
			HolidayListMasterForm holidayListMasterForm, Long companyId) {
		Company company = companyDAO.findById(companyId);
		String fileName = holidayListMasterForm.getFileUpload()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		List<HashMap<String, HolidayListMasterDTO>> holidayListMasterList = null;
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			holidayListMasterList = ExcelUtils.getHolidayListMasterFromXLS(
					holidayListMasterForm.getFileUpload(),
					company.getDateFormat());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			holidayListMasterList = ExcelUtils.getHolidayListMasterFromXLSX(
					holidayListMasterForm.getFileUpload(),
					company.getDateFormat());
		}
		HolidayListMasterForm holidayListMasterFrm = new HolidayListMasterForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();
		if (holidayListMasterList != null) {
			validateImpotedData(holidayListMasterList, dataImportLogDTOs,
					companyId);
		}
		if (!dataImportLogDTOs.isEmpty()) {
			holidayListMasterFrm.setDataValid(false);
			holidayListMasterFrm.setDataImportLogDTOs(dataImportLogDTOs);
			return holidayListMasterFrm;
		}

		for (HashMap<String, HolidayListMasterDTO> holidayListMaster : holidayListMasterList) {
			Set<String> keySet = holidayListMaster.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				HolidayListMasterDTO holidayListMasterDTO = holidayListMaster
						.get(key);
				String countryName = "";
				HolidayConfigMaster holidayConfigMaster = new HolidayConfigMaster();
				Long countryIdVal = getCountryId(holidayListMasterDTO
						.getCountry());
				CountryMaster countryPersistObj = null;
				if (countryIdVal == 0) {
					countryName = holidayListMasterDTO.getCountry();
					CountryMaster countryMaster = new CountryMaster();
					countryMaster.setCountryName(countryName);
					countryPersistObj = countryMasterDAO
							.saveAndPersist(countryMaster);
					holidayConfigMaster.setCountryMaster(countryPersistObj);
				}
				if (countryIdVal != 0) {
					CountryMaster countryMaster = countryMasterDAO
							.findById(countryIdVal);
					holidayConfigMaster.setCountryMaster(countryMaster);
				}

				Long stateIdVal = getStateId(holidayListMasterDTO.getState());
				if (stateIdVal == 0) {
					CountryMaster countryMaster;
					StateMaster stateMaster = new StateMaster();
					if (countryIdVal != 0) {
						countryMaster = countryMasterDAO.findById(countryIdVal);
						stateMaster.setCountryMaster(countryMaster);
					} else {
						stateMaster.setCountryMaster(countryPersistObj);
					}

					stateMaster.setStateName(holidayListMasterDTO.getState());
					StateMaster persistObj = stateMasterDAO
							.saveAndPersist(stateMaster);
					holidayConfigMaster.setStateMaster(persistObj);
				}
				if (stateIdVal != 0) {
					StateMaster stateMaster = stateMasterDAO
							.findById(stateIdVal);
					holidayConfigMaster.setStateMaster(stateMaster);
				}

				holidayConfigMaster.setHolidayDate(DateUtils.stringToTimestamp(
						holidayListMasterDTO.getHolidayDateString(),
						company.getDateFormat()));

				holidayConfigMaster.setHolidayDesc(holidayListMasterDTO
						.getOccasion());
				holidayConfigMasterDAO.save(holidayConfigMaster);
			}

		}
		holidayListMasterFrm.setDataValid(true);
		return holidayListMasterFrm;

	}

	/**
	 * Purpose: To Validate impoted employee leave scheme.
	 * 
	 * @param empLeaveSchemeList
	 *            the employee LeaveScheme List
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(
			List<HashMap<String, HolidayListMasterDTO>> holidayListMasterList,
			List<DataImportLogDTO> dataImportLogDTOs, Long companyId) {
		Company company = companyDAO.findById(companyId);
		for (HashMap<String, HolidayListMasterDTO> holidayListMaster : holidayListMasterList) {
			Set<String> keySet = holidayListMaster.keySet();
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				HolidayListMasterDTO holidayListMasterDTO = holidayListMaster
						.get(key);
				boolean status = true;

				if (StringUtils.isBlank(holidayListMasterDTO.getCountry())) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setColName(PayAsiaConstants.HOLIDAY_LIST_MASTER_COUNTRY_NAME);
					dataImportLogDTO
							.setRemarks("Payasia.holidaylist.master.blank.countryname");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}
				if (StringUtils.isBlank(holidayListMasterDTO.getState())) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setColName(PayAsiaConstants.HOLIDAY_LIST_MASTER_STATE_NAME);
					dataImportLogDTO
							.setRemarks("Payasia.holidaylist.master.blank.statename");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}
				if (StringUtils.isBlank(holidayListMasterDTO.getOccasion())) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setColName(PayAsiaConstants.HOLIDAY_LIST_MASTER_OCCASION);
					dataImportLogDTO
							.setRemarks("Payasia.holidaylist.master.blank.occasion");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}
				if (StringUtils.isBlank(holidayListMasterDTO
						.getHolidayDateString())) {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setColName(PayAsiaConstants.HOLIDAY_LIST_MASTER_HOLIDAY_DATE);
					dataImportLogDTO
							.setRemarks("Payasia.holidaylist.master.blank.holidaydate");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}

				if (StringUtils.isNotBlank(holidayListMasterDTO.getCountry())) {
					Long countryIdVal = getCountryId(holidayListMasterDTO
							.getCountry());
					if (countryIdVal != 0) {
						if (StringUtils.isNotBlank(holidayListMasterDTO
								.getState())
								&& StringUtils.isNotBlank(holidayListMasterDTO
										.getOccasion())
								&& StringUtils.isNotBlank(holidayListMasterDTO
										.getHolidayDateString())) {
							Long stateIdVal = getStateId(holidayListMasterDTO
									.getState());
							if (stateIdVal != 0) {

								status = checkHolidayDate(null, countryIdVal,
										stateIdVal,
										holidayListMasterDTO.getOccasion(),
										holidayListMasterDTO
												.getHolidayDateString(),
										company.getDateFormat());
								if (!status) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.HOLIDAY_LIST_MASTER_OCCASION);
									dataImportLogDTO
											.setRemarks("Payasia.holidaylist.master.duplicate.holiday");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								}
							}
						}

					}
				}

			}

		}
	}

	/**
	 * Gets the country id.
	 * 
	 * @param countryName
	 *            the country name
	 * @return the country id
	 */
	public Long getCountryId(String countryName) {
		List<CountryMaster> countryMasterList = countryMasterDAO.findAll();
		for (CountryMaster countryMaster : countryMasterList) {
			if (countryName.equalsIgnoreCase(countryMaster.getCountryName())) {
				return countryMaster.getCountryId();
			}
		}
		return 0l;

	}

	/**
	 * Gets the state id.
	 * 
	 * @param stateName
	 *            the state name
	 * @return the state id
	 */
	public Long getStateId(String stateName) {
		List<StateMaster> stateMasterList = stateMasterDAO.findAll();
		for (StateMaster stateMaster : stateMasterList) {
			if (stateName.equalsIgnoreCase(stateMaster.getStateName())) {
				return stateMaster.getStateId();
			}
		}
		return 0l;

	}

	/**
	 * Check holiday date.
	 * 
	 * @param holidayId
	 *            the holiday id
	 * @param countryId
	 *            the country id
	 * @param stateId
	 *            the state id
	 * @param occasion
	 *            the occasion
	 * @param holidayDate
	 *            the holiday date
	 * @param dateFormat
	 *            the date format
	 * @return true, if successful
	 */
	public boolean checkHolidayDate(Long holidayId, Long countryId,
			Long stateId, String occasion, String holidayDate, String dateFormat) {
		HolidayConfigMaster holidayConfigMasterVO = holidayConfigMasterDAO
				.findByOccasionAndDate(holidayId, countryId, stateId, occasion,
						holidayDate, dateFormat);
		if (holidayConfigMasterVO == null) {
			return true;
		}
		return false;

	}

}
