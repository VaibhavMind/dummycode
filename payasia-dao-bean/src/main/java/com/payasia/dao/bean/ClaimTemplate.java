package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Template database table.
 * 
 */
@Entity
@Table(name = "Claim_Template")
public class ClaimTemplate extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Template_ID")
	private long claimTemplateId;

	@Column(name = "Template_Name")
	private String templateName;

	@Column(name = "Visibility")
	private boolean visibility;

	@Column(name = "Entitlement_Per_Claim")
	private BigDecimal entitlementPerClaim = new BigDecimal(0);

	@Column(name = "Entitlement_Per_Month")
	private BigDecimal entitlementPerMonth = new BigDecimal(0);

	@Column(name = "Entitlement_Per_Year")
	private BigDecimal entitlementPerYear = new BigDecimal(0);

	@Column(name = "Allowed_Times_Value")
	private Integer allowedTimesValue;

	@ManyToOne
	@JoinColumn(name = "Front_End_View_Mode")
	private AppCodeMaster frontEndViewMode;

	@Column(name = "Front_End_Application_Mode")
	private Boolean frontEndApplicationMode;

	@ManyToOne
	@JoinColumn(name = "Back_End_View_Mode")
	private AppCodeMaster backEndViewMode;

	@Column(name = "Back_End_Application_Mode")
	private Boolean backEndApplicationMode;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	 
	@ManyToOne
	@JoinColumn(name = "Default_Currency")
	private CurrencyMaster defaultCurrency;

	 
	@OneToMany(mappedBy = "claimTemplate")
	private Set<ClaimTemplateItem> claimTemplateItems;

	 
	@OneToMany(mappedBy = "claimTemplate")
	private Set<ClaimTemplateWorkflow> claimTemplateWorkflows;

	 
	@OneToMany(mappedBy = "claimTemplate")
	private Set<EmployeeClaimTemplate> employeeClaimTemplates;

	 
	@ManyToOne
	@JoinColumn(name = "Allowed_Times_Field")
	private AppCodeMaster allowedTimesField;

	@Column(name = "Default_CC")
	private String defaultCC;

	@Column(name = "Send_Mail_To_Default_CC")
	private Boolean sendMailToDefaultCC;

	@Column(name = "Cut_Off_Day")
	private Integer cutOffValue;

	@ManyToOne
	@JoinColumn(name = "Proration_Method")
	private AppCodeMaster prorationMethod;

	@Column(name = "Allow_If_At_Least_One_Attachment")
	private Boolean AllowIfAtLeastOneAttachment;

	@Column(name = "Proration")
	private Boolean proration;

	 
	@ManyToOne
	@JoinColumn(name = "Proration_Based_On")
	private AppCodeMaster prorationBasedOn;

	@Column(name = "Claim_Reviewers_Based_On_Claim_Amount")
	private boolean claimReviewersBasedOnClaimAmount;

	 
	@OneToMany(mappedBy = "claimTemplate", cascade = { CascadeType.REMOVE })
	private Set<ClaimAmountReviewerTemplate> claimAmountReviewerTemplates;

	@ManyToOne
	@JoinColumn(name = "Consider_Additional_Balance_From")
	private ClaimTemplate considerAdditionalBalanceFrom;

	public ClaimTemplate() {
	}

	public long getClaimTemplateId() {
		return this.claimTemplateId;
	}

	public void setClaimTemplateId(long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public boolean getVisibility() {
		return this.visibility;
	}

	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Set<ClaimTemplateItem> getClaimTemplateItems() {
		return this.claimTemplateItems;
	}

	public void setClaimTemplateItems(Set<ClaimTemplateItem> claimTemplateItems) {
		this.claimTemplateItems = claimTemplateItems;
	}

	public Set<ClaimTemplateWorkflow> getClaimTemplateWorkflows() {
		return this.claimTemplateWorkflows;
	}

	public void setClaimTemplateWorkflows(
			Set<ClaimTemplateWorkflow> claimTemplateWorkflows) {
		this.claimTemplateWorkflows = claimTemplateWorkflows;
	}

	public Set<EmployeeClaimTemplate> getEmployeeClaimTemplates() {
		return employeeClaimTemplates;
	}

	public void setEmployeeClaimTemplates(
			Set<EmployeeClaimTemplate> employeeClaimTemplates) {
		this.employeeClaimTemplates = employeeClaimTemplates;
	}

	public BigDecimal getEntitlementPerClaim() {
		return entitlementPerClaim;
	}

	public void setEntitlementPerClaim(BigDecimal entitlementPerClaim) {
		this.entitlementPerClaim = entitlementPerClaim;
	}

	public BigDecimal getEntitlementPerMonth() {
		return entitlementPerMonth;
	}

	public void setEntitlementPerMonth(BigDecimal entitlementPerMonth) {
		this.entitlementPerMonth = entitlementPerMonth;
	}

	public BigDecimal getEntitlementPerYear() {
		return entitlementPerYear;
	}

	public void setEntitlementPerYear(BigDecimal entitlementPerYear) {
		this.entitlementPerYear = entitlementPerYear;
	}

	public Integer getAllowedTimesValue() {
		return allowedTimesValue;
	}

	public void setAllowedTimesValue(Integer allowedTimesValue) {
		this.allowedTimesValue = allowedTimesValue;
	}

	public AppCodeMaster getAllowedTimesField() {
		return allowedTimesField;
	}

	public void setAllowedTimesField(AppCodeMaster allowedTimesField) {
		this.allowedTimesField = allowedTimesField;
	}

	public CurrencyMaster getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(CurrencyMaster defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public String getDefaultCC() {
		return defaultCC;
	}

	public void setDefaultCC(String defaultCC) {
		this.defaultCC = defaultCC;
	}

	public Boolean getSendMailToDefaultCC() {
		return sendMailToDefaultCC;
	}

	public void setSendMailToDefaultCC(Boolean sendMailToDefaultCC) {
		this.sendMailToDefaultCC = sendMailToDefaultCC;
	}

	public Boolean getAllowIfAtLeastOneAttachment() {
		return AllowIfAtLeastOneAttachment;
	}

	public void setAllowIfAtLeastOneAttachment(
			Boolean allowIfAtLeastOneAttachment) {
		AllowIfAtLeastOneAttachment = allowIfAtLeastOneAttachment;
	}

	public Boolean getProration() {
		return proration;
	}

	public void setProration(Boolean proration) {
		this.proration = proration;
	}

	public AppCodeMaster getProrationBasedOn() {
		return prorationBasedOn;
	}

	public void setProrationBasedOn(AppCodeMaster prorationBasedOn) {
		this.prorationBasedOn = prorationBasedOn;
	}

	public AppCodeMaster getFrontEndViewMode() {
		return frontEndViewMode;
	}

	public void setFrontEndViewMode(AppCodeMaster frontEndViewMode) {
		this.frontEndViewMode = frontEndViewMode;
	}

	public Boolean getFrontEndApplicationMode() {
		return frontEndApplicationMode;
	}

	public void setFrontEndApplicationMode(Boolean frontEndApplicationMode) {
		this.frontEndApplicationMode = frontEndApplicationMode;
	}

	public AppCodeMaster getBackEndViewMode() {
		return backEndViewMode;
	}

	public void setBackEndViewMode(AppCodeMaster backEndViewMode) {
		this.backEndViewMode = backEndViewMode;
	}

	public Boolean getBackEndApplicationMode() {
		return backEndApplicationMode;
	}

	public void setBackEndApplicationMode(Boolean backEndApplicationMode) {
		this.backEndApplicationMode = backEndApplicationMode;
	}

	public boolean isClaimReviewersBasedOnClaimAmount() {
		return claimReviewersBasedOnClaimAmount;
	}

	public void setClaimReviewersBasedOnClaimAmount(
			boolean claimReviewersBasedOnClaimAmount) {
		this.claimReviewersBasedOnClaimAmount = claimReviewersBasedOnClaimAmount;
	}

	public Set<ClaimAmountReviewerTemplate> getClaimAmountReviewerTemplates() {
		return claimAmountReviewerTemplates;
	}

	public void setClaimAmountReviewerTemplates(
			Set<ClaimAmountReviewerTemplate> claimAmountReviewerTemplates) {
		this.claimAmountReviewerTemplates = claimAmountReviewerTemplates;
	}
	public Integer getCutOffValue() {
		return cutOffValue;
	}

	public void setCutOffValue(Integer cutOffValue) {
		this.cutOffValue = cutOffValue;
	}

	public AppCodeMaster getProrationMethod() {
		return prorationMethod;
	}

	public void setProrationMethod(AppCodeMaster prorationMethod) {
		this.prorationMethod = prorationMethod;
	}

	public ClaimTemplate getConsiderAdditionalBalanceFrom() {
		return considerAdditionalBalanceFrom;
	}

	public void setConsiderAdditionalBalanceFrom(ClaimTemplate considerAdditionalBalanceFrom) {
		this.considerAdditionalBalanceFrom = considerAdditionalBalanceFrom;
	}

}