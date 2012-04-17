
package Annealing;

// <editor-fold defaultstate="collapsed" desc="Imports">
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
// </editor-fold>

public class InitializeData {
 
// <editor-fold defaultstate="collapsed" desc="Creating class TVWS">
     public static class TVWS {

        public final int id;
        public final int subid;
        public final int bandwidth;
        public final int transmissionPower;
        public  boolean isAvailable;
      
        public TVWS(int id,int subid, int bandwidth, int transmissionPower, boolean isAvailable) {
            this.id = id;
            this.subid=subid;
            this.bandwidth = bandwidth;
            this.transmissionPower = transmissionPower;
            this.isAvailable = isAvailable;
        }
}

     // </editor-fold>
     
// <editor-fold  defaultstate="collapsed" desc="Creating Class SecondarySystem">
    public static class SecondarySystem {

        public  String name;
        public int id;
        public final int bandwidth;
        public final int transmissionPower;
        public final int priority;
        public final int start;
        public final int end;
        public int diff=0;
        public List<TVWS[]> potentialSolutions = Collections.EMPTY_LIST;

    public SecondarySystem(String name,int id, int bandwidth, int transmissionPower,int priority,int start,int end) {
            this.name = name;
            this.id=id;
            this.bandwidth = bandwidth;
            this.transmissionPower = transmissionPower;
            this.priority = priority;
            this.start=start;
            this.end=end;
            this.potentialSolutions=Calculations.getValidChannelsCombinations(Main.tvws,Integer.parseInt(Main.bandwidth));
    }
  
    }
    // </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc="Calcaulations for potential solution">
 public static class Calculations{
      public static List<TVWS[]> getValidChannelsCombinations(List<TVWS> channels, int bandwidth) {
        List<TVWS[]> potentialSolutions = new ArrayList<TVWS[]>();
        TVWS[] validChannels = new TVWS[bandwidth+1];
        for (int index = 0; index < channels.size(); index++) {//Για κάθε ενα κανάλια
        validChannels = new TVWS[bandwidth+1];//δημιουργώ πίνακα μεγάθους ίσου με το πλήθος των συνεχόμενων καναλιών που απαιτούνται
            TVWS currentChannel = channels.get(index);
            if (currentChannel.isAvailable) {//Εαν το τρέχον κανάλι ειναι διαθέσιμο, δηλαδή ειναι ελεύθερο
                int indexInSolution = 0;
                validChannels[indexInSolution++] = currentChannel;//Τότε το ορίζω ως το πρώτο για την τρέχουσα λύση
                int maxIndexForSystem = index + (Integer.parseInt(Main.bandwidth)+1);//Βρίσκω ποιος ειναι ο δείκτης του τελευταίου καναλιού, προκειμένου να πάρω τόσα συνεχόμενα όσα απαιτούνται
                for (int index2 = index + 1; ((index2 < maxIndexForSystem) && (index2 < channels.size())); index2++) {//ΣΗΜΕΙΩΣΗ: Παίρνω ένα ενα τα κανάλια μέχρι τον δεικτη που υπολόγισα, εκτός κι αν ο δεικτης αυτός ειναι εκτός του μεγέθους της λίστας
                    currentChannel = channels.get(index2);
                    if (currentChannel.isAvailable) {//Εαν το τρέχον κανάλι ειναι διαθέσιμο, δηλαδή ελεύθερο
                        validChannels[indexInSolution++] = currentChannel;//Το προσθέσω στην επόμενη θέση της τρέχουσας λύσης
                    } else {//Διαφορετικά
                        break;//Διακόπτω την διαδικασία καθώς δεν υπάρχουν τόσα διαθέσιμα κανάλια στην σειρά ξεκινώντας από τον δεικτη index, όσα χρειάζεται το σύστημα
                    }
                }
            }
            //Κάνω έλεγχο εαν ο πίνακας της λύσης έχει συμπληρωθεί πλήρως
            boolean hasEmpty = false;
            for (TVWS t : validChannels) {
                if (t == null) {
                    hasEmpty = true;
                    break;
                }
            }
            if (!hasEmpty) {
                potentialSolutions.add(validChannels);//Εαν ειναι πλήρως συμπληρωμένος, συνεπάγεται ότι βρέθηκαν τοσα διαθέσιμα κανάλια οσα απαιτούνται στην σειρά
            }
        }

        return potentialSolutions;//Αφού ολοκλήρωσα των έλεγχο σε όλα τα διαθέσιμα κανάλια επιστρέφω την λίστα με τις ενδεχόμενες λύσεις
    }
    }
 // </editor-fold>
}
