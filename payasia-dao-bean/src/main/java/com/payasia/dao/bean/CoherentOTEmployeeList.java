package com.payasia.dao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Coherent_OT_Employee_List")
public class CoherentOTEmployeeList extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8532269562414780574L;

	@Id
	@GeneratedValue
	@Column(name = "Coherent_OT_Employee_List_ID")
	private long CoherentOTEmployeeListID;

	@ManyToOne
	@JoinColumn(name = "Company_ID")
	private Company company;

	@ManyToOne
	@JoinColumn(name = "Employee_ID")
	private Employee employee;

	public long getCoherentOTEmployeeListID() {
		return CoherentOTEmployeeListID;
	}

	public void setCoherentOTEmployeeListID(long coherentOTEmployeeListID) {
		CoherentOTEmployeeListID = coherentOTEmployeeListID;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
