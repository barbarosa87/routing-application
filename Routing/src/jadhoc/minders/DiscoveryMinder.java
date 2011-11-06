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

package jadhoc.minders;

import java.net.*;

import jadhoc.msg.*;
import jadhoc.conf.*;
import jadhoc.other.*;
import jadhoc.net.*;

/**
* This class defines the thread that managers the route discovery process. A single
* thread gets activated for each request for route discovery. The procedur is as
* follows,
*
*	// retries = 1
*	// sleep-time = NET_TRAVEERSAL_TIME
*	// in loop
*	//	sleep for sleep-time
*	//	check if route has been made (made = route entry AND valid AND not expired)
*	//	if made
*	//		stop thread
*	//	if not made
*	//		retries = retries + 1
*	//		if retries > RREQ_RETRIES
*	//			stop thread
*	//		increment TTL in RREQ by TTL_INCREMENT
*	//		if TTL is now > TTL_THRESHHOLD
*	//			set TTL = NET_DIAMETER
*	//		increment RREQ ID by 1
*	// 		put RREQ ID + originator ip in list for RREQ minder with PATH_DISCOVERY_TIME
*	// 		multicast RREQ
*	//		sleep-time = sleep-time * 2
*
*
* @author : Asanga Udugama
* @date : 18-aug-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class DiscoveryMinder extends Thread {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;
	public RouteManager rtMgr;

	public InetAddress destIPAddr;
	public int sleepTime;

	/**
	* Constructor to create an initialize the thread
	*
	* @param ConfigInfo cfg - config info object
	* @param CurrentInfo cur - current info object
	* @param RouteManager rm - route manger to get work done
	* @param InetAddress da - the destination for which a route is
	*				required
	*/
	public DiscoveryMinder(ConfigInfo cfg, CurrentInfo cur, RouteManager rm, InetAddress da, int st) {
		cfgInfo = cfg;
		curInfo = cur;
		rtMgr = rm;
		sleepTime = st;

		destIPAddr = da;
	}

	/**
	* Method to start the thread to perform route discovery
	*/
	public void run() {

		try {

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Route Discoverer - Route Discoverer started for destination " + destIPAddr.getHostAddress());

			sleep(sleepTime);

			sleepTime = rtMgr.continueRouteDiscovery(destIPAddr);

			while(sleepTime > 0) {

				sleep(sleepTime);

				sleepTime = rtMgr.continueRouteDiscovery(destIPAddr);
			}

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Route Discoverer - Route Discoverer terminated for destination " + destIPAddr.getHostAddress());

		} catch(Exception e) {
			// do not consider as error if the thread ended
			// due to the interrupt exception as this is done
			// purposely
			if(!(e instanceof InterruptedException)) {

				// log as error
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Route Discoverer - Route Discoverer failed -  " + e);

			}
		}
	}

	/**
	* Method to terminate the route discovery process
	*/
	public void terminate() {
		interrupt();
	}
}
