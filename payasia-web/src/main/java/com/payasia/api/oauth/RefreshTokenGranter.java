package com.payasia.api.oauth;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class RefreshTokenGranter extends AbstractTokenGranter {

protected RefreshTokenGranter(AuthorizationServerTokenServices tokenServices,
			ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
		super(tokenServices, clientDetailsService, requestFactory, grantType);
	}


@Override
protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
    String refreshToken = tokenRequest.getRequestParameters().get("refresh_token");
    return getTokenServices().refreshAccessToken(refreshToken, tokenRequest);
  }

}
