package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Password_Security_Question_Master database
 * table.
 * 
 */
@Entity
@Table(name = "Password_Security_Question_Master")
public class PasswordSecurityQuestionMaster extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Pwd_Security_Question_ID")
	private long pwdSecurityQuestionId;

	@Column(name = "Secret_Question")
	private String secretQuestion;

	 
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	public PasswordSecurityQuestionMaster() {
	}

	public long getPwdSecurityQuestionId() {
		return this.pwdSecurityQuestionId;
	}

	public void setPwdSecurityQuestionId(long pwdSecurityQuestionId) {
		this.pwdSecurityQuestionId = pwdSecurityQuestionId;
	}

	public String getSecretQuestion() {
		return this.secretQuestion;
	}

	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}

	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}