package com.payasia.api.oauth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthenticationToken extends AbstractAuthenticationToken {
       private static final long serialVersionUID = -1092219614309982278L;
       private final Object principal;
       private Object credentials;
   	   private final long userId;

       public UserAuthenticationToken(Object principal, Object credentials,
               Collection<? extends GrantedAuthority> authorities,long userid) {
               super(authorities);
               this.principal = principal;
               this.credentials = credentials;
               super.setAuthenticated(true);
               this.userId = userid;
       }
       
       public long getUserId() {
   		return userId;
   	}

       public UserAuthenticationToken loadUserByUsername(String username) {
   		return this.loadUserByUsername(username);
   	}

       

       public Object getCredentials() {
               return this.credentials;
        }

        public Object getPrincipal() {
               return this.principal;
         }
}