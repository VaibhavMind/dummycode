package com.payasia.dao;

import com.payasia.dao.bean.DynamicFormSeq;

public interface DynamicFormSeqDAO {

	void save(DynamicFormSeq dynamicFormSeq);

	void update(DynamicFormSeq dynamicFormSeq);

	void delete(DynamicFormSeq dynamicFormSeq);

	Long getNextVal();

}
