package com.payasia.api.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.common.form.CurrencyDefinitionForm;

public interface CurrencyDefinitionApi {
	
	
	ResponseEntity<?> viewCurrencyDefinition(SearchParam searchParam,Long currencyId, String currencyDate);
	
	ResponseEntity<?> addCurrencyDefinition(CurrencyDefinitionForm currencyDefinitionForm);
	
	ResponseEntity<?> editCurrencyDefinition( CurrencyDefinitionForm currencyDefinitionForm);
	
	ResponseEntity<?> removeCurrencyDefinition(Long companyExchangeRateId);
	
	
	ResponseEntity<?> showBaseCurrency();
	

	
	ResponseEntity<?> importCompanyExchangeRate(CommonsMultipartFile file)  throws Exception;
	
	
	

}
