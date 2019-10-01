package com.payasia.api.hris.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.EmployeePendingItemsApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.HrisPendingItemWorkflowRes;
import com.payasia.common.form.HrisPendingItemsForm;
import com.payasia.common.form.HrisPendingItemsFormResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.HrisMyRequestLogic;
import com.payasia.logic.HrisPendingItemsLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Pending Items APIs
 * 
 */
@RestController
// TODO : Request mapping root URL get the value of ApiUtil
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class EmployeePendingItemsApiImpl implements EmployeePendingItemsApi {

	@Resource
	private HrisPendingItemsLogic hrisPendingItemsLogic;

	@Resource
	private HrisMyRequestLogic hrisMyRequestLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;

	@Override
	@PostMapping(value = "pending-items")
	public ResponseEntity<?> getPendingItems(@RequestBody SearchParam searchParamObj) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());

		Long languageId = UserContext.getLanguageId();
		HrisPendingItemsFormResponse hrisPendingItemsFormResponse = null;

		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		if ((filterllist != null && !filterllist.isEmpty())) {
			hrisPendingItemsFormResponse = hrisPendingItemsLogic.getAllPendingItems(employeeId, companyId, searchSortDTO.getPageRequest(),
					searchSortDTO.getSortCondition(), filterllist.get(0).getField(), filterllist.get(0).getValue(), languageId);

		} else {
			hrisPendingItemsFormResponse = hrisPendingItemsLogic.getAllPendingItems(employeeId, companyId, searchSortDTO.getPageRequest(),
					searchSortDTO.getSortCondition(), null, null, languageId);
		}

		if (hrisPendingItemsFormResponse != null) {
			return new ResponseEntity<>(hrisPendingItemsFormResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "review-pending-item")
	public ResponseEntity<?> reviewHrisPendingItem(@RequestBody String jsonStr) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Long languageId = UserContext.getLanguageId();

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long reviewId = jsonObj.getLong("reviewId");
		reviewId = FormatPreserveCryptoUtil.decrypt(reviewId);

		HrisPendingItemsForm hrisPendingItemsForm = hrisPendingItemsLogic.reviewHrisPendingItem(reviewId, companyId, languageId, employeeId);

		return new ResponseEntity<>(hrisPendingItemsForm, HttpStatus.OK);
	}

	/**
	 * PERFORMS THE ACTION ON PENDING ITEMS ACCORDING TO THE REQUEST TYPE
	 */
	@Override
	@PostMapping(value = "process-my-action")
	public ResponseEntity<?> getDesiredItemsAction(@RequestBody HrisPendingItemsForm hrisPendingItemsForm, @RequestParam(value = "requestType", required = true) String requestType) {

		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Long languageId = UserContext.getLanguageId();

		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = null;
		hrisPendingItemsForm.sethRISChangeRequestReviewerId(FormatPreserveCryptoUtil.decrypt(hrisPendingItemsForm.gethRISChangeRequestReviewerId()));
		
		requestType = requestType.toUpperCase();
		switch (requestType) {

		case "ACCEPT":
			hrisPendingItemWorkflowRes = hrisPendingItemsLogic.accept(hrisPendingItemsForm, employeeId, languageId);
			break;

		case "FORWARD":
			hrisPendingItemWorkflowRes = hrisPendingItemsLogic.forward(hrisPendingItemsForm, employeeId, languageId);
			break;

		case "REJECT":
			hrisPendingItemWorkflowRes = hrisPendingItemsLogic.reject(hrisPendingItemsForm, employeeId, languageId);
			break;
		}
		if (hrisPendingItemWorkflowRes != null) {
			return new ResponseEntity<>(hrisPendingItemsForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "search-employee")
	public @ResponseBody ResponseEntity<?> searchEmployee(@RequestBody SearchParam searchParamObj) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		String arg1 = null;
		String arg2 = null;
		
		HrisPendingItemWorkflowRes hrisPendingItemWorkflowRes = null;

		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		if ((filterllist != null && !filterllist.isEmpty())) {
			switch(filterllist.size()){
			
			case 1:
				if(StringUtils.equalsIgnoreCase(filterllist.get(0).getField(), "empname")){
					arg1 = filterllist.get(0).getValue();
				}
				else{
					arg2 = filterllist.get(0).getValue();
				}
				break;

			case 2:
				if(StringUtils.equalsIgnoreCase(filterllist.get(0).getField(), "empname")){
					arg1 = filterllist.get(0).getValue();
					arg2 = filterllist.get(1).getValue();
				}
				else{
					arg1 = filterllist.get(1).getValue();
					arg2 = filterllist.get(0).getValue();
				}
				break;
			}
		}
		hrisPendingItemWorkflowRes = hrisPendingItemsLogic.searchEmployee(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), employeeId, arg1, arg2, companyId);
		
		if (hrisPendingItemWorkflowRes != null) {
			return new ResponseEntity<>(hrisPendingItemWorkflowRes, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

}
