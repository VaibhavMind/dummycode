package com.payasia.api.admin.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.admin.CurrencyDefinitionApi;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.form.CurrencyDefinitionForm;
import com.payasia.common.form.CurrencyDefinitionResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CurrencyDefinitionLogic;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN+"/currency-definition")
public class CurrencyDefinitionApiImpl implements CurrencyDefinitionApi {

	@Resource
	private CurrencyDefinitionLogic currencyDefinitionLogic;

	@Autowired
	private MessageSource messageSource;

	@Override
	@PostMapping("show")
	public ResponseEntity<?> viewCurrencyDefinition(@RequestBody SearchParam searchParam,
			@RequestParam(value = "currencyId", required = true) Long currencyId,
			@RequestParam(value = "currencyDate", required = true) String currencyDate) {
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParam.getMultiSortMeta());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(searchParam.getPage());
		pageDTO.setPageSize(searchParam.getRows());
		Long companyId = UserContext.getClientAdminId();
		SortCondition sortDTO = new SortCondition();
		 if(multisortlist != null && !multisortlist.isEmpty()) {
             sortDTO.setColumnName(multisortlist.get(0).getField());
             sortDTO.setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
       }
		CurrencyDefinitionResponse currencyDefinitionResponse = currencyDefinitionLogic
				.viewCurrencyDefinition(companyId, currencyId, currencyDate, pageDTO, sortDTO);
		if (currencyDefinitionResponse.getRows().isEmpty()) {
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.employee.currency.definition.list", new Object[] {}, UserContext.getLocale())),HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(currencyDefinitionResponse, HttpStatus.OK);
	}

	@Override
	@PostMapping("add")
	public ResponseEntity<?> addCurrencyDefinition(@RequestBody CurrencyDefinitionForm currencyDefinitionForm) {
		Long companyId = UserContext.getClientAdminId();
		String currencyDefinitionStatus = currencyDefinitionLogic.addCurrencyDefinition(currencyDefinitionForm,
				companyId);
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),currencyDefinitionStatus),HttpStatus.OK);
	}

	@Override
	@PostMapping("edit")
	public ResponseEntity<?> editCurrencyDefinition(@RequestBody CurrencyDefinitionForm currencyDefinitionForm) {
		Long companyId = UserContext.getClientAdminId();
		String currencyDefinitionStatus = currencyDefinitionLogic.updateCurrencyDefinition(currencyDefinitionForm,
				companyId);
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),currencyDefinitionStatus),HttpStatus.OK);
	}

	@Override
	@PostMapping("remove")
	public ResponseEntity<?> removeCurrencyDefinition(
			@RequestParam(value = "companyExchangeRateId", required = true) Long companyExchangeRateId) {
		companyExchangeRateId = FormatPreserveCryptoUtil.decrypt(companyExchangeRateId);
		Long companyId = UserContext.getClientAdminId();
		String strMess = currencyDefinitionLogic.deleteCurrencyDefinition(companyId, companyExchangeRateId);
		return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(),strMess),HttpStatus.OK);
	}



	@Override
	@PostMapping("base-currency")
	public ResponseEntity<?> showBaseCurrency() {
		Long companyId = UserContext.getClientAdminId();
		String baseCurrency = currencyDefinitionLogic.getBaseCurrency(companyId);
		List<CurrencyDefinitionForm> currencyNameList = currencyDefinitionLogic
				.getCurrencyName();
		Map<String,Object> map = new HashMap<>();
		map.put("baseCurrency",baseCurrency);
		map.put("currencyNameList",currencyNameList);
		return new ResponseEntity<>(map, HttpStatus.OK);

	}

	@Override
	@PostMapping("import-company-exchange-rate")
	public ResponseEntity<?> importCompanyExchangeRate(
			@RequestParam("importCompanyExchangeRate") CommonsMultipartFile commonsMultipartFile) throws Exception {

		boolean isVaildFile = false;
		Long companyId = UserContext.getClientAdminId();
		CurrencyDefinitionForm currencyDefinitionFrm = new CurrencyDefinitionForm();
		currencyDefinitionFrm.setFileUpload(commonsMultipartFile);
		isVaildFile = FileUtils.isValidFile(currencyDefinitionFrm.getFileUpload(),
				currencyDefinitionFrm.getFileUpload().getOriginalFilename(),
				PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT,
				PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);

		if (isVaildFile) {
			currencyDefinitionFrm = currencyDefinitionLogic.importCompanyExchangeRate(currencyDefinitionFrm, companyId);
			for(DataImportLogDTO dataImportLogDTO : currencyDefinitionFrm.getDataImportLogDTOs()) {
				dataImportLogDTO.setRemarks(messageSource.getMessage(dataImportLogDTO.getRemarks(), new Object[] {}, UserContext.getLocale()));
			}
			
		} else {
			List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
		
			DataImportLogDTO error = new DataImportLogDTO();
			error.setFailureType(messageSource.getMessage("payasia.record.error", new Object[] {}, UserContext.getLocale()));
			error.setRemarks(messageSource.getMessage("payasia.record.error", new Object[] {}, UserContext.getLocale()));
			error.setFromMessageSource(false);
			errors.add(error);
			currencyDefinitionFrm.setDataValid(false);
			currencyDefinitionFrm.setDataImportLogDTOs(errors);
		}
		return new ResponseEntity<>(currencyDefinitionFrm, HttpStatus.OK);
	}

	

	
}
