package com.payasia.logic.impl;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.mind.payasia.xml.bean.EmployeeFilter;
import com.mind.payasia.xml.bean.EmployeeFilterTemplate;
import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.ClaimAmtReviewersTemplateDTO;
import com.payasia.common.dto.ClaimTemplateConditionDTO;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.CustomFieldsDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ClaimItemForm;
import com.payasia.common.form.ClaimTemplateForm;
import com.payasia.common.form.ClaimTemplateItemForm;
import com.payasia.common.form.ClaimTemplateResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.common.form.LeaveGranterForm;
import com.payasia.common.form.LeaveGranterFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.EmployeeFilterXMLUtil;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.ShortlistOperatorEnum;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.ClaimAmountReviewerTemplateDAO;
import com.payasia.dao.ClaimItemMasterDAO;
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.ClaimTemplateItemClaimTypeDAO;
import com.payasia.dao.ClaimTemplateItemCustomFieldDAO;
import com.payasia.dao.ClaimTemplateItemCustomFieldDropDownDAO;
import com.payasia.dao.ClaimTemplateItemDAO;
import com.payasia.dao.ClaimTemplateItemGeneralDAO;
import com.payasia.dao.ClaimTemplateItemShortlistDAO;
import com.payasia.dao.ClaimTemplateItemWorkflowDAO;
import com.payasia.dao.ClaimTemplateWorkflowDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.CurrencyMasterDAO;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.DynamicFormDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeClaimTemplateItemDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EntityMasterDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.ClaimAmountReviewerTemplate;
import com.payasia.dao.bean.ClaimItemMaster;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemClaimType;
import com.payasia.dao.bean.ClaimTemplateItemCustomField;
import com.payasia.dao.bean.ClaimTemplateItemCustomFieldDropDown;
import com.payasia.dao.bean.ClaimTemplateItemGeneral;
import com.payasia.dao.bean.ClaimTemplateItemShortlist;
import com.payasia.dao.bean.ClaimTemplateItemWorkflow;
import com.payasia.dao.bean.ClaimTemplateWorkflow;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CurrencyMaster;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.EmployeeClaimTemplateItem;
import com.payasia.dao.bean.EntityMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.ClaimTemplateLogic;
import com.payasia.logic.FiltersInfoUtilsLogic;
import com.payasia.logic.GeneralFilterLogic;

