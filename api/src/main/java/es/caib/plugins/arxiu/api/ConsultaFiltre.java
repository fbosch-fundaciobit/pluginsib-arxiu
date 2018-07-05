/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Informació sobre el filtre per a realitzar consultes.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaFiltre {

	private String metadada;
	private ConsultaOperacio operacio;
	private String valorOperacio1;
	private String valorOperacio2;

	public String getMetadada() {
		return metadada;
	}
	public void setMetadada(String metadada) {
		this.metadada = metadada;
	}
	public ConsultaOperacio getOperacio() {
		return operacio;
	}
	public void setOperacio(ConsultaOperacio operacio) {
		this.operacio = operacio;
	}
	public String getValorOperacio1() {
		return valorOperacio1;
	}
	public void setValorOperacio1(String valorOperacio1) {
		this.valorOperacio1 = valorOperacio1;
	}
	public String getValorOperacio2() {
		return valorOperacio2;
	}
	public void setValorOperacio2(String valorOperacio2) {
		this.valorOperacio2 = valorOperacio2;
	}

}
