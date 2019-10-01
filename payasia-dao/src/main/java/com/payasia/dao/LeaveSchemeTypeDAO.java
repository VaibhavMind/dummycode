package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.LeaveGrantProcDTO;
import com.payasia.common.dto.RollBackProcDTO;
import com.payasia.dao.bean.LeaveSchemeType;

public interface LeaveSchemeTypeDAO {

	LeaveSchemeType findById(Long leaveSchemeTypeId);

	void update(LeaveSchemeType leaveSchemeType);

	void save(LeaveSchemeType leaveSchemeType);

	void delete(LeaveSchemeType leaveSchemeType);

	LeaveSchemeType findBySchemeType(Long leaveSchemeId, Long leaveTypeId);

	Long findByConditionCount(Long leaveSchemeId, Long companyId);

	LeaveSchemeType saveReturn(LeaveSchemeType leaveSchemeType);

	List<LeaveSchemeType> findByLeaveSchemeAndEmployee(Long leaveSchemeId,
			Long employeeId, Long companyId);

	List<LeaveSchemeType> findByCompany(Long companyId);

	List<LeaveSchemeType> findByConditionLeaveScheme(Long leaveSchemeId,
			Long leaveSchemeTypeId, Long companyId);

	List<LeaveSchemeType> findByCondition(Long leaveSchemeId, Long companyId);

	Boolean callLeaveGrantProc(LeaveGrantProcDTO leaveGrantProcDTO);

	List<LeaveSchemeType> findByConditionLeaveSchemeIdCompanyId(
			Long leaveSchemeId, Long companyId);

	RollBackProcDTO rollbackResignedEmployeeLeaveProc(long employeeId,
			long loggedInEmployeeId);

	LeaveSchemeType findByLeaveSchemeAndTypeName(String leaveSchemeName,
			String leaveTypeName, Long companyId);

	List<LeaveSchemeType> findLeaveSchTypeByCmpId(long companyId);

	RollBackProcDTO childCareLeaveEntitlementProc(long companyId,
			long employeeId, String leaveSchemeTypeIds);

	LeaveSchemeType findLeaveSchemeTypeByCompId(Long leaveSchemeTypeId, Long companyId);

}
