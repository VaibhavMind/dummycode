package com.payasia.common.form;

import java.io.Serializable;

public class ConfigureClaimItemForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -377411929883552934L;
	private String item;
	private String shortlist;
	private String visible;

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getShortlist() {
		return shortlist;
	}

	public void setShortlist(String shortlist) {
		this.shortlist = shortlist;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

}
