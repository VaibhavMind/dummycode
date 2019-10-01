package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.YearEndProcessingConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveYearEndBatch;

public interface LeaveYearEndBatchDAO {

	List<LeaveYearEndBatch> findByCondition(
			YearEndProcessingConditionDTO yearEndProcessingConditionDTO,
			int year, Long leaveTypeId, PageRequest pageDTO,
			SortCondition sortDTO);

	List<Integer> getYearFromProcessedDate(Long companyId);

	Integer getCountByCondition(
			YearEndProcessingConditionDTO yearEndProcessingConditionDTO);

}
