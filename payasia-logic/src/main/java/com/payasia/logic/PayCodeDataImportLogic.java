package com.payasia.logic;

import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.dao.bean.EmpDataImportTemplateField;

@Transactional
public interface PayCodeDataImportLogic {

	List<DataImportLogDTO> saveValidDataFile(
			List<HashMap<String, DataImportKeyValueDTO>> keyValMapList,
			long templateId, long entityId,
			HashMap<String, EmpDataImportTemplateField> colMap,
			String uploadType, String transactionType, Long companyId);

}
