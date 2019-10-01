package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.LeaveBatchForm;
import com.payasia.common.form.LeaveBatchResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface LeaveBatchLogic {

	LeaveBatchResponse viewLeaveBatch(PageRequest pageDTO,
			SortCondition sortDTO, String leaveBatchFilter, String filterText,
			Long companyID);

	String addLeaveBatch(LeaveBatchForm leaveBatchForm, Long companyId);

	LeaveBatchForm getLeaveBatchData(Long leaveBatchId);

	String editLeaveBatch(LeaveBatchForm leaveBatchForm, Long leaveBatchId,
			Long companyID);

	void deleteLeaveBatch(Long leaveBatchId);

	LeaveBatchForm getLeaveBatchDataByCompany(Long leaveBatchId, Long companyId);

}
