package com.payasia.common.form;

import java.io.Serializable;

public class EmployeeQualificationDetailsForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 416316819716869200L;
	private String year;
	private String qualification;
	private String duration;
	private String institute;
	private String university;
	private String level;
	private String qualificationArea;
	private String grade;
	private String remarks;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getQualificationArea() {
		return qualificationArea;
	}

	public void setQualificationArea(String qualificationArea) {
		this.qualificationArea = qualificationArea;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
