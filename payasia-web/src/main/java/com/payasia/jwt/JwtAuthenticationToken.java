package com.payasia.jwt;

public class JwtAuthenticationToken{

	private static final long serialVersionUID = 1L;

	private String token;

    public JwtAuthenticationToken(String token) {
       // super(null, null);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

  
}
