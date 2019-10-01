package com.payasia.logic.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.OTReviewerConditionDTO;
import com.payasia.common.form.OTReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeOTReviewerDAO;
import com.payasia.dao.OTTemplateDAO;
import com.payasia.dao.OTTemplateWorkflowDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeOTReviewer;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplateWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.OTReviewerLogic;

@Component
public class OTReviewerLogicImpl implements OTReviewerLogic {
	private static final Logger LOGGER = Logger
			.getLogger(OTReviewerLogicImpl.class);
	@Resource
	OTTemplateDAO oTTemplateDAO;

	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	@Resource
	OTTemplateWorkflowDAO oTTemplateWorkflowDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	EmployeeOTReviewerDAO employeeOTReviewerDAO;

	@Override
	public List<OTReviewerForm> getOTTemplateList(Long companyId) {

		List<OTReviewerForm> otTemplateList = new ArrayList<OTReviewerForm>();
		List<OTTemplate> otTemplateListVO = oTTemplateDAO
				.getOTTemplateList(companyId);
		for (OTTemplate OTTemplateVO : otTemplateListVO) {
			OTReviewerForm oTReviewerForm = new OTReviewerForm();
			oTReviewerForm.setOtSchemeId(OTTemplateVO.getOtTemplateId());
			oTReviewerForm.setOtSchemeName(OTTemplateVO.getTemplateName());

			otTemplateList.add(oTReviewerForm);

		}

		return otTemplateList;
	}

	@Override
	public List<OTReviewerForm> getWorkFlowRuleList() {

		List<OTReviewerForm> workFlowRuleList = new ArrayList<OTReviewerForm>();
		List<WorkFlowRuleMaster> workFlowRuleListVO = workFlowRuleMasterDAO
				.findByRuleName(PayAsiaConstants.WORK_FLOW_RULE_NAME_OT_REVIEWER);

		for (WorkFlowRuleMaster workFlowRuleMasterVO : workFlowRuleListVO) {
			OTReviewerForm oTReviewerForm = new OTReviewerForm();
			oTReviewerForm.setOtReviewerRuleId(workFlowRuleMasterVO
					.getWorkFlowRuleId());
			workFlowRuleList.add(oTReviewerForm);

		}

		return workFlowRuleList;

	}

	@Override
	public OTReviewerForm getOTReviewers(Long otTemplateId) {
		OTTemplateWorkflow otTemplateWorkflow = oTTemplateWorkflowDAO
				.findByTemplateIdRuleName(otTemplateId,
						PayAsiaConstants.LEAVE_REVIEWER_RULE);
		OTReviewerForm oTReviewerForm = new OTReviewerForm();
		oTReviewerForm.setRuleName(otTemplateWorkflow.getWorkFlowRuleMaster()
				.getRuleName());
		oTReviewerForm.setRuleValue(otTemplateWorkflow.getWorkFlowRuleMaster()
				.getRuleValue());

		return oTReviewerForm;
	}

