/**
 * CescaiArxiuWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.cesca.ws.client;

public interface CescaiArxiuWS extends java.rmi.Remote {
    public java.lang.String obrirConnexio(java.lang.String numexp, java.lang.String acronim) throws java.rmi.RemoteException;
    public java.lang.String peticioExpedient(java.lang.String idExpMid, java.lang.String metadades) throws java.rmi.RemoteException;
    public java.lang.String peticioDocument(java.lang.String idExpMid, java.lang.String metadades, java.lang.String nomDoc, java.lang.String document,java.lang.String acronim) throws java.rmi.RemoteException;
    public java.lang.String peticioSignatura(java.lang.String idExpMid, java.lang.String idDoc, java.lang.String metadades, java.lang.String signatura, java.lang.String nomSignatura) throws java.rmi.RemoteException;
    public java.lang.String tancarConnexio(java.lang.String idExpMid, java.lang.String tipus) throws java.rmi.RemoteException;
    public java.lang.String consultaEstatPeticio(java.lang.String idExpMid) throws java.rmi.RemoteException;
    public java.lang.String recuperaPIC(java.lang.String pia) throws java.rmi.RemoteException;
    public java.lang.String eliminaMETS(java.lang.String idPia) throws java.rmi.RemoteException;
}
