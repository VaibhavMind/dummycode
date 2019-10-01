/**
 * @author vivekjain
 *
 */
package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

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
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AssignLeaveSchemeForm;
import com.payasia.common.form.AssignLeaveSchemeResponse;
import com.payasia.common.form.EmployeeLeaveDistributionForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AssignLeaveSchemeLogic;
import com.payasia.logic.impl.AssignLeaveSchemeLogicImpl;
import com.payasia.web.controller.AssignLeaveSchemeController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class assignLeaveSchemeControllerImpl.
 */

@Controller
public class AssignLeaveSchemeControllerImpl implements
		AssignLeaveSchemeController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AssignLeaveSchemeLogicImpl.class);

	@Resource
	AssignLeaveSchemeLogic assignLeaveSchemeLogic;
	@Autowired
	private MessageSource messageSource;

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/searchAssignLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchAssignLeaveScheme(
			@RequestParam(value = "sidx", required = false) String columnName,
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

		AssignLeaveSchemeResponse response = assignLeaveSchemeLogic
				.searchAssignLeaveScheme(searchCondition, searchText, fromDate,
						toDate, pageDTO, sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber){
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		AssignLeaveSchemeResponse leaveSchemeResponse = assignLeaveSchemeLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/leaveSchemeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveSchemeList() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<AssignLeaveSchemeForm> leaveSchemeList = assignLeaveSchemeLogic
				.getLeaveSchemeList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(leaveSchemeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/addEmpLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String addEmpLeaveScheme(
			@ModelAttribute(value = "AssignLeaveSchemeForm") AssignLeaveSchemeForm assignLeaveSchemeForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = assignLeaveSchemeLogic.addEmpLeaveScheme(companyId,
				assignLeaveSchemeForm);
		return status;
	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/empLeaveSchemeDataForEdit.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmpLeaveSchemeForEdit(
			@RequestParam(value = "empLeaveSchemeId", required = true) Long empLeaveSchemeId) {
		/* ID DECRYPT */
		empLeaveSchemeId = FormatPreserveCryptoUtil.decrypt(empLeaveSchemeId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AssignLeaveSchemeForm leaveSchemeForm = assignLeaveSchemeLogic
				.getEmpLeaveSchemeForEdit(empLeaveSchemeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/editEmpLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String editEmpLeaveScheme(
			@ModelAttribute(value = "AssignLeaveSchemeForm") AssignLeaveSchemeForm assignLeaveSchemeForm,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = assignLeaveSchemeLogic.editEmpLeaveScheme(companyId,
				assignLeaveSchemeForm);
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
	@RequestMapping(value = "/admin/assignLeaveScheme/deleteEmpLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteEmpLeaveScheme(
			@RequestParam(value = "empLeaveSchemeId", required = true) Long empLeaveSchemeId) {
		try {
			/* ID DECRYPT */
			empLeaveSchemeId =FormatPreserveCryptoUtil.decrypt(empLeaveSchemeId);
			Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
			assignLeaveSchemeLogic.deleteEmpLeaveScheme(empLeaveSchemeId, companyId);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return "false";
		}
		return "true";

	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/viewEmployeeleaveDistribution.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewEmployeeleaveDistribution(
			@RequestParam(value = "empLeaveSchemeId", required = true) Long empLeaveSchemeId,
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/* ID DECRYPT */
		empLeaveSchemeId= FormatPreserveCryptoUtil.decrypt(empLeaveSchemeId);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AssignLeaveSchemeResponse leaveSchemeResponse = assignLeaveSchemeLogic
				.viewEmployeeleaveDistribution(pageDTO, sortDTO, companyId,
						empLeaveSchemeId, year);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/editEmpLeaveDistribution.html", method = RequestMethod.POST)
	@ResponseBody
	public String editEmpLeaveDistribution(
			@ModelAttribute(value = "EmployeeLeaveDistributionForm") EmployeeLeaveDistributionForm employeeLeaveDistributionForm
			) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status = assignLeaveSchemeLogic.editEmpLeaveDistribution(
				companyId, employeeLeaveDistributionForm);
		return status;

	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/importAssignLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String importAssignLeaveScheme(
			@ModelAttribute("AssignLeaveSchemeForm") AssignLeaveSchemeForm assignLeaveSchemeForm,
			Locale locale) throws Exception {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AssignLeaveSchemeForm assignLeaveSchemefrm = new AssignLeaveSchemeForm();
		try {
		
			boolean isFileValid = false;
			if (assignLeaveSchemeForm.getFileUpload()!=null) {
				isFileValid = FileUtils.isValidFile(assignLeaveSchemeForm.getFileUpload(), assignLeaveSchemeForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}

			if(isFileValid){
				assignLeaveSchemefrm = assignLeaveSchemeLogic
						.importAssignLeaveScheme(assignLeaveSchemeForm, companyId);
				if (assignLeaveSchemefrm.getDataImportLogDTOs() != null) {
					for (DataImportLogDTO dataImportLogDTO : assignLeaveSchemefrm
							.getDataImportLogDTOs()) {
						try {
							dataImportLogDTO.setRemarks(URLEncoder.encode(messageSource
									.getMessage(dataImportLogDTO.getRemarks(),
											new Object[] {}, locale), "UTF-8"));
						} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
							LOGGER.error(exception.getMessage(), exception);
							throw new PayAsiaSystemException(exception.getMessage(),
									exception);
						}
					}

				}

			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(assignLeaveSchemefrm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/importLeaveDistribution.html", method = RequestMethod.POST)
	@ResponseBody
	public String importLeaveDistribution(
			@ModelAttribute("AssignLeaveSchemeForm") AssignLeaveSchemeForm assignLeaveSchemeForm,
			Locale locale) throws Exception {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AssignLeaveSchemeForm assignLeaveSchemefrm = new AssignLeaveSchemeForm();
		try {
			
			boolean isFileValid = false;
			if (assignLeaveSchemeForm.getFileUpload()!=null) {
				isFileValid = FileUtils.isValidFile(assignLeaveSchemeForm.getFileUpload(), assignLeaveSchemeForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}

			if(isFileValid){
				assignLeaveSchemefrm = assignLeaveSchemeLogic
						.importLeaveDistribution(assignLeaveSchemeForm, companyId);
				if (assignLeaveSchemefrm.getDataImportLogDTOs() != null) {
					for (DataImportLogDTO dataImportLogDTO : assignLeaveSchemefrm
							.getDataImportLogDTOs()) {
						try {
							dataImportLogDTO.setRemarks(URLEncoder.encode(messageSource
									.getMessage(dataImportLogDTO.getRemarks(),
											new Object[] {}, locale), "UTF-8"));
						} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
							LOGGER.error(exception.getMessage(), exception);
							throw new PayAsiaSystemException(exception.getMessage(),
									exception);
						}
					}

				}

			}
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		}
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(assignLeaveSchemefrm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/getEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<AssignLeaveSchemeForm> assignLeaveSchemeFormList = assignLeaveSchemeLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(assignLeaveSchemeFormList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/assignLeaveScheme/employeeName.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeName(
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber) {
		Long loggedInEmployeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String empName = assignLeaveSchemeLogic.getEmployeeName(
				loggedInEmployeeId, employeeNumber, companyId);
		try {
			return URLEncoder.encode(empName, "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}
	

	@Override
	@RequestMapping(value = "/employee/assignLeaveScheme/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeEmp(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		AssignLeaveSchemeResponse leaveSchemeResponse = assignLeaveSchemeLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	@Override
	@RequestMapping(value = "/employee/assignLeaveScheme/getEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeIdEmp(
			@RequestParam(value = "searchString", required = true) String searchString) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<AssignLeaveSchemeForm> assignLeaveSchemeFormList = assignLeaveSchemeLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(assignLeaveSchemeFormList,
				jsonConfig);
		return jsonObject.toString();

	}
}
