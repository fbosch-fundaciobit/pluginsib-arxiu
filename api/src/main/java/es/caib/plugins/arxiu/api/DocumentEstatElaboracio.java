/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles estats d'elaboració d'un document.
 * 
 * ORIGINAL: Original (Llei 11/2007 Art. 30)
 * COPIA_CF: Còpia electrònica autèntica amb canvi de format (Llei 
 * 11/2007 Art. 30.1).
 * COPIA_DP: Còpia electrònica autèntica de document en paper 
 * amb canvi de format (Llei 11/2007 Art. 30.2 i 30.3).
 * COPIA_PR: Còpia electrònica parcial autèntica.
 * ALTRES: Altres estats d'elaboració.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentEstatElaboracio {

	ORIGINAL("EE01"),
	COPIA_CF("EE02"),
	COPIA_DP("EE03"),
	COPIA_PR("EE04"),
	ALTRES("EE99");

	private String str;

	DocumentEstatElaboracio(String str) {
		this.str = str;
    }

	public static DocumentEstatElaboracio toEnum(String str) {
		if (str != null) {
		    for (DocumentEstatElaboracio valor: DocumentEstatElaboracio.values()) {
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
