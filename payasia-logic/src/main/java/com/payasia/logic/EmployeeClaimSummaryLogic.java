/**
 * @author vivekjain
 *
 */
package com.payasia.logic;

import java.math.BigDecimal;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.EmployeeClaimSummaryResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface EmployeeClaimSummaryLogic.
 */
public interface EmployeeClaimSummaryLogic {

	EmployeeClaimSummaryResponse getEmpClaimTemplateItemEntDetails(AddClaimDTO addClaimDTO, PageRequest pageDTO,SortCondition sortDTO);

	BigDecimal getEmpAllClaimItemEntitlement(AddClaimDTO claimDTO);

	EmployeeClaimSummaryResponse getEmpClaimSummaryDetails(AddClaimDTO claimDTO);

	EmployeeClaimSummaryResponse getEmpClaimSummaryWSDetails(AddClaimDTO claimDTO);

	EmployeeClaimSummaryResponse getEmpClaimTemplateItemEnt(AddClaimDTO addClaimDTO, PageRequest pageDTO,SortCondition sortDTO);

	EmployeeClaimSummaryResponse getEmpClaimSummary(AddClaimDTO claimDTO, PageRequest pageDTO,SortCondition sortDTO);

}
