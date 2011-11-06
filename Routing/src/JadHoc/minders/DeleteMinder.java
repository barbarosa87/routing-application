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

import jadhoc.net.RouteManager;
import java.net.*;

import jadhoc.conf.*;
import jadhoc.other.*;


/**
* This is the minder that manages a route when in the delete
* period. Procedure
*
* 	sleep for DELETE period
* 	call lifetime expiry in route manager
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class DeleteMinder extends Thread {
	ConfigInfo cfgInfo;
	CurrentInfo curInfo;
	RouteManager rtMgr;
	InetAddress destIPAddr;
	int lifeTime;

	public DeleteMinder(ConfigInfo cfg, CurrentInfo cur, RouteManager rm, InetAddress da, int st) {
		cfgInfo = cfg;
		curInfo = cur;
		rtMgr = rm;
		destIPAddr = da;
		lifeTime = st;
	}

	// in loop
	//	sleep for lifetime in route
	//	get route
	//	if lifetime expired
	//		call delete route lifetime expired in route manager

	public void run() {

		try {
			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Delete Minder - Delete Minder started for destination "
					+ destIPAddr.getHostAddress());

			sleep(lifeTime);

			while(true) {

				lifeTime = rtMgr.checkDeleteRouteLifetime(destIPAddr, this);

				if(lifeTime <= 0)
					break;

				sleep(lifeTime);
			}

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Delete Minder - Delete Minder terminated for destination "
					+ destIPAddr.getHostAddress());


		} catch(Exception e) {
			// do not consider as error if the thread ended
			// due to the interrupt exception as this is done
			// purposely
			if(!(e instanceof InterruptedException)) {

				// log as error
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Delete Minder - Delete Minder failed -  " + e);
			}
		}
	}

	/**
	* Method to terminate the delete minder
	*/
	public void terminate() {
		interrupt();
	}
}

