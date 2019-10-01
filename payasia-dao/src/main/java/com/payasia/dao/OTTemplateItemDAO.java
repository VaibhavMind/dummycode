package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.OTTemplateItem;

public interface OTTemplateItemDAO {

	void save(OTTemplateItem otTemplateItem);

	List<OTTemplateItem> findByCondition(Long otTemplateId, Long companyId);

	OTTemplateItem findById(Long otTemplateTypeId);

	void update(OTTemplateItem otTemplateItem);

}
