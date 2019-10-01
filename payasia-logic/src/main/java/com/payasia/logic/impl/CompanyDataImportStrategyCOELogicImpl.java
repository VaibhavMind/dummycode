package com.payasia.logic.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.dao.bean.Company;
import com.payasia.logic.CompanyDataImportStrategy;
import com.payasia.logic.data.CompanyDataImportAbstract;

@Component
public class CompanyDataImportStrategyCOELogicImpl extends
		CompanyDataImportAbstract implements CompanyDataImportStrategy {
	private static final Logger LOGGER = Logger
			.getLogger(CompanyDataImportStrategyCOELogicImpl.class);
	private Set<String> companyTableRecordDeletedSet = new HashSet<>();

	@Override
	public void insertNew(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		try {
			super.insertNew(company, dataImportParametersDTO, companyId);
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}

	@Override
	public void updateORInsert(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {

		try {
			super.updateORInsert(company, dataImportParametersDTO, companyId);
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}

	@Override
	public void deleteAndInsert(Company company,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		try {
			super.deleteAndInsert(company, dataImportParametersDTO, companyId,
					companyTableRecordDeletedSet);
			companyTableRecordDeletedSet.add(company.getCompanyCode());
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}

}
