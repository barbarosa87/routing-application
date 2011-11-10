/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Msg;

import java.sql.Connection;
import routing.DbConnection;


/**
 *
 * @author barbarosa
 */
public class RREQ {
    private boolean  BroadCast;
            
    
    
    public RREQ(boolean BroadCast){
                this.BroadCast=BroadCast;
                AddToDb();
            }
    
    public final void AddToDb(){
        DbConnection db=new DbConnection();
        Connection conn=db.Connect();
        if (BroadCast){
            
        }else{
            
        }
            
    }
    
}
