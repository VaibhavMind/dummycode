package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class PayslipDesignerResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8616409472668156336L;
	private List<PaySlipDesignerForm> labelList;

	public List<PaySlipDesignerForm> getLabelList() {
		return labelList;
	}

	public void setLabelList(List<PaySlipDesignerForm> labelList) {
		this.labelList = labelList;
	}

}
