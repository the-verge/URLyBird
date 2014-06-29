package suncertify.db;

public class Data implements DB {

    @Override
    public String[] read(int recNo) throws RecordNotFoundException {
        return null;
    }

    @Override
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
    }

    @Override
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException {
        
    }

    @Override
    public int[] find(String[] criteria) {
        return null;
    }

    @Override
    public int create(String[] data) throws DuplicateKeyException {
        return 0;
    }

    @Override
    public long lock(int recNo) throws RecordNotFoundException {
        return 0;
    }

    @Override
    public void unlock(int recNo, long cookie) throws RecordNotFoundException,
            SecurityException {
        
    }

}
