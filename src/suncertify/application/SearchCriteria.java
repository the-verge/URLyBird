package suncertify.application;

/**
 * This class is used to instantiate objects
 * that encapsulate search criteria for each
 * field of a database record. The use of such
 * objects eliminates the need for operations
 * on raw arrays. As such, code that involves
 * searching for rooms is more readable and
 * flexible.  Most methods in this class return
 * <code>this</code> in order to allow method 
 * chaining.
 *
 * @author John Harding
 */
public class SearchCriteria {
    
    /**
     * Array of length 7.  One element for each
     * record field.  All values are initially null.
     */
    private String[] criteria = new String[7];
    
    /**
     * Getter.
     * @return the criteria array.
     */
    public String[] getCriteria() {
        return criteria;
    }
    
    /**
     * Sets the criteria for hotel name.
     * @param name the hotel name.
     * @return the <code>this</code> object.
     */
    public SearchCriteria matchName(String name) {
        criteria[0] = name;
        return this;
    }
    
    /**
     * Sets the criteria for hotel location.
     * @param location the hotel location.
     * @return the <code>this</code> object.
     */
    public SearchCriteria matchLocation(String location) {
        criteria[1] = location;
        return this;
    }
    
    /**
     * Sets the room size criteria.
     * @param size the maximum occupancy of the room.
     * @return the <code>this</code> object.
     */
    public SearchCriteria matchSize(String size) {
        criteria[2] = size;
        return this;
    }
    
    /**
     * Sets the criteria for smoking.
     * @param smoking whether the room is a smoking room.
     * @return the <code>this</code> object.
     */
    public SearchCriteria matchSmoking(String smoking) {
        criteria[3] = smoking;
        return this;
    }
    
    /**
     * Sets the rate criteria.
     * @param rate the nightly rate of the hotel room.
     * @return the <code>this</code> object.
     */
    public SearchCriteria matchRate(String rate) {
        criteria[4] = rate;
        return this;
    }
    
    /**
     * Sets the date criteria.
     * @param date the date of availability.
     * @return the <code>this</code> object.
     */
    public SearchCriteria matchDate(String date) {
        criteria[5] = date;
        return this;
    }
    
    /**
     * Sets the criteria for the owner of the booking.
     * @param owner the customer that booked the room.
     * @return the <code>this</code> object.
     */
    public SearchCriteria matchOwner(String owner) {
        criteria[6] = owner;
        return this;
    }
    
    /**
     * Sets all values of the criteria array to an empty String.
     * The search will then match all records.
     */
    public void matchAllRecords() {
        for (int i = 0; i < criteria.length; i++) {
            criteria[i] = "";
        }
    }
}
