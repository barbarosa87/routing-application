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
import java.util.*;

import jadhoc.msg.*;
import jadhoc.conf.*;
import jadhoc.net.RouteManager;
import jadhoc.other.*;
import jpcap.EthernetPacket;
import jpcap.IPPacket;
import jpcap.Jpcap;
import jpcap.JpcapHandler;
import jpcap.Packet;
import jpcap.UDPPacket;


/**
* This class handles the threads related to listening for packets and processing
* the received packets. There are 3 threads,
*	1. thread to capture packets on the LO interface
*       2. thread to capture packets that traverse the AODV capable
*          network interface (eg. eth1, eth2, etc.)
*       3. thread to process that packets that are placed in a queue by
*          the previous threads
*
* There can be 3 types of actions that the processing thread can request
*	1. A packet that was received on network interface and is a UDP packet
*          with destination port 654 - this means an AODV message that needs
           to be processed
*	2. A packet that was picked up by the LO interface that does not
*          contain the "127.." address or the destination MAC address is 00:00:00:00:00:00
*          - this means this destination IP Address of this packet has no route made,
*          therefore requires a route discovery to be initiatedn
*	3. Any other packet on the network interface - this means it is a packet
*          that uses a route, so update the route lifetimes
*
* @author : Asanga Udugama
* @date : 28-jul-2003
* @email : adu@comnets.uni-bremen.de
*
* @modification-history-ver-0.11
* @author : Asanga Udugama
* @date : 30-nov-2003
* @email : adu@comnets.uni-bremen.de
* @modification - Changed the class to process packets of LO interface, for the
*                 purpose of getting packets that require routes from the LO
*                 interface
*
*/
public class PacketListenerLinux extends Thread {
	public ConfigInfo cfgInfo;
	public CurrentInfo curInfo;
	public RouteManager rtMgr;

	private ArrayList pktQueue;
	private IPPacket ipPacket;
	private UDPPacket udpPkt;
	private EthernetPacket ethPkt;
	private IPPkt ipPkt;
	private RREQ rreqMsg;
	private RREP rrepMsg;
	private RERR rerrMsg;
	private RREPACK rrepackMsg;
	private InetAddress aodvMsgSrcIPAddr;
	private byte ipAddr[];
	private boolean updateForRouteUse;

	/**
	* Constructor to create th
	*/
	public PacketListenerLinux(ConfigInfo cfg, CurrentInfo cur, RouteManager rm) {
		cfgInfo = cfg;
		curInfo = cur;
		rtMgr = rm;

		pktQueue = new ArrayList();

		(new NetIfcPacketQueueBuilder()).start();
		(new LoIfcPacketQueueBuilder()).start();
	}

	public void run() {
		PacketInfo pktInfo;

		synchronized(pktQueue) {
				pktQueue.clear();
		}

		while(true) {

			try  {
				try {
					synchronized(pktQueue) {
						pktInfo = (PacketInfo) pktQueue.remove(0);

					}
				} catch(Exception e) {
					pktInfo = null;
				}

				// stay a little while if no packets in the queue
				if(pktInfo == null) {
					//sleep(500);
					continue;
				}

				// if route manager inactive, stop the thread
				if(!(rtMgr.isRouteMgrActive())) {
					return;
				}

				//if packet is not IP, don't do anything
				if(!(pktInfo.packet instanceof IPPacket)) {
					continue;
				}

				ipPacket = (IPPacket) pktInfo.packet;

				// Only packets of the given IP version used
				if(ipPacket.version != cfgInfo.ipVersionVal) {
					continue;
				}

				// process packet according to type
				if(pktInfo.ifcType == PacketInfo.PACKET_REQUIRES_ROUTE) {
					processRouteRequiredPacket();
				} else {
					processNetIfcPacket();
				}

			} catch(Exception e) {
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Packet Handler - Problem in loop - " + e);
				return;
			}
		}
	}

	/**
	* Method to process a packet that requires routes
	*
	* @exception Exception - thrown when errors occur
	*/
	void processRouteRequiredPacket() throws Exception {

		// if the packet contains src or dest IP addr as "127..." then
		// do not start a route discovery
		if(ipPacket.dst_ip.getHostAddress().startsWith("127.")
		   || ipPacket.src_ip.getHostAddress().startsWith("127.")) {
			return;
		}

		// if the originator of packet is not me, then start local repair
		if(!(InetAddress.getByName(ipPacket.src_ip.getHostAddress()).equals(cfgInfo.ipAddressVal))) {
			// no local repair implemented
			return;
		}

		// else, this is a packet the requires a route

		ipPkt = new IPPkt(cfgInfo, curInfo,
				ipPacket, cfgInfo.ifaceNameVal);
		rtMgr.processRouteDiscovery(ipPkt);
	}

