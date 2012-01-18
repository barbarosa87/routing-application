/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;


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
import routing.Msg.RREQ;
import routing.Msg.RREP;
import routing.conf.*;
/**
 *
 * @author barbarosa
 */


//TODO CREATE METRICS !!!!!!!!!!!
public class StartCommunication extends Calculate{
    
    
    
    private int FlowNode=-1;
    private int DestinationNode=-1;
    private List Flows=new ArrayList<FlowStruct.Flow>();
    private List StaticFlow=new ArrayList<Integer>();
    private Map<Integer,Integer> SourceDestinationMap=new HashMap();
    private int NumberOfContendingNodes=1;
    private int StartBand;
    private int DestBand;
    private boolean FinalNode=false;
    private boolean FinalNotSameFrequency=false;
    
    
    
    
    public StartCommunication(Map SourceDestinationMap){
        this.SourceDestinationMap=SourceDestinationMap;
        //Start();
    }
    
   public final void Start(){
       for (Map.Entry<Integer,Integer> entry:SourceDestinationMap.entrySet()){
           FlowStruct.Flow fl=InitializeFlow(entry.getKey());
           MakeNodeConnected(entry.getKey());
           StaticFlow.add(entry.getKey());
           int SourceID=entry.getKey();
           DestinationNode=entry.getValue();
           boolean added=false;
           for (int j=0;j<RoutingCnf.getNumberOfBroadCastTries();j++){
               if(FlowNode==DestinationNode){break;}
               switch (BroadCastMessage(SourceID)){
                   case AddToFlow:
                   StaticFlow.add(FlowNode);
                   MakeNodeConnected(FlowNode);
                   fl.addNodeToFlow(FlowNode);
                   added=true;
                   fl.CalculateND(super.getSwitchingDelay(GetNodeFrequency(FlowNode), GetNodeFrequency(SourceID)),super.getNodeBlock());
                   break;
                   case Redirect:
                       fl.CalculateND(super.getSwitchingDelay(GetNodeFrequency(FlowNode), GetNodeFrequency(SourceID)),super.getNodeBlock());
                       ChangeFrequency(SourceID,FlowNode);
                       StaticFlow.add(FlowNode);                       
                       MakeNodeConnected(FlowNode);
                       fl.addNodeToFlow(FlowNode);
                       added=true;
                       break;
                   case Unavailable:
                       IterateChannels.ChangeFrequencies(); 
                       break;
               }
               if(added){
                   if (fl.GetAddedNodesList().get(fl.GetAddedNodesList().size()-1)==entry.getValue()){
//                for(Integer i:fl.GetAddedNodesList()){
//                System.out.println(i);
//                }
                   break;
                   }
                   else{
                   SourceID=FlowNode;
                   j=0;
                   added=false;
                   continue;
                   }
                   
               }else{
                   continue;
               }
               
           }
           
       }
       PrintResults pr=new PrintResults(Flows);
    }
    
    
   private void ChangeFrequency(int SourceNodeID,int DestinationNodeID){
       DbConnection db=new DbConnection();
       Connection conn=db.Connect();
       try {
            ResultSet SourceRs=db.SelectFromDb(TableNames.Nodes, "WHERE ID="+SourceNodeID, conn);
            SourceRs.next();
            db.UpdateTableColumnValue(TableNames.Nodes,"Frequency", SourceRs.getInt("Frequency"),  "Where ID="+DestinationNodeID, conn);
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(StartCommunication.class.getName()).log(Level.SEVERE, null, ex);
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
    
        }
    
   public ReplyCommands GetReplyFromBroadCast(RREQ Broadcast){
       DbConnection db=new DbConnection();
        try {
            Connection conn=db.Connect();
            ResultSet NeighboursRs=db.SelectFromDb(TableNames.GeolocationDb, "WHERE NeighbourID=" + Broadcast.SourceID,conn);
                     //Get ContendingNeighbourNodes
                    ResultSet ContendingNodes =(ResultSet)db.GetCountFromDB(TableNames.GeolocationDb, "WHERE NeighbourID=" + + Broadcast.SourceID, conn);
                    ContendingNodes.next();
                    NumberOfContendingNodes=ContendingNodes.getInt("Count(*)");
            while (NeighboursRs.next()){
                ResultSet IntermediateRs=db.SelectFromDb(TableNames.Nodes, "WHERE ID=" + NeighboursRs.getInt("NodeID"),conn);
                ResultSet SourceRs=db.SelectFromDb(TableNames.Nodes, "WHERE ID=" + NeighboursRs.getInt("NeighbourID"),conn);
                IntermediateRs.next();
                SourceRs.next();
                ResultSet CheckIfConnectedCount =(ResultSet)db.GetCountFromDB(TableNames.NodesWeight, "WHERE NodeID=" + IntermediateRs.getInt("ID") +" AND Connected=1", conn);
                CheckIfConnectedCount.next();
                if (StaticFlow.contains(IntermediateRs.getInt("ID"))){
                    continue;
                }
              
                //TODO Check for added node
                if ((IntermediateRs.getInt("Frequency")==SourceRs.getInt("Frequency"))){
                    //ResultSet CheckIfConnectedCount =(ResultSet)db.GetCountFromDB(TableNames.NodesWeight, "WHERE NodeID=" + IntermediateRs.getInt("ID") +" AND Connected=1", conn);
                    //CheckIfConnectedCount.next();
                    if(CheckIfConnectedCount.getInt("Count(*)")>0){
                        int NonConnectedNode=GetNonConnectedNeighbourNodes(Broadcast.SourceID,conn);
                         if (NonConnectedNode>0){
                             FlowNode=NonConnectedNode;
                             conn.close();
                             return ReplyCommands.Redirect;
                  }else{
                             FlowNode=-1;
                             conn.close();
                             return ReplyCommands.Unavailable;
                  }
                    }else{
                        FlowNode=IntermediateRs.getInt("ID");
                        conn.close();
                        return ReplyCommands.AddToFlow;
                    }
                }else  if (IntermediateRs.getInt("Area_flag")>0){
                    
                    FlowNode=IntermediateRs.getInt("ID");
                    conn.close();
                    return ReplyCommands.AddToFlow;
                 }else{
                  conn.close();
                  return ReplyCommands.Unavailable;
                }
            }
            conn.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(StartCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ReplyCommands.Unavailable;
     
    }

   
   public void MakeNodeConnected(int NodeID){
     DbConnection db=new DbConnection();
     Connection conn=db.Connect();
        try {
            db.UpdateTableColumnValue(TableNames.NodesWeight, "Connected", 1, "WHERE NodeID=" + NodeID, conn);
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(StartCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   public int GetNonConnectedNeighbourNodes(int SourceID,Connection conn){
       DbConnection db=new DbConnection();

        try {
            ResultSet NeighBoursRs=db.SelectFromDb(TableNames.GeolocationDb, "WHERE NeighbourID=" + SourceID, conn);
            while (NeighBoursRs.next()){
                ResultSet WeightRS=db.SelectFromDb(TableNames.NodesWeight, "WHERE NodeID=" + NeighBoursRs.getInt("NodeID"), conn);
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
   

   
   public int GetNodeFrequency(int NodeID){
      DbConnection db=new DbConnection();
      Connection conn=db.Connect();     
      try{
             ResultSet rs=db.SelectFromDb(TableNames.Nodes, "WHERE ID="+NodeID, conn);
             rs.next();
             int Ch=rs.getInt("Frequency");
             conn.close();
             return RoutingCnf.getFrequencyFromChannel(Ch);
         }catch(SQLException ex){
             ex.printStackTrace();
         }
      return 0;
        
   }
   
   public void SendRREP(int SourceNodeID,int DestNodeID,boolean Redirect){
       RREP Reply=new RREP(SourceNodeID,DestNodeID,Redirect);
   }
   
   
   
   public FlowStruct.Flow InitializeFlow(int SourceID){
       FlowStruct.Flow fl=new FlowStruct.Flow("Flow"+SourceID, SourceID);
       Flows.add(fl);
       return fl;
   }
   
   
   
   
   
   
   
   
}
