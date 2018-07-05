/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles estats durant el cicle de vida del document.
 * 
 * ESBORRANY: Document en elaboració i que es pot modificar.
 * DEFINITIU: Document definitiu que no admet modificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentEstat {
	ESBORRANY,
	DEFINITIU
}
