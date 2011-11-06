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

package jadhoc.os;

import java.net.*;
import java.io.*;
import java.util.*;

import jadhoc.conf.*;
import jadhoc.net.*;
import jadhoc.other.*;

/**
* This class defines all the functions related to manipulating
* the routing environment of a Zaurus/IPv4 environment. Implements
* the OSOperationsInterface to provide a consistent interface
* to the route manager.
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class OSOperationsZaurusIPv4 implements OSOperationsInterface {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	private Process proc;
	private String cmd, str, s;
	private BufferedReader stdInput, stdError;


	/**
	* Constructs the object
	*/
	public OSOperationsZaurusIPv4(ConfigInfo cfg, CurrentInfo cur) {
		cfgInfo = cfg;
		curInfo = cur;
	}

	/**
	* Method to initialize the routing environment
	* in a  IPv4 environment to perform AODV protocol
	* handling
	* @param int level - The initialization level
	*			0 = full initialization level
	*			1 - 100 = other init levels
	* @return int - returns the success or failure
	*/
	public int initializeRouteEnvironment(int level) {

                try {
			switch(level) {
				case 0:
					fullInit();
					break;
				default: // do nothing
					break;
			}
                } catch(Exception e) {
			// log
			curInfo.log.write(Logging.CRITICAL_LOGGING, "OS Ops IPv4 - Init failed");

			return 1;
                }
                return 0;
	}

	private void fullInit() throws Exception {
		curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - Init started");

		// delete the default route
		cmd = cfgInfo.pathToSystemCmdsVal
				+ "/route -n del default ";
		curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - " + cmd);
                proc = Runtime.getRuntime().exec(cmd);
		proc.waitFor();

		cmd = cfgInfo.pathToSystemCmdsVal
				+ "/route -n add default dev " + cfgInfo.loIfaceNameVal;
		curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - " + cmd);
                proc = Runtime.getRuntime().exec(cmd);
		proc.waitFor();

		stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		str = new String();
                while((s = stdError.readLine()) != null) {
			str += s;
		}
		if(str.trim().length() > 0) {
			throw new Exception(str);
		}

		cmd = cfgInfo.pathToSystemCmdsVal
				+ "/route -n add -host " + cfgInfo.ipAddressMulticastVal.getHostAddress()
				+ " dev " + cfgInfo.ifaceNameVal;
		curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - " + cmd);
                proc = Runtime.getRuntime().exec(cmd);
		proc.waitFor();

		stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		str = new String();
                while((s = stdError.readLine()) != null) {
			str += s;
		}
		if(str.trim().length() > 0) {
			throw new Exception(str);
		}

		// log
		curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - Init completed");
	}

	/**
	* Method to add a route entry in the routing environment
	* of a Zaurus/IPv4 environment.
	* @param RouteEntry rtEntry - the route entry from which to get
	*				information
	* @return int - returns the success or failure
	*/
	public int addRoute(RouteEntry rtEntry) {

                try {

			// the "nexthop via" clause is required only if the destination is
			// not in link local network (i.e. if only hop count > 1)
			if(rtEntry.destIPAddr.equals(rtEntry.nextHopIPAddr)) {
				cmd = cfgInfo.pathToSystemCmdsVal + "/route -n add -host "
					+ rtEntry.destIPAddr.getHostAddress()
					+ " dev " + rtEntry.ifaceName;
			} else {
				cmd = cfgInfo.pathToSystemCmdsVal + "/route -n add -host "
					+ rtEntry.destIPAddr.getHostAddress()
					+ " gw " + rtEntry.nextHopIPAddr.getHostAddress()
					+ " dev " + rtEntry.ifaceName;
			}
			curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - Route add  " + cmd);
                        proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();

			// TODO : check for errors

		} catch(Exception e) {
			// log
			curInfo.log.write(Logging.CRITICAL_LOGGING, "OS Ops IPv4 - Route add failed");
                        return 1;
                }
                return 0;
	}

	/**
	* Method to remove a route entry in the routing environment of
	* a Zaurus/IPv4 environment
	* @param RouteEntry rtEntry - the route entry from which to get
	*				information
	* @return int - returns the success or failure
	*/
	public int deleteRoute(RouteEntry rtEntry) {

                try {

			cmd = cfgInfo.pathToSystemCmdsVal + "/route -n del -host "
				+ rtEntry.destIPAddr.getHostAddress();
			curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - Route delete " + cmd);
                        proc = Runtime.getRuntime().exec(cmd);

			// remove entry from ARP cache; only for routes in the link
			// network
			if(rtEntry.destIPAddr.equals(rtEntry.nextHopIPAddr)) {
				cmd = cfgInfo.pathToSystemCmdsVal + "/arp del "
					+ rtEntry.destIPAddr.getHostAddress();
	                        proc = Runtime.getRuntime().exec(cmd);
				proc.waitFor();
			}

			// TODO : check for errors

                } catch(Exception e) {
			curInfo.log.write(Logging.CRITICAL_LOGGING, "OS Ops IPv4 - Route delete failed");
                        return 1;
                }
                return 0;
	}

	/**
	* Method to put the route environment to the original state
	* before terminating the protocol handler in Zaurus/IPv4
	* environment.
	* @return int - returns the success or failure
	*/
	public int finalizeRouteEnvironment() {

		try {
			curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - Termination started");

			cmd = cfgInfo.pathToSystemCmdsVal
				+ "/route del default ";

			curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - " + cmd);
                        proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();

			stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			str = new String();
                        while((s = stdError.readLine()) != null) {
                                str += s;
			}
			if(str.trim().length() > 0) {
				throw new Exception(str);
			}

			cmd = cfgInfo.pathToSystemCmdsVal
				+ "/route -n add default gw "
				+ cfgInfo.ipAddressGatewayVal.getHostAddress()
				+ " dev " + cfgInfo.ifaceNameVal;

			curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - " + cmd);
                        proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();

			stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			str = new String();
                        while((s = stdError.readLine()) != null) {
                                str += s;
			}
			if(str.trim().length() > 0) {
				throw new Exception(str);
			}

			cmd = cfgInfo.pathToSystemCmdsVal
				+ "/route -n del -host " + cfgInfo.ipAddressMulticastVal.getHostAddress();
			curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - " + cmd);
                        proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();

			stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			str = new String();
                        while((s = stdError.readLine()) != null) {
                                str += s;
			}
			if(str.trim().length() > 0) {
				throw new Exception(str);
			}

			curInfo.log.write(Logging.INFO_LOGGING, "OS Ops IPv4 - Termination completed");

                } catch(Exception e) {
			curInfo.log.write(Logging.CRITICAL_LOGGING, "OS Ops IPv4 - Termination failed");
                        return 1;
                }
                return 0;
	}
}
