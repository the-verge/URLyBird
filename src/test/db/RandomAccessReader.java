package test.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class RandomAccessReader {
    
    private static final String DATABASE_LOCATION = "/Users/john/workspace/urlybird/db-1x3.db";
    
    private int startOfFileLength = 6;
    
    private int schemaSectionLength = 0;
    
    private int dataSectionOffset;
   
    private int recordLength = 1;
    
    private Map<String, Integer> recordFields = new LinkedHashMap<String, Integer>();
    
    public static void main(String[] args) throws IOException {
        
        RandomAccessFile db = null;
        try {
            db = new RandomAccessFile(DATABASE_LOCATION, "rw");
            System.out.println("DATABASE LENGTH: " + db.length());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        RandomAccessReader reader = new RandomAccessReader();
        try {
            reader.readStartOfFile(db);
            reader.readSchemaDescription(db);
            reader.readRecords(db);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void readStartOfFile(RandomAccessFile db) throws IOException {
        byte[] magicCookieByteArray = new byte[4];
        byte[] numberOfFieldsByteArray = new byte[2];
        db.read(magicCookieByteArray);
        db.read(numberOfFieldsByteArray);
                
        System.out.println("magic cookie: " + new BigInteger(magicCookieByteArray).intValue());
        System.out.println("number of fields in each record: " + new BigInteger(numberOfFieldsByteArray).intValue());
        System.out.println("********************************");
    }
    
    private void readSchemaDescription(RandomAccessFile db) throws IOException {
        for(int i = 0; i < 7; i++) {
            byte[] fieldNameLengthByteArray = new byte[1];
            db.read(fieldNameLengthByteArray);
            int fieldNameLength = new BigInteger(fieldNameLengthByteArray).intValue();
            this.schemaSectionLength += (fieldNameLength + 2);
            
            byte[] fieldNameByteArray = new byte[fieldNameLength];
            db.read(fieldNameByteArray);
            String fieldName = new String(fieldNameByteArray, "US-ASCII");
            
            byte[] fieldLengthByteArray = new byte[1];
            db.read(fieldLengthByteArray);
            int fieldLength = new BigInteger(fieldLengthByteArray).intValue();
            this.recordLength += fieldLength;
            
            String fieldKey = fieldName.toUpperCase();
            this.recordFields.put(fieldKey, fieldLength);
            
            System.out.println("FIELD NAME: " + fieldName);
            System.out.println("FIELD NAME LENGTH: " + fieldNameLength);
            System.out.println("FIELD LENGTH (bytes): " + fieldLength);
            System.out.println("***************************");
        }
        
        this.dataSectionOffset = this.schemaSectionLength + this.startOfFileLength;
        
    }
   
    
    private void readRecords(RandomAccessFile db) throws IOException {
        int count = 0;
        while(true) {
            System.out.println("***************************");
            byte[] validRecordArray = new byte[1];
            int eof = db.read(validRecordArray);
            
            if (eof == -1) {
                break;
            }
            
            System.out.println("VALID RECORD: " + new BigInteger(validRecordArray).intValue());
            
            for (Map.Entry<String, Integer> entry : this.recordFields.entrySet()) {
                String fieldName = entry.getKey();
                int fieldLength = entry.getValue();
                byte[] fieldByteArray = new byte[fieldLength];
                db.read(fieldByteArray);
                String fieldValue = new String(fieldByteArray);
                System.out.println(fieldName + ": " + fieldValue);
            }
            count++;
        }
        db.close();
        System.out.println("Total number of records: " + count);
        System.out.println("Data section offset: " + this.dataSectionOffset);
    }
   
    
    private void readRecord(RandomAccessFile db, int recordNumber) throws IOException {
        byte[] record = new byte[this.recordLength];
        long offset = this.schemaSectionLength + (this.recordLength * recordNumber);
        db.seek(offset);
        db.read(record);
        byte[] nameBytes = Arrays.copyOfRange(record, 1, 63);
        String name = new String(nameBytes);
        System.out.println("hotel name: " + name);
    }
}

