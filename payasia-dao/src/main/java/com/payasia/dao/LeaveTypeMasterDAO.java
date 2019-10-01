package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.LeaveTypeMasterConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveTypeMaster;

public interface LeaveTypeMasterDAO {
	List<LeaveTypeMaster> findAll(long companyId, PageRequest pageDTO,
			SortCondition sortDTO);

	List<LeaveTypeMaster> findByCondition(
			LeaveTypeMasterConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Path<String> getSortPathForLeaveType(SortCondition sortDTO,
			Root<LeaveTypeMaster> leaveTypeRoot);

	Long getCountByCondition(LeaveTypeMasterConditionDTO conditionDTO);

	LeaveTypeMaster findById(Long leaveTypeId);

	void update(LeaveTypeMaster leaveTypeMaster);

	void delete(LeaveTypeMaster leaveTypeMaster);

	void save(LeaveTypeMaster leaveTypeMaster);

	LeaveTypeMaster findByLeaveIdAndName(Long leaveTypeId, String leaveName,
			Long companyId, String leaveCode);

	List<LeaveTypeMaster> findByCompanyAndVisibility(long companyId);

	List<LeaveTypeMaster> findByConditionAndVisibility(Long leaveSchemeId,
			Long companyId);

	Integer getMaxLeaveTypeSortOrder(Long companyId);

	List<LeaveTypeMaster> getAllLeaveTypes(Long companyId);

	List<Tuple> getLeaveTypeNameTupleList(Long companyId);

	List<LeaveTypeMaster> getAllLeaveTypesBasedOnGroupId(Long groupId);

	LeaveTypeMaster findLeaveTypeByCompId(Long leaveTypeId, Long companyId);

}
