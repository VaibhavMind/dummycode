package com.payasia.dao;

import com.payasia.dao.bean.DynamicFormTableRecordSeq;

public interface DynamicFormTableRecordSeqDAO {

	void save(DynamicFormTableRecordSeq dynamicFormTableRecordSeq);

	void update(DynamicFormTableRecordSeq dynamicFormTableRecordSeq);

	void delete(DynamicFormTableRecordSeq dynamicFormTableRecordSeq);

	Long getNextVal();

}
