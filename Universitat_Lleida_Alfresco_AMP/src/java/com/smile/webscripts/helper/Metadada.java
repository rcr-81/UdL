package com.smile.webscripts.helper;

import org.w3c.dom.Node;

public class Metadada {
	String nom;
	String valor;
			
	public Metadada(Node metadada){
		this.nom = metadada.getNodeName();
		this.valor = metadada.getFirstChild().getNodeValue();		
	}
	
	public Metadada(String nom, String valor){
		this.nom = nom;
		this.valor = valor;		
	}
	
	public String getNom() {
		return nom;
	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getValor() {
		return valor;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}	
}