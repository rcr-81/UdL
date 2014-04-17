package com.smile.webscripts.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Signatura implements Comparable<Signatura> {

	private String nom;
	private String nomView;
	private String dni;
	private Date data;
	private Date dataFiCert;
	private Date dataCaducitat;
	private String stringData;
	private String stringDataFiCert;
	private String stringDataCaducitat;
	private boolean estat;
	private String tipus;
	private String tipusAttached;
	private String formatAttached;
	private String carrec;
	private String carrecView;
	private Date dataIniCert;
	private String stringDataIniCert;
	private String formatSignatura;
	private String politiquesCertificat;
	private String proveidorCerficat;
	private String tipusCertificat;
	private String departamentSubjecte;
	private String numSerieSubjecte;		
	private String versioCertificat;
	private String organitzacioSubjecte;
	private String validacio;
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public Signatura() {
		this.nom = "";
		this.nomView = "";
		this.dni = "";
		this.data = null;
		this.dataFiCert = null;
		this.stringData = "";
		this.stringDataFiCert = "";
		this.estat = false;
		this.tipus = "";
		this.tipusAttached = "";
		this.formatAttached = "";
		this.carrec = "";
		this.carrecView = "";
	}

	public Signatura(String nom, String nomView, String dni, Date data, Date dataFiCert, String stringData, String stringdataFiCert,
			boolean estat, String tipus, String tipusAttached, String formatAttached, String carrec, String carrecView) {
		this.nom = nom;
		this.nomView = nomView;
		this.dni = dni;
		this.data = data;
		this.dataFiCert = dataFiCert;
		this.stringData = stringData;
		this.stringDataFiCert = stringdataFiCert;		
		this.estat = estat;
		this.tipus = tipus;
		this.tipusAttached = tipusAttached;
		this.formatAttached = formatAttached;
		this.carrecView = carrecView;
	}

	public int compareTo(Signatura paramT) {
		int result = 0;
		
		if(this.data != null) {
			return this.getData().compareTo(paramT.getData());
		}
		
		return result;
	}

	public String getCarrec() {
		return carrec;
	}

	public void setCarrec(String carrec) {
		this.carrec = carrec;
	}
	
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
		if(data != null){
			setStringData(sdf.format(data));
		}
	}

	public boolean isEstat() {
		return estat;
	}

	public void setEstat(boolean estat) {
		this.estat = estat;
	}

	public String getTipus() {
		return tipus;
	}

	public void setTipus(String tipus) {
		this.tipus = tipus;
	}

	public Date getDataFiCert() {
		return dataFiCert;
	}

	public void setDataFiCert(Date dataFiCert) {
		this.dataFiCert = dataFiCert;
		if(dataFiCert != null){
			setStringDataFiCert(sdf.format(dataFiCert));
		}
	}

	public String getStringDataFiCert() {
		return stringDataFiCert;
	}

	public void setStringDataFiCert(String stringDataFiCert) {
		this.stringDataFiCert = stringDataFiCert;
	}

	public String getNomView() {
		return nomView;
	}

	public void setNomView(String nomView) {
		this.nomView = nomView;
	}

	public String getCarrecView() {
		return carrecView;
	}

	public void setCarrecView(String carrecView) {
		this.carrecView = carrecView;
	}
	
	public Date getDataIniCert() {
		return dataIniCert;
	}

	public void setDataIniCert(Date dataIniCert) {
		this.dataIniCert = dataIniCert;
		if(dataIniCert != null){
			setStringDataIniCert(sdf.format(dataIniCert));
		}
	}

	public String getStringData() {
		return stringData;
	}

	public void setStringData(String stringData) {
		this.stringData = stringData;
	}

	public String getStringDataIniCert() {
		return stringDataIniCert;
	}

	public void setStringDataIniCert(String stringDataIniCert) {
		this.stringDataIniCert = stringDataIniCert;
	}

	public String getFormatSignatura() {
		return formatSignatura;
	}

	public void setFormatSignatura(String formatSignatura) {
		this.formatSignatura = formatSignatura;
	}

	public String getPolitiquesCertificat() {
		return politiquesCertificat;
	}

	public void setPolitiquesCertificat(String politiquesCertificat) {
		this.politiquesCertificat = politiquesCertificat;
	}

	public String getProveidorCerficat() {
		return proveidorCerficat;
	}

	public void setProveidorCerficat(String proveidorCerficat) {
		this.proveidorCerficat = proveidorCerficat;
	}

	public String getTipusCertificat() {
		return tipusCertificat;
	}

	public void setTipusCertificat(String tipusCertificat) {
		this.tipusCertificat = tipusCertificat;
	}

	public String getDepartamentSubjecte() {
		return departamentSubjecte;
	}

	public void setDepartamentSubjecte(String departamentSubjecte) {
		this.departamentSubjecte = departamentSubjecte;
	}

	public String getNumSerieSubjecte() {
		return numSerieSubjecte;
	}

	public void setNumSerieSubjecte(String numSerieSubjecte) {
		this.numSerieSubjecte = numSerieSubjecte;
	}

	public String getVersioCertificat() {
		return versioCertificat;
	}

	public void setVersioCertificat(String versioCertificat) {
		this.versioCertificat = versioCertificat;
	}

	public String getOrganitzacioSubjecte() {
		return organitzacioSubjecte;
	}

	public void setOrganitzacioSubjecte(String organitzacioSubjecte) {
		this.organitzacioSubjecte = organitzacioSubjecte;
	}

	public String getValidacio() {
		return validacio;
	}

	public void setValidacio(String validacio) {
		this.validacio = validacio;
	}

	public static SimpleDateFormat getSdf() {
		return sdf;
	}

	public static void setSdf(SimpleDateFormat sdf) {
		Signatura.sdf = sdf;
	}

	public Date getDataCaducitat() {
		return dataCaducitat;
	}

	public void setDataCaducitat(Date dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}

	public String getStringDataCaducitat() {
		return stringDataCaducitat;
	}

	public void setStringDataCaducitat(String stringDataCaducitat) {
		this.stringDataCaducitat = stringDataCaducitat;
	}

	public String getTipusAttached() {
		return tipusAttached;
	}

	public void setTipusAttached(String tipusAttached) {
		this.tipusAttached = tipusAttached;
	}

	public String getFormatAttached() {
		return formatAttached;
	}

	public void setFormatAttached(String formatAttached) {
		this.formatAttached = formatAttached;
	}
}