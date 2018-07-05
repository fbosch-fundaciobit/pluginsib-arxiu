package es.caib.plugins.arxiu.filesystem;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;

public class FilesystemArxiuDAOImpl implements FilesystemArxiuDAO {
	
	private final String FILESYSTEM_NAME = "database.lucene";
	
	private FSDirectory index;
	private StandardAnalyzer analyzer;
	
	private Long sequencia;
	
	public FilesystemArxiuDAOImpl(
			String basePath) throws ArxiuException {
		try {
			index = FSDirectory.open(Paths.get(basePath + "/" + FILESYSTEM_NAME));
			analyzer = new StandardAnalyzer(new CharArraySet(Collections.emptySet(), true));
			IndexWriter w = new IndexWriter(
					index, 
					new IndexWriterConfig(analyzer));
			w.close();
			
			sequencia = null;
		} catch (IOException e) {
			throw new ArxiuException(
					"Error intentant accedir a les metadades del sistema de fitxers de Lucene.", e);
		}
	}
	
	public IndexWriter getWriter() throws ArxiuException {
		
		try {
			return new IndexWriter(
					index, 
					new IndexWriterConfig(analyzer));
		} catch (IOException e) {
			throw new ArxiuException(
					"Error intentant iniciar una transacció", e);
		}
	}
	
	public void closeWriter(
			IndexWriter w) throws ArxiuException {
		
		try {
			if(sequencia != null)
				putIdentificador(w, String.valueOf(sequencia));
			
			w.close();
		} catch (Exception e) {
			throw new ArxiuException(
					"Error intentant finalitzar una transacció", e);
		}
	}
	
	public void rollbackWriter(
			IndexWriter w) throws ArxiuException {
		
		try {
			w.rollback();
		} catch (IOException e) {
			throw new ArxiuException(
					"Error intentant fer rollback del IndexWriter", e);
		}
	}
	
	private void addIndexField(
			org.apache.lucene.document.Document d,
			String key,
			String value){
		d.add(new TextField(key, value, Field.Store.YES));
	}
	
	private void addField(
			org.apache.lucene.document.Document d,
			String key,
			String value){
		
		d.add(new StringField(key, value, Field.Store.YES));
	}
	
	private void addObject(
			org.apache.lucene.document.Document d,
			String key,
			Object o) throws IOException{
		
		if(o instanceof byte[])
			d.add(new StoredField(key, (byte[]) o));
		else
			d.add(new StoredField(key, Utils.serialize(o)));
		
	}
	
	
	/**
	 * ================= M E T O D E S   E X P E D I E N T S =================
	 */
	
	@Override
	public ExpedientDao fileCreate(
			IndexWriter w,
			ExpedientDao expedient) throws ArxiuException {
		
		try {
			String managerVersion = null;
			String version = null;
			List<ContingutArxiu> informacioItems = null;
			
	        org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
	        
	        if(expedient.getGestorVersions() != null) {
	        	managerVersion = expedient.getGestorVersions();
	        	version = expedient.getVersio();
	        } else {
		        VersionResponse response = addVersion(w, null, expedient.getIdentificador());
		        managerVersion = response.getManagerVersion();
		        version = response.getVersion();
	        }
	        addField(d, Fields.EX_VMANAGER, managerVersion);
        	addField(d, Fields.EX_VERSIO, version);
	        
	        informacioItems = (expedient.getContinguts() == null) ?
	        		new ArrayList<ContingutArxiu>() : expedient.getContinguts();
	        addObject(d, Fields.EX_CONTINGUTS, informacioItems);
	        
	        addIndexField(d, Fields.TABLE, Tables.TABLE_EXPEDIENT.name());
	        addIndexField(d, Fields.ID, expedient.getIdentificador());
	        addIndexField(d, Fields.NOM, expedient.getNom());
	        addField(d, Fields.EX_OBERT, String.valueOf(expedient.isObert()));
	        addIndexField(d, Fields.EX_METADADESID, expedient.getIdMetadades());
	        addIndexField(d, Fields.EX_VERSIONTI, expedient.getVersioNti());
	        addIndexField(d, Fields.EX_ORIGEN, expedient.getOrigen().toString());
	        addIndexField(d, Fields.EX_DATA_OBERTURA, String.valueOf(expedient.getDataObertura().getTime()));
	        addIndexField(d, Fields.EX_CLASSIFICACIO, expedient.getClassificacio());
	        addIndexField(d, Fields.EX_ESTAT, expedient.getEstat().toString());
	        addIndexField(d, Fields.EX_SERIE_DOCUMENTAL, expedient.getSerieDocumental());
	        
	        for(String organ : expedient.getOrgans()) addIndexField(d, Fields.EX_ORGAN, organ);
	        for(String i : expedient.getInteressats()) addIndexField(d, Fields.EX_INTERESSAT, i);
	        
	        w.addDocument(d);
	        
	        return new ExpedientDao(
	        		expedient.getIdentificador(),
	        		expedient.getNom(),
	        		version,
	        		managerVersion,
	        		expedient.isObert(),
	        		expedient.getIdMetadades(),
	        		expedient.getVersioNti(),
	        		expedient.getOrgans(),
	        		expedient.getDataObertura(),
	        		expedient.getClassificacio(),
	        		expedient.getEstat(),
	        		expedient.getInteressats(),
	        		expedient.getSerieDocumental(),
	        		informacioItems);
		} catch (IOException e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant crear un expedient", e);
		}
    }

