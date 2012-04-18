package ga;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;


public class MyProblem {

    private static int MAX_ALLOWED_EVOLUTIONS =100;
    public static int j=1;
    public static List<RoundTimeSolutionClass.Solution> RoundTimeSolution=new ArrayList<RoundTimeSolutionClass.Solution> ();
    public static List<Integer> Cost_List=new ArrayList<Integer>();
    public static int Cost=0,return_cost;
    public static StringBuilder result = new StringBuilder();
    public static boolean removed=false;
    public static List<Integer> System_ID=new ArrayList<Integer>();
    public static Map<InitializeData.TVWS[],InitializeData.SecondarySystem> Temp_Solution=new HashMap<InitializeData.TVWS[], InitializeData.SecondarySystem>();
    public static Map<InitializeData.TVWS[],InitializeData.SecondarySystem> Temp_Solution2=new HashMap<InitializeData.TVWS[], InitializeData.SecondarySystem>();
    public static boolean it=true;
    public static Double fragmenta;
    public static Double fragmentation=100.0;
    public static Double return_fragmentation;
    public static Map<InitializeData.TVWS[], InitializeData.SecondarySystem> total_Solution = new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>() {};
    public static  Map<InitializeData.TVWS[], InitializeData.SecondarySystem> Solution = new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>() {};
    public static List<InitializeData.TVWS> Total_channelsUsed = new ArrayList<InitializeData.TVWS>(); 
    public static boolean fdd=false;
    public static List <InitializeData.TVWS> Temp_Channels_As_List= new ArrayList<InitializeData.TVWS> ();
    public static double start_time,end_time,elapsed_time;
    /**
     *
     * @param systems
     * @return solution if there any, else null
     * @throws Exception
     */
   public  Map<InitializeData.TVWS[], InitializeData.SecondarySystem> solve(List<InitializeData.SecondarySystem> systems)  {
        // Start with a DefaultConfiguration, which comes setup with the
        // most common settings.
        // -------------------------------------------------------------    
        // Set the fitness function we want to use. We construct it with
        // the target volume passed in to this method.
        // -------------------------------------------------------------
         try {
        MyFitnessFunction.tvws_local=main.tvws;
        List<InitializeData.SecondarySystem> RoundSystems=new ArrayList<InitializeData.SecondarySystem>();
        result.append("<HTML><HEAD><TITLE>Spectrum Allocation Results</TITLE></HEAD><BODY>");
        MyFitnessFunction.tvws_local=main.tvws;
        //MyFitnessFunction.fragmentation=MyFitnessFunction.calculateStartFragmentation();
        while(j<=10){
        return_cost=0;
        fragmenta=MyFitnessFunction.calculateStartFragmentation();
        Cost=0;
        it=true;
        fdd=false;
        it=true;
        System_ID.clear();
           Remove();
           for(int i=0;i<systems.size();i++){
            if(systems.get(i).start==j){
                RoundSystems.add(systems.get(i));
                if(systems.get(i).name.contains("LTE")){
                  fdd=true;
                }
            }
        }
        for (int k = 0; k < MAX_ALLOWED_EVOLUTIONS; k++) { 
        start_time=System.nanoTime();
        MyFitnessFunction myFunc = new MyFitnessFunction(RoundSystems);
        Temp_Solution.clear();
        Configuration conf = new DefaultConfiguration();
        conf.reset();
        conf.setPreservFittestIndividual(true);
        conf.setFitnessFunction(myFunc);
        // Now we need to tell the Configuration object how we want our
        // Chromosomes to be setup. We do that by actually creating a
        // sample Chromosome and then setting it on the Configuration
        // object. As mentioned earlier, we want our Chromosomes to each
        // have as many genes as there are different items available. We want the
        // values (alleles) of those genes to be integers, which represent
        // how many items of that type we have. We therefore use the
        // IntegerGene class to represent each of the genes. That class
        // also lets us specify a lower and upper bound, which we set
        // to senseful values (i.e. maximum possible) for each item type.
        // --------------------------------------------------------------
        Gene[] sampleGenes = new Gene[RoundSystems.size()];
        for (int i = 0; i < RoundSystems.size(); i++) {
            InitializeData.SecondarySystem system = RoundSystems.get(i);
            IntegerGene item = new IntegerGene(conf, 0, system.potentialSolutions.size() -1);
            sampleGenes[i] = item;
        }
        IChromosome sampleChromosome = new Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);
        // Finally, we need to tell the Configuration object how many
        // Chromosomes we want in our population. The more Chromosomes,
        // the larger number of potential solutions (which is good for
        // finding the answer), but the longer it will take to evolve
        // the population (which could be seen as bad).
        // ------------------------------------------------------------
        conf.setPopulationSize(10);
        // Create random initial population of Chromosomes.
        // ------------------------------------------------
        Genotype population = Genotype.randomInitialGenotype(conf);
        population.evolve();
        // Evolve the population. Since we don't know what the best answer
        // is going to be, we just evolve the max number of times.
        // ---------------------------------------------------------------
//        for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) {
//            population.evolve();
//        }
        // Save progress to file. A new run of this example will then be able to
        // resume where it stopped before!
        // ---------------------------------------------------------------------

        // represent Genotype as tree with elements Chromomes and Genes
        // ------------------------------------------------------------
//        DataTreeBuilder builder = DataTreeBuilder.getInstance();
//        IDataCreators doc2 = builder.representGenotypeAsDocument(population);
//        // create XML document from generated tree
//        // ---------------------------------------
//        XMLDocumentBuilder docbuilder = new XMLDocumentBuilder();
//        Document xmlDoc = (Document) docbuilder.buildDocument(doc2);
//        XMLManager.writeFile(xmlDoc, new File("solution.xml"));
        // Display the best solution we found.
        // -----------------------------------
        IChromosome bestSolutionSoFar = population.getFittestChromosome();
        Cost++;
         for (int i = 0; i < bestSolutionSoFar.size(); i++) {
            IntegerGene item = (IntegerGene) bestSolutionSoFar.getGene(i);
            int indexOfSolution = ((Integer) item.getAllele()).intValue();
            InitializeData.SecondarySystem system = RoundSystems.get(i);
            InitializeData.TVWS[] channels = system.potentialSolutions.get(indexOfSolution);
            Temp_Solution.put(channels, system);
           }
        if(bestSolutionSoFar.getFitnessValue()==1.0){
            if (it){
                Solution.clear();
                 Temp_Channels_As_List.clear();
              for(Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Temp_Solution.entrySet()){
                  int p=0;
                 InitializeData.TVWS[] space=entry.getKey();
                 InitializeData.SecondarySystem system=entry.getValue();
              for( InitializeData.TVWS current:space ){
                  p++;
                  if(p==system.bandwidth+1){
                   continue;
                  }
                  else{
                      Temp_Channels_As_List.add(current);
                  }
              }}
                fragmentation=MyFitnessFunction.check_fragmentation(Temp_Channels_As_List);
                for(Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Temp_Solution.entrySet()){
                 InitializeData.TVWS[] space=entry.getKey();
                 InitializeData.SecondarySystem system=entry.getValue();
                 Solution.put(space, system);
                }
            it=false;
            return_cost=Cost;
            continue;
            }
            else{
                //Creating Temp_Channels_As_List
                Temp_Channels_As_List.clear();
              for(Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Temp_Solution.entrySet()){
                  int p=0;
                 InitializeData.TVWS[] space=entry.getKey();
                 InitializeData.SecondarySystem system=entry.getValue();
              for( InitializeData.TVWS current:space ){
                  p++;
                  if(p==system.bandwidth){
                   continue;   
                  }
                  else{
                      Temp_Channels_As_List.add(current);
                  }
              } 
              }
              fragmenta=MyFitnessFunction.check_fragmentation(Temp_Channels_As_List);
              if(fragmenta<fragmentation){
               fragmentation=fragmenta;
               Solution.clear();
              for(Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Temp_Solution.entrySet()){
                 InitializeData.TVWS[] space=entry.getKey();
                 InitializeData.SecondarySystem system=entry.getValue();
                 Solution.put(space, system);
                }
              return_cost=Cost;   
                  }
                 
            }
         conf.reset();
        }
            
        conf.reset();
   }  
        end_time=System.nanoTime();
        elapsed_time=end_time-start_time;
        RoundTimeSolution.add(new RoundTimeSolutionClass.Solution(j,fragmentation,elapsed_time, new HashMap<InitializeData.TVWS[],InitializeData.SecondarySystem>(Solution)));
        Cost_List.add(return_cost);
        for(Map.Entry<InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Solution.entrySet()){
           InitializeData.TVWS[]channels =entry.getKey();
           Total_channelsUsed.addAll(Arrays.asList(channels));
              }
        
        //Make spectrum changes permanent
        Temp_Channels_As_List.clear();
        for(Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Solution.entrySet()){
                int p=0;
                 InitializeData.TVWS[] space=entry.getKey();
                 InitializeData.SecondarySystem system=entry.getValue();
              for( InitializeData.TVWS current:space ){
                  p++;
                  if(p==system.bandwidth){
                   continue;   
                  }
                  else{
                      Temp_Channels_As_List.add(current);
                  }
              } 
              }
        for(int m=0;m<Temp_Channels_As_List.size();m++){
     for(int i=0;i<MyFitnessFunction.tvws_local.size();i++){
        if( MyFitnessFunction.tvws_local.get(i)==Temp_Channels_As_List.get(m)){
                 MyFitnessFunction.tvws_local.get(i).isAvailable=false;
             }            
 }
}
        main.tvws=MyFitnessFunction.tvws_local;
       for(Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Solution.entrySet()){
            InitializeData.TVWS[] tv=entry.getKey();
            InitializeData.SecondarySystem system=entry.getValue();
            total_Solution.put(tv, system);
        }
       result.append("<HTML><HEAD><TITLE>Spectrum Allocation Results With Genetic Algorithm</TITLE></HEAD><BODY>");
        if(removed){
            for(int l=0;l<System_ID.size();l++){
                result.append("<p><b>System with ID ").append(System_ID.get(l)).append(" removed</b></p>");
            }
            removed=false;
        }
        result.append(Print(Solution));
        Solution.clear();
        Temp_Solution.clear();
        RoundSystems.clear();
        j++;
        }
        BarChart bar=new BarChart(Cost_List);
        bar.setMinimumSize(new java.awt.Dimension(800,600) );
        bar.setVisible(true);
         } catch (InvalidConfigurationException ex) {
            Logger.getLogger(MyProblem.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(Exception e){
            Logger.getLogger(MyProblem.class.getName()).log(Level.SEVERE, null, e);
        }
         result.append("</BODY>");
        return null;
        
    }
    
    
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
                   Result.append("fragmentation: ").append("<b>").append(fragmentation).append("</b>").append(" ");
                   Result.append("Spectrum Utilization: ").append("<b>").append(df.format(MyFitnessFunction.CalculateSpectrumUtilization())).append("</b>").append(" ");
                   Result.append("Bw exploited: ").append("<b>").append(df.format(MyFitnessFunction.CalculateBWexploited())).append("</b>").append(" ");
                   Result.append("Elapsed Time: ").append("<b>").append(df.format(elapsed_time)).append("</b>").append(" ");
            }
        } catch (Exception ex) {
         
        }    
           return Result;
       }
       
       // </editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Removing Passed Time Networks">
public static void Remove(){
    for (Map.Entry <InitializeData.TVWS[],InitializeData.SecondarySystem> entry:Solution.entrySet()){
                 InitializeData.SecondarySystem system=entry.getValue();
                 InitializeData.TVWS[] Channels=entry.getKey();
            if(system.end==(j-1)){
                         System_ID.add(system.id);
                         for(InitializeData.TVWS currentChannel:Channels){
                                 Total_channelsUsed.remove(currentChannel);
                            for(int i=0;i<MyFitnessFunction.tvws_local.size();i++){
                                   if((MyFitnessFunction.tvws_local.get(i).id==currentChannel.id) && (MyFitnessFunction.tvws_local.get(i).subid==currentChannel.subid)){
                                       MyFitnessFunction.tvws_local.get(i).isAvailable=true;
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
