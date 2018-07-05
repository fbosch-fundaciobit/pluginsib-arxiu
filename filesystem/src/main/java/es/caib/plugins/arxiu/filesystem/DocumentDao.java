package es.caib.plugins.arxiu.filesystem;

import java.util.Date;
import java.util.List;

import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.Firma;

public class DocumentDao {
	
	private String identificador;
	private String nom;
	
	private String versio;
	private String gestorVersions;
	private boolean esborrany;
	private String pare;
	
	private String metadadesid;
	private String versioNti;
	private List<String> organs;
	private Date data;
	private ContingutOrigen origen;
	private DocumentEstatElaboracio estatElaboracio;
	private DocumentTipus tipusDocumental;
	private String serieDocumental;
	
	private List<Firma> firmes;
	
	private String tipusMime;
	private String identificadorOrigen;
	
	
	public DocumentDao(
			String identificador,
			String nom,
			String versio,
			String gestorVersions,
			boolean esborrany,
			String pare,
			String metadadesid,
			String versioNti,
			List<String> organs,
			Date data,
			ContingutOrigen origen,
			DocumentEstatElaboracio estatElaboracio,
			DocumentTipus tipusDocumental,
			String serieDocumental,
			List<Firma> firmes,
			String tipusMime,
			String identificadorOrigen) {
		super();
		this.identificador = identificador;
		this.nom = nom;
		this.versio = versio;
		this.gestorVersions = gestorVersions;
		this.esborrany = esborrany;
		this.pare = pare;
		this.metadadesid = metadadesid;
		this.versioNti = versioNti;
		this.organs = organs;
		this.data = data;
		this.origen = origen;
		this.estatElaboracio = estatElaboracio;
		this.tipusDocumental = tipusDocumental;
		this.serieDocumental = serieDocumental;
		this.firmes = firmes;
		this.tipusMime = tipusMime;
		this.identificadorOrigen = identificadorOrigen;
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
	
	public String getVersio() {
		return versio;
	}
	public void setVersio(String versio) {
		this.versio = versio;
	}
	
	public String getGestorVersions() {
		return gestorVersions;
	}
	public void setGestorVersions(String gestorVersions) {
		this.gestorVersions = gestorVersions;
	}
	
	public boolean isEsborrany() {
		return esborrany;
	}
	public void setEsborrany(boolean esborrany) {
		this.esborrany = esborrany;
	}
	
	public String getPare() {
		return pare;
	}
	public void setPare(String pare) {
		this.pare = pare;
	}
	
	public String getMetadadesid() {
		return metadadesid;
	}
	public void setMetadadesid(String metadadesid) {
		this.metadadesid = metadadesid;
	}
	
	public String getVersioNti() {
		return versioNti;
	}
	public void setVersioNti(String versioNti) {
		this.versioNti = versioNti;
	}
	
	public List<String> getOrgans() {
		return organs;
	}
	public void setOrgans(List<String> organs) {
		this.organs = organs;
	}
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
	public ContingutOrigen getOrigen() {
		return origen;
	}
	public void setOrigen(ContingutOrigen origen) {
		this.origen = origen;
	}
	
	public DocumentEstatElaboracio getEstatElaboracio() {
		return estatElaboracio;
	}
	public void setEstatElaboracio(DocumentEstatElaboracio estatElaboracio) {
		this.estatElaboracio = estatElaboracio;
	}
	
	public DocumentTipus getTipusDocumental() {
		return tipusDocumental;
	}
	public void setTipusDocumental(DocumentTipus tipusDocumental) {
		this.tipusDocumental = tipusDocumental;
	}
	
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	
	public List<Firma> getFirmes() {
		return firmes;
	}
	public void setFirmes(List<Firma> firmes) {
		this.firmes = firmes;
	}
	
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	
	public String getIdentificadorOrigen() {
		return identificadorOrigen;
	}
	public void setIdentificadorOrigen(String identificadorOrigen) {
		this.identificadorOrigen = identificadorOrigen;
	}
	
	
}
