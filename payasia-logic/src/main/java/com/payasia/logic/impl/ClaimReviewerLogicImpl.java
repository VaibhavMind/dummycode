package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import javax.security.sasl.AuthenticationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimReviewerConditionDTO;
import com.payasia.common.dto.ClaimReviewersDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.ClaimReviewerDetailViewDAO;
import com.payasia.dao.ClaimTemplateDAO;
import com.payasia.dao.ClaimTemplateWorkflowDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.EmployeeClaimTemplateDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.OTTemplateWorkflowDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.ClaimReviewerDetailView;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateWorkflow;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeClaimReviewer;
import com.payasia.dao.bean.EmployeeClaimTemplate;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.BaseLogic;
import com.payasia.logic.ClaimReviewerLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class ClaimReviewerLogicImpl extends BaseLogic implements ClaimReviewerLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(ClaimReviewerLogicImpl.class);

	@Resource
	ClaimTemplateDAO claimTemplateDAO;

	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;

	@Resource
	ClaimTemplateWorkflowDAO claimTemplateWorkflowDAO;

	@Resource
	EmployeeDAO employeeDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	EmployeeClaimReviewerDAO employeeClaimReviewerDAO;

	@Resource
	OTTemplateWorkflowDAO oTTemplateWorkflowDAO;

	@Resource
	ClaimReviewerDetailViewDAO claimReviewerDetailViewDAO;

	@Resource
	EmployeeClaimTemplateDAO employeeClaimTemplateDAO;

	@Resource
	GeneralLogic generalLogic;

	@Override
	public List<ClaimReviewerForm> getClaimTemplateList(Long companyId) {

		List<ClaimReviewerForm> claimTemplateList = new ArrayList<ClaimReviewerForm>();
		List<ClaimTemplate> claimTemplateListVO = claimTemplateDAO.getClaimTemplateList(companyId);
		for (ClaimTemplate claimTemplateVO : claimTemplateListVO) {
			ClaimReviewerForm claimReviewerForm = new ClaimReviewerForm();
			claimReviewerForm.setClaimSchemeId(FormatPreserveCryptoUtil.encrypt(claimTemplateVO.getClaimTemplateId()));
			claimReviewerForm.setClaimSchemeName(claimTemplateVO.getTemplateName());
			claimTemplateList.add(claimReviewerForm);

		}

		return claimTemplateList;
	}

	@Override
	public List<ClaimReviewerForm> getWorkFlowRuleList() {

		List<ClaimReviewerForm> workFlowRuleList = new ArrayList<ClaimReviewerForm>();
		List<WorkFlowRuleMaster> workFlowRuleListVO = workFlowRuleMasterDAO
				.findByRuleName(PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER);

		for (WorkFlowRuleMaster workFlowRuleMasterVO : workFlowRuleListVO) {
			ClaimReviewerForm claimReviewerForm = new ClaimReviewerForm();
			claimReviewerForm.setClaimReviewerRuleId(workFlowRuleMasterVO.getWorkFlowRuleId());
			workFlowRuleList.add(claimReviewerForm);

		}

		return workFlowRuleList;

	}

	@Override
	public ClaimReviewerForm getClaimReviewers(Long claimTemplateId, Long companyId) {

		ClaimTemplateWorkflow claimTemplateWorkflow = claimTemplateWorkflowDAO.findByTemplateIdRuleName(claimTemplateId,
				companyId, PayAsiaConstants.LEAVE_REVIEWER_RULE);

		ClaimReviewerForm claimReviewerForm = new ClaimReviewerForm();
		if (claimTemplateWorkflow != null) {
			claimReviewerForm.setRuleName(claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleName());
			claimReviewerForm.setRuleValue(claimTemplateWorkflow.getWorkFlowRuleMaster().getRuleValue());
		}

		return claimReviewerForm;
	}

	@Override
	public void saveClaimReviewer(ClaimReviewerForm claimReviewerForm, Long companyId) throws AuthenticationException {

		int ruleValue = Integer.parseInt(claimReviewerForm.getRuleValue());
		Class<?> c = claimReviewerForm.getClass();
		Company company = companyDAO.findById(companyId);
		try {

			Employee employee1 = employeeDAO
					.findById(FormatPreserveCryptoUtil.decrypt(claimReviewerForm.getEmployeeId()), companyId);

			AddClaimDTO addClaimDTO = new AddClaimDTO();
			addClaimDTO.setEmployeeClaimTemplateId(
					FormatPreserveCryptoUtil.decrypt(claimReviewerForm.getEmployeeClaimTemplateId()));
			addClaimDTO.setCompanyId(companyId);
			addClaimDTO.setAdmin(true);

			EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
					.findByEmployeeClaimTemplateID(addClaimDTO);

			EmployeeClaimReviewer employeeClaimReviewer = null;
			WorkFlowRuleMaster workFlowRuleReviewer1 = null;
			Employee employee2 = null;
			String methodNameForReviewer;
			String methodNameForWorkFlow;
			Set<Long> empIdDublicatedCheck = new HashSet<Long>();
			empIdDublicatedCheck.add(employee1.getEmployeeId());
			for (int leaveReviewerCount = 1; leaveReviewerCount <= ruleValue; leaveReviewerCount++) {
				methodNameForReviewer = "getClaimReiveiwerId" + leaveReviewerCount;
				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Long employeeReviewerId;

				employeeReviewerId = (Long) methodReviewer.invoke(claimReviewerForm);

				methodNameForWorkFlow = "getClaimReviewerRuleId" + leaveReviewerCount;
				Method methodWorkflow = c.getMethod(methodNameForWorkFlow);
				Long workFlowId = (Long) methodWorkflow.invoke(claimReviewerForm);

				employee2 = employeeDAO.findByGroupCompanyId(FormatPreserveCryptoUtil.decrypt(employeeReviewerId),
						company.getCompanyGroup().getGroupId());
				employeeClaimReviewer = new EmployeeClaimReviewer();

				if (empIdDublicatedCheck.contains(employee2.getEmployeeId())) {
					throw new AuthenticationException();
				} else {
					empIdDublicatedCheck.add(employee2.getEmployeeId());
				}
				employeeClaimReviewer.setEmployee1(employee1);
				employeeClaimReviewer.setEmployee2(employee2);
				workFlowRuleReviewer1 = workFlowRuleMasterDAO.findByID(workFlowId);
				employeeClaimReviewer.setWorkFlowRuleMaster(workFlowRuleReviewer1);

				employeeClaimReviewer.setEmployeeClaimTemplate(employeeClaimTemplate);
				employeeClaimReviewerDAO.save(employeeClaimReviewer);

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
	public ClaimReviewerForm getClaimReviewers(String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {
		if (sortDTO.getColumnName().equals("")) {
			sortDTO.setColumnName(SortConstants.CLAIM_REVIEWER_EMPLOYEE_NAME);
			sortDTO.setOrderType(PayAsiaConstants.ASC);
		}

		List<ClaimReviewerDetailView> claimReviewerListVO;

		ClaimReviewerConditionDTO conditionDTO = new ClaimReviewerConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.CLAIM_REVIEWER_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setEmployeeName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.CLAIM_REVIEWER_LEAVE_SCHEME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setClaimTemplateName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.CLAIM_REVIEWER_LEAVE_REVIEWER1)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLeaveReviewer1("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.CLAIM_REVIEWER_LEAVE_REVIEWER2)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLeaveReviewer2("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.CLAIM_REVIEWER_LEAVE_REVIEWER3)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setLeaveReviewer3("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		int recordSize = 0;

		claimReviewerListVO = claimReviewerDetailViewDAO.findByCondition(conditionDTO, pageDTO, sortDTO, companyId);
		List<ClaimReviewerForm> claimReviewerFormList = new ArrayList<ClaimReviewerForm>();
		for (ClaimReviewerDetailView claimReviewerVO : claimReviewerListVO) {
			ClaimReviewerForm claimReviewerForm = new ClaimReviewerForm();
			String filterIds = "";
			filterIds += FormatPreserveCryptoUtil
					.encrypt(claimReviewerVO.getClaimReviewerDetailViewPK().getEmployeeId()) + ","
					+ FormatPreserveCryptoUtil
							.encrypt(claimReviewerVO.getClaimReviewerDetailViewPK().getEmployeeClaimTemplateId())

					+ "," + FormatPreserveCryptoUtil
							.encrypt(claimReviewerVO.getClaimReviewerDetailViewPK().getEmployeeClaimTemplateId());
			claimReviewerForm.setFilterIds(filterIds);

			claimReviewerForm.setEmployeeId(
					FormatPreserveCryptoUtil.encrypt(claimReviewerVO.getClaimReviewerDetailViewPK().getEmployeeId()));
			String empName = claimReviewerVO.getEmpFirstName() + " ";
			if (StringUtils.isNotBlank(claimReviewerVO.getEmpLastName())) {
				empName += claimReviewerVO.getEmpLastName();
			}
			claimReviewerForm.setEmployeeName(empName);
			claimReviewerForm.setClaimReiveiwerId1(claimReviewerVO.getReviewer1Id());
			String claimReviewer1Name = null;
			if (StringUtils.isNotBlank(claimReviewerVO.getReviewer1FirstName())) {
				claimReviewer1Name = claimReviewerVO.getReviewer1FirstName() + " ";
				if (StringUtils.isNotBlank(claimReviewerVO.getReviewer1LastName())) {
					claimReviewer1Name += claimReviewerVO.getReviewer1LastName();
				}
				claimReviewerForm.setClaimReviewer1(claimReviewer1Name);
			}

			claimReviewerForm.setClaimReiveiwerId2(claimReviewerVO.getReviewer2Id());

			String claimReviewer2Name = null;
			if (StringUtils.isNotBlank(claimReviewerVO.getReviewer2FirstName())) {
				claimReviewer2Name = claimReviewerVO.getReviewer2FirstName() + " ";
				if (StringUtils.isNotBlank(claimReviewerVO.getReviewer2LastName())) {
					claimReviewer2Name += claimReviewerVO.getReviewer2LastName();
				}
				claimReviewerForm.setClaimReviewer2(claimReviewer2Name);
			}
			claimReviewerForm.setClaimReiveiwerId3(claimReviewerVO.getReviewer3Id());
			String claimReviewer3Name = null;
			if (StringUtils.isNotBlank(claimReviewerVO.getReviewer3FirstName())) {
				claimReviewer3Name = claimReviewerVO.getReviewer3FirstName() + " ";
				if (StringUtils.isNotBlank(claimReviewerVO.getReviewer3LastName())) {
					claimReviewer3Name += claimReviewerVO.getReviewer3LastName();
				}
				claimReviewerForm.setClaimReviewer3(claimReviewer3Name);
			}
			claimReviewerForm
					.setClaimSchemeId(claimReviewerVO.getClaimReviewerDetailViewPK().getEmployeeClaimTemplateId());
			claimReviewerForm.setClaimSchemeName(claimReviewerVO.getClaimTemplateName());
			claimReviewerFormList.add(claimReviewerForm);
		}
		recordSize = (claimReviewerDetailViewDAO.getCountByCondition(conditionDTO, companyId)).intValue();
		;
		ClaimReviewerForm response = new ClaimReviewerForm();
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
		response.setRows(claimReviewerFormList);
		response.setRecords(recordSize);
		return response;

	}

	@Override
	public ClaimReviewerForm getClaimReviewerData(Long[] filterIds, Long companyId) {
		Long claimTemplateId = null;
		ClaimReviewerForm claimReviewerFormResponse = new ClaimReviewerForm();
		List<ClaimReviewerForm> employeeClaimReviewerList = new ArrayList<ClaimReviewerForm>();

		List<EmployeeClaimReviewer> employeeClaimReviewerListVO;

		employeeClaimReviewerListVO = employeeClaimReviewerDAO.findByCondition(
				FormatPreserveCryptoUtil.decrypt(filterIds[0]), FormatPreserveCryptoUtil.decrypt(filterIds[1]),
				companyId);
		String claimTemplateName = "";
		for (EmployeeClaimReviewer employeeClaimReviewerVO : employeeClaimReviewerListVO) {

			ClaimReviewerForm claimReviewerForm = new ClaimReviewerForm();

			claimReviewerFormResponse.setEmployeeId(
					FormatPreserveCryptoUtil.encrypt(employeeClaimReviewerVO.getEmployee1().getEmployeeId()));
			String employeeName1 = employeeClaimReviewerVO.getEmployee1().getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeClaimReviewerVO.getEmployee1().getLastName())) {
				employeeName1 += employeeClaimReviewerVO.getEmployee1().getLastName();
			}
			employeeName1 += "[" + employeeClaimReviewerVO.getEmployee1().getEmployeeNumber() + "]";
			claimReviewerFormResponse.setEmployeeName(employeeName1);
			claimReviewerFormResponse.setClaimSchemeId(FormatPreserveCryptoUtil.encrypt(
					employeeClaimReviewerVO.getEmployeeClaimTemplate().getClaimTemplate().getClaimTemplateId()));
			claimReviewerFormResponse.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil
					.encrypt(employeeClaimReviewerVO.getEmployeeClaimTemplate().getEmployeeClaimTemplateId()));
			claimTemplateId = employeeClaimReviewerVO.getEmployeeClaimTemplate().getClaimTemplate()
					.getClaimTemplateId();

			claimReviewerForm.setClaimReviewerId(
					FormatPreserveCryptoUtil.encrypt(employeeClaimReviewerVO.getEmployee2().getEmployeeId()));
			String employeeName2 = employeeClaimReviewerVO.getEmployee2().getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeClaimReviewerVO.getEmployee2().getLastName())) {
				employeeName2 += employeeClaimReviewerVO.getEmployee2().getLastName();
			}
			employeeName2 += "[" + employeeClaimReviewerVO.getEmployee2().getEmployeeNumber() + "]";
			claimReviewerForm.setClaimReviewerName(employeeName2);
			claimReviewerForm.setEmployeeClaimReviewerId(
					FormatPreserveCryptoUtil.encrypt(employeeClaimReviewerVO.getEmployeeClaimReviewerId()));
			claimTemplateName = employeeClaimReviewerVO.getEmployeeClaimTemplate().getClaimTemplate().getTemplateName();
			employeeClaimReviewerList.add(claimReviewerForm);

		}
		WorkFlowRuleMaster workFlowRuleMasterVO = workFlowRuleMasterDAO.findByClaimTemplateCondition(claimTemplateId,
				PayAsiaConstants.CLAIM_TEMPLATE_DEF_WORKFLOW_LEVEL);
		claimReviewerFormResponse.setRuleValue(workFlowRuleMasterVO.getRuleValue());
		claimReviewerFormResponse.setClaimSchemeName(claimTemplateName);

		claimReviewerFormResponse.setRows(employeeClaimReviewerList);

		return claimReviewerFormResponse;
	}

	@Override
	public void updateClaimReviewer(ClaimReviewerForm claimReviewerForm, Long companyId)
			throws AuthenticationException {

		int ruleValue = Integer.parseInt(claimReviewerForm.getRuleValue());
		Class<?> c = claimReviewerForm.getClass();

		try {
			EmployeeClaimReviewer employeeClaimReviewer = null;
			Employee employee1 = null;

			String methodNameForReviewer;
			String methodNameForClaimReviewer;
			String methodNameForClaimReviewerRule;
			Set<Long> empIdDublicatedCheck = new HashSet<Long>();
			for (int claimReviewerCount = 1; claimReviewerCount <= ruleValue; claimReviewerCount++) {
				Employee employee2 = null;

				methodNameForReviewer = "getClaimReiveiwerId" + claimReviewerCount;
				methodNameForClaimReviewer = "getEmployeeClaimReviewerId" + claimReviewerCount;
				methodNameForClaimReviewerRule = "getClaimReviewerRuleId" + claimReviewerCount;

				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Method methodEmployeeClaimReviewer = c.getMethod(methodNameForClaimReviewer);
				Method methodEmployeeClaimReviewerRule = c.getMethod(methodNameForClaimReviewerRule);

				Long employeeReviewerId;
				Long employeeClaimReviewerId;
				Long employeeClaimReviewerRuleId;

				employeeClaimReviewerId = (Long) methodEmployeeClaimReviewer.invoke(claimReviewerForm);

				employeeReviewerId = (Long) methodReviewer.invoke(claimReviewerForm);

				employeeClaimReviewerRuleId = (Long) methodEmployeeClaimReviewerRule.invoke(claimReviewerForm);

				if (employeeClaimReviewerId != null) {
					employeeClaimReviewer = employeeClaimReviewerDAO
							.findById(FormatPreserveCryptoUtil.decrypt(employeeClaimReviewerId));
					if (claimReviewerCount == 1) {
						empIdDublicatedCheck.add(employeeClaimReviewer.getEmployee1().getEmployeeId());
					}
					if (employeeReviewerId != null) {
						employee2 = employeeDAO.findById(FormatPreserveCryptoUtil.decrypt(employeeReviewerId));

						if (empIdDublicatedCheck.contains(employee2.getEmployeeId())) {
							throw new AuthenticationException();
						} else {
							empIdDublicatedCheck.add(employee2.getEmployeeId());
						}

						employeeClaimReviewer.setEmployee2(employee2);

						employeeClaimReviewerDAO.update(employeeClaimReviewer);
					} else {
						employeeClaimReviewerDAO.delete(employeeClaimReviewer);
					}
				} else {

					if (employeeReviewerId != null) {
						String[] empIds = claimReviewerForm.getFilterIds().split(",");
						EmployeeClaimReviewer emplClaimReviewer = new EmployeeClaimReviewer();
						employee1 = employeeDAO.findById(FormatPreserveCryptoUtil.decrypt(Long.parseLong(empIds[0])));
						if (claimReviewerCount == 1) {
							empIdDublicatedCheck.add(employee1.getEmployeeId());
						}
						employee2 = employeeDAO.findById(FormatPreserveCryptoUtil.decrypt(employeeReviewerId));

						if (empIdDublicatedCheck.contains(employee2.getEmployeeId())) {
							throw new AuthenticationException();
						} else {
							empIdDublicatedCheck.add(employee2.getEmployeeId());
						}
						emplClaimReviewer.setEmployee1(employee1);
						emplClaimReviewer.setEmployee2(employee2);
						WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
								.findByID(employeeClaimReviewerRuleId);
						emplClaimReviewer.setWorkFlowRuleMaster(workFlowRuleMaster);

						EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO
								.findByID(FormatPreserveCryptoUtil.decrypt(Long.parseLong(empIds[1])));

						emplClaimReviewer.setEmployeeClaimTemplate(employeeClaimTemplate);
						employeeClaimReviewerDAO.save(emplClaimReviewer);
					}
				}

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
	public void deleteClaimReviewer(Long[] employeeIds) {
		employeeClaimReviewerDAO.deleteByCondition(FormatPreserveCryptoUtil.decrypt(employeeIds[0]),
				FormatPreserveCryptoUtil.decrypt(employeeIds[1]));

	}

	@Override
	public ClaimReviewerForm checkClaimReviewer(Long employeeId, Long companyId, ClaimReviewerForm claimReviewerForm) {

		List<EmployeeClaimReviewer> employeeClaimReviewersList = employeeClaimReviewerDAO.checkExistingClaimReviewer(
				FormatPreserveCryptoUtil.decrypt(claimReviewerForm.getEmployeeId()), companyId,
				FormatPreserveCryptoUtil.decrypt(claimReviewerForm.getEmployeeClaimTemplateId()));

		if (employeeClaimReviewersList != null) {
			claimReviewerForm.setEmployeeStatus("exists");
		} else {
			claimReviewerForm.setEmployeeStatus("notexists");
		}
		return claimReviewerForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#searchEmployee(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public ClaimReviewerForm searchEmployee(PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");
		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		int recordSize = employeeDAO.getCountForCondition(conditionDTO, companyId);

		List<Employee> finalList = employeeDAO.findByCondition(conditionDTO, pageDTO, sortDTO, companyId, employeeId,
				null);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeListFormList.add(employeeForm);
		}

		ClaimReviewerForm response = new ClaimReviewerForm();
		if (pageDTO != null) {

			int pageSize = pageDTO.getPageSize();
			if (pageSize != 0) {
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
		}

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public List<ClaimReviewerForm> getClaimTemplateList(Long companyId, Long employeeId) {

		ClaimReviewerConditionDTO claimReviewerConditionDTO = new ClaimReviewerConditionDTO();
		claimReviewerConditionDTO.setEmployeeId(employeeId);
		claimReviewerConditionDTO.setCompanyId(companyId);

		List<ClaimReviewerForm> claimTemplateList = new ArrayList<>();

		List<EmployeeClaimTemplate> employeeClaimTemplateVOs = employeeClaimTemplateDAO
				.findByCondition(claimReviewerConditionDTO);

		for (EmployeeClaimTemplate employeeClaimTemplate : employeeClaimTemplateVOs) {
			ClaimReviewerForm claimReviewerForm = new ClaimReviewerForm();

			claimReviewerForm.setEmployeeClaimTemplateId(
					FormatPreserveCryptoUtil.encrypt(employeeClaimTemplate.getEmployeeClaimTemplateId()));
			claimReviewerForm.setClaimTemplateId(
					FormatPreserveCryptoUtil.encrypt(employeeClaimTemplate.getClaimTemplate().getClaimTemplateId()));
			try {
				claimReviewerForm.setClaimTemplateName(
						URLEncoder.encode(employeeClaimTemplate.getClaimTemplate().getTemplateName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			claimTemplateList.add(claimReviewerForm);
		}

		return claimTemplateList;
	}

	@Override
	public ClaimReviewerForm importClaimReviewer(ClaimReviewerForm claimReviewerForm, Long companyId) {
		String fileName = claimReviewerForm.getFileUpload().getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		ClaimReviewerForm leaveReviewerExcelDataForm = new ClaimReviewerForm();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			leaveReviewerExcelDataForm = ExcelUtils.getClaimReviewersFromXLS(claimReviewerForm.getFileUpload());
		} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			leaveReviewerExcelDataForm = ExcelUtils.getClaimReviewersFromXLSX(claimReviewerForm.getFileUpload());
		}

		HashMap<String, ClaimReviewersDTO> claimReviewersDTOMap = new HashMap<>();
		ClaimReviewerForm reviewerForm = new ClaimReviewerForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();

		setClaimReviewersImportDataDTO(dataImportLogDTOs, leaveReviewerExcelDataForm, claimReviewersDTOMap, companyId);

		validateImpotedData(dataImportLogDTOs, claimReviewersDTOMap, companyId);

		if (!dataImportLogDTOs.isEmpty()) {
			reviewerForm.setDataValid(false);
			reviewerForm.setDataImportLogDTOs(dataImportLogDTOs);
			return reviewerForm;
		}

		Set<String> keySet = claimReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ClaimReviewersDTO claimReviewersDTO = claimReviewersDTOMap.get(key);

			int ruleValue = Integer.parseInt(claimReviewersDTO.getRuleValue());
			Class<?> c = claimReviewersDTO.getClass();

			try {

				Employee employee1 = employeeDAO.findById(claimReviewersDTO.getEmployeeId());

				EmployeeClaimTemplate employeeClaimTemplate = employeeClaimTemplateDAO.findByEmpIdAndClaimTemplateId(
						claimReviewersDTO.getEmployeeId(), claimReviewersDTO.getClaimTemplateId());

				// Check Existing Claim Reviewers
				List<EmployeeClaimReviewer> employeeClaimReviewerVOList = employeeClaimReviewerDAO.findByCondition(
						claimReviewersDTO.getEmployeeId(), employeeClaimTemplate.getEmployeeClaimTemplateId(),
						companyId);
				if (employeeClaimReviewerVOList != null) {
					employeeClaimReviewerDAO.deleteByCondition(claimReviewersDTO.getEmployeeId(),
							employeeClaimTemplate.getEmployeeClaimTemplateId());
				}

				EmployeeClaimReviewer employeeClaimReviewer = null;
				WorkFlowRuleMaster workFlowRuleReviewer = null;
				Employee employee2 = null;
				String methodNameForReviewer;
				for (int claimReviewerCount = 1; claimReviewerCount <= ruleValue; claimReviewerCount++) {
					methodNameForReviewer = "getReviewer" + claimReviewerCount + "Id";
					Method methodReviewer = c.getMethod(methodNameForReviewer);
					Long employeeReviewerId;

					employeeReviewerId = (Long) methodReviewer.invoke(claimReviewersDTO);

					employee2 = employeeDAO.findById(employeeReviewerId);
					employeeClaimReviewer = new EmployeeClaimReviewer();
					employeeClaimReviewer.setEmployee1(employee1);
					employeeClaimReviewer.setEmployee2(employee2);
					workFlowRuleReviewer = workFlowRuleMasterDAO.findByRuleNameValue(
							PayAsiaConstants.WORK_FLOW_RULE_NAME_CLAIM_REVIEWER, String.valueOf(claimReviewerCount));
					employeeClaimReviewer.setWorkFlowRuleMaster(workFlowRuleReviewer);

					employeeClaimReviewer.setEmployeeClaimTemplate(employeeClaimTemplate);
					employeeClaimReviewerDAO.save(employeeClaimReviewer);

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
		reviewerForm.setDataValid(true);
		return reviewerForm;
	}

	public void setClaimReviewersImportDataDTO(List<DataImportLogDTO> dataImportLogDTOs,
			ClaimReviewerForm claimReviewerExcelFieldForm, HashMap<String, ClaimReviewersDTO> claimReviewersDTOMap,
			Long companyId) {
		for (HashMap<String, String> map : claimReviewerExcelFieldForm.getImportedData()) {
			ClaimReviewersDTO claimReviewersDTO = new ClaimReviewersDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get(PayAsiaConstants.HASH_KEY_ROW_NUMBER);
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				if (key != PayAsiaConstants.HASH_KEY_ROW_NUMBER) {
					if (key.equals(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY)) {
						claimReviewersDTO.setEmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.PAYASIA_CLAIM_TEMPLATE_NAME)) {
						claimReviewersDTO.setClaimTemplate(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_EMPLOYEE_NUMBER)) {
						claimReviewersDTO.setReviewer1EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_COMPANY_CODE)) {
						claimReviewersDTO.setReviewer1CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_EMPLOYEE_NUMBER)) {
						claimReviewersDTO.setReviewer2EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_COMPANY_CODE)) {
						claimReviewersDTO.setReviewer2CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_EMPLOYEE_NUMBER)) {
						claimReviewersDTO.setReviewer3EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_COMPANY_CODE)) {
						claimReviewersDTO.setReviewer3CompanyCode(value);
					}
					claimReviewersDTOMap.put(rowNumber, claimReviewersDTO);
				}
			}
		}
	}

	/**
	 * Purpose: To Validate imported Claim Reviewer.
	 * 
	 * @param claimReviewersDTOMap
	 *            the claimReviewersDataDTOMap
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 */
	private void validateImpotedData(List<DataImportLogDTO> dataImportLogDTOs,
			HashMap<String, ClaimReviewersDTO> claimReviewersDTOMap, Long companyId) {
		List<String> claimTemplateNameList = new ArrayList<>();
		List<Tuple> claimTemplateListVO = claimTemplateDAO.getClaimTemplateNameTupleList(companyId);
		for (Tuple claimTemplateTuple : claimTemplateListVO) {
			String claimTemplate = (String) claimTemplateTuple.get(getAlias(ClaimTemplate_.templateName), String.class);
			claimTemplateNameList.add(claimTemplate.toUpperCase());
		}

		HashMap<String, Long> employeeNumberMap = new HashMap<String, Long>();
		List<Tuple> employeeNameListVO = employeeDAO.getEmployeeNameTupleList(companyId);
		for (Tuple empTuple : employeeNameListVO) {
			String employeeName = (String) empTuple.get(getAlias(Employee_.employeeNumber), String.class);
			Long employeeId = (Long) empTuple.get(getAlias(Employee_.employeeId), Long.class);
			employeeNumberMap.put(employeeName.toUpperCase(), employeeId);
		}

		Set<String> keySet = claimReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ClaimReviewersDTO postLeavetransDTO = claimReviewersDTOMap.get(key);

			String rowNumber = key;
			if (key.equalsIgnoreCase(PayAsiaConstants.HASH_KEY_ROW_NUMBER)) {
				continue;
			}
			if (StringUtils.isBlank(postLeavetransDTO.getEmployeeNumber())) {
				setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
						"payasia.empty", Long.parseLong(rowNumber));
				continue;
			}
			if (StringUtils.isNotBlank(postLeavetransDTO.getEmployeeNumber())) {
				if (!employeeNumberMap.containsKey(postLeavetransDTO.getEmployeeNumber().toUpperCase())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY,
							"payasia.invalid.employee.number", Long.parseLong(rowNumber));
					continue;
				}
				postLeavetransDTO
						.setEmployeeId(employeeNumberMap.get(postLeavetransDTO.getEmployeeNumber().toUpperCase()));
				// Leave Scheme Name
				if (StringUtils.isBlank(postLeavetransDTO.getClaimTemplate())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_CLAIM_TEMPLATE_NAME,
							"payasia.empty", Long.parseLong(rowNumber));
					continue;
				}
				if (!claimTemplateNameList.contains(postLeavetransDTO.getClaimTemplate().toUpperCase())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_CLAIM_TEMPLATE_NAME,
							"payasia.assign.claim.template.not.valid.claim.template.name.error",
							Long.parseLong(rowNumber));
					continue;
				}
				ClaimReviewerConditionDTO claimReviewerConditionDTO = new ClaimReviewerConditionDTO();
				claimReviewerConditionDTO
						.setEmployeeId(employeeNumberMap.get(postLeavetransDTO.getEmployeeNumber().toUpperCase()));
				List<EmployeeClaimTemplate> empClaimTemplateListVO = employeeClaimTemplateDAO
						.findByCondition(claimReviewerConditionDTO);
				if (empClaimTemplateListVO.isEmpty()) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_CLAIM_TEMPLATE_NAME,
							"payasia.employee.claim.import.claim.template.not.assigned.to.employee",
							Long.parseLong(rowNumber));
					continue;
				}
				Long validClaimTemplateId = null;
				validClaimTemplateId = getValidClaimTemplate(companyId, postLeavetransDTO, empClaimTemplateListVO,
						validClaimTemplateId);
				if (validClaimTemplateId == null) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_CLAIM_TEMPLATE_NAME,
							"payasia.employee.claim.import.claim.template.not.assigned.to.employee",
							Long.parseLong(rowNumber));
					continue;
				}
				postLeavetransDTO.setClaimTemplateId(validClaimTemplateId);

				int claimReviewerWorkflowRuleVal = 0;
				ClaimReviewerForm claimReviewerForm = getClaimReviewers(validClaimTemplateId, companyId);
				if (StringUtils.isNotBlank(claimReviewerForm.getRuleValue())) {
					claimReviewerWorkflowRuleVal = Integer.valueOf(claimReviewerForm.getRuleValue());
					postLeavetransDTO.setRuleValue(claimReviewerForm.getRuleValue());
				}
				if (claimReviewerWorkflowRuleVal == 0) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.PAYASIA_CLAIM_REVIEWER,
							"payasia.hris.reviewer.workflow.level.not.configured", Long.parseLong(rowNumber));
				}
				getValidClaimReviewers(dataImportLogDTOs, rowNumber, postLeavetransDTO, claimReviewerWorkflowRuleVal);

			}
			claimReviewersDTOMap.put(key, postLeavetransDTO);
		}
	}

	private void getValidClaimReviewers(List<DataImportLogDTO> dataImportLogDTOs, String key,
			ClaimReviewersDTO postLeavetransDTO, int leaveReviewerWorkflowRuleVal) {
		if (leaveReviewerWorkflowRuleVal == 1 || leaveReviewerWorkflowRuleVal > 1) {
			if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer1EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer1CompanyCode())) {
				Employee reviewer1 = employeeDAO.getEmployeeByEmpNumAndCompCode(
						postLeavetransDTO.getReviewer1EmployeeNumber(), postLeavetransDTO.getReviewer1CompanyCode());
				if (reviewer1 == null) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER1,
							"payasia.invalid.hris.reviewer.1", Long.parseLong(key));
				} else {
					postLeavetransDTO.setReviewer1Id(reviewer1.getEmployeeId());
					if (postLeavetransDTO.getEmployeeNumber()
							.equalsIgnoreCase(postLeavetransDTO.getReviewer1EmployeeNumber())) {
						setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER1,
								"payasia.claim.reviewer.names.cannot.be.same", Long.parseLong(key));
					}
				}
			} else {
				setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER1,
						"payasia.invalid.hris.reviewer.1", Long.parseLong(key));
			}
		}
		if (leaveReviewerWorkflowRuleVal == 2 || leaveReviewerWorkflowRuleVal > 2) {
			if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer2EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer2CompanyCode())) {
				Employee reviewer2 = employeeDAO.getEmployeeByEmpNumAndCompCode(
						postLeavetransDTO.getReviewer2EmployeeNumber(), postLeavetransDTO.getReviewer2CompanyCode());
				if (reviewer2 == null) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER2,
							"payasia.invalid.hris.reviewer.2", Long.parseLong(key));
				} else {
					postLeavetransDTO.setReviewer2Id(reviewer2.getEmployeeId());
				}
			} else {
				setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER2,
						"payasia.invalid.hris.reviewer.2", Long.parseLong(key));
			}
		}
		if (leaveReviewerWorkflowRuleVal == 3 || leaveReviewerWorkflowRuleVal > 3) {
			if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer3EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer3CompanyCode())) {
				Employee reviewer3 = employeeDAO.getEmployeeByEmpNumAndCompCode(
						postLeavetransDTO.getReviewer3EmployeeNumber(), postLeavetransDTO.getReviewer3CompanyCode());
				if (reviewer3 == null) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER3,
							"payasia.invalid.hris.reviewer.3", Long.parseLong(key));
				} else {
					postLeavetransDTO.setReviewer3Id(reviewer3.getEmployeeId());
				}
			} else {
				setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER3,
						"payasia.invalid.hris.reviewer.3", Long.parseLong(key));
			}
		}
		if (leaveReviewerWorkflowRuleVal == 2 || leaveReviewerWorkflowRuleVal > 2) {
			if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer2EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer2CompanyCode())) {
				if (postLeavetransDTO.getEmployeeNumber()
						.equalsIgnoreCase(postLeavetransDTO.getReviewer2EmployeeNumber())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER2,
							"payasia.claim.reviewer.names.cannot.be.same", Long.parseLong(key));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer1EmployeeNumber())
						&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer1CompanyCode())) {
					if (postLeavetransDTO.getReviewer1EmployeeNumber()
							.equalsIgnoreCase(postLeavetransDTO.getReviewer2EmployeeNumber())) {
						setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER1,
								"payasia.claim.reviewer.names.cannot.be.same", Long.parseLong(key));
					}
				}
			}
		}
		if (leaveReviewerWorkflowRuleVal == 3 || leaveReviewerWorkflowRuleVal > 3) {
			if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer3EmployeeNumber())
					&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer3CompanyCode())) {
				if (postLeavetransDTO.getEmployeeNumber()
						.equalsIgnoreCase(postLeavetransDTO.getReviewer3EmployeeNumber())) {
					setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER3,
							"payasia.claim.reviewer.names.cannot.be.same", Long.parseLong(key));
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer1EmployeeNumber())
						&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer1CompanyCode())) {
					if (postLeavetransDTO.getReviewer1EmployeeNumber()
							.equalsIgnoreCase(postLeavetransDTO.getReviewer3EmployeeNumber())) {
						setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER1,
								"payasia.claim.reviewer.names.cannot.be.same", Long.parseLong(key));
					}
				}
				if (StringUtils.isNotBlank(postLeavetransDTO.getReviewer2EmployeeNumber())
						&& StringUtils.isNotBlank(postLeavetransDTO.getReviewer2CompanyCode())) {
					if (postLeavetransDTO.getReviewer2EmployeeNumber()
							.equalsIgnoreCase(postLeavetransDTO.getReviewer3EmployeeNumber())) {
						setClaimReviewersDataImportLogs(dataImportLogDTOs, PayAsiaConstants.REVIEWER2,
								"payasia.claim.reviewer.names.cannot.be.same", Long.parseLong(key));
					}
				}
			}
		}
	}

	private Long getValidClaimTemplate(Long companyId, ClaimReviewersDTO claimReviewersDTO,
			List<EmployeeClaimTemplate> empLClaimTemplateListVO, Long validClaimTemplateId) {
		for (EmployeeClaimTemplate empClaimTemplate : empLClaimTemplateListVO) {
			if (empClaimTemplate.getClaimTemplate().getTemplateName()
					.equalsIgnoreCase(claimReviewersDTO.getClaimTemplate())) {
				validClaimTemplateId = empClaimTemplate.getClaimTemplate().getClaimTemplateId();
			}
		}
		return validClaimTemplateId;
	}

	public void setClaimReviewersDataImportLogs(List<DataImportLogDTO> dataImportLogDTOs, String key, String remarks,
			Long rowNumber) {
		DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
		dataImportLogDTO.setColName(key);
		dataImportLogDTO.setRemarks(remarks);
		dataImportLogDTO.setRowNumber(rowNumber + 1);
		dataImportLogDTOs.add(dataImportLogDTO);
	}

	@Override
	public LeaveReviewerResponseForm searchEmployeeByCompany(PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}

		List<Employee> finalList = employeeDAO.findByCondition(conditionDTO, pageDTO, sortDTO, companyId, employeeId,
				null);

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			/* ID ENCRYPT */
			employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeListFormList.add(employeeForm);
		}

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

}
