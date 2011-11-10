/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmNewRow.java
 *
 * Created on 1 Οκτ 2011, 9:12:55 μμ
 */
package routing;

import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author elias
 */
public class frmNewRow extends javax.swing.JFrame {

    
    List<Integer> NodesID=new ArrayList<Integer>();
   
    public frmNewRow(String Tab) {
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        if (Tab.equals("Areas")){
            SelectionTabPanel.setSelectedIndex(SelectionTabPanel.indexOfTab("Areas"));
            SelectionTabPanel.setEnabledAt(SelectionTabPanel.indexOfTab("Nodes"), false);
        }else if(Tab.equals("Nodes")){
            SelectionTabPanel.setSelectedIndex(SelectionTabPanel.indexOfTab("Nodes"));
            SelectionTabPanel.setEnabledAt(SelectionTabPanel.indexOfTab("Areas"), false);
            FillFrmCmbs();
         //   bxArea.doClick();
        }
    }
    
    
    public final void FillFrmCmbs(){
         DbConnection db=new DbConnection();
            Connection conn=db.Connect();
   try{
         ResultSet rs=db.SelectFromDb(EnumeRators.Area, conn);
   if (rs!=null){
      while(rs.next()){
         SlArea.addItem(rs.getString("ID"));
     }
      conn.close();
      rs.close();
   }
    }catch(SQLException e){
    e.printStackTrace();
}
   FillNeighComboBoxes();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SelectionTabPanel = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        bxArea = new javax.swing.JCheckBox();
        SlArea = new javax.swing.JComboBox();
        NeighbourPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        ComboPlusNodes = new javax.swing.JComboBox();
        btnPlusNodes = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        AddedComboNeigh = new javax.swing.JComboBox();
        Txt_Freq = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TextStartFreq = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TextStopFreq = new javax.swing.JTextField();
        btnAddNode = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bxArea.setText("Area Flag");
        jPanel1.add(bxArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 97, -1));
        jPanel1.add(SlArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 70, -1));

        NeighbourPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setForeground(new java.awt.Color(255, 0, 51));
        jLabel3.setText("Pick Neighbours");
        NeighbourPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 145, -1));

        jLabel4.setText("Available Neighbours");
        NeighbourPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 33, -1, -1));

        jLabel5.setText("Added Neighbours");
        NeighbourPanel.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 108, -1, -1));
        NeighbourPanel.add(ComboPlusNodes, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 50, -1));

        btnPlusNodes.setText("+");
        btnPlusNodes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlusNodesActionPerformed(evt);
            }
        });
        NeighbourPanel.add(btnPlusNodes, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 60, -1, 20));

        jLabel7.setText("Nodes");
        NeighbourPanel.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 60, -1, -1));
        NeighbourPanel.add(AddedComboNeigh, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 140, -1));

        jPanel1.add(NeighbourPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 110, 157, 220));

        Txt_Freq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Txt_FreqKeyTyped(evt);
            }
        });
        jPanel1.add(Txt_Freq, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 72, 100, -1));

        jLabel8.setText("Frequency");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(56, 75, -1, -1));

        SelectionTabPanel.addTab("Nodes", jPanel1);

        jPanel3.setLayout(new java.awt.GridLayout(2, 2));

        jLabel1.setText("Start Frequency");
        jPanel3.add(jLabel1);

        TextStartFreq.setFont(new java.awt.Font("Arial", 0, 8));
        TextStartFreq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextStartFreqKeyTyped(evt);
            }
        });
        jPanel3.add(TextStartFreq);

        jLabel2.setText("Stop Frequency");
        jPanel3.add(jLabel2);

        TextStopFreq.setFont(new java.awt.Font("Arial", 0, 8));
        TextStopFreq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TextStopFreqKeyTyped(evt);
            }
        });
        jPanel3.add(TextStopFreq);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(249, Short.MAX_VALUE))
        );

        SelectionTabPanel.addTab("Areas", jPanel2);

        btnAddNode.setText("Add");
        btnAddNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(SelectionTabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(153, 153, 153)
                        .addComponent(btnAddNode)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SelectionTabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddNode)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //Set Plus Combo Boxes
    public void FillNeighComboBoxes(){
        DbConnection db=new DbConnection();
        Connection conn=db.Connect();
        try{
        
        ResultSet  rs=db.SelectFromDb(EnumeRators.Node, conn);
                while(rs.next()){
                        ComboPlusNodes.addItem(rs.getString("ID"));                  
                }
            rs.close();
            conn.close();
        }catch(SQLException e){
        e.printStackTrace();
        }
    }
    
private void btnAddNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNodeActionPerformed

