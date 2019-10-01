package com.payasia.jwt;

import org.springframework.stereotype.Component;

import com.payasia.web.security.PayAsiaUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	
	   /* @Value("${jwt.secret}")*/
	    private String secret="pay";

	    /**
	     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
	     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
	     * 
	     * @param token the JWT token to parse
	     * @return the User object extracted from specified token or null if a token is invalid.
	     */
	    public boolean parseToken(String token) {
	        try {
	            Claims body = Jwts.parser()
	                    .setSigningKey(secret)
	                    .parseClaimsJws(token)
	                    .getBody();

	            return true;

	        } catch (JwtException | ClassCastException e) {
	            return false;
	        }
	    }

	    /**
	     * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties are taken from the specified
	     * User object. Tokens validity is infinite.
	     * 
	     * @param u the user for which the token will be generated
	     * @return the JWT token
	     */
	    public String generateToken(PayAsiaUserDetails u) {
	        Claims claims = Jwts.claims().setSubject(u.getUsername());
	        claims.put("userId", u.getUsername() + "");
	        claims.put("role", u.getAuthorities());

	        return Jwts.builder()
	                .setClaims(claims)
	                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
	                .compact();
	    }
}


