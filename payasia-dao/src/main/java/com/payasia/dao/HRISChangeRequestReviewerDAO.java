/**
 * @author vivekjain
 * 
 */
package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.HrisPendingItemsConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.HRISChangeRequestReviewer;

public interface HRISChangeRequestReviewerDAO {

	void update(HRISChangeRequestReviewer hrisChangeRequestReviewer);

	void delete(HRISChangeRequestReviewer hrisChangeRequestReviewer);

	void save(HRISChangeRequestReviewer hrisChangeRequestReviewer);

	HRISChangeRequestReviewer findById(Long hrisChangeRequestReviewerId);

	List<HRISChangeRequestReviewer> findHRISChangeRequestReviewers(
			Long employeeId, PageRequest pageDTO, SortCondition sortDTO,
			HrisPendingItemsConditionDTO hrisPendingItemsConditionDTO);

	Integer getCountHRISChangeRequestReviewers(
			HrisPendingItemsConditionDTO conditionDTO);

	List<HRISChangeRequestReviewer> checkHRISEmployeeReviewer(long employeeId,
			List<String> hrisStatusList);

	List<HRISChangeRequestReviewer> findByCondition(Long employeeId,
			PageRequest pageDTO, SortCondition sortDTO);

	List<HRISChangeRequestReviewer> findByConditionPosition(Long employeeId, PageRequest pageDTO, SortCondition sortDTO,
			int startPos);
	
	

}
