package es.caib.plugins.arxiu.filesystem;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.fundaciobit.plugins.utils.AbstractPluginProperties;

import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ConsultaResultat;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentRepositori;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

public class ArxiuPluginFilesystem extends AbstractPluginProperties implements IArxiuPlugin {

	private static final String ARXIUFILESYSTEM_BASE_PROPERTY = ARXIU_BASE_PROPERTY + "filesystem.";

	private LuceneHelper luceneHelper;
	private FileSystemHelper filesystemHelper;

	public ArxiuPluginFilesystem() {
		super();
	}
	public ArxiuPluginFilesystem(Properties properties) {
		super("", properties);
	}
	public ArxiuPluginFilesystem(String propertyKeyBase, Properties properties) {
		super(propertyKeyBase, properties);
	}
	public ArxiuPluginFilesystem(String propertyKeyBase) {
		super(propertyKeyBase);
	}

	@Override
	public ContingutArxiu expedientCrear(
			Expedient expedient) throws ArxiuException {
		try {
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					true);
			String path = getFilesystemHelper().expedientCrear(
					expedient,
					identificadorEni);
			getLuceneHelper().expedientCrear(
					expedient,
					uuid,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu expedientModificar(
			Expedient expedient) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							expedient.getIdentificador()));
			getLuceneHelper().expedientModificar(
					expedient);
			return crearContingutArxiu(
					expedient.getIdentificador(), 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error modificant l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public void expedientEsborrar(
			String identificador) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificador));
			comprovarExpedientNoConteDocumentsDefinitius(
					identificador);
			String path = getLuceneHelper().getPath(
					identificador);
			getFilesystemHelper().directoriEsborrar(path);
			getLuceneHelper().contingutEsborrar(
					identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error esborrant l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public Expedient expedientDetalls(
			String identificador,
			String versio) throws ArxiuException {
		try {
			if (versio != null) {
				throw new ArxiuException(
						"Aquesta implementació de l'API d'arxiu no suporta el versionat d'expedients");
			}
			return getLuceneHelper().expedientDetalls(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al obtenir els detalls de l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ConsultaResultat expedientConsulta(
			List<ConsultaFiltre> filtres,
			Integer pagina,
			Integer itemsPerPagina) throws ArxiuException {
		try {
			return getLuceneHelper().contingutCercar(
					ContingutTipus.EXPEDIENT,
					filtres,
					pagina,
					itemsPerPagina);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error en la consulta d'expedients: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu expedientCrearSubExpedient(
			final Expedient expedient, 
			final String identificadorPare) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificadorPare));
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					true);
			String parePath = getLuceneHelper().getPath(
					identificadorPare);
			String path = getFilesystemHelper().subExpedientCrear(
					parePath,
					identificadorEni);
			getLuceneHelper().expedientCrear(
					expedient,
					uuid,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					expedient.getNom(),
					ContingutTipus.EXPEDIENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant el subexpedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public List<ContingutArxiu> expedientVersions(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"Aquesta implementació de l'API d'arxiu no suporta el versionat d'expedients");
	}

	@Override
	public void expedientTancar(
			String identificador) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificador));
			getLuceneHelper().expedientCanviEstat(
					identificador,
					ExpedientEstat.TANCAT);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error tancant l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public void expedientReobrir(
			String identificador) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.TANCAT,
					getLuceneHelper().getExpedientPareEstat(
							identificador));
			getLuceneHelper().expedientCanviEstat(
					identificador,
					ExpedientEstat.OBERT);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error reobrint l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public String expedientExportarEni(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"El mètode expedientExportarEni no està disponible");
	}

	@Override
	public ContingutArxiu documentCrear(
			Document document,
			String identificadorPare) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificadorPare));
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					false);
			String parePath = getLuceneHelper().getPath(identificadorPare);
			String path = getFilesystemHelper().documentActualitzar(
					parePath,
					document,
					identificadorEni);
			getLuceneHelper().documentCrear(
					document,
					uuid,
					identificadorPare,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant el document: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu documentModificar(
			Document document) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							document.getIdentificador()));
			comprovarDocumentEstat(
					DocumentEstat.ESBORRANY,
					getLuceneHelper().getDocumentEstat(
							document.getIdentificador()));
			comprovarDocumentDefinitiuAmbFirmes(document);
			String parePath = getLuceneHelper().getParePath(
					ContingutTipus.DOCUMENT,
					document.getIdentificador());
			String identificadorEni = getLuceneHelper().getIdentificadorEni(
					document.getIdentificador());
			getFilesystemHelper().documentActualitzar(
					parePath,
					document,
					identificadorEni);
			getLuceneHelper().documentModificar(
					document);
			return crearContingutArxiu(
					document.getIdentificador(), 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error modificant el document: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public void documentEsborrar(
			String identificador) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificador));
			comprovarDocumentEstat(
					DocumentEstat.ESBORRANY,
					getLuceneHelper().getDocumentEstat(
							identificador));
			String path = getLuceneHelper().getPath(identificador);
			getFilesystemHelper().directoriEsborrar(path);
			getLuceneHelper().contingutEsborrar(
					identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error esborrant l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public Document documentDetalls(
			String identificador,
			String versio,
			boolean ambContingut) throws ArxiuException {
		try {
			if (versio != null) {
				throw new ArxiuException(
						"Aquesta implementació de l'API d'arxiu no suporta el versionat de documents");
			}
			Document document = getLuceneHelper().documentDetalls(identificador);
			if (ambContingut && document.getContingut() != null) {
				String path = getLuceneHelper().getPath(identificador);
				document.getContingut().setContingut(
						getFilesystemHelper().documentContingut(path));
				document.getContingut().setTamany(
						document.getContingut().getContingut().length);
			}
			if (document.getFirmes() != null) {
				String path = getLuceneHelper().getPath(identificador);
				for (int i = 0; i < document.getFirmes().size(); i++) {
					Firma firma = document.getFirmes().get(i);
					firma.setContingut(
							getFilesystemHelper().documentFirma(
									path,
									i));
					firma.setTamany(firma.getContingut().length);
				}
			}
			return document;
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al obtenir els detalls del document: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ConsultaResultat documentConsulta(
			List<ConsultaFiltre> filtres,
			Integer pagina,
			Integer itemsPerPagina,
			final DocumentRepositori repositori) throws ArxiuException {
		try {
			return getLuceneHelper().contingutCercar(
					ContingutTipus.DOCUMENT,
					filtres,
					pagina,
					itemsPerPagina);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error en la consulta d'expedients: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public List<ContingutArxiu> documentVersions(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"Aquesta implementació de l'API d'arxiu no suporta el versionat de documents");
	}

	@Override
	public ContingutArxiu documentCopiar(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		try {
			Document document = getLuceneHelper().documentDetalls(identificador);
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificadorDesti));
			String uuid = UUID.randomUUID().toString();
			String identificadorEni = generarIdentificadorEni(
					uuid,
					false);
			String parePath = getLuceneHelper().getPath(identificadorDesti);
			String path = getFilesystemHelper().documentActualitzar(
					parePath,
					document,
					identificadorEni);
			getLuceneHelper().documentCrear(
					document,
					uuid,
					identificadorDesti,
					identificadorEni,
					path);
			return crearContingutArxiu(
					uuid, 
					document.getNom(),
					ContingutTipus.DOCUMENT,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al copiar el document: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public void documentMoure(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		try {
			String origenPath = getLuceneHelper().getPath(identificador);
			String destiPath = getLuceneHelper().getPath(identificadorDesti);
			getLuceneHelper().contingutMoure(
					ContingutTipus.DOCUMENT,
					identificador,
					identificadorDesti,
					destiPath);
			getFilesystemHelper().directoriMoure(
					origenPath,
					destiPath);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al moure el document: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public String documentExportarEni(
			String identificador) throws ArxiuException {
		throw new ArxiuException(
				"El mètode documentExportarEni no està disponible");
	}

	@Override
	public DocumentContingut documentImprimible(String identificador) throws ArxiuException {
		throw new ArxiuException(
				"El mètode documentImprimible no està disponible");
	}

	@Override
	public ContingutArxiu carpetaCrear(
			Carpeta carpeta,
			String identificadorPare) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificadorPare));
			String uuid = UUID.randomUUID().toString();
			String parePath = getLuceneHelper().getPath(identificadorPare);
			String path = getFilesystemHelper().carpetaCrear(
					parePath,
					uuid);
			getLuceneHelper().carpetaCrear(
					carpeta,
					uuid,
					identificadorPare,
					path);
			return crearContingutArxiu(
					uuid, 
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error creant la carpeta: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu carpetaModificar(
			Carpeta carpeta) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							carpeta.getIdentificador()));
			getLuceneHelper().carpetaModificar(
					carpeta);
			return crearContingutArxiu(
					carpeta.getIdentificador(), 
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error modificant la carpeta: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public void carpetaEsborrar(
			String identificador) throws ArxiuException {
		try {
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificador));
			String path = getLuceneHelper().getPath(identificador);
			getFilesystemHelper().directoriEsborrar(path);
			getLuceneHelper().contingutEsborrar(
					identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error esborrant la carpeta: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public Carpeta carpetaDetalls(
			String identificador) throws ArxiuException {
		try {
			return getLuceneHelper().carpetaDetalls(identificador);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al obtenir els detalls de l'expedient: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public ContingutArxiu carpetaCopiar(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		try {
			Carpeta carpeta = getLuceneHelper().carpetaDetalls(identificador);
			comprovarExpedientEstat(
					ExpedientEstat.OBERT,
					getLuceneHelper().getExpedientPareEstat(
							identificadorDesti));
			String uuid = UUID.randomUUID().toString();
			String parePath = getLuceneHelper().getPath(identificadorDesti);
			String path = getFilesystemHelper().carpetaCrear(
					parePath,
					uuid);
			getLuceneHelper().carpetaCrear(
					carpeta,
					uuid,
					identificadorDesti,
					path);
			return crearContingutArxiu(
					uuid, 
					carpeta.getNom(),
					ContingutTipus.CARPETA,
					null);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al copiar el document: " + ex.getMessage(),
					ex);
		}
	}

	@Override
	public void carpetaMoure(
			String identificador,
			String identificadorDesti) throws ArxiuException {
		try {
			String origenPath = getLuceneHelper().getPath(identificador);
			String destiPath = getLuceneHelper().getPath(identificadorDesti);
			getLuceneHelper().contingutMoure(
					ContingutTipus.CARPETA,
					identificador,
					identificadorDesti,
					destiPath);
			getFilesystemHelper().directoriMoure(
					origenPath,
					destiPath);
		} catch (ArxiuException aex) {
			throw aex;
		} catch (Exception ex) {
			throw new ArxiuException(
					"Error al moure el document: " + ex.getMessage(),
					ex);
		}
	}
	
	@Override
	public boolean suportaVersionatExpedient() {
		return false;
	}

	@Override
	public boolean suportaVersionatDocument() {
		return false;
	}

	@Override
	public boolean suportaVersionatCarpeta() {
		return false;
	}

	@Override
	public boolean suportaMetadadesNti() {
		return true;
	}

	@Override
	public boolean generaIdentificadorNti() {
		return true;
	}



	private LuceneHelper getLuceneHelper() {
		if (luceneHelper == null) {
			luceneHelper = new LuceneHelper(getPropertyBasePath());
		}
		return luceneHelper;
	}
	private FileSystemHelper getFilesystemHelper() {
		if (filesystemHelper == null) {
			filesystemHelper = new FileSystemHelper(getPropertyBasePath());
		}
		return filesystemHelper;
	}

	private void comprovarExpedientEstat(
			ExpedientEstat estatEsperat,
			ExpedientEstat estatPerComprovar) {
		if (!estatPerComprovar.equals(estatEsperat)) {
			throw new ArxiuException(
					"L'expedient no està en estat " + estatEsperat + " (estat=" + estatPerComprovar + ")");
		}
	}

	private void comprovarDocumentEstat(
			DocumentEstat estatEsperat,
			DocumentEstat estatPerComprovar) {
		if (!estatPerComprovar.equals(estatEsperat)) {
			throw new ArxiuException(
					"El document no està en estat " + estatEsperat + " (estat=" + estatPerComprovar + ")");
		}
	}

	private void comprovarDocumentDefinitiuAmbFirmes(
			Document document) throws IOException, ParseException {
		boolean esDefinitiu = DocumentEstat.DEFINITIU.equals(document.getEstat());
		boolean conteFirmes = document.getFirmes() != null;
		if (esDefinitiu && !conteFirmes && !getLuceneHelper().isDocumentConteFirmes(document.getIdentificador())) {
			throw new ArxiuException(
					"No es pot marcar com a definitiu un document sense firmes");
		}
	}

	private void comprovarExpedientNoConteDocumentsDefinitius(
			String uuid) throws IOException {
		if (getLuceneHelper().isExpedientConteDocumentsDefinitius(uuid)) {
			throw new ArxiuException(
					"L'expedient no es pot esborrar si conté documents definitius");
		}
	}

	private ContingutArxiu crearContingutArxiu(
			String identificador, 
			String nom,
			ContingutTipus tipus,
			String versio) {
		ContingutArxiu informacioItem = new ContingutArxiu(tipus);
		informacioItem.setIdentificador(identificador);
		informacioItem.setNom(nom);
		return informacioItem;
	}

	private String generarIdentificadorEni(
			String uuid,
			boolean esExpedient) throws DecoderException {
		String uuidHex = uuid.replaceAll("-", "");
		byte[] rnd = new byte[5];
		new Random().nextBytes(rnd);
		uuidHex += new String(Hex.encodeHex(rnd));
		byte[] bytes = Hex.decodeHex(uuidHex.toCharArray());
		String uuidBase64 = "FS" + new String(Base64.encodeBase64(bytes));
		int anyActual = Calendar.getInstance().get(Calendar.YEAR);
		String exp = (esExpedient) ? "EXP_" : "";
		return "ES_" + getPropertyOrganCodiDir3() + "_" + anyActual + "_" + exp + uuidBase64.replace("/", "-");
	}

	private String getPropertyBasePath() {
		return getProperty(ARXIUFILESYSTEM_BASE_PROPERTY + "base.path");
	}

	private String getPropertyOrganCodiDir3() {
		return getProperty(ARXIUFILESYSTEM_BASE_PROPERTY + "organ.codi.dir3");
	}

}
