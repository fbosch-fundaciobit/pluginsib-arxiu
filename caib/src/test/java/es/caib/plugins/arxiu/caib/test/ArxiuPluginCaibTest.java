/**
 * 
 */
package es.caib.plugins.arxiu.caib.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import es.caib.arxiudigital.apirest.constantes.MetadatosDocumento;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ArxiuNotFoundException;
import es.caib.plugins.arxiu.api.Carpeta;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ConsultaOperacio;
import es.caib.plugins.arxiu.api.ConsultaResultat;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.ContingutTipus;
import es.caib.plugins.arxiu.api.Document;
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
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;

/**
 * Test de la implementació de l'API de l'arxiu que utilitza
 * l'API REST de l'arxiu de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuPluginCaibTest {

	private static final String SERIE_DOCUMENTAL = "S0001";

	private static List<String> organsTest;
	private static List<String> interessatsTest;

	private static IArxiuPlugin arxiuPlugin;

	@BeforeClass
	public static void setUp() throws IOException {
		Properties properties = new Properties();
		properties.load(
				ArxiuPluginCaibTest.class.getClassLoader().getResourceAsStream(
						"es/caib/plugins/arxiu/caib/test.properties"));
		arxiuPlugin = new ArxiuPluginCaib(
				"",
				properties);
		organsTest = new ArrayList<String>();
		organsTest.add("A04013511");
		interessatsTest = new ArrayList<String>();
		interessatsTest.add("12345678Z");
		interessatsTest.add("00000000T");
	}

	@Test
	public void expedientCicleDeVida() throws Exception {
		System.out.println("TEST: CICLE DE VIDA DELS EXPEDIENTS");
		final String nom = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nom);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		final Date dataObertura = new Date();
		metadades.setDataObertura(dataObertura);
		final String classificacio = "organo1_PRO_123456789";
		metadades.setClassificacio(classificacio);
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						System.out.println("1.- Obtenint l'expedient creat (id=" + expedientCreatId + ")... ");
						Expedient expedientDetalls = arxiuPlugin.expedientDetalls(
								expedientCreatId,
								null);
						assertNotNull(expedientDetalls);
						System.out.println("Ok");
						System.out.println("2.- Comprovant informació de l'expedient creat (id=" + expedientCreatId + ")... ");
						expedientPerCrear.setIdentificador(expedientCreatId);
						expedientComprovar(
								expedientPerCrear,
								expedientDetalls,
								true);
						System.out.println("Ok");
						System.out.println("3.- Modificant expedient creat (id=" + expedientCreatId + ")... ");
						Expedient expedientPerModificar = new Expedient();
						expedientPerModificar.setIdentificador(expedientCreatId);
						String nomModificat = expedientPerCrear.getNom() + "_MOD";
						expedientPerModificar.setNom(nomModificat);
						metadades.setClassificacio("organo2_PRO_123456789");
						expedientPerModificar.setMetadades(metadades);
						ContingutArxiu expedientModificat = arxiuPlugin.expedientModificar(
								expedientPerModificar);
						assertNotNull(expedientModificat);
						System.out.println("Ok");
						System.out.println("4.- Comprovant informació de l'expedient modificat (id=" + expedientCreatId + ")... ");
						Expedient expedientModificatDetalls = arxiuPlugin.expedientDetalls(
								expedientCreatId,
								null);
						assertNotNull(expedientModificatDetalls);
						expedientComprovar(
								expedientPerModificar,
								expedientModificatDetalls,
								true);
						System.out.println("Ok");
						System.out.println("5.- Esborrant expedient creat (id=" + expedientCreatId + ")... ");
						arxiuPlugin.expedientEsborrar(expedientCreatId);
						elementsCreats.remove(expedientCreat);
						System.out.println("Ok");
						System.out.println("6.- Obtenint expedient esborrat per verificar que no existeix (id=" + expedientCreatId + ")... ");
						try {
							arxiuPlugin.expedientDetalls(
									expedientCreat.getIdentificador(),
									null);
							fail("No s'hauria d'haver trobat l'expedient una vegada esborrat (id=" + expedientCreatId + ")");
						} catch (ArxiuNotFoundException ex) {
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear);
	}

	@Test
	public void expedientVersionat() throws Exception {
		System.out.println("TEST: VERSIONAT DELS EXPEDIENTS");
		final String nom = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nom);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		final Date dataObertura = new Date();
		metadades.setDataObertura(dataObertura);
		final String classificacio = "organo1_PRO_123456789";
		metadades.setClassificacio(classificacio);
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
		final Document documentPerCrear = new Document();
		documentPerCrear.setNom(nomDoc);
		documentPerCrear.setEstat(DocumentEstat.ESBORRANY);
		final DocumentMetadades documentMetadades = new DocumentMetadades();
		documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
		documentMetadades.setOrgans(organsTest);
		documentMetadades.setDataCaptura(new Date());
		documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
		documentMetadades.setFormat(DocumentFormat.OASIS12);
		documentMetadades.setExtensio(DocumentExtensio.ODT);
		documentPerCrear.setMetadades(documentMetadades);
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setContingut(
				IOUtils.toByteArray(
						getDocumentContingutEsborranyOdt()));
		documentContingut.setTipusMime("application/vnd.oasis.opendocument.text");
		documentPerCrear.setContingut(documentContingut);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						System.out.println("1.- Comprovant versions de l'expedient creat (id=" + expedientCreatId + ")... ");
						List<ContingutArxiu> versions1 = arxiuPlugin.expedientVersions(expedientCreatId);
						assertNotNull(versions1);
						assertEquals(1, versions1.size());
						System.out.println("Ok");
						System.out.println("2.- Modificant expedient creat (id=" + expedientCreatId + ")... ");
						Expedient expedientPerModificar = new Expedient();
						expedientPerModificar.setIdentificador(expedientCreatId);
						String nomModificat = expedientPerCrear.getNom() + "_MOD";
						expedientPerModificar.setNom(nomModificat);
						metadades.setClassificacio("organo2_PRO_123456789");
						expedientPerModificar.setMetadades(metadades);
						ContingutArxiu expedientModificat = arxiuPlugin.expedientModificar(
								expedientPerModificar);
						assertNotNull(expedientModificat);
						System.out.println("Ok");
						System.out.println("3.- Comprovant versions de l'expedient modificat (id=" + expedientCreatId + ")... ");
						List<ContingutArxiu> versions2 = arxiuPlugin.expedientVersions(expedientCreatId);
						assertNotNull(versions2);
						assertEquals(2, versions2.size());
						System.out.println("Ok");
						System.out.println("4.- Comprovant versió antiga de l'expedient (id=" + expedientCreatId + ")... ");
						Expedient expedientV1Detalls = arxiuPlugin.expedientDetalls(
								expedientCreatId,
								versions2.get(0).getVersio());
						assertNotNull(expedientV1Detalls);
						assertEquals(
								nom,
								expedientV1Detalls.getNom());
						assertNotNull(expedientV1Detalls.getMetadades());
						assertEquals(
								classificacio,
								expedientV1Detalls.getMetadades().getClassificacio());
						System.out.println("Ok");
						System.out.println("5.- Comprovant versions de l'expedient amb un nou document creat (id=" + expedientCreatId + ")... ");
						ContingutArxiu documentCreat = arxiuPlugin.documentCrear(
								documentPerCrear,
								expedientCreatId);
						assertNotNull(documentCreat);
						assertNotNull(documentCreat.getIdentificador());
						List<ContingutArxiu> versions3 = arxiuPlugin.expedientVersions(expedientCreatId);
						assertNotNull(versions3);
						assertEquals(2, versions3.size());
						System.out.println("Ok");
					}
				},
				expedientPerCrear);
	}

	@Test
	public void documentCicleDeVida() throws Exception {
		System.out.println("TEST: CICLE DE VIDA DELS DOCUMENTS");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
		final Document documentPerCrear = new Document();
		documentPerCrear.setNom(nomDoc);
		documentPerCrear.setEstat(DocumentEstat.ESBORRANY);
		final DocumentMetadades documentMetadades = new DocumentMetadades();
		documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
		documentMetadades.setOrgans(organsTest);
		documentMetadades.setDataCaptura(new Date());
		documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
		documentMetadades.setFormat(DocumentFormat.OASIS12);
		documentMetadades.setExtensio(DocumentExtensio.ODT);
		documentPerCrear.setMetadades(documentMetadades);
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setContingut(
				IOUtils.toByteArray(
						getDocumentContingutEsborranyOdt()));
		documentContingut.setTipusMime("application/vnd.oasis.opendocument.text");
		documentPerCrear.setContingut(documentContingut);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu documentCreat = elementsCreats.get(1);
						String documentCreatId = documentCreat.getIdentificador();
						System.out.println(
								"1.- Comprovant informació del document creat (" +
								"id=" + documentCreatId + ")... ");
						Document documentDetalls = arxiuPlugin.documentDetalls(
								documentCreatId,
								null,
								true);
						assertNotNull(documentDetalls.getMetadades());
						assertNotNull(documentDetalls.getMetadades().getIdentificador());
						documentComprovar(
								documentPerCrear,
								documentDetalls,
								false);
						System.out.println("Ok");
						System.out.println(
								"2.- Modificant document (" +
								"id=" + documentCreatId + ")... ");
						Document documentPerModificar = new Document();
						documentPerModificar.setIdentificador(documentCreatId);
						documentPerModificar.setNom(documentPerCrear.getNom() + "_MOD");
						documentPerModificar.setMetadades(documentMetadades);
						DocumentContingut contingutPerModificar = new DocumentContingut();
						contingutPerModificar.setContingut(
								IOUtils.toByteArray(
										getDocumentContingutEsborranyOdtModificat()));
						contingutPerModificar.setTipusMime("application/vnd.oasis.opendocument.text");
						documentPerModificar.setContingut(contingutPerModificar);
						ContingutArxiu documentModificat = arxiuPlugin.documentModificar(
								documentPerModificar);
						assertNotNull(documentModificat);
						assertEquals(documentCreatId, documentModificat.getIdentificador());
						System.out.println("Ok");
						System.out.println(
								"3.- Comprovant informació del document modificat (" +
								"id=" + documentCreatId + ")... ");
						Document documentModificatDetalls = arxiuPlugin.documentDetalls(
								documentCreatId,
								null,
								true);
						documentComprovar(
								documentPerModificar,
								documentModificatDetalls,
								true);
						System.out.println("Ok");
						System.out.println(
								"4.- Comprovant les versions del document (" +
								"nodeId=" + documentCreatId + ")... ");
						List<ContingutArxiu> versions = arxiuPlugin.documentVersions(documentCreatId);
						assertNotNull(versions);
						assertEquals(2, versions.size());
						ContingutArxiu versio0 = versions.get(0);
						System.out.println("v" + versio0.getVersio() + "... ");
						Document documentVersio0 = arxiuPlugin.documentDetalls(
								documentCreatId,
								versio0.getVersio(),
								true);
						documentComprovar(
								documentPerCrear,
								documentVersio0,
								false);
						ContingutArxiu versio1 = versions.get(1);
						System.out.println("v" + versio1.getVersio() + "... ");
						Document documentVersio1 = arxiuPlugin.documentDetalls(
								documentCreatId,
								versio1.getVersio(),
								true);
						documentComprovar(
								documentPerModificar,
								documentVersio1,
								false);
						System.out.println("Ok");
						System.out.println(
								"5.- Esborrant document creat (" +
								"id=" + documentCreatId + ")... ");
						arxiuPlugin.documentEsborrar(documentCreatId);
						elementsCreats.remove(documentCreat);
						System.out.println("Ok");
						System.out.println(
								"6.- Obtenint document esborrat per verificar que no existeix (" +
								"id=" + documentCreatId + ")... ");
						try {
							arxiuPlugin.documentDetalls(
									documentCreat.getIdentificador(),
									null,
									false);
							fail("No s'hauria d'haver trobat el document una vegada esborrat (id=" + documentCreatId + ")");
						} catch (ArxiuNotFoundException ex) {
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear,
				documentPerCrear);
	}

	@Test
	public void documentEsborranyDefinitiu() throws Exception {
		System.out.println("TEST: DOCUMENT ESBORRANY I DEFINITIU");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
		final Document documentPerCrear = new Document();
		documentPerCrear.setNom(nomDoc);
		documentPerCrear.setEstat(DocumentEstat.ESBORRANY);
		final DocumentMetadades documentMetadades = new DocumentMetadades();
		documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
		documentMetadades.setOrgans(organsTest);
		documentMetadades.setDataCaptura(new Date());
		documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
		documentMetadades.setFormat(DocumentFormat.OASIS12);
		documentMetadades.setExtensio(DocumentExtensio.ODT);
		documentPerCrear.setMetadades(documentMetadades);
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setContingut(
				IOUtils.toByteArray(
						getDocumentContingutEsborranyOdt()));
		documentContingut.setTipusMime("application/vnd.oasis.opendocument.text");
		documentPerCrear.setContingut(documentContingut);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						ContingutArxiu documentCreat = elementsCreats.get(1);
						String documentCreatId = documentCreat.getIdentificador();
						System.out.println(
								"1.- Comprovant que no es pot establir document com a definitiu sense firma (" +
								"id=" + documentCreatId + ")... ");
						Document documentPerModificar = new Document();
						documentPerModificar.setIdentificador(documentCreatId);
						try {
							arxiuPlugin.documentModificar(documentPerModificar);
							fail("No s'hauria de poder establir com a definitiu un document sense firma (id=" + documentCreatId + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
						System.out.println(
								"2.- Guardant firma de document i marcant com a definitiu (" +
								"id=" + documentCreatId + ")... ");
						Firma firmaPades = new Firma();
						firmaPades.setTipus(FirmaTipus.PADES);
						firmaPades.setPerfil(FirmaPerfil.EPES);
						firmaPades.setTipusMime("application/pdf");
						firmaPades.setContingut(
								IOUtils.toByteArray(
										getDocumentFirmaPdf()));
						documentPerModificar.setFirmes(Arrays.asList(firmaPades));
						DocumentMetadades documentMetadadesModificar = new DocumentMetadades();
						documentMetadadesModificar.setFormat(DocumentFormat.PDF);
						documentMetadadesModificar.setExtensio(DocumentExtensio.PDF);
						documentPerModificar.setMetadades(documentMetadadesModificar);
						documentPerModificar.setEstat(DocumentEstat.DEFINITIU);
						ContingutArxiu contingutModificat = arxiuPlugin.documentModificar(
								documentPerModificar);
						assertNotNull(contingutModificat);
						System.out.println("Ok");
						elementsCreats.remove(documentCreat);
						elementsCreats.remove(expedientCreat);
						System.out.println(
								"4.- Comprovant firmes del document (" +
								"id=" + documentCreatId + ")... ");
						Document documentFirmat = arxiuPlugin.documentDetalls(
								documentCreatId,
								null,
								true);
						documentPerModificar.setNom(documentPerCrear.getNom());
						documentPerModificar.setMetadades(null);
						documentFirmat.setMetadades(null);
						documentFirmat.setContingut(null);
						documentComprovar(
								documentPerModificar,
								documentFirmat,
								false);
						System.out.println("Ok");
						System.out.println(
								"5.- Comprovant que no es pot esborrar un document definitiu (" +
								"id=" + documentCreatId + ")... ");
						try {
							arxiuPlugin.documentEsborrar(documentCreatId);
							fail("No s'hauria de poder esborrar el document creat (id=" + documentCreatId + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
						System.out.println(
								"6.- Comprovant que no es pot esborrar un expedient amb documents definitius (" +
								"id=" + expedientCreatId + ")... ");
						try {
							arxiuPlugin.expedientEsborrar(expedientCreatId);
							fail("No s'hauria de poder esborrar l'expedient amb documents definitius (id=" + expedientCreatId + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear,
				documentPerCrear);
	}

	@Test
	public void documentModificarFinal() throws Exception {
		System.out.println("TEST: DOCUMENT ESBORRANY I DEFINITIU");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
		final Document documentPerCrear = new Document();
		documentPerCrear.setNom(nomDoc);
		documentPerCrear.setEstat(DocumentEstat.ESBORRANY);
		final DocumentMetadades documentMetadades = new DocumentMetadades();
		documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
		documentMetadades.setOrgans(organsTest);
		documentMetadades.setDataCaptura(new Date());
		documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
		documentMetadades.setFormat(DocumentFormat.OASIS12);
		documentMetadades.setExtensio(DocumentExtensio.ODT);
		documentPerCrear.setMetadades(documentMetadades);
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setContingut(
				IOUtils.toByteArray(
						getDocumentContingutEsborranyOdt()));
		documentContingut.setTipusMime("application/vnd.oasis.opendocument.text");
		documentPerCrear.setContingut(documentContingut);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						ContingutArxiu documentCreat = elementsCreats.get(1);
						String documentCreatId = documentCreat.getIdentificador();
						System.out.println(
								"1.- Guardant versió PDF (" +
								"id=" + documentCreatId + ")... ");
						Document documentPerModificarPdf = new Document();
						documentPerModificarPdf.setIdentificador(documentCreatId);
						DocumentContingut documentContingutPdf = new DocumentContingut();
						documentContingutPdf.setContingut(
								IOUtils.toByteArray(
										getDocumentContingutEsborranyPdf()));
						documentContingutPdf.setTipusMime("application/pdf");
						documentPerModificarPdf.setContingut(documentContingutPdf);
						DocumentMetadades documentMetadadesPdf = new DocumentMetadades();
						documentMetadadesPdf.setFormat(DocumentFormat.PDF);
						documentMetadadesPdf.setExtensio(DocumentExtensio.PDF);
						documentPerModificarPdf.setMetadades(documentMetadadesPdf);
						ContingutArxiu documentModificatPdf = arxiuPlugin.documentModificar(
								documentPerModificarPdf);
						assertNotNull(documentModificatPdf);
						System.out.println(
								"2.- Guardant firma i marcant com a definitiu (" +
								"id=" + documentCreatId + ")... ");
						Document documentPerModificarFinal = new Document();
						documentPerModificarFinal.setIdentificador(documentCreatId);
						Firma firmaPades = new Firma();
						firmaPades.setTipus(FirmaTipus.PADES);
						firmaPades.setPerfil(FirmaPerfil.EPES);
						firmaPades.setTipusMime("application/pdf");
						firmaPades.setContingut(
								IOUtils.toByteArray(
										getDocumentFirmaPdf()));
						documentPerModificarFinal.setFirmes(
								Arrays.asList(firmaPades));
						documentPerModificarFinal.setEstat(DocumentEstat.DEFINITIU);
						ContingutArxiu documentModificatFinal = arxiuPlugin.documentModificar(
								documentPerModificarFinal);
						assertNotNull(documentModificatFinal);
						System.out.println("Ok");
						elementsCreats.remove(documentCreat);
						elementsCreats.remove(expedientCreat);
						System.out.println(
								"3.- Comprovant que no es pot esborrar un document definitiu (" +
								"id=" + documentCreatId + ")... ");
						try {
							arxiuPlugin.documentEsborrar(documentCreatId);
							fail("No s'hauria de poder esborrar el document creat (id=" + documentCreatId + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
						System.out.println(
								"4.- Comprovant que no es pot esborrar un expedient amb documents definitius (" +
								"id=" + expedientCreatId + ")... ");
						try {
							arxiuPlugin.expedientEsborrar(expedientCreatId);
							fail("No s'hauria de poder esborrar l'expedient amb documents definitius (id=" + expedientCreatId + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear,
				documentPerCrear);
	}

	@Test
	public void documentFinalCrear() throws Exception {
		System.out.println("TEST: DOCUMENT DEFINITIU DE BON COMENÇAMENT");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						final Document documentPerCrear = new Document();
						String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
						documentPerCrear.setNom(nomDoc);
						final DocumentMetadades documentMetadades = new DocumentMetadades();
						documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
						documentMetadades.setOrgans(organsTest);
						documentMetadades.setDataCaptura(new Date());
						documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
						documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
						documentMetadades.setFormat(DocumentFormat.PDF);
						documentMetadades.setExtensio(DocumentExtensio.PDF);
						documentPerCrear.setMetadades(documentMetadades);
						documentPerCrear.setEstat(DocumentEstat.DEFINITIU);
						System.out.println(
								"1.- Comprovant que la creació del document definitiu a l'expedient sense informació de firma dona error (" +
								"id=" + expedientCreatId + ")... ");
						try {
							arxiuPlugin.documentCrear(
									documentPerCrear,
									expedientCreatId);
							fail("No s'hauria de poder crear un document definitiu sense informació de firma");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
						System.out.println(
								"2.- Creant document definitiu amb informació de firma (" +
								"id=" + expedientCreatId + ")... ");
						Firma firmaPades = new Firma();
						firmaPades.setTipus(FirmaTipus.PADES);
						firmaPades.setPerfil(FirmaPerfil.EPES);
						firmaPades.setTipusMime("application/pdf");
						firmaPades.setContingut(
								IOUtils.toByteArray(
										getDocumentFirmaPdf()));
						documentPerCrear.setFirmes(
								Arrays.asList(firmaPades));
						ContingutArxiu documentCreat = arxiuPlugin.documentCrear(
								documentPerCrear,
								expedientCreatId);
						assertNotNull(documentCreat);
						String documentCreatId = documentCreat.getIdentificador();
						System.out.println("Ok");
						elementsCreats.remove(documentCreat);
						elementsCreats.remove(expedientCreat);
						System.out.println(
								"3.- Comprovant que no es pot esborrar un document definitiu (" +
								"id=" + documentCreatId + ")... ");
						try {
							arxiuPlugin.documentEsborrar(documentCreatId);
							fail("No s'hauria de poder esborrar el document creat (id=" + documentCreatId + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
						System.out.println(
								"4.- Comprovant que no es pot esborrar un expedient amb documents definitius (" +
								"id=" + expedientCreatId + ")... ");
						try {
							arxiuPlugin.expedientEsborrar(expedientCreatId);
							fail("No s'hauria de poder esborrar l'expedient amb documents definitius (id=" + expedientCreatId + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear);
	}

	/*
	 * Encara no està clar com guardar a l'arxiu aquest tipus de documents
	 */
	public void documentFisic() throws Exception {
		System.out.println("TEST: DOCUMENT FISIC");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						final Document documentPerCrear = new Document();
						String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
						documentPerCrear.setNom(nomDoc);
						final DocumentMetadades documentMetadades = new DocumentMetadades();
						documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
						documentMetadades.setOrgans(organsTest);
						documentMetadades.setDataCaptura(new Date());
						documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
						documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
						documentMetadades.setFormat(DocumentFormat.TXT);
						documentMetadades.setExtensio(DocumentExtensio.TXT);
						DocumentContingut contingutFisic = new DocumentContingut();
						contingutFisic.setContingut(
								IOUtils.toByteArray(
										getDocumentContingutFisic()));
						contingutFisic.setTipusMime("text/plain");
						documentPerCrear.setContingut(contingutFisic);
						documentPerCrear.setMetadades(documentMetadades);
						documentPerCrear.setEstat(DocumentEstat.DEFINITIU);
						Firma firmaCades = new Firma();
						firmaCades.setTipus(FirmaTipus.CADES_DET);
						firmaCades.setPerfil(FirmaPerfil.BES);
						firmaCades.setContingut(
								IOUtils.toByteArray(
										getDocumentFirmaFisic()));
						documentPerCrear.setFirmes(
								Arrays.asList(firmaCades));
						System.out.println(
								"1.- Comprovant que la creació del document definitiu a l'expedient sense contingut (" +
								"id=" + expedientCreatId + ")... ");
						arxiuPlugin.documentCrear(
								documentPerCrear,
								expedientCreatId);
						System.out.println("Ok");
					}
				},
				expedientPerCrear);
	}

	/*@Test
	public void documentSerieDocumentalDiferent() throws Exception {
		System.out.println("TEST: DOCUMENT SÈRIE DOCUMENTAL DIFERENT");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						final Document documentPerCrear = new Document();
						String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
						documentPerCrear.setNom(nomDoc);
						final DocumentMetadades documentMetadades = new DocumentMetadades();
						documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
						documentMetadades.setOrgans(organsTest);
						documentMetadades.setDataCaptura(new Date());
						documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
						documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
						documentMetadades.setFormat(DocumentFormat.PDF);
						documentMetadades.setExtensio(DocumentExtensio.PDF);
						documentMetadades.setSerieDocumental("S0702");
						documentPerCrear.setMetadades(documentMetadades);
						documentPerCrear.setEstat(DocumentEstat.DEFINITIU);
						Firma firmaPades = new Firma();
						firmaPades.setTipus(FirmaTipus.PADES);
						firmaPades.setPerfil(FirmaPerfil.EPES);
						firmaPades.setTipusMime("application/pdf");
						firmaPades.setContingut(
								IOUtils.toByteArray(
										getDocumentContingutFirma()));
						documentPerCrear.setFirmes(
								Arrays.asList(firmaPades));
						System.out.println(
								"1.- Comprovant que la creació del document a l'expedient amb sèrie documental diferent dona error (" +
								"id=" + expedientCreatId + ")... ");
						try {
							arxiuPlugin.documentCrear(
									documentPerCrear,
									expedientCreatId);
							fail("No s'hauria de poder crear un document amb una sèrie documental (" + documentMetadades.getSerieDocumental() + ") diferent de la de l'expedient (" + metadades.getSerieDocumental() + ")");
						} catch (ArxiuException ex) {
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear);
	}*/

	@Test
	public void documentSerieDocumentalBuida() throws Exception {
		System.out.println("TEST: DOCUMENT SÈRIE DOCUMENTAL DIFERENT");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu expedientCreat = elementsCreats.get(0);
						String expedientCreatId = expedientCreat.getIdentificador();
						final Document documentPerCrear = new Document();
						String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
						documentPerCrear.setNom(nomDoc);
						final DocumentMetadades documentMetadades = new DocumentMetadades();
						documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
						documentMetadades.setOrgans(organsTest);
						documentMetadades.setDataCaptura(new Date());
						documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
						documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
						documentMetadades.setFormat(DocumentFormat.PDF);
						documentMetadades.setExtensio(DocumentExtensio.PDF);
						documentMetadades.addMetadadaAddicional(
								MetadatosDocumento.CODIGO_CLASIFICACION,
								SERIE_DOCUMENTAL);
						documentPerCrear.setMetadades(documentMetadades);
						documentPerCrear.setEstat(DocumentEstat.ESBORRANY);
						System.out.println(
								"1.- Comprovant que la creació del document a l'expedient sense sèrie documental (" +
								"id=" + expedientCreatId + ")... ");
						ContingutArxiu documentCreat = arxiuPlugin.documentCrear(
								documentPerCrear,
								expedientCreatId);
						assertNotNull(documentCreat);
						String documentCreatId = documentCreat.getIdentificador();
						System.out.println("Ok");
						System.out.println(
								"2.- Comprovant la sèrie documental del document creat (" +
								"id=" + documentCreatId + ")... ");
						Document documentDetalls = arxiuPlugin.documentDetalls(
								documentCreatId,
								null,
								true);
						assertNotNull(documentDetalls.getMetadades());
						assertNotNull(documentDetalls.getMetadades().getMetadadesAddicionals());
						Object serieDocumental = documentDetalls.getMetadades().getMetadadesAddicionals().get(
								MetadatosDocumento.CODIGO_CLASIFICACION);
						assertNotNull(serieDocumental);
						System.out.println("Ok (" + serieDocumental + ")");
					}
				},
				expedientPerCrear);
	}

	@Test
	public void documentConsultaAmbCsv() throws Exception {
		String csv = "5dbd524a1bb90bde8e98c105f3aa480f7ba1784dda504e7b119d7d0394f75d65";
		ConsultaFiltre filtreCsv = new ConsultaFiltre();
		filtreCsv.setMetadada("eni:csv");
		filtreCsv.setOperacio(ConsultaOperacio.IGUAL);
		filtreCsv.setValorOperacio1(csv);
		ConsultaResultat resultat = arxiuPlugin.documentConsulta(
				Arrays.asList(filtreCsv),
				0,
				1,
				null);
		assertNotNull(resultat);
		assertNotNull(resultat.getNumRetornat());
		assertNotNull(resultat.getResultats());
		assertEquals(new Integer(1), resultat.getNumRetornat());
		assertEquals(new Integer(0), resultat.getPaginaActual());
		assertEquals(new Integer(1), resultat.getNumPagines());
		assertEquals(new Integer(1), resultat.getNumRegistres());
		assertEquals(1, resultat.getResultats().size());
	}

	@Test
	public void documentVersioImprimible() throws Exception {
		System.out.println("TEST: DOCUMENT VERSIO IMPRIMIBLE");
		String uuid = "d742c85f-9acb-4a5c-9add-f8518a284f86";
		System.out.println(
				"1.- Generant versió imprimible (" +
						"id=" + uuid + ")... ");
		DocumentContingut contingut = arxiuPlugin.documentImprimible(uuid);
		assertTrue(contingut.getTamany() > 0);
		/*System.out.println(">>>    arxiuNom: " + contingut.getArxiuNom());
		System.out.println(">>>    tipusMime: " + contingut.getTipusMime());
		System.out.println(">>>    tamany: " + contingut.getTamany());*/
		System.out.println("Ok");
	}

	@Test
	public void carpetaCicleDeVida() throws Exception {
		System.out.println("TEST: CICLE DE VIDA DE LES CARPETES");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		String nomCar = "ARXIUAPI_prova_car_" + System.currentTimeMillis();
		final Carpeta carpetaPerCrear = new Carpeta();
		carpetaPerCrear.setNom(nomCar);
		String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
		final Document documentPerCrear = new Document();
		documentPerCrear.setNom(nomDoc);
		documentPerCrear.setEstat(DocumentEstat.ESBORRANY);
		final DocumentMetadades documentMetadades = new DocumentMetadades();
		documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
		documentMetadades.setOrgans(organsTest);
		documentMetadades.setDataCaptura(new Date());
		documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
		documentMetadades.setFormat(DocumentFormat.OASIS12);
		documentMetadades.setExtensio(DocumentExtensio.ODT);
		documentPerCrear.setMetadades(documentMetadades);
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setContingut(
				IOUtils.toByteArray(
						getDocumentContingutEsborranyOdt()));
		documentContingut.setTipusMime("application/vnd.oasis.opendocument.text");
		documentPerCrear.setContingut(documentContingut);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu carpetaCreada = elementsCreats.get(2);
						String carpetaCreadaId = carpetaCreada.getIdentificador();
						System.out.println(
								"1.- Comprovant informació de la carpeta creada (" +
								"id=" + carpetaCreadaId + ")... ");
						Carpeta carpetaDetalls = arxiuPlugin.carpetaDetalls(
								carpetaCreadaId);
						carpetaComprovar(
								carpetaPerCrear,
								carpetaDetalls,
								false);
						System.out.println("Ok");
						System.out.println(
								"2.- Modificant carpeta (" +
								"id=" + carpetaCreadaId + ")... ");
						Carpeta carpetaPerModificar = new Carpeta();
						carpetaPerModificar.setIdentificador(carpetaCreadaId);
						carpetaPerModificar.setNom(carpetaPerCrear.getNom() + "_MOD");
						ContingutArxiu carpetaModificada = arxiuPlugin.carpetaModificar(
								carpetaPerModificar);
						assertNotNull(carpetaModificada);
						assertEquals(carpetaCreadaId, carpetaModificada.getIdentificador());
						System.out.println("Ok");
						System.out.println(
								"3.- Comprovant informació de la carpeta modificada (" +
								"id=" + carpetaCreadaId + ")... ");
						Carpeta carpetaModificadaDetalls = arxiuPlugin.carpetaDetalls(
								carpetaCreadaId);
						carpetaComprovar(
								carpetaPerModificar,
								carpetaModificadaDetalls,
								true);
						System.out.println("Ok");
						System.out.println("5.- Esborrant carpeta creada (" +
								"id=" + carpetaCreadaId + ")... ");
						arxiuPlugin.carpetaEsborrar(carpetaCreadaId);
						elementsCreats.remove(carpetaCreada);
						System.out.println("Ok");
						System.out.println("6.- Obtenint carpeta esborrada per verificar que no existeix (" +
								"id=" + carpetaCreadaId + ")... ");
						try {
							arxiuPlugin.carpetaDetalls(
									carpetaCreada.getIdentificador());
							fail("No s'hauria d'haver trobat la carpeta una vegada esborrada (id=" + carpetaCreadaId + ")");
						} catch (ArxiuNotFoundException ex) {
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear,
				documentPerCrear,
				carpetaPerCrear);
	}

	@Test
	public void carpetaEsborrarAmbContingut() throws Exception {
		System.out.println("TEST: ESBORRAR CARPETA AMB CONTINGUTS");
		String nomExp = "ARXIUAPI_prova_exp_" + System.currentTimeMillis();
		final Expedient expedientPerCrear = new Expedient();
		expedientPerCrear.setNom(nomExp);
		final ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setOrgans(organsTest);
		metadades.setDataObertura(new Date());
		metadades.setClassificacio("organo1_PRO_123456789");
		metadades.setEstat(ExpedientEstat.OBERT);
		metadades.setInteressats(interessatsTest);
		metadades.setSerieDocumental(SERIE_DOCUMENTAL);
		expedientPerCrear.setMetadades(metadades);
		String nomCar = "ARXIUAPI_prova_car_" + System.currentTimeMillis();
		final Carpeta carpetaPerCrear = new Carpeta();
		carpetaPerCrear.setNom(nomCar);
		String nomDoc = "ARXIUAPI_prova_doc_" + System.currentTimeMillis();
		final Document documentPerCrear = new Document();
		documentPerCrear.setNom(nomDoc);
		documentPerCrear.setEstat(DocumentEstat.ESBORRANY);
		final DocumentMetadades documentMetadades = new DocumentMetadades();
		documentMetadades.setOrigen(ContingutOrigen.CIUTADA);
		documentMetadades.setOrgans(organsTest);
		documentMetadades.setDataCaptura(new Date());
		documentMetadades.setEstatElaboracio(DocumentEstatElaboracio.ORIGINAL);
		documentMetadades.setTipusDocumental(DocumentTipus.ALTRES);
		documentMetadades.setFormat(DocumentFormat.OASIS12);
		documentMetadades.setExtensio(DocumentExtensio.ODT);
		documentPerCrear.setMetadades(documentMetadades);
		DocumentContingut documentContingut = new DocumentContingut();
		documentContingut.setContingut(
				IOUtils.toByteArray(
						getDocumentContingutEsborranyOdt()));
		documentContingut.setTipusMime("application/vnd.oasis.opendocument.text");
		documentPerCrear.setContingut(documentContingut);
		testCreantElements(
				new TestAmbElementsCreats() {
					@Override
					public void executar(List<ContingutArxiu> elementsCreats) throws IOException {
						ContingutArxiu documentCreat = elementsCreats.get(1);
						String documentCreatId = documentCreat.getIdentificador();
						ContingutArxiu carpetaCreada = elementsCreats.get(2);
						String carpetaCreadaId = carpetaCreada.getIdentificador();
						System.out.println(
								"1.- Movent document a la carpeta creada (" +
								"documentId=" + documentCreatId + ", " +
								"carpetaId=" + carpetaCreadaId + ")... ");
						arxiuPlugin.documentMoure(
								documentCreatId,
								carpetaCreadaId);
						System.out.println("Ok");
						System.out.println(
								"2.- Comprovant que el document està a dins la carpeta (" +
								"documentId=" + documentCreatId + ", " +
								"carpetaId=" + carpetaCreadaId + ")... ");
						Carpeta carpetaDetalls = arxiuPlugin.carpetaDetalls(
								carpetaCreadaId);
						assertNotNull(carpetaDetalls.getContinguts());
						assertEquals(carpetaDetalls.getContinguts().size(), 1);
						ContingutArxiu contingutDocument = carpetaDetalls.getContinguts().get(0);
						assertEquals(documentCreatId, contingutDocument.getIdentificador());
						System.out.println("Ok");
						System.out.println(
								"3.- Esborrant carpeta (" +
								"id=" + carpetaCreadaId + ")... ");
						arxiuPlugin.carpetaEsborrar(carpetaCreadaId);
						elementsCreats.remove(carpetaCreada);
						System.out.println("Ok");
						System.out.println(
								"4.- Comprovant que el document ja no existeix (" +
								"id=" + documentCreatId + ")... ");
						try {
							arxiuPlugin.documentDetalls(
									documentCreatId,
									null,
									false);
							fail("No s'hauria d'haver trobat el document una vegada esborrada la carpeta(id=" + documentCreatId + ")");
						} catch (ArxiuNotFoundException ex) {
							elementsCreats.remove(documentCreat);
							System.out.println("Ok");
						}
					}
				},
				expedientPerCrear,
				documentPerCrear,
				carpetaPerCrear);
	}



	private void testCreantElements(
			TestAmbElementsCreats test,
			Object... elements) throws Exception {
		List<ContingutArxiu> elementsCreats = new ArrayList<ContingutArxiu>();
		String expedientId = null;
		boolean excepcioLlencada = false;
		boolean totBe = false;
		try {
			for (Object element: elements) {
				if (element instanceof Expedient) {
					System.out.println("I.- Creant expedient... ");
					ContingutArxiu expedientCreat = arxiuPlugin.expedientCrear(
							(Expedient)element);
					assertNotNull(expedientCreat);
					assertNotNull(expedientCreat.getIdentificador());
					expedientId = expedientCreat.getIdentificador();
					elementsCreats.add(expedientCreat);
					System.out.println("Ok");
				} else {
					if (expedientId != null) {
						if (element instanceof Document) {
							System.out.println("I.- Creant document... ");
							Document documentPerCrear = (Document)element;
							ContingutArxiu documentCreat = arxiuPlugin.documentCrear(
									documentPerCrear,
									expedientId);
							assertNotNull(documentCreat);
							assertNotNull(documentCreat.getIdentificador());
							elementsCreats.add(documentCreat);
							System.out.println("Ok");
						} else if (element instanceof Carpeta) {
							System.out.println("I.- Creant carpeta... ");
							ContingutArxiu carpetaCreada = arxiuPlugin.carpetaCrear(
									(Carpeta)element,
									expedientId);
							assertNotNull(carpetaCreada);
							assertNotNull(carpetaCreada.getIdentificador());
							elementsCreats.add(carpetaCreada);
							System.out.println("Ok");
						} else {
							throw new RuntimeException(
									"Tipus d'element desconegut: " + element.getClass().getName());
						}
					} else {
						throw new RuntimeException("No s'ha especificat cap expedient");
					}
				}
			}
			test.executar(elementsCreats);
			totBe = true;
		} catch (Exception ex) {
			excepcioLlencada = true;
			System.out.println("Error: " + ex.getLocalizedMessage());
			throw ex;
		} finally {
			if (!excepcioLlencada && !totBe) {
				System.out.println("Nok");
			}
			Collections.reverse(elementsCreats);
			for (ContingutArxiu element: elementsCreats) {
				if (ContingutTipus.EXPEDIENT.equals(element.getTipus())) {
					String identificador = element.getIdentificador();
					System.out.println("F.- Esborrant expedient creat (id=" + identificador + ")... ");
					try {
						arxiuPlugin.expedientEsborrar(identificador);
						System.out.println("Ok");
					} catch (Exception ex) {
						System.out.println("Error: " + ex.getLocalizedMessage());
						throw ex;
					}
					expedientId = null;
				} else if (ContingutTipus.DOCUMENT.equals(element.getTipus())) {
					String identificador = element.getIdentificador();
					System.out.println("F.- Esborrant document creat (id=" + identificador + ")... ");
					try {
						arxiuPlugin.documentEsborrar(identificador);
						System.out.println("Ok");
					} catch (Exception ex) {
						System.out.println("Error: " + ex.getLocalizedMessage());
						throw ex;
					}
				} else if (ContingutTipus.CARPETA.equals(element.getTipus())) {
					String identificador = element.getIdentificador();
					System.out.println("F.- Esborrant carpeta creada (id=" + identificador + ")... ");
					try {
						arxiuPlugin.carpetaEsborrar(identificador);
						System.out.println("Ok");
					} catch (Exception ex) {
						System.out.println("Error: " + ex.getLocalizedMessage());
						throw ex;
					}
				}
			}
		}
	}

	private void expedientComprovar(
			Expedient expedientEsperat,
			Expedient expedientPerComprovar,
			boolean comprovarVersio) {
		assertNotNull(expedientPerComprovar);
		if (comprovarVersio) {
			// Les versions antigues d'un document no tenen el mateix id
			// que el document original. L'id del document original apunta
			// sempre a la darrera versió del document.
			assertEquals(
					expedientEsperat.getIdentificador(),
					expedientPerComprovar.getIdentificador());
		}
		assertEquals(
				expedientEsperat.getNom(),
				expedientPerComprovar.getNom());
		if (expedientEsperat.getMetadades() != null) {
			assertNotNull(expedientPerComprovar.getMetadades());
			ExpedientMetadades metadades = expedientEsperat.getMetadades();
			assertEquals(
					metadades.getOrgans(),
					expedientPerComprovar.getMetadades().getOrgans());
			assertEquals(
					truncar(metadades.getDataObertura()),
					truncar(expedientPerComprovar.getMetadades().getDataObertura()));
			assertEquals(
					metadades.getClassificacio(),
					expedientPerComprovar.getMetadades().getClassificacio());
			assertEquals(
					metadades.getEstat(),
					expedientPerComprovar.getMetadades().getEstat());
			assertEquals(
					metadades.getInteressats(),
					expedientPerComprovar.getMetadades().getInteressats());
			assertEquals(
					metadades.getSerieDocumental(),
					expedientPerComprovar.getMetadades().getSerieDocumental());
		} else {
			assertNull(expedientPerComprovar.getMetadades());
		}
		if (expedientEsperat.getContinguts() != null) {
			assertNotNull(expedientPerComprovar.getContinguts());
		} else {
			assertNull(expedientPerComprovar.getContinguts());
		}
	}

	private void documentComprovar(
			Document documentEsperat,
			Document documentPerComprovar,
			boolean comprovarIdentificador) {
		assertNotNull(documentPerComprovar);
		if (comprovarIdentificador) {
			// Les versions antigues d'un document no tenen el mateix id
			// que el document original. L'id del document original apunta
			// sempre a la darrera versió del document.
			assertEquals(
					documentEsperat.getIdentificador(),
					documentPerComprovar.getIdentificador());
		}
		assertEquals(
				documentEsperat.getNom(),
				documentPerComprovar.getNom());
		if (documentEsperat.getMetadades() != null) {
			assertNotNull(documentPerComprovar.getMetadades());
			DocumentMetadades metadades = documentEsperat.getMetadades();
			assertEquals(
					metadades.getOrigen(),
					documentPerComprovar.getMetadades().getOrigen());
			assertEquals(
					metadades.getOrgans(),
					documentPerComprovar.getMetadades().getOrgans());
			assertEquals(
					metadades.getDataCaptura(),
					documentPerComprovar.getMetadades().getDataCaptura());
			assertEquals(
					metadades.getEstatElaboracio(),
					documentPerComprovar.getMetadades().getEstatElaboracio());
			assertEquals(
					metadades.getTipusDocumental(),
					documentPerComprovar.getMetadades().getTipusDocumental());
			assertEquals(
					metadades.getFormat(),
					documentPerComprovar.getMetadades().getFormat());
			assertEquals(
					metadades.getExtensio(),
					documentPerComprovar.getMetadades().getExtensio());
			assertEquals(
					metadades.getIdentificadorOrigen(),
					documentPerComprovar.getMetadades().getIdentificadorOrigen());
		} else {
			assertNull(documentPerComprovar.getMetadades());
		}
		if (documentEsperat.getContingut() != null) {
			assertNotNull(documentPerComprovar.getContingut());
			DocumentContingut contingut = documentEsperat.getContingut();
			assertEquals(
					contingut.getTipusMime(),
					documentPerComprovar.getContingut().getTipusMime());
			assertEquals(
					DigestUtils.sha1Hex(contingut.getContingut()),
					DigestUtils.sha1Hex(documentPerComprovar.getContingut().getContingut()));
		} else {
			assertNull(documentPerComprovar.getContingut());
		}
		if (documentEsperat.getFirmes() != null) {
			assertNotNull(documentPerComprovar.getFirmes());
			List<Firma> firmes = documentEsperat.getFirmes();
			assertEquals(
					firmes.size(),
					documentPerComprovar.getFirmes().size());
			for (int i = 0; i < firmes.size(); i++) {
				Firma firmaEsperada = firmes.get(i);
				Firma firmaPerComprovar = documentPerComprovar.getFirmes().get(i);
				assertEquals(
						firmaEsperada.getTipus(),
						firmaPerComprovar.getTipus());
				/*assertEquals(
						firmaEsperada.getPerfil(),
						firmaPerComprovar.getPerfil());
				assertEquals(
						firmaEsperada.getFitxerNom(),
						firmaPerComprovar.getFitxerNom());
				assertEquals(
						DigestUtils.sha1Hex(firmaEsperada.getContingut()),
						DigestUtils.sha1Hex(firmaPerComprovar.getContingut()));*/
				assertEquals(
						firmaEsperada.getTipusMime(),
						firmaPerComprovar.getTipusMime());
				assertEquals(
						firmaEsperada.getCsvRegulacio(),
						firmaPerComprovar.getCsvRegulacio());
			}
		} else {
			assertNull(documentPerComprovar.getFirmes());
		}
	}

	private void carpetaComprovar(
			Carpeta carpetaEsperada,
			Carpeta carpetaPerComprovar,
			boolean comprovarIdentificador) {
		assertNotNull(carpetaPerComprovar);
		if (comprovarIdentificador) {
			assertEquals(
					carpetaEsperada.getIdentificador(),
					carpetaPerComprovar.getIdentificador());
		}
		assertEquals(
				carpetaEsperada.getNom(),
				carpetaPerComprovar.getNom());
	}

	private Date truncar(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private InputStream getDocumentContingutEsborranyOdt() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/plugins/arxiu/caib/document_test.odt");
		return is;
	}
	private InputStream getDocumentContingutEsborranyOdtModificat() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/plugins/arxiu/caib/document_test_mod.odt");
		return is;
	}
	private InputStream getDocumentContingutEsborranyPdf() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/plugins/arxiu/caib/document_test.pdf");
		return is;
	}
	private InputStream getDocumentFirmaPdf() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/plugins/arxiu/caib/firma_test_epes.pdf");
		return is;
	}
	private InputStream getDocumentContingutFisic() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/plugins/arxiu/caib/document_test.txt");
		return is;
	}
	private InputStream getDocumentFirmaFisic() {
		InputStream is = getClass().getResourceAsStream(
        		"/es/caib/plugins/arxiu/caib/firma_test_cades.csig");
		return is;
	}

	private abstract class TestAmbElementsCreats {
		public abstract void executar(List<ContingutArxiu> elementsCreats) throws Exception;
	}

}
