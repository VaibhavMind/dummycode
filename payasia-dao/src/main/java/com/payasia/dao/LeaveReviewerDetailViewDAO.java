/**
 * @author vivekjain
 * 
 */
package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.LeaveReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveReviewerDetailView;

 
/**
 * The Interface LeaveReviewerDetailViewDAO.
 */

public interface LeaveReviewerDetailViewDAO {

	/**
	 * Find by condition LeaveReviewerConditionDTO and companyId for Search.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param pageDTO
	 *            the page dto
	 * @param sortDTO
	 *            the sort dto
	 * @param companyId
	 *            the company id
	 * @return the list
	 */
	List<LeaveReviewerDetailView> findByCondition(
			LeaveReviewerConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	/**
	 * Gets the count by condition.
	 * 
	 * @param conditionDTO
	 *            the condition dto
	 * @param companyId
	 *            the company id
	 * @return the count by condition
	 */
	Long getCountByCondition(LeaveReviewerConditionDTO conditionDTO,
			Long companyId);

	/**
	 * Gets the sort path for search employee.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param empRoot
	 *            the emp root
	 * @return the sort path for search employee
	 */
	Path<String> getSortPathForSearchEmployee(SortCondition sortDTO,
			Root<LeaveReviewerDetailView> employeeLeaveReviewerRoot);

}
