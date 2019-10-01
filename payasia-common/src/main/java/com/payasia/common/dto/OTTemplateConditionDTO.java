/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;

public class OTTemplateConditionDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 337901116503210173L;
	private String templateName;
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
