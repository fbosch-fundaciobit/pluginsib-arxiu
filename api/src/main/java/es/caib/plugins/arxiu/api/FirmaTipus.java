/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles tipus de firma.
 * 
 * CSV: Código seguro de verificación
 * XADES_DET: XAdES internally detached signature
 * XADES_ENV: XAdES enveloped signature
 * CADES_DET: CAdES detached/explicit signature
 * CADES_ATT: CAdES attached/implicit signature
 * PADES: PAdES
 * SMIME
 * ODT
 * OOXML
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum FirmaTipus {

	CSV("TF01"),
	XADES_DET("TF02"),
	XADES_ENV("TF03"),
	CADES_DET("TF04"),
	CADES_ATT("TF05"),
	PADES("TF06"),
	SMIME("TF07"),
	ODT("TF08"),
	OOXML("TF09");

	private String str;

	FirmaTipus(String str) {
		this.str = str;
    }

	public static FirmaTipus toEnum(String str) {
		if (str != null) {
		    for (FirmaTipus valor: FirmaTipus.values()) {
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
