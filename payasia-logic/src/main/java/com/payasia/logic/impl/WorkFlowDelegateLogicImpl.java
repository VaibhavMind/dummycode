package com.payasia.logic.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.PreviousWorkflowDTO;
import com.payasia.common.dto.WorkflowDelegateConditionDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateForm;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
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
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.WorkFlowDelegateLogic;

/**
 * The Class WorkFlowDelegateLogicImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Component
public class WorkFlowDelegateLogicImpl implements WorkFlowDelegateLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(WorkFlowDelegateLogicImpl.class);

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The workflow delegate dao. */
	@Resource
	WorkflowDelegateDAO workflowDelegateDAO;

	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;

	/** The app code master dao. */
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;

	@Resource
	GeneralLogic generalLogic;

	@Resource
	GeneralMailLogic generalMailLogic;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;
	@Resource
	MessageSource messageSource;
	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#getWorkFlowDelegateList(com.payasia
	 * .common.form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public WorkFlowDelegateResponse getWorkFlowDelegateList(
			PageRequest pageDTO, SortCondition sortDTO, String criteria,
			String keyword, String workFlowType, Long companyId,
			CompanyModuleDTO companyModuleDTO) {
		Company company = companyDAO.findById(companyId);

		List<WorkFlowDelegateForm> workFlowDelegateFormList = new ArrayList<WorkFlowDelegateForm>();
		WorkflowDelegateConditionDTO conditionDTO = new WorkflowDelegateConditionDTO();
		if(!StringUtils.isEmpty(criteria))
		{
		if (criteria.equals(PayAsiaConstants.WORK_FLOW_DELEGATE_EMP_NUM)) {
			if (StringUtils.isNotBlank(keyword)) {

				try {
					conditionDTO.setEmployeeNumber("%"
							+ URLDecoder.decode(keyword, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (criteria.equals(PayAsiaConstants.WORK_FLOW_DELEGATE_EMP_NAME)) {
			if (StringUtils.isNotBlank(keyword)) {
				try {
					conditionDTO.setEmployeeName("%"
							+ URLDecoder.decode(keyword, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		}
		
		if (StringUtils.isNotBlank(workFlowType)) {

			List<Long> appCodeIds = new ArrayList<>();
			AppCodeMaster appCodeMaster = appCodeMasterDAO.findById(Long
					.parseLong(workFlowType));
			if (appCodeMaster.getCodeDesc().equals(
					PayAsiaConstants.WORK_FLOW_DELEGATE_ALL)) {

				List<AppCodeMaster> workFlowTypes = appCodeMasterDAO
						.findByConditionWorkFlowDelegate(PayAsiaConstants.APP_CODE_WORKFLOW_TYPE);
				Map<String, Long> workFlowMap = new HashMap<>();
				for (AppCodeMaster appCode : workFlowTypes) {
					workFlowMap.put(appCode.getCodeDesc(),
							appCode.getAppCodeID());
				}

				if (companyModuleDTO.isHasClaimModule()) {
					appCodeIds.add(workFlowMap
							.get(PayAsiaConstants.COMPANY_MODULE_CLAIM));

				}
				if (companyModuleDTO.isHasLeaveModule()) {
					appCodeIds.add(workFlowMap
							.get(PayAsiaConstants.COMPANY_MODULE_LEAVE));
				}

				if (companyModuleDTO.isHasHrisModule()) {
					appCodeIds.add(workFlowMap
							.get(PayAsiaConstants.COMPANY_MODULE_HRIS));
				}
				if (companyModuleDTO.isHasLundinTimesheetModule()) {
					appCodeIds
							.add(workFlowMap
									.get(PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET));
				}
				if (companyModuleDTO.isHasLionTimesheetModule()) {
					appCodeIds
							.add(workFlowMap
									.get(PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET));
				}
				if (companyModuleDTO.isHasCoherentTimesheetModule()) {
					appCodeIds
							.add(workFlowMap
									.get(PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET));
				}
				appCodeIds.add(appCodeMaster.getAppCodeID());

			} else {
				appCodeIds.add(appCodeMaster.getAppCodeID());
			}
			conditionDTO.setAppCodeIds(appCodeIds);
			try {
				conditionDTO.setWorkFlowType(URLDecoder.decode(workFlowType,
						"UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}

		}

		// Changes by Gaurav
		conditionDTO.setEmployeeId(Long.valueOf(UserContext.getUserId()));
		
		List<WorkflowDelegate> searchList = workflowDelegateDAO
				.findByCondition(pageDTO, sortDTO, conditionDTO, companyId);
		for (WorkflowDelegate workflowDelegateVO : searchList) {
			WorkFlowDelegateForm workFlowDelegateForm = new WorkFlowDelegateForm();
			
			//workFlowDelegateForm.setEmployeeName;
			
			/* ID ENCRYPT
			 * */
			workFlowDelegateForm.setWorkFlowDelegateId(FormatPreserveCryptoUtil.encrypt(workflowDelegateVO
					.getWorkflowDelegateId()));
			String setDelegateTo = "";
			setDelegateTo += workflowDelegateVO.getEmployee2().getFirstName()
					+ " ";

			if (StringUtils.isNotBlank(workflowDelegateVO.getEmployee2()
					.getLastName())) {
				setDelegateTo += workflowDelegateVO.getEmployee2()
						.getLastName() + " ";
			}
			setDelegateTo += "("
					+ workflowDelegateVO.getEmployee2().getEmployeeNumber()
					+ ")";
			workFlowDelegateForm.setDelegateTo(setDelegateTo);
			workFlowDelegateForm.setDelegateToId(workflowDelegateVO
					.getEmployee2().getEmployeeId());

			String setUser = "";
			setUser += workflowDelegateVO.getEmployee1().getFirstName() + " ";

			if (StringUtils.isNotBlank(workflowDelegateVO.getEmployee2()
					.getLastName())) {
				setUser += workflowDelegateVO.getEmployee1().getLastName()
						+ " ";
			}
			setUser += "("
					+ workflowDelegateVO.getEmployee1().getEmployeeNumber()
					+ ")";
			workFlowDelegateForm.setDelegateTo(setDelegateTo);

			workFlowDelegateForm.setUser(setUser);
			workFlowDelegateForm.setUserId(workflowDelegateVO.getEmployee1()
					.getEmployeeId());
			if (workflowDelegateVO
					.getWorkflowType()
					.getCodeDesc()
					.equalsIgnoreCase(
							PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET)
					|| workflowDelegateVO
							.getWorkflowType()
							.getCodeDesc()
							.equalsIgnoreCase(
									PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET)
					|| workflowDelegateVO
							.getWorkflowType()
							.getCodeDesc()
							.equalsIgnoreCase(
									PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET)) {
				workFlowDelegateForm
						.setWorkFlowType(PayAsiaConstants.MODULE_TIMESHEET);
			} else {
				workFlowDelegateForm.setWorkFlowType(workflowDelegateVO
						.getWorkflowType().getCodeDesc());
			}

			workFlowDelegateForm
					.setFromDate(DateUtils.timeStampToString(
							workflowDelegateVO.getStartDate(),
							company.getDateFormat()));
			workFlowDelegateForm.setToDate(DateUtils.timeStampToString(
					workflowDelegateVO.getEndDate(), company.getDateFormat()));

			workFlowDelegateFormList.add(workFlowDelegateForm);
		}
		int recordSize = workflowDelegateDAO.getCountForCondition(conditionDTO,
				companyId);
		WorkFlowDelegateResponse response = new WorkFlowDelegateResponse();

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

		response.setWorkFlowDelegateForm(workFlowDelegateFormList);

		return response;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#saveWorkFlowDelegate(com.payasia
	 * .common.form.WorkFlowDelegateForm, java.lang.Long)
	 */
	@Override
	public void saveWorkFlowDelegate(WorkFlowDelegateForm workFlowDelegateForm,
			Long companyId) {

		Employee employee1 = employeeDAO.findById(workFlowDelegateForm
				.getUserId());

		Employee employee2 = employeeDAO.findById(workFlowDelegateForm
				.getDelegateToId());
		Company company = companyDAO.findById(companyId);

		WorkflowDelegate workflowDelegate = new WorkflowDelegate();
		workflowDelegate.setCompany(company);
		AppCodeMaster workFlowType = appCodeMasterDAO
				.findById(workFlowDelegateForm.getWorkFlowTypeValue());
		workflowDelegate.setWorkflowType(workFlowType);
		workflowDelegate.setEmployee1(employee1);
		workflowDelegate.setEmployee2(employee2);

		workflowDelegate.setStartDate(DateUtils.stringToTimestamp(
				workFlowDelegateForm.getFromDate(), company.getDateFormat()));

		workflowDelegate.setEndDate(DateUtils.stringToTimestamp(
				workFlowDelegateForm.getToDate(), company.getDateFormat()));

		workflowDelegate = workflowDelegateDAO.saveReturn(workflowDelegate);

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(company.getCompanyId());
		
		boolean userCCmailCheck = workFlowDelegateForm.isUserCheckForMail();

		generalMailLogic.sendMailToDelgate(companyId, workflowDelegate, null,
				PayAsiaConstants.EMAIL_TEMPLATE_DELEGATE_ADD_SUB_CATEGORY_NAME, isLeaveUnitDays, userCCmailCheck);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.WorkFlowDelegateLogic#deleteWorkFlowDelegate(long)
	 */
	@Override
	public void deleteWorkFlowDelegate(long workflowDelegateId, Long companyId) {
		WorkflowDelegate workflowDelegate = workflowDelegateDAO
				.findByID(workflowDelegateId,companyId);
		workflowDelegateDAO.delete(workflowDelegate);
		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(workflowDelegate.getCompany().getCompanyId());
		generalMailLogic
				.sendMailToDelgate(
						companyId,
						workflowDelegate,
						null,
						PayAsiaConstants.EMAIL_TEMPLATE_DELEGATE_DELETE_SUB_CATEGORY_NAME,
						isLeaveUnitDays);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#getWorkFlowDelegateData(long,
	 * java.lang.Long)
	 */
	@Override
	public WorkFlowDelegateForm getWorkFlowDelegateData(
			long workflowDelegateId, Long companyId) {
		
		Company company = companyDAO.findById(companyId);
		WorkflowDelegate workflowDelegateVO = workflowDelegateDAO
				.findByID(workflowDelegateId,companyId);

		WorkFlowDelegateForm workFlowDelegateForm = new WorkFlowDelegateForm();

		workFlowDelegateForm.setWorkFlowDelegateId(workflowDelegateVO
				.getWorkflowDelegateId());

		String delegateeTo = "";
		delegateeTo += workflowDelegateVO.getEmployee2().getFirstName();
		if (StringUtils.isNotBlank(workflowDelegateVO.getEmployee2()
				.getLastName())) {
			delegateeTo += workflowDelegateVO.getEmployee2().getLastName();
		}
		delegateeTo += " ("
				+ workflowDelegateVO.getEmployee2().getEmployeeNumber() + ")";
		workFlowDelegateForm.setDelegateTo(delegateeTo);
		workFlowDelegateForm.setDelegateToId(workflowDelegateVO.getEmployee2()
				.getEmployeeId());

		String setUser = "";
		setUser += workflowDelegateVO.getEmployee1().getFirstName();
		if (StringUtils.isNotBlank(workflowDelegateVO.getEmployee2()
				.getLastName())) {
			setUser += workflowDelegateVO.getEmployee1().getLastName();
		}
		setUser += " (" + workflowDelegateVO.getEmployee1().getEmployeeNumber()
				+ ")";
		workFlowDelegateForm.setUser(setUser);
		workFlowDelegateForm.setUserId(workflowDelegateVO.getEmployee1()
				.getEmployeeId());

		workFlowDelegateForm.setWorkFlowTypeValue(workflowDelegateVO
				.getWorkflowType().getAppCodeID());
		workFlowDelegateForm.setFromDate(DateUtils.timeStampToString(
				workflowDelegateVO.getStartDate(), company.getDateFormat()));
		workFlowDelegateForm.setToDate(DateUtils.timeStampToString(
				workflowDelegateVO.getEndDate(), company.getDateFormat()));

		return workFlowDelegateForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#updateWorkFlowDelegate(com.payasia
	 * .common.form.WorkFlowDelegateForm, java.lang.Long, java.lang.Long)
	 */
	@Override
	public void updateWorkFlowDelegate(
			WorkFlowDelegateForm workFlowDelegateForm, Long workflowDelegateId,
			Long companyId) {
		Employee employee1 = employeeDAO.findById(workFlowDelegateForm
				.getUserId());

		Employee employee2 = employeeDAO.findById(workFlowDelegateForm
				.getDelegateToId());
		Company company = companyDAO.findById(companyId);

		WorkflowDelegate workflowDelegateVO = workflowDelegateDAO
				.findByID(workflowDelegateId,companyId);
		WorkflowDelegate preWorkflowDelegate = new WorkflowDelegate();
		preWorkflowDelegate.setEmployee1(workflowDelegateVO.getEmployee1());
		preWorkflowDelegate.setEmployee2(workflowDelegateVO.getEmployee2());
		preWorkflowDelegate.setStartDate(workflowDelegateVO.getStartDate());
		preWorkflowDelegate.setEndDate(workflowDelegateVO.getEndDate());
		preWorkflowDelegate.setWorkflowType(workflowDelegateVO
				.getWorkflowType());
		workflowDelegateVO.setCompany(company);
		workflowDelegateVO.setEmployee1(employee1);
		workflowDelegateVO.setEmployee2(employee2);
		AppCodeMaster workFlowType = appCodeMasterDAO
				.findById(workFlowDelegateForm.getWorkFlowTypeValue());
		workflowDelegateVO.setWorkflowType(workFlowType);
		workflowDelegateVO.setStartDate(DateUtils.stringToTimestamp(
				workFlowDelegateForm.getFromDate(), company.getDateFormat()));

		workflowDelegateVO.setEndDate(DateUtils.stringToTimestamp(
				workFlowDelegateForm.getToDate(), company.getDateFormat()));

		workflowDelegateDAO.update(workflowDelegateVO);

		PreviousWorkflowDTO previousWorkflowDTO = new PreviousWorkflowDTO();
		previousWorkflowDTO.setPreFromDate(DateUtils
				.timeStampToString(preWorkflowDelegate.getStartDate()));
		previousWorkflowDTO.setPreToDate(DateUtils
				.timeStampToString(preWorkflowDelegate.getEndDate()));
		previousWorkflowDTO.setPreWorkflow(preWorkflowDelegate
				.getWorkflowType().getCodeDesc());

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic
				.isLeaveUnitDays(company.getCompanyId());

		boolean userCCmailCheck = workFlowDelegateForm.isUserCheckForMail();
		
		if (preWorkflowDelegate.getEmployee2().getEmployeeId() != workflowDelegateVO
				.getEmployee2().getEmployeeId()) {

			generalMailLogic
					.sendMailToDelgate(
							companyId,
							workflowDelegateVO,
							null,
							PayAsiaConstants.EMAIL_TEMPLATE_DELEGATE_ADD_SUB_CATEGORY_NAME,
							isLeaveUnitDays, userCCmailCheck);

			generalMailLogic
					.sendMailToDelgate(
							companyId,
							preWorkflowDelegate,
							null,
							PayAsiaConstants.EMAIL_TEMPLATE_DELEGATE_DELETE_SUB_CATEGORY_NAME,
							isLeaveUnitDays, userCCmailCheck);

		} else if ((preWorkflowDelegate.getEmployee2().getEmployeeId() == workflowDelegateVO
				.getEmployee2().getEmployeeId())
				&& (!preWorkflowDelegate.getStartDate().equals(
						workflowDelegateVO.getStartDate())
						|| !preWorkflowDelegate.getEndDate().equals(
								workflowDelegateVO.getEndDate())
						|| preWorkflowDelegate.getEmployee1().getEmployeeId() != workflowDelegateVO
								.getEmployee1().getEmployeeId() || preWorkflowDelegate
						.getWorkflowType().getAppCodeID() != workflowDelegateVO
						.getWorkflowType().getAppCodeID())) {
			generalMailLogic
					.sendMailToDelgate(
							companyId,
							workflowDelegateVO,
							previousWorkflowDTO,
							PayAsiaConstants.EMAIL_TEMPLATE_DELEGATE_UPDATE_SUB_CATEGORY_NAME,
							isLeaveUnitDays, userCCmailCheck);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.logic.WorkFlowDelegateLogic#getWorkflowTypeList()
	 */
	@Override
	public List<WorkFlowDelegateForm> getWorkflowTypeList(
			CompanyModuleDTO companyModuleDTO) {
		Locale locale = UserContext.getLocale();
		List<AppCodeMaster> appCodeMasterList = appCodeMasterDAO
				.findByConditionWorkFlowDelegate("Workflow Type");

		List<WorkFlowDelegateForm> workflowTypeList = new ArrayList<WorkFlowDelegateForm>();
		int moduleCount = 1;

		if (companyModuleDTO.isHasClaimModule()) {
			moduleCount++;
		}
		if (companyModuleDTO.isHasLeaveModule()) {
			moduleCount++;
		}
		if (companyModuleDTO.isHasLundinTimesheetModule()) {
			moduleCount++;
		}
		if (companyModuleDTO.isHasLionTimesheetModule()) {
			moduleCount++;
		}
		if (companyModuleDTO.isHasCoherentTimesheetModule()) {
			moduleCount++;
		}

		for (AppCodeMaster appCodeMasterVO : appCodeMasterList) {

			if (appCodeMasterVO.getCodeDesc().equals(
					PayAsiaConstants.COMPANY_MODULE_CLAIM)
					&& !companyModuleDTO.isHasClaimModule()) {
				continue;
			}

			if (appCodeMasterVO.getCodeDesc().equals(
					PayAsiaConstants.COMPANY_MODULE_LEAVE)
					&& !companyModuleDTO.isHasLeaveModule()) {
				continue;
			}

			if (appCodeMasterVO.getCodeDesc().equals(
					PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET)
					&& !companyModuleDTO.isHasLundinTimesheetModule()) {
				continue;
			}
			if (appCodeMasterVO.getCodeDesc().equals(
					PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET)
					&& !companyModuleDTO.isHasLionTimesheetModule()) {
				continue;
			}
			if (appCodeMasterVO.getCodeDesc().equals(
					PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET)
					&& !companyModuleDTO.isHasCoherentTimesheetModule()) {
				continue;
			}

			if (moduleCount > 1) {
				WorkFlowDelegateForm workflowTypeForm = new WorkFlowDelegateForm();
				workflowTypeForm.setWorkFlowTypeId(appCodeMasterVO
						.getAppCodeID());
				if (StringUtils.isNotBlank(appCodeMasterVO.getLabelKey())) {
					try {
						String labelMsg = messageSource.getMessage(
								appCodeMasterVO.getLabelKey(), new Object[] {},
								locale);
						workflowTypeForm.setWorkFlowType(labelMsg);
					} catch (org.springframework.context.NoSuchMessageException nsme) {
						workflowTypeForm.setWorkFlowType(appCodeMasterVO
								.getCodeDesc());
						LOGGER.error(nsme.getMessage(), nsme);
					}
				} else {
					workflowTypeForm.setWorkFlowType(appCodeMasterVO
							.getCodeDesc());
				}

				workflowTypeForm.setWorkFlowTypeValue(appCodeMasterVO
						.getAppCodeID());
				
				workflowTypeList.add(workflowTypeForm);
			} else {
				if (!appCodeMasterVO.getCodeDesc().equals(
						PayAsiaConstants.COMPANY_MODULE_ALL)) {
					WorkFlowDelegateForm workflowTypeForm = new WorkFlowDelegateForm();
					workflowTypeForm.setWorkFlowTypeId(appCodeMasterVO
							.getAppCodeID());
					if (StringUtils.isNotBlank(appCodeMasterVO.getLabelKey())) {
						String labelMsg = messageSource.getMessage(
								appCodeMasterVO.getLabelKey(), new Object[] {},
								locale);
						workflowTypeForm.setWorkFlowType(labelMsg);
					} else {
						workflowTypeForm.setWorkFlowType(appCodeMasterVO
								.getCodeDesc());
					}
					workflowTypeForm.setWorkFlowTypeValue(appCodeMasterVO
							.getAppCodeID());
			
					workflowTypeList.add(workflowTypeForm);
				}
			}

		}
		return workflowTypeList;
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
	public WorkFlowDelegateResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String empName,
			String empNumber, Long companyId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		if (StringUtils.isNotBlank(empName)) {
			conditionDTO.setEmployeeName("%" + empName.trim() + "%");
		}

		if (StringUtils.isNotBlank(empNumber)) {
			conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

		}

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		int recordSize = employeeDAO.getCountForCondition(conditionDTO,
				companyId);

		List<Employee> finalList = employeeDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);

		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		for (Employee employee : finalList) {
			EmployeeListForm employeeForm = new EmployeeListForm();
			String empNameStr = "";
			if (StringUtils.isNotBlank(employee.getFirstName())) {
				empNameStr += employee.getFirstName() + " ";
			}
			if (StringUtils.isNotBlank(employee.getLastName())) {
				empNameStr += employee.getLastName();
			}
			employeeForm.setEmployeeName(empNameStr);

			employeeForm.setEmployeeName(employeeDetailLogic
					.getEmployeeName(employee));

			employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
			employeeForm.setCompanyName(employee.getCompany().getCompanyName());
			employeeForm.setEmployeeID(employee.getEmployeeId());
			employeeListFormList.add(employeeForm);
		}

		WorkFlowDelegateResponse response = new WorkFlowDelegateResponse();
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

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	@Override
	public WorkFlowDelegateResponse viewEmployeeWorkFlowDelegate(
			PageRequest pageDTO, SortCondition sortDTO, String criteria,
			String keyword, String workFlowType, Long companyId,
			Long employeeId, CompanyModuleDTO companyModuleDTO) {
		Company company = companyDAO.findById(companyId);
		WorkflowDelegateConditionDTO conditionDTO = new WorkflowDelegateConditionDTO();
		conditionDTO.setEmployeeId(employeeId);
		List<WorkFlowDelegateForm> workFlowDelegateFormList = new ArrayList<WorkFlowDelegateForm>();
		if (criteria.equals(PayAsiaConstants.WORK_FLOW_DELEGATE_EMP_NUM)) {
			if (StringUtils.isNotBlank(keyword)) {

				try {
					conditionDTO.setEmployeeNumber("%"
							+ URLDecoder.decode(keyword, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (criteria.equals(PayAsiaConstants.WORK_FLOW_DELEGATE_EMP_NAME)) {
			if (StringUtils.isNotBlank(keyword)) {
				try {
					conditionDTO.setEmployeeName("%"
							+ URLDecoder.decode(keyword, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (StringUtils.isNotBlank(workFlowType)) {

			List<Long> appCodeIds = new ArrayList<>();
			AppCodeMaster appCodeMaster = appCodeMasterDAO.findById(Long
					.parseLong(workFlowType));
			if (appCodeMaster.getCodeDesc().equals(
					PayAsiaConstants.WORK_FLOW_DELEGATE_ALL)) {

				List<AppCodeMaster> workFlowTypes = appCodeMasterDAO
						.findByConditionWorkFlowDelegate(PayAsiaConstants.APP_CODE_WORKFLOW_TYPE);
				Map<String, Long> workFlowMap = new HashMap<>();
				for (AppCodeMaster appCode : workFlowTypes) {
					workFlowMap.put(appCode.getCodeDesc(),
							appCode.getAppCodeID());
				}

				if (companyModuleDTO.isHasClaimModule()) {
					appCodeIds.add(workFlowMap
							.get(PayAsiaConstants.COMPANY_MODULE_CLAIM));

				}
				if (companyModuleDTO.isHasLeaveModule()) {
					appCodeIds.add(workFlowMap
							.get(PayAsiaConstants.COMPANY_MODULE_LEAVE));
				}
				if (companyModuleDTO.isHasHrisModule()) {
					appCodeIds.add(workFlowMap
							.get(PayAsiaConstants.COMPANY_MODULE_HRIS));
				}
				if (companyModuleDTO.isHasLundinTimesheetModule()) {
					appCodeIds
							.add(workFlowMap
									.get(PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET));
				}
				if (companyModuleDTO.isHasLionTimesheetModule()) {
					appCodeIds
							.add(workFlowMap
									.get(PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET));
				}
				if (companyModuleDTO.isHasCoherentTimesheetModule()) {
					appCodeIds
							.add(workFlowMap
									.get(PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET));
				}
				appCodeIds.add(appCodeMaster.getAppCodeID());

			} else {
				appCodeIds.add(appCodeMaster.getAppCodeID());
			}
			conditionDTO.setAppCodeIds(appCodeIds);
			try {
				conditionDTO.setWorkFlowType(URLDecoder.decode(workFlowType,
						"UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}

		}

		List<WorkflowDelegate> searchList = workflowDelegateDAO
				.findByCondition(pageDTO, sortDTO, conditionDTO, companyId);
		for (WorkflowDelegate workflowDelegateVO : searchList) {
			WorkFlowDelegateForm workFlowDelegateForm = new WorkFlowDelegateForm();
			/* ID ENCRYPT*/
			workFlowDelegateForm.setWorkFlowDelegateId(FormatPreserveCryptoUtil.encrypt(workflowDelegateVO
					.getWorkflowDelegateId()));
			String setDelegateTo = "";
			setDelegateTo += workflowDelegateVO.getEmployee2().getFirstName()
					+ " ";

			if (StringUtils.isNotBlank(workflowDelegateVO.getEmployee2()
					.getLastName())) {
				setDelegateTo += workflowDelegateVO.getEmployee2()
						.getLastName() + " ";
			}
			setDelegateTo += "("
					+ workflowDelegateVO.getEmployee2().getEmployeeNumber()
					+ ")";
			workFlowDelegateForm.setDelegateTo(setDelegateTo);
			workFlowDelegateForm.setDelegateToId(workflowDelegateVO
					.getEmployee2().getEmployeeId());

			String setUser = "";
			setUser += workflowDelegateVO.getEmployee1().getFirstName() + " ";

			if (StringUtils.isNotBlank(workflowDelegateVO.getEmployee1()
					.getLastName())) {
				setUser += workflowDelegateVO.getEmployee1().getLastName()
						+ " ";
			}
			setUser += "("
					+ workflowDelegateVO.getEmployee1().getEmployeeNumber()
					+ ")";
			workFlowDelegateForm.setDelegateTo(setDelegateTo);

			workFlowDelegateForm.setUser(setUser);
			workFlowDelegateForm.setUserId(workflowDelegateVO.getEmployee1()
					.getEmployeeId());
			if (workflowDelegateVO
					.getWorkflowType()
					.getCodeDesc()
					.equalsIgnoreCase(
							PayAsiaConstants.COMPANY_MODULE_LUNDIN_TIMESHEET)
					|| workflowDelegateVO
							.getWorkflowType()
							.getCodeDesc()
							.equalsIgnoreCase(
									PayAsiaConstants.COMPANY_MODULE_LION_TIMESHEET)
					|| workflowDelegateVO
							.getWorkflowType()
							.getCodeDesc()
							.equalsIgnoreCase(
									PayAsiaConstants.COMPANY_MODULE_COHERENT_TIMESHEET)) {
				workFlowDelegateForm
						.setWorkFlowType(PayAsiaConstants.MODULE_TIMESHEET);
			} else {
				workFlowDelegateForm.setWorkFlowType(workflowDelegateVO
						.getWorkflowType().getCodeDesc());
			}

			workFlowDelegateForm
					.setFromDate(DateUtils.timeStampToString(
							workflowDelegateVO.getStartDate(),
							company.getDateFormat()));
			workFlowDelegateForm.setToDate(DateUtils.timeStampToString(
					workflowDelegateVO.getEndDate(), company.getDateFormat()));

			workFlowDelegateFormList.add(workFlowDelegateForm);
		}
		int recordSize = workflowDelegateDAO.getCountForCondition(conditionDTO,
				companyId);
		WorkFlowDelegateResponse response = new WorkFlowDelegateResponse();

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

		response.setWorkFlowDelegateForm(workFlowDelegateFormList);

		return response;
	}

	@Override
	public LeaveReviewerResponseForm searchGroupEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId) {
		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		Company company = companyDAO.findById(companyId);

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
		conditionDTO.setGroupId(company.getCompanyGroup().getGroupId());

		/*
		 * recordSize += employeeDAO.getGroupCompanyEmployeeCount(conditionDTO,
		 * company.getCompanyId());
		 */

		List<Employee> finalList = employeeDAO.findEmployeesOfGroupCompanies(
				conditionDTO, pageDTO, sortDTO, company.getCompanyId());

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

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
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

	@Override
	public List<EmployeeListForm> getEmployeeId(Long companyId,
			String searchString, Long employeeId) {

		List<EmployeeListForm> leaveBalanceSummaryFormList = new ArrayList<EmployeeListForm>();

		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		List<Employee> employeeNumberList = employeeDAO.getEmployeeIds(
				searchString, companyId, employeeShortListDTO);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.WorkFlowDelegateLogic#searchEmployee(com.payasia.common
	 * .form.PageRequest, com.payasia.common.form.SortCondition,
	 * java.lang.String, java.lang.String, java.lang.Long)
	 */
	@Override
	public LeaveReviewerResponseForm searchWorkflowEmployee(
			PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();

		Company company = companyDAO.findById(companyId);
		List<Long> companyIds = companyDAO.getDistinctAssignedGroupCompanies(
				employeeId, company.getCompanyGroup().getGroupId());

		for (Long assignedCompanyId : companyIds) {

			EmployeeShortListDTO employeeShortListDTO = generalLogic
					.getShortListEmployeeIds(employeeId, assignedCompanyId);
			conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);

			if (StringUtils.isNotBlank(empName)) {
				conditionDTO.setEmployeeName("%" + empName.trim() + "%");
			}

			if (StringUtils.isNotBlank(empNumber)) {
				conditionDTO.setEmployeeNumber("%" + empNumber.trim() + "%");

			}
			employeeDAO.getCountForCondition(conditionDTO, assignedCompanyId);

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

		LeaveReviewerResponseForm response = new LeaveReviewerResponseForm();

		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

}
