/**
 * 
 */
package es.caib.plugins.arxiu.caib.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;

/**
 * Test de la implementació de l'API de l'arxiu que utilitza
 * l'API REST de l'arxiu de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuPluginCaibInfoTest {

	private static IArxiuPlugin arxiuPlugin;

	@BeforeClass
	public static void setUp() throws IOException {
		Properties properties = new Properties();
		properties.load(
				ArxiuPluginCaibInfoTest.class.getClassLoader().getResourceAsStream(
						"es/caib/plugins/arxiu/caib/test.properties"));
		arxiuPlugin = new ArxiuPluginCaib(
				"",
				properties);
	}

	@Test
	public void documentDetalls() {
		String identificador = "87704fab-9f71-4eb7-9a5f-a8bc68c1308a";
		String versio = null;
		boolean ambContingut = false;
		Document document = arxiuPlugin.documentDetalls(
				identificador,
				versio,
				ambContingut);
		assertNotNull(document);
		printContingut(document);
	}

	private void printContingut(ContingutArxiu contingut) {
		System.out.println(">>> Contingut de l'arxiu:");
		System.out.println(">>>    tipus: " + contingut.getTipus());
		System.out.println(">>>    nom: " + contingut.getNom());
		System.out.println(">>>    versio: " + contingut.getVersio());
		if (contingut instanceof Document) {
			Document document = (Document)contingut;
			System.out.println(">>>    estat: " + document.getEstat());
			if (document.getFirmes() != null) {
				
			}
			if (document.getMetadades() != null) {
				DocumentMetadades metadades = document.getMetadades();
				System.out.println(">>>    NTI version: " + metadades.getVersioNti());
				System.out.println(">>>    NTI identificador: " + metadades.getIdentificador());
				if (metadades.getOrgans() != null) {
					System.out.println(">>>    NTI organs: " + Arrays.toString(metadades.getOrgans().toArray(new String[metadades.getOrgans().size()])));
				} else {
					System.out.println(">>>    NTI organs: <null>");
				}
				System.out.println(">>>    NTI dataCaptura: " + metadades.getDataCaptura());
				System.out.println(">>>    NTI origen: " + metadades.getOrigen());
				System.out.println(">>>    NTI estadoElaboracion: " + metadades.getEstatElaboracio());
				System.out.println(">>>    NTI nombreFormato: " + metadades.getFormat());
				System.out.println(">>>    NTI tipoDocumento: " + metadades.getTipusDocumental());
				System.out.println(">>>    NTI idDocumentoOrigen: " + metadades.getIdentificadorOrigen());
				System.out.println(">>>    format: " + metadades.getFormat());
				System.out.println(">>>    extensio: " + metadades.getExtensio());
			}
		}
		if (contingut.getFirmes() != null) {
			System.out.println(">>>    count(firmes): " + contingut.getFirmes().size());
			int firmaIndex = 0;
			for (Firma firma: contingut.getFirmes()) {
				System.out.println(">>>    Firma " + firmaIndex++);
				System.out.println(">>>        tipus: " + firma.getTipus());
				System.out.println(">>>        perfil: " + firma.getPerfil());
				System.out.println(">>>        tipusMime: " + firma.getTipusMime());
				if (FirmaTipus.CSV.equals(firma.getTipus())) {
					System.out.println(">>>        csv: " + new String(firma.getContingut()));
					System.out.println(">>>        csvRegulacio: " + firma.getCsvRegulacio());
				} else {
					if (firma.getContingut() != null) {
						System.out.println(">>>        contingut: " + firma.getContingut().length + " bytes");
					} else {
						System.out.println(">>>        contingut: <null>");
					}
				}
			}
		}
	}

}
