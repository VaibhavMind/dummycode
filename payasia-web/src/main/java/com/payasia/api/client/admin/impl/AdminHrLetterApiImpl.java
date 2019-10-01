package com.payasia.api.client.admin.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.client.admin.AdminHrLetterApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.AssignLeaveSchemeForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HRLetterResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AssignLeaveSchemeLogic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.HRLetterLogic;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param : This class used for HR-Letter API(Admin Side)
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN + "hris/" + "hrletter/")
public class AdminHrLetterApiImpl implements AdminHrLetterApi{

	@Autowired
	@Qualifier("HRLetterLogic")
	private HRLetterLogic hrLetterLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Resource
	private AssignLeaveSchemeLogic assignLeaveSchemeLogic;
	
	@Resource
	private CompanyDocumentCenterLogic companyDocumentCenterLogic;
	
	@Override
	@PostMapping(value = "search")
	public ResponseEntity<?> searchAdminHrLetters(@RequestBody SearchParam searchParamObj) {
		final Long companyId = UserContext.getClientAdminId();
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());

		HRLetterResponse hrLetterResponse = null;
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		if ((filterllist != null && !filterllist.isEmpty())) {
			hrLetterResponse = hrLetterLogic.searchHRLetter(companyId, filterllist.get(0).getField(), filterllist.get(0).getValue(), searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
		} else{
			hrLetterResponse = hrLetterLogic.searchHRLetter(companyId, "", "", searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition());
		}

		if(hrLetterResponse != null && !hrLetterResponse.getRows().isEmpty()) {
			return new ResponseEntity<>(hrLetterResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "add")
	public ResponseEntity<?> saveHrLetter(@RequestBody HRLetterForm hrLetterForm) {
		final Long companyId = UserContext.getClientAdminId();
		String hrLetterstatus = hrLetterLogic.saveHRLetter(hrLetterForm, companyId);
		if(StringUtils.equalsIgnoreCase(hrLetterstatus, PayAsiaConstants.NOTAVAILABLE)) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.hr.letters.hr.letter.successfully.saved", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.hr.letters.duplicate.letter.name.please.add.another.hr.letter.name", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);		
	}
	
	@Override
	@PostMapping(value = "details")
	public ResponseEntity<?> getHrLetterDetails(@RequestParam(value = "letterId", required = true) long letterId) {
		final Long companyId = UserContext.getClientAdminId();
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		
		HRLetterForm hrLetterForm = hrLetterLogic.getHRLetter(letterId, companyId);
		if(hrLetterForm!=null) {
			return new ResponseEntity<>(hrLetterForm, HttpStatus.OK);
		}	
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "edit")
	public ResponseEntity<?> updateHrLetter(@RequestBody HRLetterForm hrLetterForm) {
		final Long companyId = UserContext.getClientAdminId();
		hrLetterForm.setLetterId(FormatPreserveCryptoUtil.decrypt(hrLetterForm.getLetterId()));
		
		String hrLetterstatus = hrLetterLogic.updateHRLetter(hrLetterForm, companyId);

		if(StringUtils.equalsIgnoreCase(hrLetterstatus, PayAsiaConstants.NOTAVAILABLE)) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.hr.letters.hr.letter.successfully.update", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.hr.letters.duplicate.letter.name.please.add.another.hr.letter.name", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);		
	}
	
