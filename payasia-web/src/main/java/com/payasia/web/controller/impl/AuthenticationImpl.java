package com.payasia.web.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/authenticationAPI")
public class AuthenticationImpl {
	
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String generateToken() {
	
		
		return "";
		
	}
	
	
	/*
	@WebMethod
	@GET
	@Path("/generateToken")
	public String generateToken(@WebParam(name = "userName") @QueryParam("userName") String userName,@WebParam(name = "password") @QueryParam("password") String password) {
	
		
		System.out.println("hello");
		
		return "";
		
	}*/
	

}
