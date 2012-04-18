package ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

public class MyFitnessFunction extends FitnessFunction {

    private final List<InitializeData.SecondarySystem> systems;
    static int pick;
    public static Map<InitializeData.TVWS[], InitializeData.SecondarySystem> BestSolution = new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>() {};
    public static double fragmentation=0.0;
    public static double Spectrum_Utilization=0.0;
    public static double bw_exploited=0.0;
    public static  int it=0;
    public static Double return_fragmentation=100.0;
    public static List<InitializeData.TVWS> tvws_local=new ArrayList<InitializeData.TVWS>();
    List<InitializeData.TVWS> Temp_Channels_first = new ArrayList<InitializeData.TVWS>();
    public int cost=0;
    public static double start_time=0.0,end_time=0.0;
    public static List <InitializeData.TVWS> Temp_Channels_As_List= new ArrayList<InitializeData.TVWS> ();
    
    public MyFitnessFunction(List<InitializeData.SecondarySystem> systems) {
        this.systems = systems;
    }

    @Override
    protected double evaluate(IChromosome ic) {
        Map<InitializeData.TVWS[],InitializeData.SecondarySystem> solutionPerSystem = translateSolution(ic);
        List<InitializeData.TVWS> channelsUsed = new ArrayList<InitializeData.TVWS>();
        List<InitializeData.TVWS[]> Temp_Channels = new ArrayList<InitializeData.TVWS[]>();    
        List<InitializeData.TVWS> Last_Channels=new ArrayList<InitializeData.TVWS>();
        InitializeData.TVWS Temp_Channel = null;
        double success=0.1;
        double fragmenta=0.0;
        for (Map.Entry<InitializeData.TVWS[], InitializeData.SecondarySystem> entry : solutionPerSystem.entrySet()) {
            InitializeData.SecondarySystem currentSystem = entry.getValue();
            InitializeData.TVWS[] channels = entry.getKey();
            for (InitializeData.TVWS currentChannel : channels) {
              if(MyProblem.Total_channelsUsed.contains(currentChannel)){
                   channelsUsed.clear();
                   return 0.1;
               }
                if (channelsUsed.contains(currentChannel)) {//1ο Ξεκαρτάρισμα συνδυασμών, συνδυασμοί[2] που χρησιμοποιούν το ίδιο κανάλι πάνω απο μια φορές θεωρούνται ακυροι.
                    channelsUsed.clear();
                    return 0.1;//1ο Ξεκαρτάρισμα συνδυασμών, συνδυασμοί[2] που χρησιμοποιούν το ίδιο κανάλι πάνω απο μια φορές θεωρούνται ακυροι.
                }
                if (currentChannel.transmissionPower < currentSystem.transmissionPower) {
                   channelsUsed.clear();
                    return 0.1;//2ο Ξεκαρτάρισμα συνδυασμών. Power Level, κάθε συστημα δεν μπορεί να εκπέμψει εάν από ενα όριο ισχύς εκπομπής που έχει καθοριστεί ανά διαθέσιμο κανάλι.
                }
                channelsUsed.add(currentChannel);//Κρατάω το τρέχον κανάλι ώστε να ελέγξω εαν κάποιο απο τα επομενα είναι ίδιο [Χρησιμοποιείται για το 1ο ξεκαρτάρισμα]
                Temp_Channel=currentChannel;
            }
             Last_Channels.add(Temp_Channel);
            Temp_Channels.add(channels);
        }
        Temp_Channels_As_List.clear();
       for(int k=0;k<Temp_Channels.size();k++){
           Temp_Channels_As_List.addAll(Arrays.asList(Temp_Channels.get(k)));
       }
       for(int i=0;i<Temp_Channels_As_List.size();i++){
           for(int j=0;j<Last_Channels.size();j++){
               if(Temp_Channels_As_List.get(i)==Last_Channels.get(j)){
                  Temp_Channels_As_List.remove(Last_Channels.get(j));                    
               }
           }
       }
       if(MyProblem.fdd){
            success=EvaluateFDD(solutionPerSystem);
            return success;
       }
       return 1.0;
    }

