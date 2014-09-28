package test.db;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.DBException;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class DataTest {

    @Before
    public void setUp() throws Exception {
    }
    
    @Test(expected = DBException.class)
    public void constructorTest() {
        Data data = new Data("/Users/dave/workspace/urlybird/db-1x3.db");
    }
    
    @Test(expected = SecurityException.class)
    public void securityTest() throws RecordNotFoundException, SecurityException {
        Data data = new Data("/Users/john/workspace/urlybird/db-1x3.db");
        String[] updateData = {"Bed & Breakfast & Business", "Lendmarch", "6", "Y", "$170.00", "2005/03/10", ""};
        long cookie = data.lock(1);
        data.update(1, updateData, 1L);
        data.unlock(1, cookie);
    }
    
    @Test(expected = RecordNotFoundException.class)
    public void lockNonExistingRecordTest() throws RecordNotFoundException {
        Data data = new Data("/Users/john/workspace/urlybird/db-1x3.db");
        data.lock(66);
    }

}
