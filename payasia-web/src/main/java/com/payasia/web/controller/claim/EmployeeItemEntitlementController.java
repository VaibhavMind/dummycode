package com.payasia.web.controller.claim;

import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.EmployeeItemEntitlementForm;

public interface EmployeeItemEntitlementController {

	String getClaimItemList(String claimTemplateName);

	String getItemDetails(String itemId, String employeeNumber, String claimTemplate);

	String saveItemEntitlementDetails(EmployeeItemEntitlementForm itemEntitlementForm);

	String importItemEntitlementTemplate(AssignClaimTemplateForm importAssignClaimTemplateForm) throws Exception;

	String deleteItemEntitlementDetails(EmployeeItemEntitlementForm itemEntitlementForm);

}
