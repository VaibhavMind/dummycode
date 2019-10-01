package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimCategoryMaster;

public interface ClaimCategoryDAO {

	List<ClaimCategoryMaster> getCategoryAll(long companyId);

	ClaimCategoryMaster findCategoryById(long categoryId);

	List<ClaimCategoryMaster> getClaimCategory(PageRequest pageDTO,
			SortCondition sortDTO, Long companyID);

	Path<String> getSortPathForclaimCat(SortCondition sortDTO,
			Root<ClaimCategoryMaster> claimCatRoot);

	void saveClaimCat(ClaimCategoryMaster claimCatMaster);

	void update(ClaimCategoryMaster claimCatMaster);

	void delete(ClaimCategoryMaster claimCatMaster);

	ClaimCategoryMaster findByNameCompany(Long categoryId, String categoryName,
			Long companyId);

	ClaimCategoryMaster findByCodeCompany(Long categoryId, String categoryCode,
			Long companyId);

	ClaimCategoryMaster findByClaimCategoryId(Long categoryId, Long companyId);

}
