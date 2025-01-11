package eu.indiegeo.polygon;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;

public class AppProperties {
	private String result = "";
	private InputStream inputStream;
	private Properties prop;
	
	AppProperties() {
		this.prop = new Properties();
		String propFileName = "config.properties";

		inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

		try {
			this.prop.load(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	public String getWorkingDirectory() {
		try { 
			// get the property value and print it out
			String wd = prop.getProperty("workingDirectory");
			result=wd;
			System.out.println(result);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	//TODO Meg kell csinalni hogy elmentse a munka konyvtart
	public void setWorkingDirectory(String wd) {
		System.out.println(wd);
		prop.setProperty("workingDirectory", wd);
		try {
			prop.store(new FileOutputStream("config.properties"), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
