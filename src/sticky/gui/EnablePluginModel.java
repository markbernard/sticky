package sticky.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class EnablePluginModel implements TableModel {
    private static final String columnNames[] = {"Enable", "Name"};
    
    private Map<String, Boolean> enablePlugin;
    private List<String> pluginNames;
    private List<TableModelListener> listeners;

    public EnablePluginModel(Map<String, Boolean> enablePlugin) {
        this.enablePlugin = enablePlugin;
        pluginNames = new ArrayList<String>();
        pluginNames.addAll(enablePlugin.keySet());
        listeners = new ArrayList<TableModelListener>();
    }
    public void addTableModelListener(TableModelListener l) {
        if(l != null) {
            listeners.add(l);
        }
    }
    public Class<?> getColumnClass(int columnIndex) {
        Class<?> result;
        
        if(columnIndex == 0) {
            result = Boolean.class;
        }
        else {
            result = String.class;
        }
        
        return result;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public int getRowCount() {
        return pluginNames.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        
        if(columnIndex == 0) {
            result = enablePlugin.get(pluginNames.get(rowIndex));
        }
        else {
            result = pluginNames.get(rowIndex);
        }
        
        return result;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    public void removeTableModelListener(TableModelListener l) {
        if(l != null) {
            listeners.remove(l);
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0) {
            enablePlugin.put(pluginNames.get(rowIndex), (Boolean)aValue);
        }
        for(Iterator<TableModelListener> it=listeners.iterator();it.hasNext();) {
            it.next().tableChanged(new TableModelEvent(this, rowIndex));
        }
    }
}
