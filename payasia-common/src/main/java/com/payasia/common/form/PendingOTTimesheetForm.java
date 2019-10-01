package com.payasia.common.form;

import java.io.Serializable;

import com.payasia.common.dto.ValidationClaimItemDTO;
public class PendingOTTimesheetForm implements Serializable {
	

		private static final long serialVersionUID = 7704109396182098943L;
		
		private OTTimesheetForm otTimesheetForm;
		
		private Long otTimesheetReviewerId;
		private String createdBy;
		private String createdDate;
		private String totalItems;
		private String batchDesc;
		public String getBatchDesc() {
			return batchDesc;
		}


		public void setBatchDesc(String batchDesc) {
			this.batchDesc = batchDesc;
		}


		public String getTotalItems() {
			return totalItems;
		}


		public void setTotalItems(String totalItems) {
			this.totalItems = totalItems;
		}


		private String remarks;
		private Long createdById;
		private Long otTimesheetId;
		private String emailCC;
		
		private ValidationClaimItemDTO validationClaimItemDTO;
		
		public Long getCreatedById() {
			return createdById;
		}


		public void setCreatedById(Long createdById) {
			this.createdById = createdById;
		}


		public String getCreatedBy() {
			return createdBy;
		}
		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}
		public String getCreatedDate() {
			return createdDate;
		}
		public void setCreatedDate(String createdDate) {
			this.createdDate = createdDate;
		}
	


		public OTTimesheetForm getOtTimesheetForm() {
			return otTimesheetForm;
		}


		public void setOtTimesheetForm(OTTimesheetForm otTimesheetForm) {
			this.otTimesheetForm = otTimesheetForm;
		}


		public Long getOtTimesheetReviewerId() {
			return otTimesheetReviewerId;
		}


		public void setOtTimesheetReviewerId(Long otTimesheetReviewerId) {
			this.otTimesheetReviewerId = otTimesheetReviewerId;
		}


		public String getRemarks() {
			return remarks;
		}


		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}


	


		public Long getOtTimesheetId() {
			return otTimesheetId;
		}


		public void setOtTimesheetId(Long otTimesheetId) {
			this.otTimesheetId = otTimesheetId;
		}


		public String getEmailCC() {
			return emailCC;
		}


		public void setEmailCC(String emailCC) {
			this.emailCC = emailCC;
		}


	

		public ValidationClaimItemDTO getValidationClaimItemDTO() {
			return validationClaimItemDTO;
		}


		public void setValidationClaimItemDTO(
				ValidationClaimItemDTO validationClaimItemDTO) {
			this.validationClaimItemDTO = validationClaimItemDTO;
		}


		

}
