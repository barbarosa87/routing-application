/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmNodes.java
 *
 * Created on 30 Σεπ 2011, 9:19:40 μμ
 */
package routing;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author elias
 */
public class FrmNodes extends javax.swing.JFrame {
   
    //Variable Declaration
    JScrollPane NodesTableScrollPanel;
    public static TableModel NodesTm;
    JTable NodesTable;
   // public static TableRowChanged Changed;

    /** Creates new form FrmNodes */
    public FrmNodes() {
        initComponents();
        btnStart.setIcon(new ImageIcon("./Resources/play_icon200.png"));
        LoadNodesTable();
    }
  
    
    
    public final void LoadNodesTable(){
   NodesTm = new TableModel(EnumeRators.Node);
   NodesTable = new JTable(NodesTm);
   NodesTable.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                NodesTable.setModel(new TableModel(EnumeRators.Node));
            }
        });
   NodesTableScrollPanel=new JScrollPane(NodesTable);
   NodesTablePanel.setLayout(new FlowLayout());
   NodesTablePanel.add(NodesTableScrollPanel);  
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        NodesTablePanel = new javax.swing.JPanel();
        NodesTableButtons = new javax.swing.JPanel();
        btnDeleteRow = new javax.swing.JButton();
        btnNewRow = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        btnAreasTable = new javax.swing.JButton();
        btnStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        NodesTablePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(NodesTablePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 480, 440));

        NodesTableButtons.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnDeleteRow.setText("Delete Row");
        btnDeleteRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteRowActionPerformed(evt);
            }
        });
        NodesTableButtons.add(btnDeleteRow, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 100, -1));

        btnNewRow.setText("New Row");
        btnNewRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewRowActionPerformed(evt);
            }
        });
        NodesTableButtons.add(btnNewRow, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 100, -1));

        getContentPane().add(NodesTableButtons, new org.netbeans.lib.awtextra.AbsoluteConstraints(508, 80, -1, 270));

        jPanel2.setForeground(new java.awt.Color(240, 240, 240));
        jPanel2.setLayout(null);
        jPanel2.add(filler1);
        filler1.setBounds(0, 0, 608, 0);

        btnAreasTable.setText("Areas Table");
        btnAreasTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAreasTableActionPerformed(evt);
            }
        });
        jPanel2.add(btnAreasTable);
        btnAreasTable.setBounds(0, 0, 120, 70);

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 608, 74));

        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });
        getContentPane().add(btnStart, new org.netbeans.lib.awtextra.AbsoluteConstraints(508, 414, 100, 89));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNewRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewRowActionPerformed
        // TODO add your handling code here:
        //Open frmNewRow with Selected Tab
        frmNewRow NewRow=new frmNewRow("Nodes");
        NewRow.setTitle("Add New Row");
        NewRow.setIconImage(Toolkit.getDefaultToolkit().getImage("./Resources/globe.png"));        
        NewRow.setLocation(this.getLocation().x+400,this.getLocation().y);
        NewRow.setVisible(true);
    }//GEN-LAST:event_btnNewRowActionPerformed

private void btnAreasTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAreasTableActionPerformed
// TODO add your handling code here:
    frmAreas frmareas=new frmAreas();
    frmareas.setTitle("Areas Table");
    frmareas.setIconImage(Toolkit.getDefaultToolkit().getImage("./Resources/globe.png"));
    frmareas.setLocationRelativeTo(this);
    frmareas.setVisible(true);
}//GEN-LAST:event_btnAreasTableActionPerformed

private void btnDeleteRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteRowActionPerformed
    DbConnection db=new DbConnection();
    try {
    Connection conn=db.Connect();
    if(NodesTable.getSelectedRow()>=0){
    db.RemoveFromDb(EnumeRators.Node, conn,String.valueOf(NodesTable.getValueAt(NodesTable.getSelectedRow(), 0)));
    NodesTm.fireTableDataChanged();
    }
    if(!conn.isClosed()){
        conn.close();
    }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
}//GEN-LAST:event_btnDeleteRowActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        Frame[] activeframes=Frame.getFrames();
        for (Frame frame:activeframes){
        frame.dispose();
         }
        frmStart Start =new frmStart();
        Start.setLocationRelativeTo(this);
        Start.setTitle("Execution");
        Start.setVisible(true);
    }//GEN-LAST:event_btnStartActionPerformed

    /**
     * @param args the command line arguments
     */
   
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel NodesTableButtons;
    private javax.swing.JPanel NodesTablePanel;
    private javax.swing.JButton btnAreasTable;
    private javax.swing.JButton btnDeleteRow;
    private javax.swing.JButton btnNewRow;
    private javax.swing.JButton btnStart;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
