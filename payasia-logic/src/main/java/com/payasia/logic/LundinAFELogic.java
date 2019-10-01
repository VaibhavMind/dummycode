package com.payasia.logic;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.LundinAFEDTO;
import com.payasia.common.dto.LundinBlockDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinAFEResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface LundinAFELogic {
	LundinAFEDTO findById(long id);

	ResponseObjectDTO save(LundinAFEDTO aef, long companyId);

	ResponseObjectDTO update(LundinAFEDTO aef, long companyId);

	ResponseObjectDTO delete(long id, long companyId);

	LundinAFEResponse getAfeResponse(String fromDate, String toDate,
			long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, String transactionType);

	List<LundinBlockDTO> getActiveBlocks(Timestamp effectiveDate, long companyId);

	LundinAFEDTO getAfeDataById(Long afeId);
}
