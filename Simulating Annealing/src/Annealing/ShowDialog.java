
package Annealing;

//<editor-fold defaultstate="collapsed" desc="Imports">
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Show Confirmation dialog">
public class ShowDialog {
    private final Desktop desktop;
    public ShowDialog(File file){
    int choise=JOptionPane.showConfirmDialog(null, "Να ανοίξω τον Default browser για να παρουσιάσω τα αποτελέσματα ???", "Confirmation Box", JOptionPane.OK_CANCEL_OPTION);
   desktop=Desktop.getDesktop();
    switch (choise){
       case 0:{
           try{
             desktop.open(file);  
           }catch(IOException e){System.out.println("IOException ShowDialog class");} 
           break;
                   }
       case 1:{
           break;
       }
   }
    }
//</editor-fold>
}

