package suncertify.gui;

import java.util.List;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class RecordTableModel extends AbstractTableModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1991L;

    private String[] columnNames = {"Name", "Location", "Size", "Smoking", "Rate", "Date", "Owner"};
    
    private List<String[]> recordFieldList = new ArrayList<String[]>();

    @Override
    public int getRowCount() {
        return recordFieldList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String[] row = recordFieldList.get(rowIndex);
        return row[columnIndex];
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Object[] row = recordFieldList.get(rowIndex);
        row[columnIndex] = value;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    public void addRecord(String[] fields) {
        recordFieldList.add(fields);
    }

}
