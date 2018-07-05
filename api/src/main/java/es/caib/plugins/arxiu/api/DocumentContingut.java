/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Informació sobre un contingut d’un document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentContingut {

	private byte[] contingut;
	private long tamany = -1;
	private String tipusMime;
	private String arxiuNom;

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
	public String getArxiuNom() {
		return arxiuNom;
	}
	public void setArxiuNom(String arxiuNom) {
		this.arxiuNom = arxiuNom;
	}

}
