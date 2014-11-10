package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import suncertify.application.Utils;

/**
 * The <code>DBAccessor</code> class provides direct access to the underlying
 * database file. It uses a RandomAccessFile instance to read from and write
 * to the file. The constructor and public methods of this class
 * (excluding getters) throw <code>DBException</code> which extends
 * <code>RunTimeException</code>. This exception wraps checked
 * Exceptions such as <code>FileNotFoundException</code>, <code>IOException</code>
 * and <code>UnsupportedEncodingException</code> in order to deal with these
 * while still maintaining <code>Data</code> compliance with the supplied DB.java
 * interface.
 * 
 * @author John Harding
 */
public class DBAccessor {
    
    /**
     * The number of fields in a record.
     */
    private static final int NUMBER_OF_FIELDS_IN_RECORD = 7;
    
    /**
     * The length in bytes of the name field.
     */
    private static final int NAME_LENGTH = 64;
    
    /**
     * The length in bytes of the location field.
     */
    private static final int LOCATION_LENGTH = 64;
    
    /**
     * The length in bytes of the size field.
     */
    private static final int SIZE_LENGTH = 4;
    
    /**
     * The length in bytes of the smoking field.
     */
    private static final int SMOKING_LENGTH = 1;
    
    /**
     * The length in bytes of the rate field.
     */
    private static final int RATE_LENGTH = 8;
    
    /**
     * The length in bytes of the date field.
     */
    private static final int DATE_LENGTH = 10;
    
    /**
     * The length in bytes of the owner field.
     */
    private static final int OWNER_LENGTH = 8;
    
    /**
     * The length in bytes of the whole record,
     * including the deleted flag.
     */
    private static final int RECORD_LENGTH = 160;
    
    /**
     * The number of bytes in a record before the data
     * section begins (the first byte contains the deleted
     * record flag).
     */
    private static final int RECORD_DATA_SECTION_OFFSET = 1;
    
    /**
     * The number of bytes in the database file before the data section begins.
     */
    private static final int FILE_DATA_SECTION_OFFSET = 56;
    
    /**
     * The value that denotes a deleted
     * record in the database file.
     */
    private static final byte DELETED_FLAG = (byte) 0xFF;
    
    /**
     * An array that holds the lengths of the data fields of a 
     * record in the database file.
     */
    private static final int[] FIELD_LENGTHS_ARRAY = {NAME_LENGTH, LOCATION_LENGTH, 
        SIZE_LENGTH, SMOKING_LENGTH, RATE_LENGTH, DATE_LENGTH, OWNER_LENGTH};
    
    /**
     * The location of the database that will be used by all
     * instances of the DBAccessor class.  The first object created from
     * the DBAccessor class will initialize this variable, after which 
     * it will not change.
     */
    private static String databaseLocation;
    
    /**
     * The RandomAccessfile instance <code>database</code> 
     * is used to directly read from and write to the 
     * database file.
     */
    private RandomAccessFile database = null;
    
    /**
     * Logger instance for DBAccessor.java.
     */
    private Logger log = Logger.getLogger(DBAccessor.class.getName());
    
    /**
     * Class constructor. All instances of the class share access
     * to the same database file.
     * @param dbLocation the path to the database file.
     * @throws DBException if the <code>RandomAccessFile<code>
     *         instance cannot be created.
     */
    public DBAccessor(String dbLocation) {
        Utils.setLogLevel(log, Level.FINER);
        if (database == null) {
            try {
                File file = new File(dbLocation);
                /**
                 * Don't want RandomAccessFile to try to create
                 * the file on disk if it doesn't exist.
                 */
                if (file.exists() && !file.isDirectory()) {
                    database = new RandomAccessFile(file, "rw");
                }
                else {
                    throw new DBException("Could not open " + dbLocation);
                }
            } catch (FileNotFoundException e) {
                log.throwing("DBAccessor.java", "Constructor", e);
                throw new DBException("Could not open " + dbLocation, e);
            }
            databaseLocation = dbLocation;
        }
        else if (dbLocation != databaseLocation) {
            log.warning("Ignored database location " + dbLocation +
                    " database already initialised");
        }
    }
    
