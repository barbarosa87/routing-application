
package ga;

//<editor-fold defaultstate="collapsed" desc="Imports">
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.csvreader.CsvReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
//</editor-fold>

public class main{

// <editor-fold defaultstate="collapsed" desc="Variable  initialization">    
public String power;
public static String ContagiousTVWS;
public static String bandwidth ;
public static List tvws = new ArrayList<InitializeData.TVWS>();      
List<InitializeData.SecondarySystem> systems = new ArrayList<InitializeData.SecondarySystem>();
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Constructor returning the solution">
public main(){
    tvws=getTvws() ;
    ReadCsv();
    try {Writer output = null;
      File file = new File("Results.html");
      output = new BufferedWriter(new FileWriter(file));
      MyProblem my=new MyProblem();
      Map<InitializeData.TVWS[], InitializeData.SecondarySystem> solution = my.solve(systems);
      output.write(MyProblem.result.toString());
      output.close();
      ShowDialog showDialog = new ShowDialog(file);
      } catch (IOException ex) {System.out.println("IOException in NewMain");}
    
}
// </editor-fold>

// <editor-fold  defaultstate="collapsed" desc="Main Function">
public static  void main(String args[]) {
    main m=new main();
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Create TVWS spectrum">
public List getTvws(){
           //Initialize TVWS table
    List tvws = new ArrayList<InitializeData.TVWS>();
    for(int i=40;i<=60;i++){
        switch (i){
            case 40:{
                for(int k=0;k<8;k++){
                tvws.add(new InitializeData.TVWS(i, k, 1, 4, true));
                }
                break;
            }
            case 41:{
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 4, true));
                }
                break;
            }
            case 42:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1,4, true));
                }
                break;
            case 43:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 2, true));
                }
                break;
            case 44:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 0, false));
                }
                break;
            case 45:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 2, true));
                }
                break;
            case 46:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1,4, true));
                }
                break;
            case 47:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 2, true));
                }
                break;
            case 48:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1,0, false));
                }
                break;
            case 49:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1,2, true));
                }
                break;
            case 50:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1,4, true));
                }
                break;
            case 51:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 4, true));
                }
                break;
            case 52:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1,4, true));
                }
                break;
            case 53:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 2, true));
                }
                break;
            case 54:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 0, false));
                }
                break;
            case 55:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 2, true));
                }
                break;
            case 56:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 0, false));
                }
                break;
            case 57:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 2, true));
                }
                break;
            case 58:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 4, true));
                }
                break;
            case 59:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1, 4, true));
                }
                break;
            case 60:
                for(int k=0;k<8;k++){
                    tvws.add(new InitializeData.TVWS(i, k, 1,4, true));
                }
                break;
            
        }
     }
    return tvws;  
   }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Function Reading the csv">
public void ReadCsv(){
    int id=0;
    try {
			
			CsvReader services = new CsvReader("services.csv");
		         services.readHeaders();
			while (services.readRecord())
			{
				String service = services.get("service");
				power = services.get("power");
				bandwidth = services.get("bandwidth");
				String priority = services.get("priority");
				String start = services.get("start");
				String end = services.get("end");
                                if(service.equals("FDD")){
                                     systems.add(new InitializeData.SecondarySystem("LTE DL",id, Integer.parseInt(bandwidth), Integer.parseInt(power), Integer.parseInt(priority), Integer.parseInt(start), Integer.parseInt(end)));
                                     systems.add(new InitializeData.SecondarySystem("LTE UL",id, Integer.parseInt(bandwidth), Integer.parseInt(power), Integer.parseInt(priority), Integer.parseInt(start), Integer.parseInt(end)));
                                }
                                else if(service.equals("WiFi")){
                                   systems.add(new InitializeData.SecondarySystem("WiFi",id, Integer.parseInt(bandwidth), Integer.parseInt(power), Integer.parseInt(priority), Integer.parseInt(start), Integer.parseInt(end))); 
                                }
                                else if(service.equals("TDD")){
                                    systems.add(new InitializeData.SecondarySystem("LTE TDD",id, Integer.parseInt(bandwidth), Integer.parseInt(power), Integer.parseInt(priority), Integer.parseInt(start), Integer.parseInt(end)));
                                }
                                else if(service.equals("Public Safety")){
                                    systems.add(new InitializeData.SecondarySystem("Public Safety",id, Integer.parseInt(bandwidth), Integer.parseInt(power), Integer.parseInt(priority), Integer.parseInt(start), Integer.parseInt(end)));
                                }
                                id++;
                        }
	
			services.close();
			
		} catch (FileNotFoundException e) {
		System.out.println("FileNotFound");
                } catch (IOException e) {
		System.out.println("ExceptionThrown");
                }
		
	}
//</editor-fold>

}


