package suncertify.presentation;

/**
 * Class that represents a hotel room.
 * @author 
 *
 */
public class Room {

    private int recNo;
    
    private String[] data;
    
    protected Room(int recNo, String[] data) {
        this.recNo = recNo;
        this.data = data;
    }
    
    protected int getRecNo() {
        return recNo;
    }
    
    protected String[] getData() {
        return data;
    }
    
    protected void setRecNo(int recNo) {
        this.recNo = recNo;
    }
    
    protected void setName(String name) {
        data[0] = name;
    }

    protected void setLocation(String location) {
        data[1] = location;
    }

    protected void setSize(String size) {
        data[2] = size;
    }

    protected void setSmoking(String smoking) {
        data[3] = smoking;
    }

    protected void setRate(String rate) {
        data[4] = rate;
    }

    protected void setDate(String date) {
        data[5] = date;
    }

    protected void setOwner(String owner) {
        data[6] = owner;
    }

}
