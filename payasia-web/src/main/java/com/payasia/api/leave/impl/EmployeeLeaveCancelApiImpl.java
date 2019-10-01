package com.payasia.api.leave.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.leave.EmployeeLeaveCancelApi;
import com.payasia.api.leave.model.MultiSortMeta;
import com.payasia.api.leave.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.PrivilegeUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LeaveBalanceSummaryDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeaveBalanceSummaryResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PropReader;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value=ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE+"/leave/")
public class EmployeeLeaveCancelApiImpl implements EmployeeLeaveCancelApi {

	@Resource
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	
	@Resource
	private EmployeeDAO employeeDAO;
	
	@Resource
	private EmployeeDetailLogic employeeDetailLogic;
	
	@Resource
	private MessageSource messageSource;
	
	@Resource
	private MonthMasterDAO monthMasterDAO;
	
	@Autowired
	private PrivilegeUtils privilegeUtils;
	
	
	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.employee.image.width']}")
	private Integer employeeImageWidth;

	/** The employee image height. */
	@Value("#{payasiaptProperties['payasia.employee.image.height']}")
	private Integer employeeImageHeight;
	
	@Override
	@PostMapping(value = "view-cancel")
	public ResponseEntity<?> doShowCancelLeaves(@RequestBody SearchParam searchParamObj) {
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveFormResponse addLeaveResponse = null;
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(searchParamObj.getSortField());
		sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? "ASC" : "DESC");
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParamObj.getPage());
		pageDTO.setPageSize(searchParamObj.getRows());
		 if (multisortlist != null && !multisortlist.isEmpty()) {
			sortDTO.setColumnName(multisortlist.get(0).getField());
			sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? "ASC" : "DESC");
		 }
		 
		addLeaveResponse = leaveBalanceSummaryLogic.getCompletedLeaves(loginEmployeeID,pageDTO, sortDTO, "", companyID);
		if(addLeaveResponse.getRows()==null){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.view.cancel.list", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
     	return new ResponseEntity<>(addLeaveResponse, HttpStatus.OK);
	}

	
	@Override
	@PostMapping(value = "view-reviewers")
	public ResponseEntity<?> doShowLeaveReviewers(@RequestBody String jsonStr) {
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveFormResponse addLeaveResponse = null;
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		Long leaveApplicationId = FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("leaveApplicationId"));
		LeaveApplication leaveApplication =  leaveBalanceSummaryLogic.findByLeaveApplicationIdAndEmpId(leaveApplicationId, loginEmployeeID, companyID);
		if(leaveApplication.getLeaveApplicationReviewers() == null )
		{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, "409", PropReader.getMessage("leave.show.reviewers.data")),HttpStatus.CONFLICT);
		}
		addLeaveResponse = leaveBalanceSummaryLogic
				.getLeaveReviewers(leaveApplicationId);
		return new ResponseEntity<>(addLeaveResponse, HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "view-calendar-month-list")
	public ResponseEntity<?> doShowLeaveCalendarMonthList(@RequestBody String jsonStr) {
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		String month = jsonObj.getString("month");
		String year = jsonObj.getString("year");
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = leaveBalanceSummaryLogic.getLeaveCalMonthListByManager(year, month, companyID,loginEmployeeID,privilegeUtils.getRole());
		leaveBalanceSummaryForm.setEmployeeLogInId(loginEmployeeID);
			return new ResponseEntity<>(leaveBalanceSummaryForm, HttpStatus.OK);

	}

	@Override
	@PostMapping(value ="view-holdiday-calendar")
	public ResponseEntity<?> doShowHolidayCalender(@RequestBody String jsonStr) {
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		int year = jsonObj.getInt("year");
		List<LeaveBalanceSummaryForm> holidayCalendarList = leaveBalanceSummaryLogic.getHolidaycalendar(companyID,loginEmployeeID, year);
		List<LeaveBalanceSummaryDTO> map = getMonthList(holidayCalendarList);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	
	private List<LeaveBalanceSummaryDTO> getMonthList(List<LeaveBalanceSummaryForm> holidayCalendarList) {

		List<MonthMaster> monthMasterList = monthMasterDAO.findAll();
		List<LeaveBalanceSummaryDTO> listOfHolidays = new ArrayList<>();
		List<LeaveBalanceSummaryForm> list = new ArrayList<>();
		List<Integer> listId = new ArrayList<>();
		for(MonthMaster master : monthMasterList){
			for(LeaveBalanceSummaryForm balanceSummaryForm : holidayCalendarList) {
				int monthId = new Long(master.getMonthId()).intValue();
				if(monthId==balanceSummaryForm.getMonth()) {
					list.add(balanceSummaryForm);
					listId.add(balanceSummaryForm.getMonth());
				}
			}
			if (!listId.contains(master.getMonthId())&& !list.isEmpty()) {
				listOfHolidays.add(new LeaveBalanceSummaryDTO(master.getMonthName(),list));
				list = new ArrayList<>();
				listId = new ArrayList<>();
			} else {
				list = new ArrayList<>();
			}
		}
		return listOfHolidays;
	}
	
	
	
	@Override
	@PostMapping(value = "view-employee-onleave-bydate")
	public ResponseEntity<?> doshowEmployeeOnLeaveByDate(@RequestBody SearchParam searchParamObj,@RequestParam("leaveIds") String leaveID[] ) {
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		 final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		 
			SortCondition sortDTO = new SortCondition();
			sortDTO.setColumnName(searchParamObj.getSortField());
			sortDTO.setOrderType(searchParamObj.getSortOrder().equals("1") ? "ASC" : "DESC");
			PageRequest pageDTO = new PageRequest();
			pageDTO.setPageNumber(searchParamObj.getPage());
			pageDTO.setPageSize(searchParamObj.getRows());
			 if (multisortlist != null && !multisortlist.isEmpty()) {
				sortDTO.setColumnName(multisortlist.get(0).getField());
				sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? "ASC" : "DESC");
			 }
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic.getEmpOnLeaveByDate(leaveID,
				companyID, loginEmployeeID, pageDTO, sortDTO, false);
		if(leaveSchemeResponse.getRows().isEmpty()){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.leave.employee.onleave.bydate", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(leaveSchemeResponse,HttpStatus.OK);
	}
	
	// TODO REMOVED 
	@Override
	@PostMapping(value = "view-employee-image")
	public ResponseEntity<?> employeeViewProfileImage(@RequestBody String jsonStr) throws IOException {
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		Long employeeId = jsonObj.getLong("employeeId");
		Map<String, Object> imageMap = new HashMap<String, Object>();
		byte[] byteFile = null;
		String empname;
		Employee emp = employeeDAO.findById(employeeId);
		empname = emp.getFirstName() + " " + emp.getMiddleName() + " " + emp.getLastName();
		imageMap.put("employeeFullname", empname);
		byteFile = employeeDetailLogic.getEmployeeImage(employeeId, null, employeeImageWidth, employeeImageHeight);
		if (byteFile!= null) {
			imageMap.put("employeeImageData", byteFile);
			return new ResponseEntity<>(imageMap, HttpStatus.OK);
		} 
		else{
			return new ResponseEntity<>(imageMap, HttpStatus.OK);
		}
	}
	
	
	
	@Override
	@PostMapping(value = "cancel-Leave")
	public ResponseEntity<?> doCancelLeave(@RequestBody String jsonStr) {	
		Long loginEmployeeID=Long.valueOf(UserContext.getUserId());
		Long companyID = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, UserContext.getLocale()));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, UserContext.getLocale()));
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj=JSONObject.fromObject(jsonStr, jsonConfig);
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
		leaveBalanceSummaryForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(jsonObj.getJSONObject("cancelLeaveModel").getLong("leaveApplicationId")));
		leaveBalanceSummaryForm.setReason(jsonObj.getJSONObject("cancelLeaveModel").getString("reason"));
		leaveBalanceSummaryForm.setLeaveCC(jsonObj.getJSONObject("cancelLeaveModel").getString("leaveCC"));

		leaveBalanceSummaryLogic.canCelLeave(leaveBalanceSummaryForm,
				loginEmployeeID, companyID, sessionDTO);
		
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),messageSource.getMessage("payasia.leave.cancel.leave", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.OK);
	}


}
