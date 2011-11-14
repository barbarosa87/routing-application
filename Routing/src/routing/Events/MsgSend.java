/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.Events;

import javax.swing.event.EventListenerList;


/**
 *
 * @author barbarosa
 */
public class MsgSend {
protected EventListenerList listenerList = new EventListenerList();

  public synchronized  void addMyEventListener(MsgSendListener listener) {
    listenerList.add(MsgSendListener.class, listener);
  }
  
  public synchronized  void removeMyEventListener(MsgSendListener listener) {
    listenerList.remove(MsgSendListener.class, listener);
  }
  
  public synchronized void FireTableRowChanged(MsgSendEvent evt) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i = i+2) {
      if (listeners[i] == MsgSendListener.class) {
        ((MsgSendListener) listeners[i+1]).FireTableRowChanged(evt);
      }
    }
  }    
}
