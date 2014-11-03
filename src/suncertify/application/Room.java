package suncertify.application;

/**
 * Class that represents a hotel room. Used as a transfer object.
 * It encapsulates a record's data and it's number.
 *
 * @author John Harding
 */
public class Room {
    
    /**
     * The record number that the data
     * belongs to.
     */
    private int recNo;
    
    /**
     * The room data.
     */
    private String[] data;
    
    /**
     * Class constructor.
     * @param recNo the database record number.
     * @param data the record data.
     */
    public Room(int recNo, String[] data) {
        this.recNo = recNo;
        this.data = data;
    }
    
    /**
     * Getter.
     * @return the record number.
     */
    public int getRecNo() {
        return recNo;
    }
    
    /**
     * Getter.
     * @return the record data.
     */
    public String[] getData() {
        return data;
    }
    
    /**
     * Setter.
     * @param recNo the record number.
     */
    public void setRecNo(int recNo) {
        this.recNo = recNo;
    }
    
    /**
     * Setter.
     * @param name the hotel name.
     */
    public void setName(String name) {
        data[0] = name;
    }
    
    /**
     * Setter.
     * @param location the hotel location.
     */
    public void setLocation(String location) {
        data[1] = location;
    }
    
    /**
     * Setter.
     * @param size the maximum occupancy of the room.
     */
    public void setSize(String size) {
        data[2] = size;
    }
    
    /**
     * Setter.
     * @param smoking whether the room is a smoking room.
     */
    public void setSmoking(String smoking) {
        data[3] = smoking;
    }
    
    /**
     * Setter.
     * @param rate the nightly rate.
     */
    public void setRate(String rate) {
        data[4] = rate;
    }
    
    /**
     * Setter.
     * @param date the date for which the room is booked.
     */
    public void setDate(String date) {
        data[5] = date;
    }
    
    /**
     * Setter.
     * @param owner the customer that has booked the room.
     */
    public void setOwner(String owner) {
        data[6] = owner;
    }

}
