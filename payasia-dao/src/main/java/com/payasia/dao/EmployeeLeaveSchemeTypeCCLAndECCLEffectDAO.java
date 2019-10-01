package com.payasia.dao;

public interface EmployeeLeaveSchemeTypeCCLAndECCLEffectDAO {

	/*void saveAndReturnEffectiveECCLAndCCL(EmployeeLeaveSchemeTypeCCLAndECCLEffect ecclEffect);

	void updateEffectiveECCLAndCCL(EmployeeLeaveSchemeTypeCCLAndECCLEffect ecclEffectObj);*/

	void deleteByCondition(Long employeeLeaveSchemeTypeId, Long companyId);
	

}
