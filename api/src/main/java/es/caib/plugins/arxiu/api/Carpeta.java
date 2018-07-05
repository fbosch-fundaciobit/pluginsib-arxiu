/**
 * 
 */
package es.caib.plugins.arxiu.api;

import java.util.List;

/**
 * Informació d'un contingut de l'arxiu de tipus carpeta.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Carpeta extends ContingutArxiu {

	private List<ContingutArxiu> continguts;

	public Carpeta() {
		super(ContingutTipus.CARPETA);
	}

	public List<ContingutArxiu> getContinguts() {
		return continguts;
	}
	public void setContinguts(List<ContingutArxiu> continguts) {
		this.continguts = continguts;
	}

}
