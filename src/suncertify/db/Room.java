package suncertify.db;

import java.io.Serializable;
import java.util.logging.*;

/**
 * Class that represents a hotel room.
 * @author 
 *
 */
public class Room implements Serializable {

    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1441L;
    
    static final int VALID_RECORD_LENGTH = 1;
    
    static final int NAME_LENGTH = 64;
    
    static final int LOCATION_LENGTH = 64;
    
    static final int SIZE_LENGTH = 4;
    
    static final int SMOKING_LENGTH = 1;
            
    static final int RATE_LENGTH = 8;
    
    static final int DATE_LENGTH = 10;
    
    static final int OWNER_LENGTH = 8;
    
    static final int RECORD_LENGTH = 160;
    
    /**
     * The name of the hotel.
     */
    private String name;
    
    /**
     * The city where the hotel is located.
     */
    private String location;
    
    /**
     * The maximum occupancy of the room, not including infants.
     */
    private String size;
    
    /**
     * Indicates whether the room is smoking or non-smoking.
     * Valid values are "Y" indicating a smoking room, 
     * and "N" indicating a non-smoking room.
     */
    private String smoking;
    
    /**
     * The price per night of the room.
     */
    private String rate;
    
    /**
     * The single night to which this record relates, format is yyyy/mm/dd.
     */
    private String date;
    
    /**
     * 8 digit customer ID which uniquely identifies the customer.
     */
    private String owner;
    
    /**
     * 
     */
    private transient Logger log = Logger.getLogger("Room.java");
    
    
    /**
     * 
     * @param name The name of the hotel
     * @param location The city where the hotel is located
     * @param size The maximum occupancy of the room
     * @param smoking Indicates whether the room is a smoking room
     * @param rate The price per night
     * @param date The date for which the room is booked
     * @param owner 8 digit number uniquely identifying the 
     * customer that has made the booking
     */
    public Room(String name, String location, String size, String smoking,
            String rate, String date, String owner) {
        super();
        this.name = name;
        this.location = location;
        this.size = size;
        this.smoking = smoking;
        this.rate = rate;
        this.date = date;
        this.owner = owner;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSmoking() {
        return smoking;
    }

    public void setSmoking(String smoking) {
        this.smoking = smoking;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    
}
