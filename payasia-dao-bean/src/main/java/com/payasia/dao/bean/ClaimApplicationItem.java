package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
 * The persistent class for the Claim_Application_Item database table.
 * 
 */
@Entity
@Table(name = "Claim_Application_Item")
public class ClaimApplicationItem extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Application_Item_ID")
	private long claimApplicationItemId;

	@Column(name = "Amount_Before_Tax")
	private BigDecimal amountBeforeTax;

	@Column(name = "Claim_Amount")
	private BigDecimal claimAmount;

	@Column(name = "Applicable_Claim_Amount")
	private BigDecimal applicableClaimAmount;

	@Column(name = "Claim_Date")
	private Timestamp claimDate;

	@Column(name = "Quantity")
	private Float quantity;

	@Column(name = "Unit_Price")
	private BigDecimal unitPrice;

	@Column(name = "Receipt_Number")
	private String receiptNumber;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Tax_Amount")
	private BigDecimal taxAmount;

	@Column(name = "Active")
	private boolean active = true;

	@Column(name = "Claimant_Name")
	private String claimantName;

	@ManyToOne
	@JoinColumn(name = "Claim_Application_ID")
	private ClaimApplication claimApplication;

	@ManyToOne
	@JoinColumn(name = "Employee_Claim_Template_Item_ID")
	private EmployeeClaimTemplateItem employeeClaimTemplateItem;

	@OneToMany(mappedBy = "claimApplicationItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimApplicationItemAttachment> claimApplicationItemAttachments;

	@OneToMany(mappedBy = "claimApplicationItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimApplicationItemWorkflow> claimApplicationItemWorkflows;

	@OneToMany(mappedBy = "claimApplicationItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimApplicationItemCustomField> claimApplicationItemCustomFields;

	@OneToMany(mappedBy = "claimApplicationItem", cascade = { CascadeType.REMOVE })
	private Set<ClaimApplicationItemLundinDetail> claimApplicationItemLundinDetails;

	@ManyToOne
	@JoinColumn(name = "Currency_ID")
	private CurrencyMaster currencyMaster;

	@Column(name = "Exchange_Rate")
	private BigDecimal exchangeRate;

	@Column(name = "Forex_Receipt_Amount")
	private BigDecimal forexReceiptAmount;

	public ClaimApplicationItem() {
	}

	public long getClaimApplicationItemId() {
		return this.claimApplicationItemId;
	}

	public void setClaimApplicationItemId(long claimApplicationItemId) {
		this.claimApplicationItemId = claimApplicationItemId;
	}

	public BigDecimal getAmountBeforeTax() {
		return this.amountBeforeTax;
	}

	public void setAmountBeforeTax(BigDecimal amountBeforeTax) {
		this.amountBeforeTax = amountBeforeTax;
	}

	public BigDecimal getClaimAmount() {
		return this.claimAmount;
	}

	public void setClaimAmount(BigDecimal claimAmount) {
		this.claimAmount = claimAmount;
	}

	public BigDecimal getApplicableClaimAmount() {
		return applicableClaimAmount;
	}

	public void setApplicableClaimAmount(BigDecimal applicableClaimAmount) {
		this.applicableClaimAmount = applicableClaimAmount;
	}

	public Timestamp getClaimDate() {
		return this.claimDate;
	}

	public void setClaimDate(Timestamp claimDate) {
		this.claimDate = claimDate;
	}

	public String getReceiptNumber() {
		return this.receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getTaxAmount() {
		return this.taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public EmployeeClaimTemplateItem getEmployeeClaimTemplateItem() {
		return employeeClaimTemplateItem;
	}

	public void setEmployeeClaimTemplateItem(
			EmployeeClaimTemplateItem employeeClaimTemplateItem) {
		this.employeeClaimTemplateItem = employeeClaimTemplateItem;
	}

	public Set<ClaimApplicationItemAttachment> getClaimApplicationItemAttachments() {
		return this.claimApplicationItemAttachments;
	}

	public void setClaimApplicationItemAttachments(
			Set<ClaimApplicationItemAttachment> claimApplicationItemAttachments) {
		this.claimApplicationItemAttachments = claimApplicationItemAttachments;
	}

	public Set<ClaimApplicationItemWorkflow> getClaimApplicationItemWorkflows() {
		return this.claimApplicationItemWorkflows;
	}

	public void setClaimApplicationItemWorkflows(
			Set<ClaimApplicationItemWorkflow> claimApplicationItemWorkflows) {
		this.claimApplicationItemWorkflows = claimApplicationItemWorkflows;
	}

	public Float getQuantity() {
		return quantity;
	}

	public void setQuantity(Float quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Set<ClaimApplicationItemCustomField> getClaimApplicationItemCustomFields() {
		return claimApplicationItemCustomFields;
	}

	public void setClaimApplicationItemCustomFields(
			Set<ClaimApplicationItemCustomField> claimApplicationItemCustomFields) {
		this.claimApplicationItemCustomFields = claimApplicationItemCustomFields;
	}

	public ClaimApplication getClaimApplication() {
		return claimApplication;
	}

	public void setClaimApplication(ClaimApplication claimApplication) {
		this.claimApplication = claimApplication;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public CurrencyMaster getCurrencyMaster() {
		return currencyMaster;
	}

	public void setCurrencyMaster(CurrencyMaster currencyMaster) {
		this.currencyMaster = currencyMaster;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public BigDecimal getForexReceiptAmount() {
		return forexReceiptAmount;
	}

	public void setForexReceiptAmount(BigDecimal forexReceiptAmount) {
		this.forexReceiptAmount = forexReceiptAmount;
	}

	public String getClaimantName() {
		return claimantName;
	}

	public void setClaimantName(String claimantName) {
		this.claimantName = claimantName;
	}

	public Set<ClaimApplicationItemLundinDetail> getClaimApplicationItemLundinDetails() {
		return claimApplicationItemLundinDetails;
	}

	public void setClaimApplicationItemLundinDetails(
			Set<ClaimApplicationItemLundinDetail> claimApplicationItemLundinDetails) {
		this.claimApplicationItemLundinDetails = claimApplicationItemLundinDetails;
	}

}