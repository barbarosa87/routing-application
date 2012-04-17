package Annealing;

// <editor-fold defaultstate="collapsed" desc="Imports">
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
//</editor-fold>

public class Annealing {   
     
// <editor-fold defaultstate="collapsed"  desc="Variable declaration"> 
private final List<InitializeData.SecondarySystem> systems ;
protected double startTemperature;
protected double stopTemperature;
public   int cycles;
public static int cycl=0;
public static int return_cost=0;
static int pick;
public double temperature;
public static double return_temperature;
public Map<InitializeData.TVWS[], InitializeData.SecondarySystem> BestSolution = new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>() {};
public  double fragmentation=0.0;
public static double Spectrum_Utilization=0.0;
public static double bw_exploited=0.0;
public static  int it=0;
public static Double return_fragmentation=0.0;
public static List<InitializeData.TVWS> tvws_local=new ArrayList<InitializeData.TVWS>();
public static List<InitializeData.TVWS> Total_channelsUsed = new ArrayList<InitializeData.TVWS>(); 
public List<InitializeData.TVWS> channelsUsed = new ArrayList<InitializeData.TVWS>();   
List<InitializeData.TVWS> Temp_Channels_first = new ArrayList<InitializeData.TVWS>(); 
public int cost=0;
public static boolean fdd=false;
public static double start_time=0.0,end_time=0.0;
public double ratio;
        ///(cycles - 1);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Class constructor">
public Annealing(List<InitializeData.SecondarySystem> network)
{
    this.systems= network;
}
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Evaluate validity of solution">
protected double evaluate( Map< InitializeData.TVWS[], InitializeData.SecondarySystem> solutionPerSystem) {
    List<InitializeData.TVWS[]> Temp_Channels = new ArrayList<InitializeData.TVWS[]>();    
    List<InitializeData.TVWS> Last_Channels=new ArrayList<InitializeData.TVWS>();
    InitializeData.TVWS Temp_Channel = null;
    Double success=0.1;
    double fragmenta=0.0;
    for (Map.Entry< InitializeData.TVWS[], InitializeData.SecondarySystem> entry : solutionPerSystem.entrySet()) {
            InitializeData.SecondarySystem currentSystem = entry.getValue();
            InitializeData.TVWS[] channels = entry.getKey();
            for (InitializeData.TVWS currentChannel : channels) {
                if(channelsUsed.contains(currentChannel)){
                   channelsUsed.clear();
                   return 0.1;
               }
               if(Total_channelsUsed.contains(currentChannel)){
                   channelsUsed.clear();
                   return 0.1;
               }
                if (currentChannel.transmissionPower < currentSystem.transmissionPower) {                        
                    channelsUsed.clear();
                    return 0.1;
              }
               Temp_Channel=currentChannel;
               channelsUsed.add(currentChannel);
            }
            Last_Channels.add(Temp_Channel);
            Temp_Channels.add(channels);
       }
       List <InitializeData.TVWS> Temp_Channels_As_List= new ArrayList<InitializeData.TVWS> ();
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
       if(fdd){
            success=EvaluateFDD(solutionPerSystem);
               if(success==0.1){
                   return 0.1;
               }
       }
       
       fragmenta=check_fragmentation(Temp_Channels_As_List);      
       if(it==0){
                Temp_Channels_first=Temp_Channels_As_List;
                BestSolution=solutionPerSystem;
                fragmentation=fragmenta;
                return_fragmentation=fragmenta;
                return_cost=cost;
                return_temperature=temperature;
                it++;
                reversePreviousTVWS();
               // reverse_tvws(Temp_Channels_As_List);
                return 1.0;
            }
     if(fragmenta<fragmentation){
               BestSolution=solutionPerSystem;
               return_cost=cost;
               return_temperature=temperature;
               return_fragmentation=fragmenta;
               fragmentation=fragmenta;
               reversePreviousTVWS();
    }
    else {
       // reversePreviousTVWS();
       reverse_tvws(Temp_Channels_As_List);
    }
     return 1.0 ;
       }
     // </editor-fold>  

// <editor-fold defaultstate="collapsed" desc="Picking random potential solution">
  protected Map<InitializeData.TVWS[],InitializeData.SecondarySystem> randomize(List<InitializeData.SecondarySystem> systems)
  {
           
           HashMap<InitializeData.TVWS[], InitializeData.SecondarySystem> solutionPerSystem = new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>(); 
           Random random = new Random(System.nanoTime());
     for (int index = 0; index < systems.size(); index++) {
            InitializeData.SecondarySystem systemk= systems.get(index); 
            pick = (int)(random.nextInt()-temperature)%systemk.potentialSolutions.size();
            if (pick <0){
                pick=Math.abs(pick);
            }
            InitializeData.TVWS[] solution = systemk.potentialSolutions.get(pick);
            solutionPerSystem.put(solution,systemk);
      }
      
      return solutionPerSystem;    
  }
// </editor-fold>
  
// <editor-fold defaultstate="collapsed"  desc="class initialize temperatures">
  /**
   * Initialize the simulated annealing class.
   * @param startTemp The starting temperature.
   * @param stopTemp The ending temperature.
   * @param cycles The number of cycles to use at each temperature.
   */
  public void start(double startTemp,double stopTemp,int cycles)
  {
    this.temperature = startTemp;
    this.cycles = cycles;
    this.startTemperature = startTemp;
    this.stopTemperature = stopTemp;
    tvws_local=Main.tvws;
    it=0;
  }
// </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc="anneal function: Starting allocation with the startTemp stopTemp cycles defined in constructor">
  public Map<InitializeData.TVWS[], InitializeData.SecondarySystem> anneal(List<InitializeData.SecondarySystem> systems)
  {
 Map<InitializeData.TVWS[], InitializeData.SecondarySystem > solutionPerSystem = new HashMap<InitializeData.TVWS[] , InitializeData.SecondarySystem >();
 ratio=Math.exp(Math.log(stopTemperature/startTemperature)/(cycles - 1));
 double success=0.1;
 start_time=System.nanoTime();
 while(temperature>stopTemperature){
 for (cycl=0;cycl<this.cycles;cycl++) {
 solutionPerSystem=randomize(systems);
 success=evaluate(solutionPerSystem);
 cost++;  
 }
 temperature=temperature*ratio;
 }
 
//Making changes permanent
  List<InitializeData.TVWS> LastChannels=new ArrayList<InitializeData.TVWS>();
  InitializeData.TVWS tv=null;
  for (Map.Entry< InitializeData.TVWS[], InitializeData.SecondarySystem> entry : BestSolution.entrySet()) {
            InitializeData.TVWS[] channels = entry.getKey();
       for(InitializeData.TVWS currentChannel :channels){
         tv=currentChannel;
       }
       LastChannels.add(tv);
  }
  
for(Map.Entry<InitializeData.TVWS[],InitializeData.SecondarySystem> entry:BestSolution.entrySet()){
 InitializeData.TVWS[]channels =entry.getKey();
 for(InitializeData.TVWS currentChannel: channels){
     for(int i=0;i<tvws_local.size();i++){
         if(LastChannels.contains(currentChannel)){
              if( tvws_local.get(i)==currentChannel){ 
             tvws_local.get(i).isAvailable=true;
              }
         }
         else{
             if( tvws_local.get(i)==currentChannel){
                 tvws_local.get(i).isAvailable=false;
             }       
         }
     }
 }
}
for(Map.Entry<InitializeData.TVWS[],InitializeData.SecondarySystem> entry:BestSolution.entrySet()){
 InitializeData.TVWS[]channels =entry.getKey();
            Total_channelsUsed.addAll(Arrays.asList(channels));
}
Spectrum_Utilization=CalculateSpectrumUtilization();
bw_exploited=CalculateBWexploited();
Main.tvws=tvws_local;
end_time=System.nanoTime();
return BestSolution;
 }
 // </editor-fold>
 
// <editor-fold defaultstate="collapsed" desc="Checking fragmentation">
public Double check_fragmentation(List<InitializeData.TVWS> Temp_Channels){
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
    return 1-total;
  }
 // </editor-fold>
  
// <editor-fold defaultstate="collapsed" desc="Calculating the starting fragmentation">
public Double calculateStartFragmentation(){
   Double frag=0.0,frag_square=0.0,f=0.0,total=0.0;
   List<Double> fragment=new ArrayList<Double>();
   boolean first=true;
   for(int i=0;i<Main.tvws.size();i++){
    InitializeData.TVWS space=(InitializeData.TVWS) Main.tvws.get(i);
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

public Double calculateStartFragmentation2(){
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

// <editor-fold defaultstate="collapsed" desc="Reversing availability of tvws in case of biggest fragmentation with new allocation">
public void reverse_tvws(List<InitializeData.TVWS> Temp_Channels){
    //List<NewInitializeData.TVWS> Temp_Tvws_List=new ArrayList<NewInitializeData.TVWS>();
    //Temp_Tvws_List=NewMain.tvws;
    for(int i=0;i<Temp_Channels.size();i++){
           for(int j=0;j<tvws_local.size();j++){
                if((Temp_Channels.get(i).id==tvws_local.get(j).id ) && (Temp_Channels.get(i).subid==tvws_local.get(j).subid) )
                {
                    tvws_local.get(j).isAvailable=true;
                 }
            }
         } 
    
    //NewMain.tvws=tvws_local;
}
// </editor-fold>

//<editor-fold defaultstate="collapsed" desc="Initialization of variables">
public void Initialization(){
  this.temperature=this.startTemperature;
  it=0;
  this.BestSolution.clear();
  Annealing.return_cost=0;
  Annealing.return_temperature=0;
  this.channelsUsed.clear();
  tvws_local=Main.tvws;
  this.cost=0;
}
//</editor-fold>

// <editor-fold defaultstate="collapsed" desc="Spectrum Utilization Calculations">
public Double CalculateSpectrumUtilization(){
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
public Double CalculateBWexploited(){
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
            if(((((First_Channel_ID*8)+First_Channel_subID)-((Second_Channel_ID*8)+Second_Channel_subID))<35) || ((((First_Channel_ID*8)+First_Channel_subID)-((Second_Channel_ID*8)+Second_Channel_subID))>45)){
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

// <editor-fold defaultstate="collapsed" desc="Function to restore preiously used tvws on change">
  public void reversePreviousTVWS(){

      for(Map.Entry<InitializeData.TVWS[],InitializeData.SecondarySystem> entry:BestSolution.entrySet()){
     InitializeData.TVWS[]channels =entry.getKey();
          for(InitializeData.TVWS currentChannel: channels){
                  for(int i=0;i<tvws_local.size();i++){
          if( tvws_local.get(i)==currentChannel){ 
             tvws_local.get(i).isAvailable=true;
              }
                }
           }
      }  
  }
  // </editor-fold>
  

}



