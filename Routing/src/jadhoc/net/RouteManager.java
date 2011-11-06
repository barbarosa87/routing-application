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

import java.net.*;
import java.util.*;

import jadhoc.*;
import jadhoc.conf.*;
import jadhoc.other.*;
import jadhoc.minders.*;
import jadhoc.os.*;
import jadhoc.msg.*;
//import jadhoc.net.RouteList;

/**
* This class manages all the route environment manipulation
* activities. All methods are syncronized as many threads
* would be requesting services thru this object and only one
* thread should manipulate the routing environment at a time.
* The threads that manipulate the object are,
*  - packet listener
*  - Hello minder
*  - route minder (one for each active route)
*  - RREQID minder
*
* In addition to these it also calls other objects to provide
* services. These are,
*  - activity logging
*  - AODV message sender
*  - OS interace
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
* @modification history
*  11-feb-2004  packet sender for each OS
*		OS module creation in route manger now
*
*/
public class RouteManager {
	public JAdhoc jadhoc;
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	public RREQIDList idList;
	public RouteList rtList;
	public RouteDiscoveryList rdList;

	public boolean rtMgrActive;

	public PacketSenderInterface pktSender;
	public OSOperationsInterface osOps;
	public Thread pktListener;
	public RREQIDMinder idMinder;
	public HelloMinder helloMinder;

	/**
	* Constructor creates the UDP socket to be passed
	* to the sender and listener for AODV mesaging
	* @param JAdhoc ja - uses this object to pass
	*			messages to the GUI
	* @param ConfigInfo cfg - uses to obtain config
	*			info and to pass to other
	*			objects
	*/
	public RouteManager(ConfigInfo cfg, CurrentInfo cur, JAdhoc ja) throws Exception {
		cfgInfo = cfg;
		curInfo = cur;
		jadhoc = ja;

		rtMgrActive = false;

		// create the collections for route list, RREQ ID list & the
		// Route Discovery list
		idList = new RREQIDList(cfgInfo, curInfo);
		rtList = new RouteList(cfgInfo, curInfo, jadhoc);
		rdList = new RouteDiscoveryList(cfgInfo, curInfo);


		// create message sender (based on OS)
		if(cfgInfo.osInUse.toLowerCase().equals(ConfigInfo.LINUX_OS)) {
			pktSender = new PacketSenderLinux(cfgInfo, curInfo);

		} else if(cfgInfo.osInUse.toLowerCase().equals(ConfigInfo.WINDOWS_OS)) {
			pktSender = new PacketSenderWindows(cfgInfo, curInfo);

		} else if(cfgInfo.osInUse.toLowerCase().equals(ConfigInfo.ZAURUS_OS)) {
			pktSender = new PacketSenderZaurus(cfgInfo, curInfo);

		} else {
			pktSender = null;
		}

		// create os ops
		if(cfgInfo.osInUseVal.trim().toLowerCase().equals(ConfigInfo.LINUX_OS)) {
			if(cfgInfo.ipVersionVal == ConfigInfo.IPv4_VERSION_VAL) {// ipv4
				osOps = new OSOperationsLinuxIPv4(cfgInfo, curInfo);
			} else {// ipv6
				//osOps = new OSOperationsLinuxIPv6(cfgInfo, curInfo);
			}
		} else if(cfgInfo.osInUseVal.trim().toLowerCase().equals(ConfigInfo.WINDOWS_OS)) {
			if(cfgInfo.ipVersionVal == ConfigInfo.IPv4_VERSION_VAL) {// ipv4
				osOps = new OSOperationsWindowsIPv4(cfgInfo, curInfo);
			} else {// ipv6
				//osOps = new OSOperationsWindowsIPv6(cfgInfo, curInfo);
			}
		} else if(cfgInfo.osInUseVal.trim().toLowerCase().equals(ConfigInfo.ZAURUS_OS)) {
			if(cfgInfo.ipVersionVal == ConfigInfo.IPv4_VERSION_VAL) {// ipv4
				osOps = new OSOperationsZaurusIPv4(cfgInfo, curInfo);
			} else {// ipv6
				//osOps = new OSOperationsZaurusIPv6(cfgInfo, curInfo);
			}

		 // add for other operating systems here
		} else {
			// log
			curInfo.log.write(Logging.CRITICAL_LOGGING, "Route Manager - Unupported OS when OSops init");
			osOps = null;
		}

		// create packet listener thread (based on OS)
		if(cfgInfo.osInUse.toLowerCase().equals(ConfigInfo.LINUX_OS)) {
			pktListener = new PacketListenerLinux(cfgInfo, curInfo, this);

		} else if(cfgInfo.osInUse.toLowerCase().equals(ConfigInfo.WINDOWS_OS)) {
			pktListener = new PacketListenerWindows(cfgInfo, curInfo, this);

		} else if(cfgInfo.osInUse.toLowerCase().equals(ConfigInfo.ZAURUS_OS)) {
			pktListener = new PacketListenerZaurus(cfgInfo, curInfo, this);

		} else {
			pktListener = null;
		}

		// create hello minder
		helloMinder = new HelloMinder(cfgInfo, curInfo, this);

		// create RREQ ID minder
		idMinder = new RREQIDMinder(cfgInfo, curInfo, this);

	}

	/**
	* Method to start the Protocol Handler. Starting
	* is done by doing the following,
	*
	*  - init the routing environment
	*  - creates & starts the packet listener
	*  - creates & starts the AODV msg listener
	*  - creates & starts the hello minder
	*  - creates and starts the RREQ ID minder
	*/
	public synchronized boolean startApplication() {

		try {
			if(rtMgrActive)
				return true;

			rtMgrActive = true;

			// start activity logging
			curInfo.log.start();

			// start packet sender
			pktSender.start();

			// initialize the routing environment
			osOps.initializeRouteEnvironment(0);

			// activate the route list
			rtList.start(osOps);

			// start packet listener thread
			pktListener.start();

			// start hello sending thread
			helloMinder.start();

			// start RREQ ID tracking thread
			idMinder.start();

			// log
			curInfo.log.write(Logging.ACTIVITY_LOGGING,
				"Route Manager - Protocol Handler started");


		} catch (Exception e) {
			// log
			curInfo.log.write(Logging.CRITICAL_LOGGING,
				"Route Manager - Problem in start - " + e);
			// show msg box
			jadhoc.displayError(e.toString());

			return false;
		}
		return true;
	}

	/**
	* Method to stop the protocol handler. Does this
	* by stoping all the threads that were started
	* start function and cleaning up the routing
	* environment
	*/
	public synchronized boolean stopApplication() {

		try {
			if(!rtMgrActive)
				return true;

			rtMgrActive = false;

			// terminate all other threads

			// inform other nodes using precursor list

			// deactivate the route list
			rtList.stop();

			// terminate the routing environment
			osOps.finalizeRouteEnvironment();

			// stop activity logging
			curInfo.log.stop();

		} catch (Exception e) {
			// log
			curInfo.log.write(Logging.CRITICAL_LOGGING,
				"Route Manager - Problem in stop - " + e);

			// show msg box
			jadhoc.displayError(e.toString());

			return false;
		}
		return true;
	}

	/**
	* This method returns the current state of the route
	* manager. If the route manager has been stopped, this
	* will return false, else true.
	*
	* @return boolean - route manager state
	*/
	public synchronized boolean isRouteMgrActive() {
		return rtMgrActive;
	}

	/**
	* Method to check whether there exist atleast one
	* unexpired route. This is required by the Hello
	* message sender to determine whether to send Hello
	* messages or not. This method passes control to
	* the same named method in the route list.
	* @return boolean - true if atleast one unexpired
	*			route else returns false
	*/
	public synchronized boolean doUnexpiredRoutesExist() {
		return rtList.doUnexpiredRoutesExist();
	}

	/*--------------------------------------------------------------------------------------*/

	// Route Minder interface
	// ----------------------------------------