	@Override
	public void fileDelete(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
		try {
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_EXPEDIENT.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException("No s'ha trobat el expedient a esborrar amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException("L'expedient a esborrar amb id: " + identificador + " esta repetit");
            
    		w.deleteDocuments(builderBooleanQuery.build());
    		
    		reader.close();
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException(
					"Error intentant esborrar l'expedient amb id: " + identificador, e);
		}
	}

	@Override
	public ExpedientDao fileGet(
			String identificador) throws ArxiuException {
		
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_EXPEDIENT.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        if(hits.length < 1) 
	        	throw new ArxiuException("No s'ha trobat cap expedient amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException("L'expedient amb id: " + identificador + " esta repetit");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        reader.close();
	        @SuppressWarnings("unchecked")
			List<ContingutArxiu> items = (List<ContingutArxiu>) Utils.deserialize(
					d.getBinaryValue(Fields.EX_CONTINGUTS).bytes);
			return new ExpedientDao(
					d.get(Fields.ID),
					d.get(Fields.NOM),
					d.get(Fields.EX_VERSIO),
					d.get(Fields.EX_VMANAGER),
					Boolean.parseBoolean(d.get(Fields.EX_OBERT)),
					d.get(Fields.EX_METADADESID),
					d.get(Fields.EX_VERSIONTI),
					Arrays.asList(d.getValues(Fields.EX_ORGAN)),
					new Date(Long.parseLong(d.get(Fields.EX_DATA_OBERTURA))),
					d.get(Fields.EX_CLASSIFICACIO),
					ExpedientEstat.toEnum(d.get(Fields.EX_ESTAT)),
					Arrays.asList(d.getValues(Fields.EX_INTERESSAT)),
					d.get(Fields.EX_SERIE_DOCUMENTAL),
					items);
		} catch (Exception e) {
			throw new ArxiuException("Error intentant obtenir la informació d'un expedient", e);
		}
	}

	@Override
	public List<ContingutArxiu> fileSearch(
			List<ConsultaFiltre> filtres) throws ArxiuException {
	
		try{
			String query = Fields.TABLE + ":\"" + Tables.TABLE_EXPEDIENT + "\"";
			if(filtres != null && filtres.size() > 0)
				query = query + " AND " + Utils.getQuery(filtres);
//			String query = Utils.getQuery(filtres);
			
			QueryParser queryParser = new QueryParser(null, analyzer);
	        queryParser.setAllowLeadingWildcard(true);
	        
	        Query q = queryParser.parse(query);
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(q, Integer.MAX_VALUE);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length == Integer.MAX_VALUE) 
	        	throw new ArxiuException("El número de resultat de la consulta de espedients supera el màxim permès");
	        
	        List<ContingutArxiu> continguts = new ArrayList<ContingutArxiu>();
	        for (ScoreDoc hit : hits) {
	        	org.apache.lucene.document.Document doc = searcher.doc(hit.doc);
		    	ExpedientDao expedient = fileGet(doc.get(Fields.ID));
		    	continguts.add(Utils.crearContingutArxiu(
        				expedient.getIdentificador(),
        				expedient.getNom(),
        				ContingutTipus.EXPEDIENT,
        				expedient.getVersio()));
    		}
	        
	        reader.close();
	        return continguts;
		} catch (Exception e) {
			throw new ArxiuException("Error intentant obtenir la llista d'expedient", e);
		}
	}
	
