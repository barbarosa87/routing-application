/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import javax.swing.event.EventListenerList;

/**
 *
 * @author barbarosa
 */
public class TableRowChanged {
protected EventListenerList listenerList = new EventListenerList();

  public synchronized  void addMyEventListener(TableRowChangedListener listener) {
    listenerList.add(TableRowChangedListener.class, listener);
  }
  
  public synchronized  void removeMyEventListener(TableRowChangedListener listener) {
    listenerList.remove(TableRowChangedListener.class, listener);
  }
  
  public synchronized void FireTableRowChanged(TableRowChangedEvent evt) {
    Object[] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i = i+2) {
      if (listeners[i] == TableRowChangedListener.class) {
        ((TableRowChangedListener) listeners[i+1]).FireTableRowChanged(evt);
      }
    }
  }    
}
