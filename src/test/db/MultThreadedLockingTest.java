package test.db;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.network.Server;

public class MultThreadedLockingTest {
    
    static Data data;
    
  @BeforeClass
  public void beforeClass() {
      Server.startServer("/Users/john/workspace/urlybird/db-1x3.db", 1099);
      data = new Data("/Users/john/workspace/URLyBird/db-1x3.db");
  }
  
  @Test(threadPoolSize = 20, invocationCount = 1000,  timeOut = 10000)
  public void hungryChildrenTest() throws RecordNotFoundException, SecurityException {
      System.out.println("Thread id = " + Thread.currentThread().getId() + " STARTING");
      long cookie = data.lock(1);
      String[] update = {"Bed & Breakfast & Business", "Lendmarch", "6", "Y", "$170.00", "2005/03/10", ""};
      data.update(1, update, cookie);
      data.unlock(1, cookie);
      System.out.println("Thread id = " + Thread.currentThread().getId() + " FINISHED");

  }

}
