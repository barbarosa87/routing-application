/*

JAdhoc ver 0.2 - Java AODV (RFC 3561) Protocol Handler
Copyright 2003-2004 ComNets, University of Bremen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/

package jadhoc.net;

import jadhoc.JAdhoc;
import java.util.*;
import java.net.*;

import jadhoc.conf.*;
import jadhoc.other.*;
import jadhoc.os.*;

/**
* Class provide all the functions related to managing
* the collection that hold route information
*
* @author : Asanga Udugama
* @date : 11-aug-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class RouteList {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;
	public JAdhoc jadhoc;

	public OSOperationsInterface osOps;
	private Map routeList;
	private int currUnexpiredRouteCount;

	/**
	* Constructor that cretes the map object to hold data
	*
	* @param ConfigInfo cfg - config info object
	* @param CurrentInfo cfg - current info object
	*/
	public RouteList(ConfigInfo cfg, CurrentInfo cur, JAdhoc ja) {
		cfgInfo = cfg;
		curInfo = cur;
		jadhoc = ja;
		routeList = new HashMap();
		currUnexpiredRouteCount = 0;
	}

	/**
	*/
	public void start(OSOperationsInterface oo) {
		osOps = oo;
		routeList = new HashMap();
		currUnexpiredRouteCount = 0;
	}

	/**
	* Method to update a route entry object, given the key and the
	* object. If object is present, the existing is removed and new
	* object is inserted. Else inserted. Before inserting, a new
	* object is cloned from the given object
	*
	* @param InetAddress key - the key to search, i.e. IP address
	* @param RouteEntry entry - the entry to update
	*/
	public synchronized void update(InetAddress key, RouteEntry entry) throws Exception {
		RouteEntry oldEntry, clonedEntry;

		// remove the entry ( if there is an entry already, adjust
		//			the unexpired route count )
		oldEntry = (RouteEntry) routeList.get(key);
		if(oldEntry != null && oldEntry.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {
			currUnexpiredRouteCount--;
		}


		// update kernel routing table

		// if kernel route is SET in old entry and also should SET for new entry
		if(oldEntry != null && oldEntry.kernelRouteSet
		    && entry.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

			entry.kernelRouteSet = true;

		// if kernel route is NOT SET in old entry but should SET for new entry
		} else if((oldEntry == null || !oldEntry.kernelRouteSet)
		          && entry.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

		    	// set kernel route
			entry.kernelRouteSet = true;
			osOps.addRoute(entry);

		// if kernel route is SET in old entry but should NOT SET in new entry
		} else if(oldEntry != null && oldEntry.kernelRouteSet
		           && (entry.routeStatusFlag != RouteEntry.ROUTE_STATUS_FLAG_VALID)) {

			// remove the kernel route entry
			entry.kernelRouteSet = false;
			osOps.deleteRoute(entry);

		// if kernel route is NOT SET in old entry and also should NOT SET for new entry
		} else {

			entry.kernelRouteSet = false;
		}



		// clone the entry and add to list and adjust the unexpired route
		// count
		clonedEntry = entry.getCopy();
		routeList.put(key, clonedEntry);
		if(clonedEntry.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {
			currUnexpiredRouteCount++;
		}


		jadhoc.updateDisplay();

		// log
		curInfo.log.write(Logging.ACTIVITY_LOGGING,
				"Route List - Route updated, " +  entry.toString());
	}

	/**
	* Method to remove a route entry given the key. The method
	* removes the object and returns it;
	*
	* @param InetAddress key - the key to use to retrieve and delete
	* @return RouteEntry - the deleted object
	*/
	public synchronized RouteEntry remove(InetAddress key) {
		RouteEntry oldEntry, clonedEntry;

		// if there is an entry, adjust the unexpired route count
		oldEntry = (RouteEntry) routeList.get(key);
		if(oldEntry != null && oldEntry.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {
			currUnexpiredRouteCount--;
		}

		// update kernel routing table
		// if kernel route is SET in old entry then delete
		if(oldEntry != null && oldEntry.kernelRouteSet) {

			// remove the kernel route entry
			osOps.deleteRoute(oldEntry);
		}

		routeList.remove(key);
		jadhoc.updateDisplay();

		// log
		curInfo.log.write(Logging.ACTIVITY_LOGGING,
				"Route List - Route removed, " +  oldEntry.toString());

		return oldEntry;
	}

	/**
	* Method to get a route entry object. Always returns a
	* clon of the original route entry object.
	*
	* @param InetAddress key - the key to use to retrieve
	* @return RouteEntry - the retrieved (cloned) object
	*/
	public synchronized RouteEntry get(InetAddress key) throws Exception {
		RouteEntry rte;

		rte = (RouteEntry) routeList.get(key);
		if(rte == null)
			return null;
		else {
			// always return a copy
			return rte.getCopy();
		}
	}

	/**
	* Method to check whether there exist atleast one
	* unexpired routes.
	* @return boolean - true if atleast one unexpired
	*			route else returns false
	*/
	public synchronized boolean doUnexpiredRoutesExist() {
		if(currUnexpiredRouteCount > 0)
			return true;
		return false;
	}

	public synchronized int getRouteCount() {
		return routeList.size();
	}

	public synchronized RouteEntry[] getRouteArray() throws Exception {
		Object array[];
		RouteEntry rtArray[];
		int i;

		array = (routeList.values()).toArray();
		if(array.length == 0) {
			return null;
		}

		rtArray = new RouteEntry[array.length];

		for(i = 0; i < array.length; i++) {
			rtArray[i] = ((RouteEntry) array[i]).getCopy();
		}

		return rtArray;
	}


	public synchronized void stop() {
		Object array[];
		RouteEntry rte;
		int i;

		array = (routeList.values()).toArray();
		for(i = 0; i < array.length; i++) {
			rte = (RouteEntry) array[i];

			// remove kernel oute entry, if exist
			if(rte.kernelRouteSet) {
				osOps.deleteRoute(rte);
			}

			routeList.remove(rte.destIPAddr);
		}
		currUnexpiredRouteCount = 0;
	}
}
