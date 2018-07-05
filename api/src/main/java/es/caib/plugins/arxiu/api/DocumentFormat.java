/**
 * 
 */
package es.caib.plugins.arxiu.api;

/**
 * Possibles formats de document.
 *
 * GML: GML
 * WFS: WFS
 * WMS: WMS
 * GZIP: GZIP
 * ZIP: ZIP
 * AVI: AVI
 * MP4A: MPEG-4 MP4 media
 * CSV: Comma Separated Values
 * HTML: HTML
 * CSS: CSS
 * JPEG: JPEG
 * MHTML: MHTML
 * OASIS12: ISO/IEC 26300:2006 OASIS 1.2
 * SOXML: Strict Open XML
 * PDF: PDF
 * PDFA: PDF/A
 * PNG: PNG
 * RTF: RTF
 * SVG: SVG
 * TIFF: TIFF
 * TXT: TXT
 * XHTML: XHTML
 * MP3: MP3. MPEG-1 Audio Layer 3
 * OGG: OGG-Vorbis
 * MP4V: MPEG-4 MP4 vídeo
 * WEBM: WebM;
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum DocumentFormat {

	GML("GML"),
	WFS("WFS"),
	WMS("WMS"),
	GZIP("GZIP"),
	ZIP("ZIP"),
	AVI("AVI"),
	MP4A("MPEG-4 MP4 media"),
	CSV("Comma Separated Values"),
	HTML("HTML"),
	CSS("CSS"),
	JPEG("JPEG"),
	MHTML("MHTML"),
	OASIS12("ISO/IEC 26300:2006 OASIS 1.2"),
	SOXML("Strict Open XML"),
	PDF("PDF"),
	PDFA("PDF/A"),
	PNG("PNG"),
	RTF("RTF"),
	SVG("SVG"),
	TIFF("TIFF"),
	TXT("TXT"),
	XHTML("XHTML"),
	MP3("MP3. MPEG-1 Audio Layer 3"),
	OGG("OGG-Vorbis"),
	MP4V("MPEG-4 MP4 vídeo"),
	WEBM("WebM"),
	CSIG("csig"),
	XSIG("xsig"),
	XML("xml");

	private String str;

	DocumentFormat(String str) {
		this.str = str;
    }

	public static DocumentFormat toEnum(String str) {
		if (str != null) {
		    for (DocumentFormat valor: DocumentFormat.values()) {
		        if (valor.toString().equals(str)) {
		            return valor;
		        }
		    }
		}
	    return null;
	}

	@Override
	public String toString() {
		return str;
	}

}
