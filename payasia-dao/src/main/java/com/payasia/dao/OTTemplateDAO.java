package com.payasia.dao;

import java.util.List;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.OTTemplateConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.OTTemplate;

public interface OTTemplateDAO {

	List<OTTemplate> getOTTemplateList(Long companyId);

	List<OTTemplate> getAllOTTemplateByConditionCompany(Long companyId,
			OTTemplateConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Path<String> getSortPathForOTTemplate(SortCondition sortDTO,
			Root<OTTemplate> otTempRoot);

	int getCountForAllOTTemplate(Long companyId,
			OTTemplateConditionDTO conditionDTO);

	void save(OTTemplate otTemplateVO);

	OTTemplate findByOTTemplateAndCompany(Long otTemplateId,
			String otTemplateName, Long companyId);

	OTTemplate findById(Long otTemplateId);

	void delete(OTTemplate otTemplateVO);

	void update(OTTemplate otTemplateVO);

}
