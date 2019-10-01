package com.payasia.logic.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.EmployeeDataImportStrategy;
import com.payasia.logic.data.EmployeeDataImportAbstract;

@Component
public class EmployeeDataImportStrategyROELogicImpl extends
		EmployeeDataImportAbstract implements EmployeeDataImportStrategy {

	private Set<String> employeeTableRecordDeletedSet = null;
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDataImportStrategyROELogicImpl.class);

	@Override
	public void insertNew(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		try {
			super.insertNew(employee, dataImportParametersDTO, companyId);
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}

	@Override
	public void updateORInsert(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {

		try {
			super.updateORInsert(employee, dataImportParametersDTO, companyId);
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}

	@Override
	public void deleteAndInsert(Employee employee,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		try {
			employeeTableRecordDeletedSet = new HashSet<>();
			super.deleteAndInsert(employee, dataImportParametersDTO, companyId,
					employeeTableRecordDeletedSet);
			employeeTableRecordDeletedSet.add(employee.getEmployeeNumber());
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}
}
