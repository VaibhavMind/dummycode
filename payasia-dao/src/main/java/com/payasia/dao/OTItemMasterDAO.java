package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.OTItemMasterConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.OTItemMaster;

public interface OTItemMasterDAO {
	List<OTItemMaster> findAll(long companyId, PageRequest pageDTO,
			SortCondition sortDTO);

	OTItemMaster findById(Long otItemId);

	void save(OTItemMaster itemMaster);

	void update(OTItemMaster itemMaster);

	void delete(OTItemMaster itemMaster);

	List<OTItemMaster> findByCondition(OTItemMasterConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	OTItemMaster findByCompanyIdItemNameItemId(Long companyId, String name,
			Long itemId);

	int getCountByCondition(OTItemMasterConditionDTO conditionDTO,
			Long companyId);

	List<OTItemMaster> findByCondition(Long otTemplateId);
}
