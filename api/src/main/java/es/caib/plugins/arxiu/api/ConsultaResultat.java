/**
 * 
 */
package es.caib.plugins.arxiu.api;

import java.util.List;

/**
 * Informació sobre el resultat d'una consulta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaResultat {

	private Integer numRegistres;				// Paginació: Número de registres a mostrar per pàgina
	private Integer numPagines;					// Paginació: Número de pagines que retorna la consulta
	private Integer numRetornat;				// Número de resultats que retorna la consulta
	private Integer paginaActual;				// Paginació: Número de la pàgina actual
	private List<ContingutArxiu> resultats;		// Resultat de la consulta 
	
	public ConsultaResultat() {
		super();
	}

	public ConsultaResultat(
			Integer numRetornat, 
			List<ContingutArxiu> resultats) {
		super();
		this.numRetornat = numRetornat;
		this.resultats = resultats;
	}
	
	public ConsultaResultat(
			Integer numRegistres, 
			Integer numPagines, 
			Integer numRetornat, 
			Integer paginaActual,
			List<ContingutArxiu> resultats) {
		super();
		this.numRegistres = numRegistres;
		this.numPagines = numPagines;
		this.numRetornat = numRetornat;
		this.paginaActual = paginaActual;
		this.resultats = resultats;
	}

	public Integer getNumRegistres() {
		return numRegistres;
	}

	public void setNumRegistres(Integer numRegistres) {
		this.numRegistres = numRegistres;
	}

	public Integer getNumPagines() {
		return numPagines;
	}

	public void setNumPagines(Integer numPagines) {
		this.numPagines = numPagines;
	}

	public Integer getNumRetornat() {
		return numRetornat;
	}

	public void setNumRetornat(Integer numRetornat) {
		this.numRetornat = numRetornat;
	}

	public Integer getPaginaActual() {
		return paginaActual;
	}

	public void setPaginaActual(Integer paginaActual) {
		this.paginaActual = paginaActual;
	}

	public List<ContingutArxiu> getResultats() {
		return resultats;
	}

	public void setResultats(List<ContingutArxiu> resultats) {
		this.resultats = resultats;
	}
	
}
