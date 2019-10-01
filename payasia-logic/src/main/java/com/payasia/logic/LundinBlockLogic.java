package com.payasia.logic;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.LundinBlockDTO;
import com.payasia.common.dto.LundinBlockWithAfeDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinBlockResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface LundinBlockLogic {
	LundinBlockDTO findById(long id);

	ResponseObjectDTO save(LundinBlockDTO lundinBlockDTO, long companyId);

	ResponseObjectDTO update(LundinBlockDTO block, long companyId);

	ResponseObjectDTO delete(long blockId, long companyId);

	LundinBlockResponse getBlockResponse(String fromDate, String toDate,
			Long compId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, String transactionType);

	List<LundinBlockWithAfeDTO> getBlockAndAfe(Timestamp timestamp,
			long companyId);

	LundinBlockDTO getBlockDataById(Long blockId);

}
