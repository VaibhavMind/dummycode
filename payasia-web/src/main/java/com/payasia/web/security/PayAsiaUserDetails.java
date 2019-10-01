package com.payasia.web.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class PayAsiaUserDetails extends User {

	private static final long serialVersionUID = 1L;
	private final long userId;

	public PayAsiaUserDetails(String username, long userid, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

		this.userId = userid;
	}

	public long getUserId() {
		return userId;
	}

	public PayAsiaUserDetails loadUserByUsername(String username) {
		return this.loadUserByUsername(username);
	}

}
