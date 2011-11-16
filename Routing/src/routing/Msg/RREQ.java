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
    public int ID;
    private boolean  BroadCast;
    public int SourceID,DestID;
    private Connection conn;
    //For BroadCast DestID=255
    
    public RREQ(boolean BroadCast,Connection conn,int SourceID,int DestID){
                this.BroadCast=BroadCast;
                this.conn=conn;
                this.SourceID=SourceID;
                this.DestID=DestID;
                Transmit(SourceID, DestID);
            }
    
    public final void Transmit(int SourceID,int DestID){
            DbConnection db=new DbConnection();
            int Key=db.ReturnUniqueKey(EnumeRators.MessageExchange, conn);
            this.ID=Key+1;
            if (BroadCast){
                db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+(Key+1)+","+SourceID+","+DestID+",'RREQ')", conn);
            }else{
                db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+(Key+1)+","+SourceID+","+DestID+",'RREQ')", conn);
            }
       
            
    }
    
}
