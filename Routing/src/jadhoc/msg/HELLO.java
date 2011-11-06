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
* This Class represents a HELLO message
*
* @author : Asanga Udugama
* @date : 28-aug-2003
* @email : adu@comnets.uni-bremen.de
*
*/
public class HELLO extends RREP {

	public HELLO(ConfigInfo cfg, CurrentInfo cur, UDPPacket up, String iface) throws Exception {
		super (cfg, cur, up, iface);
	}

	public HELLO (ConfigInfo cfg, CurrentInfo cur) throws Exception {
		super(cfg, cur, true, cfg.ipAddressMulticastVal, (short) 1,
				false, false, (byte) 0, (byte) 0, cfg.ipAddressVal,
				cur.lastSeqNum, cfg.ipAddressVal,
				(cfg.allowedHelloLossVal * cfg.helloIntervalVal));
	}
}
