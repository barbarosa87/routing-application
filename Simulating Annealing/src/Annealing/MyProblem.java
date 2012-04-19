
package Annealing;

//<editor-fold defaultstate="collapsed" desc="Imports">
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
// </editor-fold>


public class MyProblem {
   
// <editor-fold defaultstate="collapsed" desc="variable declaration for class">
       public static int max_temperature,min_temperature,iterations;
       public static Map<InitializeData.TVWS[], InitializeData.SecondarySystem> Solution = new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>() {};
       public static StringBuilder result = new StringBuilder();
       public static int j=1;
       public static List<RoundTimeSolutionClass.Solution> RoundTimeSolution=new ArrayList<RoundTimeSolutionClass.Solution> ();
       public static boolean removed=false;
       public static List<Integer> System_ID=new ArrayList<Integer>();
       // </editor-fold>
            
// <editor-fold defaultstate="collapsed" desc="Solve function returning the total solution as map">
       protected static  Map<InitializeData.TVWS[], InitializeData.SecondarySystem> solve(List<InitializeData.SecondarySystem> systems)  {
        //BarChart Cost List
        List<Integer> Cost=new ArrayList<Integer>();
        Annealing myfunc = new Annealing(systems);
        ReadINI();
        myfunc.start(max_temperature,min_temperature,iterations);
        List<InitializeData.SecondarySystem> RoundSystems=new ArrayList<InitializeData.SecondarySystem>();
        myfunc.fragmentation=myfunc.calculateStartFragmentation();
        while(j<=10){
         System_ID.clear();
         Remove();
            for(int i=0;i<systems.size();i++){
            if(systems.get(i).start==j){
                RoundSystems.add(systems.get(i));
                if(systems.get(i).name.contains("LTE")){
                   Annealing.fdd=true;
                }
            }
        }
        Map<InitializeData.TVWS[], InitializeData.SecondarySystem> Temp_Solution = new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>() {};
        //Creating Systems Considering Priority
        List<InitializeData.SecondarySystem> Priority_Systems = new ArrayList<InitializeData.SecondarySystem>();
        List<InitializeData.SecondarySystem> Priority_One_Systems =new ArrayList<InitializeData.SecondarySystem>();
        List<InitializeData.SecondarySystem> Priority_Two_Systems = new ArrayList<InitializeData.SecondarySystem>();
        //Prepei na ginei diaxwrismos me vasi priority diktyou
        for(InitializeData.SecondarySystem RoundSystem:RoundSystems){
        switch (RoundSystem.priority){
            case 1:
                Priority_One_Systems.add(RoundSystem);
                break;
            case 2:
                Priority_Two_Systems.add(RoundSystem);
        }
        }
        for(InitializeData.SecondarySystem Priority_One_System:Priority_One_Systems){
            myfunc.Initialization();
            List<InitializeData.SecondarySystem> Temp_List=new ArrayList<InitializeData.SecondarySystem>();
            Temp_List.add(Priority_One_System);
            Temp_Solution.putAll(myfunc.anneal(Temp_List));
        }
        for(InitializeData.SecondarySystem Priority_Two_System:Priority_Two_Systems){
            myfunc.Initialization();
              List<InitializeData.SecondarySystem> Temp_List=new ArrayList<InitializeData.SecondarySystem>();
            Temp_List.add(Priority_Two_System);
            Temp_Solution.putAll(myfunc.anneal(Temp_List));
        }
        
        
        //Temp_Solution=myfunc.anneal(RoundSystems);        
        
        RoundTimeSolution.add(new RoundTimeSolutionClass.Solution(j,Annealing.return_fragmentation,Annealing.end_time-Annealing.start_time, new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>(Temp_Solution)));
        //Populating Cost
        Cost.add(Annealing.return_cost);
        for(Map.Entry <InitializeData.TVWS[], InitializeData.SecondarySystem> entry : Temp_Solution.entrySet() ){
            Solution.put(entry.getKey(), entry.getValue());
        }
        result.append("<HTML><HEAD><TITLE>Spectrum Allocation Results</TITLE></HEAD><BODY>");
        if(removed){
            for(int l=0;l<System_ID.size();l++){
                result.append("<p><b>System with ID ").append(System_ID.get(l)).append(" removed</b></p>");
            }
            removed=false;
        }
        result.append(Print(Temp_Solution));
        myfunc.Initialization();
        RoundSystems.clear();
        Annealing.fdd=false;
        j++;
        }
        result.append("</BODY>");
        //Starting BarChart
        BarChart b=new BarChart(Cost);
        b.setMinimumSize(new java.awt.Dimension(800,600) );
        b.setVisible(true);
        return Solution;           
        }
      // </editor-fold>
          
// <editor-fold defaultstate="collapsed" desc="Printing function to results.html">
       public static StringBuilder Print(Map <InitializeData.TVWS[], InitializeData.SecondarySystem> Solution ){
           StringBuilder Result=new StringBuilder();
           DecimalFormat df = new DecimalFormat("#.####");
           Result.append("<p>").append("Round: ").append(j).append("</p>");
           try { 
            if (Solution.isEmpty()) {
                Result.append("<p>No Solution</p>");
            } else {
                Result.append("<table><tr><td>System</td><td>Channels</td></tr>");
                for(Map.Entry<InitializeData.TVWS[], InitializeData.SecondarySystem> entry:Solution.entrySet()){
                    Result.append("<tr><td>").append(entry.getValue().name).append("   ").append(entry.getValue().id).append("</td><td>");
                    int i=0;
                    for(InitializeData.TVWS channel:entry.getKey()){
                       if(i==entry.getValue().bandwidth){
                        Result.append(",");
                        Result.append("<font color='red'>").append("  ").append(channel.id).append(" subid  ").append(channel.subid).append(" (Guard Interval Channel) ").append("</font>");
                        break;
                       }
                        Result.append(",");
                        Result.append("  ").append(channel.id).append(" subid  ").append(channel.subid);
                        i++;        
                    }
                   
                }
                   Result.append("</td></tr></table>");
                   Result.append("fragmentation: ").append("<b>").append(df.format(Annealing.return_fragmentation)).append("</b>").append(" ");
                   Result.append("Cost: ").append("<b>").append(df.format(Annealing.return_cost)).append("</b>").append(" ");
                   Result.append("Temperature: ").append("<b>").append(df.format(Annealing.return_temperature)).append("</b>").append(" "); 
                   Result.append("Spectrum Utilization: ").append("<b>").append(df.format(Annealing.Spectrum_Utilization)).append("</b>").append(" ");
                   Result.append("Bw exploited: ").append("<b>").append(df.format(Annealing.bw_exploited)).append("</b>").append(" ");
                   Result.append("Time elapsed: ").append("<b>").append(Annealing.end_time-Annealing.start_time).append("</b>").append(" ");
            }
            
        
        } catch (Exception ex) {
         
        }    
           return Result;
       }
       
