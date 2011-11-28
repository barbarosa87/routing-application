/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Msg;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import routing.DbConnection;
import routing.Enumerators.TableNames;


/**
 *
 * @author barbarosa
 */
public class RREQ {
    public int ID;
    private boolean  BroadCast;
    public int SourceID,DestID;
    //For BroadCast DestID=255
    
    public RREQ(boolean BroadCast,int SourceID,int DestID){
                this.BroadCast=BroadCast;
                this.SourceID=SourceID;
                this.DestID=DestID;
                Transmit(SourceID, DestID);
            }
    
    public final void Transmit(int SourceID,int DestID){
             DbConnection db=new DbConnection();
            Connection conn=db.Connect(); 
            int Key=db.ReturnUniqueKey(TableNames.MessageExchange, conn);
            this.ID=Key+1;
            db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+(Key+1)+","+SourceID+","+DestID+",'RREQ')", conn);
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(RREQ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
