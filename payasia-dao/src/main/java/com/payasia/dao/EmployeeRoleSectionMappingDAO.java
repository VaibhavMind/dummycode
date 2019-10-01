package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmployeeRoleSectionMapping;

/**
 * @author vivekjain
 * 
 */
public interface EmployeeRoleSectionMappingDAO {

	void update(EmployeeRoleSectionMapping employeeRoleSectionMapping);

	void delete(EmployeeRoleSectionMapping employeeRoleSectionMapping);

	void save(EmployeeRoleSectionMapping employeeRoleSectionMapping);

	EmployeeRoleSectionMapping findById(Long employeeRoleSectionMappingId);

	void deleteByConditionRoleAndEmpId(long employeeId, long roleId);

	List<EmployeeRoleSectionMapping> findByCondition(Long roleId,
			Long employeeId, Long companyId);

	void deleteByCondition(Long employeeId, Long roleId, Long companyId,
			Long sectionId);

	void deleteByCondition(Long employeeId, Long roleId, Long companyId);

	List<Long> findByCondition(Long employeeId, Long companyId);

	void deleteByFormIdAndCompanyId(Long formId, Long companyId);

}
