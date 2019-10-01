package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;

import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LeaveReviewerConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeLeaveReviewer;

public interface EmployeeLeaveReviewerDAO {

	void update(EmployeeLeaveReviewer employeeLeaveReviewer);

	void save(EmployeeLeaveReviewer employeeLeaveReviewer);

	void delete(EmployeeLeaveReviewer employeeLeaveReviewer);

	List<EmployeeLeaveReviewer> findByCondition(LeaveReviewerConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	Long getCountByCondition(LeaveReviewerConditionDTO conditionDTO, Long companyId);

	EmployeeLeaveReviewer findById(long employeeLeaveReviewerId);

	List<EmployeeLeaveReviewer> findByLeaveSchemeId(Long leaveSchemeId);

	List<EmployeeLeaveReviewer> findByLeaveSchemeIdAndWorkFlowId(Long leaveSchemeId, Long workFlowRuleId);

	List<EmployeeLeaveReviewer> findByEmpLeaveSchemeAndLeaveTypeId(Long employeeId, Long empLeaveSchemeId, Long empLeaveSchemeTypeId, Long companyId);

	List<EmployeeLeaveReviewer> findByLeaveSchemeTypeId(Long leaveSchemeTypeId);

	List<EmployeeLeaveReviewer> findByEmpLeaveSchemeId(Long employeeLeaveSchemeId);

	List<EmployeeLeaveReviewer> findByCondition(Long employeeId, Long employeeLeaveSchemeId, Long employeeLeaveSchemeTypeId, Long companyId);

	void deleteByCondition(Long employeeId, Long employeeLeaveSchemeId, Long employeeLeaveSchemeTypeId);

	List<EmployeeLeaveReviewer> findByEmployeeLeaveSchemeID(Long employeeLeaveSchemeTypeId);

	List<EmployeeLeaveReviewer> checkEmployeeReviewer(Long employeeId);

	List<EmployeeLeaveReviewer> getEmployeeIdsForLeaveReviewer(String searchString, Long companyId, Long employeeId,
			EmployeeShortListDTO employeeShortListDTO);

	List<Tuple> getEmployeeIdsTupleForLeaveReviewer(String searchString, Long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO);

	List<Tuple> findEmpsByCondition(EmployeeConditionDTO conditionDTO, PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long employeeId);

	List<Tuple> getEmployeeIdsTupleForManager(Long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO);

	List<Object[]> getEmployeeReviewersCountByCondition(Long employeeId, Long employeeLeaveSchemeId, Long workFlowRuleId);

	void deleteByConditionEmpId(Long employeeId, Long employeeLeaveSchemeId);

	Tuple getEmployeeIdTupleForManager(String employeeNumber, Long companyId, Long employeeId, EmployeeShortListDTO employeeShortListDTO);

	List<Tuple> getEmployeeReviewersList(Long companyId, EmployeeShortListDTO employeeShortListDTO);

	List<EmployeeLeaveReviewer> findByLeaveApplicationId(Long leaveApplicationId);

	List<Long> getLeaveReviewerList(Long companyId);

}
