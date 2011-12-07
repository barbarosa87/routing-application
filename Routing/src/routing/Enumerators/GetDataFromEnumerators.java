/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Enumerators;

/**
 *
 * @author barbarosa
 */
public class GetDataFromEnumerators {
    public String GetTableName(TableNames TableType){
     String Type="";
     switch (TableType){
         case Area:Type="Areas";break;
         case Node:Type="Nodes";break;
         case NodesNeighbours:Type="NodesNeighbours";break;
         case AreasNeighbours:Type="AreasNeighbours";break;
         case GeolocationDb:Type="GeolocationDb";break;
         case MessageExchange:Type="MessageExchange";break;
         case NodesWeight:Type="NodesWeight";break;
         case AreaFrequencies:Type="AreaFrequencies";break;
         default:Type="Error";break;
     }
     return Type;
}
}
