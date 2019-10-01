package com.payasia.dao;

import com.payasia.dao.bean.EmpDataExportTemplateFilter;

public interface EmpDataExportTemplateFilterDAO {

	void save(EmpDataExportTemplateFilter empDataExportTemplateFilter);

	void update(EmpDataExportTemplateFilter empDataExportTemplateFilter);

	void delete(EmpDataExportTemplateFilter empDataExportTemplateFilter);

	EmpDataExportTemplateFilter findById(Long filterId);

	void deleteByCondition(Long exportTemplateId);
}
