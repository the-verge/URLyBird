package test;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class DataTest {
    
    @Test(expectedExceptions = SecurityException.class)
    public void securityTest() throws RecordNotFoundException, SecurityException {
        Data data = new Data("/Users/john/workspace/URLyBird/db-1x3.db");
        String[] updateData = {"Bed & Breakfast & Business", "Lendmarch", "6", "Y", "$170.00", "2005/03/10", ""};
        long cookie = data.lock(1);
        data.update(1, updateData, 1L);
        data.unlock(1, cookie);
    }
    
    @Test(expectedExceptions = RecordNotFoundException.class)
    public void lockNonExistingRecordTest() throws RecordNotFoundException {
        Data data = new Data("/Users/john/workspace/URLyBird/db-1x3.db");
        data.lock(66);
    }

}
