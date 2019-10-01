package com.payasia.dao;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.LeaveSchemeConditionDTO;
import com.payasia.common.dto.LeaveSchemeProcDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveScheme;

public interface LeaveSchemeDAO {

	void update(LeaveScheme leaveScheme);

	void save(LeaveScheme leaveScheme);

	void delete(LeaveScheme leaveScheme);

	LeaveScheme findByID(long leaveSchemeId);

	Path<String> getSortPathForleaveScheme(SortCondition sortDTO,
			Root<LeaveScheme> leaveSchemeRoot);

	Long getCountForAllLeaveScheme(Long companyId,
			LeaveSchemeConditionDTO conditionDTO);

	List<LeaveScheme> getAllLeaveSchemeByConditionCompany(Long companyId,
			LeaveSchemeConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	LeaveScheme findByLeaveSchemeAndCompany(Long leaveSchemeId,
			String leaveSchemeName, Long companyId);

	List<LeaveScheme> getAllLeaveScheme(Long companyId);

	LeaveScheme saveReturn(LeaveScheme leaveScheme);

	LeaveSchemeProcDTO callRedistributeProc(Long leaveSchemeTypeId);

	List<LeaveScheme> getAllLeaveSchemes(Long companyId);

	List<LeaveScheme> getAllLeaveSchemeBasedOnGroupId(Long groupId);

	List<Tuple> getLeaveSchemeNameTupleList(Long companyId);

	LeaveScheme findSchemeByCompanyID(Long leaveSchemeId, Long companyId);

}
