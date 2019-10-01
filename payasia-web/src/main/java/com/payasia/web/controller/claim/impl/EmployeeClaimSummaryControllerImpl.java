/**
 * @author vivekjain
 *
 */
package com.payasia.web.controller.claim.impl;

import java.math.BigDecimal;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.EmployeeClaimAdjustments;
import com.payasia.common.form.EmployeeClaimSummaryResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.EmployeeClaimSummaryLogic;
import com.payasia.logic.EmployeeEntitlementsLogic;
import com.payasia.web.controller.claim.EmployeeClaimSummaryController;

/**
 * The Class EmployeeClaimSummaryControllerImpl.
 * 
 */

@Controller
@RequestMapping(value = { PayAsiaURLConstant.ADMIN_CLAIM_SUMMARY, PayAsiaURLConstant.EMPLOYEE_CLAIM_SUMMARY })
public class EmployeeClaimSummaryControllerImpl implements EmployeeClaimSummaryController {

	@Resource
	EmployeeClaimSummaryLogic employeeClaimSummaryLogic;

	@Resource
	EmployeeEntitlementsLogic employeeEntitlementsLogic;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EMP_CLAIM_SUMMARY_DETAILS, method = RequestMethod.POST)
	@ResponseBody
	public String getEmpClaimSummaryDetails(@RequestParam(value = "year", required = true) int year,
			HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setLocale(locale);
		claimDTO.setYear(year);
		claimDTO.setAdmin(isURLfromAdmin(request));
		
		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = employeeClaimSummaryLogic
				.getEmpClaimSummaryDetails(claimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(employeeClaimSummaryResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EMPLOYEE_CLAIM_ADJUSTMENTS, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeClaimAdjustments(
			@RequestParam(value = "employeeClaimTemplateId", required = false) long employeeClaimTemplateId,
		 HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		claimDTO.setAdmin(isURLfromAdmin(request));
		
		EmployeeClaimAdjustments employeeClaimAdjustments = employeeEntitlementsLogic.getEmployeeClaimAdjustments(claimDTO);
		employeeClaimAdjustments.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(employeeClaimAdjustments);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EMP_CLAIM_TEMPLATE_ITEM_ENT_DETAILS, method = RequestMethod.POST)
	@ResponseBody
	public String getEmpClaimTemplateItemEntDetails(
			@RequestParam(value = "employeeClaimTemplateId", required = false) long employeeClaimTemplateId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		claimDTO.setAdmin(isURLfromAdmin(request));

		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = employeeClaimSummaryLogic
				.getEmpClaimTemplateItemEntDetails(claimDTO, pageDTO, sortDTO);
		employeeClaimSummaryResponse.setPage(1);
		return ResponseDataConverter.getJsonURLEncodedData(employeeClaimSummaryResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EMP_ALL_CLAIM_ITEM_ENTITLEMENT, method = RequestMethod.POST)
	@ResponseBody
	public String getEmpAllClaimItemEntitlement(
			@RequestParam(value = "employeeClaimTemplateId", required = false) long employeeClaimTemplateId,
			@RequestParam(value = "entitlement", required = false) BigDecimal entitlement, HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		claimDTO.setAdmin(isURLfromAdmin(request));
		claimDTO.setEntitlement(entitlement);
		
		BigDecimal claimItemEntitlements = employeeClaimSummaryLogic.getEmpAllClaimItemEntitlement(claimDTO);
		
		return String.valueOf(claimItemEntitlements);
	}

	private boolean isURLfromAdmin(HttpServletRequest request) {

		String url = request.getRequestURL().toString();
		if (url != null && StringUtils.isNotBlank(url)) {
			return url.contains("/admin/");
		}
		return false;
	}
}
