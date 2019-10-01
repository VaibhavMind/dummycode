/*
 * Copyright 2011-2012 Kevin Seim
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http: 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.payasia.test.logic.beans;

public class BankInfoTextPayslipDTO {

	private String bankInfo;
	private String currentLabel;
	private String ytdLabel;

	public String getBankInfo() {
		return bankInfo;
	}

	public void setBankInfo(String bankInfo) {
		this.bankInfo = bankInfo;
	}

	public String getCurrentLabel() {
		return currentLabel;
	}

	public void setCurrentLabel(String currentLabel) {
		this.currentLabel = currentLabel;
	}

	public String getYtdLabel() {
		return ytdLabel;
	}

	public void setYtdLabel(String ytdLabel) {
		this.ytdLabel = ytdLabel;
	}

}
