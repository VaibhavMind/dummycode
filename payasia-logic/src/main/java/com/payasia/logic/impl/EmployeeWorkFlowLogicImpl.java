package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.WorkflowDelegateConditionDTO;
import com.payasia.common.form.EmployeeWorkFlowDelegateResponse;
import com.payasia.common.form.EmployeeWorkFlowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.WorkflowDelegateDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.WorkflowDelegate;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.EmployeeWorkFlowLogic;
import com.payasia.logic.GeneralLogic;

/**
 * The Class EmployeeWorkFlowLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class EmployeeWorkFlowLogicImpl implements EmployeeWorkFlowLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeWorkFlowLogicImpl.class);

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The workflow delegate dao. */
	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;

	/** The app code master dao. */
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	GeneralLogic generalLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeWorkFlowLogic#getEmployeeInfo(java.lang.Long)
	 */
	@Override
	public String getEmployeeInfo(Long employeeId) {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		Employee employee = employeeDAO.findById(employeeId,companyID);
		String employeeInfo = employee.getFirstName() + " "
				+ employee.getLastName() + " (" + employee.getEmployeeNumber()
				+ ")";
		return employeeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeWorkFlowLogic#getEmployeeWorkFlowDetails(java
	 * .lang.Long, java.lang.String)
	 */
	@Override
	public List<EmployeeWorkFlowForm> getEmployeeWorkFlowDetails(
			Long companyId, String searchString, Long employeeId) {
		List<EmployeeWorkFlowForm> employeeWorkFlowDTOList = new ArrayList<EmployeeWorkFlowForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);

		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(
				searchString.trim(), companyId, employeeShortListDTO);

		for (Employee employee : employeeNumberList) {
			EmployeeWorkFlowForm employeeWorkFlowDTO = new EmployeeWorkFlowForm();

			try {
				employeeWorkFlowDTO.setEmployeeNumber(URLEncoder.encode(
						employee.getEmployeeNumber(), "UTF-8"));
				employeeWorkFlowDTO.setEmployeeName(URLEncoder.encode(
						employee.getFirstName(), "UTF-8")
						+ " "
						+ URLEncoder.encode(employee.getLastName(), "UTF-8"));

			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
			employeeWorkFlowDTO.setEmployeeId(employee.getEmployeeId());
			employeeWorkFlowDTOList.add(employeeWorkFlowDTO);

		}
		return employeeWorkFlowDTOList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.EmployeeWorkFlowLogic#getWorkflowTypeList()
	 */
	@Override
	public List<AppCodeDTO> getWorkflowTypeList() {
		List<AppCodeDTO> appCodeDTOs = new ArrayList<AppCodeDTO>();
		List<AppCodeMaster> workflowList = appCodeMasterDAO
				.findByCondition(PayAsiaConstants.APP_CODE_WORKFLOW_TYPE);
		for (AppCodeMaster appCodeMaster : workflowList) {
			AppCodeDTO appCodeDTO = new AppCodeDTO();
			appCodeDTO.setAppCodeID(appCodeMaster.getAppCodeID());
			appCodeDTO.setCodeDesc(appCodeMaster.getCodeDesc());
			appCodeDTOs.add(appCodeDTO);
		}

		return appCodeDTOs;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeWorkFlowLogic#saveEmployeeWorkFlowDelagateData
	 * (com.payasia.common.form.EmployeeWorkFlowForm)
	 */
	@Override
	public void saveEmployeeWorkFlowDelagateData(
			EmployeeWorkFlowForm employeeWorkFlowForm) {

		 
		Company company = companyDAO.findById(employeeWorkFlowForm
				.getCompanyId());

		Employee employee1 = employeeDAO.findById(employeeWorkFlowForm
				.getEmployeeId());

		Employee employee2 = employeeDAO.findByNumber(
				employeeWorkFlowForm.getDelegateTo(),
				employeeWorkFlowForm.getCompanyId());

		 
		 

		 
		 

		WorkflowDelegate workflowDelegate = new WorkflowDelegate();

		workflowDelegate.setCompany(company);
		workflowDelegate.setEmployee1(employee1);
		workflowDelegate.setEmployee2(employee2);

		workflowDelegate.setStartDate(DateUtils
				.stringToTimestamp(employeeWorkFlowForm.getFromDate()));
		workflowDelegate.setEndDate(DateUtils
				.stringToTimestamp(employeeWorkFlowForm.getToDate()));
		 

		workflowDelegateDAO.save(workflowDelegate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeWorkFlowLogic#viewEmployeeWorkFlowDelegateData
	 * (com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long, java.lang.Long)
	 */
	@Override
	public EmployeeWorkFlowDelegateResponse viewEmployeeWorkFlowDelegateData(
			PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			Long companyId) {

		WorkflowDelegateConditionDTO workflowDelegateConditionDTO = new WorkflowDelegateConditionDTO();
		workflowDelegateConditionDTO.setEmployeeId(employeeId);
		int recordSize = workflowDelegateDAO.getCountForCondition(
				workflowDelegateConditionDTO, companyId);

		List<WorkflowDelegate> workflowDelegates = workflowDelegateDAO
				.findByCondition(pageDTO, sortDTO,
						workflowDelegateConditionDTO, companyId);
		List<WorkFlowDelegateForm> workFlowDelegateForms = new ArrayList<WorkFlowDelegateForm>();

		for (WorkflowDelegate workflowDelegate : workflowDelegates) {

			WorkFlowDelegateForm workFlowForm = new WorkFlowDelegateForm();

			Employee employee = workflowDelegate.getEmployee2();

			workFlowForm.setDelegateTo(employee.getFirstName() + " "
					+ employee.getLastName());
			workFlowForm.setEmployeeNumber(employee.getEmployeeNumber());
			workFlowForm.setWorkFlowDelegateId(workflowDelegate
					.getWorkflowDelegateId());
			 
			workFlowForm.setFromDate(DateUtils
					.timeStampToString(workflowDelegate.getStartDate()));
			workFlowForm.setToDate(DateUtils.timeStampToString(workflowDelegate
					.getEndDate()));

			workFlowDelegateForms.add(workFlowForm);
		}

		EmployeeWorkFlowDelegateResponse response = new EmployeeWorkFlowDelegateResponse();

		response.setRows(workFlowDelegateForms);

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

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeWorkFlowLogic#deleteEmployeeWorkFlowDelegate
	 * (java.lang.Long)
	 */
	@Override
	public String deleteEmployeeWorkFlowDelegate(Long workFlowDelegateId) {
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		WorkflowDelegate workflowDelegates = workflowDelegateDAO
				.findByID(workFlowDelegateId,companyID);
		if(workflowDelegates!=null)
		{
		workflowDelegateDAO.delete(workflowDelegates);
		}
		return "deleted".toLowerCase();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.EmployeeWorkFlowLogic#editEmployeeWorkFlowDelagateData
	 * (com.payasia.common.form.EmployeeWorkFlowForm)
	 */
	@Override
	public void editEmployeeWorkFlowDelagateData(
			EmployeeWorkFlowForm employeeWorkFlowForm) {
		 
		Company company = companyDAO.findById(employeeWorkFlowForm
				.getCompanyId());

		Employee employee1 = employeeDAO.findById(employeeWorkFlowForm
				.getEmployeeId());

		Employee employee2 = employeeDAO.findByNumber(
				employeeWorkFlowForm.getDelegateTo(),
				employeeWorkFlowForm.getCompanyId());

		 WorkflowDelegate workflowDelegate = new WorkflowDelegate();

		workflowDelegate.setWorkflowDelegateId(employeeWorkFlowForm
				.getEmployeeWorkFlowDelegateId());
		workflowDelegate.setCompany(company);
		workflowDelegate.setEmployee1(employee1);
		workflowDelegate.setEmployee2(employee2);

		workflowDelegate.setStartDate(DateUtils
				.stringToTimestamp(employeeWorkFlowForm.getFromDate()));
		workflowDelegate.setEndDate(DateUtils
				.stringToTimestamp(employeeWorkFlowForm.getToDate()));
		 

		workflowDelegateDAO.update(workflowDelegate);

	}
}
