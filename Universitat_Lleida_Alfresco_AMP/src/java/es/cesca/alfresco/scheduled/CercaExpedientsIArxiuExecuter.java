package es.cesca.alfresco.scheduled;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.cesca.alfresco.util.CescaUtil;
import es.cesca.alfresco.util.XMLHelper;
import es.cesca.alfresco.util.XMLHelper.MandatoryFieldException;

public class CercaExpedientsIArxiuExecuter extends ExecuterAbstractBase {

	private static Log logger = LogFactory.getLog(CercaExpedientsIArxiuExecuter.class);
	@Override
	protected void executeImpl(Action action, NodeRef node) {
		
		System.out.println("Entrant: CercaExpedientsIArxiuExecuter: executeImpl");
		String query = null;
		boolean isPeticioExp = false;
		String idConfiguracio = "";
		
		if(logger.isDebugEnabled())
			if(node!=null) logger.debug("Inici cerca expedients pel node: "+ node.getId());
		
		//Recupera les dades del node
		if(node!=null){
			Map<QName, Serializable> props = getServiceRegistry().getNodeService().getProperties(node);
			if(logger.isDebugEnabled())
				logger.debug("Comença la cerca d'elements pels que crear una peticio. Configuracio a carregar: "+props.get(ContentModel.PROP_NAME));	
			
			query = (String)props.get(CescaUtil.CONFIGURACIO_PROP_CRITERI_CERCA);
			isPeticioExp = (Boolean)props.get(CescaUtil.CONFIGURACIO_PROP_PETICIO_DOC_EXP);
			idConfiguracio = (String)props.get(CescaUtil.CONFIGURACIO_PROP_ID_CONFIG);
		}
		
		System.out.println("Iniciem peticions IArxiu amb valor peticionExp " + isPeticioExp);
		System.out.println("valor query a executar " + query);
		//Creamos tambien Iarxiu para expedientes
		createPeticioIArxiuFromCFG(query, isPeticioExp, null, idConfiguracio);
		System.out.println("Peticiones IArxiu creadas correctament");
		
		//Creamos tambien peticiones Iarxiu para peticiones de documentos sin expedientes si los hay
		//createPeticioIArxiuFromDocsSenseExp();
	}