	/**
	* Method to process a packet that has come on the network interface
	* on which AODV is being supported.
	*
	* @exception Exception - thrown when errors occur
	*/
	void processNetIfcPacket() throws Exception {

		updateForRouteUse = true;

		// if a packet is UDP, has dest port 654 and is not originating
		// from your own machine, then this packet is an AODV message
		// that was received by your own machine
		if(ipPacket instanceof UDPPacket) {

			udpPkt = (UDPPacket) ipPacket;

			if(udpPkt.dst_port == AODVMessage.AODV_PORT) {

				aodvMsgSrcIPAddr = InetAddress.getByName(udpPkt.src_ip.getHostAddress());

				if(!aodvMsgSrcIPAddr.equals(cfgInfo.ipAddressVal)) {

					// create msg based on type and call process function
					switch(udpPkt.data[0]) {
						case AODVMessage.AODV_RREQ_MSG_CODE:
							rreqMsg = new RREQ(cfgInfo,
									curInfo,
									udpPkt,
									cfgInfo.ifaceNameVal);
							rtMgr.processAODVMsgRREQ(rreqMsg);
							break;
						case AODVMessage.AODV_RREP_MSG_CODE:
							rrepMsg = new RREP(cfgInfo,
									curInfo,
									udpPkt,
									cfgInfo.ifaceNameVal);

							// if the RREP is a HELLO, the process differently
							if(rrepMsg.fromIPAddr.equals(rrepMsg.origIPAddr)
							   && rrepMsg.origIPAddr.equals(rrepMsg.destIPAddr)) {
								rtMgr.processAODVMsgHELLO(rrepMsg);
								updateForRouteUse = false;
							} else {
								rtMgr.processAODVMsgRREP(rrepMsg);
							}
							break;
						case AODVMessage.AODV_RERR_MSG_CODE:
							rerrMsg = new RERR(cfgInfo,
									curInfo,
									udpPkt,
									cfgInfo.ifaceNameVal);
							rtMgr.processAODVMsgRERR(rerrMsg);
							break;
						case AODVMessage.AODV_RREPACK_MSG_CODE:
							rrepackMsg = new RREPACK(cfgInfo,
									curInfo,
									udpPkt,
									cfgInfo.ifaceNameVal);
							rtMgr.processAODVMsgRREPACK(rrepackMsg);
							break;
					}
				}


			}
		}

		// for certain AODV messages, this update is not done
		if(updateForRouteUse) {
			// if it is not a AODV msg, then it means a packet which
			// is using an existing route
			ipPkt = new IPPkt(cfgInfo, curInfo,
					ipPacket, cfgInfo.ifaceNameVal);
			rtMgr.processExistingRouteUse(ipPkt);
		}

	}

	/**
	* This inner class defines the information that is placed in the
	* packet queue.
	*/
	public class PacketInfo {

		// values for ifcType
		public static final int PACKET_REQUIRES_ROUTE = 1;
		public static final int PACKET_IS_NORMAL = 2;

		public int ifcType;
		public Packet packet;

		public PacketInfo(int ift, Packet pkt) {
			ifcType = ift;
			packet = pkt;
		}
	}

	/**
	* This inner class processes the thread that listens to the network interface
	* to extract packets
	*/
	public class NetIfcPacketQueueBuilder extends Thread implements JpcapHandler {

		public void run() {
			try {
				Jpcap jpcap = Jpcap.openDevice(cfgInfo.ifaceName, 4096, false, 20);
				jpcap.loopPacket(-1, this);
			} catch(Exception e) {
				// log
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Net Ifc Packet Queue Builder - Problem in run - " + e);
			}
		}

		public void handlePacket(Packet pkt) {
			synchronized(pktQueue) {
				pktQueue.add(pktQueue.size(),
						new PacketInfo(PacketInfo.PACKET_IS_NORMAL,
									pkt));
			}
		}
	}

	/**
	* This inner class processes the thread that listens to the LO interface
	* to extract packets
	*/
	public class LoIfcPacketQueueBuilder extends Thread implements JpcapHandler {
		public void run() {
			try {
				Jpcap jpcap = Jpcap.openDevice(cfgInfo.loIfaceName, 4096, false, 20);
				jpcap.loopPacket(-1, this);
			} catch(Exception e) {
				// log
				curInfo.log.write(Logging.CRITICAL_LOGGING,
					"Lo Ifc Packet Queue Builder - Problem in run - " + e);
			}
		}

		public void handlePacket(Packet pkt) {
			synchronized(pktQueue) {
				pktQueue.add(pktQueue.size(),
						new PacketInfo(PacketInfo.PACKET_REQUIRES_ROUTE, pkt));
			}
		}
	}
}
