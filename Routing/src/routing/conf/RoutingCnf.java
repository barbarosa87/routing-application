/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.conf;

/**
 *
 * @author barbarosa
 */
public final class RoutingCnf {

//DECLARATION
//<editor-fold defaultstate="collapsed" desc="NodesFrequencyRange">
    private static int StartFrequncy=40;
    private static int StopFrequncy=60;
//</editor-fold>

public static int getStartFrequency(){
    return StartFrequncy;
}
public static int getStopFrequency(){
    return StopFrequncy;
}

public static int getFrequencyRange(){
    return StopFrequncy-StartFrequncy;
}

}
