package com.smile.webscripts.helper;

import java.io.IOException;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;

public class Impersonate extends BaseScopableProcessorExtension {
	
	/**
	 * Impersonate user. Requires administrator privileges. 
	 * Available from javascript web-scripts (see udl-application-context.xml).
	 * @param username
	 * @throws IOException
	 */
	public static void impersonate(String username) throws IOException {
		AuthenticationUtil.setRunAsUser(username);
		AuthenticationUtil.setFullyAuthenticatedUser(username);
	}
}