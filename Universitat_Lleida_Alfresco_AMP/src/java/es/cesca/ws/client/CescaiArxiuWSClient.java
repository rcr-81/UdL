package es.cesca.ws.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.ws.client.exception.ParameterException;

public class CescaiArxiuWSClient {
	
	private static Log logger = LogFactory.getLog(CescaiArxiuWSClient.class);
	
	
	private CescaiArxiuWS ws;

	//CONSTRUCTORS
	public CescaiArxiuWSClient() throws ServiceException, MalformedURLException {
		
		CescaiArxiuWSServiceLocator locator = new CescaiArxiuWSServiceLocator();
		ws = locator.getCescaiArxiuWS(new URL(getiArxiuURL()));
		
		//Configure timeout
		((CescaiArxiuWSSoapBindingStub)ws).setTimeout(getTimeOut());
	}
	
	//OPERATIONS
	public String consultaEstatPeticio(String idMexMid) throws RemoteException, ParameterException {
		
		if (logger.isDebugEnabled())
			logger.debug("Consulta Estat Petició: idMexMid > "+idMexMid);
		
		if (StringUtils.isEmpty(idMexMid))
			throw new ParameterException("idMexMid", "consultaEstatPeticio");
		
		String result = ws.consultaEstatPeticio(idMexMid);
		
		if (logger.isDebugEnabled())
			logger.debug("Consulta Estat Petició: result > "+result);
			
		return result;
	}

	public String eliminaPeticio(String pia) throws RemoteException, ParameterException {

		if (logger.isDebugEnabled())
			logger.debug("Elimina Petició: PIA > "+pia);
		
		if (StringUtils.isEmpty(pia))
			throw new ParameterException("pia", "eliminaPeticio");
		
		String result = ws.eliminaMETS(pia);
		
		if (logger.isDebugEnabled())
			logger.debug("Elimina Petició: result > "+result);
			
		return result;
	}

	/**
	 * @param numExp
	 * @param acronim
	 * @return Identificador de la peticio
	 * @throws RemoteException
	 * @throws ParameterException 
	 */
	public String obrirConnexio(String numExp) throws RemoteException, ParameterException {
		
		String acronim = getAcronim();
		
		if (logger.isDebugEnabled())
			logger.debug("Obrir Connexio: numExp > "+numExp+"  acronim > "+ acronim);
		
		if (StringUtils.isEmpty(acronim))
			throw new ParameterException("acronim", "obrirConnexio");
		
		if (StringUtils.isEmpty(numExp))
			throw new ParameterException("numExp", "obrirConnexio");
		
		String result = ws.obrirConnexio(numExp, acronim);
		
		if (logger.isDebugEnabled())
			logger.debug("Obrir Connexio: result > "+result);
			
		return result;
	}

	/**
	 * @param idMexMid
	 * @param metadata
	 * @param document
	 * @return
	 * @throws RemoteException
	 * @throws ParameterException 
	 */
	public String peticioDocument(String idMexMid, String metadata, String nomDoc, String document) throws RemoteException, ParameterException {
		
		if (logger.isDebugEnabled())
			logger.debug("Petició doc: idMexMid > "+idMexMid+" metadata > "+metadata+" nomDoc > "+nomDoc+"  document > "+getAcronim()+"  acronim > not attached");
		
		//idMexMid NO es obligatorio cuando el doc es indepe
		
		if (StringUtils.isEmpty(metadata))
			throw new ParameterException("metadata", "peticioDocument");
		
		if (StringUtils.isEmpty(nomDoc))
			throw new ParameterException("nomDoc", "peticioDocument");
		
		if (StringUtils.isEmpty(document))
			throw new ParameterException("document", "peticioDocument");

		
		String result = ws.peticioDocument(idMexMid, metadata, nomDoc, document, getAcronim());
				
		if (logger.isDebugEnabled())
			logger.debug("Petició doc: result > "+result);
			
		return result;
	}

	/**
	 * @param idMexMid
	 * @param metadata
	 * @return
	 * @throws RemoteException
	 * @throws ParameterException 
	 */
	public String peticioExpedient(String idMexMid, String metadata) throws RemoteException, ParameterException {
				
		if (logger.isDebugEnabled())
			logger.debug("Petició exp: idMexMid > "+idMexMid+" metadata > "+metadata);
		
		if (StringUtils.isEmpty(idMexMid))
			throw new ParameterException("idMexMid", "peticioExpedient");
		
		if (StringUtils.isEmpty(metadata))
			throw new ParameterException("metadata", "peticioExpedient");
		
		String result = ws.peticioExpedient(idMexMid, metadata);
		
		if (logger.isDebugEnabled())
			logger.debug("Petició exp: result > "+result);
			
		return result;
	}

