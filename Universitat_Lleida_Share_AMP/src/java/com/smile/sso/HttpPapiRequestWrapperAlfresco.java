package com.smile.sso;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author juseg
 * Override method getRemoteUser() required for Alfresco External Authentication Subsystem. 
 * Based on the original source of Prise OpenPAPIFilter: HttpPapiRequestWrapper.java
 */
public class HttpPapiRequestWrapperAlfresco extends HttpServletRequestWrapper {
	Map attrs;
	Map papiConfig;
	String userId;
	boolean isLogout;

	public HttpPapiRequestWrapperAlfresco(HttpServletRequest req, String userid, Map attrs, Map papiConfig) {
		super(req);
		this.userId = userid;
		this.attrs = attrs;
		this.papiConfig = papiConfig;
		this.isLogout = false;
	}

	public boolean isLogout() {
		return isLogout;
	}

	public void setLogout(boolean isLogout) {
		this.isLogout = isLogout;
	}

	public String getAuthType() {
		return "PAPI";
	}	

	public String getHeader(String headerName) {
		if (headerName.startsWith("PAPI-ATTR-")) {
			String attrName = headerName.substring(10);
			if (attrs.containsKey(attrName)) {
				String[] values = (String[]) attrs.get(attrName);
				return implode("|", values);
			}
		} else if (headerName.startsWith("PAPI-CONF-")) {
			String attrName = headerName.substring(10);
			if (papiConfig.containsKey(attrName)) {
				String value = (String) papiConfig.get(attrName);
				return value;
			}
		} else if (headerName.startsWith("PAPI-DO-LOGOUT")) {
			return String.valueOf(isLogout);
		}

		return super.getHeader(headerName);
	}

	public Enumeration getHeaders(String headerName) {
		return super.getHeaders(headerName);
	}

	public Enumeration getHeaderNames() {

		Vector allHeaders = new Vector();
		Iterator it = attrs.keySet().iterator();
		while (it.hasNext()) {
			allHeaders.add("PAPI-ATTR-" + it.next().toString());
		}
		it = papiConfig.keySet().iterator();
		while (it.hasNext()) {
			allHeaders.add("PAPI-CONF-" + it.next().toString());
		}
		if (isLogout) {
			allHeaders.add("PAPI-DO-LOGOUT");
		}

		Enumeration enumeration = super.getHeaderNames();
		while (enumeration.hasMoreElements()) {
			String headerElement = (String) enumeration.nextElement();

			if (!allHeaders.contains(headerElement)) {
				allHeaders.add(headerElement);
			}

		}

		return allHeaders.elements();

	}

	private String implode(String separator, String[] data) {
		String res = "";
		for (int i = 0; i < data.length; i++) {
			if (!res.equals("")) {
				res = res + separator;
			}
			res = res + data[i];
		}
		return res;
	}

	@Override
	public String getRemoteUser() { 		
		return (String) getSession().getAttribute(OpenPAPIFilterAlfresco.SESS_PARAM_REMOTE_USER); 
	} 
}