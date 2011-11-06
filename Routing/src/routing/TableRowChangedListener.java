/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.util.EventListener;

/**
 *
 * @author barbarosa
 */

public interface TableRowChangedListener extends EventListener {
   public void FireTableRowChanged(TableRowChangedEvent e);
}
