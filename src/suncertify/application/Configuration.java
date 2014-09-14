package suncertify.application;

public class Configuration {
    
    private String databaseLocation;
    
    private String hostname;
    
    private String port;
    
    public Configuration(String databaseLocation) {
        this.databaseLocation = databaseLocation;
    }
    
    public Configuration(String hostname, String port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getDatabaseLocation() {
        return databaseLocation;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPort() {
        return port;
    }

}
