package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationPdfDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.LeaveApplicationAttachmentDAO;
import com.payasia.dao.LeaveSchemeDAO;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.web.controller.AddLeaveController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class AddLeaveControllerImpl implements AddLeaveController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AddLeaveControllerImpl.class);

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	@Resource
	AddLeaveLogic addLeaveLogic;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	AWSS3Logic awss3LogicImpl;
	
	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	
	@Resource
	LeaveSchemeDAO leaveSchemeDao;
	
	@Resource
	LeaveApplicationAttachmentDAO leaveApplicationAttachmentDAO;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Override
	@RequestMapping(value = "/admin/addLeaves/getLeaveTypes.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypes(
			@RequestParam(value = "leaveSchemeId") Long leaveSchemeId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		LeaveScheme leaveScheme = leaveSchemeDao.findSchemeByCompanyID(leaveSchemeId, companyId);
		if(leaveScheme == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveTypes(leaveSchemeId,
				companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	//To do url not mapped
	@Override
	@RequestMapping(value = "/employee/addLeaves/addLeave.html", method = RequestMethod.POST)
	public ModelAndView addLeave(
			@ModelAttribute("addLeaveForm") AddLeaveForm addLeaveForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		addLeaveLogic.addLeave(companyId, employeeId, addLeaveForm, sessionDTO);
		return new ModelAndView("employee/addLeaves");
	}

	//To do url not mapped
	@Override
	//@RequestMapping(value = "/employee/addLeaves/editLeave.html", method = RequestMethod.POST)
	public ModelAndView editLeave(
			@ModelAttribute("addLeaveForm") AddLeaveForm addLeaveForm,
			BindingResult result, ModelMap model, HttpServletRequest request,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		addLeaveLogic
				.editLeave(companyId, employeeId, addLeaveForm, sessionDTO);
		return new ModelAndView("employee/addLeaves");
	}

	@Override
	@RequestMapping(value = "/employee/addLeaves/applyLeave.html", method = RequestMethod.POST)
	public ModelAndView applyLeave(
			@ModelAttribute("addLeaveForm") AddLeaveForm addLeaveForm,
			 ModelMap model,Locale locale) {
			
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		int invalidFileCount = 0;
		if (addLeaveForm.getAttachmentList() != null) {
			for (LeaveApplicationAttachmentDTO applicationAttachmentDTO : addLeaveForm
					.getAttachmentList()) {
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() > 0) {

					boolean isFileValid = FileUtils.isValidFile(applicationAttachmentDTO.getAttachment(), applicationAttachmentDTO.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
					
					if (!isFileValid) {
						invalidFileCount++;
					}
				}
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() == 0) {
					invalidFileCount++;
				}
			}
		}
		if (invalidFileCount != 0) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_INVALID_FILE);
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		} else {
			String leaveMode = addLeaveForm.getLeaveMode();
			if ("apply".equalsIgnoreCase(leaveMode)) {
				addLeaveFormRes = addLeaveLogic.addLeave(companyId, employeeId,
						addLeaveForm, sessionDTO);
			} else if ("modify".equalsIgnoreCase(leaveMode)) {
				addLeaveFormRes = addLeaveLogic.editLeave(companyId,
						employeeId, addLeaveForm, sessionDTO);
			}
		}

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null
				&& addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue()
							.split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr.append(messageSource.getMessage(
								errorKeyArr[count], errorVal, locale)
								+ " </br> ");
					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes,
				jsonConfig);
		model.put("jSonObject", jsonObject);
		return new ModelAndView("employee/applyResponse");
	}
	@Override
	@RequestMapping(value = "/employee/addLeaves/extendLeave.html", method = RequestMethod.POST)
	public ModelAndView extensionLeave(@ModelAttribute("addLeaveForm") AddLeaveForm addLeaveForm,
			ModelMap model, Locale locale) {
         /* ID DECRYPT */
		addLeaveForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(addLeaveForm.getLeaveApplicationId()));
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(
				messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY, new Object[] {}, locale));

		addLeaveFormRes = addLeaveLogic.extensionLeave(companyId, employeeId, addLeaveForm, sessionDTO);

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null && addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO().getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO().getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr
								.append(messageSource.getMessage(errorKeyArr[count], errorVal, locale) + " </br> ");
					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes, jsonConfig);
		model.put("jSonObject", jsonObject);
		return new ModelAndView("employee/applyResponse");
	}

	@Override
	@RequestMapping(value = "/admin/addLeaves/extensionLeaveView.html", method = RequestMethod.POST)
	@ResponseBody
	public String extensionLeave(@RequestParam(value = "leaveApplicationId") Long leaveApplicationId,
			 Locale locale) {
         // To check company Id check for admin
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*String response = addLeaveLogic.extensionLeaveView(leaveApplicationId, empId, companyId);*/
		String response = addLeaveLogic.extensionLeaveViewAdmin(leaveApplicationId, empId, companyId);
		return response;

	}


	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getDataForPendingLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForPendingLeave(
			@RequestParam(value = "leaveApplicationId") Long leaveApplicationId) {
		
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm = null;
		if(leaveApplicationId != null)
		{
		   addLeaveForm = addLeaveLogic.getDataForPendingLeave(leaveApplicationId, employeeId, companyId);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/viewLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewLeave(
			@RequestParam(value = "leaveApplicationId") Long leaveApplicationId,
			Locale locale) {
		/* ID DECRYPT */
		leaveApplicationId =  FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddLeaveForm addLeaveForm = null;
		if(leaveApplicationId != null)
		{
			addLeaveForm = addLeaveLogic.viewLeave(leaveApplicationId,employeeId,companyId);
		}

		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(messageSource.getMessage(
					addLeaveForm.getLeaveAppStatus(), new Object[] {}, locale));
		}

		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm
				.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(messageSource.getMessage(
						leaveApplicationWorkflowDTO.getStatus(),
						new Object[] {}, locale));
			}
		}

		addLeaveForm.setLeaveDTO(addLeaveForm.getLeaveDTO()!=null ? addLeaveForm.getLeaveDTO() : new LeaveDTO());
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/addLeaves/viewAttachment.html", method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachment(
			@RequestParam(value = "attachmentId", required = true) long attachmentId,
			HttpServletResponse response) {
		/* ID DECRYPT */
		attachmentId = FormatPreserveCryptoUtil.decrypt(attachmentId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveApplicationAttachment attachment = leaveApplicationAttachmentDAO.findAttachmentByEmployeeCompanyId(attachmentId, companyId);
		if(attachment == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		LeaveApplicationAttachmentDTO attachmentDTO = addLeaveLogic
				.viewAttachment(attachmentId);
		byte[] byteFile = attachmentDTO.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(attachmentDTO
				.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = attachmentDTO.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);

		return byteFile;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getPendingLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows
			) {
		Long empId = Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
  		pageDTO.setPageSize(rows);
  		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getPendingLeaves(fromDate, toDate,
				empId, pageDTO, sortDTO, searchCondition, searchText,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getSubmittedLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getSubmittedLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getSubmittedLeaves(fromDate, toDate,
				empId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getSubmittedCancelledLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getSubmittedCancelledLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getSubmittedCancelledLeaves(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getCompletedLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompletedLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getCompletedLeaves(fromDate, toDate,
				empId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getCompletedCancelLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompletedCancelLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getCompletedCancelLeaves(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getRejectedLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getRejectedLeaves(fromDate, toDate,
				empId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getRejectedCancelLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedCancelLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getRejectedCancelLeaves(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getWithDrawnLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getWithDrawnLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getWithDrawnLeaves(fromDate, toDate,
				empId, pageDTO, sortDTO, pageContextPath, searchCondition,
				searchText,companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getWithDrawnCancelLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getWithDrawnCancelLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long empId = Long.parseLong(UserContext.getUserId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = addLeaveLogic.getWithDrawnCancelLeaves(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/deleteAttachment.html", method = RequestMethod.POST)
	public void deleteAttachment(@RequestParam(value = "attachmentId", required = true) long attachmentId) {
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		if(attachmentId != 0L)
		{
			addLeaveLogic.deleteAttachment(attachmentId,empId,companyId);
		}
		
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/withdrawLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String withdrawLeave(
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			Locale locale) {
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		String response = addLeaveLogic.withdrawLeave(leaveApplicationId,
				empId, sessionDTO, companyId);
		return response;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/deleteLeave.html", method = RequestMethod.POST)
	public void deleteLeave(
			@RequestParam(value = "leaveApplicationId", required = true) long leaveApplicationId) {
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		if(leaveApplicationId != 0L)
		{
		  addLeaveLogic.deleteLeave(leaveApplicationId,empId,companyId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#searchEmployee(
	 * java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.Sring, tjavax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/addLeaves/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
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

		LeaveReviewerResponseForm leaveReviewerResponse = addLeaveLogic
				.searchEmployee(pageDTO, sortDTO, employeeId, empName,
						empNumber, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/addLeaves/getLeaveCustomFields.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveCustomFields(
			@RequestParam(value = "leaveSchemeId") Long leaveSchemeId,
			@RequestParam(value = "leaveTypeId") Long leaveTypeId,
			@RequestParam(value = "employeeLeaveSchemeId") Long employeeLeaveSchemeId) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveCustomFields(
				leaveSchemeId, leaveTypeId, companyId, employeeId,
				employeeLeaveSchemeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/addLeaves/getLeaveCustomFieldAndReviewerForAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveCustomFieldsForAdmin(
			@RequestParam(value = "leaveSchemeId") Long leaveSchemeId,
			@RequestParam(value = "leaveTypeId") Long leaveTypeId,
			@RequestParam(value = "employeeLeaveSchemeId") Long employeeLeaveSchemeId) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm = addLeaveLogic
				.getLeaveCustomFieldAndReviewerForAdmin(leaveSchemeId,
						leaveTypeId, companyId, employeeId,
						employeeLeaveSchemeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	//To do url not mapped	
	@Override
	@RequestMapping(value = "/getViewLeaveCustomFields.html", method = RequestMethod.POST)
	@ResponseBody
	public String getViewLeaveCustomFields(
			@RequestParam(value = "leaveSchemeId") Long leaveSchemeId,
			@RequestParam(value = "leaveTypeId") Long leaveTypeId,
			@RequestParam(value = "employeeLeaveSchemeId") Long employeeLeaveSchemeId,
			HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveCustomFields(
				leaveSchemeId, leaveTypeId, companyId, employeeId,
				employeeLeaveSchemeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/addLeaves/getLeaveBalance.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveBalance(
			@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId,
			HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findSchemeTypeByCompanyId(employeeLeaveSchemeTypeId, companyId);
		if(employeeLeaveSchemeType == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveBalance(companyId,
				employeeId, employeeLeaveSchemeTypeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/addLeaves/getDays.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDays(
			@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate,
			@RequestParam(value = "session1") Long session1,
			@RequestParam(value = "session2") Long session2,
			@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId,
			HttpServletRequest request, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(fromDate);
		leaveDTO.setToDate(toDate);
		leaveDTO.setSession1(session1);
		leaveDTO.setSession2(session2);
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findSchemeTypeByCompanyId(employeeLeaveSchemeTypeId, companyId);
		if(employeeLeaveSchemeType == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeTypeId);
		AddLeaveForm addLeaveForm = addLeaveLogic.getNoOfDays(companyId,
				employeeId, leaveDTO);

		if (addLeaveForm.getLeaveDTO() != null) {
			if (StringUtils
					.isNotBlank(addLeaveForm.getLeaveDTO().getErrorKey())) {
				addLeaveForm.getLeaveDTO().setErrorKey(
						messageSource.getMessage(addLeaveForm.getLeaveDTO()
								.getErrorKey(), new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/printLeaveApplicationForm.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printLeaveApplicationForm(
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			HttpServletResponse response) {
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveApplicationPdfDTO leaveAppPdfDTO = addLeaveLogic
				.generateLeaveApplicationPrintPDF(companyId, employeeId,
						leaveApplicationId);

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(leaveAppPdfDTO
				.getEmployeeNumber()
				+ "_"
				+ leaveAppPdfDTO.getLeaveSchemeName() + uuid + ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(leaveAppPdfDTO.getLeaveAppPdfByteFile().length);
		String filename = leaveAppPdfDTO.getEmployeeNumber() + "_"
				+ leaveAppPdfDTO.getLeaveSchemeName() + uuid + ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);

		return leaveAppPdfDTO.getLeaveAppPdfByteFile();
	}
	//To do url not mapped
	@Override
	@RequestMapping(value = "/employee/addLeaves/getEmpLeaveSchemeInfo.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmpLeaveSchemeInfo(HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		LeaveSchemeForm leaveSchemeForm = addLeaveLogic.getLeaveSchemes(
				companyId, employeeId);
		/* ID ENCRYPT*/
		//leaveSchemeForm.setEmployeeLeaveSchemeId(FormatPreserveCryptoUtil.encrypt(leaveSchemeForm.getEmployeeLeaveSchemeId()));
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/addLeaves/addLeaveByAdmin.html", method = RequestMethod.POST)
	public ModelAndView addLeaveByAdmin(
			@ModelAttribute("addLeaveForm") AddLeaveForm addLeaveForm,
			ModelMap model,	Locale locale) {
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		int invalidFileCount = 0;
		if (addLeaveForm.getAttachmentList() != null) {
			for (LeaveApplicationAttachmentDTO applicationAttachmentDTO : addLeaveForm
					.getAttachmentList()) {
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() > 0) {
				
					boolean isFileValid = FileUtils.isValidFile(applicationAttachmentDTO.getAttachment(), applicationAttachmentDTO.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
					
					if (!isFileValid) {
						invalidFileCount++;
					}
				}
				if (applicationAttachmentDTO.getAttachment() != null
						&& applicationAttachmentDTO.getAttachment().getSize() == 0) {
					invalidFileCount++;
				}
			}
		}
		if (invalidFileCount != 0) {
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey(PayAsiaConstants.PAYASIA_INVALID_FILE);
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		} else {
			String leaveMode = addLeaveForm.getLeaveMode();
			if ("apply".equalsIgnoreCase(leaveMode)) {
				addLeaveFormRes = addLeaveLogic.addLeaveByAdmin(companyId,
						employeeId, addLeaveForm, sessionDTO);
			}
		}

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null
				&& addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue()
							.split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr.append(messageSource.getMessage(
								errorKeyArr[count], errorVal, locale)
								+ " </br> ");
					}

				}

			}
			addLeaveFormRes.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes,
				jsonConfig);
		model.put("jSonObject", jsonObject);
		return new ModelAndView("employee/applyResponse");
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getLeaveTypes.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypesEmp(@RequestParam(value = "leaveSchemeId") Long leaveSchemeId) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveTypes(leaveSchemeId, companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getDays.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDaysEmp(@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate, @RequestParam(value = "session1") Long session1,
			@RequestParam(value = "session2") Long session2,
			@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId,
			Locale locale) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(fromDate);
		leaveDTO.setToDate(toDate);
		leaveDTO.setSession1(session1);
		leaveDTO.setSession2(session2);
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeTypeId);
		AddLeaveForm addLeaveForm = addLeaveLogic.getNoOfDays(companyId, employeeId, leaveDTO);

		if (addLeaveForm.getLeaveDTO() != null) {
			if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO().getErrorKey())) {
				addLeaveForm.getLeaveDTO().setErrorKey(
						messageSource.getMessage(addLeaveForm.getLeaveDTO().getErrorKey(), new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}
	
	
	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/extensionLeaveView.html", method = RequestMethod.POST)
	@ResponseBody
	public String extensionLeaveEmp(@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId) {

		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String response = null;
		if(leaveApplicationId != null)
		{
			response = addLeaveLogic.extensionLeaveView(leaveApplicationId, empId,companyId);
		}
		return response;

	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/viewAttachment.html", method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachmentEmp(@RequestParam(value = "attachmentId", required = true) long attachmentId,
			HttpServletResponse response) {
		/* ID DECRYPT */
		attachmentId= FormatPreserveCryptoUtil.decrypt(attachmentId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveApplicationAttachmentDTO attachmentDTO = null;
		if(attachmentId != 0L)
		{
			attachmentDTO = addLeaveLogic.viewAttachment(attachmentId,employeeId,companyId);
		}
		 byte[] byteFile = attachmentDTO.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(attachmentDTO.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = attachmentDTO.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/getLeaveBalance.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveBalanceEmp(@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId) {
			
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveBalance(companyId, employeeId, employeeLeaveSchemeTypeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.WorkFlowDelegateController#searchEmployee(
	 * java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.Sring, tjavax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	//@PreAuthorize("hasRole('PRIV_MY_REQUESTS')")
	@RequestMapping(value = "/employee/addLeaves/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeEmp(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
	
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReviewerResponseForm leaveReviewerResponse = addLeaveLogic.searchEmployee(pageDTO, sortDTO, employeeId,
				empName, empNumber, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
}
