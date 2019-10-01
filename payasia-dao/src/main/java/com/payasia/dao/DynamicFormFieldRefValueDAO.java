package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.dao.bean.DynamicFormFieldRefValue;

public interface DynamicFormFieldRefValueDAO {

	void save(DynamicFormFieldRefValue dynamicFormFieldRefValue);

	void update(DynamicFormFieldRefValue dynamicFormFieldRefValue);

	void delete(DynamicFormFieldRefValue dynamicFormFieldRefValue);

	DynamicFormFieldRefValue findById(Long fieldRefId);

	List<DynamicFormFieldRefValue> findByDataDictionayId(Long dataDictionaryId,
			LundinConditionDTO condition);

	DynamicFormFieldRefValue findByCondition(Long dataDictionaryId, String code);

	DynamicFormFieldRefValue saveReturn(DynamicFormFieldRefValue saveObj);

	DynamicFormFieldRefValue newTranSaveReturn(DynamicFormFieldRefValue saveObj);

	List<DynamicFormFieldRefValue> findByCondition(Long companyId,
			Long entityId, Long formId);

	List<DynamicFormFieldRefValue> findByDataDictionayId(Long dataDictionaryId);
	
	List<DynamicFormFieldRefValue> findByDataDictionayId(Long dataDictionaryId,Long companyId);

}
