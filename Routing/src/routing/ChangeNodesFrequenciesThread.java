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
 * @author barbarosa
 */

public class ChangeNodesFrequenciesThread implements Runnable {

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
               
           }
       } catch(SQLException ex){
           ex.printStackTrace();
       }
    }
    
}
