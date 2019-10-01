package com.payasia.logic.impl;

/**
 * @author vivekjain
 *
 */
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.AccessControlConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.form.AdminAccessControlForm;
import com.payasia.common.form.CustomAdminAccessControlResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.AdminAccessControlLogic;
import com.payasia.logic.GeneralLogic;

/**
 * The Class AdminAccessControlLogicImpl.
 */

@Component
public class AdminAccessControlLogicImpl implements AdminAccessControlLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AdminAccessControlLogicImpl.class);

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	GeneralLogic generalLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.AdminAccessControlLogic#searchEmployee(java.lang.String
	 * , java.lang.String, java.lang.String,
	 * com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition, java.lang.Long)
	 */
	@Override
	public CustomAdminAccessControlResponse searchEmployee(
			String searchCondition, String searchText, String employeeStatus,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long employeeId) {
		List<AdminAccessControlForm> adminAccessControlFormList = new ArrayList<AdminAccessControlForm>();
		AccessControlConditionDTO conditionDTO = new AccessControlConditionDTO();
		if (searchCondition.equals(PayAsiaConstants.ACCESS_CONTROL_EMP_NO)) {
			if (StringUtils.isNotBlank(searchText)) {

				try {
					conditionDTO.setEmployeeNumber("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.ACCESS_CONTROL_EMPNAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setEmployeeName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.ACCESS_CONTROL_FIRSTNAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setFirstName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}
		if (searchCondition.equals(PayAsiaConstants.ACCESS_CONTROL_LASTNAME)) {
			if (StringUtils.isNotBlank(searchText)) {
				try {
					conditionDTO.setLastName("%"
							+ URLDecoder.decode(searchText, "UTF-8") + "%");
				} catch (UnsupportedEncodingException ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}

		}

		if (StringUtils.isNotBlank(employeeStatus)) {
			try {
				conditionDTO.setEmployeeStatus(URLDecoder.decode(
						employeeStatus, "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}

		}
		EmployeeShortListDTO employeeShortListDTO = generalLogic
				.getShortListEmployeeIds(employeeId, companyId);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		List<Employee> searchList = employeeDAO.findByCondition(conditionDTO,
				pageDTO, sortDTO, companyId);
		for (Employee employee : searchList) {
			AdminAccessControlForm adminAccessControlForm = new AdminAccessControlForm();
			adminAccessControlForm.setEmployeeNumber(employee
					.getEmployeeNumber());
			adminAccessControlForm.setEmployeeName(employee.getFirstName());
			adminAccessControlForm.setFirstName(employee.getFirstName());
			if (StringUtils.isNotBlank(employee.getLastName())) {
				adminAccessControlForm.setLastName(employee.getLastName());
			}

			if (employee.isStatus() == true) {
				adminAccessControlForm
						.setStatus(PayAsiaConstants.ACCESS_CONTROL_ENABLED);
			}
			if (employee.isStatus() == false) {
				adminAccessControlForm
						.setStatus(PayAsiaConstants.ACCESS_CONTROL_DISABLED);
			}

			adminAccessControlForm.setEmployeeId(employee.getEmployeeId());
			adminAccessControlFormList.add(adminAccessControlForm);
		}
		int recordSize = employeeDAO.getCountForCondition(conditionDTO,
				companyId).intValue();
		CustomAdminAccessControlResponse response = new CustomAdminAccessControlResponse();
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
		response.setRows(adminAccessControlFormList);
		response.setRecords(recordSize);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.AdminAccessControlLogic#enableEmployee(java.lang.String
	 * [])
	 */
	@Override
	public void enableEmployee(String[] employeeId,Long companyId) {
		Employee employee;
		for (int count = 0; count < employeeId.length; count++) {
			employee = employeeDAO.findById(Long.parseLong(employeeId[count]),companyId);
			if (employee.isStatus() == false) {
				employee.setStatus(true);
				employeeDAO.update(employee);
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.logic.AdminAccessControlLogic#disableEmployee(java.lang.String
	 * [])
	 */
	@Override
	public void disableEmployee(String[] employeeId,Long companyId) {
		Employee employee;
		for (int count = 0; count < employeeId.length; count++) {
			employee = employeeDAO.findById(Long.parseLong(employeeId[count]),companyId);
			if (employee.isStatus() == true) {
				employee.setStatus(false);
				employeeDAO.update(employee);
			}

		}
	}
}
