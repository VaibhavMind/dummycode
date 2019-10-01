package com.payasia.web.controller.claim.impl;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.ClaimItemForm;
import com.payasia.common.form.ClaimItemResponse;
import com.payasia.common.form.ClaimItemsCategoryForm;
import com.payasia.common.form.ClaimItemsCategoryResponse;
import com.payasia.common.form.ClaimTemplateResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.ClaimItemDefLogic;
import com.payasia.web.controller.claim.ClaimItemDefController;

@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_CLAIM_ITEM)
public class ClaimItemDefControllerImpl implements ClaimItemDefController {

	private static final Logger LOGGER = Logger.getLogger(ClaimItemDefControllerImpl.class);

	@Resource
	ClaimItemDefLogic claimItemDefLogic;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_CLAIM_ITEM, method = RequestMethod.POST)
	@ResponseBody
	public String viewClaimItems(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "claimItemFilter", required = true) String searchCondition,
			@RequestParam(value = "filterText", required = true) String searchText, Locale locale) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setLocale(locale);

		ClaimItemResponse claimItemResponse = claimItemDefLogic.viewClaimItems(claimDTO, pageDTO, sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(claimItemResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.UPDATE_CLAIM_ITEM_SORT_ORDER, method = RequestMethod.POST)
	@ResponseBody
	public String updateClaimItemSortOrder(@RequestParam(value = "sortOrder", required = true) String[] sortOrder) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		claimItemDefLogic.updateClaimItemSortOrder(sortOrder, companyId);
		return "true";

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_CATEGORY_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimCatList() {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<ClaimItemForm> categoryList = claimItemDefLogic.getCategoryList(companyId);

		return ResponseDataConverter.getJsonURLEncodedDataForList(categoryList);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ADD_CLAIM_ITEM, method = RequestMethod.POST)
	@ResponseBody
	public String addClaimItem(@ModelAttribute("claimItemForm") ClaimItemForm claimItemForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return claimItemDefLogic.addClaimItem(claimItemForm, companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_CLAIM_CATEGORY, method = RequestMethod.POST)
	@ResponseBody
	public String viewClaimCategory(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		ClaimItemsCategoryResponse response = claimItemDefLogic.viewClaimCategory(pageDTO, sortDTO, companyId);

		return ResponseDataConverter.getJsonURLEncodedData(response);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ADD_CLAIM_CATEGORY, method = RequestMethod.POST)
	@ResponseBody
	public String addClaimCategory(@ModelAttribute("claimCategoryForm") ClaimItemsCategoryForm claimCatForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return claimItemDefLogic.addClaimCategory(claimCatForm, companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_ITEM_ROW, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimItemData(@RequestParam(value = "itemId", required = true) Long claimItemId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimItemId = FormatPreserveCryptoUtil.decrypt(claimItemId);
		ClaimItemForm response = claimItemDefLogic.getClaimItemById(claimItemId, companyId);

		return ResponseDataConverter.getJsonURLEncodedData(response);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EDIT_CLAIM_ITEM, method = RequestMethod.POST)
	@ResponseBody
	public String editClaimItem(@ModelAttribute("claimItemForm") ClaimItemForm claimItemForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		return claimItemDefLogic.editClaimItem(claimItemForm, companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_CATEGORY_ROW, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimCatData(@RequestParam(value = "categoryId", required = true) Long categoryId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		ClaimItemsCategoryForm response = claimItemDefLogic
				.getClaimCatById(FormatPreserveCryptoUtil.decrypt(categoryId), companyId);

		return ResponseDataConverter.getJsonURLEncodedData(response);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EDIT_CLAIM_CATEGORY, method = RequestMethod.POST)
	@ResponseBody
	public String editClaimCategory(@ModelAttribute("claimCategoryForm") ClaimItemsCategoryForm claimItemCatForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return claimItemDefLogic.editClaimCategory(claimItemCatForm, companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_ITEM, method = RequestMethod.POST)
	@ResponseBody
	public String delClaimItem(@RequestParam(value = "itemId") Long claimItemId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimItemId = FormatPreserveCryptoUtil.decrypt(claimItemId);
		String status;
		try {
			claimItemDefLogic.deleteClaimItem(claimItemId, companyId);
			status = "true";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "false";
		}
		return status;

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_CATEGORY, method = RequestMethod.POST)
	@ResponseBody
	public String delClaimCategory(@RequestParam(value = "categoryId") Long categoryId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return claimItemDefLogic.deleteClaimCategory(FormatPreserveCryptoUtil.decrypt(categoryId), companyId);
	}

	@RequestMapping(value = PayAsiaURLConstant.ASSIGNED_CLAIM_TEMPLATES, method = RequestMethod.POST)
	@Override
	@ResponseBody
	public String assignedClaimTemplates(@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "claimItemId", required = true) Long claimItemId, Locale locale) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimItemId = FormatPreserveCryptoUtil.decrypt(claimItemId);
		ClaimTemplateResponse claimTemplateResponse = claimItemDefLogic.assignedClaimTemplates(companyId, pageDTO,
				sortDTO, claimItemId, locale);

		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateResponse);
	}

	@RequestMapping(value = PayAsiaURLConstant.UN_ASSIGNED_CLAIM_TEMPLATES, method = RequestMethod.POST)
	@Override
	@ResponseBody
	public String unAssignedClaimTemplates(@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "claimItemId", required = true) Long claimItemId) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimItemId = FormatPreserveCryptoUtil.decrypt(claimItemId);
		ClaimTemplateResponse claimTemplateResponse = claimItemDefLogic.unAssignedClaimTemplates(companyId, pageDTO,
				sortDTO, claimItemId);

		return ResponseDataConverter.getJsonURLEncodedData(claimTemplateResponse);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ASSIGN_CLAIM_TEMPLATES, method = RequestMethod.POST)
	public @ResponseBody void assignClaimTemplate(
			@RequestParam(value = "claimTemplateId", required = true) String[] claimTemplateIdArr,
			@RequestParam(value = "claimItemId", required = true) Long claimItemId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimItemId = FormatPreserveCryptoUtil.decrypt(claimItemId);
		claimItemDefLogic.assignClaimTemplate(claimTemplateIdArr, claimItemId, companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_TEMPLATE_ITEM, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimTemplateItem(
			@RequestParam(value = "claimTemplateItemId", required = true) Long claimTemplateItemId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		String status;
		try {
			claimTemplateItemId = FormatPreserveCryptoUtil.decrypt(claimTemplateItemId);
			claimItemDefLogic.deleteClaimTemplateItem(claimTemplateItemId, companyId);
			status = "false";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "true";
		}
		return status;
	}

}
