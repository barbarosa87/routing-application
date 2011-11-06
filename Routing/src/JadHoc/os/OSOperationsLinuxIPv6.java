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

import jadhoc.conf.*;
import jadhoc.net.*;
import jadhoc.other.*;

/**
* This class defines all the functions related to manipulating
* the routing environment of a Linux/IPv6 environment. Implements
* the OSOperationsInterface to provide a consistent interface
* to the route manager.
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class OSOperationsLinuxIPv6 implements OSOperationsInterface {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	Process proc;
	String cmd, s;
	BufferedReader stdInput, stdError;

	/**
	* Constructs the object
	*/
	public OSOperationsLinuxIPv6(ConfigInfo cfg, CurrentInfo cur) {
		cfgInfo = cfg;
		curInfo = cur;
	}

	/**
	* Method to initialize the routing environment
	* in a Linux IPv6 environment to perform AODV protocol
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
                        return 1;
                }
                return 0;
	}

	private void fullInit() throws Exception {
		cmd = "ip -6 neigh add " + cfgInfo.ipAddressGateway + " lladdr "
			+ ConfigInfo.MAC_ADDRESS_OF_GATEWAY + " dev " + cfgInfo.ifaceNameVal;
                proc = Runtime.getRuntime().exec(cmd);

                stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		// To see errors enable stdError print loop
		//
		//while((s = stdInput.readLine()) != null) {
               	//        System.out.println(s);
               	//}
		//
                //while((s = stdError.readLine()) != null) {
                //        System.out.println(s);
                //}
	}

	/**
	* Method to add a route entry in the routing environment
	* of a Linux/IPv6 environment.
	* @param RouteEntry rtEntry - the route entry from which to get
	*				information
	* @return int - returns the success or failure
	*/
	public int addRoute(RouteEntry rtEntry) {

                try {

			cmd = "ip -6 route add " + rtEntry.destIPAddr.getHostAddress() + " via "
				+ rtEntry.nextHopIPAddr.getHostAddress() + " dev " + rtEntry.ifaceName;
                        proc = Runtime.getRuntime().exec(cmd);

                        stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// To see errors enable stdError print loop
			//
                        //while((s = stdInput.readLine()) != null) {
                        //        System.out.println(s);
                        //}
			//
                        //while((s = stdError.readLine()) != null) {
                        //        System.out.println(s);
                        //}
                } catch(Exception e) {
                        return 1;
                }
                return 0;
	}

	/**
	* Method to remove a route entry in the routing environment of
	* a Linux/IPv6 environment
	* @param RouteEntry rtEntry - the route entry from which to get
	*				information
	* @return int - returns the success or failure
	*/
	public int deleteRoute(RouteEntry rtEntry) {

                try {

			cmd = "ip -6 route del " + rtEntry.destIPAddr.getHostAddress() + " via "
				+ rtEntry.nextHopIPAddr.getHostAddress() + " dev " + rtEntry.ifaceName;
                        proc = Runtime.getRuntime().exec(cmd);

                        stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// To see errors enable stdError print loop
			//
                        //while((s = stdInput.readLine()) != null) {
                        //        System.out.println(s);
                        //}
			//
                        //while((s = stdError.readLine()) != null) {
                        //        System.out.println(s);
                        //}
                } catch(Exception e) {
                        return 1;
                }
                return 0;
	}

	/**
	* Method to put the route environment to the original state
	* before terminating the protocol handler in Linux/IPv6
	* environment.
	* @return int - returns the success or failure
	*/
	public int finalizeRouteEnvironment() {

                try {

			cmd = "ip -6 neigh del " + cfgInfo.ipAddressGateway + " lladdr "
				+ ConfigInfo.MAC_ADDRESS_OF_GATEWAY + " dev " + cfgInfo.ifaceNameVal;
                        proc = Runtime.getRuntime().exec(cmd);

                        stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                        stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// To see errors enable stdError print loop
			//
                        //while((s = stdInput.readLine()) != null) {
                        //        System.out.println(s);
                        //}
			//
                        //while((s = stdError.readLine()) != null) {
                        //        System.out.println(s);
                        //}
                } catch(Exception e) {
                        return 1;
                }
                return 0;
	}
}
