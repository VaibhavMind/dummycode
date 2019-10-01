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
import com.payasia.common.dto.HRISReviewersDTO;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.OTReviewerConditionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LundinReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeTimesheetReviewerDAO;
import com.payasia.dao.LundinOTReviewerDetailViewDAO;
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.WorkFlowRuleMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetReviewer;
import com.payasia.dao.bean.LundinOTReviewerDetailView;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LundinTimesheetReviewerLogic;

@Component
public class LundinTimesheetReviewerLogicImpl implements
		LundinTimesheetReviewerLogic {

	@Resource
	EmployeeTimesheetReviewerDAO lundinEmpReviewerDAO;

	@Override
	public List<LundinEmployeeTimesheetReviewerDTO> getWorkFlowRuleList(long employeeId, long companyId) {
		List<LundinEmployeeTimesheetReviewerDTO> workFlowRuleList = new ArrayList<LundinEmployeeTimesheetReviewerDTO>();
		List<EmployeeTimesheetReviewer> reviewerList = lundinEmpReviewerDAO
				.findByCondition(employeeId, companyId);

		if (reviewerList != null) {
			for (EmployeeTimesheetReviewer reviewer : reviewerList) {
				LundinEmployeeTimesheetReviewerDTO dto = new LundinEmployeeTimesheetReviewerDTO();
				dto.setEmployeeId(employeeId);
				dto.setEmployeeReviewerId(reviewer.getEmployeeReviewer()
						.getEmployeeId());
				dto.setWorkFlowRuleMasterId(reviewer.getWorkFlowRuleMaster()
						.getWorkFlowRuleId());
				dto.setEmployeeReviewerId(reviewer.getEmployeeReviewer()
						.getEmployeeId());

				workFlowRuleList.add(dto);

			}
		}
		return workFlowRuleList;
	}

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetReviewerLogicImpl.class);
	@Resource
	private TimesheetWorkflowDAO lundinWorkflowDAO;
	@Resource
	private EmployeeDAO employeeDAO;
	@Resource
	private WorkFlowRuleMasterDAO workFlowRuleMasterDAO;
	@Resource
	private EmployeeTimesheetReviewerDAO lundinEmployeeReviewerDAO;
	@Resource
	private LundinOTReviewerDetailViewDAO lundinOTReviewerDetailViewDAO;
	@Resource
	private GeneralLogic generalLogic;
	@Resource
	private CompanyDAO companyDAO;

	@Override
	public LundinTimesheetReviewerForm getLundinWorkFlow(Long companyId) {
		int otRuleVal = 0;

		LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();

		List<TimesheetWorkflow> otWorkflowList = lundinWorkflowDAO
				.findByCompanyId(companyId);
		if (!otWorkflowList.isEmpty()) {
			for (TimesheetWorkflow otWorkflow : otWorkflowList) {
				if (otWorkflow.getWorkFlowRuleMaster().getRuleName()
						.equalsIgnoreCase(PayAsiaConstants.REVIEWER_RULE)) {
					otRuleVal = Integer.parseInt(otWorkflow
							.getWorkFlowRuleMaster().getRuleValue());
				}
			}
		}
		otReviewerForm.setRuleValue(String.valueOf(otRuleVal));
		return otReviewerForm;
	}

	@Override
	public List<LundinTimesheetReviewerForm> getWorkFlowRuleList() {

		List<LundinTimesheetReviewerForm> workFlowRuleList = new ArrayList<LundinTimesheetReviewerForm>();
		List<WorkFlowRuleMaster> workFlowRuleListVO = workFlowRuleMasterDAO
				.findByRuleName(PayAsiaConstants.WORK_FLOW_RULE_NAME_OT_REVIEWER);

		for (WorkFlowRuleMaster workFlowRuleMasterVO : workFlowRuleListVO) {
			LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();
			otReviewerForm.setLundinReviewerRuleId(workFlowRuleMasterVO
					.getWorkFlowRuleId());
			workFlowRuleList.add(otReviewerForm);
		}

		return workFlowRuleList;
	}

	@Override
	public LundinReviewerResponseForm getLundinReviewers(
			String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId) {

		if (sortDTO.getColumnName().equals("")) {
			sortDTO.setColumnName(SortConstants.LEAVE_REVIEWER_EMPLOYEE_NAME);
			sortDTO.setOrderType(PayAsiaConstants.ASC);
		}

		List<LundinOTReviewerDetailView> otReviewerListVO;

		OTReviewerConditionDTO conditionDTO = new OTReviewerConditionDTO();
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

		if (searchCondition.equals(PayAsiaConstants.LUNDIN_REVIEWER_REVIEWER1)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setOtReviewer1(URLDecoder.decode(searchText,
							"UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.LUNDIN_REVIEWER_REVIEWER2)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setOtReviewer2("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.LUNDIN_REVIEWER_REVIEWER3)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setOtReviewer3("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		int recordSize = 0;
		otReviewerListVO = lundinOTReviewerDetailViewDAO.findByCondition(
				conditionDTO, pageDTO, sortDTO, companyId);
		List<LundinTimesheetReviewerForm> otReviewerFormList = new ArrayList<LundinTimesheetReviewerForm>();
		for (LundinOTReviewerDetailView otReviewerVO : otReviewerListVO) {
			LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();

			/* ID ENCRYPT*/
			otReviewerForm.setEmployeeId(FormatPreserveCryptoUtil.encrypt(otReviewerVO.getLundinOTReviewerDetailViewPK().getEmployeeId()));

			String empName = otReviewerVO.getEmpFirstName() + " ";
			if (StringUtils.isNotBlank(otReviewerVO.getEmpLastName())) {
				empName += otReviewerVO.getEmpLastName();
			}
			otReviewerForm.setEmployeeName(empName + " ("
					+ otReviewerVO.getEmpEmployeeNumber() + ")");

			otReviewerForm.setLundinReviewerId1(otReviewerVO.getReviewer1Id());
			String LeaveReviewer1Name = otReviewerVO.getReviewer1FirstName()
					+ " ";
			if (StringUtils.isNotBlank(otReviewerVO.getReviewer1LastName())) {
				LeaveReviewer1Name += otReviewerVO.getReviewer1LastName();
			}
			if (StringUtils.isNotBlank(otReviewerVO.getReviewer1FirstName())) {
				otReviewerForm.setLundinReviewer1(LeaveReviewer1Name);
			} else {
				otReviewerForm.setLundinReviewer1("");
			}

			otReviewerForm.setLundinReviewerId2(otReviewerVO.getReviewer2Id());

			String LeaveReviewer2Name = otReviewerVO.getReviewer2FirstName()
					+ " ";
			if (StringUtils.isNotBlank(otReviewerVO.getReviewer2LastName())) {
				LeaveReviewer2Name += otReviewerVO.getReviewer2LastName();
			}
			if (StringUtils.isNotBlank(otReviewerVO.getReviewer2FirstName())) {
				otReviewerForm.setLundinReviewer2(LeaveReviewer2Name);
			} else {
				otReviewerForm.setLundinReviewer2("");
			}

			otReviewerForm.setLundinReviewerId3(otReviewerVO.getReviewer3Id());
			String LeaveReviewer3Name = otReviewerVO.getReviewer3FirstName()
					+ " ";
			if (StringUtils.isNotBlank(otReviewerVO.getReviewer3LastName())) {
				LeaveReviewer3Name += otReviewerVO.getReviewer3LastName();
			}
			if (StringUtils.isNotBlank(otReviewerVO.getReviewer3FirstName())) {
				otReviewerForm.setLundinReviewer3(LeaveReviewer3Name);
			} else {
				otReviewerForm.setLundinReviewer3("");
			}

			otReviewerFormList.add(otReviewerForm);
		}
		recordSize = (lundinOTReviewerDetailViewDAO.getCountByCondition(
				conditionDTO, companyId));

		LundinReviewerResponseForm response = new LundinReviewerResponseForm();
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
		response.setRows(otReviewerFormList);
		response.setRecords(recordSize);
		return response;

	}

	@Override
	public LundinReviewerResponseForm getLundinReviewerData(Long employeeId,
			Long companyId) {
		LundinReviewerResponseForm otReviewerFormResponse = new LundinReviewerResponseForm();
		List<LundinTimesheetReviewerForm> employeeOTReviewerList = new ArrayList<LundinTimesheetReviewerForm>();

		List<EmployeeTimesheetReviewer> employeeOTReviewerListVO = lundinEmployeeReviewerDAO
				.findByCondition(employeeId, companyId);

		for (EmployeeTimesheetReviewer employeeOTReviewerVO : employeeOTReviewerListVO) {

			LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();

			otReviewerForm.setEmployeeId(employeeOTReviewerVO.getEmployee()
					.getEmployeeId());

			String employeeName1 = employeeOTReviewerVO.getEmployee()
					.getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeOTReviewerVO.getEmployee()
					.getLastName())) {
				employeeName1 += employeeOTReviewerVO.getEmployee()
						.getLastName();
			}
			employeeName1 += "["
					+ employeeOTReviewerVO.getEmployee().getEmployeeNumber()
					+ "]";
			otReviewerForm.setEmployeeName(employeeName1);
		/*	ID ENCRYPT*/
			otReviewerForm.setLundinReviewerId(FormatPreserveCryptoUtil.encrypt(employeeOTReviewerVO
					.getEmployeeReviewer().getEmployeeId()));

			String employeeName2 = employeeOTReviewerVO.getEmployeeReviewer()
					.getFirstName() + " ";
			if (StringUtils.isNotBlank(employeeOTReviewerVO
					.getEmployeeReviewer().getLastName())) {
				employeeName2 += employeeOTReviewerVO.getEmployeeReviewer()
						.getLastName();
			}
			employeeName2 += "["
					+ employeeOTReviewerVO.getEmployeeReviewer()
							.getEmployeeNumber() + "]";
			otReviewerForm.setLundinReviewerName(employeeName2);
			otReviewerForm.setEmployeeLundinReviewerId(employeeOTReviewerVO
					.getEmployeeTimesheetReviewerId());
			employeeOTReviewerList.add(otReviewerForm);

		}
		List<TimesheetWorkflow> workFlowRuleMasterList = lundinWorkflowDAO
				.findByCompanyId(companyId);
		for (TimesheetWorkflow otChangeWorkflow : workFlowRuleMasterList) {
			if (otChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
				otReviewerFormResponse.setRuleValue(otChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
			}
		}

		otReviewerFormResponse.setRows(employeeOTReviewerList);

		return otReviewerFormResponse;
	}

	@Override
	public LundinTimesheetReviewerForm checkLundinReviewer(Long employeeId,
			Long companyId) {
		LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();
		List<EmployeeTimesheetReviewer> employeeOTReviewersList = lundinEmployeeReviewerDAO
				.findByCondition(employeeId, companyId);
		if (!employeeOTReviewersList.isEmpty()) {
			otReviewerForm.setEmployeeStatus("exists");

		} else {
			otReviewerForm.setEmployeeStatus("notexists");
		}

		return otReviewerForm;
	}

	@Override
	public void saveLundinReviewer(
			LundinTimesheetReviewerForm otTimesheetReviewerForm, Long companyId) {
		int ruleValue = Integer
				.parseInt(otTimesheetReviewerForm.getRuleValue());
		Class<?> c = otTimesheetReviewerForm.getClass();

		try {

			Employee employee1 = employeeDAO.findById(otTimesheetReviewerForm
					.getEmployeeId());

			EmployeeTimesheetReviewer employeeOTReviewer = null;
			WorkFlowRuleMaster workFlowRuleReviewer1 = null;
			Employee employee2 = null;
			String methodNameForReviewer;
			String methodNameForWorkFlow;
			for (int reviewerCount = 1; reviewerCount <= ruleValue; reviewerCount++) {
				methodNameForReviewer = "getLundinReviewerId" + reviewerCount;
				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Long employeeReviewerId;

				employeeReviewerId = (Long) methodReviewer
						.invoke(otTimesheetReviewerForm);

				methodNameForWorkFlow = "getLundinReviewerRuleId"
						+ reviewerCount;
				Method methodWorkflow = c.getMethod(methodNameForWorkFlow);
				Long workFlowId = (Long) methodWorkflow
						.invoke(otTimesheetReviewerForm);

				employee2 = employeeDAO.findById(employeeReviewerId);
				employeeOTReviewer = new EmployeeTimesheetReviewer();
				employeeOTReviewer.setEmployee(employee1);
				employeeOTReviewer.setEmployeeReviewer(employee2);
				workFlowRuleReviewer1 = workFlowRuleMasterDAO
						.findByID(workFlowId);
				employeeOTReviewer.setWorkFlowRuleMaster(workFlowRuleReviewer1);
				lundinEmployeeReviewerDAO.save(employeeOTReviewer);

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
	public void deleteLundinReviewer(Long employeeId) {
				lundinEmployeeReviewerDAO.deleteByCondition(employeeId);

	}

	@Override
	public LundinReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		Company company = companyDAO.findById(companyId);
		List<Long> companyIds = companyDAO.getDistinctAssignedGroupCompanies(
				employeeId, company.getCompanyGroup().getGroupId());
		// int recordSize = 0;

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
			// recordSize += employeeDAO.getCountForCondition(conditionDTO,
			// assignedCompanyId);

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

				employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
				employeeForm.setCompanyName(employee.getCompany()
						.getCompanyName());
				employeeListFormList.add(employeeForm);
			}

		}

		LundinReviewerResponseForm response = new LundinReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public void updateLundinReviewer(
			LundinTimesheetReviewerForm otTimesheetReviewerForm, Long companyId) {

		int ruleValue = Integer
				.parseInt(otTimesheetReviewerForm.getRuleValue());
		Class<?> c = otTimesheetReviewerForm.getClass();

		try {

			EmployeeTimesheetReviewer employeeOTReviewer = null;
			Employee employee1 = null;
			Employee employee2 = null;

			String methodNameForReviewer;
			String methodNameForOTReviewer;
			String methodNameForOTReviewerRule;

			for (int reviewerCount = 1; reviewerCount <= ruleValue; reviewerCount++) {

				methodNameForReviewer = "getLundinReviewerId" + reviewerCount;
				methodNameForOTReviewer = "getEmployeeLundinReviewerId"
						+ reviewerCount;
				methodNameForOTReviewerRule = "getLundinReviewerRuleId"
						+ reviewerCount;

				Method methodReviewer = c.getMethod(methodNameForReviewer);
				Method methodEmployeeOTReviewer = c
						.getMethod(methodNameForOTReviewer);
				Method methodEmployeeOTReviewerRule = c
						.getMethod(methodNameForOTReviewerRule);

				Long employeeReviewerId;
				Long employeeOTReviewerId;
				Long employeeOTReviewerRuleId;

				employeeOTReviewerId = (Long) methodEmployeeOTReviewer
						.invoke(otTimesheetReviewerForm);

				employeeReviewerId = (Long) methodReviewer
						.invoke(otTimesheetReviewerForm);

				employeeOTReviewerRuleId = (Long) methodEmployeeOTReviewerRule
						.invoke(otTimesheetReviewerForm);

				if (employeeOTReviewerId != null) {
					employeeOTReviewer = lundinEmployeeReviewerDAO
							.findById(employeeOTReviewerId);
					if (employeeReviewerId != null) {
						employee2 = employeeDAO.findById(employeeReviewerId);

						employeeOTReviewer.setEmployeeReviewer(employee2);

						lundinEmployeeReviewerDAO.update(employeeOTReviewer);
					}
					if (employeeReviewerId == null) {
						if (employeeOTReviewer != null) {
							lundinEmployeeReviewerDAO
									.delete(employeeOTReviewer);
						}
					}
				} else {

					if (employeeReviewerId != null) {
						String[] empIds = otTimesheetReviewerForm
								.getEmployeeName().split(",");
						EmployeeTimesheetReviewer empOTReviewer = new EmployeeTimesheetReviewer();
						employee1 = employeeDAO.findById(Long
								.parseLong(empIds[0]));
						employee2 = employeeDAO.findById(employeeReviewerId);
						empOTReviewer.setEmployee(employee1);
						empOTReviewer.setEmployeeReviewer(employee2);
						WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
								.findByID(employeeOTReviewerRuleId);
						empOTReviewer.setWorkFlowRuleMaster(workFlowRuleMaster);
						lundinEmployeeReviewerDAO.save(empOTReviewer);
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
	public LundinReviewerResponseForm searchEmployeeBySessionCompany(
			PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		// int recordSize = 0;

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

			employeeForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeListFormList.add(employeeForm);
		}

		LundinReviewerResponseForm response = new LundinReviewerResponseForm();

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
/*ID ENCRYPT*/
			employeeListForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeListFormList.add(employeeListForm);

		}
		return employeeListFormList;
	}

	@Override
	public List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId,
			String searchString, Long employeeId) {
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();
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
			/*ID ENCRYPT*/
			employeeListForm.setEmployeeID(FormatPreserveCryptoUtil.encrypt(employee.getEmployeeId()));
			employeeListFormList.add(employeeListForm);

		}
		return employeeListFormList;
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
	public String configureLundinRevWorkflow(Long companyId,
			LundinTimesheetReviewerForm otTimesheetReviewerForm) {
		Company company = companyDAO.findById(companyId);

		List<WorkFlowRuleMaster> workFlowRuleMasterList = workFlowRuleMasterDAO
				.findAll();

		List<TimesheetWorkflow> changeWorkflowList = lundinWorkflowDAO
				.findByCompanyId(companyId);
		if (!changeWorkflowList.isEmpty()) {
			for (TimesheetWorkflow otChangeWorkflow : changeWorkflowList) {
				lundinWorkflowDAO.deleteByCondition(otChangeWorkflow
						.getWorkflowId());
			}
		}

		if (otTimesheetReviewerForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.HRIS_DEF_WORKFLOWL1)) {
			Long workFlowId = getWorkFlowMasterId(
					PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "1",
					workFlowRuleMasterList);
			TimesheetWorkflow otChangeWorkflow = new TimesheetWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
					.findByID(workFlowId);
			otChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			otChangeWorkflow.setCompany(company);
			lundinWorkflowDAO.save(otChangeWorkflow);
		}
		if (otTimesheetReviewerForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.HRIS_DEF_WORKFLOWL2)) {
			Long workFlowId = getWorkFlowMasterId(
					PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "2",
					workFlowRuleMasterList);
			TimesheetWorkflow otChangeWorkflow = new TimesheetWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
					.findByID(workFlowId);
			otChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			otChangeWorkflow.setCompany(company);
			lundinWorkflowDAO.save(otChangeWorkflow);
		}
		if (otTimesheetReviewerForm.getWorkFlowLevel().toUpperCase()
				.equals(PayAsiaConstants.HRIS_DEF_WORKFLOWL3)) {
			Long workFlowId = getWorkFlowMasterId(
					PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL, "3",
					workFlowRuleMasterList);
			TimesheetWorkflow otChangeWorkflow = new TimesheetWorkflow();

			WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
					.findByID(workFlowId);
			otChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
			otChangeWorkflow.setCompany(company);
			lundinWorkflowDAO.save(otChangeWorkflow);
		}

		String allowOverrideStr = "";
		if (otTimesheetReviewerForm.getAllowOverrideL1() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowOverrideL2() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowOverrideL3() == true) {
			allowOverrideStr += "1";
		} else {
			allowOverrideStr += "0";
		}

		Long allowOverrideId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_OVERRIDE,
				allowOverrideStr.trim(), workFlowRuleMasterList);
		TimesheetWorkflow otChangeWorkflow = new TimesheetWorkflow();

		WorkFlowRuleMaster workFlowRuleMaster = workFlowRuleMasterDAO
				.findByID(allowOverrideId);
		otChangeWorkflow.setWorkFlowRuleMaster(workFlowRuleMaster);
		otChangeWorkflow.setCompany(company);
		lundinWorkflowDAO.save(otChangeWorkflow);

		String allowRejectStr = "";
		if (otTimesheetReviewerForm.getAllowRejectL1() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowRejectL2() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowRejectL3() == true) {
			allowRejectStr += "1";
		} else {
			allowRejectStr += "0";
		}

		Long allowRejectId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_REJECT, allowRejectStr.trim(),
				workFlowRuleMasterList);
		TimesheetWorkflow otChangeWorkflowAllowReject = new TimesheetWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowRejectMaster = workFlowRuleMasterDAO
				.findByID(allowRejectId);
		otChangeWorkflowAllowReject
				.setWorkFlowRuleMaster(workFlowRuleAllowRejectMaster);
		otChangeWorkflowAllowReject.setCompany(company);
		lundinWorkflowDAO.save(otChangeWorkflowAllowReject);

		String allowForwardStr = "";
		if (otTimesheetReviewerForm.getAllowForward1() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowForward2() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowForward3() == true) {
			allowForwardStr += "1";
		} else {
			allowForwardStr += "0";
		}

		Long allowForwardId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_FORWARD,
				allowForwardStr.trim(), workFlowRuleMasterList);
		TimesheetWorkflow otWorkflowWorkflowAllowForward = new TimesheetWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowForward = workFlowRuleMasterDAO
				.findByID(allowForwardId);
		otWorkflowWorkflowAllowForward
				.setWorkFlowRuleMaster(workFlowRuleAllowForward);
		otWorkflowWorkflowAllowForward.setCompany(company);
		lundinWorkflowDAO.save(otWorkflowWorkflowAllowForward);

		String allowApproveStr = "";
		if (otTimesheetReviewerForm.getAllowApprove1() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowApprove2() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		if (otTimesheetReviewerForm.getAllowApprove3() == true) {
			allowApproveStr += "1";
		} else {
			allowApproveStr += "0";
		}

		Long allowApproveId = getWorkFlowMasterId(
				PayAsiaConstants.HRIS_DEF_ALLOW_APPROVE,
				allowApproveStr.trim(), workFlowRuleMasterList);
		TimesheetWorkflow otWorkflowAllowApprove = new TimesheetWorkflow();

		WorkFlowRuleMaster workFlowRuleAllowApprove = workFlowRuleMasterDAO
				.findByID(allowApproveId);
		otWorkflowAllowApprove.setWorkFlowRuleMaster(workFlowRuleAllowApprove);
		otWorkflowAllowApprove.setCompany(company);
		lundinWorkflowDAO.save(otWorkflowAllowApprove);

		return "notavailable";

	}

	@Override
	public LundinTimesheetReviewerForm getLundinWorkFlowLevel(Long companyId) {
		LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();

		List<TimesheetWorkflow> changeWorkflowList = lundinWorkflowDAO
				.findByCompanyId(companyId);

		for (TimesheetWorkflow otChangeWorkflow : changeWorkflowList) {

			if (otChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_WORKFLOW_LEVEL)) {
				otReviewerForm.setWorkFlowLevel(otChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue());
			}

			if (otChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_OVERRIDE)) {
				String allowOverRideVal = otChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowOverRideVal.charAt(0);
				char atlevelTwo = allowOverRideVal.charAt(1);
				char atlevelThree = allowOverRideVal.charAt(2);

				if (atlevelOne == '0') {
					otReviewerForm.setAllowOverrideL1(false);
				}
				if (atlevelOne == '1') {
					otReviewerForm.setAllowOverrideL1(true);
				}
				if (atlevelTwo == '0') {
					otReviewerForm.setAllowOverrideL2(false);
				}
				if (atlevelTwo == '1') {
					otReviewerForm.setAllowOverrideL2(true);
				}
				if (atlevelThree == '0') {
					otReviewerForm.setAllowOverrideL3(false);
				}
				if (atlevelThree == '1') {
					otReviewerForm.setAllowOverrideL3(true);
				}
			}

			if (otChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.LEAVE_SCHEME_DEF_ALLOW_REJECT)) {
				String allowRejectVal = otChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowRejectVal.charAt(0);
				char atlevelTwo = allowRejectVal.charAt(1);
				char atlevelThree = allowRejectVal.charAt(2);

				if (atlevelOne == '0') {
					otReviewerForm.setAllowRejectL1(false);
				}
				if (atlevelOne == '1') {
					otReviewerForm.setAllowRejectL1(true);
				}
				if (atlevelTwo == '0') {
					otReviewerForm.setAllowRejectL2(false);
				}
				if (atlevelTwo == '1') {
					otReviewerForm.setAllowRejectL2(true);
				}
				if (atlevelThree == '0') {
					otReviewerForm.setAllowRejectL3(false);
				}
				if (atlevelThree == '1') {
					otReviewerForm.setAllowRejectL3(true);
				}
			}
			if (otChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.HRIS_DEF_ALLOW_FORWARD)) {
				String allowForwardVal = otChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowForwardVal.charAt(0);
				char atlevelTwo = allowForwardVal.charAt(1);
				char atlevelThree = allowForwardVal.charAt(2);

				if (atlevelOne == '0') {
					otReviewerForm.setAllowForward1(false);
				}
				if (atlevelOne == '1') {
					otReviewerForm.setAllowForward1(true);
				}
				if (atlevelTwo == '0') {
					otReviewerForm.setAllowForward2(false);
				}
				if (atlevelTwo == '1') {
					otReviewerForm.setAllowForward2(true);
				}
				if (atlevelThree == '0') {
					otReviewerForm.setAllowForward3(false);
				}
				if (atlevelThree == '1') {
					otReviewerForm.setAllowForward3(true);
				}
			}
			if (otChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
					.toUpperCase()
					.equals(PayAsiaConstants.HRIS_DEF_ALLOW_APPROVE)) {
				String allowApproveVal = otChangeWorkflow
						.getWorkFlowRuleMaster().getRuleValue();
				char atlevelOne = allowApproveVal.charAt(0);
				char atlevelTwo = allowApproveVal.charAt(1);
				char atlevelThree = allowApproveVal.charAt(2);

				if (atlevelOne == '0') {
					otReviewerForm.setAllowApprove1(false);
				}
				if (atlevelOne == '1') {
					otReviewerForm.setAllowApprove1(true);
				}
				if (atlevelTwo == '0') {
					otReviewerForm.setAllowApprove2(false);
				}
				if (atlevelTwo == '1') {
					otReviewerForm.setAllowApprove2(true);
				}
				if (atlevelThree == '0') {
					otReviewerForm.setAllowApprove3(false);
				}
				if (atlevelThree == '1') {
					otReviewerForm.setAllowApprove3(true);
				}
			}
		}
		return otReviewerForm;
	}

	@Override
	public LundinTimesheetReviewerForm importLundinReviewer(
			LundinTimesheetReviewerForm otReviewerform, Long companyId) {
		String fileName = otReviewerform.getFileUpload().getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,
				fileName.length());
		LundinTimesheetReviewerForm otRevList = new LundinTimesheetReviewerForm();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			otRevList = ExcelUtils.getLundinReviewersFromXLS(otReviewerform
					.getFileUpload());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			otRevList = ExcelUtils.getLundinReviewersFromXLSX(otReviewerform
					.getFileUpload());
		}
		LundinTimesheetReviewerForm reviewerForm = new LundinTimesheetReviewerForm();
		List<DataImportLogDTO> dataImportLogDTOs = new ArrayList<>();

		int otRuleVal = 0;
		List<TimesheetWorkflow> otChangeWorkflowList = lundinWorkflowDAO
				.findByCompanyId(companyId);
		if (!otChangeWorkflowList.isEmpty()) {
			for (TimesheetWorkflow otChangeWorkflow : otChangeWorkflowList) {
				if (otChangeWorkflow.getWorkFlowRuleMaster().getRuleName()
						.equalsIgnoreCase(PayAsiaConstants.REVIEWER_RULE)) {
					otRuleVal = Integer.parseInt(otChangeWorkflow
							.getWorkFlowRuleMaster().getRuleValue());
				}
			}
		}

		if (otRuleVal == 0) {
			reviewerForm.setDataValid(false);

			DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
			dataImportLogDTO
					.setColName(PayAsiaConstants.PAYASIA_HRIS_WORKFLOW_LEVEL);
			dataImportLogDTO
					.setRemarks("payasia.ot.timesheet.reviewer.workflow.level.not.configured");
			dataImportLogDTOs.add(dataImportLogDTO);

			reviewerForm.setDataImportLogDTOs(dataImportLogDTOs);
			return reviewerForm;
		}
		HashMap<String, HRISReviewersDTO> otReviewersDTOMap = new HashMap<>();
		setOTReviewersImportDataDTO(dataImportLogDTOs, otRevList,
				otReviewersDTOMap, companyId);

		validateImportedData(otReviewersDTOMap, dataImportLogDTOs, companyId,
				otRuleVal);

		if (!dataImportLogDTOs.isEmpty()) {
			reviewerForm.setDataValid(false);
			reviewerForm.setDataImportLogDTOs(dataImportLogDTOs);
			return reviewerForm;
		}

		Set<String> keySet = otReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();

			HRISReviewersDTO hrisReviewersDTO = otReviewersDTOMap.get(key);
			Class<?> c = hrisReviewersDTO.getClass();
			try {
				if (!key.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {
					Employee employeeVO = employeeDAO.findByNumber(
							hrisReviewersDTO.getEmployeeNumber(), companyId);

					EmployeeTimesheetReviewer employeeOTReviewer = null;
					WorkFlowRuleMaster workFlowRuleReviewer = null;
					String methodNameForReviewer;
					for (int otReviewerCount = 1; otReviewerCount <= otRuleVal; otReviewerCount++) {
						methodNameForReviewer = "getReviewer" + otReviewerCount
								+ "Id";
						Method methodReviewer = c
								.getMethod(methodNameForReviewer);
						Long employeeReviewerId;

						employeeReviewerId = (Long) methodReviewer
								.invoke(hrisReviewersDTO);
						Employee employeeRevVO = employeeDAO
								.findById(employeeReviewerId);
						employeeOTReviewer = new EmployeeTimesheetReviewer();
						employeeOTReviewer.setEmployee(employeeVO);
						employeeOTReviewer.setEmployeeReviewer(employeeRevVO);
						workFlowRuleReviewer = workFlowRuleMasterDAO
								.findByRuleNameValue(
										PayAsiaConstants.WORK_FLOW_RULE_NAME_OT_REVIEWER,
										String.valueOf(otReviewerCount));
						employeeOTReviewer
								.setWorkFlowRuleMaster(workFlowRuleReviewer);

						EmployeeTimesheetReviewer employeeOTReviewerVO = lundinEmployeeReviewerDAO
								.findByWorkFlowCondition(employeeVO
										.getEmployeeId(), workFlowRuleReviewer
										.getWorkFlowRuleId());

						if (employeeOTReviewerVO == null) {
							lundinEmployeeReviewerDAO.save(employeeOTReviewer);
						} else {
							employeeOTReviewerVO
									.setEmployeeReviewer(employeeRevVO);
							lundinEmployeeReviewerDAO
									.update(employeeOTReviewerVO);
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

	public void setOTReviewersImportDataDTO(
			List<DataImportLogDTO> dataImportLogDTOs,
			LundinTimesheetReviewerForm otReviewerExcelFieldForm,
			HashMap<String, HRISReviewersDTO> otReviewersDTOMap, Long companyId) {
		for (HashMap<String, String> map : otReviewerExcelFieldForm
				.getImportedData()) {
			HRISReviewersDTO otReviewersDTO = new HRISReviewersDTO();
			Set<String> keySet = map.keySet();
			String rowNumber = map.get(PayAsiaConstants.HASH_KEY_ROW_NUMBER);
			for (Iterator<String> iterator = keySet.iterator(); iterator
					.hasNext();) {
				String key = (String) iterator.next();
				String value = (String) map.get(key);
				if (key != PayAsiaConstants.HASH_KEY_ROW_NUMBER) {
					if (key.equals(PayAsiaConstants.EMPLOYEE_NUMBER_COMPANY)) {
						otReviewersDTO.setEmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_EMPLOYEE_NUMBER)) {
						otReviewersDTO.setReviewer1EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER1_COMPANY_CODE)) {
						otReviewersDTO.setReviewer1CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_EMPLOYEE_NUMBER)) {
						otReviewersDTO.setReviewer2EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER2_COMPANY_CODE)) {
						otReviewersDTO.setReviewer2CompanyCode(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_EMPLOYEE_NUMBER)) {
						otReviewersDTO.setReviewer3EmployeeNumber(value);
					}
					if (key.equals(PayAsiaConstants.IMPORT_REVIEWER3_COMPANY_CODE)) {
						otReviewersDTO.setReviewer3CompanyCode(value);
					}
					otReviewersDTOMap.put(rowNumber, otReviewersDTO);
				}
			}
		}
	}

	/**
	 * Purpose: To Validate imported OT Reviewer.
	 * 
	 * @param otRevList
	 *            the OTRevList
	 * @param companyId
	 *            the company Id
	 * @param dataImportLogDTOs
	 *            the data import log dtos
	 * @param otRuleVal
	 */
	private void validateImportedData(
			HashMap<String, HRISReviewersDTO> otReviewersDTOMap,
			List<DataImportLogDTO> dataImportLogDTOs, Long companyId,
			int otRuleVal) {
		Set<String> keySet = otReviewersDTOMap.keySet();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			HRISReviewersDTO otReviewersDTO = otReviewersDTOMap.get(key);
			if (!key.equalsIgnoreCase(PayAsiaConstants.COLUMN_NAME_EMPLOYEE_NUMBER)) {

				if (StringUtils.isNotBlank(otReviewersDTO.getEmployeeNumber())) {
					Employee employeeVO = employeeDAO.findByNumber(
							otReviewersDTO.getEmployeeNumber(), companyId);
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
						if (otRuleVal == 1 || otRuleVal > 1) {
							if (StringUtils.isNotBlank(otReviewersDTO
									.getReviewer1EmployeeNumber())) {
								Employee reviewer1 = employeeDAO
										.getEmployeeByEmpNumAndCompCode(
												otReviewersDTO
														.getReviewer1EmployeeNumber(),
												otReviewersDTO
														.getReviewer1CompanyCode());
								if (reviewer1 == null) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER1);
									dataImportLogDTO
											.setRemarks("payasia.invalid.ot.timesheet.reviewer.1");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								} else {
									otReviewersDTO.setReviewer1Id(reviewer1
											.getEmployeeId());
									if (employeeVO
											.getEmployeeNumber()
											.equalsIgnoreCase(
													otReviewersDTO
															.getReviewer1EmployeeNumber())) {
										DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
										dataImportLogDTO
												.setColName(PayAsiaConstants.REVIEWER1);
										dataImportLogDTO
												.setRemarks("payasia.lundin.reviewer.names.cannot.be.same");
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
										.setRemarks("payasia.invalid.ot.timesheet.reviewer.1");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}

						}
						if (otRuleVal == 2 || otRuleVal > 2) {

							if (StringUtils.isNotBlank(otReviewersDTO
									.getReviewer2EmployeeNumber())) {
								Employee reviewer2 = employeeDAO
										.getEmployeeByEmpNumAndCompCode(
												otReviewersDTO
														.getReviewer2EmployeeNumber(),
												otReviewersDTO
														.getReviewer2CompanyCode());
								if (reviewer2 == null) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER2);
									dataImportLogDTO
											.setRemarks("payasia.invalid.ot.timesheet.reviewer.2");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								} else {
									otReviewersDTO.setReviewer2Id(reviewer2
											.getEmployeeId());
									if (employeeVO
											.getEmployeeNumber()
											.equalsIgnoreCase(
													otReviewersDTO
															.getReviewer2EmployeeNumber())) {
										DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
										dataImportLogDTO
												.setColName(PayAsiaConstants.REVIEWER2);
										dataImportLogDTO
												.setRemarks("payasia.lundin.reviewer.names.cannot.be.same");
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
										.setRemarks("payasia.invalid.ot.timesheet.reviewer.2");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}

						}
						if (otRuleVal == 3 || otRuleVal > 3) {

							if (StringUtils.isNotBlank(otReviewersDTO
									.getReviewer3EmployeeNumber())) {
								Employee reviewer3 = employeeDAO
										.getEmployeeByEmpNumAndCompCode(
												otReviewersDTO
														.getReviewer3EmployeeNumber(),
												otReviewersDTO
														.getReviewer3CompanyCode());
								if (reviewer3 == null) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER3);
									dataImportLogDTO
											.setRemarks("payasia.invalid.ot.timesheet.reviewer.3");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								} else {
									otReviewersDTO.setReviewer3Id(reviewer3
											.getEmployeeId());
									if (employeeVO
											.getEmployeeNumber()
											.equalsIgnoreCase(
													otReviewersDTO
															.getReviewer3EmployeeNumber())) {
										DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
										dataImportLogDTO
												.setColName(PayAsiaConstants.REVIEWER3);
										dataImportLogDTO
												.setRemarks("payasia.lundin.reviewer.names.cannot.be.same");
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
										.setRemarks("payasia.invalid.ot.timesheet.reviewer.3");
								dataImportLogDTO.setRowNumber(Long
										.parseLong(key));
								dataImportLogDTOs.add(dataImportLogDTO);
							}

						}
						if (otRuleVal == 2 || otRuleVal > 2) {
							if (StringUtils.isNotBlank(otReviewersDTO
									.getReviewer1EmployeeNumber())
									&& StringUtils.isNotBlank(otReviewersDTO
											.getReviewer2EmployeeNumber())) {
								if (otReviewersDTO
										.getReviewer1EmployeeNumber()
										.equalsIgnoreCase(
												otReviewersDTO
														.getReviewer2EmployeeNumber())) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER1);
									dataImportLogDTO
											.setRemarks("payasia.lundin.reviewer.names.cannot.be.same");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								}
							}
						}
						if (otRuleVal == 3 || otRuleVal > 3) {
							if (StringUtils.isNotBlank(otReviewersDTO
									.getReviewer1EmployeeNumber())
									&& StringUtils.isNotBlank(otReviewersDTO
											.getReviewer3EmployeeNumber())) {
								if (otReviewersDTO
										.getReviewer1EmployeeNumber()
										.equalsIgnoreCase(
												otReviewersDTO
														.getReviewer3EmployeeNumber())) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER1);
									dataImportLogDTO
											.setRemarks("payasia.lundin.reviewer.names.cannot.be.same");
									dataImportLogDTO.setRowNumber(Long
											.parseLong(key));
									dataImportLogDTOs.add(dataImportLogDTO);
								}
							}
							if (StringUtils.isNotBlank(otReviewersDTO
									.getReviewer2EmployeeNumber())
									&& StringUtils.isNotBlank(otReviewersDTO
											.getReviewer3EmployeeNumber())) {
								if (otReviewersDTO
										.getReviewer2EmployeeNumber()
										.equalsIgnoreCase(
												otReviewersDTO
														.getReviewer3EmployeeNumber())) {
									DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
									dataImportLogDTO
											.setColName(PayAsiaConstants.REVIEWER2);
									dataImportLogDTO
											.setRemarks("payasia.lundin.reviewer.names.cannot.be.same");
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
				otReviewersDTOMap.put(key, otReviewersDTO);
			}

		}
	}

	

}
