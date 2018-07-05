package es.caib.plugins.arxiu.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import es.caib.plugins.arxiu.api.ArxiuException;

public class FilesystemArxiuFilesystem {
	
	private String base;
	
	public FilesystemArxiuFilesystem(
			String base) {
		
		this.base = base;
	}
	
	public static boolean esCarpeta(
			String pathCarpeta) throws ArxiuException {
		
		return new File(pathCarpeta).isDirectory();
	}
	
	public void crearCarpeta(
			String pathCarpeta) throws ArxiuException {
		
		String path = base + pathCarpeta;
		File theDir = new File(path);

		if (theDir.exists())
			throw new ArxiuException(
					"La carpeta de la ruta '" + path + "' ja existeix a filesystem.");
	    try{
	        theDir.mkdir();
	    } catch(SecurityException se){
	    	throw new ArxiuException(
					"S'ha produit un error en el proces de creació del directori '" + path + "'", se);
	    }
	}
	
	public void modificarNomCarpeta(
			String anticNom,
			String nouNom) throws ArxiuException {
		
		File file = new File(base + anticNom);
		File file2 = new File(base + nouNom);

		if (file2.exists())
		   throw new ArxiuException("El ficher no es pot renombrar per que ja existeix (path=" + nouNom + ")");

		if (!file.renameTo(file2))
			throw new ArxiuException("El ficher no es pot renombrar (path=" + nouNom + ")");
	}
	
	public void esborrarCarpeta(
			String path) throws ArxiuException {
		
		File file = new File(base + path);
		if(!file.delete())
			throw new ArxiuException("Error esborrant el directori (path=" + path + ")");
	}
	
	public void moureCarpeta(
			String carpetaPath,
			String destiPath) throws ArxiuException {
		
		if(!new File(base + carpetaPath).renameTo(new File(base + destiPath)))
			throw new ArxiuException(
					"Error moguent la carpeta (origen=" + carpetaPath + " ,destí=" + destiPath + ")");
	}
	
	public void copiarCarpeta(
			String pathOrigen,
			String pathDesti) throws ArxiuException {
		
		try {
			copiarCarpeta(
					new File(base + pathOrigen),
					new File(base + pathDesti));
		} catch (IOException e) {
			throw new ArxiuException(
					"Error copiant la carpeta (origen=" + pathOrigen + " ,destí=" + pathDesti + ")");
		}
	}



	public void crearDocument(
			String pathDocument, 
			byte[] contingut) throws IOException {
		String path = base + pathDocument;
		File theDoc = new File(path);
		if (theDoc.exists() && !theDoc.isDirectory())
			throw new ArxiuException(
					"El document de la ruta '" + path + "' ja existeix a filesystem.");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			fos.write(contingut);
        } finally {
        	if (fos != null) {
        		fos.close();
        	}
        }
	}

	public void modificarDocument(
			String path,
			byte[] contingutNew) throws IOException {
		File document = new File(base + path);
		document.delete();
		crearDocument(path, contingutNew);
	}

	public void esborrarDocument(
			String path) throws ArxiuException {
		
		File file = new File(base + path);
		if(!file.delete())
			throw new ArxiuException("Error esborrant el document (path=" + path + ")");
	}
	
	public void moureDocument(
			String documentPath,
			String destiPath) throws ArxiuException {
		
		if(!new File(base + documentPath).renameTo(new File(base + destiPath)))
			throw new ArxiuException(
					"Error moguent el document (origen=" + documentPath + " ,destí=" + destiPath + ")");
	}
	
	public void copiarDocument(
			String pathOrigen,
			String pathDesti) throws ArxiuException {
		
		try {
			copiarFitxers(
					new File(base + pathOrigen),
					new File(base + pathDesti));
		} catch (IOException e) {
			throw new ArxiuException(
					"Error copiant el document (origen=" + pathOrigen + " ,destí=" + pathDesti + ")");
		}
	}
	
	public byte[] getDocumentContingut(
			String path) throws ArxiuException {
		
		File file = new File(base + path);
		
		if (!file.exists() || file.isDirectory())
			throw new ArxiuException("El document de la ruta '" + path + "' no existeix a filesystem.");
		
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			throw new ArxiuException("Error llegint el contingut del document (path=" + path + ")");
		}
	}
	
	
	private void copiarCarpeta(
			File f1,
			File f2) throws IOException {
		
		if (f1.isDirectory()) {
			if (!f2.exists()) f2.mkdir();
				String[] fitxers = f1.list();
				for (int x=0; x < fitxers.length; x++) {
					copiarCarpeta(new File(f1, fitxers[x]),new File(f2,fitxers[x]));                           
				}
		} else copiarFitxers(f1, f2);
	}
	private void copiarFitxers(File f1, File f2) throws IOException {
			 
		InputStream in = new FileInputStream(f1);
		OutputStream out = new FileOutputStream(f2);
		 
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
		 
		in.close();
		out.close();
	}
	
	public static void deleteFolder(
			String path) {
		
		File folder = new File(path);
		
	    File[] files = folder.listFiles();
	    if(files!=null) {
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolderAux(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	}
	private static void deleteFolderAux(
			File folder) {
		
	    File[] files = folder.listFiles();
	    if(files!=null) {
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolderAux(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	

}
