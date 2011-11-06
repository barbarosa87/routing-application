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

import jadhoc.conf.*;
import jadhoc.other.*;

import java.net.*;
import java.util.*;

/**
* Objects of this class holds information ralated to a
* single RREQ ID. These objects are used to manage the
* expiry of these RREQ IDs.
*
* @author : Asanga Udugama
* @date : 11-aug-2003
* @email : adu@comnets.uni-bremen.de
*/
public class RREQIDEntry {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	// RREQ ID info
	public InetAddress origIPAddr;
	public int RREQIDNum;
	public long expiryTime;

	/**
	* Constructor to create a RREQ ID entry to be placed in the list.
	* @param ConfigInfo cfg - config object
	* @param InetAddress adr - Originator IP address of RREQ
	* @param int id - RREQ ID number in the RREQ
	*/
	public RREQIDEntry(ConfigInfo cfg, CurrentInfo cur, InetAddress adr, int id) {
		cfgInfo = cfg;
		curInfo = cur;

		origIPAddr = adr;
		RREQIDNum = id;
		try {
			expiryTime = new Date().getTime() + cfgInfo.pathDiscoveryTimeVal;

		} catch(Exception e) {
			expiryTime = new Date().getTime();
		}
	}
}
