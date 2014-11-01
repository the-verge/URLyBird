package test.db;

import suncertify.db.*;
import suncertify.db.SecurityException;

/**
 * Created by john on 10/27/14.
 */
public class DeleteRecords {

    public static void main(String[] args) throws RecordNotFoundException, suncertify.db.SecurityException {
        DeleteRecords d = new DeleteRecords();
        d.deleteRecord(1);
        d.deleteRecord(5);
        d.deleteRecord(25);
       // d.deleteRecord(2);
    }

    public void deleteRecord(int recNo) throws RecordNotFoundException, SecurityException {
        Data data = new Data("/Users/john/URLyBird/db-1x3.db");
        long cookie = data.lock(recNo);
        data.delete(recNo, cookie);
        data.unlock(recNo, cookie);
    }
}
