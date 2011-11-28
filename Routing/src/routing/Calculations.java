/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;
import routing.conf.RoutingCnf;
/**
 *
 * @author barbarosa
 */
public class Calculations {
    
    
    

    
    public double GetDQueuing(int NodesCount){
        return (2*RoutingCnf.getLoadOfFlow()*NodesCount)/RoutingCnf.getSystemCapacity()*(1-2*RoutingCnf.getLoadOfFlow())*(1-RoutingCnf.getLoadOfFlow());
    }
    
//    public double GetDRoute(){
//        return GetPathDelay()+GetNodeDelay();
//    }
//    
//    
//    public double GetPathDelay(){
//        
//    }
    
    public double GetPathSwitchingDelay(int ActiveFrequency,int MoveFrequency){
        return RoutingCnf.getK()*Math.abs(ActiveFrequency-MoveFrequency);
    }
    public double GetPathBackOffDelay(){
        if (RoutingCnf.getHx()%2==0){
            return ((1-RoutingCnf.getPoPath())*RoutingCnf.getQCPath())*((1-RoutingCnf.getPCPath())/(1-Math.pow(RoutingCnf.getPCPath(),2.0)));
        }else {
            return ((1-RoutingCnf.getPoPath())*RoutingCnf.getQCPath())*((1-RoutingCnf.getPCPath())/(1-Math.pow(RoutingCnf.getPCPath(),2.0)))+(RoutingCnf.getPoPath()*Math.pow(RoutingCnf.getPoPath(),RoutingCnf.getHx()-1));
        }
    }
    
    
    public double GetNodeDelay(int ActiveFrequenciesCount,int ActiveFrequency,int NodeNumber){
        return GetNodeSwitchingDelay(ActiveFrequenciesCount,ActiveFrequency)+GetNodeBackOffDelay(NodeNumber);
    }
    
    
    public double GetNodeSwitchingDelay(int ActiveFrequenciesCount,int ActiveFrequency){
        return 2*RoutingCnf.getK()*Math.abs(ActiveFrequenciesCount-ActiveFrequency);
    }
    
    public double GetNodeBackOffDelay(int NodeNumber){
        return (1/(1-RoutingCnf.getPc())*(1-Math.pow((1-RoutingCnf.getPc()),(1/NodeNumber-1))))*RoutingCnf.getWo();
    }
    
    
    
}
