package es.cesca.alfresco.scheduled;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.namespace.QName;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import es.cesca.alfresco.util.CescaUtil;

public abstract class ExecuterAbstractBase extends ActionExecuterAbstractBase {
	
	private static Log log = LogFactory.getLog(ExecuterAbstractBase.class);
	public static final String RESULT_ERROR = "Error:";
	private static final String MIMETYPE = "text/plain";
	protected ServiceRegistry serviceRegistry;
	private JavaMailSender mailService;

	protected void marcaErrorAPeticio(NodeRef node, String descripcio) {//Sept mod - descripcio
		getServiceRegistry().getNodeService().setProperty(node, CescaUtil.PETICIO_PROP_ESTAT, CescaUtil.STATUS_ERROR);
		getServiceRegistry().getNodeService().setProperty(node, CescaUtil.PETICIO_PROP_DESC_ERROR, descripcio);
	}

	protected boolean isPeticioOK(String result) {
		if(result.trim().equalsIgnoreCase(""))
			return true;
		
		if (CescaUtil.isEmpty(result.trim())) {//Sept mod: new Check
			return false;
		}
		String[] tokens = result.trim().split(" ");
		System.out.println("token " + tokens[0]);
		if(tokens[0].length() > 4){
			if(tokens[0].contains("Error")){
				return false;
			}
			/*String initial = tokens[0].substring(0, 5);
			if(initial.equalsIgnoreCase(RESULT_ERROR)){
				return false;
			}*/
		}
		return true;
		
		//return !(CescaUtil.isEmpty(result) || result.trim().startsWith(RESULT_ERROR));  Sept mod: OLD check
	}
	
	protected NodeRef searchForANode(String query) {
		//cerca el node del path
		List<NodeRef> nodes = this.searchForNodes(query);
		System.out.println("numero de resultados query " + nodes.size());
		if (nodes != null && nodes.size()>0){
			return nodes.get(0);}
		else
			return null;
	}
	
	protected List<NodeRef> searchForNodes(String query) {
		//cerca el node del path
    	SearchParameters sp = new SearchParameters();
		sp.setLanguage(SearchService.LANGUAGE_LUCENE);
		sp.setQuery(query);
		sp.addStore(CescaUtil.STORE);
		
		ResultSet resultSet = null;
		List<NodeRef> result = null;
		try {
			resultSet = serviceRegistry.getSearchService().query(sp);
			
            if(resultSet != null) {
            	log.debug("S'han trobat " + resultSet.length() + "nodes a la query");
            	result = resultSet.getNodeRefs();
    		}else{
    			log.debug("El resultat de la query es null");
    		}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
			// TODO: handle exception
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return result;
	}
	
	
	protected NodeRef getExpedientCFG(String idConfiguracio) {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_EXP.replace("{0}", idConfiguracio));
	}
	
