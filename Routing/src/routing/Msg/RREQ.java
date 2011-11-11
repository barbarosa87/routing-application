/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Msg;

import java.sql.Connection;
import routing.DbConnection;
import routing.EnumeRators;


/**
 *
 * @author barbarosa
 */
public class RREQ {
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
        int Key=db.ReturnUniqueKey(EnumeRators.MessageExchange, conn);
        if (BroadCast){
            db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+Key+","+SourceID+","+DestID+",'RREQ')", conn);
        }else{
            db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+Key+","+SourceID+","+DestID+",'RREQ')", conn);
        }
            
    }
    
}
