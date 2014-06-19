package es.cesca.alfresco.Object;

public class Vocabulari {

	private static final long serialVersionUID = -557955106388369334L;
	
	private String nombreVoc;
	private boolean addPlantilla;
	
	public String getNombreVoc() {
		return nombreVoc;
	}
	public void setNombreVoc(String nombreVoc) {
		this.nombreVoc = nombreVoc;
	}
	public boolean isAddPlantilla() {
		return addPlantilla;
	}
	public void setAddPlantilla(boolean addPlantilla) {
		this.addPlantilla = addPlantilla;
	}
}