	/**
	 * @param idMexMid
	 * @param idDoc
	 * @param metadata
	 * @param signatura
	 * @return
	 * @throws RemoteException
	 * @throws ParameterException 
	 */
	public String peticioSignatura(String idMexMid, String idDoc, String metadata, String nomSignatura, String signatura) throws RemoteException, ParameterException {
		
		if (logger.isDebugEnabled())
			logger.debug("Petició sig: idMexMid > "+idMexMid+" idDoc > "+idDoc+" metadata > "+metadata+" signatura attached");
		
		//idMexMid no es obligatorio siempre, ya que los documentos indepe tb envian signatures
//		if (StringUtils.isEmpty(idMexMid))
//			throw new ParameterException("idMexMid", "peticioSignatura");
		
		if (StringUtils.isEmpty(idDoc))
			throw new ParameterException("idDoc", "peticioSignatura");
		
		if (StringUtils.isEmpty(metadata))
			throw new ParameterException("metadata", "peticioSignatura");
		
		if (StringUtils.isEmpty(idDoc))
			throw new ParameterException("nomSignatura", "peticioSignatura");
		
		if (StringUtils.isEmpty(metadata))
			throw new ParameterException("signatura", "peticioSignatura");
		
		System.out.println("---------------------> idMexMid: " + idMexMid);
		System.out.println("---------------------> idDoc: " + idDoc);
		System.out.println("---------------------> metadata: " + metadata);
		System.out.println("---------------------> signatura: " + signatura);
		System.out.println("---------------------> nomSignatura: " + nomSignatura);
		
		String result = ws.peticioSignatura(idMexMid, idDoc, metadata, signatura, nomSignatura);
		
		System.out.println("---------------------> result: " + result);
		
		if (logger.isDebugEnabled())
			logger.debug("Petició sig: result > "+result);
			
		return result;
	}

	/**
	 * @param pia
	 * @return
	 * @throws RemoteException
	 * @throws ParameterException 
	 */
	public String recuperaPIC(String pia) throws RemoteException, ParameterException {
		
		System.out.println("Recupera Peticio: PIA > "+pia);
		
		if (StringUtils.isEmpty(pia))
			throw new ParameterException("pia", "recuperaPIA");
		
		String result = ws.recuperaPIC(pia);
		
		System.out.println("Recupera petició: result > ");
			
		return result;
	}
	
	private static String expedient = "EXPEDIENT";
	private static String document = "DOCUMENT";

	/**
	 * @param idMexMid
	 * @return
	 * @throws RemoteException
	 * @throws ParameterException 
	 */
	public String tancarConnexio(String idMexMid, String tipus)
			throws RemoteException, ParameterException {
		
		
		
		if (logger.isDebugEnabled())
			logger.debug("Obrir Connexio: idMexMid > "+idMexMid+" tipus > "+tipus);
		
		if (StringUtils.isEmpty(idMexMid))
			throw new ParameterException("idMexMid", "tancarConnexio");
		
		if ((StringUtils.isEmpty(tipus) || (!tipus.equalsIgnoreCase(expedient) && !tipus.equalsIgnoreCase(document)))){
			if (logger.isDebugEnabled())
				logger.debug("Error tipus conexió és obligatori");
			
			throw new ParameterException("tipus", "tancarConnexio");
		}
		
		String result = ws.tancarConnexio(idMexMid, tipus);
		
		if (logger.isDebugEnabled())
			logger.debug("Tanca Connexio: result > "+result);
			
		return result;
	}

	private static final String SRV_HOME = "CESCA_FOLDER";
	private static final String CFG_PATH = "configCesca.cfg";
	private static final String TIMEOUT_PROP = "timeout";
	private static final String ACRONIM_PROP = "acronim";
	private static final String IARXIU_URL_PROP = "iarxiu-url";
	private static Integer TIMEOUT = -1;
	private static String ACRONIM = null;
	private static String IARXIU_URL = null;
	
	public static String getiArxiuURL() {
		
		if (IARXIU_URL != null) {
			return IARXIU_URL;
		}
		
		getWSProperties();

        if (logger.isDebugEnabled())
        	logger.debug("iArxiu url recovered > "+ IARXIU_URL);
        
		return IARXIU_URL;
	}
	
	private static String getAcronim() {
		
		if (ACRONIM != null){
			return ACRONIM;
		}
		
		getWSProperties();

        if (logger.isDebugEnabled())
        	logger.debug("Acronim recovered > "+ ACRONIM);
        
		return ACRONIM;
	}
	
	private static Integer getTimeOut() {
		
		if (TIMEOUT > 0l){
			return TIMEOUT;
		}
		
		getWSProperties();

        if (logger.isDebugEnabled())
        	logger.debug("Timeout recovered > "+ TIMEOUT);
        
		return TIMEOUT;
	}
	
	private static void getWSProperties() {
		String path = System.getenv(SRV_HOME);
		
		if (logger.isDebugEnabled())
			logger.debug("SRV_HOME > "+ path);
		
		if (path == null || path.trim().length() == 0){
			throw new RuntimeException(SRV_HOME+" can not be empty: ");
//			path = "/opt/dev/workspace_cesca/cesca_configuration/tomcat/";
		}

        Properties props = new Properties();
        FileInputStream fis = null;
        try {
        	fis = new FileInputStream(path +"/"+CFG_PATH);
			props.load(fis);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			if(fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					logger.warn(e.getMessage());
				}
		}
        TIMEOUT = Integer.valueOf((String)props.get(TIMEOUT_PROP));
		ACRONIM = (String)props.get(ACRONIM_PROP);
		IARXIU_URL = (String)props.get(IARXIU_URL_PROP);
		
	}

}
