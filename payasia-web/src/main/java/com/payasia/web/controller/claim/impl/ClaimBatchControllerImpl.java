package com.payasia.web.controller.claim.impl;

import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.ClaimBatchForm;
import com.payasia.common.form.ClaimBatchResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.ClaimBatchLogic;
import com.payasia.web.controller.claim.ClaimBatchController;

@Controller
@RequestMapping(value = { PayAsiaURLConstant.ADMIN_CLAIM_BATCH, PayAsiaURLConstant.EMPLOYEE_CLAIM_BATCH })
public class ClaimBatchControllerImpl implements ClaimBatchController {

	@Resource
	ClaimBatchLogic claimBatchLogic;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_CLAIM_BATCH, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimBatch(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "claimBatchFilter", required = true) String claimBatchFilter,
			@RequestParam(value = "filterText", required = true) String filterText,
			@RequestParam(value = "year", required = true) int year, HttpServletRequest request, Locale locale) {

		ClaimBatchResponse response = null;
		if (isURLfromAdmin(request)) {
			
			PageRequest pageDTO = new PageRequest();
			pageDTO.setPageNumber(page);
			pageDTO.setPageSize(rows);

			SortCondition sortDTO = new SortCondition();
			sortDTO.setColumnName(columnName);
			sortDTO.setOrderType(sortingType);

			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			AddClaimDTO claimDTO = new AddClaimDTO();
			claimDTO.setAdmin(true);
			claimDTO.setCompanyId(companyId);
			claimDTO.setYear(year);
			claimDTO.setLocale(locale);
			claimDTO.setSearchCondition(claimBatchFilter);
			claimDTO.setSearchText(filterText);
						
			response = claimBatchLogic.viewClaimBatch(claimDTO,pageDTO, sortDTO);
		}
		return ResponseDataConverter.getJsonURLEncodedData(response);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_CLAIM_BATCH_DETAILS, method = RequestMethod.POST)
	@ResponseBody
	public String viewClaimBatchDetail(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "claimBatchId", required = true) Long claimBatchId, HttpServletRequest request) {

		ClaimBatchResponse response = null;
		if (isURLfromAdmin(request)) {
			PageRequest pageDTO = new PageRequest();
			pageDTO.setPageNumber(page);
			pageDTO.setPageSize(rows);

			SortCondition sortDTO = new SortCondition();
			sortDTO.setColumnName(columnName);
			sortDTO.setOrderType(sortingType);

			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			claimBatchId=FormatPreserveCryptoUtil.decrypt(claimBatchId);
			response = claimBatchLogic.viewClaimBatchDetail(pageDTO, sortDTO, claimBatchId, companyId);
		}
		return ResponseDataConverter.getJsonURLEncodedData(response);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ADD_CLAIM_BATCH, method = RequestMethod.POST)
	@ResponseBody
	public String addClaimBatch(@ModelAttribute("claimBatchForm") ClaimBatchForm claimBatchForm, HttpServletRequest request) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String responseStatus = null;
		if (isURLfromAdmin(request)) {
			responseStatus = claimBatchLogic.addClaimBatch(claimBatchForm, companyId);
		}
		return responseStatus;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_BATCH, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimBatchData(@RequestParam(value = "claimBatchID", required = true) Long claimBatchId,
			HttpServletRequest request) {
		ClaimBatchForm response = null;
		if (isURLfromAdmin(request)) {
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			response = claimBatchLogic.getClaimBatchData(FormatPreserveCryptoUtil.decrypt(claimBatchId), companyId);
		}
		return ResponseDataConverter.getJsonURLEncodedData(response);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EDIT_CLAIM_BATCH, method = RequestMethod.POST)
	@ResponseBody
	public String editClaimBatch(@ModelAttribute("claimBatchForm") ClaimBatchForm claimBatchForm,
			@RequestParam(value = "claimBatchID", required = true) Long claimBatchId, HttpServletRequest request) {
		String responseStatus = null;
		if (isURLfromAdmin(request)) {
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			claimBatchForm.setClaimBatchID(FormatPreserveCryptoUtil.decrypt(claimBatchForm.getClaimBatchID()));
			responseStatus = claimBatchLogic.editClaimBatch(claimBatchForm, companyId);
		}
		return responseStatus;

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_BATCH, method = RequestMethod.POST)
	public void deleteClaimBatch(@RequestParam(value = "claimBatchID", required = true) Long claimBatchId,
			HttpServletRequest request) {
		if (isURLfromAdmin(request)) {
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			claimBatchId=FormatPreserveCryptoUtil.decrypt(claimBatchId);
			claimBatchLogic.deleteClaimBatch(claimBatchId,companyId);
		}
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_BATCH_CONG, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimBatchConf() {		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());		
		ClaimBatchForm claimBatchForm = claimBatchLogic.getClaimBatchConf(companyId);
		return ResponseDataConverter.getObjectToJson(claimBatchForm);
	}

	private boolean isURLfromAdmin(HttpServletRequest request) {

		String url = request.getRequestURL().toString();
		if (url != null && StringUtils.isNotBlank(url)) {
			return url.contains("/admin/");
		}
		return false;
	}

}
