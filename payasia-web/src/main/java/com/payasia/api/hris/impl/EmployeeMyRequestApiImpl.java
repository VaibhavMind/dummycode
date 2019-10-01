package com.payasia.api.hris.impl;

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

import com.payasia.api.hris.EmployeeMyRequestApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.HrisChangeRequestForm;
import com.payasia.common.form.HrisMyRequestFormResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.HrisMyRequestLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param :This class used for HRIS My-Request APIs
 * 
 */

@RestController
// TODO : Request mapping root URL get the value of ApiUtil
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class EmployeeMyRequestApiImpl implements EmployeeMyRequestApi {

	@Resource
	private HrisMyRequestLogic hrisMyRequestLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;

	/**
	 * SHOWS THE SELECTED HRIS REQUEST ACCORDING TO THE "hrisChangeRequestId"
	 */
	// TODO : Page Context Path issue
	@Override
	@PostMapping(value = "view-change-request")
	public ResponseEntity<?> viewChangeRequest(@RequestBody String jsonStr) {

		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Long languageId = UserContext.getLanguageId();

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long hrisChangeRequestId = jsonObj.getLong("hrisChangeRequestId");
		hrisChangeRequestId = FormatPreserveCryptoUtil.decrypt(hrisChangeRequestId);

		HrisChangeRequestForm hrisChangeRequestForm = hrisMyRequestLogic.viewChangeRequest(hrisChangeRequestId,	employeeId, languageId);

		if (hrisChangeRequestForm != null) {
			return new ResponseEntity<>(hrisChangeRequestForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	/**
	 * PUTS THE SELECTED HRIS REQUEST ACCORDING TO THE "hrisChangeRequestId" IN THE WITHDRAWN HRIS REQUESTS CATEGORY
	 */
	// TODO : Page Context Path issue
	@Override
	@PostMapping(value = "withdraw-hris-change-request")
	public ResponseEntity<?> withdrawChangeRequest(@RequestBody String jsonStr) {

		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Long languageId = UserContext.getLanguageId();
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long hrisChangeRequestId = jsonObj.getLong("hrisChangeRequestId");
		hrisChangeRequestId =  FormatPreserveCryptoUtil.decrypt(hrisChangeRequestId);
		
		HrisChangeRequestForm hrisChangeRequestForm = hrisMyRequestLogic.withdrawChangeRequest(hrisChangeRequestId,	employeeId, languageId);

		if (hrisChangeRequestForm != null) {
			return new ResponseEntity<>(hrisChangeRequestForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	/**
	 * SHOWS THE HRIS REQUESTS ACCORDING TO THE REQUEST TYPE
	 */
	@Override
	@PostMapping(value = "process-my-request")
	public ResponseEntity<?> getDesiredRequest(@RequestBody SearchParam searchParamObj,	@RequestParam(value = "requestType", required = true) String requestType) {

		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		HrisMyRequestFormResponse hrisMyRequestFormResponse = null;
		String pageContextPath = "";
		Long languageId = UserContext.getLanguageId();
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}
		requestType = requestType.toUpperCase();

		if ((filterllist != null && !filterllist.isEmpty())) {

			switch (requestType) {
			case "SUBMIT":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getSubmittedRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, filterllist.get(0).getField(), filterllist.get(0).getValue(), languageId);
				break;

			case "APPROVE":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getApprovedRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, filterllist.get(0).getField(), filterllist.get(0).getValue(), languageId);
				break;

			case "REJECT":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getRejectedRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, filterllist.get(0).getField(), filterllist.get(0).getValue(), languageId);
				break;

			case "WITHDRAW":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getWithdrawnRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, filterllist.get(0).getField(), filterllist.get(0).getValue(), languageId);
				break;
			}
		} else {
			switch (requestType) {
			case "SUBMIT":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getSubmittedRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, null, null, languageId);
				break;

			case "APPROVE":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getApprovedRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, null, null, languageId);
				break;

			case "REJECT":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getRejectedRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, null, null, languageId);
				break;

			case "WITHDRAW":
				hrisMyRequestFormResponse = hrisMyRequestLogic.getWithdrawnRequest(employeeId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
						pageContextPath, null, null, languageId);
				break;
			}
		}
		if (hrisMyRequestFormResponse != null) {
			return new ResponseEntity<>(hrisMyRequestFormResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

}
