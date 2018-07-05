/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles orígens d'un document.
 * 
 * CIUTADA: Ciudadano.
 * ADMINISTRACIO: Administración.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ContingutOrigen {

	CIUTADA("0"),
	ADMINISTRACIO("1");

	private String str;

	ContingutOrigen(String str) {
		this.str = str;
    }

	public static ContingutOrigen toEnum(String str) {
		if (str != null) {
		    for (ContingutOrigen valor: ContingutOrigen.values()) {
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
