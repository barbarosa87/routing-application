/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author spyros
 */
public class IterateChannels {
         
    
    public static void ChangeFrequencies(){
       DbConnection db=new DbConnection();
       Connection conn=db.ReturnConnectionObject();
       try{
           if (conn==null){
               conn=db.Connect();
           }
           if (conn.isClosed()){
               conn=db.Connect();
           }
           
           ResultSet NotConnectedNodesRs=db.SelectFromDbWithClause(EnumeRators.NodesWeight, "WHERE Connected=0", conn);
           while (NotConnectedNodesRs.next()){
               ResultSet NodesRS=db.SelectFromDbWithClause(EnumeRators.Node,"WHERE ID=" + NotConnectedNodesRs.getInt("NodeID"), conn);
               //To Anevazw kata 1 mexri ta oria mporei na to kanw kai random na ginetai i epilogi sixnotitas 
               db.UpdateTableColumnValue(EnumeRators.Node, "Frequency", NodesRS.getInt("Frequency")+1, "WHERE ID=" +NodesRS.getInt("ID"), conn);
               
           }
           
           if (!conn.isClosed()){
               conn.close();
           }
       } catch(SQLException ex){
          System.out.println("Database is locked");
          
       }
    }
}
