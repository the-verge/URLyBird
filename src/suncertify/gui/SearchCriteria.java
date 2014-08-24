package suncertify.gui;

public class SearchCriteria {
    
    private String[] criteria = new String[7];
    
    public String[] getCriteria() {
        return criteria;
    }
    
    public SearchCriteria matchName(String name) {
        criteria[0] = name;
        return this;
    }

    public SearchCriteria matchLocation(String location) {
        criteria[1] = location;
        return this;
    }

    public SearchCriteria matchSize(String size) {
        criteria[2] = size;
        return this;
    }

    public SearchCriteria matchSmoking(String smoking) {
        criteria[3] = smoking;
        return this;
    }

    public SearchCriteria matchRate(String rate) {
        criteria[4] = rate;
        return this;
    }

    public SearchCriteria matchDate(String date) {
        criteria[5] = date;
        return this;
    }

    public SearchCriteria matchOwner(String owner) {
        criteria[6] = owner;
        return this;
    }

    public void matchAllRecords() {
        for (int i = 0; i < criteria.length; i++) {
            criteria[i] = "";
        }
    }
}
