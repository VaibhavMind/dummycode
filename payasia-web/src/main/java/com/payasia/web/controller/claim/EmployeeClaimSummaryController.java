/**
 * @author vivekjain
 *
 */
package com.payasia.web.controller.claim;

import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * The Interface EmployeeClaimSummaryController.
 */

public interface EmployeeClaimSummaryController {

	String getEmployeeClaimAdjustments(long employeeClaimTemplateId, HttpServletRequest request);

	String getEmpClaimTemplateItemEntDetails(long employeeClaimTemplateId, String columnName, String sortingType,
			int page, int rows, HttpServletRequest request);

	String getEmpAllClaimItemEntitlement(long employeeClaimTemplateId, BigDecimal entitlement,
			HttpServletRequest request);

	String getEmpClaimSummaryDetails(int year, HttpServletRequest request, Locale locale);

}
