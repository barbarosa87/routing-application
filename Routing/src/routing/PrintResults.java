/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.util.ArrayList;
import java.util.List;
import routing.Structs.FlowStruct;
import routing.Presentation.PresentationPrimitive;

/**
 *
 * @author barbarosa
 */
public class PrintResults {
    private List<FlowStruct.Flow> Flows=new ArrayList<FlowStruct.Flow>();
    
    public PrintResults(List<FlowStruct.Flow> Flows){
        this.Flows=Flows;
        PresentationPrimitive pr=new PresentationPrimitive(Flows);
        pr.setVisible(true);
        
    }
    
}
