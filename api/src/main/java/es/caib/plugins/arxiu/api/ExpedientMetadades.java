/**
 * 
 */
package es.caib.plugins.arxiu.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meta-dades associades a un expedient.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientMetadades {

	private String identificador;
	private String versioNti;
	private List<String> organs;
	private Date dataObertura;
	private String classificacio;
	private ExpedientEstat estat;
	private List<String> interessats;
	private String serieDocumental;
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
