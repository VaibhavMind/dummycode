package com.payasia.logic;

import java.util.List;
import java.util.Map;

import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.LionTimesheetPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LionTimesheetPreference;

public interface LionTimesheetPreferenceLogic {

	/*
	 * LundinTimesheetPreferenceForm getLionHKTimesheetPreference(Long
	 * companyId);
	 */

	void saveLionTimesheetPreference(
			LionTimesheetPreferenceForm lionTimesheetPreferenceForm,
			Long companyId);

	List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId,
			Long employeeId);

	LionTimesheetPreference getByCompanyId(long companyId) throws Exception;

	void createOTBatches(LionTimesheetPreference lionTimesheetPreference,
			int yearOfBatch, long companyId);

	Map<String, Integer> getNumberofBatches(String codeDesc);

	AddClaimFormResponse getAllTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText);

	LionTimesheetPreferenceForm getLionTimesheetPreference(Long companyId);

	AddClaimFormResponse getSubmittedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText);

	AddClaimFormResponse getApprovedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText);

	AddClaimFormResponse getPendingTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText);

	Long getEmployeeIdByNumber(String employeeNumber, Long companyId);

}
