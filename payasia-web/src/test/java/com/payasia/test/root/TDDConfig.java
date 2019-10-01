package com.payasia.test.root;

import java.util.Locale;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.payasia.api.utils.ApiUtils;
import com.payasia.common.bean.util.UserContext;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-application-context.xml", "/test-application-context-properties.xml",
		"/test-application-context-aop.xml", "/test-application-context-logic.xml",
		"/application-context-mail.xml"}, inheritLocations = false)
public abstract class TDDConfig {
	
	private String appUrl;	
	@Autowired
	private WebApplicationContext wac;
	
	public MockMvc mockMvc;

	@Mock
	public MessageSource messageSource;
	
	@Mock
	private UserContext userContext;

	@Before
	public void init() {
		//this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).dispatchOptions(true).build();
		MockitoAnnotations.initMocks(this);
		this.appUrl = ApiUtils.API_ROOT_PATH;
		this.setUserData();

	}

	public String getAppUrl() {
		return appUrl;
	}
	
	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}
	
	@SuppressWarnings("static-access")
	private void setUserData(){
		userContext.setUserId("12440");
		userContext.setClaimModule(true);
		userContext.setCompanyCode("minddemo");
		userContext.setDevice("WEB");
		userContext.setHrisModule(true);
		userContext.setLanguageId(1l);
		userContext.setLeaveModule(true);
		userContext.setLocale(new Locale("en","US"));
		userContext.setLundinTimesheetModule(true);
		userContext.setPassword("1");
		userContext.setWorkingCompanyId("267");
		userContext.setIpAddress("127.0.0.1");
		//userContext.setWorkingCompanyDateFormat(workingCompanyDateFormat);
		
	}
	
//	public abstract void bad_request_400(); 
//	public abstract void request_not_found_404(); 
//	public abstract void request_not_found_403(); 
//	public abstract void internal_error_500(); 
}
