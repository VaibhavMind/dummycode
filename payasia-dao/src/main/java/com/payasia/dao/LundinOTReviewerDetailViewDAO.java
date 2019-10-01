package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.OTReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LundinOTReviewerDetailView;

public interface LundinOTReviewerDetailViewDAO {

	List<LundinOTReviewerDetailView> findByCondition(
			OTReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	int getCountByCondition(OTReviewerConditionDTO conditionDTO, Long companyId);

}