    /**
     * Reads a record from the database file.
     * @param recNo the record number to retrieve.
     * @return a <code>String</code> array containing
     * 		   the fields of the record.
     * @throws RecordNotFoundException if the record doesn't
     * 		   exist in the database.
     * @throws DBException if an IOException is thrown when 
     * 		   trying to read from the database file.
     */
    public String[] readRecord(int recNo) throws RecordNotFoundException {
        final long position = calculateFilePosition(recNo);
        byte[] record;
        String[] result;
        
        try {
            if (!recordExists(recNo)) {
                log.warning("Record number " + recNo + " does not exist");
                throw new RecordNotFoundException("Record number " + recNo + " does not exist");
            }
            else {
                record = retrieveRecord(position);
                result = recordToStringArray(record); 
            }
        } catch (UnsupportedEncodingException e) {
            log.throwing("DBAccessor.java", "readRecord", e);
            throw new DBException("Could not decode data", e);
        } catch (IOException e) {
            log.throwing("DBAccessor.java", "readRecord", e);
            throw new DBException("Could not retrieve record", e);
        }
        
        return result;
    }
    
    /**
     * Retrieves a record from a specified position
     * in the file.
     * @param position the position of the record in the file.
     * @return <code>byte[]</code> the bytes that constitute
     * 		   the record.
     * @throws IOException if there is an error reading from 
     * 		   the file.
     */
    private byte[] retrieveRecord(long position) throws IOException {
    	final byte[] record = new byte[RECORD_LENGTH];
        
        synchronized (database) {
            database.seek(position);
            database.readFully(record);
        }
        
        return record;
    }
    
    /**
     * Updates a record.
     * @param recNo the number of the record to update.
     * @param data <code>String</code> array containing the
     * 		   fields of the record to update.
     * @throws DBException if an IOException is thrown when
     * 		   trying to write to the database file.
     */
    public void updateRecord(int recNo, String[] data) {
        log.info("Updating record number " + recNo);
        final long position = calculateFilePosition(recNo);
        byte[] record = stringArrayToRecord(data);
        
        synchronized (database) {
            try {
                database.seek(position);
                database.write(record);
            } catch (IOException e) {
                log.throwing("DBAccessor.java", "updateRecord", e);
                throw new DBException("Could not update record", e);
            }
        }
    }
    
    /**
     * Flags a record as being deleted by assigning the
     * first bye of the record a value of 0xFF.
     * @param recNo the number of the record to delete.
     * @throws DBException if an IOException is thrown 
     * 		   when attempting to write to the database file.
     */
    public void deleteRecord(int recNo) {
        long position = calculateFilePosition(recNo);
        synchronized (database) {
            try {
                database.seek(position);
                database.writeByte(DELETED_FLAG);
            } catch (IOException e) {
                log.throwing("DBAccessor.java", "deleteRecord", e);
                throw new DBException("Could not delete record", e);
            }
        }
        log.info("Deleted record number " + recNo);
    }
    
    
    /**
     * Searches the database file for records whose fields match
     * the supplied criteria. 
     * @param criteria the criteria for which to search.
     * @return <code>int[]</code> the record numbers that match the
     *         criteria.
     * @throws DBException if an IOException is thrown 
     *         when attempting to read from the database file.
     */
    public int[] find(String[] criteria) {
        ArrayList<Integer> matches = new ArrayList<Integer>();
        long filePosition = FILE_DATA_SECTION_OFFSET;
        
        try {
			while (filePosition < database.length()) {
			    byte[] record = retrieveRecord(filePosition);
			    if (isDeletedRecord(record)) {
			        log.info("Found deleted record at position " + filePosition);
		            filePosition += RECORD_LENGTH;
			        continue;
			    }
			    else {
			        String[] data = recordToStringArray(record);
			        if (matchRecord(data, criteria)) {
			            int recNo = calculateRecordNumber(filePosition);
			            matches.add(recNo);
			        }
		            filePosition += RECORD_LENGTH;
			    }
			}
		} catch (IOException e) {
		    log.throwing("DBAccessor.java", "find", e);
			throw new DBException("Could not retrieve records", e);
		}
        int[] result = arrayListToArray(matches);
        log.info("Matched " + result.length + " records");
        return result;
    }
    
