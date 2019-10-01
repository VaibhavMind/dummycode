package com.payasia.api.common.claim.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.common.claim.CommonClaimApi;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.MyClaimLogic;
import com.payasia.logic.PendingClaimsLogic;

/**
 * @author gauravkumar
 * @param : This class used for My Claim APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_CLAIM, produces={"application/json"}, consumes={"application/json"})
public class CommonClaimApiImpl implements CommonClaimApi{

	@Resource
	private MyClaimLogic myClaimLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private PendingClaimsLogic pendingClaimsLogic;
	
	@Resource
	private CompanyInformationLogic companyInformationLogic;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Override
	@PostMapping(value = "view-claim-template-item")
	public ResponseEntity<?> searchClaimTemplateItem(@RequestParam(value = "claimApplicationId", required = true) Long claimApplicationId, @RequestBody SearchParam searchParamObj) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		addClaimDTO.setLocale(UserContext.getLocale());
	
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.searchClaimTemplateItems(addClaimDTO, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), messageSource);
		
		if(addClaimFormResponse.getClaimApplicationItemForm()!=null && !addClaimFormResponse.getClaimApplicationItemForm().isEmpty()) {
			return new ResponseEntity<>(addClaimFormResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "claim-reviewers")
	public ResponseEntity<?> claimReviewers(@RequestParam(value = "claimApplicationId", required = true) Long claimApplicationId) {
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(Long.parseLong(UserContext.getWorkingCompanyId()));
		claimDTO.setEmployeeId(Long.parseLong(UserContext.getUserId()));
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setLocale(UserContext.getLocale());
		claimDTO.setAdmin(false);
		
		AddClaimForm claimsForm = pendingClaimsLogic.getClaimReviewersData(claimDTO);
		
		if(claimsForm == null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		Map<String, Object> claimReviewerMap = new HashMap<>();
		claimReviewerMap.put("map", claimsForm.getClaimReviewer1Img());
		List<Map<String, Object>> listMap = setClaimReviewer(claimsForm);
		return new ResponseEntity<>(listMap, HttpStatus.OK);
	}
	
	
	private List<Map<String, Object>> setClaimReviewer(AddClaimForm claimsForm) {
		List<Map<String, Object>> listMap = new ArrayList<>();
		Map<String, Object> listMapObj = null; 

		if (claimsForm.getClaimReviewer1() != null) {
			listMapObj = new HashMap<>();
			listMapObj.put("reviewerNumber", 1);
			listMapObj.put("empName", claimsForm.getClaimReviewer1());
			listMapObj.put("empImage", claimsForm.getClaimReviewer1Img());
			listMapObj.put("statusName", "");
			listMap.add(listMapObj);
			

		}
		if (claimsForm.getClaimReviewer2() != null) {
			listMapObj = new HashMap<>();
			listMapObj.put("reviewerNumber", 2);
			listMapObj.put("empName", claimsForm.getClaimReviewer2());
			listMapObj.put("empImage", claimsForm.getClaimReviewer2Img());
			listMapObj.put("statusName", "");
			listMap.add(listMapObj);
		}
		if (claimsForm.getClaimReviewer3() != null) {
			listMapObj = new HashMap<>();
			listMapObj.put("reviewerNumber", 3);
			listMapObj.put("empName", claimsForm.getClaimReviewer3());
			listMapObj.put("empImage", claimsForm.getClaimReviewer3Img());
			listMapObj.put("statusName", "");
			listMap.add(listMapObj);
		}
		return listMap;

	}
	
	
	@Override
	@GetMapping(value = "currency-name-list")
	public ResponseEntity<?> getCurrencyNameList() {
		Map<String, Object> currencyNameListMap = new HashMap<>(); 
		
		List<CompanyForm> currencyNameList = companyInformationLogic.getCurrencyName();
		currencyNameListMap.put("currencyNameList", currencyNameList);

		return new ResponseEntity<>(currencyNameList, HttpStatus.OK);
	}

}

