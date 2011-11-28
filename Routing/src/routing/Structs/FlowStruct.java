/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Structs;

import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author spyros
 */
public class FlowStruct {
    
public static class Flow{
   private String Name;
   //private int NodeID;
   private List AddedNodes=new ArrayList<Integer>();
   
   //Node with index 1 in AddedNodes is the sourceNode
   public Flow(String Name){
       this.Name=Name;
   }
   
   
   public void addNodeToFlow(int NodeID){
       AddedNodes.add(NodeID);
   }
}


}
