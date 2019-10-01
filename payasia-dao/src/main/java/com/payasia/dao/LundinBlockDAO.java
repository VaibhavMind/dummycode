package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LundinBlock;

public interface LundinBlockDAO {
	LundinBlock findById(long id);

	void save(LundinBlock block);

	void update(LundinBlock block);

	void delete(long id);

	Long getCountForCondition(LundinConditionDTO conditionDTO);

	List<LundinBlock> findByCondition(LundinConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	List<LundinBlock> getBlocks(Timestamp effectiveDate, long companyId);

	LundinBlock findByDescCompany(Long blockId, String blockCode, long companyId);

	List<LundinBlock> findByCondition(Long companyId);
	
	LundinBlock findById(long id, Long companyId);

}
