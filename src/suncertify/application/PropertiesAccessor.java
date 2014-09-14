package suncertify.application;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesAccessor {
	
    private static Properties properties = new Properties();

	private static File propertiesFile = new File("suncertify.properties");
	
	public static Configuration getConfiguration(ApplicationMode mode) throws ConfigurationException {
	    Configuration config = null;
	    
	    if (mode == ApplicationMode.STANDALONE_CLIENT) {
	        String databaseLocation = readProperty("local.databaseLocation");
	        config = new Configuration(databaseLocation);
	    }
	    else if (mode == ApplicationMode.NETWORK_CLIENT) {
	        String hostname = readProperty("server.hostname");
	        String port = readProperty("server.port");
	        config = new Configuration(hostname, port);
	    }
	    return config;
    }
    
    public static void saveConfiguration(Configuration config, ApplicationMode mode) throws ConfigurationException {
        if (mode == ApplicationMode.STANDALONE_CLIENT) {
            String databaseLocation = config.getDatabaseLocation();
            writeProperty("local.databaseLocation", databaseLocation);
        }
        else if(mode == ApplicationMode.NETWORK_CLIENT) {
            String hostname = config.getHostname();
            String port = config.getPort();
            writeProperty("server.hostname", hostname);
            writeProperty("server.port", port);
        }
    }
	
	private static String readProperty(String propertyName) throws ConfigurationException {
		String property = null;
		try (FileReader reader = new FileReader(propertiesFile)) {
			properties.load(reader);
			property = properties.getProperty(propertyName);
		} catch (IOException e) {
            throw new ConfigurationException();
        } 
		return property;
	}
	
	private static void writeProperty(String key, String value) throws ConfigurationException {
		try (FileWriter writer = new FileWriter(propertiesFile)) {
		    properties.setProperty(key, value);
		    properties.store(writer, "Configuration updated");
		} catch (IOException e) {
            throw new ConfigurationException();
        } 
	}
	
}