	/*private void createPeticioIArxiuFromDocsSenseExp() {
		if(logger.isDebugEnabled())
			logger.debug("Creamos consulta IArxiu para docs sense exp");

		String queryDocsWithoutExp = null;
		queryDocsWithoutExp = "+PATH:\"/app:company_home/cm:CESCA/cm:Administracio/cm:DocumentsSenseExpedient//.\" +TYPE:\"cesca:CESCA_CFG_DOCUMENT_SENSE_EXP\"";

		//Executa la consulta
    	SearchParameters spQuery = new SearchParameters();
    	spQuery.setLanguage(SearchService.LANGUAGE_LUCENE);
    	spQuery.setQuery(queryDocsWithoutExp);
    	spQuery.addStore(CescaUtil.STORE);
		
		ResultSet resultSetQuery = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("QueryDocsWithoutExp (cercaExpedientsIArxiu) > "+ queryDocsWithoutExp);
			
			resultSetQuery = serviceRegistry.getSearchService().query(spQuery);
            if(resultSetQuery != null) {
            	for (NodeRef noderef : resultSetQuery.getNodeRefs()) {
            		FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(noderef);
        	        String querySign = (String) fo.getProperties().get(CescaUtil.CFG_DOC_PROP_CRITERI_CERCA_SIG_SENSE);
        	      //Executa la consulta
        	    	SearchParameters spSign = new SearchParameters();
        	    	spSign.setLanguage(SearchService.LANGUAGE_LUCENE);
        	    	spSign.setQuery(querySign);
        	    	spSign.addStore(CescaUtil.STORE);
        			
        			ResultSet resultSetSignatures = null;
        			try {
        				 if (logger.isDebugEnabled())
             				logger.debug("Querysignatures (cercaExpedientsIArxiu) > "+ querySign);
        				
        				resultSetSignatures = serviceRegistry.getSearchService().query(spSign);
        	            if(resultSetSignatures != null) {
        	            	//creamos peticion iarxiu para cada config file de doc
        	            	if (logger.isDebugEnabled())
        	    				logger.debug("creamos peticion iarxiu para cada config file de doc > ");
        	            	
        	    			List<NodeRef> nodesDocSenseExp = resultSetSignatures.getNodeRefs();
        	    			if(nodesDocSenseExp != null && nodesDocSenseExp.size() > 0) {
        	    				for (NodeRef nodesDocSense : nodesDocSenseExp) {
            	    				UserTransaction tx = null;
                            		try {
                        			//Creamos peticiones
                        			tx = this.getServiceRegistry().getTransactionService().getNonPropagatingUserTransaction();
                        	        tx.begin();
                        	        createPeticioIArxiu(nodesDocSense);

                        			tx.commit();
                        			tx = null;
                            		} catch (ExecuterException e) {
                            			logger.error(e.getMessage(),e);
                            			e.printStackTrace();
                            		} catch (AlfrescoRuntimeException e) {
                            			logger.error("S'ha produit un error al repositori alfresco: " +e.getMessage(), e);
                            			e.printStackTrace();
                            		} catch (Exception e) {
                            			logger.error("S'ha produit un error no identificat: " +e.getMessage(), e);
                            			e.printStackTrace();
                            		} finally {
                            			try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
                            		}
        	    				}
        	    			}
        	    			
        	            }
        			}
    	            catch (Exception e) {
    	    			logger.error(e.getMessage(),e);
    	    			e.printStackTrace();
    	    		} finally {
    	    			
    	        		if(logger.isDebugEnabled())
    	        			logger.debug("Tanca resultSetSignatures");
    	    			if (resultSetSignatures != null) {
    	    				resultSetSignatures.close();
    	    			}
    	    			
    	    		}
            	}
            }
		}catch (Exception e) {
    			logger.error(e.getMessage(),e);
    			e.printStackTrace();
		} finally {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Tanca resultSetQuery");
			if (resultSetQuery != null) {
				resultSetQuery.close();
			}	
		}
	}*/

	private void createPeticioIArxiuFromCFG(String query, boolean isPeticioExp, String manual, String idConfiguracio) {
		if(logger.isDebugEnabled())
			logger.debug("Cerca expedients: "+ query);
		
		if(query!=null){
			//Executa la consulta
	    	SearchParameters sp = new SearchParameters();
			sp.setLanguage(SearchService.LANGUAGE_LUCENE);
			sp.setQuery(query);
			sp.addStore(CescaUtil.STORE);
			
			ResultSet resultSet = null;
			try {
				resultSet = serviceRegistry.getSearchService().query(sp);
			
	            if(resultSet != null) {
	            	
	    			List<NodeRef> nodes = resultSet.getNodeRefs();
	    			if(nodes != null && nodes.size() > 0) {
	    				
	            		if(logger.isDebugEnabled())
	            			logger.debug("Resultats cerca: "+ nodes.size());

	        			for (NodeRef row : nodes) {
	                		//Per cada resultat genera una peticio
	                		UserTransaction tx = null;
	                		try {
	                	        tx = this.getServiceRegistry().getTransactionService().getNonPropagatingUserTransaction();
	                	        tx.begin();
	                			
	                	        boolean peticioCreada = createPeticioIArxiu(row, isPeticioExp, null, idConfiguracio);
	                	        if(peticioCreada) {
	                	        	tx.commit();
	                	        	tx = null;
	                	        }
	                		} catch (ExecuterException e) {
	                			logger.error(e.getMessage(),e);
	                		} catch (AlfrescoRuntimeException e) {
	                			logger.error("S'ha produit un error al repositori alfresco: " +e.getMessage(), e);
	                		} catch (Exception e) {
	                			logger.error("S'ha produit un error no identificat: " +e.getMessage(), e);
	                		} finally {
	                			try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
	                		}
	                	}
	    			}
	
		    	}
			
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				//e.printStackTrace();
			} finally {
				
	    		if(logger.isDebugEnabled())
	    			logger.debug("Tanca resultset");
				if (resultSet != null) {
					resultSet.close();
				}
				
			}
		}
	}

