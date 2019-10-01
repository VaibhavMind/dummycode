package com.payasia.dao.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Workday_Paygroup_Employee_TimeTracking")
public class WorkdayPaygroupEmployeeTimeTracking extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Workday_Paygroup_Employee_TimeTracking_ID")
	private long workdayPaygroupEmployeeTimeTrackingId;
	
	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;
	
	@Column(name = "Code")
	private String code;

	@Column(name = "Quantity")
	private BigDecimal quantity;
	
	@Column(name = "Unit_of_Time")
	private String unitOfTime;

	public long getWorkdayPaygroupEmployeeTimeTrackingId() {
		return workdayPaygroupEmployeeTimeTrackingId;
	}

	public void setWorkdayPaygroupEmployeeTimeTrackingId(long workdayPaygroupEmployeeTimeTrackingId) {
		this.workdayPaygroupEmployeeTimeTrackingId = workdayPaygroupEmployeeTimeTrackingId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUnitOfTime() {
		return unitOfTime;
	}

	public void setUnitOfTime(String unitOfTime) {
		this.unitOfTime = unitOfTime;
	}
}
