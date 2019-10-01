package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.LundinConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LundinAFE;
import com.payasia.dao.bean.LundinBlock;

public interface LundinAFEDAO {
	LundinAFE findById(long id);

	void save(LundinAFE afe);

	void update(LundinAFE afe);

	void delete(long id);

	Long getCountForCondition(LundinConditionDTO conditionDTO);

	List<LundinAFE> findByCondition(LundinConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	List<LundinBlock> getActiveBlocksList(Timestamp effectiveDate,
			long companyId);

	LundinAFE saveReturn(LundinAFE lundinAfeObj);

	LundinAFE findByAfeCode(Long afeId, String afeCode, long companyId);

	List<LundinAFE> findByCondition(Long blockId, Long companyId);
	
	LundinAFE findById(Long afeId,Long companyId);
}
