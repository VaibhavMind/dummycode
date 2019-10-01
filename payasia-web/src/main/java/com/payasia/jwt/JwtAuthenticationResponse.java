package com.payasia.jwt;

import java.io.Serializable;

public class JwtAuthenticationResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String token;

	public JwtAuthenticationResponse() {}
	
	public JwtAuthenticationResponse(String token){
		this.token = token;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
