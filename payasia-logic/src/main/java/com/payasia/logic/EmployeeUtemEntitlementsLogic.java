package com.payasia.logic;

import com.payasia.common.dto.EmployeeItemEntitlementDetailsDTO;
import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.EmployeeItemEntitlementForm;

public interface EmployeeUtemEntitlementsLogic {

	EmployeeItemEntitlementDetailsDTO getEmpItemEntitlementDetails(
			String claimTemplateName, Long companyId);

	void saveItemEntitlementDetails(Long companyId,
			EmployeeItemEntitlementForm employeeItemEntitlementForm);

	EmployeeItemEntitlementForm getItemDetails(String itemId,
			String employeeNo, String itemEntitlementlaimTemplate,
			Long companyId);

	void importItemEntitlementTemplate(Long companyId,
			AssignClaimTemplateForm importAssignClaimTemplateForm);

	void deleteItemEntitlementDetails(Long companyId,
			EmployeeItemEntitlementForm itemEntitlementForm);

}
