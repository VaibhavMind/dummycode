package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.HRISReviewerConditionDTO;
import com.payasia.common.dto.HRISReviewersDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.HRISReviewerForm;
import com.payasia.common.form.HRISReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeHRISReviewerDAO;
import com.payasia.dao.HRISChangeWorkflowDAO;
import com.payasia.dao.HRISReviewerDetailViewDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeHRISReviewer;
import com.payasia.dao.bean.HRISChangeWorkflow;
import com.payasia.dao.bean.HRISReviewerDetailView;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.HRISReviewerLogic;

@Component
public class HRISReviewerLogicImpl implements HRISReviewerLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HRISReviewerLogicImpl.class);
	@Resource
	HRISChangeWorkflowDAO hrisChangeWorkflowDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	EmployeeHRISReviewerDAO employeeHRISReviewerDAO;
	@Resource
	HRISReviewerDetailViewDAO hrisReviewerDetailViewDAO;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	CompanyDAO companyDAO;

	@Override
	public HRISReviewerForm getHRISWorkFlow(Long companyId) {
		int hrisRuleVal = 0;

		HRISReviewerForm hrisReviewerForm = new HRISReviewerForm();

		List<HRISChangeWorkflow> hrisChangeWorkflowList = hrisChangeWorkflowDAO
				.findByCompanyId(companyId);
		if (!hrisChangeWorkflowList.isEmpty()) {
			for (HRISChangeWorkflow hrisChangeWorkflow : hrisChangeWorkflowList) {
				if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
						.equalsIgnoreCase(PayAsiaConstants.REVIEWER_RULE)) {
					hrisRuleVal = Integer.parseInt(hrisChangeWorkflow
							.getWorkFlowRuleMaster().getRuleValue());
				}
			}
		}

		hrisReviewerForm.setRuleValue(String.valueOf(hrisRuleVal));
		return hrisReviewerForm;

	}

	@Override
	public List<HRISReviewerForm> getWorkFlowRuleList() {

		List<HRISReviewerForm> workFlowRuleList = new ArrayList<HRISReviewerForm>();
		List<WorkFlowRuleMaster> workFlowRuleListVO = workFlowRuleMasterDAO
				.findByRuleName(PayAsiaConstants.WORK_FLOW_RULE_NAME_HRIS_REVIEWER);

		for (WorkFlowRuleMaster workFlowRuleMasterVO : workFlowRuleListVO) {
			HRISReviewerForm hrisReviewerForm = new HRISReviewerForm();
			hrisReviewerForm.setHrisReviewerRuleId(workFlowRuleMasterVO
					.getWorkFlowRuleId());
			workFlowRuleList.add(hrisReviewerForm);

		}

		return workFlowRuleList;

	}

	@Override
	public HRISReviewerResponseForm getHRISReviewers(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId) {

		if (sortDTO.getColumnName().equals("")) {
			sortDTO.setColumnName(SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME);
			sortDTO.setOrderType(PayAsiaConstants.ASC);
		}

		List<HRISReviewerDetailView> hrisReviewerListVO;

		HRISReviewerConditionDTO conditionDTO = new HRISReviewerConditionDTO();
		if (searchCondition
				.equals(PayAsiaConstants.LEAVE_REVIEWER_EMPLOYEE_NAME)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setEmployeeName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		if (searchCondition.equals(PayAsiaConstants.HRIS_EMPLOYEE_NUMBER)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setEmployeeNumber("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}

			}

		}

		if (searchCondition.equals(PayAsiaConstants.HRIS_REVIEWER_REVIEWER1)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setHrisReviewer1("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HRIS_REVIEWER_REVIEWER2)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setHrisReviewer2("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.HRIS_REVIEWER_REVIEWER3)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setHrisReviewer3("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		int recordSize = 0;

		hrisReviewerListVO = hrisReviewerDetailViewDAO.findByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);
		List<HRISReviewerForm> hrisReviewerFormList = new ArrayList<HRISReviewerForm>();
		for (HRISReviewerDetailView hrisReviewerVO : hrisReviewerListVO) {
			HRISReviewerForm hrisReviewerForm = new HRISReviewerForm();
			/*ID ENCRYPT*/
			hrisReviewerForm.setEmployeeId(FormatPreserveCryptoUtil.encrypt(hrisReviewerVO
					.getHrisReviewerDetailViewPK().getEmployeeId()));

			String empName = hrisReviewerVO.getEmpFirstName() + " ";
			if (StringUtils.isNotBlank(hrisReviewerVO.getEmpLastName())) {
				empName += hrisReviewerVO.getEmpLastName();
			}
			hrisReviewerForm.setEmployeeName(empName + " ("
					+ hrisReviewerVO.getEmpEmployeeNumber() + ")");

			
			hrisReviewerForm
					.setHrisReviewerId1(hrisReviewerVO.getReviewer1Id());
			String LeaveReviewer1Name = hrisReviewerVO.getReviewer1FirstName()
					+ " ";
			if (StringUtils.isNotBlank(hrisReviewerVO.getReviewer1LastName())) {
				LeaveReviewer1Name += hrisReviewerVO.getReviewer1LastName();
			}
			if (StringUtils.isNotBlank(hrisReviewerVO.getReviewer1FirstName())) {
				hrisReviewerForm.setHrisReviewer1(LeaveReviewer1Name);
			} else {
				hrisReviewerForm.setHrisReviewer1("");
			}

			hrisReviewerForm
					.setHrisReviewerId2(hrisReviewerVO.getReviewer2Id());

			String LeaveReviewer2Name = hrisReviewerVO.getReviewer2FirstName()
					+ " ";
			if (StringUtils.isNotBlank(hrisReviewerVO.getReviewer2LastName())) {
				LeaveReviewer2Name += hrisReviewerVO.getReviewer2LastName();
			}
			if (StringUtils.isNotBlank(hrisReviewerVO.getReviewer2FirstName())) {
				hrisReviewerForm.setHrisReviewer2(LeaveReviewer2Name);
			} else {
				hrisReviewerForm.setHrisReviewer2("");
			}

			hrisReviewerForm
					.setHrisReviewerId3(hrisReviewerVO.getReviewer3Id());
			String LeaveReviewer3Name = hrisReviewerVO.getReviewer3FirstName()
					+ " ";
			if (StringUtils.isNotBlank(hrisReviewerVO.getReviewer3LastName())) {
				LeaveReviewer3Name += hrisReviewerVO.getReviewer3LastName();
			}
			if (StringUtils.isNotBlank(hrisReviewerVO.getReviewer3FirstName())) {
				hrisReviewerForm.setHrisReviewer3(LeaveReviewer3Name);
			} else {
				hrisReviewerForm.setHrisReviewer3("");
			}

			hrisReviewerFormList.add(hrisReviewerForm);
		}
		recordSize = (hrisReviewerDetailViewDAO.getCountByCondition(
				conditionDTO, companyId));

		HRISReviewerResponseForm response = new HRISReviewerResponseForm();
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
		response.setRows(hrisReviewerFormList);
		response.setRecords(recordSize);
		return response;

	}

	@Override
	public HRISReviewerResponseForm getHRISReviewerData(Long employeeId,
			Long companyId) {
		HRISReviewerResponseForm hrisReviewerFormResponse = new HRISReviewerResponseForm();
		List<HRISReviewerForm> employeeHRISReviewerList = new ArrayList<HRISReviewerForm>();

		List<EmployeeHRISReviewer> employeeHRISReviewerListVO = employeeHRISReviewerDAO
				.findByCondition(employeeId, companyId);

		for (EmployeeHRISReviewer employeeHRISReviewerVO : employeeHRISReviewerListVO) {

			HRISReviewerForm hrisReviewerForm = new HRISReviewerForm();

			hrisReviewerForm.setEmployeeId(employeeHRISReviewerVO.getEmployee()
					.getEmployeeId());

			String employeeName1 = employeeHRISReviewerVO.getEmployee()
					.getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeHRISReviewerVO.getEmployee()
					.getLastName())) {
				employeeName1 += employeeHRISReviewerVO.getEmployee()
						.getLastName();
			}
			employeeName1 += "["
					+ employeeHRISReviewerVO.getEmployee().getEmployeeNumber()
					+ "]";
			hrisReviewerForm.setEmployeeName(employeeName1);
			hrisReviewerForm.setHrisReviewerId(employeeHRISReviewerVO
					.getEmployeeReviewer().getEmployeeId());

			String employeeName2 = employeeHRISReviewerVO.getEmployeeReviewer()
					.getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeHRISReviewerVO
					.getEmployeeReviewer().getLastName())) {
				employeeName2 += employeeHRISReviewerVO.getEmployeeReviewer()
						.getLastName();
			}
			employeeName2 += "["
					+ employeeHRISReviewerVO.getEmployeeReviewer()
							.getEmployeeNumber() + "]";
			hrisReviewerForm.setHrisReviewerName(employeeName2);
			hrisReviewerForm.setEmployeeHRISReviewerId(employeeHRISReviewerVO
					.getEmployeeHRISReviewerId());
			employeeHRISReviewerList.add(hrisReviewerForm);

		}
		List<HRISChangeWorkflow> workFlowRuleMasterList = hrisChangeWorkflowDAO
				.findByCompanyId(companyId);
		for (HRISChangeWorkflow hrisChangeWorkflow : workFlowRuleMasterList) {
			if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
				hrisReviewerFormResponse.setRuleValue(hrisChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
			}
		}

		hrisReviewerFormResponse.setRows(employeeHRISReviewerList);

		return hrisReviewerFormResponse;
	}

	@Override
	public HRISReviewerForm checkHRISReviewer(Long employeeId, Long companyId) {
		HRISReviewerForm hrisReviewerForm = new HRISReviewerForm();
		List<EmployeeHRISReviewer> employeeHRISReviewersList = employeeHRISReviewerDAO
				.findByCondition(employeeId, companyId);
		if (!employeeHRISReviewersList.isEmpty()) {
			hrisReviewerForm.setEmployeeStatus("exists");

		} else {
			hrisReviewerForm.setEmployeeStatus("notexists");
		}

		return hrisReviewerForm;
	}

	@Override
	public void saveHRISReviewer(HRISReviewerForm hrisReviewerForm,
			Long companyId) {
		int ruleValue = Integer.parseInt(hrisReviewerForm.getRuleValue());
		Class<?> c = hrisReviewerForm.getClass();

		try {

			Employee employee1 = employeeDAO.findById(hrisReviewerForm
					.getEmployeeId());

			EmployeeHRISReviewer employeeHRISReviewer = null;
			WorkFlowRuleMaster workFlowRuleReviewer1 = null;
			Employee employee2 = null;
			String methodNameForReviewer;
			String methodNameForWorkFlow;
			for (int hrisReviewerCount = 1; hrisReviewerCount <= ruleValue; hrisReviewerCount++) {
				methodNameForReviewer = "getHrisReviewerId" + hrisReviewerCount;
				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Long employeeReviewerId;

				employeeReviewerId = (Long) methodReviewer
						.invoke(hrisReviewerForm);

				methodNameForWorkFlow = "getHrisReviewerRuleId"
						+ hrisReviewerCount;
				Method methodWorkflow = c.getMethod(methodNameForWorkFlow);
				Long workFlowId = (Long) methodWorkflow
						.invoke(hrisReviewerForm);

				employee2 = employeeDAO.findById(employeeReviewerId);
				employeeHRISReviewer = new EmployeeHRISReviewer();
				employeeHRISReviewer.setEmployee(employee1);
				employeeHRISReviewer.setEmployeeReviewer(employee2);
				workFlowRuleReviewer1 = workFlowRuleMasterDAO
						.findByID(workFlowId);
				employeeHRISReviewer
						.setWorkFlowRuleMaster(workFlowRuleReviewer1);
				employeeHRISReviewerDAO.save(employeeHRISReviewer);

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
	public void deleteHRISReviewer(Long employeeId,Long companyId) {
		employeeHRISReviewerDAO.deleteByCondition(employeeId,companyId);

	}

	@Override
	public HRISReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		Company company = companyDAO.findById(companyId);
		List<Long> companyIds = companyDAO.getDistinctAssignedGroupCompanies(
				employeeId, company.getCompanyGroup().getGroupId());
		int recordSize = 0;

		for (Long assignedCompanyId : companyIds) {

			EmployeeShortListDTO employeeShortListDTO = generalLogic
					.getShortListEmployeeIds(employeeId, assignedCompanyId);
			conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

			if (StringUtils.isNotBlank(empName)) {
				try {
					empName = URLDecoder.decode(empName, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
				conditionDTO.setEmployeeName("%" + empName.trim() + "%");
			}

			if (StringUtils.isNotBlank(empNumber)) {
				try {
					empNumber = URLDecoder.decode(empNumber, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
				conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

			}
			recordSize += employeeDAO.getCountForCondition(conditionDTO,
					assignedCompanyId);

			List<Employee> finalList = employeeDAO.findByCondition(
					conditionDTO, pageDTO, sortDTO, assignedCompanyId);

			for (Employee employee : finalList) {
				EmployeeListForm employeeForm = new EmployeeListForm();

				String employeeName = "";
				employeeName += employee.getFirstName() + " ";
				if (StringUtils.isNotBlank(employee.getLastName())) {
					employeeName += employee.getLastName();
				}
				employeeForm.setEmployeeName(employeeName);

				employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

				employeeForm.setEmployeeID(employee.getEmployeeId());
				employeeForm.setCompanyName(employee.getCompany()
						.getCompanyName());
				employeeListFormList.add(employeeForm);
			}

		}

		HRISReviewerResponseForm response = new HRISReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public void updateHRISReviewer(HRISReviewerForm hrisReviewerForm,
			Long companyId) {

		int ruleValue = Integer.parseInt(hrisReviewerForm.getRuleValue());
		Class<?> c = hrisReviewerForm.getClass();

		try {

			EmployeeHRISReviewer employeeHrisReviewer = null;
			Employee employee1 = null;
			Employee employee2 = null;

			String methodNameForReviewer;
			String methodNameForHrisReviewer;
			String methodNameForHrisReviewerRule;

			for (int hrisReviewerCount = 1; hrisReviewerCount <= ruleValue; hrisReviewerCount++) {

				methodNameForReviewer = "getHrisReviewerId" + hrisReviewerCount;
				methodNameForHrisReviewer = "getEmployeeHRISReviewerId"
						+ hrisReviewerCount;
				methodNameForHrisReviewerRule = "getHrisReviewerRuleId"
						+ hrisReviewerCount;

				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Method methodEmployeeHRISReviewer = c
						.getMethod(methodNameForHrisReviewer);
				Method methodEmployeeHRISReviewerRule = c
						.getMethod(methodNameForHrisReviewerRule);

				Long employeeReviewerId;
				Long employeeHRISReviewerId;
				Long employeeHRISReviewerRuleId;

				employeeHRISReviewerId = (Long) methodEmployeeHRISReviewer
						.invoke(hrisReviewerForm);

				employeeReviewerId = (Long) methodReviewer
						.invoke(hrisReviewerForm);

				employeeHRISReviewerRuleId = (Long) methodEmployeeHRISReviewerRule
						.invoke(hrisReviewerForm);

				if (employeeHRISReviewerId != null) {
					employeeHrisReviewer = employeeHRISReviewerDAO
							.findById(employeeHRISReviewerId);
					if (employeeReviewerId != null) {
						employee2 = employeeDAO.findById(employeeReviewerId);

						employeeHrisReviewer.setEmployeeReviewer(employee2);

						employeeHRISReviewerDAO.update(employeeHrisReviewer);
					}
					if (employeeReviewerId == null) {
						if (employeeHrisReviewer != null) {
							employeeHRISReviewerDAO
									.delete(employeeHrisReviewer);
						}
					}
				} else {

					if (employeeReviewerId != null) {
						String[] empIds = hrisReviewerForm.getEmployeeName()
								.split(",");
						EmployeeHRISReviewer empHrisReviewer = new EmployeeHRISReviewer();
						employee1 = employeeDAO.findById(Long
								.parseLong(empIds[0]));
						employee2 = employeeDAO.findById(employeeReviewerId);
						empHrisReviewer.setEmployee(employee1);
						empHrisReviewer.setEmployeeReviewer(employee2);
						WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
								.findByID(employeeHRISReviewerRuleId);
						empHrisReviewer
								.setWorkFlowRuleMaster(workFlowRuleMaster);
						employeeHRISReviewerDAO.save(empHrisReviewer);
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
	public HRISReviewerResponseForm searchEmployeeBySessionCompany(
			PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		int recordSize = 0;

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

		if (StringUtils.isNotBlank(empName)) {
			try {
				empName = URLDecoder.decode(empName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			try {
				empNumber = URLDecoder.decode(empNumber, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}

		List<Employee> finalList = employeeDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();

			String employeeName = "";
			employeeName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				employeeName += employee.getLastName();
			}
			employeeForm.setEmployeeName(employeeName);

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());

			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeListFormList.add(employeeForm);
		}

		HRISReviewerResponseForm response = new HRISReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public List<EmployeeListForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(
				searchString.trim(), companyId, employeeShortListDTO);
		for (Employee employee : employeeNumberList) {
			EmployeeListForm employeeListForm = new EmployeeListForm();

			try {
				employeeListForm.setEmployeeNumber(URLEncoder.encode(
						employee.getEmployeeNumber(), "UTF-8"));

				String employeeName = URLEncoder.encode(
						employee.getFirstName(), "UTF-8");

				if (employee.getLastName() != null) {
					employeeName = employeeName
							+ " "
							+ URLEncoder
									.encode(employee.getLastName(), "UTF-8");
				}
				employeeListForm.setEmployeeName(employeeName);

			} catch (UnsupportedEncodingException unsupportedEncodingException) {
				LOGGER.error(unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
				throw new PayAsiaSystemException(
						unsupportedEncodingException.getMessage(),
						unsupportedEncodingException);
			}

			employeeListForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeListForm);

		}
		return employeeListFormList;
	}

	@Override
	public List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<EmployeeListForm> leaveBalanceSummaryFormList = new ArrayList<EmployeeListForm>();
		Company companyVO = companyDAO.findById(companyId);
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		List<Employee> employeeNumberList = employeeDAO
				.getEmployeeIdsForGroupCompany(searchString.trim(), companyId,
						companyVO.getCompanyGroup().getGroupId(),
						employeeShortListDTO);

		for (Employee employee : employeeNumberList) {
			EmployeeListForm employeeListForm = new EmployeeListForm();
			String empName = "";
			empName += employee.getFirstName() + " ";
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empName += employee.getLastName();
			}

			try {
				employeeListForm.setEmployeeName(URLEncoder.encode(empName,
						"UTF-8"));
				employeeListForm.setEmployeeNumber(URLEncoder.encode(
						employee.getEmployeeNumber(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			employeeListForm.setEmployeeID(employee.getEmployeeId());
			leaveBalanceSummaryFormList.add(employeeListForm);

		}
		return leaveBalanceSummaryFormList;
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
	public String configureHRISRevWorkflow(Long companyId,
			HRISReviewerForm hrisReviewerForm) {

		Company company = companyDAO.findById(companyId);

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO
				.findAll();

		List<HRISChangeWorkflow> changeWorkflowList = hrisChangeWorkflowDAO
				.findByCompanyId(companyId);
		if (!changeWorkflowList.isEmpty()) {
			for (HRISChangeWorkflow hrisChangeWorkflow : changeWorkflowList) {
				hrisChangeWorkflowDAO.deleteByCondition(hrisChangeWorkflow
						.getHrisChangeWorkflowId());
			}
		}

		if (hrisReviewerForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.HRIS_DEF_WORKFLOWL1)) {
			Long workFlowId = getWorkFlowMasterId(
					PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "1",
					workFlowRuleMasterList);
			HRISChangeWorkflow hrisChangeWorkflow = new HRISChangeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
					.findByID(workFlowId);
			hrisChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			hrisChangeWorkflow.setCompany(company);
			hrisChangeWorkflowDAO.save(hrisChangeWorkflow);
		}
		if (hrisReviewerForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.HRIS_DEF_WORKFLOWL2)) {
			Long workFlowId = getWorkFlowMasterId(
					PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "2",
					workFlowRuleMasterList);
			HRISChangeWorkflow hrisChangeWorkflow = new HRISChangeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
					.findByID(workFlowId);
			hrisChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			hrisChangeWorkflow.setCompany(company);
			hrisChangeWorkflowDAO.save(hrisChangeWorkflow);
		}
		if (hrisReviewerForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.HRIS_DEF_WORKFLOWL3)) {
			Long workFlowId = getWorkFlowMasterId(
					PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "3",
					workFlowRuleMasterList);
			HRISChangeWorkflow hrisChangeWorkflow = new HRISChangeWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
					.findByID(workFlowId);
			hrisChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			hrisChangeWorkflow.setCompany(company);
			hrisChangeWorkflowDAO.save(hrisChangeWorkflow);
		}

		String allowOverrideStr = "";
		if (hrisReviewerForm.getAllowOverrideL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (hrisReviewerForm.getAllowOverrideL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (hrisReviewerForm.getAllowOverrideL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		Long allowOverrideId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		HRISChangeWorkflow hrisChangeWorkflow = new HRISChangeWorkflow();

		WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
				.findByID(allowOverrideId);
		hrisChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		hrisChangeWorkflow.setCompany(company);
		hrisChangeWorkflowDAO.save(hrisChangeWorkflow);

		String allowRejectStr = "";
		if (hrisReviewerForm.getAllowRejectL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (hrisReviewerForm.getAllowRejectL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (hrisReviewerForm.getAllowRejectL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		Long allowRejectId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_REJECT, allowRejectStr.trim(),
				workFlowRuleMasterList);
		HRISChangeWorkflow hrisChangeWorkflowAllowReject = new HRISChangeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = workFlowRuleMasterDAO
				.findByID(allowRejectId);
		hrisChangeWorkflowAllowReject
				.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		hrisChangeWorkflowAllowReject.setCompany(company);
		hrisChangeWorkflowDAO.save(hrisChangeWorkflowAllowReject);

		String allowForwardStr = "";
		if (hrisReviewerForm.getAllowForward1() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (hrisReviewerForm.getAllowForward2() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (hrisReviewerForm.getAllowForward3() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		Long allowForwardId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_FORWARD,
				allowForwardStr.trim(), workFlowRuleMasterList);
		HRISChangeWorkflow hrisWorkflowWorkflowAllowForward = new HRISChangeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowForward = workFlowRuleMasterDAO
				.findByID(allowForwardId);
		hrisWorkflowWorkflowAllowForward
				.setWorkFlowRuleMaster(workFlowRuleAllowForward);
		hrisWorkflowWorkflowAllowForward.setCompany(company);
		hrisChangeWorkflowDAO.save(hrisWorkflowWorkflowAllowForward);

		String allowApproveStr = "";
		if (hrisReviewerForm.getAllowApprove1() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (hrisReviewerForm.getAllowApprove2() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (hrisReviewerForm.getAllowApprove3() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		Long allowApproveId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_APPROVE,
				allowApproveStr.trim(), workFlowRuleMasterList);
		HRISChangeWorkflow hrisWorkflowAllowApprove = new HRISChangeWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowApprove = workFlowRuleMasterDAO
				.findByID(allowApproveId);
		hrisWorkflowAllowApprove
				.setWorkFlowRuleMaster(workFlowRuleAllowApprove);
		hrisWorkflowAllowApprove.setCompany(company);
		hrisChangeWorkflowDAO.save(hrisWorkflowAllowApprove);

		return "notavailable";

	}

	@Override
	public HRISReviewerForm getHRISWorkFlowLevel(Long companyId) {
		HRISReviewerForm hrisReviewerForm = new HRISReviewerForm();

		List<HRISChangeWorkflow> changeWorkflowList = hrisChangeWorkflowDAO
				.findByCompanyId(companyId);

		for (HRISChangeWorkflow hrisChangeWorkflow : changeWorkflowList) {

			if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
				hrisReviewerForm.setWorkFlowLevel(hrisChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
			}

			if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
				String allowOverRideVal = hrisChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowOverRideVal.charAt(0);
				char atlevelTwo = allowOverRideVal.charAt(1);
				char atlevelThree = allowOverRideVal.charAt(2);

				if (atlevelOne == '0') {
					hrisReviewerForm.setAllowOverrideL1(false);
				}
				if (atlevelOne == '1') {
					hrisReviewerForm.setAllowOverrideL1(true);
				}
				if (atlevelTwo == '0') {
					hrisReviewerForm.setAllowOverrideL2(false);
				}
				if (atlevelTwo == '1') {
					hrisReviewerForm.setAllowOverrideL2(true);
				}
				if (atlevelThree == '0') {
					hrisReviewerForm.setAllowOverrideL3(false);
				}
				if (atlevelThree == '1') {
					hrisReviewerForm.setAllowOverrideL3(true);
				}
			}

			if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
				String allowRejectVal = hrisChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowRejectVal.charAt(0);
				char atlevelTwo = allowRejectVal.charAt(1);
				char atlevelThree = allowRejectVal.charAt(2);

				if (atlevelOne == '0') {
					hrisReviewerForm.setAllowRejectL1(false);
				}
				if (atlevelOne == '1') {
					hrisReviewerForm.setAllowRejectL1(true);
				}
				if (atlevelTwo == '0') {
					hrisReviewerForm.setAllowRejectL2(false);
				}
				if (atlevelTwo == '1') {
					hrisReviewerForm.setAllowRejectL2(true);
				}
				if (atlevelThree == '0') {
					hrisReviewerForm.setAllowRejectL3(false);
				}
				if (atlevelThree == '1') {
					hrisReviewerForm.setAllowRejectL3(true);
				}
			}
			if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.HRIS_DEF_ALLOW_FORWARD)) {
				String allowForwardVal = hrisChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowForwardVal.charAt(0);
				char atlevelTwo = allowForwardVal.charAt(1);
				char atlevelThree = allowForwardVal.charAt(2);

				if (atlevelOne == '0') {
					hrisReviewerForm.setAllowForward1(false);
				}
				if (atlevelOne == '1') {
					hrisReviewerForm.setAllowForward1(true);
				}
				if (atlevelTwo == '0') {
					hrisReviewerForm.setAllowForward2(false);
				}
				if (atlevelTwo == '1') {
					hrisReviewerForm.setAllowForward2(true);
				}
				if (atlevelThree == '0') {
					hrisReviewerForm.setAllowForward3(false);
				}
				if (atlevelThree == '1') {
					hrisReviewerForm.setAllowForward3(true);
				}
			}
			if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.HRIS_DEF_ALLOW_APPROVE)) {
				String allowApproveVal = hrisChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowApproveVal.charAt(0);
				char atlevelTwo = allowApproveVal.charAt(1);
				char atlevelThree = allowApproveVal.charAt(2);

				if (atlevelOne == '0') {
					hrisReviewerForm.setAllowApprove1(false);
				}
				if (atlevelOne == '1') {
					hrisReviewerForm.setAllowApprove1(true);
				}
				if (atlevelTwo == '0') {
					hrisReviewerForm.setAllowApprove2(false);
				}
				if (atlevelTwo == '1') {
					hrisReviewerForm.setAllowApprove2(true);
				}
				if (atlevelThree == '0') {
					hrisReviewerForm.setAllowApprove3(false);
				}
				if (atlevelThree == '1') {
					hrisReviewerForm.setAllowApprove3(true);
				}
			}
		}
		return hrisReviewerForm;
	}

	@Override
	public HRISReviewerForm importHRISReviewer(
			HRISReviewerForm hrisReviewerform, Long companyId) {
		Company companyVO = companyDAO.findById(companyId);
		String fileName = hrisReviewerform.getFileUpload()
				.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		HRISReviewerForm hrisRevList = new HRISReviewerForm();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			hrisRevList = ExcelUtils.getHRISReviewersFromXLS(hrisReviewerform
					.getFileUpload());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			hrisRevList = ExcelUtils.getHRISReviewersFromXLSX(hrisReviewerform
					.getFileUpload());
		}
		HRISReviewerForm reviewerForm = new HRISReviewerForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();

		int hrisRuleVal = 0;
		List<HRISChangeWorkflow> hrisChangeWorkflowList = hrisChangeWorkflowDAO
				.findByCompanyId(companyId);
		if (!hrisChangeWorkflowList.isEmpty()) {
			for (HRISChangeWorkflow hrisChangeWorkflow : hrisChangeWorkflowList) {
				if (hrisChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
						.equalsIgnoreCase(PayAsiaConstants.REVIEWER_RULE)) {
					hrisRuleVal = Integer.parseInt(hrisChangeWorkflow
							.getWorkFlowRuleMaster().getRuleValue());
				}
			}
		}

		if (hrisRuleVal == 0) {
			reviewerForm.setDataValid(false);

			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			dataImportLogDTO
					.setColName(PayAsiaConstants.PAYASIA_HRIS_WORKFLOW_LEVEL);
			dataImportLogDTO
					.setRemarks("payasia.hris.reviewer.workflow.level.not.configured");
			dataImportLogDTOs.add(dataImportLogDTO);

			reviewerForm.setDataImportLogDTOs(dataImportLogDTOs);
			return reviewerForm;
		}
		HashMap<String, HRISReviewersDTO> hrisReviewersDTOMap = new HashMap<>();
		setHRISReviewersImportDataDTO(dataImportLogDTOs, hrisRevList,
				hrisReviewersDTOMap, companyId);

		validateImportedData(hrisReviewersDTOMap, dataImportLogDTOs, companyId,
				hrisRuleVal);

		if (!dataImportLogDTOs.isEmpty()) {
			reviewerForm.setDataValid(false);
			reviewerForm.setDataImportLogDTOs(dataImportLogDTOs);
			return reviewerForm;
		}

		Set<String> keySet = hrisReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();

			HRISReviewersDTO hrisReviewersDTO = hrisReviewersDTOMap.get(key);
			Class<?> c = hrisReviewersDTO.getClass();
			try {
				if (!key.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {
					Employee employeeVO = employeeDAO.findByNumber(
							hrisReviewersDTO.getEmployeeNumber(), companyId);

					EmployeeHRISReviewer employeeHRISReviewer = null;
					WorkFlowRuleMaster workFlowRuleReviewer = null;
					String methodNameForReviewer;
					for (int hrisReviewerCount = 1; hrisReviewerCount <= hrisRuleVal; hrisReviewerCount++) {
						methodNameForReviewer = "getReviewer"
								+ hrisReviewerCount + "Id";
						Method methodReviewer = c
								.getMethod(methodNameForReviewer);
						Long employeeReviewerId;

						employeeReviewerId = (Long) methodReviewer
								.invoke(hrisReviewersDTO);
						Employee employeeRevVO = employeeDAO
								.findById(employeeReviewerId);
						employeeHRISReviewer = new EmployeeHRISReviewer();
						employeeHRISReviewer.setEmployee(employeeVO);
						employeeHRISReviewer.setEmployeeReviewer(employeeRevVO);
						workFlowRuleReviewer = workFlowRuleMasterDAO
								.findByRuleNameValue(
										PayAsiaConstants.WORK_FLOW_RULE_NAME_HRIS_REVIEWER,
										String.valueOf(hrisReviewerCount));
						employeeHRISReviewer
								.setWorkFlowRuleMaster(workFlowRuleReviewer);

						EmployeeHRISReviewer employeeHRISReviewerVO = employeeHRISReviewerDAO
								.findByWorkFlowCondition(employeeVO
										.getEmployeeId(), workFlowRuleReviewer
										.getWorkFlowRuleId());

						if (employeeHRISReviewerVO == null) {
							employeeHRISReviewerDAO.save(employeeHRISReviewer);
						} else {
							employeeHRISReviewerVO
									.setEmployeeReviewer(employeeRevVO);
							employeeHRISReviewerDAO
									.update(employeeHRISReviewerVO);
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
		reviewerForm.setDataValid(true);
		return reviewerForm;

	}

	public void setHRISReviewersImportDataDTO(
			List<DataImportLogDTO> dataImportLogDTOs,
			HRISReviewerForm hrisReviewerExcelFieldForm,
			HashMap<String, HRISReviewersDTO> hrisReviewersDTOMap,
			Long companyId) {
		for (HashMap<String, String> map : hrisReviewerExcelFieldForm
				.getImportedData()) {
			HRISReviewersDTO hrisReviewersDTO = new HRISReviewersDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get(PayAsiaConstants.HASH_KEY_ROW_NUMBER);
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				if (key != PayAsiaConstants.HASH_KEY_ROW_NUMBER) {
					if (key.equals(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY)) {
						hrisReviewersDTO.setEmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_EMPLOYEE_NUMBER)) {
						hrisReviewersDTO.setReviewer1EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_COMPANY_CODE)) {
						hrisReviewersDTO.setReviewer1CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_EMPLOYEE_NUMBER)) {
						hrisReviewersDTO.setReviewer2EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_COMPANY_CODE)) {
						hrisReviewersDTO.setReviewer2CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_EMPLOYEE_NUMBER)) {
						hrisReviewersDTO.setReviewer3EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_COMPANY_CODE)) {
						hrisReviewersDTO.setReviewer3CompanyCode(value);
					}
					hrisReviewersDTOMap.put(rowNumber, hrisReviewersDTO);
				}
			}
		}
	}

	/**
	 * Purpose: To Validate imported HRIS Reviewer.
	 * 
	 * @param hrisRevList
	 *            the hrisRevList
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 * @param hrisRuleVal
	 */
	private void validateImportedData(
			HashMap<String, HRISReviewersDTO> hrisReviewersDTOMap,
			List<DataImportLogDTO> dataImportLogDTOs, Long companyId,
			int hrisRuleVal) {
		Set<String> keySet = hrisReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			HRISReviewersDTO hrisReviewersDTO = hrisReviewersDTOMap.get(key);
			if (!key.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {

				if (StringUtils
						.isNotBlank(hrisReviewersDTO.getEmployeeNumber())) {
					Employee employeeVO = employeeDAO.findByNumber(
							hrisReviewersDTO.getEmployeeNumber(), companyId);
					if (employeeVO == null) {
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO
								.setColName(PayAsiaConstants.CAMEL_CASE_EMPLOYEE_NUMBER);
						dataImportLogDTO
								.setRemarks("payasia.assign.leave.scheme.not.valid.emp.num.error");
						dataImportLogDTO.setRowNumber(Long.parseLong(key));
						dataImportLogDTOs.add(dataImportLogDTO);
					}

					if (employeeVO != null) {
						if (hrisRuleVal == 1 || hrisRuleVal > 1) {
							if (StringUtils.isNotBlank(hrisReviewersDTO
									.getReviewer1EmployeeNumber())) {
								Employee reviewer1 = employeeDAO
										.getEmployeeByEmpNumAndCompCode(
												hrisReviewersDTO
														.getReviewer1EmployeeNumber(),
												hrisReviewersDTO
														.getReviewer1CompanyCode());
								if (reviewer1 == null) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER1);
									dataImportLogDTO
											.setRemarks("payasia.invalid.hris.reviewer.1");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								} else {
									hrisReviewersDTO.setReviewer1Id(reviewer1
											.getEmployeeId());
									if (employeeVO
											.getEmployeeNumber()
											.equalsIgnoreCase(
													hrisReviewersDTO
															.getReviewer1EmployeeNumber())) {
										DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
										dataImportLogDTO
												.setColName(PayAsiaConstants.REVIEWER1);
										dataImportLogDTO
												.setRemarks("payasia.hris.reviewers.employee.name.cannot.same");
										dataImportLogDTO.setRowNumber(Long
												.parseLong(key));
										dataImportLogDTOs.add(dataImportLogDTO);
									}

								}
							} else {
								DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
								dataImportLogDTO
										.setColName(PayAsiaConstants.REVIEWER1);
								dataImportLogDTO
										.setRemarks("payasia.invalid.hris.reviewer.1");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}

						}
						if (hrisRuleVal == 2 || hrisRuleVal > 2) {

							if (StringUtils.isNotBlank(hrisReviewersDTO
									.getReviewer2EmployeeNumber())) {
								Employee reviewer2 = employeeDAO
										.getEmployeeByEmpNumAndCompCode(
												hrisReviewersDTO
														.getReviewer2EmployeeNumber(),
												hrisReviewersDTO
														.getReviewer2CompanyCode());
								if (reviewer2 == null) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER2);
									dataImportLogDTO
											.setRemarks("payasia.invalid.hris.reviewer.2");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								} else {
									hrisReviewersDTO.setReviewer2Id(reviewer2
											.getEmployeeId());
									if (employeeVO
											.getEmployeeNumber()
											.equalsIgnoreCase(
													hrisReviewersDTO
															.getReviewer2EmployeeNumber())) {
										DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
										dataImportLogDTO
												.setColName(PayAsiaConstants.REVIEWER2);
										dataImportLogDTO
												.setRemarks("payasia.hris.reviewers.employee.name.cannot.same");
										dataImportLogDTO.setRowNumber(Long
												.parseLong(key));
										dataImportLogDTOs.add(dataImportLogDTO);
									}

								}
							} else {
								DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
								dataImportLogDTO
										.setColName(PayAsiaConstants.REVIEWER2);
								dataImportLogDTO
										.setRemarks("payasia.invalid.hris.reviewer.2");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}

						}
						if (hrisRuleVal == 3 || hrisRuleVal > 3) {

							if (StringUtils.isNotBlank(hrisReviewersDTO
									.getReviewer3EmployeeNumber())) {
								Employee reviewer3 = employeeDAO
										.getEmployeeByEmpNumAndCompCode(
												hrisReviewersDTO
														.getReviewer3EmployeeNumber(),
												hrisReviewersDTO
														.getReviewer3CompanyCode());
								if (reviewer3 == null) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER3);
									dataImportLogDTO
											.setRemarks("payasia.invalid.hris.reviewer.3");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								} else {
									hrisReviewersDTO.setReviewer3Id(reviewer3
											.getEmployeeId());
									if (employeeVO
											.getEmployeeNumber()
											.equalsIgnoreCase(
													hrisReviewersDTO
															.getReviewer3EmployeeNumber())) {
										DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
										dataImportLogDTO
												.setColName(PayAsiaConstants.REVIEWER3);
										dataImportLogDTO
												.setRemarks("payasia.hris.reviewers.employee.name.cannot.same");
										dataImportLogDTO.setRowNumber(Long
												.parseLong(key));
										dataImportLogDTOs.add(dataImportLogDTO);
									}

								}
							} else {
								DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
								dataImportLogDTO
										.setColName(PayAsiaConstants.REVIEWER3);
								dataImportLogDTO
										.setRemarks("payasia.invalid.hris.reviewer.3");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}

						}
						if (hrisRuleVal == 2 || hrisRuleVal > 2) {
							if (StringUtils.isNotBlank(hrisReviewersDTO
									.getReviewer1EmployeeNumber())
									&& StringUtils.isNotBlank(hrisReviewersDTO
											.getReviewer2EmployeeNumber())) {
								if (hrisReviewersDTO
										.getReviewer1EmployeeNumber()
										.equalsIgnoreCase(
												hrisReviewersDTO
														.getReviewer2EmployeeNumber())) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER1);
									dataImportLogDTO
											.setRemarks("payasia.hris.reviewers.employee.name.cannot.same");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								}
							}
						}
						if (hrisRuleVal == 3 || hrisRuleVal > 3) {
							if (StringUtils.isNotBlank(hrisReviewersDTO
									.getReviewer1EmployeeNumber())
									&& StringUtils.isNotBlank(hrisReviewersDTO
											.getReviewer3EmployeeNumber())) {
								if (hrisReviewersDTO
										.getReviewer1EmployeeNumber()
										.equalsIgnoreCase(
												hrisReviewersDTO
														.getReviewer3EmployeeNumber())) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER1);
									dataImportLogDTO
											.setRemarks("payasia.hris.reviewers.employee.name.cannot.same");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								}
							}
							if (StringUtils.isNotBlank(hrisReviewersDTO
									.getReviewer2EmployeeNumber())
									&& StringUtils.isNotBlank(hrisReviewersDTO
											.getReviewer3EmployeeNumber())) {
								if (hrisReviewersDTO
										.getReviewer2EmployeeNumber()
										.equalsIgnoreCase(
												hrisReviewersDTO
														.getReviewer3EmployeeNumber())) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER2);
									dataImportLogDTO
											.setRemarks("payasia.hris.reviewers.employee.name.cannot.same");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								}
							}
						}

					}
				} else {
					DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
					dataImportLogDTO
							.setColName(PayAsiaConstants.CAMEL_CASE_EMPLOYEE_NUMBER);
					dataImportLogDTO
							.setRemarks("payasia.assign.leave.scheme.not.valid.emp.num.error");
					dataImportLogDTO.setRowNumber(Long.parseLong(key));
					dataImportLogDTOs.add(dataImportLogDTO);
				}
				hrisReviewersDTOMap.put(key, hrisReviewersDTO);
			}

		}
	}


}
