package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class ClaimReviewerInfoReportDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ClaimReportHeaderDTO> claimHeaderDTOs;
	private List<ClaimReviewerReportDataDTO> claimReviewerDataDTOs;
	
	
	public List<ClaimReportHeaderDTO> getClaimHeaderDTOs() {
		return claimHeaderDTOs;
	}
	public void setClaimHeaderDTOs(List<ClaimReportHeaderDTO> claimHeaderDTOs) {
		this.claimHeaderDTOs = claimHeaderDTOs;
	}
	public List<ClaimReviewerReportDataDTO> getClaimReviewerDataDTOs() {
		return claimReviewerDataDTOs;
	}
	public void setClaimReviewerDataDTOs(
			List<ClaimReviewerReportDataDTO> claimReviewerDataDTOs) {
		this.claimReviewerDataDTOs = claimReviewerDataDTOs;
	}
	
	
	
	

}
