/**
 * 
 */
package es.caib.plugins.arxiu.api;

import java.util.List;

import org.fundaciobit.plugins.IPlugin;

/**
 * Interfície del plugin per a accedir a la funcionalitat 
 * d'un arxiu digital.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface IArxiuPlugin extends IPlugin {

	public static final String ARXIU_BASE_PROPERTY = "plugin.arxiu.";

	/**
	 * Crea un nou expedient.
	 * 
	 * @param expedient
	 *            Informació per a la creació de l'expedient.
	 * @return La informació sobre l'expedient creat.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu expedientCrear(
			Expedient expedient) throws ArxiuException;

	/**
	 * Modifica un expedient existent.
	 * 
	 * @param expedient
	 *            Informació per a la modificació de l'expedient.
	 * @return La informació sobre l'expedient modificat.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu expedientModificar(
			Expedient expedient) throws ArxiuException;

	/**
	 * Esborra un expedient.
	 * 
	 * @param identificador
	 *            Identificador de l'expedient.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public void expedientEsborrar(
			String identificador) throws ArxiuException;

	/**
	 * Retorna la informació detallada d'un expedient.
	 * 
	 * @param identificador
	 *            Identificador de l'expedient.
	 * @param versio
	 *            Versió de l'expedient (opcional). Si te el valor null
	 *            es consulta la darrera versió disponible.
	 * @return La informació de l'expedient.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public Expedient expedientDetalls(
			String identificador,
			String versio) throws ArxiuException;

	/**
	 * Realitza una consulta d'expedients.
	 * 
	 * @param filtres
	 *            Llista de filtres per aplicar a la consulta.
	 * @param pagina
	 *            Número de la pàgina de resultats que s'ha de retornar.
	 *            Si te el valor null es fa la consulta sense paginació.
	 * @param itemsPerPagina
	 *            Nombre d'elements per pàgina. Si te el valor null es fa
	 *            la consulta sense paginació.
	 * @return El resultat de la consulta.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ConsultaResultat expedientConsulta(
			List<ConsultaFiltre> filtres,
			Integer pagina,
			Integer itemsPerPagina) throws ArxiuException;

	/**
	 * Crea un nou expedient a dins un expedient pare.
	 * 
	 * @param expedient
	 *            Informació per a la creació de l'expedient.
	 * @param identificadorPare
	 *            Identificador de l'expedient pare.
	 * @return La informació sobre l'expedient creat.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu expedientCrearSubExpedient(
			Expedient expedient, 
			String identificadorPare) throws ArxiuException;

	/**
	 * Retorna les versions disponibles d'un expedient.
	 * 
	 * @param identificador
	 *            Identificador de l'expedient.
	 * @return La llista de versions començant per la més antiga.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public List<ContingutArxiu> expedientVersions(
			String identificador) throws ArxiuException;

	/**
	 * Tanca un expedient obert.
	 * 
	 * @param identificador
	 *            Identificador de l'expedient.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public void expedientTancar(
			String identificador) throws ArxiuException;

	/**
	 * Reobre un expedient tancat.
	 * 
	 * @param identificador
	 *            Identificador de l'expedient.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public void expedientReobrir(
			String identificador) throws ArxiuException;

	/**
	 * Exporta l'expedient en format ENI.
	 * 
	 * @param identificador
	 *            Identificador de l'expedient.
	 * @return Un document XML amb la informació de l'expedient.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public String expedientExportarEni(
			String identificador) throws ArxiuException;

	/**
	 * Crea un nou document.
	 * 
	 * @param document
	 *            Informació per a la creació del document.
	 * @param identificadorPare
	 *            Identificador del pare (expedient o carpeta) del document.
	 * @return La informació sobre el document creat.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu documentCrear(
			Document document, 
			String identificadorPare) throws ArxiuException;

	/**
	 * Modifica un document existent.
	 * 
	 * @param document
	 *            Informació per a la modificació del document.
	 * @return La informació sobre el document modificat.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu documentModificar(
			Document document) throws ArxiuException;

	/**
	 * Esborra un document existent.
	 * 
	 * @param identificador
	 *            Identificador del document.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public void documentEsborrar(
			String identificador) throws ArxiuException;

	/**
	 * Retorna la informació detallada d'un document.
	 * 
	 * @param identificador
	 *            Identificador del document.
	 * @param versio
	 *            Versió del document (opcional).
	 * @param ambContingut
	 *            Indica si s'ha de retornar també el contingut del document.
	 * @return La informació del document.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public Document documentDetalls(
			String identificador,
			String versio,
			boolean ambContingut) throws ArxiuException;

	/**
	 * Realitza una consulta de documents.
	 * 
	 * @param filtres
	 *            Llista de filtres per aplicar a la consulta.
	 * @param pagina
	 *            Número de la pàgina de resultats que s'ha de retornar.
	 * @param itemsPerPagina
	 *            Nombre d'elements per pàgina.
	 * @param repositori
	 *            Repositori a on consultar els documents. Si no s'especifica
	 *            (valor null) s'agafarà com a repositori eni:documento. 
	 * @return El resultat de la consulta.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ConsultaResultat documentConsulta(
			List<ConsultaFiltre> filtres,
			Integer pagina,
			Integer itemsPerPagina,
			final DocumentRepositori repositori) throws ArxiuException;

	/**
	 * Retorna les versions disponibles d'un document.
	 * 
	 * @param identificador
	 *            Identificador del document.
	 * @return La llista de versions començant per la més antiga.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public List<ContingutArxiu> documentVersions(
			String identificador) throws ArxiuException;

	/**
	 * Copia un document a una altra carpeta o expedient.
	 * 
	 * @param identificador
	 *            Identificador del document que es vol copiar.
	 * @param identificadorDesti
	 *            Identificador de la carpeta o expedient destí.
	 * @return La informació sobre el document creat.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu documentCopiar(
			String identificador,
			String identificadorDesti) throws ArxiuException;

	/**
	 * Mou un document a una altra carpeta o expedient.
	 * 
	 * @param identificador
	 *            Identificador del document que es vol moure.
	 * @param identificadorDesti
	 *            Identificador de la carpeta o expedient destí.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public void documentMoure(
			String identificador,
			String identificadorDesti) throws ArxiuException;

	/**
	 * Exporta el document en format ENI.
	 * 
	 * @param identificador
	 *            Identificador del document.
	 * @return Un document XML amb la informació del document.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public String documentExportarEni(
			String identificador) throws ArxiuException;

	/**
	 * Genera la versió imprimible del document.
	 * 
	 * @param identificador
	 *            Identificador del document.
	 * @return La informació de l'arxiu imprimible en format PDF.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public DocumentContingut documentImprimible(
			String identificador) throws ArxiuException;

	/**
	 * Crea una nova carpeta.
	 * 
	 * @param carpeta
	 *            Informació per a la creació de la carpeta.
	 * @param identificadorPare
	 *            Identificador del contingut pare de la carpeta.
	 * @return La informació sobre la carpeta creada.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu carpetaCrear(
			Carpeta carpeta,
			String identificadorPare) throws ArxiuException;

	/**
	 * Modifica una carpeta existent.
	 * 
	 * @param carpeta
	 *            Informació per a la modificació de la carpeta.
	 * @return La informació sobre la carpeta modificada.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu carpetaModificar(
			Carpeta carpeta) throws ArxiuException;

	/**
	 * Esborra una carpeta existent.
	 * 
	 * @param identificador
	 *            Identificador de la carpeta.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public void carpetaEsborrar(
			String identificador) throws ArxiuException;

	/**
	 * Retorna la informació detallada d'una carpeta.
	 * 
	 * @param identificador
	 *            Identificador de la carpeta.
	 * @return La informació de la carpeta.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public Carpeta carpetaDetalls(
			String identificador) throws ArxiuException;

	/**
	 * Copia una carpeta a una altra carpeta o expedient.
	 * 
	 * @param identificador
	 *            Identificador de la carpeta que es vol copiar.
	 * @param identificadorDesti
	 *            Identificador de la carpeta o expedient destí.
	 * @return La informació sobre la carpeta creada.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public ContingutArxiu carpetaCopiar(
			String identificador,
			String identificadorDesti) throws ArxiuException;

	/**
	 * Mou una carpeta a una altra carpeta o expedient.
	 * 
	 * @param identificador
	 *            Identificador de la carpeta que es vol moure.
	 * @param identificadorDesti
	 *            Identificador de la carpeta o expedient destí.
	 * @throws ArxiuException
	 *             Si es produeix algun problema al realitzar l’operació amb l’arxiu.
	 */
	public void carpetaMoure(
			String identificador,
			String identificadorDesti) throws ArxiuException;

	/**
	 * Indica si la implementació del plugin suporta el versionat
	 * de expedients.
	 * 
	 * @return true si ho suporta o false en cas contrari.
	 */
	public boolean suportaVersionatExpedient();

	/**
	 * Indica si la implementació del plugin suporta el versionat
	 * de documents.
	 * 
	 * @return true si ho suporta o false en cas contrari.
	 */
	public boolean suportaVersionatDocument();

	/**
	 * Indica si la implementació del plugin suporta el versionat
	 * de carpetes.
	 * 
	 * @return true si ho suporta o false en cas contrari.
	 */
	public boolean suportaVersionatCarpeta();

	/**
	 * Indica si la implementació del plugin suporta le gestió
	 * de les metadades NTI.
	 * 
	 * @return true si ho suporta o false en cas contrari.
	 */
	public boolean suportaMetadadesNti();

	/**
	 * Indica si la implementació del plugin genera automàticament
	 * l'identificador NTI dels expedients i documents.
	 * 
	 * @return true si el genera o false en cas contrari.
	 */
	public boolean generaIdentificadorNti();

}
