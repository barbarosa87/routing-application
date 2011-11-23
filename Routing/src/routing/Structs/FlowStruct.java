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
   private int NodeID;
   private List AddedNodes=new ArrayList<Integer>();
   
   
   public Flow(String Name){
       this.Name=Name;
   }
}


}
