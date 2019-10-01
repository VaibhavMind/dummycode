package com.payasia.logic;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.OTTemplateForm;
import com.payasia.common.form.OTTemplateResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface OTTemplateLogic {

	OTTemplateResponse accessControl(Long companyId, String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO);

	String addOTTemplate(Long companyId, String otTemplateName);

	void deleteOTTemplate(Long companyId, Long otTemplateId);

	OTTemplateForm getOTTemplate(Long companyId, Long otTemplateId);

	String configureOTTemplate(OTTemplateForm otTemplateForm, Long companyId);

	Set<OTTemplateForm> getOTTypeList(Long companyId, Long otTemplateId);

	void addOTType(String[] otTypeId, Long otTemplateId);

	OTTemplateResponse viewOTType(Long otTemplateId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	void editOTType(Long otTemplateTypeId, OTTemplateForm otTemplateForm);

	OTTemplateForm getOTTypeForEdit(Long otTemplateTypeId);

	void deleteOTType(Long otTemplateTypeId, Long otTemplateId);

}
