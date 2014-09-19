package suncertify.application;

/**
 * Instances of this class are transfer or value objects
 * used to convey client configuration data.
 * @author john
 *
 */
public class Configuration {
    
    /**
     * The location of the database - used when in non-networked client mode.
     */
    private String databaseLocation;
    
    /**
     * The hostname / ip address of the server - used in networked client mode.
     */
    private String hostname;
    
    /**
     * The port on which a server application is listening for requests.
     */
    private String port;
    
    /**
     * Constructor used when creating a <code>Configuration</code>
     * for a local database connection - only database location is
     * required.
     * @param databaseLocation the path to the database file.
     */
    public Configuration(String databaseLocation) {
        this.databaseLocation = databaseLocation;
    }
    
    /**
     * Constructor used when creating a <code>Configuration</code>
     * for a remote database connection - hostname and port
     * required.
     * @param hostname the name / ip address of the
     *        machine on which the database server is located.
     * @param port the port that the server application
     *        is running on.
     */
    public Configuration(String hostname, String port) {
        this.hostname = hostname;
        this.port = port;
    }
    /**
     * Getter.
     * @return the location of the local database.
     */
    public String getDatabaseLocation() {
        return databaseLocation;
    }
    
    /**
     * Getter.
     * @return the hostname / ip address of 
     * the machine that the database server 
     * is running on.
     */
    public String getHostname() {
        return hostname;
    }
    
    /**
     * Getter.
     * @return port on which a server application
     * is listening for requests.
     */
    public String getPort() {
        return port;
    }

}
