package suncertify.application;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * This class handles writing configuration data to and 
 * retrieving configuration data from <code>suncertify.properties</code>.
 *
 * @author John Harding
 */
public class PropertiesAccessor {
	
    /**
     * The <code>Properties</code> instance used to read from / write to the 
     * properties file.
     */
    private static Properties properties = new Properties();
    
    /**
     * The <code>File</code> instance instantiated with <code>suncertify.properties</code>
     */
	private static File propertiesFile = new File("suncertify.properties");
	
	/**
	 * Retrieves configuration data from <code>suncertify.properties</code>
	 * @param mode the client <code>ApplicationMode</code> that represents
	 *        the mode the application is being executed in.
	 * @return a <code>Configuration</code> instance.
	 * @throws ConfigurationException if the configuration data cannot be
	 *         retrieved.
	 */
	public static Configuration getConfiguration(ApplicationMode mode) throws ConfigurationException {
	    Configuration config = null;
	    
	    if (mode == ApplicationMode.STANDALONE_CLIENT) {
	        String databaseLocation = readProperty("standalone.databaseLocation");
	        config = Configuration.standaloneClientConfig(databaseLocation);
	    }
	    else if (mode == ApplicationMode.NETWORK_CLIENT) {
	        String hostname = readProperty("networkClient.hostname");
	        String port = readProperty("networkClient.port");
	        config = Configuration.networkClientConfig(hostname, port);
	    }
	    else if (mode == ApplicationMode.SERVER) {
	        String databaseLocation = readProperty("server.databaseLocation");
            String port = readProperty("server.port");
            config = Configuration.serverConfig(databaseLocation, port);
	    }
	    return config;
    }
    
	/**
	 * Saves configuration data to <code>suncertify.properties</code>.
	 * @param config the <code>Configuration</code> instance that
	 *        wraps the application mode config data.
	 * @param mode the client <code>ApplicationMode</code> that 
	 *        represents the mode that the application is being run in.
	 * @throws ConfigurationException if the configuration data cannot 
	 *         be written to file.
	 */
    public static void saveConfiguration(Configuration config, ApplicationMode mode) throws ConfigurationException {
        if (mode == ApplicationMode.STANDALONE_CLIENT) {
            String databaseLocation = config.getDatabaseLocation();
            writeProperty("standalone.databaseLocation", databaseLocation);
        }
        else if(mode == ApplicationMode.NETWORK_CLIENT) {
            String hostname = config.getHostname();
            String port = config.getPort();
            writeProperty("networkClient.hostname", hostname);
            writeProperty("networkClient.port", port);
        }
        else if (mode == ApplicationMode.SERVER) {
            String databaseLocation = config.getDatabaseLocation();
            String port = config.getPort();
            writeProperty("server.databaseLocation", databaseLocation);
            writeProperty("server.port", port);
        }
    }
	
    /**
     * Reads a single property from file.
     * @param propertyName the property to be read from file.
     * @return the property of interest.
     * @throws ConfigurationException if the property cannot be retrieved.
     */
	private static String readProperty(String propertyName) throws ConfigurationException {
		String property = null;
		try (FileReader reader = new FileReader(propertiesFile)) {
			properties.load(reader);
			property = properties.getProperty(propertyName);
			if (property == null || property.equals("")) {
			    throw new ConfigurationException();
			}
		} catch (Exception e) {
            throw new ConfigurationException();
        } 
		return property;
	}
	
	/**
	 * Writes a single property to file.
	 * @param key the key of the property.
	 * @param value the value of the property.
	 * @throws ConfigurationException if the property cannot be written to file.
	 */
	private static void writeProperty(String key, String value) throws ConfigurationException {
		try (FileWriter writer = new FileWriter(propertiesFile)) {
		    properties.setProperty(key, value);
		    properties.store(writer, "Configuration updated");
		} catch (Exception e) {
            throw new ConfigurationException();
        } 
	}
	
}
