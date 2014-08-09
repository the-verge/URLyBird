package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import suncertify.db.DBException;
import suncertify.db.Data;

public class DataTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test(expected=DBException.class)
    public void exceptionTest() {
        Data data = new Data("/User/john/workspace/URLyBird/db-1x3.db");
    }
    
    @Test
    public void catchExceptionTest() {
        try {
            Data data = new Data("/User/john/workspace/URLyBird/db-1x3.db");
        }
        catch (DBException e){
            String msg = e.getMessage();
            assertEquals("Database file not found", msg);
        }
        
    }

}
