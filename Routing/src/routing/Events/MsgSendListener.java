/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Events;

import java.util.EventListener;

/**
 *
 * @author barbarosa
 */

public interface MsgSendListener extends EventListener {
   public void FireTableRowChanged(MsgSendEvent e);
}
