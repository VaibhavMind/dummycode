package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.OTBatchForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface OTBatchDefinitionLogic {

	OTBatchForm checkOTItem(Long companyId, OTBatchForm oTBatchForm,
			Long OtBatchId);

	void saveOTItem(OTBatchForm oTBatchForm, Long companyId);

	OTBatchForm getOTBatchList(String searchCriteria, String searchKeyword,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	void updateOTItem(OTBatchForm oTBatchForm, Long companyId);

	void deleteOTBatch(Long otBatchId);

}
