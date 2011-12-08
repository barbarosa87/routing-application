/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import routing.Enumerators.ReturnType;
import routing.Enumerators.TableNames;

/**
 *
 * @author barbarosa
 */
public class FixDbErrors {
    public FixDbErrors(){
        boolean Status=true;
        
    }
    
    public boolean FixNodes(){
    return true;    
    }
    public boolean FixAreas(){
    return true;    
    }
    public void FixAreaFrequencies() throws SQLException{
    DbConnection db=new DbConnection();
    Connection conn=db.Connect();
    ResultSet rs=(ResultSet)db.SelectFromDb(TableNames.Area, "", conn, ReturnType.ResultSet);
    while (rs.next()){
        ResultSet rs2=(ResultSet)db.SelectFromDb(TableNames.AreaFrequencies, "WHERE ID="+rs.getInt("ID"), conn, ReturnType.ResultSet);
        if(rs2.next()){
            
        }
    }
    }
    public boolean FixGeolocationDb(){
        return true;
    }
    public boolean FixNodesWeight(){
        return true;
    }
    
    
    
}
