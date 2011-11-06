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

import jadhoc.conf.*;
import jadhoc.other.*;
import jadhoc.net.*;

/**
* This class defines the thread that managers the lifetime of hello messages
* that are received for a given route.
*
* @author : Asanga Udugama
* @date : 15-dec-2003
* @email : adu@comnets.uni-bremen.de
*
*/

public class HelloReceiptMinder extends Thread {
	ConfigInfo cfgInfo;
	CurrentInfo curInfo;
	RouteManager rtMgr;
	InetAddress destIPAddr;
	int lifeTime;

	public HelloReceiptMinder(ConfigInfo cfg, CurrentInfo cur,
				RouteManager rm, InetAddress da, int st) {
		cfgInfo = cfg;
		curInfo = cur;
		rtMgr = rm;
		destIPAddr = da;
		lifeTime = st;
	}

	// in loop
	//	sleep for lifetime given in hello expiry
	// 	then call method in route manager to check
	// 	expiry

	public void run() {

		try {

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Hello Receipt Minder - Hello Receipt Minder started for destination "
						+ destIPAddr.getHostAddress());

			// sleep for the 1st time
			sleep(lifeTime);

			while(true) {

				lifeTime = rtMgr.checkHelloReceived(destIPAddr, this);

				if(lifeTime <= 0)
					break;

				sleep(lifeTime);
			}

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Hello Receipt Minder - Hello Receipt Minder terminated for destination "
						+ destIPAddr.getHostAddress());


		} catch(Exception e) {
			// do not consider as error if the thread ended
			// due to the interrupt exception as this is done
			// purposely
			if(!(e instanceof InterruptedException)) {

				// log as error
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Hello Receipt Minder - Hello Receipt Minder failed -  " + e);
			}
		}
	}

	/**
	* Method to terminate the hello receipt minder
	*/
	public void terminate() {
		interrupt();
	}
}
