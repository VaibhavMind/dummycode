package com.payasia.api.dashboard.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.dashboard.RecentActivityApi;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.PrivilegeUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.EmployeeHomePageForm;
import com.payasia.logic.EmployeeHomePageLogic;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Recent Activity API
 */
@RestController
// TODO : Request mapping root URL get the value of ApiUtil
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class RecentActivityApiImpl implements RecentActivityApi {

	@Resource
	private EmployeeHomePageLogic employeeHomePageLogic;
	
	@Autowired
	private PrivilegeUtils privilegeUtils;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Override
	@PostMapping(value = "recent-activity")
	public ResponseEntity<?> getAllRecentActivityListCount(@RequestParam(value = "requestType", required = false) String requestType) {
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Long languageId = Long.valueOf(UserContext.getLanguageId());
		CompanyModuleDTO companyModuleDTO = getModule();
		List<String> listOfPrivilege = privilegeUtils.getPrivilege();
		List<String> listOfRoles = privilegeUtils.getRole();
		
		Map<String, Object> employeeHomePageFormMap = employeeHomePageLogic.getAllRecentActivityListCount(companyId,
				employeeId, companyModuleDTO, languageId, requestType, listOfPrivilege, listOfRoles);

		return new ResponseEntity<>(employeeHomePageFormMap, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "recent-activity-data")
	public ResponseEntity<?> getAllRecentActivityList(@RequestParam(value = "recentActivityType", required = true) String recentActivityType, @RequestBody SearchParam searchParamObj) {
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		Long languageId = Long.valueOf(UserContext.getLanguageId());
		CompanyModuleDTO companyModuleDTO = getModule();
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		List<String> listOfPrivilege = privilegeUtils.getPrivilege();
		List<String> listOfRoles = privilegeUtils.getRole();
		
		EmployeeHomePageForm employeeHomePageFormMap = employeeHomePageLogic.getRecentActivityList(companyId,
				employeeId, companyModuleDTO, languageId, recentActivityType, searchSortDTO.getPageRequest(),
				searchSortDTO.getSortCondition(),listOfPrivilege, listOfRoles);
		/*if(employeeHomePageFormMap==null || employeeHomePageFormMap.getRecentActivityDTOList().size()<1){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.error.datanot.found", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
		}*/
		return new ResponseEntity<>(employeeHomePageFormMap, HttpStatus.OK);		
	}

	// TODO: HRIS MODULE IS ENABLED; REMAINING OTHERS ARE DISABLED
	private CompanyModuleDTO getModule() {
		CompanyModuleDTO companyModuleDTO = new CompanyModuleDTO();
		companyModuleDTO.setHasHrisModule(UserContext.isHrisModule());
		companyModuleDTO.setHasClaimModule(UserContext.isClaimModule());
		companyModuleDTO.setHasLeaveModule(UserContext.isLeaveModule());
 	    //companyModuleDTO.setHasLundinTimesheetModule(UserContext.isLundinTimesheetModule());
	    //companyModuleDTO.setHasLionTimesheetModule(UserContext.isLionTimesheetModule());
	    //companyModuleDTO.setHasCoherentTimesheetModule(UserContext.isCoherentTimesheetModule());
		return companyModuleDTO;
	}

}
