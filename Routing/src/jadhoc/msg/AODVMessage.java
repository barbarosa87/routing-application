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

package jadhoc.msg;

import java.net.*;
import jpcap.*;

import jadhoc.conf.*;
import jadhoc.other.*;
import jpcap.UDPPacket;

/**
* This Class represents a base AODV message
*
* @author : Asanga Udugama
* @date : 28-aug-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class AODVMessage {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	public DatagramPacket javaUDPDgram;
	public UDPPacket jpcapUDPDgram;

	// Header info of an AODV message
	public InetAddress toIPAddr;
	public InetAddress fromIPAddr;
	public short ttlValue;
	public boolean multiCast;
	public String ifaceName;

	public static final byte AODV_RREQ_MSG_CODE = 1;
	public static final byte AODV_RREP_MSG_CODE = 2;
	public static final byte AODV_RERR_MSG_CODE = 3;
	public static final byte AODV_RREPACK_MSG_CODE = 4;
	public static final short AODV_PORT = 654;


	public AODVMessage(ConfigInfo cfg, CurrentInfo cur, UDPPacket up, String iface) throws Exception {
		cfgInfo = cfg;
		curInfo = cur;

		jpcapUDPDgram = up;
		ifaceName = iface;

		if(cfgInfo.ipVersionVal == ConfigInfo.IPv4_VERSION_VAL)
			breakDgramToIPv4AODV();
		else if(cfgInfo.ipVersionVal == ConfigInfo.IPv6_VERSION_VAL)
			breakDgramToIPv6AODV();
		else
			throw new Exception("Invalid IP version");
	}

	public AODVMessage(ConfigInfo cfg, CurrentInfo cur, boolean mf, InetAddress sendto,
								short ttl) throws Exception {
		cfgInfo = cfg;
		curInfo = cur;

		multiCast = mf;
		if(multiCast){
			toIPAddr = cfgInfo.ipAddressMulticastVal;
		} else {
			toIPAddr = sendto;
		}
		fromIPAddr = cfgInfo.ipAddressVal;
		ttlValue = ttl;
		ifaceName = null;
	}

	/**
	* Method to build the header info of an IPv4 AODV
	* DatagramPacket.
	*
	*/
	private void breakDgramToIPv4AODV() throws Exception {

		// extract IP header info
		fromIPAddr = InetAddress.getByName(jpcapUDPDgram.src_ip.getHostAddress());
		toIPAddr = InetAddress.getByName(jpcapUDPDgram.dst_ip.getHostAddress());
		ttlValue = jpcapUDPDgram.hop_limit;
		if(toIPAddr.equals(cfgInfo.ipAddressMulticastVal))
			multiCast = true;
		else
			multiCast = false;
	}

	/**
	* Method to build the header info of an IPv6 AODV
	* DatagramPacket.
	*
	*/
	private void breakDgramToIPv6AODV() throws Exception {
	}

	/**
	* Method to return the values in header of AODV messages
	* @return String - AODV Message header contents as a string
	*/

	public String toString() {
		return "Destination Address : " + toIPAddr.getHostAddress() + ", "
			+ "Source Address : " + fromIPAddr.getHostAddress() + ", "
			+ "TTL Value : " + ttlValue + ", "
			+ "Multicast : " + multiCast + ", ";
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
