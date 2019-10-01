package com.payasia.api.client.admin.impl;

import static com.payasia.api.utils.ApiUtils.API_ROOT_PATH_FOR_ADMIN;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.client.admin.DocumentCenterApi;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.CompanyDocumentCenterResponseForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.web.security.XSSRequestWrapper;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@RestController
@RequestMapping(value = API_ROOT_PATH_FOR_ADMIN+"/documentcenter")
public class DocumentCenterApiImpl implements DocumentCenterApi {
	
	@Resource
	private CompanyDocumentCenterLogic companyDocumentCenterLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	private String appDeployLocation;
	
	@Autowired
	@Qualifier("AWSS3Logic")
	private AWSS3Logic awss3LogicImpl;
	
	@Resource
	private GeneralLogic generalLogic;
	
	@Override
	@DeleteMapping(value="delete-document")
	public ResponseEntity<?> deleteCompanyDocument(@RequestParam(value = "docId", required = true) Long docId) {
		final Long companyId = UserContext.getClientAdminId();
		docId=FormatPreserveCryptoUtil.decrypt(docId);
		String deleteStatus=companyDocumentCenterLogic.deleteCompanyDocument(docId, companyId);
		if(StringUtils.equalsIgnoreCase(deleteStatus,PayAsiaConstants.PAYASIA_SUCCESS)){
			return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.documentcenter.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.documentcenter.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "search-document")
	public ResponseEntity<?> searchDocument(@RequestBody SearchParam searchParamObj,@RequestParam(value = "categoryId", required = true) Long categoryId) {
		final Long companyId = UserContext.getClientAdminId();
		final Long employeeId = Long.parseLong(UserContext.getUserId());

		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		
		CompanyDocumentCenterResponseForm companyDocumentCenterForm = companyDocumentCenterLogic
				.searchDocument((searchParamObj.getFilters().length>0)?searchParamObj.getFilters()[0].getField():"", (searchParamObj.getFilters().length>0)?searchParamObj.getFilters()[0].getValue():"", 
						searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId, categoryId, employeeId);
		 if(companyDocumentCenterForm.getRows()!=null && !companyDocumentCenterForm.getRows().isEmpty()){
		     return new ResponseEntity<>(companyDocumentCenterForm, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
				messageSource.getMessage("payasia.documentcenter.document.not.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);

	}
	
	@Override
	@PostMapping(value = "upload-document")
	public ResponseEntity<?> uploadDocument(@RequestParam("uploadDocument") String jsonStr,
			@RequestParam("files") CommonsMultipartFile files) {
		CompanyDocumentCenterForm companyDocumentResponse = new CompanyDocumentCenterForm();

		final Long companyId = UserContext.getClientAdminId();

		CompanyDocumentCenterForm companyDocumentCenterForm=new CompanyDocumentCenterForm();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		companyDocumentCenterForm.setCategoryId((jsonObj.getLong("categoryId")));
		companyDocumentCenterForm.setCategoryName(companyDocumentCenterLogic.getDocumentCategoryName(companyDocumentCenterForm.getCategoryId()));
		companyDocumentCenterForm.setDescription(jsonObj.getString("description"));
		
		if(companyDocumentCenterForm.getCategoryName().equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL) ||
			companyDocumentCenterForm.getCategoryName().equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_EMPLOYEE_LETTER)) {
			  companyDocumentCenterForm.setFileData(files);
			  companyDocumentCenterForm.setYear("-1");
			  companyDocumentCenterForm.setFileNameContainsCompName(false);
			
		}
		else if(companyDocumentCenterForm.getCategoryName().equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_TAX_DOCUMENT)) {
			companyDocumentCenterForm.setYear(jsonObj.getString("year"));
			companyDocumentCenterForm.setFileNameContainsCompName(jsonObj.getBoolean("fileNameContainsCompName"));
			companyDocumentCenterForm.setFileData(files);
		}
		else if(companyDocumentCenterForm.getCategoryName().equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_PAYSLIP)) {
			companyDocumentCenterForm.setYear(jsonObj.getString("year"));
			companyDocumentCenterForm.setFileNameContainsCompName(false);
			companyDocumentCenterForm.setFileData(files);
		}
		
		
		String fileName = companyDocumentCenterForm.getFileData().getOriginalFilename();

		
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length()).toLowerCase();
		Boolean isFileValid = false;
		isFileValid=XSSRequestWrapper.isValidString(companyDocumentCenterForm.getDescription());
		
