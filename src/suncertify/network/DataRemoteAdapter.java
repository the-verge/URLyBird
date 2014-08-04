package suncertify.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

import suncertify.db.DB;
import suncertify.db.DuplicateKeyException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * 
 * @author john
 *
 */
public interface DataRemoteAdapter extends Remote {
    
    public String[] read(int recNo) throws RecordNotFoundException, RemoteException;

    public void update(int recNo, String[] data, long lockCookie)
      throws RecordNotFoundException, SecurityException, RemoteException;

    public void delete(int recNo, long lockCookie)
      throws RecordNotFoundException, SecurityException, RemoteException;

    public int[] find(String[] criteria) throws RemoteException;

    public int create(String[] data) throws DuplicateKeyException, RemoteException;

    public long lock(int recNo) throws RecordNotFoundException, RemoteException;
    
    public void unlock(int recNo, long cookie)
      throws RecordNotFoundException, SecurityException, RemoteException;

}
