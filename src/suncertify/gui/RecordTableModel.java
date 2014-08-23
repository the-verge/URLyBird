package suncertify.gui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class RecordTableModel extends AbstractTableModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1991L;
    
    private Map<Integer, Room> tableIndexToRoomMap = new LinkedHashMap<Integer, Room>();

    private String[] columnNames = {"Name", "Location", "Size", "Smoking", "Rate", "Date", "Owner"};
    
    private List<String[]> records = new ArrayList<String[]>();
    
    public RecordTableModel() {
    	
    }
    
    public RecordTableModel(List<String[]> records) {
    	this.records = records;
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String[] row = records.get(rowIndex);
        return row[columnIndex];
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Object[] row = records.get(rowIndex);
        row[columnIndex] = value;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    public Room getRoom(int rowIndex) {
        return tableIndexToRoomMap.get(rowIndex);
    }
    
    public void setTableIndexToRoomMap(Map<Integer, Room> map) {
        tableIndexToRoomMap = map;
        setRecords();
    }
    
    public void setRecords() {
        records.clear();
        for (Map.Entry<Integer, Room> entry: tableIndexToRoomMap.entrySet()) {
            Room room = entry.getValue();
            String[] record = room.getData();
            records.add(record);
        }
        this.fireTableDataChanged();

    }
    
    public void addRecord(String[] fields) {
        records.add(fields);
    }
    
    public void setRecordFieldList(List<String[]> records) {
    	this.records = records;
    }

}
