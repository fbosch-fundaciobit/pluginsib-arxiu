/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Informació sobre la firma d’un expedient o document.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Firma {

	private FirmaTipus tipus;
	private FirmaPerfil perfil;
	private String fitxerNom;
	private byte[] contingut;
	private long tamany = -1;
	private String tipusMime;
	private String csvRegulacio;

	public FirmaTipus getTipus() {
		return tipus;
	}
	public void setTipus(FirmaTipus tipus) {
		this.tipus = tipus;
	}
	public FirmaPerfil getPerfil() {
		return perfil;
	}
	public void setPerfil(FirmaPerfil perfil) {
		this.perfil = perfil;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public long getTamany() {
		return tamany;
	}
	public void setTamany(long tamany) {
		this.tamany = tamany;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public String getCsvRegulacio() {
		return csvRegulacio;
	}
	public void setCsvRegulacio(String csvRegulacio) {
		this.csvRegulacio = csvRegulacio;
	}

}
