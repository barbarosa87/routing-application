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
   private List AddedNodes=new ArrayList<Integer>();
   private double ND=0;
   
   //Node with index 1 in AddedNodes is the sourceNode
   public Flow(String Name,int SourceNode){
       this.Name=Name;
       addNodeToFlow(SourceNode);
   }
   
   public List<Integer> GetAddedNodesList(){
       return AddedNodes;
   }
   public void addNodeToFlow(int NodeID){
       AddedNodes.add(NodeID);
   }
   public void CalculateND(double SwitchingDelay,double BackOffDelay){
       ND=ND+(SwitchingDelay+BackOffDelay);
   }
   public String GetFlowName(){
       return this.Name;
   }
   
   
}

public static class DesignNode{
    private int ID;
    private double XPoint;
    private double YPoint;
    
    public DesignNode(int ID,double XPoint,double YPoint){
        this.ID=ID;
        this.XPoint=XPoint;
        this.YPoint=YPoint;
    }
    public int GetDesignNodeID(){
        return ID;
    }
    public double GetXpoint(){
        return XPoint;
    }
    public double GetYpoint(){
        return YPoint;
    }
    
}


}