	@Override
	@PostMapping(value = "employees")
	public ResponseEntity<?> getEmployeeIds(@RequestParam(value = "searchString", required = true) String searchString) {
		final Long companyId = UserContext.getClientAdminId();
		final Long loggedInEmployeeId = Long.parseLong(UserContext.getUserId());
		
		List<AssignLeaveSchemeForm> assignLeaveSchemeFormList = assignLeaveSchemeLogic.getEmployeeId(companyId, searchString, loggedInEmployeeId);
		Map<String, Object> empIdMap = new HashMap<>();
		empIdMap.put("employeeIdList", assignLeaveSchemeFormList);
		return new ResponseEntity<>(empIdMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "preview")
	public ResponseEntity<?> previewHrLetter(@RequestBody String jsonStr) throws JSONException {
		final Long companyId = UserContext.getClientAdminId();
		final Long loggedInEmployeeId = Long.valueOf(UserContext.getUserId());
		boolean isAdmin = true;
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long letterId = jsonObj.getLong("letterId");
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		Long employeeId = jsonObj.getLong("employeeId");
		
		HRLetterForm hrLetterForm = hrLetterLogic.getHRLetter(letterId, companyId);
		String convertedPreviewMessage = hrLetterLogic.previewHRLetterBodyText(companyId, letterId, employeeId, loggedInEmployeeId, null, null, isAdmin);
		if(StringUtils.isEmpty(convertedPreviewMessage) || "send.hr.letter.configuration.is.not.defined".equalsIgnoreCase(convertedPreviewMessage)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(convertedPreviewMessage, new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		
		String isSaveHrLetterInDocumentCenter = hrLetterLogic.isSaveHrLetterInDocumentCenter(companyId);
//		hrLetterForm.setSaveInDocumentCenter(isSaveHrLetterInDocumentCenter!=null && isSaveHrLetterInDocumentCenter.equalsIgnoreCase("TRUE")?true:false);
		hrLetterForm.setSaveInDocumentCenter(StringUtils.isNotBlank(isSaveHrLetterInDocumentCenter) && StringUtils.equalsIgnoreCase(isSaveHrLetterInDocumentCenter, PayAsiaConstants.TRUE));
		hrLetterForm.setBody(convertedPreviewMessage);
		return new ResponseEntity<>(hrLetterForm, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "send")
	public ResponseEntity<?> sendHrLetter(@RequestBody String jsonStr) {
		final Long companyId = UserContext.getClientAdminId();
		final Long loginEmployeeID = Long.valueOf(UserContext.getUserId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(jsonStr, jsonConfig);
		Long letterId = jsonObject.getLong("letterId");
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		Long employeeId = jsonObject.getLong("employeeId");
		String hrLetterBodyText = jsonObject.getString("hrLetterBodyText");
		String ccEmails = jsonObject.getString("ccEmails");
		Boolean saveInDocCenterCheck = jsonObject.getBoolean("saveInDocCenterCheck");
		
		String status = hrLetterLogic.sendHrLetterWithPDF(companyId, letterId, employeeId, loginEmployeeID, hrLetterBodyText, ccEmails, saveInDocCenterCheck);

		if(StringUtils.isNotBlank(status) && StringUtils.equalsIgnoreCase(status, "payasia.hr.letters.hrletter.send.successfully")) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage(status, new Object[] {}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage(status, new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "delete")
	public ResponseEntity<?> deleteHrLetter(@RequestParam(value="letterId") Long letterId){
		final Long companyId = UserContext.getClientAdminId();
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		
		String status = hrLetterLogic.deleteHRLetter(letterId, companyId);
		
		if(StringUtils.isNotBlank(status) && StringUtils.equalsIgnoreCase(status, "success")) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.hr.letters.hr.letter.successfully.deleted", new Object[] {}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.hr.letters.hrletter.can.not.deleted", new Object[] {}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "edit-employee-filter-list")
	public ResponseEntity<?> getEditEmployeeFilterList(@RequestParam(value = "letterId", required = true) Long letterId) {
		final Long companyId = UserContext.getClientAdminId();
		letterId = FormatPreserveCryptoUtil.decrypt(letterId);
		
		List<EmployeeFilterListForm> filterList = hrLetterLogic.getEditEmployeeFilterList(letterId, companyId);
		Map<String, Object> editEmpFilterListMap = new HashMap<>();
		editEmpFilterListMap.put("editEmployeeFilterList", filterList);
		return new ResponseEntity<>(editEmpFilterListMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "employee-filter-list")
	public ResponseEntity<?> getEmployeeFilterList() {
		final Long companyId = UserContext.getClientAdminId();

		List<EmployeeFilterListForm> filterList = companyDocumentCenterLogic.getEmployeeFilterList(companyId);
		Map<String, Object> empFilterListMap = new HashMap<>();
		empFilterListMap.put("employeeFilterList", filterList);
		return new ResponseEntity<>(empFilterListMap, HttpStatus.OK);
	}
	
}
