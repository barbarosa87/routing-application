/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmFreq.java
 *
 * Created on Dec 14, 2011, 3:49:14 AM
 */
package routing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import routing.Enumerators.TableNames;

/**
 *
 * @author barbarosa
 */
public class frmFreq extends javax.swing.JFrame {

    /** Creates new form frmFreq */
    public frmFreq(int AreaID,frmNewRow Row) {
        initComponents();
        FreqTable.setColumnSelectionAllowed(true);
        FreqTable.setCellSelectionEnabled(true);
        FixTable(AreaID);
        SelectionListener listener = new SelectionListener(FreqTable,this,Row);
        FreqTable.getSelectionModel().addListSelectionListener(listener);
        FreqTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        //FreqTable.setTableHeader(null);
    }
    
    
    
    
    private int FixTable(int AreaID){
        if (AreaID==0){return 0;}
        DbConnection db=new DbConnection();
        Connection conn=db.Connect();
        int Rows=FreqTable.getModel().getRowCount();
        int Columns=FreqTable.getModel().getColumnCount();
        List<Integer> UnColumns=new ArrayList<Integer>();
        try {
             ResultSet AreaFreqRs=db.SelectFromDb(TableNames.AreaFrequencies, "WHERE ID="+AreaID, conn);
             while(AreaFreqRs.next()){
                 UnColumns.add(AreaFreqRs.getInt("Frequency"));
//                if(!FreqTable.getValueAt( j, i).equals(AreaFreqRs.getString("Frequency"))) {
//                      FreqTable.setValueAt("", j, i);
//                  } 
                   }
           for (int i=0;i<Columns-1;i++){
                 for (int j=0;j<Rows-1;j++){
                if(UnColumns.contains(Integer.parseInt((String)FreqTable.getValueAt( j, i)))) {
                    continue;  
                  }else{
                    FreqTable.setValueAt("", j, i);
                }                     
                }
            }
           conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(frmFreq.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        FreqTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        FreqTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"40", "41", "42", "43", "44"},
                {"45", "46", "47", "48", "49"},
                {"50", "51", "52", "53", "54"},
                {"55", "56", "57", "58", "59"},
                {"60", null, null, null, null}
            },
            new String [] {
                "", "", "", "", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        FreqTable.setTableHeader(null);
        jScrollPane1.setViewportView(FreqTable);
        FreqTable.getColumnModel().getColumn(0).setResizable(false);
        FreqTable.getColumnModel().getColumn(1).setResizable(false);
        FreqTable.getColumnModel().getColumn(2).setResizable(false);
        FreqTable.getColumnModel().getColumn(3).setResizable(false);
        FreqTable.getColumnModel().getColumn(4).setResizable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTable FreqTable;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
