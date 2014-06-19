/**
 * CescaiArxiuWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package es.cesca.ws.client;

public class CescaiArxiuWSServiceLocator extends org.apache.axis.client.Service implements es.cesca.ws.client.CescaiArxiuWSService {

	private static final long serialVersionUID = 3209564033805433217L;

	public CescaiArxiuWSServiceLocator() {
    }


    public CescaiArxiuWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CescaiArxiuWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CescaiArxiuWS
    private java.lang.String CescaiArxiuWS_address = "http://127.0.0.1:8080/ws-axis/services/CescaiArxiuWS";

    public java.lang.String getCescaiArxiuWSAddress() {
        return CescaiArxiuWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CescaiArxiuWSWSDDServiceName = "CescaiArxiuWS";

    public java.lang.String getCescaiArxiuWSWSDDServiceName() {
        return CescaiArxiuWSWSDDServiceName;
    }

    public void setCescaiArxiuWSWSDDServiceName(java.lang.String name) {
        CescaiArxiuWSWSDDServiceName = name;
    }

    public es.cesca.ws.client.CescaiArxiuWS getCescaiArxiuWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CescaiArxiuWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCescaiArxiuWS(endpoint);
    }

    public es.cesca.ws.client.CescaiArxiuWS getCescaiArxiuWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            es.cesca.ws.client.CescaiArxiuWSSoapBindingStub _stub = new es.cesca.ws.client.CescaiArxiuWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getCescaiArxiuWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCescaiArxiuWSEndpointAddress(java.lang.String address) {
        CescaiArxiuWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (es.cesca.ws.client.CescaiArxiuWS.class.isAssignableFrom(serviceEndpointInterface)) {
                es.cesca.ws.client.CescaiArxiuWSSoapBindingStub _stub = new es.cesca.ws.client.CescaiArxiuWSSoapBindingStub(new java.net.URL(CescaiArxiuWS_address), this);
                _stub.setPortName(getCescaiArxiuWSWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CescaiArxiuWS".equals(inputPortName)) {
            return getCescaiArxiuWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://localhost:8080/ws-axis/services/CescaiArxiuWS", "CescaiArxiuWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://localhost:8080/ws-axis/services/CescaiArxiuWS", "CescaiArxiuWS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CescaiArxiuWS".equals(portName)) {
            setCescaiArxiuWSEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
