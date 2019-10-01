package com.payasia.web.security;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author manojkumar2
 * @param :  CsrfSecurityRequestMatcher
*/
public class CsrfSecurityRequestMatcher implements RequestMatcher {
	
    private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
    private RequestMatcher unprotectedMatcher = new RegexRequestMatcher("/unprotected", null);
  
    /*MOBILE API CALL DISABLE CSRF TOKEN*/
    private AntPathRequestMatcher[] disableURLRequestMatchers = {
        	new AntPathRequestMatcher("/service/loginWS/**"),
        	new AntPathRequestMatcher("/service/leaveReviewWS/**"),
        	new AntPathRequestMatcher("/service/generalWS/**"),
        	new AntPathRequestMatcher("/service/leaveWS/**"),
        	new AntPathRequestMatcher("/service/notificationWS/**"),
        	new AntPathRequestMatcher("/service/payslipWS/**"),
        	new AntPathRequestMatcher("/service/claimReviewWS/**"),
        	new AntPathRequestMatcher("/service/claimWS/**"),
        	new AntPathRequestMatcher("/saml/SSO/alias/**"),
     };
   
    @Override
    public boolean matches(HttpServletRequest request) {
        if(allowedMethods.matcher(request.getMethod()).matches()){
            return false;
        }
  
        if(disableURLRequestMatchers.length > 0){
        	for (AntPathRequestMatcher rm : disableURLRequestMatchers) {
	    		if (rm.matches(request)){
	    			return false;
	    		}
    		}
    	}
        return !unprotectedMatcher.matches(request);
    }
}