package suncertify.presentation;

import suncertify.application.Room;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

/**
 * This class extends <code>AbstractTableModel</code> in order
 * to provide a suitable table model for this application. A 
 * <code>LinkedHashMap</code> is used to maintain a mapping between
 * table rows and database record numbers. That is, 
 * Table row <code>i</code> corresponds to <code>roomMap[i]</code> 
 * and each <code>Room</code> object encapsulates the corresponding
 * database record number.
 *
 * @author John Harding
 */
public class RoomTableModel extends AbstractTableModel {
    
    /**
     * The SUID.
     */
    private static final long serialVersionUID = 1881L;
    
    /**
     * Maps tables rows to <code>Room</code> objects.
     */
    private Map<Integer, Room> roomMap = new LinkedHashMap<Integer, Room>();
    
    /**
     * The column names for the table.
     */
    private String[] columnNames = {"Name", "Location", "Size", "Smoking", "Rate", "Date", "Owner"};
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
        return roomMap.size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room room = roomMap.get(rowIndex);
        String[] row = room.getData();
        return row[columnIndex];
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Room room = roomMap.get(rowIndex);
        Object[] row = room.getData();
        row[columnIndex] = value;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    /**
     * Returns the <code>Room</code> at the specified row index.
     * @param rowIndex the number of the table row.
     * @return <code>Room</code>
     */
    public Room getRoom(int rowIndex) {
        return roomMap.get(rowIndex);
    }
    
    /**
     * Sets the <code>roomMap</code> hash map.
     * Fires table data changed notification.
     * @param map <code>LinkedHashMap<Integer, Room></code>
     */
    public void setRoomMap(Map<Integer, Room> map) {
        roomMap = map;
        this.fireTableDataChanged();
    }
    
}
