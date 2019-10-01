package com.payasia.web.controller;

import com.payasia.common.form.LeaveBatchForm;

public interface LeaveBatchController {

	String getLeaveBatch(String columnName, String sortingType, int page,
			int rows, String leaveBatchFilter, String filterText);

	String addLeaveBatch(LeaveBatchForm leaveBatchForm);

	String getLeaveBatchData(Long leaveBatchId);

	String editLeaveBatch(LeaveBatchForm leaveBatchForm, Long leaveBatchId);

	void deleteLeaveBatch(Long leaveBatchId);

}