		List<CompanyDocumentLogDTO> companyDocumentLogs = new ArrayList<>();
		if (companyDocumentCenterForm.getCategoryName().equalsIgnoreCase(PayAsiaConstants.DOCUMENT_CATEGORY_GENERAL)) {
			isFileValid = FileUtils.isValidFile(companyDocumentCenterForm.getFileData(), fileName, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		}else{
			if (fileExt.equalsIgnoreCase(PayAsiaConstants.FILE_TYPE_ZIP)) {
			   isFileValid = FileUtils.isValidFile(companyDocumentCenterForm.getFileData(), fileName, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT,PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE,PayAsiaConstants.FILE_SIZE);
			}
		}
		if (isFileValid) {
			companyDocumentResponse = companyDocumentCenterLogic.uploadDocument(companyDocumentCenterForm, companyId);
			if (companyDocumentResponse.isZipEntriesInvalid()) {
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.documentcenter.zip.entries.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
			}
			if(StringUtils.equalsIgnoreCase(PayAsiaConstants.PAYASIA_ERROR, companyDocumentResponse.getSuccess())) {
				return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.documentcenter.file.already.exist", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(companyDocumentResponse, HttpStatus.OK);
		} else {
			CompanyDocumentLogDTO companyDocumentLogDTO = new CompanyDocumentLogDTO();
			companyDocumentLogDTO.setName(fileName);
			companyDocumentLogDTO.setMessage(PayAsiaConstants.INVALID_FILE);
			companyDocumentLogs.add(companyDocumentLogDTO);
			companyDocumentResponse.setCompanyDocumentLogs(companyDocumentLogs);
		}
		 return new ResponseEntity<>(companyDocumentResponse, HttpStatus.NOT_FOUND);
		}
	
	 @Override
	 @PostMapping(value = "edit-uploaded-data")
	 public ResponseEntity<?> editUploadedData(@RequestParam(value = "docId", required = true) Long docId) {
		docId = FormatPreserveCryptoUtil.decrypt(docId);
		CompanyDocumentCenterForm uploadedDoc = companyDocumentCenterLogic.getUploadedDoc(docId);

		if(uploadedDoc!=null) {
			return new ResponseEntity<>(uploadedDoc, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
				messageSource.getMessage("payasia.documentcenter.data.not.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	 @Override
	 @GetMapping(value = "download-documents")
	 public ResponseEntity<?> downloadDocuments(@RequestParam(value = "documentId", required = true) Long documentId) throws IOException{
		 Long documentID = FormatPreserveCryptoUtil.decrypt(documentId);
		 final Long companyId = UserContext.getClientAdminId();
		 final Long employeeId = Long.parseLong(UserContext.getUserId());
		 String filePath = companyDocumentCenterLogic.viewDocument(documentID,companyId, employeeId);
		 Map<String, Object> downloadMap = new HashMap<>();
		 byte[] byteFile;
			if (StringUtils.isNotBlank(filePath)) {
				String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
					File file = null;
					if (!PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
							.equalsIgnoreCase(appDeployLocation)) {
						file = new File(filePath);
						fileName=file.getName();
						byteFile = IOUtils.toByteArray(awss3LogicImpl.readS3ObjectAsStream(filePath));
					} else {
						file = new File(filePath);
						InputStream fileIn=new FileInputStream(file);
						byteFile = IOUtils.toByteArray(fileIn);
					}
			
			downloadMap.put("Document", byteFile);
			downloadMap.put("FileName",fileName );
			return new ResponseEntity<>(downloadMap, HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
					messageSource.getMessage("payasia.documentcenter.document.not.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	 }
	 
	 @Override
	 @PutMapping(value = "update-document")
		public ResponseEntity<?> updateDocument(@RequestBody CompanyDocumentCenterForm companyDocumentCenterForm) {
		 		final Long companyId = UserContext.getClientAdminId();
				boolean isFileValid = false;
				
				isFileValid=XSSRequestWrapper.isValidString(companyDocumentCenterForm.getDescription());
				
				if(isFileValid) {
	          
				if (companyDocumentCenterForm.getFileData()!=null && companyDocumentCenterForm.getFileData().getBytes().length>0) {
					isFileValid = FileUtils.isValidFile(companyDocumentCenterForm.getFileData(), companyDocumentCenterForm.getFileData().getOriginalFilename(), PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
				}else{
					
					isFileValid = true;
				}
			
				if(isFileValid){
					
					/*ID DECRYPT*/
				   companyDocumentCenterForm.setDocId(FormatPreserveCryptoUtil.decrypt(companyDocumentCenterForm.getDocId()));
				   String status=companyDocumentCenterLogic.updateDocument(companyDocumentCenterForm, companyId);
				   if(status.equalsIgnoreCase(PayAsiaConstants.PAYASIA_SUCCESS)) {
						return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),
								messageSource.getMessage("payasia.documentcenter.updated.document.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
				   }
				}
			}
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
							messageSource.getMessage("payasia.documentcenter.updated.document.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
	 
	 @Override
	 @DeleteMapping(value="delete-tax-document")
		public ResponseEntity<?> deleteTaxDocument(@RequestParam(value = "docDetailsIds", required = true) Long[] docDetailsIds) {
		 	final Long companyId = UserContext.getClientAdminId();
			String deleteStatus=companyDocumentCenterLogic.deleteTaxDocuments(docDetailsIds, companyId);
			if(StringUtils.equalsIgnoreCase(deleteStatus,PayAsiaConstants.PAYASIA_SUCCESS)){
				return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.documentcenter.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.documentcenter.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
	 
	 @Override
	 @GetMapping(value = "employee-filter-list")
		public ResponseEntity<?> getEmployeeFilterList() {
		 	final Long companyId = UserContext.getClientAdminId();
			List<EmployeeFilterListForm> filterList = companyDocumentCenterLogic
					.getEmployeeFilterList(companyId);
			if(filterList!=null && !filterList.isEmpty()) {
				return new ResponseEntity<>(filterList, HttpStatus.OK);		
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.documentcenter.filter.list.not.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		
		}
	 
	 @Override
	 @PostMapping(value = "edit-employee-filter-list")
		public ResponseEntity<?> getEditEmployeeFilterList(@RequestBody CompanyDocumentCenterForm companyDocumentCenterForm) {
			/*ID DECRYPT*/
		 
			Long documentId = FormatPreserveCryptoUtil.decrypt(companyDocumentCenterForm.getDocId());
			final Long companyId = UserContext.getClientAdminId();
			List<EmployeeFilterListForm> filterList = companyDocumentCenterLogic
					.getEditEmployeeFilterList(documentId, companyId);
			
			if(filterList!=null && !filterList.isEmpty()) {
				return new ResponseEntity<>(filterList, HttpStatus.OK);		
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.documentcenter.filter.list.not.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		
		}
	 //TODO 
	 @Override
	 @PostMapping(value = "/search-document-employee")
		public ResponseEntity<?> searchDocumentEmployeeDocumentCenter(@RequestBody SearchParam searchParamObj,@RequestParam(value = "categoryId", required = true) Long categoryId) {

		 	final Long companyId = UserContext.getClientAdminId();
			final Long employeeId = Long.parseLong(UserContext.getUserId());

			SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
			
			CompanyDocumentCenterResponseForm companyDocumentCenterFormResponse = companyDocumentCenterLogic
					.searchDocumentEmployeeDocumentCenter(searchParamObj.getFilters()[0].getField(), searchParamObj.getFilters()[0].getValue(), searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(),
							companyId, categoryId, employeeId);
			 if(companyDocumentCenterFormResponse.getRows()!=null && !companyDocumentCenterFormResponse.getRows().isEmpty()){
			     return new ResponseEntity<>(companyDocumentCenterFormResponse, HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
					messageSource.getMessage("payasia.documentcenter.document.not.found", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);

		}
	 
	 @Override
	  @DeleteMapping(value = "delete-filter")
		public  ResponseEntity<?>  deleteFilter(@RequestParam(value = "filterId", required = true) Long filterId) {
			String status=companyDocumentCenterLogic.deleteFilter(filterId);
			  if(status.equalsIgnoreCase(PayAsiaConstants.PAYASIA_SUCCESS)) {
				return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),
						messageSource.getMessage("payasia.documentcenter.filter.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
					messageSource.getMessage("payasia.documentcenter.filter.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);

		}
	 
	 @Override
	 @PostMapping(value = "save-employee-filterlist")
		public ResponseEntity<?> saveEmployeeFilterList(@RequestBody CompanyDocumentCenterForm companyDocumentCenterForm) throws UnsupportedEncodingException {

			String filterStatus= null;
		    final String decodedMetaData = URLDecoder.decode(companyDocumentCenterForm.getMetaData(), "UTF-8"); 
			  
			   /*ID DECRYPT*/
			   long documentId = FormatPreserveCryptoUtil.decrypt(companyDocumentCenterForm.getDocId());
			   
			   filterStatus = companyDocumentCenterLogic.saveEmployeeFilterList(decodedMetaData, documentId);
			   if(!filterStatus.equalsIgnoreCase("0")) {
				   return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
							messageSource.getMessage("payasia.company.document.center.shortlist.saved.successfully", new Object[]{}, UserContext.getLocale())),HttpStatus.OK);
			   }
			   return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), 
						messageSource.getMessage("payasia.company.document.center.shortlist.saved.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
			}
	
		@Override
		@PostMapping(value = "shortlist-filter-data")
		public ResponseEntity<?> shortListData() {
			final Long companyId = UserContext.getClientAdminId();
			Map<String, Object> dynamicObjectMap = new HashMap<>();
			List<CompanyDocumentCenterForm> categoryList = companyDocumentCenterLogic.getCategoryList();
			dynamicObjectMap.put("categoryList", categoryList);
			Map<String, String> advanceFilterCombosHashMap = generalLogic.getEmployeeFilterComboList(companyId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(advanceFilterCombosHashMap, jsonConfig);
			dynamicObjectMap.put("advanceFilterCombosHashMap", jsonObject.toString());
			return new ResponseEntity<>(dynamicObjectMap, HttpStatus.OK);
		}
}
	

