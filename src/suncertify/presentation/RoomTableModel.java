package suncertify.presentation;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class RoomTableModel extends AbstractTableModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1881L;
    
    private Map<Integer, Room> roomMap = new LinkedHashMap<Integer, Room>();

    private String[] columnNames = {"Name", "Location", "Size", "Smoking", "Rate", "Date", "Owner"};
    
    @Override
    public int getRowCount() {
        return roomMap.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room room = roomMap.get(rowIndex);
        String[] row = room.getData();
        return row[columnIndex];
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Room room = roomMap.get(rowIndex);
        Object[] row = room.getData();
        row[columnIndex] = value;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    public Room getRoom(int rowIndex) {
        return roomMap.get(rowIndex);
    }
    
    public void setRoomMap(Map<Integer, Room> map) {
        roomMap = map;
        this.fireTableDataChanged();
    }
    
}
