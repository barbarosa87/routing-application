
package ga;


import java.util.Map;

//<editor-fold defaultstate="collapsed" desc="RoundTimeSolution Class">
public class RoundTimeSolutionClass {
    
    public static class Solution{
    
    public final int Round;
    public final Double Fragmentation;
    public final double Elapsed_Time;
    public  Map<InitializeData.TVWS[],InitializeData.SecondarySystem> Final_Solution;
    
    public Solution(int Round,Double Fragmentation,double elapsed_Time,Map<InitializeData.TVWS[],InitializeData.SecondarySystem> Final_Solution){
        this.Round=Round;
        this.Fragmentation=Fragmentation;
        this.Elapsed_Time=elapsed_Time;
        this.Final_Solution=Final_Solution;
    }
    }
//</editor-fold>
    
}
