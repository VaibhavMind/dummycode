package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LundinDepartment;

public interface LundinDepartmentDAO {
	LundinDepartment findById(long id);

	void save(LundinDepartment department);

	void update(LundinDepartment department);

	void delete(long id);

	Long getCountForCondition(LundinConditionDTO conditionDTO);

	List<LundinDepartment> findByCondition(LundinConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	LundinDepartment findByDescCompany(Long deptId, String departmentCode,
			long companyId);
	List<LundinDepartment> findByCondition(Long companyId);
	LundinDepartment findByDynamicFieldId(long dynamicFieldId);

	List<LundinDepartment> getAllEntries(Long compId);

	long saveAndReturn(LundinDepartment timesheet);

	LundinDepartment findByDepartmentCompanyId(Long deptId, long companyId);
	
}
