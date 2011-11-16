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
   List Flows=new ArrayList<Structs.Flow>();
   
   
   public StartCommunication(List SourcesList,List DestinationList){
        this.SourcesList=SourcesList;
        this.DestinationList=DestinationList;
        Start();
    }
    
    
   public final void Start(){
    int ReplayNodeID=0;
       for (int i=0;i<SourcesList.size();i++){
           for (int j=0;j<10;j++){
               ReplayNodeID=BroadCastMessage((Integer)SourcesList.get(i),0);
               if (ReplayNodeID>0){
                   //Send RREP
                   //AND Make Connected
                   break;
               }else{
                   IterateChannels.ChangeFrequencies(); 
                   continue;
               }
           }
       }
    }
    
    
    
   public int BroadCastMessage(int NodeID,int i){
        DbConnection db=new DbConnection();
        Connection conn=db.Connect(); 
        RREQ broadcast=new RREQ(true,conn,NodeID,255);
        int Destination=GetReplyFromBroadCast(broadcast,db,conn);
        return Destination;              
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
                return -1;
            }
        }

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return 0;
   }
   
   public void MakeNodeConnected(int NodeID){
       
   }
   
   
   
   
   public void SendRREP(int SourceNodeID,int DestNodeID){
       
       
   }
   
   
   
   
   
}
