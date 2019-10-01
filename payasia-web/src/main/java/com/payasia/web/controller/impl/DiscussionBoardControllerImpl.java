package com.payasia.web.controller.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmailAttachmentDTO;
import com.payasia.common.dto.TopicAttachmentDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DiscussionBoardForm;
import com.payasia.common.form.DiscussionBoardFormResponse;
import com.payasia.common.form.DiscussionTopicCommentForm;
import com.payasia.common.form.DiscussionTopicCommentResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.bean.ForumTopic;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DiscussionBoardLogic;
import com.payasia.web.controller.DiscussionBoardController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = { "/admin/discussionBoard" })
public class DiscussionBoardControllerImpl implements DiscussionBoardController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(DiscussionBoardControllerImpl.class);
	@Resource
	DiscussionBoardLogic discussionBoardLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	AWSS3Logic awss3LogicImpl;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Resource
	MessageSource messageSource;

	/** The python exe path. */
	@Value("#{payasiaptProperties['payasia.python.exe.path']}")
	private String PYTHON_EXE_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.python.DocumentConverter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;
	
	@Autowired
	private ServletContext servletContext;

	@Override
	@RequestMapping(value = "/getDiscussionBoardTopics.html", method = RequestMethod.POST)
	@ResponseBody
	public String findDiscussionBoardTopics(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "searchCreatedFromText", required = false) String searchCreatedFromText,
			@RequestParam(value = "searchCreatedToText", required = false) String searchCreatedToText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "year", required = false) int year, HttpServletRequest request) {
		
		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		DiscussionBoardFormResponse discussionBoardResponse = null;
		discussionBoardResponse = discussionBoardLogic.findDiscussionBoardTopics(empId, companyId, pageDTO, sortDTO,
				searchCondition, searchText, searchCreatedFromText, searchCreatedToText, year);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/getForumTopicDataById.html", method = RequestMethod.POST)
	@ResponseBody
	public String getForumTopicDataById(@RequestParam(value = "topicId", required = true) String topicId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*ID DECRYPT*/
		topicId=FormatPreserveCryptoUtil.decrypt(Long.valueOf(topicId)).toString();
		DiscussionBoardFormResponse discussionBoardFormResponse = discussionBoardLogic
				.getForumTopicDataById(Long.parseLong(topicId), companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardFormResponse.getDiscussionBoardForm(), jsonConfig);

		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/addDiscussionBoardTopic.html", method = RequestMethod.POST)
	@ResponseBody
	public String addDiscussionBoardTopic(
			@ModelAttribute("discussionBoardForm") DiscussionBoardForm discussionBoardForm,
			@RequestParam(value = "employeeIdsList", required = true) String employeeIdsList,
			HttpServletRequest request) {

		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		if (employeeIdsList != null && !employeeIdsList.isEmpty()) {

		}
		String addTopicValue = discussionBoardLogic.addDiscussionBoardTopic(discussionBoardForm, companyId, empId,
				employeeIdsList);
		return addTopicValue;
	}

	@Override
	@RequestMapping(value = "/updateDiscussionBoardTopic.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateDiscussionBoardTopic(
			@ModelAttribute("discussionBoardForm") DiscussionBoardForm discussionBoardForm,
			@RequestParam(value = "employeeIdsList", required = true) String employeeIdsList,
			HttpServletRequest request) {
		/*ID DECRYPT*/
		discussionBoardForm.setTopicId(FormatPreserveCryptoUtil.decrypt(discussionBoardForm.getTopicId()));
		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		return discussionBoardLogic.updateDiscussionBoardTopic(companyId, empId, discussionBoardForm, employeeIdsList);
	}

	@Override
	@RequestMapping(value = "/deleteDiscussionBoardTopic.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteDiscussionBoardTopic(@RequestParam(value = "topicId", required = true) String topicId,
			HttpServletRequest request) {
		/*ID DECRYPT*/
		topicId=FormatPreserveCryptoUtil.decrypt(Long.valueOf(topicId)).toString();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
        
		return discussionBoardLogic.deleteDiscussionBoardTopic(companyId, Long.parseLong(topicId));
	}

	@RequestMapping(value = "/downloadDiscussionBoardTopicComments.html", method = RequestMethod.GET)
	@Override
	public void downloadDiscussionBoardTopicComments(@RequestParam(value = "topicId", required = true) Long topicId,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		/*ID DECRYPT*/
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		DiscussionTopicCommentResponseForm topicCommentsForm = discussionBoardLogic
				.downloadDiscussionBoardTopicComments(topicId, companyId, employeeId);

		String templateFileName = servletContext
				.getRealPath("/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx");

		List<DiscussionTopicCommentResponseForm> forms = new ArrayList<>();
		forms.add(topicCommentsForm);

		byte[] data = discussionBoardLogic.generateDiscussionTopicCommentsExcel(forms, templateFileName);

		ServletOutputStream outputStream = null;

		try {

			String fileName = topicCommentsForm.getTopicName();
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

	@RequestMapping(value = "/downloadMultipleTopicComments.html", method = RequestMethod.GET)
	@Override
	public void downloadMultipleTopicComments(@RequestParam(value = "topicIds", required = true) String topicIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
        
		List<DiscussionTopicCommentResponseForm> topicCommentForms = discussionBoardLogic
				.downloadMultipleTopicComments(topicIds, companyId, employeeId);
		String templateFileNam = servletContext
				.getRealPath("/resources/HRISReportTemplate/DiscussionBoardTopicComments.xlsx");
		byte[] data = discussionBoardLogic.generateDiscussionTopicCommentsExcel(topicCommentForms, templateFileNam);

		try {
			String fileName = PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_MULTIPLE_TOPIC_EXCEL_FILE_NAME;
			ServletOutputStream outputStream = null;
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

			String user_agent = request.getHeader("user-agent");
			boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);

			if (isInternetExplorer) {
				response.setHeader("Content-disposition",
						"attachment; filename=\"" + URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", " ") + "\"");

			} else {
				response.setHeader("Content-disposition",
						"attachment; filename=\"" + MimeUtility.encodeWord(fileName, "utf-8", "Q") + "\"");

			}

			outputStream.write(data);
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}

		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	@Override
	@RequestMapping(value = "/addTopicComment.html", method = RequestMethod.POST)
	public @ResponseBody String addTopicComment(
			@ModelAttribute("discussionTopicReplyForm") DiscussionTopicCommentForm discussionTopicReplyForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		String employeeIdsList = discussionTopicReplyForm.getTopicEmployeeIds();
		
		boolean isValid = checkValidFileType(discussionTopicReplyForm);
		
		if (isValid) {
			/*ID DECRYPT*/
			discussionTopicReplyForm.setTopicId(FormatPreserveCryptoUtil.decrypt(discussionTopicReplyForm.getTopicId()));
			return discussionBoardLogic.addTopicComment(discussionTopicReplyForm, employeeIdsList, companyId,
					employeeId);
		}
		return PayAsiaConstants.INVALID_FILE;
	}

	private boolean checkValidFileType(DiscussionTopicCommentForm discussionTopicReplyForm) {


		if (discussionTopicReplyForm.getAttachmentList() != null) {
			for (TopicAttachmentDTO topicAttachmentDTO : discussionTopicReplyForm.getAttachmentList()) {
				if (topicAttachmentDTO.getAttachment() != null) {
					if (topicAttachmentDTO.getAttachment().getSize() > 0) {	
						boolean isFileValid = FileUtils.isValidFile(topicAttachmentDTO.getAttachment(), topicAttachmentDTO.getAttachment().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);						
						if (!isFileValid) {
							return false;
						}

					}
				}
			}
		}
		return true;
	}

	@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
	public class NotAcceptableException extends RuntimeException {

	}

	@Override
	@RequestMapping(value = "/deleteTopicComment.html", method = RequestMethod.POST)
	public @ResponseBody String deleteTopicComment(
			@RequestParam(value = "topicCommentId", required = true) Long topicCommentId, HttpServletRequest request) {
		/*ID DECRYPT*/
		topicCommentId=FormatPreserveCryptoUtil.decrypt(topicCommentId);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		return discussionBoardLogic.deleteTopicComment(topicCommentId, companyId);
	}

	@Override
	@RequestMapping(value = "/updateTopicComment.html", method = RequestMethod.POST)
	public @ResponseBody String updateTopicComment(
			@ModelAttribute("discussionTopicReplyForm") DiscussionTopicCommentForm discussionTopicReplyForm,
			@RequestParam(value = "employeeIdsList", required = true) String employeeIdsList, BindingResult result,
			ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		
		boolean isFileValid = checkValidFileType(discussionTopicReplyForm);
		
		if (isFileValid) {
			return discussionBoardLogic.updateTopicComment(discussionTopicReplyForm, companyId, employeeId,
					employeeIdsList);
		}
		return PayAsiaConstants.INVALID_FILE;
	}

	@Override
	@RequestMapping(value = "/getTopicCommentData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTopicCommentData(long topicCommentId, ModelMap model) {
		/*ID DECRYPT*/
		topicCommentId=FormatPreserveCryptoUtil.decrypt(topicCommentId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		DiscussionTopicCommentForm discussionTopicReplyForm = discussionBoardLogic.getTopicCommentData(topicCommentId,companyId);
		model.addAttribute("discussionTopicReplyForm", discussionTopicReplyForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicReplyForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/getTopicCommentList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTopicCommentList(long topicId, ModelMap model, HttpServletResponse response,
			HttpServletRequest request) {
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		/*ID DECRYPT*/
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);
		
		DiscussionTopicCommentResponseForm discussionTopicForm = discussionBoardLogic.getTopicCommentList(topicId,
				companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/viewAttachment", method = RequestMethod.GET)
	public @ResponseBody void viewAttachment(@RequestParam(value = "attachmentId", required = true) Long attachmentId,
			HttpServletResponse response, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmailAttachmentDTO attachmentDTO = discussionBoardLogic.viewAttachment(attachmentId, companyId, employeeId);
		String filePath = attachmentDTO.getAttachmentPath();
		BufferedInputStream is;
		long contentLength = 0;
		if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
			try {
				InputStream fins = awss3LogicImpl.readS3ObjectAsStream(filePath);
				is = new BufferedInputStream(fins);
				contentLength = fins.available();
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if (filePath != null) {
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			FileInputStream fis;
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
			String strFileExt = fileType;
			FileInputStream stream = null;
			try {

				File file = new File(filePath);

				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation) || (file
						.exists()
						&& !PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation))) {

					switch (strFileExt.toLowerCase()) {
					case "pdf":
						response.setHeader("Content-Type", "application/pdf");
						break;
					case "doc":
						response.setHeader("Content-Type", "application/msword");
						break;
					case "xls":
						response.setHeader("Content-Type", "application/vnd.ms-excel");
						break;
					case "ppt":
						response.setHeader("Content-Type", "application/vnd.ms-powerpoint");
						break;
					case "docx":
						response.setHeader("Content-Type",
								"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
						break;
					case "pptx":
						response.setHeader("Content-Type",
								"application/vnd.openxmlformats-officedocument.presentationml.presentation");
						break;
					case "xlsx":
						response.setHeader("Content-Type",
								"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
						break;
					default:
						response.setHeader("Content-Type", "application/octet-stream");
						break;
					}

					if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						fis = new FileInputStream(file);
						is = new BufferedInputStream(fis);
					} else {
						InputStream fins = awss3LogicImpl.readS3ObjectAsStream(filePath);
						is = new BufferedInputStream(fins);
					}
					response.setHeader("Content-Disposition",
							"attachment;filename=" + attachmentDTO.getAttachmentName());

					ServletOutputStream out = response.getOutputStream();
					byte[] buf = new byte[50 * 50 * 1024];
					int bytesRead;
					while ((bytesRead = is.read(buf)) != -1) {
						out.write(buf, 0, bytesRead);
					}
					is.close();

				}
			} catch (IOException iOException) {
				LOGGER.error(iOException.getMessage(), iOException);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException iOException) {
						LOGGER.error(iOException.getMessage(), iOException);
					}
				}
			}
		}

	}

	@Override
	@RequestMapping(value = "/viewSeletecdAttachments", method = RequestMethod.GET)
	public @ResponseBody void viewSeletecdAttachments(
			@RequestParam(value = "attachmentIds", required = true) String attachmentIds, HttpServletResponse response,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		DiscussionTopicCommentForm discussionTopicCommentForm = discussionBoardLogic
				.viewSeletecdAttachments(attachmentIds, companyId, employeeId);
		List<TopicAttachmentDTO> attachmentDTOList = discussionTopicCommentForm.getAttachmentList();

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + discussionTopicCommentForm.getAttachmentName() + ".zip");

		ServletOutputStream out = null;

		try {
			out = response.getOutputStream();
			ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(out));

			for (TopicAttachmentDTO emailAttachmentDTO : attachmentDTOList) {

				zos.putNextEntry(new ZipEntry(emailAttachmentDTO.getAttachmentName()));

				String filePath = emailAttachmentDTO.getAttachmentPath();
				BufferedInputStream fif;
				try {
					FileInputStream fis = null;

					if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
						InputStream fins = awss3LogicImpl.readS3ObjectAsStream(filePath);
						fif = new BufferedInputStream(fins);
					} else {
						File newfile = new File(filePath);
						fis = new FileInputStream(newfile);
						fif = new BufferedInputStream(fis);
					}

				} catch (FileNotFoundException fnfe) {
					zos.write(("ERRORld not find file " + emailAttachmentDTO.getAttachmentName()).getBytes());
					zos.closeEntry();
					continue;
				}

				int data = 0;
				while ((data = fif.read()) != -1) {
					zos.write(data);
				}
				fif.close();

				zos.closeEntry();
			}

			zos.close();
		} catch (IOException iOException) {
			LOGGER.error(iOException.getMessage(), iOException);
		}

	}

	@Override
	@RequestMapping(value = "/deleteAttachment.html", method = RequestMethod.POST)
	public @ResponseBody String deleteAttachment(
			@RequestParam(value = "attachmentId", required = true) long attachmentId, HttpServletResponse response,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = discussionBoardLogic.deleteAttachment(attachmentId, companyId);
		return status;
	}

	@Override
	@RequestMapping(value = "/getTopicCommentAttachmentList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTopicCommentAttachmentList(
			@RequestParam(value = "topicCommentId", required = true) Long topicCommentId, ModelMap model,
			HttpServletResponse response, HttpServletRequest request) {
		/*ID DECRYPT*/
		topicCommentId = FormatPreserveCryptoUtil.decrypt(topicCommentId);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		DiscussionTopicCommentResponseForm discussionTopicForm = discussionBoardLogic
				.getTopicCommentAttachmentList(topicCommentId, companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionTopicForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/getTopicDefaultEmail.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTopicDefaultEmail(@RequestParam(value = "topicId", required = true) Long topicId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String emailIds = discussionBoardLogic.getTopicDefaultEmail(companyId, topicId, employeeId);
		return emailIds;

	}

	@Override
	@RequestMapping(value = "/getTopicDefaultEmailCC.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTopicDefaultEmailCC(@RequestParam(value = "topicId", required = true) Long topicId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String emailIds = discussionBoardLogic.getTopicDefaultEmailCC(companyId, topicId, employeeId);
		return emailIds;

	}

	@Override
	@RequestMapping(value = "/getDisscusBoardDefaultEmail.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDisscusBoardDefaultEmail(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String emailIds = discussionBoardLogic.getDisscusBoardDefaultEmail(companyId, employeeId);
		return emailIds;

	}

	@Override
	@RequestMapping(value = "/getDisscusBoardDefaultEmailCC.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDisscusBoardDefaultEmailCC(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String emailIds = discussionBoardLogic.getDisscusBoardDefaultEmailCC(companyId, employeeId);
		return emailIds;

	}

	@Override
	@RequestMapping(value = "/getEnableVisibility.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEnableVisibility(HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String enableVisibility = discussionBoardLogic.getEnableVisibility(companyId);
		return enableVisibility;

	}

	@Override
	@RequestMapping(value = "/getDiscussionBoardTopicStatusHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String getdDiscussionBoardTopicStatusHistory(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "topicId", required = true) Long topicId, HttpServletRequest request) {

		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		/*ID DECRYPT*/
		topicId = FormatPreserveCryptoUtil.decrypt(topicId);
		
		DiscussionBoardFormResponse discussionBoardResponse = null;
		discussionBoardResponse = discussionBoardLogic.getDiscussionBoardTopicStatusHistory(empId, companyId, pageDTO,
				sortDTO, topicId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/getYearList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getYearList(HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Set<Integer> yearList = discussionBoardLogic.getDistinctYears(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(yearList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/searchEmployee", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "metaData", required = false) String metaData,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		DiscussionBoardFormResponse discussionBoardFormResponse = discussionBoardLogic.searchEmployee(null, pageDTO,
				sortDTO, searchCondition, searchText, companyId, employeeId, metaData, includeResignedEmployees,
				"SearchEmployee", "");

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/searchTopicEmployee", method = RequestMethod.POST)
	@ResponseBody
	public String searchTopicEmployees(@RequestParam(value = "topicId", required = true) String topicId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "metaData", required = false) String metaData,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		topicId=FormatPreserveCryptoUtil.decrypt(Long.parseLong(topicId)).toString();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		ForumTopic topicData = discussionBoardLogic.getTopicData(companyId,
				(topicId == null ? Long.parseLong("0") : Long.parseLong(topicId)));
		String topicEmployeeList = topicData.getTopicEmployeeIds();

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		DiscussionBoardFormResponse discussionBoardFormResponse = discussionBoardLogic.searchEmployee(topicData,
				pageDTO, sortDTO, searchCondition, searchText, companyId, employeeId, metaData,
				includeResignedEmployees, "SearchTopicEmployee", topicEmployeeList);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(discussionBoardFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
}