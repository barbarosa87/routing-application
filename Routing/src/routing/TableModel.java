/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import routing.Enumerators.TableNames;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import routing.Enumerators.ReturnType;


public class TableModel extends AbstractTableModel {
 //Variable Declaration
    Vector cache;
    Statement statement;
    int colCount;
    String[] headers;
    private String[] AreaColumnNames={"ID","Frequencies"};
 
     
    
    @Override
  public String getColumnName(int i) {
    return headers[i];
  }

    @Override
    public int getRowCount() {
      return cache.size();
    }

    @Override
    public int getColumnCount() {
         return colCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((String[]) cache.elementAt(rowIndex))[columnIndex];
    }
    
    
    
    public TableModel(TableNames TableType) {
    cache = new Vector();
    DbConnection db=new DbConnection();
    Connection conn=db.Connect();
    //String Type=null;
    
     try {
         ResultSet rs;
      // Execute the query and store the result set and its metadata
      switch (TableType){
          case Area:
              //Type="Areas";
              //statement = conn.createStatement(); 
              rs = (ResultSet)db.SelectFromDb(TableType, "", conn, ReturnType.ResultSet);
              colCount=2;
              headers = new String[colCount];
              headers[0]="ID";
              headers[1]="Frequencies";
              while(rs.next()){
                  String[] record = new String[colCount];
                  ResultSet rs2=(ResultSet)db.SelectFromDb(TableNames.AreaFrequencies, "WHERE ID= "+rs.getInt("ID") , conn, ReturnType.ResultSet);
                  record[0]=rs.getString("ID");
                  while (rs2.next()){
                      if (record[1]==null){
                          record[1] =rs2.getString("Frequency");
                          continue;
                      }
                      record[1] = record[1]+","+ rs2.getString("Frequency");
              }
              cache.addElement(record);
              }
              conn.close();
              rs.close();
              break;
          case Node:
              //Type="Nodes";
              //statement = conn.createStatement(); 
              //rs = statement.executeQuery("Select * from "+Type);
              rs=(ResultSet)db.SelectFromDb(TableType, "", conn, ReturnType.ResultSet);
              ResultSetMetaData meta = rs.getMetaData();
              colCount = meta.getColumnCount();
              // Now we must rebuild the headers array with the new column names
             headers = new String[colCount];
             for (int h = 1; h <= colCount; h++) {
               headers[h - 1] = meta.getColumnName(h);
            }
   
      while (rs.next()) {
        String[] record = new String[colCount];
        for (int i = 0; i < colCount; i++) {
          record[i] = rs.getString(i + 1);
        }
        cache.addElement(record);
      }
      //fireTableChanged(null); // notify everyone that we have a new table.
      conn.close();
      rs.close();
              break;
      }  
//      statement = conn.createStatement(); 
//      ResultSet rs = statement.executeQuery("Select * from "+Type);
//      ResultSetMetaData meta = rs.getMetaData();
//      colCount = meta.getColumnCount();
//      // Now we must rebuild the headers array with the new column names
//      headers = new String[colCount];
//      for (int h = 1; h <= colCount; h++) {
//        headers[h - 1] = meta.getColumnName(h);
//      }
//   
//      while (rs.next()) {
//        String[] record = new String[colCount];
//        for (int i = 0; i < colCount; i++) {
//          record[i] = rs.getString(i + 1);
//        }
//        cache.addElement(record);
//      }
//      //fireTableChanged(null); // notify everyone that we have a new table.
//      conn.close();
//      rs.close();
     } catch (Exception e) {
      cache = new Vector(); // blank it out and keep going.
      e.printStackTrace();
    }
    
  }

    
}
