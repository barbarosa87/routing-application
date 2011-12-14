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
    private int FlowNode=-1;
    private int ReplayNodeID=-1;
    private int DestinationNode=-1;
    private List Flows=new ArrayList<FlowStruct.Flow>();
    private List StaticFlow=new ArrayList<Integer>();
    private Map<Integer,Integer> SourceDestinationMap=new HashMap();
    private List<Integer> CalculationNodeDelay=new ArrayList<Integer>();
    private List<Integer> NodeSwitchingDelay=new ArrayList<Integer>();
    private List<Integer> NodeBackOffDelay=new ArrayList<Integer>();
    private List<Integer> PathBackOffDelay=new ArrayList<Integer>();
    private List<Integer> PathSwitchingDelay=new ArrayList<Integer>();
    private int NumberOfContendingNodes=1;
    
    
    
    public StartCommunication(Map SourceDestinationMap){
        this.SourceDestinationMap=SourceDestinationMap;
        //GetRowSets();
        Start();
    }
    
   
//   public final void GetRowSets(){
//       DbConnection db=new DbConnection();
//        try {
//            Nodes=(CachedRowSetImpl)db.SelectFromDb(TableNames.Node, null, null, ReturnType.CachedRowSet);
//            NodesWeight=(CachedRowSetImpl)db.SelectFromDb(TableNames.NodesWeight, null, null, ReturnType.CachedRowSet);
//            //MessageExchange=(CachedRowSetImpl)db.SelectFromDb(TableNames.MessageExchange, null, null, ReturnType.CachedRowSet);
//            GeolocationDb=(CachedRowSetImpl)db.SelectFromDb(TableNames.GeolocationDb, null, null, ReturnType.CachedRowSet);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//       
//   }
   
   public final void Start(){
       for (Map.Entry<Integer,Integer> entry:SourceDestinationMap.entrySet()){
           FlowStruct.Flow fl=InitializeFlow(entry.getKey());
           MakeNodeConnected(entry.getKey());
           StaticFlow.add(entry.getKey());
           int SourceID=entry.getKey();
           DestinationNode=entry.getValue();
           //Node Delay
           Map<Integer, List> NodeDelay=new HashMap<Integer, List>();
           //Path Delay
           boolean added=false;
           for (int j=0;j<RoutingCnf.getNumberOfBroadCastTries();j++){
               switch (BroadCastMessage(SourceID)){
                   case AddToFlow:
                   StaticFlow.add(FlowNode);
                   MakeNodeConnected(FlowNode);
                   fl.addNodeToFlow(FlowNode);
                   added=true;
                   List<Double> Delays=new ArrayList<Double>();
                   Delays.add(0.0);
                   Delays.add(Calculations.GetNodeBackOffDelay(NumberOfContendingNodes));
                   NodeDelay.put(FlowNode,Delays);
                   //System.out.println("Node "+ String.valueOf(FlowNode)+"Node BackoffDelay: "+Calculations.GetNodeBackOffDelay(fl.GetAddedNodesList().size())+" Node SwitchingDelay:0");
                   break;
                   case Redirect:
                       //TODO REDIRECTION
                       //TODO METRICS
                       ChangeFrequency(SourceID,RedirectNodeID);
                       StaticFlow.add(RedirectNodeID);                       
                       MakeNodeConnected(RedirectNodeID);
                       fl.addNodeToFlow(RedirectNodeID);
                       added=true;
                       break;
                   case Unavailable:
                       IterateChannels.ChangeFrequencies(Nodes); 
                       break;
               }
               if(added){
                   if (fl.GetAddedNodesList().get(fl.GetAddedNodesList().size()-1)==entry.getValue()){
                for(Integer i:fl.GetAddedNodesList()){
                System.out.println(i);
                }
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
    }
    
    
   private void ChangeFrequency(int SourceNodeID,int DestinationNodeID){
       DbConnection db=new DbConnection();
       Connection conn=db.Connect();
       try {
            ResultSet SourceRs=db.SelectFromDb(TableNames.Node, "WHERE ID="+SourceNodeID, conn);
            SourceRs.next();
            db.UpdateTableColumnValue(TableNames.Node,"Frequency", SourceRs.getInt("Frequency"),  "Where ID="+DestinationNodeID, conn);
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
                ResultSet IntermediateRs=db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighboursRs.getInt("NodeID"),conn);
                ResultSet SourceRs=db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighboursRs.getInt("NeighbourID"),conn);
                IntermediateRs.next();
                SourceRs.next();
                if (StaticFlow.contains(IntermediateRs.getInt("ID"))){
                    continue;
                }
                if(IntermediateRs.getInt("ID")==DestinationNode){
                    FlowNode=IntermediateRs.getInt("ID");
                    conn.close();
                    return ReplyCommands.AddToFlow;
                }
                  
                //TODO Check for added node
                if ((IntermediateRs.getInt("Frequency")==SourceRs.getInt("Frequency"))){
                    ResultSet CheckIfConnectedCount =(ResultSet)db.GetCountFromDB(TableNames.NodesWeight, "WHERE NodeID=" + IntermediateRs.getInt("ID") +" AND Connected=1", conn);
                    CheckIfConnectedCount.next();
                    if(CheckIfConnectedCount.getInt("Count(*)")>0){
                        int NonConnectedNode=GetNonConnectedNeighbourNodes(Broadcast.SourceID,conn);
                         if (NonConnectedNode>0){
                             RedirectNodeID=NonConnectedNode;
                             conn.close();
                             return ReplyCommands.Redirect;
                  }else{
                             RedirectNodeID=-1;
                             conn.close();
                             return ReplyCommands.Unavailable;
                  }
                    }else{
                        FlowNode=IntermediateRs.getInt("ID");
                        conn.close();
                        return ReplyCommands.AddToFlow;
                    }
                }else {
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
//       Connection conn=db.Connect();
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
   
//   public boolean CheckIfConnected(int NodeID){
////       try {
//////           DbConnection db=new DbConnection(ReturnType.CachedRowSet);
//////           //Connection conn=db.Connect();
//////           CachedRowSetImpl rs=(CachedRowSetImpl)db.SelectFromDb(TableNames.NodesWeight, "WHERE NodeID=" + NodeID +" AND Connected=1", null, ReturnType.CachedRowSet);
//////           while(rs.next()){
//////               return true;
////////           }
//////           while(NodesWeight.next()){
//////                  if (NodesWeight.getBoolean("Connected")&&NodesWeight.getInt("NodeID")==NodeID){
//////                         return true;
//////                      }
//////           }
//////           NodesWeight.first();
////       }catch(SQLException ex){
////           ex.printStackTrace();
////       }
//          return false;
//       
//       
//       
//   }
   
   public void SendRREP(int SourceNodeID,int DestNodeID,boolean Redirect){
       RREP Reply=new RREP(SourceNodeID,DestNodeID,Redirect);
   }
   
   
   
   public FlowStruct.Flow InitializeFlow(int SourceID){
       FlowStruct.Flow fl=new FlowStruct.Flow("Flow"+SourceID, SourceID);
       Flows.add(fl);
       return fl;
   }
   
   
   public void Redirect(int NodeID,int ToNodeID){
       //TODO START REDIRECT
   }
   
   
   
   
   
}
