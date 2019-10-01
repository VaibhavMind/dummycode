package com.payasia.api.common.hris.impl;

import static com.payasia.api.utils.ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.common.hris.CommonApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.AnouncementForm;
import com.payasia.common.form.AnouncementFormResponse;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AnouncementLogic;

@RestController
@RequestMapping(value = API_ROOT_PATH_FOR_EMPLOYEE+"/announcement")
public class CommonApiImpl implements CommonApi{
	
	@Resource
	private AnouncementLogic anouncementLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;

	@Override
	@PostMapping(value = "emp-announcement")
	public ResponseEntity<?> getAnouncementList(@RequestBody SearchParam searchParamObj) {

		final Long companyId = UserContext.getClientAdminId();
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
		
		AnouncementFormResponse anouncementFormResponse = anouncementLogic.getAnnouncementList(companyId, searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), announcementForm);

		if (anouncementFormResponse != null && !anouncementFormResponse.getRows().isEmpty()) {
			return new ResponseEntity<>(anouncementFormResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource
				.getMessage("payasia.error.datanot.found", new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}

}
