package com.payasia.api.emp.hris.impl;

import static com.payasia.api.utils.ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.payasia.api.emp.hris.EmpMyProfileDetails;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.DynamicFormRecordDTO;
import com.payasia.common.dto.DynamicFormTableDataForm;
import com.payasia.common.dto.DynamicFormTableRecordDTO;
import com.payasia.common.form.CalculatoryForm;
import com.payasia.common.form.DataDictionaryForm;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmployeeDetailForm;
import com.payasia.common.form.EmployeeDocumentForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.dao.bean.HRISChangeRequest;
import com.payasia.logic.hris.EmpMyProfileDetailsLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value=API_ROOT_PATH_FOR_EMPLOYEE_HRIS+"myprofile")
public class EmpMyProfileDetailsImpl implements EmpMyProfileDetails{
	
	@Resource
	private EmpMyProfileDetailsLogic empMyProfileDetailsLogic;
	
	@Override
	@RequestMapping(value="/getuserdetails",method=RequestMethod.POST)
	public ResponseEntity<?> getUserDetails(){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
				
		EmployeeDetailForm employeeDetailForm= empMyProfileDetailsLogic.getUserDetailsData(companyId,employeeId);
		
		Map<String,Object> response= new HashMap<String,Object>();
		response.put("employeeDetail", employeeDetailForm);
		response.put("status", "success");
		response.put("code", 200);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value="/getdynamicformdata",method=RequestMethod.POST)
	public ResponseEntity<?> getDynamicFormData(@RequestBody DynamicFormDataForm dynamicFormDataForm){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		dynamicFormDataForm.setCompanyId(companyId);
		dynamicFormDataForm.setEmployeeId(employeeId);
		
		DynamicFormRecordDTO dynamicFormRecordDTO= empMyProfileDetailsLogic.getDynamicFormRecordData(dynamicFormDataForm);
		Map<String, Object> object= new HashMap<String, Object>();
		if(dynamicFormRecordDTO!=null){
			object.put("dynamicFormDetails", dynamicFormRecordDTO);
			object.put("status", "success");
			object.put("code", 200);
		}else{
			object.put("status", "success");
			object.put("code", 300);
		}
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value="/getdynamicformtabledata",method=RequestMethod.POST)
	public ResponseEntity<?> getDynamicFormTableData(@RequestBody DynamicFormTableDataForm dynamicFormTableDataForm){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		dynamicFormTableDataForm.setCompanyId(companyId);
		dynamicFormTableDataForm.setEmployeeId(employeeId);
		
		List<DynamicFormTableRecordDTO> dynamicFormTableRecordList= empMyProfileDetailsLogic.getDynamicFormTableRecordData(dynamicFormTableDataForm);
		Map<String, Object> object= new HashMap<String, Object>();
		object.put("dynamicFormTableRecordList", dynamicFormTableRecordList);
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
		
	@Override
	@RequestMapping(value="/getreference",method=RequestMethod.POST)
	public ResponseEntity<?> getReference(@RequestBody DataDictionaryForm dataDictionaryForm){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		dataDictionaryForm.setCompanyId(companyId);
		dataDictionaryForm.setEmployeeId(employeeId);
			
		String refValue= empMyProfileDetailsLogic.getReferenceValue(dataDictionaryForm);
		JSONObject object= new JSONObject();
		object.put("refValue", refValue);
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value="/getcalculatory",method=RequestMethod.POST)
	public ResponseEntity<?> getCalculatory(@RequestBody CalculatoryForm calculatoryFieldForm){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		calculatoryFieldForm.setCompanyId(companyId);
		calculatoryFieldForm.setEmployeeId(employeeId);
			
		String calValue= empMyProfileDetailsLogic.getCalculatoryValue(calculatoryFieldForm);
		JSONObject object= new JSONObject();
		object.put("calValue", calValue);
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value="/getchangesreqid",method=RequestMethod.POST)
	public ResponseEntity<?> getChangesReqId(@RequestBody DataDictionaryForm dataDictionaryForm){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		dataDictionaryForm.setCompanyId(companyId);
		dataDictionaryForm.setEmployeeId(employeeId);
			
		HRISChangeRequest hrisChangeRequest= empMyProfileDetailsLogic.getChangesReqId(dataDictionaryForm);
		JSONObject object= new JSONObject();
		if(hrisChangeRequest!=null){
			object.put("changeReqId", hrisChangeRequest.getHrisChangeRequestId());
			object.put("oldValue", (hrisChangeRequest.getNewValue()==null || hrisChangeRequest.getNewValue().equalsIgnoreCase("null"))?"":hrisChangeRequest.getOldValue());
			object.put("newValue", (hrisChangeRequest.getNewValue()==null || hrisChangeRequest.getNewValue().equalsIgnoreCase("null"))?"":hrisChangeRequest.getNewValue());
			object.put("code", 200);
		}
		else{
			object.put("code", 300);
		}
		object.put("status", "success");
		
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value="/saveuserdetails",method=RequestMethod.POST)
	public ResponseEntity<?> saveUserDetails(@RequestBody String userDetails){
		
		DynamicFormDataForm dynamicFormDataForm = new DynamicFormDataForm();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(userDetails, jsonConfig);
		dynamicFormDataForm.setEmployeeId(Long.valueOf(UserContext.getUserId()));
		dynamicFormDataForm.setCompanyId(Long.valueOf(UserContext.getWorkingCompanyId()));
		dynamicFormDataForm.setLanguageId(UserContext.getLanguageId());
		
		JSONObject formDetail=jsonObj.getJSONObject("formDetail");
				
		dynamicFormDataForm.setFormId(formDetail.getString("formId"));
		dynamicFormDataForm.setIsDefault(Boolean.valueOf(formDetail.getString("isDefault")));
		dynamicFormDataForm.setFormRecordId(formDetail.getString("formRecordId"));
		JSONObject employeeData=jsonObj.getJSONObject("formValue");
		
		Map<String,Object> status=empMyProfileDetailsLogic.saveUserDetails(dynamicFormDataForm,employeeData);
		if(status.isEmpty()){
			status.put("status", "success");
			status.put("code", 200);
		}else{
			status.put("status",status.get("error"));
			status.put("code", 300);
			status.remove("error");
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK); 
	}
	
	@Override
	@RequestMapping(value="/savetabledetails",method=RequestMethod.POST)
	public ResponseEntity<?> saveTableDetails(@RequestBody String userDetails){
		
		DynamicFormDataForm dynamicFormDataForm = new DynamicFormDataForm();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(userDetails, jsonConfig);
		dynamicFormDataForm.setEmployeeId(Long.valueOf(UserContext.getUserId()));
		dynamicFormDataForm.setCompanyId(Long.valueOf(UserContext.getWorkingCompanyId()));
		dynamicFormDataForm.setLanguageId(UserContext.getLanguageId());
		
		JSONObject formDetail=jsonObj.getJSONObject("formDetail");
		
		dynamicFormDataForm.setFormId(formDetail.getString("formId"));
		dynamicFormDataForm.setIsDefault(Boolean.valueOf(formDetail.getString("isDefault")));
		dynamicFormDataForm.setFormRecordId(formDetail.getString("formRecordId"));
		dynamicFormDataForm.setFormTableRecordId(formDetail.getString("formTableRecordId"));
		dynamicFormDataForm.setSeqNo(formDetail.getString("seqNo"));
		dynamicFormDataForm.setDataDictionaryId(formDetail.getString("dataDictionaryId"));		
		
		JSONObject employeeData=jsonObj.getJSONObject("formValue");
		
		Map<String,Object> status=empMyProfileDetailsLogic.saveTableDetails(dynamicFormDataForm,employeeData);
		if(status.isEmpty()){
			status.put("formRecordId", dynamicFormDataForm.getFormRecordId());
			status.put("formTableRecordId", dynamicFormDataForm.getFormTableRecordId());			
			status.put("status", "success");
			status.put("code", 200);
		}else{
			status.put("status",status.get("error"));
			status.put("code", 300);
			status.remove("error");
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK); 
	}
	
	@Override
	@RequestMapping(value="/deletetabledetails",method=RequestMethod.POST)
	public ResponseEntity<?> deleteTableDetails(@RequestBody String userDetails){
		
		DynamicFormDataForm dynamicFormDataForm = new DynamicFormDataForm();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(userDetails, jsonConfig);
		dynamicFormDataForm.setEmployeeId(Long.valueOf(UserContext.getUserId()));
		dynamicFormDataForm.setCompanyId(Long.valueOf(UserContext.getWorkingCompanyId()));
		dynamicFormDataForm.setLanguageId(UserContext.getLanguageId());
		
		JSONObject formDetail=jsonObj.getJSONObject("formDetail");
		
		dynamicFormDataForm.setFormId(formDetail.getString("formId"));
		dynamicFormDataForm.setIsDefault(Boolean.valueOf(formDetail.getString("isDefault")));
		dynamicFormDataForm.setFormRecordId(formDetail.getString("formRecordId"));
		dynamicFormDataForm.setFormTableRecordId(formDetail.getString("formTableRecordId"));
		dynamicFormDataForm.setSeqNo(formDetail.getString("seqNo"));
		dynamicFormDataForm.setDataDictionaryId(formDetail.getString("dataDictionaryId"));		
		
		Map<String,Object> status=empMyProfileDetailsLogic.deleteTableDetails(dynamicFormDataForm);
		if(status.isEmpty()){
			status.put("formRecordId", dynamicFormDataForm.getFormRecordId());
			status.put("status", "success");
			status.put("code", 200);
		}else{
			status.put("status",status.get("error"));
			status.put("code", 300);
			status.remove("error");
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK); 
	}
	
	@Override
	@RequestMapping(value="/savedocumentdetails",method=RequestMethod.POST)
	public ResponseEntity<?> saveDocumentDetails(@RequestParam("data") String userDetails,@RequestParam("files") MultipartFile files[]){
		
		DynamicFormDataForm dynamicFormDataForm = new DynamicFormDataForm();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(userDetails, jsonConfig);
		dynamicFormDataForm.setEmployeeId(Long.valueOf(UserContext.getUserId()));
		dynamicFormDataForm.setCompanyId(Long.valueOf(UserContext.getWorkingCompanyId()));
		dynamicFormDataForm.setLanguageId(UserContext.getLanguageId());
		
		JSONObject formDetail=jsonObj.getJSONObject("formDetail");
		
		dynamicFormDataForm.setFormId(formDetail.getString("formId"));
		dynamicFormDataForm.setIsDefault(Boolean.valueOf(formDetail.getString("isDefault")));
		dynamicFormDataForm.setFormRecordId(formDetail.getString("formRecordId"));
		dynamicFormDataForm.setFormTableRecordId(formDetail.getString("formTableRecordId"));
		dynamicFormDataForm.setSeqNo(formDetail.getString("seqNo"));
		dynamicFormDataForm.setDataDictionaryId(formDetail.getString("dataDictionaryId"));		
				
		JSONObject employeeData=jsonObj.getJSONObject("formValue");
		
		Map<String,Object> status=empMyProfileDetailsLogic.saveDocumentDetails(dynamicFormDataForm,employeeData,files);
		if(status.isEmpty()){
			status.put("formRecordId", dynamicFormDataForm.getFormRecordId());
			status.put("formTableRecordId", dynamicFormDataForm.getFormTableRecordId());			
			status.put("status", "success");
			status.put("code", 200);
		}else{
			status.put("status",status.get("error"));
			status.put("code", 300);
			status.remove("error");
		}
		
		return new ResponseEntity<>(status, HttpStatus.OK); 
	}
	
	
	@Override
	@RequestMapping(value ="/showhistory", method = RequestMethod.POST)
	@ResponseBody 
	public ResponseEntity<?> showHistory(@RequestBody EmployeeDocumentForm employeeDocumentForm) {
		 
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		employeeDocumentForm.setCompanyId(companyId);
		employeeDocumentForm.setEmployeeId(employeeId);
		
		EmployeeListFormPage importHistory = empMyProfileDetailsLogic.getEmployeeDocHistory(employeeDocumentForm);
		JSONObject object= new JSONObject();		
		if(importHistory!=null && importHistory.getEmpDocHistoryList()!=null && !importHistory.getEmpDocHistoryList().isEmpty()){
			object.put("empDocHistoryList", importHistory.getEmpDocHistoryList());
			object.put("code", 200);
		}else{
			object.put("code", 300);
		}
		object.put("status", "success");
		return new ResponseEntity<>(object, HttpStatus.OK); 
	}
	
	@Override
	@RequestMapping(value ="/getdocumentfile", method = RequestMethod.POST)
	@ResponseBody 
	public ResponseEntity<?> getDocumentFile(@RequestBody String fileName) {
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());		
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(fileName, jsonConfig);
		if(jsonObj!=null && !jsonObj.isEmpty() && jsonObj.containsKey("fileName")){
			fileName=(String) jsonObj.get("fileName");
		}		
		
		DynamicFormTableDocumentDTO dynamicFormTableDocumentDTO=empMyProfileDetailsLogic
				.getDocumentFile(companyId,employeeId, fileName);
		
		Map<String,Object> response= new HashMap<String,Object>();
		if(dynamicFormTableDocumentDTO!=null){
			response.put("code", 200);
			response.put("status", "success");
			response.put("dynamicFormTableDocument", dynamicFormTableDocumentDTO);
		 }else{
			 response.put("code", 300);
			 response.put("status", "success");
		 }
		 return new ResponseEntity<>(response, HttpStatus.OK); 
	}
}
