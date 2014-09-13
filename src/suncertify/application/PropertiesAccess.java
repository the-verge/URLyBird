package suncertify.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesAccess {
	
    private static Properties properties = new Properties();

	private static File propertiesFile = new File("suncertify.properties");
	
	public static Configuration getConfiguration() {
        String databaseLocation = readProperty("databaseLocation");
        String hostname = readProperty("hostname");
        String port = readProperty("port");
        
        return new Configuration(databaseLocation, hostname, port);
    }
    
    public static void saveConfiguration(Configuration config) {
        String databaseLocation = config.getDatabaseLocation();
        String hostname = config.getHostname();
        String port = config.getPort();
        
        writeProperty("databaseLocation", databaseLocation);
        writeProperty("hostname", hostname);
        writeProperty("port", port);
    }
	
	private static String readProperty(String propertyName) {
		String property = null;
		try (FileReader reader = new FileReader(propertiesFile)) {
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
	
	private static void writeProperty(String key, String value) {
		try (FileWriter writer = new FileWriter(propertiesFile)) {
		    properties.setProperty(key, value);
		    properties.store(writer, "Configuration updated");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
