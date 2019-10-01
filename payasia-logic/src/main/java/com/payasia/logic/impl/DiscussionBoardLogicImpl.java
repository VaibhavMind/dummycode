package com.payasia.logic.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.payasia.common.dto.DiscussionBoardConditionDTO;
import com.payasia.common.dto.EmailAttachmentDTO;
import com.payasia.common.dto.EmployeeConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.TimeZoneDTO;
import com.payasia.common.dto.TopicAttachmentDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DiscussionBoardForm;
import com.payasia.common.form.DiscussionBoardFormResponse;
import com.payasia.common.form.DiscussionTopicCommentForm;
import com.payasia.common.form.DiscussionTopicCommentResponseForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.RandomNumberGenerator;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmailDAO;
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.EmailTemplateDAO;
import com.payasia.dao.EmailTemplateSubCategoryMasterDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.ForumTopicCommentAttachmentDAO;
import com.payasia.dao.ForumTopicCommentDAO;
import com.payasia.dao.ForumTopicDAO;
import com.payasia.dao.ForumTopicStatusChangeHistoryDAO;
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.TimeZoneMasterDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Email;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailTemplate;
import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.ForumTopic;
import com.payasia.dao.bean.ForumTopicComment;
import com.payasia.dao.bean.ForumTopicCommentAttachment;
import com.payasia.dao.bean.ForumTopicStatusChangeHistory;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.TimeZoneMaster;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DiscussionBoardLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.GeneralLogic;

@Component
public class DiscussionBoardLogicImpl implements DiscussionBoardLogic {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(DiscussionBoardLogicImpl.class);

	@Resource
	ForumTopicDAO forumTopicDAO;
	@Resource
	EmployeeDAO employeeDAO;
	@Resource
	CompanyDAO companyDAO;
	@Resource
	ForumTopicCommentDAO forumTopicCommentDAO;
	@Resource
	ForumTopicCommentAttachmentDAO forumTopicCommentAttachmentDAO;
	@Resource
	EmailDAO emailDAO;
	@Autowired
	private VelocityEngine velocityEngine;
	@Resource
	EmailTemplateSubCategoryMasterDAO emailTemplateSubCategoryMasterDAO;
	@Resource
	EmailTemplateDAO emailTemplateDAO;
	@Resource
	HRISPreferenceDAO hrisPreferenceDAO;
	@Resource
	ForumTopicStatusChangeHistoryDAO forumTopicStatusChangeHistoryDAO;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	TimeZoneMasterDAO timeZoneMasterDAO;
	@Resource
	AWSS3Logic awss3LogicImpl;

	@Resource
	GeneralLogic generalLogic;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;

	/** The documents Root Directory Name. */
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;

	@Value("#{payasiaptProperties['payasia.tax.document.document.name.separator']}")
	private String fileNameSeperator;

	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;
	@Resource
	EmailPreferenceMasterDAO emailPreferenceMasterDAO;

	@Resource
	FileUtils fileUtils;
	private final String EXCELSHEET_NAME_SEPARATOR = "_";
	
	@Resource
	private EmployeeDetailLogic employeeDetailLogic;
	
	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;

