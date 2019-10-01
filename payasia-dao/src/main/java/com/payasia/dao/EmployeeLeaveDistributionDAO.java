/**
 * @author vivekjain
 *
 */
package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeLeaveDistribution;
import com.payasia.dao.bean.EmployeeLeaveScheme;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveTypeMaster;

/**
 * The Interface EmployeeLeaveDistributionDAO.
 */

public interface EmployeeLeaveDistributionDAO {

	void save(EmployeeLeaveDistribution employeeLeaveDistribution);

	EmployeeLeaveDistribution findById(Long employeeLeaveDistributionId);

	void update(EmployeeLeaveDistribution employeeLeaveDistribution);

	void delete(EmployeeLeaveDistribution employeeLeaveDistribution);

	List<EmployeeLeaveDistribution> findByCondition(PageRequest pageDTO,
			SortCondition sortDTO, Long empLeaveSchemeId, Integer year);

	Integer getCountForCondition(PageRequest pageDTO, SortCondition sortDTO,
			Long empLeaveSchemeId, Integer year);

	Path<String> getSortPathForEmpLeaveDistribution(
			SortCondition sortDTO,
			Root<EmployeeLeaveDistribution> empRoot,
			Join<EmployeeLeaveSchemeType, EmployeeLeaveScheme> empLeaveSchemeJoin,
			Join<LeaveSchemeType, LeaveTypeMaster> leaveTypeJoin);

	void deleteByCondition(Long employeeLeaveSchemeTypeId);

	EmployeeLeaveDistribution findByCondition(Long empLeaveSchemeTypeId,
			Integer year);

}
