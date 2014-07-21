package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
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
    
    private final static int[] fieldLengths = {Room.NAME_LENGTH, Room.LOCATION_LENGTH, Room.SIZE_LENGTH, Room.SMOKING_LENGTH, Room.RATE_LENGTH, Room.DATE_LENGTH, Room.OWNER_LENGTH};
    
    private static int totalRecords;
    
    private RandomAccessFile database;
    
    private Logger log = Logger.getLogger("DBAccessor.java");
    
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

    public String[] readRecord(int recNo) {
        log.entering("DBAccessor.java", "readRecord", recNo);
        final long position = DATA_SECTION_OFFSET + ((recNo - 1) * Room.RECORD_LENGTH);
        final byte[] record = new byte[Room.RECORD_LENGTH];
        
        synchronized(database) {
            try {
                database.seek(position);
                database.readFully(record);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String[] result = {};
        result = this.recordToStringArray(record);
        
        log.exiting("DBAccessor.java", "readRecord", result);
        
        return result;
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
        byte[] record = this.stringArrayToRecord(data);
        final long position = DATA_SECTION_OFFSET + (30 * Room.RECORD_LENGTH);
        synchronized (database) {
            try {
                database.seek(position);
                database.write(record);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    private String[] recordToStringArray(byte[] record) {
        Byte validRecordByte = record[0];
        int validRecord = validRecordByte.intValue();
        
        int offset = Room.VALID_RECORD_LENGTH;
        String[] result = new String[fieldLengths.length];
        
        for(int i = 0; i < fieldLengths.length; i++) {
            int fieldLength = fieldLengths[i];
            String field = "";
            try {
                field = new String(record, offset, fieldLength, "US-ASCII").trim();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            result[i] = field;
            offset += fieldLength;
        }
        return result;
    }
    
    public byte[] stringArrayToRecord(String[] fields) {
        byte[] emptyRecordByteArray = new byte[Room.RECORD_LENGTH];
        String emptyRecordString = new String(emptyRecordByteArray);
        StringBuilder builder = new StringBuilder(emptyRecordString);
        int startPosition = 1;
        
        for(int i = 0; i < fieldLengths.length; i++) {
            int endPosition = startPosition + fields[i].length();
            int fieldLength = fieldLengths[i];
            builder.replace(startPosition, endPosition, fields[i]);
            startPosition += fieldLength;
        }
        System.out.println(builder.toString());
        byte[] record = builder.toString().getBytes();
        
        return record;
    }
    
    

}
