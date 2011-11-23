/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Test;

import com.sun.rowset.CachedRowSetImpl;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author barbarosa
 */
public class DbConnection_new {
public DbConnection_new()
{
InitializeConnection();
}
    
    public void InitializeConnection(){
        
            try {
                org.sqlite.JDBC jd=new org.sqlite.JDBC();
                CachedRowSetImpl cac=new CachedRowSetImpl();
                //cac.setSyncProvider("org.sqlite.JDBC");                
                cac.setUrl("jdbc:sqlite:db.sqlite");
                cac.setCommand("SELECT * from Nodes");
                cac.execute();
                while(cac.next()){
                    System.out.println(cac.getInt("ID"));
                }
                //Class.forName("org.sqlite.JDBC");
            } catch (SQLException ex) {
                Logger.getLogger(DbConnection_new.class.getName()).log(Level.SEVERE, null, ex);
            }
            
    }
        
    public static void main(String args[]){
        new DbConnection_new();
    }
}
