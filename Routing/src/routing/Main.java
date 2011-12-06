/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.awt.Toolkit;
import java.sql.Connection;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import routing.Enumerators.ReturnType;






public class Main {

  public static void main(String args[]) {
          try {
    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
            UIManager.setLookAndFeel(info.getClassName());
            break;
        }
    }
} catch (Exception e) {
    
}
   DbConnection db=new DbConnection();
   Connection conn=db.Connect();
   db.TruncateTables(null, conn, true);
   frmSplash Splash=new frmSplash();
   Splash.setIconImage(Toolkit.getDefaultToolkit().getImage("./Resources/globe.png"));
   Splash.setTitle("CR Routing Simulator");
   Splash.setLocationRelativeTo(null);
   Splash.setVisible(true);
   
//   FrmNodes frmnodes = new FrmNodes();
//   frmnodes.setIconImage(Toolkit.getDefaultToolkit().getImage("./Resources/globe.png"));
//   frmnodes.setTitle("Nodes Table");
//   frmnodes.setLocationRelativeTo(null);
//   frmnodes.setVisible(true);
  }
}
