/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import routing.Enumerators.TableNames;

/**
 *
 * @author barbarosa
 */
public class IterateChannels {
         
    
    public static void ChangeFrequencies(CachedRowSetImpl Nodes){
        
        
       DbConnection db=new DbConnection();
       Connection conn=db.Connect();
       try{
           if (conn==null){
               conn=db.Connect();
           }
           if (conn.isClosed()){
               conn=db.Connect();
           }
           
           ResultSet NotConnectedNodesRs=db.SelectFromDb(TableNames.NodesWeight, "WHERE Connected=0", conn);
           while (NotConnectedNodesRs.next()){
               ResultSet NodesRS=db.SelectFromDb(TableNames.Node,"WHERE ID=" + NotConnectedNodesRs.getInt("NodeID"), conn);
               //To Anevazw kata 1 mexri ta oria mporei na to kanw kai random na ginetai i epilogi sixnotitas 
               db.UpdateTableColumnValue(TableNames.Node, "Frequency", NodesRS.getInt("Frequency")+1, "WHERE ID=" +NodesRS.getInt("ID"), conn);
           }
           
           if (!conn.isClosed()){
               conn.close();
           }
       } catch(SQLException ex){
          System.out.println("Database is locked");
          
       }
    }
}
