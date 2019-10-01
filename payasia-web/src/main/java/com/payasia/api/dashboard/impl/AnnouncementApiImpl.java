package com.payasia.api.dashboard.impl;

import static com.payasia.api.utils.ApiUtils.API_ROOT_PATH_FOR_ADMIN;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.dashboard.AnnouncementApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.AnouncementForm;
import com.payasia.common.form.AnouncementFormResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AnouncementLogic;

/**
 * @author rohitsrivastava
 * @param :
 *            This class used for Announcement APIs
 * 
 */
@RestController
@RequestMapping(value = API_ROOT_PATH_FOR_ADMIN+"/announcement")
public class AnnouncementApiImpl implements AnnouncementApi {
	
	@Resource
	private AnouncementLogic anouncementLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Override
	@PostMapping(value = "add-announcement")
	public ResponseEntity<?> addAnnouncements(@RequestBody AnouncementForm anouncementForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		String addAnnouncementStatus = anouncementLogic.postAnouncement(anouncementForm, companyId);
		if(StringUtils.equalsIgnoreCase(addAnnouncementStatus,PayAsiaConstants.PAYASIA_SUCCESS)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.announcement.added.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.announcement.added.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@DeleteMapping(value = "delete-announcement")
	public ResponseEntity<?> deleteAnnouncement(@RequestParam(value = "announcementId", required = true) Long announcementId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		announcementId=FormatPreserveCryptoUtil.decrypt(announcementId);
        
		String deleteStatus = anouncementLogic.deleteAnouncement(announcementId, companyId);
		if(StringUtils.equalsIgnoreCase(deleteStatus,PayAsiaConstants.PAYASIA_SUCCESS)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.announcement.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.announcement.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PutMapping(value = "update-announcement")
	public ResponseEntity<?> updateAnnouncement(@RequestBody AnouncementForm anouncementForm) {
	
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		anouncementForm.setAnnouncementId(FormatPreserveCryptoUtil.decrypt(anouncementForm.getAnnouncementId()));
		String updatedStatus = anouncementLogic.updateAnouncement(anouncementForm, companyId);
		
		if(StringUtils.equalsIgnoreCase(updatedStatus,PayAsiaConstants.PAYASIA_SUCCESS)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.announcement.updated.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.announcement.updated.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "get-announcement")
	public ResponseEntity<?> getAnouncement(@RequestParam(value = "announcementId", required = true) Long announcementId){
	
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		
		AnouncementForm getAnouncementForEdit = anouncementLogic.getAnouncementForEdit(companyId, FormatPreserveCryptoUtil.decrypt(announcementId));
		
		if(getAnouncementForEdit!=null){
			return new ResponseEntity<>(getAnouncementForEdit, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.announcement.get.data.for.update.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "search")
	public ResponseEntity<?> getAllAnouncementList(@RequestBody SearchParam searchParamObj) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		AnouncementForm announcementForm = new AnouncementForm();
		
		final List<Filters> filterlist = Arrays.asList(searchParamObj.getFilters());
		
		for (Filters filter : filterlist) {
			switch (filter.getField()) {
			case "title":
				announcementForm.setTitle(StringUtils.isNotBlank(filter.getValue())?filter.getValue().trim():"");
				break;
			case "description":
				announcementForm.setDescription(StringUtils.isNotBlank(filter.getValue())?filter.getValue().trim():"");
				break;
			case "scope":
				announcementForm.setScope(StringUtils.isNotBlank(filter.getValue())?filter.getValue().trim():"");
				break;
			case "postDateTime":
				announcementForm.setPostDateTime(StringUtils.isNotBlank(filter.getValue())?filter.getValue().trim():"");
				break;
			case "removeDateTime":
				announcementForm.setRemoveDateTime(StringUtils.isNotBlank(filter.getValue())?filter.getValue().trim():"");
				break;
			}
		}
		
		if(StringUtils.isBlank(announcementForm.getScope())) {
			announcementForm.setScope(PayAsiaConstants.ANNOUNCEMENT_ALL);
		}
		
		AnouncementFormResponse anouncementFormResponse = anouncementLogic.getAllAnnouncementList(companyId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), announcementForm);

		if (anouncementFormResponse != null && !anouncementFormResponse.getRows().isEmpty()) {
			return new ResponseEntity<>(anouncementFormResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
				.getMessage("payasia.error.datanot.found", new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
}
