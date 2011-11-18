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
import routing.Msg.RREP;
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
               ReplayNodeID=BroadCastMessage((Integer)SourcesList.get(i));
               if (ReplayNodeID>0){
                   //SendRREP TO CORRECT
                   //SendRREP(ReplayNodeID,(Integer)SourcesList.get(i));
                   //MakeNodeConnected
                   MakeNodeConnected(ReplayNodeID);
                   //InitializeFlow
                   InitializeFlow(ReplayNodeID);
                   break;
               }else{
                   IterateChannels.ChangeFrequencies(); 
                   continue;
               }
           }
       }
    }
    
    
    
   public int BroadCastMessage(int NodeID){
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
               if (CheckIfConnected(IntermediateRs.getInt("ID"))){
                 //REDIRECT  
                  if (GetNonConnectedNeighbourNodes(Broadcast.SourceID)>0){
                      //SEND REPLY COMMAND FROM SPECIFIED NODE
                      //SendRREP(Broadcast.DestID, Broadcast.SourceID, true, conn);
                  }else{
                      //NO AVAILABLE NODE TO PASS FLOW
                      System.out.println("No available node to pass flow from channel " + Broadcast.SourceID +" try again later/n");
                  }
                }    
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
   
   public int GetNonConnectedNeighbourNodes(int SourceID){
       DbConnection db=new DbConnection();
       Connection conn=db.Connect();
        try {
            ResultSet NeighBoursRs=db.SelectFromDbWithClause(EnumeRators.GeolocationDb, "WHERE NeighbourID=" + SourceID, conn);
            while (NeighBoursRs.next()){
                ResultSet WeightRS=db.SelectFromDbWithClause(EnumeRators.NodesWeight, "WHERE NodeID=" + NeighBoursRs.getInt("NodeID"), conn);
                WeightRS.next();
                if (WeightRS.getInt("Connected")==0){
                    return WeightRS.getInt("NodeID");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
       return 0;
       
       
   }
   
   public boolean CheckIfConnected(int NodeID){
       try {
           DbConnection db=new DbConnection();
           Connection conn=db.Connect();
           ResultSet rs=db.SelectFromDbWithClause(EnumeRators.NodesWeight, "WHERE NodeID=" + NodeID +" AND Connected=1", conn);
           while(rs.next()){
               return true;
           }
           
       }catch(SQLException ex){
           ex.printStackTrace();
       }
          return false;
       
   }
   
   public void SendRREP(int SourceNodeID,int DestNodeID,boolean Redirect,Connection conn){
       RREP Reply=new RREP(SourceNodeID,DestNodeID,Redirect,conn);
   }
   
   
   
   public void InitializeFlow(int SourceID){
       Flows.add(new Structs.Flow("Flow"+SourceID));
   }
   
   
   public void Redirect(int NodeID,int ToNodeID){
       
   }
   
   
   
   
   
}
