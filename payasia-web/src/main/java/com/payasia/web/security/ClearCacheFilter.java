package com.payasia.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Manoj
 * @Param :  This class is used clear browser cache. 
*/
public class ClearCacheFilter implements Filter {

	@Override
	public void init(FilterConfig arg0) throws ServletException {}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletResponse httpServletResponse = (HttpServletResponse)(res);
		httpServletResponse.setHeader("Cache-Control", "no-cache,no-store,must-revalidate,max-age=0");
		httpServletResponse.setHeader("Pragma","no-cache");
		httpServletResponse.setDateHeader("Expires", 0);
		httpServletResponse.addHeader("X-Frame-Option", "SAMEORIGIN");
		httpServletResponse.setHeader("x-content-type-options:", "nosniff");
		httpServletResponse.setHeader("x-xss-protection","1");
		httpServletResponse.setHeader("X-Content-Security-Policy", "defautl-src 'self'");
		httpServletResponse.setHeader("refrrer-policy","no-referrer");
		httpServletResponse.setHeader("strict-transport-security","max-age=0");
		filterChain.doFilter(req, res);
	}
	
	@Override
	public void destroy() {}

}
