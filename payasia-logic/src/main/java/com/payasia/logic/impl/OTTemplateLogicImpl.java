package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.OTTemplateConditionDTO;
import com.payasia.common.form.OTTemplateForm;
import com.payasia.common.form.OTTemplateResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeOTReviewerDAO;
import com.payasia.dao.OTItemMasterDAO;
import com.payasia.dao.OTTemplateDAO;
import com.payasia.dao.OTTemplateItemDAO;
import com.payasia.dao.OTTemplateWorkflowDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.EmployeeOTReviewer;
import com.payasia.dao.bean.OTItemMaster;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplateItem;
import com.payasia.dao.bean.OTTemplateWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.OTTemplateLogic;

@Component
public class OTTemplateLogicImpl implements OTTemplateLogic {
	private static final Logger LOGGER = Logger
			.getLogger(OTTemplateLogicImpl.class);
	@Resource
	OTTemplateWorkflowDAO otTemplateWorkflowDAO;
	@Resource
	OTTemplateDAO otTemplateDAO;
	@Resource
	OTTemplateItemDAO otTemplateItemDAO;
	@Resource
	OTItemMasterDAO otItemMasterDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	EmployeeOTReviewerDAO employeeOTReviewerDAO;

	@Override
	public OTTemplateResponse accessControl(Long companyId,
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO) {
		OTTemplateConditionDTO conditionDTO = new OTTemplateConditionDTO();
		if ("templateName".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setTemplateName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}

		if ("status".equals(searchCondition)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setStatus(URLDecoder.decode(searchText,
							"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}

			}
		}

		List<OTTemplateForm> otTemplateFormList = new ArrayList<OTTemplateForm>();

		List<OTTemplate> otTemplateVOList = otTemplateDAO
				.getAllOTTemplateByConditionCompany(companyId, conditionDTO,
						pageDTO, sortDTO);

		for (OTTemplate otTemplateVO : otTemplateVOList) {
			OTTemplateForm otTemplateForm = new OTTemplateForm();

			otTemplateForm.setTemplateName(otTemplateVO.getTemplateName());
			if (otTemplateVO.getVisibility() == true) {
				otTemplateForm.setActive("Visible");
			}
			if (otTemplateVO.getVisibility() == false) {
				otTemplateForm.setActive("Hidden");
			}
			otTemplateForm.setOtTemplateId(otTemplateVO.getOtTemplateId());
			otTemplateForm.setAccountCode(otTemplateVO.getAccountCode());
			otTemplateFormList.add(otTemplateForm);
		}
		OTTemplateResponse response = new OTTemplateResponse();
		int recordSize = otTemplateDAO.getCountForAllOTTemplate(companyId,
				conditionDTO);
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
		response.setRows(otTemplateFormList);

		return response;
	}

	@Override
	public String addOTTemplate(Long companyId, String otTemplateName) {
		boolean status = true;
		OTTemplate otTemplateVO = new OTTemplate();

		Company company = companyDAO.findById(companyId);
		otTemplateVO.setCompany(company);
		otTemplateVO.setTemplateName(otTemplateName);
		otTemplateVO.setVisibility(true);

		status = checkOTTemplateName(null, otTemplateName, companyId);

		if (status) {
			otTemplateDAO.save(otTemplateVO);
			return "notavailable";
		} else {
			return "available";
		}
	}

	public boolean checkOTTemplateName(Long otTemplateId,
			String otTemplateName, Long companyId) {
		OTTemplate otTemplateVO = otTemplateDAO.findByOTTemplateAndCompany(
				otTemplateId, otTemplateName, companyId);
		if (otTemplateVO == null) {
			return true;
		}
		return false;

	}

	@Override
	public void deleteOTTemplate(Long companyId, Long otTemplateId) {
		otTemplateWorkflowDAO.deleteByCondition(otTemplateId);

		OTTemplate otTemplateVO = otTemplateDAO.findById(otTemplateId);
		otTemplateDAO.delete(otTemplateVO);

	}

	@Override
	public OTTemplateForm getOTTemplate(Long companyId, Long otTemplateId) {
		List<OTTemplateWorkflow> otTemplateWorkflowVOList = otTemplateWorkflowDAO
				.findByCondition(otTemplateId);
		OTTemplateForm otTemplateForm = new OTTemplateForm();

		OTTemplate otTemplateVO = otTemplateDAO.findById(otTemplateId);
		otTemplateForm.setTemplateName(otTemplateVO.getTemplateName());
		otTemplateForm.setAccountCode(otTemplateVO.getAccountCode());
		if (otTemplateVO.getVisibility() == true) {
			otTemplateForm.setVisibility(true);
		}
		if (otTemplateVO.getVisibility() == false) {
			otTemplateForm.setVisibility(false);
		}

		for (OTTemplateWorkflow otTemplateWorkflowVO : otTemplateWorkflowVOList) {

			if (otTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL)) {
				otTemplateForm.setWorkFlowLevelOT(otTemplateWorkflowVO
						.getWorkFlowRuleMaster().getRuleValue());
			}

			if (otTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_OVERRIDE)) {
				String allowOverRideVal = otTemplateWorkflowVO
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowOverRideVal.charAt(0);
				char atlevelTwo = allowOverRideVal.charAt(1);
				char atlevelThree = allowOverRideVal.charAt(2);

				if (atlevelOne == '0') {
					otTemplateForm.setAllowOverrideOTL1(false);
				}
				if (atlevelOne == '1') {
					otTemplateForm.setAllowOverrideOTL1(true);
				}
				if (atlevelTwo == '0') {
					otTemplateForm.setAllowOverrideOTL2(false);
				}
				if (atlevelTwo == '1') {
					otTemplateForm.setAllowOverrideOTL2(true);
				}
				if (atlevelThree == '0') {
					otTemplateForm.setAllowOverrideOTL3(false);
				}
				if (atlevelThree == '1') {
					otTemplateForm.setAllowOverrideOTL3(true);
				}
			}

			if (otTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_REJECT)) {
				String allowRejectVal = otTemplateWorkflowVO
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowRejectVal.charAt(0);
				char atlevelTwo = allowRejectVal.charAt(1);
				char atlevelThree = allowRejectVal.charAt(2);

				if (atlevelOne == '0') {
					otTemplateForm.setAllowRejectOTL1(false);
				}
				if (atlevelOne == '1') {
					otTemplateForm.setAllowRejectOTL1(true);
				}
				if (atlevelTwo == '0') {
					otTemplateForm.setAllowRejectOTL2(false);
				}
				if (atlevelTwo == '1') {
					otTemplateForm.setAllowRejectOTL2(true);
				}
				if (atlevelThree == '0') {
					otTemplateForm.setAllowRejectOTL3(false);
				}
				if (atlevelThree == '1') {
					otTemplateForm.setAllowRejectOTL3(true);
				}
			}
		}
		return otTemplateForm;
	}

	@Override
	public String configureOTTemplate(OTTemplateForm otTemplateForm,
			Long companyId) {
		boolean status = true;
		status = checkOTTemplateName(otTemplateForm.getOtTemplateId(),
				otTemplateForm.getTemplateName(), companyId);

		OTTemplate otTemplateVO = otTemplateDAO.findById(otTemplateForm
				.getOtTemplateId());

		if (!status) {
			return "available";
		}
		if (status) {
			Company company = companyDAO.findById(companyId);
			otTemplateVO.setCompany(company);
			otTemplateVO.setTemplateName(otTemplateForm.getTemplateName());
			otTemplateVO.setVisibility(otTemplateForm.getVisibility());
			otTemplateVO.setAccountCode(otTemplateForm.getAccountCode());
			otTemplateDAO.update(otTemplateVO);
		}

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO
				.findAll();

		OTTemplateWorkflow otTemplateWorkflowVO = otTemplateWorkflowDAO
				.findByTemplateIdRuleName(otTemplateForm.getOtTemplateId(),
						PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL);

		if (otTemplateWorkflowVO != null) {
			if (otTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue()
					.equals("1")) {
				otTemplateWorkflowDAO.deleteByCondition(otTemplateForm
						.getOtTemplateId());
				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL1)) {
					Long workFlowId = getWorkFlowMasterId(
							PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
							"1", workFlowRuleMasterList);
					OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

					WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
							.findByID(workFlowId);
					otTemplateWorkflow
							.setWorkFlowRuleMaster(workFlowRuleMaster);
					otTemplateWorkflow.setOtTemplate(otTemplateVO);
					otTemplateWorkflowDAO.save(otTemplateWorkflow);
				}
				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL2)) {
					Long workFlowId = getWorkFlowMasterId(
							PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
							"2", workFlowRuleMasterList);
					OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

					WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
							.findByID(workFlowId);
					otTemplateWorkflow
							.setWorkFlowRuleMaster(workFlowRuleMaster);
					otTemplateWorkflow.setOtTemplate(otTemplateVO);
					otTemplateWorkflowDAO.save(otTemplateWorkflow);
				}
				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL3)) {
					Long workFlowId = getWorkFlowMasterId(
							PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
							"3", workFlowRuleMasterList);
					OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

					WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
							.findByID(workFlowId);
					otTemplateWorkflow
							.setWorkFlowRuleMaster(workFlowRuleMaster);
					otTemplateWorkflow.setOtTemplate(otTemplateVO);
					otTemplateWorkflowDAO.save(otTemplateWorkflow);
				}
			}
			if (otTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue()
					.equals("2")) {

				Long otReviewerId = getWorkFlowMasterId(
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_CLAIM_REVIEWER,
						"1", workFlowRuleMasterList);

				List<EmployeeOTReviewer> empOTReviewerForLevelTwoVO = employeeOTReviewerDAO
						.findByOTTemplateIdAndWorkFlowId(
								otTemplateForm.getOtTemplateId(), otReviewerId);
				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL1)) {
					if (empOTReviewerForLevelTwoVO.size() != 0) {
						return "1";
					}
					if (empOTReviewerForLevelTwoVO.size() == 0) {
						otTemplateWorkflowDAO.deleteByCondition(otTemplateForm
								.getOtTemplateId());
						Long workFlowId = getWorkFlowMasterId(
								PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
								"1", workFlowRuleMasterList);
						OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

						WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
								.findByID(workFlowId);
						otTemplateWorkflow
								.setWorkFlowRuleMaster(workFlowRuleMaster);
						otTemplateWorkflow.setOtTemplate(otTemplateVO);
						otTemplateWorkflowDAO.save(otTemplateWorkflow);
					}
				}

				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL2)) {
					otTemplateWorkflowDAO.deleteByCondition(otTemplateForm
							.getOtTemplateId());
					Long workFlowId = getWorkFlowMasterId(
							PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
							"2", workFlowRuleMasterList);
					OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

					WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
							.findByID(workFlowId);
					otTemplateWorkflow
							.setWorkFlowRuleMaster(workFlowRuleMaster);
					otTemplateWorkflow.setOtTemplate(otTemplateVO);
					otTemplateWorkflowDAO.save(otTemplateWorkflow);
				}
				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL3)) {
					otTemplateWorkflowDAO.deleteByCondition(otTemplateForm
							.getOtTemplateId());
					Long workFlowId = getWorkFlowMasterId(
							PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
							"3", workFlowRuleMasterList);
					OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

					WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
							.findByID(workFlowId);
					otTemplateWorkflow
							.setWorkFlowRuleMaster(workFlowRuleMaster);
					otTemplateWorkflow.setOtTemplate(otTemplateVO);
					otTemplateWorkflowDAO.save(otTemplateWorkflow);
				}
			}
			if (otTemplateWorkflowVO.getWorkFlowRuleMaster().getRuleValue()
					.equals("3")) {
				Long otReviewerForLevelOneId = getWorkFlowMasterId(
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_CLAIM_REVIEWER,
						"1", workFlowRuleMasterList);

				List<EmployeeOTReviewer> empOTReviewerForLevelOneVO = employeeOTReviewerDAO
						.findByOTTemplateIdAndWorkFlowId(
								otTemplateForm.getOtTemplateId(),
								otReviewerForLevelOneId);

				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL1)) {
					if (empOTReviewerForLevelOneVO.size() != 0) {
						return "1";
					}
					if (empOTReviewerForLevelOneVO.size() == 0) {
						otTemplateWorkflowDAO.deleteByCondition(otTemplateForm
								.getOtTemplateId());
						Long workFlowId = getWorkFlowMasterId(
								PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
								"1", workFlowRuleMasterList);
						OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

						WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
								.findByID(workFlowId);
						otTemplateWorkflow
								.setWorkFlowRuleMaster(workFlowRuleMaster);
						otTemplateWorkflow.setOtTemplate(otTemplateVO);
						otTemplateWorkflowDAO.save(otTemplateWorkflow);
					}

				}

				Long otReviewerForLevelTwoId = getWorkFlowMasterId(
						PayAsiaConstants.CLAIM_TEMPLATE_DEF_CLAIM_REVIEWER,
						"2", workFlowRuleMasterList);

				List<EmployeeOTReviewer> empOTReviewerForLevelTwoVO = employeeOTReviewerDAO
						.findByOTTemplateIdAndWorkFlowId(
								otTemplateForm.getOtTemplateId(),
								otReviewerForLevelTwoId);
				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL2)) {
					if (empOTReviewerForLevelTwoVO.size() != 0) {
						return "2";
					}
					if (empOTReviewerForLevelTwoVO.size() == 0) {
						otTemplateWorkflowDAO.deleteByCondition(otTemplateForm
								.getOtTemplateId());
						Long workFlowId = getWorkFlowMasterId(
								PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
								"2", workFlowRuleMasterList);
						OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

						WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
								.findByID(workFlowId);
						otTemplateWorkflow
								.setWorkFlowRuleMaster(workFlowRuleMaster);
						otTemplateWorkflow.setOtTemplate(otTemplateVO);
						otTemplateWorkflowDAO.save(otTemplateWorkflow);
					}
				}
				if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
						.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL3)) {
					otTemplateWorkflowDAO.deleteByCondition(otTemplateForm
							.getOtTemplateId());
					Long workFlowId = getWorkFlowMasterId(
							PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL,
							"3", workFlowRuleMasterList);
					OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

					WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
							.findByID(workFlowId);
					otTemplateWorkflow
							.setWorkFlowRuleMaster(workFlowRuleMaster);
					otTemplateWorkflow.setOtTemplate(otTemplateVO);
					otTemplateWorkflowDAO.save(otTemplateWorkflow);
				}
			}
		} else {
			if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
					.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL1)) {
				Long workFlowId = getWorkFlowMasterId(
						PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL, "1",
						workFlowRuleMasterList);
				OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

				WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
						.findByID(workFlowId);
				otTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
				otTemplateWorkflow.setOtTemplate(otTemplateVO);
				otTemplateWorkflowDAO.save(otTemplateWorkflow);
			}
			if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
					.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL2)) {
				Long workFlowId = getWorkFlowMasterId(
						PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL, "2",
						workFlowRuleMasterList);
				OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

				WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
						.findByID(workFlowId);
				otTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
				otTemplateWorkflow.setOtTemplate(otTemplateVO);
				otTemplateWorkflowDAO.save(otTemplateWorkflow);
			}
			if (otTemplateForm.getWorkFlowLevelOT().toUpperCase()
					.equals(PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOWOTL3)) {
				Long workFlowId = getWorkFlowMasterId(
						PayAsiaConstants.OT_TEMPLATE_DEF_WORKFLOW_LEVEL, "3",
						workFlowRuleMasterList);
				OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

				WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
						.findByID(workFlowId);
				otTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
				otTemplateWorkflow.setOtTemplate(otTemplateVO);
				otTemplateWorkflowDAO.save(otTemplateWorkflow);
			}
		}

		String allowOverrideStr = "";
		if (otTemplateForm.getAllowOverrideOTL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (otTemplateForm.getAllowOverrideOTL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (otTemplateForm.getAllowOverrideOTL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		Long allowOverrideId = getWorkFlowMasterId(
				PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		OTTemplateWorkflow otTemplateWorkflow = new OTTemplateWorkflow();

		WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
				.findByID(allowOverrideId);
		otTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		otTemplateWorkflow.setOtTemplate(otTemplateVO);
		otTemplateWorkflowDAO.save(otTemplateWorkflow);

		String allowRejectStr = "";
		if (otTemplateForm.getAllowRejectOTL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (otTemplateForm.getAllowRejectOTL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (otTemplateForm.getAllowRejectOTL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		Long allowRejectId = getWorkFlowMasterId(
				PayAsiaConstants.OT_TEMPLATE_DEF_ALLOW_REJECT,
				allowRejectStr.trim(), workFlowRuleMasterList);
		otTemplateWorkflow = new OTTemplateWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = workFlowRuleMasterDAO
				.findByID(allowRejectId);
		otTemplateWorkflow.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		otTemplateWorkflow.setOtTemplate(otTemplateVO);
		otTemplateWorkflowDAO.save(otTemplateWorkflow);

		return "notavailable";

	}

	public Long getWorkFlowMasterId(String ruleName, String ruleValue,
			List<WorkFlowRuleMaster> workFlowRuleMasterList) {
		for (WorkFlowRuleMaster workFlowRuleMaster : workFlowRuleMasterList) {
			if (ruleName.equalsIgnoreCase(workFlowRuleMaster.getRuleName())
					&& ruleValue.equals(workFlowRuleMaster.getRuleValue())) {
				return workFlowRuleMaster.getWorkFlowRuleId();
			}
		}
		return null;
	}

	@Override
	public Set<OTTemplateForm> getOTTypeList(Long companyId, Long otTemplateId) {
		Set<OTTemplateForm> otTemplateFormSet = new HashSet<OTTemplateForm>();

		List<OTItemMaster> otItemMasterList = otItemMasterDAO
				.findByCondition(otTemplateId);

		List<OTItemMaster> otItemMasterVOList = otItemMasterDAO.findAll(
				companyId, null, null);
		otItemMasterVOList.removeAll(otItemMasterList);

		for (OTItemMaster otItemMasterVO : otItemMasterVOList) {
			OTTemplateForm otTemplateForm = new OTTemplateForm();

			otTemplateForm.setOtItemId(otItemMasterVO.getOtItemId());
			otTemplateForm.setOtItem(otItemMasterVO.getOtItemName());
			otTemplateFormSet.add(otTemplateForm);

		}

		return otTemplateFormSet;
	}

	@Override
	public void addOTType(String[] otTypeId, Long otTemplateId) {
		OTTemplateItem otTemplateItem = new OTTemplateItem();

		OTTemplate otTemplateVO = otTemplateDAO.findById(otTemplateId);
		for (int count = 0; count < otTypeId.length; count++) {
			OTItemMaster otItemMasterVO = otItemMasterDAO.findById(Long
					.parseLong(otTypeId[count]));

			otTemplateItem.setOtTemplate(otTemplateVO);
			otTemplateItem.setOtItemMaster(otItemMasterVO);
			otTemplateItem.setVisibility(true);
			otTemplateItemDAO.save(otTemplateItem);
		}

	}

	@Override
	public OTTemplateResponse viewOTType(Long otTemplateId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO) {
		List<OTTemplateForm> otTemplateFormList = new ArrayList<OTTemplateForm>();

		List<OTTemplateItem> otTemplateItemVOList = otTemplateItemDAO
				.findByCondition(otTemplateId, companyId);
		for (OTTemplateItem otTemplateItemVO : otTemplateItemVOList) {
			OTTemplateForm otTemplateForm = new OTTemplateForm();
			otTemplateForm.setOtItemId(otTemplateItemVO.getOtItemMaster()
					.getOtItemId());
			otTemplateForm.setOtItem(otTemplateItemVO.getOtItemMaster()
					.getOtItemName());
			otTemplateForm.setOtTemplateItemId(otTemplateItemVO
					.getOTTemplateItemId());
			if (otTemplateItemVO.getVisibility() == true) {
				otTemplateForm.setActive("Visible");
			}
			if (otTemplateItemVO.getVisibility() == false) {
				otTemplateForm.setActive("Hidden");
			}
			otTemplateFormList.add(otTemplateForm);
		}
		OTTemplateResponse response = new OTTemplateResponse();
		response.setRows(otTemplateFormList);

		return response;
	}

	@Override
	public void editOTType(Long otTemplateTypeId, OTTemplateForm otTemplateForm) {
		OTTemplateItem otTemplateItem = otTemplateItemDAO
				.findById(otTemplateTypeId);
		otTemplateItem.setVisibility(otTemplateForm.getVisibility());
		otTemplateItemDAO.update(otTemplateItem);

	}

	@Override
	public OTTemplateForm getOTTypeForEdit(Long otTemplateTypeId) {
		OTTemplateItem otTemplateItemVO = otTemplateItemDAO
				.findById(otTemplateTypeId);
		OTTemplateForm otTemplateForm = new OTTemplateForm();
		otTemplateForm.setOtItem(otTemplateItemVO.getOtItemMaster()
				.getOtItemName());
		otTemplateForm.setVisibility(otTemplateItemVO.getVisibility());
		return otTemplateForm;
	}

	@Override
	public void deleteOTType(Long otTemplateTypeId, Long otTemplateId) {

	}

}
