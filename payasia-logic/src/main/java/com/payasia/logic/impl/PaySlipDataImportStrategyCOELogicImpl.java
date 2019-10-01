package com.payasia.logic.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.DataImportParametersDTO;
import com.payasia.common.exception.PayAsiaDataException;
import com.payasia.dao.bean.Payslip;
import com.payasia.logic.PaySlipDataImportStrategy;
import com.payasia.logic.data.PayslipDataImportAbstract;

@Component
public class PaySlipDataImportStrategyCOELogicImpl extends
		PayslipDataImportAbstract implements PaySlipDataImportStrategy {
	private static final Logger LOGGER = Logger
			.getLogger(PaySlipDataImportStrategyCOELogicImpl.class);

	@Override
	public void insertNew(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		try {
			super.insertNew(payslip, dataImportParametersDTO, companyId);
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}

	@Override
	public void updateORInsert(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {

		try {
			super.updateORInsert(payslip, dataImportParametersDTO, companyId);
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}

	@Override
	public void deleteAndInsert(Payslip payslip,
			DataImportParametersDTO dataImportParametersDTO, Long companyId) {
		try {
			super.deleteAndInsert(payslip, dataImportParametersDTO, companyId);
		} catch (PayAsiaDataException pde) {
			LOGGER.error(pde.getMessage(), pde);
			throw new PayAsiaDataException(pde.getErrors());
		}

	}
}
