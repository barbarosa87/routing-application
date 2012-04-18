/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PanelDraw.java
 *
 * Created on 9 Δεκ 2011, 2:17:20 πμ
 */
package routing.Presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import routing.DbConnection;
import routing.Enumerators.TableNames;
import routing.Structs.FlowStruct.Flow;
import routing.Structs.FlowStruct.DesignNode;
/**
 *
 * @author barbarosa
 */
public class PanelDraw extends javax.swing.JPanel {
 private List<Flow> FLows=new ArrayList<Flow>();
    /** Creates new form PanelDraw */
    public PanelDraw(List<Flow> Flows) {
        initComponents();
        this.FLows=Flows;
        repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    @Override
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        List<DesignNode> NodesPoints=new ArrayList<DesignNode>();
        g2.setColor(Color.BLACK);
        List<Integer> AreasIDs=GetAreas();
        List<Integer> PassedNodes=new ArrayList<Integer>();
        Point2D.Double point = new Point2D.Double(0, 0);
        try{
        for(Integer AreaID:AreasIDs){
            Ellipse2D.Double Area=new Ellipse2D.Double(point.x,point.y,50,50);
            g2.draw(Area);
            List<Integer> NodesIDs=GetContainingNodes(AreaID);
            Double FirstXNodePoint=Area.getCenterX()-10;
            Double FirstYNodePoint=Area.getCenterY()-10;
            for(Integer NodeID:NodesIDs){
            g2.draw(new Ellipse2D.Double(FirstXNodePoint,FirstYNodePoint,5,5));
            NodesPoints.add(new DesignNode(NodeID, FirstXNodePoint, FirstYNodePoint));
            FirstXNodePoint=FirstXNodePoint+5;
            FirstYNodePoint=FirstYNodePoint+5;
            }
            //int IndependentNodesIDs=GetIndependentNodes(AreaID,PassedNodes);
            //int IndependentNodesIDs=3;
            double plus=point.x+70;
            double plusy=point.y+70;
            for(Integer NodeID:GetIndependentNodes(AreaID, PassedNodes)){
                g2.draw(new Ellipse2D.Double(plus,plusy,5,5));
                NodesPoints.add(new DesignNode(NodeID, plus, plusy));
                plus=plus+8;
                plusy=plusy+8;
            }
            point.x=point.x+100;
            point.y=point.y+100;
            
        }
        for(Flow fl:FLows){
            int i=0;
            int j=0;
            int size=fl.GetAddedNodesList().size();
            Point2D p=null;
            for(Integer NodeID:fl.GetAddedNodesList()){
                
                if(i==0){
                    p=GetPoint(NodeID, NodesPoints);
                    i=1;
                }else if(i==1){
                    g2.draw(new Line2D.Double(p, GetPoint(NodeID, NodesPoints)));
                    i=0;
                }
              
//                if((j+1)==size){
//                    g2.draw(new Line2D.Double(GetPoint(fl.GetAddedNodesList().get(size-1),NodesPoints),GetPoint(NodeID, NodesPoints)));
//                }
//                j++;
            }
             g2.draw(new Line2D.Double(GetPoint(fl.GetAddedNodesList().get(size-1),NodesPoints),GetPoint(fl.GetAddedNodesList().get(size-2), NodesPoints)));
            //g2.draw(new Line2D.Double(GetPoint(fl.GetAddedNodesList(), NodesPoints)
        }
        }catch(Throwable e){
       e.printStackTrace();
        }

    }
    
    
    private Point2D GetPoint(int NodeID,List<DesignNode> NodesPoints){
         Point2D.Double point = new Point2D.Double(); 
         for(DesignNode Node:NodesPoints){
             if(Node.GetDesignNodeID()==NodeID){
                point.x=Node.GetXpoint();
                point.y=Node.GetYpoint();
                return point;
             }
                 
         }
         return null;
         
    }
    public List<Integer> GetIndependentNodes(int AreaID,List<Integer> PassedNodes){
        DbConnection db=new DbConnection();
        Connection conn=db.Connect();
        List<Integer> IndependentNodesIDs=new ArrayList<Integer>();
        try{
            ResultSet rs=db.SelectFromDb(TableNames.GeolocationDb,"WHERE NeighbourID="+AreaID, conn);
            while(rs.next()){
                int NodeID=rs.getInt("NodeID");
               if (!PassedNodes.contains(NodeID)){
                   PassedNodes.add(NodeID);
                   if(CheckIfNodeIntermediate(NodeID, conn, db)){
                       IndependentNodesIDs.add(NodeID); 
                   }
               }
            }
            conn.close();
            //return IndependentNodesIDs;
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return IndependentNodesIDs;
    }
    
    
    
    private boolean CheckIfNodeIntermediate(int NodeID,Connection conn,DbConnection db){
        try {
            ResultSet rs=db.SelectFromDb(TableNames.Nodes, "Where ID="+NodeID, conn);
            while(rs.next()){
                if(rs.getInt("Area_flag")>0){
                    return false;
                }else{
                    return true;
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(PanelDraw.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public List<Integer> GetContainingNodes(int AreaID){
      DbConnection db=new DbConnection();
    Connection conn=db.Connect();
    List<Integer> NodesIDs=new ArrayList<Integer>();   
    try{
    ResultSet rs=db.SelectFromDb(TableNames.Nodes,"WHERE Area_ID="+AreaID, conn);
    while(rs.next()){
        NodesIDs.add(rs.getInt("ID"));
    }
    conn.close();
    }catch(SQLException e){
        e.printStackTrace();
    }
    return NodesIDs;
    }
    
    
    public List<Integer> GetAreas(){
    DbConnection db=new DbConnection();
    Connection conn=db.Connect();
    List<Integer> AreasIDs=new ArrayList<Integer>();
    try{
    ResultSet rs=db.SelectFromDb(TableNames.Areas,"", conn);
    while(rs.next()){
        AreasIDs.add(rs.getInt("ID"));
    }
    conn.close();
    }catch(SQLException e){
        e.printStackTrace();
    }
    return AreasIDs;
    }
    
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}