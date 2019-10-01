package com.payasia.api.auth.impl;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.auth.TwoFactorAuthApi;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.RedisConnectionUtil;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.GeneralMailLogic;
import com.payasia.logic.LoginLogic;

@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_COMMON)
public class TwoFactorAuthApiImpl implements TwoFactorAuthApi  {
    
	@Value("#{payasiaptProperties['redis.host-name']}")
	private String redisHost;
	@Value("#{payasiaptProperties['redis.port']}")
	private String redisPort;
	@Value("#{payasiaptProperties['redis.key.expire']}")
	private String keyExpire;
	@Value("#{payasiaptProperties['redis.database.name']}")
	private String databaseName;
	@Autowired
	private GeneralMailLogic generalMailLogic;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private LoginLogic loginLogic;
	
	@Autowired
	private TokenStore tokenStore;

	@Override
	@PostMapping("send-otp")
	public ResponseEntity<?> sendOtp(){
		final String otpCode = generateVerificationCode();

		boolean status = new RedisConnectionUtil(redisHost,redisPort,keyExpire,databaseName).add("otpkey"+UserContext.getUserId(), otpCode);
		if(status){
			String email = loginLogic.getLoginUserEmail(Long.valueOf(UserContext.getUserId()));
			if(!StringUtils.isEmpty(email)){
				String statusMsg = generalMailLogic.sendMailToEmployee(Long.valueOf(UserContext.getWorkingCompanyId()),PayAsiaConstants.TWO_FACTOR_AUTH, email, otpCode,keyExpire);
				if(statusMsg!=null && "send.otp.template.is.not.defined".equalsIgnoreCase(statusMsg)){
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("send.otp.template.is.not.defined", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
				}else if(statusMsg!=null && "email.configuration.is.not.defined".equalsIgnoreCase(statusMsg)){
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("email.configuration.is.not.defined", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
				}else if(statusMsg!=null && "email.configuration.contact.email.is.not.defined".equalsIgnoreCase(statusMsg)){
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("email.configuration.contact.email.is.not.defined", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
				}
			   Map<String, Object> objMap  = new HashMap<>();
			   objMap.put("msg", messageSource.getMessage("send.otp.success.msg", new Object[]{}, UserContext.getLocale()));
			   objMap.put("email",email);
			   return  new ResponseEntity<>(objMap,HttpStatus.OK);
			}else{
				   return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("user.email.not.mapped", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
			}	
		}else{
			    return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("otp.generate.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@PostMapping("validate-otp")
	public ResponseEntity<?> validateOtp(@RequestParam(value = "otp", required = true) String otpValue){
		boolean isValidOtp = new RedisConnectionUtil(redisHost,redisPort,keyExpire,databaseName).isValidOtp("otpkey"+UserContext.getUserId(),otpValue);
		if(isValidOtp){
		    Map<String, Object> objMap  = new HashMap<>();
			objMap.put("otpMsg", messageSource.getMessage("otp.success.msg", new Object[]{}, UserContext.getLocale()));
			setTwoWayAuth();
			return  new ResponseEntity<>(objMap, HttpStatus.OK);	
		}
		 return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("wrong.opt.msg", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
   }
	
   static String generateVerificationCode() {
     final String activationCode = UserContext.getUserId() + PasswordUtils.getRandomPassword(4);
	 return activationCode;
   }
   
	@Override
	@PutMapping("two-factor-status")
	public ResponseEntity<?> changeTwoFactorStatus() {
		setTwoWayAuth();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	 }
   
	private void setTwoWayAuth() {
		final OAuth2Authentication auth = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		OAuth2AccessToken accessToken = tokenStore.getAccessToken(auth);
		Map<String, Object> additionalInfo = accessToken.getAdditionalInformation();
		additionalInfo.put("twoWayAuthCheck", true);
		DefaultOAuth2AccessToken accessTokenWithUser = (DefaultOAuth2AccessToken) accessToken;
		accessTokenWithUser.setAdditionalInformation(additionalInfo);

	}
}
