/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.LeaveTypeDTO;
import com.payasia.common.form.LeaveTypeForm;
import com.payasia.common.form.LeaveTypeResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface LeaveTypeLogic {

	LeaveTypeResponse getLeaveTypeFormList(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	void deleteLeaveType(Long leaveTypeId);

	String saveLeaveType(LeaveTypeForm leaveTypeForm, Long companyId);

	String updateLeaveType(LeaveTypeForm leaveTypeForm, Long leaveTypeId,
			Long companyId);

	void deleteLeaveScheme(Long leaveSchemeId, Long leaveSchemeTypeId,
			Long companyId);

	LeaveTypeForm editLeaveType(Long leaveTypeId);

	void updateLeaveTypeSortOrder(String[] sortOrder);

	List<LeaveTypeDTO> getAllLeaveTypes(Long companyId);

	LeaveTypeForm getLeaveTypeAppcodeList();

	void addLeaveScheme(String[] leaveSchemeId, Long leaveTypeId, Long companyId);

	LeaveTypeForm editLeaveType(Long leaveTypeId, Long companyId);
}
