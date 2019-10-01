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

import java.util.List;

public class TextPaySlipDTO {

	private List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSection1DTOList;
	private List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSection2DTOList;
	private List<TotalIncomeSectionTextPayslipDTO> totalIncomeSectionDTOList;
	private List<BankInfoTextPayslipDTO> bankInfoSectionDTOList;
	private List<SummarySectionTextPayslipDTO> summarySectionDTOList;

	public List<TotalIncomeSectionTextPayslipDTO> getTotalIncomeSectionDTOList() {
		return totalIncomeSectionDTOList;
	}

	public void setTotalIncomeSectionDTOList(
			List<TotalIncomeSectionTextPayslipDTO> totalIncomeSectionDTOList) {
		this.totalIncomeSectionDTOList = totalIncomeSectionDTOList;
	}

	public List<BankInfoTextPayslipDTO> getBankInfoSectionDTOList() {
		return bankInfoSectionDTOList;
	}

	public void setBankInfoSectionDTOList(
			List<BankInfoTextPayslipDTO> bankInfoSectionDTOList) {
		this.bankInfoSectionDTOList = bankInfoSectionDTOList;
	}

	public List<SummarySectionTextPayslipDTO> getSummarySectionDTOList() {
		return summarySectionDTOList;
	}

	public void setSummarySectionDTOList(
			List<SummarySectionTextPayslipDTO> summarySectionDTOList) {
		this.summarySectionDTOList = summarySectionDTOList;
	}

	public List<EmpInfoHeaderTextPayslipDTO> getEmpInfoHeaderSection1DTOList() {
		return empInfoHeaderSection1DTOList;
	}

	public void setEmpInfoHeaderSection1DTOList(
			List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSection1DTOList) {
		this.empInfoHeaderSection1DTOList = empInfoHeaderSection1DTOList;
	}

	public List<EmpInfoHeaderTextPayslipDTO> getEmpInfoHeaderSection2DTOList() {
		return empInfoHeaderSection2DTOList;
	}

	public void setEmpInfoHeaderSection2DTOList(
			List<EmpInfoHeaderTextPayslipDTO> empInfoHeaderSection2DTOList) {
		this.empInfoHeaderSection2DTOList = empInfoHeaderSection2DTOList;
	}

}
