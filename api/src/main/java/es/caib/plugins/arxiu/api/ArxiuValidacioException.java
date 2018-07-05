/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Excepció que es produeix al validar les peticions que es fan
 * a l'arxiu.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuValidacioException extends ArxiuException {

	private String codi;
	private String error;

	public ArxiuValidacioException(
			String codi,
			String error) {
		super("Error de validació: " + generarMessage(codi, error));
		this.codi = codi;
		this.error = error;
	}
	public ArxiuValidacioException(
			String error) {
		super("Error de validació: " + generarMessage(null, error));
		this.error = error;
	}

	public String getCodi() {
		return codi;
	}
	public String getError() {
		return error;
	}

	private static String generarMessage(
			String codi,
			String error) {
		if (codi == null) {
			return error;
		} else {
			return "[" + codi + "] " + error;
		}
	}

	private static final long serialVersionUID = 6220288486244096013L;

}
