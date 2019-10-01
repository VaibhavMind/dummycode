package com.payasia.common.form;

import java.io.Serializable;

public class fileDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 674992379722713322L;
	private String fName;
	private String lName;

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

}
