package es.caib.plugins.arxiu.filesystem;

public class Fields {
	
	// Camps comuns
	public final static String TABLE = "table";
	public final static String ID = "id";
	public final static String NOM = "nom";
		
	// Camps de sequencia
	public final static String SEQ_VALOR = "seq_valor";
	
	// Camps de versions
	public final static String VER_VERSIO = "ver_versio";
	public final static String VER_IDS = "ver_ids";
	
	// Camps de Expedient
	public final static String EX_VERSIO = "ex_versio";
	public final static String EX_VMANAGER = "ex_vmanager";
	public final static String EX_OBERT = "ex_obert";
	public final static String EX_METADADESID = "ex_metadadesid";
	public final static String EX_VERSIONTI = "ex_versionti";
	public final static String EX_ORIGEN = "ex_origen";
	public final static String EX_ORGAN = "ex_organ";
	public final static String EX_DATA_OBERTURA = "ex_data_obertura";
	public final static String EX_CLASSIFICACIO = "ex_classificacio";
	public final static String EX_ESTAT = "ex_estat";
	public final static String EX_INTERESSAT = "ex_interessat";
	public final static String EX_SERIE_DOCUMENTAL = "ex_serie_documental";
	public final static String EX_CONTINGUTS = "ex_continguts";
	
	// Camps de Document
	public final static String DOC_VERSIO = "doc_versio";//String
	public final static String DOC_VMANAGER = "doc_vmanager";//String
	public final static String DOC_DRAFT = "doc_draft";//String
	public final static String DOC_PARE = "doc_pare";//String
	public final static String DOC_METADADESID = "doc_metadadesid";//String
	public final static String DOC_VERSIONTI = "doc_versionti";//String
	public final static String DOC_ORGAN = "doc_organ";//List<String>
	public final static String DOC_DATA = "doc_data";//Date
	public final static String DOC_ORIGEN = "doc_origen";//String
	public final static String DOC_ESTAT_ELABORACIO = "doc_estat_elaboracio";//String
	public final static String DOC_TIPUS_DOCUMENTAL = "doc_tipus_documental";//String
	public final static String DOC_SERIE_DOCUMENTAL = "doc_serie_documental";//String
	public final static String DOC_FIRMES = "doc_firmes";//List<String>
	public final static String DOC_CONTINGUT = "doc_contingut";//byte[]
	public final static String DOC_TIPUS_MIME = "doc_tipus_mime";//String
	public final static String DOC_ID_ORIGEN = "doc_id_origen";//String
	
	// Camps de carpetes
	public final static String CPT_PARE = "cpt_pare";//String
	public final static String CPT_ITEMS = "cpt_items";//byte[]
	
	// Camps de firma
	public final static String FIR_DOCUMENT_PARE = "fir_document_pare";//String
	public final static String FIR_CONTINGUT = "fir_contingut";//byte[]
	public final static String FIR_MIME = "fir_mime";//String
	public final static String FIR_TIPUS = "fir_tipus";//String
	public final static String FIR_CSV_REGULACIO = "fir_nom";//String
	
	// Camps metainformacio
	public final static String MI_EMCOD = "mi_emcod";//String
	
}
