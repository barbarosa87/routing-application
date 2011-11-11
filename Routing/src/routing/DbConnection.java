/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public  class DbConnection {
    Connection conn;
    
//<editor-fold defaultstate="collapsed" desc="Constructor">
    public DbConnection(){
        //Class Constructor
    }
    //</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="ReturnConnection">
    public Connection Connect()  {
        try{
            Class.forName("org.sqlite.JDBC");
            conn =DriverManager.getConnection("jdbc:sqlite:db.sqlite");
            //Statement stat = conn.createStatement();
            return conn;
        }catch(SQLException e){
            //Handle Exception
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            //Handle Exception
            e.printStackTrace();
        }
        return null;
    }
    //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="SelectFromDbWithClause">
  public ResultSet SelectFromDbWithClause(EnumeRators TableType,String Clause,Connection conn){
      String Type=CheckEnumeration(TableType);
      try{
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("SELECT * FROM "+Type +" "+Clause);
          return rs;
      }catch(SQLException e){
          e.printStackTrace();;
      }
      return null;
  }
  //</editor-fold>
  
  
//<editor-fold defaultstate="collapsed" desc="SelectFromDb">
  public ResultSet SelectFromDb(EnumeRators TableType,Connection conn){
      String Type=CheckEnumeration(TableType);
      try{
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("SELECT * FROM "+Type);
          return rs;
      }catch(SQLException e){
          e.printStackTrace();;
      }
      return null;
  }
  //</editor-fold>

//<editor-fold defaultstate="collapsed" desc="ReturnUniqueKey">  
  public int ReturnUniqueKey(EnumeRators TableType,Connection conn){
      String Type=CheckEnumeration(TableType);
      int i=0;
      try{
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("Select * From "+Type);
          while(rs.next()){
              i=rs.getInt("ID");
          }
          return i;
      }catch(SQLException e){
          e.printStackTrace();
          
      }
      return i;
  }
  //</editor-fold>
  
//<editor-fold defaultstate="collapsed" desc="AddToDb">
  public void AddToDb(String statement,Connection conn){
      try{
          Statement stat=conn.createStatement();
          stat.executeUpdate(statement);
      }catch(SQLException e){
          e.printStackTrace();
      }
  }
  //</editor-fold>
 
//<editor-fold defaultstate="collapsed" desc="GetRowCount">
  public ResultSet getRowCount(EnumeRators TableType,Connection conn){
      String Type=CheckEnumeration(TableType);
      try{
          Statement stat=conn.createStatement();
          ResultSet rs=stat.executeQuery("SELECT COUNT(*) as 'RowCount' FROM "+Type);
          return rs;
      }catch(SQLException e){
          e.printStackTrace();;
      }
      return null;
  }
  //</editor-fold>
 
//<editor-fold defaultstate="collapsed" desc="RemoveFromDbWithClause">
  public void RemoveFromDb(EnumeRators TableType,Connection conn,String id){
      String Type=CheckEnumeration(TableType);
      try{
          Statement stat=conn.createStatement();
          stat.executeUpdate("DELETE FROM "+Type+" WHERE ID= "+id);
          stat.close();
      }catch(SQLException e){
          e.printStackTrace();
      }
  }
  //</editor-fold>
  
//<editor-fold defaultstate="collapsed" desc="GettingTableType">
 public String CheckEnumeration(EnumeRators TableType){
     String Type="";
     switch (TableType){
         case Area:Type="Areas";break;
         case Node:Type="Nodes";break;
         case NodesNeighbours:Type="NodesNeighbours";break;
         case AreasNeighbours:Type="AreasNeighbours";break;
         case GeolocationDb:Type="GeolocationDb";break;
         case MessageExchange:Type="MessageExchange";break;
         default:Type="Error";break;
     }
     return Type;
 }
 //</editor-fold>
 
 
//<editor-fold defaultstate="collapsed" desc="TruncateTables">
 public void TruncateTables(EnumeRators TableType,Connection conn,boolean all){
     if(all){
         try{
             Statement stat=conn.createStatement();
             //stat.executeUpdate("DELETE FROM "+CheckEnumeration(EnumeRators.Area));
             //stat.executeUpdate("DELETE FROM "+CheckEnumeration(EnumeRators.Node));
             stat.executeUpdate("DELETE FROM "+CheckEnumeration(EnumeRators.AreasNeighbours));
             stat.executeUpdate("DELETE FROM "+CheckEnumeration(EnumeRators.NodesNeighbours));
             stat.executeUpdate("DELETE FROM "+CheckEnumeration(EnumeRators.MessageExchange));
         }catch(SQLException e){
             e.printStackTrace();
         }
         
     }else{
         String Type=CheckEnumeration(TableType);
         try{
             Statement stat=conn.createStatement();
             stat.executeUpdate("DELETE FROM "+Type);
         }catch(SQLException e){
             e.printStackTrace();
         }
         
         
     }
     
     
 }
 //</editor-fold>



} 
