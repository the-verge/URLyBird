package suncertify.connection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ConfigAccess {
	
	private static File propertiesFile = new File("suncertify.properties");
	
	public static String readProperty(String propertyName) {
		String property = null;
		try (FileReader reader = new FileReader(propertiesFile)) {
			Properties properties = new Properties();
			properties.load(reader);
			property = properties.getProperty(propertyName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return property;
	}
	
	public static void writeProperty(String key, String value) {
		try (FileWriter writer = new FileWriter(propertiesFile)) {
		    Properties properties = new Properties();
		    properties.setProperty(key, value);
		    properties.store(writer, key + " updated");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String port = ConfigAccess.readProperty("port");
		System.out.println(port);
	}
	
}
