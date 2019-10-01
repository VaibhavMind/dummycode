package com.payasia.web.controller.claim;

import java.util.Locale;

import com.payasia.common.form.ClaimItemForm;
import com.payasia.common.form.ClaimItemsCategoryForm;

public interface ClaimItemDefController {

	String getClaimCatList();

	String getClaimItemData(Long itemId);

	String getClaimCatData(Long categoryId);

	String delClaimItem(Long itemId);

	String delClaimCategory(Long categoryId);

	void assignClaimTemplate(String[] claimTemplateId, Long claimItemId);

	String updateClaimItemSortOrder(String[] sortOrder);

	String viewClaimItems(String columnName, String sortingType, int page, int rows, String claimItemFilter,
			String filterText, Locale locale);

	String addClaimItem(ClaimItemForm claimItemForm);

	String viewClaimCategory(String columnName, String sortingType, int page, int rows);

	String addClaimCategory(ClaimItemsCategoryForm claimCatForm);

	String editClaimItem(ClaimItemForm claimItemForm);

	String editClaimCategory(ClaimItemsCategoryForm claimItemCatForm);

	String assignedClaimTemplates(int page, int rows, String columnName, String sortingType, Long claimItemId,
			Locale locale);

	String unAssignedClaimTemplates(int page, int rows, String columnName, String sortingType, Long claimItemId);

	String deleteClaimTemplateItem(Long claimTemplateItemId);

}
