package com.payasia.logic;

import com.payasia.common.form.HrisPendingItemWorkflowRes;
import com.payasia.common.form.HrisPendingItemsForm;
import com.payasia.common.form.HrisPendingItemsFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface HrisPendingItemsLogic {

	HrisPendingItemWorkflowRes searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String empName,
			String empNumber, Long companyId);

	HrisPendingItemsFormResponse getPendingItems(Long employeeId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long languageId);

	HrisPendingItemsForm reviewHrisPendingItem(Long reviewId, Long companyId,
			Long languageId,Long employeeId);

	HrisPendingItemWorkflowRes forward(
			HrisPendingItemsForm hrisPendingItemsForm, Long employeeId,
			Long languageId);

	HrisPendingItemWorkflowRes reject(
			HrisPendingItemsForm hrisPendingItemsForm, Long employeeId,
			Long languageId);

	HrisPendingItemWorkflowRes accept(
			HrisPendingItemsForm hrisPendingItemsForm, Long employeeId,
			Long languageId);

	HrisPendingItemsFormResponse getAllPendingItems(Long employeeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText, Long languageId);
	

}
