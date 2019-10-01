package com.payasia.api.dashboard.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.dashboard.HelpDeskApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.dto.HelpDeskDTO;
import com.payasia.common.form.HelpDeskFormResponse;
import com.payasia.logic.AnouncementLogic;

/**
 * @author gauravkumar
 * @param :
 *            This class used for HelpDesk API
 * 
 */
@RestController
@RequestMapping(value= ApiUtils.API_ROOT_PATH_FOR_COMMON)
public class HelpDeskApiImpl implements HelpDeskApi {
	
	@Resource
	private AnouncementLogic anouncementLogic;
	
	@Override
	@PostMapping(value = "guidelines")
	public ResponseEntity<?> provideGuidelines(@RequestBody HelpDeskDTO helpDesk){
		
		List<HelpDeskFormResponse> helpDeskFormResponse=anouncementLogic.getTopicGuidelineList(helpDesk);
		return new ResponseEntity<>(helpDeskFormResponse, HttpStatus.OK);
	}

}
