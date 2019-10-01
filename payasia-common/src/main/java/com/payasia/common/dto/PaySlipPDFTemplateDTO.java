package com.payasia.common.dto;

import java.io.Serializable;

public class PaySlipPDFTemplateDTO implements Serializable {
	/**
	 * This class stores Template A dimensions
	 */
	private static final long serialVersionUID = -2754529955192104804L;
	private PaysSlipPDFSectionDTO logoHeaderSection = new PaysSlipPDFSectionDTO();
	private PaysSlipPDFSectionDTO otherSection = new PaysSlipPDFSectionDTO();
	private PaysSlipPDFSectionDTO footerSection = new PaysSlipPDFSectionDTO();
	public PaysSlipPDFSectionDTO getLogoHeaderSection() {
		return logoHeaderSection;
	}
	public void setLogoHeaderSection(PaysSlipPDFSectionDTO logoHeaderSection) {
		this.logoHeaderSection = logoHeaderSection;
	}
	public PaysSlipPDFSectionDTO getOtherSection() {
		return otherSection;
	}
	public void setOtherSection(PaysSlipPDFSectionDTO otherSection) {
		this.otherSection = otherSection;
	}
	public PaysSlipPDFSectionDTO getFooterSection() {
		return footerSection;
	}
	public void setFooterSection(PaysSlipPDFSectionDTO footerSection) {
		this.footerSection = footerSection;
	}

	

}