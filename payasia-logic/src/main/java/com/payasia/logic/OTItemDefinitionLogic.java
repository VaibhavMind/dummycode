package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.OTItemDefinitionForm;
import com.payasia.common.form.OTItemDefinitionResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface OTItemDefinitionLogic {
	OTItemDefinitionResponse getOTItemList(String searchCriteria,
			String keyword, PageRequest pageDTO, SortCondition sortDTO,
			Long companyIds);

	void updateOTItem(OTItemDefinitionForm otItemDefinitionForm, Long otItemId);

	void saveOTItem(OTItemDefinitionForm otItemDefinitionForm, Long companyId);

	void deleteOTItem(Long otItemId);

	OTItemDefinitionForm checkOTItem(Long companyId,
			OTItemDefinitionForm otItemDefinitionForm, Long itemId);
}
