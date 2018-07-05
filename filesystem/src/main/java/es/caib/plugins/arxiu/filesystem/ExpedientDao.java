package es.caib.plugins.arxiu.filesystem;

import java.util.Date;
import java.util.List;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;

/**
 * 
 * @author Limit
 *
 */
public class ExpedientDao {

	private String identificador;
	private String nom;
	
	private String versio;
	private String gestorVersions;
	private boolean obert;
	
	private String idMetadades;
	private String versioNti;
	private ContingutOrigen origen;
	private List<String> organs;
	private Date dataObertura;
	private String classificacio;
	private ExpedientEstat estat;
	private List<String> interessats;
	private String serieDocumental;	

	private List<ContingutArxiu> continguts;

	public Expedient getExpedient() {
		Expedient expedient = new Expedient();
		expedient.setIdentificador(identificador);
		expedient.setNom(nom);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setIdentificador(idMetadades);
		metadades.setVersioNti(versioNti);
		metadades.setOrgans(organs);
		metadades.setDataObertura(dataObertura);
		metadades.setClassificacio(classificacio);
		metadades.setEstat(estat);
		metadades.setInteressats(interessats);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setContinguts(continguts);
		return expedient;
	}

	public ExpedientDao(
			String identificador,
			String nom,
			String versio,
			String gestorVersions,
			boolean obert,
			String idMetadades,
			String versioNti,
			List<String> organs,
			Date dataObertura,
			String classificacio,
			ExpedientEstat estat,
			List<String> interessats,
			String serieDocumental,
			List<ContingutArxiu> continguts) {
		super();
		this.identificador = identificador;
		this.nom = nom;
		this.versio = versio;
		this.gestorVersions = gestorVersions;
		this.obert = obert;
		this.idMetadades = idMetadades;
		this.versioNti = versioNti;
		this.organs = organs;
		this.dataObertura = dataObertura;
		this.classificacio = classificacio;
		this.estat = estat;
		this.interessats = interessats;
		this.serieDocumental = serieDocumental;
		this.continguts = continguts;
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
	
	public boolean isObert() {
		return obert;
	}
	public void setObert(boolean obert) {
		this.obert = obert;
	}
	
	public String getIdMetadades() {
		return idMetadades;
	}
	public void setIdMetadades(String idMetadades) {
		this.idMetadades = idMetadades;
	}
	
	public String getVersioNti() {
		return versioNti;
	}
	public void setVersioNti(String versioNti) {
		this.versioNti = versioNti;
	}
	
	public ContingutOrigen getOrigen() {
		return origen;
	}
	public void setOrigen(ContingutOrigen origen) {
		this.origen = origen;
	}
	
	public List<String> getOrgans() {
		return organs;
	}
	public void setOrgans(List<String> organs) {
		this.organs = organs;
	}
	
	public Date getDataObertura() {
		return dataObertura;
	}
	public void setDataObertura(Date dataObertura) {
		this.dataObertura = dataObertura;
	}
	
	public String getClassificacio() {
		return classificacio;
	}
	public void setClassificacio(String classificacio) {
		this.classificacio = classificacio;
	}
	
	public ExpedientEstat getEstat() {
		return estat;
	}
	public void setEstat(ExpedientEstat estat) {
		this.estat = estat;
	}
	
	public List<String> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<String> interessats) {
		this.interessats = interessats;
	}
	
	public String getSerieDocumental() {
		return serieDocumental;
	}
	public void setSerieDocumental(String serieDocumental) {
		this.serieDocumental = serieDocumental;
	}
	
	public List<ContingutArxiu> getContinguts() {
		return continguts;
	}
	public void setContinguts(List<ContingutArxiu> continguts) {
		this.continguts = continguts;
	}
	
	
}
