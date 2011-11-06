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

import java.util.*;
import java.net.*;

import jadhoc.conf.*;
import jadhoc.net.*;
import jadhoc.msg.*;
import jadhoc.net.RouteManager;
import jadhoc.other.*;

/**
* This class defines the thread that managers the release of buffered packets
* that were buffered when a route was being made.
*
* @author : Asanga Udugama
* @date : 01-dec-2003
* @email : adu@comnets.uni-bremen.de
*
*/

public class BufferMinder extends Thread {
	ConfigInfo cfgInfo;
	CurrentInfo curInfo;
	RouteManager rtMgr;
	InetAddress destIPAddr;

	/**
	* Constructor to create an object and initialize
	* @param ConfigInfo cfg - config object
	* @param CurrentInfo cur - current info object
	* @param RouteManager rtm - route manager object
	* @param InetAddress dest - the detination IP address
	*/
	public BufferMinder(ConfigInfo cfg, CurrentInfo cur, RouteManager rtm, InetAddress dest) {
		cfgInfo = cfg;
		curInfo = cur;
		rtMgr = rtm;
		destIPAddr = dest;
	}

	/**
	* Method to start the Buffer minding thread. It will wait for the
	* given duration and then release the contents of the buffer.
	*/
	public void run() {

		try {

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Buffer Minder - Buffer minder started");

			sleep(500);
			rtMgr.releaseBuffer(destIPAddr);


			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Buffer Minder - Buffer released and minder stopped");


		} catch(Exception e) {
			// the InterruptedException is due to user invoking
			// stop ; so dont consider it as an error
			if(!(e instanceof InterruptedException)) {
				// call log
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Buffer Minder - Buffer minder failed - " + e);
			}
		}
	}

	/**
	* Method to stop the Buffer minder
	*/
	public void terminate() {
		interrupt();
		return;
	}
}
