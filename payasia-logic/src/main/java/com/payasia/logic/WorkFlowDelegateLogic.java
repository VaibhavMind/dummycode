package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateForm;
import com.payasia.common.form.WorkFlowDelegateResponse;

/**
 * The Interface WorkFlowDelegateLogic.
 */
/**
 * @author vivekjain
 * 
 */
@Transactional
public interface WorkFlowDelegateLogic {

	/**
	 * Save the work flow delegate.
	 * 
	 * @param workFlowDelegateForm
	 *            the work flow delegate form
	 * @param companyId
	 */
	void saveWorkFlowDelegate(WorkFlowDelegateForm workFlowDelegateForm,
			Long companyId);

	/**
	 * Delete work flow delegate.
	 * 
	 * @param companyId
	 * 
	 * @param configId
	 *            the config id
	 */
	void deleteWorkFlowDelegate(long workflowDelegateId, Long companyId);

	/**
	 * Gets the work flow delegate data.
	 * 
	 * @param configId
	 *            the config id
	 * @return the work flow delegate data
	 */
	WorkFlowDelegateForm getWorkFlowDelegateData(long workflowDelegateId,
			Long companyId);

	/**
	 * purpose : view WorkFlow Delegate List on search criteria.
	 * 
	 * @param companyModuleDTO
	 * 
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 * @param String
	 *            the criteria
	 * @param String
	 *            the keyword
	 * @param String
	 *            the workFlowType
	 * @param Long
	 *            the companyId
	 * @return WorkFlowDelegateResponse contains All Employee list
	 */
	WorkFlowDelegateResponse getWorkFlowDelegateList(PageRequest pageDTO,
			SortCondition sortDTO, String criteria, String keyword,
			String workFlowType, Long companyId,
			CompanyModuleDTO companyModuleDTO);

	/**
	 * purpose : update WorkFlow Delegate.
	 * 
	 * @param WorkFlowDelegateForm
	 *            the workFlowDelegateForm
	 * @param long the workflowDelegateId
	 * @param Long
	 *            the companyId
	 */
	void updateWorkFlowDelegate(WorkFlowDelegateForm workFlowDelegateForm,
			Long workflowDelegateId, Long companyId);

	/**
	 * purpose : get Workflow Type List.
	 * 
	 * @param companyId
	 * 
	 * @return WorkFlowDelegateForm contains WorkflowTypeList
	 */
	List<WorkFlowDelegateForm> getWorkflowTypeList(
			CompanyModuleDTO companyModuleDTO);

	/**
	 * purpose : search Employee for workFlow Delegatee.
	 * 
	 * @param employeeId
	 * 
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 * @param String
	 *            the empName
	 * @param String
	 *            the empNumber
	 * @param Long
	 *            the companyId
	 * @return WorkFlowDelegateResponse contains All Employee list
	 */
	WorkFlowDelegateResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId, String empName,
			String empNumber, Long companyId);

	WorkFlowDelegateResponse viewEmployeeWorkFlowDelegate(PageRequest pageDTO,
			SortCondition sortDTO, String criteria, String keyword,
			String workFlowType, Long companyId, Long employeeId,
			CompanyModuleDTO companyModuleDTO);

	LeaveReviewerResponseForm searchGroupEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

	List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId,
			String searchString, Long employeeId);

	List<EmployeeListForm> getEmployeeId(Long companyId, String searchString,
			Long employeeId);

	LeaveReviewerResponseForm searchWorkflowEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

}
