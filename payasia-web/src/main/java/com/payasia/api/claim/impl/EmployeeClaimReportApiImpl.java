package com.payasia.api.claim.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.claim.EmployeeClaimReportApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.ClaimDetailsReportDTO;
import com.payasia.common.dto.ClaimItemDTO;
import com.payasia.common.dto.ClaimReportHeaderDTO;
import com.payasia.common.dto.ClaimTemplateDTO;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.logic.ClaimReportsLogic;
import com.payasia.logic.GeneralLogic;

import net.sf.json.JSONObject;
import net.sf.jxls.transformer.XLSTransformer;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_CLAIM + "/claimReports", produces = {
		MediaType.APPLICATION_JSON_UTF8_VALUE })
public class EmployeeClaimReportApiImpl implements EmployeeClaimReportApi {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(EmployeeClaimReportApiImpl.class);

	@Resource
	ClaimReportsLogic claimReportsLogic;

	@Resource
	GeneralLogic generalLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	@Autowired
	private ServletContext servletContext;

	@Autowired
	private MessageSource messageSource;

	@Override
	@GetMapping(value = "claimReportType ")
	public ResponseEntity<?> getClaimReportType() {
		List<String> claimReportTypeList = claimReportsLogic.getClaimReportType();
		JSONObject object = new JSONObject();
		object.put("claimReportTypeList", claimReportTypeList);
		object.put("status", "success");
		object.put("code", 200);
		return new ResponseEntity<>(object, HttpStatus.OK);
	}

