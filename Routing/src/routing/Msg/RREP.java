/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Msg;

import java.sql.Connection;
import routing.DbConnection;
import routing.Enumerators.ReturnType;
import routing.Enumerators.TableNames;

/**
 *
 * @author spyros
 */
public class RREP {
    
    private boolean Redirect=false;
    private Connection conn;
    
    public RREP(int SourceID,int DestinationID,boolean Redirect,Connection conn){
    this.conn=conn;
    }
    
    
    
    public void SendRREP(int SourceID,int DestinationID){
            DbConnection db=new DbConnection();
            int Key=db.ReturnUniqueKey(TableNames.MessageExchange, conn);
            if (Redirect){
                db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+(Key+1)+","+SourceID+","+DestinationID+",'RREQ')", conn);
            }else{
                db.AddToDb("INSERT INTO MessageExchange(ID,SourceNode,DestinationNode,MessageName) VALUES("+(Key+1)+","+SourceID+","+DestinationID+",'RREQ')", conn);
            }
    }
    
}
