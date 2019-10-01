package com.payasia.common.dto;


import java.util.List;

public class EmployeeCompanyTupleDTO {
	
	
	List<Object[]> tupleListCompany;
	List<Object[]> tupleListEmployee;
	
	public List<Object[]> getTupleListCompany() {
		return tupleListCompany;
	}
	public void setTupleListCompany(List<Object[]> tupleListCompany) {
		this.tupleListCompany = tupleListCompany;
	}
	public List<Object[]> getTupleListEmployee() {
		return tupleListEmployee;
	}
	public void setTupleListEmployee(List<Object[]> tupleListEmployee) {
		this.tupleListEmployee = tupleListEmployee;
	}
	
	
	
	
	

}