    /**
     * Determines whether a record is markes as deleted.
     * @param record the byte array that constitutes the record.
     * @return boolean
     */
    private boolean isDeletedRecord(byte[] record) {
        return record[0] == DELETED_FLAG;
    }
    
    /**
     * Attempts to match a record's fields with supplied criteria.
     * If the criteria String matches the beginning of the record field, 
     * a match in that field is recorded.  For a record to match the criteria, 
     * criteria[n] must match the beginning of data[n] 
     * for all non-null criteria[n].
     * @param data array containing the fields of a record.
     * @param criteria array containing the query criteria
     * 		  for each element in the record field array.  An attempt is made
     * 		  to match the beginning of <code>data[n]</code> 
     * 		  with <code>criteria[n]</code>.
     * @return <code>boolean</code> indicating a match or not.
     */
    private boolean matchRecord(String[] data, String[] criteria) {
        
        int nullCriteria = 0;
        int matches = 0;
        
        for (int i = 0; i < criteria.length; i++) {
            String query = criteria[i];
            String field = data[i];
            if (query == null) {
                nullCriteria++;
                continue;
            }
            else {
                String escapedQuery = Pattern.quote(query);
                // Pattern.CASE_INSENSITIVE assumes US-ASCII
                Pattern pattern = Pattern.compile(escapedQuery, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(field);
                if (matcher.lookingAt()) {
                    matches++;
                }
            }
        }
        
        int fieldsToMatch = NUMBER_OF_FIELDS_IN_RECORD - nullCriteria;
        
        if (matches == fieldsToMatch && nullCriteria != NUMBER_OF_FIELDS_IN_RECORD) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Converts an <code>ArrayList<Integer></code> to an 
     * <code>int</code> array.
     * @param list the ArrayList to convert.
     * @return an <code>int</code> array.
     */
    private int[] arrayListToArray(ArrayList<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }
    
    /**
     * Creates a new record.
     * @param data <code>String</code> array containing the 
     *        fields of the record to be created.
     * @return the number of the newly created record.
     * @throws DBException if an IOException is thrown 
     * 			when attempting to write to the database file.
     */
    public int createRecord(String[] data) {
        byte[] record = stringArrayToRecord(data);
        int recordNumber = 0;
        synchronized (database) {
            try {
                long position = firstAvailablePosition();
                recordNumber = calculateRecordNumber(position);
                database.seek(position);
                database.write(record);
            } catch (IOException e) {
                log.throwing("DBAccessor.java", "createRecord", e);
                throw new DBException("Could not create record", e);
            }
        }
        log.info("Created new record with number: " + recordNumber);
        return recordNumber;
    }
    
    /**
     * Finds the first available position to write
     * to in the file.  Accomplished by reading the 
     * first byte of the records in the file 
     * to determine whether they are marked as deleted.  
     * If a deleted record is found, it's position in the file
     * is returned.  If no deleted record is found,
     * the position of the end of the file is
     * returned.
     * @return the first available position in which
     *         a record can be written.
     * @throws IOException
     */
    private long firstAvailablePosition() throws IOException {
        long filePosition = FILE_DATA_SECTION_OFFSET;
        while (filePosition < database.length()) {
            database.seek(filePosition);
            byte validRecord = database.readByte();
            if (validRecord == DELETED_FLAG) {
                return filePosition;
            }
            filePosition += RECORD_LENGTH;
        }
        return database.length();
    }
    
    /**
     * Creates a <code>String</code> array from a <code>byte</code>
     * array read from the database file.
     * @param record <code>byte</code> array read from database file.
     * @return <code>String</code> array containing the fields of
     * 		   the record.
     * @throws UnsupportedEncodingException 
     */
    private String[] recordToStringArray(byte[] record) throws UnsupportedEncodingException {
        
        int offset = RECORD_DATA_SECTION_OFFSET;
        String[] data = new String[FIELD_LENGTHS_ARRAY.length];
        String field;
        
        for (int i = 0; i < FIELD_LENGTHS_ARRAY.length; i++) {
            int fieldLength = FIELD_LENGTHS_ARRAY[i];
            // POSSIBLE NPE
            field = new String(record, offset, fieldLength, "US-ASCII").trim();
            data[i] = field;
            offset += fieldLength;
        }
        return data;
    }
    
    /**
     * Converts a <code>String</code> array containing record fields
     * to a <code>byte</code> array that can be written to file.
     * @param data <code>String</code> array containing the 
     * 		  fields of the record to be written to file.
     * @return a <code>byte</code> array that can be written to file.
     */
    private byte[] stringArrayToRecord(String[] data) {
        byte[] emptyRecordByteArray = new byte[RECORD_LENGTH];
        String emptyRecordString = new String(emptyRecordByteArray);
        StringBuilder builder = new StringBuilder(emptyRecordString);
        
        int startPosition = RECORD_DATA_SECTION_OFFSET;
        // 1st byte will automatically be 0 and therefore be a valid record
        
        for (int i = 0; i < FIELD_LENGTHS_ARRAY.length; i++) {
            String field = data[i];
            if (field != null) {
                int endPosition = startPosition + field.length();
                builder.replace(startPosition, endPosition, field);
            }
            int maxFieldLength = FIELD_LENGTHS_ARRAY[i];
            startPosition += maxFieldLength;
        }
        byte[] record = builder.toString().getBytes();
        
        return record;
    }
    
    /**
     * Finds the position in the file for a given record number.
     * @param recNo the number of the record.
     * @return the position in the file where the record
     * 		   is stored.
     */
    private long calculateFilePosition(int recNo) {
        return FILE_DATA_SECTION_OFFSET + ((recNo - 1) * RECORD_LENGTH);
    }
    
    /**
     * Calculates the record number from the position of a record in the file.
     * @param filePosition the position of the record in the database file.
     * @return <code>int</code> record number of the file at the specified
     * 			position.
     */
    private int calculateRecordNumber(long filePosition) {
        return (int) (filePosition - FILE_DATA_SECTION_OFFSET) / RECORD_LENGTH + 1;
    }
    
    /**
     * Determines whether a given record number exists in the
     * database file.
     * @param recNo the number of the record.
     * @return boolean indicating if the specified
     *         record number exists in the database file.
     * @throws DBException if an IOException is thrown 
     *         when attempting to read from the database file.
     */
    public boolean recordExists(int recNo) {
        long position = calculateFilePosition(recNo);
        try {
            return validFilePosition(position) && !markedAsDeleted(recNo);
        } catch (IOException e) {
            log.throwing("DBAccessor.java", "recordExists", e);
            throw new DBException("Could not determine if record number "
                      + recNo + " exists", e);
        }
    }
    
    /**
     * Determines whether a file is marked as deleted in the database file.
     * @param recNo the number of the record.
     * @return boolean indicating if the specified record
     *         number is marked as deleted.
     * @throws IOException if an error occurs when reading from
     *         the file.
     */
    private boolean markedAsDeleted(int recNo) throws IOException {
        long position = calculateFilePosition(recNo);
        synchronized (database) {
            database.seek(position);
            byte flag = database.readByte();
            return flag == DELETED_FLAG;
        }
    }
    
    /**
     * Determines if a specified file position exists.
     * @param position the file position to test for
     *        validity.
     * @return boolean indicating if the specified position
     *         is a valid file position.
     * @throws IOException if an error occurs when reading
     *         the database file.
     */
    private boolean validFilePosition(long position) throws IOException {
        return (position < database.length()) && (position > 0);
    }
    
    /**
     * Closes the <code>RandomAccessFile</code> instance.
     * @throws IOException
     */
    public void close() throws IOException {
        database.close();
        log.info("DB file closed");
    }
    
}
