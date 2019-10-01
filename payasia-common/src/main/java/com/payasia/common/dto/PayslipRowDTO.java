package com.payasia.common.dto;

import java.io.Serializable;

import com.mind.payasia.xml.bean.Field;

public class PayslipRowDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4393769268681018736L;
	private Field columnField1;
	private Field columnField2;
	private int leftColSize;
	private int rightColSize;
	
	
	public Field getColumnField1() {
		return columnField1;
	}
	public void setColumnField1(Field columnField1) {
		this.columnField1 = columnField1;
	}
	public Field getColumnField2() {
		return columnField2;
	}
	public void setColumnField2(Field columnField2) {
		this.columnField2 = columnField2;
	}
	public int getLeftColSize() {
		return leftColSize;
	}
	public void setLeftColSize(int leftColSize) {
		this.leftColSize = leftColSize;
	}
	public int getRightColSize() {
		return rightColSize;
	}
	public void setRightColSize(int rightColSize) {
		this.rightColSize = rightColSize;
	}
	
	
}
