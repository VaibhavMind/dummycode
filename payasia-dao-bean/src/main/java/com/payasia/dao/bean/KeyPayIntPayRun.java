package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the KeyPay_Int_PayRun database table.
 * 
 */
@Entity
@Table(name = "KeyPay_Int_PayRun")
public class KeyPayIntPayRun extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "KeyPay_Int_PayRun_ID")
	private long keyPayIntPayRunId;

	@Column(name = "PayRun_ID")
	private Long payRunId;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@Column(name = "Process_Status")
	private int processStatus;

	@Column(name = "PayRun_Finalized_Date")
	private Timestamp payRunFinalizedDate;

	@Column(name = "PayRun_Parameter_Date")
	private Timestamp payRunParameterDate;

	@OneToMany(mappedBy = "keyPayIntPayRun")
	private Set<KeyPayIntPayRunDetail> keyPayIntPayRunDetails;

	public KeyPayIntPayRun() {
	}

	public long getKeyPayIntPayRunId() {
		return keyPayIntPayRunId;
	}

	public void setKeyPayIntPayRunId(long keyPayIntPayRunId) {
		this.keyPayIntPayRunId = keyPayIntPayRunId;
	}

	public Long getPayRunId() {
		return payRunId;
	}

	public void setPayRunId(Long payRunId) {
		this.payRunId = payRunId;
	}

	public int getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}

	public Set<KeyPayIntPayRunDetail> getKeyPayIntPayRunDetails() {
		return keyPayIntPayRunDetails;
	}

	public void setKeyPayIntPayRunDetails(
			Set<KeyPayIntPayRunDetail> keyPayIntPayRunDetails) {
		this.keyPayIntPayRunDetails = keyPayIntPayRunDetails;
	}

	public Timestamp getPayRunFinalizedDate() {
		return payRunFinalizedDate;
	}

	public void setPayRunFinalizedDate(Timestamp payRunFinalizedDate) {
		this.payRunFinalizedDate = payRunFinalizedDate;
	}

	public Timestamp getPayRunParameterDate() {
		return payRunParameterDate;
	}

	public void setPayRunParameterDate(Timestamp payRunParameterDate) {
		this.payRunParameterDate = payRunParameterDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}