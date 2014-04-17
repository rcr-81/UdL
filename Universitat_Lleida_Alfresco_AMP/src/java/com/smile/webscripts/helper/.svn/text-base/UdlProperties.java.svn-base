package com.smile.webscripts.helper;

import java.util.Properties;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;

public class UdlProperties extends BaseScopableProcessorExtension implements ConstantsUdL {
	
	/**
	 * Read UDL custom properties file. Available from javascript web-scripts (see udl-application-context.xml).
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String getProperty(String name) throws Exception {
		Properties properties = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        properties.load(classLoader.getResourceAsStream(UDL_PROPERTIES_FILE));
        return (String)properties.get(name);
	}
}