	public boolean createPeticioIArxiu(NodeRef noderef, boolean isPeticioExp, String manual, String idConfiguracio) 
			throws ExecuterException, MandatoryFieldException, XMLParseException {

		System.out.println("creando peticio IArxiu");
		
		System.out.println("valor isPeticioExp "+ isPeticioExp);
		
		if(logger.isDebugEnabled())
			logger.debug("Inici creaPeticio: "+ noderef.getId());
		
		//Con el nodo recuperamos su tipo y buscamos su configuracion activa
		QName type = this.getServiceRegistry().getNodeService().getType(noderef);
		
		if(logger.isDebugEnabled())
			logger.debug("type config " + type.toPrefixString());
		
		//En aquest punt no sabem si la peticio es d'un expedient o un document
		if(logger.isDebugEnabled())
			logger.debug("Recuperem el fitxer de configuracio amb id configuracio: " + idConfiguracio + ". En cas de ser NULL agafarem la configuració establerta per defecte.");
		System.out.println("Recuperem el fitxer de configuracio amb id configuracio: " + idConfiguracio + ". En cas de ser NULL agafarem la configuració establerta per defecte.");
		
		NodeRef configuration = null;
		
		if(idConfiguracio == null && isPeticioExp) {
			configuration = this.getExpedientCFGDefault();
			if(configuration != null) {
				FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(configuration);
				idConfiguracio = (String)fo.getProperties().get(CescaUtil.CFG_EXP_PROP_ID_CONFIG);
			}
		} else if(isPeticioExp) {
			configuration = this.getExpedientCFG(idConfiguracio);
		}
		
		
		if (configuration == null) { //No es un expedient o expedient amb error a la cerca per defecte
			System.out.println("tratando docs");
				if(manual==null) {
					if(isPeticioExp){
						System.out.println("creando peticion documentos con expedientes ");
						configuration = this.getDocumentConExpCFG(idConfiguracio);
					}else{
						System.out.println("creando peticion documentos sin expedientes ");
						configuration = this.getDocumentSinExpCFG(idConfiguracio);
					}
				//si es una peticio manual hem d'agafar la configuracio per defecte, si hi ha
				} else{
					System.out.println("creando peticion manual ");
					configuration = this.getDocumentSinExpCFGPerDefecte();
					if(configuration != null) {	
						FileInfo fo = this.getServiceRegistry().getFileFolderService().getFileInfo(configuration);
						idConfiguracio = (String)fo.getProperties().get(CescaUtil.CFG_DOC_PROP_ID_CONFIG);
					}	
				}
			if(logger.isDebugEnabled())
				logger.debug("tenemos configuracion documento, no es expediente " + configuration.getId());
			
				if (configuration == null) {
					
					throw new ExecuterException("No s'ha trobat l'arxiu de configuracio per l'expedient/document amb id configuracio: "+idConfiguracio+ " [pot NO ser un error si el node no eés ni un expedient ni un document apte per enviar a iArxiu]");
				}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("Tipus d'expedient/document: "+type.toPrefixString() +" node amb la configuracio: "+configuration.getId());
		
		System.out.println("Tipus d'expedient/document: "+type.toPrefixString() +" node amb la configuracio: "+configuration.getId());
		
		if(logger.isDebugEnabled())
			logger.debug("antes leer XML ");
		
		ContentReader reader = this.getServiceRegistry().getContentService().getReader(configuration, ContentModel.PROP_CONTENT);
		String xmlbase = reader.getContentString();
		//---
		
		//Recupera metadades ---
		Map<QName, Serializable> props = this.getServiceRegistry().getNodeService().getProperties(noderef);
		Map<String, Serializable> toBind = this.getProperties(props);
		
		if(logger.isDebugEnabled())
			logger.debug("recuperamos metadatos para XML ");
		
		XMLHelper helper = new XMLHelper(xmlbase);
		helper.xmlToIArxiu(toBind, this.serviceRegistry, false);
		
		if (!helper.isExp() && !helper.isDoc()) {
			//No puede ser que el xml de un expediente diga que no lo es >> Exception
			throw new ExecuterException("El node actual és un expedient/document però no s'ha pogut localitzar la propietat que l'identifica com expedient/document");
		}
		
		if(logger.isDebugEnabled())
			logger.debug("fin leer XML, creamos peticion  IArxiu");
		

		props = new HashMap<QName, Serializable>();
				
		String name = "P-"+noderef.getId();//node name has no restriction
		
		props.put(CescaUtil.PETICIO_PROP_ID, noderef.getId());//id de la peticio - >>> NO hay forma de encontrar los numexp/numdoc una vez son peticiones, así que lo mejor es guardar aquí el node_id de alfresco
		if(helper.isExp()) {
			props.put(CescaUtil.PETICIO_PROP_NUMEXP, helper.getNumExp());//recover from XML binding
			if(logger.isDebugEnabled())
				logger.debug("Es procedeix a crear la peticio: "+name+" per expedient: "+helper.getNumExp()+" en estat: "+CescaUtil.STATUS_PENDENT);
		} else {
			props.put(CescaUtil.PETICIO_PROP_NUM_DOC, helper.getNumDoc());//recover from XML binding
			if(logger.isDebugEnabled())
				logger.debug("Es procedeix a crear la peticio: "+name+" per document: "+helper.getNumDoc()+" en estat: "+CescaUtil.STATUS_PENDENT);
		}
		helper = null;//Helper will not be required anymore > break reference >> GC
		props.put(CescaUtil.PETICIO_PROP_ESTAT, CescaUtil.STATUS_PENDENT);
		props.put(ContentModel.PROP_NAME, name);
		props.put(ContentModel.PROP_TITLE, name);
		props.put(CescaUtil.PETICIO_PROP_ID_CONFIG, idConfiguracio);
		
		NodeRef parent = getPeticionsFolderNode();
	
		//crea node de tipus peticio amb les metadades corresponents ESTO GENERA EL ERROR QUE NO QUIEREN QUE SE VEA
		
		try {
			FileInfo info = getServiceRegistry().getFileFolderService().create(parent, name, CescaUtil.TYPE_PETICIO);
			
			NodeRef node = info.getNodeRef();
			
			getServiceRegistry().getNodeService().setProperties(node, props);
			
			System.out.println("finalizada peticion IArxiu con id "+noderef.getId());
			return true;
			//No hi ha contingut per guardar dins la peticio.
		} catch(FileExistsException e) {
			//no mostramos aviso por los logs. Solo puede existir una petición, por tanto si peta es porque ya existe una y es correcto que no se cree y se controla el bool
			return false;
		}
	}
	
	private static NodeRef peticioFolderPeticio = null;
	private static NodeRef peticioFolderVocabulari = null;
	private static NodeRef peticioFolderAtribut = null;
	protected NodeRef getPeticionsFolderNode() {
		
		if (peticioFolderPeticio == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder peticions");
			
    		peticioFolderPeticio = searchForANode(CescaUtil.PATH_PETICIO);

		}
		
		if (peticioFolderPeticio == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar les peticions no existeix");
		}
		return peticioFolderPeticio;
		
	}
	
	protected NodeRef getPeticionsFolderVocabulariNode() {
		
		if (peticioFolderVocabulari == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder vocabularis");
			
    		peticioFolderVocabulari = searchForANode(CescaUtil.PATH_VOCABULARI);

		}
		
		if (peticioFolderVocabulari == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els vocabularis no existeix");
		}
		return peticioFolderVocabulari;
		
	}
	
	protected NodeRef getPeticionsFolderAtributosNode() {
		
		if (peticioFolderAtribut == null) {
			
    		if(logger.isDebugEnabled())
    			logger.debug("Cerca folder atributos");
			
    		peticioFolderAtribut = searchForANode(CescaUtil.PATH_ATRIBUTO);

		}
		
		if (peticioFolderAtribut == null) {
			throw new AlfrescoRuntimeException("L'espai per deixar els atributos no existeix");
		}
		return peticioFolderAtribut;
		
	}
	
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> arg0) {
		//Nothing to do
	}
	
}
