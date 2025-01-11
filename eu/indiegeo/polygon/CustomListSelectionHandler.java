/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.indiegeo.polygon;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author lenovo
 * Mérési adatoknál többb sor kijelölésére szolgál és azok adatai adja össze
 */
public class CustomListSelectionHandler implements ListSelectionListener {

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if( !lse.getValueIsAdjusting() ){
            JTable dataTable = MainWindow.getDataTable();
            DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
            int[] selectedRows = dataTable.getSelectedRows();
            double length = 0;
            for (int i : selectedRows) {
                length += (double)model.getValueAt(i, dataTable.getColumn("Length").getModelIndex());
            }
            MainWindow.getSelectedLength().setText(String.format("%.2f",length));
        }
    }

   
    
}
