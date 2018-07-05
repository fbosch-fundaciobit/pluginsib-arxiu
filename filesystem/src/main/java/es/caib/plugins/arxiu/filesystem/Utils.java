package es.caib.plugins.arxiu.filesystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ConsultaFiltre;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutTipus;

public class Utils {

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
	
	public static String formatDateIso8601(Date date) {
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		
		return df.format(date);
	}
	
	public static Date parseDateIso8601(String date) throws ArxiuException {
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		
		try {
			return df.parse(date);
		} catch (ParseException e) {
			throw new ArxiuException(
					"No s'ha pogut parsejar el valor per el camp Data (" +
					"valor=" + date + ")");
		}
	}

	public static ContingutArxiu crearContingutArxiu(
			String identificador, 
			String nom,
			ContingutTipus tipus,
			String versio) {
		ContingutArxiu informacioItem = new ContingutArxiu(tipus);
		informacioItem.setIdentificador(identificador);
		informacioItem.setNom(nom);
		return informacioItem;
	}

	private static enum Type {String, List, Date};
	public static String getQuery(List<ConsultaFiltre> filtres) throws ArxiuException {
		String query = "";
		for (int i = 0; i < filtres.size(); i++) {
			ConsultaFiltre filtre = filtres.get(i);
			Type type = metadataClass(filtre.getMetadada());
			switch(type) {
				case String:
					switch(filtre.getOperacio()) {
						case IGUAL:
							query = query + filtre.getMetadada() + ":\"" + filtre.getValorOperacio1() + "\"";
							break;
						case CONTE:
							query = query + filtre.getMetadada() + ":*" + filtre.getValorOperacio1() + "*";
							break;
						case MAJOR:
							query = query + filtre.getMetadada() + ":[" + filtre.getValorOperacio1() + " TO *]" +
									" -" + filtre.getMetadada() + ":\"" + filtre.getValorOperacio1() + "\"";
							break;
						case MENOR:
							query = query + filtre.getMetadada() + ":[* TO " + filtre.getValorOperacio1() + "]" +
									" -" + filtre.getMetadada() + ":\"" + filtre.getValorOperacio1() + "\"";
							break;
						case ENTRE:
							query = query + filtre.getMetadada() + ":[" + filtre.getValorOperacio1() + " TO " + filtre.getValorOperacio2() + "]";
							break;
						default:
							throw new ArxiuException("No s'ha definit un operador per la metadada " + filtre.getMetadada());
					}
					break;
				case List:
					switch(filtre.getOperacio()) {
						case IGUAL:
							throw new ArxiuException("L'operador " + filtre.getOperacio() + " no es aplicable al un tipus de dades " + type);
						case CONTE:
							query = query + filtre.getMetadada() + ":\"" + filtre.getValorOperacio1() + "\"";
							break;
						case MAJOR:
							throw new ArxiuException("L'operador " + filtre.getOperacio() + " no es aplicable al un tipus de dades " + type);
						case MENOR:
							throw new ArxiuException("L'operador " + filtre.getOperacio() + " no es aplicable al un tipus de dades " + type);
						case ENTRE:
							throw new ArxiuException("L'operador " + filtre.getOperacio() + " no es aplicable al un tipus de dades " + type);
						default:
							throw new ArxiuException("No s'ha definit un operador per la metadada " + filtre.getMetadada());
					}
					break;
				case Date:
					switch(filtre.getOperacio()) {
						case IGUAL:
							query = query + filtre.getMetadada() + ":\"" + filtre.getValorOperacio1() + "\"";
							break;
						case CONTE:
							throw new ArxiuException("L'operador " + filtre.getOperacio() + " no es aplicable al un tipus de dades " + type);
						case MAJOR:
							query = query + filtre.getMetadada() + ":[" + filtre.getValorOperacio1() + " TO *]" +
									" -" + filtre.getMetadada() + ":\"" + filtre.getValorOperacio1() + "\"";
							break;
						case MENOR:
							query = query + filtre.getMetadada() + ":[* TO " + filtre.getValorOperacio1() + "]" +
									" -" + filtre.getMetadada() + ":\"" + filtre.getValorOperacio1() + "\"";
							break;
						case ENTRE:
							query = query + filtre.getMetadada() + ":[" + filtre.getValorOperacio1() + " TO " + filtre.getValorOperacio2() + "]";
							break;
						default:
							throw new ArxiuException("No s'ha definit un operador per la metadada " + filtre.getMetadada());
					}
					break;
				default:
					throw new ArxiuException("La metadada " + filtre.getMetadada() + " no esta definida");
			}
			if(i < filtres.size()-1) query = query + " AND ";
		}
		return query;
	}

	private static Type metadataClass(String metadada) throws ArxiuException {
		if (Fields.EX_METADADESID.equals(metadada)) {
			return Type.String;
		} else if (Fields.EX_VERSIONTI.equals(metadada)) {
			return Type.String;
		} else if (Fields.EX_ORIGEN.equals(metadada)) {
			return Type.String;
		} else if (Fields.EX_ORGAN.equals(metadada)) {
			return Type.List;
		} else if (Fields.EX_DATA_OBERTURA.equals(metadada)) {
			return Type.Date;
		} else if (Fields.EX_CLASSIFICACIO.equals(metadada)) {
			return Type.String;
		} else if (Fields.EX_ESTAT.equals(metadada)) {
			return Type.String;
		} else if (Fields.EX_INTERESSAT.equals(metadada)) {
			return Type.List;
		} else if (Fields.EX_SERIE_DOCUMENTAL.equals(metadada)) {
			return Type.String;
		} else if (Fields.DOC_METADADESID.equals(metadada)) {
			return Type.String;
		} else if (Fields.DOC_VERSIONTI.equals(metadada)) {
			return Type.String;
		} else if (Fields.DOC_ORGAN.equals(metadada)) {
			return Type.List;
		} else if (Fields.DOC_DATA.equals(metadada)) {
			return Type.Date;
		} else if (Fields.DOC_ORIGEN.equals(metadada)) {
			return Type.String;
		} else if (Fields.DOC_ESTAT_ELABORACIO.equals(metadada)) {
			return Type.String;
		} else if (Fields.DOC_SERIE_DOCUMENTAL.equals(metadada)) {
			return Type.String;
		} else {
			return null;
		}
	}

}