/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles estats d'un expedient.
 * 
 * OBERT: Abierto.
 * TANCAT: Cerrado.
 * INDEX_REMISSIO: Índice para remisión cerrado
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ExpedientEstat {

	OBERT("E01"),
	TANCAT("E02"),
	INDEX_REMISSIO("E03");

	private String str;

	ExpedientEstat(String str) {
		this.str = str;
    }

	public static ExpedientEstat toEnum(String str) {
		if (str != null) {
		    for (ExpedientEstat valor: ExpedientEstat.values()) {
		        if (valor.toString().equals(str)) {
		            return valor;
		        }
		    }
		}
	    return null;
	}

	@Override
	public String toString() {
		return str;
	}

}
