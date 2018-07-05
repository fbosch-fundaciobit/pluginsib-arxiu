/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Excepció que es produeix quan no es troba el contingut que
 * es vol consultar.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuNotFoundException extends ArxiuException {

	private String tipus;
	private String identificador;

	public ArxiuNotFoundException(
			String tipus,
			String identificador) {
		super("Contingut no trobat: " + generarMessage(tipus, identificador));
		this.tipus = tipus;
		this.identificador = identificador;
	}
	public ArxiuNotFoundException(
			String identificador) {
		super("Contingut no trobat: " + generarMessage(null, identificador));
		this.identificador = identificador;
	}
	public ArxiuNotFoundException() {
		super("Contingut no trobat");
	}

	public String getTipus() {
		return tipus;
	}
	public String getIdentificador() {
		return identificador;
	}

	private static String generarMessage(
			String tipus,
			String identificador) {
		if (tipus == null) {
			return identificador;
		} else {
			return "[" + tipus + "] " + identificador;
		}
	}

	private static final long serialVersionUID = 6220288486244096013L;

}
