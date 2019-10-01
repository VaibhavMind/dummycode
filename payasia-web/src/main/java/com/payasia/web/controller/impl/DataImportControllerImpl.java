/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.PayslipFrequencyDTO;
import com.payasia.common.dto.TextPaySlipListDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.PaySlipDynamicFormLogic;
import com.payasia.logic.TextPayslipUtils;
import com.payasia.web.controller.DataImportController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class DataImportControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/dataImport")
public class DataImportControllerImpl implements DataImportController {

	/** The data import logic. */
	@Resource
	DataImportLogic dataImportLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/** The data import utils. */
	@Resource
	DataImportUtils dataImportUtils;
	@Resource
	TextPayslipUtils textPayslipUtils;
	@Resource
	PaySlipDynamicFormLogic paySlipDynamicFormLogic;

	private static final Logger LOGGER = Logger
			.getLogger(DataImportControllerImpl.class);
	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Resource
	FileUtils fileUtils;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.DataImportController#uploadFile(com.payasia
	 * .common.form.DataImportForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/uploadFile.html", method = RequestMethod.POST)
	@ResponseBody
	public String uploadFile(
			@ModelAttribute("dataImportForm") final DataImportForm dataImportForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		String excelFileName = dataImportForm.getFileName()
				.getOriginalFilename();
		final String fileExt = excelFileName.substring(
				excelFileName.lastIndexOf(".") + 1, excelFileName.length());

		final String modifiedfileName = new String(
				Base64.encodeBase64((dataImportForm.getFileName()
						.getOriginalFilename() + DateUtils
						.convertCurrentDateTimeWithMilliSecToString(PayAsiaConstants.TEMP_FILE_TIMESTAMP_WITH_MILLISEC_FORMAT))
						.getBytes()));

		boolean isValidFile  = FileUtils.isValidFile(dataImportForm.getFileName(), excelFileName,PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
		if(!isValidFile){
			List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
			DataImportForm importLogs = new DataImportForm();
			DataImportLogDTO error = new DataImportLogDTO();
			error.setFailureType("payasia.data.import.invalid.template");
			error.setRemarks("Please verify the template");
			error.setFromMessageSource(false);
			errors.add(error);
			importLogs.setDataImportLogList(errors);
			dataImportUtils.dataImportStatusMap.put(modifiedfileName,"error");
			dataImportUtils.dataImportStatusMap.put(modifiedfileName,"success");
			dataImportUtils.dataImportLogMap.put(modifiedfileName,importLogs);
			return modifiedfileName;
			
		}
		
		final Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		boolean payslipReleasedStatus = paySlipDynamicFormLogic
				.getPayslipReleasedStatus(companyId,
						dataImportForm.getMonthId(), dataImportForm.getYear(),
						dataImportForm.getPart());
		String entityName = dataImportLogic.getEntityName(dataImportForm
				.getEntityId());
		if (payslipReleasedStatus
				&& entityName
						.equalsIgnoreCase(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {

			List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
			DataImportForm importLogs = new DataImportForm();
			DataImportLogDTO error = new DataImportLogDTO();

			error.setFailureType("payasia.employee.payslip");
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, Integer.parseInt(String
					.valueOf(dataImportForm.getMonthId())) - 1);
			String month = calendar.getDisplayName(Calendar.MONTH,
					Calendar.LONG, Locale.getDefault());
			Object[] payslipDetailsArr = new Object[3];
			payslipDetailsArr[0] = month;
			payslipDetailsArr[1] = dataImportForm.getYear();
			payslipDetailsArr[2] = dataImportForm.getPart();
			error.setPostParameter(payslipDetailsArr);
			error.setRemarks("payasia.data.import.payslip.released.status.msg");
			error.setFromMessageSource(true);

			errors.add(error);

			importLogs.setDataImportLogList(errors);
			dataImportUtils.dataImportStatusMap.put(modifiedfileName, "error");
			dataImportUtils.dataImportStatusMap
					.put(modifiedfileName, "success");

			dataImportUtils.dataImportLogMap.put(modifiedfileName, importLogs);
			return modifiedfileName;
		}

		DataImportForm readData = new DataImportForm();
		TextPaySlipListDTO textPaySlipListDTO = null;
		List<String> zipFileNames = null;
		DataImportLogDTO zipFileDataImportSummaryDTO = null;

		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			readData = ExcelUtils.getImportedData(dataImportForm.getFileName());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			readData = ExcelUtils.getImportedDataForXLSX(dataImportForm
					.getFileName());
		} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_TXT)) {

			try {

				if (dataImportForm.getPayslipTextFormat().equalsIgnoreCase(
						PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_MALAYSIA)) {
					textPaySlipListDTO = textPayslipUtils
							.readMalaysiaTextPayslip(dataImportForm
									.getFileName());
				} else if (dataImportForm
						.getPayslipTextFormat()
						.equalsIgnoreCase(
								PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_HONGKONG)) {
					textPaySlipListDTO = textPayslipUtils
							.readHongKongTextPayslip(dataImportForm
									.getFileName());
				} else if (dataImportForm
						.getPayslipTextFormat()
						.equalsIgnoreCase(
								PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_INDONESIA)) {
					textPaySlipListDTO = textPayslipUtils
							.readIndonesiaTextPayslip(dataImportForm
									.getFileName());
				} else if (dataImportForm
						.getPayslipTextFormat()
						.equalsIgnoreCase(
								PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_SINGAPORE_PAYMENT_MODE_CASH)) {
					textPaySlipListDTO = textPayslipUtils
							.readSingaporeTextPayslipPaymentModeCash(dataImportForm
									.getFileName());
				} else {
					textPaySlipListDTO = textPayslipUtils
							.readSingaporeTextPayslip(dataImportForm
									.getFileName());
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
				DataImportForm importLogs = new DataImportForm();
				DataImportLogDTO error = new DataImportLogDTO();

				error.setFailureType("payasia.data.import.invalid.template");
				error.setRemarks("Please verify the template");
				error.setFromMessageSource(false);

				errors.add(error);

				importLogs.setDataImportLogList(errors);
				dataImportUtils.dataImportStatusMap.put(modifiedfileName,
						"error");
				dataImportUtils.dataImportStatusMap.put(modifiedfileName,
						"success");

				dataImportUtils.dataImportLogMap.put(modifiedfileName,
						importLogs);
				return modifiedfileName;
			}
		} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_ZIP)) {
			/*
			 * String filePath = downloadPath + "/company/" + companyId + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
			 * dataImportForm.getYear() + "/";
			 */

			FilePathGeneratorDTO filePathGenerator = fileUtils
					.getFileCommonPath(downloadPath, rootDirectoryName,
							companyId,
							PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, null,
							String.valueOf(dataImportForm.getYear()), null,
							null, PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

			List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();
			zipFileDataImportSummaryDTO = dataImportLogic
					.uploadPDFZipFileForPayslips(dataImportForm.getFileName(),
							filePath, fileExt.toLowerCase(), companyId,
							documentLogs, dataImportForm);

		}

		final TextPaySlipListDTO textPaySlipListDTOCopy = textPaySlipListDTO;
		final DataImportForm readDataCopy = readData;
		final DataImportLogDTO zipFileDataImportSummaryDTOCopy = zipFileDataImportSummaryDTO;

		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				DataImportForm importLogs = new DataImportForm();

				dataImportUtils.dataImportStatusMap.put(modifiedfileName,
						"processing");

				if (fileExt.toLowerCase()
						.equals(PayAsiaConstants.FILE_TYPE_XLS)
						|| fileExt.toLowerCase().equals(
								PayAsiaConstants.FILE_TYPE_XLSX)) {
					try {
						importLogs = dataImportLogic.importFile(dataImportForm,
								companyId, readDataCopy);
					} catch (PayAsiaRollBackDataException ex) {
						LOGGER.error(ex.getMessage(), ex);
						importLogs.setDataImportLogList(ex.getErrors());
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();

						DataImportLogDTO error = new DataImportLogDTO();

						error.setFailureType("payasia.record.error");
						error.setRemarks("payasia.record.error");
						error.setFromMessageSource(false);

						errors.add(error);

						importLogs.setDataImportLogList(errors);
						dataImportUtils.dataImportStatusMap.put(
								modifiedfileName, "error");
					}
				} else if (fileExt.toLowerCase().equals(
						PayAsiaConstants.FILE_TYPE_TXT)) {

					try {
						DataImportLogDTO dataImportLogDTO = dataImportLogic
								.generateTextPaySlipPDF(textPaySlipListDTOCopy,
										dataImportForm, companyId);
						importLogs.setDataImportLogSummaryDTO(dataImportLogDTO);
						importLogs.setImportTypeTextPdfFile(true);
						dataImportUtils.dataImportStatusMap.put(
								modifiedfileName, "success");
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
						DataImportLogDTO error = new DataImportLogDTO();

						error.setFailureType("payasia.data.import.invalid.template");
						error.setRemarks("Please verify the template");
						error.setFromMessageSource(false);

						errors.add(error);

						importLogs.setDataImportLogList(errors);
						importLogs.setImportTypeTextPdfFile(true);
						dataImportUtils.dataImportStatusMap.put(
								modifiedfileName, "error");
					}

				} else if (fileExt.toLowerCase().equals(
						PayAsiaConstants.FILE_TYPE_ZIP)) {

					try {
						dataImportLogic.uploadPaySlipPDF(dataImportForm,
								zipFileDataImportSummaryDTOCopy
										.getZipFileNamesList(), companyId);
						DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
						dataImportLogDTO.setImportTypeTextPdfFile(true);
						dataImportLogDTO
								.setTotalRecords(zipFileDataImportSummaryDTOCopy
										.getTotalRecords());
						dataImportLogDTO
								.setTotalSuccessRecords(zipFileDataImportSummaryDTOCopy
										.getZipFileNamesList().size());
						dataImportLogDTO
								.setTotalFailedRecords(zipFileDataImportSummaryDTOCopy
										.getInvalidEmployeeNumbersList().size());
						dataImportLogDTO
								.setInvalidEmployeeNumbersList(zipFileDataImportSummaryDTOCopy
										.getInvalidEmployeeNumbersList());
						importLogs.setDataImportLogSummaryDTO(dataImportLogDTO);
						importLogs.setImportTypeTextPdfFile(true);
						dataImportUtils.dataImportStatusMap.put(
								modifiedfileName, "success");
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
						DataImportLogDTO error = new DataImportLogDTO();

						error.setFailureType("payasia.data.import.invalid.file");
						error.setRemarks("Please verify the uploaded file");
						error.setFromMessageSource(false);

						errors.add(error);

						importLogs.setDataImportLogList(errors);
						importLogs.setImportTypeTextPdfFile(true);
						DataImportUtils.dataImportStatusMap.put(
								modifiedfileName, "error");
					}

				}

				DataImportUtils.dataImportStatusMap.put(modifiedfileName,
						"success");

				DataImportUtils.dataImportLogMap.put(modifiedfileName,
						importLogs);

			}
		};
		Thread thread = new Thread(runnable, modifiedfileName);
		thread.start();
		return modifiedfileName;

	}

	@Override
	@RequestMapping(value = "/previewTextPayslipPdf.html", method = RequestMethod.POST)
	@ResponseBody
	public String previewTextPayslipPdf(
			@ModelAttribute("dataImportForm") DataImportForm dataImportForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String excelFileName = dataImportForm.getFileName()
				.getOriginalFilename();
		final String fileExt = excelFileName.substring(
				excelFileName.lastIndexOf(".") + 1, excelFileName.length());

		TextPaySlipListDTO textPaySlipListDTO = null;

		String previewPdfFilePath = null;
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_TXT)) {

			try {
				if (dataImportForm.getPayslipTextFormat().equalsIgnoreCase(
						PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_MALAYSIA)) {
					textPaySlipListDTO = textPayslipUtils
							.readMalaysiaTextPayslip(dataImportForm
									.getFileName());
				} else if (dataImportForm
						.getPayslipTextFormat()
						.equalsIgnoreCase(
								PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_HONGKONG)) {
					textPaySlipListDTO = textPayslipUtils
							.readHongKongTextPayslip(dataImportForm
									.getFileName());
				} else {
					textPaySlipListDTO = textPayslipUtils
							.readSingaporeTextPayslip(dataImportForm
									.getFileName());
				}

				previewPdfFilePath = dataImportLogic
						.generateSamplePaySlipPDFForPreview(textPaySlipListDTO,
								dataImportForm, companyId);
			} catch (ParseException | IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return previewPdfFilePath;

	}

	@Override
	@RequestMapping(value = "/getPreviewTextPayslipPdf.html", method = RequestMethod.GET)
	public @ResponseBody void getPreviewTextPayslipPdf(
			@RequestParam(value = "previewPdfFilePath", required = true) String previewPdfFilePath,
			HttpServletRequest request, HttpServletResponse response) {

		ServletOutputStream outStream = null;
		try {
			Long companyId = (Long) request.getSession().getAttribute(
					PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

			byte[] byteFile = dataImportLogic
					.getPaySlipFromCompanyDocumentFolder(companyId,
							previewPdfFilePath);

			response.reset();
			response.setContentType("application/pdf");
			response.setContentLength(byteFile.length);
			String pdfName = "PA_SamplePaySlipPdf";

			response.setHeader("Content-Disposition", "inline;filename="
					+ pdfName);

			outStream = response.getOutputStream();
			outStream.write(byteFile);

		} catch (IOException exception) {
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		} finally {
			try {

				if (outStream != null) {

					outStream.flush();
					outStream.close();
				}

			} catch (IOException exception) {
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(),
						exception);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.DataImportController#showHistory(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/showHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String showHistory(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		DataImportForm importHistory = dataImportLogic
				.getImportHistory(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(importHistory, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.DataImportController#showTemplateList(long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/showTemplateList.html", method = RequestMethod.POST)
	@ResponseBody
	public String showTemplateList(
			@RequestParam(value = "entityId") long entityId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<ExcelImportToolForm> importHistory = dataImportLogic
				.getTemplateList(entityId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(importHistory, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.DataImportController#getCompanyFreq(javax.
	 * servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getCompanyFreq.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyFreq(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<PayslipFrequencyDTO> paySlipFreq = dataImportLogic
				.getPayslipFrequency(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(paySlipFreq, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/dataImportStatus.html", method = RequestMethod.POST)
	@ResponseBody
	public String dataImportStatus(
			@RequestParam(value = "fileName", required = true) String fileName,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {

		DataImportForm importLog = null;

		String key = fileName;
		String status = dataImportUtils.dataImportStatusMap.get(key);

		if ((status != null) && (status != "")
				&& ("SUCCESS".equals(status.toUpperCase()))) {

			dataImportUtils.dataImportStatusMap.put(key, "");

			importLog = dataImportUtils.dataImportLogMap.get(key);
			if (importLog.getDataImportLogList() != null) {
				for (DataImportLogDTO dataImportLogDTO : importLog
						.getDataImportLogList()) {
					try {
						dataImportLogDTO.setFailureType(URLEncoder.encode(
								messageSource.getMessage(
										dataImportLogDTO.getFailureType(),
										new Object[] {}, locale), "UTF-8"));
						if (dataImportLogDTO.isFromMessageSource()) {

							if (dataImportLogDTO.getPostParameter() != null) {
								dataImportLogDTO.setRemarks(URLEncoder.encode(
										messageSource.getMessage(
												dataImportLogDTO.getRemarks(),
												dataImportLogDTO
														.getPostParameter(),
												locale), "UTF-8"));
							} else {
								dataImportLogDTO.setRemarks(URLEncoder.encode(
										messageSource.getMessage(
												dataImportLogDTO.getRemarks(),
												new Object[] {}, locale),
										"UTF-8"));
							}

						}
					} catch (UnsupportedEncodingException
							| NoSuchMessageException exception) {
						LOGGER.error(exception.getMessage(), exception);
						throw new PayAsiaSystemException(
								exception.getMessage(), exception);
					}
				}
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(importLog, jsonConfig);

		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getPayslipFormat.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPayslipFormat(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String companyIsPayAsia = dataImportLogic
				.getPayslipFormatforCompany(companyId);
		return companyIsPayAsia;

	}

	@Override
	@RequestMapping(value = "/getCompanyFilterList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyFilterList(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		List<EmployeeFilterListForm> filterList = dataImportLogic
				.getCompanyFilterList(companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/configureCompanyAddress.html", method = RequestMethod.POST)
	@ResponseBody
	public String configureCompanyAddress(
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String companyAddress = dataImportLogic.configureCompanyAddress(
				companyId, dataDictionaryIds);
		return companyAddress;

	}

	@Override
	@RequestMapping(value = "/getCompanyAddressMappingList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyAddressMappingList(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EmployeeFilterListForm> filterList = dataImportLogic
				.getCompanyAddressMappingList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getCompanyAddress.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyAddress(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String companyAddress = dataImportLogic.getCompanyAddress(companyId);
		return companyAddress;
	}
}
