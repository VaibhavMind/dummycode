package com.payasia.api.oauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.payasia.api.utils.JSONConverterUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.util.CryptoUtil;
import com.payasia.logic.LoginLogic;

/**
 * @author manojkumar2
 * @param : This class used to put additional information in  OAuth2Token.
*/
public class OAuth2CustomToken implements TokenEnhancer {

	/** The login logic. */
	@Resource
	private LoginLogic loginLogic;
	
	@Value("#{payasiaptProperties['payasia.app.encryption']}")
	private  String encryptionValue;
	
	@Value("#{payasiaptProperties['app.two.factor.auth']}")
	private  String appTwoFactorAuth;
	
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,OAuth2Authentication authentication) {
        final Map<String, Object> employeeInfo = new HashMap<>(); 
        String privateKey = null;
        if(encryptionValue!=null && encryptionValue.equalsIgnoreCase("YES")){
        	privateKey=getNumber();
        }
        CompanyForm companyForm = loginLogic.getCompany(UserContext.getLoginId(), UserContext.getCompanyCode());
        UserData userData = new UserData(UserContext.getLoginId(), UserContext.getCompanyCode(), UserContext.getUserId(),  String.valueOf(companyForm.getCompanyId()), companyForm.getGmtOffset(), 
         companyForm.getDateFormat(), 12l, privateKey, UserContext.getTab(), companyForm.isHasHrisModule(), companyForm.isHasClaimModule(), companyForm.isHasLeaveModule(), companyForm.isHasMobileModule(), 
         companyForm.isHasLundinTimesheetModule(), companyForm.isHasLionTimesheetModule(), companyForm.isHasCoherentTimesheetModule(),UserContext.getLocale(),UserContext.getDevice(),UserContext.getIpAddress(),companyForm.getCompanyId());
  
        String json = JSONConverterUtils.objectToJsonString(userData);
        String encryptData = CryptoUtil.encrypt(json,CryptoUtil.SECRET_KEY);    
    
        boolean hasAnyRecordInLoginHistoryStatus = loginLogic.getEmployeeLoginHistoryStatus(UserContext.getLoginId(), UserContext.getCompanyCode());
		boolean isMaxPwdAgeValidStatus = loginLogic.checkMaxPwdAgeExceeded(Long.valueOf(UserContext.getUserId()), companyForm.getCompanyId());
		boolean isPasswordReseted = loginLogic.checkIsPasswordReseted(Long.valueOf(UserContext.getUserId()));
        
        employeeInfo.put("userData", encryptData);
        employeeInfo.put("isLoginHistory",hasAnyRecordInLoginHistoryStatus);
        employeeInfo.put("isPasswordExpired",isMaxPwdAgeValidStatus);
        employeeInfo.put("isPasswordReset",isPasswordReseted);
        employeeInfo.put("dateFormat",companyForm.getDateFormat());
        boolean isTwoFactorAuth = false;
      
        if(appTwoFactorAuth!=null && appTwoFactorAuth.equalsIgnoreCase("YES") && companyForm.getIsTwoFactorAuth()){
        	isTwoFactorAuth=true;
        }
        employeeInfo.put("twoWayAuthCheck", false);
        employeeInfo.put("isTwoWayAuth",isTwoFactorAuth);
       // setPrivilageData(employeeInfo);
        DefaultOAuth2AccessToken accessTokenWithUser = (DefaultOAuth2AccessToken)accessToken;
       accessTokenWithUser.setAdditionalInformation(employeeInfo);
        return accessTokenWithUser;
	}
	
	
	private void setPrivilageData(Map<String, Object> employeeInfo) {
		
		List<String> roleList = new ArrayList<>();
		List<String> moduleList = new ArrayList<>();
		List<String> privList = new ArrayList<>();

		for(GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {

			if (authority.getAuthority().startsWith("ROLE_")) {
				roleList.add(authority.getAuthority());

			}
			if (authority.getAuthority().startsWith("MODULE_")) {
				moduleList.add(authority.getAuthority());

			}
			if (authority.getAuthority().startsWith("PRIV_")) {
				privList.add(authority.getAuthority());
			}

		}
		employeeInfo.put("module", moduleList);
		employeeInfo.put("role", roleList);
		employeeInfo.put("privilege", privList);
	}
	
	/* Generate 16 digit number*/	
	private static String getNumber() {
		Random rand = new Random();
		int num = rand.nextInt(9000000) + 10000000;
		int num1 = 24682468;
		String finalKey = String.valueOf(num).concat(String.valueOf(num1));
		return finalKey;

	}

}