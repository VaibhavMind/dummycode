package com.payasia.logic;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.ClaimBatchForm;
import com.payasia.common.form.ClaimBatchResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface ClaimBatchLogic {

	String addClaimBatch(ClaimBatchForm claimBatchForm, Long companyId);

	String editClaimBatch(ClaimBatchForm claimBatchForm, Long companyId);

	ClaimBatchResponse viewClaimBatchDetail(PageRequest pageDTO,
			SortCondition sortDTO, Long claimBatchId, Long companyId);

	ClaimBatchForm getClaimBatchData(Long claimBatchId, Long companyId);

	ClaimBatchForm getClaimBatchConf(Long companyId);

	void deleteClaimBatch(Long claimBatchId, Long companyId);

	ClaimBatchResponse viewClaimBatch(AddClaimDTO claimDTO, PageRequest pageDTO, SortCondition sortDTO);

	
}
