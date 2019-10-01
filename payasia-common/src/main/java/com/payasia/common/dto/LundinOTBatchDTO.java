package com.payasia.common.dto;

import java.sql.Timestamp;

public class LundinOTBatchDTO {

		private long otBatchId;
		private String otBatchDesc;
		private Timestamp endDate;
		private Timestamp startDate;
		private long companyId;
		
		public long getOtBatchId() {
			return otBatchId;
		}

		public void setOtBatchId(long otBatchId) {
			this.otBatchId = otBatchId;
		}

		public String getOtBatchDesc() {
			return otBatchDesc;
		}

		public void setOtBatchDesc(String otBatchDesc) {
			this.otBatchDesc = otBatchDesc;
		}

		public Timestamp getEndDate() {
			return endDate;
		}

		public void setEndDate(Timestamp endDate) {
			this.endDate = endDate;
		}

		public Timestamp getStartDate() {
			return startDate;
		}

		public void setStartDate(Timestamp startDate) {
			this.startDate = startDate;
		}

		public long getCompanyId() {
			return companyId;
		}

		public void setCompanyId(long companyId) {
			this.companyId = companyId;
		}

}
