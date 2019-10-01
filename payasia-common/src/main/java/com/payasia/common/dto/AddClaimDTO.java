package com.payasia.common.dto;

import java.math.BigDecimal;
import java.util.Locale;

public class AddClaimDTO {
	
	private Long claimTemplateId;
	private Long employeeId;
	private Long companyId;
	private Long employeeClaimTemplateId;
	private Long claimApplicationItemAttachmentId;
	private BigDecimal entitlement;
	private Integer year;
	private String fromDate;
	private String toDate;
	private String pageContextPath;
	private Long employeeClaimTemplateItemId;
	private Long claimTemplateItemId;
	private Long claimApplicationId;
	private Long claimApplicationItemId;
	private Long claimApplicationAttachementId;
	private boolean hasLundinTimesheetModule;
	private Locale locale;
	private String searchCondition;
	private String searchText;
	private String transactionType;
	
	private Boolean admin=false;
	
	
	
	public String getSearchCondition() {
		return searchCondition;
	}
	public void setSearchCondition(String searchCondition) {
		this.searchCondition = searchCondition;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Long getClaimApplicationItemAttachmentId() {
		return claimApplicationItemAttachmentId;
	}
	public void setClaimApplicationItemAttachmentId(Long claimApplicationItemAttachmentId) {
		this.claimApplicationItemAttachmentId = claimApplicationItemAttachmentId;
	}
	
	public Long getClaimTemplateItemId() {
		return claimTemplateItemId;
	}
	public void setClaimTemplateItemId(Long claimTemplateItemId) {
		this.claimTemplateItemId = claimTemplateItemId;
	}
	public Long getClaimApplicationAttachementId() {
		return claimApplicationAttachementId;
	}
	public void setClaimApplicationAttachementId(Long claimApplicationAttachementId) {
		this.claimApplicationAttachementId = claimApplicationAttachementId;
	}
	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}
	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}
	public String getPageContextPath() {
		return pageContextPath;
	}
	public void setPageContextPath(String pageContextPath) {
		this.pageContextPath = pageContextPath;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public Long getClaimTemplateId() {
		return claimTemplateId;
	}
	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Boolean getAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	public Long getEmployeeClaimTemplateItemId() {
		return employeeClaimTemplateItemId;
	}
	public void setEmployeeClaimTemplateItemId(Long employeeClaimTemplateItemId) {
		this.employeeClaimTemplateItemId = employeeClaimTemplateItemId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getClaimApplicationId() {
		return claimApplicationId;
	}
	public void setClaimApplicationId(Long claimApplicationId) {
		this.claimApplicationId = claimApplicationId;
	}
	public Long getClaimApplicationItemId() {
		return claimApplicationItemId;
	}
	public void setClaimApplicationItemId(Long claimApplicationItemId) {
		this.claimApplicationItemId = claimApplicationItemId;
	}
	public boolean isHasLundinTimesheetModule() {
		return hasLundinTimesheetModule;
	}
	public void setHasLundinTimesheetModule(boolean hasLundinTimesheetModule) {
		this.hasLundinTimesheetModule = hasLundinTimesheetModule;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public BigDecimal getEntitlement() {
		return entitlement;
	}
	public void setEntitlement(BigDecimal entitlement) {
		this.entitlement = entitlement;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((admin == null) ? 0 : admin.hashCode());
		result = prime * result
				+ ((claimApplicationAttachementId == null) ? 0 : claimApplicationAttachementId.hashCode());
		result = prime * result + ((claimApplicationId == null) ? 0 : claimApplicationId.hashCode());
		result = prime * result
				+ ((claimApplicationItemAttachmentId == null) ? 0 : claimApplicationItemAttachmentId.hashCode());
		result = prime * result + ((claimApplicationItemId == null) ? 0 : claimApplicationItemId.hashCode());
		result = prime * result + ((claimTemplateId == null) ? 0 : claimTemplateId.hashCode());
		result = prime * result + ((claimTemplateItemId == null) ? 0 : claimTemplateItemId.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((employeeClaimTemplateId == null) ? 0 : employeeClaimTemplateId.hashCode());
		result = prime * result + ((employeeClaimTemplateItemId == null) ? 0 : employeeClaimTemplateItemId.hashCode());
		result = prime * result + ((employeeId == null) ? 0 : employeeId.hashCode());
		result = prime * result + ((entitlement == null) ? 0 : entitlement.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + (hasLundinTimesheetModule ? 1231 : 1237);
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((pageContextPath == null) ? 0 : pageContextPath.hashCode());
		result = prime * result + ((searchCondition == null) ? 0 : searchCondition.hashCode());
		result = prime * result + ((searchText == null) ? 0 : searchText.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddClaimDTO other = (AddClaimDTO) obj;
		if (admin == null) {
			if (other.admin != null)
				return false;
		} else if (!admin.equals(other.admin))
			return false;
		if (claimApplicationAttachementId == null) {
			if (other.claimApplicationAttachementId != null)
				return false;
		} else if (!claimApplicationAttachementId.equals(other.claimApplicationAttachementId))
			return false;
		if (claimApplicationId == null) {
			if (other.claimApplicationId != null)
				return false;
		} else if (!claimApplicationId.equals(other.claimApplicationId))
			return false;
		if (claimApplicationItemAttachmentId == null) {
			if (other.claimApplicationItemAttachmentId != null)
				return false;
		} else if (!claimApplicationItemAttachmentId.equals(other.claimApplicationItemAttachmentId))
			return false;
		if (claimApplicationItemId == null) {
			if (other.claimApplicationItemId != null)
				return false;
		} else if (!claimApplicationItemId.equals(other.claimApplicationItemId))
			return false;
		if (claimTemplateId == null) {
			if (other.claimTemplateId != null)
				return false;
		} else if (!claimTemplateId.equals(other.claimTemplateId))
			return false;
		if (claimTemplateItemId == null) {
			if (other.claimTemplateItemId != null)
				return false;
		} else if (!claimTemplateItemId.equals(other.claimTemplateItemId))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (employeeClaimTemplateId == null) {
			if (other.employeeClaimTemplateId != null)
				return false;
		} else if (!employeeClaimTemplateId.equals(other.employeeClaimTemplateId))
			return false;
		if (employeeClaimTemplateItemId == null) {
			if (other.employeeClaimTemplateItemId != null)
				return false;
		} else if (!employeeClaimTemplateItemId.equals(other.employeeClaimTemplateItemId))
			return false;
		if (employeeId == null) {
			if (other.employeeId != null)
				return false;
		} else if (!employeeId.equals(other.employeeId))
			return false;
		if (entitlement == null) {
			if (other.entitlement != null)
				return false;
		} else if (!entitlement.equals(other.entitlement))
			return false;
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (hasLundinTimesheetModule != other.hasLundinTimesheetModule)
			return false;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		if (pageContextPath == null) {
			if (other.pageContextPath != null)
				return false;
		} else if (!pageContextPath.equals(other.pageContextPath))
			return false;
		if (searchCondition == null) {
			if (other.searchCondition != null)
				return false;
		} else if (!searchCondition.equals(other.searchCondition))
			return false;
		if (searchText == null) {
			if (other.searchText != null)
				return false;
		} else if (!searchText.equals(other.searchText))
			return false;
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		if (transactionType == null) {
			if (other.transactionType != null)
				return false;
		} else if (!transactionType.equals(other.transactionType))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}

}
