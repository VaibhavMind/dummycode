package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.DynamicFormRecord;

public interface DynamicFormRecordDAO {

	void save(DynamicFormRecord dynamicFormRecord);

	Long getMaxFormId(Long companyId, Long entityId, Long formId);

	DynamicFormRecord findById(Long recordId);

	void update(DynamicFormRecord dynamicFormRecord);

	DynamicFormRecord getEmpRecords(Long entityKey, Integer version,
			Long dynamicFormId, Long entityId, Long companyId);

	void delete(DynamicFormRecord dynamicFormRecord);

	List<DynamicFormRecord> findByEntityKey(Long entityKey, Long entityId,
			Long companyId);

	List<DynamicFormRecord> findByFormId(Long formId, Long entityId,
			Long companyId, Integer version);

	List<String> findDataForDictionary(Long formId, Long companyId,
			String colNumber, Long entityId);

	DynamicFormRecord saveReturn(DynamicFormRecord dynamicFormRecord);

	List<String> getExistanceOfCodeDescDynField(String colNumber,
			String fieldRefValueId);

	void updateEmployeeExternalId(Long companyId, Long entityKey,
			Long entityId, Long formId, Long recordId, String columnName,
			String externalId);

}
