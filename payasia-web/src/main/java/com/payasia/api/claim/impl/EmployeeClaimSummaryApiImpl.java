package com.payasia.api.claim.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.claim.EmployeeClaimSummaryApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.EmployeeClaimAdjustments;
import com.payasia.common.form.EmployeeClaimSummaryResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.EmployeeClaimSummaryLogic;
import com.payasia.logic.EmployeeEntitlementsLogic;

/**
 * @author gauravkumar
 * @param : This class used for Claim Summary APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_CLAIM+ApiUtils.EMPLOYEE_CLAIM_SUMMARY, produces={"application/json"}, consumes={"application/json"})
public class EmployeeClaimSummaryApiImpl implements EmployeeClaimSummaryApi {

	@Resource
	private EmployeeClaimSummaryLogic employeeClaimSummaryLogic;
	
	@Resource
	private EmployeeEntitlementsLogic employeeEntitlementsLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Override
	@PostMapping(value = "summary-details")
	public ResponseEntity<?> getEmpClaimSummaryDetails(@RequestBody SearchParam searchParamObj) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		int year = 0;
		if (filterllist != null && !filterllist.isEmpty()) {
			year = Integer.valueOf(filterllist.get(0).getValue());
		}
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setLocale(UserContext.getLocale());
		claimDTO.setYear(year);
		
		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = employeeClaimSummaryLogic.getEmpClaimSummary(claimDTO,searchSortDTO.getPageRequest(),searchSortDTO.getSortCondition());
		if(employeeClaimSummaryResponse.getRows()!=null && !employeeClaimSummaryResponse.getRows().isEmpty()) {
			return new ResponseEntity<>(employeeClaimSummaryResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "adjustments")
	public ResponseEntity<?> getEmpClaimAdjustments(@RequestParam(value = "employeeClaimTemplateId", required = true) long employeeClaimTemplateId, @RequestBody SearchParam searchParamObj) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		EmployeeClaimAdjustments employeeClaimAdjustments = employeeEntitlementsLogic.getEmployeeClaimAdjustmentsNew(claimDTO,searchSortDTO.getPageRequest(),searchSortDTO.getSortCondition());
		if(employeeClaimAdjustments.getRows()!=null && !employeeClaimAdjustments.getRows().isEmpty()) {
			return new ResponseEntity<>(employeeClaimAdjustments, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "template-item-entitilement-details")
	public ResponseEntity<?> getEmpClaimTemplateItemEntDetails(@RequestParam(value = "employeeClaimTemplateId", required = true) long employeeClaimTemplateId, @RequestBody SearchParam searchParamObj) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);	
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		EmployeeClaimSummaryResponse employeeClaimSummaryResponse = employeeClaimSummaryLogic.getEmpClaimTemplateItemEnt(claimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
		if(employeeClaimSummaryResponse.getRows()!=null && !employeeClaimSummaryResponse.getRows().isEmpty()) {
			return new ResponseEntity<>(employeeClaimSummaryResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "item-entitlement")
	public ResponseEntity<?> getEmpAllClaimItemEntitlement(@RequestParam(value="employeeClaimTemplateId", required = true) long employeeClaimTemplateId, @RequestParam(value = "entitlement", required = false) BigDecimal entitlement) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddClaimDTO claimDTO= new AddClaimDTO();
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		claimDTO.setEntitlement(entitlement);
		BigDecimal claimItemEntitlements = employeeClaimSummaryLogic.getEmpAllClaimItemEntitlement(claimDTO);
		if(claimItemEntitlements != null) {
		  return new ResponseEntity<>(claimItemEntitlements, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
}