if(SelectionTabPanel.getSelectedIndex()==SelectionTabPanel.indexOfTab("Nodes")){
   DbConnection db=new DbConnection();
   Connection conn=db.Connect(); 
   try{
   int KeyNodesCount=db.ReturnUniqueKey(EnumeRators.Node, conn);
    if (bxArea.isSelected()){
       if(SlArea.getItemCount()>0){
       db.AddToDb( "INSERT INTO Nodes (ID,Area_flag,Area_ID,Frequency) VALUES (" + (KeyNodesCount+1)+"," + "1," + SlArea.getSelectedItem()+ "," + Integer.parseInt(Txt_Freq.getText()) +")", conn);
       FrmNodes.NodesTm.fireTableDataChanged();
       }else{
           JOptionPane.showMessageDialog(this, "You should pick an area to include the node", "Warning", JOptionPane.ERROR_MESSAGE);
           conn.close();
       }
   }
    else{
       db.AddToDb( "INSERT INTO Nodes (ID,Area_flag,Area_ID,Frequency) VALUES (" + (KeyNodesCount+1)+"," + "0,-1," + Integer.parseInt(Txt_Freq.getText()) +")", conn);
       FrmNodes.NodesTm.fireTableDataChanged();
    }
    //CreaTingGeolocationDB
    for (int i=0;i<NodesID.size();i++){
        int GeoDBCount=db.ReturnUniqueKey(EnumeRators.GeolocationDb, conn);
        db.AddToDb( "INSERT INTO GeolocationDB (ID,NodeID,NeighbourID) VALUES ("+(GeoDBCount+1)+","+ (KeyNodesCount+1)+","+NodesID.get(i)+")", conn);
    }
    NodesID.clear();
   if(!conn.isClosed()){
       conn.close();
   }
   }catch(SQLException e){
       e.printStackTrace();
   }
   this.dispose();
}else if(SelectionTabPanel.getSelectedIndex()==SelectionTabPanel.indexOfTab("Areas")){
    if((TextStartFreq.getText().isEmpty()) || (TextStopFreq.getText().isEmpty())){
        
    }else {
    DbConnection db=new DbConnection();
    Connection conn=db.Connect();
    int AreasCount=db.ReturnUniqueKey(EnumeRators.Area, conn);
    db.AddToDb("INSERT INTO Areas(ID,Start_frq,Stop_frq) VALUES("+(AreasCount+1)+","+TextStartFreq.getText()+","+TextStopFreq.getText()+")", conn);
    frmAreas.AreasTm.fireTableDataChanged();
    this.dispose();
    }
}


}//GEN-LAST:event_btnAddNodeActionPerformed

private void btnPlusNodesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlusNodesActionPerformed
 if(ComboPlusNodes.getSelectedItem()!=null){
     AddedComboNeigh.addItem(" Node with ID: "+ComboPlusNodes.getSelectedItem());
     NodesID.add(Integer.parseInt(ComboPlusNodes.getSelectedItem().toString()));
    }
}//GEN-LAST:event_btnPlusNodesActionPerformed

private void TextStartFreqKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextStartFreqKeyTyped
char c=evt.getKeyChar();
if ((Character.isDigit(c)) || (c==KeyEvent.VK_BACK_SPACE) || (c==KeyEvent.VK_DELETE)){
    
}else{
    evt.consume();
}

}//GEN-LAST:event_TextStartFreqKeyTyped

private void TextStopFreqKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TextStopFreqKeyTyped
char c=evt.getKeyChar();
if ((Character.isDigit(c)) || (c==KeyEvent.VK_BACK_SPACE) || (c==KeyEvent.VK_DELETE)){
    
}else{
    evt.consume();
}
}//GEN-LAST:event_TextStopFreqKeyTyped

private void Txt_FreqKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Txt_FreqKeyTyped
char c=evt.getKeyChar();
if ((Character.isDigit(c)) || (c==KeyEvent.VK_BACK_SPACE) || (c==KeyEvent.VK_DELETE)){
    
}else{
    evt.consume();
}
}//GEN-LAST:event_Txt_FreqKeyTyped



private boolean ValidateInput(String Tab){
    
    return true;
}

//private void CreateGeolocationDb(){
//   DbConnection db=new DbConnection();
//   Connection conn=db.Connect(); 
//   
//   }
    

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox AddedComboNeigh;
    private javax.swing.JComboBox ComboPlusNodes;
    private javax.swing.JPanel NeighbourPanel;
    private javax.swing.JTabbedPane SelectionTabPanel;
    private javax.swing.JComboBox SlArea;
    private javax.swing.JTextField TextStartFreq;
    private javax.swing.JTextField TextStopFreq;
    private javax.swing.JTextField Txt_Freq;
    private javax.swing.JButton btnAddNode;
    private javax.swing.JButton btnPlusNodes;
    private javax.swing.JCheckBox bxArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
