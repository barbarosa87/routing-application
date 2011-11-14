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
import java.util.logging.Level;
import java.util.logging.Logger;
import routing.Msg.RREQ;
/**
 *
 * @author barbarosa
 */


//TODO CREATE METRICS !!!!!!!!!!!
public class StartCommunication {
    
    List SourcesList=new ArrayList<Integer>();
    List DestinationList=new ArrayList<Integer>();
    List Flows=new ArrayList<Structs.Flow>();
    public StartCommunication(List SourcesList,List DestinationList){
        this.SourcesList=SourcesList;
        this.DestinationList=DestinationList;
        Start();
    }
    
    
    public final void Start(){
    for (int i=0;i<SourcesList.size();i++){
        BroadCastMessage((Integer)SourcesList.get(i),0);
       }
    }
    
    
    
    public void BroadCastMessage(int NodeID,int i){
        DbConnection db=new DbConnection();
        Connection conn=db.Connect(); 
        RREQ broadcast=new RREQ(true,conn,NodeID,255);
        int Destination=GetReplyFromBroadCast(broadcast,db,conn);
      
       
        if (Destination>0){
          System.out.println("GOAL");
        }else{
                IterateChannels.ChangeFrequencies();
                BroadCastMessage(NodeID,i+1);
                try{
                conn.close();
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
        }
    }


   public int GetReplyFromBroadCast(RREQ Broadcast,DbConnection db,Connection conn){
        try{
        ResultSet NeighBoursRs=db.SelectFromDbWithClause(EnumeRators.GeolocationDb, "WHERE NeighbourID=" + Broadcast.SourceID, conn);
        while (NeighBoursRs.next()){
            ResultSet IntermediateRs=db.SelectFromDbWithClause(EnumeRators.Node, "WHERE ID=" + NeighBoursRs.getInt("NodeID"), conn);
            ResultSet SourceRs=db.SelectFromDbWithClause(EnumeRators.Node, "WHERE ID=" + NeighBoursRs.getInt("NeighbourID"), conn);
            IntermediateRs.next();
            SourceRs.next();
            if (IntermediateRs.getInt("Frequency")==SourceRs.getInt("Frequency")){
                //Make Node Connected And Put To Flow
                
                return IntermediateRs.getInt("ID");
            }else {
                //return -1;
            }
        }
        
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return 0;
   }
}
