package com.payasia.common.dto;

import java.io.Serializable;

public class PaysSlipPDFSectionDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3181780228069074681L;
	private float height;
	private float width;
	private float yline;

	public void setHeight(float height) {
		this.height = height;
	}

	public float getHeight() {
		return height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getWidth() {
		return width;
	}

	public void setYline(float yLine) {
		this.yline = yLine;
	}

	public float getYline() {
		return yline;
	}
}
