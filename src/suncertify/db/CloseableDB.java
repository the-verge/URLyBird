package suncertify.db;

import java.io.IOException;

/**
 * This interface extends the supplied <code>DB</code>
 * interface in order to add a <code>close</code>
 * method to <code>Data</code>.  This method is used 
 * to close the database connection just before the
 * application exits.
 * @author john
 *
 */
public interface CloseableDB extends DB {
    
    /**
     * Closes the database connection.
     * @throws IOException if the connection
     *         cannot be closed.
     */
    public void closeDatabaseConnection() throws IOException;
    
}
