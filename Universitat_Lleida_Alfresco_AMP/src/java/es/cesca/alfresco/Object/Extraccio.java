package es.cesca.alfresco.Object;

public class Extraccio {

	private static final long serialVersionUID = -557955106388369334L;
	
	private String tipusDocumentalExp;
	private String tipusDocumentalDoc;
	private String tipusDocumentalSign;
	
	private String selExp;
	private String selDoc;
	private String selSign;
	
	private String plantillaExp;
	private String plantillaDoc;
	private String plantillaSign;
	
	private boolean checkboxExpedient;
	private boolean checkboxDocument;
	private boolean checkboxSignatura;
	
	private String name;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isCheckboxExpedient() {
		return checkboxExpedient;
	}
	public void setCheckboxExpedient(boolean checkboxExpedient) {
		this.checkboxExpedient = checkboxExpedient;
	}
	public boolean isCheckboxDocument() {
		return checkboxDocument;
	}
	public void setCheckboxDocument(boolean checkboxDocument) {
		this.checkboxDocument = checkboxDocument;
	}
	public boolean isCheckboxSignatura() {
		return checkboxSignatura;
	}
	public void setCheckboxSignatura(boolean checkboxSignatura) {
		this.checkboxSignatura = checkboxSignatura;
	}
	public String getTipusDocumentalExp() {
		return tipusDocumentalExp;
	}
	public void setTipusDocumentalExp(String tipusDocumentalExp) {
		this.tipusDocumentalExp = tipusDocumentalExp;
	}
	public String getTipusDocumentalDoc() {
		return tipusDocumentalDoc;
	}
	public void setTipusDocumentalDoc(String tipusDocumentalDoc) {
		this.tipusDocumentalDoc = tipusDocumentalDoc;
	}
	public String getTipusDocumentalSign() {
		return tipusDocumentalSign;
	}
	public void setTipusDocumentalSign(String tipusDocumentalSign) {
		this.tipusDocumentalSign = tipusDocumentalSign;
	}
	public String getSelExp() {
		return selExp;
	}
	public void setSelExp(String selExp) {
		this.selExp = selExp;
	}
	public String getSelDoc() {
		return selDoc;
	}
	public void setSelDoc(String selDoc) {
		this.selDoc = selDoc;
	}
	public String getSelSign() {
		return selSign;
	}
	public void setSelSign(String selSign) {
		this.selSign = selSign;
	}
	public String getPlantillaExp() {
		return plantillaExp;
	}
	public void setPlantillaExp(String plantillaExp) {
		this.plantillaExp = plantillaExp;
	}
	public String getPlantillaDoc() {
		return plantillaDoc;
	}
	public void setPlantillaDoc(String plantillaDoc) {
		this.plantillaDoc = plantillaDoc;
	}
	public String getPlantillaSign() {
		return plantillaSign;
	}
	public void setPlantillaSign(String plantillaSign) {
		this.plantillaSign = plantillaSign;
	}
}