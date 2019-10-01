package com.payasia.logic;

import com.payasia.common.dto.LundinDepartmentDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.LundinDepartmentResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface LundinDepartmentLogic {
	LundinDepartmentDTO findById(long id);

	ResponseObjectDTO save(LundinDepartmentDTO department, long companyId);

	ResponseObjectDTO update(LundinDepartmentDTO department, long companyId);

	ResponseObjectDTO delete(long id, long companyId);

	LundinDepartmentDTO getDepartmentType();

	LundinDepartmentResponse getDepartmentResponse(String fromDate,
			String toDate, Long compId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			String transactionType);

	LundinDepartmentDTO getBlockDataById(Long departmetnId);

}
