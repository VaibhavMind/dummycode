package com.payasia.web.controller.impl;

/**
 * @author vivekjain
 * 
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HRLetterResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.HRLetterLogic;
import com.payasia.web.controller.HRLetterController;
import com.payasia.web.security.XSSRequestWrapper;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;

/**
 * The Class HRLetterControllerImpl.
 */

@Controller
@RequestMapping(value = { "/admin/hrLetters", "/employee/hrLetters" })
public class HRLetterControllerImpl implements HRLetterController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HRLetterControllerImpl.class);

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/** The hr letter logic. */
	@Resource
	HRLetterLogic hrLetterLogic;
	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HRLetterController#viewHRLetterList(java.lang
	 * .String, java.lang.String, int, int,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/viewHRLetterList.html", method = RequestMethod.POST)
	@ResponseBody public String viewHRLetterList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HRLetterResponse hRLetterResponse = hrLetterLogic.getHRLetterList(
				companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hRLetterResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HRLetterController#searchHRLetter(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String, int, int,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/searchHRLetter.html", method = RequestMethod.POST)
	@ResponseBody public String searchHRLetter(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HRLetterResponse hrLetterResponse = hrLetterLogic.searchHRLetter(
				companyId, searchCondition, searchText, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrLetterResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HRLetterController#saveHRLetter(com.payasia
	 * .common.form.HRLetterForm, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/addHRLetter.html", method = RequestMethod.POST)
	@ResponseBody public String saveHRLetter(
			@ModelAttribute(value = "hrLetterForm") HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		try {
		Boolean isValidDesc = false,isValidName= false,isValidSubject= false;
		isValidDesc=XSSRequestWrapper.isValidString(hrLetterForm.getLetterDescription());
		isValidName=XSSRequestWrapper.isValidString(hrLetterForm.getLetterName());
		isValidSubject=XSSRequestWrapper.isValidString(hrLetterForm.getSubject());
	
		if(!isValidDesc || !isValidName || !isValidSubject || !isValidString(URLDecoder.decode(hrLetterForm.getLetterDescription(),"UTF-8")) || !isValidString(URLDecoder.decode(hrLetterForm.getLetterName(),"UTF-8")) || !isValidString(URLDecoder.decode(hrLetterForm.getSubject(),"UTF-8"))) {
            throw new PayAsiaSystemException("payasia.record.invalid.data");
		}
		String hrLetterstatus = hrLetterLogic.saveHRLetter(hrLetterForm,
				companyId);
		return hrLetterstatus;
	}catch (Exception exception) {
		LOGGER.error(exception.getMessage(), exception);
		String ex=exception.getMessage();
		return ex;
	}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.HRLetterController#deleteHRLetter(long)
	 */
	@Override
	@RequestMapping(value = "/deleteHRLetter.html", method = RequestMethod.POST)
	@ResponseBody public String deleteHRLetter(
			@RequestParam(value = "letterId", required = true) long letterId) {

		/*ID DECRYPT*/
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = hrLetterLogic.deleteHRLetter(letterId,companyId);
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HRLetterController#getHRLetterDetails(long)
	 */
	@Override
	@RequestMapping(value = "/getHRLetterDetails.html", method = RequestMethod.POST)
	@ResponseBody public String getHRLetterDetails(
			@RequestParam(value = "letterId", required = true) long letterId) {
		
		/*ID DECRYPT*/
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		
        Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		HRLetterForm hrLetterForm = hrLetterLogic.getHRLetter(letterId,companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrLetterForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HRLetterController#updateHRLetter(com.payasia
	 * .common.form.HRLetterForm, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/editHRLetter.html", method = RequestMethod.POST)
	@ResponseBody public String updateHRLetter(
			@ModelAttribute(value = "hrLetterForm") HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/
		hrLetterForm.setLetterId(FormatPreserveCryptoUtil.decrypt(hrLetterForm.getLetterId()));
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		try {
			Boolean isValidDesc = false,isValidName= false,isValidSubject= false;
			isValidDesc=XSSRequestWrapper.isValidString(hrLetterForm.getLetterDescription());
			isValidName=XSSRequestWrapper.isValidString(hrLetterForm.getLetterName());
			isValidSubject=XSSRequestWrapper.isValidString(hrLetterForm.getSubject());
		
			if(!isValidDesc || !isValidName || !isValidSubject || !isValidString(URLDecoder.decode(hrLetterForm.getLetterDescription(),"UTF-8")) || !isValidString(URLDecoder.decode(hrLetterForm.getLetterName(),"UTF-8")) || !isValidString(URLDecoder.decode(hrLetterForm.getSubject(),"UTF-8"))) {
	            throw new PayAsiaSystemException("payasia.record.invalid.data");
			}
		  
		String letterStatus = hrLetterLogic.updateHRLetter(hrLetterForm,
				companyId);

		return letterStatus;
		}
		catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
			String ex=exception.getMessage();
			return ex;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.HRLetterController#viewHRLetter(long)
	 */
	@Override
	@RequestMapping(value = "/viewHRLetter.html", method = RequestMethod.POST)
	@ResponseBody public String viewHRLetter(long letterId) {
		
		 Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		HRLetterForm hrLetterForm = hrLetterLogic.getHRLetter(letterId,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrLetterForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.HRLetterController#sendHRLetter(long,
	 * java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/sendHRLetter.html", method = RequestMethod.POST)
	@ResponseBody public String sendHRLetter(
			@RequestParam(value = "hrLetterId", required = true) long hrLetterId,
			@ModelAttribute(value = "hrLetterForm") HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {

		String hrLetterStatus = "";

		try {
			hrLetterStatus = URLEncoder.encode(messageSource.getMessage(
					hrLetterStatus, new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return hrLetterStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
	 * getEditEmployeeFilterList(java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/editEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody public String getEditEmployeeFilterList(
			@RequestParam(value = "hrLetterId", required = true) Long hrLetterId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EmployeeFilterListForm> filterList = hrLetterLogic.getEditEmployeeFilterList(hrLetterId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.CompanyDocumentCenterController#deleteFilter
	 * (java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "c", method = RequestMethod.POST)
	public void deleteFilter(
			@RequestParam(value = "filterId", required = true) Long filterId) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		hrLetterLogic.deleteFilter(filterId,companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.CompanyDocumentCenterController#
	 * saveEmployeeFilterList(java.lang.String, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/saveEmployeeFilterList.html", method = RequestMethod.POST)
	@ResponseBody public String saveEmployeeFilterList(
			@RequestParam(value = "metaData", required = true) String metaData,
			@RequestParam(value = "hrLetterId", required = true) Long hrLetterId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {

		String filterStatus = null;
		try {
			String decodedMetaData = URLDecoder.decode(metaData, "UTF-8");
			filterStatus = hrLetterLogic.saveEmployeeFilterList(decodedMetaData,hrLetterId);			
			filterStatus = URLEncoder.encode(messageSource.getMessage(
					filterStatus, new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}
		return filterStatus;

	}

	@Override
	@RequestMapping(value = "/custPlaceHolderList.html", method = RequestMethod.POST)
	@ResponseBody public String custPlaceHolderList(
			@RequestParam(value = "hrLetterId", required = true) long hrLetterId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/* ID DECRYPT*/
		hrLetterId = FormatPreserveCryptoUtil.decrypt(hrLetterId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Set<String> custFieldList = hrLetterLogic.custPlaceHolderList(
				companyId, hrLetterId, loggedInEmployeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(custFieldList, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/previewHRLetterBodyText.html", method = RequestMethod.POST)
	@ResponseBody public String previewHRLetterBodyText(
			@RequestParam(value = "hrLetterId", required = true) long hrLetterId,
			@RequestParam(value = "employeeId", required = true) long employeeId,
			@RequestParam(value = "userInputKeyArr", required = true) String[] userInputKeyArr,
			@RequestParam(value = "userInputValArr", required = true) String[] userInputValArr,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
/*ID DECRYPT*/
		hrLetterId=FormatPreserveCryptoUtil.decrypt(hrLetterId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean isAdmin = true;
		String convertedPreviewMessage = hrLetterLogic.previewHRLetterBodyText(
				companyId, hrLetterId, employeeId, loggedInEmployeeId,
				userInputKeyArr, userInputValArr, isAdmin);

		try {
			return URLEncoder.encode(convertedPreviewMessage, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/previewEmployeeHRLetterBodyText.html", method = RequestMethod.POST)
	@ResponseBody public String previewHRLetterBodyText(
			@RequestParam(value = "hrLetterId", required = true) long hrLetterId,
			@RequestParam(value = "userInputKeyArr", required = true) String[] userInputKeyArr,
			@RequestParam(value = "userInputValArr", required = true) String[] userInputValArr,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/* ID DECRYPT*/
		hrLetterId = FormatPreserveCryptoUtil.decrypt(hrLetterId);
	
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean isAdmin = false;
		String convertedPreviewMessage = hrLetterLogic.previewHRLetterBodyText(
				companyId, hrLetterId, loggedInEmployeeId, loggedInEmployeeId,
				userInputKeyArr, userInputValArr, isAdmin);

		try {
			return URLEncoder.encode(convertedPreviewMessage, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/viewHrLetterInPDF.html", method = RequestMethod.POST)
	public @ResponseBody void viewHrLetterInPDF(
			@ModelAttribute(value = "hrLetterForm") HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		String hrLetterBodyText = hrLetterForm.getEncodedBodyString();
		hrLetterBodyText = new String(Base64.decodeBase64(hrLetterBodyText
				.getBytes()));
		try {
			hrLetterBodyText = URLDecoder.decode(hrLetterBodyText, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error(e2.getMessage(), e2);
		}

		String hrLetterHTMLBody = "<html><head><meta charset='UTF-8' /></head><body>";
		hrLetterHTMLBody += hrLetterBodyText;
		hrLetterHTMLBody += "</body></html>";

		File tempPdfFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrLetter/" + uuid);
		tempFolder.mkdirs();

		String destFileNameHTML = PAYASIA_TEMP_PATH + "/HRLetter" + uuid
				+ ".html";
		File htmlFile = new File(destFileNameHTML);
		FileWriterWithEncoding fw;
		try {
			fw = new FileWriterWithEncoding(htmlFile.getAbsoluteFile(), "UTF-8");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(hrLetterHTMLBody);
			bw.close();
		} catch (IOException e1) {
			LOGGER.error(e1);
		}

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/HRLetter" + uuid
				+ ".pdf";

		try {
			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameHTML;
			String file2 = destFileNamePDF;

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			process.waitFor();

		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		ServletOutputStream outputStream = null;
		try {

			Path path = Paths.get(destFileNamePDF);
			byte[] data = Files.readAllBytes(path);

			String fileName = "HRLetter Email PDF";
			response.reset();
			outputStream = response.getOutputStream();

			response.setContentType("application/pdf");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			fileName = fileName + ".pdf";

			response.setHeader("Content-disposition", "inline; filename=\""
					+ MimeUtility.encodeWord(fileName, "UTF-8", "Q") + "\"");

			outputStream = response.getOutputStream();
			outputStream.write(data);

		} catch (IOException ioException) {
			tempPdfFile = new File(destFileNamePDF);
			if (tempPdfFile != null) {
				tempPdfFile.delete();
			}
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		} finally {
			try {
				tempPdfFile = new File(destFileNamePDF);
				if (tempPdfFile != null) {
					tempPdfFile.delete();
				}
				htmlFile.delete();
				tempFolder.delete();
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage(), ioException);
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}

	@Override
	@RequestMapping(value = "/viewEmployeeHrLetterInPDF.html", method = RequestMethod.POST)
	public @ResponseBody void viewEmployeeHrLetterInPDF(
			@ModelAttribute(value = "hrLetterForm") HRLetterForm hrLetterForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long loggedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String hrLetterBodyText = hrLetterForm.getEncodedBodyString();
		hrLetterBodyText = new String(Base64.decodeBase64(hrLetterBodyText
				.getBytes()));
		try {
			hrLetterBodyText = URLDecoder.decode(hrLetterBodyText, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error(e2.getMessage(), e2);
		}

		String hrLetterHTMLBody = "<html><head><meta charset='UTF-8' /></head><body>";
		hrLetterHTMLBody += hrLetterBodyText;
		hrLetterHTMLBody += "</body></html>";

		File tempPdfFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrLetter/"
				+ loggedInEmployeeId + uuid);
		tempFolder.mkdirs();

		String destFileNameHTML = PAYASIA_TEMP_PATH + "/HRLetter"
				+ loggedInEmployeeId + uuid + ".html";
		File htmlFile = new File(destFileNameHTML);
		FileWriterWithEncoding fw;
		try {
			fw = new FileWriterWithEncoding(htmlFile.getAbsoluteFile(), "UTF-8");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(hrLetterHTMLBody);
			bw.close();
		} catch (IOException e1) {
			LOGGER.error(e1);
		}

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/HRLetter"
				+ loggedInEmployeeId + uuid + ".pdf";

		try {
			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameHTML;
			String file2 = destFileNamePDF;

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			process.waitFor();

		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		ServletOutputStream outputStream = null;
		try {

			Path path = Paths.get(destFileNamePDF);
			byte[] data = Files.readAllBytes(path);

			String fileName = "HRLetter Email PDF";
			response.reset();
			outputStream = response.getOutputStream();

			response.setContentType("application/pdf");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");
			fileName = fileName + ".pdf";

			response.setHeader("Content-disposition", "inline; filename=\""
					+ MimeUtility.encodeWord(fileName, "UTF-8", "Q") + "\"");

			outputStream = response.getOutputStream();
			outputStream.write(data);

		} catch (IOException ioException) {
			tempPdfFile = new File(destFileNamePDF);
			if (tempPdfFile != null) {
				tempPdfFile.delete();
			}
			htmlFile.delete();
			tempFolder.delete();
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
		} finally {
			try {
				tempPdfFile = new File(destFileNamePDF);
				if (tempPdfFile != null) {
					tempPdfFile.delete();
				}
				htmlFile.delete();
				tempFolder.delete();
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage(), ioException);
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}

	@Override
	@RequestMapping(value = "/sendHrLetterWithPDF.html", method = RequestMethod.POST)
	@ResponseBody public String sendHrLetterWithPDF(
			@RequestParam(value = "hrLetterId", required = true) long hrLetterId,
			@RequestParam(value = "employeeId", required = true) long employeeId,
			@RequestParam(value = "hrLetterBodyText", required = true) String hrLetterBodyText,
			@RequestParam(value = "ccEmails", required = true) String ccEmails,
			@RequestParam(value = "saveInDocCenterCheck", required = true) boolean saveInDocCenterCheck,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		
		/* ID DECRYPT*/
		hrLetterId = FormatPreserveCryptoUtil.decrypt(hrLetterId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String status = hrLetterLogic.sendHrLetterWithPDF(companyId,
				hrLetterId, employeeId, loggedInEmployeeId, hrLetterBodyText,
				ccEmails, saveInDocCenterCheck);
		try {

			status = URLEncoder.encode(
					messageSource.getMessage(status, new Object[] {}, locale),
					"UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return status;
	}

	@Override
	@RequestMapping(value = "/searchEmployeeHRLetter.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployeeHRLetter(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long loggedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		HRLetterResponse hrLetterResponse = hrLetterLogic
				.searchEmployeeHRLetter(companyId, searchCondition, searchText,
						pageDTO, sortDTO, loggedInEmployeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrLetterResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/isSaveHrLetterInDocumentCenter.html", method = RequestMethod.POST)
	@ResponseBody public String isSaveHrLetterInDocumentCenter(
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String isSaveHrLetterInDocumentCenter = hrLetterLogic
				.isSaveHrLetterInDocumentCenter(companyId);
		return isSaveHrLetterInDocumentCenter;

	}
	
	private boolean isValidString(String str){
		
		if(str !=null && str.contains("<") ||  str.contains("%3C") || str.contains(">") || str.contains("%3E")){
		return  false;	
		}
		return true;
		
		
	}
}
