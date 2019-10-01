package com.payasia.web.controller.claim.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.AssignClaimTemplateFormRes;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AssignClaimSchemeResponse;
import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.AssignClaimTemplateLogic;
import com.payasia.web.controller.AssignClaimTemplateController;

@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_ASSIGN_CLAIM_TEMPLATE)
public class AssignClaimTemplateControllerImpl implements AssignClaimTemplateController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(AssignClaimTemplateControllerImpl.class);

	@Resource
	MessageSource messageSource;

	@Resource
	AssignClaimTemplateLogic assignClaimTemplateLogic;
	
	@Autowired
	private ServletContext servletContext;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.CLAIM_TEMPLATE_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String claimTemplateList() {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<AssignClaimTemplateForm> claimTemplateList = assignClaimTemplateLogic.getClaimTemplateList(companyId);

		return ResponseDataConverter.getListToJson(claimTemplateList);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ASSIGN_CLM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String assignClaimTemplate(
			@ModelAttribute(value = "AssignClaimTemplateForm") AssignClaimTemplateForm assignClaimTemplateForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return assignClaimTemplateLogic.assignClaimTemplate(companyId, assignClaimTemplateForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SEARCH_ASSIGN_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String searchAssignClaimTemplate(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setFromDate(fromDate);
		claimDTO.setToDate(toDate);

		AssignClaimTemplateFormRes assClaimTemplateFormres = assignClaimTemplateLogic
				.searchAssignClaimTemplate(claimDTO, pageDTO, sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(assClaimTemplateFormres);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_EMPLOYEE_CLAIM_TEMPLATE_DATA, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeClaimTemplateData(
			@RequestParam(value = "employeeClaimTempateId", required = true) Long employeeClaimTemplateId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setAdmin(true);
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		addClaimDTO.setCompanyId(companyId);

		AssignClaimTemplateForm assClaimTemplateForm = assignClaimTemplateLogic
				.getEmployeeClaimTemplateData(addClaimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(assClaimTemplateForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_ASSIGN_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String deleteAssignClaimTemplate(
			@RequestParam(value = "employeeClaimTemplateId", required = true) Long employeeClaimTemplateId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setAdmin(true);
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		addClaimDTO.setCompanyId(companyId);

		return assignClaimTemplateLogic.deleteEmployeeClaimTemplateId(addClaimDTO);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.UPDATE_ASSIGN_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String updateAssignClaimTemplate(
			@ModelAttribute(value = "AssignClaimTemplateForm") AssignClaimTemplateForm assignClaimTemplateForm,
			Locale locale) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		String status = assignClaimTemplateLogic.updateAssignClaimTemplate(companyId, assignClaimTemplateForm);

		return ResponseDataConverter.getURLEncoded(messageSource.getMessage(status, new Object[] {}, locale));
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.IMPORT_ASSIGN_CLAIM_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String importAssignClaimTemplate(
			@ModelAttribute(value = "AssignClaimTemplateForm") AssignClaimTemplateForm assignClaimTemplateForm,
			Locale locale) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AssignClaimTemplateForm assignClaimTemplatefrm = new AssignClaimTemplateForm();
		boolean isFileValid = false;
		if (assignClaimTemplateForm.getFileUpload() != null) {
			isFileValid = FileUtils.isValidFile(assignClaimTemplateForm.getFileUpload(),
					assignClaimTemplateForm.getFileUpload().getOriginalFilename(),
					PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		}

		if (isFileValid) {

			assignClaimTemplatefrm = assignClaimTemplateLogic.importAssignClaimTemplate(assignClaimTemplateForm,
					companyId);

			if (assignClaimTemplatefrm.getDataImportLogDTOs() != null) {
				for (DataImportLogDTO dataImportLogDTO : assignClaimTemplatefrm.getDataImportLogDTOs()) {
					try {
						dataImportLogDTO.setRemarks(URLEncoder.encode(
								messageSource.getMessage(dataImportLogDTO.getRemarks(), new Object[] {}, locale),
								"UTF-8"));
					} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
						LOGGER.error(exception.getMessage(), exception);
						throw new PayAsiaSystemException(exception.getMessage(), exception);
					}
				}
			}

		}

		return ResponseDataConverter.getObjectToJson(assignClaimTemplatefrm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_IMPORT_ASSIGN_CLAIM_TEMPLATE_FILE, method = RequestMethod.GET)
	public @ResponseBody void getImportAssignCLaimTemplateFile(
			@RequestParam(value = "fileName", required = true) String fileName, HttpServletRequest request,
			HttpServletResponse response) {
		ServletOutputStream outStream = null;
		try {

			InputStream pdfIn = null;
			String templateFileName = servletContext.getRealPath(fileName);
			File pdfFile = new File(templateFileName);
			try {
				pdfIn = new FileInputStream(pdfFile);
			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
				throw new PayAsiaSystemException(ex.getMessage(), ex);
			}

			byte[] byteFile = IOUtils.toByteArray(pdfIn);

			response.reset();
			response.setContentType("application/octet-stream");
			response.setContentLength(byteFile.length);
			String excelName = "ImportAssignClaimTemplate.xlsx";
			response.setHeader("Content-Disposition", "attachment;filename=" + excelName);
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
				throw new PayAsiaSystemException(exception.getMessage(), exception);
			}
		}
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SEARCH_EMPLOYEE, method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		AssignClaimSchemeResponse claimSchemeResponse = assignClaimTemplateLogic.searchEmployee(pageDTO, sortDTO,
				empName, empNumber, companyId, employeeId);

		return ResponseDataConverter.getObjectToJson(claimSchemeResponse);
	}

	
}
