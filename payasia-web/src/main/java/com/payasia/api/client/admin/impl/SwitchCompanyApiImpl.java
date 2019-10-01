package com.payasia.api.client.admin.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.client.admin.SwitchCompanyApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.MultiSortMeta;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.oauth.UserData;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.JSONConverterUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.common.util.CryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.SwitchCompanyLogic;

/**
 * @author gauravkumar
 * @param : This class used for Switch Company APIs
 */

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN + "switch/" + "company/")
public class SwitchCompanyApiImpl implements SwitchCompanyApi {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Autowired
	private SwitchCompanyLogic switchCompanyLogic;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Override
	@PostMapping(value = "company-list")
	public ResponseEntity<?> getSwitchCompanyList(@RequestBody SearchParam searchParamObj, @RequestParam(value = "includeInactiveCompany", required = false) Boolean includeInactiveCompany) {
		final Long employeeId = Long.valueOf(UserContext.getUserId());
		final List<MultiSortMeta> multisortlist = Arrays.asList(searchParamObj.getMultiSortMeta());
		final List<Filters> filterlist = Arrays.asList(searchParamObj.getFilters());
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);	
		if (multisortlist != null && !multisortlist.isEmpty()) {
			searchSortDTO.getSortCondition().setColumnName(multisortlist.get(0).getField());
			searchSortDTO.getSortCondition().setOrderType(multisortlist.get(0).getOrder().equals("1") ? PayAsiaConstants.ASC : PayAsiaConstants.DESC);
		}
		
		SwitchCompanyResponse response = switchCompanyLogic.getSwitchCompanyList(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), 
				employeeId, (!filterlist.isEmpty())?filterlist.get(0).getField():"", (!filterlist.isEmpty())?filterlist.get(0).getValue():"", includeInactiveCompany);

		if(response!=null && !response.getSwitchCompanyFormList().isEmpty()) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "company-switch")
	public ResponseEntity<?> switchCompany(@RequestParam(value = "companyId", required = true) Long companyId,@RequestParam(value = "companyCode", required = false) String companyCode) {
		CompanyForm companyForm = switchCompanyLogic.getCompany(companyId);

		if (companyForm != null) {
			updateToken(companyForm, companyId);
			Map<String, Object> codeMap = new HashMap<>();
			codeMap.put("switchedCompanyCode", companyForm.getCompanyCode());
			return new ResponseEntity<>(codeMap, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.switch.company.not.assigned", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}

	private void updateToken(CompanyForm companyForm, Long companyId) {
		final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
		Map<String, Object> additionalInfo = tokenStore.getAccessToken(auth).getAdditionalInformation();
		String employeeInfo = (String) additionalInfo.get("userData");
		String decryptData = CryptoUtil.decrypt(employeeInfo, CryptoUtil.SECRET_KEY);

		UserData userData = (UserData) JSONConverterUtils.jsonStringToObject(decryptData, UserData.class);
		userData.setClientAdminId(companyId);
		userData.setCompanyCode(companyForm.getCompanyCode());
		userData.setWorkingCompanyDateFormat(companyForm.getDateFormat());
		userData.setWorkingCompanyTimeZone(companyForm.getGmtOffset());

		String encrypt = JSONConverterUtils.objectToJsonString(userData);
		employeeInfo = CryptoUtil.encrypt(encrypt, CryptoUtil.SECRET_KEY);
		additionalInfo.put("userData", employeeInfo);
		DefaultOAuth2AccessToken accessTokenWithUser = (DefaultOAuth2AccessToken) accessToken;
		accessTokenWithUser.setAdditionalInformation(additionalInfo);
	}
	
}
