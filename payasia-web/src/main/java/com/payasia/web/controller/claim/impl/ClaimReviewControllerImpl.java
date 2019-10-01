package com.payasia.web.controller.claim.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.ClaimReviewerLogic;
import com.payasia.web.controller.claim.ClaimReviewController;

@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_CLAIM_REVIEW)
public class ClaimReviewControllerImpl implements ClaimReviewController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(ClaimReviewControllerImpl.class);
	@Resource
	ClaimReviewerLogic claimReviewerLogic;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_REVIEWER, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimReviewers(@RequestParam(value = "claimTemplateId", required = true) Long claimTemplateId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		ClaimReviewerForm claimReviewerForm = claimReviewerLogic
				.getClaimReviewers(FormatPreserveCryptoUtil.decrypt(claimTemplateId), companyId);

		return ResponseDataConverter.getJsonURLEncodedData(claimReviewerForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_CLAIM_REVIEWER, method = RequestMethod.POST)
	@ResponseBody
	public String saveClaimReviewer(@ModelAttribute("claimReviewerForm") ClaimReviewerForm claimReviewerForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Long employeeId = Long.parseLong(UserContext.getUserId());

		claimReviewerLogic.checkClaimReviewer(employeeId, companyId, claimReviewerForm);

		if (claimReviewerForm.getEmployeeStatus().equalsIgnoreCase("notexists")) {
			try {
				claimReviewerLogic.saveClaimReviewer(claimReviewerForm, companyId);
				claimReviewerForm.setStatus("success");
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				claimReviewerForm.setStatus("failure");
			}
		}
		return ResponseDataConverter.getJsonURLEncodedData(claimReviewerForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_CLAIM_REVIEWERS, method = RequestMethod.GET)
	@ResponseBody
	public String viewClaimReviewers(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		ClaimReviewerForm claimReviewerFormResponse = claimReviewerLogic.getClaimReviewers(searchCondition, searchText,
				pageDTO, sortDTO, companyId);

		return ResponseDataConverter.getJsonURLEncodedData(claimReviewerFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_REVIEWER_DATA, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimReviewerData(@RequestParam(value = "filterIds", required = true) Long[] filterIds) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		ClaimReviewerForm claimReviewerForm = claimReviewerLogic.getClaimReviewerData(filterIds, companyId);

		return ResponseDataConverter.getJsonURLEncodedData(claimReviewerForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.UPDATE_CLAIM_REVIEWER, method = RequestMethod.POST)
	@ResponseBody
	public String updateClaimReviewer(@ModelAttribute("claimReviewerForm") ClaimReviewerForm claimReviewerForm) {

		ClaimReviewerForm claimReviewerFormResponse = new ClaimReviewerForm();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			claimReviewerLogic.updateClaimReviewer(claimReviewerForm, companyId);
			claimReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			claimReviewerFormResponse.setStatus("failure");
		}
		return ResponseDataConverter.getJsonURLEncodedData(claimReviewerFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_REVIEWER, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimReviewer(@RequestParam(value = "employeeIds", required = true) Long[] employeeIds,
			HttpServletRequest request) {

		ClaimReviewerForm cLaimReviewerFormResponse = new ClaimReviewerForm();
		try {
			claimReviewerLogic.deleteClaimReviewer(employeeIds);
			cLaimReviewerFormResponse.setStatus("success");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			cLaimReviewerFormResponse.setStatus("failure");
		}

		return ResponseDataConverter.getObjectToJson(cLaimReviewerFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SEARCH_GROUP_COMPANY_EMPLOYEE , method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		ClaimReviewerForm claimReviewerResponse = claimReviewerLogic.searchEmployee(pageDTO, sortDTO, empName,
				empNumber, companyId, employeeId);

		return ResponseDataConverter.getJsonURLEncodedData(claimReviewerResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EMPLOYEE_CLAIM_TEMPLATES, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeClaimTemplates(@RequestParam(value = "employeeId", required = true) Long employeeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<ClaimReviewerForm> claimTemplateList = claimReviewerLogic.getClaimTemplateList(companyId,
				FormatPreserveCryptoUtil.decrypt(employeeId));

		return ResponseDataConverter.getListToJson(claimTemplateList);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.IMPORT_CLAIM_REVIEWER, method = RequestMethod.POST)
	@ResponseBody
	public String importClaimReviewer(
			@ModelAttribute(value = "claimReviewerForm") ClaimReviewerForm claimReviewerForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ClaimReviewerForm claimReviewerFrm = new ClaimReviewerForm();
		boolean isFileValid = false;
		if (claimReviewerForm.getFileUpload() != null) {
			isFileValid = FileUtils.isValidFile(claimReviewerForm.getFileUpload().getBytes(),
					claimReviewerForm.getFileUpload().getOriginalFilename(),
					PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT,
					PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		}
		if (isFileValid) {
			claimReviewerFrm = claimReviewerLogic.importClaimReviewer(claimReviewerForm, companyId);
		}

		return ResponseDataConverter.getObjectToJson(claimReviewerFrm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SEARCH_EMPLOYEE_BY_GROUP_COMPANY, method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeBySessionCompany(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);
		
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReviewerResponseForm leaveReviewerResponse = claimReviewerLogic.searchEmployeeByCompany(pageDTO,
				sortDTO, empName, empNumber, companyId, employeeId);

		return ResponseDataConverter.getJsonURLEncodedData(leaveReviewerResponse);
	}

}
