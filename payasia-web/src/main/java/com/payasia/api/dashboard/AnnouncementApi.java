package com.payasia.api.dashboard;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.AnouncementForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="ADMIN-ANNOUNCEMENT")
public interface AnnouncementApi extends SwaggerTags{

	@ApiOperation(value = "Add Announcements")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> addAnnouncements(AnouncementForm anouncementForm);

	@ApiOperation(value = "Delete Announcements")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteAnnouncement(Long announcementId);

	@ApiOperation(value = "Update Announcements")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> updateAnnouncement(AnouncementForm anouncementForm);

	@ApiOperation(value = "Get Announcement For Edit")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getAnouncement(Long announcementId);

	@ApiOperation(value = "Display all announcements", notes = "Announcement data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getAllAnouncementList(SearchParam searchParamObj);

}
