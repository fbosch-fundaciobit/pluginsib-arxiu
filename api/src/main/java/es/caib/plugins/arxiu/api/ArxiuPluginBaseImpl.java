/**
 * 
 */
package es.caib.plugins.arxiu.api;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.fundaciobit.plugins.utils.AbstractPluginProperties;

/**
 * Classe abstracta del plugin d'arxiu que incorpora funcionalitat
 * per a llegir els paràmetres d'un fitxer de properties.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ArxiuPluginBaseImpl extends AbstractPluginProperties implements IArxiuPlugin {

	protected final Logger log = Logger.getLogger(getClass());

	public ArxiuPluginBaseImpl() {
		super();
	}
	public ArxiuPluginBaseImpl(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	public ArxiuPluginBaseImpl(String propertyKeyBase) {
		super(propertyKeyBase);
	}

	protected abstract String[] getSupportedSignatureTypes();
	
	protected abstract String getPropertyBase();

}
