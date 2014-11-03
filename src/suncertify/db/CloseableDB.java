package suncertify.db;

import java.io.IOException;

/**
 * This interface extends the supplied <code>DB</code>
 * interface in order to add a <code>closeDatabaseConnection</code>
 * method to <code>Data</code>.  This method is used 
 * to close the database connection just before the
 * application exits.
 *
 * @author John Harding
 */
public interface CloseableDB extends DB {
    
    /**
     * Closes the database connection.
     * @throws IOException if the connection
     *         cannot be closed.
     */
    public void closeDatabaseConnection() throws IOException;

    /**
     * Indicates whether implementing classes
     * have a local database connection.
     * @return boolean
     */
    public boolean hasLocalDatabaseConnection();
    
}
