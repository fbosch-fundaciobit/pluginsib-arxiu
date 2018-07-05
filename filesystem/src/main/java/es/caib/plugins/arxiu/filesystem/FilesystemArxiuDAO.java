/**
 * 
 */
package es.caib.plugins.arxiu.filesystem;

import java.util.List;

import org.apache.lucene.index.IndexWriter;

import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Firma;

/**
 * Interfície per a accedir a la base de dades Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface FilesystemArxiuDAO {
	

	/**
	 * ================= M E T O D E S   E X P E D I E N T S =================
	 */
	
	public ExpedientDao fileCreate(
			IndexWriter w,
			ExpedientDao expedient) throws ArxiuException;
	
	public void fileDelete(
			IndexWriter w,
			String identificador) throws ArxiuException;
	
	public ExpedientDao fileGet(
			String identificador) throws ArxiuException;
	
	public List<ContingutArxiu> fileSearch(
			List<ConsultaFiltre> filtres) throws ArxiuException;
	
	public void fileClose(
			IndexWriter w,
			String identificador) throws ArxiuException;
	
	public void fileReopen(
			IndexWriter w,
			String identificador) throws ArxiuException;
	
	public void fileDeleteSon(
			IndexWriter w,
			String expedientId,
			String sonId) throws ArxiuException;
	
	public void fileAddSon(
			IndexWriter w,
			String expedientId,
			ContingutArxiu son) throws ArxiuException;
	
	
	/**
	 * ================= M E T O D E S   D O C U M E N T S =================
	 */
	
	public DocumentDao documentCreate(
			IndexWriter w,
			DocumentDao document) throws ArxiuException;
	
	public void documentFinalSet(
			IndexWriter w,
			String identificador) throws ArxiuException;
	
	public void documentDelete(
			IndexWriter w,
			String identificador) throws ArxiuException;
	
	public DocumentDao documentGet(
			String identificador) throws ArxiuException;
	
	public List<ContingutArxiu> documentSearch(
			List<ConsultaFiltre> filtres) throws ArxiuException;
	
	
	/**
	 * ================= M E T O D E S   C A R P E T E S =================
	 */

	public String folderCreate(
			IndexWriter w,
			CarpetaDao carpeta) throws ArxiuException;
	
	public void folderDelete(
			IndexWriter w,
			String identificador) throws ArxiuException;
	
	public CarpetaDao folderGet(
			String identificador) throws ArxiuException;
	
	public void folderDeleteSon(
			IndexWriter w,
			String folderId,
			String sonId) throws ArxiuException;
	
	public void folderAddSon(
			IndexWriter w,
			String folderId,
			ContingutArxiu son) throws ArxiuException;
	
	
	/**
	 * ================= M E T O D E S   F I R M E S ================= 
	 */
	
	public String firmaCreate(
			IndexWriter w,
			Firma firma,
			String pareId) throws ArxiuException;
		
	public void firmaDelete(
			IndexWriter w,
			String identificador) throws ArxiuException;
	
	public Firma firmaGet(
			String identificador) throws ArxiuException;
	
	
	/**
	 * =============== M E T O D E S   V E R S I O N S =============== 
	 */
	
	public VersionResponse addVersion(
			IndexWriter w,
			String versionManager,
			String version) throws ArxiuException;
	
	public List<String> versionManagerGetVersions(
			String versionManager) throws ArxiuException;
	
	/**
	 * ================= A L T R E S   M E T O D E S ================= 
	 */
	
	public IndexWriter getWriter() throws ArxiuException;
	
	public void closeWriter(
			IndexWriter w) throws ArxiuException;
	
	public void rollbackWriter(
			IndexWriter w) throws ArxiuException;
	
	public String getIdentificador() throws ArxiuException;
	
	public boolean conte(
			Tables table,
			String identificador) throws ArxiuException;
	
	public void guardarMetainformacioFilesystem(
			IndexWriter w,
			boolean emmagatzemamentCodificat) throws ArxiuException;
	
	public Boolean isEmmagatzemamentCodificat() throws ArxiuException;
	
	public boolean isSistemaInicialitzat() throws ArxiuException;
	
}
