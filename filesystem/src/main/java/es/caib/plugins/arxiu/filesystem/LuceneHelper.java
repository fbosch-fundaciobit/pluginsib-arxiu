/**
 * 
 */
package es.caib.plugins.arxiu.filesystem;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ArxiuNotFoundException;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ConsultaResultat;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;

/**
 * Mètodes per a gestionar la indexació de continguts amb
 * Apache Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LuceneHelper {

	private static final String ENI_EXP_VERSIO = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/expediente-e";
	private static final String ENI_DOC_VERSIO = "http://administracionelectronica.gob.es/ENI/XSD/v1.0/documento-e";

	private static final String LUCENE_FOLDER_NAME = "_lucene";

	private static final String LUCENE_FIELD_UUID = "uuid";
	private static final String LUCENE_FIELD_PATH = "path";
	private static final String LUCENE_FIELD_TIPUS = "tipus";
	private static final String LUCENE_FIELD_NOM = "nom";
	private static final String LUCENE_FIELD_PARE_UUID = "pare_uuid";
	private static final String LUCENE_FIELD_EXP_UUID = "exp_uuid";
	private static final String LUCENE_FIELD_CONT_ARXIU = "cont_arxiu";
	private static final String LUCENE_FIELD_CONT_ARXIUNOM = "cont_arxiunom";
	private static final String LUCENE_FIELD_CONT_MIME = "cont_mime";
	private static final String LUCENE_FIELD_META_ID = "meta_id";
	private static final String LUCENE_FIELD_META_VERSIO = "meta_versio";
	private static final String LUCENE_FIELD_META_ORGAN = "meta_organ";
	private static final String LUCENE_FIELD_META_DATA = "meta_data";
	private static final String LUCENE_FIELD_META_CLASSIFICACIO = "meta_classificacio";
	private static final String LUCENE_FIELD_META_ESTAT = "meta_estat";
	private static final String LUCENE_FIELD_META_INTERESSAT = "meta_interessat";
	private static final String LUCENE_FIELD_META_ORIGEN = "meta_origen";
	private static final String LUCENE_FIELD_META_ESTAT_ELAB = "meta_estat_elab";
	private static final String LUCENE_FIELD_META_FORMAT = "meta_format";
	private static final String LUCENE_FIELD_META_EXTENSIO = "meta_extensio";
	private static final String LUCENE_FIELD_META_DOC_TIPUS = "meta_doc_tipus";
	private static final String LUCENE_FIELD_META_ORIG_ID = "meta_orig_id";
	private static final String LUCENE_FIELD_META_SERIE_DOC = "meta_serie_doc";
	private static final String LUCENE_FIELD_FIRMA_TIPUS = "firma_tipus";
	private static final String LUCENE_FIELD_FIRMA_CSV = "firma_csv";
	private static final String LUCENE_FIELD_FIRMA_DEFCSV = "firma_defcsv";
	private static final String LUCENE_FIELD_FIRMA_PERFIL = "firma_perfil";
	private static final String LUCENE_FIELD_FIRMA_MIME = "firma_mime";

	//private FSDirectory luceneDirectory;
	private Analyzer luceneAnalyzer;
	private IndexWriter luceneWriter;
	//private IndexReader luceneReader;
	//private IndexSearcher luceneSearcher;

	private String basePath;



	public LuceneHelper(
			String basePath) throws ArxiuException {
		if (basePath.endsWith("/")) {
			this.basePath = basePath;
		} else {
			this.basePath = basePath + "/";
		}
		String lucenePath = this.basePath + LUCENE_FOLDER_NAME;
		try {
			FSDirectory luceneDirectory = FSDirectory.open(Paths.get(lucenePath));
			luceneAnalyzer = new KeywordAnalyzer();
			luceneWriter = new IndexWriter(
					luceneDirectory, 
					new IndexWriterConfig(luceneAnalyzer));
			//luceneReader = DirectoryReader.open(luceneDirectory);
			//luceneSearcher = new IndexSearcher(luceneReader);
		} catch (IOException e) {
			throw new ArxiuException(
					"Error al inicialitzar el directori base de Lucene", e);
		}
	}

	public void expedientCrear(
			Expedient expedient,
			String uuid,
			String identificadorEni,
			String path) throws IOException {
		Document doc = modificarLuceneDocument(
				null,
				expedient,
				ExpedientEstat.OBERT,
				uuid,
				identificadorEni,
				path);
		afegirDocument(doc);
	}
	public void expedientModificar(
			Expedient expedient) throws IOException {
		Document docGuardat = findByUuid(
				ContingutTipus.EXPEDIENT,
				expedient.getIdentificador());
		String identificadorEni = docGuardat.get(LUCENE_FIELD_META_ID);
		String path = docGuardat.get(LUCENE_FIELD_PATH);
		ExpedientEstat estat = ExpedientEstat.toEnum(docGuardat.get(LUCENE_FIELD_META_ESTAT));
		Document docModificat = modificarLuceneDocument(
				docGuardat,
				expedient,
				estat,
				expedient.getIdentificador(),
				identificadorEni,
				path);
		actualitzarDocument(
				expedient.getIdentificador(),
				docModificat);
	}
	public Expedient expedientDetalls(
			String uuid) throws IOException, ParseException {
		Document doc = findByUuid(
				ContingutTipus.EXPEDIENT,
				uuid);
		ContingutTipus tipus = ContingutTipus.valueOf(doc.get(LUCENE_FIELD_TIPUS));
		if (!ContingutTipus.EXPEDIENT.equals(tipus)) {
			throw new ArxiuException(
					"El contingut retornat per Lucene no és del tipus expedient (tipus=" + tipus + ")");
		}
		Expedient expedient = new Expedient();
		expedient.setIdentificador(uuid);
		expedient.setNom(
				doc.get(LUCENE_FIELD_NOM));
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setIdentificador(
				doc.get(LUCENE_FIELD_META_ID));
		metadades.setVersioNti(
				doc.get(LUCENE_FIELD_META_VERSIO));
		String[] organs = doc.getValues(LUCENE_FIELD_META_ORGAN);
		if (organs != null && organs.length > 0) {
			metadades.setOrgans(Arrays.asList(organs));
		}
		if (doc.get(LUCENE_FIELD_META_DATA) != null) {
			metadades.setDataObertura(
					dataDesDeLucene(doc.get(LUCENE_FIELD_META_DATA)));
		}
		metadades.setClassificacio(
				doc.get(LUCENE_FIELD_META_CLASSIFICACIO));
		metadades.setEstat(
				ExpedientEstat.toEnum(doc.get(LUCENE_FIELD_META_ESTAT)));
		String[] interessats = doc.getValues(LUCENE_FIELD_META_INTERESSAT);
		if (interessats != null && interessats.length > 0) {
			metadades.setInteressats(Arrays.asList(interessats));
		}
		metadades.setSerieDocumental(
				doc.get(LUCENE_FIELD_META_SERIE_DOC));
		expedient.setMetadades(metadades);
		return expedient;
	}

	public String documentCrear(
			es.caib.plugins.arxiu.api.Document document,
			String uuid,
			String pareUuid,
			String identificadorEni,
			String path) throws IOException {
		Document doc = modificarLuceneDocument(
				null,
				document,
				document.getEstat(),
				uuid,
				pareUuid,
				identificadorEni,
				path);
		afegirDocument(doc);
		return uuid;
	}
	public void documentModificar(
			es.caib.plugins.arxiu.api.Document document) throws IOException {
		Document docGuardat = findByUuid(
				ContingutTipus.DOCUMENT,
				document.getIdentificador());
		String identificadorEni = docGuardat.get(LUCENE_FIELD_META_ID);
		String path = docGuardat.get(LUCENE_FIELD_PATH);
		Document docModificat = modificarLuceneDocument(
				docGuardat,
				document,
				document.getEstat(),
				document.getIdentificador(),
				docGuardat.get(LUCENE_FIELD_PARE_UUID),
				identificadorEni,
				path);
		actualitzarDocument(
				document.getIdentificador(),
				docModificat);
	}
	public es.caib.plugins.arxiu.api.Document documentDetalls(
			String uuid) throws IOException, ParseException {
		Document doc = findByUuid(
				ContingutTipus.DOCUMENT,
				uuid);
		ContingutTipus tipus = ContingutTipus.valueOf(doc.get(LUCENE_FIELD_TIPUS));
		if (!ContingutTipus.DOCUMENT.equals(tipus)) {
			throw new ArxiuException(
					"El contingut retornat per Lucene no és del tipus document (tipus=" + tipus + ")");
		}
		es.caib.plugins.arxiu.api.Document document = new es.caib.plugins.arxiu.api.Document();
		document.setIdentificador(uuid);
		document.setNom(
				doc.get(LUCENE_FIELD_NOM));
		String contingutArxiu = doc.get(LUCENE_FIELD_CONT_ARXIU);
		if (contingutArxiu != null) {
			DocumentContingut contingut = new DocumentContingut();
			contingut.setArxiuNom(
					doc.get(LUCENE_FIELD_CONT_ARXIUNOM));
			contingut.setTipusMime(
					doc.get(LUCENE_FIELD_CONT_MIME));
			document.setContingut(contingut);
		}
		DocumentMetadades metadades = new DocumentMetadades();
		metadades.setIdentificador(
				doc.get(LUCENE_FIELD_META_ID));
		metadades.setVersioNti(
				doc.get(LUCENE_FIELD_META_VERSIO));
		String[] organs = doc.getValues(LUCENE_FIELD_META_ORGAN);
		if (organs != null && organs.length > 0) {
			metadades.setOrgans(Arrays.asList(organs));
		}
		if (doc.get(LUCENE_FIELD_META_DATA) != null) {
			metadades.setDataCaptura(
					dataDesDeLucene(doc.get(LUCENE_FIELD_META_DATA)));
		}
		metadades.setOrigen(
				ContingutOrigen.toEnum(doc.get(LUCENE_FIELD_META_ORIGEN)));
		metadades.setEstatElaboracio(
				DocumentEstatElaboracio.toEnum(doc.get(LUCENE_FIELD_META_ESTAT_ELAB)));
		metadades.setTipusDocumental(
				DocumentTipus.toEnum(doc.get(LUCENE_FIELD_META_DOC_TIPUS)));
		metadades.setFormat(
				DocumentFormat.toEnum(doc.get(LUCENE_FIELD_META_FORMAT)));
		metadades.setExtensio(
				DocumentExtensio.toEnum(doc.get(LUCENE_FIELD_META_EXTENSIO)));
		metadades.setIdentificadorOrigen(doc.get(LUCENE_FIELD_META_ORIG_ID));
		String[] firmesTipus = doc.getValues(LUCENE_FIELD_FIRMA_TIPUS);
		String[] firmaPerfils = doc.getValues(LUCENE_FIELD_FIRMA_PERFIL);
		String[] firmaMimes = doc.getValues(LUCENE_FIELD_FIRMA_MIME);
		String[] firmaCsvs = doc.getValues(LUCENE_FIELD_FIRMA_CSV);
		String[] firmaDefCsvs = doc.getValues(LUCENE_FIELD_FIRMA_DEFCSV);
		if (firmesTipus != null && firmesTipus.length > 0) {
			List<Firma> firmes = new ArrayList<Firma>();
			int csvIndex = 0;
			for (int i = 0; i < firmesTipus.length; i++) {
				Firma firma = new Firma();
				FirmaTipus firmaTipus = FirmaTipus.toEnum(firmesTipus[i]);
				firma.setTipus(firmaTipus);
				firma.setPerfil(FirmaPerfil.toEnum(firmaPerfils[i]));
				firma.setTipusMime(firmaMimes[i]);
				if (FirmaTipus.CSV.equals(firmaTipus)) {
					String csv = firmaCsvs[csvIndex];
					String defcsv = firmaDefCsvs[csvIndex];
					csvIndex++;
					firma.setContingut(csv.getBytes());
					firma.setTamany(firma.getContingut().length);
					firma.setCsvRegulacio(defcsv);
				}
				firmes.add(firma);
			}
			document.setFirmes(firmes);
		}
		document.setMetadades(metadades);
		return document;
	}

	public String carpetaCrear(
			Carpeta carpeta,
			String uuid,
			String pareUuid,
			String path) throws IOException {
		Document doc = modificarLuceneDocument(
				null,
				carpeta,
				uuid,
				pareUuid,
				path);
		afegirDocument(doc);
		return uuid;
	}
	public void carpetaModificar(
			Carpeta carpeta) throws IOException {
		Document docGuardat = findByUuid(
				ContingutTipus.CARPETA,
				carpeta.getIdentificador());
		String path = docGuardat.get(LUCENE_FIELD_PATH);
		Document docModificat = modificarLuceneDocument(
				docGuardat,
				carpeta,
				carpeta.getIdentificador(),
				docGuardat.get(LUCENE_FIELD_PARE_UUID),
				path);
		actualitzarDocument(
				carpeta.getIdentificador(),
				docModificat);
	}
	public Carpeta carpetaDetalls(
			String uuid) throws IOException {
		Document doc = findByUuid(
				ContingutTipus.CARPETA,
				uuid);
		ContingutTipus tipus = ContingutTipus.valueOf(doc.get(LUCENE_FIELD_TIPUS));
		if (!ContingutTipus.CARPETA.equals(tipus)) {
			throw new ArxiuException(
					"El contingut retornat per Lucene no és del tipus carpeta (tipus=" + tipus + ")");
		}
		Carpeta carpeta = new Carpeta();
		carpeta.setNom(
				doc.get(LUCENE_FIELD_NOM));
		return carpeta;
	}

	public void contingutMoure(
			ContingutTipus tipus,
			String uuid,
			String destiUuid,
			String pathDesti) throws IOException {
		Document doc = findByUuid(
				tipus,
				uuid);
		String pathContingutMogut = pathDesti + "/" + doc.get(LUCENE_FIELD_META_ID);
		Document docMogut = crearLuceneDocument(
				tipus,
				uuid,
				destiUuid,
				pathContingutMogut);
		doc.removeField(LUCENE_FIELD_PATH);
		doc.add(
				new StringField(
						LUCENE_FIELD_PATH,
						pathContingutMogut,
						Store.YES));
		doc.removeField(LUCENE_FIELD_PARE_UUID);
		doc.add(
				new StringField(
						LUCENE_FIELD_PARE_UUID,
						docMogut.get(LUCENE_FIELD_PARE_UUID),
						Store.YES));
		doc.removeField(LUCENE_FIELD_EXP_UUID);
		doc.add(
				new StringField(
						LUCENE_FIELD_EXP_UUID,
						docMogut.get(LUCENE_FIELD_EXP_UUID),
						Store.YES));
		actualitzarDocument(
				uuid,
				doc);
	}
	public void contingutEsborrar(
			String uuid) throws IOException {
		//System.out.println(">>> LUCENE DELETE " + uuid);
		luceneWriter.deleteDocuments(
				new Term(LUCENE_FIELD_UUID, uuid));
		luceneWriter.commit();
	}

	public ConsultaResultat contingutCercar(
			ContingutTipus tipus,
			List<ConsultaFiltre> filtres,
			int pagina,
			int itemsPerPagina) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
		StringBuilder query = new StringBuilder();
		query.append(LUCENE_FIELD_TIPUS + ":\"" + tipus.toString() + "\"");
		if (filtres != null) {
			for (ConsultaFiltre filtre: filtres) {
				query.append(" AND ");
				query.append(getQueryPerFiltre(filtre));
			}
		}
		QueryParser queryParser = new QueryParser(null, luceneAnalyzer);
        queryParser.setAllowLeadingWildcard(true);
        ConsultaLuceneResultat luceneResultat = executarConsultaLucenePaginada(
        		queryParser.parse(query.toString()),
        		pagina,
        		itemsPerPagina,
        		Integer.MAX_VALUE);
        List<ContingutArxiu> continguts = new ArrayList<ContingutArxiu>();
        for (Document doc: luceneResultat.getDocuments()) {
        	continguts.add(crearContingutArxiu(
        			doc.get(LUCENE_FIELD_UUID),
        			doc.get(LUCENE_FIELD_NOM),
    				ContingutTipus.valueOf(doc.get(LUCENE_FIELD_TIPUS)),
    				null));
        }
        ConsultaResultat resultat = new ConsultaResultat();
        resultat.setNumPagines(luceneResultat.getPaginesCount());
        resultat.setNumRegistres(luceneResultat.getResultatsCount());
        resultat.setNumRetornat(continguts.size());
        resultat.setPaginaActual(pagina);
        resultat.setResultats(continguts);
        return resultat;
	}

	public String getPath(
			String uuid) throws IOException {
		Document doc = findByUuid(
				null,
				uuid);
		return doc.get(LUCENE_FIELD_PATH);
	}

	public String getParePath(
			ContingutTipus tipus,
			String uuid) throws IOException {
		Document doc = findByUuid(
				tipus,
				uuid);
		String pareUuid = doc.get(LUCENE_FIELD_PARE_UUID);
		if (pareUuid != null) {
			return getPath(
					pareUuid);
		} else {
			return null;
		}
	}

	public ExpedientEstat getExpedientPareEstat(
			String uuid) throws IOException {
		Document doc = findByUuid(
				null,
				uuid);
		Document expedientPareDoc;
		if (ContingutTipus.EXPEDIENT.equals(getTipusPerLuceneDocument(doc))) {
			expedientPareDoc = doc;
		} else {
			expedientPareDoc = findByUuid(
					ContingutTipus.EXPEDIENT,
					doc.get(LUCENE_FIELD_EXP_UUID));
		}
		return ExpedientEstat.toEnum(expedientPareDoc.get(LUCENE_FIELD_META_ESTAT));
	}

	public void expedientCanviEstat(
			String uuid,
			ExpedientEstat estat) throws IOException {
		Document doc = findByUuid(
				ContingutTipus.EXPEDIENT,
				uuid);
		if (ExpedientEstat.TANCAT.equals(estat)) {
			// TODO esborrar documents de tipus esborrany (i les carpetes buides?)
		}
		doc.removeField(LUCENE_FIELD_META_ESTAT);
		doc.add(
				new StringField(
						LUCENE_FIELD_META_ESTAT,
						estat.toString(),
						Store.YES));
		actualitzarDocument(
				uuid,
				doc);
	}

	public DocumentEstat getDocumentEstat(
			String uuid) throws IOException {
		Document doc = findByUuid(
				ContingutTipus.DOCUMENT,
				uuid);
		return DocumentEstat.valueOf(doc.get(LUCENE_FIELD_META_ESTAT));
	}

	public boolean isDocumentConteFirmes(
			String uuid) throws IOException {
		Document doc = findByUuid(
				ContingutTipus.DOCUMENT,
				uuid);
		return doc.get(LUCENE_FIELD_FIRMA_TIPUS) != null;
	}

	public boolean isExpedientConteDocumentsDefinitius(
			String uuid) throws IOException {
		List<Document> documents = findDocumentsDefinitiusByExpedient(uuid);
		return documents != null && !documents.isEmpty();
	}

	public String getIdentificadorEni(
			String uuid) throws IOException {
		Document doc = findByUuid(
				null,
				uuid);
		return doc.get(LUCENE_FIELD_META_ID);
	}



	private Document modificarLuceneDocument(
			Document docOriginal,
			Expedient expedient,
			ExpedientEstat estat,
			String uuid,
			String identificadorEni,
			String path) throws IOException {
		Document doc;
		if (docOriginal != null) {
			doc = clonarDocument(docOriginal);
		} else {
			doc = crearLuceneDocument(
					ContingutTipus.EXPEDIENT,
					uuid,
					null,
					path);
		}
		updateStringField(
				doc,
				LUCENE_FIELD_NOM,
				expedient.getNom());
		ExpedientMetadades metadades = expedient.getMetadades();
		if (metadades != null) {
			updateStringField(
					doc,
					LUCENE_FIELD_META_ID,
					identificadorEni);
			updateStringField(
					doc,
					LUCENE_FIELD_META_VERSIO,
					ENI_EXP_VERSIO);
			updateStringField(
					doc,
					LUCENE_FIELD_META_ORGAN,
					metadades.getOrgans());
			updateStringField(
					doc,
					LUCENE_FIELD_META_DATA,
					dataCapALucene(metadades.getDataObertura()));
			updateStringField(
					doc,
					LUCENE_FIELD_META_CLASSIFICACIO,
					metadades.getClassificacio());
			updateStringField(
					doc,
					LUCENE_FIELD_META_ESTAT,
					(estat != null) ? estat.toString() : null);
			updateStringField(
					doc,
					LUCENE_FIELD_META_INTERESSAT,
					metadades.getInteressats());
			updateStringField(
					doc,
					LUCENE_FIELD_META_SERIE_DOC,
					metadades.getSerieDocumental());
		}
		return doc;
	}
	private Document modificarLuceneDocument(
			Document docOriginal,
			es.caib.plugins.arxiu.api.Document document,
			DocumentEstat estat,
			String uuid,
			String pareUuid,
			String identificadorEni,
			String path) throws IOException {
		Document doc;
		if (docOriginal != null) {
			doc = clonarDocument(docOriginal);
		} else {
			doc = crearLuceneDocument(
					ContingutTipus.DOCUMENT,
					uuid,
					pareUuid,
					path);
		}
		updateStringField(
				doc,
				LUCENE_FIELD_NOM,
				document.getNom());
		DocumentContingut contingut = document.getContingut();
		if (contingut != null) {
			updateStringField(
					doc,
					LUCENE_FIELD_CONT_ARXIU,
					"1");
			doc.removeField(LUCENE_FIELD_CONT_ARXIUNOM);
			updateStringField(
					doc,
					LUCENE_FIELD_CONT_ARXIUNOM,
					contingut.getArxiuNom());
			doc.removeField(LUCENE_FIELD_CONT_MIME);
			updateStringField(
					doc,
					LUCENE_FIELD_CONT_MIME,
					contingut.getTipusMime());
		}
		DocumentMetadades metadades = document.getMetadades();
		if (metadades != null) {
			updateStringField(
					doc,
					LUCENE_FIELD_META_ID,
					identificadorEni);
			updateStringField(
					doc,
					LUCENE_FIELD_META_VERSIO,
					ENI_DOC_VERSIO);
			updateStringField(
					doc,
					LUCENE_FIELD_META_ORGAN,
					metadades.getOrgans());
			updateStringField(
					doc,
					LUCENE_FIELD_META_DATA,
					dataCapALucene(metadades.getDataCaptura()));
			updateStringField(
					doc,
					LUCENE_FIELD_META_ESTAT,
					(estat != null) ? estat.toString() : null);
			updateStringField(
					doc,
					LUCENE_FIELD_META_ORIGEN,
					(metadades.getOrigen() != null) ? metadades.getOrigen().toString() : null);
			updateStringField(
					doc,
					LUCENE_FIELD_META_ESTAT_ELAB,
					(metadades.getEstatElaboracio() != null) ? metadades.getEstatElaboracio().toString() : null);
			updateStringField(
					doc,
					LUCENE_FIELD_META_FORMAT,
					(metadades.getFormat() != null) ? metadades.getFormat().toString() : null);
			updateStringField(
					doc,
					LUCENE_FIELD_META_EXTENSIO,
					(metadades.getExtensio() != null) ? metadades.getExtensio().toString() : null);
			updateStringField(
					doc,
					LUCENE_FIELD_META_DOC_TIPUS,
					(metadades.getTipusDocumental() != null) ? metadades.getTipusDocumental().toString() : null);
			updateStringField(
					doc,
					LUCENE_FIELD_META_ORIG_ID,
					metadades.getIdentificadorOrigen());
		}
		if (document.getFirmes() != null) {
			doc.removeFields(LUCENE_FIELD_FIRMA_TIPUS);
			doc.removeFields(LUCENE_FIELD_FIRMA_PERFIL);
			doc.removeFields(LUCENE_FIELD_FIRMA_MIME);
			doc.removeFields(LUCENE_FIELD_FIRMA_CSV);
			doc.removeFields(LUCENE_FIELD_FIRMA_DEFCSV);
			for (Firma firma: document.getFirmes()) {
				doc.add(
						new StringField(
								LUCENE_FIELD_FIRMA_TIPUS,
								firma.getTipus().toString(),
								Store.YES));
				doc.add(
						new StringField(
								LUCENE_FIELD_FIRMA_PERFIL,
								firma.getPerfil().toString(),
								Store.YES));
				doc.add(
						new StringField(
								LUCENE_FIELD_FIRMA_MIME,
								firma.getTipusMime(),
								Store.YES));
				if (FirmaTipus.CSV.equals(firma.getTipus())) {
					doc.add(
							new StringField(
									LUCENE_FIELD_FIRMA_CSV,
									new String(firma.getContingut()),
									Store.YES));
					doc.add(
							new StringField(
									LUCENE_FIELD_FIRMA_DEFCSV,
									firma.getCsvRegulacio(),
									Store.YES));
				}
			}
		}
		return doc;
	}
	private Document modificarLuceneDocument(
			Document docOriginal,
			Carpeta carpeta,
			String uuid,
			String pareUuid,
			String path) throws IOException {
		Document doc;
		if (docOriginal != null) {
			doc = clonarDocument(docOriginal);
		} else {
			doc = crearLuceneDocument(
					ContingutTipus.CARPETA,
					uuid,
					pareUuid,
					path);
		}
		updateStringField(
				doc,
				LUCENE_FIELD_NOM,
				carpeta.getNom());
		return doc;
	}

	private Document crearLuceneDocument(
			ContingutTipus tipus,
			String uuid,
			String pareUuid,
			String path) throws IOException {
		Document document = new Document();
		document.add(
				new StringField(
						LUCENE_FIELD_TIPUS,
						tipus.name(),
						Store.YES));
		document.add(
				new StringField(
						LUCENE_FIELD_UUID,
						uuid,
						Store.YES));
		if (pareUuid != null) {
			document.add(
					new StringField(
							LUCENE_FIELD_PARE_UUID,
							pareUuid,
							Store.YES));
			Document expedientPareDoc;
			String auxPareUuid = pareUuid;
			do {
				expedientPareDoc = findByUuid(
						tipus,
						auxPareUuid);
				auxPareUuid = getPareUuidPerLuceneDocument(expedientPareDoc);
			} while (!ContingutTipus.EXPEDIENT.equals(getTipusPerLuceneDocument(expedientPareDoc)));
			String expedientPareUuid = expedientPareDoc.get(LUCENE_FIELD_UUID);
			document.add(
					new StringField(
							LUCENE_FIELD_EXP_UUID,
							expedientPareUuid,
							Store.YES));
		}
		document.add(
				new StringField(
						LUCENE_FIELD_PATH,
						path,
						Store.YES));
		return document;
	}

	private ContingutTipus getTipusPerLuceneDocument(
			Document doc) {
		return ContingutTipus.valueOf(doc.get(LUCENE_FIELD_TIPUS));
	}
	
	private String getPareUuidPerLuceneDocument(
			Document doc) {
		return doc.get(LUCENE_FIELD_PARE_UUID);
	}

	private Document findByUuid(
			ContingutTipus tipus,
			String uuid) throws IOException {
		ConsultaLuceneResultat resultat = executarConsultaLucenePaginada(
				new TermQuery(new Term(LUCENE_FIELD_UUID, uuid)),
				0,
				1,
				Integer.MAX_VALUE);
		List<Document> documents = resultat.getDocuments();
		if (documents == null || documents.isEmpty()) {
			throw new ArxiuNotFoundException(
					(tipus != null) ? tipus.name() : "CONTINGUT",
					uuid);
		} else if (documents.size() > 1) {
			throw new ArxiuException(
					"S'ha trobat més d'un resultat en la consulta per UUID (uuid=" + uuid + ")");
		} else {
			return documents.get(0);
		}
	}

	private List<Document> findDocumentsDefinitiusByExpedient(
			String uuid) throws IOException {
		BooleanQuery booleanQuery = new BooleanQuery.Builder().
				add(
						new TermQuery(new Term(LUCENE_FIELD_TIPUS, ContingutTipus.DOCUMENT.toString())),
						BooleanClause.Occur.MUST).
				add(
						new TermQuery(new Term(LUCENE_FIELD_EXP_UUID, uuid)),
						BooleanClause.Occur.MUST).
				add(
						new TermQuery(new Term(LUCENE_FIELD_META_ESTAT, DocumentEstat.DEFINITIU.toString())),
						BooleanClause.Occur.MUST).build();
		ConsultaLuceneResultat resultat = executarConsultaLucenePaginada(
				booleanQuery,
				0,
				1,
				Integer.MAX_VALUE);
		return resultat.getDocuments();
	}

	private String dataCapALucene(Date data) {
		if (data == null) {
			return null;
		}
		return String.format("%013d", data.getTime());
	}
	private Date dataDesDeLucene(String timestamp) {
		if (timestamp == null) {
			return null;
		}
		return new Date(Long.parseLong(timestamp));
	}

	private Document clonarDocument(Document original) {
		Document doc = new Document();
		for (IndexableField field: original.getFields()) {
			doc.add(field);
		}
		return doc;
	}
	private void updateStringField(
			Document doc,
			String field,
			String valor) {
		if (valor != null) {
			doc.removeFields(field);
			doc.add(
					new StringField(
							field,
							valor,
							Store.YES));
		}
	}
	private void updateStringField(
			Document doc,
			String field,
			List<String> valors) {
		if (valors != null) {
			doc.removeFields(field);
			for (String valor: valors) {
				doc.add(
						new StringField(
								field,
								valor,
								Store.YES));
			}
		}
	}

	private void afegirDocument(
			Document doc) throws IOException {
		//System.out.println(">>> LUCENE ADD " + doc.get(LUCENE_FIELD_UUID));
		luceneWriter.addDocument(doc);
		luceneWriter.commit();
	}
	private void actualitzarDocument(
			String uuid,
			Document doc) throws IOException {
		//System.out.println(">>> LUCENE UPDATE " + uuid);
		luceneWriter.updateDocument(
				new Term(LUCENE_FIELD_UUID, uuid),
				doc);
		luceneWriter.commit();
		//imprimirDocument(doc);
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

	private String getQueryPerFiltre(ConsultaFiltre filtre) {
		StringBuilder query = new StringBuilder();
		switch(filtre.getOperacio()) {
		case IGUAL:
			query.append(filtre.getMetadada());
			query.append(":\"");
			query.append(filtre.getValorOperacio1());
			query.append("\"");
			break;
		case CONTE:
			query.append(filtre.getMetadada());
			query.append(":*");
			query.append(filtre.getValorOperacio1());
			query.append("*");
			break;
		case MAJOR:
			query.append(filtre.getMetadada());
			query.append(":[");
			query.append(filtre.getValorOperacio1());
			query.append(" TO *] -");
			query.append(filtre.getMetadada());
			query.append(":\"");
			query.append(filtre.getValorOperacio1());
			query.append("\"");
			break;
		case MENOR:
			query.append(filtre.getMetadada());
			query.append(":[* TO ");
			query.append(filtre.getValorOperacio1());
			query.append("] -");
			query.append(filtre.getMetadada());
			query.append(":\"");
			query.append(filtre.getValorOperacio1());
			query.append("\"");
			break;
		case ENTRE:
			query.append(filtre.getMetadada());
			query.append(":[");
			query.append(filtre.getValorOperacio1());
			query.append(" TO ");
			query.append(filtre.getValorOperacio2());
			query.append("]");
			break;
		default:
			throw new ArxiuException("No s'ha definit un operador per la metadada " + filtre.getMetadada());
		}
		return query.toString();
	}

	private ConsultaLuceneResultat executarConsultaLucenePaginada(
			final Query query,
			final int pagina,
			final int itemsPerPagina,
			final int maxCount) throws IOException {
		IndexReader luceneReader = null;
		try {
			//System.out.println(">>> LUCENE QUERY (" + query.toString() + ")");
			luceneReader = DirectoryReader.open(
					luceneWriter,
					true);
			ExecucioDinsConsultaLucene execucio = new ExecucioDinsConsultaLucene() {
				public ConsultaLuceneResultat executar(IndexSearcher searcher) throws IOException {
					TopDocs tops = searcher.search(
							query,
							maxCount);
					ScoreDoc[] hits = tops.scoreDocs;
					int primer = pagina * itemsPerPagina;
					int darrer = primer + itemsPerPagina;
					if (darrer > hits.length) {
						darrer = hits.length;
					}
					List<Document> resultats = new ArrayList<Document>();
					for (int i = primer; i < darrer; i++) {
						if (resultats == null) {
							resultats = new ArrayList<Document>();
						}
						resultats.add(searcher.doc(hits[i].doc));
					}
					return new ConsultaLuceneResultat(
							hits.length,
							(int)Math.ceil((double)hits.length / itemsPerPagina),
							resultats);
				}
			};
			return execucio.executar(new IndexSearcher(luceneReader));
		} finally {
			if (luceneReader != null) {
				luceneReader.close();
			}
		}
	}

	private abstract class ExecucioDinsConsultaLucene {
		public abstract ConsultaLuceneResultat executar(IndexSearcher luceneSearcher) throws IOException;
	}

	private class ConsultaLuceneResultat {
		private Integer resultatsCount;
		private Integer paginesCount;
		private List<Document> documents;
		public ConsultaLuceneResultat(Integer resultatsCount, Integer paginesCount, List<Document> documents) {
			super();
			this.resultatsCount = resultatsCount;
			this.paginesCount = paginesCount;
			this.documents = documents;
		}
		public Integer getResultatsCount() {
			return resultatsCount;
		}
		public Integer getPaginesCount() {
			return paginesCount;
		}
		public List<Document> getDocuments() {
			return documents;
		}
	}

	@SuppressWarnings("unused")
	private void imprimirContinguts() throws IOException {
		IndexReader luceneReader = null;
		try {
			luceneReader = DirectoryReader.open(
					luceneWriter,
					true);
			System.out.println(">>> LUCENE DBG NUMDOCS: " + luceneReader.numDocs());
			System.out.println(">>> LUCENE DBG NUMDELETEDDOCS: " + luceneReader.numDeletedDocs());
			IndexSearcher searcher = new IndexSearcher(luceneReader);
			TopDocs tops = searcher.search(
					new MatchAllDocsQuery(),
					Integer.MAX_VALUE);
			ScoreDoc[] hits = tops.scoreDocs;
			for (ScoreDoc hit: hits) {
				System.out.println("   >>> DOC ID: " + hit.doc);
				System.out.println("   >>> DOC UUID: " + searcher.doc(hit.doc).getField(LUCENE_FIELD_UUID).stringValue());
			}
			System.out.println(">>> LUCENE DBG LASTDOC " + hits[hits.length - 1].doc);
			imprimirDocument(searcher.doc(hits[hits.length - 1].doc));
		} finally {
			if (luceneReader != null) {
				luceneReader.close();
			}
		}
	}
	private void imprimirDocument(
			Document doc) throws IOException {
		System.out.println(">>> LUCENE DBG PRINTDOC");
		for (IndexableField field: doc.getFields()) {
			System.out.println("   >>> " + field);
		}
	}

}