		// if thread = route minder AND route status = valid
			// delete route from os
			// route expiry staus = true
			// active minder = null (i.e. stop route minder)
			// if hop count not= 1 {
			//	route status = invalid
			//	set lifetime to DELETE_PERIOD
			//	dest seq in route incremented by 1
			//	start route deleter
			// }
			//
	public synchronized int checkActiveRouteLifetime(InetAddress da,
						RouteMinder rtMinder) throws Exception {
		RouteEntry destRte;
		int lifetime;
		long currTime;

		destRte = rtList.get(da);
		if(destRte == null)
			return 0;


		// if for some reason the rtMinder is not the current thread that
		// is managing the route, then stop the thread
		if(destRte.activeMinder != rtMinder) {
			return 0;
		}

		// if for some reason the route has become invalid, then stop the thread
		if(destRte.routeStatusFlag != RouteEntry.ROUTE_STATUS_FLAG_VALID) {
			return 0;
		}

		currTime = (new Date()).getTime();
		lifetime = (int) (destRte.expiryTime - currTime);
		if(lifetime <= 0) {

			// simply expire route (also start route
			// delete)

			destRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_INVALID;
			destRte.destSeqNum++;
			destRte.expiryTime = currTime + cfgInfo.deletePeriodVal;
			destRte.activeMinder = new DeleteMinder(cfgInfo, curInfo, this,
								destRte.destIPAddr, cfgInfo.deletePeriodVal);
			destRte.activeMinder.start();

			rtList.update(da, destRte);

			return 0;

		} else
			return lifetime;
	}

	/*--------------------------------------------------------------------------------------*/

	// Hello Receipt Minder interface
	// ----------------------------------------
			// if hello not heard {
			//	send RERRs to all dependents -
			//			find dependents on this route (i.e. as next hop)
			//			remove route from os
			//			send RERR to all
			//			route status of all = invalid
			//			set lifetime to DELETE_PERIOD
			//			start route deleters for all
			//
			// }

	/**
	* Method called by the HelloReceiptMinder thread to check whether a route is
	* expired due to not receiving hello messages.
	*
	* @param InetAddress da - the destination for which this minder is active
	* @param HelloReceiptMinder hrMinder - the minder that calls this method
	* @exception Exception - thrown in case of errors
	* @return int - returns the lifetime
	*/
	public synchronized int checkHelloReceived(InetAddress da, HelloReceiptMinder hrMinder)
								throws Exception {
		RouteEntry destRte;
		long currTime;

		destRte = rtList.get(da);
		if(destRte == null)
			return 0;

		if(destRte.helloReceiptMinder != hrMinder)
			return 0;

		if(destRte.hopCount > 1)
			return 0;

		currTime = (new Date()).getTime();

		// if no hellos heard but route is active, then generate RERRs
		// to all precursors and expire all the destinations which are
		// reachable thru this route and also expire himself
		if(currTime > destRte.nextHelloReceiveTime
			&& destRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

			invalidateDestinations(destRte);

			// remove the route
			destRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_INVALID;
			destRte.destSeqNum++;
			destRte.expiryTime = currTime + cfgInfo.deletePeriodVal;
			destRte.activeMinder = new DeleteMinder(cfgInfo, curInfo, this,
								destRte.destIPAddr, cfgInfo.deletePeriodVal);
			destRte.activeMinder.start();

			rtList.update(da, destRte);

			return 0;
		}

		return (int) (destRte.nextHelloReceiveTime - currTime);
	}

	/**
	* Method to send RERRs dependent on a route that has expired and no hellos
	* have been heard.
	*
	* @RouteEntry destRte - the destination that is expiring
	* @return Exception - any errors
	*
	*/
	private synchronized void invalidateDestinations(RouteEntry destRte) throws Exception {
		ArrayList unreachableIPList;
		ArrayList unreachableSeqList;
		RouteEntry entry;
		Object array[];
		int i, j;
		InetAddress adrList[];
		int seqList[];
		RERR rerr;

		array = rtList.getRouteArray();

		// if RERR unicast, send a RERR to each precursor in a
		// invalidating route
		if(cfgInfo.RERRSendingModeVal == ConfigInfo.RERR_UNICAST_VAL) {
			adrList = new InetAddress[1];
			seqList = new int[1];

			for(i = 0; i < array.length; i++) {
				entry = (RouteEntry) array[i];

				// send RERR only if the 'link break' (destRte) route
				// was the next hop
				if(!entry.destIPAddr.equals(destRte.destIPAddr)
				     && entry.nextHopIPAddr.equals(destRte.destIPAddr)) {
					adrList[0] = entry.destIPAddr;
					seqList[0] = entry.destSeqNum;

					if(entry.validDestSeqNumFlag == RouteEntry.DEST_SEQ_FLAG_VALID) {
						entry.destSeqNum++;
					}

					// send RERR to each precursor
					for(j = 0; j < entry.precursorList.size(); j++) {
						rerr = new RERR(cfgInfo, curInfo, false,
							(InetAddress) entry.precursorList.get(j), (short) 1,
							false, (byte) 1, adrList, seqList);
						pktSender.sendMessage(rerr);
					}

					// invalidate route & start route delete
					entry.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_INVALID;
					entry.expiryTime = (new Date()).getTime()
								    + cfgInfo.deletePeriodVal;
					entry.activeMinder = new DeleteMinder(cfgInfo, curInfo,	this,
								entry.destIPAddr, cfgInfo.deletePeriodVal);
					entry.activeMinder.start();

					rtList.update(entry.destIPAddr, entry);
				}
			}

			// send RERR to the precursors of the link broken route
			adrList[0] = destRte.destIPAddr;
			seqList[0] = destRte.destSeqNum;
			// send RERR to each precursor
			for(j = 0; j < destRte.precursorList.size(); j++) {
				rerr = new RERR(cfgInfo, curInfo, false,
					(InetAddress) destRte.precursorList.get(j), (short) 1,
					false, (byte) 1, adrList, seqList);
				pktSender.sendMessage(rerr);
			}


		// if RERR multicast, send one RERR with all the invalidating
		// destinations
		} else {
			unreachableIPList = new ArrayList();
			unreachableSeqList = new ArrayList();

			// collect all the destinations that become
			// invalid & start route delete
			for(i = 0; i < array.length; i++) {
				entry = (RouteEntry) array[i];

				// collect dest only if the 'link break' (destRte) route
				// was the next hop

				if(!entry.destIPAddr.equals(destRte.destIPAddr)
				   && entry.nextHopIPAddr.equals(destRte.destIPAddr)) {
					unreachableIPList.add(entry.destIPAddr);
					unreachableSeqList.add(new Integer(entry.destSeqNum));

					// invalidate route & start route delete
					entry.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_INVALID;
					entry.expiryTime = (new Date()).getTime()
								    + cfgInfo.deletePeriodVal;
					entry.activeMinder = new DeleteMinder(cfgInfo, curInfo, this,
								entry.destIPAddr, cfgInfo.deletePeriodVal);
					entry.activeMinder.start();

					rtList.update(entry.destIPAddr, entry);
				}
			}

			// add the link broken IP address
			unreachableIPList.add(destRte.destIPAddr);
			unreachableSeqList.add(new Integer(destRte.destSeqNum));

			if(unreachableIPList.size() > 0) {
				adrList = (InetAddress []) unreachableIPList.toArray();
				seqList = new int[unreachableSeqList.size()];
				for(i = 0; i < seqList.length; i++) {
					seqList[i] = ((Integer) unreachableSeqList.get(i)).intValue();
				}
				rerr = new RERR(cfgInfo, curInfo, true,
						cfgInfo.ipAddressMulticastVal, (short) 1,
						false, (byte) unreachableIPList.size(),
						adrList, seqList);
				pktSender.sendMessage(rerr);
			}
		}
	}



	/*--------------------------------------------------------------------------------------*/

	// Route deleter interface
	// -----------------------



