package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.HRISReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.HRISReviewerDetailView;

public interface HRISReviewerDetailViewDAO {

	List<HRISReviewerDetailView> findByCondition(
			HRISReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	int getCountByCondition(HRISReviewerConditionDTO conditionDTO,
			Long companyId);

}
