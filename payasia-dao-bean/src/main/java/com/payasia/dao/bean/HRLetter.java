package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the HR_Letter database table.
 * 
 */
@Entity
@Table(name = "HR_Letter")
public class HRLetter extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HR_Letter_ID")
	private long hrLetterId;

	@Column(name = "Active")
	private boolean active;

	@Column(name = "Body")
	private String body;

	@Column(name = "Letter_Desc")
	private String letterDesc;

	@Column(name = "Letter_Name")
	private String letterName;

	@Column(name = "Subject")
	private String subject;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public HRLetter() {
	}

	public long getHrLetterId() {
		return this.hrLetterId;
	}

	public void setHrLetterId(long hrLetterId) {
		this.hrLetterId = hrLetterId;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getBody() {
		return this.body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getLetterDesc() {
		return this.letterDesc;
	}

	public void setLetterDesc(String letterDesc) {
		this.letterDesc = letterDesc;
	}

	public String getLetterName() {
		return this.letterName;
	}

	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}