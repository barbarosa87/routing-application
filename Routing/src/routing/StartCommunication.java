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
import javax.sql.rowset.CachedRowSet;
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
    
//   List SourcesList=new ArrayList<Integer>();
//   List DestinationList=new ArrayList<Integer>();
    private CachedRowSetImpl Nodes;
    private CachedRowSetImpl NodesWeight;
    private CachedRowSetImpl MessageExchange;
    private CachedRowSetImpl GeolocationDb;
   List Flows=new ArrayList<FlowStruct.Flow>();
   private Map<Integer,Integer> SourceDestinationMap=new HashMap();
   
   public StartCommunication(Map SourceDestinationMap){
        //this.SourcesList=SourcesList;
        //this.DestinationList=DestinationList;
        this.SourceDestinationMap=SourceDestinationMap;
        GetRowSets();
        Start();
    }
    
   
   public final void GetRowSets(){
       DbConnection db=new DbConnection(ReturnType.CachedRowSet);
        try {
            Nodes=(CachedRowSetImpl)db.SelectFromDb(TableNames.Node, null, null, ReturnType.CachedRowSet);
            while(Nodes.next()){
                System.out.println(Nodes.getInt("ID"));
            }
           
        } catch (SQLException ex) {
            Logger.getLogger(StartCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
       
   }
   
   
    
   public final void Start(){
       int ReplayNodeID=0;
       for (Map.Entry<Integer,Integer> entry:SourceDestinationMap.entrySet()){
           for (int j=0;j<RoutingCnf.getNumberOfBroadCastTries();j++){
               //HERE I AM
               ReplayNodeID=BroadCastMessage(entry.getKey());
               
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
        ResultSet NeighBoursRs=(ResultSet)db.SelectFromDb(TableNames.GeolocationDb, "WHERE NeighbourID=" + Broadcast.SourceID, conn,ReturnType.ResultSet);
        while (NeighBoursRs.next()){
            ResultSet IntermediateRs=(ResultSet)db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighBoursRs.getInt("NodeID"), conn, ReturnType.ResultSet);
            ResultSet SourceRs=(ResultSet)db.SelectFromDb(TableNames.Node, "WHERE ID=" + NeighBoursRs.getInt("NeighbourID"), conn,ReturnType.ResultSet);
            IntermediateRs.next();
            SourceRs.next();
            if (IntermediateRs.getInt("Frequency")==SourceRs.getInt("Frequency")){
               if (CheckIfConnected(IntermediateRs.getInt("ID"))){
                 //REDIRECT  
                   int NonConnectedNode=GetNonConnectedNeighbourNodes(Broadcast.SourceID);
                  if (NonConnectedNode>0){
                      //SEND REPLY COMMAND FROM SPECIFIED NODE
                      SendRREP(Broadcast.DestID, Broadcast.SourceID, true, conn);
                      Redirect(Broadcast.SourceID, NonConnectedNode);
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
   
   public void SendRREP(int SourceNodeID,int DestNodeID,boolean Redirect,Connection conn){
       RREP Reply=new RREP(SourceNodeID,DestNodeID,Redirect,conn);
   }
   
   
   
   public void InitializeFlow(int SourceID){
       Flows.add(new FlowStruct.Flow("Flow"+SourceID));
   }
   
   
   public void Redirect(int NodeID,int ToNodeID){
       //TODO START REDIRECT
   }
   
   
   
   
   
}
