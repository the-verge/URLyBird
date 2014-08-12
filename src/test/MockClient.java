package test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

public class MockClient implements Runnable {
    
    private Data data;
    
    private long lockCookie;
    
    private int recNo;
    
    
    public MockClient(Data data, int recNo) {
        this.data = data;
        this.recNo = recNo;
    }

    @Override
    public void run() {
        while (true) {
			System.out.println(Thread.currentThread().getName()
					+ ": starting up....");
			try {
				lockCookie = data.lock(recNo);
			} catch (RecordNotFoundException e) {
				try {
					PrintStream out = new PrintStream(new FileOutputStream(
							"~/logs/RecordNotFound.txt"));
					out.print(Thread.currentThread().getName()
							+ ": RecordNotFoundException - " + recNo + "\n");
				} catch (FileNotFoundException f) {}
			}
			System.out.println(Thread.currentThread().getName()
					+ ": doing some work.....");
			long endTime = System.currentTimeMillis() + 2000;
			while (System.currentTimeMillis() < endTime) {

			}
			try {
				data.unlock(recNo, lockCookie);
			} catch (RecordNotFoundException e) {
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()
					+ ": finished....");
		}
    }
    
    

}