		// if thread = route deleter AND route status = invalid
			// delete route from internal table
			// active minder = null;
	public synchronized int checkDeleteRouteLifetime(InetAddress da,
					DeleteMinder delMinder) throws Exception {
		RouteEntry destRte;
		int lifetime;

		destRte = rtList.get(da);
		if(destRte == null)
			return 0;

		// if for some reason the route has got some status other than invalid
		// OR delMinder is not the current thread that is managing the route,
		// then stop the thread
		if((destRte.routeStatusFlag != RouteEntry.ROUTE_STATUS_FLAG_INVALID)
		    || (destRte.activeMinder != delMinder)) {
			return 0;
		}

		lifetime = (int) (destRte.expiryTime - (new Date()).getTime());
		if(lifetime <= 0) {

			// if lifetime expired, delete the route from list
			rtList.remove(da);

			return 0;

		} else
			return lifetime;
	}




	/*--------------------------------------------------------------------------------------*/

	// RREQID minder interface
	// -----------------------

	public synchronized RREQIDEntry getFirstRREQID() {
		return idList.getFirst();
	}

	public synchronized RREQIDEntry removeRREQID(InetAddress adr, int id) {
		return idList.remove(adr, id);
	}


	/*--------------------------------------------------------------------------------------*/

	// Hello minder interface
	// ----------------------

	/**
	* Method called by the hello minder to send a hello message. Sends
	* it only if there are unexpired routes.
	*
	* @param HELLO hm - the hello message to send
	*/
	public synchronized boolean sendHello(HELLO hm) throws Exception {

		// check if route manager is active
		if(!rtMgrActive) {
			return false;
		}

		// send hello only if any active unexpired routes exist
		if(doUnexpiredRoutesExist()) {
			pktSender.sendMessage(hm);
		}

		return true;
	}

	/*--------------------------------------------------------------------------------------*/

	/**
	* This method handles a RREQ messge received by the protocol handle. In
	* summary, either it will send a RREP or propogate the RREQ. Following
	* text describes the
	*
	*	create or update a route to the prev hop increase lifetime
	*		by ACTIVE_ROUTE_TIMEOUT (without a valid seq num, i.e. validDestSeqNumFlag = invalid
	* 	check RREQ prevously recvd (check from RREQ ID + orig ip list), if so drop
	*
	*	in RREQ increment hop count
	*	serach a route to the originator of RREQ, then add or update route
	*			(use orig seq num in RREQ to update, see FORMULA
	*			 set validDestSeqNumFlag = valid)
	*
	*	route lifetime to originator should be updated using FORMULA
	*
	*	check if i am the destnation, then RREP generated
	*	check if route to dest available AND active AND D flag is not set AND my dest seq num is valid
	*				AND my dest seq num >= to dest seq num in RREQ, then RREP generated
	*	if RREP generated
	*		if destination
	*			seq num FORMULA
	*			hop count = 0, lifetime = MY_ROUTE_TIME, prefix = 0, R flag 0,
	*			A flag from parametrs, rest from RREQ
	*			unicast send, to sender of RREQ (prev hop)
	*
	*		if not destination (intermediate)
	*			seq num = what is route entry
	*			hop count = what is route entry
	*			lifetime = what is route entry (route time - curr time)
	*			rest from RREQ
	*			unicast send, to sender of RREQ (prev hop)
	*			if G flag set in RREP (send RREP to destination)
	*				hop count = from route to originator
	*				dest ip adddress = originate of RREQ
	*				dest seq num = originator seq num of RREQ
	*				originator ip address = dest ip address
	*				lifetime = (route time - curr time) to originator
	*				unicast send to next hop to destination (from route)
	*
	*	if no RREP generated then
	*		check the TTL, should be > 1 else drop packet
	*		reduce TTL by 1
	*		place highest of dest seq considering RREQ and route to dest in my route list
	*		put RREQ ID + originator ip in list for RREQ minder with PATH_DISCOVERY_TIME
	*		propogate RREQ
	*
	* @param RREQ rreq - the RREQ to process
	* @exception Exception - exceptions thrown when error
	*/
	public synchronized void processAODVMsgRREQ(RREQ rreq) throws Exception {
		RouteEntry prevHopRte, origRte, destRte;
		boolean mf, rf, af;
		InetAddress sendto;
		short ttl;
		byte ps, hc;
		InetAddress da, oa;
		int lt, dsn;
		boolean jf, gf, df, usnf;
		int ri, osn;
		boolean generateRREP;
		long minimalLifetime;
		RREP rrep;
		RREQ newRREQ;


		// log
		curInfo.log.write(Logging.INFO_LOGGING,
				"Route Manager - RREQ Received "+  rreq.toString());

		// create or update a route to the prev hop increase lifetime
		//		by ACTIVE_ROUTE_TIMEOUT (without a valid seq num, i.e. validDestSeqNumFlag = invalid
		prevHopRte = rtList.get(rreq.fromIPAddr);

		// if no route entry available
		if(prevHopRte == null) {

			// create entry
			prevHopRte = new RouteEntry(cfgInfo, curInfo);

			prevHopRte.destIPAddr = rreq.fromIPAddr;
			prevHopRte.destSeqNum = 0;
			prevHopRte.validDestSeqNumFlag = RouteEntry.DEST_SEQ_FLAG_INVALID;
			prevHopRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			prevHopRte.ifaceName = rreq.ifaceName ;
			prevHopRte.hopCount = 1;
			prevHopRte.nextHopIPAddr = rreq.fromIPAddr;
			prevHopRte.precursorList = new ArrayList();
			prevHopRte.expiryTime = (new Date()).getTime() + cfgInfo.activeRouteTimeoutVal;
			prevHopRte.activeMinder = new RouteMinder(cfgInfo, curInfo, this, rreq.fromIPAddr,
							cfgInfo.activeRouteTimeoutVal);
			prevHopRte.activeMinder.start();

		// if available and not expired
		} else if(prevHopRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

			  // route is active, only extend lifetime
			prevHopRte.expiryTime = (new Date()).getTime() + cfgInfo.activeRouteTimeoutVal;

		// if available but expired
		} else {

			// set kernel route, start the minder and extend lifetime
			prevHopRte.hopCount = 1;
			prevHopRte.nextHopIPAddr = rreq.fromIPAddr;

			prevHopRte.expiryTime = (new Date()).getTime() + cfgInfo.activeRouteTimeoutVal;
			prevHopRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			prevHopRte.activeMinder = new RouteMinder(cfgInfo, curInfo, this, rreq.fromIPAddr,
							cfgInfo.activeRouteTimeoutVal);
			prevHopRte.activeMinder.start();
		}

		rtList.update(rreq.fromIPAddr, prevHopRte);


		// check RREQ prevously recvd (check from RREQ ID + orig IP in list), if so drop
		if(idList.exist(rreq.origIPAddr, rreq.RREQID)) {

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
					"Route Manager - RREQ disregarded as previously processed");

			return;
		}

		// in RREQ, increment hop count
		rreq.hopCount++;


		// serach a route to the originator of RREQ, then add or update route
		//			(use orig seq num in RREQ to update, see FORMULA
		//			 set validDestSeqNumFlag = valid)
		origRte = rtList.get(rreq.origIPAddr);

		// if route not available
	   	if(origRte == null) {
			origRte = new RouteEntry(cfgInfo, curInfo);
			origRte.destIPAddr = rreq.origIPAddr;
			origRte.destSeqNum = rreq.origSeqNum;
			origRte.validDestSeqNumFlag = RouteEntry.DEST_SEQ_FLAG_VALID;
			origRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			origRte.ifaceName = rreq.ifaceName ;
			origRte.hopCount = (byte) rreq.hopCount;
			origRte.nextHopIPAddr = rreq.fromIPAddr;
			origRte.precursorList = new ArrayList();
			origRte.expiryTime = (new Date()).getTime();

		// if available and not expired
		} else if(origRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

			  // only lifetime need extended, done later

		// if available but expired
		} else {

			// create whole route
			origRte.destSeqNum = rreq.origSeqNum;
			origRte.validDestSeqNumFlag = RouteEntry.DEST_SEQ_FLAG_VALID;
			origRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			origRte.ifaceName = rreq.ifaceName ;
			origRte.hopCount = (byte) rreq.hopCount;
			origRte.nextHopIPAddr = rreq.fromIPAddr;
			origRte.precursorList = new ArrayList();
			origRte.expiryTime = (new Date()).getTime();
		}


