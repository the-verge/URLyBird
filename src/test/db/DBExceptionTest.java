package test.db;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.DBAccessor;
import suncertify.db.DBException;
import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;

public class DBExceptionTest {
    
    private static DBAccessor accessor;

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected = DBException.class)
    public void testLocalDBAccessorConstructionWithInvalidFilePath() throws RecordNotFoundException, FileNotFoundException {
        accessor = new DBAccessor("/Users/john/db-144.db");
    }
    
    @Test(expected = DBException.class)
    public void testLocalDataConstructionWithInvalidFilePath() throws RecordNotFoundException, FileNotFoundException {
        Data data = new Data("/Users/john/db-144.db");
    }

}
