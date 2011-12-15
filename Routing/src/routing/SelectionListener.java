/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author barbarosa
 */
public class SelectionListener implements ListSelectionListener {
   JTable table;
   frmFreq Frmfreq;
   frmNewRow FrmRow;

  SelectionListener(JTable table,frmFreq freq,frmNewRow Row) {
    this.table = table;
    this.Frmfreq=freq;
    this.FrmRow=Row;
  }
  
  
    @Override
  public void valueChanged(ListSelectionEvent e) {
    int Row=0,Column=0;
   if (e.getSource() == table.getColumnModel().getSelectionModel() && table.getRowSelectionAllowed()) {
      Row =table.getSelectedRow();
       Column = table.getSelectedColumn();
         
}
   if(!e.getValueIsAdjusting()){
      
       this.FrmRow.SetTxt_Freq((String)table.getValueAt(Row, Column));
       Frmfreq.dispose();
       
        }
    
   } } 

