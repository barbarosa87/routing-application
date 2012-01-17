/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;


import java.sql.*;
import routing.Enumerators.TableNames;


public  class DbConnection  {

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
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("SELECT * FROM "+TableType.toString()+" "+Clause);
          return rs;
  }
  //</editor-fold>
  
  
//<editor-fold defaultstate="collapsed" desc="SelectFromDb">
  public ResultSet GetCountFromDB(TableNames TableType,String Clause,Connection conn ) throws SQLException{
                Statement stat=conn.createStatement();
                ResultSet rs=stat.executeQuery("SELECT Count(*) FROM "+TableType.toString()+" "+Clause);
                return rs; 
     
  }
  //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="ReturnUniqueKey">  
public int ReturnUniqueKey(TableNames TableType,Connection conn){
          int i=0;
          try{
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("Select * From "+TableType.toString());
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
          Statement stat=conn.createStatement();
          stat.executeUpdate("DELETE FROM "+TableType.toString()+" WHERE ID= "+id);
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
             stat.executeUpdate("DELETE FROM "+TableNames.MessageExchange.toString());
             stat.executeUpdate("DELETE FROM "+TableNames.NodesWeight.toString());
             //stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.GeolocationDb));
             //stat.executeUpdate("DELETE FROM "+GetTableName(TableNames.AreaFrequencies));
     }else{
             Statement stat=conn.createStatement();
             stat.executeUpdate("DELETE FROM "+TableType.toString());
     }
     
     }catch(SQLException ex){
         ex.printStackTrace();
     }
 }
 //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="UpdateTableColumnValue">
 public void UpdateTableColumnValue(TableNames TableType,String ColumnName,int ColumnValue,String Clause,Connection conn) throws SQLException{
         Statement stat=conn.createStatement();
         stat.executeUpdate("UPDATE "+TableType.toString()+" SET "+ColumnName+" =" +ColumnValue+" "+Clause);
    
 }
 //</editor-fold>

} 
