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

import jadhoc.conf.*;
import jadhoc.other.*;
import jpcap.EthernetPacket;
import jpcap.IPPacket;

/**
* This Class represents a IP Packet received by the machine on which this
* protocol handler is being executed. The protocol handler performs 2
* tasks using these packets.
*  1. if route not available, initiates a Route Discovery
*  2. if route available, updates the time of the existing route
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class IPPkt {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;

	public IPPacket jpcapIPPkt;

	// Ether info
	public String fromMACAddr;
	public String toMACAddr;
	public String ifaceName;

	// IP info
	public InetAddress fromIPAddr;
	public InetAddress toIPAddr;
	public short protocol; // UDP, TCP, etc.

	/**
	* Constructor to create a IP Packet using the information
	* sent on the jpcap packet
	*
	* @param ConfigInfo cfg - the config info object
	* @param CurrentInfo cur - the current info object
	* @param jpcap.IPPacket ipp - the jpcap packet from which this IP
	*				packet is created
	* @param String iface - the interface on which the packet received
	*/
	public IPPkt(ConfigInfo cfg, CurrentInfo cur, IPPacket ipp, String iface) throws Exception {
		cfgInfo = cfg;
		curInfo = cur;

		jpcapIPPkt = ipp;
		ifaceName = iface;

		if(cfgInfo.ipVersionVal == ConfigInfo.IPv4_VERSION_VAL)
			breakJpcapIPToIPv4Pkt();
		else if(cfgInfo.ipVersionVal == ConfigInfo.IPv6_VERSION_VAL)
			breakJpcapIPToIPv6Pkt();
		else
			throw new Exception("Invalid IP version");
	}

	/**
	* Method to build the IPv4 IP packet using the information in
	* the Jpcap packet.
	*/
	private void breakJpcapIPToIPv4Pkt() throws Exception {
		EthernetPacket ethPkt;

		// extract Ether info
		ethPkt = (EthernetPacket)jpcapIPPkt.datalink;
		fromMACAddr = ethPkt.getSourceAddress();
		toMACAddr = ethPkt.getDestinationAddress();

		// extract IP info
		fromIPAddr = InetAddress.getByName(jpcapIPPkt.src_ip.getHostAddress());
		toIPAddr = InetAddress.getByName(jpcapIPPkt.dst_ip.getHostAddress());
		protocol = jpcapIPPkt.protocol;
	}

	/**
	* Method to build the IPv6 IP packet using the information in
	* the Jpcap packet.
	*/
	private void breakJpcapIPToIPv6Pkt() throws Exception {
		EthernetPacket ethPkt;

		// extract Ether info
		ethPkt = (EthernetPacket)jpcapIPPkt.datalink;
		fromMACAddr = ethPkt.getSourceAddress();
		toMACAddr = ethPkt.getDestinationAddress();

		// extract IP info
                //fromIPAddr=jpcapIPPkt.src_ip.getAddress();
		//fromIPAddr = jpcapIPPkt.src_ip.getInetAddress();
		//toIPAddr = jpcapIPPkt.dst_ip.getInetAddress();
		protocol = jpcapIPPkt.protocol;
	}
}
