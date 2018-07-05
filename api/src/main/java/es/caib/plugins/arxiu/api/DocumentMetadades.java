/**
 * 
 */
package es.caib.plugins.arxiu.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meta-dades associades a un document.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentMetadades {

	private String identificador;
	private String versioNti;
	private ContingutOrigen origen;
	private List<String> organs;
	private Date dataCaptura;
	private DocumentEstatElaboracio estatElaboracio;
	private DocumentTipus tipusDocumental;
	private DocumentFormat format;
	private DocumentExtensio extensio;
	private String identificadorOrigen;
	private Map<String, Object> metadadesAddicionals;

	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
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
	public Date getDataCaptura() {
		return dataCaptura;
	}
	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
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
	public DocumentFormat getFormat() {
		return format;
	}
	public void setFormat(DocumentFormat format) {
		this.format = format;
	}
	public DocumentExtensio getExtensio() {
		return extensio;
	}
	public void setExtensio(DocumentExtensio extensio) {
		this.extensio = extensio;
	}
	public String getIdentificadorOrigen() {
		return identificadorOrigen;
	}
	public void setIdentificadorOrigen(String identificadorOrigen) {
		this.identificadorOrigen = identificadorOrigen;
	}
	public Map<String, Object> getMetadadesAddicionals() {
		return metadadesAddicionals;
	}
	public void setMetadadesAddicionals(Map<String, Object> metadadesAddicionals) {
		this.metadadesAddicionals = metadadesAddicionals;
	}

	public Object getMetadadaAddicional(String clau) {
		if (metadadesAddicionals == null) {
			return null;
		} else {
			return metadadesAddicionals.get(clau);
		}
	}
	public void addMetadadaAddicional(
			String clau,
			Object valor) {
		if (metadadesAddicionals == null) {
			metadadesAddicionals = new HashMap<String, Object>();
		}
		metadadesAddicionals.put(clau, valor);
	}

}
