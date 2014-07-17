package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

class DBAccessor {
    
    private final static int DATA_SECTION_OFFSET = 56;
    
    /**
     * The location of the database that will be used by all
     * instances of the DBAccessor class.  The first object created from
     * the DBAccessor class will initialize this variable, after which 
     * it will not change.
     */
    private static String databaseLocation;
    
    private Logger log = Logger.getLogger("DBAccessor.java");
    
    private RandomAccessFile database;
    
    private static int totalRecords;
    
    public DBAccessor(String dbLocation) {
        if (databaseLocation == null) {
            databaseLocation = dbLocation;
        }
        else if (dbLocation != databaseLocation) {
            log.logp(Level.WARNING, "DBAccessor.java", "Constructor", 
                    "Ignored database file path "
                  + "- database location already initialised");;
        }
        try {
            database = new RandomAccessFile(databaseLocation, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String[] read(int recNo) {
        // TODO Auto-generated method stub
        return null;
    }

    public void update(int recNo, String[] data, long lockCookie) {
        // TODO Auto-generated method stub
        
    }

    public void delete(int recNo, long lockCookie) {
        // TODO Auto-generated method stub
        
    }

    public int[] find(String[] criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    public int create(String[] data) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    private String[] readRecord(int recNo) throws IOException {
        final long position = recNo * Room.RECORD_LENGTH;
        final byte[] record = new byte[Room.RECORD_LENGTH];
        
        synchronized(database) {
            database.seek(position);
            database.readFully(record);
        }
        return null;
    }

}
