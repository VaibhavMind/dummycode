package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.OTBatchMasterConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.OTBatchMaster;

public interface OTBatchMasterDAO {

	OTBatchMaster findByCompanyIdItemNameItemId(Long companyId,
			String description, Long otBatchId);

	void update(OTBatchMaster oTBatchMaster);

	void save(OTBatchMaster oTBatchMaster);

	void delete(OTBatchMaster oTBatchMaster);

	List<OTBatchMaster> findByCondition(OTBatchMasterConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO, Long companyId);

	int getCountByCondition(OTBatchMasterConditionDTO conditionDTO,
			Long companyId);

	OTBatchMaster findById(long otBatchId);

}