	@Override
	public void fileClose(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_EXPEDIENT.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException(
	        			"No s'ha trobat el expedient del que es vol recuperar l'estat obert (id=" + identificador +")");
	        if(hits.length > 1) 
	        	throw new ArxiuException(
	        			"L'expedient del que es vol recuperar l'estat obert esta repetit (id=" + identificador +")");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        
	        if("false".equals(d.get(Fields.EX_OBERT)))
	        		throw new ArxiuException("L'expedient ja esta tencat");
            
    		w.deleteDocuments(builderBooleanQuery.build());
			
			d.removeField(Fields.EX_OBERT);
			d.add(new StringField(Fields.EX_OBERT, "false", Field.Store.YES));
			
	        w.addDocument(d);
	        
	        reader.close();
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant tencar un expedient", e);
		}
	}

	@Override
	public void fileReopen(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_EXPEDIENT.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException(
	        			"No s'ha trobat el expedient del que es vol reobrir (id=" + identificador +")");
	        if(hits.length > 1) 
	        	throw new ArxiuException(
	        			"L'expedient del que es vol reobrir esta repetit (id=" + identificador +")");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        
	        if("true".equals(d.get(Fields.EX_OBERT)))
	        		throw new ArxiuException("L'expedient ja esta obert");
            
    		w.deleteDocuments(builderBooleanQuery.build());
			
			d.removeField(Fields.EX_OBERT);
			d.add(new StringField(Fields.EX_OBERT, "true", Field.Store.YES));
			
	        w.addDocument(d);
	        
	        reader.close();
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant obrir un expedient", e);
		}
	}
	
	public void fileDeleteSon(
			IndexWriter w,
			String expedientId,
			String sonId) throws ArxiuException {
		
		ExpedientDao expedient = fileGet(expedientId);
		for(ContingutArxiu informacioItem : expedient.getContinguts()) {
			if(informacioItem.getIdentificador().equals(sonId)) {
				expedient.getContinguts().remove(informacioItem);
				break;
			}
		}
		fileDelete(w, expedient.getIdentificador());
		fileCreate(w, expedient);
	}
	
	public void fileAddSon(
			IndexWriter w,
			String expedientId,
			ContingutArxiu son) throws ArxiuException {
		
		ExpedientDao expedient = fileGet(expedientId);
		expedient.getContinguts().add(son);
		fileDelete(w, expedient.getIdentificador());
		fileCreate(w, expedient);
	}
	
	
	/**
	 * ================= M E T O D E S   D O C U M E N T S =================
	 */
	
	@Override
	public DocumentDao documentCreate(
			IndexWriter w,
			DocumentDao document) throws ArxiuException {
		
		try {			
			org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
	        
			String gestorVersions = null;
			String versio = null;
			
	        if(document.getGestorVersions() != null) {
	        	gestorVersions = document.getGestorVersions();
	        	versio = document.getVersio();
	        } else {
		        VersionResponse response = addVersion(w, null, document.getIdentificador());
		        gestorVersions = response.getManagerVersion();
	        	versio = response.getVersion();
	        }
	        addField(d, Fields.DOC_VMANAGER, gestorVersions);
        	addField(d, Fields.DOC_VERSIO, versio);
	        
	        addIndexField(d, Fields.TABLE, Tables.TABLE_DOCUMENT.name());
	        addIndexField(d, Fields.ID, document.getIdentificador());
	        addIndexField(d, Fields.NOM, document.getNom());
	        addField(d, Fields.DOC_DRAFT, String.valueOf(document.isEsborrany()));
	        addField(d, Fields.DOC_PARE, document.getPare());
	        addIndexField(d, Fields.DOC_METADADESID, document.getMetadadesid());
	        addIndexField(d, Fields.DOC_VERSIONTI, document.getVersioNti());
	        addIndexField(d, Fields.DOC_DATA, String.valueOf(document.getData().getTime()));
	        addIndexField(d, Fields.DOC_ORIGEN, document.getOrigen().toString());
	        addIndexField(d, Fields.DOC_ESTAT_ELABORACIO, document.getEstatElaboracio().toString());
	        addIndexField(d, Fields.DOC_TIPUS_DOCUMENTAL, document.getTipusDocumental().toString());
	        addIndexField(d, Fields.DOC_SERIE_DOCUMENTAL, document.getSerieDocumental());
	        addField(d, Fields.DOC_TIPUS_MIME, document.getTipusMime());
	        addField(
	        		d,
	        		Fields.DOC_ID_ORIGEN,
	        		document.getIdentificadorOrigen() == null ? "" : document.getIdentificadorOrigen());
	        
	        for(String organ : document.getOrgans()) addField(d, Fields.DOC_ORGAN, organ);
	        
	        for(Firma firma : document.getFirmes())
	        	addField(
	        			d,
	        			Fields.DOC_FIRMES,
	        			firmaCreate(w, firma, document.getIdentificador()));
	        
	        w.addDocument(d);
	        
	        return new DocumentDao(
	        		document.getIdentificador(),
	        		document.getNom(),
	        		versio,
	        		gestorVersions,
	        		document.isEsborrany(),
	        		document.getPare(),
	        		document.getMetadadesid(),
	        		document.getVersioNti(),
	        		document.getOrgans(),
	        		document.getData(),
	        		document.getOrigen(),
	        		document.getEstatElaboracio(),
	        		document.getTipusDocumental(),
	        		document.getSerieDocumental(),
	        		document.getFirmes(),
	        		document.getTipusMime(),
	        		document.getIdentificadorOrigen());
		} catch (IOException e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant crear un document", e);
		}
	}

	@Override
	public void documentFinalSet(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_DOCUMENT.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException(
	        			"No s'ha trobat el document del que es vol fer modificar com a final (id=" + identificador +")");
	        if(hits.length > 1) 
	        	throw new ArxiuException(
	        			"El document del que es vol fer modificar com a final esta repetit (id=" + identificador +")");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        
	        if(!Boolean.parseBoolean(d.get(Fields.DOC_DRAFT)))
	        		throw new ArxiuException("Ja es un document final.");
            
    		w.deleteDocuments(builderBooleanQuery.build());
			
			d.removeField(Fields.DOC_DRAFT);
			addField(d, Fields.DOC_DRAFT, "false");
			
	        w.addDocument(d);
	        
	        reader.close();
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException(
					"Error intentant canviar l'estat d'un document de esborrany a final", e);
		}
	}

	@Override
	public void documentDelete(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
		try {
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_DOCUMENT.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException(
	        			"No s'ha trobat cap document a esborrar amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException(
	        			"El document a esborrar amb id: " + identificador + " esta repetit");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        reader.close();
	        
	        List<String> firmes = Arrays.asList(d.getValues(Fields.DOC_FIRMES));
	        for(String firma : firmes) {
	        	try{
	        		firmaDelete(w, firma);
	        	} catch (ArxiuException e) {
	        		e.printStackTrace();
	        	}
	        }
	        
    		w.deleteDocuments(builderBooleanQuery.build());
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException(
					"Error intentant esborrar el document amb id: " + identificador, e);
		}
	}

	@Override
	public DocumentDao documentGet(
			String identificador) throws ArxiuException {
		
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_DOCUMENT.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException("No s'ha trobat cap document amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException("El document amb id: " + identificador + " esta repetit");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        reader.close();
	        
	        List<String> idsFirma = Arrays.asList(d.getValues(Fields.DOC_FIRMES));
	        List<Firma> firmes = new ArrayList<Firma>();
	        for(String firma : idsFirma) firmes.add(firmaGet(firma));
	        
	        return new DocumentDao(
	        		d.get(Fields.ID),
	        		d.get(Fields.NOM),
	        		d.get(Fields.DOC_VERSIO),
	        		d.get(Fields.DOC_VMANAGER),
	        		Boolean.parseBoolean(d.get(Fields.DOC_DRAFT)),
	        		d.get(Fields.DOC_PARE),
	        		d.get(Fields.DOC_METADADESID),
	        		d.get(Fields.DOC_VERSIONTI),
	        		Arrays.asList(d.getValues(Fields.DOC_ORGAN)),
	        		new Date(Long.parseLong(d.get(Fields.DOC_DATA))),
	        		ContingutOrigen.toEnum(d.get(Fields.DOC_ORIGEN)),
	        		DocumentEstatElaboracio.toEnum(d.get(Fields.DOC_ESTAT_ELABORACIO)),
	        		DocumentTipus.toEnum(d.get(Fields.DOC_TIPUS_DOCUMENTAL)),
					d.get(Fields.DOC_SERIE_DOCUMENTAL),
	        		firmes,
	        		d.get(Fields.DOC_TIPUS_MIME),
	        		d.get(Fields.DOC_ID_ORIGEN));
		} catch (Exception e) {
			throw new ArxiuException("Error intentant obtenir el document id=" + identificador, e);
		}
	}

	@Override
	public List<ContingutArxiu> documentSearch(
			List<ConsultaFiltre> filtres) throws ArxiuException {
		
		try{
			String query = Fields.TABLE + ":\"" + Tables.TABLE_DOCUMENT + "\"";
			if(filtres != null && filtres.size() > 0)
				query = query + " AND " + Utils.getQuery(filtres);
			
			QueryParser queryParser = new QueryParser(null, analyzer);
	        queryParser.setAllowLeadingWildcard(true);
	        
	        Query q = queryParser.parse(query);
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(q, Integer.MAX_VALUE);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length == Integer.MAX_VALUE) 
	        	throw new ArxiuException("El número de resultat de la consulta de documents supera el màxim permès");
	        
	        List<ContingutArxiu> informacioItems = new ArrayList<ContingutArxiu>();
	        for (ScoreDoc hit : hits) {
	        	org.apache.lucene.document.Document doc = searcher.doc(hit.doc);
		    	DocumentDao document = documentGet(doc.get(Fields.ID));
        		informacioItems.add(Utils.crearContingutArxiu(
        				document.getIdentificador(),
        				document.getNom(),
        				ContingutTipus.DOCUMENT,
        				document.getVersio()));
    		}
	        
	        reader.close();
	        
	        return informacioItems;
		} catch (Exception e) {
			throw new ArxiuException("Error intentant obtenir la llista de documents", e);
		}
	}
	
	
	/**
	 * ================= M E T O D E S   C A R P E T E S =================
	 */
	
	@Override
	public String folderCreate(
			IndexWriter w,
			CarpetaDao carpeta) throws ArxiuException {
		
		try {			
	        org.apache.lucene.document.Document d = new org.apache.lucene.document.Document();
	        
	        addIndexField(d, Fields.TABLE, Tables.TABLE_CARPETA.name());
	        addIndexField(d, Fields.ID, carpeta.getIdentificador());
	        addIndexField(d, Fields.NOM, carpeta.getNom());
	        addIndexField(d, Fields.CPT_PARE, carpeta.getPare());
	        addObject(d, Fields.CPT_ITEMS, carpeta.getInformacioItems());
	        
	        w.addDocument(d);
	        
	        return carpeta.getIdentificador();
		} catch (IOException e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant crear una carpeta", e);
		}
	}

	@Override
	public void folderDelete(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
		try {
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_CARPETA.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException(
	        			"No s'ha trobat cap carpeta a esborrar amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException(
	        			"La carpeta a esborrar amb id: " + identificador + " esta repetida");
            
    		w.deleteDocuments(builderBooleanQuery.build());
    		
    		reader.close();
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException(
					"Error intentant esborrar la carpeta amb id: " + identificador, e);
		}
	}

	@Override
	public CarpetaDao folderGet(
			String identificador) throws ArxiuException {
		
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_CARPETA.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException("No s'ha trobat cap carpeta amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException("La carpeta amb id: " + identificador + " esta repetida");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        
	        reader.close();
	        
	        @SuppressWarnings("unchecked")
			List<ContingutArxiu> continguts = (List<ContingutArxiu>)Utils.deserialize(d.getBinaryValue(Fields.CPT_ITEMS).bytes);
	        return new CarpetaDao(
	        		d.get(Fields.ID),
	        		d.get(Fields.NOM),
	        		d.get(Fields.CPT_PARE),
	        		continguts);
		} catch (Exception e) {
			throw new ArxiuException("Error intentant obtenir la carpeta amb id=" + identificador, e);
		}
	}
	
	public void folderDeleteSon(
			IndexWriter w,
			String folderId,
			String sonId) throws ArxiuException {
		
		CarpetaDao carpeta = folderGet(folderId);
		for(ContingutArxiu informacioItem : carpeta.getInformacioItems()) {
			if(informacioItem.getIdentificador().equals(sonId)) {
				carpeta.getInformacioItems().remove(informacioItem);
				break;
			}
		}
		folderDelete(w, carpeta.getIdentificador());
		folderCreate(w, carpeta);
	}
	
	public void folderAddSon(
			IndexWriter w,
			String folderId,
			ContingutArxiu son) throws ArxiuException {
		
		CarpetaDao carpeta = folderGet(folderId);
		carpeta.getInformacioItems().add(son);
		folderDelete(w, carpeta.getIdentificador());
		folderCreate(w, carpeta);
	}
	
	
	/**
	 * ================= M E T O D E S   F I R M E S ================= 
	 */
	
	@Override
	public String firmaCreate(
			IndexWriter w,
			Firma firma,
			String pareId) throws ArxiuException {
		
		try {
			org.apache.lucene.document.Document doc =
	        		new org.apache.lucene.document.Document();
	        
	        doc.add(new TextField(Fields.TABLE, Tables.TABLE_FIRMA.name(), Field.Store.YES));
	        String id = getIdentificador();
	        doc.add(new TextField(Fields.ID, id, Field.Store.YES));
	        doc.add(new StringField(Fields.FIR_DOCUMENT_PARE, pareId, Field.Store.YES));
	        doc.add(new StoredField(Fields.FIR_CONTINGUT, firma.getContingut()));
	        doc.add(new StringField(Fields.FIR_MIME, firma.getTipusMime(), Field.Store.YES));
	        doc.add(
	        		new StringField(
	        				Fields.FIR_TIPUS,
	        				firma.getTipus().toString(),
	        				Field.Store.YES));
	        doc.add(new StringField(Fields.NOM, firma.getFitxerNom(), Field.Store.YES));
	        doc.add(new StringField(
	        		Fields.FIR_CSV_REGULACIO,
	        		firma.getCsvRegulacio(),
	        		Field.Store.YES));
	       
	        w.addDocument(doc);
	        
	        return id;
		} catch (IOException e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant crear una firma", e);
		}
	}
	
	@Override
	public void firmaDelete(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
		try {
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_FIRMA.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException("No s'ha trobat la firma a esborrar amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException("La firma a esborrar amb id: " + identificador + " esta repetida");
            
    		w.deleteDocuments(builderBooleanQuery.build());
    		
    		reader.close();
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException(
					"Error intentant esborrar la firma amb id: " + identificador, e);
		}
	}
	
	@Override
	public Firma firmaGet(
			String identificador) throws ArxiuException {
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_FIRMA.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(identificador),
					Occur.MUST);
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        if(hits.length < 1) 
	        	throw new ArxiuException("No s'ha trobat cap firma amb id: " + identificador);
	        if(hits.length > 1) 
	        	throw new ArxiuException("La firma amb id: " + identificador + " esta repetida");
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        reader.close();
	        Firma firma = new Firma();
	        firma.setContingut(d.getBinaryValue(Fields.FIR_CONTINGUT).bytes);
	        firma.setTamany(firma.getContingut().length);
	        firma.setTipus(FirmaTipus.toEnum(Fields.FIR_TIPUS));
	        firma.setTipusMime(Fields.FIR_MIME);
	        firma.setFitxerNom(Fields.NOM);
	        firma.setCsvRegulacio(Fields.FIR_CSV_REGULACIO);
	        return firma;
		} catch (Exception e) {
			throw new ArxiuException("Error intentant obtenir la firma id=" + identificador, e);
		}
	}
	
	
	/**
	 * =============== M E T O D E S   V E R S I O N S =============== 
	 */
	
	@Override
	public VersionResponse addVersion(
			IndexWriter w,
			String versionManager,
			String identificador) throws ArxiuException {
	
		try {
			String versionManagerId = null;
			String version;
			org.apache.lucene.document.Document dwrite = new org.apache.lucene.document.Document();
			
			if(versionManager == null) {
				version = "1";
				versionManagerId = getIdentificador();
				
				dwrite.add(new TextField(Fields.TABLE, Tables.TABLE_VERSIONS.name(), Field.Store.YES));
		        dwrite.add(new TextField(Fields.ID, versionManagerId, Field.Store.YES));
		        dwrite.add(new StringField(
		        		Fields.VER_VERSIO,
		        		String.valueOf(Integer.parseInt(version)+1),
		        		Field.Store.YES));
		        dwrite.add(new StringField(Fields.VER_IDS, identificador, Field.Store.YES));    	
			} else {
				BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
				builderBooleanQuery.add(
						new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_VERSIONS.name()),
						Occur.MUST);
				builderBooleanQuery.add(
						new QueryParser(Fields.ID, analyzer).parse(versionManager),
						Occur.MUST);
				
		        IndexReader reader = DirectoryReader.open(index);
		        IndexSearcher searcher = new IndexSearcher(reader);
		        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
		        ScoreDoc[] hits = docs.scoreDocs;
		        
		        if(hits.length < 1) 
		        	throw new ArxiuException("No s'ha trobat gestor de versions amb id: " + versionManager);
		        if(hits.length > 1) 
		        	throw new ArxiuException("El gestor de versions amb id: " + versionManager + " esta repetit");
		        
		        org.apache.lucene.document.Document dread = searcher.doc(hits[0].doc);
		        reader.close();
		        version = dread.get(Fields.VER_VERSIO);
		        versionManagerId = versionManager;
		        
		        dwrite.add(new TextField(Fields.TABLE, Tables.TABLE_VERSIONS.name(), Field.Store.YES));
		        dwrite.add(new TextField(Fields.ID, versionManager, Field.Store.YES));
		        dwrite.add(new StringField(
		        		Fields.VER_VERSIO,
		        		String.valueOf(Integer.parseInt(version)+1),
		        		Field.Store.YES));
		        String[] ids = dread.getValues(Fields.VER_IDS);
		        for(String id : ids) {
		        	dwrite.add(new StringField(Fields.VER_IDS, id, Field.Store.YES));
		        }
		        dwrite.add(new StringField(Fields.VER_IDS, identificador, Field.Store.YES));
		        
		        w.deleteDocuments(builderBooleanQuery.build());
			}
			
	        w.addDocument(dwrite);
	        
	        
	        return new VersionResponse(
	        		versionManagerId,
	        		version);
		} catch (Exception e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant afegir una versió al gestor de versions", e);
		}
	}
	
	@Override
	public List<String> versionManagerGetVersions(
			String versionManager) throws ArxiuException {
		
		try{
			BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
			builderBooleanQuery.add(
					new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_VERSIONS.name()),
					Occur.MUST);
			builderBooleanQuery.add(
					new QueryParser(Fields.ID, analyzer).parse(versionManager),
					Occur.MUST);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) 
	        	throw new ArxiuException(
	        			"No s'ha trobat cap gestor de versions amb id: " + versionManager);
	        if(hits.length > 1) 
	        	throw new ArxiuException(
	        			"El gestor de versions amb id: " + versionManager + " esta repetit");
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        
	        reader.close();
	        
			return Arrays.asList(d.getValues(Fields.VER_IDS));
		} catch (Exception e) {
			throw new ArxiuException(
					"Error intentant obtenir les versions del gestor amb id=" + versionManager, e);
		}
	}
	
	
	/**
	 * ================= A L T R E S   M E T O D E S ================= 
	 */
	
	@Override
	public synchronized String getIdentificador() throws ArxiuException {
		
		try {
			if(sequencia == null) {
				Query qtable = new 
						QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_SEQ.name());
				BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
				builderBooleanQuery.add(qtable, Occur.MUST);
		
		        IndexReader reader = DirectoryReader.open(index);
		        IndexSearcher searcher = new IndexSearcher(reader);
		        TopDocs docs = searcher.search(builderBooleanQuery.build(), 1);
		        ScoreDoc[] hits = docs.scoreDocs;
		        
		        String identificador = null;
		        if(hits.length < 1) {
		        	identificador = "0";
		        } else {
		        	org.apache.lucene.document.Document d = searcher.doc(
		        			hits[0].doc);
		        	identificador = d.get(Fields.SEQ_VALOR);
		        }
		        
		        sequencia = Long.parseLong(identificador);
			}
			
			if(sequencia == Long.MAX_VALUE)
				throw new ArxiuException("S'han exhaurit el número màxim de identificadors possibles");
			
			long seq = sequencia;
			sequencia++;
			
			return String.valueOf(seq);	        
		} catch (Exception e) {
			throw new ArxiuException(
        			"Error al obtenir un identificador", e);
        }
        
	}
	private void putIdentificador(
			IndexWriter w,
			String identificador) throws ArxiuException {
		
        try {
        	Query qtable = new 
    				QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_SEQ.name());
    		BooleanQuery.Builder builderBooleanQuery = new BooleanQuery.Builder();
    		builderBooleanQuery.add(qtable, Occur.MUST);
            
    		w.deleteDocuments(builderBooleanQuery.build());
            
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            doc.add(new TextField(Fields.TABLE, Tables.TABLE_SEQ.name(), Field.Store.YES));
            doc.add(new StringField(Fields.SEQ_VALOR, identificador, Field.Store.YES));
            
			w.addDocument(doc);
		} catch (Exception e) {
			throw new ArxiuException(
        			"Error al escriure l'identificador", e);
		}
	}
	
	@Override
	public boolean conte(
			Tables table,
			String identificador) throws ArxiuException {
		
		try{
			Query q = new QueryParser(Fields.ID, analyzer).parse(identificador);
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(q, Tables.values().length);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        for(ScoreDoc hit : hits) {
	        	org.apache.lucene.document.Document d = searcher.doc(hit.doc);
	        	String tipus = d.get(Fields.TABLE);
	        	if(table.name().equals(tipus)) return true;
	        }
	        
	        return false;
		} catch (Exception e) {
			throw new ArxiuException("Error comprovant si l'element es un expedient id=" + identificador, e);
		}
	}

	@Override
	public void guardarMetainformacioFilesystem(
			IndexWriter w,
			boolean emmagatzemamentCodificat) throws ArxiuException {
		
		try {
	        org.apache.lucene.document.Document doc =
	        		new org.apache.lucene.document.Document();
	        
	        doc.add(new TextField(Fields.TABLE, Tables.TABLE_METAINFORMACIO.name(), Field.Store.YES));
	        doc.add(new StringField(
	        		Fields.MI_EMCOD, 
	        		String.valueOf(emmagatzemamentCodificat), 
	        		Field.Store.YES));
	        
	        w.addDocument(doc);
		} catch (IOException e) {
			rollbackWriter(w);
			throw new ArxiuException("Error intentant guardar les metadades de filesystem", e);
		}
	}


	@Override
	public Boolean isEmmagatzemamentCodificat() throws ArxiuException {
		
		try{
			Query q = new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_METAINFORMACIO.name());
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(q, 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        if(hits.length < 1) return null;
	        
	        org.apache.lucene.document.Document d = searcher.doc(hits[0].doc);
	        
	        reader.close();
	        
			return "true".equals(d.get(Fields.MI_EMCOD));
		} catch (Exception e) {
			throw new ArxiuException("Error agafant el identificador de la carpeta root", e);
		}
	}
	
	@Override
	public boolean isSistemaInicialitzat() throws ArxiuException {
		
		try{
			Query q = new QueryParser(Fields.TABLE, analyzer).parse(Tables.TABLE_METAINFORMACIO.name());
			
	        IndexReader reader = DirectoryReader.open(index);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        TopDocs docs = searcher.search(q, 1);
	        ScoreDoc[] hits = docs.scoreDocs;
	        
	        reader.close();
	        
	        return hits.length > 0;
		} catch (Exception e) {
			throw new ArxiuException("Error comprovant si el sistema estava inicialitzat", e);
		}
	}
	
	
}
