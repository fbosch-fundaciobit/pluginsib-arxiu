/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Excepció que es produeix al processar les peticions a l'arxiu.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuException extends RuntimeException {

	public ArxiuException() {
		super();
	}
	public ArxiuException(String message, Throwable cause) {
		super(message, cause);
	}
	public ArxiuException(String message) {
		super(message);
	}
	public ArxiuException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 6220288486244096013L;

}
