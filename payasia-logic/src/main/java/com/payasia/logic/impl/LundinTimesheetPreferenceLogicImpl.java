package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.form.LundinTimesheetPreferenceForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.LundinTimesheetPreferenceDAO;
import com.payasia.dao.TimesheetBatchDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.LundinTimesheetPreference;
import com.payasia.dao.bean.TimesheetBatch;
import com.payasia.logic.LundinTimesheetPreferenceLogic;

@Component
public class LundinTimesheetPreferenceLogicImpl implements
		LundinTimesheetPreferenceLogic {

	@Resource
	LundinTimesheetPreferenceDAO lundinTimesheetPreferenceDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	TimesheetBatchDAO lundinTimesheetBatchDAO;
	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Override
	public LundinTimesheetPreferenceForm getLundinTimesheetPreference(
			Long companyId) {
		LundinTimesheetPreference lundinOTPreference = lundinTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		LundinTimesheetPreferenceForm lundinTimesheetPreferenceFormObj = new LundinTimesheetPreferenceForm();
		if (lundinOTPreference != null) {
			try {
				BeanUtils.copyProperties(lundinTimesheetPreferenceFormObj,
						lundinOTPreference);
				lundinTimesheetPreferenceFormObj.setCutOffDay(String
						.valueOf(lundinOTPreference.getCutoff_Day()));
				if (lundinOTPreference.getDataDictionary() != null) {
					lundinTimesheetPreferenceFormObj
							.setDataDictionaryId(lundinOTPreference
									.getDataDictionary().getDataDictionaryId());
				} else {
					lundinTimesheetPreferenceFormObj.setDataDictionaryId(-1);
				}
				if (lundinOTPreference.getCostCategory() != null) {
					lundinTimesheetPreferenceFormObj
							.setCostCategoryDictId(lundinOTPreference
									.getCostCategory().getDataDictionaryId());
				} else {
					lundinTimesheetPreferenceFormObj.setCostCategoryDictId(-1l);
				}
				if (lundinOTPreference.getDailyRate() != null) {
					lundinTimesheetPreferenceFormObj
							.setDailyRateDictId(lundinOTPreference
									.getDailyRate().getDataDictionaryId());
				} else {
					lundinTimesheetPreferenceFormObj.setDailyRateDictId(-1l);
				}
				if (lundinOTPreference.getAutoTimewrite() != null) {
					lundinTimesheetPreferenceFormObj
							.setAutoTimewriteDictId(lundinOTPreference
									.getAutoTimewrite().getDataDictionaryId());
				} else {
					lundinTimesheetPreferenceFormObj
							.setAutoTimewriteDictId(-1l);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lundinTimesheetPreferenceFormObj;

	}

	@Override
	public void saveLundinTimesheetPreference(
			LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm,
			Long companyId) {

		LundinTimesheetPreference lundinOTPreferenceVO = lundinTimesheetPreferenceDAO
				.findByCompanyId(companyId);
		if (lundinOTPreferenceVO == null) {
			LundinTimesheetPreference lundinOTPreference = new LundinTimesheetPreference();
			lundinOTPreference.setCompany(companyDAO.findById(companyId));
			lundinOTPreference.setCutoff_Day(Integer
					.parseInt(lundinTimesheetPreferenceForm.getCutOffDay()));
			lundinOTPreference
					.setUseSystemMailAsFromAddress(lundinTimesheetPreferenceForm
							.getUseSystemMailAsFromAddress());
			// Department Mapped by dataDictionary
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getDataDictionaryId());
			lundinOTPreference.setDataDictionary(dataDictionary);

			DataDictionary costCategoryDict = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getCostCategoryDictId());
			lundinOTPreference.setCostCategory(costCategoryDict);
			DataDictionary dailyRateDict = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getDailyRateDictId());
			lundinOTPreference.setDailyRate(dailyRateDict);
			DataDictionary autoTimewriteDict = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getAutoTimewriteDictId());
			lundinOTPreference.setAutoTimewrite(autoTimewriteDict);

			lundinTimesheetPreferenceDAO.save(lundinOTPreference);
		} else {
			lundinOTPreferenceVO.setCutoff_Day(Integer
					.parseInt(lundinTimesheetPreferenceForm.getCutOffDay()));
			lundinOTPreferenceVO
					.setUseSystemMailAsFromAddress(lundinTimesheetPreferenceForm
							.getUseSystemMailAsFromAddress());
			DataDictionary dataDictionary = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getDataDictionaryId());
			lundinOTPreferenceVO.setDataDictionary(dataDictionary);

			DataDictionary costCategoryDict = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getCostCategoryDictId());
			lundinOTPreferenceVO.setCostCategory(costCategoryDict);
			DataDictionary dailyRateDict = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getDailyRateDictId());
			lundinOTPreferenceVO.setDailyRate(dailyRateDict);

			DataDictionary autoTimewriteDict = dataDictionaryDAO
					.findById(lundinTimesheetPreferenceForm
							.getAutoTimewriteDictId());
			lundinOTPreferenceVO.setAutoTimewrite(autoTimewriteDict);
			lundinTimesheetPreferenceDAO.update(lundinOTPreferenceVO);
		}

	}

	@Override
	public void createOTBatches(int cutOffDay, int yearOfBatch, long companyId) {
		TimesheetBatch oTBatchResult = lundinTimesheetBatchDAO
				.findOtBatchExist(companyId);
		Calendar getYear = Calendar.getInstance();
		if (oTBatchResult != null) {
			getYear.setTimeInMillis(oTBatchResult.getStartDate().getTime());
		}
		int createdBatchYear = getYear.get(Calendar.YEAR);
		if (yearOfBatch > createdBatchYear || oTBatchResult == null) {

			int allowedCutOffLimit = 27;
			Company company = companyDAO.findById(companyId);
			boolean isLeapYear = ((yearOfBatch % 4 == 0)
					&& (yearOfBatch % 100 != 0) || (yearOfBatch % 400 == 0));
			// boolean isLeapYear = true;
			if (isLeapYear) {
				allowedCutOffLimit = allowedCutOffLimit + 1;
			}
			if (cutOffDay <= allowedCutOffLimit) {
				smallerDateBatch(cutOffDay, yearOfBatch, company);
			} else {
				largerDateBatch(cutOffDay, yearOfBatch, isLeapYear, company);
			}
		}

	}

	private void smallerDateBatch(int cutDay, int year, Company company) {
		Calendar startDate = GregorianCalendar.getInstance();
		Calendar endDate = GregorianCalendar.getInstance();

		startDate.set(year - 1, 11, cutDay + 1);
		endDate.set(year, 0, cutDay);
		saveBatch(startDate, endDate, company);

		for (int count = 0; count < 11; count++) {
			startDate.set(year, count, cutDay + 1);
			endDate.set(year, count + 1, cutDay);
			saveBatch(startDate, endDate, company);

		}
	}

	private void saveBatch(Calendar startDate, Calendar endDate, Company company) {
		TimesheetBatch lundinOtBatch = new TimesheetBatch();
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.HOUR, 0);
		endDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);
		DateTime fromDt = new DateTime(startDate.getTimeInMillis());
		MutableDateTime toDt = new MutableDateTime(endDate.getTimeInMillis());

		Timestamp startDateTmp = new Timestamp(fromDt.getMillis());
		Timestamp endDateTmp = new Timestamp(toDt.getMillis());

		String batchDescr = toDt.toString("MMM YYYY");
		// + PayAsiaConstants.OT_BATCH_DESCRIPTION;

		lundinOtBatch.setStartDate(startDateTmp);
		lundinOtBatch.setEndDate(endDateTmp);
		lundinOtBatch.setTimesheetBatchDesc(batchDescr + " Timesheet");
		lundinOtBatch.setCompany(company);

		lundinTimesheetBatchDAO.save(lundinOtBatch);
	}

	private void largerDateBatch(int cutDay, int year, boolean leapYear,
			Company company) {
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		int daysInMonth = 0;
		int useMaxLimit = 11;
		if (cutDay == 31) {
			useMaxLimit = 12;
		} else {

			startDate.set(Calendar.DAY_OF_MONTH, cutDay + 1);
			startDate.set(Calendar.YEAR, year - 1);
			startDate.set(Calendar.MONTH, 11);

			endDate.set(Calendar.DAY_OF_MONTH, cutDay);
			endDate.set(Calendar.YEAR, year);
			endDate.set(Calendar.MONTH, 0);
			saveBatch(startDate, endDate, company);
		}

		for (int month = 0; month < useMaxLimit; month++) {

			if (cutDay == 31) {
				startDate.set(Calendar.DAY_OF_MONTH, 1);
				startDate.set(Calendar.YEAR, year);
				startDate.set(Calendar.MONTH, month);
				int days = startDate.getActualMaximum(Calendar.DAY_OF_MONTH);
				endDate.set(Calendar.DAY_OF_MONTH, days);
				endDate.set(Calendar.YEAR, year);
				endDate.set(Calendar.MONTH, month);
			} else {
				if (month == 3 || month == 5 || month == 8 || month == 10)
					daysInMonth = 30;
				else if (month == 1)
					daysInMonth = (leapYear) ? 29 : 28;
				else
					daysInMonth = 31;
				if (month == 0) {
					startDate.set(Calendar.DAY_OF_MONTH, cutDay + 1);
					startDate.set(Calendar.YEAR, year);
					startDate.set(Calendar.MONTH, month);
					if (leapYear) {
						endDate.set(Calendar.DAY_OF_MONTH, 29);
					} else {
						endDate.set(Calendar.DAY_OF_MONTH, 28);
					}
					endDate.set(Calendar.YEAR, year);
					endDate.set(Calendar.MONTH, month + 1);
				} else if (month == 1) {
					startDate.set(Calendar.DAY_OF_MONTH, 1);
					startDate.set(Calendar.YEAR, year);
					startDate.set(Calendar.MONTH, month + 1);

					endDate.set(Calendar.DAY_OF_MONTH, cutDay);
					endDate.set(Calendar.YEAR, year);
					endDate.set(Calendar.MONTH, month + 1);
				} else {
					startDate.add(Calendar.DAY_OF_MONTH, daysInMonth);
					startDate.set(Calendar.DAY_OF_MONTH, cutDay + 1);
					startDate.set(Calendar.YEAR, year);
					startDate.set(Calendar.MONTH, month);

					endDate.add(Calendar.DAY_OF_MONTH, daysInMonth);
					endDate.set(Calendar.DAY_OF_MONTH, cutDay);
					endDate.set(Calendar.YEAR, year);
					endDate.set(Calendar.MONTH, month + 1);
				}
			}
			saveBatch(startDate, endDate, company);

		}
	}

	@Override
	public List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId,
			Long employeeId) {
		List<TimesheetBatch> lundinOTBatchList = new ArrayList<TimesheetBatch>();
		List<String> otStatusList = new ArrayList<String>();
		otStatusList.add("Rejected");
		otStatusList.add("Withdrawn");

		lundinOTBatchList = lundinTimesheetBatchDAO.getOTBacthesByStatus(
				companyId, employeeId, otStatusList);

		List<LundinOTBatchDTO> lundinOTBatchDTOList = new ArrayList<LundinOTBatchDTO>();

		for (TimesheetBatch lundinOTBatch : lundinOTBatchList) {
			LundinOTBatchDTO lundinOtBatchDto = new LundinOTBatchDTO();
			lundinOtBatchDto.setStartDate(lundinOTBatch.getStartDate());
			lundinOtBatchDto.setEndDate(lundinOTBatch.getEndDate());
			lundinOtBatchDto.setOtBatchDesc(lundinOTBatch
					.getTimesheetBatchDesc());
			/*ID ENCRYPT*/
			lundinOtBatchDto.setOtBatchId(FormatPreserveCryptoUtil.encrypt(lundinOTBatch.getTimesheetBatchId()));

			lundinOTBatchDTOList.add(lundinOtBatchDto);
		}
		return lundinOTBatchDTOList;
	}

	@Override
	public LundinOTBatchDTO getOTBatchById(Long otBachId,Long companyId,Long employeeId) {
		TimesheetBatch lundinOtBatch = lundinTimesheetBatchDAO
				.findById(otBachId,companyId,employeeId);
		LundinOTBatchDTO lundinOtBatchDto = new LundinOTBatchDTO();
		lundinOtBatchDto.setOtBatchId(lundinOtBatch.getTimesheetBatchId());
		lundinOtBatchDto.setStartDate(lundinOtBatch.getStartDate());
		lundinOtBatchDto.setEndDate(lundinOtBatch.getEndDate());
		lundinOtBatchDto.setOtBatchDesc(lundinOtBatch.getTimesheetBatchDesc());
		return lundinOtBatchDto;
	}

	@Override
	public int getCutOffDay(long companyId) throws Exception {
		return lundinTimesheetPreferenceDAO.findByCompanyId(companyId)
				.getCutoff_Day();
	}
}