	@Override
	public void saveOTReviewer(OTReviewerForm otReviewerForm, Long companyId) {

		int ruleValue = Integer.parseInt(otReviewerForm.getRuleValue());
		Class<?> c = otReviewerForm.getClass();

		try {

			Employee employee1 = employeeDAO.findById(otReviewerForm
					.getEmployeeId());
			OTTemplate otTemplate = oTTemplateDAO.findById(otReviewerForm
					.getOtSchemeId());

			EmployeeOTReviewer employeeOTReviewer = null;
			WorkFlowRuleMaster workFlowRuleReviewer1 = null;
			Employee employee2 = null;
			String methodNameForReviewer;
			String methodNameForWorkFlow;
			for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
				methodNameForReviewer = "getOtReveiwerId" + leaveReviewerCount;
				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Long employeeReviewerId;

				employeeReviewerId = (Long) methodReviewer
						.invoke(otReviewerForm);

				methodNameForWorkFlow = "getOtReviewerRuleId"
						+ leaveReviewerCount;
				Method methodWorkflow = c.getMethod(methodNameForWorkFlow);
				Long workFlowId = (Long) methodWorkflow.invoke(otReviewerForm);

				employee2 = employeeDAO.findById(employeeReviewerId);
				employeeOTReviewer = new EmployeeOTReviewer();
				employeeOTReviewer.setEmployee1(employee1);
				employeeOTReviewer.setEmployee2(employee2);
				workFlowRuleReviewer1 = workFlowRuleMasterDAO
						.findByID(workFlowId);
				employeeOTReviewer.setWorkFlowRuleMaster(workFlowRuleReviewer1);

				employeeOTReviewer.setOtTemplate(otTemplate);
				employeeOTReviewerDAO.save(employeeOTReviewer);

			}
		} catch (IllegalArgumentException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (SecurityException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {

			LOGGER.error(e.getMessage(), e);
		}

	}

	@Override
	public OTReviewerForm getOTReviewers(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {

		List<EmployeeOTReviewer> employeeOTReviewerListVO;

		OTReviewerConditionDTO conditionDTO = new OTReviewerConditionDTO();

		int recordSize = 0;

		recordSize = employeeOTReviewerDAO.getCountByCondition(conditionDTO,
				companyId);
		employeeOTReviewerListVO = employeeOTReviewerDAO.findByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);

		Iterator<EmployeeOTReviewer> it = employeeOTReviewerListVO.iterator();

		int ruleValue;

		OTReviewerForm otReviewerFormResponse = new OTReviewerForm();
		try {

			List<OTReviewerForm> oTReviewerFormList = new ArrayList<OTReviewerForm>();
			OTReviewerForm oTReviewerForm = null;

			while (it.hasNext()) {
				oTReviewerForm = new OTReviewerForm();

				Class<?> otReviewerFormClass = oTReviewerForm.getClass();

				EmployeeOTReviewer employeeOTReviewerVO = it.next();

				OTTemplateWorkflow otTemplateWorkflow = oTTemplateWorkflowDAO
						.findByTemplateIdRuleName(employeeOTReviewerVO
								.getOtTemplate().getOtTemplateId(),
								PayAsiaConstants.REVIEWER_RULE);

				ruleValue = Integer.parseInt(otTemplateWorkflow
						.getWorkFlowRuleMaster().getRuleValue());

				oTReviewerForm.setOtSchemeName(employeeOTReviewerVO
						.getOtTemplate().getTemplateName());

				oTReviewerForm.setEmployeeId(employeeOTReviewerVO
						.getEmployee1().getEmployeeId());

				oTReviewerForm.setEmployeeName(employeeOTReviewerVO
						.getEmployee1().getFirstName()
						+ " "
						+ employeeOTReviewerVO.getEmployee1().getLastName());

				for (int ruleValueCount = 1; ruleValueCount <= ruleValue; ruleValueCount++) {

					String otReviewMethodName = "setOtReviewer"
							+ ruleValueCount;
					Method otReviewMethod;

					otReviewMethod = otReviewerFormClass.getMethod(
							otReviewMethodName, String.class);

					if (ruleValueCount == 1) {
						otReviewMethod.invoke(oTReviewerForm,
								employeeOTReviewerVO.getEmployee2()
										.getFirstName()
										+ " "
										+ employeeOTReviewerVO.getEmployee2()
												.getLastName());

					} else {
						if (it.hasNext()) {
							EmployeeOTReviewer employeeOTReviewerVO1 = it
									.next();
							otReviewMethod.invoke(oTReviewerForm,
									employeeOTReviewerVO1.getEmployee2()
											.getFirstName()
											+ " "
											+ employeeOTReviewerVO1
													.getEmployee2()
													.getLastName());
						}
					}

				}

				oTReviewerFormList.add(oTReviewerForm);

			}

			if (pageDTO != null) {

				int pageSize = pageDTO.getPageSize();
				int totalPages = recordSize / pageSize;

				if (recordSize % pageSize != 0) {
					totalPages = totalPages + 1;
				}
				if (recordSize == 0) {
					pageDTO.setPageNumber(1);
				}

				otReviewerFormResponse.setPage(pageDTO.getPageNumber());
				otReviewerFormResponse.setTotal(totalPages);

				otReviewerFormResponse.setRecords(recordSize);
			}
			otReviewerFormResponse.setRows(oTReviewerFormList);

		} catch (SecurityException e) {
			 
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			 
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			 
			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			 
			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			 
			LOGGER.error(e.getMessage(), e);
		}

		return otReviewerFormResponse;

	}

	@Override
	public OTReviewerForm getOTReviewerData(Long employeeId, Long companyId) {

		OTReviewerForm oTReviewerFormResponse = new OTReviewerForm();
		List<OTReviewerForm> oTReviewerFormList = new ArrayList<OTReviewerForm>();

		List<EmployeeOTReviewer> employeeOTReviewerListVO;

		employeeOTReviewerListVO = employeeOTReviewerDAO
				.findByEmployeeIdCompanyId(employeeId, companyId);

		for (EmployeeOTReviewer employeeOTReviewerVO : employeeOTReviewerListVO) {

			OTReviewerForm oTReviewerForm = new OTReviewerForm();

			oTReviewerFormResponse.setEmployeeId(employeeOTReviewerVO
					.getEmployee1().getEmployeeId());
			oTReviewerFormResponse.setEmployeeName(employeeOTReviewerVO
					.getEmployee1().getFirstName()
					+ " "
					+ employeeOTReviewerVO.getEmployee1().getLastName());
			oTReviewerFormResponse.setOtSchemeId(employeeOTReviewerVO
					.getOtTemplate().getOtTemplateId());
			oTReviewerFormResponse.setRuleValue(employeeOTReviewerVO
					.getWorkFlowRuleMaster().getRuleValue());

			oTReviewerForm.setOtReviewerId(employeeOTReviewerVO.getEmployee2()
					.getEmployeeId());
			oTReviewerForm.setOtReviewerName(employeeOTReviewerVO
					.getEmployee2().getFirstName()
					+ " "
					+ employeeOTReviewerVO.getEmployee2().getLastName());
			oTReviewerForm.setEmployeeOTReviewerId(employeeOTReviewerVO
					.getEmployeeOTReviewerId());
			oTReviewerFormList.add(oTReviewerForm);

		}

		oTReviewerFormResponse.setRows(oTReviewerFormList);

		return oTReviewerFormResponse;
	}

	@Override
	public void updateOTReviewer(OTReviewerForm otReviewerForm, Long companyId) {

		int ruleValue = Integer.parseInt(otReviewerForm.getRuleValue());
		Class<?> c = otReviewerForm.getClass();

		try {

			EmployeeOTReviewer employeeOTReviewer = null;

			Employee employee2 = null;

			String methodNameForReviewer;
			String methodNameForOTReviewer;

			for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {

				methodNameForReviewer = "getOtReveiwerId" + leaveReviewerCount;
				methodNameForOTReviewer = "getEmployeeOTReviewerId"
						+ leaveReviewerCount;

				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Method methodEmployeeOTReviewer = c
						.getMethod(methodNameForOTReviewer);

				Long employeeReviewerId;
				Long employeeOTReviewerId;

				employeeOTReviewerId = (Long) methodEmployeeOTReviewer
						.invoke(otReviewerForm);

				employeeReviewerId = (Long) methodReviewer
						.invoke(otReviewerForm);
				employeeOTReviewer = employeeOTReviewerDAO
						.findById(employeeOTReviewerId);

				employee2 = employeeDAO.findById(employeeReviewerId);

				employeeOTReviewer.setEmployee2(employee2);

				employeeOTReviewerDAO.update(employeeOTReviewer);

			}
		} catch (IllegalArgumentException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (InvocationTargetException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (SecurityException e) {

			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {

			LOGGER.error(e.getMessage(), e);
		}

	}

	@Override
	public void deleteOTReviewer(Long employeeId) {

		employeeOTReviewerDAO.deleteByCondition(employeeId);

	}

	@Override
	public OTReviewerForm checkEmployeeReviewer(Long employeeId, Long companyId) {
		OTReviewerForm oTReviewerForm = new OTReviewerForm();
		List<EmployeeOTReviewer> employeeOTReviewersList = employeeOTReviewerDAO
				.findByEmployeeIdCompanyId(employeeId, companyId);
		if (employeeOTReviewersList != null) {
			oTReviewerForm.setEmployeeStatus("exists");
		} else {
			oTReviewerForm.setEmployeeStatus("notexists");
		}
		return oTReviewerForm;

	}
}
