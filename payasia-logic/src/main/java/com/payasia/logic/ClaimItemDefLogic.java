package com.payasia.logic;

import java.util.List;
import java.util.Locale;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.ClaimItemForm;
import com.payasia.common.form.ClaimItemResponse;
import com.payasia.common.form.ClaimItemsCategoryForm;
import com.payasia.common.form.ClaimItemsCategoryResponse;
import com.payasia.common.form.ClaimTemplateResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface ClaimItemDefLogic {

	String addClaimItem(ClaimItemForm claimItemForm, Long companyId);

	ClaimItemsCategoryResponse viewClaimCategory(PageRequest pageDTO, SortCondition sortDTO, Long companyID);

	String addClaimCategory(ClaimItemsCategoryForm claimCatForm, Long companyId);

	String editClaimItem(ClaimItemForm claimItemForm, Long companyId);

	String editClaimCategory(ClaimItemsCategoryForm claimItemCatForm, Long companyId);

	ClaimTemplateResponse assignedClaimTemplates(Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			Long claimItemId, Locale locale);

	ClaimTemplateResponse unAssignedClaimTemplates(Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			Long claimItemId);

	ClaimItemResponse viewClaimItems(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	void updateClaimItemSortOrder(String[] sortOrder, Long companyId);

	List<ClaimItemForm> getCategoryList(Long companyId);

	ClaimItemForm getClaimItemById(Long itemId, Long companyId);

	ClaimItemsCategoryForm getClaimCatById(Long categoryId, Long companyId);

	void deleteClaimItem(Long itemId, Long companyId);

	String deleteClaimCategory(Long categoryId, Long companyId);

	void assignClaimTemplate(String[] claimTemplateId, Long claimItemId, Long companyId);

	void deleteClaimTemplateItem(Long claimTemplateItemId, Long companyId);

}
