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

import java.net.*;
import java.io.*;

import jpcap.*;

import jadhoc.msg.*;
import jadhoc.conf.*;
import jadhoc.other.*;

/**
* This Class provides the functionality to transmit packets on a Windows
* environment. These packets can be either AODV messages or other IP
* packets.
*
* @author : Asanga Udugama
* @date : 11-feb-2004
* @email : adu@comnets.uni-bremen.de
*
*/
public class PacketSenderWindows implements PacketSenderInterface {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;
	public MulticastSocket mcastSock;
	public InetAddress mcastGrpAddr;
	public JpcapSender jpcapSender;
	/**
	* Constructor to create the packet sender. Constructor
	* will open the AODV socket for sending messages.
	* @param CfgInfo cfg - config info object
	* @exception Exception - thrown if errors encountered
	*/
	public PacketSenderWindows(ConfigInfo cfg, CurrentInfo cur) throws Exception {
		cfgInfo = cfg;
		curInfo = cur;
	}

	/**
	* Method to start the packet sender. Start open the packet
	* sending connection in jpcap
	* @exception Exception - thrown if error
	*/
	public void start() throws Exception {
		mcastGrpAddr = cfgInfo.ipAddressMulticastVal;
		mcastSock = new MulticastSocket(AODVMessage.AODV_PORT);
		//mcastSock.joinGroup(mcastGrpAddr);

		jpcapSender = JpcapSender.openDevice(cfgInfo.ifaceName);
	}

	/**
	* Method to stop the packet sender. Stops by leaving the
	* multicast group.
	* @exception Exception - thrown when joining group, if error
	*/
	public void stop() throws Exception {
		//mcastSock.leaveGroup(mcastGrpAddr);
		jpcapSender.close();
	}

	/**
	* Method to send a AODV message through the multicast socket.
	* @param AODVMessage msg - AODV message top send
	* @exception Exception - thrown if error
	*/
	public void sendMessage(AODVMessage msg) throws Exception {
		mcastSock.setTimeToLive(msg.ttlValue);
		mcastSock.send(msg.javaUDPDgram);

		// log
		curInfo.log.write(Logging.INFO_LOGGING,
				"Packet Sender - AODV Message Generated - "
						+ msg.toString());
	}

	/**
	* Method to send a IP packet out through jpcap.
	* @param IPPkt pkt - the packet to be sent
	* @exception Exception - thrown if errors encountered
	*/
	public void sendPkt(IPPkt pkt) throws Exception {
		jpcapSender.sendPacket(pkt.jpcapIPPkt);
	}
}
