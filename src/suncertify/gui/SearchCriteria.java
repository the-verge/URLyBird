package suncertify.gui;

public class SearchCriteria {
    
    private String[] criteria = new String[7];
    
    public String[] getCriteria() {
        return criteria;
    }
    
    public void setName(String name) {
        criteria[0] = name;
    }

    public void setLocation(String location) {
        criteria[1] = location;
    }

    public void setSize(String size) {
        criteria[2] = size;
    }

    public void setSmoking(String smoking) {
        criteria[3] = smoking;
    }

    public void setRate(String rate) {
        criteria[4] = rate;
    }

    public void setDate(String date) {
        criteria[5] = date;
    }

    public void setOwner(String owner) {
        criteria[6] = owner;
    }


}