	protected NodeRef getExpedientCFGDefault() {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_EXP_DEFAULT);
	}
	
	protected NodeRef getDocumentCFG(String idConfiguracio) {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_DOC.replace("{0}", idConfiguracio));
	}
	
	protected NodeRef getDocumentConExpCFG(String idConfiguracio) {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_DOC_CON_EXP.replace("{0}", idConfiguracio));
	}
	
	protected NodeRef getDocumentSinExpCFG(String idConfiguracio) {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_DOC_SIN_EXP.replace("{0}", idConfiguracio));
	}
	
	protected NodeRef getDocumentSinExpCFGPerDefecte() {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_DOC_SIN_EXP_DEFAULT);
	}
	
	/*protected NodeRef getSignaturaCFG(String sigType) {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_SIG.replace("{0}", sigType));
	}*/
	
	protected NodeRef getSignaturaConExpCFG(String idConfiguracio) {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_SIG_CON_EXP.replace("{0}", idConfiguracio));
	}
	
	protected NodeRef getSignaturaSinExpCFG(String idConfiguracio) {
		//ONLY one configuration can be enabled at the same time
		return this.searchForANode(CescaUtil.QUERY_CFG_SIG_SIN_EXP.replace("{0}", idConfiguracio));
	}
	
	/**
	 * Get all properties with localname and values. No QName.
	 * [Required to bind properties and XML]
	 * 
	 * @param objProps
	 * @return props
	 */
	protected Map<String, Serializable> getProperties(Map<QName, Serializable> objProps) {
		
		Map<String, Serializable> props = new HashMap<String, Serializable>();
		
		if (objProps != null && !objProps.isEmpty()) {
		
			Set<QName> set = objProps.keySet();
			for (Iterator<QName> iterator = set.iterator(); iterator.hasNext();) {
				QName qName = (QName) iterator.next();
				String name = qName.getLocalName();
				props.put(name, (Serializable)objProps.get(qName));
			}
			
		}		
		return props;
	}
	//********************BASE64*******************************
	protected String encodeBase64ToString(NodeRef content) {
		//get byte[]
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ContentReader reader = this.getServiceRegistry().getContentService().getReader(content, ContentModel.PROP_CONTENT);
		reader.getContent(bos);
		byte[] binaryData = bos.toByteArray();
		
		try {
			bos.close();
		} catch (Exception e) {
		}

		return encodeBase64(binaryData);
	}
	
	protected String encodeBase64(byte[] binaryData) {

		byte[] encoded = Base64.encodeBase64(binaryData);
		return new String(encoded);
	}
	
	//NOTA en RecuperaPeticioEmmagatzemadaWizard hi ha un exemple de decode base 64 
	
	//********************MAIL*******************************
	
	protected List<NodeRef> getMailTemplate() {
		//cerca el node del path
		return this.searchForNodes(CescaUtil.QUERY_MAIL_TEMPLATE);
	}
	
	protected void sendErrorMail(String process, String logFilename) {
		List<NodeRef> templates = this.searchForNodes(CescaUtil.QUERY_MAIL_TEMPLATE);
		if (templates != null && templates.size()>0 ) {//Only one template is supported in current version > no way to identify a template with a procedure
			Map<QName, Serializable> props = this.getServiceRegistry().getNodeService().getProperties(templates.get(0));
			
			//--------------------------------------------------------------------------------------------------------
			String group = (String) this.getServiceRegistry().getNodeService().getProperty(templates.get(0), CescaUtil.MAIL_PROP_USUARIS);
			if (CescaUtil.isEmpty(group)) {
				setTracerErrorMessage("No hi ha group a la plantilla, no es pot enviar el mail. Plantilla: "+templates.get(0).getId());
			} else {
				Set<String> users = this.getServiceRegistry().getAuthorityService().getContainedAuthorities(AuthorityType.USER, group, true);
				List<String> addresses = new ArrayList<String>();
				for (String personID : users) {
					NodeRef personNodeRef = this.getServiceRegistry().getPersonService().getPerson(personID);
					if (personNodeRef !=null) {
						String emailAddress = (String) this.getServiceRegistry().getNodeService().getProperty(personNodeRef, ContentModel.PROP_EMAIL);
						if (!CescaUtil.isEmpty(emailAddress)) {
							addresses.add(emailAddress);
						}
					}
				}
			//--------------------------------------------------------------------------------------------------------
				if (addresses.size() == 0) {
					setTracerErrorMessage("No s'ha pogut enviar el mail perque no hi ha direccions de mail al node: "+templates.get(0).getId());
				} else {
					String[] toAddress = new String[addresses.size()];
					addresses.toArray(toAddress);
					String subject = (String)props.get(CescaUtil.MAIL_PROP_SUBJECT);

					StringBuilder content = new StringBuilder();
					content.append("\nProces: "+process);
					content.append("\nData: "+CescaUtil.humanDateFormat.format(new Date()));
					content.append("\nArxiu de Log: "+logFilename+CescaUtil.LOG_EXTENSION);
					content.append("\nError: "+this.getTracerLastError());
					this.sendMail(CescaUtil.MAIL_NO_REPLY, toAddress, subject, content.toString());
				}
			}
		} else {
			if (templates == null || templates.size()==0 )
				setTracerErrorMessage("No s'ha pogut enviar el mail perque no hi ha plantilles de mail");
			else if (templates.size()>1)
				setTracerErrorMessage("No s'ha pogut enviar el mail perque no hi ha més d'una plantilla de mail");
		}
	}
	
	
	private void sendMail(final String fromAddress, final String[] toAddress, final String subject, final String content) {

		// create the MIME mail message
		MimeMessagePreparator mailPreparer = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage)
					throws MessagingException {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				mimeMessage.setHeader("Content-Transfer-Encoding", "UTF-8");
				message.setTo(toAddress);
				message.setFrom(fromAddress);
				message.setSubject(subject);
				message.setText(content, true);
			}
		};
		mailService.send(mailPreparer);

	}
	
	//********************TRACER*******************************
	protected Logger tracer;
	private String lastErrorMessage;
	private static NodeRef logFolder = null;
	protected NodeRef getLogFolder() {
		
		if (logFolder == null) {
			
    		if(log.isDebugEnabled())
    			log.debug("Cerca log folder");
			
    		logFolder = searchForANode(CescaUtil.PATH_LOG);

		}
		
		if (logFolder == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els logs no existeix");
		}
		return logFolder;
	}
	
	protected boolean containsError() {
		return !CescaUtil.isEmpty(lastErrorMessage);
	}
	
	protected String getTracerLastError() {
		return lastErrorMessage;
	}
	
	protected void setTracerErrorMessage(String error){
		lastErrorMessage = error;
		if(error==null){
		}else{
			tracer.error(error);
		}
	}
	
	protected void setTracerErrorMessage(String error, Exception e){
		lastErrorMessage = error;
		tracer.error(error, e);
	}
	
	protected void setTracerMessage(String msg){
		tracer.info(msg);
	}
		
	protected FileAppender startTracer(String filename) {
		String path = System.getenv(CescaUtil.SRV_HOME);
	
		if (CescaUtil.isEmpty(path))
			path = "";
		
		String logPath = path+"/"+ filename +CescaUtil.LOG_EXTENSION;
		
		//Path local de FS para evitar la indexación
		tracer = Logger.getLogger(filename);
		
	    SimpleLayout layout = new SimpleLayout();
	    FileAppender appender = null;
	    try {
	       appender = new FileAppender(layout, logPath, false);
	    } catch(IOException e) {
	    	tracer.info("Appender can not be created");
	    }
	    
	    tracer.addAppender(appender);
	    tracer.setLevel(Level.INFO);
	    
	    return appender;
	}
	
	protected void storeTracer(String process, String filename, FileAppender appender, boolean containsError) {
		if (appender == null) {
			return;
		}
	    if (tracer != null)
	    	tracer.removeAppender(appender);
	    
	    filename = filename+CescaUtil.LOG_EXTENSION;

	    FileInfo info = this.getServiceRegistry().getFileFolderService().create(getLogFolder(), filename, CescaUtil.TYPE_LOG);
		NodeRef node = info.getNodeRef();
		if (node == null) {
			throw new AlfrescoRuntimeException("No s'ha pogut crear el node de log");
		}
		Map<QName, Serializable> props = new HashMap<QName, Serializable>();
		props.put(ContentModel.PROP_NAME, filename);
		props.put(ContentModel.PROP_TITLE, filename);
		props.put(CescaUtil.LOG_PROP_ISERROR, Boolean.FALSE);
		props.put(CescaUtil.LOG_PROP_USUARI, this.getServiceRegistry().getAuthenticationService().getCurrentUserName());
		props.put(CescaUtil.LOG_PROP_PROCESS, process);
		props.put(CescaUtil.LOG_PROP_DATA, new Date());
		this.getServiceRegistry().getNodeService().setProperties(node, props);
		
	    // Write the content
        ContentWriter cw = this.getServiceRegistry().getContentService().getWriter(node,
            ContentModel.PROP_CONTENT, true);
        cw.setMimetype(MIMETYPE);
        cw.setEncoding("UTF-8");
        cw.putContent(new File(appender.getFile()));
        
        try {
        	File file = new File(appender.getFile());
        	file.delete();
        } catch (Exception e) {
		}
        
	    this.getServiceRegistry().getNodeService().setProperty(node, CescaUtil.LOG_PROP_ISERROR, containsError);
	}
	
	//***************************************************
	public class ExecuterException extends Exception {
		private static final long serialVersionUID = -7001572408107083398L;
		public ExecuterException(String message) {
			super(message);
		}
	}
	//***************************************************
	
	/**
	 * @return the serviceRegistry
	 */
	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	/**
	 * @param serviceRegistry the serviceRegistry to set
	 */
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * @return the mailService
	 */
	public JavaMailSender getMailService() {
		return mailService;
	}

	/**
	 * @param mailService the mailService to set
	 */
	public void setMailService(JavaMailSender mailService) {
		this.mailService = mailService;
	}
	
	
}
