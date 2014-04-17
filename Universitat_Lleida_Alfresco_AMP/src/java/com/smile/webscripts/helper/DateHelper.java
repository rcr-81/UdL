package com.smile.webscripts.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.util.ISO8601DateFormat;

import com.smile.actions.RecalcularDatesAction;

public class DateHelper extends BaseScopableProcessorExtension {
	
	private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * Return current date with ISO8601 format. Available from javascript web-scripts (see udl-application-context.xml).
	 * @return
	 */
	public String getNow(){
		Date date = new Date();
		return ISO8601DateFormat.format(date);
	}
	
	/**
	 * Parse given date with ISO8601 format.
	 * @param sDate
	 * @return
	 */
	public String parse(String sDate){
		String ret = null;
		if(sDate != null && !sDate.equals("") && !sDate.equals("null")){
			sDate = sDate.replaceAll("-", "/");
			DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			Date date;
			try {
				date = df.parse(sDate);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}		
			ret = ISO8601DateFormat.format(date);
		}		
		return ret;
	}
	
	public void runRecalcularDates(String sNodeRef){
		RecalcularDatesAction recalculator = new RecalcularDatesAction();
		recalculator.runRecalcularDates(sNodeRef, serviceRegistry);		
	}	
}
