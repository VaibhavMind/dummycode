package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CompanyModuleMapping;

public interface CompanyModuleMappingDAO {
	CompanyModuleMapping findById(long companyModuleMapping);

	void delete(Long companyId, Long moduleId);

	CompanyModuleMapping save(CompanyModuleMapping companyModuleMapping);

	List<CompanyModuleMapping> fetchModuleByCompanyId(Long companyID);
}
