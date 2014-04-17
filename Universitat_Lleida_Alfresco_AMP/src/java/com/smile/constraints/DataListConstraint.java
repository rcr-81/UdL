package com.smile.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.ConstraintException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;

import com.smile.webscripts.helper.ConstantsUdL;
import com.smile.webscripts.helper.UdlProperties;


public class DataListConstraint extends ListOfValuesConstraint implements Serializable, ConstantsUdL {
	private static final long serialVersionUID = -534952098955583420L;
	private static final String ERR_NON_STRING = "d_dictionary.constraint.string_length.non_string";
	private static final String ERR_INVALID_VALUE = "d_dictionary.constraint.list_of_values.invalid_value";
	private List<String> allowedValues;
	private String datalist;
	private String viewMetadata;

	private String site;

	public DataListConstraint(){
		super();
		// Set list of value constraints to be sorted by default
		sorted = true;
	}

	public String getDatalist() {
		return datalist;
	}

	public void setDatalist(String datalist) {
		this.datalist = datalist;
	}

	public String getViewMetadata() {
		return viewMetadata;
	}

	public void setViewMetadata(String viewMetadata) {
		this.viewMetadata = viewMetadata;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	@Override
	public Map<String, Object> getParameters(){
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put(CASE_SENSITIVE_PARAM, this.caseSensitive);
		params.put(ALLOWED_VALUES_PARAM, this.allowedValues);
		params.put("datalist", this.datalist);
		params.put("viewMetadata", this.viewMetadata);		
		params.put("site", this.site);
		return params;
	}

	@Override
	public List<String> getAllowedValues(){
		List<String> rawValues = loadData();
		List<String> values = new ArrayList<String>(rawValues);
		Collections.sort(values);
		return values;    	
	}

	@Override
	public void initialize(){
		checkPropertyNotNull("allowedValues", allowedValues);
		checkPropertyNotNull("datalist", datalist);
	}

	@Override
	public String getDisplayLabel(String constraintAllowableValue){
		return constraintAllowableValue;
	}

	@Override
	public void setAllowedValues(List allowedValues){
		if (allowedValues == null){
			allowedValues = new ArrayList<String>(0);
		}
		this.allowedValues = Collections.unmodifiableList(allowedValues);        
	}

	@Override
	protected void evaluateSingleValue(Object value){
		// convert the value to a String
		String valueStr = null;
		try {
			valueStr = DefaultTypeConverter.INSTANCE.convert(String.class, value);
		}
		catch (TypeConversionException e){
			throw new ConstraintException(ERR_NON_STRING, value);
		}
		if(!valueStr.equals("")){
			// check that the value is in the set of allowed values
			if (!allowedValues.contains(valueStr)){
				throw new ConstraintException(ERR_INVALID_VALUE, value);
			}		
		}
	}

	/**
	 * @return datalist values
	 */
	private List<String> loadData() {
		List<String> av = new ArrayList<String>();
		//if(this.allowedValues.size() == 0){
		ServiceRegistry registry = (ServiceRegistry)MyApplicationContextHelper.getBean(ServiceRegistry.SERVICE_REGISTRY);		
		UdlProperties props = new UdlProperties();
		try {
			registry.getAuthenticationService().authenticate(props.getProperty(ADMIN_USERNAME), props.getProperty(ADMIN_PASSWORD).toCharArray());
		} 
		catch (Exception e) {			
			e.printStackTrace();
		}
		

		SearchService searchService = registry.getSearchService();
		NodeService nodeService = registry.getNodeService();

		String query = "PATH:\"/app:company_home/st:sites/cm:" + this.site + "/cm:dataLists/cm:" + this.datalist + "\"";
		StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "SpacesStore");
		ResultSet resultSet = searchService.query(storeRef, SearchService.LANGUAGE_LUCENE, query);
		if (resultSet.length() > 0){
			NodeRef dl = resultSet.getNodeRef(0);
			for(ChildAssociationRef ca : nodeService.getChildAssocs(dl)){
				NodeRef elem = ca.getChildRef();
				String nom;
				
				if(this.viewMetadata == null) {
					nom = (String)nodeService.getProperty(elem, QName.createQName("http://www.alfresco.org/model/datalist/1.0", "nom"));

				}else {
					nom = (String)nodeService.getProperty(elem, QName.createQName("http://www.alfresco.org/model/datalist/1.0", this.viewMetadata));					
				}
				
				av.add(nom);
			}
		}

		resultSet.close();	
		this.allowedValues = av;
		//}			
		return this.allowedValues;
	}	
}