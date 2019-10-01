package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the Multi_Lingual_Data database table.
 * 
 */
@Embeddable
public class MultiLingualDataPK implements Serializable {
	 
	private static final long serialVersionUID = 1L;

	@Column(name = "Data_Dictionary_ID")
	private long data_Dictionary_ID;

	@Column(name = "Language_ID")
	private long language_ID;

	public MultiLingualDataPK() {
	}

	public long getData_Dictionary_ID() {
		return this.data_Dictionary_ID;
	}

	public void setData_Dictionary_ID(long data_Dictionary_ID) {
		this.data_Dictionary_ID = data_Dictionary_ID;
	}

	public long getLanguage_ID() {
		return this.language_ID;
	}

	public void setLanguage_ID(long language_ID) {
		this.language_ID = language_ID;
	}

		public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MultiLingualDataPK)) {
			return false;
		}
		MultiLingualDataPK castOther = (MultiLingualDataPK) other;
		return (this.data_Dictionary_ID == castOther.data_Dictionary_ID)
				&& (this.language_ID == castOther.language_ID);

	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash
				* prime
				+ ((int) (this.data_Dictionary_ID ^ (this.data_Dictionary_ID >>> 32)));
		hash = hash * prime
				+ ((int) (this.language_ID ^ (this.language_ID >>> 32)));

		return hash;
	}
}