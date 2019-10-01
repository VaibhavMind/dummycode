package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimCategoryMaster;
import com.payasia.dao.bean.ClaimItemMaster;

public interface ClaimItemMasterDAO {

	List<ClaimItemMaster> getClaimItemByCondition(PageRequest pageDTO,
			SortCondition sortDTO, ClaimItemDTO claimItemDTO, Long companyID);

	Path<String> getSortPathForclaimItem(SortCondition sortDTO,
			Root<ClaimItemMaster> claimItemRoot,
			Join<ClaimItemMaster, ClaimCategoryMaster> claimItemCategoryJoin);

	void saveClaimItem(ClaimItemMaster claimItemMaster);

	ClaimItemMaster findByID(Long itemId);

	void update(ClaimItemMaster claimItemMaster);

	void delete(ClaimItemMaster claimItemMaster);

	ClaimItemMaster findById(Long claimTypeId);

	List<ClaimItemMaster> findAll(Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, Long itemCategoryId);

	ClaimItemMaster findByNameCategoryCompany(Long claimItemId,
			String itemName, Long categoryId, Long companyId);

	ClaimItemMaster findByCodeCategoryCompany(Long claimItemId,
			String itemCode, Long categoryId, Long companyId);

	List<ClaimItemMaster> findByCategoryId(Long categoryId);

	List<ClaimItemMaster> findByCompany(Long companyId);

	Integer getMaxClaimTypeSortOrder(Long companyId);

	ClaimItemMaster findByClaimItemMasterId(Long claimItemId, Long companyId);

	List<ClaimItemMaster> findByCondition(Long claimTemplateId, Long companyId);

}
