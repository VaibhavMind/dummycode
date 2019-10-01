package com.payasia.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class XSSFilter implements Filter {
	
	private static final Logger LOGGER = Logger.getLogger(XSSFilter.class);
	private FilterConfig config = null;
	private static boolean no_init = true;
	private String apostrophe = "&#39;";

	public XSSFilter() {
	}

	public void init(FilterConfig paramFilterConfig) throws ServletException {
		config = paramFilterConfig;
		no_init = false;
		String str = paramFilterConfig.getInitParameter("apostrophe");
		if (str != null) {
			apostrophe = str.trim();
		}
	}

	public void destroy() {
		config = null;
	}

	public FilterConfig getFilterConfig() {
		return config;
	}

	public void setFilterConfig(FilterConfig paramFilterConfig) {
		if (no_init) {
			no_init = false;
			config = paramFilterConfig;
			String str = paramFilterConfig.getInitParameter("apostrophe");
			if (str != null) {
				apostrophe = str.trim();
			}
		}
	}

	public void doFilter(ServletRequest paramServletRequest, ServletResponse paramServletResponse,
			FilterChain paramFilterChain) throws IOException, ServletException {
		paramFilterChain.doFilter(new XSSRequestWrapper((HttpServletRequest) paramServletRequest, apostrophe),
				paramServletResponse);
	}
}
