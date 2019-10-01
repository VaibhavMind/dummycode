package com.payasia.saml.security;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Context provider which overrides request attributes with values of the load-balancer or reverse-proxy in front
 * of the local application. The settings help to provide correct redirect URls and verify destination URLs during
 * SAML processing.
 */
public class PayAsiaSAMLContextProviderLB extends SAMLContextProviderImpl {

    private String scheme;
    private boolean includeServerPortInRequestURL;
    private int serverPort;
    private String contextPath;

    /**
     * Method wraps the original request and provides values specified for load-balancer. The following methods
     * are overriden: getContextPath, getRequestURL, getRequestURI, getScheme, getServerName, getServerPort and isSecure.
     *
     * @param request  original request
     * @param response response object
     * @param context  context to populate values to
     * @throws MetadataProviderException
     */
    @Override
    protected void populateGenericContext(HttpServletRequest request, HttpServletResponse response, SAMLMessageContext context) throws MetadataProviderException {

        super.populateGenericContext(new LPRequestWrapper(request), response, context);

    }

    /**
     * Wrapper for original request which overrides values of scheme, server name, server port and contextPath.
     * Method isSecure returns value based on specified scheme.
     */
    private class LPRequestWrapper extends HttpServletRequestWrapper {
    	private LPRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getContextPath() {
            return contextPath;
        }

        @Override
        public String getScheme() {
            return scheme;
        }

        @Override
        public int getServerPort() {
            return serverPort;
        }

        @Override
        public String getRequestURI() {
            StringBuilder sb = new StringBuilder(contextPath);
            sb.append(getServletPath());
            return sb.toString();
        }

        @Override
        public StringBuffer getRequestURL() {
            StringBuffer sb = new StringBuffer();
            sb.append(scheme).append("://").append(getServerName());
            if (includeServerPortInRequestURL) sb.append(":").append(serverPort);
            sb.append(contextPath);
            sb.append(getServletPath());
            if (getPathInfo() != null) sb.append(getPathInfo());
            return sb;
        }

        @Override
        public boolean isSecure() {
            return "https".equalsIgnoreCase(scheme);
        }

    }

    /**
     * Scheme of the LB server - either http or https
     *
     * @param scheme scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Port of the server, in case value is <= 0 port will not be included in the requestURL and port
     * from the original request will be used for getServerPort calls.
     *
     * @param serverPort server port
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * When true serverPort will be used in construction of LB requestURL.
     *
     * @param includeServerPortInRequestURL true to include port
     */
    public void setIncludeServerPortInRequestURL(boolean includeServerPortInRequestURL) {
        this.includeServerPortInRequestURL = includeServerPortInRequestURL;
    }

    /**
     * Context path of the LB, must be starting with slash, e.g. /saml-extension
     *
     * @param contextPath context path
     */
    public void setContextPath(String contextPath) {
        if (contextPath == null || "/".equals(contextPath)) {
            contextPath = "";
        }
        this.contextPath = contextPath;
    }

    /**
     * Verifies that required entities were autowired or set and initializes resolvers used to construct trust engines.
     *
     * @throws javax.servlet.ServletException
     */
    public void afterPropertiesSet() throws ServletException {

        super.afterPropertiesSet();
        Assert.hasText(scheme, "Scheme must be set");
        Assert.notNull(contextPath, "Context path must be set");
        if (StringUtils.hasLength(contextPath)) {
            Assert.isTrue(contextPath.startsWith("/"), "Context path must be set and start with a forward slash");
        }

    }

}