    /**
     * Μετατρέπει το χρωμόσωμα σε έναν χάρτη που έχει την αντιστοιχη επιλεγμένη λύση ανά σύστημα
     * @param ic
     * @return χάρτη που έχει την αντιστοιχη επιλεγμένη λύση ανά σύστημα
     */
    public Map<InitializeData.TVWS[], InitializeData.SecondarySystem> translateSolution(IChromosome ic) {
    Map< InitializeData.TVWS[],InitializeData.SecondarySystem> solutionPerSystem = new HashMap<InitializeData.TVWS[], InitializeData.SecondarySystem>();
        for (int index = 0; index < systems.size(); index++) {
            InitializeData.SecondarySystem system = systems.get(index);
            Gene gene = ic.getGene(index);
            InitializeData.TVWS[] solution = system.potentialSolutions.get((Integer) gene.getAllele());
            solutionPerSystem.put(solution, system);
        }
        return solutionPerSystem;
    }


// <editor-fold defaultstate="collapsed" desc="Spectrum Utilization Calculations">
public static Double CalculateSpectrumUtilization(){
    Double f=0.0;
    for(int i=0;i<tvws_local.size();i++){
        if (tvws_local.get(i).isAvailable==false){
            f=f+1;
        }
        
   }
    
    return f/168;
}
// </editor-fold>

//<editor-fold defaultstate="collapsed" desc="Calculate BW exploited by all systems">
public static Double CalculateBWexploited(){
     Double f=0.0;
    for(int i=0;i<tvws_local.size();i++){
        if (tvws_local.get(i).isAvailable==false){
            f=f+1;
        }
        
            }
    
    return f;
}
//</editor-fold>

// <editor-fold defaultstate="collapsed" desc="EvaluatingFDD range between uplink downlink">
public Double EvaluateFDD(Map< InitializeData.TVWS[], InitializeData.SecondarySystem> solutionPerSystem){
    List<Double> success = new ArrayList<Double>();
    List<Integer> ID = new ArrayList<Integer>();
    List<InitializeData.TVWS[]> Channels_Temp = new ArrayList<InitializeData.TVWS[]>();  
    int First_Channel_ID=0,First_Channel_subID=0,Second_Channel_ID=0,Second_Channel_subID=0;
    for(Map.Entry < InitializeData.TVWS[], InitializeData.SecondarySystem> entry : solutionPerSystem.entrySet()){
   InitializeData.TVWS[] channels=entry.getKey();
   InitializeData.SecondarySystem system=entry.getValue();
   if(system.name.equals("LTE DL")){
       ID.add(system.id);
       Channels_Temp.add(channels);
   }
   if(system.name.equals("LTE UL")){
       ID.add(system.id);
       Channels_Temp.add(channels);
   }
   }
    removeDuplicate(ID);
    for(int i=0;i<ID.size();i++){
        for(Map.Entry < InitializeData.TVWS[], InitializeData.SecondarySystem> entry : solutionPerSystem.entrySet()){
             InitializeData.TVWS[] channels=entry.getKey();
             InitializeData.SecondarySystem system=entry.getValue();
             if(system.name.equals("LTE DL") && system.id==ID.get(i)){
                 for(InitializeData.TVWS currentChannel:channels){
                   First_Channel_ID=currentChannel.id;
                   First_Channel_subID=currentChannel.subid;
                   break;
                 }
             }
               if(system.name.equals("LTE UL") && system.id==ID.get(i)){
                 for(InitializeData.TVWS currentChannel:channels){
                   Second_Channel_ID=currentChannel.id;
                   Second_Channel_subID=currentChannel.subid;
                   break;
                 }
             }
        }
        int min = Math.min(First_Channel_ID, Second_Channel_ID); 
        if (min==First_Channel_ID){
          int sum_first=(Second_Channel_ID*8)+Second_Channel_subID;
          int sum_Second=(First_Channel_ID*8)+First_Channel_subID;  
          if((sum_first-sum_Second)<30 || (sum_first-sum_Second)>48) {
              return 0.1;
          }  
        }
        else if (min==Second_Channel_ID){
            if(((((First_Channel_ID*8)+First_Channel_subID)-((Second_Channel_ID*8)+Second_Channel_subID))<30) || ((((First_Channel_ID*8)+First_Channel_subID)-((Second_Channel_ID*8)+Second_Channel_subID))>48)){
              return 0.1;
         }
        }
    }
return 1.0;
}
// </editor-fold>

// <editor-fold  defaultstate="collapsed" desc="Removing Duplicate on input list">
  public static void removeDuplicate(List arlList)
  {
   HashSet h = new HashSet(arlList);
   arlList.clear();
   arlList.addAll(h);
  }
// </editor-fold>
  
// <editor-fold defaultstate="collapsed" desc="Calculating the starting fragmentation">
public static Double calculateStartFragmentation(){
   Double frag=0.0,frag_square=0.0,f=0.0,total=0.0;
   List<Double> fragment=new ArrayList<Double>();
   boolean first=true;
   for(int i=0;i<main.tvws.size();i++){
    InitializeData.TVWS space=(InitializeData.TVWS) main.tvws.get(i);
     if(space.isAvailable==true){
      f=f+space.bandwidth;
      first=true;
    }
    else if(space.isAvailable==false){
       if(first){
        fragment.add(f);
        f=0.0;
        first=false;
       }
       fragment.add(f);
        f=0.0;
    }
}
    for(int i=0;i<fragment.size();i++){
        if(!fragment.get(i).isNaN()){
        frag=frag+fragment.get(i);
        }
    }
    
    for(int i=0;i<fragment.size();i++){
        if(!fragment.get(i).isNaN()){
        frag_square=frag_square+(fragment.get(i)*fragment.get(i));
        }
    }
    total=frag_square/(frag*frag);
return 1-total;
}

public  Double calculateStartFragmentation2(){
    Double frag=0.0,frag_square=0.0,f=0.0,total=0.0;
    List<Double> fragment=new ArrayList<Double>();
   for(int i=0;i<tvws_local.size();i++){
    InitializeData.TVWS space=(InitializeData.TVWS) tvws_local.get(i);
     if(space.isAvailable==true){
      f=f+space.bandwidth;  
    }
    else if(space.isAvailable==false){
       fragment.add(f);
        f=0.0;
    }
}
    for(int i=0;i<fragment.size();i++){
        if(!fragment.get(i).isNaN()){
        frag=frag+fragment.get(i);
        }
    }
    
    for(int i=0;i<fragment.size();i++){
        if(!fragment.get(i).isNaN()){
        frag_square=frag_square+(fragment.get(i)*fragment.get(i));
        }
    }
    total=frag_square/(frag*frag);
return 1-total;
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Checking fragmentation">
public static Double check_fragmentation(List<InitializeData.TVWS> Temp_Channels){
      List <Double> fragment=new ArrayList<Double>(); 
      Double total_fragment=0.0,f=0.0,total_fragment_square=0.0,total=0.0;
      List<InitializeData.TVWS> Temp_Channels_used = new ArrayList<InitializeData.TVWS>();
      Temp_Channels_used=Temp_Channels;
    for(int i=0;i<Temp_Channels_used.size();i++){
        for(int j=0;j<tvws_local.size();j++){
                if((Temp_Channels_used.get(i).id==tvws_local.get(j).id) && (Temp_Channels_used.get(i).subid==tvws_local.get(j).subid ))
                {
                    tvws_local.get(j).isAvailable=false;
                 }
            }    
    }
    for(int i=0;i<tvws_local.size();i++){
        if(tvws_local.get(i).isAvailable==true){
            f=f+1;
        }
        else if(tvws_local.get(i).isAvailable==false) {
            fragment.add(f);
            f=0.0;
            }
        if(i==tvws_local.size()-1){
            fragment.add(f);
            f=0.0;
        }
    }
    for(int i=0;i<fragment.size();i++){
        if(!fragment.get(i).isNaN()){
        total_fragment=total_fragment+fragment.get(i);
        }
    }
    for(int i=0;i<fragment.size();i++){
        if(!fragment.get(i).isNaN()){
        total_fragment_square=total_fragment_square+(fragment.get(i)*fragment.get(i));
        }
    }
    total=total_fragment_square/(total_fragment*total_fragment);
    //Reversing Changes;
   for(int i=0;i<Temp_Channels_used.size();i++){
        for(int j=0;j<tvws_local.size();j++){
                if((Temp_Channels_used.get(i).id==tvws_local.get(j).id) && (Temp_Channels_used.get(i).subid==tvws_local.get(j).subid ))
                {
                    tvws_local.get(j).isAvailable=true;
                 }
            }    
    }
    return 1-total;
  }
 // </editor-fold>
  
}
