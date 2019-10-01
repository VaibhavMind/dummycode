package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the Lundin_OT_Reviewer_Detail_View database table.
 * 
 */
@Entity
@Table(name = "Lundin_OT_Reviewer_Detail_View")
public class LundinOTReviewerDetailView implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LundinOTReviewerDetailViewPK lundinOTReviewerDetailViewPK;

	@Column(name = "Emp_Employee_Number")
	private String empEmployeeNumber;

	@Column(name = "Emp_First_Name")
	private String empFirstName;

	@Column(name = "Emp_Last_Name")
	private String empLastName;

	@Column(name = "Reviewer1_Employee_Number")
	private String reviewer1EmployeeNumber;

	@Column(name = "Reviewer1_First_Name")
	private String reviewer1FirstName;

	@Column(name = "Reviewer1_ID")
	private Long reviewer1Id;

	@Column(name = "Reviewer1_Last_Name")
	private String reviewer1LastName;

	@Column(name = "Reviewer2_Employee_Number")
	private String reviewer2EmployeeNumber;

	@Column(name = "Reviewer2_First_Name")
	private String reviewer2FirstName;

	@Column(name = "Reviewer2_ID")
	private Long reviewer2Id;

	@Column(name = "Reviewer2_Last_Name")
	private String reviewer2LastName;

	@Column(name = "Reviewer3_Employee_Number")
	private String reviewer3EmployeeNumber;

	@Column(name = "Reviewer3_First_Name")
	private String reviewer3FirstName;

	@Column(name = "Reviewer3_ID")
	private Long reviewer3Id;

	@Column(name = "Reviewer3_Last_Name")
	private String reviewer3LastName;

	@Column(name = "Emp_Email")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LundinOTReviewerDetailView() {
	}

	public String getEmpEmployeeNumber() {
		return this.empEmployeeNumber;
	}

	public void setEmpEmployeeNumber(String empEmployeeNumber) {
		this.empEmployeeNumber = empEmployeeNumber;
	}

	public String getEmpFirstName() {
		return this.empFirstName;
	}

	public void setEmpFirstName(String empFirstName) {
		this.empFirstName = empFirstName;
	}

	public String getEmpLastName() {
		return this.empLastName;
	}

	public void setEmpLastName(String empLastName) {
		this.empLastName = empLastName;
	}

	public String getReviewer1EmployeeNumber() {
		return this.reviewer1EmployeeNumber;
	}

	public void setReviewer1EmployeeNumber(String reviewer1EmployeeNumber) {
		this.reviewer1EmployeeNumber = reviewer1EmployeeNumber;
	}

	public String getReviewer1FirstName() {
		return this.reviewer1FirstName;
	}

	public void setReviewer1FirstName(String reviewer1FirstName) {
		this.reviewer1FirstName = reviewer1FirstName;
	}

	public Long getReviewer1Id() {
		return this.reviewer1Id;
	}

	public void setReviewer1Id(Long reviewer1Id) {
		this.reviewer1Id = reviewer1Id;
	}

	public String getReviewer1LastName() {
		return this.reviewer1LastName;
	}

	public void setReviewer1LastName(String reviewer1LastName) {
		this.reviewer1LastName = reviewer1LastName;
	}

	public String getReviewer2EmployeeNumber() {
		return this.reviewer2EmployeeNumber;
	}

	public void setReviewer2EmployeeNumber(String reviewer2EmployeeNumber) {
		this.reviewer2EmployeeNumber = reviewer2EmployeeNumber;
	}

	public String getReviewer2FirstName() {
		return this.reviewer2FirstName;
	}

	public void setReviewer2FirstName(String reviewer2FirstName) {
		this.reviewer2FirstName = reviewer2FirstName;
	}

	public Long getReviewer2Id() {
		return this.reviewer2Id;
	}

	public void setReviewer2Id(Long reviewer2Id) {
		this.reviewer2Id = reviewer2Id;
	}

	public String getReviewer2LastName() {
		return this.reviewer2LastName;
	}

	public void setReviewer2LastName(String reviewer2LastName) {
		this.reviewer2LastName = reviewer2LastName;
	}

	public String getReviewer3EmployeeNumber() {
		return this.reviewer3EmployeeNumber;
	}

	public void setReviewer3EmployeeNumber(String reviewer3EmployeeNumber) {
		this.reviewer3EmployeeNumber = reviewer3EmployeeNumber;
	}

	public String getReviewer3FirstName() {
		return this.reviewer3FirstName;
	}

	public void setReviewer3FirstName(String reviewer3FirstName) {
		this.reviewer3FirstName = reviewer3FirstName;
	}

	public Long getReviewer3Id() {
		return this.reviewer3Id;
	}

	public void setReviewer3Id(Long reviewer3Id) {
		this.reviewer3Id = reviewer3Id;
	}

	public String getReviewer3LastName() {
		return this.reviewer3LastName;
	}

	public void setReviewer3LastName(String reviewer3LastName) {
		this.reviewer3LastName = reviewer3LastName;
	}

	public LundinOTReviewerDetailViewPK getLundinOTReviewerDetailViewPK() {
		return lundinOTReviewerDetailViewPK;
	}

	public void setLundinOTReviewerDetailViewPK(
			LundinOTReviewerDetailViewPK lundinOTReviewerDetailViewPK) {
		this.lundinOTReviewerDetailViewPK = lundinOTReviewerDetailViewPK;
	}

}