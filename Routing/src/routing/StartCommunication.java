/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;


import com.sun.rowset.CachedRowSetImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import routing.Enumerators.TableNames;
import routing.Structs.FlowStruct;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import routing.Enumerators.ReplyCommands;
import routing.Enumerators.ReturnType;
import routing.Msg.RREQ;
import routing.Msg.RREP;
import routing.conf.*;
/**
 *
 * @author barbarosa
 */


//TODO CREATE METRICS !!!!!!!!!!!
public class StartCommunication {
    
    
    private CachedRowSetImpl Nodes;
    private CachedRowSetImpl NodesWeight;
    private CachedRowSetImpl GeolocationDb;
    private int RedirectNodeID=-1;
    private int ReplayNodeID=-1;
    private List Flows=new ArrayList<FlowStruct.Flow>();
    private Map<Integer,Integer> SourceDestinationMap=new HashMap();
   
   
    
    public StartCommunication(Map SourceDestinationMap){
       
        this.SourceDestinationMap=SourceDestinationMap;
        GetRowSets();
        Start();
       
    }
    
   
   public final void GetRowSets(){
       DbConnection db=new DbConnection(ReturnType.CachedRowSet);
        try {
            Nodes=(CachedRowSetImpl)db.SelectFromDb(TableNames.Node, null, null, ReturnType.CachedRowSet);
            NodesWeight=(CachedRowSetImpl)db.SelectFromDb(TableNames.NodesWeight, null, null, ReturnType.CachedRowSet);
            //MessageExchange=(CachedRowSetImpl)db.SelectFromDb(TableNames.MessageExchange, null, null, ReturnType.CachedRowSet);
            GeolocationDb=(CachedRowSetImpl)db.SelectFromDb(TableNames.GeolocationDb, null, null, ReturnType.CachedRowSet);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
       
   }
   
   public final void Start(){
       for (Map.Entry<Integer,Integer> entry:SourceDestinationMap.entrySet()){
           InitializeFlow(entry.getKey());
           for (int j=0;j<RoutingCnf.getNumberOfBroadCastTries();j++){
               switch (BroadCastMessage(entry.getKey())){
                   case AddToFlow:
                       //MakeNodeConnected
                   MakeNodeConnected(ReplayNodeID);
                   //InitializeFlow
                   InitializeFlow(ReplayNodeID);
                   break;
                   case Redirect:
                       //RedirectFlow
                   case Unavailable:
                       IterateChannels.ChangeFrequencies(Nodes); 
                       break;
               }
               //ReplayNodeID=BroadCastMessage(entry.getKey());
               
              
           }
       }
    }
    
    
    
   public ReplyCommands BroadCastMessage(int SourceNodeID){
        RREQ broadcast=new RREQ(true,SourceNodeID,255);
        switch (GetReplyFromBroadCast(broadcast)){
            case AddToFlow:return ReplyCommands.AddToFlow;
            case Redirect:return ReplyCommands.Redirect;
            case Unavailable:return ReplyCommands.Unavailable;
            default: return ReplyCommands.Unavailable;
        }
            
        //int Destination=GetReplyFromBroadCast(broadcast);
        
        }
    
   public ReplyCommands GetReplyFromBroadCast(RREQ Broadcast){
       DbConnection db=new DbConnection(ReturnType.CachedRowSet);
        try {
            CachedRowSetImpl NeighboursRs=(CachedRowSetImpl)db.SelectFromDb(TableNames.GeolocationDb, "WHERE NeighbourID=" + Broadcast.SourceID,null,ReturnType.CachedRowSet);
            while (NeighboursRs.next()){
                CachedRowSetImpl IntermediateRs=(CachedRowSetImpl)db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighboursRs.getInt("NodeID"), null, ReturnType.CachedRowSet);
                CachedRowSetImpl SourceRs=(CachedRowSetImpl)db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighboursRs.getInt("NeighbourID"), null, ReturnType.CachedRowSet);
                IntermediateRs.next();
                SourceRs.next();
                if (IntermediateRs.getInt("Frequency")==SourceRs.getInt("Frequency")){
                    if(CheckIfConnected(IntermediateRs.getInt("Frequency"))){
                        int NonConnectedNode=GetNonConnectedNeighbourNodes(Broadcast.SourceID);
                         if (NonConnectedNode>0){
                      //SEND REPLY COMMAND FROM SPECIFIED NODE
                      //SendRREP(Broadcast.DestID, Broadcast.SourceID, true);
                      //Redirect(Broadcast.SourceID, NonConnectedNode);
                             RedirectNodeID=NonConnectedNode;
                             return ReplyCommands.Redirect;
                  }else{
                      //NO AVAILABLE NODE TO PASS FLOW
                      //System.out.println("No available node to pass flow from channel " + Broadcast.SourceID +" try again later/n");
                             RedirectNodeID=-1;
                             return ReplyCommands.Redirect;
                  }
                    }
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(StartCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ReplyCommands.Unavailable;
     
    }

//   public int GetReplyFromBroadCast(RREQ Broadcast,DbConnection db,Connection conn){
//        try{
//        ResultSet NeighBoursRs=(ResultSet)db.SelectFromDb(TableNames.GeolocationDb, "WHERE NeighbourID=" + Broadcast.SourceID, conn,ReturnType.ResultSet);
//        while (NeighBoursRs.next()){
//            ResultSet IntermediateRs=(ResultSet)db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighBoursRs.getInt("NodeID"), conn, ReturnType.ResultSet);
//            ResultSet SourceRs=(ResultSet)db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighBoursRs.getInt("NeighbourID"), conn,ReturnType.ResultSet);
//            IntermediateRs.next();
//            SourceRs.next();
//            if (IntermediateRs.getInt("Frequency")==SourceRs.getInt("Frequency")){
//               if (CheckIfConnected(IntermediateRs.getInt("ID"))){
//                 //REDIRECT  
//                   int NonConnectedNode=GetNonConnectedNeighbourNodes(Broadcast.SourceID);
//                  if (NonConnectedNode>0){
//                      //SEND REPLY COMMAND FROM SPECIFIED NODE
//                      SendRREP(Broadcast.DestID, Broadcast.SourceID, true, conn);
//                      Redirect(Broadcast.SourceID, NonConnectedNode);
//                  }else{
//                      //NO AVAILABLE NODE TO PASS FLOW
//                      System.out.println("No available node to pass flow from channel " + Broadcast.SourceID +" try again later/n");
//                  }
//                }    
//                return IntermediateRs.getInt("ID");
//            }else {
//                return -1;
//            }
//        }
//
//        }catch(SQLException ex){
//            ex.printStackTrace();
//        }
//        return 0;
//   }
   
   public void MakeNodeConnected(int NodeID){
       
   }
   
   public int GetNonConnectedNeighbourNodes(int SourceID){
       DbConnection db=new DbConnection();
       Connection conn=db.Connect();
        try {
            ResultSet NeighBoursRs=(ResultSet)db.SelectFromDb(TableNames.GeolocationDb, "WHERE NeighbourID=" + SourceID, conn, ReturnType.ResultSet);
            while (NeighBoursRs.next()){
                ResultSet WeightRS=(ResultSet)db.SelectFromDb(TableNames.NodesWeight, "WHERE NodeID=" + NeighBoursRs.getInt("NodeID"), conn, ReturnType.ResultSet);
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
           ResultSet rs=(ResultSet)db.SelectFromDb(TableNames.NodesWeight, "WHERE NodeID=" + NodeID +" AND Connected=1", conn, ReturnType.ResultSet);
           while(rs.next()){
               return true;
           }
       }catch(SQLException ex){
           ex.printStackTrace();
       }
          return false;
       
   }
   
   public void SendRREP(int SourceNodeID,int DestNodeID,boolean Redirect){
       RREP Reply=new RREP(SourceNodeID,DestNodeID,Redirect);
   }
   
   
   
   public void InitializeFlow(int SourceID){
       Flows.add(new FlowStruct.Flow("Flow"+SourceID,SourceID));
       
   }
   
   
   public void Redirect(int NodeID,int ToNodeID){
       //TODO START REDIRECT
   }
   
   
   
   
   
}
