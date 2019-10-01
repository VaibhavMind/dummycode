package com.payasia.dao;

import com.payasia.dao.bean.EmpDataExportTemplateField;

public interface EmpDataExportTemplateFieldDAO {

	EmpDataExportTemplateField save(
			EmpDataExportTemplateField empDataExportTemplateField);

	void update(EmpDataExportTemplateField empDataExportTemplateField);

	void delete(EmpDataExportTemplateField empDataExportTemplateField);

	EmpDataExportTemplateField findById(Long templateId);

	void deleteByCondition(Long exportTemplateId);

}
