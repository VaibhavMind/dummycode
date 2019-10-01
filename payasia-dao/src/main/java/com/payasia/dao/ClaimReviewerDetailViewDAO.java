package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.ClaimReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimReviewerDetailView;

public interface ClaimReviewerDetailViewDAO {

	List<ClaimReviewerDetailView> findByCondition(
			ClaimReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<ClaimReviewerDetailView> employeeLeaveReviewerRoot);

	Long getCountByCondition(ClaimReviewerConditionDTO conditionDTO,
			Long companyId);

}
