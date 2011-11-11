/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimerTask;

/**
 *
 * @author barbarosa
 */

public class ChangeNodesFrequenciesThread extends TimerTask  {

    @Override
    public void run() {
       ChangeFrequency();
    }
    
    public void ChangeFrequency(){
        DbConnection db=new DbConnection();
        Connection conn=db.Connect();
       try{
           ResultSet NotConnectedNodesRs=db.SelectFromDbWithClause(EnumeRators.NodesWeight, "WHERE Connected=0", conn);
           while (NotConnectedNodesRs.next()){
               ResultSet NodesRS=db.SelectFromDbWithClause(EnumeRators.Node,"WHERE ID=" + NotConnectedNodesRs.getInt("NodeID"), conn);
               db.UpdateTableColumnValue(EnumeRators.Node, "Frequency", NodesRS.getInt("Frequency")+1, "WHERE ID=" +NodesRS.getInt("ID"), conn);
               //To Anevazw kata 1 mexri ta oria mporei na to kanw kai random na ginetai i epilogi sixnotitas 
           }
       } catch(SQLException ex){
           ex.printStackTrace();
       }
    }
    
}
