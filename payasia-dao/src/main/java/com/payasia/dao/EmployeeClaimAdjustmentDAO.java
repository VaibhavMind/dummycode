package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.EmployeeEntitlementDTO;
import com.payasia.dao.bean.EmployeeClaimAdjustment;

public interface EmployeeClaimAdjustmentDAO {

	void save(EmployeeClaimAdjustment employeeClaimAdjustment);

	void delete(EmployeeClaimAdjustment leaveScheme);

	void update(EmployeeClaimAdjustment employeeClaimAdjustment);

	Integer getCountForCondition(long employeeClaimTemplateId, Long companyId);

	EmployeeClaimAdjustment findByID(long employeeClaimAdjustmentId);

	List<EmployeeEntitlementDTO> callEmployeeClaimEntitlementSummary(
			Long companyId, String employeeNumber, Long claimTemplateId,
			int year);

	List<EmployeeClaimAdjustment> findByEffectiveDate(Long companyId,
			Timestamp startDate, Timestamp endDate);

	List<EmployeeClaimAdjustment> findByEmployeeClaimTemplateId(AddClaimDTO claimDTO);

}