	@Override
	@GetMapping(value = "allClaimTemplate")
	public ResponseEntity<?> getAllClaimTemplate() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<ClaimTemplateDTO> claimTemplateDTOList = claimReportsLogic.getAllClaimTemplate(companyId);
		JSONObject object = new JSONObject();
		object.put("claimTemplateDTOList", claimTemplateDTOList);
		object.put("status", "success");
		object.put("code", 200);
		return new ResponseEntity<>(claimTemplateDTOList, HttpStatus.OK);
	}

	@Override
	@GetMapping(value = "claimItemListForClaimDetailsReport")
	public ResponseEntity<?> getClaimItemListForClaimDetailsReport(
			@RequestParam(value = "claimTemplateId", required = true) Long[] claimTemplateIdArr) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<ClaimItemDTO> claimItemList = claimReportsLogic.getClaimItemListForClaimDetailsReport(companyId,
				claimTemplateIdArr);
		JSONObject object = new JSONObject();
		object.put("claimItemList", claimItemList);
		object.put("status", "success");
		object.put("code", 200);
		return new ResponseEntity<>(claimItemList, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "showClaimTransactionReport")
	public ResponseEntity<?> showClaimTransactionReport(@RequestBody ClaimReportsForm claimReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			@RequestParam(value = "isCheckedFromCreatedDate", required = false) Boolean isCheckedFromCreatedDate) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Locale locale = UserContext.getLocale();

		boolean hasLundinTimesheetModule = UserContext.isLundinTimesheetModule();

		if (claimReportsForm.getStatusName() == null && isCheckedFromCreatedDate == false) {
			claimReportsForm.setStatusName("Approved");
		}

		ClaimDetailsReportDTO claimDetailsReportDTO = claimReportsLogic.showClaimTransactionReport(companyId,
				claimReportsForm, employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale,
				isCheckedFromCreatedDate, messageSource);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", claimDetailsReportDTO.getClaimDetailsDataDTOs());
		jsonObject.put("aoColumns", claimDetailsReportDTO.getClaimHeaderDTOs());
		jsonObject.put("status", "success");
		jsonObject.put("code", 200);

		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "genEmployeeClaimTranasactionReport")
	public ResponseEntity<?> genEmployeeClaimTranasactionReport(@RequestBody ClaimReportsForm claimReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			@RequestParam(value = "isCheckedFromCreatedDate", required = false) Boolean isCheckedFromCreatedDate,
			@RequestParam(value = "fileType", required = false) String fileType) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Locale locale = UserContext.getLocale();

		boolean hasLundinTimesheetModule = UserContext.isLundinTimesheetModule();

		if (claimReportsForm.getStatusName() == null && isCheckedFromCreatedDate == false) {
			claimReportsForm.setStatusName("Approved");
		}

		ClaimDetailsReportDTO claimDetailsReportDTO = claimReportsLogic.showClaimTransactionReport(companyId,
				claimReportsForm, employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale,
				isCheckedFromCreatedDate, messageSource);
		Map<String, Object> reportDataMap = new HashMap<>();

		ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
		claimantName.setmDataProp("claimantName");
		claimantName.setsTitle("Claimaint Name");

		UUID uuid = UUID.randomUUID();

		boolean toShowClaimant = claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("claimDetailsReportList", claimDetailsReportDTO.getClaimDetailsDataDTOs());
		if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", claimDetailsReportDTO.getDataDictNameList());
		}
		beans.put("claimDetailsReportCustomDataList", claimDetailsReportDTO.getClaimDetailsCustomDataDTOs());
		beans.put("claimIsSubordinateEnable", claimDetailsReportDTO.isSubordinateCompanyEmployee());

		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName;
		if (hasLundinTimesheetModule) {
			if (toShowClaimant) {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext.getRealPath(
							"/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantLundin.xlsx");
				} else {
					templateFileName = servletContext.getRealPath(
							"/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantWOCustomLundin.xlsx");
				}
			} else {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportLundin.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWOCustomLundin.xlsx");
				}
			}
		} else {
			if (toShowClaimant) {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimant.xlsx");
				} else {
					templateFileName = servletContext.getRealPath(
							"/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantWOCustom.xlsx");
				}
			} else {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReport.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWOCustom.xlsx");
				}
			}
		}

		String destFileName = "";
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/ClaimTransactionReport" + uuid + ".xlsx";
		try {
			transformer.transformXLS(templateFileName, beans, destFileNameExcel);
			String fileName = "Claim Transaction Report";
			switch (fileType.toLowerCase()) {
			case "pdf":
				destFileName = PAYASIA_TEMP_PATH + "/ClaimTransactionReport" + uuid + ".pdf";
				ArrayList<String> cmdList = new ArrayList<String>();
				cmdList.add(DOCUMENT_CONVERTOR_PATH);
				cmdList.add(destFileNameExcel);
				cmdList.add(destFileName);

				ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
				processBuilder.redirectErrorStream(true);
				Process process = processBuilder.start();
				process.waitFor();
				fileName = fileName + ".pdf";
				break;
			case "excel":
				destFileName = destFileNameExcel;
				fileName = fileName + ".xlsx";
				break;
			}

			Path path = Paths.get(destFileName);
			byte[] data = Files.readAllBytes(path);
			reportDataMap.put("data", data);
			reportDataMap.put("fileName", fileName);

			reportDataMap.put("status", "success");
			reportDataMap.put("code", 200);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			reportDataMap.put("status", "error");
			reportDataMap.put("code", 500);
		} finally {
			tempDestPdfFile = new File(destFileName);
			if (tempDestPdfFile != null) {
				tempDestPdfFile.delete();
			}
			tempDestExcelFile = new File(destFileNameExcel);
			if (tempDestExcelFile != null) {
				tempDestExcelFile.delete();
			}
			tempFolder.delete();
		}

		return new ResponseEntity<>(reportDataMap, HttpStatus.OK);
	}

	/*
	 * @Override
	 * 
	 * @GetMapping(value = "allClaimCategory") public ResponseEntity<?>
	 * getAllClaimCategory(HttpServletRequest request) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID);
	 * 
	 * List<ClaimCategoryForm> claimCategoryList =
	 * claimReportsLogic.getAllClaimCategory(companyId); return new
	 * ResponseEntity<>(claimCategoryList, HttpStatus.OK); }
	 */

	/*
	 * @Override
	 * 
	 * @GetMapping(value = "allClaimTemplateWithTemplateCapping") public
	 * ResponseEntity<?>
	 * getAllClaimTemplateWithTemplateCapping(HttpServletRequest request) { Long
	 * companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); List<ClaimReportsForm> claimTemplateList =
	 * claimReportsLogic.getAllClaimTemplateWithTemplateCapping(companyId);
	 * return new ResponseEntity<>(claimTemplateList, HttpStatus.OK);
	 * 
	 * }
	 */

	/*
	 * @Override
	 * 
	 * @GetMapping(value = "allClaimTemplateWithClaimItemCapping") public
	 * ResponseEntity<?>
	 * getAllClaimTemplateWithClaimItemCapping(HttpServletRequest request) {
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); List<ClaimReportsForm> claimTemplateList =
	 * claimReportsLogic.getAllClaimTemplateWithClaimItemCapping(companyId);
	 * return new ResponseEntity<>(claimTemplateList, HttpStatus.OK); }
	 */

	/*
	 * @Override
	 * 
	 * @GetMapping(value = "claimCategoryList") public ResponseEntity<?>
	 * getClaimCategoryList(HttpServletRequest request) { Long companyId =
	 * (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); List<ClaimReportsForm> claimCategoryList =
	 * claimReportsLogic.getClaimCategoryList(companyId); return new
	 * ResponseEntity<>(claimCategoryList, HttpStatus.OK); }
	 */

	/*
	 * @Override
	 * 
	 * @GetMapping(value = "claimItemList") public ResponseEntity<?>
	 * getClaimItemList(@RequestParam(value = "claimCategoryId", required =
	 * true) Long claimCategoryId, HttpServletRequest request) { Long companyId
	 * = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); List<ClaimReportsForm> claimItemList =
	 * claimReportsLogic.getClaimItemList(companyId, claimCategoryId); return
	 * new ResponseEntity<>(claimItemList, HttpStatus.OK); }
	 */

	/*
	 * @Override
	 * 
	 * @GetMapping(value = "claimItemListByTemplateId") public ResponseEntity<?>
	 * getClaimItemListByTemplateId(
	 * 
	 * @RequestParam(value = "claimTemplateId", required = true) Long
	 * claimTemplateId, HttpServletRequest request) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); List<ClaimReportsForm> claimItemList =
	 * claimReportsLogic.getClaimItemListByTemplateId(companyId,
	 * claimTemplateId); return new ResponseEntity<>(claimItemList,
	 * HttpStatus.OK); }
	 * 
	 * 
	 * 
	 * @Override
	 * 
	 * @GetMapping(value = "claimBatchList") public ResponseEntity<?>
	 * getClaimBatchList(HttpServletRequest request) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); List<ClaimReportsForm> claimBatchList =
	 * claimReportsLogic.getClaimBatchList(companyId); return new
	 * ResponseEntity<>(claimBatchList, HttpStatus.OK); }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showClaimReviewerReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showClaimReviewerReport(@ModelAttribute(value
	 * = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
	 * HttpServletRequest request, HttpServletResponse response, Locale locale)
	 * { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); ClaimReviewerInfoReportDTO claimRevDataDTO =
	 * claimReportsLogic.showClaimReviewerReport(companyId, claimReportsForm,
	 * employeeId); JSONObject jsonObject = new JSONObject();
	 * jsonObject.put("aaData", claimRevDataDTO.getClaimReviewerDataDTOs());
	 * jsonObject.put("aoColumns", claimRevDataDTO.getClaimHeaderDTOs()); return
	 * jsonObject.toString();
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genClaimReviewerReportExcelFile.html", method =
	 * RequestMethod.POST)
	 * 
	 * @Override public void genClaimReviewerReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); ClaimReviewerInfoReportDTO claimRevDataDTO =
	 * claimReportsLogic.showClaimReviewerReport(companyId, claimReportsForm,
	 * employeeId);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("claimReviewerReportList",
	 * claimRevDataDTO.getClaimReviewerDataDTOs()); XLSTransformer transformer =
	 * new XLSTransformer(); File tempDestFile = null; File tempFolder = new
	 * File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid); tempFolder.mkdirs();
	 * String templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimReviewerReport.xlsx"); String
	 * destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Claim Reviewer Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Reviewer Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { try { if (outputStream != null) { outputStream.flush();
	 * outputStream.close(); } tempDestFile = new File(destFileName); if
	 * (tempDestFile != null) { tempDestFile.delete(); } tempFolder.delete(); }
	 * catch (IOException ioException) { LOGGER.error(ioException.getMessage(),
	 * ioException); throw new PayAsiaSystemException(ioException.getMessage(),
	 * ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genClaimReviewerReportPDFFile.html", method =
	 * RequestMethod.POST)
	 * 
	 * @Override public void genClaimReviewerReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); ClaimReviewerInfoReportDTO claimRevDataDTO =
	 * claimReportsLogic.showClaimReviewerReport(companyId, claimReportsForm,
	 * employeeId);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("claimReviewerReportList",
	 * claimRevDataDTO.getClaimReviewerDataDTOs()); XLSTransformer transformer =
	 * new XLSTransformer(); File tempDestPdfFile = null; File tempDestExcelFile
	 * = null; File tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" +
	 * uuid); tempFolder.mkdirs(); String templateFileName =
	 * servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimReviewerReport.xlsx"); String
	 * destFileNameExcel = PAYASIA_TEMP_PATH + "/ClaimReviewerReport" + uuid +
	 * ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH + "/ClaimReviewerReport" +
	 * uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Reviewer Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try { if (outputStream
	 * != null) { outputStream.flush(); outputStream.close(); } } catch
	 * (IOException ioException) { LOGGER.error(ioException.getMessage(),
	 * ioException); throw new PayAsiaSystemException(ioException.getMessage(),
	 * ioException); } }
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showClaimDetailsReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showClaimDetailsReport(@ModelAttribute(value
	 * = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); boolean hasLundinTimesheetModule = (boolean)
	 * request.getSession() .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showClaimDetailsReport(companyId, claimReportsForm,
	 * employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale);
	 * JSONObject jsonObject = new JSONObject(); jsonObject.put("aaData",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs());
	 * jsonObject.put("aoColumns", claimDetailsReportDTO.getClaimHeaderDTOs());
	 * try { return URLEncoder.encode(jsonObject.toString(), "UTF-8"); } catch
	 * (UnsupportedEncodingException e) { LOGGER.error(e.getMessage(), e); }
	 * return null;
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showPaidClaimDetailsReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showPaidClaimDetailsReport(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID);
	 * 
	 * Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * boolean hasLundinTimesheetModule = (boolean) request.getSession()
	 * .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showPaidClaimDetailsReport(companyId, claimReportsForm,
	 * employeeId, dataDictionaryIds, hasLundinTimesheetModule); JSONObject
	 * jsonObject = new JSONObject(); jsonObject.put("aaData",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs());
	 * jsonObject.put("aoColumns", claimDetailsReportDTO.getClaimHeaderDTOs());
	 * return jsonObject.toString();
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genPaidClaimDetailsReportExcelFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genPaidClaimDetailsReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID);
	 * 
	 * Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * boolean hasLundinTimesheetModule = (boolean) request.getSession()
	 * .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showPaidClaimDetailsReport(companyId, claimReportsForm,
	 * employeeId, dataDictionaryIds, hasLundinTimesheetModule);
	 * ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
	 * claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name"); boolean toShowClaimant =
	 * claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("claimDetailsReportList",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs()); if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * claimDetailsReportDTO.getDataDictNameList()); }
	 * beans.put("claimDetailsReportCustomDataList",
	 * claimDetailsReportDTO.getClaimDetailsCustomDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestFile = null; File
	 * tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
	 * tempFolder.mkdirs(); String templateFileName;
	 * 
	 * if (hasLundinTimesheetModule) { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimantLundin.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimantWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportLundin.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } } else { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimant.xlsx")
	 * ; } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimantWOCustom.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext
	 * .getRealPath("/resources/ClaimReportTemplate/PaidClaimDetailsReport.xlsx"
	 * ); } else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWOCustom.xlsx"); }
	 * 
	 * } }
	 * 
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Claim Details Report With Paid Status" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value =
	 * "/genCategoryWiseClaimDetailsReportExcelFile.html", method =
	 * RequestMethod.POST)
	 * 
	 * @Override public void genCategoryWiseClaimDetailsReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID);
	 * 
	 * Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * boolean hasLundinTimesheetModule = (boolean) request.getSession()
	 * .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * categoryWiseClaimDetailsReportDTO =
	 * claimReportsLogic.showCategoryWiseClaimDetailsReport( companyId,
	 * claimReportsForm, employeeId, dataDictionaryIds,
	 * hasLundinTimesheetModule); ClaimReportHeaderDTO claimantName = new
	 * ClaimReportHeaderDTO(); claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name");
	 * 
	 * String header3 = "Claims Details Report For Claims Period Between " +
	 * claimReportsForm.getStartDate() + " And " +
	 * claimReportsForm.getEndDate();
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("header1", categoryWiseClaimDetailsReportDTO.getCompanyName());
	 * beans.put("header3", header3);
	 * 
	 * beans.put("claimDetailsTranMap",
	 * categoryWiseClaimDetailsReportDTO.getClaimDetailsTranMap());
	 * 
	 * 
	 * beans.put("claimDetailsReportList",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs());
	 * 
	 * 
	 * if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * claimDetailsReportDTO.getDataDictNameList()); }
	 * beans.put("claimDetailsReportCustomDataList",
	 * claimDetailsReportDTO.getClaimDetailsCustomDataDTOs());
	 * 
	 * XLSTransformer transformer = new XLSTransformer(); File tempDestFile =
	 * null; File tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" +
	 * uuid); tempFolder.mkdirs(); String templateFileName;
	 * 
	 * if (dataDictionaryIds.length > 1) {
	 * 
	 * beans.put("dataDictNameList",
	 * categoryWiseClaimDetailsReportDTO.getDataDictNameList());
	 * 
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/CategoryWiseClaimDetailsReport.xlsx");
	 * 
	 * } else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/CategoryWiseClaimDetailsReportWOCustom.xlsx"
	 * ); }
	 * 
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Claim Details Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genCategoryWiseClaimDetailsReportPDFFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genCategoryWiseClaimDetailsReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID);
	 * 
	 * Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * boolean hasLundinTimesheetModule = (boolean) request.getSession()
	 * .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * categoryWiseClaimDetailsReportDTO =
	 * claimReportsLogic.showCategoryWiseClaimDetailsReport( companyId,
	 * claimReportsForm, employeeId, dataDictionaryIds,
	 * hasLundinTimesheetModule); ClaimReportHeaderDTO claimantName = new
	 * ClaimReportHeaderDTO(); claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name");
	 * 
	 * String header3 = "Claims Details Report For Claims Period Between " +
	 * claimReportsForm.getStartDate() + " And " +
	 * claimReportsForm.getEndDate();
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("header1", categoryWiseClaimDetailsReportDTO.getCompanyName());
	 * beans.put("header3", header3);
	 * 
	 * beans.put("claimDetailsTranMap",
	 * categoryWiseClaimDetailsReportDTO.getClaimDetailsTranMap());
	 * 
	 * XLSTransformer transformer = new XLSTransformer(); File tempDestPdfFile =
	 * null; File tempDestExcelFile = null; File tempFolder = new
	 * File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid); tempFolder.mkdirs();
	 * String templateFileName;
	 * 
	 * if (dataDictionaryIds.length > 1) {
	 * 
	 * beans.put("dataDictNameList",
	 * categoryWiseClaimDetailsReportDTO.getDataDictNameList());
	 * 
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/CategoryWiseClaimDetailsReport.xlsx");
	 * 
	 * } else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/CategoryWiseClaimDetailsReportWOCustom.xlsx"
	 * ); }
	 * 
	 * String destFileNameExcel = PAYASIA_TEMP_PATH + "/ClaimDetailsReport" +
	 * uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH + "/ClaimDetailsReport" + uuid
	 * + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genClaimDetailsReportExcelFile.html", method =
	 * RequestMethod.POST)
	 * 
	 * @Override public void genClaimDetailsReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); boolean hasLundinTimesheetModule = (boolean)
	 * request.getSession() .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showClaimDetailsReport(companyId, claimReportsForm,
	 * employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale);
	 * ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
	 * claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name"); boolean toShowClaimant =
	 * claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("claimDetailsReportList",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs()); if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * claimDetailsReportDTO.getDataDictNameList()); }
	 * beans.put("claimDetailsReportCustomDataList",
	 * claimDetailsReportDTO.getClaimDetailsCustomDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestFile = null; File
	 * tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
	 * tempFolder.mkdirs(); String templateFileName;
	 * 
	 * if (hasLundinTimesheetModule) { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimantLundin.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimantWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportLundin.xlsx"); } else {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWOCustomLundin.xlsx");
	 * }
	 * 
	 * } } else { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimant.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimantWOCustom.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext
	 * .getRealPath("/resources/ClaimReportTemplate/ClaimDetailsReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWOCustom.xlsx"); }
	 * 
	 * } }
	 * 
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Claim Details Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genClaimDetailsReportPDFFile.html", method =
	 * RequestMethod.POST)
	 * 
	 * @Override public void genClaimDetailsReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); boolean hasLundinTimesheetModule = (boolean)
	 * request.getSession() .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showClaimDetailsReport(companyId, claimReportsForm,
	 * employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale);
	 * 
	 * ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
	 * claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name"); boolean toShowClaimant =
	 * claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("claimDetailsReportList",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs()); if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * claimDetailsReportDTO.getDataDictNameList()); }
	 * beans.put("claimDetailsReportCustomDataList",
	 * claimDetailsReportDTO.getClaimDetailsCustomDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestPdfFile = null; File
	 * tempDestExcelFile = null; File tempFolder = new File(PAYASIA_TEMP_PATH +
	 * "/claimreport/" + uuid); tempFolder.mkdirs(); String templateFileName; if
	 * (hasLundinTimesheetModule) { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimantLundin.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimantWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportLundin.xlsx"); } else {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWOCustomLundin.xlsx");
	 * }
	 * 
	 * } } else { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimant.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWithClaimantWOCustom.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext
	 * .getRealPath("/resources/ClaimReportTemplate/ClaimDetailsReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimDetailsReportWOCustom.xlsx"); }
	 * 
	 * } }
	 * 
	 * String destFileNameExcel = PAYASIA_TEMP_PATH + "/ClaimDetailsReport" +
	 * uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH + "/ClaimDetailsReport" + uuid
	 * + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genPaidClaimDetailsReportPDFFile.html", method
	 * = RequestMethod.POST)
	 * 
	 * @Override public void genPaidClaimDetailsReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID);
	 * 
	 * Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * boolean hasLundinTimesheetModule = (boolean) request.getSession()
	 * .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showPaidClaimDetailsReport(companyId, claimReportsForm,
	 * employeeId, dataDictionaryIds, hasLundinTimesheetModule);
	 * 
	 * ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
	 * claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name"); boolean toShowClaimant =
	 * claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("claimDetailsReportList",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs()); if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * claimDetailsReportDTO.getDataDictNameList()); }
	 * beans.put("claimDetailsReportCustomDataList",
	 * claimDetailsReportDTO.getClaimDetailsCustomDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestPdfFile = null; File
	 * tempDestExcelFile = null; File tempFolder = new File(PAYASIA_TEMP_PATH +
	 * "/claimreport/" + uuid); tempFolder.mkdirs(); String templateFileName; if
	 * (hasLundinTimesheetModule) { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimantLundin.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimantWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportLundin.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } } else { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimant.xlsx")
	 * ; } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWithClaimantWOCustom.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext
	 * .getRealPath("/resources/ClaimReportTemplate/PaidClaimDetailsReport.xlsx"
	 * ); } else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/PaidClaimDetailsReportWOCustom.xlsx"); }
	 * 
	 * } }
	 * 
	 * String destFileNameExcel = PAYASIA_TEMP_PATH +
	 * "/ClaimDetailsReportWithPaidStatus" + uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH +
	 * "/ClaimDetailsReportWithPaidStatus" + uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showBatchWiseClaimDetailsReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showBatchWiseClaimDetailsReport(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); boolean hasLundinTimesheetModule = (boolean)
	 * request.getSession() .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showBatchWiseClaimDetailsReport(companyId,
	 * claimReportsForm, employeeId, dataDictionaryIds,
	 * hasLundinTimesheetModule); JSONObject jsonObject = new JSONObject();
	 * jsonObject.put("aaData",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs());
	 * jsonObject.put("aoColumns", claimDetailsReportDTO.getClaimHeaderDTOs());
	 * return jsonObject.toString();
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genBatchWiseClaimDetailsReportExcelFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genBatchWiseClaimDetailsReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); boolean hasLundinTimesheetModule = (boolean)
	 * request.getSession() .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE); ClaimDetailsReportDTO
	 * claimDetailsReportDTO =
	 * claimReportsLogic.showBatchWiseClaimDetailsReport(companyId,
	 * claimReportsForm, employeeId, dataDictionaryIds,
	 * hasLundinTimesheetModule); ClaimReportHeaderDTO claimantName = new
	 * ClaimReportHeaderDTO(); claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name"); boolean toShowClaimant =
	 * claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("batchWiseClaimDetailsReportList",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs()); if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * claimDetailsReportDTO.getDataDictNameList()); }
	 * beans.put("claimDetailsReportCustomDataList",
	 * claimDetailsReportDTO.getClaimDetailsCustomDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestFile = null; File
	 * tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
	 * tempFolder.mkdirs(); String templateFileName;
	 * 
	 * if (hasLundinTimesheetModule) { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimantLundin.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimantWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportLundin.xlsx");
	 * } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } } else { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimant.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimantWOCustom.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWOCustom.xlsx"
	 * ); }
	 * 
	 * } }
	 * 
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Batch Wise Claim Details Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Batch Wise Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genBatchWiseClaimDetailsReportPDFFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genBatchWiseClaimDetailsReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); boolean hasLundinTimesheetModule = (boolean)
	 * request.getSession() .getAttribute(PayAsiaSessionAttributes.
	 * COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
	 * 
	 * ClaimDetailsReportDTO claimDetailsReportDTO =
	 * claimReportsLogic.showBatchWiseClaimDetailsReport(companyId,
	 * claimReportsForm, employeeId, dataDictionaryIds,
	 * hasLundinTimesheetModule); ClaimReportHeaderDTO claimantName = new
	 * ClaimReportHeaderDTO(); claimantName.setmDataProp("claimantName");
	 * claimantName.setsTitle("Claimaint Name"); boolean toShowClaimant =
	 * claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("batchWiseClaimDetailsReportList",
	 * claimDetailsReportDTO.getClaimDetailsDataDTOs()); if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * claimDetailsReportDTO.getDataDictNameList()); }
	 * beans.put("claimDetailsReportCustomDataList",
	 * claimDetailsReportDTO.getClaimDetailsCustomDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestPdfFile = null; File
	 * tempDestExcelFile = null; File tempFolder = new File(PAYASIA_TEMP_PATH +
	 * "/claimreport/" + uuid); tempFolder.mkdirs(); String templateFileName; if
	 * (hasLundinTimesheetModule) { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimantLundin.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimantWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportLundin.xlsx");
	 * } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWOCustomLundin.xlsx"
	 * ); }
	 * 
	 * } } else { if (toShowClaimant) { if
	 * (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimant.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWithClaimantWOCustom.xlsx"
	 * ); }
	 * 
	 * } else { if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/BatchWiseClaimDetailsReportWOCustom.xlsx"
	 * ); }
	 * 
	 * } }
	 * 
	 * String destFileNameExcel = PAYASIA_TEMP_PATH +
	 * "/BatchWiseClaimDetailsReport" + uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH +
	 * "/BatchWiseClaimDetailsReport" + uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Batch Wise Claim Details Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genEmpWiseConsClaimReportExcelFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genEmpWiseConsClaimReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseConsolidatedClaimReportDTO
	 * empWiseClaimReportDTO = claimReportsLogic
	 * .showEmployeeWiseConsClaimReport(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>(); if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * empWiseClaimReportDTO.getDataDictNameList()); }
	 * beans.put("empWiseClaimReportList",
	 * empWiseClaimReportDTO.getEmpWiseclaimDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestFile = null; File
	 * tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
	 * tempFolder.mkdirs(); String templateFileName; if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReportWOCustom.xlsx"
	 * ); }
	 * 
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Employee Wise Consolidated Claim Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Employee Wise Consolidated Claim Report"; outputStream
	 * = response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genEmpWiseConsClaimReportPDFFile.html", method
	 * = RequestMethod.POST)
	 * 
	 * @Override public void genEmpWiseConsClaimReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseConsolidatedClaimReportDTO
	 * empWiseClaimReportDTO = claimReportsLogic
	 * .showEmployeeWiseConsClaimReport(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>(); if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * empWiseClaimReportDTO.getDataDictNameList()); }
	 * beans.put("empWiseClaimReportList",
	 * empWiseClaimReportDTO.getEmpWiseclaimDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestPdfFile = null; File
	 * tempDestExcelFile = null; File tempFolder = new File(PAYASIA_TEMP_PATH +
	 * "/claimreport/" + uuid); tempFolder.mkdirs(); String templateFileName; if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReportWOCustom.xlsx"
	 * ); }
	 * 
	 * String destFileNameExcel = PAYASIA_TEMP_PATH +
	 * "/EmployeeWiseConsolidatedClaimReport" + uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH +
	 * "/EmployeeWiseConsolidatedClaimReport" + uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Employee Wise Consolidated Claim Report"; outputStream
	 * = response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showEmpWiseConsClaimReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showEmpWiseConsClaimReport(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseConsolidatedClaimReportDTO
	 * empWiseClaimReportDTO = claimReportsLogic
	 * .showEmployeeWiseConsClaimReport(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds); JSONObject jsonObject = new JSONObject();
	 * jsonObject.put("aaData",
	 * empWiseClaimReportDTO.getEmpWiseclaimDataDTOs());
	 * jsonObject.put("aoColumns", empWiseClaimReportDTO.getClaimHeaderDTOs());
	 * return jsonObject.toString();
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showEmpWiseConsClaimReportII.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showEmpWiseConsClaimReportII(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseConsolidatedClaimReportDTO
	 * empWiseClaimReportDTO = claimReportsLogic
	 * .showEmpWiseConsClaimReportII(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds); JSONObject jsonObject = new JSONObject();
	 * jsonObject.put("aaData",
	 * empWiseClaimReportDTO.getEmpWiseclaimDataDTOs());
	 * jsonObject.put("aoColumns", empWiseClaimReportDTO.getClaimHeaderDTOs());
	 * return jsonObject.toString();
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genEmpWiseConsClaimReportIIExcelFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genEmpWiseConsClaimReportIIExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseConsolidatedClaimReportDTO
	 * empWiseClaimReportDTO = claimReportsLogic
	 * .showEmpWiseConsClaimReportII(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>(); if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * empWiseClaimReportDTO.getDataDictNameList()); }
	 * beans.put("empWiseClaimReportList",
	 * empWiseClaimReportDTO.getEmpWiseclaimDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestFile = null; File
	 * tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
	 * tempFolder.mkdirs(); String templateFileName; if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReportWOCustom.xlsx"
	 * ); }
	 * 
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Employee Wise Consolidated Claim Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Employee Wise Consolidated Claim Report"; outputStream
	 * = response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genEmpWiseConsClaimReportIIPDFFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genEmpWiseConsClaimReportIIPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseConsolidatedClaimReportDTO
	 * empWiseClaimReportDTO = claimReportsLogic
	 * .showEmpWiseConsClaimReportII(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>(); if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * empWiseClaimReportDTO.getDataDictNameList()); }
	 * beans.put("empWiseClaimReportList",
	 * empWiseClaimReportDTO.getEmpWiseclaimDataDTOs()); XLSTransformer
	 * transformer = new XLSTransformer(); File tempDestPdfFile = null; File
	 * tempDestExcelFile = null; File tempFolder = new File(PAYASIA_TEMP_PATH +
	 * "/claimreport/" + uuid); tempFolder.mkdirs(); String templateFileName; if
	 * (empWiseClaimReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReport.xlsx"); }
	 * else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmpWiseConsolidatedClaimReportWOCustom.xlsx"
	 * ); } String destFileNameExcel = PAYASIA_TEMP_PATH +
	 * "/EmployeeWiseConsolidatedClaimReport" + uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH +
	 * "/EmployeeWiseConsolidatedClaimReport" + uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Employee Wise Consolidated Claim Report"; outputStream
	 * = response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showEmpWiseTemplateClaimReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showEmpWiseTemplateClaimReport(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseTemplateClaimReportDTO
	 * empWiseTemplateClaimReportDTO = claimReportsLogic
	 * .showEmpWiseTemplateClaimReport(companyId, claimReportsForm, employeeId);
	 * JSONObject jsonObject = new JSONObject(); jsonObject.put("aaData",
	 * empWiseTemplateClaimReportDTO.getEmpWiseTemplateclaimDataDTOs());
	 * jsonObject.put("aoColumns",
	 * empWiseTemplateClaimReportDTO.getClaimHeaderDTOs()); return
	 * jsonObject.toString();
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genEmpWiseTemplateClaimReportExcelFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genEmpWiseTemplateClaimReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseTemplateClaimReportDTO
	 * empWiseTemplateClaimReportDTO = claimReportsLogic
	 * .showEmpWiseTemplateClaimReport(companyId, claimReportsForm, employeeId);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("empWiseTemplateClaimReportList",
	 * empWiseTemplateClaimReportDTO.getEmpWiseTemplateclaimDataDTOs());
	 * System.out.println(empWiseTemplateClaimReportDTO.
	 * getEmpWiseTemplateclaimDataDTOs()); XLSTransformer transformer = new
	 * XLSTransformer(); File tempDestFile = null; File tempFolder = new
	 * File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid); tempFolder.mkdirs();
	 * String templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmployeeWiseTemplateClaimReport.xlsx");
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Employee Wise Template Claim Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Employee Wise Template Claim Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genEmpWiseTemplateClaimReportPDFFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genEmpWiseTemplateClaimReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); EmployeeWiseTemplateClaimReportDTO
	 * empWiseTemplateClaimReportDTO = claimReportsLogic
	 * .showEmpWiseTemplateClaimReport(companyId, claimReportsForm, employeeId);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("empWiseTemplateClaimReportList",
	 * empWiseTemplateClaimReportDTO.getEmpWiseTemplateclaimDataDTOs());
	 * XLSTransformer transformer = new XLSTransformer(); File tempDestPdfFile =
	 * null; File tempDestExcelFile = null; File tempFolder = new
	 * File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid); tempFolder.mkdirs();
	 * String templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/EmployeeWiseTemplateClaimReport.xlsx");
	 * String destFileNameExcel = PAYASIA_TEMP_PATH +
	 * "/EmployeeWiseTemplateClaimReport" + uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH +
	 * "/EmployeeWiseTemplateClaimReport" + uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Employee Wise Template Claim Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try { if (outputStream
	 * != null) { outputStream.flush(); outputStream.close(); } } catch
	 * (IOException ioException) { LOGGER.error(ioException.getMessage(),
	 * ioException); throw new PayAsiaSystemException(ioException.getMessage(),
	 * ioException); } }
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/showMonthlyConsFinanceReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String showMonthlyConsFinanceReport(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); MonthlyConsolidatedFinanceReportDTO
	 * montlyConsFinReportDTO = claimReportsLogic
	 * .showMonthlyConsFinanceReport(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds); JSONObject jsonObject = new JSONObject();
	 * jsonObject.put("aaData",
	 * montlyConsFinReportDTO.getMontlyConsFinDataDTOs());
	 * jsonObject.put("aoColumns", montlyConsFinReportDTO.getClaimHeaderDTOs());
	 * return jsonObject.toString();
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genMonthlyConsFinanceReportExcelFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genMonthlyConsFinanceReportExcelFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); MonthlyConsolidatedFinanceReportDTO
	 * montlyConsFinReportDTO = claimReportsLogic
	 * .showMonthlyConsFinanceReport(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>(); if
	 * (montlyConsFinReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * montlyConsFinReportDTO.getDataDictNameList()); } if
	 * (montlyConsFinReportDTO.isShowMonthlyConsFinReportGroupingByEmp()) {
	 * beans.put("monthlyConsFinanceReportList",
	 * montlyConsFinReportDTO.getMontlyConsFinDataNewDTOs()); } else {
	 * beans.put("monthlyConsFinanceReportList",
	 * montlyConsFinReportDTO.getMontlyConsFinDataDTOs()); }
	 * 
	 * XLSTransformer transformer = new XLSTransformer(); File tempDestFile =
	 * null; File tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" +
	 * uuid); tempFolder.mkdirs(); String templateFileName; if
	 * (montlyConsFinReportDTO.isShowMonthlyConsFinReportGroupingByEmp()) { if
	 * (montlyConsFinReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReportGroupByEmp.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReportWOCustomGroupByEmp.xlsx"
	 * ); } } else { if (montlyConsFinReportDTO.getDataDictNameList().size() >
	 * 0) { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReport.xlsx");
	 * } else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReportWOCustom.xlsx"
	 * ); } }
	 * 
	 * String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid +
	 * "/Monthly Consolidated Finance Report" + uuid + ".xlsx";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans, destFileName); }
	 * catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileName); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Monthly Consolidated Finance Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".xlsx"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestFile = new File(destFileName); if (tempDestFile !=
	 * null) { tempDestFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/genMonthlyConsFinanceReportPDFFile.html",
	 * method = RequestMethod.POST)
	 * 
	 * @Override public void genMonthlyConsFinanceReportPDFFile(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm,
	 * 
	 * @RequestParam(value = "dataDictionaryIds", required = false) String[]
	 * dataDictionaryIds, HttpServletRequest request, HttpServletResponse
	 * response) { UUID uuid = UUID.randomUUID();
	 * 
	 * Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID); MonthlyConsolidatedFinanceReportDTO
	 * montlyConsFinReportDTO = claimReportsLogic
	 * .showMonthlyConsFinanceReport(companyId, claimReportsForm, employeeId,
	 * dataDictionaryIds);
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>(); if
	 * (montlyConsFinReportDTO.getDataDictNameList().size() > 0) {
	 * beans.put("custFieldColNameList",
	 * montlyConsFinReportDTO.getDataDictNameList()); } if
	 * (montlyConsFinReportDTO.isShowMonthlyConsFinReportGroupingByEmp()) {
	 * beans.put("monthlyConsFinanceReportList",
	 * montlyConsFinReportDTO.getMontlyConsFinDataNewDTOs()); } else {
	 * beans.put("monthlyConsFinanceReportList",
	 * montlyConsFinReportDTO.getMontlyConsFinDataDTOs()); }
	 * 
	 * XLSTransformer transformer = new XLSTransformer(); File tempDestPdfFile =
	 * null; File tempDestExcelFile = null; File tempFolder = new
	 * File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid); tempFolder.mkdirs();
	 * String templateFileName; if
	 * (montlyConsFinReportDTO.isShowMonthlyConsFinReportGroupingByEmp()) { if
	 * (montlyConsFinReportDTO.getDataDictNameList().size() > 0) {
	 * templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReportGroupByEmp.xlsx"
	 * ); } else { templateFileName = servletContext.getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReportWOCustomGroupByEmp.xlsx"
	 * ); } } else { if (montlyConsFinReportDTO.getDataDictNameList().size() >
	 * 0) { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReport.xlsx");
	 * } else { templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/MonthlyConsolidatedFinanceReportWOCustom.xlsx"
	 * ); } }
	 * 
	 * String destFileNameExcel = PAYASIA_TEMP_PATH +
	 * "/MonthlyConsolidatedFinanceReport" + uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH +
	 * "/MonthlyConsolidatedFinanceReport" + uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Monthly Consolidated Finance Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data);
	 * 
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try {
	 * 
	 * if (outputStream != null) { outputStream.flush(); outputStream.close(); }
	 * } catch (IOException ioException) {
	 * LOGGER.error(ioException.getMessage(), ioException); throw new
	 * PayAsiaSystemException(ioException.getMessage(), ioException); } }
	 * 
	 * }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/searchEmployee", method = RequestMethod.POST)
	 * 
	 * @ResponseBody public String searchEmployee(@RequestParam(value = "sidx",
	 * required = false) String columnName,
	 * 
	 * @RequestParam(value = "sord", required = false) String sortingType,
	 * 
	 * @RequestParam(value = "page", required = false) int page,
	 * 
	 * @RequestParam(value = "rows", required = false) int rows,
	 * 
	 * @RequestParam(value = "searchCondition", required = true) String
	 * searchCondition,
	 * 
	 * @RequestParam(value = "searchText", required = true) String searchText,
	 * 
	 * @RequestParam(value = "isShortList", required = true) boolean
	 * isShortList,
	 * 
	 * @RequestParam(value = "metaData", required = false) String metaData,
	 * HttpServletRequest request, HttpServletResponse response) { Long
	 * companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); PageRequest pageDTO = new PageRequest();
	 * pageDTO.setPageNumber(page); pageDTO.setPageSize(rows);
	 * 
	 * Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * SortCondition sortDTO = new SortCondition();
	 * sortDTO.setColumnName(columnName); sortDTO.setOrderType(sortingType);
	 * 
	 * ClaimReportsResponse claimReportResponse =
	 * claimReportsLogic.searchEmployee(pageDTO, sortDTO, searchCondition,
	 * searchText, companyId, employeeId, isShortList, metaData);
	 * 
	 * JsonConfig jsonConfig = new JsonConfig(); JSONObject jsonObject =
	 * JSONObject.fromObject(claimReportResponse, jsonConfig); try { return
	 * URLEncoder.encode(jsonObject.toString(), "UTF-8"); } catch
	 * (UnsupportedEncodingException ex) { LOGGER.error(ex.getMessage(), ex); }
	 * return null; }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = "/getAdvanceFilterComboHashmap.html", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * getAdvanceFilterComboHashmap(HttpServletRequest request,
	 * HttpServletResponse response) { Long companyId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Map<String, String> advanceFilterCombosHashMap =
	 * generalLogic.getEmployeeFilterComboList(companyId); JsonConfig jsonConfig
	 * = new JsonConfig(); JSONObject jsonObject =
	 * JSONObject.fromObject(advanceFilterCombosHashMap, jsonConfig); return
	 * jsonObject.toString(); }
	 * 
	 * @RequestMapping(value = "/genClaimHeadcountExcelReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @Override public void genClaimHeadcountExcelReport(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { UUID uuid = UUID.randomUUID(); Long companyId
	 * = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * Boolean isManager = false; ClaimDetailsReportDTO claimDataDTO =
	 * claimReportsLogic.genClaimHeadcountReport(employeeId, companyId,
	 * claimReportsForm, isManager); String header = "Claim Headcount Report";
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("header", header); beans.put("claimHeadCountHeaderDTOMap",
	 * claimDataDTO.getClaimHeadCountHeaderDTOMap());
	 * beans.put("claimHeadCountEmpDataListMap",
	 * claimDataDTO.getClaimHeadCountEmpDataListMap());
	 * beans.put("companyCodeList", claimDataDTO.getCompanyCodeList());
	 * beans.put("currentDate",
	 * DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime
	 * ()));
	 * 
	 * XLSTransformer transformer = new XLSTransformer(); File tempFolder = new
	 * File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid); tempFolder.mkdirs();
	 * String templateFileName = servletContext .getRealPath(
	 * "/resources/ClaimReportTemplate/ClaimHeadcountReportMultipleSheets.xlsx")
	 * ;
	 * 
	 * InputStream is = null; Workbook resultWorkbook = null; List<String>
	 * sheetNames = new ArrayList<>();
	 * 
	 * try { is = new BufferedInputStream(new
	 * FileInputStream(templateFileName)); for (String companyCode :
	 * claimDataDTO.getCompanyCodeList()) {
	 * sheetNames.add(ExcelUtils.getSheetSafeName(companyCode)); }
	 * 
	 * resultWorkbook = transformer.transformMultipleSheetsList(is,
	 * claimDataDTO.getCompanyCodeList(), sheetNames, "sheetName", beans, 0);
	 * 
	 * String fileName = "Claim Headcount Report.xlsx"; ServletOutputStream
	 * outputStream = null; outputStream = response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * String user_agent = request.getHeader("user-agent"); boolean
	 * isInternetExplorer = (user_agent.indexOf("MSIE") > -1);
	 * 
	 * if (isInternetExplorer) { response.setHeader("Content-disposition",
	 * "attachment; filename=\"" + URLEncoder.encode(fileName,
	 * "utf-8").replaceAll("\\+", " ") + "\"");
	 * 
	 * } else { response.setHeader("Content-disposition",
	 * "attachment; filename=\"" + MimeUtility.encodeWord(fileName, "utf-8",
	 * "Q") + "\"");
	 * 
	 * }
	 * 
	 * resultWorkbook.write(outputStream); if (outputStream != null) {
	 * outputStream.flush(); outputStream.close(); }
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } tempFolder.delete(); }
	 * 
	 * @RequestMapping(value = "/genClaimHeadcountPdfReport.html", method =
	 * RequestMethod.POST)
	 * 
	 * @Override public void genClaimHeadcountPdfReport(
	 * 
	 * @ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm
	 * claimReportsForm, HttpServletRequest request, HttpServletResponse
	 * response, Locale locale) { UUID uuid = UUID.randomUUID(); Long companyId
	 * = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * ADMIN_COMPANY_ID); Long employeeId = (Long)
	 * request.getSession().getAttribute(PayAsiaSessionAttributes.
	 * EMPLOYEE_EMPLOYEE_ID);
	 * 
	 * Boolean isManager = false; ClaimDetailsReportDTO claimDataDTO =
	 * claimReportsLogic.genClaimHeadcountReport(employeeId, companyId,
	 * claimReportsForm, isManager); String header = "Claim Headcount Report";
	 * 
	 * Map<String,Object> beans = new HashMap<String,Object>();
	 * beans.put("header", header); beans.put("claimHeadCountHeaderDTOMap",
	 * claimDataDTO.getClaimHeadCountHeaderDTOMap());
	 * beans.put("claimHeadCountEmpDataListMap",
	 * claimDataDTO.getClaimHeadCountEmpDataListMap());
	 * beans.put("companyCodeList", claimDataDTO.getCompanyCodeList());
	 * beans.put("currentDate",
	 * DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime
	 * ()));
	 * 
	 * XLSTransformer transformer = new XLSTransformer(); File tempDestPdfFile =
	 * null; File tempDestExcelFile = null; File tempFolder = new
	 * File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid); tempFolder.mkdirs();
	 * String templateFileName = servletContext
	 * .getRealPath("/resources/ClaimReportTemplate/ClaimHeadcountReportMultipleSheets For PDF.xlsx"
	 * ); String destFileNameExcel = PAYASIA_TEMP_PATH + "/ClaimHeadCountReport"
	 * + uuid + ".xlsx";
	 * 
	 * String destFileNamePDF = PAYASIA_TEMP_PATH + "/ClaimHeadCountReport" +
	 * uuid + ".pdf";
	 * 
	 * try { transformer.transformXLS(templateFileName, beans,
	 * destFileNameExcel);
	 * 
	 * ArrayList<String> cmdList = new ArrayList<String>();
	 * 
	 * String file1 = destFileNameExcel; String file2 = destFileNamePDF;
	 * 
	 * // cmdList.add(PYTHON_EXE_PATH); cmdList.add(DOCUMENT_CONVERTOR_PATH);
	 * cmdList.add(file1); cmdList.add(file2);
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
	 * 
	 * LOGGER.info("processBuilder.command()   " + processBuilder.command());
	 * processBuilder.redirectErrorStream(true); Process process =
	 * processBuilder.start(); process.waitFor();
	 * 
	 * } catch (ParsePropertyException e) { LOGGER.error(e); } catch
	 * (InvalidFormatException e) { LOGGER.error(e); } catch (IOException e) {
	 * LOGGER.error(e); } catch (Exception e) { LOGGER.error(e.getMessage(), e);
	 * } ServletOutputStream outputStream = null; try {
	 * 
	 * Path path = Paths.get(destFileNamePDF); byte[] data =
	 * Files.readAllBytes(path);
	 * 
	 * String fileName = "Claim HeadCount Report"; outputStream =
	 * response.getOutputStream();
	 * response.setContentType("application/octet-stream");
	 * response.setHeader("Cache-Control",
	 * "must-revalidate, post-check=0, pre-check=0");
	 * 
	 * fileName = fileName + ".pdf"; response.setHeader("Content-Disposition",
	 * "attachment; filename=\"" + fileName + "");
	 * 
	 * outputStream = response.getOutputStream(); outputStream.write(data); }
	 * catch (IOException ioException) { LOGGER.error(ioException.getMessage(),
	 * ioException); throw new PayAsiaSystemException(ioException.getMessage(),
	 * ioException);
	 * 
	 * } finally { tempDestPdfFile = new File(destFileNamePDF); if
	 * (tempDestPdfFile != null) { tempDestPdfFile.delete(); } tempDestExcelFile
	 * = new File(destFileNameExcel); if (tempDestExcelFile != null) {
	 * tempDestExcelFile.delete(); } tempFolder.delete(); try { if (outputStream
	 * != null) { outputStream.flush(); outputStream.close(); } } catch
	 * (IOException ioException) { LOGGER.error(ioException.getMessage(),
	 * ioException); throw new PayAsiaSystemException(ioException.getMessage(),
	 * ioException); } } }
	 * 
	 * @Override
	 * 
	 * @RequestMapping(value = PayAsiaURLConstant.GET_COMPANY_GROUP_EMPLOYEE_ID,
	 * method = RequestMethod.POST)
	 * 
	 * @ResponseBody public String getCompanyGroupEmployeeId(
	 * 
	 * @RequestParam(value = "searchString", required = true) String
	 * searchString) {
	 * 
	 * Long companyId = Long.parseLong(UserContext.getWorkingCompanyId()); Long
	 * employeeId = Long.parseLong(UserContext.getUserId());
	 * 
	 * List<EmployeeListForm> employeeListFormList =
	 * claimReportsLogic.getCompanyGroupEmployeeId(companyId, searchString,
	 * employeeId);
	 * 
	 * return ResponseDataConverter.getListToJson(employeeListFormList);
	 * 
	 * }
	 */
}
