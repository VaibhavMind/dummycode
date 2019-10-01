/**
 * @author abhisheksachdeva
 * 
 */
package com.payasia.logic;

import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mind.payasia.xml.bean.Tab;
import com.payasia.common.dto.DataImportKeyValueDTO;
import com.payasia.dao.bean.EmpDataImportTemplateField;

/**
 * The Interface EmployeeDataImportLogic.
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface EmployeeDataImportLogic {

	/**
	 * Purpose: Save valid data for Employee.
	 * 
	 * @param keyValMapList
	 *            the key val map list
	 * @param templateId
	 *            the template id
	 * @param entityId
	 *            the entity id
	 * @param colMap
	 *            the col map
	 * @param uploadType
	 *            the upload type
	 * @param transactionType
	 *            the transaction type
	 * @param companyId
	 *            the company id
	 * @param dynamicFormVersions
	 * @param dynamicFormObjects
	 */
	void saveValidDataFile(
			List<HashMap<String, DataImportKeyValueDTO>> keyValMapList,
			long templateId, long entityId,
			HashMap<String, EmpDataImportTemplateField> colMap,
			String uploadType, String transactionType, Long companyId,
			HashMap<Long, Tab> dynamicFormObjects,
			HashMap<Long, Integer> dynamicFormVersions);

}
