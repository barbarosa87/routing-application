/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.conf;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author barbarosa
 */
public final class RoutingCnf {

//DECLARATION
//<editor-fold defaultstate="collapsed" desc="Declaration">
    private static int StartFrequncy=40;
    private static int StopFrequncy=60;
    private static int NumberOfBroadCastTries=10;
    
    private static final Map<Integer,Integer> Frequencies=new HashMap<Integer,Integer>(){{
        put(40,622);
        put(41,630);
        put(42,638);
        put(43,646);
        put(44,654);
        put(45,662);
        put(46,670);
        put(47,678);
        put(48,686);
        put(49,694);
        put(50,702);
        put(51,710);
        put(52,718);
        put(53,726);
        put(54,734);
        put(55,742);
        put(56,750);
        put(57,758);
        put(58,766);
        put(59,774);
        put(60,782);
    }};
    //Load of the flow
    private static double p=0.45;
    //System capacity
    private static double C=1;
    
    private static double k=1000;
    private static double Pc=0.2;
    private static double Wo=3;
    private static double Hx=1;
    private static double PCPath=0.9;
    private static double QCPath=0.9;
    private static double PoPath=0.9;    
//</editor-fold>
    public static double getPCPath(){
        return PCPath;
    }
    public static double getQCPath(){
        return QCPath;
    }
    public static double getPoPath(){
        return PoPath;
    }
    public static double getHx(){
        return Hx;
    }
    public static double getPc(){
        return Pc;
    }
    public static double getWo(){
        return Wo;
    }
public static double getLoadOfFlow(){
    return p;
}
public static double getK(){
    return k;
}
    public static double getSystemCapacity(){
        return C;
    }
    
        
public static int getStartFrequency(){
    return StartFrequncy;
}
public static int getStopFrequency(){
    return StopFrequncy;
}

public static int getFrequencyRange(){
    return StopFrequncy-StartFrequncy;
}


public static int getNumberOfBroadCastTries(){
    return NumberOfBroadCastTries;
   }



public int getFrequencyFromChannel(int TVWS){
return Frequencies.get(TVWS);
}


}
