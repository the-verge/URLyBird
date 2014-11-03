package suncertify.application;

/**
 * Instances of this class are transfer or value objects
 * used to convey configuration data. Three variables,
 * databaseLocation, hostname and port are sufficient to
 * encapsulate configuration data for all three application
 * modes.
 *
 * @author John Harding
 */
public class Configuration {
    
    /**
     * The location of the database.
     */
    private String databaseLocation;
    
    /**
     * The hostname / ip address of the server. 
     * Used in networked client mode.
     */
    private String hostname;
    
    /**
     * The port in use.
     */
    private String port;
    
    /**
     * Class constructor is private in order to restrict
     * how <code>Configuration</code> is instantiated.
     */
    private Configuration() {
        
    }
    
    /**
     * Constructs a <code>Configuration</code> object that encapsulates
     * local client configuration data.
     * @param databaseLocation the location of the database.
     * @return Configuration.
     */
    public static Configuration standaloneClientConfig(String databaseLocation) {
        Configuration config = new Configuration();
        config.setDatabaseLocation(databaseLocation);
        return config;
    }
    
    /**
     * Constructs a <code>Configuration</code> object that encapsulates
     * network client configuration data.
     * @param hostname the hostname of the server that the network
     *        client was connected to.
     * @param port the port in use for the network client connection.
     * @return Configuration
     */
    public static Configuration networkClientConfig(String hostname, String port) {
        Configuration config = new Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        return config;
    }
    
    /**
     * Constructs a <code>Configuration</code> object that encapsulates
     * server configuration data.
     * @param databaseLocation the location of the database.
     * @param port the port that the server was listening for
     *        connections on.
     * @return Configuration
     */
    public static Configuration serverConfig(String databaseLocation, String port) {
        Configuration config = new Configuration();
        config.setDatabaseLocation(databaseLocation);
        config.setPort(port);
        return config;
    }
    
    /**
     * Getter
     * @return databaseLocation.
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }
    
    /**
     * Setter. Only used in static methods above.
     * @param databaseLocation the database location.
     */
    private void setDatabaseLocation(String databaseLocation) {
        this.databaseLocation = databaseLocation;
    }
    
    /**
     * Getter.
     * @return hostname.
     */
    public String getHostname() {
        return hostname;
    }
    
    /**
     * Setter. Only used in static methods above.
     * @param hostname the hostname.
     */
    private void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    /**
     * Getter.
     * @return port.
     */
    public String getPort() {
        return port;
    }
    
    /**
     * Setter. Only used in static methods above.
     * @param port the port number.
     */
    private void setPort(String port) {
        this.port = port;
    }
    
    

}
