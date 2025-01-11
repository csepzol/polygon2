package eu.indiegeo.polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Csépe Zoltán
 * 
 * https://www.ngdc.noaa.gov/geomag-web/calculators/calculateDeclination?lat1=40&lon1=-105.25&model=IGRF&startYear=2009&startMonth=1&startDay=6&resultFormat=xml
 * https://www.ngdc.noaa.gov/geomag/calculators/help/declinationHelp.html
 *
 */
public class MagneticDeclination {
	
	private String date;
	private double lat;
	private double lon;
	private double declination;
	
	MagneticDeclination(String date, double lat, double lon) throws JSONException, IOException{
		this.date = date;
		this.lat = lat;
		this.lon = lon;
		String[] dateSplit = date.split("-");
		this.declination = getDeclinationFromURL(Double.toString(lat), Double.toString(lon), dateSplit[0], dateSplit[1], dateSplit[2]); 
	}
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	}

	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
		  BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      JSONObject json = new JSONObject(jsonText);
		      return json;
			} finally {
		      is.close();
		    }
	}
	
	public static double getDeclinationFromURL(String lat, String lon, String year, String month, String day) throws JSONException, IOException {
		double dec = 0.0;
		String uri =
			    "https://www.ngdc.noaa.gov/geomag-web/calculators/calculateDeclination?lat1="+ lat +
			    "&lon1=" + lon +"&model=IGRF&startYear=" + year + "&startMonth=" + month +"&startDay=" + day + "&resultFormat=json";
		JSONObject json = readJsonFromUrl(uri);
		JSONArray result=(JSONArray) json.get("result");
		dec = (double) ((JSONObject)result.get(0)).get("declination");
		return dec;
	}

	public double getDeclination() {
		return declination;
	} 

}
