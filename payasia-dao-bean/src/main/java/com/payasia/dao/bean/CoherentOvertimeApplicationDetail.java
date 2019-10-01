package com.payasia.dao.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Coherent_Overtime_Application_Detail")
public class CoherentOvertimeApplicationDetail extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Overtime_Application_Detail_ID")
	private long overtimeApplicationDetailID;

	@ManyToOne
	@JoinColumn(name = "Overtime_Application_ID")
	private CoherentOvertimeApplication coherentOvertimeApplication;

	@Column(name = "Overtime_Date")
	private Timestamp overtimeDate;

	@Column(name = "Start_Time")
	private Timestamp startTime;

	@Column(name = "End_Time")
	private Timestamp endTime;

	@Column(name = "Meal_Duration")
	private Timestamp mealDuration;

	@ManyToOne
	@JoinColumn(name = "Day_Type")
	private AppCodeMaster dayType;

	@Column(name = "OT_Hours")
	private Double otHours;

	@Column(name = "OT_1_5_Hours")
	private Double ot15Hours;

	@Column(name = "OT_1_0_Day")
	private Double ot10Day;

	@Column(name = "OT_2_0_Day ")
	private Double ot20Day;

	@Column(name = "Remarks")
	private String remarks;

	@Column(name = "Start_Time_Changed")
	private boolean startTimeChanged;

	@Column(name = "End_Time_Changed ")
	private boolean endTimeChanged;

	@Column(name = "Meal_Duration_Changed ")
	private boolean mealDurationChanged;

	@Column(name = "Day_Type_Changed ")
	private boolean dayTypeChanged;

	public long getOvertimeApplicationDetailID() {
		return overtimeApplicationDetailID;
	}

	public void setOvertimeApplicationDetailID(long overtimeApplicationDetailID) {
		this.overtimeApplicationDetailID = overtimeApplicationDetailID;
	}

	public CoherentOvertimeApplication getCoherentOvertimeApplication() {
		return coherentOvertimeApplication;
	}

	public void setCoherentOvertimeApplication(
			CoherentOvertimeApplication coherentOvertimeApplication) {
		this.coherentOvertimeApplication = coherentOvertimeApplication;
	}

	public Timestamp getOvertimeDate() {
		return overtimeDate;
	}

	public void setOvertimeDate(Timestamp overtimeDate) {
		this.overtimeDate = overtimeDate;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Timestamp getMealDuration() {
		return mealDuration;
	}

	public void setMealDuration(Timestamp mealDuration) {
		this.mealDuration = mealDuration;
	}

	public AppCodeMaster getDayType() {
		return dayType;
	}

	public void setDayType(AppCodeMaster dayType) {
		this.dayType = dayType;
	}

	public Double getOtHours() {
		return otHours;
	}

	public void setOtHours(Double otHours) {
		this.otHours = otHours;
	}

	public Double getOt15Hours() {
		return ot15Hours;
	}

	public void setOt15Hours(Double ot15Hours) {
		this.ot15Hours = ot15Hours;
	}

	public Double getOt10Day() {
		return ot10Day;
	}

	public void setOt10Day(Double ot10Day) {
		this.ot10Day = ot10Day;
	}

	public Double getOt20Day() {
		return ot20Day;
	}

	public void setOt20Day(Double ot20Day) {
		this.ot20Day = ot20Day;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isStartTimeChanged() {
		return startTimeChanged;
	}

	public void setStartTimeChanged(boolean startTimeChanged) {
		this.startTimeChanged = startTimeChanged;
	}

	public boolean isEndTimeChanged() {
		return endTimeChanged;
	}

	public void setEndTimeChanged(boolean endTimeChanged) {
		this.endTimeChanged = endTimeChanged;
	}

	public boolean isMealDurationChanged() {
		return mealDurationChanged;
	}

	public void setMealDurationChanged(boolean mealDurationChanged) {
		this.mealDurationChanged = mealDurationChanged;
	}

	public boolean isDayTypeChanged() {
		return dayTypeChanged;
	}

	public void setDayTypeChanged(boolean dayTypeChanged) {
		this.dayTypeChanged = dayTypeChanged;
	}

}
