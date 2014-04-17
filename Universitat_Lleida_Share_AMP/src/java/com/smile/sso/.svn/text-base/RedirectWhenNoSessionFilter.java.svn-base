package com.smile.sso;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author juseg
 * To increase performance of AdAS integration this filter checks the Alf session is active. 
 * In case of no session, the filter redirects to index page, to run AdAS filter.
 */
public class RedirectWhenNoSessionFilter implements Filter {

	@Override
	public void destroy() {		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {		
		HttpServletRequest httpReq = (HttpServletRequest)req;
		HttpServletResponse httpRes = (HttpServletResponse) res;		
		if (httpReq.getSession().getAttribute(OpenPAPIFilterAlfresco.SESS_PARAM_REMOTE_USER) == null){
			httpRes.sendRedirect("/share");
		}
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {		
	}
}