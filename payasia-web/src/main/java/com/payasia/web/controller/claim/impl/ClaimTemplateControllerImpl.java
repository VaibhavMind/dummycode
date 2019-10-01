package com.payasia.web.controller.claim.impl;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.ClaimTemplateForm;
import com.payasia.common.form.ClaimTemplateItemForm;
import com.payasia.common.form.ClaimTemplateResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveGranterFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.ClaimPreferenceLogic;
import com.payasia.logic.ClaimTemplateLogic;
import com.payasia.web.controller.claim.ClaimTemplateController;

@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_CLAIM_TEMPLATE)
public class ClaimTemplateControllerImpl implements ClaimTemplateController {

	@Resource
	ClaimPreferenceLogic claimPreferenceLogic;

	@Resource
	ClaimTemplateLogic claimTemplateLogic;
	private static final Logger LOGGER = Logger.getLogger(ClaimTemplateControllerImpl.class);

	@RequestMapping(value = PayAsiaURLConstant.VIEW_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@Override
	@ResponseBody
	public String accessControl(@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText, Locale locale) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ClaimTemplateResponse claimTemplateResponse = claimTemplateLogic.accessControl(companyId, searchCondition,
				searchText, pageDTO, sortDTO, locale);

		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_APP_CODE_LIST_FOR_Proration, method = RequestMethod.POST)
	@ResponseBody
	public String getAppCodeListForProration(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		List<ClaimTemplateForm> claimForm = claimTemplateLogic.getAppcodeListForProration(locale);

		return ResponseDataConverter.getListToJson(claimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_APP_CODE_LIST_FOR_CLAIM_TEMPLATE_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String getAppCodeListForClaimTemplateList(
			@RequestParam(value = "claimTemplateId", required = true) Long claimTemplateId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateId=FormatPreserveCryptoUtil.decrypt(claimTemplateId);
		List<ClaimTemplateForm> claimForm = claimTemplateLogic.getClaimTemplateList(companyId, claimTemplateId);

		return ResponseDataConverter.getListToJson(claimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ADD_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String addClaimTemplate(@ModelAttribute(value = "claimTemplateForm") ClaimTemplateForm claimTemplateForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = claimTemplateLogic.addClaimTemplate(companyId, claimTemplateForm);
		return status;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_CLAIM_TEMPLATE_ITEM_CONF, method = RequestMethod.POST)
	@ResponseBody
	public String saveClaimTemplateItemConf(
			@ModelAttribute(value = "claimTemplateItemForm") ClaimTemplateItemForm claimTemplateItemForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = claimTemplateLogic.saveClaimTemplateItemConf(companyId, claimTemplateItemForm);
		return status;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.UPDATE_CLAIM_TEMPLATE_ITEM_CONF, method = RequestMethod.POST)
	@ResponseBody
	public String updateClaimTemplateItemConf(
			@ModelAttribute(value = "claimTemplateItemForm") ClaimTemplateItemForm claimTemplateItemForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = claimTemplateLogic.updateClaimTemplateItemConf(companyId, claimTemplateItemForm);
		return status;

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimTemplate(@RequestParam(value = "claimTemplateId", required = true) Long claimTemplateId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status;
		try {
			claimTemplateLogic.deleteClaimTemplate(companyId, FormatPreserveCryptoUtil.decrypt(claimTemplateId));
			status = "true";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "false";
		}
		return status;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimTemplate(@RequestParam(value = "claimTemplateId", required = true) Long claimTemplateId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateId=FormatPreserveCryptoUtil.decrypt(claimTemplateId);
		ClaimTemplateForm claimTemplateForm = claimTemplateLogic.getClaimTemplate(companyId, claimTemplateId);
		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_RESIGNED_EMP_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String getResignedEmployees(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveGranterFormResponse leaveGranterFormResponse = claimTemplateLogic.getResignedEmployees(pageDTO, sortDTO,
				companyId, fromDate, toDate);

		return ResponseDataConverter.getJsonURLEncodedData(leaveGranterFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ADJUST_CLAIM_RESIGNED_EMP, method = RequestMethod.POST)
	@ResponseBody
	public String adjustClaimResignedEmp(@RequestParam(value = "employeeIds", required = true) String employeeIds) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long userId = Long.parseLong(UserContext.getUserId());

		Boolean status = claimTemplateLogic.adjustClaimResignedEmp(companyId, employeeIds, userId);
		return ResponseDataConverter.getListToJson(status);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TEMPLATE_ID_CONF_DATA, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimTemplateIdConfData(
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateItemId=FormatPreserveCryptoUtil.decrypt(claimTemplateItemId);
		ClaimTemplateForm claimTemplateForm = claimTemplateLogic.getClaimTemplateIdConfData(companyId,
				claimTemplateItemId);
		return ResponseDataConverter.getObjectToJson(claimTemplateForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.AVAILABLE_BALANCE_OF_CLAIM_TEMPLATES, method = RequestMethod.POST)
	@ResponseBody
	public String getPrefrenceFlag() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		boolean claimTemplateForm = claimPreferenceLogic.getAdditionalValueOfClaimPreference(companyId);
		if (claimTemplateForm == true) {
			return "true";
		} else {
			return "false";
		}

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_ITEM_CATEGORIES, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimItemCategories(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ClaimTemplateForm claimTemplateForm = claimTemplateLogic.getClaimItemCategories(companyId);
		return ResponseDataConverter.getObjectToJson(claimTemplateForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TEMPLATE_WORKFLOW, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimTemplateItemWorkFlow(
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId,
			HttpServletRequest request, HttpServletResponse response) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateItemId=FormatPreserveCryptoUtil.decrypt(claimTemplateItemId);
		ClaimTemplateForm claimTemplateForm = claimTemplateLogic.getClaimTemplateItemWorkflow(companyId,
				claimTemplateItemId);
		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateForm);
		
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.CONFIGURE_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String configureClaimTemplate(
			@ModelAttribute(value = "claimTemplateForm") ClaimTemplateForm claimTemplateForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String Status = claimTemplateLogic.configureClaimTemplate(claimTemplateForm, companyId);

		return Status;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.UPDATE_CLAIM_TEMPLATE_ITEM_WORKFLOW, method = RequestMethod.POST)
	@ResponseBody
	public String updateClaimTemplateItemWorkflow(
			@ModelAttribute(value = "claimTemplateForm") ClaimTemplateForm claimTemplateForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String Status = claimTemplateLogic.updateClaimTemplateItemWorkflow(claimTemplateForm, companyId);

		return Status;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimType(@RequestParam(value = "claimTemplateId", required = true) Long claimTemplateId,
			@RequestParam(value = "itemCategoryId", required = true) Long itemCategoryId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		ClaimTemplateResponse claimTemplateResponse = new ClaimTemplateResponse();
		claimTemplateId=FormatPreserveCryptoUtil.decrypt(claimTemplateId);
		itemCategoryId=FormatPreserveCryptoUtil.decrypt(itemCategoryId);
		Set<ClaimTemplateForm> claimTypeList = claimTemplateLogic.getClaimTypeList(companyId, claimTemplateId,
				itemCategoryId);
		claimTemplateResponse.setClaimTemplateSet(claimTypeList);
		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateResponse);
	}
	
	@Override
	@RequestMapping(value = PayAsiaURLConstant.ADD_CLAIM_TYPE, method = RequestMethod.POST)
	public @ResponseBody void addClaimType(@RequestParam(value = "claimItemId", required = true) String[] claimTypeIdArr,
			@RequestParam(value = "claimTemplateId", required = true) Long claimTemplateId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		claimTemplateId=FormatPreserveCryptoUtil.decrypt(claimTemplateId);
		claimTemplateLogic.addClaimType(claimTypeIdArr, claimTemplateId,companyId);
	}

	@RequestMapping(value = PayAsiaURLConstant.VIEW_CLAIM_TYPE, method = RequestMethod.POST)
	@Override
	@ResponseBody
	public String viewClaimType(@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "claimTemplateId", required = true) Long claimTemplateId, Locale locale) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ClaimTemplateResponse claimTemplateResponse = claimTemplateLogic.viewClaimType(claimTemplateId, companyId,
				pageDTO, sortDTO, locale);

		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EDIT_CLAIM_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public void editClaimType(
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId,
			@ModelAttribute(value = "claimTemplateForm") ClaimTemplateForm claimTemplateForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateItemId = FormatPreserveCryptoUtil.decrypt(claimTemplateItemId);
		claimTemplateLogic.editClaimType(claimTemplateItemId, claimTemplateForm,companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TYPE_FOR_EDIT, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimTypeForEdit(
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateItemId = FormatPreserveCryptoUtil.decrypt(claimTemplateItemId);
		ClaimTemplateForm claimTemplateForm = claimTemplateLogic.getClaimTypeForEdit(claimTemplateItemId,companyId);
		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_TYPE, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimType(
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateItemId = FormatPreserveCryptoUtil.decrypt(claimTemplateItemId);
				try {
			claimTemplateLogic.deleteClaimType(companyId,claimTemplateItemId);
			return "true";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "false";
		}
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_EMPLOYEE_FILTER_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String saveEmployeeFilterList(@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateItemId = FormatPreserveCryptoUtil.decrypt(claimTemplateItemId); 
				return claimTemplateLogic.saveEmployeeFilterList(metaData, claimTemplateItemId, companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TEMPLATE_ITEM_SHORTLIST, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimTemplateItemShortlist(
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<EmployeeFilterListForm> filterList = claimTemplateLogic.getClaimTemplateItemShortlist(claimTemplateItemId,
				companyId);
		return ResponseDataConverter.getListToJson(filterList);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_FILTER, method = RequestMethod.POST)
	public void deleteFilter(@RequestParam(value = "filterId", required = true) Long filterId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimTemplateLogic.deleteFilter(filterId, companyId);
	}

}
