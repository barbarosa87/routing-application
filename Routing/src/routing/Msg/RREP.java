/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Msg;

import java.sql.Connection;
import routing.DbConnection;
import routing.Enumerators.TableNames;

/**
 *
 * @author barbarosa
 */
public class RREP {
    
    private boolean Redirect=false;
    private int SourceID;
    private int DestinationID;
   
    
    
    public RREP(int SourceID,int DestinationID,boolean Redirect){
    this.SourceID=SourceID;
    this.DestinationID=DestinationID;
    }
    
    
    
    public void SendRREP(int SourceID,int DestinationID){
            DbConnection db=new DbConnection();
            Connection conn=db.Connect();
            int Key=db.ReturnUniqueKey(TableNames.MessageExchange,conn);
            db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+(Key+1)+","+SourceID+","+DestinationID+",'RREQ')", conn);
            
    }
    
}
