package com.payasia.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimItemBalanceDTO;
import com.payasia.common.dto.ValidationClaimItemDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimApplicationItem;

public interface ClaimApplicationItemDAO {

	void update(ClaimApplicationItem claimApplicationItem);

	void save(ClaimApplicationItem claimApplicationItem);

	void delete(ClaimApplicationItem claimApplicationItem);

	ClaimApplicationItem findById(long claimApplicationItemId);

	ClaimApplicationItem saveReturn(ClaimApplicationItem claimApplicationItem);

	List<ClaimApplicationItem> findByCondition(Long claimApplicationId, PageRequest pageDTO, SortCondition sortDTO);

	Integer getCountForCondition(Long claimApplicationId, PageRequest pageDTO, SortCondition sortDTO);

	ClaimItemBalanceDTO getClaimItemBal(Long employeeClaimTemplateItemId, Long companyId, Long claimApplicationId,
			Long claimApplicationItemId, boolean isPreviousYearClaim);

	void deleteByCondition(Long claimApplicationId);

	boolean openToDependentsExists(long companyId);


	ValidationClaimItemDTO validateClaimItem(ClaimApplicationItem claimApplicationItem, Boolean isAdmin,
			Boolean isManager);

	List<ClaimApplicationItem> findByCondition(Long companyId, String startDate, String endDate,
			List<Long> claimTemplateId, Long claimCategoryId, List<Long> claimItemId, List<String> claimStatusList,
			String groupByName, List<BigInteger> employeeIds);

	List<ClaimApplicationItem> findApplicationByBatch(Long companyId, Long claimBatchId, Timestamp claimBatchStartDate,
			Timestamp claimBatchEndDate, List<Long> claimTemplateIdList, Long claimCategoryId,
			List<Long> claimItemIdList, String groupByName, String claimStatusCompleted, List<BigInteger> employeeIds);

	List<ClaimApplicationItem> findByCategoryWiseCondition(Long companyId, String startDate, String endDate,
			List<Long> claimTemplateIdList, List<Long> claimCategoryId, List<Long> claimItemIdList,
			List<String> claimStatusList, String groupByName, List<BigInteger> employeeIds);

	List<ClaimApplicationItem> findByConditions(Long claimApplicationId, List<SortCondition> sortConditions,
			String desc2, Long categoryId);

	List<ClaimApplicationItem> findByCondition(Long claimApplicationId, List<SortCondition> sortConditions,
			String sortOrder);

	List<ClaimApplicationItem> findByCondition(Long claimApplicationId, String sortOrder);

	ClaimApplicationItem findByClaimApplicationItemId(AddClaimDTO addClaimDTO);

	ClaimApplicationItem findByClaimApplicationItemIdReview(AddClaimDTO addClaimDTO);
	
	List<ClaimApplicationItem> findByClaimReviewerCondition(Long companyId, String startDate, String endDate,
    List<Long> claimTemplateIdList, Long claimCategoryId, List<Long> claimItemIdList,
    List<String> claimStatusList, String groupByName, Long employeeId, Boolean isIncludeResignedEmployees,Boolean isIncludeSubordinateEmployees, Boolean isCheckedFromCreatedDate);


}
