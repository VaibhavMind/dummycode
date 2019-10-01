/**
 * @author vivekjain
 *
 */
package com.payasia.logic.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.payasia.common.dto.HelpDeskDTO;
import com.payasia.common.exception.PayAsiaBusinessException;
import com.payasia.common.form.AnouncementForm;
import com.payasia.common.form.AnouncementFormResponse;
import com.payasia.common.form.HelpDeskFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.AnnouncementComparison;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AnnouncementDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.bean.Announcement;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.HelpDesk;
import com.payasia.logic.AnouncementLogic;

/**
 * The Class AnouncementLogicImpl.
 */
@Component
public class AnouncementLogicImpl implements AnouncementLogic {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AnouncementLogicImpl.class);
	/** The company dao. */
	@Resource
	CompanyDAO companyDAO;
	@Resource
	AnnouncementDAO announcementDAO;

	@Override
	public String postAnouncement(AnouncementForm anouncementForm, Long companyId) {                    
		Announcement announcement = new Announcement();

		Company company = companyDAO.findById(companyId);
		if(isAnnouncementValid(announcement, company)) {
			announcement.setCompany(company);
			announcement.setCompanyGroup(company.getCompanyGroup());
			announcement.setTitle(anouncementForm.getTitle());
			announcement.setDescription(anouncementForm.getDescription());
			announcement.setScope(anouncementForm.getScope());
			try {
				announcement.setPostDateTime(DateUtils.convertStringToTimestamp(
						anouncementForm.getPostDateTime(), "yyyy-MM-dd HH:mm"));
				if (StringUtils.isNotBlank(anouncementForm.getRemoveDateTime())) {
					announcement.setRemoveDateTime(DateUtils.convertStringToTimestamp(
							anouncementForm.getRemoveDateTime(), "yyyy-MM-dd HH:mm"));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			announcementDAO.save(announcement);
			}
			return PayAsiaConstants.PAYASIA_SUCCESS;

	}

	@Override
	public List<AnouncementForm> getAnouncement(Long companyId) {
		boolean status = true;
		List<AnouncementForm> anouncementFormList = new ArrayList<AnouncementForm>();
		Company companyVO = companyDAO.findById(companyId);

		List<Announcement> announcementByCompList = announcementDAO
				.findByCompanyAndScope(companyId,
						PayAsiaConstants.ANNOUNCEMENT_COMPANY_SCOPE);
		for (Announcement announcementVO : announcementByCompList) {
			AnouncementForm anouncementForm = new AnouncementForm();
			anouncementForm.setTitle(announcementVO.getTitle());
			anouncementForm.setScope(announcementVO.getScope());
			anouncementForm.setDescription(announcementVO.getDescription());
			anouncementForm.setPostDateTime(DateUtils
					.timeStampToString(announcementVO.getPostDateTime()));
			if (announcementVO.getRemoveDateTime() != null) {
				anouncementForm.setRemoveDateTime(DateUtils
						.timeStampToString(announcementVO.getRemoveDateTime()));
			}

			/*ID ENCRYPT*/
			anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.encrypt(announcementVO.getAnnouncementId()));
			
			if (announcementVO.getRemoveDateTime() != null) {
				status = checkAnnouncementStatus(DateUtils.stringToDate(
						DateUtils.timeStampToString(
								announcementVO.getRemoveDateTime(),
								"yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
			} else {
				status = true;
			}

			if (status) {
				anouncementFormList.add(anouncementForm);
			}

		}

		List<Announcement> announcementByGroupList = announcementDAO
				.findByCompanyGroupAndScope(companyVO.getCompanyGroup()
						.getGroupId(),
						PayAsiaConstants.ANNOUNCEMENT_GROUP_SCOPE);

		for (Announcement announcementVO : announcementByGroupList) {
			AnouncementForm anouncementForm = new AnouncementForm();
			anouncementForm.setTitle(announcementVO.getTitle());
			anouncementForm.setScope(announcementVO.getScope());
			anouncementForm.setDescription(announcementVO.getDescription());
			anouncementForm.setPostDateTime(DateUtils
					.timeStampToString(announcementVO.getPostDateTime()));
			if (announcementVO.getRemoveDateTime() != null) {
				anouncementForm.setRemoveDateTime(DateUtils
						.timeStampToString(announcementVO.getRemoveDateTime()));
			}
			
			/*ID ENCRYPT*/
			anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.encrypt(announcementVO.getAnnouncementId()));
			
			if (announcementVO.getRemoveDateTime() != null) {
				status = checkAnnouncementStatus(DateUtils.stringToDate(
						DateUtils.timeStampToString(
								announcementVO.getRemoveDateTime(),
								"yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
			} else {
				status = true;
			}

			if (status) {
				anouncementFormList.add(anouncementForm);
			}
		}
		return anouncementFormList;
	}

	private boolean checkAnnouncementStatus(Date removeDate) {
		boolean status = true;
		
		Timestamp currentDate = new Timestamp(new Date().getTime());

		if (removeDate.before(currentDate)) {
			status = false;
		}
		return status;

	}

	@Override
	public AnouncementForm getAnouncementForEdit(Long companyId,
			Long announcementId) {
		Announcement announcementVO = announcementDAO.findById(announcementId);
		Company loggedInUserCompany = companyDAO.findById(companyId);
		AnouncementForm anouncementForm = new AnouncementForm();
		if(isAnnouncementValid(announcementVO, loggedInUserCompany)) {
			anouncementForm.setTitle(announcementVO.getTitle());
			anouncementForm.setScope(announcementVO.getScope());
			anouncementForm.setDescription(announcementVO.getDescription());
			anouncementForm.setPostDateTime(DateUtils.timeStampToString(
					announcementVO.getPostDateTime(), "yyyy-MM-dd HH:mm"));
			if (announcementVO.getRemoveDateTime() != null) {
				anouncementForm.setRemoveDateTime(DateUtils.timeStampToString(
						announcementVO.getRemoveDateTime(), "yyyy-MM-dd HH:mm"));
			}
	
			/*ID ENCRYPT*/
			anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.encrypt(announcementVO.getAnnouncementId()));
		}
		return anouncementForm;
	}

	@Override
	public String updateAnouncement(AnouncementForm anouncementForm,
			Long companyId) {
		Company company = companyDAO.findById(companyId);
		Announcement announcement = announcementDAO.findById(anouncementForm.getAnnouncementId());
		if(isAnnouncementValid(announcement, company)) {
			announcement.setTitle(anouncementForm.getTitle());
			announcement.setDescription(anouncementForm.getDescription());
			announcement.setScope(anouncementForm.getScope());
			try {
				announcement.setPostDateTime(DateUtils.convertStringToTimestamp(
						anouncementForm.getPostDateTime(), "yyyy-MM-dd HH:mm"));
				if (StringUtils.isNotBlank(anouncementForm.getRemoveDateTime())) {
					announcement.setRemoveDateTime(DateUtils.convertStringToTimestamp(
							anouncementForm.getRemoveDateTime(), "yyyy-MM-dd HH:mm"));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			announcementDAO.update(announcement);
		}
		return PayAsiaConstants.PAYASIA_SUCCESS;

	}

	@Override
	public String deleteAnouncement(Long announcementId, Long companyId) {
		Announcement announcementVO = announcementDAO.findById(announcementId);
		Company company = companyDAO.findById(companyId);
		if(isAnnouncementValid(announcementVO, company))
			announcementDAO.delete(announcementVO);
		    return PayAsiaConstants.PAYASIA_SUCCESS;
	}
	
	/**
	 * 
	 * @param announcementVO
	 * @param company
	 * @return
	 */
	private boolean isAnnouncementValid(Announcement announcementVO, Company company) {
		Company announcementCompany = announcementVO.getCompany();
		if( ("C".equals(announcementVO.getScope()) && !(company.getCompanyId() == announcementCompany.getCompanyId()))
				|| ("G".equals(announcementVO.getScope()) && 
						!(company.getCompanyGroup().getGroupId() == announcementCompany.getCompanyGroup().getGroupId())) ) {
			throw new PayAsiaBusinessException("Announcement_Retrieval_Error", "Announcement is of different company or company-group.");
		}		 
		
		return true;
	}
	
	@Override
	public List<HelpDeskFormResponse> getTopicGuidelineList(HelpDeskDTO helpDesk) {

		List<HelpDeskFormResponse> helpDeskFormResponseList = new ArrayList<>();
		List<String> screenNameList = new ArrayList<>();
		Map<String, List<HelpDeskDTO>> mapp = new HashMap<>();
		
		List<HelpDesk> topicGuidelineList = announcementDAO.findByTopicAndRole(helpDesk.getKeyName(), helpDesk.getRole(), helpDesk.getModuleName());

		for (HelpDesk helpDeskObj : topicGuidelineList) {
			
			if(!screenNameList.contains(helpDeskObj.getScreenName())){
				screenNameList.add(helpDeskObj.getScreenName());
				mapp.put(helpDeskObj.getScreenName(), new ArrayList<HelpDeskDTO>());
			}
			
			HelpDeskDTO deskDTO = new HelpDeskDTO();
			deskDTO.setTopicName(helpDeskObj.getTopicName());
			deskDTO.setTopicUrl(helpDeskObj.getUrl());
			
			if(mapp.keySet().contains(helpDeskObj.getScreenName())){
				mapp.get(helpDeskObj.getScreenName()).add(deskDTO);
			}
		}
		
		Iterator<Map.Entry<String, List<HelpDeskDTO>>> itr = mapp.entrySet().iterator(); 
        
        while(itr.hasNext()) 
        { 
             Map.Entry<String, List<HelpDeskDTO>> entry = itr.next(); 
             HelpDeskFormResponse helpDeskFormResponse = new HelpDeskFormResponse();
             helpDeskFormResponse.setScreenName(entry.getKey());
             helpDeskFormResponse.setRows(entry.getValue());
             helpDeskFormResponseList.add(helpDeskFormResponse);
        } 
		return helpDeskFormResponseList;
	}
	
	@Override
	public AnouncementFormResponse getAnnouncementList(Long companyId, PageRequest pageDTO, SortCondition sortDTO, AnouncementForm announcementForm) {
		boolean status = true;

		AnouncementFormResponse anouncementFormResponse = new AnouncementFormResponse();

		List<AnouncementForm> anouncementFormList = new ArrayList<AnouncementForm>();
		Company companyVO = companyDAO.findById(companyId);

		if (announcementForm != null && StringUtils.isNotBlank(announcementForm.getScope()) && 
				(StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_COMPANY_SCOPE) || 
						StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_ALL))) {
			
			List<Announcement> announcementByCompList = announcementDAO.findByCompanyAndScope(companyId, PayAsiaConstants.ANNOUNCEMENT_COMPANY_SCOPE, announcementForm);

			for (Announcement announcementVO : announcementByCompList) {
				AnouncementForm anouncementForm = new AnouncementForm();
				anouncementForm.setTitle(announcementVO.getTitle());
				anouncementForm.setScope(announcementVO.getScope());
				anouncementForm.setDescription(announcementVO.getDescription());
				anouncementForm.setPostDateTime(DateUtils.timeStampToStringWithTime(announcementVO.getPostDateTime()));
				if (announcementVO.getRemoveDateTime() != null) {
					anouncementForm.setRemoveDateTime((announcementVO.getRemoveDateTime()!=null)?DateUtils.timeStampToStringWithTime(announcementVO.getRemoveDateTime()):"");
				}

				/* ID ENCRYPT */
				anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.encrypt(announcementVO.getAnnouncementId()));
				
				if (announcementVO.getPostDateTime() != null) {
					Date postDate = DateUtils.stringToDate(DateUtils.timeStampToStringWOTimezone(announcementVO.getPostDateTime(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
					
					if(checkAnnouncementPostStatus(postDate)) {
						if (announcementVO.getRemoveDateTime() != null) {
							status = checkAnnouncementStatus(DateUtils.stringToDate(DateUtils.timeStampToStringWOTimezone(announcementVO.getRemoveDateTime(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
							
						} else {
							status = true;
						}
					}
					else {
						status = false;
					}
				}

				if (status) {
					anouncementFormList.add(anouncementForm);
				}
			}
		}
		
		if (announcementForm != null && StringUtils.isNotBlank(announcementForm.getScope()) && 
				(StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_GROUP_SCOPE) || 
						StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_ALL))) {

			List<Announcement> announcementByGroupList = announcementDAO.findByCompanyGroupAndScope(companyVO.getCompanyGroup().getGroupId(), PayAsiaConstants.ANNOUNCEMENT_GROUP_SCOPE, announcementForm);

			for (Announcement announcementVO : announcementByGroupList) {
				AnouncementForm anouncementForm = new AnouncementForm();
				anouncementForm.setTitle(announcementVO.getTitle());
				anouncementForm.setScope(announcementVO.getScope());
				anouncementForm.setDescription(announcementVO.getDescription());
				anouncementForm.setPostDateTime(DateUtils.timeStampToStringWithTime(announcementVO.getPostDateTime()));
				if (announcementVO.getRemoveDateTime() != null) {
					anouncementForm.setRemoveDateTime((announcementVO.getRemoveDateTime()!=null)?DateUtils.timeStampToStringWithTime(announcementVO.getRemoveDateTime()):"");
				}

				/* ID ENCRYPT */
				anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.encrypt(announcementVO.getAnnouncementId()));

				if (announcementVO.getPostDateTime() != null) {
					Date postDate = DateUtils.stringToDate(DateUtils.timeStampToStringWOTimezone(announcementVO.getPostDateTime(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
					
					if(checkAnnouncementPostStatus(postDate)) {
						if (announcementVO.getRemoveDateTime() != null) {
							status = checkAnnouncementStatus(DateUtils.stringToDate(DateUtils.timeStampToStringWOTimezone(announcementVO.getRemoveDateTime(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm"));
							
						} else {
							status = true;
						}
					}
					else {
						status = false;
					}
				}

				if (status) {
					anouncementFormList.add(anouncementForm);
				}
			}
		}
		
		if (sortDTO != null && !StringUtils.isEmpty(sortDTO.getColumnName()) && !anouncementFormList.isEmpty()) {
			Collections.sort(anouncementFormList, new AnnouncementComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		List<AnouncementForm> finalAnnouncementList = new ArrayList<>();
		if (pageDTO != null && !anouncementFormList.isEmpty()) {
			int recordSizeFinal = anouncementFormList.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;

			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal) {
				endPos = startPos + pageSize;
			} else if (startPos < recordSizeFinal) {
				endPos = recordSizeFinal;
			}

			finalAnnouncementList = anouncementFormList.subList(startPos, endPos);
			anouncementFormResponse.setTotal(totalPages);
		}
		anouncementFormResponse.setRows(finalAnnouncementList);
		anouncementFormResponse.setPage(pageDTO.getPageNumber());
		anouncementFormResponse.setRecords(anouncementFormList.size());

		return anouncementFormResponse;
	}
	
	private boolean checkAnnouncementPostStatus(Date postDate) {
		boolean status = true;
		
		Timestamp currentDate = new Timestamp(new Date().getTime());

		if (postDate.after(currentDate)) {
			status = false;
		}
		return status;

	}
	
	@Override
	public AnouncementFormResponse getAllAnnouncementList(Long companyId, PageRequest pageDTO, SortCondition sortDTO, AnouncementForm announcementForm) {
		AnouncementFormResponse anouncementFormResponse = new AnouncementFormResponse();

		List<AnouncementForm> anouncementFormList = new ArrayList<>();
		Company companyVO = companyDAO.findById(companyId);

		if (announcementForm != null && StringUtils.isNotBlank(announcementForm.getScope()) && 
				(StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_COMPANY_SCOPE) || 
						StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_ALL))) {
			
			List<Announcement> announcementByCompList = announcementDAO.findByCompanyAndScope(companyId, PayAsiaConstants.ANNOUNCEMENT_COMPANY_SCOPE, announcementForm);

			for (Announcement announcementVO : announcementByCompList) {
				AnouncementForm anouncementForm = new AnouncementForm();
				anouncementForm.setTitle(announcementVO.getTitle());
				anouncementForm.setScope(announcementVO.getScope());
				anouncementForm.setDescription(announcementVO.getDescription());
				anouncementForm.setPostDateTime(DateUtils.timeStampToStringWithTime(announcementVO.getPostDateTime()));
				if (announcementVO.getRemoveDateTime() != null) {
					anouncementForm.setRemoveDateTime((announcementVO.getRemoveDateTime()!=null)?DateUtils.timeStampToStringWithTime(announcementVO.getRemoveDateTime()):"");
				}
				
				/* ID ENCRYPT */
				anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.encrypt(announcementVO.getAnnouncementId()));
				anouncementFormList.add(anouncementForm);
			}
		}
		
		if (announcementForm != null && StringUtils.isNotBlank(announcementForm.getScope()) && 
				(StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_GROUP_SCOPE) || 
						StringUtils.equalsIgnoreCase(announcementForm.getScope(), PayAsiaConstants.ANNOUNCEMENT_ALL))) {

			List<Announcement> announcementByGroupList = announcementDAO.findByCompanyGroupAndScope(companyVO.getCompanyGroup().getGroupId(), PayAsiaConstants.ANNOUNCEMENT_GROUP_SCOPE, announcementForm);

			for (Announcement announcementVO : announcementByGroupList) {
				AnouncementForm anouncementForm = new AnouncementForm();
				anouncementForm.setTitle(announcementVO.getTitle());
				anouncementForm.setScope(announcementVO.getScope());
				anouncementForm.setDescription(announcementVO.getDescription());
				anouncementForm.setPostDateTime(DateUtils.timeStampToStringWithTime(announcementVO.getPostDateTime()));
				if (announcementVO.getRemoveDateTime() != null) {
					anouncementForm.setRemoveDateTime((announcementVO.getRemoveDateTime()!=null)?DateUtils.timeStampToStringWithTime(announcementVO.getRemoveDateTime()):"");
				}

				/* ID ENCRYPT */
				anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.encrypt(announcementVO.getAnnouncementId()));
				anouncementFormList.add(anouncementForm);
			}
		}
		
		if (sortDTO != null && !StringUtils.isEmpty(sortDTO.getColumnName()) && !anouncementFormList.isEmpty()) {
			Collections.sort(anouncementFormList, new AnnouncementComparison(sortDTO.getOrderType(), sortDTO.getColumnName()));
		}

		List<AnouncementForm> finalAnnouncementList = new ArrayList<>();
		if (pageDTO != null && !anouncementFormList.isEmpty()) {
			int recordSizeFinal = anouncementFormList.size();
			int pageSize = pageDTO.getPageSize();
			int pageNo = pageDTO.getPageNumber();
			int startPos = (pageSize * (pageNo - 1));
			int endPos = 0;

			int totalPages = recordSizeFinal / pageSize;

			if (recordSizeFinal % pageSize != 0) {
				totalPages = totalPages + 1;
			}
			if (recordSizeFinal == 0) {
				pageDTO.setPageNumber(0);
			}

			if ((startPos + pageSize) <= recordSizeFinal) {
				endPos = startPos + pageSize;
			} else if (startPos < recordSizeFinal) {
				endPos = recordSizeFinal;
			}

			finalAnnouncementList = anouncementFormList.subList(startPos, endPos);
			anouncementFormResponse.setTotal(totalPages);
		}
		anouncementFormResponse.setRows(finalAnnouncementList);
		anouncementFormResponse.setPage(pageDTO.getPageNumber());
		anouncementFormResponse.setRecords(anouncementFormList.size());

		return anouncementFormResponse;
	}

}
