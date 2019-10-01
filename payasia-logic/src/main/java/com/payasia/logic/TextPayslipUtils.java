package com.payasia.logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.TextPaySlipListDTO;

public interface TextPayslipUtils {

	TextPaySlipListDTO readMalaysiaTextPayslip(CommonsMultipartFile txtFile)
			throws ParseException, FileNotFoundException, IOException;

	TextPaySlipListDTO readHongKongTextPayslip(CommonsMultipartFile txtFile)
			throws ParseException, FileNotFoundException, IOException;

	TextPaySlipListDTO readSingaporeTextPayslip(CommonsMultipartFile txtFile)
			throws ParseException, FileNotFoundException, IOException;

	TextPaySlipListDTO readIndonesiaTextPayslip(CommonsMultipartFile txtFile)
			throws ParseException, FileNotFoundException, IOException;

	TextPaySlipListDTO readSingaporeTextPayslipPaymentModeCash(
			CommonsMultipartFile txtFile) throws ParseException,
			FileNotFoundException, IOException;
}
