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

import jadhoc.conf.*;
import jadhoc.net.*;
import jadhoc.msg.*;
import jadhoc.other.*;

/**
* This lass defines the thread that managers the Hello message sender. It
* performs the following activity,
*	send Hello msg
*	   if there are any active (active flag set & unexpired) routes in route table
*		AND no RREQ is sent within last HELLO_INTERVAL
*		   send Hello in HELLO_INTERVAL
*			TTL = 1, dest ip = my own ip, dest seq = my own current seq,
*			hop count = 0, lifetime = ALLOWED_HELLO_LOSS * HELLO_INTERVAL
*
* @author : Asanga Udugama
* @date : 08-aug-2003
* @email : adu@comnets.uni-bremen.de
*
*/

public class HelloMinder extends Thread {
	ConfigInfo cfgInfo;
	CurrentInfo curInfo;
	RouteManager rtMgr;

	/**
	* Constructor to create an object and initialize
	* @param ConfigInfo cfg - config object
	* @param CurrentInfo cur - current info object
	* @param RouteManager rtm - route manager object
	*/
	public HelloMinder(ConfigInfo cfg, CurrentInfo cur, RouteManager rtm) {
		cfgInfo = cfg;
		curInfo = cur;
		rtMgr = rtm;
	}

	/**
	* Method to start the hello sender thread. Since this extends
	* Thread class the call of start will call the run method
	*/
	public void run() {
		HELLO helloMsg;
		int sleepTime;
		boolean continueHelloMinder;

		try {
			sleepTime = cfgInfo.helloIntervalVal;

			// log
			curInfo.log.write(Logging.INFO_LOGGING,
				"Hello Minder - Hello minder started");

			while(true) {

				// send hello message
				helloMsg = new HELLO(cfgInfo, curInfo);
				continueHelloMinder = rtMgr.sendHello(helloMsg);

				// if route manager stopped, stop this thread too
				if(!continueHelloMinder) {

					// log
					curInfo.log.write(Logging.INFO_LOGGING,
						"Hello Minder - Hello minder terminated");

					break;
				}

				// wait for user defined helloInterval time
				sleep(sleepTime);
			}
		} catch(Exception e) {
			// the InterruptedException is due to user invoking
			// stop ; so dont consider it as an error
			if(!(e instanceof InterruptedException)) {
				// call log
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Hello Minder - Hello minder failed - " + e);
			}
		}
	}

	/**
	* Method to stop the hello sender.
	*/
	public void terminate() {
		interrupt();
		return;
	}
}
