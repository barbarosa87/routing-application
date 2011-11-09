/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmAreas.java
 *
 * Created on 3 Οκτ 2011, 10:52:06 μμ
 */
package routing;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author barbarosa
 */

public class frmAreas extends javax.swing.JFrame {
  JTable AreasTable;
  public static TableModel AreasTm;
  JScrollPane AreasTableScrolPane;
  
    /** Creates new form frmAreas */
    public frmAreas() {
       initComponents();
       CreateAreasTable();
    }
   public final void CreateAreasTable(){
      AreasTm=new TableModel(EnumeRators.Area);
      AreasTable=new JTable(AreasTm);
      AreasTable.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
           AreasTable.setModel(new TableModel(EnumeRators.Area));
            }
        });
      AreasTablePanel.setLayout(new FlowLayout());
      AreasTableScrolPane=new JScrollPane(AreasTable);
      AreasTablePanel.add(AreasTableScrolPane);
   }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        AreasTablePanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        btnNewRow = new javax.swing.JButton();
        btnDeleteRow = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Areas Table"); // NOI18N

        javax.swing.GroupLayout AreasTablePanelLayout = new javax.swing.GroupLayout(AreasTablePanel);
        AreasTablePanel.setLayout(AreasTablePanelLayout);
        AreasTablePanelLayout.setHorizontalGroup(
            AreasTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 545, Short.MAX_VALUE)
        );
        AreasTablePanelLayout.setVerticalGroup(
            AreasTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 493, Short.MAX_VALUE)
        );

        btnNewRow.setText("New Row");
        btnNewRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewRowActionPerformed(evt);
            }
        });

        btnDeleteRow.setText("Delete Row");
        btnDeleteRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteRowActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnNewRow, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
            .addComponent(btnDeleteRow, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnNewRow)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteRow)
                .addContainerGap(175, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(AreasTablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AreasTablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnNewRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewRowActionPerformed
// TODO add your handling code here:
    frmNewRow ArFrm=new frmNewRow("Areas");
    ArFrm.setTitle("New Row");
    ArFrm.setIconImage(Toolkit.getDefaultToolkit().getImage("./Resources/globe.png"));
    ArFrm.setLocationRelativeTo(this);
    ArFrm.setVisible(true);
}//GEN-LAST:event_btnNewRowActionPerformed

private void btnDeleteRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteRowActionPerformed
 DbConnection db=new DbConnection();
   
 try {
    Connection conn=db.Connect();
    if(AreasTable.getSelectedRow()>=0){
    db.RemoveFromDb(EnumeRators.Area, conn,String.valueOf(AreasTable.getValueAt(AreasTable.getSelectedRow(), 0)));
    AreasTm.fireTableDataChanged();
    }
    if(!conn.isClosed()){
        conn.close();
    }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}//GEN-LAST:event_btnDeleteRowActionPerformed

    /**
     * @param args the command line arguments
     */
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AreasTablePanel;
    private javax.swing.JButton btnDeleteRow;
    private javax.swing.JButton btnNewRow;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
