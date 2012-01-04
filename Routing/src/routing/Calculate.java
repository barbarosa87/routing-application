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
public class Calculate  {
    
 public double getSwitchingDelay(int StartBand,int DestBand){
        return RoutingCnf.getk()*Math.abs(StartBand-DestBand);
 } 
  public  double getNodeQueuing(){
      return RoutingCnf.getp()/(RoutingCnf.getm()-RoutingCnf.getl());
  }
  public double getNodeBlock(){
      return ((1-RoutingCnf.getp())*Math.pow(RoutingCnf.getp(), RoutingCnf.getk()))/(1-Math.pow(RoutingCnf.getp(), (RoutingCnf.getk()-1)));
  }
}
