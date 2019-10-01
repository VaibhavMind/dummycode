package com.payasia.logic;

import java.util.Locale;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.EmployeeClaimAdjustmentReportDTO;
import com.payasia.common.form.EmployeeClaimAdjustmentForm;
import com.payasia.common.form.EmployeeClaimAdjustments;
import com.payasia.common.form.EmployeeEntitlements;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface EmployeeEntitlementsLogic {

	EmployeeEntitlements getEmployeeEntitlements(Long empId, PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String employeeNumber, long claimTemplateId, int year, Locale locale);

	String saveClaimAdjustment(EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm, Long companyId);

	String updateClaimAdjustment(EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm, Long companyId);

	EmployeeClaimAdjustmentReportDTO exportAdjustmentsExcelFile(Long companyId,
			EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm, Long employeeId);

	EmployeeClaimAdjustments getEmployeeClaimAdjustments(AddClaimDTO claimDTO);

	String deleteClaimAdjustment(Long employeeClaimAdjustmentId, Long companyId);

	EmployeeClaimAdjustments getEmployeeClaimAdjustmentsNew(AddClaimDTO claimDTO, PageRequest pageDTO,
			SortCondition sortDTO);

}
