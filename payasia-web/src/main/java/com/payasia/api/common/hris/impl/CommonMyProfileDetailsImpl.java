package com.payasia.api.common.hris.impl;

import static com.payasia.api.utils.ApiUtils.API_ROOT_PATH_FOR_COMMON_HRIS;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.common.hris.CommonMyProfileDetails;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.DynamicFormDetailsDTO;
import com.payasia.common.dto.EmployeeListDTO;
import com.payasia.common.dto.SectionDetailsDTO;
import com.payasia.common.form.DataDictionaryForm;
import com.payasia.logic.hris.CommonMyProfileDetailsLogic;

import net.sf.json.JSONObject;

@RestController
@RequestMapping(value=API_ROOT_PATH_FOR_COMMON_HRIS+"myprofile")
public class CommonMyProfileDetailsImpl implements CommonMyProfileDetails{
	
	@Resource
	private CommonMyProfileDetailsLogic commonMyProfileDetailsLogic;
	
	@Override
	@RequestMapping(value = "/gethriscompanyconfig", method = RequestMethod.POST)
	public ResponseEntity<?> getHRISCompanyConfig(){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		
		Map<String,Boolean> hrisPreferenceMap= commonMyProfileDetailsLogic.getHRISCompanyConfig(companyId);
		JSONObject object= new JSONObject();
		object.put("employeeChangeWorkflow", hrisPreferenceMap.get("employeeChangeWorkflow"));
		object.put("allowEmployeeUploadDoc", hrisPreferenceMap.get("allowEmployeeUploadDoc"));
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value = "/getsectiondetails", method = RequestMethod.POST)
	public ResponseEntity<?> getSectionDetails(){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		
		List<SectionDetailsDTO> sectionDetailsDTOList= commonMyProfileDetailsLogic.getSectionDetails(companyId,employeeId);
		JSONObject object= new JSONObject();
		object.put("sectionDetails", sectionDetailsDTOList);
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value="/getdynamicform",method=RequestMethod.POST)
	public ResponseEntity<?> getDynamicForm(@RequestBody DynamicFormDataForm dynamicFormDataForm){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long languageId= Long.valueOf(UserContext.getLanguageId());
		DynamicFormDetailsDTO dynamicFormDetailsDTO= commonMyProfileDetailsLogic.getDynamicForm(companyId,dynamicFormDataForm,languageId);
		JSONObject object= new JSONObject();
		object.put("dynamicFormDetails", dynamicFormDetailsDTO);
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
	
	@Override
	@RequestMapping(value="/getcodedesc",method=RequestMethod.POST)
	public ResponseEntity<?> getCodeDesc(@RequestBody DataDictionaryForm dataDictionaryForm){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
			
		List<CodeDescDTO> codeDescDTOList= commonMyProfileDetailsLogic.getCodeDesc(companyId,dataDictionaryForm);
		JSONObject object= new JSONObject();
		object.put("codeDescList", codeDescDTOList);
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}	
	
	@Override
	@RequestMapping(value="/getemployeelist",method=RequestMethod.POST)
	public ResponseEntity<?> getEmployeeList(){
		
		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
			
		List<EmployeeListDTO> employeeListDTO= commonMyProfileDetailsLogic.getEmployeeList(companyId);
		JSONObject object= new JSONObject();
		object.put("employeeList", employeeListDTO);
		object.put("status", "success");
		object.put("code", 200);
		
		return new ResponseEntity<>(object, HttpStatus.OK);
	}
}
