package es.caib.plugins.arxiu.filesystem.test;

public class GeneradorObjectes {
	
	/*public static ExpedientMetadades getExpedientMetadades(
			int i,
			boolean modificat) {
		
		String mod = modificat ? "_MOD" : "";
		
		return new ExpedientMetadades(
				"ex_id_metadades_" + i + mod,
				"ex_versioNti_" + i + mod,
				"ex_origen_" + i + mod,
				Arrays.asList("ex_organ_a_" + i + mod, "ex_organ_b_" + i + mod, "ex_organ_c_" + i + mod),
				new Date(i),
				"ex_classificacio_" + i + mod,
				"ex_estat_" + i + mod,
				Arrays.asList("ex_interess_a_" + i + mod, "ex_interess_b_" + i + mod, "ex_interess_c_" + i + mod),
				"ex_seriedoc_" + i + mod);
	}
	
	public static DocumentContingut getDocumentContingut(
			String path) throws ArxiuException {
		
		File file = new File(path);
		
		if (!file.exists() || file.isDirectory())
			throw new ArxiuException("El document de la ruta '" + path + "' no existeix al sistema de fitxers.");
		
		try {
			return new DocumentContingut(
					Files.readAllBytes(file.toPath()),
					FilenameUtils.getExtension(path),
					"");
		} catch (IOException e) {
			throw new ArxiuException("Error llegint el contingut del document (path=" + path + ")");
		}
	}
	
	public static DocumentMetadades getMetadadesDocument(
			int i,
			boolean modificat) {
		
		String mod = modificat ? "_MOD" : "";
		
		return new DocumentMetadades(
				"doc_id_metadades_" + i + mod,
				"doc_versioNti_" + i + mod,
				Arrays.asList("doc_organ_a_" + i + mod, "doc_organ_b_" + i + mod, "doc_organ_c_" + i + mod),
				new Date(i),
				"doc_origen_" + i + mod,
				"doc_estatElaboracio_" + i + mod,
				"doc_tipusDocumental_" + i + mod,
				"doc_serieDocumental_" + i + mod,
				null,
				null,
				null);
	}
	
	public static List<Firma> getDocumentFirmes(
			int i,
			String path1,
			String path2,
			String path3,
			boolean modificat) throws ArxiuException {
		
		try {
			String mod = modificat ? "_MOD" : "";
			
			List<Firma> firmes = new ArrayList<Firma>();
			
			File file1 = new File(path1);
			firmes.add(new Firma(
					Files.readAllBytes(file1.toPath()),
					FilenameUtils.getExtension(path1),
					"TF01_" + i + mod,
					"signatura1_" + i + mod,
					"CSV1_" + i + mod));
			File file2 = new File(path2);
			firmes.add(new Firma(
					Files.readAllBytes(file2.toPath()),
					FilenameUtils.getExtension(path2),
					"TF02_" + i + mod,
					"signatura2_" + i + mod,
					"CSV2_" + i + mod));
			File file3 = new File(path3);
			firmes.add(new Firma(
					Files.readAllBytes(file3.toPath()),
					FilenameUtils.getExtension(path3),
					"TF03_" + i + mod,
					"signatura3_" + i + mod,
					"CSV3_" + i + mod));
		
			return firmes;
			
		} catch (IOException e) {
			throw new ArxiuException("Error llegint el contingut un document de signatura.");
		}
	}*/
	
}
