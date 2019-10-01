package com.payasia.common.form;

import java.io.Serializable;

public class EmpMyBalancesForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5324535004641872821L;
	private String leaveType;
	private String balances;

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getBalances() {
		return balances;
	}

	public void setBalances(String balances) {
		this.balances = balances;
	}

}
