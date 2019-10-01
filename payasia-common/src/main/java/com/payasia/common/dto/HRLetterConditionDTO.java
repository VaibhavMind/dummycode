/**
 * @author vivekjain
 *
 */
package com.payasia.common.dto;

import java.io.Serializable;

public class HRLetterConditionDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2734770369893605786L;
	private String letterName;
	private String letterDescription;
	private String letterSubject;
	
	public String getLetterName() {
		return letterName;
	}
	public void setLetterName(String letterName) {
		this.letterName = letterName;
	}
	public String getLetterDescription() {
		return letterDescription;
	}
	public void setLetterDescription(String letterDescription) {
		this.letterDescription = letterDescription;
	}
	public String getLetterSubject() {
		return letterSubject;
	}
	public void setLetterSubject(String letterSubject) {
		this.letterSubject = letterSubject;
	}
}
