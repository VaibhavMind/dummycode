package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.DynamicFormTableRecord;

public interface DynamicFormTableRecordDAO {

	Long getMaxTableRecordId();

	void save(DynamicFormTableRecord DynamicFormTableRecord);

	List<DynamicFormTableRecord> getTableRecords(Long tid, String sortOrder,
			String sortBy);

	DynamicFormTableRecord findById(Long tableID);

	void update(DynamicFormTableRecord dynamicFormTableRecord);

	void delete(DynamicFormTableRecord dynamicFormTableRecord);

	void deleteByCondition(Long tableID);

	int getMaxSequenceNumber(Long tableId);

	DynamicFormTableRecord findByEffectiveDate(Long tid, String effictiveDate);

	List<String> findDataForDictionary(String colNumber, String tablePosition);

	void deleteByConditionEmployeeTableRecord(Long tableId, Integer seqNo);

	DynamicFormTableRecord findByIdAndSeq(Long tid, Integer seq);

	List<Object> getMaxEffectiveDate(Long tid);

	List<String> getExistanceOfCodeDescDynField(String colNumber, String valueOf);

	DynamicFormTableRecord findByIdAndSeqForHrisManager(Long tid, Integer seq);

	void saveWithFlush(DynamicFormTableRecord dynamicFormTableRecord);

	List<DynamicFormTableRecord> findByTableId(Long tableId);
	

}
