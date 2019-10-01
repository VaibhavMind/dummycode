package com.payasia.logic;

import java.net.MalformedURLException;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.payasia.common.dto.TextPaySlipDTO;
import com.payasia.common.form.DataImportForm;
import com.payasia.dao.bean.CompanyLogo;

public interface PayAsiaTextPaySlipToPdf {

	PdfPTable createTextPaySlipPdf(Document document, int currentPageNumber,
			TextPaySlipDTO textPaySlip, DataImportForm dataImportForm,
			Long companyId, CompanyLogo companyLogo)
			throws MalformedURLException;

}
