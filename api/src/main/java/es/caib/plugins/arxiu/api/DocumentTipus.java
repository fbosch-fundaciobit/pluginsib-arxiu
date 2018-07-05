/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles estats d'un document.
 * 
 * RESOLUCIO: Resolución
 * ACORD: Acuerdo
 * CONTRACTE: Contrato
 * CONVENI: Convenio
 * DECLARACIO: Declaración
 * COMUNICACIO: Comunicación
 * NOTIFICACIO: Notificación
 * PUBLICACIO: Publicación
 * JUSTIFICANT_RECEPCIO: Acuse de recibo
 * ACTA: Acta
 * CERTIFICAT: Certificat 
 * DILIGENCIA: Diligència
 * INFORME: Informe
 * SOLICITUD: Solicitud
 * DENUNCIA: Denuncia
 * ALEGACIO: Alegación
 * RECURS: Recurso
 * COMUNICACIO_CIUTADA: Comunicación ciudadano 
 * FACTURA: Factura
 * ALTRES_INCAUTATS: Otros incautados 
 * ALTRES: Otros
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentTipus {

	RESOLUCIO("TD01"),
	ACORD("TD02"),
	CONTRACTE("TD03"),
	CONVENI("TD04"),
	DECLARACIO("TD05"),
	COMUNICACIO("TD06"),
	NOTIFICACIO("TD07"),
	PUBLICACIO("TD08"),
	JUSTIFICANT_RECEPCIO("TD09"),
	ACTA("TD10"),
	CERTIFICAT("TD11"),
	DILIGENCIA("TD12"),
	INFORME("TD13"),
	SOLICITUD("TD14"),
	DENUNCIA("TD15"),
	ALEGACIO("TD16"),
	RECURS("TD17"),
	COMUNICACIO_CIUTADA("TD18"),
	FACTURA("TD19"),
	ALTRES_INCAUTATS("TD20"),
	ALTRES("TD99");

	private String str;

	DocumentTipus(String str) {
		this.str = str;
    }

	public static DocumentTipus toEnum(String str) {
		if (str != null) {
		    for (DocumentTipus valor: DocumentTipus.values()) {
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
