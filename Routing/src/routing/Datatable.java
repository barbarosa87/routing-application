/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import com.sun.rowset.CachedRowSetImpl;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import routing.Enumerators.ReturnType;
import routing.Enumerators.TableNames;

/**
 *
 * @author barbarosa
 */
public class Datatable {
    public CachedRowSetImpl Fill(){
        try {
            DbConnection db=new DbConnection();
            CachedRowSetImpl Cach =(CachedRowSetImpl)db.SelectFromDb(TableNames.Node, null, null, ReturnType.CachedRowSet);
            return Cach;
        } catch (SQLException ex) {
            Logger.getLogger(Datatable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
