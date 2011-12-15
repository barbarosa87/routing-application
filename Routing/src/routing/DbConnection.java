/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;


import routing.Enumerators.TableNames;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import routing.Enumerators.GetDataFromEnumerators;


public  class DbConnection extends GetDataFromEnumerators {

//<editor-fold defaultstate="collapsed" desc="Variable Declaration">
    private String ConnectionUrl="jdbc:sqlite:db.sqlite";
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="Constructors">
//    public DbConnection(ReturnType returnType){
//        try{
//                    org.sqlite.JDBC jd=new org.sqlite.JDBC();
//                    cac = new CachedRowSetImpl();
//                    cac.setUrl(ConnectionUrl);
//                    
//        }catch(SQLException ex){
//            ex.printStackTrace();
//        }
//    }
//    
    public DbConnection(){
        
    }
    
    //</editor-fold>
        
//<editor-fold defaultstate="collapsed" desc="ConnectAndReturnConnection">
    public Connection Connect() {

        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn =DriverManager.getConnection(ConnectionUrl);
            return conn;
        }catch(SQLException ex){
            ex.printStackTrace();
        }catch(ClassNotFoundException e){
            //Handle Exception
            e.printStackTrace();
        }
        return null;
    }
    //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="SelectFromDb">
  public ResultSet SelectFromDb(TableNames TableType,String Clause,Connection conn)throws SQLException{
          String Type=GetTableName(TableType);
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("SELECT * FROM "+Type +" "+Clause);
          return rs;
      
  }
  //</editor-fold>
  
  
//<editor-fold defaultstate="collapsed" desc="SelectFromDb">
  public ResultSet GetCountFromDB(TableNames TableType,String Clause,Connection conn ) throws SQLException{
      String Type=GetTableName(TableType);
      
                Statement stat=conn.createStatement();
                ResultSet rs=stat.executeQuery("SELECT Count(*) FROM "+Type+" "+Clause);
                return rs; 
     
  }
  //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="ReturnUniqueKey">  
public int ReturnUniqueKey(TableNames TableType,Connection conn){
          String Type=GetTableName(TableType);
          int i=0;
          try{
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("Select * From "+Type);
          while(rs.next()){
              i=rs.getInt("ID");
          }
          }catch(SQLException ex){
              ex.printStackTrace();
          }
          return i;
  }
  //</editor-fold>
  
//<editor-fold defaultstate="collapsed" desc="AddToDb">
  public void AddToDb(String statement,Connection conn){
      try{
          Statement stat=conn.createStatement();
          stat.executeUpdate(statement);
      }catch(SQLException ex){
          ex.printStackTrace();
      }
  }
  //</editor-fold>
 
//<editor-fold defaultstate="collapsed" desc="RemoveFromDbWithClause">
  public void RemoveFromDb(TableNames TableType,Connection conn,String id) throws SQLException{
          String Type=GetTableName(TableType);
          Statement stat=conn.createStatement();
          stat.executeUpdate("DELETE FROM "+Type+" WHERE ID= "+id);
          stat.close();
     
  }
  //</editor-fold>
  
 
//<editor-fold defaultstate="collapsed" desc="TruncateTables">
 public void TruncateTables(TableNames TableType,Connection conn,boolean all){
     try{
     if(all){
         
             Statement stat=conn.createStatement();
             //stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.Area));
             //stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.Node));
             stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.MessageExchange));
             stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.NodesWeight));
             //stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.GeolocationDb));
             //stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.AreaFrequencies));
     }else{
             String Type=GetTableName(TableType);
             Statement stat=conn.createStatement();
             stat.executeUpdate("DELETE FROM "+Type);
        
         
         
     }
     
     }catch(SQLException ex){
         ex.printStackTrace();
     }
 }
 //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="UpdateTableColumnValue">
 public void UpdateTableColumnValue(TableNames TableType,String ColumnName,int ColumnValue,String Clause,Connection conn) throws SQLException{
    
         String Type=GetTableName(TableType);
         Statement stat=conn.createStatement();
         stat.executeUpdate("UPDATE "+Type+" SET "+ColumnName+" =" +ColumnValue+" "+Clause);
    
 }
 //</editor-fold>

} 
