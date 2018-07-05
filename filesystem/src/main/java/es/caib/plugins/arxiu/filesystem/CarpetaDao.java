package es.caib.plugins.arxiu.filesystem;

import java.util.List;

import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ContingutArxiu;

public class CarpetaDao {
	
	private String identificador;
	private String nom;
	private String pare;
	private List<ContingutArxiu> informacioItems;

	public Carpeta getCarpeta() {
		Carpeta carpeta = new Carpeta();
		carpeta.setIdentificador(identificador);
		carpeta.setNom(nom);
		carpeta.setContinguts(informacioItems);
		return carpeta;
	}
	
	public CarpetaDao(
			String identificador,
			String nom,
			String pare,
			List<ContingutArxiu> informacioItems) {
		
		super();
		
		this.identificador = identificador;
		this.nom = nom;
		this.pare = pare;
		this.informacioItems = informacioItems;
	}


	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getPare() {
		return pare;
	}
	public void setPare(String pare) {
		this.pare = pare;
	}
	
	public List<ContingutArxiu> getInformacioItems() {
		return informacioItems;
	}
	public void setInformacioItems(List<ContingutArxiu> informacioItems) {
		this.informacioItems = informacioItems;
	}
	
	
}