	@Override
	public DiscussionBoardFormResponse findDiscussionBoardTopics(Long employeeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText, String searchCreatedFromText,
			String searchCreatedToText, int year) {
		DiscussionBoardConditionDTO conditionDTO = new DiscussionBoardConditionDTO();
		conditionDTO.setEmployeeId(employeeId);
		conditionDTO.setCompanyId(companyId);

		if (StringUtils.isNotBlank(searchCondition)) {
			if (searchCondition.equals(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setTopicName(searchText.trim());
				}
			}
			if (searchCondition.equals(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_AUTHOR)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setTopicAuthor(searchText.trim());
				}
			}
			if (searchCondition.equals(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_STATUS)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setTopicStatus(searchText.trim());
				}
			}
			if (searchCondition.equals(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_CREATED_ON)) {
				if (StringUtils.isNotBlank(searchText)) {
					conditionDTO.setCreatedOn(searchText.trim());
				}
			}
		}

		if (StringUtils.isNotBlank(searchCreatedFromText)) {
			conditionDTO.setCreatedFrom(searchCreatedFromText.trim());
		}
		if (StringUtils.isNotBlank(searchCreatedToText)) {
			conditionDTO.setCreatedTo(searchCreatedToText.trim());
		}
		conditionDTO.setYear(year);

		List<DiscussionBoardForm> discussionBoardForms = getTopicList(employeeId, companyId, pageDTO, sortDTO,
				conditionDTO);

		DiscussionBoardFormResponse response = new DiscussionBoardFormResponse();
		if (pageDTO != null) {
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;
			Integer recordSize = discussionBoardForms.size();
			int totalPages = recordSize / pageSize;

			if (startPos < recordSize) {
				if (recordSize % pageSize != 0) {
					totalPages = totalPages + 1;
				}
				if ((startPos + pageSize) <= recordSize){
					endPos = startPos + pageSize;
				} else if (startPos <= recordSize) {
					endPos = recordSize;
				}
				discussionBoardForms = discussionBoardForms.subList(startPos, endPos);
				response.setDiscussionBoardFormList(discussionBoardForms);
			}
			response.setTotal(totalPages);
			response.setRecords(recordSize);
			response.setPage(pageDTO.getPageNumber());
		}
		return response;
	}

	private List<DiscussionBoardForm> getTopicList(Long employeeId, Long companyId, PageRequest pageDTO,
			SortCondition sortDTO, DiscussionBoardConditionDTO conditionDTO) {
		List<ForumTopic> forumTopicList = null;
		List<DiscussionBoardForm> discussionBoardForms = new ArrayList<DiscussionBoardForm>();
		forumTopicList = forumTopicDAO.findDiscussionBoardTopics(employeeId, companyId, sortDTO, conditionDTO);

		Set<Long> forumTopicIdSet = new HashSet<Long>();
		for (ForumTopic forumTopic : forumTopicList) {
			Long topicId = forumTopic.getTopicId();
			if (forumTopicIdSet.contains(topicId)) {
				continue;
			} else {
				forumTopicIdSet.add(topicId);
			}

			DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
			String topicAuthorLastName = "";
			if (StringUtils.isNotBlank(forumTopic.getCreatedBy().getLastName())) {
				topicAuthorLastName = " " + forumTopic.getCreatedBy().getLastName();
			}
			String topicAuthorName = forumTopic.getCreatedBy().getFirstName() + topicAuthorLastName + " ("
					+ forumTopic.getCreatedBy().getEmployeeNumber() + ")";
			Set<ForumTopicComment> tempForumTopicCommentSet = forumTopic.getForumTopicComments();
			Set<ForumTopicComment> forumTopicCommentSet = new HashSet<ForumTopicComment>();
			for (ForumTopicComment forumTopicComment : tempForumTopicCommentSet) {
				if (forumTopicComment.getPublishedStatus().getCodeValue()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_PUBLISHED)) {
					forumTopicCommentSet.add(forumTopicComment);
				}

			}

			TreeSet<ForumTopicComment> forumTopicCommentTreeSet = new TreeSet<ForumTopicComment>(
					new TopicCommentIdComparator());
			forumTopicCommentTreeSet.addAll(forumTopicCommentSet);
			long topicCommentsCount = forumTopicCommentSet.size();

			String topicLastCommentDetail = "";

			if (!forumTopicCommentTreeSet.isEmpty()) {
				ForumTopicComment firstForumTopicComment = forumTopicCommentTreeSet.iterator().next();
				String commentAuthorName = "";
				if (StringUtils.isNotBlank(firstForumTopicComment.getCreatedBy().getLastName())) {
					commentAuthorName = firstForumTopicComment.getCreatedBy().getFirstName() + " "
							+ firstForumTopicComment.getCreatedBy().getLastName();
				} else {
					commentAuthorName = firstForumTopicComment.getCreatedBy().getFirstName();
				}

				topicLastCommentDetail = DateUtils.timeStampToStringWithTime2(firstForumTopicComment.getPublishedDate(),
						firstForumTopicComment.getForumTopic().getCompany()) + " [" + commentAuthorName + "]";
			}

			/* ID ENCRYPT */
			discussionBoardForm.setTopicId(FormatPreserveCryptoUtil.encrypt(forumTopic.getTopicId()));
			discussionBoardForm.setTopicName(forumTopic.getTopicName());
			discussionBoardForm.setTopicAuthorName(topicAuthorName);
			discussionBoardForm.setTopicAuthorId(forumTopic.getCreatedBy().getEmployeeId());
			discussionBoardForm.setCommentsCount(topicCommentsCount);
			discussionBoardForm.setLastComment(topicLastCommentDetail);

			if (forumTopic.isStatus()) {
				discussionBoardForm.setTopicStatus(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_OPEN);
			} else {
				discussionBoardForm.setTopicStatus(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_CLOSED);
			}
			//
			discussionBoardForm.setCreatedOn(
					DateUtils.timeStampToStringWithTime2(forumTopic.getCreatedDate(), forumTopic.getCompany()));

			HRISPreference hrisPreference = hrisPreferenceDAO.findByCompanyId(companyId);
			boolean isUserAuthorizedForTopic = false;
			if (hrisPreference != null) {
				if (hrisPreference.isEnableVisibility() != null && hrisPreference.isEnableVisibility()) {
					if (employeeId.equals(forumTopic.getCreatedBy().getEmployeeId())) {
						isUserAuthorizedForTopic = true;
					} else if ((hrisPreference.getDiscussionBoardPayAsiaUsers() != null
							&& hrisPreference.getDiscussionBoardPayAsiaUsers() != ""
							&& !hrisPreference.getDiscussionBoardPayAsiaUsers().isEmpty())) {
						String arrTopicEmployees[] = hrisPreference.getDiscussionBoardPayAsiaUsers().split(";");
						List<String> empList = Arrays.asList(arrTopicEmployees);
						if (empList.contains(String.valueOf(employeeId))
								|| forumTopic.getCreatedBy().getEmployeeId() == employeeId) {
							isUserAuthorizedForTopic = true;
						}
					}
					if (!isUserAuthorizedForTopic && (forumTopic.getTopicEmployeeIds() != null
							&& forumTopic.getTopicEmployeeIds() != "" && !forumTopic.getTopicEmployeeIds().isEmpty())) {
						String arrTopicEmployees[] = forumTopic.getTopicEmployeeIds().split(",");
						List<String> empList = Arrays.asList(arrTopicEmployees);
						if (empList.contains(String.valueOf(employeeId))
								|| forumTopic.getCreatedBy().getEmployeeId() == employeeId) {
							isUserAuthorizedForTopic = true;
						}
					}
				} else {
					isUserAuthorizedForTopic = true;
				}
				if (isUserAuthorizedForTopic) {
					discussionBoardForms.add(discussionBoardForm);
				}
			}
		}

		return discussionBoardForms;
	}

	private TimeZoneDTO getTimeZone(Long timeZoneId) {
		TimeZoneMaster timeZoneMaster = timeZoneMasterDAO.findById(timeZoneId);
		TimeZoneDTO timeZoneDTO = new TimeZoneDTO();
		timeZoneDTO.setTimeZoneId(timeZoneId);
		timeZoneDTO.setTimeZoneName(timeZoneMaster.getTimeZoneName());
		timeZoneDTO.setTimeZoneGMTOffset(timeZoneMaster.getGmtOffset());

		return timeZoneDTO;
	}

	@Override
	public DiscussionBoardFormResponse getForumTopicDataById(Long topicId, Long companyId) {
		ForumTopic forumTopic = forumTopicDAO.findById(topicId, companyId);
		
		if(forumTopic == null){
			return null;
		}

		DiscussionBoardFormResponse response = new DiscussionBoardFormResponse();
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		/* ID ENCRYPT */
		discussionBoardForm.setTopicId(FormatPreserveCryptoUtil.encrypt(forumTopic.getTopicId()));
		discussionBoardForm.setTopicName(forumTopic.getTopicName());
		discussionBoardForm.setTopicDescription(forumTopic.getTopicDesc());
		discussionBoardForm.setEmailIds(forumTopic.getEmailTo());
		discussionBoardForm.setEmailCcIds(forumTopic.getEmailCC());
		ForumTopic topicData = getTopicData(companyId, topicId);
		String topicEmployeeList = topicData.getTopicEmployeeIds();
		discussionBoardForm.setEmployeeIds(topicEmployeeList);
		if (forumTopic.isStatus()) {
			discussionBoardForm.setTopicStatus(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_OPEN);
		} else {
			discussionBoardForm.setTopicStatus(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_CLOSED);
		}
		response.setDiscussionBoardForm(discussionBoardForm);

		return response;
	}

	@Override
	public synchronized String addDiscussionBoardTopic(DiscussionBoardForm discussionBoardForm, Long companyId,
			Long empId, String employeeIdsList) {
		Company companyVO = companyDAO.findById(companyId);
		ForumTopic forumTopic = new ForumTopic();
		forumTopic.setCompany(companyVO);
		forumTopic.setTopicName(discussionBoardForm.getTopicName());
		forumTopic.setTopicDesc(discussionBoardForm.getTopicDescription());
		forumTopic.setStatus(true);
		forumTopic.setEmailTo(discussionBoardForm.getEmailIds());
		forumTopic.setEmailCC(discussionBoardForm.getEmailCcIds());
		forumTopic.setTopicEmployeeIds(employeeIdsList);
		Employee employee = employeeDAO.findById(empId);
		forumTopic.setCreatedBy(employee);
		forumTopic.setCreatedDate(DateUtils.getCurrentTimestampWithTime());

		DiscussionBoardConditionDTO discussionBoardConditionDTO = new DiscussionBoardConditionDTO();
		discussionBoardConditionDTO.setCompanyId(companyId);
		discussionBoardConditionDTO.setTopicName(discussionBoardForm.getTopicName().trim());
		List<ForumTopic> forumTopicList = null;
		forumTopicList = forumTopicDAO.findDiscussionBoardTopicsByName(companyId, discussionBoardConditionDTO);

		if (forumTopicList != null && forumTopicList.size() > 0) {
			return PayAsiaConstants.FALSE;
		} else {
			/* ID ENCRYPT */
			ForumTopic forumTopicVO = forumTopicDAO.saveReturn(forumTopic);
			if (forumTopicVO != null) {
				return String.valueOf(FormatPreserveCryptoUtil.encrypt(forumTopicVO.getTopicId()));
			} else {
				return PayAsiaConstants.PAYASIA_ERROR;
			}
		}

	}

	@Override
	public synchronized String updateDiscussionBoardTopic(Long companyId, Long employeeId,
			DiscussionBoardForm discussionBoardForm, String employeeIdsList) {
		DiscussionBoardConditionDTO discussionBoardConditionDTO = new DiscussionBoardConditionDTO();
		discussionBoardConditionDTO.setTopicId(Long.valueOf(discussionBoardForm.getTopicId()));
		discussionBoardConditionDTO.setCompanyId(companyId);
		discussionBoardConditionDTO.setTopicName(discussionBoardForm.getTopicName().trim());
		List<ForumTopic> forumTopicList = null;
		forumTopicList = forumTopicDAO.findDiscussionBoardTopicsByName(companyId, discussionBoardConditionDTO);

		if (forumTopicList != null && forumTopicList.size() > 0) {
			return PayAsiaConstants.FALSE;
		} else {
			ForumTopic forumTopic = forumTopicDAO.findById(discussionBoardForm.getTopicId());
			forumTopic.setTopicName(discussionBoardForm.getTopicName());
			forumTopic.setTopicDesc(discussionBoardForm.getTopicDescription());
			if (PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_OPEN
					.equals(discussionBoardForm.getTopicStatus())) {
				forumTopic.setStatus(true);
			} else {
				forumTopic.setStatus(false);
			}
			forumTopic.setEmailTo(discussionBoardForm.getEmailIds());
			forumTopic.setEmailCC(discussionBoardForm.getEmailCcIds());
			forumTopic.setTopicEmployeeIds(employeeIdsList);
			if (StringUtils.isNotBlank(discussionBoardForm.getTopicStatusRemarks())) {
				saveDiscussionBoardStatusChangeRemarks(discussionBoardForm, employeeId);
			}
			forumTopicDAO.updateForumTopic(forumTopic);
			return PayAsiaConstants.TRUE;
		}
	}

	@Override
	public String deleteDiscussionBoardTopic(Long companyId, Long topicId) {

		if (isSuperAdmin()) {
			ForumTopic forumTopic = forumTopicDAO.findById(topicId, companyId);
			forumTopicDAO.deleteForumTopic(forumTopic);
			return PayAsiaConstants.TRUE;
		} else {
			List<ForumTopicComment> forumTopicCommentVOList = forumTopicDAO.getDiscussionBoardTopicComments(companyId,
					topicId);

			if (forumTopicCommentVOList.size() == 0) {
				ForumTopic forumTopic = forumTopicDAO.findById(topicId);
				if(forumTopic==null){
					return PayAsiaConstants.FALSE;
				}
				forumTopicDAO.deleteForumTopic(forumTopic);
				return PayAsiaConstants.TRUE;
			} else {
				return PayAsiaConstants.FALSE;
			}
		}

	}

	@Override
	public String addTopicComment(DiscussionTopicCommentForm discussionTopicReplyForm, String employeeIdsList,
			Long companyId, Long employeeId) {

		if (StringUtils.isNotBlank(discussionTopicReplyForm.getComment())
				&& discussionTopicReplyForm.getComment().length() > 2000) {
			//return "commentMaxLength";
			return "payasia.discussion.board.topic.comment.max.length";
		}

		if (StringUtils.isNotBlank(discussionTopicReplyForm.getComment())) {
			String commentText = discussionTopicReplyForm.getComment();
			commentText = commentText.replaceAll("&nbsp;", " ");
			if (StringUtils.isBlank(commentText)) {
				//return "commentEmpty";
				return "payasia.discussion.board.topic.comment.empty";
			}
		}

		ForumTopicComment forumTopicComment = new ForumTopicComment();

		Employee employeeVO = employeeDAO.findById(employeeId);
		ForumTopic forumTopic = forumTopicDAO.findById(discussionTopicReplyForm.getTopicId());
		forumTopicComment.setForumTopic(forumTopic);

		String topicCommentData = discussionTopicReplyForm.getEncodedComment();
		topicCommentData = new String(Base64.decodeBase64(topicCommentData.getBytes()));
		try {
			topicCommentData = URLDecoder.decode(topicCommentData, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error(e2.getMessage(), e2);
		}
		forumTopicComment.setComment(topicCommentData);
		forumTopicComment.setSendMail(discussionTopicReplyForm.isSendMail());
		if (discussionTopicReplyForm.isSendMail()) {
			forumTopicComment.setEmailTo(discussionTopicReplyForm.getEmail());
			forumTopicComment.setEmailCC(discussionTopicReplyForm.getEmailCc());
		} else {
			forumTopicComment.setEmailTo("");
			forumTopicComment.setEmailCC("");
		}
		forumTopicComment.setCreatedDate(DateUtils.getCurrentTimestampWithTime());

		forumTopicComment.setCreatedBy(employeeVO);
		forumTopicComment.setPublishedDate(DateUtils.getCurrentTimestampWithTime());
		if (discussionTopicReplyForm.getCommentStatus()
				.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_PUBLISHED)) {
			AppCodeMaster appCodeMaster = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_CATEGORY,
					PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_PUBLISHED);
			forumTopicComment.setPublishedStatus(appCodeMaster);
		} else {
			AppCodeMaster appCodeMaster = appCodeMasterDAO.findByCondition(
					PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_CATEGORY,
					PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT);
			forumTopicComment.setPublishedStatus(appCodeMaster);
		}

		ForumTopicComment forumTopicCommentVO = null;
		forumTopicComment.setTopicCommentEmployeeIds(employeeIdsList);
		if (discussionTopicReplyForm.getTopicCommentId() == null) {
			forumTopicCommentVO = forumTopicCommentDAO.saveReturn(forumTopicComment);
			// forumTopicCommentVO.setTopicCommentId(FormatPreserveCryptoUtil.encrypt(forumTopicCommentVO.getTopicCommentId()));
		} else {
			forumTopicCommentVO = forumTopicCommentDAO.findById(discussionTopicReplyForm.getTopicCommentId());
			/* ID ENCRYPT */
			forumTopicComment
					.setTopicCommentId(FormatPreserveCryptoUtil.encrypt(forumTopicCommentVO.getTopicCommentId()));
			forumTopicCommentVO.setComment(forumTopicComment.getComment());
			forumTopicCommentVO.setSendMail(forumTopicComment.isSendMail());
			forumTopicCommentVO.setEmailTo(forumTopicComment.getEmailTo());
			forumTopicCommentVO.setEmailCC(forumTopicComment.getEmailCC());
			forumTopicCommentVO.setPublishedDate(forumTopicComment.getPublishedDate());
			forumTopicCommentVO.setPublishedStatus(forumTopicComment.getPublishedStatus());
			forumTopicCommentVO.setTopicCommentEmployeeIds(forumTopicComment.getTopicCommentEmployeeIds());
			forumTopicCommentDAO.update(forumTopicCommentVO);
		}

		if (discussionTopicReplyForm.isSendMail() && discussionTopicReplyForm.getCommentStatus()
				.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_PUBLISHED)) {
			Company companyVO = companyDAO.findById(companyId);
			Email email = new Email();
			email.setCompany(companyVO);

			String systemEmailFrom = isSystemMailReq(companyId);
			if (StringUtils.isNotBlank(systemEmailFrom)) {
				email.setEmailFrom(systemEmailFrom);
			} else {
				email.setEmailFrom(employeeVO.getEmail());
			}

			String emailTO = StringUtils.removeEnd(discussionTopicReplyForm.getEmail(), ";");
			email.setEmailTo(emailTO);

			String emailBCC = setPayAsiaUsersEmailBCC(companyId);
			emailBCC = StringUtils.removeEnd(emailBCC, ";");
			email.setEmailBCC(emailBCC);

			String emailCC = StringUtils.removeEnd(discussionTopicReplyForm.getEmailCc(), ";");
			email.setEmailCC(emailCC);
			setEmailBodySubject(email, forumTopicCommentVO);
			email.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			emailDAO.saveReturn(email);

		}

		if (discussionTopicReplyForm.getAttachmentList() != null) {
			for (TopicAttachmentDTO topicAttachmentDTO : discussionTopicReplyForm.getAttachmentList()) {
				if (topicAttachmentDTO.getAttachment() != null) {
					if (topicAttachmentDTO.getAttachment().getSize() > 0) {
						ForumTopicCommentAttachment attachment = new ForumTopicCommentAttachment();
						attachment.setForumTopicComment(forumTopicCommentVO);
						String fileName = topicAttachmentDTO.getAttachment().getOriginalFilename();
						String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
						// Check Duplicate file name
						List<ForumTopicCommentAttachment> forumTopicCommentAttachmentListVO = forumTopicCommentAttachmentDAO
								.findByCondition(companyId, fileName, forumTopicCommentVO.getTopicCommentId());
						if (forumTopicCommentAttachmentListVO != null && !forumTopicCommentAttachmentListVO.isEmpty()) {
							//return "duplicateFileName";
							return "payasia.discussion.board.topic.duplicate.file.name";
						}
						attachment.setFileName(fileName);

						attachment.setFileType(ext);
						ForumTopicCommentAttachment forumTopicCommentAttachmentVO = forumTopicCommentAttachmentDAO
								.saveReturn(attachment);

						// save file to file directory
						FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath,
								rootDirectoryName, companyId, PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD, null,
								null, null, null, PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, forumTopic.getTopicId());

						String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
						/*
						 * String filePath = downloadPath + "/company/" +
						 * companyId + "/" +
						 * PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD +
						 * "/" + forumTopic.getTopicId() + "/";
						 */

						if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
							fileName = topicAttachmentDTO.getAttachment().getOriginalFilename();
							String extn = fileName.substring(fileName.lastIndexOf('.') + 1);
							fileName = forumTopicCommentAttachmentVO.getTopicAttachmentId() + "." + extn;
							awss3LogicImpl.uploadCommonMultipartFile(topicAttachmentDTO.getAttachment(),
									filePath + fileName);
						} else {
							fileName = FileUtils.uploadFile(topicAttachmentDTO.getAttachment(), filePath,
									fileNameSeperator, forumTopicCommentAttachmentVO.getTopicAttachmentId());
						}

					}
				}

			}
		}
		return "TopicCommentId#" + String.valueOf(forumTopicCommentVO.getTopicCommentId());
	}

	private String isSystemMailReq(Long companyId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		String fromAddress = "";
		if (hrisPreferenceVO.isUseSystemMailAsFromAddress()) {
			EmailPreferenceMaster emailPreferenceMaster = emailPreferenceMasterDAO.findByConditionCompany(companyId);
			fromAddress = emailPreferenceMaster.getSystemEmailForSendingEmails();
		}
		return fromAddress;
	}

	private String setPayAsiaUsersEmailBCC(Long companyId) {
		String emailBCC = "";
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO != null && StringUtils.isNotBlank(hrisPreferenceVO.getDiscussionBoardPayAsiaUsers())) {
			String[] empIdArray = hrisPreferenceVO.getDiscussionBoardPayAsiaUsers().split(";");
			for (int count = 0; count < empIdArray.length; count++) {
				if (StringUtils.isNotBlank(empIdArray[count])) {
					Employee employeeObjVO = employeeDAO.findById(Long.valueOf(empIdArray[count]));
					if (employeeObjVO != null && StringUtils.isNotBlank(employeeObjVO.getEmail())) {
						emailBCC += employeeObjVO.getEmail() + ";";
					}
				}
			}
		}
		return emailBCC;
	}

	private void setEmailBodySubject(Email email, ForumTopicComment forumTopicCommentVO) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NAME, getEmployeeName(forumTopicCommentVO.getCreatedBy()));
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_TOPIC, forumTopicCommentVO.getForumTopic().getTopicName());
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_POSTED_DATE,
				com.payasia.common.util.DateUtils.timeStampToString(forumTopicCommentVO.getCreatedDate(),
						forumTopicCommentVO.getForumTopic().getCompany().getDateFormat()));
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMMENT, forumTopicCommentVO.getComment());
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_EMPLOYEE_NUMBER,
				forumTopicCommentVO.getCreatedBy().getEmployeeNumber());

		String topicStatus = "Closed";
		if (forumTopicCommentVO.getForumTopic().isStatus()) {
			topicStatus = "Open";
		}
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_TOPIC_STATUS, topicStatus);
		modelMap.put(PayAsiaConstants.HRIS_EMAIL_COMPANY_NAME,
				forumTopicCommentVO.getForumTopic().getCompany().getCompanyName());

		EmailPreferenceMaster emailPreferenceURL = emailPreferenceMasterDAO
				.findByConditionCompany(forumTopicCommentVO.getForumTopic().getCompany().getCompanyId());
		if (emailPreferenceURL != null) {
			if (StringUtils.isNotBlank(emailPreferenceURL.getCompanyUrl())) {
				String link = "<a href='" + emailPreferenceURL.getCompanyUrl() + "'>"
						+ emailPreferenceURL.getCompanyUrl() + "</a>";
				modelMap.put(PayAsiaConstants.URL, link);
			} else {
				modelMap.put(PayAsiaConstants.URL, "");
			}
		}

		List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList = emailTemplateSubCategoryMasterDAO
				.findAll();
		Long subCategoryId = getSubCategoryId(PayAsiaConstants.PAYASIA_SUB_CATEGORY_DISCUSSION_BOARD,
				EmailTemplateSubCategoryMasterList);
		EmailTemplate emailTemplateVO = emailTemplateDAO.findByConditionSubCategory(subCategoryId,
				forumTopicCommentVO.getForumTopic().getCompany().getCompanyId());

		File emailTemplate = null;
		File emailSubjectTemplate = null;
		FileOutputStream fos = null;
		FileOutputStream fosSubject = null;
		try {
			byte[] mailBodyBytes = emailTemplateVO.getBody().getBytes();
			byte[] mailSubjectBytes = emailTemplateVO.getSubject().getBytes();

			String uniqueId = RandomNumberGenerator.getNDigitRandomNumber(8);

			emailTemplate = new File(PAYASIA_TEMP_PATH + "//" + "sendTopicCommentTemplate"
					+ forumTopicCommentVO.getTopicCommentId() + "_" + uniqueId + ".vm");
			emailSubjectTemplate = new File(PAYASIA_TEMP_PATH + "//" + "sendTopicCommentTemplateSubject"
					+ forumTopicCommentVO.getTopicCommentId() + "_" + uniqueId + ".vm");
			emailSubjectTemplate.deleteOnExit();
			emailTemplate.deleteOnExit();

			try {
				fos = new FileOutputStream(emailTemplate);
				fos.write(mailBodyBytes);
				fos.flush();
				fos.close();
				fosSubject = new FileOutputStream(emailSubjectTemplate);
				fosSubject.write(mailSubjectBytes);
				fosSubject.flush();
				fosSubject.close();

			} catch (FileNotFoundException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			String templateBodyString = emailTemplate.getPath().substring(emailTemplate.getParent().length() + 1);
			String templateSubjectString = emailSubjectTemplate.getPath()
					.substring(emailSubjectTemplate.getParent().length() + 1);

			StringBuilder subjectText = new StringBuilder("");

			subjectText.append(
					VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateSubjectString, modelMap));

			StringBuilder bodyText = new StringBuilder("");

			bodyText.append(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateBodyString, modelMap));

			email.setBody(bodyText.toString());
			email.setSubject(subjectText.toString());

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (emailTemplate != null) {
				emailTemplate.delete();
				emailSubjectTemplate.delete();
			}
		}
	}

	private String getEmployeeName(Employee employee) {
		String employeeName = employee.getFirstName();

		if (StringUtils.isNotBlank(employee.getLastName())) {
			employeeName = employeeName + " " + employee.getLastName();
		}
		return employeeName;
	}

	private Long getSubCategoryId(String subCategoryName,
			List<EmailTemplateSubCategoryMaster> EmailTemplateSubCategoryMasterList) {
		for (EmailTemplateSubCategoryMaster emailTemplateSubCategoryMaster : EmailTemplateSubCategoryMasterList) {
			if ((subCategoryName.toUpperCase())
					.equals(emailTemplateSubCategoryMaster.getSubCategoryName().toUpperCase())) {
				return emailTemplateSubCategoryMaster.getEmailTemplateSubCategoryId();
			}
		}
		return null;
	}

	@Override
	public String deleteTopicComment(Long topicCommentId, Long companyId) {
		boolean success = true;
		ForumTopicComment forumTopicComment = forumTopicCommentDAO.findByForumTopicCommentId(topicCommentId, companyId);
		if(forumTopicComment==null){
			return PayAsiaConstants.FALSE;
		}
		
		List<ForumTopicCommentAttachment> forumTopicCommentAttachmentListVO = forumTopicCommentAttachmentDAO
				.findByCommentId(companyId, topicCommentId);
		for (ForumTopicCommentAttachment forumTopicCommentAttachmentVO : forumTopicCommentAttachmentListVO) {
			try {
				String fileExt = forumTopicCommentAttachmentVO.getFileType();

				FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
						companyId, PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD,
						String.valueOf(forumTopicCommentAttachmentVO.getTopicAttachmentId()), null, null, fileExt,
						PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT, forumTopicComment.getForumTopic().getTopicId());

				String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
				/*
				 * String filePath = "/company/" + companyId + "/" +
				 * PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD + "/" +
				 * forumTopicComment.getForumTopic().getTopicId() + "/" +
				 * forumTopicCommentAttachmentVO.getTopicAttachmentId() + "." +
				 * fileExt;
				 */
				if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
					List<String> fileList = new ArrayList<String>();
					fileList.add(filePath);
					awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
				} else {
					FileUtils.deletefile(filePath);
				}
			} catch (Exception exception) {
				success = false;
				LOGGER.error(exception.getMessage(), exception);
				throw new PayAsiaSystemException(exception.getMessage(), exception);
			}
		}

		if (success == true) {
			// delete All attachment by topicCommentId
			forumTopicCommentAttachmentDAO.deleteByCondition(topicCommentId);
			forumTopicCommentDAO.delete(forumTopicComment);
			return PayAsiaConstants.TRUE;
		}
		return PayAsiaConstants.FALSE;

	}

	@Override
	public String updateTopicComment(DiscussionTopicCommentForm discussionTopicReplyForm, Long companyId,
			Long employeeId, String employeeIdsList) {

		if (StringUtils.isNotBlank(discussionTopicReplyForm.getComment())
				&& discussionTopicReplyForm.getComment().length() > 2000) {
			//return "commentMaxLength";
			return "payasia.discussion.board.topic.comment.max.length";
		}
		if (StringUtils.isNotBlank(discussionTopicReplyForm.getComment())) {
			String commentText = discussionTopicReplyForm.getComment();
			commentText = commentText.replaceAll("&nbsp;", " ");
			if (StringUtils.isBlank(commentText.trim())) {
				//return "commentEmpty";
				return "payasia.discussion.board.topic.comment.empty";
			}
		}

		ForumTopicComment forumTopicComment = forumTopicCommentDAO
				.findById(discussionTopicReplyForm.getTopicCommentId());

		String topicCommentData = discussionTopicReplyForm.getEncodedComment();
		topicCommentData = new String(Base64.decodeBase64(topicCommentData.getBytes()));
		try {
			topicCommentData = URLDecoder.decode(topicCommentData, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			LOGGER.error(e2.getMessage(), e2);
		}
		forumTopicComment.setComment(topicCommentData);
		forumTopicComment.setTopicCommentEmployeeIds(employeeIdsList);
		forumTopicComment.setSendMail(discussionTopicReplyForm.isSendMail());
		if (discussionTopicReplyForm.isSendMail()) {
			forumTopicComment.setEmailTo(discussionTopicReplyForm.getEmail());
			forumTopicComment.setEmailCC(discussionTopicReplyForm.getEmailCc());
		} else {
			forumTopicComment.setEmailTo("");
			forumTopicComment.setEmailCC("");
		}
		if (employeeId.equals(forumTopicComment.getCreatedBy().getEmployeeId())) {
			forumTopicComment.setPublishedDate(DateUtils.getCurrentTimestampWithTime());
			if (discussionTopicReplyForm.getCommentStatus()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_PUBLISHED)) {
				AppCodeMaster appCodeMaster = appCodeMasterDAO.findByCondition(
						PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_CATEGORY,
						PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_PUBLISHED);
				forumTopicComment.setPublishedStatus(appCodeMaster);
			} else {
				AppCodeMaster appCodeMaster = appCodeMasterDAO.findByCondition(
						PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_CATEGORY,
						PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT);
				forumTopicComment.setPublishedStatus(appCodeMaster);
			}
		}

		forumTopicCommentDAO.update(forumTopicComment);

		if (discussionTopicReplyForm.isSendMail() && discussionTopicReplyForm.getCommentStatus()
				.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_PUBLISHED)) {
			Company companyVO = companyDAO.findById(companyId);
			Email email = new Email();
			email.setCompany(companyVO);

			String systemEmailFrom = isSystemMailReq(companyId);
			if (StringUtils.isNotBlank(systemEmailFrom)) {
				email.setEmailFrom(systemEmailFrom);
			} else {
				email.setEmailFrom(forumTopicComment.getCreatedBy().getEmail());
			}

			String emailTO = StringUtils.removeEnd(discussionTopicReplyForm.getEmail(), ";");
			email.setEmailTo(emailTO);

			String emailBCC = setPayAsiaUsersEmailBCC(companyId);
			emailBCC = StringUtils.removeEnd(emailBCC, ";");
			email.setEmailBCC(emailBCC);

			String emailCC = StringUtils.removeEnd(discussionTopicReplyForm.getEmailCc(), ";");
			email.setEmailCC(emailCC);
			setEmailBodySubject(email, forumTopicComment);
			email.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
			emailDAO.saveReturn(email);

		}

		if (discussionTopicReplyForm.getAttachmentList() != null) {
			for (TopicAttachmentDTO topicAttachmentDTO : discussionTopicReplyForm.getAttachmentList()) {

				if (topicAttachmentDTO.getAttachment() != null) {
					if (topicAttachmentDTO.getAttachment().getSize() > 0) {
						ForumTopicCommentAttachment attachment = new ForumTopicCommentAttachment();
						attachment.setForumTopicComment(forumTopicComment);
						String fileName = topicAttachmentDTO.getAttachment().getOriginalFilename();
						String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
						attachment.setFileName(fileName);
						// Check Duplicate file name
						List<ForumTopicCommentAttachment> forumTopicCommentAttachmentListVO = forumTopicCommentAttachmentDAO
								.findByCondition(companyId, fileName, forumTopicComment.getTopicCommentId());
						if (forumTopicCommentAttachmentListVO != null && !forumTopicCommentAttachmentListVO.isEmpty()) {
							//return "duplicateFileName";
							return "payasia.discussion.board.topic.duplicate.file.name";
						}

						attachment.setFileType(ext);
						ForumTopicCommentAttachment forumTopicCommentAttachmentVO = forumTopicCommentAttachmentDAO
								.saveReturn(attachment);

						// save file to file directory
						FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath,
								rootDirectoryName, companyId, PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD, null,
								null, null, null, PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT,
								forumTopicComment.getForumTopic().getTopicId());
						String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
						/*
						 * String filePath = downloadPath + "/company/" +
						 * companyId + "/" + PayAsiaConstants.
						 * COMPANY_DOCUMENT_DISCUSSION_BOARD + "/" +
						 * forumTopicComment.getForumTopic() .getTopicId() +
						 * "/";
						 */

						if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
							String fileNameNew = topicAttachmentDTO.getAttachment().getOriginalFilename();
							String extn = fileNameNew.substring(fileNameNew.lastIndexOf('.') + 1);

							if (!("").equals(fileNameNew)) {
								fileName = forumTopicCommentAttachmentVO.getTopicAttachmentId() + "." + extn;
							}
							awss3LogicImpl.uploadCommonMultipartFile(topicAttachmentDTO.getAttachment(),
									filePath + fileName);
						} else {
							fileName = FileUtils.uploadFile(topicAttachmentDTO.getAttachment(), filePath,
									fileNameSeperator, forumTopicCommentAttachmentVO.getTopicAttachmentId());
						}
					}
				}
			}
		}
		return PayAsiaConstants.PAYASIA_SUCCESS;

	}

	@Override
	public DiscussionTopicCommentForm getTopicCommentData(long topicCommentId) {

		ForumTopicComment forumTopicComment = forumTopicCommentDAO.findById(topicCommentId);

		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		discussionTopicReplyForm.setTopicCommentId(forumTopicComment.getTopicCommentId());
		discussionTopicReplyForm.setTopicId(forumTopicComment.getForumTopic().getTopicId());
		discussionTopicReplyForm.setComment(forumTopicComment.getComment());
		discussionTopicReplyForm.setTopicEmployeeIds(forumTopicComment.getTopicCommentEmployeeIds());
		if (StringUtils.isBlank(forumTopicComment.getEmailTo())) {
			discussionTopicReplyForm.setEmail(getTopicDefaultEmail(
					forumTopicComment.getForumTopic().getCompany().getCompanyId(),
					forumTopicComment.getForumTopic().getTopicId(), forumTopicComment.getCreatedBy().getEmployeeId()));
		} else {
			discussionTopicReplyForm.setEmail(forumTopicComment.getEmailTo());
		}

		if (StringUtils.isBlank(forumTopicComment.getEmailCC())) {
			discussionTopicReplyForm.setEmailCc(getTopicDefaultEmailCC(
					forumTopicComment.getForumTopic().getCompany().getCompanyId(),
					forumTopicComment.getForumTopic().getTopicId(), forumTopicComment.getCreatedBy().getEmployeeId()));
		} else {
			discussionTopicReplyForm.setEmailCc(forumTopicComment.getEmailCC());
		}
		discussionTopicReplyForm.setSendMail(forumTopicComment.isSendMail());

		List<TopicAttachmentDTO> attachmentList = new ArrayList<TopicAttachmentDTO>();
		for (ForumTopicCommentAttachment attachment : forumTopicComment.getForumTopicAttachments()) {
			TopicAttachmentDTO attachmentDTO = new TopicAttachmentDTO();
			attachmentDTO.setEmailAttachmentId(String.valueOf(attachment.getTopicAttachmentId()));
			attachmentDTO.setAttachmentName(attachment.getFileName());
			attachmentList.add(attachmentDTO);

		}
		discussionTopicReplyForm.setAttachmentList(attachmentList);
		return discussionTopicReplyForm;
	}

	@Override
	public EmailAttachmentDTO viewAttachment(Long attachmentId, Long companyId, Long employeeId) {

		EmailAttachmentDTO attachmentDTO = new EmailAttachmentDTO();
		ForumTopicCommentAttachment forumTopicCommentAttachmentVO = forumTopicCommentAttachmentDAO
				.findById(attachmentId, companyId);
		if(forumTopicCommentAttachmentVO == null) {
			return null;
		}
		attachmentDTO.setAttachmentName(forumTopicCommentAttachmentVO.getFileName());
		String fileExt = forumTopicCommentAttachmentVO.getFileType();
		/*
		 * String filePath = "/company/" + companyId + "/" +
		 * PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD + "/" +
		 * forumTopicCommentAttachmentVO.getForumTopicComment()
		 * .getForumTopic().getTopicId() + "/" +
		 * forumTopicCommentAttachmentVO.getTopicAttachmentId() + "." + fileExt;
		 */
		String fileName = String.valueOf(forumTopicCommentAttachmentVO.getTopicAttachmentId());
		FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName, companyId,
				PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD, fileName, null, null, fileExt,
				PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT,
				forumTopicCommentAttachmentVO.getForumTopicComment().getForumTopic().getTopicId());
		String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
		attachmentDTO.setAttachmentPath(filePath);
		return attachmentDTO;
	}

	@Override
	public DiscussionTopicCommentForm viewSeletecdAttachments(String attachmentIds, Long companyId, Long employeeId) {
		String[] attachmentIdArray = null;
		List<String> attachmentIdList = new ArrayList<String>();
		if (StringUtils.isNotBlank(attachmentIds)) {
			try {
				attachmentIds = URLDecoder.decode(attachmentIds, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
			attachmentIdArray = attachmentIds.split(",");
			for (String attachment : attachmentIdArray) {
				if (StringUtils.isNotBlank(attachment)) {
					attachmentIdList.add(attachment);
				}
			}
		}

		DiscussionTopicCommentForm response = new DiscussionTopicCommentForm();
		List<TopicAttachmentDTO> emailAttachmentDTOList = new ArrayList<TopicAttachmentDTO>();
		List<ForumTopicCommentAttachment> forumTopicCommentAttachmentListVO = forumTopicCommentAttachmentDAO
				.findByAttachmentId(companyId, attachmentIdList);
		for (ForumTopicCommentAttachment forumTopicCommentAttachmentVO : forumTopicCommentAttachmentListVO) {
			response.setAttachmentName(
					forumTopicCommentAttachmentVO.getForumTopicComment().getForumTopic().getTopicName());
			TopicAttachmentDTO attachmentDTO = new TopicAttachmentDTO();
			attachmentDTO.setAttachmentName(forumTopicCommentAttachmentVO.getFileName());
			String fileExt = forumTopicCommentAttachmentVO.getFileType();
			String fileName = String.valueOf(forumTopicCommentAttachmentVO.getTopicAttachmentId());
			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyId, PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD, fileName, null, null, fileExt,
					PayAsiaConstants.PAYASIA_DOC_VIEW_EVENT,
					forumTopicCommentAttachmentVO.getForumTopicComment().getForumTopic().getTopicId());
			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * String filePath = "/company/" + companyId + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD + "/" +
			 * forumTopicCommentAttachmentVO.getForumTopicComment()
			 * .getForumTopic().getTopicId() + "/" +
			 * forumTopicCommentAttachmentVO.getTopicAttachmentId() + "." +
			 * fileExt;
			 */
			attachmentDTO.setAttachmentPath(filePath);
			emailAttachmentDTOList.add(attachmentDTO);
		}
		response.setAttachmentList(emailAttachmentDTOList);
		return response;
	}

	@Override
	public String deleteAttachment(long attachmentId, Long companyId) {
		ForumTopicCommentAttachment forumTopicCommentAttachmentVO = forumTopicCommentAttachmentDAO
				.findById(attachmentId);
		boolean success = true;
		try {
			String fileExt = forumTopicCommentAttachmentVO.getFileType();

			FilePathGeneratorDTO filePathGenerator = fileUtils.getFileCommonPath(downloadPath, rootDirectoryName,
					companyId, PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD,
					String.valueOf(forumTopicCommentAttachmentVO.getTopicAttachmentId()), null, null, fileExt,
					PayAsiaConstants.PAYASIA_DOC_DELETE_EVENT,
					forumTopicCommentAttachmentVO.getForumTopicComment().getForumTopic().getTopicId());

			String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);
			/*
			 * String filePath = "/company/" + companyId + "/" +
			 * PayAsiaConstants.COMPANY_DOCUMENT_DISCUSSION_BOARD + "/" +
			 * forumTopicCommentAttachmentVO.getForumTopicComment()
			 * .getForumTopic().getTopicId() + "/" +
			 * forumTopicCommentAttachmentVO.getTopicAttachmentId() + "." +
			 * fileExt;
			 */
			if (PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION.equalsIgnoreCase(appDeployLocation)) {
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				awss3LogicImpl.deleteMultiObjectNonVersioned(fileList);
			} else {
				FileUtils.deletefile(filePath);
			}
		} catch (Exception exception) {
			success = false;
			LOGGER.error(exception.getMessage(), exception);
			throw new PayAsiaSystemException(exception.getMessage(), exception);
		}

		if (success == true) {
			// delete attachment
			forumTopicCommentAttachmentDAO.delete(forumTopicCommentAttachmentVO);
			return PayAsiaConstants.TRUE;
		}
		return PayAsiaConstants.FALSE;
	}

	/**
	 * Comparator Class for Ordering ForumTopicCommentComp List
	 */
	private class ForumTopicCommentComp implements Comparator<ForumTopicComment> {
		public int compare(ForumTopicComment templateField, ForumTopicComment compWithTemplateField) {
			return templateField.getPublishedDate().compareTo(compWithTemplateField.getPublishedDate());
		}
	}

	@Override
	public DiscussionTopicCommentResponseForm getTopicCommentList(long topicId, Long companyId, Long employeeId) {
		DiscussionTopicCommentResponseForm responseForm = new DiscussionTopicCommentResponseForm();

		ForumTopic forumTopicVO = forumTopicDAO.findById(topicId, companyId);
		/* ID ENCRYPT */
		responseForm.setTopicId(FormatPreserveCryptoUtil.encrypt(forumTopicVO.getTopicId()));
		responseForm.setTopicName(forumTopicVO.getTopicName());
		responseForm.setTopicDesc(forumTopicVO.getTopicDesc());
		responseForm.setStatus(forumTopicVO.isStatus());
		Employee employeeVO = employeeDAO.findById(employeeId);
		responseForm.setGeneratedBy(getEmployeeName(employeeVO));

		if (isSuperAdmin() || (forumTopicVO.getCreatedBy().getEmployeeId() == employeeId)) {
			responseForm.setTopicEditable(true);
		} else {
			responseForm.setTopicEditable(false);
		}
		if (isSuperAdmin() || (forumTopicVO.getCreatedBy().getEmployeeId() == employeeId)
				|| employeeVO.getCompany().getCompanyName().equalsIgnoreCase(PayAsiaConstants.PAYASIA_COMPANY_NAME)) {
			responseForm.setTopicVisibilityEditable(true);
		} else {
			responseForm.setTopicVisibilityEditable(false);
		}

		List<DiscussionTopicCommentForm> topicCommentFormList = getCommentList(topicId, companyId, employeeId,
				forumTopicVO);

		responseForm.setDiscussionTopicCommentFormList(topicCommentFormList);

		return responseForm;
	}

	private List<DiscussionTopicCommentForm> getCommentList(long topicId, Long companyId, Long employeeId,
			ForumTopic forumTopicVO) {

		List<DiscussionTopicCommentForm> topicCommentFormList = new ArrayList<DiscussionTopicCommentForm>();

		List<ForumTopicComment> forumTopicCommentList = forumTopicCommentDAO
				.getTopicCommentList(forumTopicVO.getTopicId(), companyId);
		for (ForumTopicComment forumTopicCommentVO : forumTopicCommentList) {
			if (forumTopicCommentVO.getCreatedBy().getEmployeeId() != employeeId
					&& forumTopicCommentVO.getPublishedStatus().getCodeValue()
							.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT)) {
				continue;
			}
			DiscussionTopicCommentForm commentForm = new DiscussionTopicCommentForm();
			commentForm.setComment(forumTopicCommentVO.getComment());
			commentForm.setEmail(forumTopicCommentVO.getEmailTo());
			commentForm.setEmailCc(forumTopicCommentVO.getEmailCC());
			commentForm.setSendMail(forumTopicCommentVO.isSendMail());
			commentForm.setTopicCommentId(FormatPreserveCryptoUtil.encrypt(forumTopicCommentVO.getTopicCommentId()));
			if (isSuperAdmin() || (forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId
					&& forumTopicCommentVO.getPublishedStatus().getCodeValue()
							.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT))) {
				commentForm.setCommentEditable(true);
			} else {
				commentForm.setCommentEditable(false);
			}
			commentForm.setCreatedById(forumTopicCommentVO.getCreatedBy().getEmployeeId());
			commentForm.setCreatedByEmpNumber(forumTopicCommentVO.getCreatedBy().getEmployeeNumber());
			commentForm.setCreatedByName(getEmployeeName(forumTopicCommentVO.getCreatedBy()));
			commentForm.setCreatedDate(DateUtils.timeStampToStringWithTime(forumTopicCommentVO.getPublishedDate(),
					forumTopicCommentVO.getForumTopic().getCompany().getDateFormat()));
			
			commentForm.setAttachmentListSize(forumTopicCommentVO.getForumTopicAttachments()==null ? 0 : forumTopicCommentVO.getForumTopicAttachments().size());
			
			/*
			 * if (forumTopicCommentVO.getTopicCommentEmployeeIds() != null &&
			 * forumTopicCommentVO.getTopicCommentEmployeeIds() != "" &&
			 * !forumTopicCommentVO.getTopicCommentEmployeeIds() .isEmpty()) {
			 * String arrTopicEmployees[] = forumTopicCommentVO
			 * .getTopicCommentEmployeeIds().split(","); List<String> empList =
			 * Arrays.asList(arrTopicEmployees); if
			 * (empList.contains(String.valueOf(employeeId)) ||
			 * forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId)
			 * { topicCommentFormList.add(commentForm); } } else { //
			 * HRISPreference hrisPreference = hrisPreferenceDAO //
			 * .findByCompanyId(forumTopicVO.getCompany() // .getCompanyId());
			 * // boolean isUserAuthorizedForTopic = false; // if
			 * ((hrisPreference.getDiscussionBoardPayAsiaUsers() != null // &&
			 * hrisPreference.getDiscussionBoardPayAsiaUsers() != "" &&
			 * !hrisPreference // .getDiscussionBoardPayAsiaUsers().isEmpty()))
			 * { // String arrTopicEmployees[] = hrisPreference //
			 * .getDiscussionBoardPayAsiaUsers().split(";"); // List<String>
			 * empList = Arrays.asList(arrTopicEmployees); // if
			 * (empList.contains(String.valueOf(employeeId)) // ||
			 * forumTopicCommentVO.getCreatedBy() // .getEmployeeId() ==
			 * employeeId) { // isUserAuthorizedForTopic = true; // } // } // if
			 * (isUserAuthorizedForTopic) { //
			 * topicCommentFormList.add(commentForm); // }
			 * 
			 * // topicCommentFormList.add(commentForm); }
			 */

			HRISPreference hrisPreference = hrisPreferenceDAO.findByCompanyId(companyId);
			boolean isUserAuthorizedForTopicComment = false;
			if (hrisPreference != null) {
				if (hrisPreference.isEnableVisibility() != null && hrisPreference.isEnableVisibility()) {
					if (employeeId.equals(forumTopicCommentVO.getCreatedBy().getEmployeeId())) {
						isUserAuthorizedForTopicComment = true;
					} else if ((hrisPreference.getDiscussionBoardPayAsiaUsers() != null
							&& hrisPreference.getDiscussionBoardPayAsiaUsers() != ""
							&& !hrisPreference.getDiscussionBoardPayAsiaUsers().isEmpty())) {
						String arrTopicEmployees[] = hrisPreference.getDiscussionBoardPayAsiaUsers().split(";");
						List<String> empList = Arrays.asList(arrTopicEmployees);
						if (empList.contains(String.valueOf(employeeId))
								|| forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId) {
							isUserAuthorizedForTopicComment = true;
						}
					}
					if (!isUserAuthorizedForTopicComment && (forumTopicCommentVO.getTopicCommentEmployeeIds() != null
							&& forumTopicCommentVO.getTopicCommentEmployeeIds() != ""
							&& !forumTopicCommentVO.getTopicCommentEmployeeIds().isEmpty())) {
						String arrTopicEmployees[] = forumTopicCommentVO.getTopicCommentEmployeeIds().split(",");
						List<String> empList = Arrays.asList(arrTopicEmployees);
						if (empList.contains(String.valueOf(employeeId))
								|| forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId) {
							isUserAuthorizedForTopicComment = true;
						}
					}
				} else {
					isUserAuthorizedForTopicComment = true;
				}

				if (isUserAuthorizedForTopicComment) {
					topicCommentFormList.add(commentForm);
				}
			}
		}
		return topicCommentFormList;
	}

	@Override
	public DiscussionTopicCommentResponseForm getTopicCommentAttachmentList(Long topicCommentId, Long companyId,
			Long employeeId) {
		DiscussionTopicCommentResponseForm responseForm = new DiscussionTopicCommentResponseForm();
		List<DiscussionTopicCommentForm> topicCommentFormList = new ArrayList<DiscussionTopicCommentForm>();

		List<ForumTopicCommentAttachment> forumTopicCommentAttachmentListVO = forumTopicCommentAttachmentDAO
				.findByCommentId(companyId, topicCommentId);
		for (ForumTopicCommentAttachment forumTopicCommentAttachmentVO : forumTopicCommentAttachmentListVO) {
			DiscussionTopicCommentForm commentForm = new DiscussionTopicCommentForm();
			commentForm.setAttachmentName(forumTopicCommentAttachmentVO.getFileName());
			commentForm.setAttachmentId(forumTopicCommentAttachmentVO.getTopicAttachmentId());
			topicCommentFormList.add(commentForm);
		}
		responseForm.setDiscussionTopicCommentFormList(topicCommentFormList);
		return responseForm;
	}

	private class TopicCommentIdComparator implements Comparator<ForumTopicComment> {
		public int compare(ForumTopicComment templateField, ForumTopicComment compWithTemplateField) {
			String x1 = ((ForumTopicComment) templateField).getPublishedStatus().getCodeValue();
			String x2 = ((ForumTopicComment) compWithTemplateField).getPublishedStatus().getCodeValue();
			int sComp = x1.compareTo(x2);

			if (sComp != 0) {
				return sComp;
			} else {
				Timestamp x3 = ((ForumTopicComment) templateField).getPublishedDate();
				Timestamp x4 = ((ForumTopicComment) compWithTemplateField).getPublishedDate();
				if (x3.compareTo(x4) > 0) {
					return -1;
				} else if (x3.compareTo(x4) == 0) {
					return 0;
				} else {
					return 1;
				}
			}
		}

	}

	@Override
	public String getTopicDefaultEmail(Long companyId, Long topicId, Long employeeId) {
		ForumTopic forumTopicVO = forumTopicDAO.findById(topicId, companyId);
		return forumTopicVO.getEmailTo();
	}

	@Override
	public String getTopicDefaultEmailCC(Long companyId, Long topicId, Long employeeId) {
		ForumTopic forumTopicVO = forumTopicDAO.findById(topicId, companyId);
		return forumTopicVO.getEmailCC();
	}

	public boolean isSuperAdmin() {
		ArrayList<String> roleList = new ArrayList<>();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
			if (grantedAuthority.getAuthority().equals("ROLE_SUPER ADMIN")) {
				roleList.add("ROLE_ADMIN");
			}
		}
		if (roleList.contains("ROLE_ADMIN")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDisscusBoardDefaultEmail(Long companyId, Long employeeId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO != null && StringUtils.isNotBlank(hrisPreferenceVO.getDiscussionBoardDefaultEmailTo())) {
			return hrisPreferenceVO.getDiscussionBoardDefaultEmailTo();
		}
		return "";
	}

	@Override
	public String getDisscusBoardDefaultEmailCC(Long companyId, Long employeeId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO != null && StringUtils.isNotBlank(hrisPreferenceVO.getDiscussionBoardDefaultEmailCC())) {
			return hrisPreferenceVO.getDiscussionBoardDefaultEmailCC();
		}
		return "";
	}

	@Override
	public String getEnableVisibility(Long companyId) {
		HRISPreference hrisPreferenceVO = hrisPreferenceDAO.findByCompanyId(companyId);
		if (hrisPreferenceVO != null) {
			if (hrisPreferenceVO.isEnableVisibility() != null && hrisPreferenceVO.isEnableVisibility()) {
				return PayAsiaConstants.PAYASIA_REQUIRED_YES;
			} else {
				return PayAsiaConstants.PAYASIA_REQUIRED_NO;
			}
		}
		return "";
	}

	@Override
	public DiscussionBoardFormResponse getDiscussionBoardTopicStatusHistory(Long employeeId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO, Long topicId) {
		DiscussionBoardConditionDTO conditionDTO = new DiscussionBoardConditionDTO();
		conditionDTO.setEmployeeId(employeeId);
		conditionDTO.setCompanyId(companyId);

		if (topicId != null && topicId != 0L) {
			conditionDTO.setTopicId(topicId);
		}

		List<ForumTopicStatusChangeHistory> forumTopicStatusChangeHistoryList = null;
		forumTopicStatusChangeHistoryList = forumTopicStatusChangeHistoryDAO
				.getDiscussionBoardTopicStatusHistory(employeeId, companyId, sortDTO, conditionDTO);

		List<DiscussionBoardForm> discussionBoardForms = new ArrayList<DiscussionBoardForm>();

		for (ForumTopicStatusChangeHistory forumTopicStatusChangeHistory : forumTopicStatusChangeHistoryList) {

			DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();

			discussionBoardForm.setStatusChangedBy(forumTopicStatusChangeHistory.getCreatedBy().getFirstName() + " "
					+ forumTopicStatusChangeHistory.getCreatedBy().getLastName());

			discussionBoardForm.setTopicStatusRemarks(forumTopicStatusChangeHistory.getRemarks());

			if (forumTopicStatusChangeHistory.isChangedStatus()) {
				discussionBoardForm.setTopicStatus(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_OPEN);
			} else {
				discussionBoardForm.setTopicStatus(PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_CLOSED);
			}

			discussionBoardForm.setCreatedOn(DateUtils.timeStampToStringWithTime2(
					forumTopicStatusChangeHistory.getCreatedDate(), companyDAO.findById(companyId)));

			discussionBoardForms.add(discussionBoardForm);
		}

		DiscussionBoardFormResponse response = new DiscussionBoardFormResponse();

		if (pageDTO != null) {
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;
			int recordSizeFinal = discussionBoardForms.size();
			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal){
				endPos = startPos + pageSize;
			} else if (startPos <= recordSizeFinal) {
				endPos = recordSizeFinal;
			}
			discussionBoardForms = discussionBoardForms.subList(startPos, endPos);
			response.setDiscussionBoardFormList(discussionBoardForms);
			response.setTotal(totalPages);
			response.setRecords(recordSizeFinal);
			response.setPage(pageDTO.getPageNumber());
		}
		return response;
	}

	public void saveDiscussionBoardStatusChangeRemarks(DiscussionBoardForm discussionBoardForm, Long employeeId) {
		ForumTopic forumTopic = forumTopicDAO.findById(discussionBoardForm.getTopicId());
		ForumTopicStatusChangeHistory forumTopicStatusChangeHistory = new ForumTopicStatusChangeHistory();
		if (PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_OPEN.equals(discussionBoardForm.getTopicStatus())) {
			forumTopicStatusChangeHistory.setChangedStatus(true);
		} else {
			forumTopicStatusChangeHistory.setChangedStatus(false);
		}
		forumTopicStatusChangeHistory.setRemarks(discussionBoardForm.getTopicStatusRemarks());
		forumTopicStatusChangeHistory.setForumTopic(forumTopic);
		Employee employee = employeeDAO.findById(employeeId);
		forumTopicStatusChangeHistory.setCreatedBy(employee);
		forumTopicStatusChangeHistory.setCreatedDate(DateUtils.getCurrentTimestampWithTime());
		forumTopicStatusChangeHistoryDAO.save(forumTopicStatusChangeHistory);
	}

	@Override
	public DiscussionTopicCommentResponseForm downloadDiscussionBoardTopicComments(long topicId, Long companyId,
			Long employeeId) {
		DiscussionTopicCommentResponseForm responseForm = new DiscussionTopicCommentResponseForm();

		ForumTopic forumTopicVO = forumTopicDAO.findById(topicId, companyId);
		responseForm.setTopicId(forumTopicVO.getTopicId());
		responseForm.setTopicName(forumTopicVO.getTopicName());
		responseForm.setTopicDesc(forumTopicVO.getTopicDesc());
		responseForm.setStatus(forumTopicVO.isStatus());
		Employee employeeVO = employeeDAO.findById(employeeId);
		responseForm.setGeneratedBy(getEmployeeName(employeeVO));

		if (isSuperAdmin() || (forumTopicVO.getCreatedBy().getEmployeeId() == employeeId)) {
			responseForm.setTopicEditable(true);
		} else {
			responseForm.setTopicEditable(false);
		}

		List<DiscussionTopicCommentForm> topicCommentFormList = new ArrayList<DiscussionTopicCommentForm>();

		List<ForumTopicComment> forumTopicCommentList = forumTopicCommentDAO
				.getTopicCommentListAscOrder(forumTopicVO.getTopicId(), companyId);
		// Collections.sort(forumTopicCommentList, new ForumTopicCommentComp());
		for (ForumTopicComment forumTopicCommentVO : forumTopicCommentList) {
			if (forumTopicCommentVO.getPublishedStatus().getCodeValue()
					.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT)) {
				continue;
			}
			DiscussionTopicCommentForm commentForm = new DiscussionTopicCommentForm();
			String plainText = forumTopicCommentVO.getComment().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
			plainText = plainText.replaceAll("&nbsp;", " ").replaceAll("&quot;", "\"").replaceAll("&#39;", "'");
			commentForm.setComment(plainText);
			commentForm.setEmail(forumTopicCommentVO.getEmailTo());
			commentForm.setEmailCc(forumTopicCommentVO.getEmailCC());
			commentForm.setSendMail(forumTopicCommentVO.isSendMail());
			commentForm.setTopicCommentId(forumTopicCommentVO.getTopicCommentId());
			if (isSuperAdmin() || (forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId
					&& forumTopicCommentVO.getPublishedStatus().getCodeValue()
							.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT))) {
				commentForm.setCommentEditable(true);
			} else {
				commentForm.setCommentEditable(false);
			}
			commentForm.setCreatedById(forumTopicCommentVO.getCreatedBy().getEmployeeId());
			commentForm.setCreatedByEmpNumber(forumTopicCommentVO.getCreatedBy().getEmployeeNumber());
			commentForm.setCreatedByName(getEmployeeName(forumTopicCommentVO.getCreatedBy()));
			commentForm.setCreatedDate(DateUtils.timeStampToStringWithTime(forumTopicCommentVO.getPublishedDate(),
					forumTopicCommentVO.getForumTopic().getCompany().getDateFormat()));

			topicCommentFormList.add(commentForm);
		}

		responseForm.setDiscussionTopicCommentFormList(topicCommentFormList);

		return responseForm;
	}

	public List<DiscussionTopicCommentResponseForm> downloadMultipleTopicComments(String topicIds, Long companyId,
			Long employeeId) {
		List<DiscussionTopicCommentResponseForm> commentForms = new ArrayList<>();
		List<Long> topicIdList = new ArrayList<Long>();
		if (StringUtils.isNotBlank(topicIds)) {
			String[] topicIdArray = topicIds.split(",");
			for (String topicId : topicIdArray) {
				if (StringUtils.isNotBlank(topicId)) {
					topicIdList.add(FormatPreserveCryptoUtil.decrypt(Long.valueOf(topicId)));
				}
			}
		}
		Employee employeeVO = employeeDAO.findById(employeeId);

		List<ForumTopic> forumTopicListVO = forumTopicDAO.getMultipleTopicList(companyId, topicIdList);

		List<ForumTopic> sortedForumTopicListVO = new ArrayList<ForumTopic>();
		for (long topicId : topicIdList) {
			for (ForumTopic forumTopic : forumTopicListVO) {
				if (forumTopic.getTopicId() == topicId) {
					sortedForumTopicListVO.add(forumTopic);
					break;
				}
			}
		}

		for (ForumTopic forumTopicVO : sortedForumTopicListVO) {
			DiscussionTopicCommentResponseForm responseForm = new DiscussionTopicCommentResponseForm();

			responseForm.setTopicId(forumTopicVO.getTopicId());
			responseForm.setTopicName(forumTopicVO.getTopicName());
			responseForm.setTopicDesc(forumTopicVO.getTopicDesc());
			responseForm.setStatus(forumTopicVO.isStatus());

			responseForm.setGeneratedBy(getEmployeeName(employeeVO));

			if (isSuperAdmin() || (forumTopicVO.getCreatedBy().getEmployeeId() == employeeId)) {
				responseForm.setTopicEditable(true);
			} else {
				responseForm.setTopicEditable(false);
			}

			List<DiscussionTopicCommentForm> topicCommentFormList = new ArrayList<DiscussionTopicCommentForm>();

			List<ForumTopicComment> forumTopicCommentList = forumTopicCommentDAO
					.getTopicCommentListAscOrder(forumTopicVO.getTopicId(), companyId);
			// Collections.sort(forumTopicCommentList, new
			// ForumTopicCommentComp());
			for (ForumTopicComment forumTopicCommentVO : forumTopicCommentList) {
				if (forumTopicCommentVO.getPublishedStatus().getCodeValue()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT)) {
					continue;
				}
				DiscussionTopicCommentForm commentForm = new DiscussionTopicCommentForm();
				String plainText = forumTopicCommentVO.getComment().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
				plainText = plainText.replaceAll("&nbsp;", " ").replaceAll("&quot;", "\"").replaceAll("&#39;", "'");
				commentForm.setComment(plainText);
				commentForm.setEmail(forumTopicCommentVO.getEmailTo());
				commentForm.setEmailCc(forumTopicCommentVO.getEmailCC());
				commentForm.setSendMail(forumTopicCommentVO.isSendMail());
				commentForm.setTopicCommentId(forumTopicCommentVO.getTopicCommentId());
				if (isSuperAdmin() || (forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId
						&& forumTopicCommentVO.getPublishedStatus().getCodeValue()
								.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT))) {
					commentForm.setCommentEditable(true);
				} else {
					commentForm.setCommentEditable(false);
				}
				commentForm.setCreatedById(forumTopicCommentVO.getCreatedBy().getEmployeeId());
				commentForm.setCreatedByEmpNumber(forumTopicCommentVO.getCreatedBy().getEmployeeNumber());
				commentForm.setCreatedByName(getEmployeeName(forumTopicCommentVO.getCreatedBy()));
				commentForm.setCreatedDate(DateUtils.timeStampToStringWithTime(forumTopicCommentVO.getPublishedDate(),
						forumTopicCommentVO.getForumTopic().getCompany().getDateFormat()));

				topicCommentFormList.add(commentForm);
			}

			responseForm.setDiscussionTopicCommentFormList(topicCommentFormList);

			commentForms.add(responseForm);
		}

		return commentForms;
	}

	public byte[] generateDiscussionTopicCommentsExcel(List<DiscussionTopicCommentResponseForm> topicCommentForms,
			String templateFilePath) {
		byte[] data = null;
		Workbook wb = null;

		Set<String> sheetNames = new HashSet<>();

		try {

			wb = new XSSFWorkbook(new FileInputStream(templateFilePath));

			int sheetCount = 0;

			for (DiscussionTopicCommentResponseForm topicCommentsForm : topicCommentForms) {

				Sheet sheet = null;
				sheet = wb.getSheetAt(sheetCount);
				String sheetName = WorkbookUtil.createSafeSheetName(
						topicCommentsForm.getTopicId() + EXCELSHEET_NAME_SEPARATOR + topicCommentsForm.getTopicName());
				// String sheetName = WorkbookUtil
				// .createSafeSheetName(topicCommentsForm.getTopicName());
				boolean validSheetName = sheetNames.add(sheetName);
				if (!validSheetName) {
					sheetName += " {" + sheetCount + "}";
				}

				wb.setSheetName(sheetCount, sheetName);
				sheetCount++;

				int rowNum = 0;

				CellStyle cellStyle = wb.createCellStyle();
				cellStyle.setWrapText(true);
				cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

				Row row = sheet.getRow(rowNum++);
				Cell cell = row.getCell(1);

				cell.setCellStyle(cellStyle);

				cell.setCellValue("Topic Name:");

				cell = row.getCell(2);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(topicCommentsForm.getTopicName());
				row = sheet.getRow(rowNum++);
				cell = row.getCell(1);
				cell.setCellStyle(cellStyle);
				cell.setCellValue("Topic Description:");

				cell = row.getCell(2);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(topicCommentsForm.getTopicDesc());
				row = sheet.getRow(rowNum++);
				cell = row.getCell(1);
				cell.setCellStyle(cellStyle);
				cell.setCellValue("Topic Status:");
				cell = row.getCell(2);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(
						(topicCommentsForm.isStatus() ? PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_OPEN
								: PayAsiaConstants.PAYASIA_DISCUSSION_BOARD_TOPIC_STATUS_CLOSED));

				rowNum = 6;
				List<DiscussionTopicCommentForm> comments = topicCommentsForm.getDiscussionTopicCommentFormList();

				CellStyle commentCellStyle = wb.createCellStyle();
				commentCellStyle.setWrapText(true);
				commentCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

				for (int i = 0; i < comments.size(); i++) {
					DiscussionTopicCommentForm comment = comments.get(i);
					int cellNum = 0;
					row = sheet.createRow(rowNum++);

					cell = row.createCell(cellNum++);
					cell.setCellStyle(cellStyle);

					cell.setCellValue(i + 1);

					cell = row.createCell(cellNum++);
					cell.setCellStyle(cellStyle);

					cell.setCellValue(comment.getCreatedByEmpNumber());

					cell = row.createCell(cellNum++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(comment.getCreatedByName());

					cell = row.createCell(cellNum++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(comment.getCreatedDate());

					cell = row.createCell(cellNum++);
					cell.setCellStyle(commentCellStyle);

					cell.setCellValue(comment.getComment());

				}
				row = sheet.createRow(rowNum + 2);
				cell = row.createCell(1);
				cell.setCellStyle(cellStyle);
				cell.setCellValue("Generated On:");

				cell = row.createCell(2);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()));

				row = sheet.createRow(rowNum + 3);

				cell = row.createCell(1);
				cell.setCellStyle(cellStyle);
				cell.setCellValue("Generated By:");

				cell = row.createCell(2);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(topicCommentsForm.getGeneratedBy());

			}

			while (sheetCount <= 100) {
				try {
					wb.setSheetHidden(sheetCount++, Workbook.SHEET_STATE_VERY_HIDDEN);
				} catch (Exception e) {

				}
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			wb.write(out);
			data = out.toByteArray();
			out.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return data;
	}

	@Override
	public Set<Integer> getDistinctYears(Long companyId) {
		List<Integer> yearList = new ArrayList<>(forumTopicDAO.getYearList(companyId));

		Calendar now = Calendar.getInstance();
		Integer currentYear = now.get(Calendar.YEAR);
		yearList.add(currentYear);
		Collections.sort(yearList);
		Set<Integer> yearSet = new LinkedHashSet<>(yearList);
		return yearSet;
	}

	@Override
	public DiscussionBoardFormResponse searchEmployee(ForumTopic topicData, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long companyId, Long employeeId, String metaData,
			Boolean includeResignedEmployees, String searchFor, String topicEmployeeList) {

		EmployeeConditionDTO conditionDTO = new EmployeeConditionDTO();

		EmployeeShortListDTO employeeShortListDTO = generalLogic.getShortListEmployeeIds(employeeId, companyId);
		Set<BigInteger> employeesWithDBPrivList = new HashSet<BigInteger>();

		if (topicData == null) {
			List<Employee> employeesWithDBPriv = employeeDAO.getEmpByPrivilageName(companyId, "DISCUSSION_BOARD");
			if (employeesWithDBPriv == null || employeesWithDBPriv.size() == 0) {
				return new DiscussionBoardFormResponse();
			}
			for (Employee employee : employeesWithDBPriv) {
				employeesWithDBPrivList.add(BigInteger.valueOf(employee.getEmployeeId()));
			}
		} else {
			String[] topicEmployeeIds =  topicData.getTopicEmployeeIds()!=null ? topicData.getTopicEmployeeIds().split(","):null;
			if(topicEmployeeIds!=null) {
			for(String topicEmployeeId : topicEmployeeIds) {
				if (topicEmployeeId.trim().equals("")) {
					continue;
				}
				employeesWithDBPrivList.add(new BigInteger(topicEmployeeId));
			}
			}

		}
		List<BigInteger> shorlistedEmpTempList = employeeShortListDTO.getShortListEmployeeIds();
		List<BigInteger> finalEmpList = null;
		if (shorlistedEmpTempList.size() > 0) {
			shorlistedEmpTempList.retainAll(employeesWithDBPrivList);
			finalEmpList = new ArrayList<BigInteger>(employeesWithDBPrivList);
		} else {
			finalEmpList = new ArrayList<BigInteger>(employeesWithDBPrivList);
		}

		employeeShortListDTO.setShortListEmployeeIds(finalEmpList);
		employeeShortListDTO.setEmployeeShortList(true);
		conditionDTO.setEmployeeShortListDTO(employeeShortListDTO);
		conditionDTO.setShortListEmployeeIds(finalEmpList);

		if (StringUtils.isNotBlank(searchCondition) && searchCondition.equals(PayAsiaConstants.EMPLOYEE_NUMBER) && StringUtils.isNotBlank(searchText)) {
			conditionDTO.setEmployeeNumber("%" + searchText.trim() + "%");
		}
		if (StringUtils.isNotBlank(searchCondition) && searchCondition.equals(PayAsiaConstants.FIRST_NAME) && StringUtils.isNotBlank(searchText)) {
			conditionDTO.setFirstName("%" + searchText.trim() + "%");
		}
		if (StringUtils.isNotBlank(searchCondition) && searchCondition.equals(PayAsiaConstants.LAST_NAME) && StringUtils.isNotBlank(searchText)) {
			conditionDTO.setLastName("%" + searchText.trim() + "%");
		}

		conditionDTO.setStatus(includeResignedEmployees);
//		List<Employee> employeeVOList = employeeDAO.findByCondition(conditionDTO, null, null, companyId);
		Company cmp = companyDAO.findById(companyId);
		List<Employee> totalEmployeeVOList = employeeDAO.findByGroupCondition(conditionDTO, null, null, companyId, cmp.getCompanyGroup().getGroupId());
		List<Employee> employeeVOList = employeeDAO.findByGroupCondition(conditionDTO, pageDTO, sortDTO, companyId, cmp.getCompanyGroup().getGroupId());
		
		List<EmployeeListForm> employeeListFormList = new ArrayList<EmployeeListForm>();
		byte[] empImg = null;
		for (Employee employee : employeeVOList) {
			EmployeeListForm employeeForm = new EmployeeListForm();
			
//			if ("SearchTopicEmployee".equals(searchFor) && topicEmployeeList != null && topicEmployeeList != "" && !topicEmployeeList.isEmpty()) {
			if (StringUtils.equalsIgnoreCase("SearchTopicEmployee", searchFor) && StringUtils.isNotBlank(topicEmployeeList) && StringUtils.isNotEmpty(topicEmployeeList)) {
	
				List<String> employeeArr = Arrays.asList(topicEmployeeList.split(","));
				if (employeeArr.contains(String.valueOf(employee.getEmployeeId()))) {
					String employeeName = "";
					employeeName += employee.getFirstName() + " ";
					if (StringUtils.isNotBlank(employee.getLastName())) {
						employeeName += employee.getLastName();
					}
					employeeForm.setEmployeeName(employeeName);
					employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
					employeeForm.setEmployeeID(employee.getEmployeeId());

					try {
						empImg = employeeDetailLogic.getEmployeeImage(employee.getEmployeeId(), null, employeeImageWidth, employeeImageHeight);
					} catch (IOException e) {
						e.printStackTrace();
					}
					employeeForm.setEmpImage(empImg);
					
					employeeListFormList.add(employeeForm);
				}
			}
//			else if ("SearchEmployee".equals(searchFor) || topicEmployeeList == null || topicEmployeeList == "" || topicEmployeeList.isEmpty()) {
			 else if (StringUtils.equalsIgnoreCase("SearchEmployee", searchFor) && StringUtils.isBlank(topicEmployeeList) && StringUtils.isEmpty(topicEmployeeList)) {
				String employeeName = "";
				employeeName += employee.getFirstName() + " ";
				if (StringUtils.isNotBlank(employee.getLastName())) {
					employeeName += employee.getLastName();
				}
				employeeForm.setEmployeeName(employeeName);
				employeeForm.setEmployeeNumber(employee.getEmployeeNumber());
				employeeForm.setEmployeeID(employee.getEmployeeId());
				
				try {
					empImg = employeeDetailLogic.getEmployeeImage(employee.getEmployeeId(), null, employeeImageWidth, employeeImageHeight);
				} catch (IOException e) {
					e.printStackTrace();
				}
				employeeForm.setEmpImage(empImg);
				employeeListFormList.add(employeeForm);
			}
		}
		DiscussionBoardFormResponse response = new DiscussionBoardFormResponse();
		if (pageDTO != null) {
			int pageSize = pageDTO.getPageSize();
			int startPos = (pageSize * (pageDTO.getPageNumber() - 1));
			int recordSize = totalEmployeeVOList.size();
			int totalPages = recordSize / pageSize;

			if (startPos < recordSize && recordSize % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			response.setTotal(totalPages);
			response.setRecords(recordSize);
			response.setPage(pageDTO.getPageNumber());
		}
		
		response.setSearchEmployeeList(employeeListFormList);
		return response;
	}

	 public static void writeByte(byte[] bytes, File file) 
     { 
         try { 
   
             // Initialize a pointer in file using OutputStream 
             OutputStream  os = new FileOutputStream(file); 
   
             // Starts writing the bytes in it 
             os.write(bytes); 
             System.out.println("Successfully" + " byte inserted"); 
   
             // Close the file 
             os.close(); 
         } 
   
         catch (Exception e) { 
             System.out.println("Exception: " + e); 
         } 
     } 
	 
	@Override
	public ForumTopic getTopicData(Long companyId, Long topicId) {
		ForumTopic forumTopicVO = forumTopicDAO.findById(topicId, companyId);
		return forumTopicVO;
	}

	@Override
	public DiscussionTopicCommentForm getTopicCommentData(long topicCommentId, Long companyId) {

		ForumTopicComment forumTopicComment = forumTopicCommentDAO.findByForumTopicCommentId(topicCommentId, companyId);
		if(forumTopicComment==null){
			return null;
		}
		
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		discussionTopicReplyForm.setTopicCommentId(forumTopicComment.getTopicCommentId());

		discussionTopicReplyForm.setTopicId(forumTopicComment.getForumTopic().getTopicId());
		discussionTopicReplyForm.setComment(forumTopicComment.getComment());
		discussionTopicReplyForm.setTopicEmployeeIds(forumTopicComment.getTopicCommentEmployeeIds());
		if (StringUtils.isBlank(forumTopicComment.getEmailTo())) {
			discussionTopicReplyForm.setEmail(getTopicDefaultEmail(
					forumTopicComment.getForumTopic().getCompany().getCompanyId(),
					forumTopicComment.getForumTopic().getTopicId(), forumTopicComment.getCreatedBy().getEmployeeId()));
		} else {
			discussionTopicReplyForm.setEmail(forumTopicComment.getEmailTo());
		}

		if (StringUtils.isBlank(forumTopicComment.getEmailCC())) {
			discussionTopicReplyForm.setEmailCc(getTopicDefaultEmailCC(
					forumTopicComment.getForumTopic().getCompany().getCompanyId(),
					forumTopicComment.getForumTopic().getTopicId(), forumTopicComment.getCreatedBy().getEmployeeId()));
		} else {
			discussionTopicReplyForm.setEmailCc(forumTopicComment.getEmailCC());
		}
		discussionTopicReplyForm.setSendMail(forumTopicComment.isSendMail());

		List<TopicAttachmentDTO> attachmentList = new ArrayList<TopicAttachmentDTO>();
		for (ForumTopicCommentAttachment attachment : forumTopicComment.getForumTopicAttachments()) {
			TopicAttachmentDTO attachmentDTO = new TopicAttachmentDTO();
			attachmentDTO.setEmailAttachmentId(String.valueOf(attachment.getTopicAttachmentId()));
			attachmentDTO.setAttachmentName(attachment.getFileName());
			attachmentList.add(attachmentDTO);

		}
		discussionTopicReplyForm.setAttachmentList(attachmentList);
		return discussionTopicReplyForm;
	}

	public List<DiscussionTopicCommentResponseForm> downloadTopicCommentsCommon(String topicIds, Long companyId,
			Long employeeId, String type) {
		List<DiscussionTopicCommentResponseForm> commentForms = new ArrayList<>();
		List<Long> topicIdList = new ArrayList<Long>();
		List<ForumTopic> sortedForumTopicListVO = new ArrayList<ForumTopic>();
		Employee employeeVO = employeeDAO.findById(employeeId);

		if ((StringUtils.equalsIgnoreCase(type, "multiple"))) {
			if (StringUtils.isNotBlank(topicIds)) {
				String[] topicIdArray = topicIds.split(",");
				for (String topicId : topicIdArray) {
					if (StringUtils.isNotBlank(topicId)) {
						topicIdList.add(FormatPreserveCryptoUtil.decrypt(Long.valueOf(topicId)));
					}
				}
			}
			List<ForumTopic> forumTopicListVO = forumTopicDAO.getMultipleTopicList(companyId, topicIdList);
			for (long topicId : topicIdList) {
				for (ForumTopic forumTopic : forumTopicListVO) {
					if (forumTopic.getTopicId() == topicId) {
						sortedForumTopicListVO.add(forumTopic);
						break;
					}
				}
			}
		} else {
			ForumTopic forumTopicVO = forumTopicDAO.findById(FormatPreserveCryptoUtil.decrypt(Long.valueOf(topicIds)), companyId);
			if(forumTopicVO != null) {
				sortedForumTopicListVO.add(forumTopicVO);
			}
		}
		for (ForumTopic forumTopicVO : sortedForumTopicListVO) {
			DiscussionTopicCommentResponseForm responseForm = new DiscussionTopicCommentResponseForm();
			responseForm.setTopicId(forumTopicVO.getTopicId());
			responseForm.setTopicName(forumTopicVO.getTopicName());
			responseForm.setTopicDesc(forumTopicVO.getTopicDesc());
			responseForm.setStatus(forumTopicVO.isStatus());

			responseForm.setGeneratedBy(getEmployeeName(employeeVO));

			if (isSuperAdmin() || (forumTopicVO.getCreatedBy().getEmployeeId() == employeeId)) {
				responseForm.setTopicEditable(true);
			} else {
				responseForm.setTopicEditable(false);
			}

			List<DiscussionTopicCommentForm> topicCommentFormList = new ArrayList<DiscussionTopicCommentForm>();

			List<ForumTopicComment> forumTopicCommentList = forumTopicCommentDAO
					.getTopicCommentListAscOrder(forumTopicVO.getTopicId(), companyId);
			// Collections.sort(forumTopicCommentList, new
			// ForumTopicCommentComp());
			for (ForumTopicComment forumTopicCommentVO : forumTopicCommentList) {
				if (forumTopicCommentVO.getPublishedStatus().getCodeValue()
						.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT)) {
					continue;
				}
				DiscussionTopicCommentForm commentForm = new DiscussionTopicCommentForm();
				String plainText = forumTopicCommentVO.getComment().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
				plainText = plainText.replaceAll("&nbsp;", " ").replaceAll("&quot;", "\"").replaceAll("&#39;", "'");
				commentForm.setComment(plainText);
				commentForm.setEmail(forumTopicCommentVO.getEmailTo());
				commentForm.setEmailCc(forumTopicCommentVO.getEmailCC());
				commentForm.setSendMail(forumTopicCommentVO.isSendMail());
				commentForm.setTopicCommentId(forumTopicCommentVO.getTopicCommentId());
				if (isSuperAdmin() || (forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId
						&& forumTopicCommentVO.getPublishedStatus().getCodeValue()
								.equalsIgnoreCase(PayAsiaConstants.APP_CODE_DISCUSSION_BOARD_STATUS_DRAFT))) {
					commentForm.setCommentEditable(true);
				} else {
					commentForm.setCommentEditable(false);
				}
				commentForm.setCreatedById(forumTopicCommentVO.getCreatedBy().getEmployeeId());
				commentForm.setCreatedByEmpNumber(forumTopicCommentVO.getCreatedBy().getEmployeeNumber());
				commentForm.setCreatedByName(getEmployeeName(forumTopicCommentVO.getCreatedBy()));
				commentForm.setCreatedDate(DateUtils.timeStampToStringWithTime(forumTopicCommentVO.getPublishedDate(),
						forumTopicCommentVO.getForumTopic().getCompany().getDateFormat()));

				HRISPreference hrisPreference = hrisPreferenceDAO.findByCompanyId(companyId);
				boolean isUserAuthorizedForTopicComment = false;
				if (hrisPreference != null) {
					if (hrisPreference.isEnableVisibility() != null && hrisPreference.isEnableVisibility()) {
						if (employeeId.equals(forumTopicCommentVO.getCreatedBy().getEmployeeId())) {
							isUserAuthorizedForTopicComment = true;
						} else if ((hrisPreference.getDiscussionBoardPayAsiaUsers() != null
								&& hrisPreference.getDiscussionBoardPayAsiaUsers() != ""
								&& !hrisPreference.getDiscussionBoardPayAsiaUsers().isEmpty())) {
							String arrTopicEmployees[] = hrisPreference.getDiscussionBoardPayAsiaUsers().split(";");
							List<String> empList = Arrays.asList(arrTopicEmployees);
							if (empList.contains(String.valueOf(employeeId))
									|| forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId) {
								isUserAuthorizedForTopicComment = true;
							}
						}
						if (!isUserAuthorizedForTopicComment && (forumTopicCommentVO.getTopicCommentEmployeeIds() != null
								&& forumTopicCommentVO.getTopicCommentEmployeeIds() != ""
								&& !forumTopicCommentVO.getTopicCommentEmployeeIds().isEmpty())) {
							String arrTopicEmployees[] = forumTopicCommentVO.getTopicCommentEmployeeIds().split(",");
							List<String> empList = Arrays.asList(arrTopicEmployees);
							if (empList.contains(String.valueOf(employeeId))
									|| forumTopicCommentVO.getCreatedBy().getEmployeeId() == employeeId) {
								isUserAuthorizedForTopicComment = true;
							}
						}
					} else {
						isUserAuthorizedForTopicComment = true;
					}

					if (isUserAuthorizedForTopicComment) {
						topicCommentFormList.add(commentForm);
					}
				}
			}
			responseForm.setDiscussionTopicCommentFormList(topicCommentFormList);

			commentForms.add(responseForm);
		}
		return commentForms;
	}
}
