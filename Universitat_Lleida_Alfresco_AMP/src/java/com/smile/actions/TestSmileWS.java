package com.smile.actions;

import java.io.*;
import java.net.*;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
//http://jakarta.apache.org/commons/httpclient/dependencies.html
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class TestSmileWS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String TicketAlFresco="";
//Demanar ticket per operar capa serveis
		//
		 String url = "http://gdpreprod.udl.cat:8080/alfresco/service/api/login?u=admin&pw=admin";
		 try
		 {
			 HttpClient client = new HttpClient();
			 String response;

			 GetMethod method = new GetMethod(url);
			 int status = client.executeMethod(method);

			 TicketAlFresco = method.getResponseBodyAsString();
			 TicketAlFresco=TicketAlFresco.replaceAll("\\<\\?xml(.+?)\\?\\>\n<ticket>", "").trim();			 
			 TicketAlFresco=TicketAlFresco.replaceAll("</ticket>", "");
			 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	        
	      
			
		// Cridar operació capa serveis amb el Ticket
		  url = "http://gdpreprod.udl.cat:8080/alfresco/service/udl/carpeta/crearCarpeta?alf_ticket="+TicketAlFresco;
		  InputStream in = null;
		  String Resposta="";
	      //  http://gdpreprod.udl.cat:8080/alfresco/service/udl/carpeta/crearCarpeta?alf_ticket=TICKET_eb2045e88762be11df29b24d00a7580858355e1d
	        try {
	            HttpClient client2 = new HttpClient();
	            PostMethod method2 = new PostMethod(url);
	            	            
	            //String MetadadesXML="arguments=<?xml version=\"1.0\" encoding=\"utf-8\"?><arguments><username>admin</username><carpetaName>ProvaIsaac_a52828</carpetaName><carpetaPath>Company Home/Sites/dm/documentLibrary/Test visualització docs</carpetaPath><carpetaType>udl:expedient</carpetaType><metadades><descripcio_expedient>descripcio_expedient</descripcio_expedient><tipus_entitat_expedient>tipus_entitat_expedient</tipus_entitat_expedient><categoria_expedient>categoria_expedient</categoria_expedient><secuencial_identificador_expedient>secuencial_identificador_expedient</secuencial_identificador_expedient><esquema_identificador_expedient>esquema_identificador_expedient</esquema_identificador_expedient><data_inici_expedient>01/01/2013</data_inici_expedient><data_fi_expedient>02/01/2013</data_fi_expedient><classificacio_acces_expedient>Restringit</classificacio_acces_expedient><advertencia_seguretat_expedient>advertencia_seguretat_expedient</advertencia_seguretat_expedient><categoria_advertencia_seguretat_expedient>Solament accès autoritzat</categoria_advertencia_seguretat_expedient><sensibilitat_dades_caracter_personal_expedient>Alt</sensibilitat_dades_caracter_personal_expedient><condicions_acces_1_expedient>condicions_acces_1_expedient</condicions_acces_1_expedient><condicions_acces_2_expedient>condicions_acces_2_expedient</condicions_acces_2_expedient><tipus_acces_1_expedient>tipus_acces_1_expedient</tipus_acces_1_expedient><tipus_acces_2_expedient>tipus_acces_2_expedient</tipus_acces_2_expedient><idioma_1_expedient>en. Anglès</idioma_1_expedient><idioma_2_expedient>en. Anglès</idioma_2_expedient><idioma_3_expedient>en. Anglès</idioma_3_expedient><idioma_4_expedient>en. Anglès</idioma_4_expedient><valoracio_expedient>Sense cobertura d'avaluació</valoracio_expedient><tipus_dictamen_1_expedient>Conservació permanent</tipus_dictamen_1_expedient><accio_dictaminada_1_expedient>accio_dictaminada_1_expedient</accio_dictaminada_1_expedient><tipus_dictamen_2_expedient>Conservació permanent</tipus_dictamen_2_expedient><accio_dictaminada_2_expedient>accio_dictaminada_2_expedient</accio_dictaminada_2_expedient><suport_origen_expedient>Físic</suport_origen_expedient><dimensions_fisiques_expedient>dimensions_fisiques_expedient</dimensions_fisiques_expedient><quantitat_expedient>quantitat_expedient</quantitat_expedient><unitats_expedient>Volum</unitats_expedient><suport_1_expedient>Paper DIN A4</suport_1_expedient><suport_2_expedient>Paper DIN A4</suport_2_expedient><suport_3_expedient>Paper DIN A4</suport_3_expedient><suport_4_expedient>Paper DIN A4</suport_4_expedient><suport_5_expedient>Paper DIN A4</suport_5_expedient><suport_6_expedient>Paper DIN A4</suport_6_expedient><suport_7_expedient>Paper DIN A4</suport_7_expedient><suport_8_expedient>Paper DIN A4</suport_8_expedient><suport_9_expedient>Paper DIN A4</suport_9_expedient><suport_10_expedient>Paper DIN A4</suport_10_expedient><suport_11_expedient>Paper DIN A4</suport_11_expedient><suport_12_expedient>Paper DIN A4</suport_12_expedient><suport_13_expedient>Paper DIN A4</suport_13_expedient><suport_14_expedient>Paper DIN A4</suport_14_expedient><suport_15_expedient>Paper DIN A4</suport_15_expedient><suport_16_expedient>Paper DIN A4</suport_16_expedient><suport_17_expedient>Paper DIN A4</suport_17_expedient><suport_18_expedient>Paper DIN A4</suport_18_expedient><suport_19_expedient>Paper DIN A4</suport_19_expedient><suport_20_expedient>Paper DIN A4</suport_20_expedient><suport_21_expedient>Paper DIN A4</suport_21_expedient><localitzacio_1_expedient>localitzacio_1_expedient</localitzacio_1_expedient><localitzacio_2_expedient>localitzacio_2_expedient</localitzacio_2_expedient><algoritme_expedient>algoritme_expedient</algoritme_expedient><valor_expedient>valor_expedient</valor_expedient><codi_classificacio_1_expedient>codi_classificacio_1_expedient</codi_classificacio_1_expedient><denominacio_classe_1_expedient>denominacio_classe_1_expedient</denominacio_classe_1_expedient><codi_classificacio_2_expedient>codi_classificacio_2_expedient</codi_classificacio_2_expedient><denominacio_classe_2_expedient>denominacio_classe_2_expedient</denominacio_classe_2_expedient></metadades></arguments>";
	            
	            // INICI SMILE
	            String MetadadesXML="<arguments><username>admin</username><carpetaName>ProvaIsaac_a52828</carpetaName><carpetaPath>Company Home/Sites/dm/documentLibrary/Test visualització docs</carpetaPath><carpetaType>udl:expedient</carpetaType><metadades><descripcio_expedient>descripcio_expedient</descripcio_expedient><tipus_entitat_expedient>tipus_entitat_expedient</tipus_entitat_expedient><categoria_expedient>categoria_expedient</categoria_expedient><secuencial_identificador_expedient>secuencial_identificador_expedient</secuencial_identificador_expedient><esquema_identificador_expedient>esquema_identificador_expedient</esquema_identificador_expedient><data_inici_expedient>01/01/2013</data_inici_expedient><data_fi_expedient>02/01/2013</data_fi_expedient><classificacio_acces_expedient>Restringit</classificacio_acces_expedient><advertencia_seguretat_expedient>advertencia_seguretat_expedient</advertencia_seguretat_expedient><categoria_advertencia_seguretat_expedient>Solament accès autoritzat</categoria_advertencia_seguretat_expedient><sensibilitat_dades_caracter_personal_expedient>Alt</sensibilitat_dades_caracter_personal_expedient><condicions_acces_1_expedient>condicions_acces_1_expedient</condicions_acces_1_expedient><condicions_acces_2_expedient>condicions_acces_2_expedient</condicions_acces_2_expedient><tipus_acces_1_expedient>tipus_acces_1_expedient</tipus_acces_1_expedient><tipus_acces_2_expedient>tipus_acces_2_expedient</tipus_acces_2_expedient><idioma_1_expedient>en. Anglès</idioma_1_expedient><idioma_2_expedient>en. Anglès</idioma_2_expedient><idioma_3_expedient>en. Anglès</idioma_3_expedient><idioma_4_expedient>en. Anglès</idioma_4_expedient><valoracio_expedient>Sense cobertura d'avaluació</valoracio_expedient><tipus_dictamen_1_expedient>Conservació permanent</tipus_dictamen_1_expedient><accio_dictaminada_1_expedient>accio_dictaminada_1_expedient</accio_dictaminada_1_expedient><tipus_dictamen_2_expedient>Conservació permanent</tipus_dictamen_2_expedient><accio_dictaminada_2_expedient>accio_dictaminada_2_expedient</accio_dictaminada_2_expedient><suport_origen_expedient>Físic</suport_origen_expedient><dimensions_fisiques_expedient>dimensions_fisiques_expedient</dimensions_fisiques_expedient><quantitat_expedient>quantitat_expedient</quantitat_expedient><unitats_expedient>Volum</unitats_expedient><suport_1_expedient>Paper DIN A4</suport_1_expedient><suport_2_expedient>Paper DIN A4</suport_2_expedient><suport_3_expedient>Paper DIN A4</suport_3_expedient><suport_4_expedient>Paper DIN A4</suport_4_expedient><suport_5_expedient>Paper DIN A4</suport_5_expedient><suport_6_expedient>Paper DIN A4</suport_6_expedient><suport_7_expedient>Paper DIN A4</suport_7_expedient><suport_8_expedient>Paper DIN A4</suport_8_expedient><suport_9_expedient>Paper DIN A4</suport_9_expedient><suport_10_expedient>Paper DIN A4</suport_10_expedient><suport_11_expedient>Paper DIN A4</suport_11_expedient><suport_12_expedient>Paper DIN A4</suport_12_expedient><suport_13_expedient>Paper DIN A4</suport_13_expedient><suport_14_expedient>Paper DIN A4</suport_14_expedient><suport_15_expedient>Paper DIN A4</suport_15_expedient><suport_16_expedient>Paper DIN A4</suport_16_expedient><suport_17_expedient>Paper DIN A4</suport_17_expedient><suport_18_expedient>Paper DIN A4</suport_18_expedient><suport_19_expedient>Paper DIN A4</suport_19_expedient><suport_20_expedient>Paper DIN A4</suport_20_expedient><suport_21_expedient>Paper DIN A4</suport_21_expedient><localitzacio_1_expedient>localitzacio_1_expedient</localitzacio_1_expedient><localitzacio_2_expedient>localitzacio_2_expedient</localitzacio_2_expedient><algoritme_expedient>algoritme_expedient</algoritme_expedient><valor_expedient>valor_expedient</valor_expedient><codi_classificacio_1_expedient>codi_classificacio_1_expedient</codi_classificacio_1_expedient><denominacio_classe_1_expedient>denominacio_classe_1_expedient</denominacio_classe_1_expedient><codi_classificacio_2_expedient>codi_classificacio_2_expedient</codi_classificacio_2_expedient><denominacio_classe_2_expedient>denominacio_classe_2_expedient</denominacio_classe_2_expedient></metadades></arguments>";
	            // FI SMILE
	            
	            //Add any parameter if u want to send it with Post req.
	            //method2.addParameter("arguments", "<?xml version=\"1.0\" encoding=\"utf-8\"?><arguments><username>admin</username><carpetaName>ProvaIsaac_a52828</carpetaName><carpetaPath>Company Home/Sites/dm/documentLibrary/Test visualització docs</carpetaPath><carpetaType>udl:expedient</carpetaType><metadades><descripcio_expedient>descripcio_expedient</descripcio_expedient><tipus_entitat_expedient>tipus_entitat_expedient</tipus_entitat_expedient><categoria_expedient>categoria_expedient</categoria_expedient><secuencial_identificador_expedient>secuencial_identificador_expedient</secuencial_identificador_expedient><esquema_identificador_expedient>esquema_identificador_expedient</esquema_identificador_expedient><data_inici_expedient>01/01/2013</data_inici_expedient><data_fi_expedient>02/01/2013</data_fi_expedient><classificacio_acces_expedient>Restringit</classificacio_acces_expedient><advertencia_seguretat_expedient>advertencia_seguretat_expedient</advertencia_seguretat_expedient><categoria_advertencia_seguretat_expedient>Solament accès autoritzat</categoria_advertencia_seguretat_expedient><sensibilitat_dades_caracter_personal_expedient>Alt</sensibilitat_dades_caracter_personal_expedient><condicions_acces_1_expedient>condicions_acces_1_expedient</condicions_acces_1_expedient><condicions_acces_2_expedient>condicions_acces_2_expedient</condicions_acces_2_expedient><tipus_acces_1_expedient>tipus_acces_1_expedient</tipus_acces_1_expedient><tipus_acces_2_expedient>tipus_acces_2_expedient</tipus_acces_2_expedient><idioma_1_expedient>en. Anglès</idioma_1_expedient><idioma_2_expedient>en. Anglès</idioma_2_expedient><idioma_3_expedient>en. Anglès</idioma_3_expedient><idioma_4_expedient>en. Anglès</idioma_4_expedient><valoracio_expedient>Sense cobertura d'avaluació</valoracio_expedient><tipus_dictamen_1_expedient>Conservació permanent</tipus_dictamen_1_expedient><accio_dictaminada_1_expedient>accio_dictaminada_1_expedient</accio_dictaminada_1_expedient><tipus_dictamen_2_expedient>Conservació permanent</tipus_dictamen_2_expedient><accio_dictaminada_2_expedient>accio_dictaminada_2_expedient</accio_dictaminada_2_expedient><suport_origen_expedient>Físic</suport_origen_expedient><dimensions_fisiques_expedient>dimensions_fisiques_expedient</dimensions_fisiques_expedient><quantitat_expedient>quantitat_expedient</quantitat_expedient><unitats_expedient>Volum</unitats_expedient><suport_1_expedient>Paper DIN A4</suport_1_expedient><suport_2_expedient>Paper DIN A4</suport_2_expedient><suport_3_expedient>Paper DIN A4</suport_3_expedient><suport_4_expedient>Paper DIN A4</suport_4_expedient><suport_5_expedient>Paper DIN A4</suport_5_expedient><suport_6_expedient>Paper DIN A4</suport_6_expedient><suport_7_expedient>Paper DIN A4</suport_7_expedient><suport_8_expedient>Paper DIN A4</suport_8_expedient><suport_9_expedient>Paper DIN A4</suport_9_expedient><suport_10_expedient>Paper DIN A4</suport_10_expedient><suport_11_expedient>Paper DIN A4</suport_11_expedient><suport_12_expedient>Paper DIN A4</suport_12_expedient><suport_13_expedient>Paper DIN A4</suport_13_expedient><suport_14_expedient>Paper DIN A4</suport_14_expedient><suport_15_expedient>Paper DIN A4</suport_15_expedient><suport_16_expedient>Paper DIN A4</suport_16_expedient><suport_17_expedient>Paper DIN A4</suport_17_expedient><suport_18_expedient>Paper DIN A4</suport_18_expedient><suport_19_expedient>Paper DIN A4</suport_19_expedient><suport_20_expedient>Paper DIN A4</suport_20_expedient><suport_21_expedient>Paper DIN A4</suport_21_expedient><localitzacio_1_expedient>localitzacio_1_expedient</localitzacio_1_expedient><localitzacio_2_expedient>localitzacio_2_expedient</localitzacio_2_expedient><algoritme_expedient>algoritme_expedient</algoritme_expedient><valor_expedient>valor_expedient</valor_expedient><codi_classificacio_1_expedient>codi_classificacio_1_expedient</codi_classificacio_1_expedient><denominacio_classe_1_expedient>denominacio_classe_1_expedient</denominacio_classe_1_expedient><codi_classificacio_2_expedient>codi_classificacio_2_expedient</codi_classificacio_2_expedient><denominacio_classe_2_expedient>denominacio_classe_2_expedient</denominacio_classe_2_expedient></metadades></arguments>");
	            
	            //method2.setRequestBody(MetadadesXML);
	            //method2.setRequestContentLength(MetadadesXML.length());
	            //method2.addRequestHeader("Content-type", "multipart/form-data" );

	            // INICI SMILE
	            method2.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

	    		Part[] parts = { new StringPart("arguments", MetadadesXML, "UTF-8") };
	    		MultipartRequestEntity entity = new MultipartRequestEntity(parts, method2.getParams());
	    		method2.setRequestEntity(entity);
	            // FI SMILE
	    		
	            int statusCode = client2.executeMethod(method2);
	            
	            if (statusCode != -1) {
	             //   in = method2.getResponseBodyAsStream();
	            	Resposta = method2.getResponseBodyAsString();
	            }

	            System.out.println(in);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
}