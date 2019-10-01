package com.payasia.api.hris.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.hris.EmployeeDocumentCenterApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.dto.SelectOptionDTO;
import com.payasia.common.form.EmployeeDocFormResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.EmpDocumentCenterLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author gauravkumar
 * @param :
 *            This class used for Employee Document Center APIs
 * 
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_HRIS)
public class EmployeeDocumentCenterApiImpl implements EmployeeDocumentCenterApi {

	@Resource
	private EmpDocumentCenterLogic empDocumentCenterLogic;

	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;

	@Autowired
	@Qualifier("AWSS3Logic")
	private AWSS3Logic awss3Logic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;

	@Override
	@PostMapping(value = "search-emp-doc")
	public ResponseEntity<?> searchEmployeeDocument(@RequestBody SearchParam searchParamObj, @RequestParam(value = "categoryId", required = true) Long categoryId) {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());

		EmployeeDocFormResponse companyDocCenterFormResponse = null;
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		
		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}

		if ((filterllist != null && !filterllist.isEmpty())) {
			companyDocCenterFormResponse = empDocumentCenterLogic.searchEmployeeDocument(
					filterllist.get(0).getField(), filterllist.get(0).getValue(), searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId, categoryId, employeeId);
		} else {
			companyDocCenterFormResponse = empDocumentCenterLogic.searchEmployeeDocument(null, null, searchSortDTO.getPageRequest(),
					searchSortDTO.getSortCondition(), companyId, categoryId, employeeId);
		}

		if (companyDocCenterFormResponse != null) {
			return new ResponseEntity<>(companyDocCenterFormResponse, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}

	@Override
	@PostMapping(value = "download-emp-doc")
	public ResponseEntity<?> downloadEmployeeDocument(@RequestBody String jsonStr) throws IOException {

		final Long companyId = Long.valueOf(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.valueOf(UserContext.getUserId());

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(jsonStr, jsonConfig);
		Long documentId = jsonObject.getLong("documentId");
		documentId = FormatPreserveCryptoUtil.decrypt(documentId);
		Map<String, Object> docCenterMap = new HashMap<>();
		String filePath = empDocumentCenterLogic.viewDocument(documentId, companyId, employeeId);
		File file = null;
		BufferedInputStream is = null;
		FileInputStream fis=null;
		if(filePath!=null){
			
			String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
			String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
			file = new File(filePath);
			byte[] data = null;
			if(PayAsiaConstants.PAYASIA_APPLICATION_DEPLOY_LOCATION
					.equalsIgnoreCase(appDeployLocation)) {
				InputStream fins = awss3Logic.readS3ObjectAsStream(filePath);
				data = IOUtils.toByteArray(fins);
			}else{
				fis = new FileInputStream(file);
				is = new BufferedInputStream(fis);
			   data = IOUtils.toByteArray(is);
			}

			if (data != null && !(data.toString().isEmpty())) {
					docCenterMap.put("fileName", fileName);
					docCenterMap.put("fileType", fileType);
					docCenterMap.put("documentbody", data);
				   return new ResponseEntity<>(docCenterMap, HttpStatus.OK);
				}
			}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nofile", new Object[]{}, UserContext.getLocale()).toString()), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "doc-category-list")
	public ResponseEntity<?> getDocFilterList(Locale locale) {
		List<SelectOptionDTO> list = empDocumentCenterLogic.fetchList();
		Map<String, Object> docmap = new HashMap<>();
		docmap.put("categoryList", list);
		return new ResponseEntity<>(docmap, HttpStatus.OK);
	}

}
