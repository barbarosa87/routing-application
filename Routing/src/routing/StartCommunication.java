/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import routing.Msg.RREQ;
/**
 *
 * @author barbarosa
 */


//TODO CREATE METRICS !!!!!!!!!!!
public class StartCommunication {
    
    List SourcesList=new ArrayList<Integer>();
    List DestinationList=new ArrayList<Integer>();
    
    public StartCommunication(List SourcesList,List DestinationList){
        this.SourcesList=SourcesList;
        this.DestinationList=DestinationList;
        Start();
    }
    
    
    public final void Start(){
    for (int i=0;i<SourcesList.size();i++){
        BroadCastMessage((Integer)SourcesList.get(i));
       }
    }
    
    
    
    public void BroadCastMessage(int NodeID){
        RREQ broadcast=new RREQ(true,NodeID,255);
    }


   public int GetReplyFromBroadCast(RREQ broadcast){
        DbConnection db=new DbConnection();
        Connection conn=db.Connect(); 
        ResultSet NeighBoursRs=db.SelectFromDbWithClause(EnumeRators.GeolocationDb, "WHERE NeighbourID=" + broadcast.SourceID, conn);
        try{
        while (NeighBoursRs.next()){
            ResultSet IntermediateRs=db.SelectFromDbWithClause(EnumeRators.Node, "WHERE ID=" + NeighBoursRs.getInt("NodeID")+"AND Area_flag=0", conn);
            ResultSet SourceRs=db.SelectFromDbWithClause(EnumeRators.Node, "WHERE ID=" + NeighBoursRs.getInt("NeighbourID")+"AND Area_flag=0", conn);
            if (IntermediateRs.getInt("Frequency")==SourceRs.getInt("Frequency")){
                //Make Node Connected And Put To Flow
            }else {
                //Change Neighbours Frequencies
            }
            
        }   
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return 0;
   }
}
