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

import jadhoc.msg.*;

/**
* This interface defines the methods that needs to be implemented to provide
* a packet sender.
*
* @author : Asanga Udugama
* @date : 11-feb-2004
* @email : adu@comnets.uni-bremen.de
*
*/
public interface PacketSenderInterface {

	/**
	* Method to start the packet sender. Start open the packet
	* sending connection in jpcap
	* @exception Exception - thrown if error
	*/
	public void start() throws Exception;

	/**
	* Method to stop the packet sender. Stops by leaving the
	* multicast group.
	* @exception Exception - thrown when joining group, if error
	*/
	public void stop() throws Exception;

	/**
	* Method to send a AODV message through the multicast socket.
	* @param AODVMessage msg - AODV message top send
	* @exception Exception - thrown if error
	*/
	public void sendMessage(AODVMessage msg) throws Exception ;

	/**
	* Method to send a IP packet out through jpcap.
	* @param IPPkt pkt - the packet to be sent
	* @exception Exception - thrown if errors encountered
	*/
	public void sendPkt(IPPkt pkt) throws Exception ;
}
