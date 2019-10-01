package com.payasia.web.controller.claim.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeClaimAdjustmentReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeClaimAdjustmentForm;
import com.payasia.common.form.EmployeeEntitlements;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.EmployeeEntitlementsLogic;
import com.payasia.web.controller.claim.EmployeeEntitlementsController;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_EMPLOYEE_ENTITLEMENT)
public class EmployeeEntitlementsControllerImpl implements EmployeeEntitlementsController {
	
	private static final Logger LOGGER = Logger.getLogger(EmployeeEntitlementsControllerImpl.class);
	
	@Resource
	EmployeeEntitlementsLogic employeeEntitlementsLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.python.exe.path']}")
	private String PYTHON_EXE_PATH;

	@Value("#{payasiaptProperties['payasia.python.DocumentConverter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	@Autowired
	private ServletContext servletContext;
	
	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_EMPLOYEE_ENTITLEMENTS, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeEntitlements(
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "claimTemplateId", required = false) Long claimTemplateId,
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "columnName", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,Locale locale) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(1);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		claimTemplateId=claimTemplateId==-1?-1:FormatPreserveCryptoUtil.decrypt(claimTemplateId);
		
		EmployeeEntitlements employeeEntitlements = employeeEntitlementsLogic.getEmployeeEntitlements(employeeId, pageDTO,
				sortDTO, companyId, employeeNumber, claimTemplateId, year, locale);
		employeeEntitlements.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(employeeEntitlements);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_CLAIM_ADJUSTMENT, method = RequestMethod.POST)
	@ResponseBody
	public String saveClaimAdjustment(
			@ModelAttribute("employeeClaimAdjustmentForm") EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		return employeeEntitlementsLogic.saveClaimAdjustment(employeeClaimAdjustmentForm, companyId);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.UPDATE_CLAIM_ADJUSTMENT, method = RequestMethod.POST)
	@ResponseBody
	public String updateClaimAdjustment(
			@ModelAttribute("employeeClaimAdjustmentForm") EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		return employeeEntitlementsLogic.updateClaimAdjustment(employeeClaimAdjustmentForm, companyId);
		
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_ADJUSTMENT, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimAdjustment(@ModelAttribute("employeeClaimAdjustmentId") Long employeeClaimAdjustmentId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		return employeeEntitlementsLogic.deleteClaimAdjustment(employeeClaimAdjustmentId,companyId);
	}

	@RequestMapping(value = PayAsiaURLConstant.EXPORT_ADJUSTMENTS_EXCEL_FILE, method = RequestMethod.POST)
	@Override
	public void exportAdjustmentsExcelFile(
			@ModelAttribute(value = "employeeClaimAdjustmentForm") EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		EmployeeClaimAdjustmentReportDTO empClaimAdjustmentDataDTO = employeeEntitlementsLogic
				.exportAdjustmentsExcelFile(companyId, employeeClaimAdjustmentForm, employeeId);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("empClaimAdjustmentDataDTOs", empClaimAdjustmentDataDTO.getEmployeeClaimAdjustmentDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/ClaimReportTemplate/EmployeeClaimAdjustments.xlsx");
		String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid + "/Employee Claim Adjustments Report" + uuid
				+ ".xlsx";

		try {
			transformer.transformXLS(templateFileName, beans, destFileName);
		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (InvalidFormatException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		ServletOutputStream outputStream = null;
		try {

			Path path = Paths.get(destFileName);
			byte[] data = Files.readAllBytes(path);

			String fileName = "Employee Claim Adjustments Report";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);

		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(), ioException);
		} finally {
			tempDestFile = new File(destFileName);
			if (tempDestFile != null) {
				tempDestFile.delete();
			}
			tempFolder.delete();
			try {
				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage(), ioException);
				throw new PayAsiaSystemException(ioException.getMessage(), ioException);
			}
		}
	}
}
