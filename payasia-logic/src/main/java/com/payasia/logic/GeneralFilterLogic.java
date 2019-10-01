package com.payasia.logic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.payasia.common.form.GeneralFilterDTO;
import com.payasia.dao.bean.DataDictionary;

/**
 * @author ragulapraveen
 * 
 */
public interface GeneralFilterLogic {

	/**
	 * 
	 * @param employeeId
	 * @param finalFilterList
	 * @return
	 */

	String createWhere(Long employeeId, List<GeneralFilterDTO> finalFilterList,
			Long companyId, Map<String, String> paramValueMap);

	String getFieldDataType(Long companyId, DataDictionary dataDictionary);

	String createFiltersWhere(Long employeeId,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Date currentdate, Map<String, String> paramValueMap);

	String createSubWhereWithCount(String where,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Date currentdate, Map<String, String> paramValueMap, int count);

	String createSubWhere(String where, List<GeneralFilterDTO> finalFilterList,
			Long companyId, Date currentdate, Map<String, String> paramValueMap);

	String createWhereWithCount(Long employeeId,
			List<GeneralFilterDTO> finalFilterList, Long companyId,
			Map<String, String> paramValueMap, int count);

}