@Component
public class ClaimTemplateLogicImpl implements ClaimTemplateLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(ClaimTemplateLogicImpl.class);
	@Autowired
	private MessageSource messageSource;
	@Resource
	ClaimItemMasterDAO claimItemMasterDAO;
	@Resource
	ClaimTemplateDAO claimTemplateDAO;
	@Resource
	ClaimTemplateItemDAO claimTemplateItemDAO;
	@Resource
	ClaimPreferenceDAO claimPreferenceDAO;
	@Resource
	ClaimTemplateWorkflowDAO claimTemplateWorkflowDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	EmployeeClaimReviewerDAO employeeClaimReviewerDAO;

	@Resource
	ClaimTemplateItemWorkflowDAO claimTemplateItemWorkflowDAO;

	@Resource
	ClaimTemplateItemShortlistDAO claimTemplateItemShortlistDAO;

	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	DataDictionaryDAO dataDictionaryDAO;

	@Resource
	ClaimTemplateItemClaimTypeDAO claimTemplateItemClaimTypeDAO;

	@Resource
	ClaimTemplateItemGeneralDAO claimTemplateItemGeneralDAO;

	@Resource
	ClaimTemplateItemCustomFieldDAO claimTemplateItemCustomFieldDAO;

	@Resource
	CurrencyMasterDAO currencyMasterDAO;

	@Resource
	EmployeeClaimTemplateItemDAO employeeClaimTemplateItemDAO;

	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	EntityMasterDAO entityMasterDAO;

	@Resource
	DynamicFormDAO dynamicFormDAO;

	@Resource
	FiltersInfoUtilsLogic filtersInfoUtilsLogic;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	GeneralFilterLogic generalFilterLogic;
	@Resource
	ClaimAmountReviewerTemplateDAO claimAmountReviewerTemplateDAO;
	@Resource
	ClaimTemplateItemCustomFieldDropDownDAO claimTemplateItemCustomFieldDropDownDAO;

	@Override
	public ClaimTemplateResponse accessControl(Long companyId, String searchCondition, String searchText,
			PageRequest pageDTO, SortCondition sortDTO, Locale locale) {
		ClaimTemplateConditionDTO conditionDTO = new ClaimTemplateConditionDTO();
		String edit = "payasia.edit";
		String items = "payasia.items";
		String visible = "payasia.visible";
		String hidden = "payasia.hidden";
		if ("templateName".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setTemplateName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}

		if ("status".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setStatus(URLDecoder.decode(searchText, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}

			}
		}

		List<ClaimTemplateForm> claimTemplateFormList = new ArrayList<ClaimTemplateForm>();

		List<ClaimTemplate> claimTemplateVOList = claimTemplateDAO.getAllClaimTemplateByConditionCompany(companyId,
				conditionDTO, pageDTO, sortDTO);

		for (ClaimTemplate claimTemplateVO : claimTemplateVOList) {
			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();

			StringBuilder claimItemCount = new StringBuilder();
			Long claimTemplateId = FormatPreserveCryptoUtil.encrypt(claimTemplateVO.getClaimTemplateId());
			claimItemCount.append("<span class='Text'><h2>"
					+ String.valueOf(claimTemplateVO.getClaimTemplateItems().size()) + "</h2></span>");
			claimItemCount.append("<span class='Textsmall' style='padding-top: 5px;'>&nbsp;"
					+ messageSource.getMessage(items, new Object[] {}, locale) + "</span>");
			claimItemCount.append("<br><br>");
			claimItemCount
					.append("<span class='Textsmall' style='padding-bottom: 3px;'><a class='alink' style='text-decoration: none;' href='#' onClick = 'configureClaimTemplateItem("
							+ claimTemplateId + ")'>["
							+ messageSource.getMessage(edit, new Object[] {}, locale) + "]</a></span>");

			claimTemplateForm.setNoOfItems(String.valueOf(claimItemCount));

			StringBuilder claimTemplateName = new StringBuilder();
			claimTemplateName.append("<span class='jqGridColumnHighlight'>");
			claimTemplateName.append(claimTemplateVO.getTemplateName());
			claimTemplateName.append("</span>");

			claimTemplateForm.setTemplateName(String.valueOf(claimTemplateName));
			if (claimTemplateVO.getVisibility() == true) {
				claimTemplateForm.setActive(messageSource.getMessage(visible, new Object[] {}, locale));
			}
			if (claimTemplateVO.getVisibility() == false) {
				claimTemplateForm.setActive(messageSource.getMessage(hidden, new Object[] {}, locale));
			}
			claimTemplateForm.setClaimTemplateId(claimTemplateId);
			claimTemplateFormList.add(claimTemplateForm);
		}
		ClaimTemplateResponse response = new ClaimTemplateResponse();
		int recordSize = claimTemplateDAO.getCountForAllClaimTemplate(companyId, conditionDTO);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

			response.setRecords(recordSize);
		}
		response.setRows(claimTemplateFormList);

		return response;
	}

	@Override
	public String addClaimTemplate(Long companyId, ClaimTemplateForm claimTemplateForm) {
		boolean status = true;
		ClaimTemplate claimTemplateVO = new ClaimTemplate();

		Company company = companyDAO.findById(companyId);
		claimTemplateVO.setCompany(company);
		try {
			claimTemplateVO.setTemplateName(URLDecoder.decode(claimTemplateForm.getTemplateName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		claimTemplateVO.setVisibility(claimTemplateForm.getVisibility());
		claimTemplateVO.setDefaultCC(claimTemplateForm.getDefaultCC());
		claimTemplateVO.setSendMailToDefaultCC(claimTemplateForm.getSendMailToDefaultCC());
		AppCodeMaster appCodeMaster4 = null;
		if (claimTemplateForm.getProrationMethod() != null) {
			appCodeMaster4 = appCodeMasterDAO.findById(claimTemplateForm.getProrationMethod());
		}
		if (claimTemplateForm.getConsiderAdditionalBalanceFrom() != null) {
			ClaimTemplate considerFromClaimTemplate = claimTemplateDAO
					.findById(claimTemplateForm.getConsiderAdditionalBalanceFrom());
			claimTemplateVO.setConsiderAdditionalBalanceFrom(considerFromClaimTemplate);
		}
		claimTemplateVO.setProrationMethod(appCodeMaster4);
		claimTemplateVO.setCutOffValue(claimTemplateForm.getCutOffValue());
		AppCodeMaster frontEndViewMode = appCodeMasterDAO.findById(claimTemplateForm.getFrontEndViewModeIdClaim());
		claimTemplateVO.setFrontEndViewMode(frontEndViewMode);

		AppCodeMaster backEndViewMode = appCodeMasterDAO.findById(claimTemplateForm.getBackEndViewModeIdClaim());
		claimTemplateVO.setBackEndViewMode(backEndViewMode);
		claimTemplateVO.setFrontEndApplicationMode(claimTemplateForm.getFrontEndAppModeClaim());
		claimTemplateVO.setBackEndApplicationMode(claimTemplateForm.getBackEndAppModeClaim());

		CurrencyMaster currencyMaster = currencyMasterDAO.findById(claimTemplateForm.getCurrencyId());
		claimTemplateVO.setDefaultCurrency(currencyMaster);

		claimTemplateVO.setEntitlementPerClaim(claimTemplateForm.getPerClaim());
		claimTemplateVO.setEntitlementPerMonth(claimTemplateForm.getPerMonth());
		claimTemplateVO.setEntitlementPerYear(claimTemplateForm.getPerYear());
		if (claimTemplateForm.getAllowNoOfTimesAppCodeId() != 0) {
			AppCodeMaster allowedTimesField = appCodeMasterDAO.findById(claimTemplateForm.getAllowNoOfTimesAppCodeId());
			claimTemplateVO.setAllowedTimesField(allowedTimesField);
		}
		if (StringUtils.isNotBlank(claimTemplateForm.getAllowNoOfTimesVal())) {
			claimTemplateVO.setAllowedTimesValue(Integer.parseInt(claimTemplateForm.getAllowNoOfTimesVal()));
		}
		claimTemplateVO.setAllowIfAtLeastOneAttachment(claimTemplateForm.getAllowIfAtLeastOneAttacment());
		claimTemplateVO.setProration(claimTemplateForm.getProration());
		if (claimTemplateForm.getProration()) {
			AppCodeMaster prorationBasedOnAppCodeMaster = appCodeMasterDAO
					.findById(claimTemplateForm.getProrationBasedOnAppCodeId());
			claimTemplateVO.setProrationBasedOn(prorationBasedOnAppCodeMaster);
		}

		claimTemplateVO.setClaimReviewersBasedOnClaimAmount(claimTemplateForm.getClaimReviewersBasedOnClaimAmt());

		status = checkClaimTemplateName(null, claimTemplateForm.getTemplateName(), companyId);
		ClaimTemplate claimTemplate = null;
		if (!status) {
			return "available";
		} else {
			claimTemplate = claimTemplateDAO.saveReturn(claimTemplateVO);
		}

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO.findAll();

		claimAmountReviewerTemplateDAO.deleteByCondition(claimTemplateVO.getClaimTemplateId());
		if (claimTemplateForm.getClaimReviewersBasedOnClaimAmt()) {
			List<ClaimAmtReviewersTemplateDTO> customFieldsDTOLists = claimTemplateForm.getClaimAmtRevTempDTOList();
			for (ClaimAmtReviewersTemplateDTO customFieldsDTO : customFieldsDTOLists) {

				ClaimAmountReviewerTemplate claimAmountReviewerTemplate = new ClaimAmountReviewerTemplate();
				claimAmountReviewerTemplate.setFromClaimAmount(customFieldsDTO.getFromClaimAmount());
				claimAmountReviewerTemplate.setToClaimAmount(customFieldsDTO.getToClaimAmount());

				if (!customFieldsDTO.getLevel1ReviewerId().equals("0")) {
					WorkFlowRuleMaster workFlowRuleMasterLevel1 = getWorkFlowMasterId(
							PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER,
							String.valueOf(customFieldsDTO.getLevel1ReviewerId()), workFlowRuleMasterList);
					claimAmountReviewerTemplate.setLevel1(workFlowRuleMasterLevel1);
				}

				if (!customFieldsDTO.getLevel2ReviewerId().equals("0")) {
					WorkFlowRuleMaster workFlowRuleMasterLevel2 = getWorkFlowMasterId(
							PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER,
							String.valueOf(customFieldsDTO.getLevel2ReviewerId()), workFlowRuleMasterList);
					claimAmountReviewerTemplate.setLevel2(workFlowRuleMasterLevel2);
				}

				if (!customFieldsDTO.getLevel3ReviewerId().equals("0")) {
					WorkFlowRuleMaster workFlowRuleMasterLevel3 = getWorkFlowMasterId(
							PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER,
							String.valueOf(customFieldsDTO.getLevel3ReviewerId()), workFlowRuleMasterList);
					claimAmountReviewerTemplate.setLevel3(workFlowRuleMasterLevel3);
				}

				claimAmountReviewerTemplate.setClaimTemplate(claimTemplate);
				claimAmountReviewerTemplateDAO.save(claimAmountReviewerTemplate);
			}
		}

		ClaimTemplateWorkflow claimTemplateWorkflowVO = claimTemplateWorkflowDAO.findByTemplateIdRuleName(
				claimTemplateForm.getClaimTemplateId(), companyId, PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL);
		if (claimTemplateWorkflowVO != null) {
			claimTemplateWorkflowDAO.deleteByCondition(claimTemplateForm.getClaimTemplateId());
		}
		if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL1)) {
			WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
					PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "1", workFlowRuleMasterList);
			ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

			claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			claimTemplateWorkflow.setClaimTemplate(claimTemplate);
			claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

		}
		if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL2)) {
			WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
					PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "2", workFlowRuleMasterList);
			ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

			claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			claimTemplateWorkflow.setClaimTemplate(claimTemplate);
			claimTemplateWorkflowDAO.save(claimTemplateWorkflow);
		}
		if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL3)) {
			WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
					PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "3", workFlowRuleMasterList);
			ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

			claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			claimTemplateWorkflow.setClaimTemplate(claimTemplate);
			claimTemplateWorkflowDAO.save(claimTemplateWorkflow);
		}

		String allowOverrideStr = "";
		if (claimTemplateForm.getAllowOverrideCTL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (claimTemplateForm.getAllowOverrideCTL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (claimTemplateForm.getAllowOverrideCTL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

		claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		claimTemplateWorkflow.setClaimTemplate(claimTemplate);
		claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

		String allowRejectStr = "";
		if (claimTemplateForm.getAllowRejectCTL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (claimTemplateForm.getAllowRejectCTL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (claimTemplateForm.getAllowRejectCTL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = getWorkFlowMasterId(
				PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_REJECT, allowRejectStr.trim(), workFlowRuleMasterList);
		claimTemplateWorkflow = new ClaimTemplateWorkflow();

		claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		claimTemplateWorkflow.setClaimTemplate(claimTemplate);
		claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

		String allowForward = "";
		if (claimTemplateForm.getAllowForward1() == true) {
			allowForward += "1";
		} else {
			allowForward += "0";
		}

		if (claimTemplateForm.getAllowForward2() == true) {
			allowForward += "1";
		} else {
			allowForward += "0";
		}

		if (claimTemplateForm.getAllowForward3() == true) {
			allowForward += "1";
		} else {
			allowForward += "0";
		}

		WorkFlowRuleMaster workFlowRuleAllowForwardMaster = getWorkFlowMasterId(
				PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_FORWARD, allowForward.trim(), workFlowRuleMasterList);
		claimTemplateWorkflow = new ClaimTemplateWorkflow();

		claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowForwardMaster);
		claimTemplateWorkflow.setClaimTemplate(claimTemplate);
		claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

		String allowApproveStr = "";
		if (claimTemplateForm.getAllowApprove1() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (claimTemplateForm.getAllowApprove2() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (claimTemplateForm.getAllowApprove3() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		WorkFlowRuleMaster workFlowRuleAllowApproveMaster = getWorkFlowMasterId(
				PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_APPROVE, allowApproveStr.trim(), workFlowRuleMasterList);
		claimTemplateWorkflow = new ClaimTemplateWorkflow();

		claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowApproveMaster);
		claimTemplateWorkflow.setClaimTemplate(claimTemplate);
		claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

		return "notavailable";

	}

	public boolean checkClaimTemplateName(Long claimTemplateId, String claimTemplateName, Long companyId) {
		ClaimTemplate claimTemplateVO = claimTemplateDAO.findByClaimTemplateAndCompany(claimTemplateId,
				claimTemplateName, companyId);
		if (claimTemplateVO == null) {
			return true;
		}
		return false;

	}

	@Override
	public void deleteClaimTemplate(Long companyId, Long claimTemplateId) {
		
		claimTemplateWorkflowDAO.deleteByCondition(claimTemplateId);

		ClaimTemplate claimTemplateVO = claimTemplateDAO.findByClaimTemplateID(claimTemplateId,companyId);
		claimTemplateDAO.delete(claimTemplateVO);

	}

	/**
	 * Comparator Class for Ordering ClaimAmountReviewerPreferenceComp List
	 */
	private class ClaimAmountReviewerTemplateComp implements Comparator<ClaimAmountReviewerTemplate> {
		public int compare(ClaimAmountReviewerTemplate claimAmountRevPref,
				ClaimAmountReviewerTemplate compWithClaimAmountRevPref) {
			if (claimAmountRevPref.getClaimAmountReviewerTemplateId() > compWithClaimAmountRevPref
					.getClaimAmountReviewerTemplateId()) {
				return 1;
			} else if (claimAmountRevPref.getClaimAmountReviewerTemplateId() < compWithClaimAmountRevPref
					.getClaimAmountReviewerTemplateId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public ClaimTemplateForm getClaimTemplate(Long companyId, Long claimTemplateId) {
		
		List<ClaimTemplateWorkflow> claimTemplateWorkflowVOList = claimTemplateWorkflowDAO.findByCondition(claimTemplateId);
		ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();

		ClaimTemplate claimTemplateVO = claimTemplateDAO.findByClaimTemplateID(claimTemplateId,companyId);
		claimTemplateForm.setTemplateName(claimTemplateVO.getTemplateName());
		if (claimTemplateVO.getVisibility() == true) {
			claimTemplateForm.setVisibility(true);
		}
		if (claimTemplateVO.getVisibility() == false) {
			claimTemplateForm.setVisibility(false);
		}
		if (claimTemplateVO.getDefaultCurrency() != null) {
			claimTemplateForm.setCurrencyId(claimTemplateVO.getDefaultCurrency().getCurrencyId());
		} else {
			claimTemplateForm.setCurrencyId(0l);
		}

		if (claimTemplateVO.getFrontEndViewMode() != null) {
			/*
			 * claimTemplateForm.setFrontEndViewModeIdClaim(claimTemplateVO
			 * .getFrontEndViewMode().getAppCodeID());
			 */
			claimTemplateForm.setFrontEndViewModeIdClaim(claimTemplateVO.getFrontEndViewMode().getAppCodeID());
		}
		if (claimTemplateVO.getBackEndViewMode() != null) {
			claimTemplateForm.setBackEndViewModeIdClaim(claimTemplateVO.getBackEndViewMode().getAppCodeID());
		}
		if (claimTemplateVO.getFrontEndApplicationMode() != null) {
			claimTemplateForm.setFrontEndAppModeClaim(claimTemplateVO.getFrontEndApplicationMode());
		} else {
			claimTemplateForm.setFrontEndAppModeClaim(true);
		}
		if (claimTemplateVO.getBackEndApplicationMode() != null) {
			claimTemplateForm.setBackEndAppModeClaim(claimTemplateVO.getBackEndApplicationMode());
		} else {
			claimTemplateForm.setBackEndAppModeClaim(true);
		}

		claimTemplateForm.setDefaultCC(claimTemplateVO.getDefaultCC() == null ? "" : claimTemplateVO.getDefaultCC());
		claimTemplateForm.setAllowIfAtLeastOneAttacment(claimTemplateVO.getAllowIfAtLeastOneAttachment());
		if (claimTemplateVO.getProration() != null) {
			claimTemplateForm.setProration(claimTemplateVO.getProration());
		}

		if (claimTemplateVO.getConsiderAdditionalBalanceFrom() != null) {
			claimTemplateForm.setConsiderAdditionalBalanceFrom(
					claimTemplateVO.getConsiderAdditionalBalanceFrom().getClaimTemplateId());
		}
		if (claimTemplateVO.getProrationBasedOn() != null) {
			claimTemplateForm.setProrationBasedOnAppCodeId(claimTemplateVO.getProrationBasedOn().getAppCodeID());
		}

		claimTemplateForm.setSendMailToDefaultCC(
				claimTemplateVO.getSendMailToDefaultCC() == null ? false : claimTemplateVO.getSendMailToDefaultCC());

		if (claimTemplateVO.getProrationMethod() != null) {
			claimTemplateForm.setProrationMethod(claimTemplateVO.getProrationMethod().getAppCodeID());
		}

		claimTemplateForm.setCutOffValue(claimTemplateVO.getCutOffValue());
		claimTemplateForm.setClaimReviewersBasedOnClaimAmt(claimTemplateVO.isClaimReviewersBasedOnClaimAmount());
		if (claimTemplateVO.isClaimReviewersBasedOnClaimAmount()) {
			List<ClaimAmtReviewersTemplateDTO> customFieldsDTOLists = new ArrayList<ClaimAmtReviewersTemplateDTO>();
			List<ClaimAmountReviewerTemplate> claimAmtReviewersTemplateDTOs = new ArrayList<>(
					claimTemplateVO.getClaimAmountReviewerTemplates());
			Collections.sort(claimAmtReviewersTemplateDTOs, new ClaimAmountReviewerTemplateComp());

			if (!claimAmtReviewersTemplateDTOs.isEmpty()) {
				for (ClaimAmountReviewerTemplate amountReviewerTemplate : claimAmtReviewersTemplateDTOs) {
					ClaimAmtReviewersTemplateDTO claimAmtReviewersTemplateDTO = new ClaimAmtReviewersTemplateDTO();
					claimAmtReviewersTemplateDTO.setClaimAmountReviewerTemplateId(
							amountReviewerTemplate.getClaimAmountReviewerTemplateId());
					claimAmtReviewersTemplateDTO.setFromClaimAmount(amountReviewerTemplate.getFromClaimAmount());
					claimAmtReviewersTemplateDTO.setToClaimAmount(amountReviewerTemplate.getToClaimAmount());
					claimAmtReviewersTemplateDTO.setLevel1ReviewerId(amountReviewerTemplate.getLevel1().getRuleValue());
					claimAmtReviewersTemplateDTO.setLevel2ReviewerId(amountReviewerTemplate.getLevel2() == null ? "0"
							: amountReviewerTemplate.getLevel2().getRuleValue());
					claimAmtReviewersTemplateDTO.setLevel3ReviewerId(amountReviewerTemplate.getLevel3() == null ? "0"
							: amountReviewerTemplate.getLevel3().getRuleValue());
					customFieldsDTOLists.add(claimAmtReviewersTemplateDTO);
				}

			}
			claimTemplateForm.setClaimAmtRevTemplateDTOList(customFieldsDTOLists);

		}

		claimTemplateForm.setPerClaim(claimTemplateVO.getEntitlementPerClaim());
		claimTemplateForm.setPerYear(claimTemplateVO.getEntitlementPerYear());
		claimTemplateForm.setPerMonth(claimTemplateVO.getEntitlementPerMonth());
		if (claimTemplateVO.getAllowedTimesField() != null) {
			AppCodeMaster allowedTimesField = claimTemplateVO.getAllowedTimesField();
			claimTemplateForm.setAllowNoOfTimesAppCodeId(allowedTimesField.getAppCodeID());
		}
		if (claimTemplateVO.getAllowedTimesValue() != null) {
			claimTemplateForm.setAllowNoOfTimesVal(String.valueOf(claimTemplateVO.getAllowedTimesValue()));
		}

		for (ClaimTemplateWorkflow claimTemplateWorkflowVO : claimTemplateWorkflowVOList) {

			if (claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL)) {
				claimTemplateForm.setWorkFlowLevelCT(claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue());
			}

			if (claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
				String allowOverRideVal = claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowOverRideVal.charAt(0);
				char atlevelTwo = allowOverRideVal.charAt(1);
				char atlevelThree = allowOverRideVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowOverrideCTL1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowOverrideCTL1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowOverrideCTL2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowOverrideCTL2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowOverrideCTL3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowOverrideCTL3(true);
				}
			}

			if (claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_REJECT)) {
				String allowRejectVal = claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowRejectVal.charAt(0);
				char atlevelTwo = allowRejectVal.charAt(1);
				char atlevelThree = allowRejectVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowRejectCTL1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowRejectCTL1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowRejectCTL2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowRejectCTL2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowRejectCTL3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowRejectCTL3(true);
				}
			}

			if (claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_FORWARD)) {
				String allowForwardVal = claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowForwardVal.charAt(0);
				char atlevelTwo = allowForwardVal.charAt(1);
				char atlevelThree = allowForwardVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowForward1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowForward1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowForward2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowForward2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowForward3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowForward3(true);
				}
			}

			if (claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_APPROVE)) {
				String allowApproveVal = claimTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowApproveVal.charAt(0);
				char atlevelTwo = allowApproveVal.charAt(1);
				char atlevelThree = allowApproveVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowApprove1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowApprove1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowApprove2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowApprove2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowApprove3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowApprove3(true);
				}
			}
		}
		return claimTemplateForm;
	}

	@Override
	public String configureClaimTemplate(ClaimTemplateForm claimTemplateForm, Long companyId) {
		boolean status = true;
		long claimTemplateId= FormatPreserveCryptoUtil.decrypt(claimTemplateForm.getClaimTemplateId());
		status = checkClaimTemplateName(claimTemplateId, claimTemplateForm.getTemplateName(),
				companyId);
		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO.findAll();
		ClaimTemplate claimTemplateVO = claimTemplateDAO.findByClaimTemplateID(claimTemplateId,companyId);
		Integer maxWorkFlowRuleValue = workFlowRuleMasterDAO.findMaxRuleValueByClaimTemplate(companyId,claimTemplateId, PayAsiaConstants.CLAIM_TEMPLATE_DEF_CLAIM_REVIEWER);
		if (maxWorkFlowRuleValue == null) {
			maxWorkFlowRuleValue = 0;
		}
		if (!status) {
			return "available";
		}
		if (status) {
			Company company = companyDAO.findById(companyId);
			claimTemplateVO.setCompany(company);
			try {
				claimTemplateVO.setTemplateName(URLDecoder.decode(claimTemplateForm.getTemplateName(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			claimTemplateVO.setVisibility(claimTemplateForm.getVisibility());
			claimTemplateVO.setDefaultCC(claimTemplateForm.getDefaultCC());
			claimTemplateVO.setSendMailToDefaultCC(claimTemplateForm.getSendMailToDefaultCC());
			AppCodeMaster appCodeMaster4 = null;
			if (claimTemplateForm.getProrationMethod() != null) {
				appCodeMaster4 = appCodeMasterDAO.findById(claimTemplateForm.getProrationMethod());
			}
			claimTemplateVO.setProrationMethod(appCodeMaster4);
			claimTemplateVO.setCutOffValue(claimTemplateForm.getCutOffValue());

			if (claimTemplateForm.getConsiderAdditionalBalanceFromEdit() != null) {
				ClaimTemplate considerFromClaimTemplate = claimTemplateDAO
						.findById(claimTemplateForm.getConsiderAdditionalBalanceFromEdit());
				claimTemplateVO.setConsiderAdditionalBalanceFrom(considerFromClaimTemplate);
			} else {
				claimTemplateVO.setConsiderAdditionalBalanceFrom(null);
			}
			claimTemplateVO.setEntitlementPerClaim(
					claimTemplateForm.getPerClaim() == null ? new BigDecimal(0) : claimTemplateForm.getPerClaim());

			claimTemplateVO.setEntitlementPerYear(
					claimTemplateForm.getPerYear() == null ? new BigDecimal(0) : claimTemplateForm.getPerYear());
			claimTemplateVO.setEntitlementPerMonth(
					claimTemplateForm.getPerMonth() == null ? new BigDecimal(0) : claimTemplateForm.getPerMonth());

			AppCodeMaster frontEndViewMode = appCodeMasterDAO.findById(claimTemplateForm.getFrontEndViewModeIdClaim());
			claimTemplateVO.setFrontEndViewMode(frontEndViewMode);

			AppCodeMaster backEndViewMode = appCodeMasterDAO.findById(claimTemplateForm.getBackEndViewModeIdClaim());
			claimTemplateVO.setBackEndViewMode(backEndViewMode);
			claimTemplateVO.setFrontEndApplicationMode(claimTemplateForm.getFrontEndAppModeClaim());
			claimTemplateVO.setBackEndApplicationMode(claimTemplateForm.getBackEndAppModeClaim());

			if (claimTemplateForm.getAllowNoOfTimesAppCodeId() != 0) {
				AppCodeMaster allowedTimesField = appCodeMasterDAO
						.findById(claimTemplateForm.getAllowNoOfTimesAppCodeId());
				claimTemplateVO.setAllowedTimesField(allowedTimesField);
			}
			else{
				claimTemplateVO.setAllowedTimesField(null);
			}
			if (StringUtils.isNotBlank(claimTemplateForm.getAllowNoOfTimesVal())) {
				claimTemplateVO.setAllowedTimesValue(Integer.parseInt(claimTemplateForm.getAllowNoOfTimesVal()));
			}
			claimTemplateVO.setAllowIfAtLeastOneAttachment(claimTemplateForm.getAllowIfAtLeastOneAttacment());
			claimTemplateVO.setProration(claimTemplateForm.getProration());

			if (claimTemplateForm.getProration()) {
				AppCodeMaster appCodeMaster = appCodeMasterDAO
						.findById(claimTemplateForm.getProrationBasedOnAppCodeId());
				claimTemplateVO.setProrationBasedOn(appCodeMaster);
			} else {
				claimTemplateVO.setProrationBasedOn(null);
			}

			CurrencyMaster currencyMaster = currencyMasterDAO.findById(claimTemplateForm.getCurrencyId());
			claimTemplateVO.setDefaultCurrency(currencyMaster);
			claimTemplateVO.setClaimReviewersBasedOnClaimAmount(claimTemplateForm.getClaimReviewersBasedOnClaimAmt());
			claimTemplateDAO.update(claimTemplateVO);

			claimAmountReviewerTemplateDAO.deleteByCondition(claimTemplateVO.getClaimTemplateId());
			if (claimTemplateForm.getClaimReviewersBasedOnClaimAmt()) {
				List<ClaimAmtReviewersTemplateDTO> customFieldsDTOLists = claimTemplateForm.getClaimAmtRevTempDTOList();
				for (ClaimAmtReviewersTemplateDTO customFieldsDTO : customFieldsDTOLists) {
					if (StringUtils.isBlank(customFieldsDTO.getLevel1ReviewerId())) {
						continue;
					}
					ClaimAmountReviewerTemplate claimAmountReviewerTemplate = new ClaimAmountReviewerTemplate();
					claimAmountReviewerTemplate.setFromClaimAmount(customFieldsDTO.getFromClaimAmount());
					claimAmountReviewerTemplate.setToClaimAmount(customFieldsDTO.getToClaimAmount());

					if (!customFieldsDTO.getLevel1ReviewerId().equals("0")) {
						WorkFlowRuleMaster workFlowRuleMasterLevel1 = getWorkFlowMasterId(
								PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER,
								String.valueOf(customFieldsDTO.getLevel1ReviewerId()), workFlowRuleMasterList);
						claimAmountReviewerTemplate.setLevel1(workFlowRuleMasterLevel1);
					}

					if (!customFieldsDTO.getLevel2ReviewerId().equals("0")) {
						WorkFlowRuleMaster workFlowRuleMasterLevel2 = getWorkFlowMasterId(
								PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER,
								String.valueOf(customFieldsDTO.getLevel2ReviewerId()), workFlowRuleMasterList);
						claimAmountReviewerTemplate.setLevel2(workFlowRuleMasterLevel2);
					}

					if (!customFieldsDTO.getLevel3ReviewerId().equals("0")) {
						WorkFlowRuleMaster workFlowRuleMasterLevel3 = getWorkFlowMasterId(
								PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER,
								String.valueOf(customFieldsDTO.getLevel3ReviewerId()), workFlowRuleMasterList);
						claimAmountReviewerTemplate.setLevel3(workFlowRuleMasterLevel3);
					}

					claimAmountReviewerTemplate.setClaimTemplate(claimTemplateVO);
					claimAmountReviewerTemplateDAO.save(claimAmountReviewerTemplate);
				}
			}

		}

		if (StringUtils.isNotBlank(claimTemplateForm.getWorkFlowLevel())
				&& (!claimTemplateForm.getWorkFlowLevel().equalsIgnoreCase("-1"))) {
			Integer workFlowRuleValue = Integer.valueOf(claimTemplateForm.getWorkFlowLevel().substring(11));

			if (workFlowRuleValue >= maxWorkFlowRuleValue) {
				ClaimTemplateWorkflow claimTemplateWorkflowVO = claimTemplateWorkflowDAO.findByTemplateIdRuleName(
						FormatPreserveCryptoUtil.decrypt(claimTemplateForm.getClaimTemplateId()), companyId,
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL);
				if (claimTemplateWorkflowVO != null) {
					claimTemplateWorkflowDAO.deleteByCondition(FormatPreserveCryptoUtil.decrypt(claimTemplateForm.getClaimTemplateId()));
				}
				if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
						.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL1)) {
					WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
							PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "1", workFlowRuleMasterList);
					ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

					claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
					claimTemplateWorkflow.setClaimTemplate(claimTemplateVO);
					claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

				}
				if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
						.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL2)) {
					WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
							PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "2", workFlowRuleMasterList);
					ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

					claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
					claimTemplateWorkflow.setClaimTemplate(claimTemplateVO);
					claimTemplateWorkflowDAO.save(claimTemplateWorkflow);
				}
				if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
						.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL3)) {
					WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
							PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "3", workFlowRuleMasterList);
					ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

					claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
					claimTemplateWorkflow.setClaimTemplate(claimTemplateVO);
					claimTemplateWorkflowDAO.save(claimTemplateWorkflow);
				}

				String allowOverrideStr = "";
				if (claimTemplateForm.getAllowOverrideCTL1() == true) {
					allowOverrideStr += "1";
				} else {
					allowOverrideStr += "0";
				}

				if (claimTemplateForm.getAllowOverrideCTL2() == true) {
					allowOverrideStr += "1";
				} else {
					allowOverrideStr += "0";
				}

				if (claimTemplateForm.getAllowOverrideCTL3() == true) {
					allowOverrideStr += "1";
				} else {
					allowOverrideStr += "0";
				}

				WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_OVERRIDE, allowOverrideStr.trim(),
						workFlowRuleMasterList);
				ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();

				claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
				claimTemplateWorkflow.setClaimTemplate(claimTemplateVO);
				claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

				String allowRejectStr = "";
				if (claimTemplateForm.getAllowRejectCTL1() == true) {
					allowRejectStr += "1";
				} else {
					allowRejectStr += "0";
				}

				if (claimTemplateForm.getAllowRejectCTL2() == true) {
					allowRejectStr += "1";
				} else {
					allowRejectStr += "0";
				}

				if (claimTemplateForm.getAllowRejectCTL3() == true) {
					allowRejectStr += "1";
				} else {
					allowRejectStr += "0";
				}

				WorkFlowRuleMaster workFlowRuleAllowRejectMaster = getWorkFlowMasterId(
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_REJECT, allowRejectStr.trim(),
						workFlowRuleMasterList);
				claimTemplateWorkflow = new ClaimTemplateWorkflow();

				claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
				claimTemplateWorkflow.setClaimTemplate(claimTemplateVO);
				claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

				String allowForward = "";
				if (claimTemplateForm.getAllowForward1() == true) {
					allowForward += "1";
				} else {
					allowForward += "0";
				}

				if (claimTemplateForm.getAllowForward2() == true) {
					allowForward += "1";
				} else {
					allowForward += "0";
				}

				if (claimTemplateForm.getAllowForward3() == true) {
					allowForward += "1";
				} else {
					allowForward += "0";
				}

				WorkFlowRuleMaster workFlowRuleAllowForwardMaster = getWorkFlowMasterId(
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_FORWARD, allowForward.trim(), workFlowRuleMasterList);
				claimTemplateWorkflow = new ClaimTemplateWorkflow();

				claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowForwardMaster);
				claimTemplateWorkflow.setClaimTemplate(claimTemplateVO);
				claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

				String allowApproveStr = "";
				if (claimTemplateForm.getAllowApprove1() == true) {
					allowApproveStr += "1";
				} else {
					allowApproveStr += "0";
				}

				if (claimTemplateForm.getAllowApprove2() == true) {
					allowApproveStr += "1";
				} else {
					allowApproveStr += "0";
				}

				if (claimTemplateForm.getAllowApprove3() == true) {
					allowApproveStr += "1";
				} else {
					allowApproveStr += "0";
				}

				WorkFlowRuleMaster workFlowRuleAllowApproveMaster = getWorkFlowMasterId(
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_APPROVE, allowApproveStr.trim(),
						workFlowRuleMasterList);
				claimTemplateWorkflow = new ClaimTemplateWorkflow();

				claimTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowApproveMaster);
				claimTemplateWorkflow.setClaimTemplate(claimTemplateVO);
				claimTemplateWorkflowDAO.save(claimTemplateWorkflow);

				return "notavailable";
			} else {
				return workFlowRuleValue.toString();
			}
		}
		return "notavailable";

	}

	public WorkFlowRuleMaster getWorkFlowMasterId(String ruleName, String ruleValue,
			List<WorkFlowRuleMaster> workFlowRuleMasterList) {
		for (WorkFlowRuleMaster workFlowRuleMaster : workFlowRuleMasterList) {
			if (ruleName.equalsIgnoreCase(workFlowRuleMaster.getRuleName())
					&& ruleValue.equals(workFlowRuleMaster.getRuleValue())) {
				return workFlowRuleMaster;
			}
		}
		return null;
	}

	@Override
	public Set<ClaimTemplateForm> getClaimTypeList(Long companyId, Long claimTemplateId, Long itemCategoryId) {
		Set<ClaimTemplateForm> claimTemplateFormSet = new HashSet<ClaimTemplateForm>();

		List<ClaimItemMaster> claimItemMasterList = claimItemMasterDAO.findByCondition(claimTemplateId,companyId);

		List<ClaimItemMaster> claimItemMasterVOList = claimItemMasterDAO.findAll(companyId, null, null, itemCategoryId);
		claimItemMasterVOList.removeAll(claimItemMasterList);

		for (ClaimItemMaster claimItemMasterVO : claimItemMasterVOList) {
			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();

			claimTemplateForm.setClaimItemId(FormatPreserveCryptoUtil.encrypt(claimItemMasterVO.getClaimItemId()));
			claimTemplateForm.setClaimItem(claimItemMasterVO.getClaimItemName());
			claimTemplateForm.setClaimItemCategory(claimItemMasterVO.getClaimCategoryMaster().getClaimCategoryName());
			claimTemplateFormSet.add(claimTemplateForm);

		}

		return claimTemplateFormSet;
	}

	@Override
	public void addClaimType(String[] claimTypeId, Long claimTemplateId,Long companyId) {
		ClaimTemplateItem claimTemplateItem = new ClaimTemplateItem();

		ClaimTemplate claimTemplateVO = claimTemplateDAO.findByClaimTemplateID(claimTemplateId,companyId);
		Set<ClaimTemplateWorkflow> claimTemplateWorkflows = claimTemplateVO.getClaimTemplateWorkflows();

		for (int count = 0; count < claimTypeId.length; count++) {
			
			ClaimItemMaster claimItemMasterVO = claimItemMasterDAO.findByClaimItemMasterId(FormatPreserveCryptoUtil.decrypt(Long.parseLong(claimTypeId[count])),companyId);

			claimTemplateItem.setClaimTemplate(claimTemplateVO);
			claimTemplateItem.setClaimItemMaster(claimItemMasterVO);
			claimTemplateItem.setVisibility(true);
			claimTemplateItem.setWorkflowChanged(false);
			ClaimTemplateItem persistClaimTemplateItem = claimTemplateItemDAO.saveReturn(claimTemplateItem);

			if (claimTemplateVO.getEmployeeClaimTemplates().size() > 0) {

				for (EmployeeClaimTemplate employeeClaimTemplate : claimTemplateVO.getEmployeeClaimTemplates()) {

					EmployeeClaimTemplateItem employeeClaimTemplateItem = new EmployeeClaimTemplateItem();
					employeeClaimTemplateItem.setClaimTemplateItem(persistClaimTemplateItem);
					employeeClaimTemplateItem.setEmployeeClaimTemplate(employeeClaimTemplate);
					employeeClaimTemplateItemDAO.save(employeeClaimTemplateItem);

				}

			}

			for (ClaimTemplateWorkflow claimTemplateWorkflowVO : claimTemplateWorkflows) {

				ClaimTemplateItemWorkflow claimTemplateItemWorkflowVO = new ClaimTemplateItemWorkflow();
				claimTemplateItemWorkflowVO.setClaimTemplateItem(persistClaimTemplateItem);
				claimTemplateItemWorkflowVO.setWorkFlowRuleMaster(claimTemplateWorkflowVO.getWorkFlowRuleMaster());
				claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflowVO);

			}

		}

	}

	@Override
	public ClaimTemplateResponse viewClaimType(Long claimTemplateId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, Locale locale) {
		List<ClaimTemplateForm> claimTemplateFormList = new ArrayList<ClaimTemplateForm>();
		String yes = "payasia.yes";
		String no = "payasia.no";
		String changed = "payasia.changed";
		String noChange = "payasia.no.change";
		String set = "payasia.set";
		String notSet = "payasia.not.set";
		List<ClaimTemplateItem> claimTemplateItemVOList = claimTemplateItemDAO.findByCondition(FormatPreserveCryptoUtil.decrypt(claimTemplateId),companyId);
		
		for (ClaimTemplateItem claimTemplateItemVO : claimTemplateItemVOList) {
			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
			claimTemplateForm.setClaimItemId(claimTemplateItemVO.getClaimItemMaster().getClaimItemId());
			claimTemplateForm.setClaimItem(claimTemplateItemVO.getClaimItemMaster().getClaimItemName());
			claimTemplateForm.setClaimTemplateItemId(FormatPreserveCryptoUtil.encrypt(claimTemplateItemVO.getClaimTemplateItemId()));
			claimTemplateForm.setClaimItemCategory(
					claimTemplateItemVO.getClaimItemMaster().getClaimCategoryMaster().getClaimCategoryName());
			StringBuilder claimTypeActive = new StringBuilder();
			long claimTemplateItemId = FormatPreserveCryptoUtil.encrypt(claimTemplateItemVO.getClaimTemplateItemId());
			if (claimTemplateItemVO.getVisibility() == true) {
				claimTypeActive
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'editClaimType("
								+ claimTemplateItemId + ")'><span class='ctextgreen'>"
								+ messageSource.getMessage(yes, new Object[] {}, locale) + "</span></a>");

			}
			if (claimTemplateItemVO.getVisibility() == false) {
				claimTypeActive
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'editClaimType("
								+ claimTemplateItemId + ")'><span class='ctextgray'>"
								+ messageSource.getMessage(no, new Object[] {}, locale) + "</span></a>");
			}
			claimTemplateForm.setActive(String.valueOf(claimTypeActive));

			StringBuilder claimTemplateItemWorkFlow = new StringBuilder();
			if (claimTemplateItemVO.isWorkflowChanged()) {

				claimTemplateItemWorkFlow
						.append("<a class='alink' style='text-decoration: none;' href='#' ><span class='ctextgreen'>"
								+ messageSource.getMessage(changed, new Object[] {}, locale) + "</span></a>");

			} else {

				claimTemplateItemWorkFlow
						.append("<a class='alink' style='text-decoration: none;' href='#' ><span class='ctextgreen'>"
								+ messageSource.getMessage(noChange, new Object[] {}, locale) + "</span></a>");

			}
			claimTemplateForm.setClaimTemplateItemWorkflow(String.valueOf(claimTemplateItemWorkFlow));

			Set<ClaimTemplateItemShortlist> claimTemplateItemShortlistVOs = claimTemplateItemVO
					.getClaimTemplateItemShortlists();

			StringBuilder claimTemplateItemShortList = new StringBuilder();
			if (!claimTemplateItemShortlistVOs.isEmpty()) {

				claimTemplateItemShortList
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'viewClaimTemplateItemShortList("
								+ claimTemplateItemId + ")'><span class='ctextgreen'>"
								+ messageSource.getMessage(set, new Object[] {}, locale) + "</span></a>");

			} else {

				claimTemplateItemShortList
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'viewClaimTemplateItemShortList("
								+ claimTemplateItemId + ")'><span class='ctextgray'>"
								+ messageSource.getMessage(notSet, new Object[] {}, locale) + "</span></a>");

			}
			claimTemplateForm.setClaimTemplateItemShortList(String.valueOf(claimTemplateItemShortList));

			Set<ClaimTemplateItemClaimType> claimTemplateItemClaimTypes = claimTemplateItemVO
					.getClaimTemplateItemClaimTypes();

			StringBuilder claimTypeConfigure = new StringBuilder();
			if (!claimTemplateItemClaimTypes.isEmpty()) {

				claimTypeConfigure
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'viewConfigureClaimTemplateItem("
								+ claimTemplateItemId + ")'><span class='ctextgreen'>"
								+ messageSource.getMessage(set, new Object[] {}, locale) + "</span></a>");

			} else {

				claimTypeConfigure
						.append("<a class='alink' style='text-decoration: none;' href='#' onClick = 'viewConfigureClaimTemplateItem("
								+ claimTemplateItemId + ")'><span class='ctextgray'>"
								+ messageSource.getMessage(notSet, new Object[] {}, locale) + "</span></a>");

			}
			claimTemplateForm.setClaimTypeConfigure(String.valueOf(claimTypeConfigure));

			claimTemplateFormList.add(claimTemplateForm);
		}
		ClaimTemplateResponse response = new ClaimTemplateResponse();
		response.setRows(claimTemplateFormList);

		return response;
	}

	@Override
	public void editClaimType(Long claimTemplateTypeId, ClaimTemplateForm claimTemplateForm,Long companyId) {
		ClaimTemplateItem claimTemplateItem = claimTemplateItemDAO.findByClaimTemplateItemId(claimTemplateTypeId,companyId);
		claimTemplateItem.setVisibility(claimTemplateForm.getVisibility());
		claimTemplateItemDAO.update(claimTemplateItem);
	}

	@Override
	public ClaimTemplateForm getClaimTypeForEdit(Long claimTemplateTypeId,Long companyId) {		
		ClaimTemplateItem claimTemplateItemVO = claimTemplateItemDAO.findByClaimTemplateItemId(claimTemplateTypeId,companyId);
		ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
		claimTemplateForm.setClaimItem(claimTemplateItemVO.getClaimItemMaster().getClaimItemName());
		claimTemplateForm.setVisibility(claimTemplateItemVO.getVisibility());
		return claimTemplateForm;
	}

	@Override
	public void deleteClaimType(Long companyId,Long claimTemplateItemId) {
		ClaimTemplateItem claimTemplateItemVO = claimTemplateItemDAO.findByClaimTemplateItemId(claimTemplateItemId,companyId);
		claimTemplateItemDAO.delete(claimTemplateItemVO);
	}

	@Override
	public ClaimTemplateForm getClaimTemplateItemWorkflow(Long companyId, Long claimTemplateItemId) {
		ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();

		ClaimTemplateItem claimTemplateItem = claimTemplateItemDAO.findByClaimTemplateItemId(claimTemplateItemId,companyId);
		Set<ClaimTemplateItemWorkflow> claimTemplateItemWorkflowVOs = claimTemplateItem.getClaimTemplateItemWorkflows();

		for (ClaimTemplateItemWorkflow claimTemplateItemWorkflowVO : claimTemplateItemWorkflowVOs) {

			if (claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL)) {
				claimTemplateForm
						.setWorkFlowLevelCT(claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleValue());
			}

			if (claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
				String allowOverRideVal = claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowOverRideVal.charAt(0);
				char atlevelTwo = allowOverRideVal.charAt(1);
				char atlevelThree = allowOverRideVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowOverrideCTL1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowOverrideCTL1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowOverrideCTL2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowOverrideCTL2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowOverrideCTL3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowOverrideCTL3(true);
				}
			}

			if (claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_REJECT)) {
				String allowRejectVal = claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowRejectVal.charAt(0);
				char atlevelTwo = allowRejectVal.charAt(1);
				char atlevelThree = allowRejectVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowRejectCTL1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowRejectCTL1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowRejectCTL2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowRejectCTL2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowRejectCTL3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowRejectCTL3(true);
				}
			}

			if (claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_FORWARD)) {
				String allowForwardVal = claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowForwardVal.charAt(0);
				char atlevelTwo = allowForwardVal.charAt(1);
				char atlevelThree = allowForwardVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowForward1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowForward1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowForward2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowForward2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowForward3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowForward3(true);
				}
			}

			if (claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleName().toUpperCase()
					.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_APPROVE)) {
				String allowApproveVal = claimTemplateItemWorkflowVO.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowApproveVal.charAt(0);
				char atlevelTwo = allowApproveVal.charAt(1);
				char atlevelThree = allowApproveVal.charAt(2);

				if (atlevelOne == '0') {
					claimTemplateForm.setAllowApprove1(false);
				}
				if (atlevelOne == '1') {
					claimTemplateForm.setAllowApprove1(true);
				}
				if (atlevelTwo == '0') {
					claimTemplateForm.setAllowApprove2(false);
				}
				if (atlevelTwo == '1') {
					claimTemplateForm.setAllowApprove2(true);
				}
				if (atlevelThree == '0') {
					claimTemplateForm.setAllowApprove3(false);
				}
				if (atlevelThree == '1') {
					claimTemplateForm.setAllowApprove3(true);
				}
			}
		}
		return claimTemplateForm;
	}

	private void isWorkFlowChanged(Long claimTemplateId, Long claimTemplateItemId) {
		Boolean workFlowChanged = false;

		List<ClaimTemplateWorkflow> claimTemplateWorkflows = claimTemplateWorkflowDAO.findByCondition(claimTemplateId);

		Collections.sort(claimTemplateWorkflows, new ClaimTemplateWorkFlowComp());
		List<ClaimTemplateItemWorkflow> claimTemplateItemWorkflows = claimTemplateItemWorkflowDAO
				.findByCondition(claimTemplateItemId);

		Collections.sort(claimTemplateItemWorkflows, new ClaimTemplateItemWorkFLowComp());

		if (claimTemplateWorkflows.size() == claimTemplateItemWorkflows.size()) {

			int workFlowCount = 0;
			for (ClaimTemplateWorkflow claimTemplateWorkFlow : claimTemplateWorkflows) {

				if (claimTemplateWorkFlow.getWorkFlowRuleMaster().getWorkFlowRuleId() != claimTemplateItemWorkflows
						.get(workFlowCount).getWorkFlowRuleMaster().getWorkFlowRuleId()) {
					workFlowChanged = true;
				}
				workFlowCount++;
			}

		} else {
			workFlowChanged = true;

		}
		ClaimTemplateItem claimTemplateItemVO = claimTemplateItemDAO.findById(claimTemplateItemId);

		if (workFlowChanged) {
			claimTemplateItemVO.setWorkflowChanged(true);

		} else {
			claimTemplateItemVO.setWorkflowChanged(false);

		}
		claimTemplateItemDAO.update(claimTemplateItemVO);

	}

	/**
	 * Comparator
	 */
	private class ClaimTemplateWorkFlowComp implements Comparator<ClaimTemplateWorkflow> {
		public int compare(ClaimTemplateWorkflow templateField, ClaimTemplateWorkflow compWithTemplateField) {
			if (templateField.getWorkFlowRuleMaster().getWorkFlowRuleId() > compWithTemplateField
					.getWorkFlowRuleMaster().getWorkFlowRuleId()) {
				return 1;
			} else if (templateField.getWorkFlowRuleMaster().getWorkFlowRuleId() < compWithTemplateField
					.getWorkFlowRuleMaster().getWorkFlowRuleId()) {
				return -1;
			}
			return 0;

		}

	}

	/**
	 * Comparator
	 */
	private class ClaimTemplateItemWorkFLowComp implements Comparator<ClaimTemplateItemWorkflow> {
		public int compare(ClaimTemplateItemWorkflow templateField, ClaimTemplateItemWorkflow compWithTemplateField) {
			if (templateField.getWorkFlowRuleMaster().getWorkFlowRuleId() > compWithTemplateField
					.getWorkFlowRuleMaster().getWorkFlowRuleId()) {
				return 1;
			} else if (templateField.getWorkFlowRuleMaster().getWorkFlowRuleId() < compWithTemplateField
					.getWorkFlowRuleMaster().getWorkFlowRuleId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public String updateClaimTemplateItemWorkflow(ClaimTemplateForm claimTemplateForm, Long companyId) {

		ClaimTemplateItem claimTemplateItemVO = claimTemplateItemDAO
				.findById(claimTemplateForm.getClaimTemplateItemId());
		Set<ClaimTemplateItemWorkflow> claimTemplateItemWorkflows = claimTemplateItemVO.getClaimTemplateItemWorkflows();

		if (!claimTemplateItemWorkflows.isEmpty()) {
			claimTemplateItemWorkflowDAO.deleteByCondition(claimTemplateForm.getClaimTemplateItemId());
		}

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO.findAll();

		if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL1)) {
			WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
					PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "1", workFlowRuleMasterList);
			ClaimTemplateItemWorkflow claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();

			claimTemplateItemWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			claimTemplateItemWorkflow.setClaimTemplateItem(claimTemplateItemVO);
			claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflow);

		}
		if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL2)) {
			WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
					PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "2", workFlowRuleMasterList);
			ClaimTemplateItemWorkflow claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();

			claimTemplateItemWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			claimTemplateItemWorkflow.setClaimTemplateItem(claimTemplateItemVO);
			claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflow);
		}
		if (claimTemplateForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOWCTL3)) {
			WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(
					PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL, "3", workFlowRuleMasterList);
			ClaimTemplateItemWorkflow claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();

			claimTemplateItemWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			claimTemplateItemWorkflow.setClaimTemplateItem(claimTemplateItemVO);
			claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflow);
		}

		String allowOverrideStr = "";
		if (claimTemplateForm.getAllowOverrideCTL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (claimTemplateForm.getAllowOverrideCTL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (claimTemplateForm.getAllowOverrideCTL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		WorkFlowRuleMaster workFlowRuleMaster = getWorkFlowMasterId(PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		ClaimTemplateItemWorkflow claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();

		claimTemplateItemWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		claimTemplateItemWorkflow.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflow);

		String allowRejectStr = "";
		if (claimTemplateForm.getAllowRejectCTL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (claimTemplateForm.getAllowRejectCTL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (claimTemplateForm.getAllowRejectCTL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = getWorkFlowMasterId(
				PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_REJECT, allowRejectStr.trim(), workFlowRuleMasterList);
		claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();

		claimTemplateItemWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		claimTemplateItemWorkflow.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflow);

		String allowForward = "";
		if (claimTemplateForm.getAllowForward1() == true) {
			allowForward += "1";
		} else {
			allowForward += "0";
		}

		if (claimTemplateForm.getAllowForward2() == true) {
			allowForward += "1";
		} else {
			allowForward += "0";
		}

		if (claimTemplateForm.getAllowForward3() == true) {
			allowForward += "1";
		} else {
			allowForward += "0";
		}

		WorkFlowRuleMaster workFlowRuleAllowForwardMaster = getWorkFlowMasterId(
				PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_FORWARD, allowForward.trim(), workFlowRuleMasterList);
		claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();

		claimTemplateItemWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowForwardMaster);
		claimTemplateItemWorkflow.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflow);

		String allowApproveStr = "";
		if (claimTemplateForm.getAllowApprove1() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (claimTemplateForm.getAllowApprove2() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (claimTemplateForm.getAllowApprove3() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		WorkFlowRuleMaster workFlowRuleAllowApproveMaster = getWorkFlowMasterId(
				PayAsiaConstants.CLAIM_TEMPLATE_DEF_ALLOW_APPROVE, allowApproveStr.trim(), workFlowRuleMasterList);
		claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();

		claimTemplateItemWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowApproveMaster);
		claimTemplateItemWorkflow.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemWorkflowDAO.save(claimTemplateItemWorkflow);

		isWorkFlowChanged(claimTemplateItemVO.getClaimTemplate().getClaimTemplateId(),
				claimTemplateItemVO.getClaimTemplateItemId());

		return "notavailable";

	}

	@Override
	public String saveEmployeeFilterList(String metaData, Long claimTemplateItemId, Long companyId) {

		ClaimTemplateItem claimTemplateItem = claimTemplateItemDAO.findByClaimTemplateItemId(claimTemplateItemId,companyId);

		EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId, entityMaster.getEntityId());

		Calendar cal = Calendar.getInstance();
		List<EmployeeClaimTemplateItem> cmpEmployeeClaimTemplateItems = employeeClaimTemplateItemDAO
				.findByCompanyCondition(companyId, cal.getTime(),
						claimTemplateItem.getClaimTemplate().getClaimTemplateId());

		HashMap<String, EmployeeClaimTemplateItem> employeeClaimTemplateItemMap = new HashMap<>();
		for (EmployeeClaimTemplateItem employeeClaimTemplateItem : cmpEmployeeClaimTemplateItems) {

			String mapkey = String.valueOf(employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId())
					+ "_" + String.valueOf(
							employeeClaimTemplateItem.getEmployeeClaimTemplate().getEmployee().getEmployeeId());
			employeeClaimTemplateItemMap.put(mapkey, employeeClaimTemplateItem);
		}

		List<Long> claimTemplateEmpIds = employeeClaimTemplateDAO
				.getEmployeesOfClaimTemplate(claimTemplateItem.getClaimTemplate().getClaimTemplateId());

		claimTemplateItemShortlistDAO.deleteByCondition(claimTemplateItemId);
		EmployeeFilterTemplate empFilterTemplate = getEmpFilterTab(metaData);
		List<EmployeeFilter> listOfFields = empFilterTemplate.getEmployeeFilter();
		for (EmployeeFilter field : listOfFields) {

			ClaimTemplateItemShortlist claimTemplateItemShortlist = new ClaimTemplateItemShortlist();

			claimTemplateItemShortlist.setClaimTemplateItem(claimTemplateItem);

			if (field.getCloseBracket() != null && !field.getCloseBracket().equals("")) {
				claimTemplateItemShortlist.setCloseBracket(field.getCloseBracket());
			}
			if (field.getOpenBracket() != null && !field.getOpenBracket().equals("")) {
				claimTemplateItemShortlist.setOpenBracket(field.getOpenBracket());

			}
			if (field.getDictionaryId() != 0) {
				DataDictionary dataDictionary = dataDictionaryDAO.findById(field.getDictionaryId());
				claimTemplateItemShortlist.setDataDictionary(dataDictionary);
			}

			if (field.getEqualityOperator() != null && !field.getEqualityOperator().equals("")) {
				try {
					String equalityOperator = URLDecoder.decode(field.getEqualityOperator(), "UTF-8");
					// Check Valid ShortList Operator
					ShortlistOperatorEnum shortlistOperatorEnum = ShortlistOperatorEnum
							.getFromOperator(equalityOperator);
					if (shortlistOperatorEnum == null) {
						throw new PayAsiaSystemException(PayAsiaConstants.PAYASIA_SHORTLIST_OPERATOR_NOT_VALID);
					}

					claimTemplateItemShortlist.setEqualityOperator(equalityOperator);
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

			}
			if (field.getLogicalOperator() != null && !field.getLogicalOperator().equals("")) {
				claimTemplateItemShortlist.setLogicalOperator(field.getLogicalOperator());
			}
			if (field.getValue() != null && !field.getValue().equals("")) {
				String fieldValue = "";
				try {
					fieldValue = URLDecoder.decode(field.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
				claimTemplateItemShortlist.setValue(fieldValue);
			}

			claimTemplateItemShortlistDAO.save(claimTemplateItemShortlist);

		}
		List<ClaimTemplateItemShortlist> claimTemplateItemShortlists = claimTemplateItemShortlistDAO
				.findByCondition(claimTemplateItem.getClaimTemplateItemId(),companyId);

		setEmployeeClaimTemplateItem(claimTemplateItemShortlists, companyId, claimTemplateEmpIds, claimTemplateItem,
				null, formIds, employeeClaimTemplateItemMap);

		return null;
	}

	@Override
	public void setEmployeeClaimTemplateItem(List<ClaimTemplateItemShortlist> claimTemplateItemShortlists,
			Long companyId, List<Long> claimTemplateEmpIds, ClaimTemplateItem claimTemplateItem, Date currentDate,
			List<Long> formIds, HashMap<String, EmployeeClaimTemplateItem> employeeClaimTemplateItemMap) {
		Map<String, String> paramValueMap = new HashMap<String, String>();
		HashMap<Long, Tab> tabMap = new HashMap<>();
		Set<DynamicTableDTO> tableNames = new HashSet<DynamicTableDTO>();
		List<CodeDescDTO> codeDescDTOs = new ArrayList<CodeDescDTO>();
		List<GeneralFilterDTO> finalFilterList = new ArrayList<GeneralFilterDTO>();

		setFilterInfo(claimTemplateItemShortlists, finalFilterList, tableNames, codeDescDTOs, tabMap);

		String query = filtersInfoUtilsLogic.createQueryFilters(null, formIds, finalFilterList, tableNames,
				codeDescDTOs, companyId, currentDate, paramValueMap);

		List<BigInteger> resultSet = employeeDAO.checkForEmployeeDocuments(query, paramValueMap, null, companyId);

		List<Long> shortListedEmployeeIds = new ArrayList<>();

		for (BigInteger employeeId : resultSet) {
			shortListedEmployeeIds.add(Long.parseLong(String.valueOf(employeeId)));
		}

		List<Long> claimTemplateEmployeeIdsCopy = new ArrayList<>(claimTemplateEmpIds);

		List<EmployeeClaimTemplate> employeeClaimTemplates = employeeClaimTemplateDAO
				.findByClaimTemplate(claimTemplateItem.getClaimTemplate().getClaimTemplateId());
		HashMap<String, EmployeeClaimTemplate> employeeClaimTemplateMap = new HashMap<>();
		for (EmployeeClaimTemplate employeeClaimTemplate : employeeClaimTemplates) {

			String empLeaMapKey = String.valueOf(employeeClaimTemplate.getClaimTemplate().getClaimTemplateId()) + "_"
					+ String.valueOf(employeeClaimTemplate.getEmployee().getEmployeeId());
			employeeClaimTemplateMap.put(empLeaMapKey, employeeClaimTemplate);

		}

		claimTemplateEmpIds.retainAll(shortListedEmployeeIds);

		claimTemplateEmployeeIdsCopy.removeAll(claimTemplateEmpIds);

		for (Long employeeId : claimTemplateEmpIds) {

			EmployeeClaimTemplateItem employeeClaimTemplateItem = employeeClaimTemplateItemMap
					.get(String.valueOf(claimTemplateItem.getClaimTemplateItemId()) + "_" + String.valueOf(employeeId));

			if (employeeClaimTemplateItem == null) {

				EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateMap
						.get(String.valueOf(claimTemplateItem.getClaimTemplate().getClaimTemplateId()) + "_"
								+ String.valueOf(employeeId));

				employeeClaimTemplateItem = new EmployeeClaimTemplateItem();
				employeeClaimTemplateItem.setClaimTemplateItem(claimTemplateItem);
				employeeClaimTemplateItem.setEmployeeClaimTemplate(employeeClaimTemplate);
				employeeClaimTemplateItem.setActive(true);
				employeeClaimTemplateItemDAO.save(employeeClaimTemplateItem);
			} else {

				if (!employeeClaimTemplateItem.getActive()) {
					employeeClaimTemplateItem.setActive(true);
					employeeClaimTemplateItemDAO.update(employeeClaimTemplateItem);
				}

			}

		}

		for (Long employeeId : claimTemplateEmployeeIdsCopy) {

			EmployeeClaimTemplateItem employeeClaimTemplateItem = employeeClaimTemplateItemDAO
					.findByEmployeeIdAndClaimTemplateItemId(employeeId, claimTemplateItem.getClaimTemplateItemId(),companyId);

			if (employeeClaimTemplateItem != null) {
				employeeClaimTemplateItem.setActive(false);
				employeeClaimTemplateItemDAO.update(employeeClaimTemplateItem);
			}

		}

	}

	private void setFilterInfo(List<ClaimTemplateItemShortlist> claimTemplateItemShortlists,
			List<GeneralFilterDTO> finalFilterList, Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			HashMap<Long, Tab> tabMap) {
		for (ClaimTemplateItemShortlist claimTemplateItemShortlist : claimTemplateItemShortlists) {
			DataImportKeyValueDTO dataImportKeyValueDTO = new DataImportKeyValueDTO();

			if (claimTemplateItemShortlist.getDataDictionary().getFieldType()
					.equalsIgnoreCase(PayAsiaConstants.STATIC_TYPE)) {

				filtersInfoUtilsLogic.getStaticDictionaryInfo(claimTemplateItemShortlist.getDataDictionary(),
						dataImportKeyValueDTO);

			} else {

				filtersInfoUtilsLogic.getDynamicDictionaryInfo(claimTemplateItemShortlist.getDataDictionary(),
						dataImportKeyValueDTO, tableNames, codeDescDTOs, tabMap);

			}
			GeneralFilterDTO generalFilterDTO = new GeneralFilterDTO();

			if (claimTemplateItemShortlist.getCloseBracket() != null) {
				generalFilterDTO.setCloseBracket(claimTemplateItemShortlist.getCloseBracket());
			} else {
				generalFilterDTO.setCloseBracket("");
			}

			if (claimTemplateItemShortlist.getOpenBracket() != null) {
				generalFilterDTO.setOpenBracket(claimTemplateItemShortlist.getOpenBracket());
			} else {
				generalFilterDTO.setOpenBracket("");
			}

			generalFilterDTO.setDataDictionaryName(claimTemplateItemShortlist.getDataDictionary().getDataDictName());
			generalFilterDTO.setDataImportKeyValueDTO(dataImportKeyValueDTO);
			generalFilterDTO.setDictionaryId(claimTemplateItemShortlist.getDataDictionary().getDataDictionaryId());
			generalFilterDTO.setEqualityOperator(claimTemplateItemShortlist.getEqualityOperator());
			generalFilterDTO.setFilterId(claimTemplateItemShortlist.getShort_List_ID());
			generalFilterDTO.setLogicalOperator(claimTemplateItemShortlist.getLogicalOperator());
			generalFilterDTO.setValue(claimTemplateItemShortlist.getValue());

			finalFilterList.add(generalFilterDTO);
		}

	}

	private EmployeeFilterTemplate getEmpFilterTab(String metaData) {
		Unmarshaller unmarshaller = null;
		String decodedMetaData=null;
		try {
			
		   decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
		   unmarshaller = EmployeeFilterXMLUtil.getDocumentUnmarshaller();
		} catch (UnsupportedEncodingException | JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		} catch (SAXException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}

		final StringReader xmlReader = new StringReader(decodedMetaData);
		Source xmlSource = null;
		try {
			xmlSource = EmployeeFilterXMLUtil.getSAXSource(xmlReader);
		} catch (SAXException | ParserConfigurationException e1) {
			LOGGER.error(e1.getMessage(), e1);
			throw new PayAsiaSystemException(e1.getMessage(),
					e1);
		}

		EmployeeFilterTemplate empFilterTemplate = null;
		try {
			empFilterTemplate = (EmployeeFilterTemplate) unmarshaller.unmarshal(xmlSource);
		} catch (JAXBException ex) {

			LOGGER.error(ex.getMessage(), ex);
		}
		return empFilterTemplate;
	}

	@Override
	public List<EmployeeFilterListForm> getClaimTemplateItemShortlist(Long claimTemplateItemId, Long companyId) {
		List<EmployeeFilterListForm> employeeFilterList = new ArrayList<EmployeeFilterListForm>();
		List<ClaimTemplateItemShortlist> claimTemplateItemShortlists = claimTemplateItemShortlistDAO
				.findByCondition(FormatPreserveCryptoUtil.decrypt(claimTemplateItemId),companyId);

		for (ClaimTemplateItemShortlist claimTemplateItemShortlist : claimTemplateItemShortlists) {
			EmployeeFilterListForm employeeFilterListForm = new EmployeeFilterListForm();
			employeeFilterListForm.setCloseBracket(claimTemplateItemShortlist.getCloseBracket());
			employeeFilterListForm
					.setDataDictionaryId(claimTemplateItemShortlist.getDataDictionary().getDataDictionaryId());
			employeeFilterListForm.setEqualityOperator(claimTemplateItemShortlist.getEqualityOperator());
			employeeFilterListForm.setFilterId(claimTemplateItemShortlist.getShort_List_ID());
			employeeFilterListForm.setLogicalOperator(claimTemplateItemShortlist.getLogicalOperator());
			employeeFilterListForm.setOpenBracket(claimTemplateItemShortlist.getOpenBracket());
			employeeFilterListForm.setValue(claimTemplateItemShortlist.getValue());
			employeeFilterListForm.setDataType(
					generalFilterLogic.getFieldDataType(companyId, claimTemplateItemShortlist.getDataDictionary()));
			employeeFilterList.add(employeeFilterListForm);
		}

		return employeeFilterList;
	}

	@Override
	public List<AppCodeDTO> getClaimTypeItemList(Locale locale) {
		List<AppCodeMaster> appCodeList = appCodeMasterDAO.findByCondition(PayAsiaConstants.APP_CODE_ClAIM_TYPE);
		List<AppCodeDTO> appCodeDTOs = new ArrayList<>();
		for (AppCodeMaster appCodeMaster : appCodeList) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
			if (StringUtils.isNotBlank(appCodeMaster.getLabelKey())) {
				appCodeDTO.setCodeDesc(messageSource.getMessage(appCodeMaster.getLabelKey(), new Object[] {}, locale));
			} else {
				appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			}
			appCodeDTOs.add(appCodeDTO);
		}

		return appCodeDTOs;
	}

	@Override
	public List<AppCodeDTO> getCustomFieldTypes() {
		List<AppCodeMaster> appCodeList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE);
		List<AppCodeDTO> appCodeDTOs = new ArrayList<>();
		for (AppCodeMaster appCodeMaster : appCodeList) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			appCodeDTOs.add(appCodeDTO);
		}

		return appCodeDTOs;
	}

	@Override
	public String saveClaimTemplateItemConf(Long companyId, ClaimTemplateItemForm claimTemplateItemForm) {
		
		ClaimTemplateItem claimTemplateItemVO = claimTemplateItemDAO
				.findByClaimTemplateItemId(FormatPreserveCryptoUtil.decrypt(claimTemplateItemForm.getClaimTemplateItemId()),companyId);
		
		AppCodeMaster appCodeMasterVO = appCodeMasterDAO.findById(claimTemplateItemForm.getClaimType());
		String claimType = appCodeMasterVO.getCodeDesc();

		saveClaimTemplateItemClaimType(claimTemplateItemForm, claimType, claimTemplateItemVO, appCodeMasterVO);

		saveClaimTemplateItemGeneral(claimTemplateItemForm, claimType, claimTemplateItemVO, appCodeMasterVO);

		saveClaimTemplateItemCustomFields(claimTemplateItemForm, claimTemplateItemVO);

		return "true";
	}

	@Override
	public String updateClaimTemplateItemConf(Long companyId, ClaimTemplateItemForm claimTemplateItemForm) {

		ClaimTemplateItem claimTemplateItemVO = claimTemplateItemDAO
				.findByClaimTemplateItemId(FormatPreserveCryptoUtil.decrypt(claimTemplateItemForm.getClaimTemplateItemId()),companyId);
		AppCodeMaster appCodeMasterVO = appCodeMasterDAO.findById(claimTemplateItemForm.getClaimType());
		String claimType = appCodeMasterVO.getCodeDesc();

		updateClaimTemplateItemClaimType(claimTemplateItemForm, claimType, claimTemplateItemVO, appCodeMasterVO);

		updateClaimTemplateItemGeneral(claimTemplateItemForm, claimType, claimTemplateItemVO, appCodeMasterVO);

		updateClaimTemplateItemCustomFields(claimTemplateItemForm, claimTemplateItemVO);

		return "true";
	}

	private void saveClaimTemplateItemCustomFields(ClaimTemplateItemForm claimTemplateItemForm,
			ClaimTemplateItem claimTemplateItemVO) {

		List<CustomFieldsDTO> customFieldsDTOLists = claimTemplateItemForm.getCustomFieldsDTOList();
		for (CustomFieldsDTO customFieldsDTO : customFieldsDTOLists) {
			if (customFieldsDTO.getFieldName() == null) {
				continue;
			}
			ClaimTemplateItemCustomField claimTemplateItemCustomField = new ClaimTemplateItemCustomField();
			claimTemplateItemCustomField.setClaimTemplateItem(claimTemplateItemVO);
			try {
				claimTemplateItemCustomField.setFieldName(URLDecoder.decode(customFieldsDTO.getFieldName(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			claimTemplateItemCustomField.setMandatory(customFieldsDTO.isMandatory());
			AppCodeMaster customFieldType = appCodeMasterDAO.findById(customFieldsDTO.getCustomFieldType());
			claimTemplateItemCustomField.setFieldType(customFieldType);
			ClaimTemplateItemCustomField saveReturnCustomField = claimTemplateItemCustomFieldDAO
					.saveReturn(claimTemplateItemCustomField);
			if (customFieldType.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE_DROPDOWN)) {
				saveCustomFieldDropDownValues(customFieldsDTO, saveReturnCustomField);
			}
		}

	}

	private void updateClaimTemplateItemCustomFields(ClaimTemplateItemForm claimTemplateItemForm,
			ClaimTemplateItem claimTemplateItemVO) {

		List<CustomFieldsDTO> customFieldsDTOLists = claimTemplateItemForm.getCustomFieldsDTOList();

		List<Long> customFieldIdList = new ArrayList<>();

		List<ClaimTemplateItemCustomField> claimTemplateItemCustomFields = claimTemplateItemCustomFieldDAO
				.findByClaimTemplateItemId(FormatPreserveCryptoUtil.decrypt(claimTemplateItemForm.getClaimTemplateItemId()));

		for (CustomFieldsDTO customFieldsDTO : customFieldsDTOLists) {

			if (customFieldsDTO.getFieldName() == null) {
				continue;
			}

			customFieldIdList.add(customFieldsDTO.getCustomFieldId());
			ClaimTemplateItemCustomField claimTemplateItemCustomField;
			Long customFieldId;
			if (customFieldsDTO.getCustomFieldId() != 0) {
				customFieldId = customFieldsDTO.getCustomFieldId();
				claimTemplateItemCustomField = claimTemplateItemCustomFieldDAO.findByID(customFieldId);
			} else {
				claimTemplateItemCustomField = new ClaimTemplateItemCustomField();

			}

			claimTemplateItemCustomField.setClaimTemplateItem(claimTemplateItemVO);
			try {
				claimTemplateItemCustomField.setFieldName(URLDecoder.decode(customFieldsDTO.getFieldName(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			claimTemplateItemCustomField.setMandatory(customFieldsDTO.isMandatory());
			AppCodeMaster customFieldType = appCodeMasterDAO.findById(customFieldsDTO.getCustomFieldType());
			claimTemplateItemCustomField.setFieldType(customFieldType);

			if (customFieldsDTO.getCustomFieldId() != 0) {
				claimTemplateItemCustomFieldDAO.update(claimTemplateItemCustomField);
				if (customFieldType.getCodeDesc()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE_DROPDOWN)) {
					claimTemplateItemCustomFieldDropDownDAO
							.deleteByCondition(claimTemplateItemCustomField.getCustomFieldId());
					saveCustomFieldDropDownValues(customFieldsDTO, claimTemplateItemCustomField);
				}
			} else {
				ClaimTemplateItemCustomField saveReturnCustomField = claimTemplateItemCustomFieldDAO
						.saveReturn(claimTemplateItemCustomField);
				if (customFieldType.getCodeDesc()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE_DROPDOWN)) {
					saveCustomFieldDropDownValues(customFieldsDTO, saveReturnCustomField);
				}
			}

		}

		for (ClaimTemplateItemCustomField claimTemplateItemCustomField : claimTemplateItemCustomFields) {
			if (!customFieldIdList.contains(claimTemplateItemCustomField.getCustomFieldId())) {
				claimTemplateItemCustomFieldDAO.delete(claimTemplateItemCustomField);

			}
		}

	}

	private void saveCustomFieldDropDownValues(CustomFieldsDTO customFieldsDTO,
			ClaimTemplateItemCustomField claimTemplateItemCustomField) {
		if (StringUtils.isNotBlank(customFieldsDTO.getFieldDropdownValues())) {
			String[] fieldDropdownValueArray = customFieldsDTO.getFieldDropdownValues().split(";");
			for (String fieldDropdownValue : fieldDropdownValueArray) {
				if (StringUtils.isNotBlank(fieldDropdownValue)) {
					ClaimTemplateItemCustomFieldDropDown customFieldDropDown = new ClaimTemplateItemCustomFieldDropDown();
					customFieldDropDown.setClaimTemplateItemCustomField(claimTemplateItemCustomField);
					customFieldDropDown.setFieldValue(fieldDropdownValue);
					claimTemplateItemCustomFieldDropDownDAO.save(customFieldDropDown);
				}
			}
		}
	}

	@Override
	public List<ClaimTemplateForm> getAppcodeListForProration(Locale locale) {
		List<ClaimTemplateForm> list = new ArrayList<ClaimTemplateForm>();
		List<AppCodeMaster> appCodeLeaveProrationMethodList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.CLAIM_TEMPLATE_PRORATION_METHOD);
		for (AppCodeMaster appCodeMaster : appCodeLeaveProrationMethodList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.CLAIM_PRORATION_METHOD_MONTH)
					|| appCodeMaster.getCodeDesc()
							.equalsIgnoreCase(PayAsiaConstants.CLAIM_PRORATION_METHOD_CALENDAR_DAYS)) {
				ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
				claimTemplateForm.setOptionId(appCodeMaster.getAppCodeID());
				claimTemplateForm.setOptionValue(appCodeMaster.getCodeDesc());
				list.add(claimTemplateForm);
			}

		}
		return list;
	}

	private void saveClaimTemplateItemGeneral(ClaimTemplateItemForm claimTemplateItemForm, String claimType,
			ClaimTemplateItem claimTemplateItemVO, AppCodeMaster appCodeMasterVO) {
		ClaimTemplateItemGeneral claimTemplateItemGeneralVO = new ClaimTemplateItemGeneral();

		claimTemplateItemGeneralVO.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemGeneralVO.setReceiptNoMandatory(claimTemplateItemForm.getReceiptNoMandatory());
		claimTemplateItemGeneralVO.setClaimDateMandatory(claimTemplateItemForm.getClaimDateMandatory());
		claimTemplateItemGeneralVO.setRemarksMandatory(claimTemplateItemForm.getRemarksMandatory());
		claimTemplateItemGeneralVO.setAttachmentMandatory(claimTemplateItemForm.getAttachmentMandatory());
		claimTemplateItemGeneralVO.setAllowOverrideTaxAmt(claimTemplateItemForm.getAllowOverrideTaxAmt());
		claimTemplateItemGeneralVO.setOpenToDependents(claimTemplateItemForm.getOpenToDependents());
		claimTemplateItemGeneralVO.setTaxPercentage(claimTemplateItemForm.getTaxPercentage());

		claimTemplateItemGeneralVO.setAmountBeforeTaxVisible(claimTemplateItemForm.getAmountBeforeTaxVisible());
		claimTemplateItemGeneralVO.setClaimsAllowedPerMonth(claimTemplateItemForm.getClaimsAllowedPerMonth());
		claimTemplateItemGeneralVO.setBackdatedClaimDaysLimit(claimTemplateItemForm.getBackdatedClaimDaysLimit());
		claimTemplateItemGeneralVO.setEntitlementPerDay(claimTemplateItemForm.getEntitlementPerDay());
		claimTemplateItemGeneralVO.setEntitlementPerMonth(claimTemplateItemForm.getEntitlementPerMonth());
		claimTemplateItemGeneralVO.setEntitlementPerYear(claimTemplateItemForm.getEntitlementPerYear());
		claimTemplateItemGeneralVO.setAllowExceedEntitlement(claimTemplateItemForm.getAllowExceedEntitlement());
		try {
			claimTemplateItemGeneralVO.setRemarks(URLDecoder.decode(claimTemplateItemForm.getRemarks()==null?"":claimTemplateItemForm.getRemarks(), "UTF-8"));
			claimTemplateItemGeneralVO.setHelpText(URLDecoder.decode(claimTemplateItemForm.getHelpText()==null?"":claimTemplateItemForm.getHelpText(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		claimTemplateItemGeneralDAO.save(claimTemplateItemGeneralVO);

	}

	private void updateClaimTemplateItemGeneral(ClaimTemplateItemForm claimTemplateItemForm, String claimType,
			ClaimTemplateItem claimTemplateItemVO, AppCodeMaster appCodeMasterVO) {
		ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGeneralDAO.findByID( FormatPreserveCryptoUtil.decrypt(claimTemplateItemForm.getGeneralID()));
		if (claimTemplateItemGeneralVO == null) {
			return;
		}

		claimTemplateItemGeneralVO.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemGeneralVO.setReceiptNoMandatory(claimTemplateItemForm.getReceiptNoMandatory());
		claimTemplateItemGeneralVO.setClaimDateMandatory(claimTemplateItemForm.getClaimDateMandatory());
		claimTemplateItemGeneralVO.setRemarksMandatory(claimTemplateItemForm.getRemarksMandatory());
		claimTemplateItemGeneralVO.setAttachmentMandatory(claimTemplateItemForm.getAttachmentMandatory());
		claimTemplateItemGeneralVO.setAllowOverrideTaxAmt(claimTemplateItemForm.getAllowOverrideTaxAmt());
		claimTemplateItemGeneralVO.setOpenToDependents(claimTemplateItemForm.getOpenToDependents());
		claimTemplateItemGeneralVO.setTaxPercentage(claimTemplateItemForm.getTaxPercentage());
		claimTemplateItemGeneralVO.setOverrideReceiptAmountForQuantityBased(
				claimTemplateItemForm.isOverrideReceiptAmountForQuantityBased());
		claimTemplateItemGeneralVO.setAmountBeforeTaxVisible(claimTemplateItemForm.getAmountBeforeTaxVisible());
		claimTemplateItemGeneralVO.setClaimsAllowedPerMonth(claimTemplateItemForm.getClaimsAllowedPerMonth());
		claimTemplateItemGeneralVO.setBackdatedClaimDaysLimit(claimTemplateItemForm.getBackdatedClaimDaysLimit());
		claimTemplateItemGeneralVO.setEntitlementPerDay(claimTemplateItemForm.getEntitlementPerDay());
		claimTemplateItemGeneralVO.setEntitlementPerMonth(claimTemplateItemForm.getEntitlementPerMonth());
		claimTemplateItemGeneralVO.setEntitlementPerYear(claimTemplateItemForm.getEntitlementPerYear());
		claimTemplateItemGeneralVO.setAllowExceedEntitlement(claimTemplateItemForm.getAllowExceedEntitlement());
		try {
			claimTemplateItemGeneralVO.setRemarks(URLDecoder.decode(claimTemplateItemForm.getRemarks()==null?"":claimTemplateItemForm.getRemarks(), "UTF-8"));
			claimTemplateItemGeneralVO.setHelpText(URLDecoder.decode(claimTemplateItemForm.getHelpText()==null?"":claimTemplateItemForm.getHelpText(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}

		claimTemplateItemGeneralDAO.update(claimTemplateItemGeneralVO);

	}

	private void saveClaimTemplateItemClaimType(ClaimTemplateItemForm claimTemplateItemForm, String claimType,
			ClaimTemplateItem claimTemplateItemVO, AppCodeMaster appCodeMasterVO) {
		ClaimTemplateItemClaimType claimTemplateItemClaimTypeVO = new ClaimTemplateItemClaimType();
		claimTemplateItemClaimTypeVO.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemClaimTypeVO.setClaimType(appCodeMasterVO);
		switch (claimType) {
		case PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED:
			claimTemplateItemClaimTypeVO
					.setMinLimit(claimTemplateItemForm.getMinLimit() == null ? 0 : claimTemplateItemForm.getMinLimit());
			claimTemplateItemClaimTypeVO
					.setMaxLimit(claimTemplateItemForm.getMaxLimit() == null ? 0 : claimTemplateItemForm.getMaxLimit());
			claimTemplateItemClaimTypeVO.setDefaultUnit(
					claimTemplateItemForm.getDefaultUnit() == null ? 0 : (claimTemplateItemForm.getDefaultUnit()));
			claimTemplateItemClaimTypeVO.setShowDefaultUnit(claimTemplateItemForm.getShowDefaultUnit());
			claimTemplateItemClaimTypeVO.setDefaultUnitPrice(claimTemplateItemForm.getDefaultUnitPrice() == null
					? new BigDecimal(0) : claimTemplateItemForm.getDefaultUnitPrice());
			claimTemplateItemClaimTypeVO.setAllowChangeDefaultPrice(claimTemplateItemForm.getAllowChangeDefaultPrice());

			break;
		case PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED:

			claimTemplateItemClaimTypeVO
					.setReceiptAmtPercentApplicable(claimTemplateItemForm.getReceiptAmountPercentApplicable() == null
							? 0 : claimTemplateItemForm.getReceiptAmountPercentApplicable());

			break;
		case PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED:

			claimTemplateItemClaimTypeVO.setAllowChangeForexRate(claimTemplateItemForm.getAllowChangeForexRate());
			claimTemplateItemClaimTypeVO
					.setAllowOverrideConvertedAmt(claimTemplateItemForm.getAllowOverrideConvertedAmt());
			break;
		}
		claimTemplateItemClaimTypeVO.setAllowOverrideConvertedAmt(false);
		claimTemplateItemClaimTypeDAO.save(claimTemplateItemClaimTypeVO);

	}

	private void updateClaimTemplateItemClaimType(ClaimTemplateItemForm claimTemplateItemForm, String claimType,
			ClaimTemplateItem claimTemplateItemVO, AppCodeMaster appCodeMasterVO) {
		
		ClaimTemplateItemClaimType claimTemplateItemClaimTypeVO = claimTemplateItemClaimTypeDAO
				.findByID(FormatPreserveCryptoUtil.decrypt(claimTemplateItemForm.getClaimTemplateItemClaimTypeId()));
		if (claimTemplateItemClaimTypeVO == null) {
			return;
		}
		claimTemplateItemClaimTypeVO.setClaimTemplateItem(claimTemplateItemVO);
		claimTemplateItemClaimTypeVO.setClaimType(appCodeMasterVO);
		switch (claimType) {
		case PayAsiaConstants.APP_CODE_CLAIM_TYPE_QUANTITY_BASED:
			claimTemplateItemClaimTypeVO.setMinLimit(claimTemplateItemForm.getMinLimit());
			claimTemplateItemClaimTypeVO.setMaxLimit(claimTemplateItemForm.getMaxLimit());
			claimTemplateItemClaimTypeVO.setDefaultUnit(claimTemplateItemForm.getDefaultUnit());
			claimTemplateItemClaimTypeVO.setShowDefaultUnit(claimTemplateItemForm.getShowDefaultUnit());
			claimTemplateItemClaimTypeVO.setDefaultUnitPrice(claimTemplateItemForm.getDefaultUnitPrice());
			claimTemplateItemClaimTypeVO.setAllowChangeDefaultPrice(claimTemplateItemForm.getAllowChangeDefaultPrice());

			break;
		case PayAsiaConstants.APP_CODE_CLAIM_TYPE_AMOUNT_BASED:

			claimTemplateItemClaimTypeVO
					.setReceiptAmtPercentApplicable(claimTemplateItemForm.getReceiptAmountPercentApplicable());

			break;
		case PayAsiaConstants.APP_CODE_CLAIM_TYPE_FOREX_BASED:

			claimTemplateItemClaimTypeVO.setAllowChangeForexRate(claimTemplateItemForm.getAllowChangeForexRate());
			claimTemplateItemClaimTypeVO
					.setAllowOverrideConvertedAmt(claimTemplateItemForm.getAllowOverrideConvertedAmt());
			break;
		}

		claimTemplateItemClaimTypeDAO.update(claimTemplateItemClaimTypeVO);

	}

	@Override
	public ClaimTemplateForm getClaimTemplateIdConfData(Long companyId, Long claimTemplateItemId) {
		ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
		ClaimTemplateItemForm claimTemplateItemForm = new ClaimTemplateItemForm();
		List<AppCodeDTO> customFieldList = getCustomFieldTypes();
		claimTemplateForm.setCustomFieldList(customFieldList);
		ClaimTemplateItem claimTemplateItemVO = claimTemplateItemDAO.findByClaimTemplateItemId(claimTemplateItemId,companyId);
		List<ClaimTemplateItemClaimType> claimTemplateItemClaimTypes = new ArrayList<>(
				claimTemplateItemVO.getClaimTemplateItemClaimTypes());
		List<ClaimTemplateItemGeneral> claimTemplateItemGenerals = new ArrayList<>(
				claimTemplateItemVO.getClaimTemplateItemGenerals());
		List<ClaimTemplateItemCustomField> claimTemplateItemCustomFields = new ArrayList<>(
				claimTemplateItemVO.getClaimTemplateItemCustomFields());
		if (!claimTemplateItemClaimTypes.isEmpty()) {
			claimTemplateItemForm.setConfAvailable(true);

			ClaimTemplateItemClaimType claimTemplateItemClaimTypeVO = claimTemplateItemClaimTypes.get(0);

			setClaimTemplateItemClaimType(claimTemplateItemClaimTypeVO, claimTemplateItemForm);

			if (!claimTemplateItemGenerals.isEmpty()) {
				ClaimTemplateItemGeneral claimTemplateItemGeneralVO = claimTemplateItemGenerals.get(0);
				setClaimTemplateItemClaimGeneral(claimTemplateItemGeneralVO, claimTemplateItemForm);
			}

			if (!claimTemplateItemCustomFields.isEmpty()) {
				Collections.sort(claimTemplateItemCustomFields, new ItemCustomFieldComp());
				setClaimTemplateItemClaimCustomfields(claimTemplateItemCustomFields, claimTemplateItemForm);
			}
		} else {
			claimTemplateItemForm.setConfAvailable(false);
		}
		claimTemplateForm.setClaimTemplateItemForm(claimTemplateItemForm);

		return claimTemplateForm;
	}

	private class ClaimTemplateItemCustomFieldDropDownComp implements Comparator<ClaimTemplateItemCustomFieldDropDown> {
		public int compare(ClaimTemplateItemCustomFieldDropDown templateField,
				ClaimTemplateItemCustomFieldDropDown compWithTemplateField) {
			if (templateField.getCustomFieldDropDownId() > compWithTemplateField.getCustomFieldDropDownId()) {
				return 1;
			} else if (templateField.getCustomFieldDropDownId() < compWithTemplateField.getCustomFieldDropDownId()) {
				return -1;
			}
			return 0;

		}

	}

	private void setClaimTemplateItemClaimCustomfields(List<ClaimTemplateItemCustomField> claimTemplateItemCustomFields,
			ClaimTemplateItemForm claimTemplateItemForm) {

		List<CustomFieldsDTO> customFieldsDTOLists = new ArrayList<>();
		List<AppCodeDTO> customFieldList = getCustomFieldTypes();
		for (ClaimTemplateItemCustomField claimTemplateItemCustomField : claimTemplateItemCustomFields) {
			CustomFieldsDTO customField = new CustomFieldsDTO();
			customField.setCustomFieldId(claimTemplateItemCustomField.getCustomFieldId());
			try {
				customField.setFieldName(URLEncoder.encode(claimTemplateItemCustomField.getFieldName(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			customField.setMandatory(claimTemplateItemCustomField.isMandatory());
			customField.setCustomFieldType(claimTemplateItemCustomField.getFieldType().getAppCodeID());
			customField.setCustomFieldTypeName(claimTemplateItemCustomField.getFieldType().getCodeDesc());
			customField.setCustomFieldList(customFieldList);

			if (claimTemplateItemCustomField.getFieldType().getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_CLAIM_CUSTOM_FIELD_TYPE_DROPDOWN)) {
				List<ClaimTemplateItemCustomFieldDropDown> customFieldDDLists = new ArrayList<>(
						claimTemplateItemCustomField.getClaimTemplateItemCustomFieldDropDowns());
				Collections.sort(customFieldDDLists, new ClaimTemplateItemCustomFieldDropDownComp());
				String customFieldDropDownValues = "";
				for (ClaimTemplateItemCustomFieldDropDown customFieldDropDown : customFieldDDLists) {
					if (StringUtils.isNotBlank(customFieldDropDown.getFieldValue())) {
						customFieldDropDownValues += customFieldDropDown.getFieldValue() + ';';
					}
				}
				if (customFieldDropDownValues.endsWith(";")) {
					customFieldDropDownValues = StringUtils.removeEnd(customFieldDropDownValues, ";");
				}
				customField.setFieldDropdownValues(customFieldDropDownValues);
			}
			customFieldsDTOLists.add(customField);
		}
		claimTemplateItemForm.setCustomFieldsDTOList(customFieldsDTOLists);

	}

	private void setClaimTemplateItemClaimGeneral(ClaimTemplateItemGeneral claimTemplateItemGeneralVO,
			ClaimTemplateItemForm claimTemplateItemForm) {

		claimTemplateItemForm.setGeneralID(FormatPreserveCryptoUtil.encrypt(claimTemplateItemGeneralVO.getGeneralId()));
		claimTemplateItemForm.setReceiptNoMandatory(claimTemplateItemGeneralVO.getReceiptNoMandatory());
		claimTemplateItemForm.setClaimDateMandatory(claimTemplateItemGeneralVO.getClaimDateMandatory());
		claimTemplateItemForm.setRemarksMandatory(claimTemplateItemGeneralVO.getRemarksMandatory());
		claimTemplateItemForm.setAttachmentMandatory(claimTemplateItemGeneralVO.getAttachmentMandatory());
		claimTemplateItemForm.setAllowOverrideTaxAmt(claimTemplateItemGeneralVO.getAllowOverrideTaxAmt());
		claimTemplateItemForm.setOpenToDependents(claimTemplateItemGeneralVO.getOpenToDependents());
		claimTemplateItemForm.setAmountBeforeTaxVisible(claimTemplateItemGeneralVO.getAmountBeforeTaxVisible());
		claimTemplateItemForm.setOverrideReceiptAmountForQuantityBased(
				claimTemplateItemGeneralVO.isOverrideReceiptAmountForQuantityBased());
		claimTemplateItemForm.setTaxPercentage(claimTemplateItemGeneralVO.getTaxPercentage());
		claimTemplateItemForm.setClaimsAllowedPerMonth(claimTemplateItemGeneralVO.getClaimsAllowedPerMonth());
		claimTemplateItemForm.setBackdatedClaimDaysLimit(claimTemplateItemGeneralVO.getBackdatedClaimDaysLimit());
		claimTemplateItemForm.setEntitlementPerDay(claimTemplateItemGeneralVO.getEntitlementPerDay());
		claimTemplateItemForm.setEntitlementPerMonth(claimTemplateItemGeneralVO.getEntitlementPerMonth());
		claimTemplateItemForm.setEntitlementPerYear(claimTemplateItemGeneralVO.getEntitlementPerYear());
		claimTemplateItemForm.setAllowExceedEntitlement(claimTemplateItemGeneralVO.getAllowExceedEntitlement());
		claimTemplateItemForm.setRemarks(claimTemplateItemGeneralVO.getRemarks());
		claimTemplateItemForm.setHelpText(claimTemplateItemGeneralVO.getHelpText());
		

	}

	private void setClaimTemplateItemClaimType(ClaimTemplateItemClaimType claimTemplateItemClaimTypeVO,
			ClaimTemplateItemForm claimTemplateItemForm) {
		claimTemplateItemForm.setClaimTemplateItemClaimTypeId(FormatPreserveCryptoUtil.encrypt(claimTemplateItemClaimTypeVO.getClaimTypeId()));
		claimTemplateItemForm.setClaimType(claimTemplateItemClaimTypeVO.getClaimType().getAppCodeID());
		claimTemplateItemForm
				.setClaimTemplateItemId(FormatPreserveCryptoUtil.encrypt(claimTemplateItemClaimTypeVO.getClaimTemplateItem().getClaimTemplateItemId()));
		claimTemplateItemForm.setMinLimit(claimTemplateItemClaimTypeVO.getMinLimit());
		claimTemplateItemForm.setMaxLimit(claimTemplateItemClaimTypeVO.getMaxLimit());
		claimTemplateItemForm.setDefaultUnit(claimTemplateItemClaimTypeVO.getDefaultUnit());
		claimTemplateItemForm.setShowDefaultUnit(claimTemplateItemClaimTypeVO.getShowDefaultUnit());
		claimTemplateItemForm.setDefaultUnitPrice(claimTemplateItemClaimTypeVO.getDefaultUnitPrice());
		claimTemplateItemForm.setAllowChangeDefaultPrice(claimTemplateItemClaimTypeVO.getAllowChangeDefaultPrice());
		claimTemplateItemForm
				.setReceiptAmountPercentApplicable(claimTemplateItemClaimTypeVO.getReceiptAmtPercentApplicable());
		claimTemplateItemForm.setAllowChangeForexRate(claimTemplateItemClaimTypeVO.getAllowChangeForexRate());
		claimTemplateItemForm.setAllowOverrideConvertedAmt(claimTemplateItemClaimTypeVO.getAllowOverrideConvertedAmt());

	}

	private class ItemCustomFieldComp implements Comparator<ClaimTemplateItemCustomField> {

		public int compare(ClaimTemplateItemCustomField customField, ClaimTemplateItemCustomField compcustomField) {
			if (customField.getCustomFieldId() > compcustomField.getCustomFieldId()) {
				return 1;
			} else if (customField.getCustomFieldId() < compcustomField.getCustomFieldId()) {
				return -1;
			}
			return 0;

		}

	}

	@Override
	public ClaimTemplateForm getClaimItemCategories(Long companyId) {
		ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
		List<ClaimItemMaster> claimItemMasters = claimItemMasterDAO.findByCompany(companyId);
		List<ClaimItemForm> claimItemCategories = new ArrayList<>();
		List<Long> claimCategoryIds = new ArrayList<>();

		for (ClaimItemMaster claimItemMaster : claimItemMasters) {

			Long claimCategoryId=FormatPreserveCryptoUtil.encrypt(claimItemMaster.getClaimCategoryMaster().getClaimCategoryId());
			if (claimCategoryIds.contains(claimCategoryId)) {
				continue;
			}
			claimCategoryIds.add(claimCategoryId);

			ClaimItemForm claimItemForm = new ClaimItemForm();
			claimItemForm.setCategoryId(claimCategoryId);
			claimItemForm.setClaimCategory(claimItemMaster.getClaimCategoryMaster().getClaimCategoryName());
			claimItemCategories.add(claimItemForm);
		}
		claimTemplateForm.setClaimItemCategories(claimItemCategories);
		return claimTemplateForm;
	}

	@Override
	public void deleteFilter(Long filterId, Long companyId) {
		
		ClaimTemplateItemShortlist claimTemplateItemShortlistVO = claimTemplateItemShortlistDAO.findByClaimTemplateItemShortlistID(filterId,companyId);
		ClaimTemplateItem claimTemplateItem = claimTemplateItemShortlistVO.getClaimTemplateItem();

		EntityMaster entityMaster = entityMasterDAO.findByEntityName(PayAsiaConstants.EMPLOYEE_ENTITY_NAME);

		List<Long> formIds = dynamicFormDAO.getDistinctFormId(companyId, entityMaster.getEntityId());

		Calendar cal = Calendar.getInstance();
		List<EmployeeClaimTemplateItem> cmpEmployeeClaimTemplateItems = employeeClaimTemplateItemDAO
				.findByCompanyCondition(companyId, cal.getTime(),
						claimTemplateItem.getClaimTemplate().getClaimTemplateId());

		HashMap<String, EmployeeClaimTemplateItem> employeeClaimTemplateItemMap = new HashMap<>();
		for (EmployeeClaimTemplateItem employeeClaimTemplateItem : cmpEmployeeClaimTemplateItems) {

			String mapkey = String.valueOf(employeeClaimTemplateItem.getClaimTemplateItem().getClaimTemplateItemId())
					+ "_" + String.valueOf(
							employeeClaimTemplateItem.getEmployeeClaimTemplate().getEmployee().getEmployeeId());
			employeeClaimTemplateItemMap.put(mapkey, employeeClaimTemplateItem);
		}

		List<Long> claimTemplateEmpIds = employeeClaimTemplateDAO
				.getEmployeesOfClaimTemplate(claimTemplateItem.getClaimTemplate().getClaimTemplateId());

		claimTemplateItemShortlistDAO.delete(claimTemplateItemShortlistVO);

		List<ClaimTemplateItemShortlist> claimTemplateItemShortlists = claimTemplateItemShortlistDAO
				.findByCondition(claimTemplateItem.getClaimTemplateItemId(),companyId);

		setEmployeeClaimTemplateItem(claimTemplateItemShortlists, companyId, claimTemplateEmpIds, claimTemplateItem,
				null, formIds, employeeClaimTemplateItemMap);

	}

	@Override
	public List<ClaimTemplateForm> getAllowedNoOfTimesAppCodeList() {

		List<ClaimTemplateForm> claimTemplateFormList = new ArrayList<>();
		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_CLAIM_TEMPLATE_ALLOWED_TIMES_FIELD);
		for (AppCodeMaster appCodeMaster : appCodeMasterList) {
			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
			claimTemplateForm.setAllowNoOfTimesAppCodeId(appCodeMaster.getAppCodeID());
			claimTemplateForm.setAllowNoOfTimesAppCodeVal(appCodeMaster.getCodeValue());
			claimTemplateFormList.add(claimTemplateForm);
		}
		return claimTemplateFormList;
	}

	@Override
	public List<ClaimTemplateForm> getProrationBasedOnAppCodeList() {

		List<ClaimTemplateForm> claimTemplateFormList = new ArrayList<>();
		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_CLAIM_TEMPLATE_CLAIM_PRORATION_BASED_ON);
		for (AppCodeMaster appCodeMaster : appCodeMasterList) {
			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
			claimTemplateForm.setProrationBasedOnAppCodeId(appCodeMaster.getAppCodeID());
			claimTemplateForm.setProrationBasedOnAppCodeVal(appCodeMaster.getCodeValue());
			claimTemplateFormList.add(claimTemplateForm);
		}
		return claimTemplateFormList;
	}

	@Override
	public ClaimTemplateForm getClaimTemplateAppCodeList(Locale locale) {

		ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
		List<AppCodeMaster> appCodeFrontEndList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.CLAIM_TEMPLATE_FRONT_END_VIEW_MODE);
		List<AppCodeMaster> appCodeBackEndList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.CLAIM_TEMPLATE_BACK_END_VIEW_MODE);
		ClaimTemplateConditionDTO claimTypeDTO = new ClaimTemplateConditionDTO();

		for (AppCodeMaster appCodeMaster : appCodeFrontEndList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON)) {

				claimTypeDTO.setFrontEndViewModeOnId(appCodeMaster.getAppCodeID());
				if (StringUtils.isNotBlank(appCodeMaster.getLabelKey())) {
					claimTypeDTO.setFrontEndViewModeOn(
							messageSource.getMessage(appCodeMaster.getLabelKey(), new Object[] {}, locale));
				} else {
					claimTypeDTO.setFrontEndViewModeOn(appCodeMaster.getCodeDesc());
				}

			} else if (appCodeMaster.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON_WHEN_TRAN_EXIST)) {
				claimTypeDTO.setFrontEndViewModeOnWhenTranId(appCodeMaster.getAppCodeID());
				if (StringUtils.isNotBlank(appCodeMaster.getLabelKey())) {
					claimTypeDTO.setFrontEndViewModeOnWhenTran(
							messageSource.getMessage(appCodeMaster.getLabelKey(), new Object[] {}, locale));
				} else {
					claimTypeDTO.setFrontEndViewModeOnWhenTran(appCodeMaster.getCodeDesc());
				}

			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF)) {
				claimTypeDTO.setFrontEndViewModeOffId(appCodeMaster.getAppCodeID());
				if (StringUtils.isNotBlank(appCodeMaster.getLabelKey())) {
					claimTypeDTO.setFrontEndViewModeOff(
							messageSource.getMessage(appCodeMaster.getLabelKey(), new Object[] {}, locale));
				} else {
					claimTypeDTO.setFrontEndViewModeOff(appCodeMaster.getCodeDesc());
				}

			}
		}
		for (AppCodeMaster appCodeMaster : appCodeBackEndList) {

			if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON)) {

				claimTypeDTO.setBackEndViewModeOnId(appCodeMaster.getAppCodeID());
				if (StringUtils.isNotBlank(appCodeMaster.getLabelKey())) {
					claimTypeDTO.setBackEndViewModeOn(
							messageSource.getMessage(appCodeMaster.getLabelKey(), new Object[] {}, locale));
				} else {
					claimTypeDTO.setBackEndViewModeOn(appCodeMaster.getCodeDesc());
				}

			} else if (appCodeMaster.getCodeDesc()
					.equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_ON_WHEN_TRAN_EXIST)) {
				claimTypeDTO.setBackEndViewModeOnWhenTranId(appCodeMaster.getAppCodeID());
				if (StringUtils.isNotBlank(appCodeMaster.getLabelKey())) {
					claimTypeDTO.setBackEndViewModeOnWhenTran(
							messageSource.getMessage(appCodeMaster.getLabelKey(), new Object[] {}, locale));
				} else {
					claimTypeDTO.setBackEndViewModeOnWhenTran(appCodeMaster.getCodeDesc());
				}

			} else if (appCodeMaster.getCodeDesc().equalsIgnoreCase(PayAsiaConstants.CLAIM_TEMPLATE_VIEW_MODE_OFF)) {
				claimTypeDTO.setBackEndViewModeOffId(appCodeMaster.getAppCodeID());
				if (StringUtils.isNotBlank(appCodeMaster.getLabelKey())) {
					claimTypeDTO.setBackEndViewModeOff(
							messageSource.getMessage(appCodeMaster.getLabelKey(), new Object[] {}, locale));
				} else {
					claimTypeDTO.setBackEndViewModeOff(appCodeMaster.getCodeDesc());
				}

			}
		}
		claimTemplateForm.setClaimTemplateDTO(claimTypeDTO);
		return claimTemplateForm;
	}

	@Override
	public Boolean adjustClaimResignedEmp(Long companyId, String employeeIds, Long userId) {
		String[] empIds = employeeIds.split(",");
		for (String employeeId : empIds) {
			claimTemplateDAO.adjustClaimResignedEmp(Long.parseLong(employeeId), userId);
		}

		return true;
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();
		if (employee.getMiddleName() != null) {
			employeeName = employeeName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			employeeName = employeeName + " " + employee.getLastName();
		}

		return employeeName;
	}

	@Override
	public LeaveGranterFormResponse getResignedEmployees(PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			String fromDate, String toDate) {
		LeaveGranterFormResponse response = new LeaveGranterFormResponse();

		Set<LeaveGranterForm> leaveGranterFormSet = new LinkedHashSet<>();
		List<Employee> granterEmployeesList = employeeDAO.findResignedEmpForClaim(companyId, fromDate, toDate, pageDTO,
				sortDTO);
		for (Employee employeeVO : granterEmployeesList) {
			LeaveGranterForm leaveGranterForm = new LeaveGranterForm();
			leaveGranterForm.setEmployeeId(employeeVO.getEmployeeId());
			leaveGranterForm.setEmployeeName(getEmployeeName(employeeVO));
			if (employeeVO.getResignationDate() != null) {
				leaveGranterForm.setResignationDate(DateUtils.timeStampToString(employeeVO.getResignationDate()));
			}
			leaveGranterForm.setEmployeeNumber(employeeVO.getEmployeeNumber());
			leaveGranterFormSet.add(leaveGranterForm);
		}

		int recordSize = employeeDAO.getCountAnnualRollbackEmps(companyId, fromDate, toDate);
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			int totalPages = recordSize / pageSize;

			if (recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSize == 0) {
				pageDTO.setPageNumber(0);
			}

			response.setPage(pageDTO.getPageNumber());
			response.setTotal(totalPages);

		}
		response.setRecords(recordSize);
		response.setLeaveGranterFormSet(leaveGranterFormSet);
		return response;
	}

	@Override
	public List<ClaimTemplateForm> getClaimTemplateList(Long companyId, Long claimTemplateId) {

		List<ClaimTemplateForm> claimTemplateFormList = new ArrayList<>();
		
		List<ClaimTemplate> claimTemplateList = claimTemplateDAO.getClaimCategoryCompany(companyId, claimTemplateId);

		for (ClaimTemplate claimTemplate : claimTemplateList) {
			ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
			claimTemplateForm.setClaimTemplateId(claimTemplate.getClaimTemplateId());
			claimTemplateForm.setClaimTemplateName(claimTemplate.getTemplateName());
			claimTemplateFormList.add(claimTemplateForm);
		}
		return claimTemplateFormList;
	}

}
