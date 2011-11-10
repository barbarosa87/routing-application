/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;


import java.util.ArrayList;
import java.util.List;
import routing.Msg.RREQ;
/**
 *
 * @author barbarosa
 */
public class StartCommunication {
    
    List SourcesList=new ArrayList<Integer>();
    List DestinationList=new ArrayList<Integer>();
    
    public StartCommunication(List SourcesList,List DestinationList){
        this.SourcesList=SourcesList;
        this.DestinationList=DestinationList;
                 
    }
    
    public void Start(){
    for (int i=0;i<SourcesList.size();i++){
        BroadCastMessage((Integer)SourcesList.get(i));
       }
    }
    
    
    
    public void BroadCastMessage(int NodeID){
        RREQ broadcast=new RREQ(true,NodeID,255);
    }



}
