package com.payasia.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.common.dto.DynamicTableDTO;
import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.dao.bean.DataDictionary;

@Transactional
public interface FiltersInfoUtilsLogic {

	void getDynamicDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs);

	void getStaticDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO);

	void getDynamicDictionaryInfo(DataDictionary dataDictionary,
			DataImportKeyValueDTO dataImportKeyValueDTO,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			HashMap<Long, Tab> tabMap);

	String createQueryFilters(Long employeeId, List<Long> formIds,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Long companyId, Date currentDate, Map<String, String> paramValueMap);

	String createQuery(Long employeeId, List<Long> formIds,
			List<GeneralFilterDTO> finalFilterList,
			Set<DynamicTableDTO> tableNames, List<CodeDescDTO> codeDescDTOs,
			Long companyId, Map<String, String> currentDate);

}
