/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.util.ArrayList;
import java.util.List;

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
    
    
    public int BroadCastMessage(int NodeID){
        
        return 1;
    }



}
