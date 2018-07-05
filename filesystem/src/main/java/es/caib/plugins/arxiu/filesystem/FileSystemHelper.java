/**
 * 
 */
package es.caib.plugins.arxiu.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;

/**
 * Mètodes per a gestionar els diferents tipus d'elements del
 * sistema de fitxers.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FileSystemHelper {

	private static final String CONTINGUT_ARXIU_NOM = "contingut";
	private static final String FIRMA_ARXIU_NOM = "firma";

	private String basePath;



	public FileSystemHelper(
			String basePath) throws ArxiuException {
		if (basePath.endsWith("/")) {
			this.basePath = basePath;
		} else {
			this.basePath = basePath + "/";
		}
	}

	public String expedientCrear(
			Expedient expedient,
			String identificadorEni) {
		String path = getPathPerExpedient(
				expedient,
				identificadorEni);
		if (!new File(path).mkdirs()) {
		    throw new ArxiuException(
		    		"No s'ha pogut crear la carpeta al sistema de fitxers (path=" + path + ")");
		}
		return path;
	}
	public String subExpedientCrear(
			String parePath,
			String identificadorEni) {
		String path = getPathPerSubExpedient(
				parePath,
				identificadorEni);
		if (!new File(path).mkdirs()) {
		    throw new ArxiuException(
		    		"No s'ha pogut crear la carpeta al sistema de fitxers (path=" + path + ")");
		}
		return path;
	}
	public String documentActualitzar(
			String parePath,
			Document document,
			String identificadorEni) throws IOException {
		String path = getPathPerDocument(
				parePath,
				identificadorEni);
		if (!new File(path).exists() && !new File(path).mkdirs()) {
		    throw new ArxiuException(
		    		"No s'ha pogut crear la carpeta al sistema de fitxers (path=" + path + ")");
		}
		if (document.getContingut() != null) {
			File f = filePerDocumentContingut(path);
			f.delete();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(document.getContingut().getContingut());
			fos.flush();
			fos.close();
		}
		if (document.getFirmes() != null) {
			for (File f: new File(path).listFiles()) {
				if (f.getName().startsWith(FIRMA_ARXIU_NOM)) {
					f.delete();
				}
			}
			int index = 0;
			for (Firma firma: document.getFirmes()) {
				if (firma.getContingut() != null) {
					File f = filePerDocumentFirma(path, index);
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(firma.getContingut());
					fos.flush();
					fos.close();
				}
				index++;
			}
		}
		return path;
	}
	public String carpetaCrear(
			String parePath,
			String uuid) throws IOException {
		String path = getPathPerCarpeta(
				parePath,
				uuid);
		if (!new File(path).mkdirs()) {
		    throw new ArxiuException(
		    		"No s'ha pogut crear la carpeta al sistema de fitxers (path=" + path + ")");
		}
		return path;
	}

	public void directoriMoure(
			String pathOrigen,
			String pathDesti) throws IOException {
		FileUtils.moveDirectory(
				new File(pathOrigen),
				new File(pathDesti));
	}
	public void directoriEsborrar(
			String path) throws IOException {
		FileUtils.deleteDirectory(new File(path));
	}

	public byte[] documentContingut(
			String path) throws FileNotFoundException, IOException {
		File f = filePerDocumentContingut(path);
		if (f.exists()) {
			return IOUtils.toByteArray(
					new FileInputStream(f));
		} else {
			return null;
		}
	}
	public byte[] documentFirma(
			String path,
			int index) throws FileNotFoundException, IOException {
		File f = filePerDocumentFirma(path, index);
		if (f.exists()) {
			return IOUtils.toByteArray(
					new FileInputStream(f));
		} else {
			return null;
		}
	}



	private String getPathPerExpedient(
			Expedient expedient,
			String identificadorEni) {
		StringBuilder path = new StringBuilder(basePath);
		ExpedientMetadades metadades = expedient.getMetadades();
		path.append(metadades.getSerieDocumental());
		path.append("/");
		Calendar cal = Calendar.getInstance();
		cal.setTime(metadades.getDataObertura());
		path.append(String.format("%02d", cal.get(Calendar.YEAR)));
		path.append("/");
		path.append(String.format("%02d", cal.get(Calendar.MONTH) + 1));
		path.append("/");
		path.append(String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
		path.append("/");
		path.append("E_");
		path.append(identificadorEni);
		return path.toString();
	}

	private String getPathPerSubExpedient(
			String parePath,
			String identificadorEni) {
		StringBuilder path = new StringBuilder(parePath);
		path.append("/");
		path.append("E_");
		path.append(identificadorEni);
		return path.toString();
	}

	private String getPathPerDocument(
			String parePath,
			String identificadorEni) {
		StringBuilder path = new StringBuilder(parePath);
		path.append("/");
		path.append("D_");
		path.append(identificadorEni);
		return path.toString();
	}

	private String getPathPerCarpeta(
			String parePath,
			String identificadorEni) {
		StringBuilder path = new StringBuilder(parePath);
		path.append("/");
		path.append("C_");
		path.append(identificadorEni);
		return path.toString();
	}

	private File filePerDocumentContingut(
			String path) {
		return new File(path + "/" + CONTINGUT_ARXIU_NOM);
	}
	private File filePerDocumentFirma(
			String path,
			int index) {
		return new File(path + "/" + FIRMA_ARXIU_NOM + index);
	}

}