       // </editor-fold>
       
//<editor-fold defaultstate="collapsed" desc="Reading initialization File">
       public static void ReadINI(){
            try{
      Properties p = new Properties();
      p.load(new FileInputStream("config.ini"));
      iterations=Integer.parseInt(p.getProperty("Cycles"));
      max_temperature=Integer.parseInt(p.getProperty("StartTemperature"));
      min_temperature=Integer.parseInt(p.getProperty("StopTemperature"));
            }
    catch (Exception e) {
      System.out.println(e);
      }
    }  
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Removing Passed Time Networks">
public static void Remove(){
    for (Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Solution.entrySet()){
                 InitializeData.SecondarySystem system=entry.getValue();
                 InitializeData.TVWS[] Channels=entry.getKey();
            if(system.end==(j-1)){
                         System_ID.add(system.id);
                         for(InitializeData.TVWS currentChannel:Channels){
                            Annealing.Total_channelsUsed.remove(currentChannel);
                            for(int i=0;i<Annealing.tvws_local.size();i++){
                                   if((Annealing.tvws_local.get(i).id==currentChannel.id) && (Annealing.tvws_local.get(i).subid==currentChannel.subid)){
                                       Annealing.tvws_local.get(i).isAvailable=true;
                                   }
                                }
                        }
                         removed=true;
                               }
    }
    removeDuplicate(System_ID);
}
//</editor-fold>

// <editor-fold  defaultstate="collapsed" desc="Removing Duplicate on input list">
  public static void removeDuplicate(List arlList)
  {
   HashSet h = new HashSet(arlList);
   arlList.clear();
   arlList.addAll(h);
  }
// </editor-fold>


}
   
 



       
