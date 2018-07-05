/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles perfils de firma.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum FirmaPerfil {

	BES("BES"),
	EPES("EPES"),
	LTV("LTV"),
	T("T"),
	C("C"),
	X("X"),
	XL("XL"),
	A("A");

	private String str;

	FirmaPerfil(String str) {
		this.str = str;
    }

	public static FirmaPerfil toEnum(String str) {
		if (str != null) {
		    for (FirmaPerfil valor: FirmaPerfil.values()) {
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
