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
* single route in the protcol handler.
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*/

public class RouteEntry {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	// values related to a single route
	public InetAddress destIPAddr;
	public int destSeqNum;
	public int validDestSeqNumFlag;
	public int routeStatusFlag;
	public String ifaceName;
	public int hopCount;
	public InetAddress nextHopIPAddr;
	public ArrayList precursorList;
	public long expiryTime;
	// public int lifeTime;
	// public boolean routeExpired; // becomes true when the routeStatusFlag is VALID and lifeTime is expired
	public Thread activeMinder; // 4 possibilities ; instance of a routeMinder,
				    //      instance of a routeDeleter, instance of a routeDiscoverer
				    //      or null
	public boolean kernelRouteSet; // true if route entry set in the kernel, else false

	// values used to manage HELLO receipts
	public Thread helloReceiptMinder;
	public long nextHelloReceiveTime;

	// constants of all flags

	// possible status values of validDestSeqNumFlag
	public static final int DEST_SEQ_FLAG_INVALID = 0;
	public static final int DEST_SEQ_FLAG_VALID = 1;

	// possible status values of routeStatusFlag
	public static final int ROUTE_STATUS_FLAG_INVALID = 0;
	public static final int ROUTE_STATUS_FLAG_VALID = 1;
	public static final int ROUTE_STATUS_FLAG_REPAIRABLE = 2;
	public static final int ROUTE_STATUS_FLAG_BEING_REPAIRED = 3;

	public RouteEntry(ConfigInfo cfg, CurrentInfo cur) {
		cfgInfo = cfg;
		curInfo = cur;
	}

	public RouteEntry getCopy() throws Exception {
		RouteEntry newEntry;

		newEntry = new RouteEntry(cfgInfo, curInfo);

		//newEntry.destIPAddr = InetAddress.getByAddress(destIPAddr.getAddress());
		newEntry.destIPAddr = getInetByAddress(destIPAddr.getAddress());
		newEntry.destSeqNum = destSeqNum;
		newEntry.validDestSeqNumFlag = validDestSeqNumFlag;
		newEntry.routeStatusFlag = routeStatusFlag;
		newEntry.ifaceName = new String(ifaceName);
		newEntry.hopCount = hopCount;
		//newEntry.nextHopIPAddr = InetAddress.getByAddress(nextHopIPAddr.getAddress());
		newEntry.nextHopIPAddr = getInetByAddress(nextHopIPAddr.getAddress());
		newEntry.precursorList = new ArrayList(precursorList);
		newEntry.expiryTime = expiryTime;

		//newEntry.routeExpired = routeExpired;

		newEntry.activeMinder = activeMinder;

		newEntry.kernelRouteSet = kernelRouteSet;

		newEntry.helloReceiptMinder = helloReceiptMinder;
		newEntry.nextHelloReceiveTime = nextHelloReceiveTime;

		return newEntry;
	}

	public String toString() {
		String str;

		str = "Destination : " + destIPAddr + ", Destnation Sequence : " + destSeqNum
			+ ", Valid Sequence Flag : ";
		if(validDestSeqNumFlag == DEST_SEQ_FLAG_INVALID) {
			str += "Valid";
		} else if(validDestSeqNumFlag == DEST_SEQ_FLAG_VALID) {
			str += "Invalid";
		} else {
			str += "Indetermined";
		}

		str += ", Route Status : ";
		if(routeStatusFlag == ROUTE_STATUS_FLAG_INVALID) {
			str += "Invalid";
		} else if(routeStatusFlag == ROUTE_STATUS_FLAG_VALID) {
			str += "Valid";
		} else if(routeStatusFlag == ROUTE_STATUS_FLAG_REPAIRABLE) {
			str += "Repairable";
		} else if(routeStatusFlag == ROUTE_STATUS_FLAG_BEING_REPAIRED) {
			str += "Being Repaired";
		} else {
			str += "Indetermined";
		}

		// TODO : include precursor list

		str += ", Interface : " + ifaceName + ", Hop Count : " + hopCount
			+ ", Next Hop : " + nextHopIPAddr + ", Expiry Time : " + expiryTime
			//+ ", Route Expired : " + routeExpired
			+ ", Kernel Routes Set : " + kernelRouteSet;

		return str;
	}

	public InetAddress getInetByAddress(byte addr[]) throws Exception {
		String str;
		int num;

		// this an IPv4 address
		if(addr.length == 4) {
			str = "";
			for(int i = 0; i < 4; i++) {
				num = addr[i];
				num = num & 0x000000FF;
				str = str + num;
				if(i < 3) {
					str += ".";
				}
			}

		// means, IPv6 address
		} else {
			str = "";
			for(int i = 0; i < 16; i++) {
				num = addr[i];
				num = num & 0x000000FF;
				str = str + num;
				if(i < 15) {
					str += ":";
				}
			}
		}
		return InetAddress.getByName(str);
	}
}
