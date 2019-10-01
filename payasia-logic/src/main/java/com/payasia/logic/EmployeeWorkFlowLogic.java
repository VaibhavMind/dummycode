package com.payasia.logic;

/**
 * @author vivekjain
 * 
 */
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.form.EmployeeWorkFlowDelegateResponse;
import com.payasia.common.form.EmployeeWorkFlowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

/**
 * The Interface EmployeeWorkFlowLogic.
 */
@Transactional
public interface EmployeeWorkFlowLogic {
	/**
	 * purpose : Get Employee WorkFlow Data.
	 * 
	 * @param Long
	 *            the employeeId
	 * @return Employee WorkFlow Data
	 */
	String getEmployeeInfo(Long employeeId);

	/**
	 * purpose : get Employee WorkFlow Details List.
	 * 
	 * @param employeeId
	 * 
	 * @param Long
	 *            the companyId
	 * @param String
	 *            the searchString
	 * @return EmployeeWorkFlowForm contains List
	 */
	List<EmployeeWorkFlowForm> getEmployeeWorkFlowDetails(Long companyId,
			String searchString, Long employeeId);

	/**
	 * purpose : Get WorkFlow Type List.
	 * 
	 * @return AppCodeDTO List
	 */
	List<AppCodeDTO> getWorkflowTypeList();

	/**
	 * purpose : Save Employee WorkFlow Details.
	 * 
	 * @param EmployeeWorkFlowForm
	 *            the employeeWorkFlowForm
	 */
	void saveEmployeeWorkFlowDelagateData(
			EmployeeWorkFlowForm employeeWorkFlowForm);

	/**
	 * purpose : View Employee WorkFlow Delegate Data.
	 * 
	 * @param PageRequest
	 *            the pageDTO
	 * @param SortCondition
	 *            the sortDTO
	 * @param Long
	 *            the employeeId
	 * @param Long
	 *            the companyId
	 * @return EmployeeWorkFlowDelegateResponse Contains List Of Employees of
	 *         workFlow
	 */
	EmployeeWorkFlowDelegateResponse viewEmployeeWorkFlowDelegateData(
			PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			Long companyId);

	/**
	 * purpose : Delete Employee WorkFlow Delegate.
	 * 
	 * @param Long
	 *            the workFlowDelegateId
	 * @return response
	 */
	String deleteEmployeeWorkFlowDelegate(Long workFlowDelegateId);

	/**
	 * purpose : Edit Employee WorkFlow Details.
	 * 
	 * @param EmployeeWorkFlowForm
	 */
	void editEmployeeWorkFlowDelagateData(
			EmployeeWorkFlowForm employeeWorkFlowForm);

}
