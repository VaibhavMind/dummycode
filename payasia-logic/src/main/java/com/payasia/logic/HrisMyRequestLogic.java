package com.payasia.logic;

import com.payasia.common.form.HrisChangeRequestForm;
import com.payasia.common.form.HrisMyRequestFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface HrisMyRequestLogic {

	HrisMyRequestFormResponse getSubmittedRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId);

	HrisMyRequestFormResponse getApprovedRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId);

	HrisMyRequestFormResponse getRejectedRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId);

	HrisMyRequestFormResponse getWithdrawnRequest(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO, String pageContextPath,
			String searchCondition, String searchText, Long languageId);

	HrisChangeRequestForm viewChangeRequest(Long hrisChangeRequestId, Long employeeId,
			Long languageId);

	HrisChangeRequestForm withdrawChangeRequest(Long hrisChangeRequestId,
			Long employeeId, Long languageId);

	Long getEmployeeIdByCode(String employeeNumber, Long companyID);

}