		// update lifetime
		// route lifetime to originator should be updated using FORMULA
		//	maximum of (ExistingLifetime, MinimalLifetime)
		//   MinimalLifetime = (current time + 2*NET_TRAVERSAL_TIME -
                //                                    2*HopCount*NODE_TRAVERSAL_TIME).
		minimalLifetime = (2 * cfgInfo.netTraversalTimeVal)
				   - (2 * origRte.hopCount * cfgInfo.nodeTraversalTimeVal)
				   + (new Date()).getTime();
		if(minimalLifetime > origRte.expiryTime)
			origRte.expiryTime = minimalLifetime;

		origRte.activeMinder = new RouteMinder(cfgInfo, curInfo, this, rreq.origIPAddr,
						(int) (origRte.expiryTime - (new Date()).getTime()));
		origRte.activeMinder.start();

		rtList.update(rreq.origIPAddr, origRte);

		// check if i am the destnation, then RREP generated
		if(cfgInfo.ipAddressVal.equals(rreq.destIPAddr)) {
			generateRREP = true;
			destRte = null;

		// check if route to dest available AND active AND D flag is not set AND my dest seq num is valid
		// 			AND my dest seq num >= to dest seq num in RREQ, then RREP generated
		} else {
			destRte = rtList.get(rreq.destIPAddr);
			if(destRte != null
			    && (destRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID)
			    && !rreq.destOnlyFlag
			    && destRte.validDestSeqNumFlag == RouteEntry.DEST_SEQ_FLAG_VALID
			    && (curInfo.destSeqCompare(destRte.destSeqNum, rreq.destSeqNum) == curInfo.GREATER
			        || curInfo.destSeqCompare(destRte.destSeqNum, rreq.destSeqNum) == curInfo.EQUAL)) {
				generateRREP = true;

			// if none of above, propogate the RREQ
			} else {
				generateRREP = false;
				destRte = null;
			}
		}

