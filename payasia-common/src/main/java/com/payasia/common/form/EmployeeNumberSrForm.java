package com.payasia.common.form;

import java.io.Serializable;

 
/**
 * The Class EmployeeNumberSrForm.
 */
public class EmployeeNumberSrForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2110958531205523890L;

	/** The emp no series id. */
	private long empNoSeriesId;

	/** The description. */
	private String description;
	private String prefixSuffix;
	
	/** The prefix. */
	private String prefix;

	
	/** The suffix. */
	private String suffix;

	
	/** The active. */
	private boolean active;

	/** The status id. */
	private long statusId;

	/** The company id. */
	private long companyId;

	/**
	 * Gets the company id.
	 * 
	 * @return the company id
	 */
	public long getCompanyId() {
		return companyId;
	}

	/**
	 * Sets the company id.
	 * 
	 * @param companyId
	 *            the new company id
	 */
	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	/**
	 * Gets the status id.
	 * 
	 * @return the status id
	 */
	public long getStatusId() {
		return statusId;
	}

	/**
	 * Sets the status id.
	 * 
	 * @param statusId
	 *            the new status id
	 */
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}

	/**
	 * Gets the emp no series id.
	 * 
	 * @return the emp no series id
	 */
	public long getEmpNoSeriesId() {
		return empNoSeriesId;
	}

	/**
	 * Sets the emp no series id.
	 * 
	 * @param empNoSeriesId
	 *            the new emp no series id
	 */
	public void setEmpNoSeriesId(long empNoSeriesId) {
		this.empNoSeriesId = empNoSeriesId;
	}

	

	/**
	 * Gets the prefix.
	 * 
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Sets the prefix.
	 * 
	 * @param prefix
	 *            the new prefix
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	
	/**
	 * Gets the suffix.
	 * 
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * Sets the suffix.
	 * 
	 * @param suffix
	 *            the new suffix
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	
	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 * 
	 * @param active
	 *            the new active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrefixSuffix() {
		return prefixSuffix;
	}

	public void setPrefixSuffix(String prefixSuffix) {
		this.prefixSuffix = prefixSuffix;
	}

}