		// if RREP generated
		if(generateRREP) {
			// if i am destination
			if(cfgInfo.ipAddressVal.equals(rreq.destIPAddr)) {
				// seq num see FORMULA
				// hop count = 0, lifetime = MY_ROUTE_TIME, prefix = 0, R flag 0,
				// A flag from parametrs, rest from RREQ
				// unicast send, to sender of RREQ (prev hop)

				mf = false;
				sendto = prevHopRte.destIPAddr;
				ttl = 255;
				rf = false;
				af = cfgInfo.RREPAckRequiredVal;
				ps = 0;
				hc = 0;
				da = cfgInfo.ipAddressVal;
				//rreq.destSeqNum++;

				if(curInfo.destSeqCompare(rreq.destSeqNum, curInfo.lastSeqNum) == curInfo.GREATER) {
					curInfo.lastSeqNum = rreq.destSeqNum;
				} else if(curInfo.destSeqCompare(rreq.destSeqNum, curInfo.lastSeqNum) == curInfo.EQUAL) {
					curInfo.incrementOwnSeqNum();
				} else {
					// use existing value
				}
				dsn = curInfo.lastSeqNum;
				oa = origRte.destIPAddr;
				lt = cfgInfo.myRouteTimeoutVal;

				rrep = new RREP(cfgInfo, curInfo, mf, sendto, ttl, rf,
						af, ps, hc, da, dsn, oa, lt);
				pktSender.sendMessage(rrep);


			// if not destination (intermediate node)
			} else {

				// seq num = what is route entry
				// hop count = what is route entry
				// lifetime = what is route entry (route time - curr time)
				// rest from RREQ
				// unicast send, to sender of RREQ (prev hop)
				mf = false;
				sendto = prevHopRte.destIPAddr;
				ttl = 255;
				rf = false;
				af = cfgInfo.RREPAckRequiredVal;
				ps = 0;
				hc = (byte) destRte.hopCount;
				da = destRte.destIPAddr;
				dsn = destRte.destSeqNum;
				oa = rreq.origIPAddr;
				lt = (int) (destRte.expiryTime - (new Date()).getTime());

				rrep = new RREP(cfgInfo, curInfo, mf, sendto, ttl, rf,
						af, ps, hc, da, dsn, oa, lt);
				pktSender.sendMessage(rrep);

				// if G flag set in RREQ (send RREP to destination)
				if(rreq.gratRREPFlag) {
					// hop count = from route to originator
					// dest ip adddress = originate of RREQ
					// dest seq num = originator seq num of RREQ
					// originator ip address = dest ip address
					// lifetime = (route time - curr time) to originator
					// unicast send to next hop to destination (from route)

					mf = false;
					sendto = destRte.nextHopIPAddr;
					ttl = 225;
					rf = false;
					af = cfgInfo.RREPAckRequiredVal;
					ps = 0;
					hc = (byte) origRte.hopCount;
					da = origRte.destIPAddr;
					dsn = origRte.destSeqNum;
					oa = destRte.destIPAddr;
					lt = (int) (origRte.expiryTime - (new Date()).getTime());

					rrep = new RREP(cfgInfo, curInfo, mf, sendto, ttl, rf,
						af, ps, hc, da, dsn, oa, lt);
					pktSender.sendMessage(rrep);

				}
			}

		// if no RREP generated, then propogate RREQ
		} else {
			// check the TTL, should be > 1 else drop packet
			if(rreq.ttlValue > 1) {
				// reduce TTL by 1
				// place highest of dest seq considering RREQ and route to dest in my route list
				// put RREQ ID + originator ip in list for RREQ minder with PATH_DISCOVERY_TIME
				// propogate RREQ
				rreq.ttlValue--;
				if(destRte != null) {
					if(!rreq.unknownSeqNumFlag
					    && curInfo.destSeqCompare(rreq.destSeqNum, destRte.destSeqNum)
					    				== CurrentInfo.GREATER) {

						destRte.destSeqNum = rreq.destSeqNum;
						rtList.update(rreq.destIPAddr, destRte);
						rreq.unknownSeqNumFlag = false;
					} else {
						rreq.destSeqNum = destRte.destSeqNum;
					}
				}

				sendto = cfgInfo.ipAddressMulticastVal;
				ttl = rreq.ttlValue;
				jf = rreq.joinFlag;
				rf = rreq.repairFlag;
				gf = rreq.gratRREPFlag;
				df = rreq.destOnlyFlag;
				usnf = rreq.unknownSeqNumFlag;
				hc = rreq.hopCount;
				ri = rreq.RREQID;
				da = rreq.destIPAddr;
				dsn = rreq.destSeqNum;
				oa = rreq.origIPAddr;
				osn = rreq.origSeqNum;
				newRREQ = new RREQ(cfgInfo, curInfo, true, sendto, ttl, jf, rf, gf, df, usnf, hc,
						ri, da, dsn, oa, osn);
				idList.add(oa, ri);
				pktSender.sendMessage(newRREQ);

			}
		}
	}

	/**
	* This method is responsible for handling RREP messages recived by the
	* node. The following is the procedure,
	*
	*		find route to prev hop (who sent RREP, i.e dest=prev hop)
	*		if not, create route without a valid seq num
	*		increment hop count in RREP
	*
	*		find route to dest
	*		if route found, compare dest seq num
	*			if seq num invalid in route
	*			   OR (dest seq in RREP > what is in route (2s comp) AND dest seq valid)
	*			   OR (seq == seq AND route is inactive route)
	*			   OR (seq num == seq num AND active route AND hop count in RREP is < hop count in route)
	*				update route
	*					do as (100)
	*		if route not found
	*			(100) create route - route flag = active, dest seq flag = valid,
	*				 next hop = src ip in RREP, hop count = hop count in RREP
	*				 expiry time = current time + lifetime in RREP
	*				 dest seq num = dest seq num of RREP
	*				 dest ip = dest ip in RREP
	*				 iface = iface from which RREP recvd
	*
	*
	*		if i am not originator of RREP
	*			find route to originator
	*			update lifetime of route to max of (existing lifetime, currtime + ACTIVE_ROUTE_TIMEOUT)
	*			update precursor list - using from the src ip from whom the RREP was
	*											recvd (i.e. next hop)
	*
	*			find route to dest
	*			update precuror list to dest (i.e. next hop to dest) - put ip of next hop to which RREP is
	*					 forwarded (not	ncessarily originator)
	*			send RREP to next hop to originator
	*
	*		if i am originator
	*			unicast RREP-ACK to dest
	*
	* @param RREP rrep - the RREP recived
	* @exception Exception - thrown for any error occured
	*/
	public synchronized void processAODVMsgRREP(RREP rrep) throws Exception {
		RouteEntry prevHopRte, destRte, origRte;
		boolean	mf, rf, af;
		InetAddress sendto;
		short ttl;
		byte ps, hc;
		InetAddress da, oa;
		int lt, dsn;
		long activeRouteExpiryTime;
		RREP newRREP;
		RouteDiscoveryEntry rde;

		// log
		curInfo.log.write(Logging.INFO_LOGGING,
				"Route Manager - RREP Received "+  rrep.toString());

		// find route to prev hop (who sent RREP, i.e dest=prev hop)
		// if not, create route without a valid seq num
		prevHopRte = rtList.get(rrep.fromIPAddr);

		// if no route entry available
		if(prevHopRte == null) {

			// create entry
			prevHopRte = new RouteEntry(cfgInfo, curInfo);

			prevHopRte.destIPAddr = rrep.fromIPAddr;
			prevHopRte.destSeqNum = 0;
			prevHopRte.validDestSeqNumFlag = RouteEntry.DEST_SEQ_FLAG_INVALID;
			prevHopRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			prevHopRte.ifaceName = rrep.ifaceName ;
			prevHopRte.hopCount = 1;
			prevHopRte.nextHopIPAddr = rrep.fromIPAddr;
			prevHopRte.precursorList = new ArrayList();
			prevHopRte.expiryTime = (new Date()).getTime() + cfgInfo.activeRouteTimeoutVal;
			prevHopRte.activeMinder = new RouteMinder(cfgInfo, curInfo, this, rrep.fromIPAddr,
							cfgInfo.activeRouteTimeoutVal);
			prevHopRte.activeMinder.start();

		// if available and not expired
		} else if(prevHopRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

			  // route is active, only extend lifetime
			prevHopRte.expiryTime = (new Date()).getTime() + cfgInfo.activeRouteTimeoutVal;

		// if available but expired
		} else {

			// set kernel route, start the minder and extend lifetime
			prevHopRte.hopCount = 1;
			prevHopRte.nextHopIPAddr = rrep.fromIPAddr;

			prevHopRte.expiryTime = (new Date()).getTime() + cfgInfo.activeRouteTimeoutVal;
			prevHopRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			prevHopRte.activeMinder = new RouteMinder(cfgInfo, curInfo, this, rrep.fromIPAddr,
							cfgInfo.activeRouteTimeoutVal);
			prevHopRte.activeMinder.start();
		}

		rtList.update(rrep.fromIPAddr, prevHopRte);


		// increment hop count in RREP
		rrep.hopCount++;

		// find route to dest
		destRte = rtList.get(rrep.destIPAddr);


		// if route found ( compare dest seq num)
			// AND (seq num invalid in route
			//    OR (dest seq in RREP > what is in route (2s comp) AND dest seq valid)
			//    OR (seq == seq AND route is inactive route)
			//    OR (seq num == seq num AND active route AND hop count in RREP is < hop count in route))
				// update route
		if(destRte != null
		    && (destRte.validDestSeqNumFlag == RouteEntry.DEST_SEQ_FLAG_INVALID
			|| (curInfo.destSeqCompare(rrep.destSeqNum, destRte.destSeqNum) == curInfo.GREATER
				&& destRte.validDestSeqNumFlag == RouteEntry.DEST_SEQ_FLAG_VALID)
			|| (curInfo.destSeqCompare(rrep.destSeqNum, destRte.destSeqNum) == curInfo.EQUAL
				&& destRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_INVALID)
			|| (curInfo.destSeqCompare(rrep.destSeqNum, destRte.destSeqNum) == curInfo.EQUAL
				&& destRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID
				&& rrep.hopCount < destRte.hopCount))) {

			destRte.destIPAddr = rrep.destIPAddr;
			destRte.destSeqNum = rrep.destSeqNum;
			destRte.validDestSeqNumFlag = RouteEntry.DEST_SEQ_FLAG_VALID;
			destRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			destRte.ifaceName = rrep.ifaceName ;
			destRte.hopCount = rrep.hopCount;
			destRte.nextHopIPAddr = rrep.fromIPAddr;
			destRte.expiryTime = (new Date()).getTime() + rrep.lifeTime;
			destRte.activeMinder = new RouteMinder(cfgInfo, curInfo, this, rrep.destIPAddr,
							rrep.lifeTime);
			destRte.activeMinder.start();
			rtList.update(rrep.destIPAddr, destRte);
			// log

		// if route not found
		} else if(destRte == null) {
			// (100) create route - route flag = active, dest seq flag = valid,
			// 	 next hop = src ip in RREP, hop count = hop count in RREP
			// 	 expiry time = current time + lifetime in RREP
			// 	 dest seq num = dest seq num of RREP
			// 	 dest ip = dest ip in RREP
			// 	 iface = iface from which RREP recvd
			destRte = new RouteEntry(cfgInfo, curInfo);

			destRte.destIPAddr = rrep.destIPAddr;
			destRte.destSeqNum = rrep.destSeqNum;
			destRte.validDestSeqNumFlag = RouteEntry.DEST_SEQ_FLAG_VALID;
			destRte.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_VALID;
			destRte.ifaceName = rrep.ifaceName ;
			destRte.hopCount = rrep.hopCount;
			destRte.nextHopIPAddr = rrep.fromIPAddr;
			destRte.precursorList = new ArrayList();
			destRte.expiryTime = (new Date()).getTime() + rrep.lifeTime;
			destRte.activeMinder = new RouteMinder(cfgInfo, curInfo, this, rrep.destIPAddr,
							rrep.lifeTime);
			destRte.activeMinder.start();
			rtList.update(rrep.destIPAddr, destRte);
		} else {
			destRte.expiryTime = (new Date()).getTime() + rrep.lifeTime;
			rtList.update(rrep.destIPAddr, destRte);
		}


		// if i am not originator of RREP
		if(!(cfgInfo.ipAddressVal.equals(rrep.origIPAddr))) {
			//find route to originator
			origRte = rtList.get(rrep.origIPAddr);

			if(origRte == null) {
				// somethin wrong
				//log
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Route Manager - No originator route entry found ; try extending lifetime ");

				return;
			} else {
				// update lifetime of route to max of (existing lifetime, currtime + ACTIVE_ROUTE_TIMEOUT)
				// update precursor list - using from the src ip from whom the RREP was
				// 							recvd (i.e. next hop)
				activeRouteExpiryTime = cfgInfo.activeRouteTimeoutVal + (new Date()).getTime();

				if(activeRouteExpiryTime > origRte.expiryTime) {
					origRte.expiryTime = activeRouteExpiryTime;
				}

				origRte.precursorList.add(rrep.fromIPAddr);

				rtList.update(rrep.origIPAddr, origRte);

				// find route to dest
				// update precuror list to dest (i.e. next hop to dest) - put ip of
				//  next hop to which RREP is forwarded (not necessarily originator)
				destRte.precursorList.add(origRte.nextHopIPAddr);
				rtList.update(rrep.destIPAddr, destRte);


				// send RREP to next hop to originator

				mf = false;
				sendto = origRte.nextHopIPAddr;
				ttl = 225;
				rf = rrep.repairFlag;
				af = rrep.ackFlag;
				ps = rrep.prefixSize;
				hc = rrep.hopCount;
				da = rrep.destIPAddr;
				dsn = rrep.destSeqNum;
				oa = rrep.origIPAddr;
				lt = rrep.lifeTime;

				newRREP = new RREP(cfgInfo, curInfo, mf, sendto, ttl, rf,
						af, ps, hc, da, dsn, oa, lt);
				pktSender.sendMessage(newRREP);

			}

		// if i am originator
		} else {

			//	unicast RREP-ACK to dest
		}

		// due to this RREP, if a route was made for a route being
		// discovered, start the BufferMinder to release the packets
		// at a given time after the route is made
		// if buffering is not set, simply delete the route discovery
		// entry
		rde = rdList.get(rrep.destIPAddr);
		if(rde != null) {

			if(cfgInfo.packetBufferingVal) {
				(new BufferMinder(cfgInfo, curInfo, this,
							rrep.destIPAddr)).start();
			} else {
				rdList.remove(rrep.destIPAddr);
			}

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Route Manager - Route discovery terminated as route made to "
				+ rrep.destIPAddr.getHostAddress());
		}
	}

	/**
		// RERR
			// compile list of routes to (unrechable dest in RERR
			//	AND that have the sender of RERR as next hop)
			//
			// send RERR to all precursors of the above list
			//			{ copy dest seq from RERR
			//			  set route status = INVALID
			//			  set lifetime to DELETE_PERIOD
			//			  start route deleters for all }
			//

	*/
	public synchronized void processAODVMsgRERR(RERR rerr) throws Exception {
		ArrayList unreachableIPList;
		ArrayList unreachableSeqList;
		RouteEntry entry;
		int i, j;
		InetAddress adrList[];
		int seqList[];
		RERR newRERR;

		// if RERR unicast, send a RERR to each precursor in a
		// invalidating route
		if(cfgInfo.RERRSendingModeVal == ConfigInfo.RERR_UNICAST_VAL) {

			adrList = new InetAddress[1];
			seqList = new int[1];

			for(i = 0; i < rerr.destCount; i++) {
				entry = rtList.get(rerr.destIPAddr[i]);

				// regenerate RERR only if the nexthop of this route is the
				// sender of the RERR but dont remove the route to the
				// sender of the RERR
				if(entry != null
				   && entry.nextHopIPAddr.equals(rerr.fromIPAddr)
				   && !entry.destIPAddr.equals(rerr.fromIPAddr) ) {

					entry.destSeqNum = rerr.destSeqNum[i];

					adrList[0] = entry.destIPAddr;
					seqList[0] = entry.destSeqNum;

					// regenerate RERR to each precursor
					for(j = 0; j < entry.precursorList.size(); j++) {
						newRERR = new RERR(cfgInfo, curInfo, false,
							(InetAddress) entry.precursorList.get(j), (short) 1,
							false, (byte) 1, adrList, seqList);
						pktSender.sendMessage(newRERR);
					}

					// invalidate route & start route delete
					entry.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_INVALID;
					entry.expiryTime = (new Date()).getTime()
								    + cfgInfo.deletePeriodVal;
					entry.activeMinder = new DeleteMinder(cfgInfo, curInfo, this,
								entry.destIPAddr, cfgInfo.deletePeriodVal);
					entry.activeMinder.start();

					rtList.update(entry.destIPAddr, entry);
				}
			}


		// if RERR multicast, regenerate one RERR with all the invalidating
		// destinations
		} else {
			unreachableIPList = new ArrayList();
			unreachableSeqList = new ArrayList();

			// collect all the destinations that become
			// invalid & start route delete
			for(i = 0; i < rerr.destCount; i++) {
				entry = rtList.get(rerr.destIPAddr[i]);

				// collect dest only if the 'link break' (destRte) route
				// was the next hop
				if(entry != null
				   && !entry.destIPAddr.equals(rerr.fromIPAddr)
				   && entry.nextHopIPAddr.equals(rerr.fromIPAddr)) {
					entry.destSeqNum = rerr.destSeqNum[i];

					unreachableIPList.add(entry.destIPAddr);
					unreachableSeqList.add(new Integer(entry.destSeqNum));

					// invalidate route & start route delete
					entry.routeStatusFlag = RouteEntry.ROUTE_STATUS_FLAG_INVALID;
					entry.expiryTime = (new Date()).getTime()
								    + cfgInfo.deletePeriodVal;
					entry.activeMinder = new DeleteMinder(cfgInfo, curInfo, this,
								entry.destIPAddr, cfgInfo.deletePeriodVal);
					entry.activeMinder.start();

					rtList.update(entry.destIPAddr, entry);
				}
			}
			if(unreachableIPList.size() > 0) {
				adrList = (InetAddress []) unreachableIPList.toArray();
				seqList = new int[unreachableSeqList.size()];
				for(i = 0; i < seqList.length; i++) {
					seqList[i] = ((Integer) unreachableSeqList.get(i)).intValue();
				}
				newRERR = new RERR(cfgInfo, curInfo, true,
						cfgInfo.ipAddressMulticastVal, (short) 1,
						false, (byte) unreachableIPList.size(),
						adrList, seqList);
				pktSender.sendMessage(newRERR);
			}
		}
	}


	public synchronized void processAODVMsgRREPACK(RREPACK rrepack) throws Exception {
		// RREP-ACK

		// not implemented

		return;
	}

	/**
	* Method to process HELLO messages received by this node.
	*
	* @param RREP rrep - HELLO message to process. A RREP becomes
	*			a HELLO message when it's
	*			DestIPAddr = OrigIPAddr and when it comes
	*			from a next hop
	* @exception Exception - thrown in case of errors
	*/
	public synchronized void processAODVMsgHELLO(RREP rrep) throws Exception {
		RouteEntry prevHopRte;

		// log
		curInfo.log.write(Logging.INFO_LOGGING,
				"Route Manager - HELLO Received "+  rrep.toString());

		// if no active routes exist, don't react to HELLOs
		if(!doUnexpiredRoutesExist()) {
			return;
		}

		// find route to prev hop (who sent RREP, i.e dest=prev hop)
		// if not, create route with a valid seq num
		prevHopRte = rtList.get(rrep.fromIPAddr);


		// if no route found, means no active
		// route for this destination, so dont do anything
		if(prevHopRte == null) {
			return;
		}

		prevHopRte.nextHelloReceiveTime = (new Date()).getTime() + rrep.lifeTime;
		prevHopRte.destSeqNum = rrep.destSeqNum;
		prevHopRte.validDestSeqNumFlag = RouteEntry.DEST_SEQ_FLAG_VALID;

		// start the thread, if not started already
		if(prevHopRte.helloReceiptMinder == null) {
			prevHopRte.helloReceiptMinder = new HelloReceiptMinder(cfgInfo,
								curInfo, this, rrep.fromIPAddr,
								rrep.lifeTime);
			prevHopRte.helloReceiptMinder.start();
		}
		rtList.update(rrep.fromIPAddr, prevHopRte);
	}

	/**
	* This method releases the packets in a buffer when given the
	* destination IP address. This method is called by the BufferMinder
	* that is responsible for releasing the buffer.
	*
	* @param InetAddress dest - Destination IP
	*/
	public synchronized void releaseBuffer(InetAddress dest) throws Exception {
		RouteDiscoveryEntry rde;
		IPPkt pkt;

		rde = rdList.get(dest);
		if(rde != null) {

			// send buffered packets only if parameter is set
			if(cfgInfo.packetBufferingVal) {
				while(rde.pktBuffer.size() > 0) {
					pkt = (IPPkt) rde.pktBuffer.remove(0);
					pktSender.sendPkt(pkt);
				}
			}
			rdList.remove(dest);
		}
	}

	/*--------------------------------------------------------------------------------------*/

	public synchronized void sendHelloMessage() throws Exception {
		// RREP - hello ????
			// find route to hello sender
			// if found
				// update
				//	lifetime increased to ALLOWED_HELLO_LOSS * HELLO_INTERVAL
				//	dest seq num = from hello

			// if not found
				// create
				//	get from Hello msg

	}



	/*--------------------------------------------------------------------------------------*/

	// After rebooting
	// ---------------



	/*--------------------------------------------------------------------------------------*/

	// Route discoverer interface
	//---------------------------

	/**
	* Method to be called when the local(my) machine requires a route
	* to some destination. This method is called by the packet listener
	* when it receives a packet with the given destination MAC address.
	*
	* The following text defines the procedure
	*	find route to dest in table
	*	if found( found means (route status = valid AND expired) OR (route status = invalid) )
	*		(200) dest seq = last know seq num
	*		increment own seq num
	*		orig seq num = own num
	*		increment RREQ ID
	*		RREQ ID of originator = RREQ ID
	*		hop count = 0
	*		G flag from parameters
	*		D flag from parameters
	*		set TTL to (hop count of route + TTL_INCREMENT)
	*
	*	if not found
	*		do as (200)
	*		U flag = true
	*		set TTL to TTL_START
	*
	*	put RREQ ID + originator ip in list for RREQ minder with PATH_DISCOVERY_TIME
	*	multicast RREQ
	*	start route discoverer with NET_TRAVERSAL_TIME and RREQ packet
	*
	* @param IPPkt pkt - the packet for which a route was required
	*/
	public synchronized void processRouteDiscovery(IPPkt pkt) {
		RouteEntry dest;
		RREQ rreq;
		InetAddress sendto, da, oa;
		short ttl;
		boolean jf, rf, gf, df, usnf;
		byte hc;
		int ri, dsn, osn;
		RouteDiscoveryEntry rde;
		DiscoveryMinder rdThread;
		int initialSleep;

		try {

			// if already a route is being discovered for the
			// given destination, then add the packet to the list;
			// don't do anything else
			rde = rdList.get(pkt.toIPAddr);
			if(rde != null) {

				// dont add if packet buffering is not
				// enabled
				if(cfgInfo.packetBufferingVal) {
					rde.pktBuffer.add(pkt);
				}
				return;
			}

			//find route to dest in table
			dest = rtList.get(pkt.toIPAddr);

			// if route made, this means that this is a
			// bufferred packet ; therefore, send the packet out
			// don't do anything else
			if(dest != null && dest.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

				// send buffered packets only if parameter is set
				// as this too is related to packet buffering
				if(cfgInfo.packetBufferingVal) {
					pktSender.sendPkt(pkt);
				}

				return;
			}


			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Route Manager - Route discovey started for "
						+ pkt.toIPAddr.getHostAddress());


			// generate RREQ

			//( found means (route status = invalid) )
			if(dest != null) {

				// multicast RREQ
				// set TTL to (hop count of route + TTL_INCREMENT)
				// join flag & repair flag is not set as simple dest search
				// G flag from parameters
				// D flag from parameters
				// dest seq num known
				// hop count = 0
				// increment RREQ ID, RREQ ID of originator = RREQ ID
				// destination adr from the IP packet
				// dest seq = last know seq num
				// has to be always my own IP adr
				// increment own seq num, orig seq num = own num

				sendto = cfgInfo.ipAddressMulticastVal;
				ttl = (short) (dest.hopCount + cfgInfo.TTLIncrementVal);
				jf = false;
				rf = false;
				gf = cfgInfo.gratuitousRREPVal;
				df = cfgInfo.onlyDestinationVal;
				usnf = false;
				hc = 0;
				ri = curInfo.incrementOwnRREQID();
				da = pkt.toIPAddr;
				dsn = dest.destSeqNum;
				oa = pkt.fromIPAddr;
				osn = curInfo.incrementOwnSeqNum();

			} else { //if not found

				// multicast RREQ
				// set TTL to TTL_START
				// join flag & repair flag is not set as simple dest search
				// G flag from parameters
				// D flag from parameters
				// since not in route, dest seq num not known
				// hop count = 0
				// increment RREQ ID, RREQ ID of originator = RREQ ID
				// destination adr from the IP packet
				// dest seq not known
				// has to be always my own IP adr
				// increment own seq num, orig seq num = own num
				sendto = cfgInfo.ipAddressMulticastVal;
				ttl = (short) cfgInfo.TTLStartVal;
				jf = false;
				rf = false;
				gf = cfgInfo.gratuitousRREPVal;
				df = cfgInfo.onlyDestinationVal;
				usnf = true;
				hc = 0;
				ri = curInfo.incrementOwnRREQID();
				da = pkt.toIPAddr;
				dsn = 0;
				oa = pkt.fromIPAddr;
				osn = curInfo.incrementOwnSeqNum();
			}


			rreq = new RREQ(cfgInfo, curInfo, true, sendto, ttl, jf, rf, gf, df, usnf, hc,
						ri, da, dsn, oa, osn);

			// put RREQ ID + originator ip in list for RREQ minder with PATH_DISCOVERY_TIME
			idList.add(oa, ri);

			// multicast the RREQ
			pktSender.sendMessage(rreq);

			// if route discovery is ERS
			if(cfgInfo.routeDiscoveryModeVal == ConfigInfo.ROUTE_DISCOVERY_ERS_VAL) {
				initialSleep = 2 * cfgInfo.nodeTraversalTimeVal
						* ( ttl + cfgInfo.timeoutBufferVal);

			// else assumes, route discovery is non-ERS
			} else {
				initialSleep = cfgInfo.netTraversalTimeVal;
			}


			// start route discoverer
			rdThread = new DiscoveryMinder(cfgInfo, curInfo, this, da, initialSleep);
			rdThread.start();

			// add the fist packet to the packet buffer (if enabled) and
			// update the route dioscovery list
			rde = new RouteDiscoveryEntry(cfgInfo, curInfo, da, rreq, rdThread, initialSleep);
			if(cfgInfo.packetBufferingVal) {
				rde.pktBuffer.add(pkt);
			}
			rdList.update(da, rde);

		} catch(Exception e) {

			// log
			curInfo.log.write(Logging.CRITICAL_LOGGING,
				"Route Manager - Route discovey failed " + e);
		}
	}

	/**
	* Method to be called to resend a RREQ after the specified duration. This method
	* is called by the Route Discovering thread.
	*
	* @param int retries - the number of RREQ retries done
	* @param InetAddress destIP - the destination for which route being searched
	* @return int - returns > 0 if the route discovery should continue (this being
	*		the next wait time, or else 0 to stop route discovery
	* @exception Exception - due to any error when calling other methods
	*/
	public synchronized int continueRouteDiscovery(InetAddress destIP) throws Exception {
		RouteEntry entry;
		RREQ newRREQ;
		RouteDiscoveryEntry rde;
		InetAddress sendto, da, oa;
		short ttl;
		boolean jf, rf, gf, df, usnf;
		byte hc;
		int ri, dsn, osn;

		entry = rtList.get(destIP);

		// if route made, stop discovery
		if(entry != null && entry.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {
			return 0;
		}

		// get entry from the discovery list
		rde = rdList.get(destIP);
		if(rde == null) {

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Route Manager - Route discovery terminated due to no RDE entry ");
			return 0;
		}

		// if max retries exceeded, stop discovery
		if((rde.rreqRetries + 1) > cfgInfo.RREQRetriesVal) {
			rdList.remove(destIP);

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Route Manager - Route discovery terminated as max retries reached ("
				+ rde.rreqRetries + ")");
			return 0;
		}


		// create the new RREQ, using the old RREQ (except for TTL and RREQID)
		sendto = rde.rreq.toIPAddr;

		// increment TTL by TTL_INCREMENT, but it should not exceed TTL_THRESHHOLD
		ttl = (short) (rde.rreq.ttlValue + cfgInfo.TTLIncrementVal);
		if(ttl >= cfgInfo.TTLThresholdVal) {
			ttl = (short) cfgInfo.netDiameterVal;
		}

		jf = rde.rreq.joinFlag;
		rf = rde.rreq.repairFlag;
		gf = rde.rreq.gratRREPFlag;
		df = rde.rreq.destOnlyFlag;
		usnf = rde.rreq.unknownSeqNumFlag;
		hc = rde.rreq.hopCount;

		// Increment RREQID and use this value
		ri = curInfo.incrementOwnRREQID();

		da = rde.rreq.destIPAddr;
		dsn = rde.rreq.destSeqNum;
		oa = rde.rreq.origIPAddr;
		osn = rde.rreq.origSeqNum;

		newRREQ = new RREQ(cfgInfo, curInfo, true, sendto, ttl, jf, rf, gf, df, usnf, hc,
						ri, da, dsn, oa, osn);

		rde.rreq = newRREQ;

		// put RREQ ID + originator ip in list for RREQ minder with PATH_DISCOVERY_TIME
		idList.add(oa, ri);

		// multicast the RREQ again
		pktSender.sendMessage(newRREQ);

		// increment the number of RREQs re-send
		rde.rreqRetries++;

		// if route discovery is ERS
		if(cfgInfo.routeDiscoveryModeVal == ConfigInfo.ROUTE_DISCOVERY_ERS_VAL) {
			if(ttl >= cfgInfo.TTLThresholdVal) {
				rde.sleepTime = cfgInfo.netTraversalTimeVal;
			} else {
				rde.sleepTime = 2 * cfgInfo.nodeTraversalTimeVal
						* ( ttl + cfgInfo.timeoutBufferVal);
			}

		// else assumes, route discovery is non-ERS
		} else {
			rde.sleepTime = rde.sleepTime * 2;
		}

		return rde.sleepTime;
	}

	/*--------------------------------------------------------------------------------------*/


	/**
			//curInfo.log.write(Logging.INFO_LOGGING,
			//	"Route Manager - Updating route use for "
			//	+ pkt.toIPAddr.getHostAddress());
			// get routes to dest & nextop to dest
			// update lifetime = curr time + ACTIVE_ROUTE_TIMEOUT
			// get routes to originator & next hop to orig
			// update lifetime = curr time + ACTIVE_ROUTE_TIMEOUT

	*/
	public synchronized void processExistingRouteUse(IPPkt pkt) throws Exception {
		RouteEntry origRte, destRte, nextHopRte;
		long currTime;

		currTime = (new Date()).getTime();

		// if I am not the originator of the packet, then update route to originator
		// of packet and also the next hop, if one exists
		if(!(pkt.fromIPAddr.equals(cfgInfo.ipAddressVal))) {

			origRte = rtList.get(pkt.fromIPAddr);

			if(origRte != null
				&& origRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

				origRte.expiryTime = currTime + cfgInfo.activeRouteTimeoutVal;
				rtList.update(pkt.fromIPAddr, origRte);

				if(origRte.hopCount > 1) {

					nextHopRte = rtList.get(origRte.nextHopIPAddr);

					if(nextHopRte != null
						&& nextHopRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

						nextHopRte.expiryTime = currTime
							+ cfgInfo.activeRouteTimeoutVal;
						rtList.update(origRte.nextHopIPAddr, nextHopRte);

					}
				}

			}
		}

		// if I am not the destination of the packet, then update route to destination
		// of packet and also the next hop, if one exists
		if(!(pkt.toIPAddr.equals(cfgInfo.ipAddressVal))) {

			destRte = rtList.get(pkt.toIPAddr);

			if(destRte != null
				&& destRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

				destRte.expiryTime = currTime + cfgInfo.activeRouteTimeoutVal;
				rtList.update(pkt.toIPAddr, destRte);

				if(destRte.hopCount > 1) {

					nextHopRte = rtList.get(destRte.nextHopIPAddr);

					if(nextHopRte != null
						&& nextHopRte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID) {

						nextHopRte.expiryTime = currTime
							+ cfgInfo.activeRouteTimeoutVal;
						rtList.update(destRte.nextHopIPAddr, nextHopRte);

					}
				}

			}
		}
	}

	/*--------------------------------------------------------------------------------------*/

	// Methods invoked by the GUI
	// --------------------------

	/**
	* Method to provide a reinitization of the routing environment
	* if required.
	* @param int level - The initialization level
	*			0 = full initialization level
	*			1 - 100 = other init levels
	* @return int - route count
	*/
	public synchronized int reInitRouteEnvironment(int level) {
		return osOps.initializeRouteEnvironment(level);
	}


	/*--------------------------------------------------------------------------------------*/

	// Methods invoked by the GUI
	// --------------------------

	/**
	* Method to provide the number of routes in the routing
	* environment(related to protocol handler) to the GUI.
	* @return int - route count
	*/
	public synchronized int getRouteCount() {
		return rtList.getRouteCount();
	}

	/**
	* Method to get the number of fields (i.e. route info)
	* that would be shown on the GUI.
	* @return int - field count
	*/
	public synchronized int getFieldCount() {
		return 10;
	}

	/**
	* Method to return the value related to a field in the
	* routing table to the GUI.
	* @param int row - the data row (route entry)
	* @param int column - data column (field)
	* @return String - string value of data
	*/
	public synchronized String getRouteValueAt(int row, int column) {
		Object array[];
		RouteEntry rte;
		long lifetimeLong;
		String str;
		int i;

		// return spaces if the route entry count
		// changed before comming here
		if(row >= rtList.getRouteCount())
			return " ";

		try {
			array = rtList.getRouteArray();
		} catch(Exception e) {
			return " ";
		}
		
		rte = (RouteEntry) array[row];

		if(column == 0) {
			return rte.destIPAddr.getHostAddress();

		} else if(column == 1) {
			return "" + rte.destSeqNum;

		} else if(column == 2) {
			if(rte.validDestSeqNumFlag == RouteEntry.DEST_SEQ_FLAG_VALID)
				return "Valid";
			else
				return "Invalid";

		} else if(column == 3) {
			if(rte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_BEING_REPAIRED)
				return "Being Repaired";
			else if(rte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_REPAIRABLE)
				return "Repairable";
			else if(rte.routeStatusFlag == RouteEntry.ROUTE_STATUS_FLAG_VALID)
				return "Valid";
			else
				return "Invalid";

		} else if(column == 4) {
			return rte.ifaceName;

		} else if(column == 5) {
			return "" + rte.hopCount;

		} else if(column == 6) {
			return rte.nextHopIPAddr.getHostAddress();

		} else if(column == 7) {
			lifetimeLong = rte.expiryTime - (new Date()).getTime();
			return (lifetimeLong >= 0 ? "" + lifetimeLong : "expired");

		} else if(column == 8) {
			if(rte.precursorList != null && rte.precursorList.size() > 0) {
				str = "";
				for(i = 0; i < rte.precursorList.size(); i++) {
					str += ((InetAddress) rte.precursorList.get(i)).getHostAddress()
						+ " ";
				}
				return str;
			}
			return "";

		} else if(column == 9) {
			lifetimeLong = rte.nextHelloReceiveTime - (new Date()).getTime();
			return (lifetimeLong >= 0 ? "" + lifetimeLong : " ");

		} else
			return " ";
	}

	/**
	* Method to return the name of the information field
	* in the routing table, to the GUI
	* @param int column - field column
	* @return String - string value of the field name
	*/
	public synchronized String getFieldName(int column) {
		if(column == 0)
			return "Destination";
		else if(column == 1)
			return "Sequence";
		else if(column == 2)
			return "Sequence Flag";
		else if(column == 3)
			return "Route Flag";
		else if(column == 4)
			return "Interface";
		else if(column == 5)
			return "Hop Count";
		else if(column == 6)
			return "Next Hop";
		else if(column == 7)
			return "Lifetime";
		else if(column == 8)
			return "Precursors";
		else if(column == 9)
			return "Hello Lifetime";
		else
			return " ";
	}
}
